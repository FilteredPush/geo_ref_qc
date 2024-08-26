/**
 * 
 */
package org.filteredpush.qc.georeference.util;

import java.io.IOException;
import java.lang.System.Logger;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.api.data.FileDataStore;
import org.geotools.api.data.FileDataStoreFinder;
import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.filter.Filter;
import org.geotools.data.simple.SimpleFeatureCollection;
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
	 */
	public boolean pointIsWithinOrNearLand(double longitude, double latitude, boolean invertSense, double distanceKm) {
		
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
	
	

}
