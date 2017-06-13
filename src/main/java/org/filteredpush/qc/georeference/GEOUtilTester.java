package org.filteredpush.qc.georeference;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import org.filteredpush.qc.georeference.util.GEOUtil;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by lowery on 6/12/17.
 */
public class GEOUtilTester {
    private URL countryShapeFile = GEOUtil.class.getResource("/org.filteredpush.kuration.services/ne_10m_admin_0_countries.shp");

    private final GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
    private Map<String, MultiPolygon> polys = new HashMap<>();

    public GEOUtilTester() throws IOException {
        FileDataStore store = null;
        try {
            store = FileDataStoreFinder.getDataStore(countryShapeFile);
            SimpleFeatureSource featureSource = store.getFeatureSource();

            SimpleFeatureIterator iter = featureSource.getFeatures().features();

            while (iter.hasNext()) {
                SimpleFeature feature = iter.next();
                String name = (String) feature.getAttribute("NAME");
                MultiPolygon poly = (MultiPolygon) feature.getAttribute("the_geom");

                polys.put(name, poly);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading shapefile: " + countryShapeFile.getPath(), e);
        } finally {
            if (store!=null) { store.dispose(); }
        }

    }

    public boolean pointInCountry(String country, double latitude, double longitude) {
        MultiPolygon polygon = polys.get(country);
        return polygon.contains(geometryFactory.createPoint(new Coordinate(longitude, latitude)));
    }


    public static void main(String[] args) throws IOException {

        GEOUtilTester tester = new GEOUtilTester();

        // Barrow, Alaska
        double originalLng = -156.766389d;
        double originalLat = 71.295556d;

        long startTime = System.currentTimeMillis();

        //boolean result = GEOUtil.isPointInCountry("United States", originalLat, originalLng);
        boolean result = tester.pointInCountry("United States", originalLat, originalLng);
        
        System.out.println("result: " + result + ", time: " + (System.currentTimeMillis() - startTime));
    }
}
