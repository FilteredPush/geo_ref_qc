package org.filteredpush.qc.georeference.util;

import java.awt.geom.Path2D;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.api.data.FileDataStore;
import org.geotools.api.data.FileDataStoreFinder;
import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.filter.Filter;
import org.geotools.api.metadata.quality.PositionalAccuracy;
import org.geotools.api.metadata.quality.QuantitativeResult;
import org.geotools.api.metadata.quality.Result;
import org.geotools.api.geometry.MismatchedDimensionException;
import org.geotools.api.geometry.Position;
import org.geotools.geometry.Position2D;
import org.geotools.geometry.Position3D;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.NoSuchAuthorityCodeException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.CoordinateOperation;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.api.util.Record;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.GeodeticCalculator;
import org.geotools.referencing.operation.DefaultCoordinateOperationFactory;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

public class GEOUtil {
    private static final Log logger = LogFactory.getLog(GEOUtil.class);

	/**
	 * Equatorial radius of the Earth in kilometers (GRS80).
	 */
	private static double EARTH_EQUATORIAL_RADIUS_KM = 6378.138;
	
	// GRS80 value for the equatorial radius of the Earth = 6,378,138.0 meters.
	// IERS value for the equatorial radius of the Earth =  6,378,136.3 meters.
	// Wikipedia/Australian Geodetic Datum mean equatorial radius = 6,378,160.0 meters.
	// CRC Mean radius of the Earth: 6370949.0 meters
	
	/**
	 * Mean radius of the Earth in meters (CRC).
	 */
	private static double EARTH_MEAN_RADIUS_METERS = 6370949.0d;  // Mean radius, from CRC
	
	/**
	 * 
	 * @param lat1
	 * @param lng1
	 * @param lat2
	 * @param lng2
	 * @return
	 */
	public static double getDistanceKm(double lat1, double lng1, double lat2, double lng2)
	{
	   double radLat1 = Math.toRadians(lat1);
	   double radLat2 = Math.toRadians(lat2);
	   double a = radLat1 - radLat2;
	   double b = Math.toRadians(lng1) - Math.toRadians(lng2);

	   double s = 2d * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2d),2d) + Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2d),2d)));
	   s = s * EARTH_EQUATORIAL_RADIUS_KM;
	   s = Math.round(s * 10000) / 10000;
	   return s;
	}

	/**
	 * Calculate distance in meters between two points on the Earth's surface using the Haversine formula,
	 * which maintains accuracy even when points are a short distance apart.
	 * 
	 * @param lat1 latitude of the first point
	 * @param lon1 longitude of the first point
	 * @param lat2 latitude of the second point
	 * @param lon2 longitude of the second point
	 * 
	 * @return great circle distance between the two points in meters.
	 */
	public static long calcDistanceHaversineMeters(double lat1, double lon1, double lat2, double lon2) {

		double lat1r = Math.toRadians(lat1);
		double lat2r = Math.toRadians(lat2);
		double long1r = Math.toRadians(lon1);
		double long2r = Math.toRadians(lon2);
	    double deltaLat = lat2r - lat1r;
	    double deltaLon = long2r - long1r;
	    double a = Math.pow((Math.sin(deltaLat/2.0d)), 2) + Math.cos(lat1r) * Math.cos(lat2r) * Math.pow((Math.sin(deltaLon/2.0d)), 2);
	    double c = 2.0d * Math.atan2(Math.sqrt(a), Math.sqrt(1.0d-a));
	    long distance = Math.round(EARTH_MEAN_RADIUS_METERS * c);

	    return distance;
	}
	
	public static DegreeWithPrecision convertLatLongDecimal(int degrees, Integer minutes, Integer seconds) { 
		double deg = degrees;
		int precision = 0;
		if (minutes==null && seconds==null) { 
			deg = degrees;
			precision = 0;
		}
		if (minutes!=null&&seconds==null) { 
		    deg = degrees + (minutes / 60d);
		    precision = 2;
		}
		if (minutes!=null&&seconds==null) { 
		    deg = degrees + (minutes/60d) + ((seconds/60d)/60d);
		    precision = 5;
		}
		return new DegreeWithPrecision(deg,precision);
	}
	
	// Recognize type of string 
	/*  D = degree
	 *  M = minute
	 *  S = second
	 *  N = N/S, E/W 
	 *  - = sign = S/W
	 *  d = degree sign
	 *  
	 *  Decimal Degrees
	 *  DD.DDDDD[d]N
	 *  [-]DD.DDDDD[d]
	 *  
	 *  Decimal Minutes
	 *  DDMM.MMN
	 *  DDd MM.MM'N
	 *  [-]DDd MM.MM
	 *  
	 *  DMS
	 *  DD MM SS.SN
	 *  DDdMM'SS.S"N
	 *  [-]DD MM SS.S
     *  [-]DDdMM'SS.S"
	 *  DD MM SSN
	 *  DDdMM'SS"N
	 *  [-]DD MM SS
     *  [-]DDdMM'SS"
	 */
	
    /**
     * Test to see if an x/y coordinate is inside any of a set of polygons.
     * 
     * @param polygonSet
     * @param Xvalue
     * @param Yvalue
     * @param invertSense true to invert the result, false to keep the result unchanged.
     * 
     * @return true if the x/y value is inside polygonSet and invertSense is false 
     *         false if the x/y value is outside polygonSet and invertSense is false
     *         false if the x/y value is insidePolygonSet and invertSense is true
     *         true if the x/y value is outside polygonSet and invertSense is true
     */
    public static boolean isInPolygon(Set<Path2D> polygonSet, double Xvalue, double Yvalue, boolean invertSense){
        boolean foundInPolygon = GEOUtil.isInPolygon(polygonSet, Xvalue, Yvalue);
        if (invertSense) { foundInPolygon = ! foundInPolygon; } 
        return foundInPolygon;
    } 	
    
    /**
     * Test to see if a point is on land 
     * @param Xvalue a decimal longitude expressed as a double
     * @param Yvalue a decimal latitude expressed as a double 
     * @param invertSense true to invert the result, false to keep the result unchanged.
     *    that is, if invertSense is true, make this a test of is marine.
     * @return true if the x/y value is inside land and invertSense is false 
     *         false if the x/y value is outside land and invertSense is false
     *         false if the x/y value is inside land and invertSense is true
     *         true if the x/y value is outside land and invertSense is true
     */
    public static boolean isOnLand(double Xvalue, double Yvalue, boolean invertSense) { 
    	boolean result = false;
    	
    	GISDataLoader loader = new GISDataLoader();
    	
    	result = loader.pointIsWithinLand(Xvalue, Yvalue, invertSense);
    	
    	return result;
    }
 
    /**
     * Test to see if an x/y coordinate is inside any of a set of polygons.
     * 
     * @param polygonSet
     * @param Xvalue
     * @param Yvalue
     * 
     * @return true if the x/y value is inside polygonSet
     *         false if the x/y value is outside or on a boundary of polygonSet
     */    
    public static boolean isInPolygon(Set<Path2D> polygonSet, double Xvalue, double Yvalue){
        Boolean foundInPolygon = false;
        Iterator it = polygonSet.iterator();
        while(it.hasNext()){
            Path2D poly=(Path2D)it.next();
            if (poly.contains(Xvalue, Yvalue)) {
                //System.out.println("Found in polygon");
                foundInPolygon = true;
            }
        }
        return foundInPolygon;
    }    
	
	public static boolean isPointInCountry(String country, double latitude, double longitude) { 
		boolean result = false;
        URL countryShapeFile = GEOUtil.class.getResource("/org.filteredpush.kuration.services/ne_10m_admin_0_countries.shp");
        FileDataStore store = null;
		try {
			store = FileDataStoreFinder.getDataStore(countryShapeFile);
            SimpleFeatureSource featureSource = store.getFeatureSource();
		    Filter filter = ECQL.toFilter("NAME ILIKE '"+ country +"' AND CONTAINS(the_geom, POINT(" + Double.toString(longitude) + " " + Double.toString(latitude) + "))");
		    SimpleFeatureCollection collection=featureSource.getFeatures(filter);
		    result = !collection.isEmpty();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally { 
			if (store!=null) { store.dispose(); }			
		}
		return result;
	}
	
	/**
	 * Given a decimal latitude and decimal longitude, return the country code for a containing country, including EEZ
	 * for the specified coordinate.
	 * 
	 * @param latitude to check
	 * @param longitude to check
	 * @return an ISO three letter country code, or null if not matched or if multiple matches.
	 */
	public static String getCountryForPoint(String latitude, String longitude) { 
		String result = null;
		URL combinedShapeFile = GEOUtil.class.getResource("/org.filteredpush.kuration.services/merged_countries_and_eez.shp");
        FileDataStore store = null;
		try {
			store = FileDataStoreFinder.getDataStore(combinedShapeFile);
            SimpleFeatureSource featureSource = store.getFeatureSource();
		    Filter filter = ECQL.toFilter("CONTAINS(the_geom, POINT(" + longitude + " " + latitude + "))");
		    logger.debug(filter.toString());
		    SimpleFeatureCollection collection=featureSource.getFeatures(filter);
		    logger.debug(collection.size());
		    if (!collection.isEmpty()) {
		    	if (collection.size()==1) {
		    		SimpleFeature feature = collection.features().next();
		    		result = feature.getAttribute("ISO_SOV1").toString();
		    		if (!GEOUtil.isEmpty(feature.getAttribute("ISO_SOV2").toString())) {
		    			result = null;
		    		}
		    	}  else { 
		    		SimpleFeatureIterator i = collection.features();
		    		SimpleFeature feature = i.next();
		    		String aMatch = feature.getAttribute("ISO_SOV1").toString();
		    		logger.debug(aMatch);
		    		boolean singleMatch = true;
	    			if (!GEOUtil.isEmpty(feature.getAttribute("ISO_SOV2").toString())) {
	    				singleMatch=false;
	    			}
		    		while (i.hasNext() && singleMatch) { 
		    			feature = i.next();
		    			String anotherMatch = feature.getAttribute("ISO_SOV1").toString();
		    			if (!GEOUtil.isEmpty(feature.getAttribute("ISO_SOV2").toString())) {
		    				singleMatch=false;
		    			}
		    			logger.debug(anotherMatch);
		    			if (! aMatch.equals(anotherMatch)) { 
		    				singleMatch = false;
		    			}
		    		}
		    		i.close();
		    		if (singleMatch) {
		    			result = aMatch;
		    		}
		    	}
		    }
		} catch (IOException e) {
			logger.debug(e.getMessage());
		} catch (CQLException e) {
			logger.debug(e.getMessage());
		} finally { 
			if (store!=null) {
				store.dispose(); 
			}			
		}
		return result;
	}
	
	/**
	 * Test to see if a point is near (to a specified distance in km) or within a country.
	 * 
	 * @param country
	 * @param latitude
	 * @param longitude
	 * @param distanceKm
	 * 
	 * @return true if latitude/longitude is inside or within distanceKm of any part of country.
	 */
	public static boolean isPointNearCountry(String country, double latitude, double longitude, double distanceKm) { 
		boolean result = false;
        URL countryShapeFile = GEOUtil.class.getResource("/org.filteredpush.kuration.services/ne_10m_admin_0_countries.shp");
        FileDataStore store = null;
		try {
			store = FileDataStoreFinder.getDataStore(countryShapeFile);
            SimpleFeatureSource featureSource = store.getFeatureSource();
            double distanceD = distanceKm / 111d; // GeoTools ignores units, uses units of underlying projection (degrees in this case), fudge by dividing km by number of km in one degree of latitude (this will describe a wide ellipse far north or south).
		    Filter filter = ECQL.toFilter("NAME ILIKE '"+ country +"' AND DWITHIN(the_geom, POINT(" + Double.toString(longitude) + " " + Double.toString(latitude) + "), "+ distanceD +", kilometers)");
		    SimpleFeatureCollection collection=featureSource.getFeatures(filter);
		    result = !collection.isEmpty();
		} catch (IOException e) {
			logger.debug(e.getMessage());
		} catch (CQLException e) {
			logger.debug(e.getMessage());
		} finally { 
			if (store!=null) { store.dispose(); }			
		}
		return result;
	}	
	
	/**
	 * Test to see if a point is near (to a specified distance in km) or within a country including 
	 * Marine Exclusive Economic Zones.
	 * 
	 * @param countryCode three letter country code
	 * @param latitude of point to check 
	 * @param longitude of point to check
	 * @param distanceKm buffer distance in km.
	 * 
	 * @return true if latitude/longitude is inside or within distanceKm of any part of country or EEZ.
	 */
	public static boolean isPointNearCountryPlusEEZ(String countryCode, double latitude, double longitude, double distanceKm) { 
		boolean result = false;
		URL combinedShapeFile = GEOUtil.class.getResource("/org.filteredpush.kuration.services/merged_countries_and_eez.shp");
		FileDataStore store = null;
		try {
			store = FileDataStoreFinder.getDataStore(combinedShapeFile);
			SimpleFeatureSource featureSource = store.getFeatureSource();
			double distanceD = distanceKm / 111d; // GeoTools ignores units, uses units of underlying projection (degrees in this case), fudge by dividing km by number of km in one degree of latitude (this will describe a wide ellipse far north or south).
			Filter filter = ECQL.toFilter("ISO_SOV1 ILIKE '"+ countryCode +"' AND DWITHIN(the_geom, POINT(" + Double.toString(longitude) + " " + Double.toString(latitude) + "), "+ distanceD +", kilometers)");
			SimpleFeatureCollection collection=featureSource.getFeatures(filter);
			result = !collection.isEmpty();
		} catch (IOException e) {
			logger.debug(e.getMessage());
		} catch (CQLException e) {
			logger.debug(e.getMessage());
		} finally { 
			if (store!=null) { store.dispose(); }			
		}		
		return result;
	}	
	
	/**
	 * Is a given point inside a primary division (state/province) of a given country.
	 * 
	 * @param country
	 * @param primaryDivision
	 * @param latitude
	 * @param longitude
	 * @return 
	 */
	public static boolean isPointInPrimary(String country, String primaryDivision, double latitude, double longitude) { 
		boolean result = false;
        URL countryShapeFile = GEOUtil.class.getResource("/org.filteredpush.kuration.services/ne_10m_admin_1_states_provinces.shp");
        FileDataStore store = null;
		try {
			store = FileDataStoreFinder.getDataStore(countryShapeFile);
            SimpleFeatureSource featureSource = store.getFeatureSource();
            if (country.toLowerCase().equals("united states")) { country = "United States of America"; } 
		    Filter filter = ECQL.toFilter("name ILIKE '"+ primaryDivision.replace("'", "''") +"' AND admin ILIKE '"+ country +"' AND CONTAINS(the_geom, POINT(" + Double.toString(longitude) + " " + Double.toString(latitude) + "))");
		    SimpleFeatureCollection collection=featureSource.getFeatures(filter);
		    result = !collection.isEmpty();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CQLException e) {
			System.out.println("GEOUtil.isPointInPrimary error: " + e.getMessage());
		} finally { 
			if (store!=null) { store.dispose(); }			
		}
		return result;
	}

	/**
	 * Test to see if a point is near (to a specified distance in km) or within a primary division (state/province) of a given country.
	 *
	 * @param country
	 * @param latitude
	 * @param longitude
	 * @param distanceKm
	 *
	 * @return true if latitude/longitude is inside or within distanceKm of a primary division (state/province) of a given country.
	 */
	public static boolean isPointNearPrimary(String country, String primaryDivision, double latitude, double longitude, double distanceKm) {
		boolean result = false;
		URL countryShapeFile = GEOUtil.class.getResource("/org.filteredpush.kuration.services/ne_10m_admin_1_states_provinces.shp");
		FileDataStore store = null;
		try {
			store = FileDataStoreFinder.getDataStore(countryShapeFile);
			SimpleFeatureSource featureSource = store.getFeatureSource();
			if (country.toLowerCase().equals("united states")) { country = "United States of America"; }
			double distanceD = distanceKm / 111d; // GeoTools ignores units, uses units of underlying projection (degrees in this case), fudge by dividing km by number of km in one degree of latitude (this will describe a wide ellipse far north or south).
			Filter filter = ECQL.toFilter("name ILIKE '"+ primaryDivision.replace("'", "''") +"' AND admin ILIKE '"+ country +"' AND DWITHIN(the_geom, POINT(" + Double.toString(longitude) + " " + Double.toString(latitude) + "), "+ distanceD +", kilometers)");
			SimpleFeatureCollection collection=featureSource.getFeatures(filter);
			result = !collection.isEmpty();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (store!=null) { store.dispose(); }
		}
		return result;
	}

	public static boolean isCountryKnown(String country) { 
		boolean result = false;
        URL countryShapeFile = GEOUtil.class.getResource("/org.filteredpush.kuration.services/ne_10m_admin_0_countries.shp");
        FileDataStore store = null;
		try {
			store = FileDataStoreFinder.getDataStore(countryShapeFile);
            SimpleFeatureSource featureSource = store.getFeatureSource();
		    Filter filter = ECQL.toFilter("NAME ILIKE '"+ country +"'");
		    SimpleFeatureCollection collection=featureSource.getFeatures(filter);
		    result = !collection.isEmpty();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally { 
			if (store!=null) { store.dispose(); }
		}
		return result;
	}	
	
	/**
	 * Is a combination of country name and primary division (state/province) name known the primary division data set. 
	 * 
	 * @param country
	 * @param primaryDivision
	 * @return
	 */
	public static boolean isPrimaryKnown(String country, String primaryDivision) { 
		boolean result = false;
        URL countryShapeFile = GEOUtil.class.getResource("/org.filteredpush.kuration.services/ne_10m_admin_1_states_provinces.shp");
        FileDataStore store = null;
		try {
			store = FileDataStoreFinder.getDataStore(countryShapeFile);
            SimpleFeatureSource featureSource = store.getFeatureSource();
            if (country.toLowerCase().equals("united states")) { country = "United States of America"; } 
		    Filter filter = ECQL.toFilter("name ILIKE '"+ primaryDivision.replace("'", "''") +"' AND admin ILIKE '"+ country +"'");
		    // Filter filter = ECQL.toFilter("name ILIKE '"+ primaryDivision +"'");
		    SimpleFeatureCollection collection=featureSource.getFeatures(filter);
		    if (collection!=null && collection.size()>0) { 
		        result = !collection.isEmpty();
		    }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CQLException e) {
			System.out.println("GEOUtil.isPrimaryKnown error: " + e.getMessage());
		} finally { 
			if (store!=null) { store.dispose(); }			
		}
		return result;
	}

	/**
	 * Parse latitude from string and check that value is in range (-90 to 90 inclusive).
	 *
	 * @param latitude
	 * @return parsed latitude or null if not valid
	 */
	public static Double parseLatitude(String latitude) {
		Double lat = null;

		if (latitude != null && !latitude.isEmpty()) {
			try {
				lat = Double.parseDouble(latitude);
			} catch (NumberFormatException e) { /* ignore exception and just return null lat */ }
		}

		if (lat == null || Math.abs(lat) > 90) {
			return null;
		} else {
			return lat;
		}
	}

	/**
	 * Parse longitude from string and check that value is in range (-90 to 90 inclusive).
	 *
	 * @param longitude
	 * @return parsed longitude or null if not valid
	 */
	public static Double parseLongitude(String longitude) {
		Double lon = null;

		if (longitude != null && !longitude.isEmpty()) {
			try {
				lon = Double.parseDouble(longitude);
			} catch (NumberFormatException e) { /* ignore exception and just return null lon */ }
		}

		if (lon == null || Math.abs(lon) > 180) {
			return null;
		} else {
			return lon;
		}
	}


    /**
     * Checks coordinate consistency with country/stateProvince or checks that coordinates are not on land if flagged
     * as a marine locality
     *
     * @param country
     * @param stateProvince
     * @param originalLat
     * @param originalLong
     * @param isMarine
     * @return true if consistent, false otherwise
     */
    public static boolean validateCoordinates(String country, String stateProvince, double originalLat, double originalLong, boolean isMarine) {
        if (!isMarine) {
            // standardize country names
			country = standardizeCountryName(country);

            // Locality not inside country or not inside primary division?
            return GEOUtil.isCountryKnown(country) && GEOUtil.isPointInCountry(country, originalLat, originalLong) &&
                    GEOUtil.isPrimaryKnown(country, stateProvince) &&
                    GEOUtil.isPointInPrimary(country, stateProvince, originalLat, originalLong);
        } else {
            try {
                // Marine locality on land?
            	GISDataLoader loader = new GISDataLoader();
                return !loader.pointIsWithinLand(originalLat, originalLong);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }

        return false;
    }

	public static String standardizeCountryName(String country) {
		if (country.toUpperCase().equals("USA") || country.toUpperCase().equals("U.S.A.") || country.toLowerCase().equals("united states of america")) {
			return "United States";
		} else {
			return country.toUpperCase();
		}
	}

	/**
     * Checks to see if a locality is marine. If country, stateProvince and county are not present, or if waterBody is
     * a known value, the test returns true.
     *
     * @param country
     * @param stateProvince
     * @param county
     * @param waterBody
     * @return true if marine locality, false otherwise
     */
    public static boolean isMarine(String country, String stateProvince, String county, String waterBody) {
        // if no country, stateProvince or county are provided, assume locality is marine
        if ((country == null || country.isEmpty()) && (stateProvince == null || stateProvince.isEmpty()) &&
                (county == null || county.isEmpty())) {
            return true;
        } else if (waterBody != null && waterBody.trim().length() > 0 && waterBody.matches("(Indian|Pacific|Arctic|Atlantic|Ocean|Sea|Carribean|Mediteranian)")) {
            return true;
        }

        return false;
    }
    
    /**
     * Check a presented string against the list of two letter country codes.
     * 
     * @param countryCode a string to test for a case sensitive match against two letter country codes.
     * @return true if an exact match to a country code on the list, otherwise false.
     */
    public static boolean isISOTwoLetterCountryCode(String countryCode)  {

    	// TODO: Lookup and cache current list on startup.
        // Potential sources of current country codes values
        // https://restcountries.eu/#api-endpoints-list-of-codes mentioned in list, but is currently timing out.
        // https://www.iso.org/obp/ui/#search appears to be an api for one item at once.
        // wikidata is a possible source for country codes.
        // list in json is available from https://pkgstore.datahub.io/core/country-list/data_json/data/8c458f2d15d9f2119654b29ede6e45b8/data_json.json
        // see metadata at: https://datahub.io/core/country-list
    	
    	// per wikipedia, as of 2022 Feb 16
    	List<String > isoCodesPerWikipedia = Arrays.asList(new String[]{"AD","AE","AF","AG","AI","AL","AM","AO","AQ","AR","AS","AT","AU","AW","AX","AZ","BA","BB","BD","BE","BF","BG","BH","BI","BJ","BL","BM","BN","BO","BQ","BR","BS","BT","BV","BW","BY","BZ","CA","CC","CD","CF","CG","CH","CI","CK","CL","CM","CN","CO","CR","CU","CV","CW","CX","CY","CZ","DE","DJ","DK","DM","DO","DZ","EC","EE","EG","EH","ER","ES","ET","FI","FJ","FK","FM","FO","FR","GA","GB","GD","GE","GF","GG","GH","GI","GL","GM","GN","GP","GQ","GR","GS","GT","GU","GW","GY","HK","HM","HN","HR","HT","HU","ID","IE","IL","IM","IN","IO","IQ","IR","IS","IT","JE","JM","JO","JP","KE","KG","KH","KI","KM","KN","KP","KR","KW","KY","KZ","LA","LB","LC","LI","LK","LR","LS","LT","LU","LV","LY","MA","MC","MD","ME","MF","MG","MH","MK","ML","MM","MN","MO","MP","MQ","MR","MS","MT","MU","MV","MW","MX","MY","MZ","NA","NC","NE","NF","NG","NI","NL","NO","NP","NR","NU","NZ","OM","PA","PE","PF","PG","PH","PK","PL","PM","PN","PR","PS","PT","PW","PY","QA","RE","RO","RS","RU","RW","SA","SB","SC","SD","SE","SG","SH","SI","SJ","SK","SL","SM","SN","SO","SR","SS","ST","SV","SX","SY","SZ","TC","TD","TF","TG","TH","TJ","TK","TLa","TM","TN","TO","TR","TT","TV","TW","TZ","UA","UG","UM","US","UY","UZ","VA","VC","VE","VG","VI","VN","VU","WF","WS","YE","YT","ZA","ZM","ZW"});
    	
    	boolean result = false;
    	
    	result = isoCodesPerWikipedia.contains(countryCode);
    	
    	return result;
    }

    /**
     * Does a string contain a non-blank value.
     * 
     * @param aString to check
     * @return true if the string is null, is an empty string, 
     *     or contains only whitespace.
     */
    public static boolean isEmpty(String aString)  {
    	boolean result = true;
    	if (aString != null && aString.trim().length()>0) { 
    		// TG2, do not consider string representations of NULL as null, consider as data.
    		//if (!aString.trim().toUpperCase().equals("NULL")) { 
    		   result = false;
    		//}
    	}
    	return result;
    }
    
    
    /**
     * Test to see if a string contains only numeric characters.
     * 
     * @param aString to test
     * @return true if the string, ignoring leading and trailing whitespace,
     *  contains at least one digit 0-9 and no other characters except for - and .
     */
    public static boolean isNumericCharacters(String aString)  {
    	boolean result = false;
    	if (aString != null && aString.trim().matches("^[0-9.-]+$") && aString.matches(".*[0-9]+.*") ) { 
    	   result = true;
    	}
    	return result;
    }

    /** is the provided geodeticDatum on a known list of datums that this software
     * can work with.
     * 
     * @param geodeticDatum string containing a representation of a geodetic datum 
     *    as an EPSG code or as member of a short set of other alternatives.
     * @return true if the geodetic datum is recognized, false otherwise
     */
	public static boolean isDatumKnown(String geodeticDatum) {
		
		boolean retval = false;
		
		AssumeCRS lookupEPSG = new AssumeCRS();
		
		String lookup = geodeticDatum;
		if (lookupEPSG.isTransformable(geodeticDatum)) { 
			lookup = lookupEPSG.getEpsgForDatumAndGCRS(geodeticDatum);
		}
		if (lookup!=null) { 
			//CoordinateOperationFactory factory = new DefaultCoordinateOperationFactory();
			try {
				CoordinateReferenceSystem crsFrom = CRS.decode(lookup);
				logger.debug(crsFrom.getName());
				logger.debug(crsFrom.getCoordinateSystem().getName());
				logger.debug(crsFrom.getCoordinateSystem().getIdentifiers());
				retval = true;
			} catch (NoSuchAuthorityCodeException e) {
				retval = false;
				logger.debug(e.getMessage());
			} catch (FactoryException e) {
				retval = false;
				logger.debug(e.getMessage());
			}
		} 
		return retval;
	}
	
	/**
	 * is the provided geodeticDatum a known EPSG code.
	 * 
	 * @param geodeticDatum string containing a representation of a geodetic datum 
     *    as an EPSG code
	 * @return true if the geodetic datum is recognized, false otherwise
	 * @throws FactoryException 
	 */
	public static boolean isCooridnateSystemCodeKnown(String geodeticDatum) throws FactoryException {
		
		boolean retval = false;
		
		//CoordinateOperationFactory factory = new DefaultCoordinateOperationFactory();
		try {
			CoordinateReferenceSystem crsFrom = CRS.decode(geodeticDatum);
			retval = true;
		} catch (NoSuchAuthorityCodeException e) {
			retval = false;
			logger.debug(e.getMessage());
		} catch (FactoryException ex) { 
			if (ex.getMessage().startsWith("No transform for classification")) { 
				retval = true;
			} else if (ex.getMessage().startsWith("Can't set a value to the parameter")) { 
				retval = true;
			} else { 
				throw(ex);
			}
		}
		return retval;
	}

	public static TransformationStruct coordinateSystemTransformTo4326(
			String sourceY, String sourceX,
			String sourceSRS) throws FactoryException, TransformException
	{
		
		String targetSRS = "EPSG:4326";
		
		TransformationStruct retval = null;
		
		Double lat = Double.parseDouble(sourceY);
		Double lon = Double.parseDouble(sourceX);
		
		org.locationtech.proj4j.CRSFactory crsFactory = new org.locationtech.proj4j.CRSFactory();
		org.locationtech.proj4j.CoordinateReferenceSystem fromCRS = crsFactory.createFromName(sourceSRS);
		org.locationtech.proj4j.CoordinateReferenceSystem toCRS = crsFactory.createFromName(targetSRS);

		org.locationtech.proj4j.CoordinateTransformFactory ctFactory = new org.locationtech.proj4j.CoordinateTransformFactory();
		org.locationtech.proj4j.CoordinateTransform transformation = ctFactory.createTransform(fromCRS, toCRS);
		// `result` is an output parameter to `transform()`
		org.locationtech.proj4j.ProjCoordinate transformed = new org.locationtech.proj4j.ProjCoordinate();
		transformed = transformation.transform(new org.locationtech.proj4j.ProjCoordinate(lon, lat), transformed);
		
		logger.debug(transformed);
		logger.debug(transformed.x);
		logger.debug(transformed.y);
		
		retval = new TransformationStruct();
		retval.setDecimalLatitude(transformed.y);
		retval.setDecimalLongitude(transformed.x);
		retval.setGeodeticDatum(targetSRS);
		//retval.setUncertainty();
		//retval.setPrecision(transformed.getPrecisionModel().getMaximumSignificantDigits());
		retval.setSuccess(true);

		return retval;
	}
		
	public static TransformationStruct datumTransform(
			String decimalLatitude, String decimalLongitude,
			String geodeticDatum, String targetGeodeticDatum) throws FactoryException, TransformException
	{
		TransformationStruct retval = null;
		
		DefaultCoordinateOperationFactory factory = new DefaultCoordinateOperationFactory();
		
		CoordinateReferenceSystem crsFrom = CRS.decode(geodeticDatum);
		CoordinateReferenceSystem crsTarget = CRS.decode(targetGeodeticDatum);
		logger.debug("From: " + crsFrom.getName());
		logger.debug("To: " + crsTarget.getName());
		
		
		Set<CoordinateOperation> operations = CRS.getCoordinateOperationFactory(true).findOperations(crsFrom, crsTarget);
		Iterator<CoordinateOperation> itop = operations.iterator();
		while (itop.hasNext()) { 
			CoordinateOperation operation = itop.next();
			logger.debug(operation.getName());
			logger.debug(operation);
			logger.debug(operation.getCoordinateOperationAccuracy());
		}
		
		CoordinateOperation transform = factory.createOperation(crsFrom, crsTarget);
		
		logger.debug(transform.getName());
		
		Collection<PositionalAccuracy> operationAccuracy = transform.getCoordinateOperationAccuracy();
		Iterator<PositionalAccuracy> it = operationAccuracy.iterator();
		while (it.hasNext()) { 
			PositionalAccuracy pa = it.next();
			logger.debug(pa.getMeasureDescription());
            for (Result result : pa.getResults()) {
                if (result instanceof QuantitativeResult) {
                    for (Record record : ((QuantitativeResult) result).getValues()) {
                    	logger.debug(record);
                    }
                } else {
                	logger.debug(result);
                }
            }
		}

		Position fromPosition = new Position2D(Double.parseDouble(decimalLongitude), Double.parseDouble(decimalLatitude));
		// DirectPosition toPosition = new DirectPosition2D(Double.parseDouble(decimalLongitude), Double.parseDouble(decimalLatitude));
		Position toPosition = new Position2D();
		MathTransform mathTransform = transform.getMathTransform();
		
// 		  MathTransform mtransform = CRS.findMathTransform(crsFrom, crsTarget, true);
//        double[] srcProjec = {Double.parseDouble(decimalLongitude),Double.parseDouble(decimalLatitude)};// x, y, 
//        double[] dstProjec = {0, 0};
//        mtransform.transform(srcProjec, 0, dstProjec, 0, 1);
//        logger.debug("longitude: " + dstProjec[0] + ", latitude: " + dstProjec[1]);
		
		
		logger.debug(fromPosition.toString());
		logger.debug(toPosition.toString());
		Position transformedPosition = null;
		try {
			transformedPosition = mathTransform.transform(fromPosition, toPosition);
		} catch (MismatchedDimensionException ex) { 
			fromPosition = new Position3D(Double.parseDouble(decimalLongitude), Double.parseDouble(decimalLatitude),0d);
			logger.debug(fromPosition.toString());
			transformedPosition = mathTransform.transform(fromPosition, toPosition);
		}
		logger.debug(transformedPosition.toString());
		
		GeometryFactory gf = new GeometryFactory();
		Point point = gf.createPoint(new Coordinate(Double.parseDouble(decimalLongitude), Double.parseDouble(decimalLatitude)));
		Point transformed = (Point) JTS.transform(point, transform.getMathTransform());
		logger.debug(transformed.getPrecisionModel().getMaximumSignificantDigits());
		logger.debug(transformed);
		
		final int dimensions = transformedPosition.getDimension();
		for (int i=0; i<dimensions; i++) {
			logger.debug(transformedPosition.getOrdinate(i));
		}
		if (dimensions==2) {
			double lon = transformedPosition.getOrdinate(0);
			double lat = transformedPosition.getOrdinate(1);
			logger.debug(lon);
			logger.debug(lat);
			lon = transformed.getX();
			lat = transformed.getY();
			logger.debug(lon);
			logger.debug(lat);
			retval = new TransformationStruct();
			retval.setDecimalLatitude(lat);
			retval.setDecimalLongitude(lon);
			retval.setGeodeticDatum(targetGeodeticDatum);
			//retval.setUncertainty();
			retval.setPrecision(transformed.getPrecisionModel().getMaximumSignificantDigits());
			retval.setSuccess(true);
		}
		
		return retval;
	}
    
}

