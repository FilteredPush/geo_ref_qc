package org.filteredpush.qc.georeference;

import org.datakurator.ffdq.annotations.Provides;
import org.datakurator.ffdq.api.EnumDQAmendmentResultState;
import org.datakurator.ffdq.api.EnumDQResultState;
import org.datakurator.ffdq.api.EnumDQValidationResult;
import org.filteredpush.qc.georeference.util.GEOUtil;
import org.filteredpush.qc.georeference.util.GeolocationResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lowery on 2/24/17.
 */
public class DwCGeoRefDQ {
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

    @Provides(value = "COORDINATE_IN_COUNTRY")
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

    @Provides(value = "COORDINATE_FILLED_IN_FROM_SERVICE")
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
        }

        // Try to fill in missing values
        if (latitude == null || longitude == null) {
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

    private static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch(NumberFormatException e){
            return false;
        }

        return true;
    }
}
