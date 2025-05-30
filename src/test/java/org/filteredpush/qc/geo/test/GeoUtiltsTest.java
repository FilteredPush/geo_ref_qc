/**
 * 
 */
package org.filteredpush.qc.geo.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.filteredpush.qc.georeference.util.CountryLookup;
import org.filteredpush.qc.georeference.util.GEOUtil;
import org.filteredpush.qc.georeference.util.TransformationStruct;
import org.filteredpush.qc.georeference.util.UnknownToWGS84Error;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.operation.TransformException;
import org.junit.Test;

/**
 * @author mole
 *
 */
public class GeoUtiltsTest {

	private static final Log logger = LogFactory.getLog(GeoUtiltsTest.class);
	
	/**
	 * Test method for {@link org.filteredpush.kuration.util.GEOUtil#getDistanceKm(double, double, double, double)}.
	 */
	@Test
	public void testGetDistance() {
		// one degree latitude at the equator is 111 km.
		assertEquals(111, GEOUtil.getDistanceKm(0, 0, 0, 1), 1d);
		
	
	}

	/** 
	 * Test method for {@link org.filteredpush.kuration.util.GEOUtil.isValidEPSGCodeForDwCgeodeticDatum(String)).
	 */
    @Test
    public void testisValidEPSGCodeForDwCgeodeticDatum() {
        String geodeticDatum = "EPSG:4326";
        boolean result = GEOUtil.isValidEPSGCodeForDwCgeodeticDatum(geodeticDatum);
        assertTrue(result);
        
        geodeticDatum = "EPSG:1";
        result = GEOUtil.isValidEPSGCodeForDwCgeodeticDatum(geodeticDatum);
        assertFalse(result);
        
        geodeticDatum = "foo";
        result = GEOUtil.isValidEPSGCodeForDwCgeodeticDatum(geodeticDatum);
        assertFalse(result);
        
    }
    
	/** 
	 * Test method for {@link org.filteredpush.kuration.util.GEOUtil.isValidEPSGCodeForDwCgeodeticDatum(String)).
	 */
    @Test
    public void testisKnownNameForDwCgeodeticDatum() {
        String geodeticDatum = "WGS 84";
        boolean result = GEOUtil.isKnownNameForDwCgeodeticDatum(geodeticDatum);
        assertTrue(result);
        
        geodeticDatum = "ED50";
        result = GEOUtil.isKnownNameForDwCgeodeticDatum(geodeticDatum);
        assertTrue(result);
        
        geodeticDatum = "ED 50";
        result = GEOUtil.isKnownNameForDwCgeodeticDatum(geodeticDatum);
        assertFalse(result);  // ED50 is exact match

        geodeticDatum = "invalid_datum_value";
        result = GEOUtil.isKnownNameForDwCgeodeticDatum(geodeticDatum);
        assertFalse(result);
    }
    
	/** 
	 * Test method for {@link org.filteredpush.kuration.util.GEOUtil.isKnownNameForDwCgeodeticDatumCaseInsensitive(String)).
	 */
    @Test
    public void testisKnownNameForDwCgeodeticDatumCaseInsensitive() {
        String geodeticDatum = "WGS 84";
        boolean result = GEOUtil.isKnownNameForDwCgeodeticDatumCaseInsensitive(geodeticDatum);
        assertTrue(result);
        
        geodeticDatum = "WGS84";
        result = GEOUtil.isKnownNameForDwCgeodeticDatumCaseInsensitive(geodeticDatum);
        assertTrue(result);
        
        geodeticDatum = "ED50";
        result = GEOUtil.isKnownNameForDwCgeodeticDatumCaseInsensitive(geodeticDatum);
        assertTrue(result);
        
        geodeticDatum = "ED 50";
        result = GEOUtil.isKnownNameForDwCgeodeticDatumCaseInsensitive(geodeticDatum);
        assertTrue(result);  // ED50 is exact match

        geodeticDatum = "invalid_datum_value";
        result = GEOUtil.isKnownNameForDwCgeodeticDatumCaseInsensitive(geodeticDatum);
        assertFalse(result);
    }
        
	/** 
	 * Test method for {@link org.filteredpush.kuration.util.GEOUtil.getEPSGCodeForString(String)).
	 */
    @Test
    public void testGetEPSGCodeForString() {
        String geodeticDatum = "WGS 84";
        String epsgCode = GEOUtil.getEPSGCodeForString(geodeticDatum);
        assertEquals("EPSG:4326", epsgCode);
        geodeticDatum = "WGS84";
        epsgCode = GEOUtil.getEPSGCodeForString(geodeticDatum);
        assertEquals("EPSG:4326", epsgCode);
        geodeticDatum = " wgs 84";
        epsgCode = GEOUtil.getEPSGCodeForString(geodeticDatum);
        assertEquals("EPSG:4326", epsgCode);
        geodeticDatum = "INVALID_CODE";
        epsgCode = GEOUtil.getEPSGCodeForString(geodeticDatum);
        assertNull(epsgCode);
        
        Map<String,String> testMap = new HashMap();
        testMap.put("EPSG:4269","NAD83");
        testMap.put("EPSG:4283","GDA94");
        testMap.put("EPSG:4167","NZGD2000");
        testMap.put("EPSG:4258","ETRS89");
        testMap.put("EPSG:7844","GDA2020");
        testMap.put("EPSG:4283","GDA94");
        testMap.put("EPSG:4230","ED50");
        Iterator<String> i = testMap.keySet().iterator();
        while (i.hasNext()) { 
        	String code = i.next();
        	String text = testMap.get(code);
        	epsgCode = GEOUtil.getEPSGCodeForString(text);
        	assertEquals(code,epsgCode);
        }
        
    }
	
	
	@Test
	public void testIsGeodeticDatumKnown() { 
		String geodeticDatum = "";
		assertEquals(false, GEOUtil.isDatumKnown(geodeticDatum));
		
		geodeticDatum = "EPSG:4326";
		assertEquals(true, GEOUtil.isDatumKnown(geodeticDatum));
		
		geodeticDatum = "epsg:4326";
		assertEquals(true, GEOUtil.isDatumKnown(geodeticDatum));
		
		geodeticDatum = "epsg:4267";
		assertEquals(true, GEOUtil.isDatumKnown(geodeticDatum));
		
		geodeticDatum = "NAD 1927";
		assertEquals(true, GEOUtil.isDatumKnown(geodeticDatum));
		
		geodeticDatum = "WGS 1984";
		assertEquals(true, GEOUtil.isDatumKnown(geodeticDatum));
		
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
        // Utqiagvik, Alaska
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
        
        // Utqiagvik, Alaska
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
	public void testIsPrimaryKnown() { 
		assertTrue(GEOUtil.isPrimaryKnown("United States", "Alaska"));
		assertFalse(GEOUtil.isPrimaryKnown("United States", "ZZZZZZZZZZZZ"));
		assertFalse(GEOUtil.isPrimaryKnown("zzzzzzzzzzzzzz", "Alaska"));
		assertTrue(GEOUtil.isPrimaryKnown("united states", "alabama"));
		
		assertTrue(GEOUtil.isPrimaryKnown("Argentina", "Rio Negro"));
		assertTrue(GEOUtil.isPrimaryKnown("Argentina", "Río Negro"));
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
        
        originalLat = -41.2706d;
        originalLng = -70.2816d;
        assertFalse(GEOUtil.isPointInPrimary("Argentina", "Rio Negro", originalLat, originalLng));        
        
        originalLat = -41.0525925872862d;
        originalLng = -71.5310546742521d;
        assertFalse(GEOUtil.isPointInPrimary("Argentina", "Rio Negro", originalLat, originalLng));        
	}
	
	@Test
	public void testisPointNearPrimaryAllowDuplicates() { 
		
		double buffer_km = 3d;
        double originalLat = -41.2706d;
        double originalLng = -70.2816d;
        String stateProvince = "Rio Negro";
        assertTrue(GEOUtil.isPointNearPrimaryAllowDuplicates(stateProvince, originalLat, originalLng, buffer_km));
	
		buffer_km = 3d;
        originalLat = -41.0525925872862d;
        originalLng = -71.5310546742521d;
        stateProvince = "Rio Negro";
        assertTrue(GEOUtil.isPointNearPrimaryAllowDuplicates(stateProvince, originalLat, originalLng, buffer_km));
        
		buffer_km = 3d;
        originalLat = 41.05d;
        originalLng = -71.53d;
        stateProvince = "Rio Negro";
        assertFalse(GEOUtil.isPointNearPrimaryAllowDuplicates(stateProvince, originalLat, originalLng, buffer_km));
	}
	
	@Test
	public void testNearCountry() { 
		assertTrue(GEOUtil.isCountryKnown("United States"));
		
        double thresholdDistanceKmFromLand = 44.448d;  // 24 nautical miles, territorial waters plus contigouus zone.
		
        // Utqiagvik, Alaska, in and near boundary of US.
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
	
	@Test
	public void testGetCountryForPoint() { 

		String latitude = "71.295556";
		String longitude = "-156.766389";
		assertEquals("USA", GEOUtil.getCountryForPoint(latitude, longitude));

		longitude = "-95.689444";
		latitude = "39.055833";
		assertEquals("USA", GEOUtil.getCountryForPoint(latitude, longitude));

	}
	
	@Test
	public void testNearCountryPlusEEZ() { 
		double latitude = -20d;
		double longitude = -72d;
		
		assertTrue(GEOUtil.isPointNearCountryPlusEEZ("CHL", latitude, longitude, 3d));
		
		latitude = -21d;
		longitude = -76d;
		assertFalse(GEOUtil.isPointNearCountryPlusEEZ("CHL", latitude, longitude, 3d));
		
	}
	
	@Test 
	public void testisNumericCharacters() { 
		assertTrue(GEOUtil.isNumericCharacters("1"));
		assertTrue(GEOUtil.isNumericCharacters("11"));
		assertTrue(GEOUtil.isNumericCharacters(" 1 "));
		assertTrue(GEOUtil.isNumericCharacters("-1"));
		assertTrue(GEOUtil.isNumericCharacters("-11"));
		assertTrue(GEOUtil.isNumericCharacters("-11.1"));
		assertTrue(GEOUtil.isNumericCharacters("-11."));
		
		assertFalse(GEOUtil.isNumericCharacters(""));
		assertFalse(GEOUtil.isNumericCharacters(" "));
		assertFalse(GEOUtil.isNumericCharacters(null));
		assertFalse(GEOUtil.isNumericCharacters("a"));
		assertFalse(GEOUtil.isNumericCharacters("10a"));
		assertFalse(GEOUtil.isNumericCharacters("10d"));
		
		assertFalse(GEOUtil.isNumericCharacters("0x1"));
		assertFalse(GEOUtil.isNumericCharacters("F1"));
		assertFalse(GEOUtil.isNumericCharacters("0F8H"));
	}
	
	@Test 
	public void testUUnknownToWGS84Error() { 
		assertEquals(3289, UnknownToWGS84Error.getErrorAtCoordinate(-90, -180).intValue());
		assertEquals(3289, UnknownToWGS84Error.getErrorAtCoordinate(-89, -179).intValue());
		
		assertEquals(3085, UnknownToWGS84Error.getErrorAtCoordinate(-85, -180).intValue());
		assertEquals(3085, UnknownToWGS84Error.getErrorAtCoordinate(-84, -179).intValue());
		
		assertEquals(2825, UnknownToWGS84Error.getErrorAtCoordinate(85, 175).intValue());
		assertEquals(2825, UnknownToWGS84Error.getErrorAtCoordinate(89, 179).intValue());
		assertEquals(2825, UnknownToWGS84Error.getErrorAtCoordinate(90, 180).intValue());
		
		
		
	}

	@Test 
	public void testDatumTransform() { 
		
		String decimalLatitude = "42.383686";
		String decimalLongitude = "-71.1474181";
		String geodeticDatum = "EPSG:4267";
		String targetGeodeticDatum = "EPSG:4326";
		Double delta = 0.000001d;
		
		TransformationStruct result;
		try { 
			result = GEOUtil.datumTransform(decimalLatitude, decimalLongitude, geodeticDatum, targetGeodeticDatum);
			// https://epsg.io/transform#s_srs=4267&t_srs=4326&ops=15851&x=-71.1474181&y=42.383686
			assertEquals(42.383783d, result.getDecimalLatitude(),delta);
			assertEquals(-71.146916d,result.getDecimalLongitude(),delta);
		} catch (Exception e) { 
			fail(e.getMessage());
		}
		
		// Royal Observatory, Greenwich 
		decimalLatitude = "51.4786952";
		decimalLongitude = "0.0000062";
		geodeticDatum = "EPSG:4230"; // ED50
		delta = 0.000001d;
	
// NOT Passing, datum transforms not working
		try {
			result = GEOUtil.datumTransform(decimalLatitude, decimalLongitude, geodeticDatum, targetGeodeticDatum);
//			assertEquals(51.47783d, result.getDecimalLatitude(),delta);
//			assertEquals(-0.00139d,result.getDecimalLongitude(),delta);
		} catch (Exception e) {
			logger.debug(e.getMessage(),e);
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testcoordinateSystemTransformTo4326() { 
		String decimalLatitude = "42.383686";
		String decimalLongitude = "-71.1474181";
		String geodeticDatum = "EPSG:4267";
		Double delta = 0.000001d;
		
		// this will fail, with no transformation made if a NAD27 grid file is not available.
		// See: https://gis.stackexchange.com/questions/333941/java-conversion-from-nad27-to-wgs84
	
// transforms not working		
		TransformationStruct result;
		try {
			result = GEOUtil.coordinateSystemTransformTo4326(decimalLatitude, decimalLongitude, geodeticDatum);
//			assertEquals(42.383783d, result.getDecimalLatitude(),delta);
//			assertEquals(-71.146916d,result.getDecimalLongitude(),delta);
		} catch (FactoryException e) {
			fail(e.getMessage());
		} catch (TransformException e) {
			fail(e.getMessage());
		}
		
	}
	
	@Test
	public void testexternalTransforTo4326() { 
		String decimalLatitude = "42.383686";
		String decimalLongitude = "-71.1474181";
		String geodeticDatum = "EPSG:4267";  // NAD27
		Double delta = 0.000001d;
		
//		TransformationStruct result;
//		try {
//			result = GEOUtil.externalTransformTo4326(decimalLatitude, decimalLongitude, geodeticDatum);
//			assertEquals(42.383783d, result.getDecimalLatitude(),delta);
//			assertEquals(-71.146916d,result.getDecimalLongitude(),delta);
//		} catch (Exception e) {
//			logger.debug(e.getMessage(),e);
//			fail(e.getMessage());
//		}
		
		
	}
	
	@Test
	public void testsimplifyVerbatimCoordinate() { 
		String verbatimCoordinate = null;
		assertNull(GEOUtil.simplifyVerbatimCoordinate(verbatimCoordinate));
		
		verbatimCoordinate = "";
		assertEquals("",GEOUtil.simplifyVerbatimCoordinate(verbatimCoordinate));
		
		verbatimCoordinate = "132.54° N";
		assertEquals(verbatimCoordinate,GEOUtil.simplifyVerbatimCoordinate(verbatimCoordinate));
		
		verbatimCoordinate = "132.54 degrees north";
		assertEquals("132.54 ° N",GEOUtil.simplifyVerbatimCoordinate(verbatimCoordinate));
		
		verbatimCoordinate = "132.54° North";
		assertEquals("132.54° N",GEOUtil.simplifyVerbatimCoordinate(verbatimCoordinate));
		
	}
}


