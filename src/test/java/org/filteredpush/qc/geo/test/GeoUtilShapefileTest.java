/**
 * GeoUtilShapefileTest.java
 */
package org.filteredpush.qc.geo.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.filteredpush.qc.georeference.util.GEOUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests of the GEOUtil methods that query shapefiles, including that they close the
 * shapefile readers that they open, so that GeoTools does not report readers left
 * unclosed or locks still held on the shapefiles.
 *
 * @author mole
 *
 */
public class GeoUtilShapefileTest {

	private static final Log logger = LogFactory.getLog(GeoUtilShapefileTest.class);

	/** GeoTools shapefile logger, parent of the loggers that report unclosed readers and held locks. */
	private static final String SHAPEFILE_LOGGER = "org.geotools.data.shapefile";

	/** Point in the Democratic People's Republic of Korea. */
	private static final String KP_LAT = "40.339852";
	private static final String KP_LNG = "127.510093";
	/** Point in the Republic of Korea. */
	private static final String KR_LAT = "35.907757";
	private static final String KR_LNG = "127.766922";
	/** Point in the high seas of the South Atlantic, outside any country or EEZ. */
	private static final String HIGH_SEAS_LAT = "-40";
	private static final String HIGH_SEAS_LNG = "-20";

	private Logger shapefileLogger;
	private Level originalLevel;
	private List<LogRecord> records;
	private Handler handler;

	/**
	 * Capture warnings logged by the GeoTools shapefile readers.
	 */
	@Before
	public void setUp() {
		records = new CopyOnWriteArrayList<LogRecord>();
		handler = new Handler() {
			@Override
			public void publish(LogRecord record) {
				if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
					records.add(record);
				}
			}
			@Override
			public void flush() { }
			@Override
			public void close() { }
		};
		shapefileLogger = Logger.getLogger(SHAPEFILE_LOGGER);
		originalLevel = shapefileLogger.getLevel();
		if (originalLevel==null || originalLevel.intValue() > Level.WARNING.intValue()) {
			shapefileLogger.setLevel(Level.WARNING);
		}
		shapefileLogger.addHandler(handler);
	}

	/**
	 * Remove the handler capturing GeoTools shapefile warnings.
	 */
	@After
	public void tearDown() {
		shapefileLogger.removeHandler(handler);
		shapefileLogger.setLevel(originalLevel);
	}

	private void assertNoShapefileWarnings() {
		StringBuilder messages = new StringBuilder();
		for (LogRecord record : records) {
			messages.append(record.getLevel()).append(" ").append(record.getMessage()).append("; ");
		}
		assertTrue("GeoTools reported problems with shapefile readers: " + messages, records.isEmpty());
	}

	/**
	 * Test getCountryForPoint for points in a country, in the high seas, and invalid coordinates.
	 */
	@Test
	public void testGetCountryForPoint() {
		assertEquals("PRK", GEOUtil.getCountryForPoint(KP_LAT, KP_LNG));
		assertEquals("KOR", GEOUtil.getCountryForPoint(KR_LAT, KR_LNG));
		assertNull(GEOUtil.getCountryForPoint(HIGH_SEAS_LAT, HIGH_SEAS_LNG));
		// not numbers, CQL can not be parsed
		assertNull(GEOUtil.getCountryForPoint("foo", "bar"));
		assertNoShapefileWarnings();
	}

	/**
	 * Test isPointInCountry for points in, and not in, a country, and for an unknown country.
	 */
	@Test
	public void testIsPointInCountry() {
		assertTrue(GEOUtil.isPointInCountry("Korea", Double.parseDouble(KR_LAT), Double.parseDouble(KR_LNG)));
		assertTrue(GEOUtil.isPointInCountry("korea", Double.parseDouble(KR_LAT), Double.parseDouble(KR_LNG)));
		assertFalse(GEOUtil.isPointInCountry("Korea", Double.parseDouble(KP_LAT), Double.parseDouble(KP_LNG)));
		assertFalse(GEOUtil.isPointInCountry("Nowhere", Double.parseDouble(KR_LAT), Double.parseDouble(KR_LNG)));
		assertNoShapefileWarnings();
	}

	/**
	 * Test isPointNearCountry and isPointNearCountryPlusEEZ for near and distant points.
	 */
	@Test
	public void testIsPointNearCountry() {
		double lat = Double.parseDouble(KR_LAT);
		double lng = Double.parseDouble(KR_LNG);
		assertTrue(GEOUtil.isPointNearCountry("Korea", lat, lng, 10d));
		assertFalse(GEOUtil.isPointNearCountry("Korea", -40d, -20d, 10d));
		assertTrue(GEOUtil.isPointNearCountryPlusEEZ("KOR", lat, lng, 10d));
		assertFalse(GEOUtil.isPointNearCountryPlusEEZ("KOR", -40d, -20d, 10d));
		assertFalse(GEOUtil.isPointNearCountryPlusEEZ("XXX", lat, lng, 10d));
		assertNoShapefileWarnings();
	}

	/**
	 * Test that repeated shapefile queries do not leave shapefile readers unclosed,
	 * which GeoTools reports when disposing of a data store with a reader still open,
	 * and when garbage collecting an unclosed reader.
	 */
	@Test
	public void testRepeatedQueriesCloseReaders() {
		for (int i=0; i<10; i++) {
			assertEquals("PRK", GEOUtil.getCountryForPoint(KP_LAT, KP_LNG));
			assertEquals("KOR", GEOUtil.getCountryForPoint(KR_LAT, KR_LNG));
			assertTrue(GEOUtil.isPointInCountry("Korea", Double.parseDouble(KR_LAT), Double.parseDouble(KR_LNG)));
			assertTrue(GEOUtil.isPointNearCountry("Korea", Double.parseDouble(KR_LAT), Double.parseDouble(KR_LNG), 10d));
			assertTrue(GEOUtil.isPointNearCountryPlusEEZ("KOR", Double.parseDouble(KR_LAT), Double.parseDouble(KR_LNG), 10d));
		}
		// encourage finalization of any unclosed readers, which log a warning when finalized.
		System.gc();
		System.runFinalization();
		logger.debug("Shapefile warnings: " + records.size());
		assertNoShapefileWarnings();
	}

	/**
	 * Test that shapefile queries from many threads at once, sharing data stores, give 
	 * correct results and leave no shapefile readers unclosed.
	 * 
	 * @throws Exception on an unexpected failure.
	 */
	@Test
	public void testConcurrentQueries() throws Exception { 
		ExecutorService executor = Executors.newFixedThreadPool(8);
		try { 
			List<Callable<Boolean>> tasks = new ArrayList<Callable<Boolean>>();
			for (int t=0; t<8; t++) { 
				tasks.add(() -> { 
					for (int i=0; i<10; i++) { 
						assertEquals("PRK", GEOUtil.getCountryForPoint(KP_LAT, KP_LNG));
						assertEquals("KOR", GEOUtil.getCountryForPoint(KR_LAT, KR_LNG));
						assertNull(GEOUtil.getCountryForPoint(HIGH_SEAS_LAT, HIGH_SEAS_LNG));
						assertTrue(GEOUtil.isPointInCountry("Korea", Double.parseDouble(KR_LAT), Double.parseDouble(KR_LNG)));
						assertFalse(GEOUtil.isPointInCountry("Korea", Double.parseDouble(KP_LAT), Double.parseDouble(KP_LNG)));
					}
					return true;
				});
			}
			for (Future<Boolean> future : executor.invokeAll(tasks, 300, TimeUnit.SECONDS)) { 
				assertTrue(future.get());
			}
		} finally { 
			executor.shutdownNow();
		}
		System.gc();
		System.runFinalization();
		assertNoShapefileWarnings();
	}

}
