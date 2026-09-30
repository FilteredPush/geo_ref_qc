/**
 * SharedShapefiles.java
 */
package org.filteredpush.qc.georeference.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.api.data.FileDataStore;
import org.geotools.api.data.FileDataStoreFinder;
import org.geotools.api.data.SimpleFeatureSource;

/**
 * Holds a single, shared, read only data store for each shapefile used by this library, opened
 * on first use and kept open, so that each query does not open (and parse the headers of) the
 * shapefile again.  GeoTools data stores are thread safe for reading, each query opens and
 * closes its own readers, so callers must close any feature iterators they open, but must
 * not dispose of the shared data store.
 *
 * <p>A shapefile that is not a local file (e.g. a resource inside the geo_ref_qc jar) is first
 * copied, with its sibling files (.shx, .dbf, .prj, etc.), to a temporary directory, which is
 * deleted when the JVM exits.  Reading a shapefile from inside a jar requires decompressing the
 * whole file for every query, a local copy can be read directly, and can have a spatial index.
 * If the copy can not be made, the shapefile is read from its original location.</p>
 *
 * @author mole
 */
public final class SharedShapefiles {

	private static final Log logger = LogFactory.getLog(SharedShapefiles.class);

	/** Open data stores, keyed on the external form of the shapefile URL. */
	private static final Map<String,FileDataStore> stores = new ConcurrentHashMap<String,FileDataStore>();

	/** Extensions of the files that may make up a shapefile, other than .shp. */
	private static final String[] SIBLING_EXTENSIONS = { "shx", "dbf", "prj", "cpg", "qix", "fix", "sbn", "sbx" };

	/** Temporary directory holding local copies of shapefiles, created on first use. */
	private static File tempDirectory;

	private SharedShapefiles() { }

	/**
	 * Obtain a feature source for a shapefile, from the shared data store for that shapefile,
	 * opening the data store if this is the first use of the shapefile.
	 *
	 * @param shapefile the URL of the .shp file, e.g. from Class.getResource().
	 * @return a feature source for querying the shapefile.
	 * @throws FileNotFoundException if shapefile is null (e.g. a resource that was not found).
	 * @throws IOException if the shapefile can not be opened.
	 */
	public static SimpleFeatureSource getFeatureSource(URL shapefile) throws IOException {
		return getDataStore(shapefile).getFeatureSource();
	}

	/**
	 * Obtain the shared data store for a shapefile, opening it if this is the first use of
	 * the shapefile.
	 *
	 * @param shapefile the URL of the .shp file, e.g. from Class.getResource().
	 * @return the shared data store, which callers must not dispose of.
	 * @throws FileNotFoundException if shapefile is null (e.g. a resource that was not found).
	 * @throws IOException if the shapefile can not be opened.
	 */
	public static FileDataStore getDataStore(URL shapefile) throws IOException {
		if (shapefile==null) {
			throw new FileNotFoundException("Shapefile not found");
		}
		String key = shapefile.toExternalForm();
		FileDataStore store = stores.get(key);
		if (store==null) {
			synchronized (stores) {
				store = stores.get(key);
				if (store==null) {
					store = FileDataStoreFinder.getDataStore(localCopy(shapefile));
					if (store==null) {
						throw new IOException("No data store available for " + key);
					}
					logger.debug("Opened shared data store for " + key);
					stores.put(key, store);
				}
			}
		}
		return store;
	}

	/**
	 * Obtain a local file copy of a shapefile that is not a local file.
	 *
	 * @param shapefile the URL of the .shp file.
	 * @return shapefile if it is a local file, otherwise the URL of a local copy, or shapefile if a
	 *   local copy could not be made.
	 */
	static URL localCopy(URL shapefile) {
		if ("file".equals(shapefile.getProtocol())) {
			return shapefile;
		}
		String external = shapefile.toExternalForm();
		if (!external.toLowerCase().endsWith(".shp")) {
			return shapefile;
		}
		String base = external.substring(0, external.length() - 4);
		String name = base.substring(base.lastIndexOf('/') + 1);
		try {
			// a directory for each source location, so shapefiles with the same name from different sources do not collide
			File directory = new File(getTempDirectory(), Integer.toHexString(base.hashCode()));
			if (!directory.isDirectory() && !directory.mkdirs()) {
				throw new IOException("Unable to create directory " + directory.getPath());
			}
			directory.deleteOnExit();
			File target = new File(directory, name + ".shp");
			target.deleteOnExit();
			copy(shapefile, target);
			for (String extension : SIBLING_EXTENSIONS) {
				File sibling = new File(directory, name + "." + extension);
				// register even if not copied, GeoTools may create index files (.qix, .fix) next to the shapefile
				sibling.deleteOnExit();
				try {
					copy(new URL(base + "." + extension), sibling);
				} catch (FileNotFoundException e) {
					// optional sibling file not present
					logger.trace(e.getMessage());
				}
			}
			logger.debug("Copied " + external + " to " + target.getPath());
			return target.toURI().toURL();
		} catch (IOException e) {
			logger.warn("Unable to make a local copy of " + external + ", reading it in place: " + e.getMessage());
			return shapefile;
		}
	}

	/**
	 * Obtain the temporary directory for local copies of shapefiles, creating it if needed.
	 *
	 * @return the temporary directory, deleted when the JVM exits.
	 * @throws IOException if the directory can not be created.
	 */
	private static synchronized File getTempDirectory() throws IOException {
		if (tempDirectory==null || !tempDirectory.isDirectory()) {
			File directory = Files.createTempDirectory("geo_ref_qc-shapefiles").toFile();
			// files registered for deletion after the directory are deleted before it on exit
			directory.deleteOnExit();
			tempDirectory = directory;
		}
		return tempDirectory;
	}

	/**
	 * Copy the content of a URL to a file.
	 *
	 * @param source the URL to copy.
	 * @param target the file to copy to, replaced if it exists.
	 * @throws FileNotFoundException if source does not exist.
	 * @throws IOException on another error copying.
	 */
	private static void copy(URL source, File target) throws IOException {
		try (InputStream in = source.openStream()) {
			Files.copy(in, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
	}

	/**
	 * @return the number of shapefiles for which a shared data store is open.
	 */
	public static int getOpenStoreCount() {
		return stores.size();
	}

	/**
	 * Dispose of all of the shared data stores, releasing their resources, for example when
	 * a web application using this library is being shut down.  Data stores are opened again
	 * if the shapefiles are used after this call.  Must not be called while queries are in progress.
	 */
	public static void disposeAll() {
		List<FileDataStore> toDispose;
		synchronized (stores) {
			toDispose = new ArrayList<FileDataStore>(stores.values());
			stores.clear();
		}
		for (FileDataStore store : toDispose) {
			try {
				store.dispose();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

}
