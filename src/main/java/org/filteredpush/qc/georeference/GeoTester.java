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

import org.filteredpush.qc.georeference.util.GEOUtil;
import org.geotools.api.data.FileDataStore;
import org.geotools.api.data.FileDataStoreFinder;
import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.MultiPolygon;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Georeference validation utility based on org.filteredpush.qc.georeference.util.GEOUtil
 *
 * @author mole
 * @version $Id: $Id
 */
public class GeoTester {
    private final ExecutorService executor = Executors.newFixedThreadPool(8);
    private final org.locationtech.jts.geom.GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);

    private Map<String, MultiPolygon> countryPolys = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private Map<String, Map<String, MultiPolygon>> countryPrimaryDivisions = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    /**
     * <p>Constructor for GeoTester.</p>
     *
     * @throws java.io.IOException if any.
     */
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
                    primaryDivisions = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
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

    /**
     * <p>isPointInCountry.</p>
     *
     * @param country a {@link java.lang.String} object.
     * @param latitude a double.
     * @param longitude a double.
     * @return a boolean.
     */
    public boolean isPointInCountry(String country, double latitude, double longitude) {
        if (!countryPolys.containsKey(country)) {
            return false;
        }

        MultiPolygon polygon = countryPolys.get(country);
        return polygon.contains(geometryFactory.createPoint(new Coordinate(longitude, latitude)));
    }

    /**
     * <p>isPointNearCountry.</p>
     *
     * @param country a {@link java.lang.String} object.
     * @param latitude a double.
     * @param longitude a double.
     * @param distanceKm a double.
     * @return a boolean.
     */
    public boolean isPointNearCountry(String country, double latitude, double longitude, double distanceKm) {
        if (!countryPolys.containsKey(country)) {
            return false;
        }

        // GeoTools ignores units, uses units of underlying projection (degrees in this case), fudge by dividing km by
        // number of km in one degree of latitude (this will describe a wide ellipse far north or south).
        double distanceD = distanceKm / 111d;

        MultiPolygon polygon = countryPolys.get(country);
        return polygon.isWithinDistance(geometryFactory.createPoint(new Coordinate(longitude, latitude)), distanceD);
    }

    /**
     * <p>isPointInPrimary.</p>
     *
     * @param country a {@link java.lang.String} object.
     * @param primaryDivision a {@link java.lang.String} object.
     * @param latitude a double.
     * @param longitude a double.
     * @return a boolean.
     */
    public boolean isPointInPrimary(String country, String primaryDivision, double latitude, double longitude) {
        // Standardize country names first
        if (country.toLowerCase().equals("united states")) {
            country = "United States of America";
        }

        if (!countryPrimaryDivisions.containsKey(country)) {
            return false;
        }

        Map<String, MultiPolygon> primaryDivisions = countryPrimaryDivisions.get(country);

        if (!primaryDivisions.containsKey(primaryDivision)) {
            return false;
        }

        MultiPolygon polygon = primaryDivisions.get(primaryDivision);
        return polygon.contains(geometryFactory.createPoint(new Coordinate(longitude, latitude)));
    }

    /**
     * <p>isPointNearPrimary.</p>
     *
     * @param country a {@link java.lang.String} object.
     * @param primaryDivision a {@link java.lang.String} object.
     * @param latitude a double.
     * @param longitude a double.
     * @param distanceKm a double.
     * @return a boolean.
     */
    public boolean isPointNearPrimary(String country, String primaryDivision, double latitude, double longitude, double distanceKm) {
        // GeoTools ignores units, uses units of underlying projection (degrees in this case), fudge by dividing km by
        // number of km in one degree of latitude (this will describe a wide ellipse far north or south).
        double distanceD = distanceKm / 111d;

        // Standardize country names first
        if (country.toLowerCase().equals("united states")) {
            country = "United States of America";
        }

        if (!countryPrimaryDivisions.containsKey(country)) {
            return false;
        }

        Map<String, MultiPolygon> primaryDivisions = countryPrimaryDivisions.get(country);

        if (!primaryDivisions.containsKey(primaryDivision)) {
            return false;
        }

        MultiPolygon polygon = primaryDivisions.get(primaryDivision);
        return polygon.isWithinDistance(geometryFactory.createPoint(new Coordinate(longitude, latitude)), distanceD);
    }

    /**
     * <p>isCountryKnown.</p>
     *
     * @param country a {@link java.lang.String} object.
     * @return a boolean.
     */
    public boolean isCountryKnown(String country) {
        return countryPolys.containsKey(country);
    }

    /**
     * <p>isPrimaryKnown.</p>
     *
     * @param country a {@link java.lang.String} object.
     * @param primaryDivision a {@link java.lang.String} object.
     * @return a boolean.
     */
    public boolean isPrimaryKnown(String country, String primaryDivision) {
        // Standardize country names first
        if (country.toLowerCase().equals("united states")) {
            country = "United States of America";
        }

        if (!countryPrimaryDivisions.containsKey(country)) {
            return false;
        }

        Map<String, MultiPolygon> primaryDivisions = countryPrimaryDivisions.get(country);
        return primaryDivisions.containsKey(primaryDivision);
    }

    /**
     * <p>validate.</p>
     *
     * @param record a {@link java.util.Map} object.
     */
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

    /**
     * <p>close.</p>
     */
    public void close() {
        executor.shutdown();
    }

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     * @throws java.io.IOException if any.
     */
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
