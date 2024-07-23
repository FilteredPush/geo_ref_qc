/**
 * 
 */
package org.filteredpush.qc.georeference.util;

import java.io.IOException;
import java.net.URL;

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
            System.out.println(featureSource.getInfo());
            System.out.println(featureSource.getName());
		    String filterString = " CONTAINS (the_geom, POINT(" + Double.toString(longitude) + " " + Double.toString(latitude) + "))";
		    System.out.println(filterString);
		    Filter filter = ECQL.toFilter(filterString);
		    SimpleFeatureCollection collection=featureSource.getFeatures(filter);
		    result = !collection.isEmpty();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (invertSense) {
			result = !result;
		}
 		
		return result;
	}
	
	
	

}
