/**
 * SharedShapefilesTest.java
 */
package org.filteredpush.qc.georeference.util;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.api.data.FileDataStore;
import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.filter.text.ecql.ECQL;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Tests of SharedShapefiles.
 *
 * @author mole
 *
 */
public class SharedShapefilesTest {

	private static final Log logger = LogFactory.getLog(SharedShapefilesTest.class);

	private static final String RESOURCE_DIRECTORY = "/org.filteredpush.kuration.services/";
	/** Small shapefile used for tests. */
	private static final String CENTROIDS = "gbif_pcli_country_centroids";

	/** Temporary folder for a jar containing a shapefile. */
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	private static URL centroidsResource() {
		return GEOUtil.class.getResource(RESOURCE_DIRECTORY + CENTROIDS + ".shp");
	}

	/**
	 * Test that the same data store is returned for repeated requests for the same shapefile,
	 * and that a missing shapefile is reported.
	 *
	 * @throws Exception on an unexpected failure.
	 */
	@Test
	public void testSharedDataStore() throws Exception {
		URL shapefile = centroidsResource();
		assertNotNull(shapefile);
		FileDataStore store = SharedShapefiles.getDataStore(shapefile);
		assertSame(store, SharedShapefiles.getDataStore(shapefile));
		assertSame(store, SharedShapefiles.getDataStore(new URL(shapefile.toExternalForm())));
		assertTrue(SharedShapefiles.getOpenStoreCount() >= 1);

		SimpleFeatureSource source = SharedShapefiles.getFeatureSource(shapefile);
		assertFalse(source.getFeatures(ECQL.toFilter("iso2 ILIKE 'KR'")).isEmpty());
		assertTrue(source.getFeatures(ECQL.toFilter("iso2 ILIKE 'QQ'")).isEmpty());

		try {
			SharedShapefiles.getFeatureSource(null);
			fail("Expected a FileNotFoundException");
		} catch (FileNotFoundException e) {
			logger.debug(e.getMessage());
		}
	}

	/**
	 * Test that disposing of the shared data stores allows them to be opened again.
	 *
	 * @throws Exception on an unexpected failure.
	 */
	@Test
	public void testDisposeAll() throws Exception {
		URL shapefile = centroidsResource();
		FileDataStore store = SharedShapefiles.getDataStore(shapefile);
		SharedShapefiles.disposeAll();
		assertEquals(0, SharedShapefiles.getOpenStoreCount());
		FileDataStore reopened = SharedShapefiles.getDataStore(shapefile);
		assertNotSame(store, reopened);
		assertFalse(SharedShapefiles.getFeatureSource(shapefile).getFeatures(ECQL.toFilter("iso2 ILIKE 'KR'")).isEmpty());
	}

	/**
	 * Test that a local file shapefile is used in place.
	 */
	@Test
	public void testLocalCopyOfFile() {
		URL shapefile = centroidsResource();
		assertEquals("file", shapefile.getProtocol());
		assertSame(shapefile, SharedShapefiles.localCopy(shapefile));
	}

	/**
	 * Test that a shapefile inside a jar is copied, with its sibling files, to a local
	 * file, and that querying it gives the same results as querying the original.
	 *
	 * @throws Exception on an unexpected failure.
	 */
	@Test
	public void testLocalCopyOfJarResource() throws Exception {
		File jar = folder.newFile("shapefiles.jar");
		String[] extensions = { "shp", "shx", "dbf", "prj" };
		try (JarOutputStream out = new JarOutputStream(Files.newOutputStream(jar.toPath()))) {
			for (String extension : extensions) {
				URL resource = GEOUtil.class.getResource(RESOURCE_DIRECTORY + CENTROIDS + "." + extension);
				if (resource!=null) {
					out.putNextEntry(new JarEntry("shapes/" + CENTROIDS + "." + extension));
					copy(resource, out);
					out.closeEntry();
				}
			}
		}
		URL inJar = new URL("jar:" + jar.toURI().toURL().toExternalForm() + "!/shapes/" + CENTROIDS + ".shp");

		URL local = SharedShapefiles.localCopy(inJar);
		assertEquals("file", local.getProtocol());
		File localShp = new File(local.toURI());
		assertTrue(localShp.isFile());
		assertEquals(new File(centroidsResource().toURI()).length(), localShp.length());
		File directory = localShp.getParentFile();
		assertTrue(new File(directory, CENTROIDS + ".shx").isFile());
		assertTrue(new File(directory, CENTROIDS + ".dbf").isFile());
		// sibling not in the jar is not created
		assertFalse(new File(directory, CENTROIDS + ".sbn").exists());

		// queries give the same results as the original
		SimpleFeatureSource fromJar = SharedShapefiles.getFeatureSource(inJar);
		SimpleFeatureSource original = SharedShapefiles.getFeatureSource(centroidsResource());
		assertNotSame(original, fromJar);
		String filter = "iso2 ILIKE 'KR'";
		assertEquals(original.getFeatures(ECQL.toFilter(filter)).size(), fromJar.getFeatures(ECQL.toFilter(filter)).size());
		assertEquals(original.getFeatures().size(), fromJar.getFeatures().size());
	}

	/**
	 * Test that a non-local URL that is not a shapefile, or that can not be copied, is used in place.
	 *
	 * @throws Exception on an unexpected failure.
	 */
	@Test
	public void testLocalCopyNotPossible() throws Exception {
		URL notShapefile = new URL("jar:" + folder.newFile("empty.jar").toURI().toURL().toExternalForm() + "!/readme.txt");
		assertSame(notShapefile, SharedShapefiles.localCopy(notShapefile));

		File jar = folder.newFile("missing.jar");
		try (JarOutputStream out = new JarOutputStream(Files.newOutputStream(jar.toPath()))) {
			out.putNextEntry(new JarEntry("other.txt"));
			out.closeEntry();
		}
		URL missing = new URL("jar:" + jar.toURI().toURL().toExternalForm() + "!/missing.shp");
		assertSame(missing, SharedShapefiles.localCopy(missing));
	}

	private static void copy(URL source, OutputStream out) throws IOException {
		try (InputStream in = source.openStream()) {
			byte[] buffer = new byte[8192];
			int read;
			while ((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}
		}
	}

}
