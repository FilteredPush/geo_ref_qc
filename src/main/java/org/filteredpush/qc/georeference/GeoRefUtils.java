/**GeoRefValidator.java
 *
 * Copyright 2016 President and Fellows of Harvard College
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
package org.kurator.validation.georeference;

import edu.tulane.museum.www.webservices.Georef_Result;
import edu.tulane.museum.www.webservices.Georef_Result_Set;
import org.filteredpush.kuration.services.exceptions.ServiceException;
import org.filteredpush.kuration.services.geolocate.GeoLocateAlternative;
import org.filteredpush.kuration.services.geolocate.GeoLocateRequest;
import org.filteredpush.kuration.services.geolocate.GeoLocateResult;
import org.filteredpush.kuration.services.geolocate.GeoLocateService;
import org.filteredpush.kuration.util.*;
import org.kurator.validation.model.Georeference;
import org.nocrala.tools.gis.data.esri.shapefile.ShapeFileReader;
import org.nocrala.tools.gis.data.esri.shapefile.ValidationPreferences;
import org.nocrala.tools.gis.data.esri.shapefile.exception.InvalidShapeFileException;
import org.nocrala.tools.gis.data.esri.shapefile.shape.AbstractShape;
import org.nocrala.tools.gis.data.esri.shapefile.shape.PointData;
import org.nocrala.tools.gis.data.esri.shapefile.shape.shapes.PolygonShape;

import java.awt.geom.Path2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.filteredpush.kuration.StringUtils.*;

public class GeoRefUtils {
    private static final Log logger = LogFactory.getLog(SciNameUtils.class);

    // TODO: should be factored out into a DwcGeoRefDQ class to match architecture of event_date_qc project
    public void validate(String country, String stateProvince, String county, String locality, String latitude,
                         String longitude, double thresholdDistanceKm) throws ServiceException {

        georeference = new Georeference(country, stateProvince, county, locality, latitude, longitude);

        // Make values null if they don't contain valid lat/long
        if (latitude != null && latitude.trim().length()==0) { latitude = null; }
        if (longitude != null && longitude.trim().length()==0) { longitude = null; }

        Double decimalLatitude = isNumeric(latitude) ? Double.parseDouble(latitude) : null;
        Double decimalLongitude = isNumeric(longitude) ? Double.parseDouble(longitude) : null;

        // Look up locality in Tulane's GeoLocateService service

        List<GeoLocateResult> potentialMatches = queryGeoLocate(country, stateProvince,
                county, locality, decimalLatitude, decimalLongitude);

        if (potentialMatches.isEmpty()) {
            georeference.apply(CurationStatus.UNABLE_DETERMINE_VALIDITY, "GeoLocateService service can't find " +
                    "coordinates of Locality.");
        }

        // Try to fill in missing values
        if ((decimalLatitude == null || decimalLongitude == null)) {
            fillInMissingValues(potentialMatches, decimalLatitude, decimalLongitude, thresholdDistanceKm);
        }

    }

    public void fillInMissingValues(List<GeoLocateResult> potentialMatches,
                                     Double latitude, Double longitude, double thresholdDistanceKm) {

        if (potentialMatches.size() > 0 && potentialMatches.get(0).getConfidence() > 80) {
            int thresholdDistanceMeters = (int) Math.round(thresholdDistanceKm * 1000);

            if (latitude != null && longitude == null) {

                // Try to fill in the longitude
                if (GeoLocateResult.isLocationNearAResult(latitude, potentialMatches.get(0).getLongitude(),
                        potentialMatches, thresholdDistanceMeters)) {

                    // if latitude plus longitude from best match is near a match, propose
                    // the longitude from the best match.

                    // TODO: If we do this, then we need to add the datum, georeference source, georeference method, etc
                    Map<String, Object> correctedLongitude = Collections.singletonMap("longitude",
                            potentialMatches.get(0).getLongitude().toString());

                    georeference.apply(CurationStatus.FILLED_IN, correctedLongitude, comment("Added a longitude from " +
                            "{} as longitude was missing and geolocate had a confident match near the original line " +
                            "of latitude.", serviceName()));
                }

            } else if (latitude == null && longitude != null) {

                // Try to fill in the latitude
                if (GeoLocateResult.isLocationNearAResult(potentialMatches.get(0).getLatitude(), longitude,
                        potentialMatches, thresholdDistanceMeters)) {

                    // if latitude plus longitude from best match is near a match, propose
                    // the longitude from the best match.

                    // TODO: If we do this, then we need to add the datum, georeference source, georeference method, etc
                    Map<String, Object> correctedLatitude = Collections.singletonMap("latitude",
                            potentialMatches.get(0).getLatitude().toString());

                    georeference.apply(CurationStatus.FILLED_IN, correctedLatitude, comment("Added a latitude from " +
                            "{} as latitude was missing and geolocate had a confident match near the original line " +
                            "of longitude.", serviceName()));
                }

            } else if (latitude == null && longitude == null) {

                //Both coordinates in the original record are missing

                Map<String, Object> correctedValues = new HashMap<>();

                // TODO: If we do this, then we need to add the datum, georeference source, georeference method, etc.
                correctedValues.put("latitude", potentialMatches.get(0).getLatitude());
                correctedValues.put("longitude", potentialMatches.get(0).getLongitude());

                georeference.apply(CurationStatus.FILLED_IN, correctedValues, comment("Added a georeference using " +
                        "cached data or {} service since the original coordinates are missing and geolocate had a " +
                        "confident match.", serviceName()));

            } else {

                georeference.apply(CurationStatus.UNABLE_DETERMINE_VALIDITY, "No latitude and/or longitude provided," +
                        " and geolocate didn't return a good match.");

            }
        }
    }

    public List<GeoLocateResult> queryGeoLocate(String country, String stateProvince,
                                                  String county, String locality, double latitude, double longitude) {
        try {

            locality = locality.toLowerCase();

            boolean hwyX = locality != null && locality.matches("bridge");
            boolean findWaterbody = locality != null && locality.matches("(lake|pond|sea|ocean)");

            GeoLocateRequest request = new GeoLocateRequest(country, stateProvince, county, locality,
                    hwyX, findWaterbody);

            Georef_Result_Set response = GeoLocateService.geoLocate2(request);

            georeference.apply(comment("Found {} possible georeferences with Geolocate engine: {}",
                    response.getNumResults(), response.getEngineVersion()));

            if (response != null && response.getNumResults() > 0) {

                for (Georef_Result result : response.getResultSet()) {
                    long distance = GEOUtil.calcDistanceHaversineMeters(result.getWGS84Coordinate().getLatitude(),
                            result.getWGS84Coordinate().getLongitude(), latitude, longitude) / 100;

                    georeference.apply(comment("{} score:{} {} {} km:{}", result.getParsePattern(), result.getScore(),
                            result.getWGS84Coordinate().getLatitude(), result.getWGS84Coordinate().getLongitude(),
                            distance));
                }

                return GeoLocateResult.constructFromGeolocateResultSet(response);
            }

        } catch (ServiceException e) {
            georeference.apply(CurationStatus.UNABLE_DETERMINE_VALIDITY, e.getMessage());
        }

        return null;
    }

    public boolean isLatLongInRange(double latitude, double longitude) {
        boolean isInRange = true;

        // (1) Latitude and longitude out of range
        if (Math.abs(latitude)>90) {
            georeference.apply("The original latitude is out of range.");
            isInRange = false;
        }
        if (Math.abs(longitude)>180) {
            georeference.apply("The original longitude is out of range.");
            isInRange = false;
        }
        if (isInRange) {
            georeference.apply("Latitute is within +/-90 and longitude is within +/-180.");
        }

        return isInRange;
    }

    public void calculateDistanceBetweenPoints(double originalLat, double originalLong) {
        //calculate the distance from the returned point and original point in the record
        //If the distance is smaller than a certainty, then use the original point --- GEOService, like GeoLocate can't
        // parse detailed locality. In this case, the original point has higher confidence
        //Otherwise, use the point returned from GeoLocate

        georeference.apply("Latitute and longitude are both present.");
        boolean flagError = false;
        boolean foundGoodMatch = false;

        // Check for possible error conditions
        flagError = !isLatLongInRange(originalLat, originalLong);
    }

    public String standardizeCountryNames(String country) {
        //standardize country names
        if (country.toUpperCase().equals("USA")) {
            country = "United States";
        } else if (country.toUpperCase().equals("U.S.A.")) {
            country = "United States";
        } else if (country.toLowerCase().equals("united states of america")) {
            country = "United States";
        } else {
            country = country.toUpperCase();
        }

        return country;
    }

    public boolean isCountryKnown(String country) {
        // (2) Locality not inside country?
        if (GEOUtil.isCountryKnown(country)) {
            return true;
        } else {
            georeference.apply(comment("Can't find country: {} in country name list", country));
            return false;
        }
    }

    public boolean isPointInCountry(String country, double originalLat, double originalLong) {
        if (GEOUtil.isPointInCountry(country, originalLat, originalLong)) {
            georeference.apply(comment("Original coordinate is inside country ({})", country));
            //addToServiceName("Country boundary data from Natural Earth");
            return true;
        } else {
            georeference.apply(comment("Original coordinate is not inside country ({}).", country));
            //addToServiceName("Country boundary data from Natural Earth");
            return false;
        }
    }

    public boolean isPrimaryKnown(String country, String stateProvince) {
        // (3) Locality not inside primary division?
        if (GEOUtil.isPrimaryKnown(country, stateProvince)) {
            return true;
        } else {
            georeference.apply(comment("Can't find state/province: {} in primaryDivision name list", stateProvince));
            return false;
        }
    }

    public boolean isPointInPrimary(String country, String stateProvince, double originalLat, double originalLong) {
        if (GEOUtil.isPointInPrimary(country, stateProvince, originalLat, originalLong)) {
            georeference.apply(comment("Original coordinate is inside primary division ({}).", stateProvince));
            //addToServiceName("State/province boundary data from Natural Earth");
            return true;
        } else {
            georeference.apply(comment("Original coordinate is not inside primary division ({}).", stateProvince));
            //addToServiceName("State/province boundary data from Natural Earth");
            return false;
        }
    }

    public boolean isLocalityMarine(Set<Path2D> setPolygon, String waterBody, String country, String stateProvince,
                                    String county) {
        boolean isMarine = false;
        if ((country == null || country.length() == 0) && (stateProvince == null || stateProvince.length() == 0) &&
                (county == null || county.length() == 0)) {
            georeference.apply("No country, state/province, or county provided, guessing that this is a marine " +
                    "locality.");
            // no country provided, assume locality is marine
            return true;
        } else {
            if (waterBody != null && waterBody.trim().length() > 0) {
                if (waterBody.matches("(Indian|Pacific|Arctic|Atlantic|Ocean|Sea|Carribean|Mediteranian)")) {
                    georeference.apply("A water body name that appears to be an ocean or a sea was provided, " +
                            "guessing that this is a marine locality. ");
                    return true;
                } else {
                    georeference.apply("A country, state/province, or county was provided with a water body that " +
                            "doesn't appear to be an ocean or a sea, guessing that this is a non-marine locality. ");
                }
            } else {
                georeference.apply("A country, state/province, or county was provided but no water body, guessing " +
                        "that this is a non-marine locality. ");
            }
        }

        return false;
    }

    public boolean isInPolygon(Set<Path2D> setPolygon, boolean isMarine, String country, double originalLat,
                               double originalLong) {
        // TODO: is this method named appropriately?

        if (!GEOUtil.isInPolygon(setPolygon, originalLong, originalLat, isMarine)) {
            if (isMarine) {
                georeference.apply("Coordinate is on land for a supposedly marine locality.");
                return false;
            } else {
                georeference.apply("Coordinate is not on land for a supposedly non-marine locality.");
                double thresholdDistanceKmFromLand = 44.448d;  // 24 nautical miles, territorial waters plus contigouus
                // zone.
                if (GEOUtil.isPointNearCountry(country, originalLat, originalLong, thresholdDistanceKmFromLand)) {
                    georeference.apply("Coordinate is within 24 nautical miles of country boundary, could be a " +
                            "nearshore marine locality.");
                } else {
                    georeference.apply("Coordinate is further than 24 nautical miles of country boundary, country in " +
                            "error or marine within EEZ.");
                    return false;
                }
            }
        }

        return true;
    }

    public boolean isOriginalLocalityNearResult(List<GeoLocateResult> potentialMatches, double originalLat,
                                                double originalLong, int thresholdDistanceKm) {
        if (potentialMatches != null && potentialMatches.size() > 0) {
            if (GeoLocateResult.isLocationNearAResult(originalLat, originalLong, potentialMatches,
                    (int) Math.round(thresholdDistanceKm * 1000))) {
                Map<String, Object> correctedValues = new HashMap<>();

                correctedValues.put("latitude", originalLat);
                correctedValues.put("longitude", originalLong);

                georeference.apply(CurationStatus.CORRECT, correctedValues, comment("Original coordinates are near " +
                        "(within georeference error radius or {} km) the georeference for the locality text from the " +
                        "Geolocate service.  Accepting the original coordinates. ", thresholdDistanceKm));
                return true;
            } else {
                georeference.apply(comment("Original coordinates are not near (within georeference error radius or " +
                        "{} km) the georeference for the locality text from the Geolocate service.",
                        thresholdDistanceKm));
                return false;
            }
        }

        return false;
    }

    public GeoLocateAlternative findMatchGeolocateResults(
            List<GeoLocateResult> potentialMatches, List<GeoLocateAlternative> alternatives,
            double thresholdDistanceKm) {

        for (GeoLocateAlternative alt : alternatives) {
            if (GeoLocateResult.isLocationNearAResult(alt.getLatitude(), alt.getLongitude(), potentialMatches,
                    (int) Math.round(thresholdDistanceKm * 1000))) {
                Map<String, Object> correctedValues = new HashMap<>();

                correctedValues.put("latitude", alt.getLatitude());
                correctedValues.put("longitude", alt.getLongitude());

                georeference.apply(CurationStatus.CURATED, comment("Modified coordinates ({}) are near (within " +
                                "georeference error radius or {} km) the georeference for the " +
                                "locality text from the Geolocate service.  Accepting the {} coordinates. ",
                        alt.getAlternative(), thresholdDistanceKm, alt.getAlternative()));
                return alt;
            }
        }

        return null;
    }

    // precondition: ifCountryIsKnown
    public GeoLocateAlternative findMatchMarine(String country, double originalLat, double originalLong,
                                                List<GeoLocateAlternative> alternatives) {
        if (country != null && GEOUtil.isCountryKnown(country)) {
            for (GeoLocateAlternative alt : alternatives) {
                // 24 nautical miles, territorial waters plus contigouus zone.
                double thresholdDistanceKmFromLand = 44.448d;
                if (GEOUtil.isPointNearCountry(country, originalLat, originalLong,
                        thresholdDistanceKmFromLand)) {
                    georeference.apply(CurationStatus.CURATED, comment("Modified coordinate ({}) is within " +
                            "24 nautical miles of country boundary.", alt.getAlternative()));
                    Map<String, Object> correctedValues = new HashMap<>();

                    correctedValues.put("latitude", alt.getLatitude());
                    correctedValues.put("longitude", alt.getLongitude());

                    return alt;
                }
            }
        }

        return null;
    }

    public GeoLocateAlternative findMatchTerrestrial(String country, String stateProvince, double originalLat,
                                                     double originalLong, List<GeoLocateAlternative> alternatives) {

        for (GeoLocateAlternative alt : alternatives) {
            if (GEOUtil.isCountryKnown(country)) {

                if (GEOUtil.isPointInCountry(country, alt.getLatitude(), alt.getLongitude())) {
                    georeference.apply(comment("Modified coordinate ({}) is inside country ({}).",
                            alt.getAlternative(), country));
                    if (GEOUtil.isPrimaryKnown(country, stateProvince) &&
                            GEOUtil.isPointInPrimary(country, stateProvince, originalLat, originalLong)) {

                        Map<String, Object> correctedValues = new HashMap<>();

                        correctedValues.put("latitude", alt.getLatitude());
                        correctedValues.put("longitude", alt.getLongitude());

                        georeference.apply(CurationStatus.CURATED, correctedValues, comment("Modified coordinate " +
                                "({}) is inside stateProvince ({}).", alt.getAlternative(), stateProvince));

                        return alt;
                    }
                }
            }
        }

        return null;
    }

    public void checkAlternatives(List<GeoLocateResult> potentialMatches, double originalLat, double originalLong,
                                  String country, String stateProvince, double thresholdDistanceKm, boolean isMarine) {

        // Construct a list of alternatives
        List<GeoLocateAlternative> alternatives = GeoLocateAlternative.constructListOfAlternatives(originalLat,
                originalLong);

        GeoLocateAlternative match = null;

        if (potentialMatches != null && potentialMatches.size() > 0) {
            match = findMatchGeolocateResults(potentialMatches, alternatives, thresholdDistanceKm);
        } else if (isMarine) {
            match = findMatchMarine(country, originalLat, originalLong, alternatives);
        } else {
            match = findMatchTerrestrial(country, stateProvince, originalLat, originalLat, alternatives);
        }

        if (match == null) {
                if (country!=null && GEOUtil.isCountryKnown(country)) {
                    if (isMarine) {
                        georeference.apply(CurationStatus.UNABLE_CURATE, "No transformation of the coordinates is near the provided country.");
                    } else {
                        if (stateProvince!=null && GEOUtil.isPrimaryKnown(country, stateProvince)) {
                            georeference.apply(CurationStatus.UNABLE_CURATE, "No transformation of the coordinates is inside the provided country and state/province.");
                        }
                    }
                }
            }
        }

    public Set<Path2D> readLandData() throws IOException, InvalidShapeFileException {
        InputStream is = GeoRefValidator.class.getResourceAsStream("/org.filteredpush.kuration.services/ne_10m_land.shp");

        ValidationPreferences prefs = new ValidationPreferences();
        prefs.setMaxNumberOfPointsPerShape(420000);
        ShapeFileReader reader = null;
        reader = new ShapeFileReader(is, prefs);

        Set<Path2D> polygonSet = new HashSet<Path2D>();

        AbstractShape shape;
        while ((shape = reader.next()) != null) {

            PolygonShape aPolygon = (PolygonShape) shape;

            //System.out.println("content: " + aPolygon.toString());
            //System.out.println("I read a Polygon with "
            //    + aPolygon.getNumberOfParts() + " parts and "
            //    + aPolygon.getNumberOfPoints() + " points. "
            //     + aPolygon.getShapeType());

            for (int i = 0; i < aPolygon.getNumberOfParts(); i++) {
                PointData[] points = aPolygon.getPointsOfPart(i);
                //System.out.println("- part " + i + " has " + points.length + " points");

                Path2D polygon = new Path2D.Double();
                for (int j = 0; j < points.length; j++) {
                    if (j==0) polygon.moveTo(points[j].getX(), points[j].getY());
                    else polygon.lineTo(points[j].getX(), points[j].getY());
                    //System.out.println("- point " + i + " has " + points[j].getX() + " and " + points[j].getY());
                }
                polygonSet.add(polygon);
            }
        }
        is.close();
        return polygonSet;
    }

    private String serviceName() {
        return "GeoLocate";
    }
}