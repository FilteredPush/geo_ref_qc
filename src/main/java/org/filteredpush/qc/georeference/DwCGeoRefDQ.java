/* NOTE: requires the ffdq-api dependecy in the maven pom.xml */

package org.filteredpush.qc.georeference;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datakurator.ffdq.annotations.*;
import org.datakurator.ffdq.api.DQResponse;
import org.datakurator.ffdq.model.ResultState;
import org.filteredpush.qc.georeference.util.GEOUtil;
import org.filteredpush.qc.georeference.util.TransformationStruct;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.datakurator.ffdq.api.result.*;

/**
 *
 * Implementations of TDWG BDQ SPACE related tests.
 * 
 * Provides:
 * 
 * #20 	VALIDATION_COUNTRYCODE_STANDARD 0493bcfb-652e-4d17-815b-b0cce0742fbe
 * #187 VALIDATION_MAXDEPTH_INRANGE 3f1db29a-bfa5-40db-9fd1-fde020d81939
 * #24	VALIDATION_MINDEPTH_LESSTHAN_MAXDEPTH 8f1e6e58-544b-4365-a569-fb781341644e
 * #42 	VALIDATION_COUNTRY_NOTEMPTY 6ce2b2b4-6afe-4d13-82a0-390d31ade01c 
 * #98	VALIDATION_COUNTRYCODE_NOTEMPTY 853b79a2-b314-44a2-ae46-34a1e7ed85e4 
 * #119	VALIDATION_DECIMALLATITUDE_EMPTY 7d2485d5-1ba7-4f25-90cb-f4480ff1a275
 * #79	VALIDATION_DECIMALLATITUDE_INRANGE b6ecda2a-ce36-437a-b515-3ae94948fe83
 * #96	VALIDATION_DECIMALLONGITUDE_EMPTY 9beb9442-d942-4f42-8b6a-fcea01ee086a
 * #30	VALIDATION_DECIMALLONGITUDE_INRANGE 0949110d-c06b-450e-9649-7c1374d940d1
 * #87	VALIDATION_COORDINATES_NOTZERO 1bf0e210-6792-4128-b8cc-ab6828aa4871
 * #107	VALIDATION_MINDEPTH_INRANGE 04b2c8f3-c71b-4e95-8e43-f70374c5fb92
 * #112	VALIDATION_MAXELEVATION_INRANGE c971fe3f-84c1-4636-9f44-b1ec31fd63c7
 * #39	VALIDATION_MINELEVATION_INRANGE 0bb8297d-8f8a-42d2-80c1-558f29efe798
 * #108	VALIDATION_MINELEVATION_LESSTHAN_MAXELEVATION d708526b-6561-438e-aa1a-82cd80b06396
 * #109	VALIDATION_COORDINATEUNCERTAINTY_INRANGE c6adf2ea-3051-4498-97f4-4b2f8a105f57
 * #78	VALIDATION_GEODETICDATUM_NOTEMPTY 239ec40e-a729-4a8e-ba69-e0bf03ac1c44
 * #59	VALIDATION_GEODETICDATUM_STANDARD 7e0c0418-fe16-4a39-98bd-80e19d95b9d1
 * #40	VALIDATION_LOCATION_NOTEMPTY 58486cb6-1114-4a8a-ba1e-bd89cfe887e9
 * 
 * 
 * #102 AMENDMENT_GEODETICDATUM_ASSUMEDDEFAULT 7498ca76-c4d4-42e2-8103-acacccbdffa7
 * 
 * For #72, see rec_occur_qc DwCMetadataDQ
 * #72 ISSUE_DATAGENERALIZATIONS_NOTEMPTY 13d5a10e-188e-40fd-a22c-dbaa87b91df2
 * 
 * @author mole
 *
 */
@Mechanism(value="71fa3762-0dfa-43c7-a113-d59797af02e8",label="Kurator: Date Validator - DwCGeoRefDQ:v2.0.0")
public class DwCGeoRefDQ{
	
	private static final Log logger = LogFactory.getLog(DwCGeoRefDQ.class);
    
    /**
     * Is the value of dwc:countryCode a valid ISO 3166-1-alpha-2 country code?
     *
     * Provides: #20 VALIDATION_COUNTRYCODE_STANDARD
     * Version: 2022-05-02
     *
     * @param countryCode the provided dwc:countryCode to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_COUNTRYCODE_STANDARD", description="Is the value of dwc:countryCode a valid ISO 3166-1-alpha-2 country code?")
    @Provides("0493bcfb-652e-4d17-815b-b0cce0742fbe")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/0493bcfb-652e-4d17-815b-b0cce0742fbe/2022-05-02")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:SourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if the dwc:countryCode was EMPTY; COMPLIANT if the value of dwc:countryCode is found in bdq:sourceAuthority; otherwise NOT_COMPLIANT bdq:sourceAuthority is 'ISO 3166-1-alpha-2' [https://restcountries.eu/#api-endpoints-list-of-codes, https://www.iso.org/obp/ui/#search]")
    public static DQResponse<ComplianceValue> validationCountrycodeStandard(
    		@ActedUpon("dwc:countryCode") String countryCode) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:SourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if the 
        // dwc:countryCode was EMPTY; COMPLIANT if the value of dwc:countryCode 
        // is found in bdq:sourceAuthority; otherwise NOT_COMPLIANT 
        
        // bdq:sourceAuthority is "ISO 3166-1-alpha-2" [https://restcountries.eu/#api-endpoints-list-of-codes, 
        // https://www.iso.org/obp/ui/#search] 
        // https://restcountries.eu/#api-endpoints-list-of-codes is currently timing out.
        // https://www.iso.org/obp/ui/#search appears to be an api for one item at once.
        
        
        if (GEOUtil.isEmpty(countryCode)) { 
        	result.addComment("dwc:countryCode is empty");
        	result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        } else if (countryCode.length() != 2) { 
        	// From notes: "This test will fail if there is leading or trailing whitespace or there are leading or trailing non-printing characters."
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
     * Is the value of dwc:minimumDepthInMeters a number that is less than or equal to the value of dwc:maximumDepthInMeters?
     * 
     * #24 Validation SingleRecord Conformance: mindepth greaterthan maxdepth
     *
     * Provides: VALIDATION_MINDEPTH_LESSTHAN_MAXDEPTH
     * Version: 2022-03-22
     *
     * @param maximumDepthInMeters the provided dwc:maximumDepthInMeters to evaluate
     * @param minimumDepthInMeters the provided dwc:minimumDepthInMeters to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_MINDEPTH_LESSTHAN_MAXDEPTH", description="Is the value of dwc:minimumDepthInMeters a number that is less than or equal to the value of dwc:maximumDepthInMeters?")
    @Provides("8f1e6e58-544b-4365-a569-fb781341644e")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/8f1e6e58-544b-4365-a569-fb781341644e/2022-03-22")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if dwc:minimumDepthInMeters or dwc:maximumDepthInMeters is EMPTY, or if either are interpretable as not zero or a positive number; COMPLIANT if the value of dwc:minimumDepthInMeters is less than or equal to the value of dwc:maximumDepthInMeters; otherwise NOT_COMPLIANT ")
    public static DQResponse<ComplianceValue> validationMindepthLessthanMaxdepth(
    		@ActedUpon("dwc:minimumDepthInMeters") String minimumDepthInMeters,
    		@ActedUpon("dwc:maximumDepthInMeters") String maximumDepthInMeters) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:minimumDepthInMeters 
        // or dwc:maximumDepthInMeters is EMPTY, or if either are interpretable 
        // as not zero or a positive number; COMPLIANT if the value 
        // of dwc:minimumDepthInMeters is less than or equal to the 
        // value of dwc:maximumDepthInMeters; otherwise NOT_COMPLIANT 

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
     * Is the value of dwc:decimalLongitude a number between -180 and 180 inclusive?
     *
     * #30 Validation SingleRecord Conformance: decimallongitude outofrange
     *
     * Provides: VALIDATION_DECIMALLONGITUDE_INRANGE
     * Version: 2022-03-22
     *
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_DECIMALLONGITUDE_INRANGE", description="Is the value of dwc:decimalLongitude a number between -180 and 180 inclusive?")
    @Provides("0949110d-c06b-450e-9649-7c1374d940d1")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/0949110d-c06b-450e-9649-7c1374d940d1/2022-03-22")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if dwc:decimalLongitude is EMPTY or the value is not a number; COMPLIANT if the value of dwc:decimalLongitude is between -180 and 180 degrees, inclusive; otherwise NOT_COMPLIANT ")
    public static DQResponse<ComplianceValue> validationDecimallongitudeInrange(
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
        //otherwise NOT_AMENDED 

        return result;
    }

    
    /**
     * Is the value of dwc:minimumElevationInMeters within the Parameter range?
     *
     * Provides: #39 VALIDATION_MINELEVATION_INRANGE
     *
     * @param minimumElevationInMeters the provided dwc:minimumElevationInMeters to evaluate
     * @param minimumValidElevationInMeters minimum valid value to test against, if null, defaults to -430
     * @param maximumValidElevationInMeters maximum valid value to test against, if null, defaults to 8550
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_MINELEVATION_INRANGE", description="Is the value of dwc:minimumElevationInMeters within the Parameter range?")
    @Provides("0bb8297d-8f8a-42d2-80c1-558f29efe798")
    public static DQResponse<ComplianceValue> validationMinelevationInrange(
    		@ActedUpon("dwc:minimumElevationInMeters") String minimumElevationInMeters,
    		@Parameter(name="bdq:minimumValidElevationInMeters") Double minimumValidElevationInMeters,
    		@Parameter(name="bdq:maximumValidElevationInMeters") Double maximumValidElevationInMeters
    		) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:minimumElevationInMeters 
        // is EMPTY or the value is not a number; COMPLIANT if the 
        // value of dwc:minimumElevationInMeters is within the range 
        // of bdq:minimumValidElevationInMeters to bdq:maximumValidElevationInMeters 
        // inclusive; otherwise NOT_COMPLIANT 

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
     * Is there a value in any of the Darwin Core spatial terms that could specify a location?
     * 
     * #40 Validation SingleRecord Completeness: location empty
     *
     * Provides: VALIDATION_LOCATION_NOTEMPTY
     * Version: 2022-03-22
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
    @Validation(label="VALIDATION_LOCATION_NOTEMPTY", description="Is there a value in any of the Darwin Core spatial terms that could specify a location?")
    @Provides("58486cb6-1114-4a8a-ba1e-bd89cfe887e9")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/58486cb6-1114-4a8a-ba1e-bd89cfe887e9/2022-03-22")
    @Specification("COMPLIANT if at least one term needed to determine the location of the entity exists and is not EMPTY; otherwise NOT_COMPLIANT ")
    public static DQResponse<ComplianceValue> validationLocationNotempty(
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
     * Is there a value in dwc:country?
     *
     * Provides: #42 VALIDATION_COUNTRY_NOTEMPTY
     * Version: 2022-03-22
     *
     * @param country the provided dwc:country to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_COUNTRY_NOTEMPTY", description="Is there a value in dwc:country?")
    @Provides("6ce2b2b4-6afe-4d13-82a0-390d31ade01c")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/6ce2b2b4-6afe-4d13-82a0-390d31ade01c/2022-03-22")
    @Specification("COMPLIANT if dwc:country is not EMPTY; otherwise NOT_COMPLIANT ")
    public static DQResponse<ComplianceValue> validationCountryNotEmpty(@ActedUpon("dwc:country") String country) {
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
     * Propose amendment to the value of dwc:geodeticDatum and potentially to dwc:decimalLatitude and/or dwc:decimalLongitude based on a conversion between spatial reference systems.
     *
     * #43 Amendment SingleRecord Conformance: coordinates converted
     *
     * Provides: AMENDMENT_COORDINATES_CONVERTED
     * Version: 2023-01-16
     *
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate
     * @param coordinateUncertaintyInMeters the provided dwc:coordinateUncertaintyInMeters to evaluate
     * @param geodeticDatum the provided dwc:geodeticDatum to evaluate
     * @param coordinatePrecision the provided dwc:coordinatePrecision to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Amendment(label="AMENDMENT_COORDINATES_CONVERTED", description="Propose amendment to the value of dwc:geodeticDatum and potentially to dwc:decimalLatitude and/or dwc:decimalLongitude based on a conversion between spatial reference systems.")
    @Provides("620749b9-7d9c-4890-97d2-be3d1cde6da8")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/620749b9-7d9c-4890-97d2-be3d1cde6da8/2023-01-16")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if dwc:decimalLatitude or dwc:decimalLongitude or dwc:geodeticDatum are EMPTY or not interpretable as a value decimal number; AMENDED the values of dwc:decimalLatitude, dwc:decimalLongitude and  dwc:geodeticDatum by a conversion between spatial reference systems; otherwise NOT_AMENDED ")
    public static DQResponse<AmendmentValue> amendmentCoordinatesConverted(
    		@ActedUpon("dwc:decimalLatitude") String decimalLatitude, 
    		@ActedUpon("dwc:decimalLongitude") String decimalLongitude, 
    		@ActedUpon("dwc:coordinateUncertaintyInMeters") String coordinateUncertaintyInMeters, 
    		@ActedUpon("dwc:geodeticDatum") String geodeticDatum, 
    		@ActedUpon("dwc:coordinatePrecision") String coordinatePrecision) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:decimalLatitude or 
        // dwc:decimalLongitude or dwc:geodeticDatum are EMPTY or not 
        // interpretable as a value decimal number; AMENDED the values 
        // of dwc:decimalLatitude, dwc:decimalLongitude and dwc:geodeticDatum 
        // by a conversion between spatial reference systems; otherwise 
        // NOT_AMENDED 

        // TODO: Obtain effect of coordinate transformation on accuracy, 
        // TODO: Specification needs to include addition to coordinatePrecisionInMeters, this is in notes.
        // TODO: Notes refer to non-existent NOTIFICATION_COORDINATES_CONVERSIONFAILED
        
        // NOTES: These amendments have implications for dwc:coordinateUncertaintyInMeters and 
        // dwc:coordinatePrecision. If the dwc:coordinateUncertaintyInMeters is EMPTY or is not interpretable, 
        // this amendment should not provide a dwc:coordinateUncertaintyInMeters. 
        // If the dwc:coordinateUncertaintyInMeters is not EMPTY and is valid, this 
        // amendment should add the uncertainty contributed by the conversion to the value of 
        // dwc:CoordinateUncertaintyInMeters. The amended dwc:coordinatePrecision should be the 
        // precision of coordinates as provided after the conversion, ideally this should be 0.0000001, 
        // reflecting the seven digits of precision required to reverse a coordinate transformation without 
        // loss of information at the scale of one meter.  A result status for a failure condition in 
        // attempting a conversion is NOTIFICATION_COORDINATES_CONVERSIONFAILED
        
        String targetGeodeticDatum = "EPSG:4326";
        
        logger.debug("From: " + geodeticDatum);
        logger.debug("To: " + targetGeodeticDatum);
        
        if (GEOUtil.isEmpty(geodeticDatum)) { 
        	result.addComment("Unable to convert coordinates to EPSG:4326, no value provided for dwc:geodeticDatum.");
        	result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        } else if (GEOUtil.isEmpty(decimalLongitude) || GEOUtil.isEmpty(decimalLatitude)) { 
        	result.addComment("Unable to convert coordinates to EPSG:4326, provided coordinates are not complete, dwc:decimalLatitude=["+ decimalLatitude+"], decimalLongitude=["+decimalLongitude+"].");
        	result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET); 
        } else if (geodeticDatum.toUpperCase().equals(targetGeodeticDatum)) { 
        	result.addComment("Coordinates unchanged, dwc:geodeticDatum is already the desired " + targetGeodeticDatum);
        	result.setResultState(ResultState.NOT_AMENDED); 
        } else { 
        	if (GEOUtil.isDatumKnown(geodeticDatum)) {
        		TransformationStruct transform = new TransformationStruct();
        		try { 
        			transform = GEOUtil.datumTransform(decimalLatitude, decimalLongitude, geodeticDatum, targetGeodeticDatum);
        			if (transform.isSuccess()) { 
        				Map<String, String> values = new HashMap<>();
        				values.put("dwc:geodeticDatum", targetGeodeticDatum);
        				values.put("dwc:decimalLatitude", transform.getDecimalLatitudeString());
        				values.put("dwc:decimalLongitude", transform.getDecimalLongitudeString());
        				result.setValue(new AmendmentValue(values));
        				result.setResultState(ResultState.AMENDED); 
        } else { 
        			} 
        		} catch (FactoryException e) { 
        			result.addComment("Coordinates unchanged, unable to create a coordinate transform for specified Datum. " + e.getMessage());
        			result.setResultState(ResultState.NOT_AMENDED); 
        		} catch (TransformException e) {
        			result.addComment("Coordinates unchanged, error transforming to the specified Datum. " + e.getMessage());
        			result.setResultState(ResultState.NOT_AMENDED); 
				}
        	} else { 
        		result.addComment("Coordinates unchanged, not able to convert from (unrecognzied) provided dwc:geodeticDatum " + geodeticDatum + " to " + targetGeodeticDatum);
        		result.setResultState(ResultState.NOT_AMENDED); 
        	}
        } 
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
        //value of dwc:countryCode; otherwise NOT_AMENDED 

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
        //otherwise NOT_AMENDED 

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
        // NOT_AMENDED 

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
     * Does the value of dwc:geodeticDatum occur in bdq:sourceAuthority?
     *
     * #59 Validation SingleRecord Conformance: geodeticdatum notstandard
     *
     * Provides: VALIDATION_GEODETICDATUM_STANDARD
     * Version: 2022-03-22
     *
     * @param geodeticDatum the provided dwc:geodeticDatum to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_GEODETICDATUM_STANDARD", description="Does the value of dwc:geodeticDatum occur in bdq:sourceAuthority?")
    @Provides("7e0c0418-fe16-4a39-98bd-80e19d95b9d1")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/7e0c0418-fe16-4a39-98bd-80e19d95b9d1/2022-03-22")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available, INTERNAL_PREREQUISITES_NOT_MET if dwc:geodeticDatum is EMPTY; COMPLIANT if the value of dwc:geodeticDatum is a valid EPSG CRS Code (with or without the 'epsg' namespace prepended), or an unambiguous alphanumeric CRS or datum code; otherwise NOT_COMPLIANT bdq:sourceAuthority is 'epsg' [https://epsg.io]")
    public static DQResponse<ComplianceValue> validationGeodeticdatumStandard(@ActedUpon("dwc:geodeticDatum") String geodeticDatum) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available, INTERNAL_PREREQUISITES_NOT_MET if dwc:geodeticDatum 
        // is EMPTY; COMPLIANT if the value of dwc:geodeticDatum is 
        // a valid EPSG CRS Code (with or without the "epsg" namespace 
        // prepended), or an unambiguous alphanumeric CRS or datum 
        // code; otherwise NOT_COMPLIANT 
        // bdq:sourceAuthority is "epsg" [https://epsg.io] 
        
        if (GEOUtil.isEmpty(geodeticDatum)) { 
        	result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
			result.addComment("The value provided for dwc:geodeticDatum is empty");
		} else { 
			try { 
				boolean matched = false;
				result.setResultState(ResultState.RUN_HAS_RESULT);
				if (geodeticDatum.matches("^[0-9]+$")) { 
					// just a number, prepend EPSG: pseudo-namespace
					matched = GEOUtil.isCooridnateSystemCodeKnown("EPSG:"+geodeticDatum);
				} else { 
					matched = GEOUtil.isCooridnateSystemCodeKnown(geodeticDatum);
				} 
				if (matched) { 
					result.setValue(ComplianceValue.COMPLIANT);
					result.addComment("The value of dwc:geodeticDatum is a known EPSG code.");
				} else { 
					result.setValue(ComplianceValue.NOT_COMPLIANT);
					result.addComment("The value of dwc:geodeticDatum [" + geodeticDatum + "] is not a known EPSG code");
				} 
			} catch (FactoryException e) { 
				logger.debug(e.getClass());
				logger.debug(e.getMessage());
				// unmatched code exception is caught internally in is CoordinateSystemKnown, 
				// other failures are likely to be database problems.
				result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);
				result.addComment(e.getMessage());
			}
		}

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
        //otherwise NOT_AMENDED 

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
        //from dwc:verbatimElevation; otherwise NOT_AMENDED 

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
        //to a single valid country code; otherwise NOT_AMENDED. 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority; bdq:spatialBufferInMeters

        return result;
    }

    /**
     * Is there a value in dwc:geodeticDatum?
     * 
     * #78 Validation SingleRecord Completeness: geodeticdatum empty
     *
     * Provides: VALIDATION_GEODETICDATUM_NOTEMPTY
     * Version: 2022-03-22
     *
     * @param geodeticDatum the provided dwc:geodeticDatum to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_GEODETICDATUM_NOTEMPTY", description="Is there a value in dwc:geodeticDatum?")
    @Provides("239ec40e-a729-4a8e-ba69-e0bf03ac1c44")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/239ec40e-a729-4a8e-ba69-e0bf03ac1c44/2022-03-22")
    @Specification("COMPLIANT if dwc:geodeticDatum is not EMPTY; otherwise NOT_COMPLIANT ")
    public static DQResponse<ComplianceValue> validationGeodeticdatumNotempty(@ActedUpon("dwc:geodeticDatum") String geodeticDatum) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // COMPLIANT if dwc:geodeticDatum is not EMPTY; otherwise NOT_COMPLIANT 
        
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
     * Is the value of dwc:decimalLatitude a number between -90 and 90 inclusive?
     * 
     * #79 Validation SingleRecord Conformance: decimallatitude outofrange
     *
     * Provides: VALIDATION_DECIMALLATITUDE_INRANGE
     * Version: 2022-03-26
     *
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_DECIMALLATITUDE_INRANGE", description="Is the value of dwc:decimalLatitude a number between -90 and 90 inclusive?")
    @Provides("b6ecda2a-ce36-437a-b515-3ae94948fe83")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/b6ecda2a-ce36-437a-b515-3ae94948fe83/2022-03-26")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if dwc:decimalLatitude is EMPTY or the value is not interpretable as a number; COMPLIANT if the value of dwc:decimalLatitude is between -90 and 90, inclusive; otherwise NOT_COMPLIANT ")
    public static DQResponse<ComplianceValue> validationDecimallatitudeInrange(
    		@ActedUpon("dwc:decimalLatitude") String decimalLatitude) {
        
    	DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:decimalLatitude is 
        // EMPTY or the value is not interpretable as a number; COMPLIANT 
        // if the value of dwc:decimalLatitude is between -90 and 90, 
        // inclusive; otherwise NOT_COMPLIANT 

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
     * Are the values of either dwc:decimalLatitude or dwc:decimalLongitude numbers that are not equal to 0?
     *
     * Provides: #87 VALIDATION_COORDINATES_NOTZERO
     * Version: 2022-05-22
     *
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_COORDINATES_NOTZERO", description="Are the values of either dwc:decimalLatitude or dwc:decimalLongitude numbers that are not equal to 0?")
    @Provides("1bf0e210-6792-4128-b8cc-ab6828aa4871")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/1bf0e210-6792-4128-b8cc-ab6828aa4871/2022-05-22")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if dwc:decimalLatitude and/or dwc:decimalLongitude are EMPTY or both of the values are not interpretable as numbers; COMPLIANT if either the value of dwc:decimalLatitude is not = 0 or the value of dwc:decimalLongitude is not = 0; otherwise NOT_COMPLIANT ")
    public static DQResponse<ComplianceValue> validationCoordinatesNotzero(@ActedUpon("dwc:decimalLatitude") String decimalLatitude, @ActedUpon("dwc:decimalLongitude") String decimalLongitude) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:decimalLatitude and/or 
        // dwc:decimalLongitude are EMPTY or both of the values are 
        // not interpretable as numbers; COMPLIANT if either the value 
        // of dwc:decimalLatitude is not = 0 or the value of dwc:decimalLongitude 
        // is not = 0; otherwise NOT_COMPLIANT 
        
    	if (GEOUtil.isEmpty(decimalLatitude)) { 
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("provided value for dwc:decimalLatitude is empty.");
    		if (GEOUtil.isEmpty(decimalLongitude)) { 
    			result.addComment("provided value for dwc:decimalLongitude is empty.");
    		}
    	} else if (GEOUtil.isEmpty(decimalLongitude)) { 
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("provided value for dwc:decimalLongitude is empty.");
    	} else if (!GEOUtil.isNumericCharacters(decimalLatitude) && !GEOUtil.isNumericCharacters(decimalLongitude)) { 
    		// internal prerequisites not met only if both are not numbers
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("provided value for dwc:decimalLatitude ["+decimalLatitude+"] and dwc:decimalLongitude ["+decimalLatitude+"] both contain non-numeric characters.");
    	} else {
    		// internal prerequisites not met only if both are not numbers, if one is, set it to 1 and test the other for zero.
    		if (!GEOUtil.isNumericCharacters(decimalLongitude)) { 
    			result.addComment("provided value for dwc:decimalLongitude ["+decimalLongitude+"] contains non-numeric characters.");
    			decimalLongitude="1";
    		}
    		if (!GEOUtil.isNumericCharacters(decimalLatitude)) { 
    			result.addComment("provided value for dwc:decimalLatitude ["+decimalLatitude+"] contains non-numeric characters.");
    			decimalLatitude="1";
    		}
    		try {
    			Double decimalLatitudeNumber = Double.parseDouble(decimalLatitude);
    			try {
    				Double decimalLongitudeNumber = Double.parseDouble(decimalLongitude);
    				if ((decimalLatitudeNumber.compareTo(0d)==0 | decimalLatitudeNumber.compareTo(-0d)==0) && 
    					(decimalLongitudeNumber.compareTo(0d)==0 | decimalLongitudeNumber.compareTo(-0d)==0)) 
    				{ 
    					result.setResultState(ResultState.RUN_HAS_RESULT);
    					result.setValue(ComplianceValue.NOT_COMPLIANT);
    					result.addComment("Both dwc:decimalLatitude and dwc:decimalLongitude are zero.");
    				} else {
    					result.setResultState(ResultState.RUN_HAS_RESULT);
    					result.setValue(ComplianceValue.COMPLIANT);
    					result.addComment("Both dwc:decimalLatitude and dwc:decimalLongitude are not both zero.");
    				}
    			} catch (NumberFormatException e) { 
    				result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    				result.addComment("provided value for dwc:decimalLongitude ["+decimalLongitude+"] is not parsable as a number.");
    			}
    		} catch (NumberFormatException e) { 
    			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    			result.addComment("provided value for dwc:decimalLatitude ["+decimalLatitude+"] is not parsable as a number.");
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
     * Is there a value in dwc:decimalLongitude?
     *
     * Provides: #96 VALIDATION_DECIMALLONGITUDE_NOTEMPTY
     * Version: 2022-03-22
     *
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_DECIMALLONGITUDE_NOTEMPTY", description="Is there a value in dwc:decimalLongitude?")
    @Provides("9beb9442-d942-4f42-8b6a-fcea01ee086a")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/9beb9442-d942-4f42-8b6a-fcea01ee086a/2022-03-22")
    @Specification("COMPLIANT if dwc:decimalLongitude is not EMPTY; otherwise NOT_COMPLIANT ")
    public static DQResponse<ComplianceValue> validationDecimallongitudeNotempty(
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
     * Is there a value in dwc:countryCode?
     *
     * Provides: #98 VALIDATION_COUNTRYCODE_NOTEMPTY
     * Version: 2022-03-22
     *
     * @param countryCode the provided dwc:countryCode to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_COUNTRYCODE_NOTEMPTY", description="Is there a value in dwc:countryCode?")
    @Provides("853b79a2-b314-44a2-ae46-34a1e7ed85e4")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/853b79a2-b314-44a2-ae46-34a1e7ed85e4/2022-03-22")
    @Specification("COMPLIANT if dwc:countryCode is not EMPTY; otherwise NOT_COMPLIANT ")
    public static DQResponse<ComplianceValue> validationCountrycodeNotempty(
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
     * Propose amendment to dwc:geodeticDatum using the value of bdq:defaultGeodeticDatum if dwc:geodeticDatum is empty.
     *
     * Provides: 102 AMENDMENT_GEODETICDATUM_ASSUMEDDEFAULT
     *
     * @param coordinateUncertantyInMeters the provided dwc:coordinateUncertantyInMeters to evaluate
     * @param geodeticDatum the provided dwc:geodeticDatum to evaluate
     * @param defaultGeodeticDatum to use as default, if not specified, uses EPSG:4326
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Amendment(label="AMENDMENT_GEODETICDATUM_ASSUMEDDEFAULT", description="Propose amendment to dwc:geodeticDatum using the value of bdq:defaultGeodeticDatum if dwc:geodeticDatum is empty.")
    @Provides("7498ca76-c4d4-42e2-8103-acacccbdffa7")
    public static DQResponse<AmendmentValue> amendmentGeodeticdatumAssumeddefault(
    		@ActedUpon("dwc:coordinateUncertaintyInMeters") String coordinateUncertaintyInMeters, 
    		@ActedUpon("dwc:geodeticDatum") String geodeticDatum,
    		@Parameter(name="bdq:defaultGeodeticDatum") String defaultGeodeticDatum) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        // Specification
        // FILLED_IN the value of dwc:geodeticDatum to the value of 
        // bdq:defaultGeodeticDatum if dwc:geodeticDatum is EMPTY; 
        // otherwise NOT_AMENDED Source Authority is "epsg" [https://epsg.io] 
        // 

        // Parameters. This test is defined as parameterized.
        // bdq:defaultGeodeticDatum default value="EPSG:4326"
        
        // TODO:  Notes discuss potential amendment to an existing coordinateUncertaintyInMeters value 
        // If the dwc:coordinateUncertaintyInMeters is EMPTY or
        // is not interpretable, this amendment should not provide a
        // dwc:coordinateUncertaintyInMeters. If the 
        // dwc:coordinateUncertaintyInMeters is not EMPTY and is valid, 
        // this amendment should add to the dwc:coordinateUncertaintyInMeters 
        // the uncertainty contributed by the conversion. Since different 
        // systems have differing requirements for what the default datum 
        // should be, it is left unspecified, but should match whatever 
        // the target datum is in #43 . After the amendment is performed, 
        // the dwc:geodeticDatum field should be the assumed default datum as parameterized.
        
        if (GEOUtil.isEmpty(defaultGeodeticDatum)) {
        	defaultGeodeticDatum = "EPSG:4326";
        }
        
        if (GEOUtil.isEmpty(geodeticDatum)) { 
        	result.addComment("Propose filling in empty geodepticDatum with the default value EPSG:4326 (2D Lat/Long Unit=degrees, CRS=WGS84)");
        	result.setResultState(ResultState.FILLED_IN);
    		Map<String, String> values = new HashMap<>();
    		values.put("dwc:geodeticDatum", defaultGeodeticDatum) ;
    		result.setValue(new AmendmentValue(values));
        } else { 
        	result.addComment("The provided geodeticDatum contains a value, not proposing a change");
        	result.setResultState(ResultState.NOT_AMENDED);
        }
        
        return result;
    }

    /**
     * Is the value of dwc:minimumDepthInMeters within the Parameter range?
     *
     * Provides: #107 VALIDATION_MINDEPTH_INRANGE
     * Version: 2022-03-26
     *
     * @param minimumDepthInMeters the provided dwc:minimumDepthInMeters to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_MINDEPTH_INRANGE", description="Is the value of dwc:minimumDepthInMeters within the Parameter range?")
    @Provides("04b2c8f3-c71b-4e95-8e43-f70374c5fb92")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/04b2c8f3-c71b-4e95-8e43-f70374c5fb92/2022-03-26")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if dwc:minimumDepthInMeters is EMPTY, or the value is not interpretable as number greater than or equal to zero; COMPLIANT if the value of dwc:minimumDepthInMeters is within the range of bdq:minimumValidDepthInMeters to bdq:maximumValidDepthInMeters inclusive; otherwise NOT_COMPLIANT ")
    public static DQResponse<ComplianceValue> validationMindepthInrange(
    		@ActedUpon("dwc:minimumDepthInMeters") String minimumDepthInMeters,
    		@Parameter(name="bdq:minimumValidDepthInMeters") Double minimumValidDepthInMeters,
    		@Parameter(name="bdq:maximumValidDepthInMeters") Double maximumValidDepthInMeters
    		) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:minimumDepthInMeters 
        // is EMPTY, or the value is not interpretable as number greater 
        // than or equal to zero; COMPLIANT if the value of dwc:minimumDepthInMeters 
        // is within the range of bdq:minimumValidDepthInMeters to 
        // bdq:maximumValidDepthInMeters inclusive; otherwise NOT_COMPLIANT 
        // 

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
        		if (depthVal < -0d) { 
        			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        			result.addComment("the provided value for dwc:minimumDepthInMeters is negative, depth must be a positive number.");
        		} else if (depthVal < minimumValidDepthInMeters) { 
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
     * Is the value of dwc:minimumElevationInMeters a number less than or equal to the value of dwc:maximumElevationInMeters?
     *
     * Provides: #108 VALIDATION_MINELEVATION_LESSTHAN_MAXELEVATION
     * Version: 2022-03-22
     *
     * @param minimumElevationInMeters the provided dwc:minimumElevationInMeters to evaluate
     * @param maximumElevationInMeters the provided dwc:maximumElevationInMeters to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_MINELEVATION_LESSTHAN_MAXELEVATION", description="Is the value of dwc:minimumElevationInMeters a number less than or equal to the value of dwc:maximumElevationInMeters?")
    @Provides("d708526b-6561-438e-aa1a-82cd80b06396")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/d708526b-6561-438e-aa1a-82cd80b06396/2022-03-22")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if dwc:maximumlevationInMeters or dwc:minimumElevationInMeters is EMPTY, or if either is not a number; COMPLIANT if the value of dwc:minimumElevationInMeters is a number less than or equal to the value of the number dwc:maximumElevationInMeters, otherwise NOT_COMPLIANT ")
    public static DQResponse<ComplianceValue> validationMinelevationLessthanMaxelevation(
    		@ActedUpon("dwc:minimumElevationInMeters") String minimumElevationInMeters, 
    		@ActedUpon("dwc:maximumElevationInMeters") String maximumElevationInMeters) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();
        
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:maximumlevationInMeters 
        // or dwc:minimumElevationInMeters is EMPTY, or if either is 
        // not a number; COMPLIANT if the value of dwc:minimumElevationInMeters 
        // is a number less than or equal to the value of the number 
        // dwc:maximumElevationInMeters, otherwise NOT_COMPLIANT 
        
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
     * Is the value of dwc:coordinateUncertaintyInMeters a number between 1 and 20,037,509?
     *
     * Provides: #109 VALIDATION_COORDINATEUNCERTAINTY_INRANGE
     *
     * @param coordinateUncertaintyInMeters the provided dwc:coordinateUncertaintyInMeters to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_COORDINATEUNCERTAINTY_INRANGE", description="Is the value of dwc:coordinateUncertaintyInMeters a number between 1 and 20,037,509?")
    @Provides("c6adf2ea-3051-4498-97f4-4b2f8a105f57")
    public static DQResponse<ComplianceValue> validationCoordinateuncertaintyInrange(
    		@ActedUpon("dwc:coordinateUncertaintyInMeters") String coordinateUncertaintyInMeters) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:coordinateUncertaintyInMeters 
        // is EMPTY; COMPLIANT if the value of dwc:coordinateUncertaintyInMeters 
        // can be interpreted as a number between 1 and 20037509 inclusive; 
        // otherwise NOT_COMPLIANT 

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

    
    /**
     * Is the value of dwc:maximumElevationInMeters of a single record within a valid range
     * 
     * Is the value of dwc:maximumElevationInMeters within the Parameter range?
     *
     * Provides: #112 VALIDATION_MAXELEVATION_INRANGE
     * Version: 2022-03-26
     *
     * @param maximumElevationInMeters the provided dwc:maximumElevationInMeters to evaluate
     * @param minimumValidElevationInMeters minimum valid value to test against, if null, defaults to -430
     * @param maximumValidElevationInMeters maximum valid value to test against, if null, defaults to 8550
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_MAXELEVATION_INRANGE", description="Is the value of dwc:maximumElevationInMeters of a single record within a valid range")
    @Provides("c971fe3f-84c1-4636-9f44-b1ec31fd63c7")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/c971fe3f-84c1-4636-9f44-b1ec31fd63c7/2022-03-26")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if dwc:maximumElevationInMeters is EMPTY or the value cannot be interpreted as a number; COMPLIANT if the value of dwc:maximumElevationInMeters is within the range of bdq:minimumValidElevationInMeters to bdq:maximumValidElevationInMeters inclusive; otherwise NOT_COMPLIANT ")
    public static DQResponse<ComplianceValue> validationMaxelevationInrange(
    		@ActedUpon("dwc:maximumElevationInMeters") String maximumElevationInMeters,
    		@Parameter(name="bdq:minimumValidElevationInMeters") Double minimumValidElevationInMeters,
    		@Parameter(name="bdq:maximumValidElevationInMeters") Double maximumValidElevationInMeters
    		) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:maximumElevationInMeters 
        // is EMPTY or the value cannot be interpreted as a number; 
        // COMPLIANT if the value of dwc:maximumElevationInMeters is 
        // within the range of bdq:minimumValidElevationInMeters to 
        // bdq:maximumValidElevationInMeters inclusive; otherwise NOT_COMPLIANT 
        // 

        // Parameters. This test is defined as parameterized.
        // Default values: bdq:minimumValidElevationInMeters="-430", bdq:maximumValidElevationInMeters="8850"


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
    @Provides("ba2d868c-afa3-409a-836b-fdcea9f75945")
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
        //service; otherwise NOT_AMENDED 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority

        return result;
    }

    /**
     * Is there a value in dwc:decimalLatitude?
     *
     * Provides: #119 VALIDATION_DECIMALLATITUDE_NOTEMPTY
     * Version: 2020-04-09
     *
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_DECIMALLATITUDE_NOTEMPTY", description="Is there a value in dwc:decimalLatitude?")
    @Provides("7d2485d5-1ba7-4f25-90cb-f4480ff1a275")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/7d2485d5-1ba7-4f25-90cb-f4480ff1a275/2020-04-09")
    @Specification("COMPLIANT if dwc:decimalLatitude is not EMPTY; otherwise NOT_COMPLIANT ")
    public static DQResponse<ComplianceValue> validationDecimallatitudeNotEmpty(
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
     * Is the value of dwc:maximumDepthInMeters within the Parameter range?
     * 
     * #187 Is the value of dwc:maximumDepthInMeters within the specified Parameter range? 
     *
     * Provides: VALIDATION_MAXDEPTH_INRANGE
     * Version: 2022-09-08
     *
     * @param maximumDepthInMeters the provided dwc:maximumDepthInMeters to evaluate
     * @param minimumValidDepthInMeters the minimum valid depth, defaults to 0 if null.
     * @param maximumValidDepthInMeters the maximum valid depth, defaults to 110000 if null.
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_MAXDEPTH_INRANGE", description="Is the value of dwc:maximumDepthInMeters within the Parameter range?")
    @Provides("3f1db29a-bfa5-40db-9fd1-fde020d81939")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/3f1db29a-bfa5-40db-9fd1-fde020d81939/2022-09-08")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if dwc:maximumDepthInMeters is EMPTY or is not interpretable as a number greater than or equal to zero; COMPLIANT if the value of dwc:maximumDepthInMeters is within the range of bdq:minimumValidDepthInMeters to bdq:maximumValidDepthInMeters inclusive; otherwise NOT_COMPLIANT ")
    public static DQResponse<ComplianceValue> validationMaxdepthInrange(
    		@ActedUpon("dwc:maximumDepthInMeters") String maximumDepthInMeters, 
    		@Parameter(name="bdq:minimumValidDepthInMeters") Double minimumValidDepthInMeters,
    		@Parameter(name="bdq:maximumValidDepthInMeters") Double maximumValidDepthInMeters
    		) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:maximumDepthInMeters 
        // is EMPTY or is not interpretable as a number greater than 
        // or equal to zero; COMPLIANT if the value of dwc:maximumDepthInMeters 
        // is within the range of bdq:minimumValidDepthInMeters to 
        // bdq:maximumValidDepthInMeters inclusive; otherwise NOT_COMPLIANT 

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
        		if (depthVal < -0d) { 
        			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        			result.addComment("the provided value for dwc:maximumDepthInMeters is negative, depth must be a positive number.");
        		} else if (depthVal < minimumValidDepthInMeters) { 
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

// TODO: Implementation of VALIDATION_COUNTRYCODE_STANDARD is not up to date with current version: https://rs.tdwg.org/bdq/terms/0493bcfb-652e-4d17-815b-b0cce0742fbe/2022-05-02 see line: 90
// TODO: Implementation of VALIDATION_COUNTRY_FOUND is not up to date with current version: https://rs.tdwg.org/bdq/terms/69b2efdc-6269-45a4-aecb-4cb99c2ae134/2022-08-29 see line: 147
// TODO: Implementation of VALIDATION_MINDEPTH_LESSTHAN_MAXDEPTH is not up to date with current version: https://rs.tdwg.org/bdq/terms/8f1e6e58-544b-4365-a569-fb781341644e/2022-03-22 see line: 173
// TODO: Implementation of VALIDATION_DECIMALLONGITUDE_INRANGE is not up to date with current version: https://rs.tdwg.org/bdq/terms/0949110d-c06b-450e-9649-7c1374d940d1/2022-03-22 see line: 241
// TODO: Implementation of AMENDMENT_COORDINATES_FROM_VERBATIM is not up to date with current version: https://rs.tdwg.org/bdq/terms/3c2590c7-af8a-4eb4-af57-5f73ba9d1f8e/2023-01-13 see line: 295
// TODO: Implementation of VALIDATION_MINELEVATION_INRANGE is not up to date with current version: https://rs.tdwg.org/bdq/terms/0bb8297d-8f8a-42d2-80c1-558f29efe798/2022-03-26 see line: 325
// TODO: Implementation of VALIDATION_LOCATION_NOTEMPTY is not up to date with current version: https://rs.tdwg.org/bdq/terms/58486cb6-1114-4a8a-ba1e-bd89cfe887e9/2022-03-22 see line: 405
// TODO: Implementation of AMENDMENT_COORDINATES_CONVERTED is not up to date with current version: https://rs.tdwg.org/bdq/terms/620749b9-7d9c-4890-97d2-be3d1cde6da8/2023-01-16 see line: 513
// TODO: Implementation of AMENDMENT_COUNTRYCODE_STANDARDIZED is not up to date with current version: https://rs.tdwg.org/bdq/terms/fec5ffe6-3958-4312-82d9-ebcca0efb350/2023-03-07 see line: 536
// TODO: Implementation of VALIDATION_COORDINATES_COUNTRYCODE_CONSISTENT is not up to date with current version: https://rs.tdwg.org/bdq/terms/adb27d29-9f0d-4d52-b760-a77ba57a69c9/2023-02-27 see line: 573
// TODO: Implementation of VALIDATION_COORDINATES_TERRESTRIALMARINE is not up to date with current version: https://rs.tdwg.org/bdq/terms/b9c184ce-a859-410c-9d12-71a338200380/2022-03-02 see line: 603
// TODO: Implementation of AMENDMENT_COORDINATES_TRANSPOSED is not up to date with current version: https://rs.tdwg.org/bdq/terms/f2b4a50a-6b2f-4930-b9df-da87b6a21082/2022-03-30 see line: 639
// TODO: Implementation of AMENDMENT_MINDEPTH-MAXDEPTH_FROM_VERBATIM is not up to date with current version: https://rs.tdwg.org/bdq/terms/c5658b83-4471-4f57-9d94-bf7d0a96900c/2022-04-19 see line: 667
// TODO: Implementation of VALIDATION_COORDINATES_STATEPROVINCE_CONSISTENT is not up to date with current version: https://rs.tdwg.org/bdq/terms/f18a470b-3fe1-4aae-9c65-a6d3db6b550c/2023-03-19 see line: 693
// TODO: Implementation of VALIDATION_GEODETICDATUM_STANDARD is not up to date with current version: https://rs.tdwg.org/bdq/terms/7e0c0418-fe16-4a39-98bd-80e19d95b9d1/2022-03-22 see line: 720
// TODO: Implementation of AMENDMENT_GEODETICDATUM_STANDARDIZED is not up to date with current version: https://rs.tdwg.org/bdq/terms/0345b325-836d-4235-96d0-3b5caf150fc0/2022-03-30 see line: 744
// TODO: Implementation of VALIDATION_COUNTRY_COUNTRYCODE_CONSISTENT is not up to date with current version: https://rs.tdwg.org/bdq/terms/b23110e7-1be7-444a-a677-cdee0cf4330c/2022-05-02 see line: 770
// TODO: Implementation of AMENDMENT_MINELEVATION-MAXELEVATION_FROM_VERBATIM is not up to date with current version: https://rs.tdwg.org/bdq/terms/2d638c8b-4c62-44a0-a14d-fa147bf9823d/2023-02-27 see line: 799
// TODO: Implementation of ISSUE_DATAGENERALIZATIONS_NOTEMPTY is not up to date with current version: https://rs.tdwg.org/bdq/terms/13d5a10e-188e-40fd-a22c-dbaa87b91df2/2022-05-16 see line: 58
// TODO: Implementation of AMENDMENT_COUNTRYCODE_FROM_COORDINATES is not up to date with current version: https://rs.tdwg.org/bdq/terms/8c5fe9c9-4ba9-49ef-b15a-9ccd0424e6ae/2022-05-02 see line: 826
// TODO: Implementation of VALIDATION_GEODETICDATUM_NOTEMPTY is not up to date with current version: https://rs.tdwg.org/bdq/terms/239ec40e-a729-4a8e-ba69-e0bf03ac1c44/2022-03-22 see line: 853
// TODO: Implementation of VALIDATION_DECIMALLATITUDE_INRANGE is not up to date with current version: https://rs.tdwg.org/bdq/terms/b6ecda2a-ce36-437a-b515-3ae94948fe83/2022-03-26 see line: 882
// TODO: Implementation of VALIDATION_COORDINATES_NOTZERO is not up to date with current version: https://rs.tdwg.org/bdq/terms/1bf0e210-6792-4128-b8cc-ab6828aa4871/2022-05-22 see line: 932
// TODO: Implementation of VALIDATION_DECIMALLONGITUDE_NOTEMPTY is not up to date with current version: https://rs.tdwg.org/bdq/terms/9beb9442-d942-4f42-8b6a-fcea01ee086a/2022-03-22 see line: 1030
// TODO: Implementation of AMENDMENT_GEODETICDATUM_ASSUMEDDEFAULT is not up to date with current version: https://rs.tdwg.org/bdq/terms/7498ca76-c4d4-42e2-8103-acacccbdffa7/2022-09-12 see line: 1098
// TODO: Implementation of VALIDATION_MINDEPTH_INRANGE is not up to date with current version: https://rs.tdwg.org/bdq/terms/04b2c8f3-c71b-4e95-8e43-f70374c5fb92/2022-03-26 see line: 1154
// TODO: Implementation of VALIDATION_MINELEVATION_LESSTHAN_MAXELEVATION is not up to date with current version: https://rs.tdwg.org/bdq/terms/d708526b-6561-438e-aa1a-82cd80b06396/2022-03-22 see line: 1228
// TODO: Implementation of VALIDATION_COORDINATEUNCERTAINTY_INRANGE is not up to date with current version: https://rs.tdwg.org/bdq/terms/c6adf2ea-3051-4498-97f4-4b2f8a105f57/2022-03-22 see line: 1291
// TODO: Implementation of VALIDATION_MAXELEVATION_INRANGE is not up to date with current version: https://rs.tdwg.org/bdq/terms/c971fe3f-84c1-4636-9f44-b1ec31fd63c7/2022-03-26 see line: 1349
// TODO: Implementation of VALIDATION_DECIMALLATITUDE_NOTEMPTY is not up to date with current version: https://rs.tdwg.org/bdq/terms/7d2485d5-1ba7-4f25-90cb-f4480ff1a275/2020-04-09 see line: 1452
// TODO: Implementation of VALIDATION_MAXDEPTH_INRANGE is not up to date with current version: https://rs.tdwg.org/bdq/terms/3f1db29a-bfa5-40db-9fd1-fde020d81939/2022-09-08 see line: 1527
    /**
     * Does the value of dwc:stateProvince occur in bdq:sourceAuthority?
     *
     * Provides: VALIDATION_STATEPROVINCE_FOUND
     * Version: 2022-09-05
     *
     * @param stateProvince the provided dwc:stateProvince to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_STATEPROVINCE_FOUND", description="Does the value of dwc:stateProvince occur in bdq:sourceAuthority?")
    @Provides("4daa7986-d9b0-4dd5-ad17-2d7a771ea71a")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/4daa7986-d9b0-4dd5-ad17-2d7a771ea71a/2022-09-05")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:stateProvince is EMPTY; COMPLIANT if the value of dwc:stateProvince occurs as an administrative entity that is a child to at least one entity representing an ISO country-like entity in the bdq:sourceAuthority; otherwise NOT_COMPLIANT bdq:sourceAuthority default = 'The Getty Thesaurus of Geographic Names (TGN)' [https://www.getty.edu/research/tools/vocabularies/tgn/index.html]")
    public DQResponse<ComplianceValue> validationStateprovinceFound(@ActedUpon("dwc:stateProvince") String stateProvince) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:stateProvince 
        // is EMPTY; COMPLIANT if the value of dwc:stateProvince occurs 
        // as an administrative entity that is a child to at least 
        // one entity representing an ISO country-like entity in the 
        // bdq:sourceAuthority; otherwise NOT_COMPLIANT bdq:sourceAuthority 
        // default = "The Getty Thesaurus of Geographic Names (TGN)" 
        // [https://www.getty.edu/research/tools/vocabularies/tgn/index.html] 
        // 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority

        return result;
    }

    /**
     * Are the combination of the values of dwc:country, dwc:stateProvince consistent with the values in the bdq:sourceAuthority?
     *
     * Provides: VALIDATION_COUNTRYSTATEPROVINCE_CONSISTENT
     * Version: 2022-12-12
     *
     * @param country the provided dwc:country to evaluate
     * @param stateProvince the provided dwc:stateProvince to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_COUNTRYSTATEPROVINCE_CONSISTENT", description="Are the combination of the values of dwc:country, dwc:stateProvince consistent with the values in the bdq:sourceAuthority?")
    @Provides("e654f562-44f8-43fd-983b-2aaba4c6dda9")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/e654f562-44f8-43fd-983b-2aaba4c6dda9/2022-12-12")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if the terms dwc:country or dwc:stateProvince are EMPTY; COMPLIANT if the value of dwc:stateProvince occurs as an administrative entity that is a child to the entity matching the value of dwc:country in the bdq:sourceAuthority, and the match to dwc:country is an ISO country-like entity in the bdq:sourceAuthority; otherwise NOT_COMPLIANT bdq:sourceAuthority default = 'The Getty Thesaurus of Geographic Names (TGN)' [https://www.getty.edu/research/tools/vocabularies/tgn/index.html]")
    public DQResponse<ComplianceValue> validationCountrystateprovinceConsistent(@ActedUpon("dwc:country") String country, @ActedUpon("dwc:stateProvince") String stateProvince) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if the 
        // terms dwc:country or dwc:stateProvince are EMPTY; COMPLIANT 
        // if the value of dwc:stateProvince occurs as an administrative 
        // entity that is a child to the entity matching the value 
        // of dwc:country in the bdq:sourceAuthority, and the match 
        // to dwc:country is an ISO country-like entity in the bdq:sourceAuthority; 
        // otherwise NOT_COMPLIANT bdq:sourceAuthority default = "The 
        // Getty Thesaurus of Geographic Names (TGN)" [https://www.getty.edu/research/tools/vocabularies/tgn/index.html] 
        // 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority

        return result;
    }

    /**
     * Is the combination of the values of the terms dwc:country, dwc:stateProvince unique in the bdq:sourceAuthority?
     *
     * Provides: VALIDATION_COUNTRYSTATEPROVINCE_UNAMBIGUOUS
     * Version: 2022-09-05
     *
     * @param country the provided dwc:country to evaluate
     * @param stateProvince the provided dwc:stateProvince to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_COUNTRYSTATEPROVINCE_UNAMBIGUOUS", description="Is the combination of the values of the terms dwc:country, dwc:stateProvince unique in the bdq:sourceAuthority?")
    @Provides("d257eb98-27cb-48e5-8d3c-ab9fca4edd11")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/d257eb98-27cb-48e5-8d3c-ab9fca4edd11/2022-09-05")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if the terms dwc:country and dwc:stateProvince are EMPTY; COMPLIANT if the combination of values of dwc:country and dwc:stateProvince are unambiguously resolved to a single result with a child-parent relationship in the bdq:sourceAuthority and the entity matching the value of dwc:country in the bdq:sourceAuthority is an ISO country-like entity in the bdq:sourceAuthority; otherwise NOT_COMPLIANT bdq:sourceAuthority default = 'The Getty Thesaurus of Geographic Names (TGN)' [https://www.getty.edu/research/tools/vocabularies/tgn/index.html]")
    public DQResponse<ComplianceValue> validationCountrystateprovinceUnambiguous(@ActedUpon("dwc:country") String country, @ActedUpon("dwc:stateProvince") String stateProvince) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if the 
        // terms dwc:country and dwc:stateProvince are EMPTY; COMPLIANT 
        // if the combination of values of dwc:country and dwc:stateProvince 
        // are unambiguously resolved to a single result with a child-parent 
        // relationship in the bdq:sourceAuthority and the entity matching 
        // the value of dwc:country in the bdq:sourceAuthority is an 
        // ISO country-like entity in the bdq:sourceAuthority; otherwise 
        // NOT_COMPLIANT bdq:sourceAuthority default = "The Getty Thesaurus 
        // of Geographic Names (TGN)" [https://www.getty.edu/research/tools/vocabularies/tgn/index.html] 
        // 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority default="The Getty Thesaurus of Geographic Names (TGN)"

        return result;
    }

}
