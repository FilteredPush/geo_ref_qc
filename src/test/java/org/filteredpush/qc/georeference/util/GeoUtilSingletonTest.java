/**
 * GeoUtilSingletonTest.java
 */
package org.filteredpush.qc.georeference.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

/**
 * Tests of the caches held in GeoUtilSingleton, which is shared between threads.
 *
 * @author mole
 *
 */
public class GeoUtilSingletonTest {

	private static final Log logger = LogFactory.getLog(GeoUtilSingletonTest.class);

	/**
	 * Test caching of country and primary division matches, including null values, which are not cached.
	 */
	@Test
	public void testCacheEntries() {
		GeoUtilSingleton instance = GeoUtilSingleton.getInstance();
		assertSame(instance, GeoUtilSingleton.getInstance());

		assertNull(instance.getTgnCountriesEntry("GeoUtilSingletonTest Country Unseen"));
		instance.addTgnCountry("GeoUtilSingletonTest Country True", true);
		instance.addTgnCountry("GeoUtilSingletonTest Country False", false);
		instance.addTgnCountry("GeoUtilSingletonTest Country Null", null);
		assertTrue(instance.getTgnCountriesEntry("GeoUtilSingletonTest Country True"));
		assertFalse(instance.getTgnCountriesEntry("GeoUtilSingletonTest Country False"));
		assertNull(instance.getTgnCountriesEntry("GeoUtilSingletonTest Country Null"));

		assertNull(instance.getTgnPrimaryEntry("GeoUtilSingletonTest Primary Unseen"));
		instance.addTgnPrimary("GeoUtilSingletonTest Primary True", true);
		instance.addTgnPrimary("GeoUtilSingletonTest Primary False", false);
		instance.addTgnPrimary("GeoUtilSingletonTest Primary Null", null);
		assertTrue(instance.getTgnPrimaryEntry("GeoUtilSingletonTest Primary True"));
		assertFalse(instance.getTgnPrimaryEntry("GeoUtilSingletonTest Primary False"));
		assertNull(instance.getTgnPrimaryEntry("GeoUtilSingletonTest Primary Null"));

		ArrayList<String> names = new ArrayList<String>();
		names.add("GeoUtilSingletonTest Name");
		assertFalse(instance.isGettyCountryLookupItem("GeoUtilSingletonTest Lookup"));
		instance.addGettyCountryLookupItem("GeoUtilSingletonTest Lookup", names);
		assertTrue(instance.isGettyCountryLookupItem("GeoUtilSingletonTest Lookup"));
		assertEquals(names, instance.getGettyCountryLookupItem("GeoUtilSingletonTest Lookup"));

		assertFalse(instance.isGettyPrimaryLookupItem("GeoUtilSingletonTest Lookup"));
		instance.addGettyPrimaryLookupItem("GeoUtilSingletonTest Lookup", names);
		assertTrue(instance.isGettyPrimaryLookupItem("GeoUtilSingletonTest Lookup"));
		assertEquals(names, instance.getGettyPrimaryLookupItem("GeoUtilSingletonTest Lookup"));
	}

	/**
	 * Test that the shared GettyLookup is a single instance, even when first requested
	 * from many threads at once.
	 *
	 * @throws Exception on an unexpected failure.
	 */
	@Test
	public void testGetGettyLookupShared() throws Exception {
		ExecutorService executor = Executors.newFixedThreadPool(8);
		try {
			List<Future<GettyLookup>> futures = new ArrayList<Future<GettyLookup>>();
			for (int i=0; i<32; i++) {
				futures.add(executor.submit(() -> GeoUtilSingleton.getInstance().getGettyLookup()));
			}
			GettyLookup expected = GeoUtilSingleton.getInstance().getGettyLookup();
			assertNotNull(expected);
			for (Future<GettyLookup> future : futures) {
				assertSame(expected, future.get(30, TimeUnit.SECONDS));
			}
		} finally {
			executor.shutdownNow();
		}
	}

	/**
	 * Test that concurrent additions to and reads from the caches are all retained,
	 * unsynchronized HashMaps can lose entries (or corrupt) under concurrent writes.
	 *
	 * @throws Exception on an unexpected failure.
	 */
	@Test
	public void testConcurrentCacheAccess() throws Exception {
		final GeoUtilSingleton instance = GeoUtilSingleton.getInstance();
		final int threads = 8;
		final int perThread = 2000;
		ExecutorService executor = Executors.newFixedThreadPool(threads);
		try {
			List<Callable<Boolean>> tasks = new ArrayList<Callable<Boolean>>();
			for (int t=0; t<threads; t++) {
				final int thread = t;
				tasks.add(() -> {
					for (int i=0; i<perThread; i++) {
						String key = "GeoUtilSingletonTest Concurrent " + thread + " " + i;
						instance.addTgnCountry(key, i % 2 == 0);
						instance.addTgnPrimary(key, i % 2 == 1);
						// interleave reads with the writes of other threads
						instance.getTgnCountriesEntry("GeoUtilSingletonTest Concurrent " + ((thread+1) % threads) + " " + i);
					}
					return true;
				});
			}
			for (Future<Boolean> future : executor.invokeAll(tasks, 120, TimeUnit.SECONDS)) {
				assertTrue(future.get());
			}
		} finally {
			executor.shutdownNow();
		}
		for (int t=0; t<threads; t++) {
			for (int i=0; i<perThread; i++) {
				String key = "GeoUtilSingletonTest Concurrent " + t + " " + i;
				assertEquals(key, Boolean.valueOf(i % 2 == 0), instance.getTgnCountriesEntry(key));
				assertEquals(key, Boolean.valueOf(i % 2 == 1), instance.getTgnPrimaryEntry(key));
			}
		}
		logger.debug("Checked " + (threads * perThread) + " concurrently cached entries");
	}

}
