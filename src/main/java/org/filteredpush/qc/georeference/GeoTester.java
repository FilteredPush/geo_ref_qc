/** GeoTester.java
 *
 * Copyright 2017 President and Fellows of Harvard College
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.filteredpush.qc.georeference;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import org.filteredpush.qc.georeference.util.GEOUtil;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.opengis.feature.simple.SimpleFeature;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Georeference validation utility based on org.filteredpush.qc.georeference.util.GEOUtil
 */
public class GeoTester {
    private final ExecutorService executor = Executors.newFixedThreadPool(8);
    private final GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);

    private Map<String, MultiPolygon> countryPolys = new HashMap<>();
    private Map<String, Map<String, MultiPolygon>> countryPrimaryDivisions = new HashMap<>();

    public GeoTester() throws IOException {
        // Preload the shapefile polygons into HashMaps as a cheap index on country and stateProvince
        loadCountryPolys();
        loadStateProvincePolys();
    }

    private void loadCountryPolys() {
        URL shapeFile = GEOUtil.class.getResource("/org.filteredpush.kuration.services/ne_10m_admin_0_countries.shp");
        FileDataStore store = null;
        SimpleFeatureIterator iter = null;

        try {
            store = FileDataStoreFinder.getDataStore(shapeFile);
            SimpleFeatureSource featureSource = store.getFeatureSource();

            iter = featureSource.getFeatures().features();

            // Store the polygon objects in a map referenced by key country name
            while (iter.hasNext()) {
                SimpleFeature feature = iter.next();
                String name = (String) feature.getAttribute("NAME");
                MultiPolygon poly = (MultiPolygon) feature.getAttribute("the_geom");

                countryPolys.put(name, poly);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading shapefile: " + shapeFile.getPath(), e);
        } finally {
            if (iter!=null) { iter.close(); }
            if (store!=null) { store.dispose(); }
        }
    }

    private void loadStateProvincePolys() {
        URL shapeFile = GEOUtil.class.getResource("/org.filteredpush.kuration.services/ne_10m_admin_1_states_provinces.shp");
        FileDataStore store = null;
        SimpleFeatureIterator iter = null;

        try {
            store = FileDataStoreFinder.getDataStore(shapeFile);
            SimpleFeatureSource featureSource = store.getFeatureSource();

            iter = featureSource.getFeatures().features();

            while (iter.hasNext()) {
                SimpleFeature feature = iter.next();
                String name = (String) feature.getAttribute("name");
                String admin = (String) feature.getAttribute("admin");
                MultiPolygon poly = (MultiPolygon) feature.getAttribute("the_geom");

                // Get the map of state province names to polygons for this feature's country
                Map<String, MultiPolygon> primaryDivisions = countryPrimaryDivisions.get(admin);

                if (primaryDivisions == null) {
                    // Create a new state province to polygon map if it doesn't already exist
                    primaryDivisions = new HashMap<>();
                    countryPrimaryDivisions.put(admin, primaryDivisions);
                }

                // Add the polygon for the current feature's state province name
                primaryDivisions.put(name, poly);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading shapefile: " + shapeFile.getPath(), e);
        } finally {
            if (iter!=null) { iter.close(); }
            if (store!=null) { store.dispose(); }
        }
    }

    public boolean isPointInCountry(String country, double latitude, double longitude) {
        MultiPolygon polygon = countryPolys.get(country);
        return polygon.contains(geometryFactory.createPoint(new Coordinate(longitude, latitude)));
    }

    public boolean isPointInPrimary(String country, String primaryDivision, double latitude, double longitude) {
        // Standardize country names first
        if (country.toLowerCase().equals("united states")) {
            country = "United States of America";
        }

        Map<String, MultiPolygon> primaryDivisions = countryPrimaryDivisions.get(country);
        MultiPolygon polygon = primaryDivisions.get(primaryDivision);
        return polygon.contains(geometryFactory.createPoint(new Coordinate(longitude, latitude)));
    }

    public boolean isCountryKnown(String country) {
        return countryPolys.containsKey(country);
    }

    public boolean isPrimaryKnown(String country, String primaryDivision) {
        Map<String, MultiPolygon> primaryDivisions = countryPrimaryDivisions.get(country);
        return primaryDivisions.containsKey(primaryDivision);
    }

    public void validate(final Map<String, String> record) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                boolean pointInCountry = false, pointInPrimary = false;

                String country = record.get("dwc:country");
                String stateProvince = record.get("dwc:stateProvince");
                String decimalLatitude = record.get("dwc:decimalLatitude");
                String decimalLongitude = record.get("dwc:decimalLongitude");

                Map<String, Boolean> flags = new HashMap<>();

                if (country.isEmpty() || country == null ||
                        stateProvince.isEmpty() || stateProvince == null ||
                        decimalLatitude.isEmpty() || decimalLatitude == null ||
                        decimalLongitude.isEmpty() || decimalLongitude == null) {

                    flags.put("MISSING_REQUIRED_FIELDS", true);

                } else {
                    double originalLat = Double.parseDouble(decimalLatitude);
                    double originalLng = Double.parseDouble(decimalLongitude);

                    pointInCountry = isPointInCountry(country, originalLat, originalLng);
                    pointInPrimary = isPointInPrimary(country, stateProvince, originalLat, originalLng);
                }

                flags.put("COORDINATE_IN_COUNTRY", pointInCountry);
                flags.put("COORDINATE_IN_STATEPROVINCE", pointInPrimary);

                System.out.println(country + ", " + stateProvince + ", " + decimalLatitude + ", " + decimalLongitude + ", " + flags);
            }
        });
    }

    public void close() {
        executor.shutdown();
    }

    public static void main(String[] args) throws IOException {

        GeoTester tester = new GeoTester();

        // Barrow, Alaska
        double originalLng = -156.766389d;
        double originalLat = 71.295556d;

        long startTime = System.currentTimeMillis();

        //boolean result = GEOUtil.isPointInCountry("United States", originalLat, originalLng);

        boolean pointInCountry = tester.isPointInCountry("United States", originalLat, originalLng);
        boolean pointInPrimary = tester.isPointInPrimary("United States","Alaska", originalLat, originalLng);

        long time = System.currentTimeMillis() - startTime;

        System.out.println("result: " + (pointInCountry && pointInPrimary)  + ", time: " + (time));
    }
}
