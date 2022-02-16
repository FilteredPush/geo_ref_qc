/**
 * 
 */
package org.filteredpush.qc.geo.test;

import static org.junit.Assert.*;

import java.awt.geom.Path2D;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Set;

import org.filteredpush.qc.georeference.util.GEOUtil;
import org.filteredpush.qc.georeference.util.GISDataLoader;
import org.geotools.data.shapefile.shp.ShapefileException;
import org.geotools.filter.text.cql2.CQLException;
import org.junit.Test;

/**
 * @author mole
 *
 */
public class GeoUtiltsTest {

	/**
	 * Test method for {@link org.filteredpush.kuration.util.GEOUtil#getDistanceKm(double, double, double, double)}.
	 */
	@Test
	public void testGetDistance() {
		// one degree latitude at the equator is 111 km.
		assertEquals(111, GEOUtil.getDistanceKm(0, 0, 0, 1), 1d);
		
	
	}

	/**
	 * Test method for {@link org.filteredpush.kuration.util.GEOUtil#calcDistanceHaversineMeters(double, double, double, double)}.
	 */
	@Test
	public void testCalcDistanceHaversineMeters() {
		// one degree latitude at the equator is 111194 meters  
		assertEquals(111194, GEOUtil.calcDistanceHaversineMeters(0, 0, 0, 1));
		
		// each degree of longitude is a constant 111194 meters
	    assertEquals(111194, GEOUtil.calcDistanceHaversineMeters(0, 0, 1, 0));
	    for (int d=-90; d<90; d++) {
	          assertEquals(111194, GEOUtil.calcDistanceHaversineMeters(d, 0, d+1, 0));
	    }		
	    
	    // Test Case from: http://rosettacode.org/wiki/Haversine_formula#Java
	    // Distance between  (36.12, -86.67) and (33.94, -118.40) is 2887260 m
	    // Distance depends on the choice of EARTH_MEAN_RADIUS  
	    // For earth radius based on surface area 6371.0 km distance is 2886.44444283798329974715782394574671655 km;
	    // For radius based on average circumference 6372.8 km distance is 2887.25995060711033944886005029688505340 km;
	    // We are using the CRC value EARTH_MEAN_RADIUS_METERS 6370949 (radius based on surface area)
	    // so our result is slightly different from either of these.
	    assertEquals(2886421,GEOUtil.calcDistanceHaversineMeters(36.120, -86.670, 33.940, -118.400));
		
	}
	
	@Test
	public void testisOnLand() { 
        boolean invertSense = false;
        
        // Topeka, Kansas
        double originalLng = -95.689444d;
        double originalLat = 39.055833d;
        assertTrue(GEOUtil.isOnLand(originalLng, originalLat, invertSense));
        // Barrow, Alaska
        originalLng = -156.766389d;
        originalLat = 71.295556d;
        assertTrue(GEOUtil.isOnLand(originalLng, originalLat, invertSense));
        // further north
        originalLng = -156.766389d;
        originalLat = 80.0d;
        assertFalse(GEOUtil.isOnLand(originalLng, originalLat, invertSense));
        originalLng = -156.766389d;
        originalLat = 80.0d;
        invertSense = true;
        assertTrue(GEOUtil.isOnLand(originalLng, originalLat, invertSense));
	}
	
	@Test
	public void testInCountry() { 
        assertTrue(GEOUtil.isCountryKnown("United States"));
        assertTrue(GEOUtil.isCountryKnown("UNITED STATES"));
        assertFalse(GEOUtil.isCountryKnown("zzzzzzzzzzzzz"));
        
        // Barrow, Alaska
        double originalLng = -156.766389d;
        double originalLat = 71.295556d;
        assertTrue(GEOUtil.isPointInCountry("United States", originalLat, originalLng));
        assertTrue(GEOUtil.isPointInCountry("UNITED STATES", originalLat, originalLng));
        // further north
        originalLng = -156.766389d;
        originalLat = 80.0d;
        assertFalse(GEOUtil.isPointInCountry("United States", originalLat, originalLng));
        // transposed
        originalLat = -156.766389d;
        originalLng = 71.295556d;
        assertFalse(GEOUtil.isPointInCountry("United States", originalLat, originalLng));        
	}
	
	@Test 
	public void testInPrimary() { 
		assertTrue(GEOUtil.isPrimaryKnown("United States", "Alaska"));
		assertFalse(GEOUtil.isPrimaryKnown("United States", "ZZZZZZZZZZZZ"));
		assertFalse(GEOUtil.isPrimaryKnown("zzzzzzzzzzzzzz", "Alaska"));
		
        double originalLng = -156.766389d;
        double originalLat = 71.295556d;
        assertTrue(GEOUtil.isPointInPrimary("United States","Alaska", originalLat, originalLng));
        
        // further north
        originalLng = -156.766389d;
        originalLat = 80.0d;
        assertFalse(GEOUtil.isPointInPrimary("United States", "Alaska", originalLat, originalLng));
        // transposed
        originalLat = -156.766389d;
        originalLng = 71.295556d;
        assertFalse(GEOUtil.isPointInPrimary("United States", "Alaska", originalLat, originalLng));        
	}
	
	@Test
	public void testNearCountry() { 
		assertTrue(GEOUtil.isCountryKnown("United States"));
		
        double thresholdDistanceKmFromLand = 44.448d;  // 24 nautical miles, territorial waters plus contigouus zone.
		
        // Barrow, Alaska, in and near boundary of US.
        double originalLng = -156.766389d;
        double originalLat = 71.295556d;
        assertTrue(GEOUtil.isPointInCountry("United States", originalLat, originalLng));
        assertTrue(GEOUtil.isPointNearCountry("United States", originalLat, originalLng, thresholdDistanceKmFromLand));
        
        // slightly further north, not in, but near boundary of US.
        originalLng = -156.766389d;
        originalLat = 71.5d;
        assertFalse(GEOUtil.isPointInCountry("United States", originalLat, originalLng));
        assertTrue(GEOUtil.isPointNearCountry("United States", originalLat, originalLng, thresholdDistanceKmFromLand));
        
        
        // slightly further South, in US. 
        originalLng = -156.766389d;
        originalLat = 70.0d;
        assertTrue(GEOUtil.isPointInCountry("United States", originalLat, originalLng));
        assertTrue(GEOUtil.isPointNearCountry("United States", originalLat, originalLng, thresholdDistanceKmFromLand));
        
        // Further north, not in, or near boundary of US.
        originalLng = -156.766389d;
        originalLat = 80d;
        assertFalse(GEOUtil.isPointInCountry("United States", originalLat, originalLng));
        assertFalse(GEOUtil.isPointNearCountry("United States", originalLat, originalLng, thresholdDistanceKmFromLand));
        
	}

}


