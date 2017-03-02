package org.filteredpush.qc.georeference;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datakurator.ffdq.annotations.Provides;
import org.datakurator.ffdq.api.EnumDQAmendmentResultState;
import org.datakurator.ffdq.api.EnumDQResultState;
import org.datakurator.ffdq.api.EnumDQValidationResult;
import org.datakurator.ffdq.api.ResultState;
import org.filteredpush.qc.georeference.util.GEOUtil;
import org.filteredpush.qc.georeference.util.GISDataLoader;
import org.filteredpush.qc.georeference.util.GeolocationAlternative;
import org.filteredpush.qc.georeference.util.GeolocationResult;
import org.nocrala.tools.gis.data.esri.shapefile.exception.InvalidShapeFileException;

import java.awt.geom.Path2D;
import java.io.IOException;
import java.util.*;

/**
 * Created by lowery on 2/24/17.
 */
public class DwCGeoRefDQ {
    private static final Log logger = LogFactory.getLog(DwCGeoRefDQ.class);

    private static final GeoLocateService service = new GeoLocateService();
    private static int thresholdDistanceKm = 20;

    @Provides(value = "COORDINATE_IN_RANGE")
    public GeoDQValidation isLatLongInRange(String latitude, String longitude) {
        GeoDQValidation result = new GeoDQValidation();

        if (latitude != null && longitude != null && !latitude.isEmpty() && !longitude.isEmpty()) {
            boolean isInRange = true;

            try {
                double lat = Double.parseDouble(latitude);
                double lon = Double.parseDouble(longitude);

                if (Math.abs(lat) > 90) {
                    result.addComment("The original latitude is out of range.");
                    isInRange = false;
                }
                if (Math.abs(lon) > 180) {
                    result.addComment("The original longitude is out of range.");
                    isInRange = false;
                }

                result.setResultState(EnumDQResultState.RUN_HAS_RESULT);

                if (isInRange) {
                    result.addComment("Latitute is within +/-90 and longitude is within +/-180.");
                    result.setResult(EnumDQValidationResult.COMPLIANT);
                } else {
                    result.setResult(EnumDQValidationResult.NOT_COMPLIANT);
                }
            } catch(NumberFormatException e) {
                result.addComment("The value for either latitude or longitude is non numeric.");
                result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
            }
        } else {
            result.addComment("Either latitude or longitude is empty.");
            result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
        }

        return result;
    }

    @Provides(value = "COUNTRY_IS_CONSISTENT")
    public GeoDQValidation isPointInCountry(String country, String originalLat, String originalLong) {
        GeoDQValidation result = new GeoDQValidation();

        if (country == null || country.isEmpty()) {
            result.addComment("No value provided for country.");
            result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
        } else if (originalLat == null || originalLong == null || originalLat.isEmpty() || originalLong.isEmpty()) {
            result.addComment("Either latitude or longitude is empty.");
            result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
        } else {
            try {
                double latitude = Double.parseDouble(originalLat);
                double longitude = Double.parseDouble(originalLong);

                if (GEOUtil.isPointInCountry(country, latitude, longitude)) {
                    result.addComment("Original coordinate is inside country (" + country + ").");
                    result.setResult(EnumDQValidationResult.COMPLIANT);
                } else {
                    result.addComment("Original coordinate is not inside country (" + country + ").");
                    result.setResult(EnumDQValidationResult.NOT_COMPLIANT);
                }
            } catch (NumberFormatException e) {
                result.addComment("The value for either latitude or longitude is non numeric.");
                result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
            }
        }

        return result;
    }

    @Provides(value = "STATE_PROVINCE_IS_CONSISTENT")
    public GeoDQValidation stateProvinceIsConsistent(String country, String stateProvince, String originalLat, String originalLong) {
        GeoDQValidation result = new GeoDQValidation();

        if (country == null || country.isEmpty() || stateProvince == null || stateProvince.isEmpty()) {
            result.addComment("No value provided for either country or state.");
            result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
        } else if (originalLat == null || originalLong == null || originalLat.isEmpty() || originalLong.isEmpty()) {
            result.addComment("Either latitude or longitude is empty.");
            result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
        } else if (GEOUtil.isPrimaryKnown(country, stateProvince)) {
            try {
                double latitude = Double.parseDouble(originalLat);
                double longitude = Double.parseDouble(originalLong);

                if (GEOUtil.isPointInPrimary(country, stateProvince, latitude, longitude)) {
                    result.addComment("Original coordinate is inside primary division ("+stateProvince+").");
                    result.setResult(EnumDQValidationResult.COMPLIANT);
                } else {
                    result.addComment("Original coordinate is not inside primary division ("+stateProvince+").");
                    result.setResult(EnumDQValidationResult.NOT_COMPLIANT);
                }
            } catch (NumberFormatException e) {
                result.addComment("The value for either latitude or longitude is non numeric.");
                result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
            }
        } else {
            result.addComment("Can't find state/province: " + stateProvince + " in primaryDivision name list");
            result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
        }
            return result;

    }

    @Provides(value = "FILL_IN_MISSING_VALUES")
    public GeoDQAmendment fillInMissing(String country, String stateProvince, String county, String locality, String latitude, String longitude) {
        GeoDQAmendment result = new GeoDQAmendment();

        // Make values null if they don't contain valid lat/long
        if (latitude != null && latitude.trim().length()==0) { latitude = null; }
        if (longitude != null && longitude.trim().length()==0) { longitude = null; }

        Double decimalLatitude = isNumeric(latitude) ? Double.parseDouble(latitude) : null;
        Double decimalLongitude = isNumeric(longitude) ? Double.parseDouble(longitude) : null;

        // Look up locality in Tulane's GeoLocate service
        List<GeolocationResult> potentialMatches = service.queryGeoLocateMulti(country, stateProvince, county, locality, latitude, longitude);

        if (potentialMatches == null || potentialMatches.size() == 0) {
            result.addComment("GeoLocate service can't find coordinates of Locality.");
            result.setResultState(EnumDQResultState.EXTERNAL_PREREQUISITES_NOT_MET);
        } else if (latitude == null || longitude == null) {
            // Try to fill in missing values
            if (potentialMatches.size() > 0 && potentialMatches.get(0).getConfidence() > 80) {
                int thresholdDistanceMeters = (int) Math.round(thresholdDistanceKm * 1000);

                if (latitude != null && longitude == null) {
                    // Try to fill in the longitude
                    if (GeolocationResult.isLocationNearAResult(decimalLatitude, potentialMatches.get(0).getLongitude(),
                            potentialMatches, thresholdDistanceMeters)) {

                        // if latitude plus longitude from best match is near a match, propose
                        // the longitude from the best match.

                        // TODO: If we do this, then we need to add the datum, georeference source, georeference method, etc
                        result.addResult("longitude", potentialMatches.get(0).getLongitude().toString());

                        result.setResultState(EnumDQAmendmentResultState.FILLED_IN);
                        result.addComment("Added a longitude from geolocate as longitude was missing and geolocate " +
                                "had a confident match near the original line of latitude.");
                    }
                } else if (latitude == null && longitude != null) {
                    // Try to fill in the latitude
                    if (GeolocationResult.isLocationNearAResult(potentialMatches.get(0).getLatitude(), decimalLongitude,
                            potentialMatches, thresholdDistanceMeters)) {

                        // if latitude plus longitude from best match is near a match, propose
                        // the longitude from the best match.

                        // TODO: If we do this, then we need to add the datum, georeference source, georeference method, etc
                        result.addResult("latitude", potentialMatches.get(0).getLatitude().toString());

                        result.setResultState(EnumDQAmendmentResultState.FILLED_IN);
                        result.addComment("Added a latitude from geolocate as latitude was missing and geolocate " +
                                "had a confident match near the original line of longitude.");
                    }
                } else if (latitude == null && longitude == null) {
                    //Both coordinates in the original record are missing

                    Map<String, Object> correctedValues = new HashMap<>();

                    // TODO: If we do this, then we need to add the datum, georeference source, georeference method, etc.
                    result.addResult("latitude", potentialMatches.get(0).getLatitude().toString());
                    result.addResult("longitude", potentialMatches.get(0).getLongitude().toString());

                    result.setResultState(EnumDQAmendmentResultState.FILLED_IN);
                    result.addComment("Added a georeference using cached data or geolocate service since the " +
                            "original coordinates are missing and geolocate had a confident match.");

                } else {
                    result.setResultState(EnumDQAmendmentResultState.EXTERNAL_PREREQUISITES_NOT_MET);
                    result.addComment("No latitude and/or longitude provided, and geolocate didn't return a good match.");
                }
            }
        } else {
            result.setResultState(EnumDQAmendmentResultState.NO_CHANGE);
            result.addComment("latitude and longitude contain values, not changing.");
        }

        return result;
    }

    public GeoDQValidation checkAlternatives(String country, String stateProvince, String county, String waterBody, String latitude, String longitude) {
        GeoDQValidation result = new GeoDQValidation();

        Double originalLat = isNumeric(latitude) ? Double.parseDouble(latitude) : null;
        Double originalLong = isNumeric(longitude) ? Double.parseDouble(longitude) : null;

        // Construct a list of alternatives
        List<GeolocationAlternative> alternatives = GeolocationAlternative.constructListOfAlternatives(originalLat, originalLong);

        boolean flagError = false;
        boolean foundGoodMatch = false;

        // Check for possible error conditions

        // Check country and stateProvince
        if (country != null && country.length()>0) {
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

            // (2) Locality not inside country?
            if (GEOUtil.isCountryKnown(country)) {
                if (GEOUtil.isPointInCountry(country, originalLat, originalLong)) {
                    result.addComment("Original coordinate is inside country ("+country+").");
                } else {
                    result.addComment("Original coordinate is not inside country ("+country+").");
                    flagError = true;
                }
            } else {
                result.addComment("Can't find country: " + country + " in country name list");
            }

            if (stateProvince!=null && stateProvince.length()>0) {
                // (3) Locality not inside primary division?
                if (GEOUtil.isPrimaryKnown(country, stateProvince)) {
                    if (GEOUtil.isPointInPrimary(country, stateProvince, originalLat, originalLong)) {
                        result.addComment("Original coordinate is inside primary division ("+stateProvince+").");
                    } else {
                        result.addComment("Original coordinate is not inside primary division ("+stateProvince+").");
                        flagError = true;
                    }
                } else {
                    result.addComment("Can't find state/province: " + stateProvince + " in primaryDivision name list");
                }
            }
        }

        // (4) Is locality marine?
        Set<Path2D> setPolygon = null;
        try {
            setPolygon = new GISDataLoader().ReadLandData();
            //System.out.println("read data");
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (InvalidShapeFileException e) {
            logger.error(e.getMessage());
        }


        boolean isMarine = false;
        if ((country==null||country.length()==0) && (stateProvince==null||stateProvince.length()==0) && (county==null||county.length()==0)) {
            result.addComment("No country, state/province, or county provided, guessing that this is a marine locality. ");
            // no country provided, assume locality is marine
            isMarine = true;
        } else {
            if (waterBody!=null && waterBody.trim().length()>0) {
                if (waterBody.matches("(Indian|Pacific|Arctic|Atlantic|Ocean|Sea|Carribean|Mediteranian)")) {
                    isMarine = true;
                    result.addComment("A water body name that appears to be an ocean or a sea was provided, guessing that this is a marine locality. ");
                } else {
                    result.addComment("A country, state/province, or county was provided with a water body that doesn't appear to be an ocean or a sea, guessing that this is a non-marine locality. ");
                }
            } else {
                //addToComment("A country, state/province, or county was provided but no water body, guessing that this is a non-marine locality. ");
            }
        }
        if (!GEOUtil.isInPolygon(setPolygon, originalLong, originalLat, isMarine)) {
            if (isMarine) {
                result.addComment("Coordinate is on land for a supposedly marine locality.");
                flagError = true;
            } else {
                result.addComment("Coordinate is not on land for a supposedly non-marine locality.");
                double thresholdDistanceKmFromLand = 44.448d;  // 24 nautical miles, territorial waters plus contigouus zone.
                if (GEOUtil.isPointNearCountry(country, originalLat, originalLong, thresholdDistanceKmFromLand)) {
                    result.addComment("Coordinate is within 24 nautical miles of country boundary, could be a nearshore marine locality.");
                } else {
                    result.addComment("Coordinate is further than 24 nautical miles of country boundary, country in error or marine within EEZ.");
                    flagError = true;
                }
            }
        }

        if (flagError) {
            result.setResult(EnumDQValidationResult.NOT_COMPLIANT);
        } else {
            result.setResult(EnumDQValidationResult.COMPLIANT);
        }

        return result;
    }

    @Provides("COORDINATE_TRANSPOSITION")
    public GeoDQAmendment coordinateTransposition(String country, String stateProvince, String county, String locality, String waterBody, String latitude, String longitude) {
        GeoDQAmendment result = new GeoDQAmendment();

        //calculate the distance from the returned point and original point in the record
        //If the distance is smaller than a certainty, then use the original point --- GEOService, like GeoLocate can't parse detailed locality. In this case, the original point has higher confidence
        //Otherwise, use the point returned from GeoLocate
        //addToComment("Latitute and longitude are both present.");

        Double originalLat = isNumeric(latitude) ? Double.parseDouble(latitude) : null;
        Double originalLong = isNumeric(longitude) ? Double.parseDouble(longitude) : null;

        // Construct a list of alternatives
        List<GeolocationAlternative> alternatives = GeolocationAlternative.constructListOfAlternatives(originalLat, originalLong);

        boolean flagError = false;
        boolean foundGoodMatch = false;

        boolean isMarine = isMarine(country, stateProvince, county, waterBody);

        // Check for possible error conditions

        if ((isMarine && checkMarineLocality(country, originalLong, originalLat)) || (checkLatLongRange(originalLat, originalLong) && checkCountry(country, originalLat, originalLong) && checkState(country, stateProvince, originalLat, originalLong))) {
            result.setResultState(EnumDQAmendmentResultState.NO_CHANGE);
            result.addComment("latitude and logitude provide and within range and ountry and state contain valid values, not changing.");
        }

        // Look up locality in Tulane's GeoLocate service or cache
        List<GeolocationResult> potentialMatches = service.queryGeoLocateMulti(country, stateProvince, county, locality, latitude, longitude);

        // Geolocate returned some result, is original locality near that result?
        if (potentialMatches!=null && potentialMatches.size()>0) {
            if (GeolocationResult.isLocationNearAResult(originalLat, originalLong, potentialMatches, (int)Math.round(thresholdDistanceKm * 1000))) {
                result.addComment("Original coordinates are near (within georeference error radius or " +  thresholdDistanceKm + " km) the georeference for the locality text from the Geolocate service.  Accepting the original coordinates. ");
                flagError = false;
            } else {
                result.addComment("Original coordinates are not near (within georeference error radius or " +  thresholdDistanceKm + " km) the georeference for the locality text from the Geolocate service. ");
                flagError = true;
            }
        }

        if (flagError) {
            // If some error condition was found, see if any transposition returns a plausible locality
            boolean matchFound = false;

            Iterator<GeolocationAlternative> i = alternatives.iterator();
            while (i.hasNext() && !matchFound) {
                GeolocationAlternative alt = i.next();
                if (potentialMatches !=null && potentialMatches.size()>0) {
                    if (GeolocationResult.isLocationNearAResult(alt.getLatitude(), alt.getLongitude(), potentialMatches, (int)Math.round(thresholdDistanceKm * 1000))) {
                        result.setResultState(EnumDQAmendmentResultState.TRANSPOSED);

                        result.addComment("Modified coordinates ("+alt.getAlternative()+") are near (within georeference error radius or " +  thresholdDistanceKm + " km) the georeference for the locality text from the Geolocate service.  Accepting the " + alt.getAlternative() + " coordinates. ");
                        result.addResult("latitude", Double.toString(alt.getLatitude()));
                        result.addResult("longitude", Double.toString(alt.getLongitude()));

                       matchFound = true;
                    }
                } else {
                    if (isMarine) {
                        if (country!=null && GEOUtil.isCountryKnown(country)) {
                            double thresholdDistanceKmFromLand = 44.448d;  // 24 nautical miles, territorial waters plus contigouus zone.
                            if (GEOUtil.isPointNearCountry(country, originalLat, originalLong, thresholdDistanceKmFromLand)) {
                                result.setResultState(EnumDQAmendmentResultState.TRANSPOSED);

                                result.addComment("Modified coordinate (" + alt.getAlternative() + ") is within 24 nautical miles of country boundary.");
                                result.addResult("latitude", Double.toString(alt.getLatitude()));
                                result.addResult("longitude", Double.toString(alt.getLongitude()));

                                matchFound = true;
                            }
                        }
                    } else {
                        if (GEOUtil.isCountryKnown(country) &&
                                GEOUtil.isPointInCountry(country, alt.getLatitude(), alt.getLongitude())) {
                            if (GEOUtil.isPrimaryKnown(country, stateProvince) &&
                                    GEOUtil.isPointInPrimary(country, stateProvince, originalLat, originalLong)) {
                                result.setResultState(EnumDQAmendmentResultState.TRANSPOSED);

                                result.addComment("Modified coordinate ("+alt.getAlternative()+") is inside stateProvince ("+stateProvince+").");
                                result.addResult("latitude", Double.toString(alt.getLatitude()));
                                result.addResult("longitude", Double.toString(alt.getLongitude()));

                                matchFound = true;
                            }
                        }
                    }
                }
            }
        } else {
            result.setResultState(EnumDQAmendmentResultState.NO_CHANGE);
        }

        return result;
    }


    private static boolean checkLatLongRange(double originalLat, double originalLong) {
        // (1) Latitude and longitude out of range
        return Math.abs(originalLat) > 90 || Math.abs(originalLong) > 180;
    }

    private static boolean checkCountry(String country, double originalLat, double originalLong) {
            //standardize country names
            if (country.toUpperCase().equals("USA") || country.toUpperCase().equals("U.S.A.") || country.toLowerCase().equals("united states of america")) {
                country = "United States";
            } else {
                country = country.toUpperCase();
            }

            // (2) Locality not inside country?
            return GEOUtil.isCountryKnown(country) && GEOUtil.isPointInCountry(country, originalLat, originalLong);
    }

    private static boolean checkState(String country, String stateProvince, double originalLat, double originalLong) {
        // (3) Locality not inside primary division?
        return GEOUtil.isPrimaryKnown(country, stateProvince) && GEOUtil.isPointInPrimary(country, stateProvince, originalLat, originalLong);
    }

    private static boolean isMarine(String country, String stateProvince, String county, String waterBody) {
        // if no country provided, assume locality is marine
        if ((country == null || country.length() == 0) && (stateProvince == null || stateProvince.length() == 0) &&
                (county == null || county.length() == 0)) {
            return true;
        } else if (waterBody != null && waterBody.trim().length() > 0 && waterBody.matches("(Indian|Pacific|Arctic|Atlantic|Ocean|Sea|Carribean|Mediteranian)")) {
             return true;
        }

        return false;
    }

    private static boolean checkMarineLocality(String country, double originalLong, double originalLat) {
        // (4) Is locality marine?
        Set<Path2D> setPolygon = null;
        try {
            setPolygon = new GISDataLoader().ReadLandData();
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (InvalidShapeFileException e) {
            logger.error(e.getMessage());
        }

        double thresholdDistanceKmFromLand = 44.448d;  // 24 nautical miles, territorial waters plus contigouus zone.
        return GEOUtil.isInPolygon(setPolygon, originalLong, originalLat, true) ||
                GEOUtil.isPointNearCountry(country, originalLat, originalLong, thresholdDistanceKmFromLand);
    }

    private static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch(NumberFormatException e){
            return false;
        }

        return true;
    }
}
