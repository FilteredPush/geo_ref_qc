/**
 * 
 */
package org.filteredpush.qc.georeference.util;

import java.io.IOException;
import java.net.URL;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.opengis.filter.Filter;

/**
 * @author mole
 *
 */
public class GISDataLoader {
	
	public boolean pointIsWithinLand(double latitude, double longitude) {
		return pointIsWithinLand(latitude, longitude, false);
	}
	
	
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
