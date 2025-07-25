package org.filteredpush.qc.georeference.util;

import java.awt.geom.Path2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.measure.Unit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.filteredpush.qc.georeference.SourceAuthorityException;
import org.geotools.api.data.FileDataStore;
import org.geotools.api.data.FileDataStoreFinder;
import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.Property;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.filter.Filter;
import org.geotools.api.metadata.quality.PositionalAccuracy;
import org.geotools.api.metadata.quality.QuantitativeResult;
import org.geotools.api.metadata.quality.Result;
import org.geotools.api.parameter.ParameterNotFoundException;
import org.geotools.api.geometry.MismatchedDimensionException;
import org.geotools.api.geometry.Position;
import org.geotools.geometry.Position2D;
import org.geotools.geometry.Position3D;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.NoSuchAuthorityCodeException;
import org.geotools.api.referencing.ReferenceIdentifier;
import org.geotools.api.referencing.crs.CRSAuthorityFactory;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.crs.GeographicCRS;
import org.geotools.api.referencing.datum.Datum;
import org.geotools.api.referencing.datum.Ellipsoid;
import org.geotools.api.referencing.operation.CoordinateOperation;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.api.util.GenericName;
import org.geotools.api.util.Record;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.geometry.jts.JTS;
import org.geotools.measure.Units;
import org.geotools.referencing.CRS;
import org.geotools.referencing.GeodeticCalculator;
import org.geotools.referencing.operation.DefaultCoordinateOperationFactory;
import org.geotools.referencing.operation.transform.NADCONTransform;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.proj4j.CoordinateTransform;
import org.locationtech.proj4j.CoordinateTransformFactory;


/**
 * <p>GEOUtil class.</p>
 *
 * @author mole
 * @version $Id: $Id
 */
public class GEOUtil {
    private static final Log logger = LogFactory.getLog(GEOUtil.class);

	/**
	 * Equatorial radius of the Earth in kilometers (GRS80).
	 */
	private static double EARTH_EQUATORIAL_RADIUS_KM = 6378.138;
	
	/**
	 * Conversion factor for fathoms to meters, using fathom as 6 imperial feet, not 6 US State Plane Feet.
	 */
	public static double FATHOMS_TO_METERS = 1.8288;
	
	/**
	 * Conversion factor for feet to meters
	 */
	public static double FEET_TO_METERS = 0.3048;
	
	/** 
	 * Conversion factor for yards to meters.
	 */
	public static double YARDS_TO_METERS = 0.9144;
	
	/**
	 * Conversion factor for miles to meters.
	 */
	public static double MILES_TO_METERS = 1609.344;
	
	// GRS80 value for the equatorial radius of the Earth = 6,378,138.0 meters.
	// IERS value for the equatorial radius of the Earth =  6,378,136.3 meters.
	// Wikipedia/Australian Geodetic Datum mean equatorial radius = 6,378,160.0 meters.
	// CRC Mean radius of the Earth: 6370949.0 meters
	
	/**
	 * Mean radius of the Earth in meters (CRC).
	 */
	private static double EARTH_MEAN_RADIUS_METERS = 6370949.0d;  // Mean radius, from CRC
	
	/** 
	 * List of codes that apply to geographic coordinate reference systems
	 */
	public static Set<String> geographicCodes;
	
	/**
	 * Map of names for geographic coordinate reference systems and their EPSG codes.
	 */
	public static Map<String,String> geographicEPSGNamesCodes;
	
	public static Map<String,String> geographicDatumEPSGNamesCodes;
	
	public static Map<String,String> geographicEllipsoidEPSGNamesCodes;
	
	public static Map<String,String> geographicEPSGNamesCodesCI;
	
	public static Map<String,String> geographicDatumEPSGNamesCodesCI;
	
	public static Map<String,String> geographicEllipsoidEPSGNamesCodesCI;
	
	/**
	 * <p>getDistanceKm.</p>
	 *
	 * @param lat1 a double.
	 * @param lng1 a double.
	 * @param lat2 a double.
	 * @param lng2 a double.
	 * @return a double.
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
	
	/**
	 * <p>convertLatLongDecimal.</p>
	 *
	 * @param degrees a int.
	 * @param minutes a {@link java.lang.Integer} object.
	 * @param seconds a {@link java.lang.Integer} object.
	 * @return a {@link org.filteredpush.qc.georeference.util.DegreeWithPrecision} object.
	 */
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
	 * Parse an input latitude or longitute string if it fits a recognized pattern
	 * and return a decimal latitude or longitude value.
	 *
	 * @param input a string containing a verbatim value for a latitude or longitude
	 * @return a decimal degrees representation of the input or null if not able to
	 * interpret.
	 */
	public static Double parseVerbatimLatLongToDecimalDegree(String input) { 
		Double retval = null;
		
		// TODO: support appropriate number of significant digits for d m s and d m.m
		
		logger.debug(input);
		
		if (!GEOUtil.isEmpty(input)) { 
			
			input = input.trim();
			
			if (input.matches("^[0-9]{1,3}([.][0-9]+){0,1}[NnEe °d]*$")) { 
				// DD.DDDDD N
				retval = Double.parseDouble(input.replaceAll("[NnEe °d]",""));
				if (retval > 360d) { 
					retval = null;
				}
				if (input.contains("N") || input.contains("n")) { 
					if (retval > 90) { 
						retval = null;
					}
				}
 			} else if (input.matches("^[-]{0,1}[0-9]{1,3}([.][0-9]+){0,1}[SsWw °d]*$")) { 
				// DD.DDDDD S
				retval = -Double.parseDouble(input.replaceAll("[-SsWw °d]",""));
				if (retval < -180d) {
					retval = null;
				}
				if (input.contains("S") || input.contains("s")) { 
					if (retval < -90) { 
						retval = null;
					}
				}
 			} else if (input.matches("^[0-9]{1,3}[ °d]+[0-9]{1,2}([.][0-9]+){0,1}['NnEe ]*$")) { 
 				//  DDd MM.MM'N
 				String degreeBit = input.split("[ °d]")[0];
 				String minuteBit = input.replaceFirst("^[0-9]{1,3}[ °d]+", "");
				Double degrees = Double.parseDouble(degreeBit.replaceAll("[NnEe °d']",""));
				Double minutes = Double.parseDouble(minuteBit.replaceAll("[NnEe °d']",""));
				retval = degrees + (minutes/60d);
				if (retval > 180d) {
					retval = null;
				}
				if (input.contains("N") || input.contains("n")) { 
					if (retval > 90) { 
						retval = null;
					}
				}
 			} else if (input.matches("^[-]{0,1}[0-9]{1,3}[ °d]+[0-9]{1,2}([.][0-9]+){0,1}['SsWw ]*$")) { 
 				//  DDd MM.MM'S
 				String degreeBit = input.split("[ °d]")[0];
 				String minuteBit = input.replaceFirst("^[0-9]{1,3}[ °d]+", "");
				Double degrees = Double.parseDouble(degreeBit.replaceAll("[-SsWw °d']",""));
				Double minutes = Double.parseDouble(minuteBit.replaceAll("[-SsWw °d']",""));
				retval = -(degrees + (minutes/60d));
				if (retval < -180d) {
					retval = null;
				}
				if (input.contains("N") || input.contains("n")) { 
					if (retval < -90) { 
						retval = null;
					}
				}
 			} else if (input.matches("^([0-9]{1,3})[ °d]+([0-9]{1,2})[' ]+([0-9]{1,2}([.][0-9]+){0,1})[\"+ '\"' + \"NnEe ]*$")) { 
 				//  DDdMM'SS.S"N
 				Pattern dmsPattern = Pattern.compile("^([0-9]{1,3})[ °d]+([0-9]{1,2})[' ]+([0-9]{1,2}([.][0-9]+){0,1})["+ '"' + "NnEe ]*$");
 				Matcher dmsMatcher = dmsPattern.matcher(input);
 				System.out.println(dmsMatcher.matches());
 				System.out.println(dmsMatcher.group(0));
 				String degreeBit = dmsMatcher.group(1);
 				String minuteBit = dmsMatcher.group(2);
 				String secondBit = dmsMatcher.group(3);
				Double degrees = Double.parseDouble(degreeBit);
				Double minutes = Double.parseDouble(minuteBit);
				Double seconds = Double.parseDouble(secondBit);
				retval = degrees + (minutes/60d) + ((seconds/60d)/60d); 
				if (retval > 180d) {
					retval = null;
				}
				if (input.contains("N") || input.contains("n")) { 
					if (retval > 90) { 
						retval = null;
					}
				}
 			} else if (input.matches("^[-]{0,1}([0-9]{1,3})[ °d]+([0-9]{1,2})[' ]+([0-9]{1,2}([.][0-9]+){0,1})[\"+ '\"' + \"SsWw ]*$")) { 
 				//  DDdMM'SS.S"N
 				Pattern dmsPattern = Pattern.compile("^[-]{0,1}([0-9]{1,3})[ °d]+([0-9]{1,2})[' ]+([0-9]{1,2}([.][0-9]+){0,1})["+ '"' + "SsWw ]*$");
 				Matcher dmsMatcher = dmsPattern.matcher(input);
 				logger.debug(dmsMatcher.matches());
 				String degreeBit = dmsMatcher.group(1);
 				String minuteBit = dmsMatcher.group(2);
 				String secondBit = dmsMatcher.group(3);
				Double degrees = Double.parseDouble(degreeBit);
				Double minutes = Double.parseDouble(minuteBit);
				Double seconds = Double.parseDouble(secondBit);
				retval = - (degrees + (minutes/60d) + ((seconds/60d)/60d)); 
				if (retval < -180d) {
					retval = null;
				}
				if (input.contains("S") || input.contains("s")) { 
					if (retval < -90) { 
						retval = null;
					}
				}				
 			} 
		}
		
		return retval;
	}
	 
    /**
     * Test to see if an x/y coordinate is inside any of a set of polygons.
     *
     * @param polygonSet a {@link java.util.Set} object.
     * @param Xvalue a double.
     * @param Yvalue a double.
     * @param invertSense true to invert the result, false to keep the result unchanged.
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
     *
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
     * Test to see if a point is on land, with a spatial buffer
     *
     * @param Xvalue a decimal longitude expressed as a double
     * @param Yvalue a decimal latitude expressed as a double
     * @param invertSense true to invert the result, false to keep the result unchanged.
     *    that is, if invertSense is true, make this a test of is marine.
     * @return true if the x/y value is inside land and invertSense is false
     *         false if the x/y value is outside land and invertSense is false
     *         false if the x/y value is inside land and invertSense is true
     *         true if the x/y value is outside land and invertSense is true
     * @param bufferInMeters a double.
     * @throws SourceAuthorityException 
     */
    public static boolean isOnOrNearLand(double Xvalue, double Yvalue, boolean invertSense, double bufferInMeters) throws SourceAuthorityException { 
    	boolean result = false;
    	
    	double bufferKm = bufferInMeters/1000;
    	
    	GISDataLoader loader = new GISDataLoader();
    	
    	result = loader.pointIsWithinOrNearLand(Xvalue, Yvalue, invertSense,bufferKm);
    	
    	return result;
    }
 
    /**
     * Test to see if an x/y coordinate is inside any of a set of polygons.
     *
     * @param polygonSet a {@link java.util.Set} object.
     * @param Xvalue a double.
     * @param Yvalue a double.
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
	
	/**
	 * <p>isPointInCountry.</p>
	 *
	 * @param country a {@link java.lang.String} object.
	 * @param latitude a double.
	 * @param longitude a double.
	 * @return a boolean.
	 */
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
		    featureSource.getFeatures().features().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally { 
			if (store!=null) { 
				store.dispose(); 
			}			
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
		    	featureSource.getDataStore().dispose();
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
	 * @param country a {@link java.lang.String} object.
	 * @param latitude a double.
	 * @param longitude a double.
	 * @param distanceKm a double.
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
		    SimpleFeatureIterator i = collection.features();
		    i.close();
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
			featureSource.getDataStore().dispose();
		    SimpleFeatureIterator i = collection.features();
		    i.close();
		} catch (IOException e) {
			logger.debug(e.getMessage());
		} catch (CQLException e) {
			logger.debug(e.getMessage());
		} finally { 
			if (store!=null) { 
				try { 
					store.dispose();
				} catch (Exception e) { 
					logger.error(e.getMessage());
				}
			}			
		}		
		return result;
	}	
	
	/**
	 * Is a given point inside a primary division (state/province) of a given country.
	 *
	 * @param country a {@link java.lang.String} object.
	 * @param primaryDivision a {@link java.lang.String} object.
	 * @param latitude a double.
	 * @param longitude a double.
	 * @return a boolean.
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
	 * @param country a {@link java.lang.String} object.
	 * @param latitude a double.
	 * @param longitude a double.
	 * @param distanceKm a double.
	 * @return true if latitude/longitude is inside or within distanceKm of a primary division (state/province) of a given country.
	 * @param primaryDivision a {@link java.lang.String} object.
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
	
	/**
	 * Test to see if a point is near (to a specified distance in km) or within a
	 * primary division (state/province) without specifying the country.
	 *
	 * @param primaryDivision the primary division to look up.
	 * @param latitude a double.
	 * @param longitude a double.
	 * @param distanceKm a double.
	 * @return true if latitude/longitude is inside or within distanceKm of a primary division (state/province) of a given country.
	 */
	public static boolean isPointNearPrimaryAllowDuplicates(String primaryDivision, double latitude, double longitude, double distanceKm) {
		boolean result = false;
		URL countryShapeFile = GEOUtil.class.getResource("/org.filteredpush.kuration.services/ne_10m_admin_1_states_provinces.shp");
		FileDataStore store = null;
		try {
			store = FileDataStoreFinder.getDataStore(countryShapeFile);
			SimpleFeatureSource featureSource = store.getFeatureSource();
			double distanceD = distanceKm / 111d; // GeoTools ignores units, uses units of underlying projection (degrees in this case), fudge by dividing km by number of km in one degree of latitude (this will describe a wide ellipse far north or south).
			
            String sanitized =  primaryDivision.replace("'", "''");
            StringBuffer filterString = new StringBuffer();
            filterString.append("(");
            filterString.append("name ILIKE '"+ sanitized +"' ");
            filterString.append("OR name_alt ILIKE '"+ sanitized +"' ");
            filterString.append("OR name_local ILIKE '"+ sanitized +"' ");
            filterString.append("OR gn_name ILIKE '"+ sanitized +"' ");
            filterString.append("OR gns_name ILIKE '"+ sanitized +"' ");
            filterString.append("OR woe_label ILIKE '"+ sanitized +"%' ");
            filterString.append("OR woe_name ILIKE '"+ sanitized +"%'");
            filterString.append(") AND (");
			filterString.append("DWITHIN(the_geom, POINT(" + Double.toString(longitude) + " " + Double.toString(latitude) + "), "+ distanceD +", kilometers)");
            filterString.append(")");
            logger.debug(filterString);
            Filter filter = ECQL.toFilter(filterString.toString());
			SimpleFeatureCollection collection=featureSource.getFeatures(filter);
			result = !collection.isEmpty();
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
	 * <p>isCountryKnown.</p>
	 *
	 * @param country a {@link java.lang.String} object.
	 * @return a boolean.
	 */
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
	 * @param country a {@link java.lang.String} object.
	 * @param primaryDivision a {@link java.lang.String} object.
	 * @return a boolean.
	 */
	public static boolean isPrimaryKnown(String country, String primaryDivision) { 
		boolean result = false;
        URL countryShapeFile = GEOUtil.class.getResource("/org.filteredpush.kuration.services/ne_10m_admin_1_states_provinces.shp");
        FileDataStore store = null;
		try {
			store = FileDataStoreFinder.getDataStore(countryShapeFile);
            SimpleFeatureSource featureSource = store.getFeatureSource();
            if (country.toLowerCase().equals("united states")) { country = "United States of America"; } 
            
            String sanitizedPrimary =  primaryDivision.replace("'", "''");
            StringBuffer filterString = new StringBuffer();
            filterString.append("( ");
		    filterString.append("name ILIKE '"+ sanitizedPrimary +"' "); 
            filterString.append("OR name_alt ILIKE '"+ sanitizedPrimary +"' ");
            filterString.append("OR name_local ILIKE '"+ sanitizedPrimary +"' ");
            filterString.append("OR woe_label ILIKE '"+ sanitizedPrimary +"%' ");
            filterString.append("OR woe_name ILIKE '"+ sanitizedPrimary +"'");
            filterString.append(") AND (");
		    filterString.append("admin ILIKE '"+ country +"'");
            filterString.append("OR woe_label ILIKE '%"+ country +"' ");
            filterString.append(") ");
            
		    Filter filter = ECQL.toFilter(filterString.toString());
		    // Filter filter = ECQL.toFilter("name ILIKE '"+ primaryDivision +"'");
		    SimpleFeatureCollection collection=featureSource.getFeatures(filter);
		    if (collection!=null && collection.size()>0) { 
		        result = !collection.isEmpty();
		    }
		} catch (IOException e) {
			logger.debug(e.getMessage(),e);
		} catch (CQLException e) {
			System.out.println("GEOUtil.isPrimaryKnown error: " + e.getMessage());
		} finally { 
			if (store!=null) { store.dispose(); }			
		}
		return result;
	}
	
	/**
	 * Is a primary division (state/province) name known the primary division data set
	 * within any country.
	 *
	 * @param primaryDivision the state/province to look up.
	 * @return a boolean true if found, otherwise false.
	 */
	public static boolean isPrimaryAloneKnown(String primaryDivision) { 
		boolean result = false;
        URL countryShapeFile = GEOUtil.class.getResource("/org.filteredpush.kuration.services/ne_10m_admin_1_states_provinces.shp");
        FileDataStore store = null;
		try {
			store = FileDataStoreFinder.getDataStore(countryShapeFile);
            SimpleFeatureSource featureSource = store.getFeatureSource();
            
            String sanitized =  primaryDivision.replace("'", "''");
            StringBuffer filterString = new StringBuffer();
            filterString.append("name ILIKE '"+ sanitized +"' ");
            filterString.append("OR name_alt ILIKE '"+ sanitized +"' ");
            filterString.append("OR name_local ILIKE '"+ sanitized +"' ");
            filterString.append("OR gn_name ILIKE '"+ sanitized +"' ");
            filterString.append("OR gns_name ILIKE '"+ sanitized +"' ");
            filterString.append("OR woe_label ILIKE '"+ sanitized +"%' ");
            filterString.append("OR woe_name ILIKE '"+ sanitized +"%'");
            logger.debug(filterString);
            Filter filter = ECQL.toFilter(filterString.toString());
		    // Filter filter = ECQL.toFilter("name ILIKE '"+ primaryDivision +"'");
		    SimpleFeatureCollection collection=featureSource.getFeatures(filter);
		    logger.debug(collection.size());
		    if (collection!=null && collection.size()>0) { 
		        result = !collection.isEmpty();
		    }
		} catch (IOException e) {
			logger.debug(e.getMessage(),e);
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
	 * @param latitude a {@link java.lang.String} object.
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
	 * @param longitude a {@link java.lang.String} object.
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
     * @param country a {@link java.lang.String} object.
     * @param stateProvince a {@link java.lang.String} object.
     * @param originalLat a double.
     * @param originalLong a double.
     * @param isMarine a boolean.
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

	/**
	 * <p>standardizeCountryName.</p>
	 *
	 * @param country a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
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
     * @param country a {@link java.lang.String} object.
     * @param stateProvince a {@link java.lang.String} object.
     * @param county a {@link java.lang.String} object.
     * @param waterBody a {@link java.lang.String} object.
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
    	// List<String > isoCodesPerWikipedia = Arrays.asList(new String[]{"AD","AE","AF","AG","AI","AL","AM","AO","AQ","AR","AS","AT","AU","AW","AX","AZ","BA","BB","BD","BE","BF","BG","BH","BI","BJ","BL","BM","BN","BO","BQ","BR","BS","BT","BV","BW","BY","BZ","CA","CC","CD","CF","CG","CH","CI","CK","CL","CM","CN","CO","CR","CU","CV","CW","CX","CY","CZ","DE","DJ","DK","DM","DO","DZ","EC","EE","EG","EH","ER","ES","ET","FI","FJ","FK","FM","FO","FR","GA","GB","GD","GE","GF","GG","GH","GI","GL","GM","GN","GP","GQ","GR","GS","GT","GU","GW","GY","HK","HM","HN","HR","HT","HU","ID","IE","IL","IM","IN","IO","IQ","IR","IS","IT","JE","JM","JO","JP","KE","KG","KH","KI","KM","KN","KP","KR","KW","KY","KZ","LA","LB","LC","LI","LK","LR","LS","LT","LU","LV","LY","MA","MC","MD","ME","MF","MG","MH","MK","ML","MM","MN","MO","MP","MQ","MR","MS","MT","MU","MV","MW","MX","MY","MZ","NA","NC","NE","NF","NG","NI","NL","NO","NP","NR","NU","NZ","OM","PA","PE","PF","PG","PH","PK","PL","PM","PN","PR","PS","PT","PW","PY","QA","RE","RO","RS","RU","RW","SA","SB","SC","SD","SE","SG","SH","SI","SJ","SK","SL","SM","SN","SO","SR","SS","ST","SV","SX","SY","SZ","TC","TD","TF","TG","TH","TJ","TK","TLa","TM","TN","TO","TR","TT","TV","TW","TZ","UA","UG","UM","US","UY","UZ","VA","VC","VE","VG","VI","VN","VU","WF","WS","YE","YT","ZA","ZM","ZW"});
    	// plus, per note in https://github.com/tdwg/bdq/issues/20 2024-08-10
    	// Including ZZ unkn and XZ.
    	List<String > isoCodesPerWikipedia = Arrays.asList(new String[]{"ZZ","XZ","AD","AE","AF","AG","AI","AL","AM","AO","AQ","AR","AS","AT","AU","AW","AX","AZ","BA","BB","BD","BE","BF","BG","BH","BI","BJ","BL","BM","BN","BO","BQ","BR","BS","BT","BV","BW","BY","BZ","CA","CC","CD","CF","CG","CH","CI","CK","CL","CM","CN","CO","CR","CU","CV","CW","CX","CY","CZ","DE","DJ","DK","DM","DO","DZ","EC","EE","EG","EH","ER","ES","ET","FI","FJ","FK","FM","FO","FR","GA","GB","GD","GE","GF","GG","GH","GI","GL","GM","GN","GP","GQ","GR","GS","GT","GU","GW","GY","HK","HM","HN","HR","HT","HU","ID","IE","IL","IM","IN","IO","IQ","IR","IS","IT","JE","JM","JO","JP","KE","KG","KH","KI","KM","KN","KP","KR","KW","KY","KZ","LA","LB","LC","LI","LK","LR","LS","LT","LU","LV","LY","MA","MC","MD","ME","MF","MG","MH","MK","ML","MM","MN","MO","MP","MQ","MR","MS","MT","MU","MV","MW","MX","MY","MZ","NA","NC","NE","NF","NG","NI","NL","NO","NP","NR","NU","NZ","OM","PA","PE","PF","PG","PH","PK","PL","PM","PN","PR","PS","PT","PW","PY","QA","RE","RO","RS","RU","RW","SA","SB","SC","SD","SE","SG","SH","SI","SJ","SK","SL","SM","SN","SO","SR","SS","ST","SV","SX","SY","SZ","TC","TD","TF","TG","TH","TJ","TK","TLa","TM","TN","TO","TR","TT","TV","TW","TZ","UA","UG","UM","US","UY","UZ","VA","VC","VE","VG","VI","VN","VU","WF","WS","YE","YT","ZA","ZM","ZW"});
    	
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

	/**
	 * is the provided geodeticDatum on a known list of datums that this software
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
	
	public static boolean isValidEPSGCodeForDwCgeodeticDatum(String geodeticDatum) { 
		boolean retval = false;
		
		if (geographicCodes==null) { 
			setupGeographicEPSGCodesList();
		}
		
		logger.debug(geodeticDatum);
		
		if (geographicEPSGNamesCodes.containsValue(geodeticDatum) || 
				geographicDatumEPSGNamesCodes.containsValue(geodeticDatum) || 
				geographicEllipsoidEPSGNamesCodes.containsValue(geodeticDatum)) 
		{ 
			retval = true;
		}
		return retval;
	}
	
	public static boolean isKnownNameForDwCgeodeticDatum(String geodeticDatum) { 
		boolean retval = false;
		
		if (geographicCodes==null) { 
			setupGeographicEPSGCodesList();
		}
		
		if (!geodeticDatum.startsWith("EPSG:")) { 
			geodeticDatum = "EPSG:"+geodeticDatum;
		}
		
		if (geographicEPSGNamesCodes.containsKey(geodeticDatum) || 
				geographicDatumEPSGNamesCodes.containsKey(geodeticDatum) || 
				geographicEllipsoidEPSGNamesCodes.containsKey(geodeticDatum)) 
		{ 
			retval = true;
		}
		return retval;
	}
	
	/**
	 * Check if a text string is a case and space insensitive match for the name of an EPSG code 
	 * appropriate for dwc:geodeticDatum (Geographic CRS, or datum or ellipsoid thereof).
	 * 
	 * @param geodeticDatum to test
	 * @return true if matched to a name, ignoring case and spaces, otherwise false.
	 */
	public static boolean isKnownNameForDwCgeodeticDatumCaseInsensitive(String geodeticDatum) { 
		boolean retval = false;
		
		if (geographicCodes==null) { 
			setupGeographicEPSGCodesList();
		}
		
		if (!geodeticDatum.startsWith("EPSG:")) { 
			geodeticDatum = "EPSG:"+geodeticDatum;
		}
		
		geodeticDatum = geodeticDatum.toUpperCase().replace(" ","");
		
		if (geographicEPSGNamesCodesCI.containsKey(geodeticDatum) || 
				geographicDatumEPSGNamesCodesCI.containsKey(geodeticDatum) || 
				geographicEllipsoidEPSGNamesCodesCI.containsKey(geodeticDatum)) 
		{ 
			retval = true;
		}
		return retval;
	}
	
	/** 
	 * setup a list of known EPSG codes for geographic coordinate
     * references systems and their names, sets up the Map geographicEPSGNamesCodes,
     * which is a map of the names of the geographic CRSs to their EPSG codes.
	 * 
	 */
	public static final void setupGeographicEPSGCodesList() { 

		geographicEPSGNamesCodes = new HashMap<String,String>();
		geographicDatumEPSGNamesCodes = new HashMap<String,String>();
		geographicEllipsoidEPSGNamesCodes = new HashMap<String,String>();
		geographicEPSGNamesCodesCI = new HashMap<String,String>();
		geographicDatumEPSGNamesCodesCI = new HashMap<String,String>();
		geographicEllipsoidEPSGNamesCodesCI = new HashMap<String,String>();
		
		CRSAuthorityFactory factory = CRS.getAuthorityFactory(true);
		try {
			geographicCodes = factory.getAuthorityCodes(GeographicCRS.class);
			
			Iterator<String> itt = geographicCodes.iterator()	;
			while (itt.hasNext()) { 
				String code = itt.next();
				if (code.startsWith("EPSG:")) { 
					try { 
						GeographicCRS epsgGeoCRS = factory.createGeographicCRS(code);
						ReferenceIdentifier name = epsgGeoCRS.getName();
						if (epsgGeoCRS.getCoordinateSystem().getDimension()==2 && epsgGeoCRS.getCoordinateSystem().getAxis(0).getUnit()==Units.DEGREE_ANGLE ) { 
							if (!geographicEPSGNamesCodes.containsKey(name.toString())) { 
								geographicEPSGNamesCodes.put(name.toString(),code);
								geographicEPSGNamesCodesCI.put(name.toString().toUpperCase().replace(" ", ""),code);
								//if (name.toString().contains("84")) { 
								//    System.out.println(code + " " + name.toString().replace("EPSG:", ""));
								//}
							}
							String ellipsoid = epsgGeoCRS.getDatum().getEllipsoid().getName().toString();
						    String ellipsoidCode = epsgGeoCRS.getDatum().getEllipsoid().getIdentifiers().toArray()[0].toString();
							if (!geographicEllipsoidEPSGNamesCodes.containsKey(ellipsoid)) { 
								geographicEllipsoidEPSGNamesCodes.put(ellipsoid, ellipsoidCode);
								geographicEllipsoidEPSGNamesCodesCI.put(ellipsoid.toUpperCase().replace(" ", "") , ellipsoidCode);
								//System.out.println(ellipsoidCode + " " + ellipsoid.replace("EPSG:", ""));
							}
							String datum = epsgGeoCRS.getDatum().getName().toString();
						    String datumCode = epsgGeoCRS.getDatum().getIdentifiers().toArray()[0].toString();
							if (!geographicDatumEPSGNamesCodes.containsKey(datum)) { 
								geographicDatumEPSGNamesCodes.put(datum, datumCode);
								geographicDatumEPSGNamesCodesCI.put(datum.toUpperCase().replace(" ", "") , datumCode);
								//System.out.println(datumCode + " " + datum.replace("EPSG:", ""));
							}
						}
					} catch (FactoryException fe) { 
						// deprecated codes with units degree minute hemisphere
						// throw unsuported unit exception
					}
				}
			}
			//logger.debug(geographicDatumEPSGNamesCodes.size());
			//logger.debug(geographicEllipsoidEPSGNamesCodes.size());
		} catch (FactoryException e) {
			logger.error(e.getMessage());
		}

		
		
	}
	
	/**
	 * get the EPSG code for a geodetic datum.
	 *
	 * @param geodeticDatum string containing a representation of a 
	 *  geographic coordinate reference system (CRS) or datum
 	 *  as would be found as a value of dwc:geodeticDatum,
	 *  in the form of a text string such as 'WGS84'.
	 * @return the EPSG code for the geodetic datum if it is known, null otherwise.
	 */
	public static String getEPSGCodeForString(String geodeticDatum) { 
		String retval = null;

		CRSAuthorityFactory factory = CRS.getAuthorityFactory(true);
		if (geographicCodes==null) { 
			setupGeographicEPSGCodesList();
		}
		Iterator<String> i = geographicEPSGNamesCodes.keySet().iterator();
		while (i.hasNext()) { 
			String namest = i.next();
			String prefixed = "EPSG:" + geodeticDatum;
			if (namest.equals(prefixed) && retval == null) { 
				retval = geographicEPSGNamesCodes.get(namest);
			} 
			if (retval == null && namest.toLowerCase().replace(" ","").matches(prefixed.toLowerCase().replace(" ", ""))) { 
				retval = geographicEPSGNamesCodes.get(namest);
			}
		}

		return retval;
	}
	
    /**
	 * isGeographicCRSCode, test if a dwc:geodeticDatum value is a
	 * known geographic CRS code.
	 *
	 * @param geodeticDatum a {@link java.lang.String} object containing an EPSG code, in the
     * form authority:number, that is prefixed with "EPSG:"
	 * @return true if the geodetic datum is a known geographic CRS code, false otherwise
	 * @throws org.geotools.api.referencing.FactoryException if any.
	 */
	public static boolean isGeographicCRSCode(String geodeticDatum) throws FactoryException {
		boolean retval = false;
		try { 
			CoordinateReferenceSystem crsFrom = CRS.decode(geodeticDatum);
		    retval = crsFrom instanceof GeographicCRS;
		    logger.debug(crsFrom.getCoordinateSystem().getAxis(0).getUnit().toString());
		} catch (NoSuchAuthorityCodeException e) { 
			logger.debug(e.getMessage());
		}
		return retval;
	}
	
    /**
	 * isGeographicCRSCodeDegrees, test if a dwc:geodeticDatum value is a
	 * known CRS code for a geographic coordinate reference system with 
     * units of degrees.
	 *
	 * @param geodeticDatum a {@link java.lang.String} object containing an EPSG code, in the
     * form authority:number, that is prefixed with "EPSG:"
	 * @return true if the geodetic datum is a known geographic CRS code who's units are degreees, false otherwise
	 * @throws org.geotools.api.referencing.FactoryException if any.
	 */
	public static boolean isGeographicCRSCodeDegrees(String geodeticDatum) throws FactoryException {
		boolean retval = false;
		try { 
			CoordinateReferenceSystem crsFrom = CRS.decode(geodeticDatum);
		    if (crsFrom instanceof GeographicCRS && crsFrom.getCoordinateSystem().getAxis(0).getUnit()==Units.DEGREE_ANGLE ) { 
		    	retval=true;
		    }
		} catch (NoSuchAuthorityCodeException e) { 
			logger.debug(e.getMessage());
		}
		return retval;
	}
	
	/**
	 * is the provided geodeticDatum a known EPSG code.
	 *
	 * @param geodeticDatum string containing a representation of a geodetic datum
	 *    as an EPSG code
	 * @return true if the geodetic datum is recognized, false otherwise
	 * @throws org.geotools.api.referencing.FactoryException if any.
	 */
	public static boolean isCoordinateSystemCodeKnown(String geodeticDatum) throws FactoryException {
		
		boolean retval = false;
		
		logger.debug(geodeticDatum);
		
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

	/**
	 * <p>getNadconTransform.</p>
	 *
	 * @param latGridFile a {@link java.lang.String} object.
	 * @param longGridFile a {@link java.lang.String} object.
	 * @return a {@link org.geotools.referencing.operation.transform.NADCONTransform} object.
	 * @throws org.geotools.api.parameter.ParameterNotFoundException if any.
	 * @throws org.geotools.api.referencing.FactoryException if any.
	 */
	public static NADCONTransform getNadconTransform(String latGridFile, String longGridFile) throws ParameterNotFoundException, FactoryException {
		File fLat = new File(latGridFile);
		File fLong = new File(longGridFile);

		if (!fLat.exists()) {
			throw new IllegalArgumentException(latGridFile +" does not exist");
		}
		if (!fLong.exists()) {
			throw new IllegalArgumentException(longGridFile+" does not exist");
		}

		URI uriLat = fLat.toURI();
		URI uriLong = fLong.toURI();

		NADCONTransform transform = new NADCONTransform(uriLat, uriLong);
		return transform;

	}
	
	
	/**
	 * <p>externalTransforTo4326.</p>
	 *
	 * @param sourceY a {@link java.lang.String} object.
	 * @param sourceX a {@link java.lang.String} object.
	 * @param sourceSRS a {@link java.lang.String} object.
	 * @return a {@link org.filteredpush.qc.georeference.util.TransformationStruct} object.
	 */
	public static TransformationStruct externalTransformTo4326(String sourceY, String sourceX, String sourceSRS) { 
		
		TransformationStruct retval = null;
		String targetSRS = "EPSG:4326";
		
		String sourceDatum = null;
		if (sourceSRS.equals("EPSG:4267")) { 
			sourceDatum = "NAD27";
		}
		
		CoordinateReferenceSystem crsFrom;
		try {
			crsFrom = CRS.decode(sourceSRS);
			logger.debug(crsFrom.getName());
			logger.debug(crsFrom.getName().getCode());
			sourceDatum = crsFrom.getName().getCode();
		} catch (NoSuchAuthorityCodeException e) {
			logger.debug(e.getMessage());
		} catch (FactoryException e) {
			logger.debug(e.getMessage());
		}
		
		// echo "-71.1474181 42.3836864" | cs2cs +proj=latlong +datum=NAD27 +to +proj=latlong +datum=WGS84 -f %.12f - 
		
		
		StringBuilder shellCommand = new StringBuilder();
		
		shellCommand.append("/usr/bin/echo").append(" \"").append(sourceX).append(" ").append(sourceY).append("\"");
		shellCommand.append(" | ");
		shellCommand.append("/usr/bin/cs2cs +proj=latlong +datum=").append(sourceDatum).append(" +to +proj=latlong +datum=WGS84 -f %.12f - ");
		
		// build command as string array to allow pipe of echo within shellCommand to work within shell.
		String[] cmd = { "/bin/sh", "-c", shellCommand.toString() };
		
		logger.debug(shellCommand);
		
		try {
			Process shell = Runtime.getRuntime().exec(cmd);
		    BufferedReader errorReader = new BufferedReader(new InputStreamReader(shell.getErrorStream()));
		    String line = "";
		    while ((line = errorReader.readLine()) != null) {
		    	logger.debug(line);
		    }
		    BufferedReader reader = new BufferedReader(new InputStreamReader(shell.getInputStream()));
		    line = "";
		    while ((line = reader.readLine()) != null) {
		    	logger.debug(line);
		    	String[] bits = StringUtils.split(line);
		    	if (bits.length==3) { 
		    		retval = new TransformationStruct();
		    		retval.setDecimalLatitude(Double.parseDouble(bits[1]));
		    		retval.setDecimalLongitude(Double.parseDouble(bits[0]));
		    		retval.setGeodeticDatum(targetSRS);
		    		//retval.setUncertainty();
		    		//retval.setPrecision(transformed.getPrecisionModel().getMaximumSignificantDigits());
		    		retval.setSuccess(true);
		    	}
		    }
			if (shell.exitValue()!=0) {
				logger.error("Process completed with an error.");
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		
		return retval;
	}
	
	/**
	 * <p>coordinateSystemTransformTo4326.</p>
	 *
	 * @param sourceY a {@link java.lang.String} object.
	 * @param sourceX a {@link java.lang.String} object.
	 * @param sourceSRS a {@link java.lang.String} object.
	 * @return a {@link org.filteredpush.qc.georeference.util.TransformationStruct} object.
	 * @throws org.geotools.api.referencing.FactoryException if any.
	 * @throws org.geotools.api.referencing.operation.TransformException if any.
	 */
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
		
		String WGS84_PARAM = "+title=long/lat:WGS84 +proj=longlat +ellps=WGS84 +datum=WGS84 +units=degrees";
		org.locationtech.proj4j.CoordinateReferenceSystem WGS84 = crsFactory.createFromParameters("WGS84", WGS84_PARAM);
		toCRS = WGS84;

		org.locationtech.proj4j.CoordinateTransformFactory ctFactory = new org.locationtech.proj4j.CoordinateTransformFactory();
		org.locationtech.proj4j.CoordinateTransform transformation = ctFactory.createTransform(fromCRS, toCRS);
		if (sourceSRS.equals("4267")) { 
			// newer replacement is https://github.com/noaa-ngs/ncat-lib
			
			// for files see: https://www.ngs.noaa.gov/PC_PROD/NADCON/
			transformation = (CoordinateTransform) GEOUtil.getNadconTransform("/counus.las","/conus.los");
		}
		logger.debug(transformation.getSourceCRS());
		logger.debug(transformation.getTargetCRS());
		logger.debug(transformation.getSourceCRS().isGeographic());
		logger.debug(transformation.getTargetCRS().isGeographic());
		logger.debug(transformation.getSourceCRS().getDatum());
		logger.debug(transformation.getTargetCRS().getDatum());
		
		org.locationtech.proj4j.ProjCoordinate transformed = new org.locationtech.proj4j.ProjCoordinate();
		logger.debug(transformed);
		
		transformation.transform(new org.locationtech.proj4j.ProjCoordinate(lon, lat), transformed);
		
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
		
	/**
	 * <p>datumTransform.</p>
	 *
	 * @param decimalLatitude a {@link java.lang.String} object.
	 * @param decimalLongitude a {@link java.lang.String} object.
	 * @param geodeticDatum a {@link java.lang.String} object.
	 * @param targetGeodeticDatum a {@link java.lang.String} object.
	 * @return a {@link org.filteredpush.qc.georeference.util.TransformationStruct} object.
	 * @throws org.geotools.api.referencing.FactoryException if any.
	 * @throws org.geotools.api.referencing.operation.TransformException if any.
	 */
	public static TransformationStruct datumTransform(
			String decimalLatitude, String decimalLongitude,
			String geodeticDatum, String targetGeodeticDatum) throws FactoryException, TransformException
	{
		TransformationStruct retval = null;
		
		if (geodeticDatum.equals("EPSG:4267") && targetGeodeticDatum.equals("EPSG:4326")) { 
			return externalTransformTo4326(decimalLatitude, decimalLongitude, geodeticDatum);
		} else { 
		
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
		
		}
		
		return retval;
	}
	
	/**
	 * shorten the strings degrees, minutes, seconds, north, south,
	 * east, west in a verbatim geographic coordinate string to one
	 * character each
	 *
	 * @param coordinate to simplify
	 * @return input coordinate with selected words replaced by single characters.
	 */
	public static String simplifyVerbatimCoordinate(String coordinate) {
		String retval = coordinate;
		if (retval!=null) { 
			if (retval.contains("degrees")) {
				retval = retval.replace("degrees", "°");
			}
			if (retval.contains("minutes")) {
				retval = retval.replace("minutes", "'");
			}
			if (retval.contains("seconds")) {
				retval = retval.replace("seconds", "\"");
			}
			if (retval.toUpperCase().contains("NORTH")) { 
				retval = retval.replaceAll("(?i)north", "N");
			}
			if (retval.toUpperCase().contains("SOUTH")) { 
				retval = retval.replaceAll("(?i)south", "S");
			}
			if (retval.toUpperCase().contains("EAST")) { 
				retval = retval.replaceAll("(?i)east", "E");
			}
			if (retval.toUpperCase().contains("WEST")) { 
				retval = retval.replaceAll("(?i)west", "W");
			}
			logger.debug(retval);
			if (!retval.contains(",")) {
				logger.debug(retval);
				// add separator if verbatim coordinates does not contain one
				if (retval.matches(".+N [0-9].*")) { 
					retval = retval.replace("N ", "N, ");
				}
				if (retval.matches(".+S [0-9].*")) { 
					retval = retval.replace("S ", "S, ");
				}
				if (retval.matches(".+E [0-9].*")) { 
					retval = retval.replace("E ", "E, ");
				}
				if (retval.matches(".+W [0-9].*")) { 
					retval = retval.replace("W ", "W, ");
				}
			} 
		}
		return retval;
	}
    
	/**
	 * Convert a UTM coordinate, assuming a WGS84 datum, to a latitude
	 * and longitude, EPSG:4256
	 *
	 * TODO: Add UPS support
	 *
	 * @param utmCoordinate to convert
	 * @param useFormalNS if true, accept only N and S as hemisphere letters,
	 * not latitude band letters C-X, if false, treat N and S as band letters.
	 * @return a GeolocaitonResult object containing latitude, longitude
	 * or null if unable to convert.
	 */
	public static GeolocationResult convertUTMToLatLong(String utmCoordinate, boolean useFormalNS) {
		GeolocationResult retval = null;

		if (!GEOUtil.isEmpty(utmCoordinate)) { 
			if (utmCoordinate.matches("^[0-9]{1,2}[ABYZ][0-9 ]+$")) {
				// UPS coordinate
				// TODO: Support conversion
			} else { 
				String regex = "([0-9]{1,2})([C–HJ-NP-X]) *([0-9]{7}) *([0-9){7})";
				if (utmCoordinate.matches(regex)) { 
					// Fits pattern of a UTM coordinate, split into components
					Pattern utmPattern = Pattern.compile(regex);
					Matcher utmMatcher = utmPattern.matcher(utmCoordinate);
					try { 
						Integer zone = Integer.parseInt(utmMatcher.group(1));
						String letter = utmMatcher.group(2);
						Double easting = Double.parseDouble(utmMatcher.group(3));
						Double northing = Double.parseDouble(utmMatcher.group(4));
						if (useFormalNS) { 
							// letter must be N or S for hemisphere
							if (letter.equals("N")) { 
								// northing is in northern hemisphere, don't change.
							} else if (letter.equals("S")) { 
								// northing is in southern hemisphere, subtract 10000000 meters 
								northing = northing-10000000;
							} else { 
								throw new Exception("Letter not N or S for hemisphere");
							}
						} else { 
							// assume letters are latitude band letters
							if (letter.matches("[MNP-X]")) { 
								// northing is in northern hemisphere, don't change.
							} else if (letter.matches("[C–HJ-L]")) { 
								// northing is in southern hemisphere, subtract 10000000 meters 
								northing = northing-10000000;
							} 
						}
						// see https://en.wikipedia.org/wiki/World_Geodetic_System for simple conversion
						Double latitude = (northing/6366197.724/0.9996+(1+0.006739496742*Math.pow(Math.cos(northing/6366197.724/0.9996),2)-0.006739496742*Math.sin(northing/6366197.724/0.9996)*Math.cos(northing/6366197.724/0.9996)*(Math.atan(Math.cos(Math.atan(( Math.exp((easting - 500000) / (0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(northing/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((easting - 500000) / (0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(northing/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(northing/6366197.724/0.9996),2)/3))-Math.exp(-(easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(northing/6366197.724/0.9996),2))))*( 1 -  0.006739496742*Math.pow((easting - 500000) / (0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(northing/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(northing/6366197.724/0.9996),2)/3)))/2/Math.cos((northing-0.9996*6399593.625*(northing/6366197.724/0.9996-0.006739496742*3/4*(northing/6366197.724/0.9996+Math.sin(2*northing/6366197.724/0.9996)/2)+Math.pow(0.006739496742*3/4,2)*5/3*(3*(northing/6366197.724/0.9996+Math.sin(2*northing/6366197.724/0.9996 )/2)+Math.sin(2*northing/6366197.724/0.9996)*Math.pow(Math.cos(northing/6366197.724/0.9996),2))/4-Math.pow(0.006739496742*3/4,3)*35/27*(5*(3*(northing/6366197.724/0.9996+Math.sin(2*northing/6366197.724/0.9996)/2)+Math.sin(2*northing/6366197.724/0.9996)*Math.pow(Math.cos(northing/6366197.724/0.9996),2))/4+Math.sin(2*northing/6366197.724/0.9996)*Math.pow(Math.cos(northing/6366197.724/0.9996),2)*Math.pow(Math.cos(northing/6366197.724/0.9996),2))/3))/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(northing/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(northing/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(northing/6366197.724/0.9996),2))+northing/6366197.724/0.9996)))*Math.tan((northing-0.9996*6399593.625*(northing/6366197.724/0.9996 - 0.006739496742*3/4*(northing/6366197.724/0.9996+Math.sin(2*northing/6366197.724/0.9996)/2)+Math.pow(0.006739496742*3/4,2)*5/3*(3*(northing/6366197.724/0.9996+Math.sin(2*northing/6366197.724/0.9996)/2)+Math.sin(2*northing/6366197.724/0.9996 )*Math.pow(Math.cos(northing/6366197.724/0.9996),2))/4-Math.pow(0.006739496742*3/4,3)*35/27*(5*(3*(northing/6366197.724/0.9996+Math.sin(2*northing/6366197.724/0.9996)/2)+Math.sin(2*northing/6366197.724/0.9996)*Math.pow(Math.cos(northing/6366197.724/0.9996),2))/4+Math.sin(2*northing/6366197.724/0.9996)*Math.pow(Math.cos(northing/6366197.724/0.9996),2)*Math.pow(Math.cos(northing/6366197.724/0.9996),2))/3))/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(northing/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(northing/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(northing/6366197.724/0.9996),2))+northing/6366197.724/0.9996))-northing/6366197.724/0.9996)*3/2)*(Math.atan(Math.cos(Math.atan((Math.exp((easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(northing/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(northing/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(northing/6366197.724/0.9996),2)/3))-Math.exp(-(easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(northing/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(northing/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(northing/6366197.724/0.9996),2)/3)))/2/Math.cos((northing-0.9996*6399593.625*(northing/6366197.724/0.9996-0.006739496742*3/4*(northing/6366197.724/0.9996+Math.sin(2*northing/6366197.724/0.9996)/2)+Math.pow(0.006739496742*3/4,2)*5/3*(3*(northing/6366197.724/0.9996+Math.sin(2*northing/6366197.724/0.9996)/2)+Math.sin(2*northing/6366197.724/0.9996)*Math.pow(Math.cos(northing/6366197.724/0.9996),2))/4-Math.pow(0.006739496742*3/4,3)*35/27*(5*(3*(northing/6366197.724/0.9996+Math.sin(2*northing/6366197.724/0.9996)/2)+Math.sin(2*northing/6366197.724/0.9996)*Math.pow(Math.cos(northing/6366197.724/0.9996),2))/4+Math.sin(2*northing/6366197.724/0.9996)*Math.pow(Math.cos(northing/6366197.724/0.9996),2)*Math.pow(Math.cos(northing/6366197.724/0.9996),2))/3))/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(northing/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(northing/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(northing/6366197.724/0.9996),2))+northing/6366197.724/0.9996)))*Math.tan((northing-0.9996*6399593.625*(northing/6366197.724/0.9996-0.006739496742*3/4*(northing/6366197.724/0.9996+Math.sin(2*northing/6366197.724/0.9996)/2)+Math.pow(0.006739496742*3/4,2)*5/3*(3*(northing/6366197.724/0.9996+Math.sin(2*northing/6366197.724/0.9996)/2)+Math.sin(2*northing/6366197.724/0.9996)*Math.pow(Math.cos(northing/6366197.724/0.9996),2))/4-Math.pow(0.006739496742*3/4,3)*35/27*(5*(3*(northing/6366197.724/0.9996+Math.sin(2*northing/6366197.724/0.9996)/2)+Math.sin(2*northing/6366197.724/0.9996)*Math.pow(Math.cos(northing/6366197.724/0.9996),2))/4+Math.sin(2*northing/6366197.724/0.9996)*Math.pow(Math.cos(northing/6366197.724/0.9996),2)*Math.pow(Math.cos(northing/6366197.724/0.9996),2))/3))/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(northing/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(northing/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(northing/6366197.724/0.9996),2))+northing/6366197.724/0.9996))-northing/6366197.724/0.9996))*180/Math.PI;
						// truncate to a reasonable number of significant digits
						latitude=(double) Math.round(latitude*10000000d);
						latitude=latitude/10000000;
						Double longitude =Math.atan((Math.exp((easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(northing/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(northing/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(northing/6366197.724/0.9996),2)/3))-Math.exp(-(easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(northing/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(northing/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(northing/6366197.724/0.9996),2)/3)))/2/Math.cos((northing-0.9996*6399593.625*( northing/6366197.724/0.9996-0.006739496742*3/4*(northing/6366197.724/0.9996+Math.sin(2*northing/6366197.724/0.9996)/2)+Math.pow(0.006739496742*3/4,2)*5/3*(3*(northing/6366197.724/0.9996+Math.sin(2*northing/6366197.724/0.9996)/2)+Math.sin(2* northing/6366197.724/0.9996)*Math.pow(Math.cos(northing/6366197.724/0.9996),2))/4-Math.pow(0.006739496742*3/4,3)*35/27*(5*(3*(northing/6366197.724/0.9996+Math.sin(2*northing/6366197.724/0.9996)/2)+Math.sin(2*northing/6366197.724/0.9996)*Math.pow(Math.cos(northing/6366197.724/0.9996),2))/4+Math.sin(2*northing/6366197.724/0.9996)*Math.pow(Math.cos(northing/6366197.724/0.9996),2)*Math.pow(Math.cos(northing/6366197.724/0.9996),2))/3)) / (0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(northing/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(northing/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(northing/6366197.724/0.9996),2))+northing/6366197.724/0.9996))*180/Math.PI+zone*6-183;
						// truncate to a reasonable number of significant digits
						longitude=(double) Math.round(longitude*10000000d);
						longitude=longitude/10000000;  

						retval = new GeolocationResult(latitude, longitude);
					} catch (NumberFormatException e) { 
						logger.debug(e.getMessage());
						logger.debug("Failed to parse a component.");
					} catch (Exception e) { 
						logger.debug(e.getMessage());
					}
				}
			}
		}

		return retval;
	}

	/**
	 * Determine if a coordinate is in the high seas, i.e. not within any
	 * country or exclusive economic zone.
	 *
	 * @param decimalLatitude latitude of the coordinate to check
	 * @param decimalLongitude longitude of the coordinate to check
	 * @return true if the coordinate is in the high seas, false otherwise including if empty or invalid coordinates were supplied
	 * @throws SourceAuthorityException if there is a problem accessing the shape file
	 */
	public static boolean isHighSeas(String decimalLatitude, String decimalLongitude) throws SourceAuthorityException {
		boolean retval = false;
		if (!GEOUtil.isEmpty(decimalLatitude) && !GEOUtil.isEmpty(decimalLongitude)) { 
			// check if decimalLatitude and decimalLongitude are numeric coordinates
			try {
				Double.parseDouble(decimalLatitude);
				Double.parseDouble(decimalLongitude);
			} catch (NumberFormatException e) {
				logger.debug("Invalid coordinate format: " + decimalLatitude + ", " + decimalLongitude);
				// if either coordinate is not a valid number, return false
				return false;
			}
			// check if decimalLatitude and decimalLongitude are in valid range
			double lat = Double.parseDouble(decimalLatitude);
			double lon = Double.parseDouble(decimalLongitude);
			if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
				// if either coordinate is not in valid range, return false
				logger.debug("Invalid coordinates out of range: " + decimalLatitude + ", " + decimalLongitude);
				return false;
			}
			// check if coordinate is in high seas, i.e. not within any country or exclusive economic zone
			URL combinedShapeFile = GEOUtil.class.getResource("/org.filteredpush.kuration.services/merged_countries_and_eez.shp");
	        FileDataStore store = null;
			try {
				store = FileDataStoreFinder.getDataStore(combinedShapeFile);
	            SimpleFeatureSource featureSource = store.getFeatureSource();
			    Filter filter = ECQL.toFilter("CONTAINS(the_geom, POINT(" + decimalLongitude + " " + decimalLatitude + "))");
			    logger.debug(filter.toString());
			    SimpleFeatureCollection collection=featureSource.getFeatures(filter);
			    logger.debug(collection.size());
			    // if the collection is empty, the coordinate is not within any country or exclusive economic zone
			    // and thus in the high seas.
			    if (collection.isEmpty()) {
			    	// high seas are outside any features in combined shape file, so high seas.
			    	retval=true;
			    }
			} catch (CQLException ex) {
				logger.info(ex.getMessage());
				// CQLException is thrown if the filter is not valid, e.g. if the decimalLatitude or decimalLongitude are not numbers.
				retval = false;
			} catch (IOException e) {
				logger.error(e.getMessage());
				throw new SourceAuthorityException("Failed to determine if coordinate is in high seas: " + e.getMessage());
			}
			finally {
				if (store!=null) { 
					store.dispose();
				}
			}
		}
		return retval;
	} 
	
}

