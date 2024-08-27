/**
 * 
 */
package org.filteredpush.qc.georeference.util;

import java.io.IOException;
import java.lang.System.Logger;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.filteredpush.qc.georeference.SourceAuthorityException;
import org.geotools.api.data.FileDataStore;
import org.geotools.api.data.FileDataStoreFinder;
import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.filter.Filter;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;

/**
 * <p>GISDataLoader class.</p>
 *
 * @author mole
 * @version $Id: $Id
 */
public class GISDataLoader {
	
	private static final Log logger = LogFactory.getLog(GISDataLoader.class);
	
	/**
	 * <p>pointIsWithinLand.</p>
	 *
	 * @param latitude a double.
	 * @param longitude a double.
	 * @return a boolean.
	 */
	public boolean pointIsWithinLand(double latitude, double longitude) {
		return pointIsWithinLand(latitude, longitude, false);
	}
	
	
	/**
	 * <p>pointIsWithinLand.</p>
	 *
	 * @param longitude a double.
	 * @param latitude a double.
	 * @param invertSense a boolean.
	 * @return a boolean.
	 */
	public boolean pointIsWithinLand(double longitude, double latitude, boolean invertSense) {
		
		boolean result = false;

        URL landShapeFile = GEOUtil.class.getResource("/org.filteredpush.kuration.services/ne_10m_land.shp");
        FileDataStore store = null;
		try {
			store = FileDataStoreFinder.getDataStore(landShapeFile);
            SimpleFeatureSource featureSource = store.getFeatureSource();
            logger.debug(featureSource.getInfo().toString());
            logger.debug(featureSource.getName().toString());
		    String filterString = " CONTAINS (the_geom, POINT(" + Double.toString(longitude) + " " + Double.toString(latitude) + "))";
		    logger.debug(filterString);
		    Filter filter = ECQL.toFilter(filterString);
		    SimpleFeatureCollection collection=featureSource.getFeatures(filter);
		    result = !collection.isEmpty();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (CQLException e) {
			logger.error(e.getMessage(), e);
		} finally { 
			// close 
			if (store!=null) { 
				try { 
					store.dispose();
				} catch (Exception e) { 
					logger.error(e.getMessage());
				}
			}
		}
		
		if (invertSense) {
			result = !result;
		}
 		
		return result;
	}
	
	/**
	 * <p>pointIsWithinLand.</p>
	 *
	 * @param longitude a double.
	 * @param latitude a double.
	 * @param invertSense a boolean.
	 * @return a boolean.
	 * @param distanceKm a double.
	 * @throws SourceAuthorityException 
	 */
	public boolean pointIsWithinOrNearLand(double longitude, double latitude, boolean invertSense, double distanceKm) throws SourceAuthorityException {
		
		boolean result = false;

        URL landShapeFile = GEOUtil.class.getResource("/org.filteredpush.kuration.services/ne_10m_land.shp");
        FileDataStore store = null;
		try {
			store = FileDataStoreFinder.getDataStore(landShapeFile);
            SimpleFeatureSource featureSource = store.getFeatureSource();
            logger.debug(featureSource.getInfo().toString());
            logger.debug(featureSource.getName().toString());
            double distanceD = distanceKm / 111d; // GeoTools ignores units, uses units of underlying projection (degrees in this case), fudge by dividing km by number of km in one degree of latitude (this will describe a wide ellipse far north or south).
		    StringBuffer filterString = new StringBuffer();
		    // filterString.append(" CONTAINS (the_geom, POINT(" + Double.toString(longitude) + " " + Double.toString(latitude) + "))");
		    filterString.append(" DWITHIN(the_geom, POINT(" + Double.toString(longitude) + " " + Double.toString(latitude) + "), "+ distanceD +", kilometers)");
		    logger.debug(filterString);
		    Filter filter = ECQL.toFilter(filterString.toString());
		    SimpleFeatureCollection collection=featureSource.getFeatures(filter);
		    result = !collection.isEmpty();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new SourceAuthorityException("Error reading spatial data file: " + e.getMessage());
		} catch (CQLException e) {
			logger.error(e.getMessage(), e);
			throw new SourceAuthorityException("Error querying spatial data file: " + e.getMessage());
		} finally { 
			// close 
			if (store!=null) { 
				try { 
					store.dispose();
				} catch (Exception e) { 
					logger.error(e.getMessage());
				}
			}
		}
		
		if (invertSense) {
			result = !result;
		}
 		
		return result;
	}
	
	/**
	 * Determine if a point is near the centroid of a country. 
	 *
	 * @param longitude a double representation of the longigude.
	 * @param latitude a double representation of the latitude.
	 * @param countryCode to check for centroids of.
	 * @param distanceKm the buffer distance in km.
	 * @return true if latitude and longitude are within buffer distance of a centroid for the country code.
	 * @throws SourceAuthorityException 
	 */
	public static boolean isPointNearCentroid(double longitude, double latitude, String countryCode, double distanceKm) throws SourceAuthorityException {
		
		boolean result = false;

        URL centroidShapeFile = GEOUtil.class.getResource("/org.filteredpush.kuration.services/gbif_pcli_country_centroids.shp");
        FileDataStore store = null;
		try {
			store = FileDataStoreFinder.getDataStore(centroidShapeFile);
            SimpleFeatureSource featureSource = store.getFeatureSource();
            logger.debug(featureSource.getInfo().toString());
            logger.debug(featureSource.getName().toString());
            double distanceD = distanceKm / 111d; // GeoTools ignores units, uses units of underlying projection (degrees in this case), fudge by dividing km by number of km in one degree of latitude (this will describe a wide ellipse far north or south).
		    StringBuffer filterString = new StringBuffer();
		    filterString.append("DWITHIN(the_geom, POINT(" + Double.toString(longitude) + " " + Double.toString(latitude) + "), "+ distanceD +", kilometers)");
		    filterString.append(" AND ");
            filterString.append("iso2 ILIKE '"+ countryCode +"' ");
		    logger.debug(filterString);
		    Filter filter = ECQL.toFilter(filterString.toString());
		    SimpleFeatureCollection collection=featureSource.getFeatures(filter);
		    result = !collection.isEmpty();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new SourceAuthorityException("Error reading country centroids: " + e.getMessage());
		} catch (CQLException e) {
			logger.error(e.getMessage(), e);
			throw new SourceAuthorityException("Error reading country centroids: " + e.getMessage());
		} finally { 
			// close 
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
	 * Obtain a value for the area of a country. 
	 *
	 * @param countryCode to check for area.
	 * @return area of country in square km, or null if not found
	 */
	public static Double getAreaOfCountry(String countryCode) {
		
		Double result = null;

        URL centroidShapeFile = GEOUtil.class.getResource("/org.filteredpush.kuration.services/gbif_pcli_country_centroids.shp");
        FileDataStore store = null;
		try {
			store = FileDataStoreFinder.getDataStore(centroidShapeFile);
            SimpleFeatureSource featureSource = store.getFeatureSource();
            logger.debug(featureSource.getInfo().toString());
            logger.debug(featureSource.getName().toString());
		    StringBuffer filterString = new StringBuffer();
            filterString.append("iso2 ILIKE '"+ countryCode +"' ");
		    logger.debug(filterString);
		    Filter filter = ECQL.toFilter(filterString.toString());
		    SimpleFeatureCollection collection=featureSource.getFeatures(filter);
		    if (!collection.isEmpty()) { 
		    	SimpleFeatureIterator i = collection.features();
		    	boolean found = false;
		    	while (i.hasNext() && !found) { 
		    		SimpleFeature feature = i.next();
		    		Object areaObject = feature.getAttribute("area_sqkm");
		    		logger.debug(areaObject);
		    		try { 
		    			result = (Double)areaObject;
		    			found = true;
		    		} catch (ClassCastException e) { 
		    			logger.debug(e.getMessage());
		    		}
		    	}
		    	i.close();
		    }
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (CQLException e) {
			logger.error(e.getMessage(), e);
		} finally { 
			// close 
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

}
