/**
 * 
 */
package org.filteredpush.qc.geo.test;

import org.filteredpush.qc.georeference.GeoTester;
import org.filteredpush.qc.georeference.util.GEOUtil;
import org.filteredpush.qc.georeference.util.GISDataLoader;
import org.junit.Before;
import org.junit.Test;

import java.awt.geom.Path2D;
import java.io.IOException;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * @author mole
 *
 */
public class GeoTesterTest {
    private GeoTester geoTester;

    @Before
    public void init() throws IOException {
        this.geoTester = new GeoTester();
    }
	
	@Test
	public void testInCountry() { 
        assertTrue(geoTester.isCountryKnown("United States"));
        assertTrue(geoTester.isCountryKnown("UNITED STATES"));
        assertFalse(geoTester.isCountryKnown("zzzzzzzzzzzzz"));
        
        // Barrow, Alaska
        double originalLng = -156.766389d;
        double originalLat = 71.295556d;
        assertTrue(geoTester.isPointInCountry("United States", originalLat, originalLng));
        assertTrue(geoTester.isPointInCountry("UNITED STATES", originalLat, originalLng));
        // further north
        originalLng = -156.766389d;
        originalLat = 80.0d;
        assertFalse(geoTester.isPointInCountry("United States", originalLat, originalLng));
        // transposed
        originalLat = -156.766389d;
        originalLng = 71.295556d;
        assertFalse(geoTester.isPointInCountry("United States", originalLat, originalLng));
	}
	
	@Test 
	public void testInPrimary() { 
		assertTrue(geoTester.isPrimaryKnown("United States", "Alaska"));
		assertFalse(geoTester.isPrimaryKnown("United States", "ZZZZZZZZZZZZ"));
		assertFalse(geoTester.isPrimaryKnown("zzzzzzzzzzzzzz", "Alaska"));
		
        double originalLng = -156.766389d;
        double originalLat = 71.295556d;
        assertTrue(geoTester.isPointInPrimary("United States","Alaska", originalLat, originalLng));
        
        // further north
        originalLng = -156.766389d;
        originalLat = 80.0d;
        assertFalse(geoTester.isPointInPrimary("United States", "Alaska", originalLat, originalLng));
        // transposed
        originalLat = -156.766389d;
        originalLng = 71.295556d;
        assertFalse(geoTester.isPointInPrimary("United States", "Alaska", originalLat, originalLng));
	}
	
	@Test
	public void testNearCountry() { 
		assertTrue(geoTester.isCountryKnown("United States"));
		
        double thresholdDistanceKmFromLand = 44.448d;  // 24 nautical miles, territorial waters plus contigouus zone.
		
        // Barrow, Alaska, in and near boundary of US.
        double originalLng = -156.766389d;
        double originalLat = 71.295556d;
        assertTrue(geoTester.isPointInCountry("United States", originalLat, originalLng));
        assertTrue(geoTester.isPointNearCountry("United States", originalLat, originalLng, thresholdDistanceKmFromLand));
        
        // slightly further north, not in, but near boundary of US.
        originalLng = -156.766389d;
        originalLat = 71.5d;
        assertFalse(geoTester.isPointInCountry("United States", originalLat, originalLng));
        assertTrue(geoTester.isPointNearCountry("United States", originalLat, originalLng, thresholdDistanceKmFromLand));
        
        
        // slightly further South, in US. 
        originalLng = -156.766389d;
        originalLat = 70.0d;
        assertTrue(geoTester.isPointInCountry("United States", originalLat, originalLng));
        assertTrue(geoTester.isPointNearCountry("United States", originalLat, originalLng, thresholdDistanceKmFromLand));
        
        // Further north, not in, or near boundary of US.
        originalLng = -156.766389d;
        originalLat = 80d;
        assertFalse(geoTester.isPointInCountry("United States", originalLat, originalLng));
        assertFalse(geoTester.isPointNearCountry("United States", originalLat, originalLng, thresholdDistanceKmFromLand));
        
	}
	
	
	@Test
	public void testparseVerbatimLatLongToDecimalDegree() {
		double DELTA = 1e-10;
		
		String verbatim = "";
		Double expected = null;
		Double result = GEOUtil.parseVerbatimLatLongToDecimalDegree(verbatim);
		assertNull(result);
		
		verbatim = "15.2";
		expected = 15.2d;
		result = GEOUtil.parseVerbatimLatLongToDecimalDegree(verbatim);
		assertEquals(expected,result,DELTA);
		
		verbatim = "15.2° N";
		expected = 15.2d;
		result = GEOUtil.parseVerbatimLatLongToDecimalDegree(verbatim);
		assertEquals(expected,result,DELTA);
		
		verbatim = "15.2°S";
		expected = -15.2d;
		result = GEOUtil.parseVerbatimLatLongToDecimalDegree(verbatim);
		assertEquals(expected,result,DELTA);
		
		verbatim = "15° 30' N";
		expected = 15.5d;
		result = GEOUtil.parseVerbatimLatLongToDecimalDegree(verbatim);
		assertEquals(expected,result,DELTA);
		
		verbatim = "15°30'S";
		expected = -15.5d;
		result = GEOUtil.parseVerbatimLatLongToDecimalDegree(verbatim);
		assertEquals(expected,result,DELTA);
		
		verbatim = "15° 30' 30" + '"' + " N";
		expected = 15.508333333333333d;
		result = GEOUtil.parseVerbatimLatLongToDecimalDegree(verbatim);
		assertEquals(expected,result,DELTA);
		
		verbatim = "82°30'30" + '"' + "S";
		expected = -82.508333333333333d;
		result = GEOUtil.parseVerbatimLatLongToDecimalDegree(verbatim);
		assertEquals(expected,result,DELTA);
		
		verbatim = "152.555W";
		expected = -152.555d;
		result = GEOUtil.parseVerbatimLatLongToDecimalDegree(verbatim);
		assertEquals(expected,result,DELTA);
		
		verbatim = " 23.456S ";
		expected = -23.456;
		result = GEOUtil.parseVerbatimLatLongToDecimalDegree(verbatim);
		assertEquals(expected,result,DELTA);
		
		
	}

}


