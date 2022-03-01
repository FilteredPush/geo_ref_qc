/* NOTE: requires the ffdq-api dependecy in the maven pom.xml */

package org.filteredpush.qc.georeference;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datakurator.ffdq.annotations.*;
import org.datakurator.ffdq.api.DQResponse;
import org.datakurator.ffdq.model.ResultState;
import org.filteredpush.qc.georeference.util.GEOUtil;
import org.datakurator.ffdq.api.result.*;

@Mechanism(value="71fa3762-0dfa-43c7-a113-d59797af02e8",label="Kurator: Date Validator - DwCGeoRefDQ:v2.0.0")
public class DwCGeoRefDQ{
	
    private static final Log logger = LogFactory.getLog(DwCGeoRefDQ.class);
	
    /**
     * #20 Validation SingleRecord Conformance: countrycode notstandard
     *
     * Provides: VALIDATION_COUNTRYCODE_NOTSTANDARD
     *
     * @param countryCode the provided dwc:countryCode to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("0493bcfb-652e-4d17-815b-b0cce0742fbe")
    public static DQResponse<ComplianceValue> validationCountrycodeNotstandard(
    		@ActedUpon("dwc:countryCode") String countryCode) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the ISO 3166 service was 
        // not available; INTERNAL_PREREQUISITES_NOT_MET if the dwc:countryCode 
        // was EMPTY; COMPLIANT if dwc:countryCode is a valid ISO (ISO 
        // 3166-1-alpha-2 country codes) value; otherwise NOT_COMPLIANT 
        //
        
        // TODO: Implement lookup of current country codes values
        // https://restcountries.eu/#api-endpoints-list-of-codes mentioned in list, but is currently timing out.
        // wikidata is a possible source for country codes.
        // test is defined as not parameterized, so this would be internal implementation, 
        // and can fall back on hardcoded list.
        // list in json is available from https://pkgstore.datahub.io/core/country-list/data_json/data/8c458f2d15d9f2119654b29ede6e45b8/data_json.json
        // see metadata at: https://datahub.io/core/country-list
        
        if (GEOUtil.isEmpty(countryCode)) { 
        	result.addComment("dwc:countryCode is empty");
        	result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        } else if (countryCode.length() != 2) { 
        	result.addComment("the value provided for dwc:countryCode does not consist of exactly two letters");
        	result.setResultState(ResultState.RUN_HAS_RESULT);
        	result.setValue(ComplianceValue.NOT_COMPLIANT);
        } else if (!countryCode.toUpperCase().equals(countryCode)) { 
        	result.addComment("the value provided for dwc:countryCode does not consist of two upper case letters");
        	result.setResultState(ResultState.RUN_HAS_RESULT);
        	result.setValue(ComplianceValue.NOT_COMPLIANT);
        } else if (GEOUtil.isISOTwoLetterCountryCode(countryCode)) { 
        	result.addComment("the value provided for dwc:countryCode is a case sensitive exact match to a two letter country code");
        	result.setResultState(ResultState.RUN_HAS_RESULT);
        	result.setValue(ComplianceValue.COMPLIANT);
        } else { 
        	result.addComment("the value provided for dwc:countryCode is not a case sensitive exact match to a two letter country code");
        	result.setResultState(ResultState.RUN_HAS_RESULT);
        	result.setValue(ComplianceValue.NOT_COMPLIANT);
        }
        

        return result;
    }

    /**
     * #21 Validation SingleRecord Conformance: country notstandard
     *
     * Provides: VALIDATION_COUNTRY_NOTSTANDARD
     *
     * @param country the provided dwc:country to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("69b2efdc-6269-45a4-aecb-4cb99c2ae134")
    public DQResponse<ComplianceValue> validationCountryNotstandard(@ActedUpon("dwc:country") String country) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // service was not available; INTERNAL_PREREQUISITES_NOT_MET 
        // if dwc:country was EMPTY; COMPLIANT if value of dwc:country 
        // is a place type equivalent to "nation" by the bdq:sourceAuthority 
        //service; otherwise NOT_COMPLIANT 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority

        return result;
    }

    /**
     * #24 Validation SingleRecord Conformance: mindepth greaterthan maxdepth
     *
     * Provides: VALIDATION_MINDEPTH_GREATERTHAN_MAXDEPTH
     *
     * @param maximumDepthInMeters the provided dwc:maximumDepthInMeters to evaluate
     * @param minimumDepthInMeters the provided dwc:minimumDepthInMeters to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("8f1e6e58-544b-4365-a569-fb781341644e")
    public static DQResponse<ComplianceValue> validationMindepthGreaterthanMaxdepth(
    		@ActedUpon("dwc:minimumDepthInMeters") String minimumDepthInMeters,
    		@ActedUpon("dwc:maximumDepthInMeters") String maximumDepthInMeters) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:minimumDepthInMeters 
        // or dwc:maximumDepthInMeters is EMPTY, or the values are 
        // not zero or a positive number; COMPLIANT if the value of 
        // dwc:minimumDepthInMeters is less than or equal to the value 
        // of dwc:maximumDepthInMeters; otherwise NOT_COMPLIANT 

        if (GEOUtil.isEmpty(maximumDepthInMeters)) { 
        	result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        	result.addComment("the provided value for dwc:maximumDepthInMeters is empty");
        	if (GEOUtil.isEmpty(minimumDepthInMeters)) {
        		result.addComment("the provided value for dwc:minimumDepthInMeters is empty");
        	}
        } else if (GEOUtil.isEmpty(minimumDepthInMeters)) { 
        	result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        	result.addComment("the provided value for dwc:maximumDepthInMeters is empty");
    	} else if (!GEOUtil.isNumericCharacters(minimumDepthInMeters)) { 
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("provided value for dwc:minimumDepthInMeters contains non-numeric characters.");
        } else { 
        	try { 
        		Double maxDepthVal = Double.parseDouble(maximumDepthInMeters);
        		try { 
        			Double minDepthVal = Double.parseDouble(minimumDepthInMeters);
            		if (maxDepthVal < 0d) { 
            			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
            			result.addComment("The value provided for dwc:maximumDepthInMeters is less than zero");
            		} else if (minDepthVal < 0d ) { 
            			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
            			result.addComment("The value provided for dwc:minimumDepthInMeters is less than zero");
            		} else  {
            			result.setResultState(ResultState.RUN_HAS_RESULT);
            			if (minDepthVal <= maxDepthVal) {
            				result.setValue(ComplianceValue.COMPLIANT);
            				result.addComment("The value provided for dwc:minimumDepthInMeters is less than or equal to that provided for dwc:maximumDepthInMeters");
            			} else {  
            				result.setValue(ComplianceValue.NOT_COMPLIANT);
            				result.addComment("The value provided for dwc:minimumDepthInMeters is greater than that provided for dwc:maximumDepthInMeters");
            			}
            		}        			
        		} catch (NumberFormatException e) { 
        			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        			result.addComment("the provided value for dwc:minimumDepthInMeters is not a number");
        		}

        	} catch (NumberFormatException e) { 
        		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        		result.addComment("the provided value for dwc:maximumDepthInMeters is not a number");
        	}
		}
        
        return result;
    }

    /**
     * #30 Validation SingleRecord Conformance: decimallongitude outofrange
     *
     * Provides: VALIDATION_DECIMALLONGITUDE_OUTOFRANGE
     *
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("0949110d-c06b-450e-9649-7c1374d940d1")
    public static DQResponse<ComplianceValue> validationDecimallongitudeOutofrange(
    		@ActedUpon("dwc:decimalLongitude") String decimalLongitude) {

    	DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:decimalLongitude is 
        // EMPTY or the value is not a number; COMPLIANT if the value 
        // of dwc:decimalLongitude is between -180 and 180 degrees, 
        // inclusive; otherwise NOT_COMPLIANT 
        
    	if (GEOUtil.isEmpty(decimalLongitude)) { 
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("provided value for dwc:decimalLongitude is empty.");
    	} else if (!GEOUtil.isNumericCharacters(decimalLongitude)) { 
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("provided value for dwc:decimalLongitude contains non-numeric characters.");
    	} else { 
    		try {
    			Double longitudeNumber = Double.parseDouble(decimalLongitude);
    			if (longitudeNumber <= 180d && longitudeNumber >= -180d) { 
    				result.setResultState(ResultState.RUN_HAS_RESULT);
    				result.setValue(ComplianceValue.COMPLIANT);
    				result.addComment("the provided value for dwc:decimalLongitude is a number between -180 and 180 inclusive.");
    			} else { 
    				result.setResultState(ResultState.RUN_HAS_RESULT);
    				result.setValue(ComplianceValue.NOT_COMPLIANT);
    				result.addComment("the provided value for dwc:decimalLongitude is a number outside the range -180 to 180.");
    			}
    		} catch (NumberFormatException e) { 
    			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    			result.addComment("provided value for dwc:decimalLongitude cannot be parsed as a number.");

    		}
    	}
        
        return result;
    }

    /**
     * #32 Amendment SingleRecord Completeness: coordinates from verbatim
     *
     * Provides: AMENDMENT_COORDINATES_FROM_VERBATIM
     *
     * @param verbatimCoordinateSystem the provided dwc:verbatimCoordinateSystem to evaluate
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate
     * @param verbatimCoordinates the provided dwc:verbatimCoordinates to evaluate
     * @param verbatimLongitude the provided dwc:verbatimLongitude to evaluate
     * @param verbatimSRS the provided dwc:verbatimSRS to evaluate
     * @param verbatimLatitude the provided dwc:verbatimLatitude to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Provides("3c2590c7-af8a-4eb4-af57-5f73ba9d1f8e")
    public DQResponse<AmendmentValue> amendmentCoordinatesFromVerbatim(@ActedUpon("dwc:verbatimCoordinateSystem") String verbatimCoordinateSystem, @ActedUpon("dwc:decimalLatitude") String decimalLatitude, @ActedUpon("dwc:decimalLongitude") String decimalLongitude, @ActedUpon("dwc:verbatimCoordinates") String verbatimCoordinates, @ActedUpon("dwc:verbatimLongitude") String verbatimLongitude, @ActedUpon("dwc:verbatimSRS") String verbatimSRS, @ActedUpon("dwc:verbatimLatitude") String verbatimLatitude) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if Verbatim coordinates (either 
        // dwc:verbatimLatitude and dwc:verbatimLongitude or dwc:verbatimCoordinates) 
        // were not interpretable into coordinates as decimal degrees 
        // or either dwc:decimalLatitude or dwc:decimalLongitude was 
        // not EMPTY; AMENDED if dwc:decimalLatitude and dwc:decimalLongitude 
        // were populated from information in verbatim coordinate information 
        // (dwc:verbatimCoordinates or dwc:verbatimLatitude and dwc:verbatimLongitude, 
        // plus dwc:verbatimCoordinateSystem and dwc:verbatimSRS); 
        //otherwise NOT_CHANGED 

        return result;
    }

    @Provides("0bb8297d-8f8a-42d2-80c1-558f29efe798")
    public static DQResponse<ComplianceValue> validationMinelevationOutofrange(
    		@ActedUpon("dwc:minimumElevationInMeters") String minimumElevationInMeters) {
    	return DwCGeoRefDQ.validationMinelevationOutofrange(minimumElevationInMeters, -430d, 8850d);
    }
    /**
     * #39 Validation SingleRecord Conformance: minelevation outofrange
     *
     * Provides: VALIDATION_MINELEVATION_OUTOFRANGE
     *
     * @param minimumElevationInMeters the provided dwc:minimumElevationInMeters to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("0bb8297d-8f8a-42d2-80c1-558f29efe798")
    public static DQResponse<ComplianceValue> validationMinelevationOutofrange(
    		@ActedUpon("dwc:minimumElevationInMeters") String minimumElevationInMeters,
    		@Parameter(name="bdq:minimumValidElevationInMeters") Double minimumValidElevationInMeters,
    		@Parameter(name="bdq:maximumValidElevationInMeters") Double maximumValidElevationInMeters
    		) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:minimumElevationInMeters 
        // is EMPTY or the value is not a number; COMPLIANT if the 
        // value of dwc:minimumElevationInMeters is within the Parameter 
        //range; otherwise NOT_COMPLIANT 

        // Parameters. This test is defined as parameterized.
        // Default values: bdq:minimumValidElevationInMeters="-430"; bdq:maximumValidElevationInMeters="8850"

        if (minimumValidElevationInMeters==null) { 
        	minimumValidElevationInMeters = -430d;
        }
        if (maximumValidElevationInMeters==null) { 
        	maximumValidElevationInMeters = 8850d;
        }
        String range = Double.toString(minimumValidElevationInMeters) + " to " + Double.toString(maximumValidElevationInMeters);
        
    	if (GEOUtil.isEmpty(minimumElevationInMeters)) { 
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("provided value for dwc:minimumElevationInMeters is empty.");
    	} else if (!GEOUtil.isNumericCharacters(minimumElevationInMeters)) { 
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("provided value for dwc:minimumElevationInMeters contains non-numeric characters.");
    	} else { 
    		try {
    			Double longitudeNumber = Double.parseDouble(minimumElevationInMeters);
    			if (longitudeNumber <= maximumValidElevationInMeters && longitudeNumber >= minimumValidElevationInMeters) { 
    				result.setResultState(ResultState.RUN_HAS_RESULT);
    				result.setValue(ComplianceValue.COMPLIANT);
    				result.addComment("the provided value for dwc:minimumElevationInMeters is a number between " + range + "  inclusive.");
    			} else { 
    				result.setResultState(ResultState.RUN_HAS_RESULT);
    				result.setValue(ComplianceValue.NOT_COMPLIANT);
    				result.addComment("the provided value for dwc:minimumElevationInMeters is a number outside the range " + range + ".");
    			}
    		} catch (NumberFormatException e) { 
    			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    			result.addComment("provided value for dwc:minimumElevationInMeters cannot be parsed as a number.");
    		}
    	}
        
        return result;
    }

    /**
     * #40 Validation SingleRecord Completeness: location empty
     *
     * Provides: VALIDATION_LOCATION_EMPTY
     *
     * @param higherGeography the provided dwc:higherGeography to evaluate
     * @param higherGeographyID the provided dwc:higherGeographyID to evaluate
     * @param continent the provided dwc:continent to evaluate
     * @param waterBody the provided dwc:waterBody to evaluate
     * @param islandGroup the provided dwc:islandGroup to evaluate
     * @param island the provided dwc:island to evaluate
     * @param country the provided dwc:country to evaluate
     * @param countryCode the provided dwc:countryCode to evaluate
     * @param stateProvince the provided dwc:stateProvince to evaluate
     * @param county the provided dwc:county to evaluate
     * @param municipality the provided dwc:municipality to evaluate
     * @param locality the provided dwc:locality to evaluate
     * @param locationID the provided dwc:locationID to evaluate
     * @param verbatimLocality the provided dwc:verbatimLocality to evaluate
     * @param verbatimCoordinates the provided dwc:verbatimCoordinates to evaluate
     * @param verbatimLatitude the provided dwc:verbatimLatitude to evaluate
     * @param verbatimLongitude the provided dwc:verbatimLongitude to evaluate
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate
     * @param footprintWKT the provided dwc:footprintWKT to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("58486cb6-1114-4a8a-ba1e-bd89cfe887e9")
    public static DQResponse<ComplianceValue> validationLocationEmpty(
    		@ActedUpon("dwc:higherGeography") String higherGeography, 
    		@ActedUpon("dwc:higherGeographyID") String higherGeographyID, 
    		@ActedUpon("dwc:continent") String continent, 
    		@ActedUpon("dwc:waterBody") String waterBody, 
    		@ActedUpon("dwc:islandGroup") String islandGroup, 
    		@ActedUpon("dwc:island") String island, 
    		@ActedUpon("dwc:country") String country, 
    		@ActedUpon("dwc:countryCode") String countryCode, 
    		@ActedUpon("dwc:stateProvince") String stateProvince, 
    		@ActedUpon("dwc:county") String county, 
    		@ActedUpon("dwc:municipality") String municipality, 
    		@ActedUpon("dwc:locality") String locality, 
    		@ActedUpon("dwc:locationID") String locationID, 
    		@ActedUpon("dwc:verbatimLocality") String verbatimLocality, 
    		@ActedUpon("dwc:verbatimCoordinates") String verbatimCoordinates, 
    		@ActedUpon("dwc:verbatimLatitude") String verbatimLatitude, 
    		@ActedUpon("dwc:verbatimLongitude") String verbatimLongitude, 
    		@ActedUpon("dwc:decimalLatitude") String decimalLatitude, 
    		@ActedUpon("dwc:decimalLongitude") String decimalLongitude, 
    		@ActedUpon("dwc:footprintWKT") String footprintWKT) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // COMPLIANT if at least one term needed to determine the location 
        // of the entity exists and is not EMPTY; otherwise NOT_COMPLIANT 
        //
        
        result.setResultState(ResultState.RUN_HAS_RESULT);
        if (
        		GEOUtil.isEmpty(higherGeography) && 
        		GEOUtil.isEmpty(higherGeographyID) && 
        		GEOUtil.isEmpty(continent) && 
        		GEOUtil.isEmpty(waterBody) && 
        		GEOUtil.isEmpty(islandGroup) && 
        		GEOUtil.isEmpty(island) && 
        		GEOUtil.isEmpty(country) && 
        		GEOUtil.isEmpty(countryCode) && 
        		GEOUtil.isEmpty(stateProvince) && 
        		GEOUtil.isEmpty(county) && 
        		GEOUtil.isEmpty(municipality) && 
        		GEOUtil.isEmpty(locality) && 
        		GEOUtil.isEmpty(locationID) && 
        		GEOUtil.isEmpty(verbatimLocality) && 
        		GEOUtil.isEmpty(verbatimCoordinates) && 
        		GEOUtil.isEmpty(verbatimLatitude) && 
        		GEOUtil.isEmpty(verbatimLongitude) && 
        		GEOUtil.isEmpty(decimalLatitude) && 
        		GEOUtil.isEmpty(decimalLongitude) && 
        		GEOUtil.isEmpty(footprintWKT)
        	)
		{ 
			result.setValue(ComplianceValue.NOT_COMPLIANT);
			result.addComment("All terms needed to determine the location are empty");
		} else { 
			result.setValue(ComplianceValue.COMPLIANT);
			result.addComment("At least one location term contains a value.");
		}
        return result;
    }

    /**
     * #42 Validation SingleRecord Completeness: country empty
     *
     * Provides: VALIDATION_COUNTRY_EMPTY
     *
     * @param country the provided dwc:country to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("6ce2b2b4-6afe-4d13-82a0-390d31ade01c")
    public static DQResponse<ComplianceValue> validationCountryEmpty(@ActedUpon("dwc:country") String country) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // COMPLIANT if dwc:country is not EMPTY; otherwise NOT_COMPLIANT 
        //

        result.setResultState(ResultState.RUN_HAS_RESULT);
        if (GEOUtil.isEmpty(country))
		{ 
			result.setValue(ComplianceValue.NOT_COMPLIANT);
			result.addComment("The value provided for dwc:country is empty");
		} else { 
			result.setValue(ComplianceValue.COMPLIANT);
			result.addComment("dwc:country contains a value.");
		}
        
        return result;
    }

    /**
     * #43 Amendment SingleRecord Conformance: coordinates converted
     *
     * Provides: AMENDMENT_COORDINATES_CONVERTED
     *
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate
     * @param coordinateUncertaintyInMeters the provided dwc:coordinateUncertaintyInMeters to evaluate
     * @param geodeticDatum the provided dwc:geodeticDatum to evaluate
     * @param coordinatePrecision the provided dwc:coordinatePrecision to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Provides("620749b9-7d9c-4890-97d2-be3d1cde6da8")
    public DQResponse<AmendmentValue> amendmentCoordinatesConverted(@ActedUpon("dwc:decimalLatitude") String decimalLatitude, @ActedUpon("dwc:decimalLongitude") String decimalLongitude, @ActedUpon("dwc:coordinateUncertaintyInMeters") String coordinateUncertaintyInMeters, @ActedUpon("dwc:geodeticDatum") String geodeticDatum, @ActedUpon("dwc:coordinatePrecision") String coordinatePrecision) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:decimalLatitude and 
        // dwc:decimalLongitude were EMPTY or the value of dwc:geodeticDatum 
        // was not interpretable; AMENDED if the values of dwc:decimalLatitude, 
        // dwc:decimalLongitude, and dwc:geodeticDatum were changed 
        // based on a conversion between spatial reference systems; 
        //otherwise NOT_CHANGED 

        return result;
    }

    /**
     * #48 Amendment SingleRecord Conformance: countrycode standardized
     *
     * Provides: AMENDMENT_COUNTRYCODE_STANDARDIZED
     *
     * @param countryCode the provided dwc:countryCode to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Provides("fec5ffe6-3958-4312-82d9-ebcca0efb350")
    public DQResponse<AmendmentValue> amendmentCountrycodeStandardized(@ActedUpon("dwc:countryCode") String countryCode) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the ISO 3166 service was 
        // not available; INTERNAL_PREREQUISTITES_NOT_MET if the value 
        // of dwc:countryCode is EMPTY; AMENDED if a valid ISO 3166-1-alpha-2 
        // country code could be unambiguously interpreted from the 
        //value of dwc:countryCode; otherwise NOT_CHANGED 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority
        
        if (GEOUtil.isEmpty(countryCode)) {
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("provided value for dwc:countryCode is empty.");
        } else { 
        	// TODO 2 letter code, capitalized matches?
        	// TODO 3 letter code matches?
        	// TODO string matches?
        }
        

        return result;
    }

    /**
     * #50 Validation SingleRecord Consistency: coordinates countrycode inconsistent
     *
     * Provides: VALIDATION_COORDINATES_COUNTRYCODE_INCONSISTENT
     *
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate
     * @param countryCode the provided dwc:countryCode to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("adb27d29-9f0d-4d52-b760-a77ba57a69c9")
    public DQResponse<ComplianceValue> validationCoordinatesCountrycodeInconsistent(@ActedUpon("dwc:decimalLatitude") String decimalLatitude, @ActedUpon("dwc:decimalLongitude") String decimalLongitude, @ActedUpon("dwc:countryCode") String countryCode) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // service was not available; INTERNAL_PREREQUISITES_NOT_MET 
        // if one or more of dwc:decimalLatitude, dwc:decimalLongitude, 
        // or dwc:countryCode are EMPTY or contain values that cannot 
        // be interpreted; COMPLIANT if the geographic coordinates 
        // fall on or within the boundary defined by the union of the 
        // boundary of the country from dwc:countryCode plus it's Exclusive 
        // Economic Zone, if any, plus an exterior buffer given by 
        //bdq:spatialBufferInMeters; otherwise NOT_COMPLIANT 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority; bdq:spatialBufferInMeters

        return result;
    }

    /**
     * #51 Validation SingleRecord Conformance: coordinates terrestrialmarine
     *
     * Provides: VALIDATION_COORDINATES_TERRESTRIALMARINE
     *
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("b9c184ce-a859-410c-9d12-71a338200380")
    public DQResponse<ComplianceValue> validationCoordinatesTerrestrialmarine(
    		@ActedUpon("dwc:decimalLatitude") String decimalLatitude, 
    		@ActedUpon("dwc:decimalLongitude") String decimalLongitude) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // service was not available; INTERNAL_PREREQUISITES_NOT_MET 
        // if the non-marine/marine status of the taxon is not provided 
        // or is not interpretable from bdq:sourceAuthority or the 
        // values of dwc:decimalLatitude or dwc:decimalLongitude are 
        // EMPTY; COMPLIANT if a taxon coded as non-marine by the bdq:sourceAuthority 
        // has geographic coordinates that fall within non-marine boundaries 
        // plus an exterior buffer given by bdq:spatialBufferInMeters 
        // or a marine taxon according to the bdq:sourceAuthority has 
        // geographic coordinates that fall within marine boundaries 
        // plus an exterior buffer given by bdq:spatialBufferInMeters; 
        //otherwise NOT_COMPLIANT 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority; bdq:spatialBufferInMeters

        return result;
    }

    /**
     * #54 Amendment SingleRecord Consistency: coordinates transposed
     *
     * Provides: AMENDMENT_COORDINATES_TRANSPOSED
     *
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate
     * @param countryCode the provided dwc:countryCode to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Provides("f2b4a50a-6b2f-4930-b9df-da87b6a21082")
    public DQResponse<AmendmentValue> amendmentCoordinatesTransposed(
    		@ActedUpon("dwc:decimalLatitude") String decimalLatitude, 
    		@ActedUpon("dwc:decimalLongitude") String decimalLongitude, 
    		@Consulted("dwc:countryCode") String countryCode) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if any of dwc:decimalLatitude 
        // or dwc:decimalLongitude or dwc:countryCode are EMPTY; AMENDED 
        // if the geographic coordinates were transposed or one or 
        // more of the signs of the coordinates were reversed to place 
        // the record in the region defined by the supplied dwc:countryCode; 
        //otherwise NOT_CHANGED 

        return result;
    }

    /**
     * #55 Amendment SingleRecord Completeness: mindepth-maxdepth from verbatim
     *
     * Provides: AMENDMENT_MINDEPTH-MAXDEPTH_FROM_VERBATIM
     *
     * @param verbatimDepth the provided dwc:verbatimDepth to evaluate
     * @param maximumDepthInMeters the provided dwc:maximumDepthInMeters to evaluate
     * @param minimumDepthInMeters the provided dwc:minimumDepthInMeters to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Provides("c5658b83-4471-4f57-9d94-bf7d0a96900c")
    public DQResponse<AmendmentValue> amendmentMindepthMaxdepthFromVerbatim(@ActedUpon("dwc:verbatimDepth") String verbatimDepth, @ActedUpon("dwc:maximumDepthInMeters") String maximumDepthInMeters, @ActedUpon("dwc:minimumDepthInMeters") String minimumDepthInMeters) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:verbatimDepth is EMPTY 
        // or the value is not unambiguously interpretable or dwc:minimumDepthInMeters 
        // and dwc:maximumDepthInMeters are not EMPTY; AMENDED if the 
        // value of dwc:minimumDepthInMeters and/or dwc:maximumDepthInMeters 
        // were unambiguously determined from dwc:verbatimDepth; otherwise 
        // NOT_CHANGED 

        return result;
    }

    /**
     * #56 Validation SingleRecord Consistency: coordinates state-province inconsistent
     *
     * Provides: VALIDATION_COORDINATES_STATEPROVINCE_INCONSISTENT
     *
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate
     * @param geodeticDatum the provided dwc:geodeticDatum to evaluate
     * @param stateProvince the provided dwc:stateProvince to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("f18a470b-3fe1-4aae-9c65-a6d3db6b550c")
    public DQResponse<ComplianceValue> validationCoordinatesStateprovinceInconsistent(@ActedUpon("dwc:decimalLatitude") String decimalLatitude, @ActedUpon("dwc:decimalLongitude") String decimalLongitude, @ActedUpon("dwc:geodeticDatum") String geodeticDatum, @ActedUpon("dwc:stateProvince") String stateProvince) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // service was not available; INTERNAL_PREREQUISITES_NOT_MET 
        // if the values of dwc:decimalLatitude, dwc:decimalLongitude, 
        // and dwc:stateProvince are EMPTY; COMPLIANT if the geographic 
        // coordinates fall on or within the bdq:spatialBufferInMeters 
        // boundary of the geometry of the given dwc:stateProvince; 
        //otherwise NOT_COMPLIANT 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority; bdq:spatialBufferInMeters

        return result;
    }

    /**
     * #59 Validation SingleRecord Conformance: geodeticdatum notstandard
     *
     * Provides: VALIDATION_GEODETICDATUM_NOTSTANDARD
     *
     * @param geodeticDatum the provided dwc:geodeticDatum to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("7e0c0418-fe16-4a39-98bd-80e19d95b9d1")
    public DQResponse<ComplianceValue> validationGeodeticdatumNotstandard(@ActedUpon("dwc:geodeticDatum") String geodeticDatum) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // service used to look up the EPSG vocabulary is not available, 
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:geodeticDatum is EMPTY; 
        // COMPLIANT if the value of dwc:geodeticDatum is a valid EPSG 
        // CRS Code (with or without the "epsg" namespace prepended), 
        // or an unambiguous alphanumeric CRS or datum code; otherwise 
        //NOT_COMPLIANT 

        return result;
    }

    /**
     * #60 Amendment SingleRecord Conformance: geodeticdatum standardized
     *
     * Provides: AMENDMENT_GEODETICDATUM_STANDARDIZED
     *
     * @param geodeticDatum the provided dwc:geodeticDatum to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Provides("0345b325-836d-4235-96d0-3b5caf150fc0")
    public DQResponse<AmendmentValue> amendmentGeodeticdatumStandardized(@ActedUpon("dwc:geodeticDatum") String geodeticDatum) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // service was not available; INTERNAL_PREREQUISITES_NOT_MET 
        // if dwc:geodeticDatum is EMPTY; AMENDED if the value of dwc:geodeticDatum 
        // has been standardized using the bdq:sourceAuthority service; 
        //otherwise NOT_CHANGED 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority

        return result;
    }

    /**
     * #62 Validation SingleRecord Consistency: country countrycode inconsistent
     *
     * Provides: VALIDATION_COUNTRY_COUNTRYCODE_INCONSISTENT
     *
     * @param country the provided dwc:country to evaluate
     * @param countryCode the provided dwc:countryCode to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("b23110e7-1be7-444a-a677-cdee0cf4330c")
    public DQResponse<ComplianceValue> validationCountryCountrycodeInconsistent(@ActedUpon("dwc:country") String country, @ActedUpon("dwc:countryCode") String countryCode) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // service was not available; INTERNAL_PREREQUISITES_NOT_MET 
        // if either of the terms dwc:country or dwc:countryCode are 
        // EMPTY; COMPLIANT if the value of the ISO 3166-1-alpha-2 
        // country code determined from the value of dwc:country is 
        // equal to the value of dwc:countryCode; otherwise NOT_COMPLIANT 
        //

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority

        return result;
    }

    /**
     * #68 Amendment SingleRecord Completeness: minelevation-maxelevation from verbatim
     *
     * Provides: AMENDMENT_MINELEVATION-MAXELEVATION_FROM_VERBATIM
     *
     * @param minimumElevationInMeters the provided dwc:minimumElevationInMeters to evaluate
     * @param maximumElevationInMeters the provided dwc:maximumElevationInMeters to evaluate
     * @param verbatimElevation the provided dwc:verbatimElevation to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Provides("2d638c8b-4c62-44a0-a14d-fa147bf9823d")
    public DQResponse<AmendmentValue> amendmentMinelevationMaxelevationFromVerbatim(@ActedUpon("dwc:minimumElevationInMeters") String minimumElevationInMeters, @ActedUpon("dwc:maximumElevationInMeters") String maximumElevationInMeters, @ActedUpon("dwc:verbatimElevation") String verbatimElevation) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:verbatimElevation 
        // is EMPTY or thevalue is not unambiguously interpretable 
        // or dwc:minimumElevationInMeters and/or dwc:maximumElevationInMeters 
        // are not EMPTY; AMENDED if the values of dwc:minimumElevationInMeters 
        // and/or dwc:maximumElevationInMeters were unambiguously interpreted 
        //from dwc:verbatimElevation; otherwise NOT_CHANGED 

        return result;
    }

    /**
     * #73 Amendment SingleRecord Completeness: countrycode from coordinates
     *
     * Provides: AMENDMENT_COUNTRYCODE_FROM_COORDINATES
     *
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate
     * @param geodeticDatum the provided dwc:geodeticDatum to evaluate
     * @param countryCode the provided dwc:countryCode to evaluate
     * @param coordinatePrecision the provided dwc:coordinatePrecision to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Provides("8c5fe9c9-4ba9-49ef-b15a-9ccd0424e6ae")
    public DQResponse<AmendmentValue> amendmentCountrycodeFromCoordinates(@ActedUpon("dwc:decimalLatitude") String decimalLatitude, @ActedUpon("dwc:decimalLongitude") String decimalLongitude, @ActedUpon("dwc:geodeticDatum") String geodeticDatum, @ActedUpon("dwc:countryCode") String countryCode, @ActedUpon("dwc:coordinatePrecision") String coordinatePrecision) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // was not available; INTERNAL_PREREQUISITES_NOT_MET if either 
        // dwc:decimalLatitude or dwc:decimalLongitude is EMPTY or 
        // uninterpretable, or if dwc:countryCode is NOT_EMPTY; AMENDED 
        // if dwc:decimalLatitude and dwc:decimalLongitude fall within 
        // a boundary from the bdq:sourceAuthority that is attributable 
        //to a single valid country code; otherwise NOT_CHANGED. 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority; bdq:spatialBufferInMeters

        return result;
    }

    /**
     * #78 Validation SingleRecord Completeness: geodeticdatum empty
     *
     * Provides: VALIDATION_GEODETICDATUM_EMPTY
     *
     * @param geodeticDatum the provided dwc:geodeticDatum to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("239ec40e-a729-4a8e-ba69-e0bf03ac1c44")
    public static DQResponse<ComplianceValue> validationGeodeticdatumEmpty(@ActedUpon("dwc:geodeticDatum") String geodeticDatum) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // COMPLIANT if dwc:geodeticDatum is not EMPTY; otherwise NOT_COMPLIANT 
        //
        
        result.setResultState(ResultState.RUN_HAS_RESULT);
        if (GEOUtil.isEmpty(geodeticDatum))
		{ 
			result.setValue(ComplianceValue.NOT_COMPLIANT);
			result.addComment("The value provided for dwc:geodeticDatum is empty");
		} else { 
			result.setValue(ComplianceValue.COMPLIANT);
			result.addComment("dwc:geodeticDatum contains a value.");
		}

        return result;
    }

    /**
     * #79 Validation SingleRecord Conformance: decimallatitude outofrange
     *
     * Provides: VALIDATION_DECIMALLATITUDE_OUTOFRANGE
     *
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("b6ecda2a-ce36-437a-b515-3ae94948fe83")
    public static DQResponse<ComplianceValue> validationDecimallatitudeOutofrange(
    		@ActedUpon("dwc:decimalLatitude") String decimalLatitude) {
        
    	DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:decimalLatitude is 
        // EMPTY or the value is not a number; COMPLIANT if the value 
        // of dwc:decimalLatitude is between -90 and 90 degrees, inclusive; 
        // otherwise NOT_COMPLIANT 

    	if (GEOUtil.isEmpty(decimalLatitude)) { 
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("provided value for dwc:decimalLatitude is empty.");
    	} else if (!GEOUtil.isNumericCharacters(decimalLatitude)) { 
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("provided value for dwc:decimalLatitude contains non-numeric characters.");
    	} else { 
    		try {
    			Double longitudeNumber = Double.parseDouble(decimalLatitude);
    			if (longitudeNumber <= 90d && longitudeNumber >= -90d) { 
    				result.setResultState(ResultState.RUN_HAS_RESULT);
    				result.setValue(ComplianceValue.COMPLIANT);
    				result.addComment("the provided value for dwc:decimalLatitude is a number between -90 and 90 inclusive.");
    			} else { 
    				result.setResultState(ResultState.RUN_HAS_RESULT);
    				result.setValue(ComplianceValue.NOT_COMPLIANT);
    				result.addComment("the provided value for dwc:decimalLatitude is a number outside the range -90 to 90.");
    			}
    		} catch (NumberFormatException e) { 
    			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    			result.addComment("provided value for dwc:decimalLatitude cannot be parsed as a number.");

    		}
    	}
        
        return result;
    }

    /**
     * #87 Validation SingleRecord Likelihood: coordinates zero
     *
     * Provides: VALIDATION_COORDINATES_ZERO
     *
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("1bf0e210-6792-4128-b8cc-ab6828aa4871")
    public DQResponse<ComplianceValue> validationCoordinatesZero(@ActedUpon("dwc:decimalLatitude") String decimalLatitude, @ActedUpon("dwc:decimalLongitude") String decimalLongitude) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:decimalLatitude and/or 
        // dwc:decimalLongitude are EMPTY or both of the values are 
        // not interpretable as numbers; COMPLIANT if either the value 
        // of dwc:decimalLatitude is not = 0 or the value of dwc:decimalLongitude 
        //is not = 0; otherwise NOT_COMPLIANT 
        
    	if (GEOUtil.isEmpty(decimalLatitude)) { 
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("provided value for dwc:decimalLatitude is empty.");
    		if (GEOUtil.isEmpty(decimalLongitude)) { 
    			result.addComment("provided value for dwc:decimalLongitude is empty.");
    		}
    	} else if (GEOUtil.isEmpty(decimalLongitude)) { 
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("provided value for dwc:decimalLongitude is empty.");
    	} else if (!GEOUtil.isNumericCharacters(decimalLatitude)) { 
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("provided value for dwc:decimalLatitude contains non-numeric characters.");
    	} else { 
    		try {
    			Double decimalLatitudeNumber = Double.parseDouble(decimalLatitude);
    			try {
    				Double decimalLongitudeNumber = Double.parseDouble(decimalLongitude);
    			} catch (NumberFormatException e) { 
    				result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    				result.addComment("provided value for dwc:decimalLongitude is not parsable as a number.");
    			}
    		} catch (NumberFormatException e) { 
    			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    			result.addComment("provided value for dwc:decimalLatitude is not parsable as a number.");
    		}
    	}

        return result;
    }

    /**
     * #95 Validation SingleRecord Conformance: geography ambiguous
     *
     * Provides: VALIDATION_GEOGRAPHY_AMBIGUOUS
     *
     * @param continent the provided dwc:continent to evaluate
     * @param county the provided dwc:county to evaluate
     * @param country the provided dwc:country to evaluate
     * @param countryCode the provided dwc:countryCode to evaluate
     * @param municipality the provided dwc:municipality to evaluate
     * @param stateProvince the provided dwc:stateProvince to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("78640f09-8353-411a-800e-9b6d498fb1c9")
    public DQResponse<ComplianceValue> validationGeographyAmbiguous(@ActedUpon("dwc:continent") String continent, @ActedUpon("dwc:county") String county, @ActedUpon("dwc:country") String country, @ActedUpon("dwc:countryCode") String countryCode, @ActedUpon("dwc:municipality") String municipality, @ActedUpon("dwc:stateProvince") String stateProvince) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // service was not available; INTERNAL_PREREQUISITES_NOT_MET 
        // if all of the terms dwc:continent, dwc:country, dwc:countryCode, 
        // dwc:stateProvince, dwc:county, dwc:municipality are EMPTY; 
        // COMPLIANT if the combination of values of administrative 
        // geographic terms (dwc:continent, dwc:country, dwc:countryCode, 
        // dwc:stateProvince, dwc:county, dwc:municipality) can be 
        // unambiguously resolved by the bdq:sourceAuthority service; 
        //otherwise NOT_COMPLIANT 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority

        return result;
    }

    /**
     * #96 Validation SingleRecord Completeness: decimallongitude empty
     *
     * Provides: VALIDATION_DECIMALLONGITUDE_EMPTY
     *
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("9beb9442-d942-4f42-8b6a-fcea01ee086a")
    public static DQResponse<ComplianceValue> validationDecimallongitudeEmpty(
    		@ActedUpon("dwc:decimalLongitude") String decimalLongitude) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // COMPLIANT if dwc:decimalLongitude is not EMPTY; otherwise 
        // NOT_COMPLIANT 
        
        result.setResultState(ResultState.RUN_HAS_RESULT);
        if (GEOUtil.isEmpty(decimalLongitude))
		{ 
			result.setValue(ComplianceValue.NOT_COMPLIANT);
			result.addComment("The value provided for dwc:decimalLongitude is empty");
		} else { 
			result.setValue(ComplianceValue.COMPLIANT);
			result.addComment("dwc:decimalLongitude contains a value.");
		}

        return result;
    }

    /**
     * #98 Validation SingleRecord Completeness: countrycode empty
     *
     * Provides: VALIDATION_COUNTRYCODE_EMPTY
     *
     * @param countryCode the provided dwc:countryCode to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("853b79a2-b314-44a2-ae46-34a1e7ed85e4")
    public static DQResponse<ComplianceValue> validationCountrycodeEmpty(
    		@ActedUpon("dwc:countryCode") String countryCode) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // COMPLIANT if dwc:countryCode is not EMPTY; otherwise NOT_COMPLIANT 
        //
        
        result.setResultState(ResultState.RUN_HAS_RESULT);
        if (GEOUtil.isEmpty(countryCode))
		{ 
			result.setValue(ComplianceValue.NOT_COMPLIANT);
			result.addComment("The value provided for dwc:countryCode is empty");
		} else { 
			result.setValue(ComplianceValue.COMPLIANT);
			result.addComment("dwc:countryCode contains a value.");
		}

        return result;
    }

    /**
     * #102 Amendment SingleRecord Completeness: geodeticdatum assumeddefault
     *
     * Provides: AMENDMENT_GEODETICDATUM_ASSUMEDDEFAULT
     *
     * @param coordinateUncertantyInMeters the provided dwc:coordinateUncertantyInMeters to evaluate
     * @param geodeticDatum the provided dwc:geodeticDatum to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Provides("7498ca76-c4d4-42e2-8103-acacccbdffa7")
    public DQResponse<AmendmentValue> amendmentGeodeticdatumAssumeddefault(@ActedUpon("dwc:coordinateUncertantyInMeters") String coordinateUncertantyInMeters, @ActedUpon("dwc:geodeticDatum") String geodeticDatum) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if the value of dwc:geodeticDatum 
        // was interpretable or the Parameter is not set; AMENDED to 
        // the Parameter value if dwc:geodeticDatum was EMPTY; otherwise 
        //NOT_CHANGED 

        //TODO: Parameters. This test is defined as parameterized.
        // dwc:geodeticDatum default

        return result;
    }

    /**
     * #107 Validation SingleRecord Conformance: mindepth outofrange
     *
     * Provides: VALIDATION_MINDEPTH_OUTOFRANGE
     *
     * @param minimumDepthInMeters the provided dwc:minimumDepthInMeters to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    public static DQResponse<ComplianceValue> validationMindepthOutofrange(
    		@ActedUpon("dwc:minimumDepthInMeters") String minimumDepthInMeters) { 
    	return DwCGeoRefDQ.validationMindepthOutofrange(minimumDepthInMeters, 0d, 11000d);
    }
    @Provides("04b2c8f3-c71b-4e95-8e43-f70374c5fb92")
    public static DQResponse<ComplianceValue> validationMindepthOutofrange(
    		@ActedUpon("dwc:minimumDepthInMeters") String minimumDepthInMeters,
    		@Parameter(name="bdq:minimumValidDepthInMeters") Double minimumValidDepthInMeters,
    		@Parameter(name="bdq:maximumValidDepthInMeters") Double maximumValidDepthInMeters
    		) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:minimumDepthInMeters 
        // is EMPTY, or the value is not zero or a positive number; 
        // COMPLIANT if the value of dwc:minimumDepthInMeters is within 
        //the Parameter range; otherwise NOT_COMPLIANT 

        // Parameters. This test is defined as parameterized.
        // Default values: bdq:minimumValidDepthInMeters="0" ; bdq:maximumValidDepthInMeters="11000"
        
        if (minimumValidDepthInMeters==null) { 
        	minimumValidDepthInMeters = 0d;
        }
        if (maximumValidDepthInMeters==null) { 
        	maximumValidDepthInMeters = 11000d;
        }
        String rangeString = minimumValidDepthInMeters.toString() + " to " + maximumValidDepthInMeters.toString();
        
        if (GEOUtil.isEmpty(minimumDepthInMeters)) { 
        	result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        	result.addComment("the provided value for dwc:minimumDepthInMeters is empty");
    	} else if (!GEOUtil.isNumericCharacters(minimumDepthInMeters)) { 
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("provided value for dwc:minimumDepthInMeters contains non-numeric characters.");
        } else { 
        	try { 
        		Double depthVal = Double.parseDouble(minimumDepthInMeters);
        		if (depthVal < minimumValidDepthInMeters) { 
        			result.setResultState(ResultState.RUN_HAS_RESULT);
        			result.setValue(ComplianceValue.NOT_COMPLIANT);
        			result.addComment("The value provided for dwc:minimumDepthInMeters is outside (below the minimum of) the range " + rangeString);
        		} else if (depthVal > maximumValidDepthInMeters ) { 
        			result.setResultState(ResultState.RUN_HAS_RESULT);
        			result.setValue(ComplianceValue.NOT_COMPLIANT);
        			result.addComment("The value provided for dwc:minimumDepthInMeters is outside (above the maximum of) the range " + rangeString);
        		} else  {
        			result.setResultState(ResultState.RUN_HAS_RESULT);
        			result.setValue(ComplianceValue.COMPLIANT);
        			result.addComment("The value provided for dwc:minimumDepthInMeters is within the range " + rangeString);
        		}
        	} catch (NumberFormatException e) { 
        		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        		result.addComment("the provided value for dwc:minimumDepthInMeters is not a number");
        	}
		}
        
        return result;
    }

    /**
     * #108 Validation SingleRecord Conformance: minelevation greaterthan maxelevation
     *
     * Provides: VALIDATION_MINELEVATION_GREATERTHAN_MAXELEVATION
     *
     * @param minimumElevationInMeters the provided dwc:minimumElevationInMeters to evaluate
     * @param maximumElevationInMeters the provided dwc:maximumElevationInMeters to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("d708526b-6561-438e-aa1a-82cd80b06396")
    public static DQResponse<ComplianceValue> validationMinelevationGreaterthanMaxelevation(
    		@ActedUpon("dwc:minimumElevationInMeters") String minimumElevationInMeters, 
    		@ActedUpon("dwc:maximumElevationInMeters") String maximumElevationInMeters) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();
        
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:maximumlevationInMeters 
        // or dwc:minimumElevationInMeters is EMPTY; COMPLIANT if the 
        // value of dwc:minimumElevationInMeters is a number less than 
        // or equal to the value of the number dwc:maximumElevationInMeters, 
        // otherwise NOT_COMPLIANT
        
        // TODO: Implementation follows change proposed in issue as of 2022Feb19, internal prerequsites not met if 
        // either of the provided values is not a number rather than not compliant, consistent with other
        // elevation/depth validations.
        
        if (GEOUtil.isEmpty(maximumElevationInMeters)) { 
        	result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        	result.addComment("the provided value for dwc:maximumElevationInMeters is empty");
        	if (GEOUtil.isEmpty(minimumElevationInMeters)) {
        		result.addComment("the provided value for dwc:minimumElevationInMeters is empty");
        	}
        } else if (GEOUtil.isEmpty(minimumElevationInMeters)) { 
        	result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        	result.addComment("the provided value for dwc:maximumElevationInMeters is empty");
    	} else if (!GEOUtil.isNumericCharacters(minimumElevationInMeters)) { 
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("provided value for dwc:minimumElevationInMeters contains non-numeric characters.");
        } else { 
        	try { 
        		Double maxDepthVal = Double.parseDouble(maximumElevationInMeters);
        		try { 
        			Double minDepthVal = Double.parseDouble(minimumElevationInMeters);
           			result.setResultState(ResultState.RUN_HAS_RESULT);
           			if (minDepthVal <= maxDepthVal) {
           				result.setValue(ComplianceValue.COMPLIANT);
           				result.addComment("The value provided for dwc:minimumElevationInMeters is less than or equal to that provided for dwc:maximumElevationInMeters");
           			} else {  
           				result.setValue(ComplianceValue.NOT_COMPLIANT);
           				result.addComment("The value provided for dwc:minimumElevationInMeters is greater than that provided for dwc:maximumElevationInMeters");
           			}
        		} catch (NumberFormatException e) { 
        			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        			result.addComment("the provided value for dwc:minimumElevationInMeters is not a number");
        		}

        	} catch (NumberFormatException e) { 
        		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        		result.addComment("the provided value for dwc:maximumElevationInMeters is not a number");
        	}
		}
        

        return result;
    }

    /**
     * #109 Validation SingleRecord Conformance: coordinateuncertainty outofrange
     *
     * Provides: VALIDATION_COORDINATEUNCERTAINTY_OUTOFRANGE
     *
     * @param coordinateUncertaintyInMeters the provided dwc:coordinateUncertaintyInMeters to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("c6adf2ea-3051-4498-97f4-4b2f8a105f57")
    public static DQResponse<ComplianceValue> validationCoordinateuncertaintyOutofrange(
    		@ActedUpon("dwc:coordinateUncertaintyInMeters") String coordinateUncertaintyInMeters) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:coordinateUncertaintyInMeters 
        // is EMPTY; COMPLIANT if the value of dwc:coordinateUncertaintyInMeters 
        // is number between 1 and 20037509 inclusive; otherwise NOT_COMPLIANT 
        //

    	if (GEOUtil.isEmpty(coordinateUncertaintyInMeters)) { 
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("provided value for dwc:coordinateUncertaintyInMeters is empty.");
    	} else if (!GEOUtil.isNumericCharacters(coordinateUncertaintyInMeters)) { 
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("provided value for dwc:coordinateUncertaintyInMeters contains non-numeric characters.");
    	} else { 
    		try {
    			Double uncertaintyNumber = Double.parseDouble(coordinateUncertaintyInMeters);
    			if (uncertaintyNumber >= 1d && uncertaintyNumber <= 20037509d) { 
    				result.setResultState(ResultState.RUN_HAS_RESULT);
    				result.setValue(ComplianceValue.COMPLIANT);
    				result.addComment("the provided value for dwc:coordinateUncertaintyInMeters is a number between 1 and 20037509 inclusive.");
    				if (coordinateUncertaintyInMeters.equals(301d)) { 
    					result.addComment("the provided value for dwc:coordinateUncertaintyInMeters of 301 may be suspect, the value of 301 was used by BioGeomancer to mean unable to determine uncertainty.");
    				}
    			} else if (uncertaintyNumber.equals(0d)) { 
    				result.setResultState(ResultState.RUN_HAS_RESULT);
    				result.setValue(ComplianceValue.NOT_COMPLIANT);
    				result.addComment("the provided value for dwc:coordinateUncertaintyInMeters is zero, but it must be the range 1 to 20037509.");
    			} else { 
    				result.setResultState(ResultState.RUN_HAS_RESULT);
    				result.setValue(ComplianceValue.NOT_COMPLIANT);
    				result.addComment("the provided value for dwc:coordinateUncertaintyInMeters is a number outside the range 1 to 20037509.");
    			}
    		} catch (NumberFormatException e) { 
    			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    			result.addComment("provided value for dwc:coordinateUncertaintyInMeters cannot be parsed as a number.");

    		}
    	}
        
        return result;
    }

    public static DQResponse<ComplianceValue> validationMaxelevationOutofrange(
    		@ActedUpon("dwc:maximumElevationInMeters") String maximumElevationInMeters) {
    	return DwCGeoRefDQ.validationMaxelevationOutofrange(maximumElevationInMeters,-430d, 8850d);
    }
    
    /**
     * #112 Validation SingleRecord Invalid: maxelevation outofrange
     *
     * Provides: VALIDATION_MAXELEVATION_OUTOFRANGE
     *
     * @param maximumElevationInMeters the provided dwc:maximumElevationInMeters to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("c971fe3f-84c1-4636-9f44-b1ec31fd63c7")
    public static DQResponse<ComplianceValue> validationMaxelevationOutofrange(
    		@ActedUpon("dwc:maximumElevationInMeters") String maximumElevationInMeters,
    		@Parameter(name="bdq:minimumValidElevationInMeters") Double minimumValidElevationInMeters,
    		@Parameter(name="bdq:maximumValidElevationInMeters") Double maximumValidElevationInMeters
    		) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:maximumElevationInMeters 
        // is EMPTY or the value is not a number; COMPLIANT if the 
        // value of dwc:maximumElevationInMeters is within the Parameter 
        // range; otherwise NOT_COMPLIANT 

        // Parameters. This test is defined as parameterized.
        // Default values: bdq:minimumValidElevationInMeters="-430"; bdq:maximumValidElevationInMeters="8850"

        if (minimumValidElevationInMeters==null) { 
        	minimumValidElevationInMeters = -430d;
        }
        if (maximumValidElevationInMeters==null) { 
        	maximumValidElevationInMeters = 8850d;
        }
        String range = Double.toString(minimumValidElevationInMeters) + " to " + Double.toString(maximumValidElevationInMeters);
        
    	if (GEOUtil.isEmpty(maximumElevationInMeters)) { 
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("provided value for dwc:maximumElevationInMeters is empty.");
    	} else if (!GEOUtil.isNumericCharacters(maximumElevationInMeters)) { 
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("provided value for dwc:maximumElevationInMeters contains non-numeric characters.");
    	} else { 
    		try {
    			Double longitudeNumber = Double.parseDouble(maximumElevationInMeters);
    			if (longitudeNumber <= maximumValidElevationInMeters && longitudeNumber >= minimumValidElevationInMeters) { 
    				result.setResultState(ResultState.RUN_HAS_RESULT);
    				result.setValue(ComplianceValue.COMPLIANT);
    				result.addComment("the provided value for dwc:maximumElevationInMeters is a number between " + range + "  inclusive.");
    			} else { 
    				result.setResultState(ResultState.RUN_HAS_RESULT);
    				result.setValue(ComplianceValue.NOT_COMPLIANT);
    				result.addComment("the provided value for dwc:maximumElevationInMeters is a number outside the range " + range + ".");
    			}
    		} catch (NumberFormatException e) { 
    			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    			result.addComment("provided value for dwc:maximumElevationInMeters cannot be parsed as a number.");
    		}
    	}
        
        
        return result;
    }

    /**
     * #118 Amendment SingleRecord Conformance: geography standardized
     *
     * Provides: AMENDMENT_GEOGRAPHY_STANDARDIZED
     *
     * @param continent the provided dwc:continent to evaluate
     * @param county the provided dwc:county to evaluate
     * @param country the provided dwc:country to evaluate
     * @param countryCode the provided dwc:countryCode to evaluate
     * @param municipality the provided dwc:municipality to evaluate
     * @param stateProvince the provided dwc:stateProvince to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Provides("78640f09-8353-411a-800e-9b6d498fb1c9")
    public DQResponse<AmendmentValue> amendmentGeographyStandardized(
    		@ActedUpon("dwc:continent") String continent, 
    		@ActedUpon("dwc:county") String county, 
    		@ActedUpon("dwc:country") String country, 
    		@ActedUpon("dwc:countryCode") String countryCode, 
    		@ActedUpon("dwc:municipality") String municipality, 
    		@ActedUpon("dwc:stateProvince") String stateProvince) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // service was not available or if the combination of administrative 
        // geography terms could not be unambiguously resolved from 
        // the bdq:sourceAuthority service; AMENDED if one or more 
        // of the administrative geographic terms (dwc:continent, dwc:country, 
        // dwc:countryCode, dwc:stateProvince, dwc:county, dwc:municipality) 
        // was changed to comply with standard values from the bdq:sourceAuthority 
        //service; otherwise NOT_CHANGED 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority

        return result;
    }

    /**
     * #119 Validation SingleRecord Completeness: decimallatitude empty
     *
     * Provides: VALIDATION_DECIMALLATITUDE_EMPTY
     *
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("7d2485d5-1ba7-4f25-90cb-f4480ff1a275")
    public static DQResponse<ComplianceValue> validationDecimallatitudeEmpty(
    		@ActedUpon("dwc:decimalLatitude") String decimalLatitude) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // COMPLIANT if dwc:decimalLatitude is not EMPTY; otherwise 
        // NOT_COMPLIANT 

        result.setResultState(ResultState.RUN_HAS_RESULT);
        if (GEOUtil.isEmpty(decimalLatitude))
		{ 
			result.setValue(ComplianceValue.NOT_COMPLIANT);
			result.addComment("The value provided for dwc:decimalLatitude is empty");
		} else { 
			result.setValue(ComplianceValue.COMPLIANT);
			result.addComment("dwc:decimalLatitude contains a value.");
		}
        
        return result;
    }

    /**
     * #139 Validation SingleRecord Conformance: geography notstandard
     *
     * Provides: VALIDATION_GEOGRAPHY_NOTSTANDARD
     *
     * @param continent the provided dwc:continent to evaluate
     * @param county the provided dwc:county to evaluate
     * @param country the provided dwc:country to evaluate
     * @param countryCode the provided dwc:countryCode to evaluate
     * @param municipality the provided dwc:municipality to evaluate
     * @param stateProvince the provided dwc:stateProvince to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("9d6f53c0-775b-4579-b7a4-5e5f093aa512")
    public DQResponse<ComplianceValue> validationGeographyNotstandard(
    		@ActedUpon("dwc:continent") String continent, 
    		@ActedUpon("dwc:country") String country, 
    		@ActedUpon("dwc:countryCode") String countryCode, 
    		@ActedUpon("dwc:stateProvince") String stateProvince,
    		@ActedUpon("dwc:county") String county, 
    		@ActedUpon("dwc:municipality") String municipality
    		) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // service was not available; INTERNAL_PREREQUISITES_NOT_MET 
        // if all of the terms dwc:continent, dwc:country, dwc:countryCode, 
        // dwc:stateProvince, dwc:county, dwc:municipality are EMPTY; 
        // COMPLIANT if the combination of dwc:continent, dwc:country, 
        // dwc:countryCode, dwc:stateProvince, dwc:county, dwc:municipality 
        // can be unambiguously resolved from the bdq:sourceAuthority 
        //service; otherwise NOT_COMPLIANT 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority

        return result;
    }

    /**
     * #187 Validation SingleRecord Conformance: maxdepth outofrange
     *
     * Provides: VALIDATION_MAXDEPTH_OUTOFRANGE
     *
     * @param maximumDepthInMeters the provided dwc:maximumDepthInMeters to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    public static DQResponse<ComplianceValue> validationMaxdepthOutofrange(
    		@ActedUpon("dwc:maximumDepthInMeters") String maximumDepthInMeters) { 
    	return (DwCGeoRefDQ.validationMaxdepthOutofrange(maximumDepthInMeters, 0d, 11000d));
    }
    @Provides("3f1db29a-bfa5-40db-9fd1-fde020d81939")
    public static DQResponse<ComplianceValue> validationMaxdepthOutofrange(
    		@ActedUpon("dwc:maximumDepthInMeters") String maximumDepthInMeters, 
    		@Parameter(name="bdq:minimumValidDepthInMeters") Double minimumValidDepthInMeters,
    		@Parameter(name="bdq:maximumValidDepthInMeters") Double maximumValidDepthInMeters
    		) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:maximumDepthInMeters 
        // is EMPTY or is not a number; COMPLIANT if the value of dwc:maximumDepthInMeters 
        // is within the Parameter range of bdq:minimumValidDepthInMeters 
        // to bdq:maximumValidDepthInMeters inclusive; otherwise NOT_COMPLIANT 
        //

        // Parameters. This test is defined as parameterized.
        // Default values: bdq:minimumValidDepthInMeters="0"; bdq:maximumValidDepthInMeters="11000"

        if (minimumValidDepthInMeters==null) { 
        	minimumValidDepthInMeters = 0d;
        }
        if (maximumValidDepthInMeters==null) { 
        	maximumValidDepthInMeters = 11000d;
        }
        String rangeString = minimumValidDepthInMeters.toString() + " to " + maximumValidDepthInMeters.toString();
        
        if (GEOUtil.isEmpty(maximumDepthInMeters)) { 
        	result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        	result.addComment("the provided value for dwc:maximumDepthInMeters is empty");
    	} else if (!GEOUtil.isNumericCharacters(maximumDepthInMeters)) { 
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("provided value for dwc:maximumDepthInMeters contains non-numeric characters.");
        } else { 
        	try { 
        		Double depthVal = Double.parseDouble(maximumDepthInMeters);
        		if (depthVal < minimumValidDepthInMeters) { 
        			result.setResultState(ResultState.RUN_HAS_RESULT);
        			result.setValue(ComplianceValue.NOT_COMPLIANT);
        			result.addComment("The value provided for dwc:maximumDepthInMeters is outside (below the minimum of) the range " + rangeString);
        		} else if (depthVal > maximumValidDepthInMeters ) { 
        			result.setResultState(ResultState.RUN_HAS_RESULT);
        			result.setValue(ComplianceValue.NOT_COMPLIANT);
        			result.addComment("The value provided for dwc:maximumDepthInMeters is outside (above the maximum of) the range " + rangeString);
        		} else  {
        			result.setResultState(ResultState.RUN_HAS_RESULT);
        			result.setValue(ComplianceValue.COMPLIANT);
        			result.addComment("The value provided for dwc:maximumDepthInMeters is within the range " + rangeString);
        		}
        	} catch (NumberFormatException e) { 
        		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        		result.addComment("the provided value for dwc:maximumDepthInMeters is not a number");
        	}
		}
        
        return result;
    }

}
