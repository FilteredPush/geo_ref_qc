/* NOTE: requires the ffdq-api dependecy in the maven pom.xml */

package org.filteredpush.qc.georeference;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datakurator.ffdq.annotations.*;
import org.datakurator.ffdq.api.DQResponse;
import org.datakurator.ffdq.model.ResultState;
import org.filteredpush.qc.georeference.util.CountryLookup;
import org.filteredpush.qc.georeference.util.GEOUtil;
import org.filteredpush.qc.georeference.util.GeoRefCacheValue;
import org.filteredpush.qc.georeference.util.GeoUtilSingleton;
import org.filteredpush.qc.georeference.util.GeolocationResult;
import org.filteredpush.qc.georeference.util.GeorefServiceException;
import org.filteredpush.qc.georeference.util.GettyLookup;
import org.filteredpush.qc.georeference.util.TransformationStruct;
import org.filteredpush.qc.georeference.util.UnknownToWGS84Error;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.ows.ServiceException;

import edu.getty.tgn.service.GettyTGNObject;

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
 * #21	VALIDATION_COUNTRY_FOUND 69b2efdc-6269-45a4-aecb-4cb99c2ae134
 * #98	VALIDATION_COUNTRYCODE_NOTEMPTY 853b79a2-b314-44a2-ae46-34a1e7ed85e4
 * #62	VALIDATION_COUNTRY_COUNTRYCODE_CONSISTENT" b23110e7-1be7-444a-a677-cdee0cf4330c
 * #199	VALIDATION_STATEPROVINCE_FOUND 4daa7986-d9b0-4dd5-ad17-2d7a771ea71a
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
 * #43	AMENDMENT_COORDINATES_CONVERTED 620749b9-7d9c-4890-97d2-be3d1cde6da8
 * #102 AMENDMENT_GEODETICDATUM_ASSUMEDDEFAULT 7498ca76-c4d4-42e2-8103-acacccbdffa7
 * #48	AMENDMENT_COUNTRYCODE_STANDARDIZED fec5ffe6-3958-4312-82d9-ebcca0efb350
 * #73	AMENDMENT_COUNTRYCODE_FROM_COORDINATES 8c5fe9c9-4ba9-49ef-b15a-9ccd0424e6ae
 * #68	AMENDMENT_MINELEVATION-MAXELEVATION_FROM_VERBATIM 2d638c8b-4c62-44a0-a14d-fa147bf9823d
 * #55	AMENDMENT_MINDEPTH-MAXDEPTH_FROM_VERBATIM c5658b83-4471-4f57-9d94-bf7d0a96900c
 * #60	AMENDMENT_GEODETICDATUM_STANDARDIZED 0345b325-836d-4235-96d0-3b5caf150fc0
 *
 *
 * For #72, see rec_occur_qc DwCMetadataDQ
 * #72 ISSUE_DATAGENERALIZATIONS_NOTEMPTY 13d5a10e-188e-40fd-a22c-dbaa87b91df2
 *
 * @author mole
 * @version $Id: $Id
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
     * Does the value of dwc:country occur in bdq:sourceAuthority?
     *
     * #21 Validation SingleRecord Conformance: country notstandard
     *
     * Provides: VALIDATION_COUNTRY_FOUND
     * Version: 2022-08-29
     *
     * @param country the provided dwc:country to evaluate
     * @param sourceAuthority the source authority to consult, if null uses ""The Getty Thesaurus of Geographic Names (TGN)",
     * 	additional supported values for sourceAuthority are "NaturalEarth" and "datahub.io".
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_COUNTRY_FOUND", description="Does the value of dwc:country occur in bdq:sourceAuthority?")
    @Provides("69b2efdc-6269-45a4-aecb-4cb99c2ae134")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/69b2efdc-6269-45a4-aecb-4cb99c2ae134/2022-08-29")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:country was EMPTY; COMPLIANT if value of dwc:country is a place type equivalent to 'nation' by the bdq:sourceAuthority; otherwise NOT_COMPLIANT bdq:sourceAuthority default = 'The Getty Thesaurus of Geographic Names (TGN)' [https://www.getty.edu/research/tools/vocabularies/tgn/index.html]")
    public static DQResponse<ComplianceValue> validationCountryFound(@ActedUpon("dwc:country") String country,
    		@Parameter(name="bdq:sourceAuthority") String sourceAuthority
    		) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:country 
        // was EMPTY; COMPLIANT if value of dwc:country is a place 
        // type equivalent to "nation" by the bdq:sourceAuthority; 
        // otherwise NOT_COMPLIANT 
        // 

        // Parameters; This test is defined as parameterized.
        // bdq:sourceAuthority
        // default = "The Getty Thesaurus of Geographic Names (TGN)" [https://www.getty.edu/research/tools/vocabularies/tgn/index.html]
        
        if (sourceAuthority==null) { 
        	sourceAuthority = GettyLookup.GETTY_TGN;
        }

        if (GEOUtil.isEmpty(country)) { 
        	result.addComment("dwc:country is empty");
        	result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        } else { 
        	if (sourceAuthority.equalsIgnoreCase(GettyLookup.GETTY_TGN)) {
        		Boolean cached = GeoUtilSingleton.getInstance().getTgnCountriesEntry(country);
        		if (cached!=null) { 
        			if (cached) {
        				result.addComment("the value provided for dwc:country [" + country + "] exists as a nation in the Getty Thesaurus of Geographic Names (TGN).");
        				result.setResultState(ResultState.RUN_HAS_RESULT);
        				result.setValue(ComplianceValue.COMPLIANT);
        			} else { 
        				result.addComment("the value provided for dwc:country [" + country + "] is not a nation in the Getty Thesaurus of Geographic Names (TGN).");
        				result.setResultState(ResultState.RUN_HAS_RESULT);
        				result.setValue(ComplianceValue.NOT_COMPLIANT);
        			}
        		} else { 
        			GettyLookup lookup = new GettyLookup();
        			if (lookup.lookupCountryExact(country)==null) { 
        				result.addComment("Error looking up country in " + sourceAuthority);
        				result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);
        			} else if (lookup.lookupCountryExact(country)) { 
        				result.addComment("the value provided for dwc:country [" + country + "] exists as a nation in the Getty Thesaurus of Geographic Names (TGN).");
        				result.setResultState(ResultState.RUN_HAS_RESULT);
        				result.setValue(ComplianceValue.COMPLIANT);
        				GeoUtilSingleton.getInstance().addTgnCountry(country, true);
        			} else { 
        				result.addComment("the value provided for dwc:country [" + country + "] is not a nation in the Getty Thesaurus of Geographic Names (TGN).");
        				result.setResultState(ResultState.RUN_HAS_RESULT);
        				result.setValue(ComplianceValue.NOT_COMPLIANT);
        				GeoUtilSingleton.getInstance().addTgnCountry(country, false);
        			}
        		}
        	} else if (sourceAuthority.equalsIgnoreCase("NaturalEarth")) {
        		if (GEOUtil.isCountryKnown(country)) {
        			result.addComment("the value provided for dwc:country [" + country + "] exists as a country name in the NaturalEarth admin regions.");
        			result.setResultState(ResultState.RUN_HAS_RESULT);
        			result.setValue(ComplianceValue.COMPLIANT);
        		} else { 
        			result.addComment("the value provided for dwc:country [" + country + "] is not a country name in the NaturalEarth admin regions.");
        			result.setResultState(ResultState.RUN_HAS_RESULT);
        			result.setValue(ComplianceValue.NOT_COMPLIANT);
        		}
        	} else if (sourceAuthority.equalsIgnoreCase("datahub.io")) {
        		if (CountryLookup.countryExistsHasCode(country)) { 
        			result.addComment("the value provided for dwc:country [" + country + "] exists as a country name in the datahub.io list of countries.");
        			result.setResultState(ResultState.RUN_HAS_RESULT);
        			result.setValue(ComplianceValue.COMPLIANT);
        		} else { 
        			result.addComment("the value provided for dwc:country [" + country + "] is not a country name in the datahub.io list of countries.");
        			result.setResultState(ResultState.RUN_HAS_RESULT);
        			result.setValue(ComplianceValue.NOT_COMPLIANT);
        		}
        	} else { 
        		result.addComment("Unknown bdq:sourceAuthority");
        		result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);
        	}
        }
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
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate
     * @param verbatimCoordinates the provided dwc:verbatimCoordinates to evaluate
     * @param verbatimLatitude the provided dwc:verbatimLatitude to evaluate
     * @param verbatimLongitude the provided dwc:verbatimLongitude to evaluate
     * @param verbatimSRS the provided dwc:verbatimSRS to evaluate
     * @param verbatimCoordinateSystem the provided dwc:verbatimCoordinateSystem to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Provides("3c2590c7-af8a-4eb4-af57-5f73ba9d1f8e")
    public static DQResponse<AmendmentValue> amendmentCoordinatesFromVerbatim(
    		@ActedUpon("dwc:decimalLatitude") String decimalLatitude, 
    		@ActedUpon("dwc:decimalLongitude") String decimalLongitude, 
    		@ActedUpon("dwc:verbatimCoordinates") String verbatimCoordinates, 
    		@ActedUpon("dwc:verbatimLatitude") String verbatimLatitude,
    		@ActedUpon("dwc:verbatimLongitude") String verbatimLongitude, 
    		@ActedUpon("dwc:verbatimSRS") String verbatimSRS, 
    		@ActedUpon("dwc:verbatimCoordinateSystem") String verbatimCoordinateSystem
    		)
    {
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
        
        boolean done = false;
        
        if (!GEOUtil.isEmpty(decimalLatitude) || !GEOUtil.isEmpty(decimalLongitude)) { 
        	result.setResultState(ResultState.NOT_AMENDED);
        	result.addComment("At least one of dwc:verbatimLatitude and dwc:dacimalLongitude contain a value.");
        	done = true;
        } 
        
        if (!done && GEOUtil.isEmpty(verbatimCoordinates) && (GEOUtil.isEmpty(verbatimLatitude) || GEOUtil.isEmpty(verbatimLongitude))) { 
        	result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        	if (GEOUtil.isEmpty(verbatimCoordinates)) { 
        		result.addComment("No value provided for dwc:verbatimCoordinates.");
        	} 
        	if (GEOUtil.isEmpty(verbatimLatitude)) { 
        		result.addComment("No value provided for dwc:verbatimLatitude.");
        	}
        	if (GEOUtil.isEmpty(verbatimLongitude)) { 
        		result.addComment("No value provided for dwc:verbatimLongitude.");
        	}
        	done = true;
        }

        // TODO: Evaluate verbatimCoordinateSystem and verbatimSRS.
        // if coordinates are lat/long and verbatimSRS is blank or consistent with EPSG:4326, do the transform
        // if not, identify an available transform (none implemented yet) or fail.
        
        boolean interpreted = false;
        
        if (!done && !GEOUtil.isEmpty(verbatimCoordinates)) { 
        	if (verbatimCoordinates.matches("^[0-9]{2}[A-Z] [A-Z]{2} [0-9]{3,5} [0-9]{3,5}")) {
        		// MGRS, USNG
        		result.addComment("Provided value for verbatimCoordinates ["+verbatimCoordinates+"] appears to be a MGRS or USNG coordinate, not converting.");
        		result.setResultState(ResultState.NOT_AMENDED);
        	} else if (verbatimCoordinates.matches("^[0-9]{2}[A-Z] [0-9]{7} [0-9]{7}")) { 
        		// UTM/UPS
        		// TODO: Support transformation to EPSG:4326
        		GeolocationResult conversion =  GEOUtil.convertUTMToLatLong(verbatimCoordinates,false);
        		if (conversion==null) { 
        			result.addComment("Provided value for verbatimCoordinates ["+verbatimCoordinates+"] appears to be a UTM or UPS coordinate, but unable to convert.");
        			result.setResultState(ResultState.NOT_AMENDED);
        		} else { 
        			result.setResultState(ResultState.FILLED_IN);
        			String newDecimalLatitude = conversion.getLatitude().toString();
        			String newDecimalLongitude = conversion.getLongitude().toString();
        			Map<String,String> map = new HashMap();
        			map.put("dwc:decimalLatitude", newDecimalLatitude);
        			map.put("dwc:decimalLongitude", newDecimalLongitude);
        			result.setValue(new AmendmentValue(map));
        			result.addComment("Interpreted decimalLatitude ["+newDecimalLatitude+"] and decimalLongitude ["+newDecimalLongitude+"] from provided verbatimCoordinates ["+verbatimCoordinates+"] as UTM coordinate, assuming a WGS84 datum and MGRS zone letters");
        			interpreted = true;
        		}
        	} else { 
        		if (verbatimCoordinates.contains(";")) {
        			verbatimCoordinates = verbatimCoordinates.replace(";", ",");
        		}
        		String nverbatimCoordinates = GEOUtil.simplifyVerbatimCoordinate(verbatimCoordinates);
        		if (nverbatimCoordinates.contains(",")) {
        			logger.debug(nverbatimCoordinates);
        			String[] bits = nverbatimCoordinates.split(",");
        			if (bits.length==2) { 
        				logger.debug(bits[0]);
        				logger.debug(bits[1]);
        				Double latitude = null;
        				Double longitude = null;
        				if (bits[0].matches(".*[NnSs].*")) { 
        					latitude = GEOUtil.parseVerbatimLatLongToDecimalDegree(bits[0]);
        					longitude = GEOUtil.parseVerbatimLatLongToDecimalDegree(bits[1]);
        				} else if (bits[0].matches(".*[EeWw].*")) { 
        					latitude = GEOUtil.parseVerbatimLatLongToDecimalDegree(bits[1]);
        					logger.debug(latitude);
        					longitude = GEOUtil.parseVerbatimLatLongToDecimalDegree(bits[0]);
        					logger.debug(longitude);
        				} else {
        					latitude = GEOUtil.parseVerbatimLatLongToDecimalDegree(bits[1]);
        					longitude = GEOUtil.parseVerbatimLatLongToDecimalDegree(bits[0]);
        					if (latitude==null && longitude!=null && longitude <=90d) { 
        						latitude = GEOUtil.parseVerbatimLatLongToDecimalDegree(bits[0]);
        						longitude = GEOUtil.parseVerbatimLatLongToDecimalDegree(bits[1]);
        					}
        				}
        	         	if (latitude!=null && latitude<=90d && latitude >= -90d &&
        	        		longitude!=null && longitude<=180d && longitude >= -180d
        	        		) { 
        	        		result.setResultState(ResultState.FILLED_IN);
        	        		String newDecimalLatitude = latitude.toString();
        	        		String newDecimalLongitude = longitude.toString();
        	        		Map<String,String> map = new HashMap();
        	        		map.put("dwc:decimalLatitude", newDecimalLatitude);
        	        		map.put("dwc:decimalLongitude", newDecimalLongitude);
        	        		result.setValue(new AmendmentValue(map));
        	        		result.addComment("Interpreted decimalLatitude ["+newDecimalLatitude+"] and decimalLongitude ["+newDecimalLongitude+"] from provided verbatimCoordinates ["+verbatimCoordinates+"]");
        	        		interpreted = true;
        	        	}
        			}
        		}
        		
        	}
        } else if (!done && !GEOUtil.isEmpty(verbatimLatitude) && !GEOUtil.isEmpty(verbatimLongitude)) {
        	String nverbatimLatitude = GEOUtil.simplifyVerbatimCoordinate(verbatimLatitude);
        	String nverbatimLongitude = GEOUtil.simplifyVerbatimCoordinate(verbatimLongitude);
        	Double latitude = GEOUtil.parseVerbatimLatLongToDecimalDegree(nverbatimLatitude);
        	Double longitude = GEOUtil.parseVerbatimLatLongToDecimalDegree(nverbatimLongitude);
         	if (latitude!=null && latitude<=90d && latitude >= -90d &&
        		longitude!=null && longitude<=180d && longitude >= -180d
        		) { 
        		result.setResultState(ResultState.FILLED_IN);
        		String newDecimalLatitude = latitude.toString();
        		String newDecimalLongitude = longitude.toString();
        		Map<String,String> map = new HashMap();
        		map.put("dwc:decimalLatitude", newDecimalLatitude);
        		map.put("dwc:decimalLongitude", newDecimalLongitude);
        		result.setValue(new AmendmentValue(map));
        		result.addComment("Interpreted decimalLatitude ["+newDecimalLatitude+"] and decimalLongitude ["+newDecimalLongitude+"] from provided verbatimLatitude ["+verbatimLatitude+"] and verbatimLongtude ["+verbatimLongitude+"]");
        	    interpreted = true;
        	}
        		
        }
        
        if (result.getResultState().equals(ResultState.NOT_RUN) && interpreted == false) { 
        	result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        	result.addComment("Unable to interpret provided verbatim coordinate values: verbatimCoordinates["+verbatimCoordinates+"], verbatimLatitude ["+verbatimLatitude+"], verbatimLongitude ["+verbatimLongitude+"].");
        }
        
        return result;
    }

    /**
     * Is the value of dwc:minimumElevationInMeters within the Parameter range?
     *
     * Provides: #39 VALIDATION_MINELEVATION_INRANGE
     * Version: 2022-03-26
     *
     * @param minimumElevationInMeters the provided dwc:minimumElevationInMeters to evaluate
     * @param minimumValidElevationInMeters minimum valid value to test against, if null, defaults to -430
     * @param maximumValidElevationInMeters maximum valid value to test against, if null, defaults to 8550
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_MINELEVATION_INRANGE", description="Is the value of dwc:minimumElevationInMeters within the Parameter range?")
    @Provides("0bb8297d-8f8a-42d2-80c1-558f29efe798")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/0bb8297d-8f8a-42d2-80c1-558f29efe798/2022-03-26")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if dwc:minimumElevationInMeters is EMPTY or the value is not a number; COMPLIANT if the value of dwc:minimumElevationInMeters is within the range of bdq:minimumValidElevationInMeters to bdq:maximumValidElevationInMeters inclusive; otherwise NOT_COMPLIANT ")
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
     * @param higherGeographyID the provided dwc:higherGeographyID to evaluate
     * @param continent the provided dwc:continent to evaluate
     * @param waterBody the provided dwc:waterBody to evaluate
     * @param islandGroup the provided dwc:islandGroup to evaluate
     * @param islandGroup the provided dwc:islandGroup to evaluate
     * @param island the provided dwc:island to evaluate
     * @param country the provided dwc:country to evaluate
     * @param countryCode the provided dwc:countryCode to evaluate
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
    public static DQResponse<ComplianceValue> validationCountryNotempty(@ActedUpon("dwc:country") String country) {
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

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:decimalLatitude is 
        // EMPTY or does not have a valid value, or dwc:decimalLongitude 
        // is EMPTY or does not have a valid value, or dwc:geodeticDatum 
        // is EMPTY or does not contain an interpretable value; AMENDED 
        // if the values of dwc:decimalLatitude, dwc:decimalLongitude 
        // and dwc:geodeticDatum are changed based on a conversion 
        // between the coordinate reference systems as specified by 
        // dwc:geodeticDatum and bdq:targetCRS, and, if dwc:coordinateUncertaintyInMeters 
        // was an interpretable value, the uncertainty from the conversion 
        // is added to it, and the value of dwc:coordinatePrecision 
        // is provided from the conversion result; otherwise NOT_AMENDED. 
        // bdq:targetCRS = "EPSG:4326" 
    
    /**
     * Propose amendment to the value of dwc:geodeticDatum and potentially to dwc:decimalLatitude and/or dwc:decimalLongitude based on a conversion between spatial reference systems.
     *
     * #43 Amendment SingleRecord Conformance: coordinates converted
     *
     * Provides: AMENDMENT_COORDINATES_CONVERTED
     * Version: 2023-06-24
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
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/620749b9-7d9c-4890-97d2-be3d1cde6da8/2023-06-24")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if dwc:decimalLatitude is EMPTY or does not have a valid value, or dwc:decimalLongitude is EMPTY or does not have a valid value, or dwc:geodeticDatum is EMPTY or does not contain an interpretable value; AMENDED if the values of dwc:decimalLatitude, dwc:decimalLongitude and dwc:geodeticDatum are changed based on a conversion between the coordinate reference systems as specified by dwc:geodeticDatum and bdq:targetCRS, and, if dwc:coordinateUncertaintyInMeters was an interpretable value, the uncertainty from the conversion is added to it, and the value of dwc:coordinatePrecision is provided from the conversion result; otherwise NOT_AMENDED. bdq:targetCRS = 'EPSG:4326'")
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
        
        // NOTES: 
        // This test relates only to EPSG codes applying to spatial reference systems where 
        // the coordinate system is EPSG:6422 (EPSG:Ellipsoidal 2D CS. Axes: latitude, longitude. 
        // Orientations: north, east. UoM: degree). Any amendment has implications for 
        // dwc:coordinateUncertaintyInMeters and dwc:coordinatePrecision. If the 
        // dwc:coordinateUncertaintyInMeters is EMPTY or is not interpretable, 
        // this amendment should not provide a dwc:coordinateUncertaintyInMeters. 
        // If the dwc:coordinateUncertaintyInMeters is not EMPTY and is valid, 
        // this amendment should add the uncertainty contributed by the conversion 
        // to the value of dwc:coordinateUncertaintyInMeters. The amended 
        // dwc:coordinatePrecision should be the precision of coordinates as 
        // provided after the conversion, ideally this should be 0.0000001, 
        // reflecting the seven digits of precision required to reverse a coordinate 
        // transformation without loss of information at the scale of one meter. 
        // If dwc:geodeticDatum specifies the same CRS for dwc:decimalLatitude and 
        // dwc:decimalLongitude as bdq:targetCRS (e.g., if dwc:geodeticDatum has 
        // either the value "WGS84" or "EPSG:4326" and the bdq:targetCRS is "EPSG:4326"), 
        // then the coordinates are assumed to be in the target CRS and the Response.status is NOT_AMENDED.
        
        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:decimalLatitude is 
        // EMPTY or does not have a valid value, or dwc:decimalLongitude 
        // is EMPTY or does not have a valid value, or dwc:geodeticDatum 
        // is EMPTY or does not contain an interpretable value; AMENDED 
        // if the values of dwc:decimalLatitude, dwc:decimalLongitude 
        // and dwc:geodeticDatum are changed based on a conversion 
        // between the coordinate reference systems as specified by 
        // dwc:geodeticDatum and bdq:targetCRS, and, if dwc:coordinateUncertaintyInMeters 
        // was an interpretable value, the uncertainty from the conversion 
        // is added to it, and the value of dwc:coordinatePrecision 
        // is provided from the conversion result; otherwise NOT_AMENDED. 
        // bdq:targetCRS = "EPSG:4326" 
    
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
        				logger.debug(transform.getDecimalLatitude());
        				logger.debug(transform.getDecimalLongitude());
        				if (!GEOUtil.isEmpty(coordinateUncertaintyInMeters)) { 
        					
        				}
        				Double precision = Math.pow(10.0, (-transform.getPrecision()));
        				logger.debug(transform.getPrecision());
        				logger.debug(precision);
        				values.put("dwc:coordinatePrecision", Double.toString(precision));
        				result.setValue(new AmendmentValue(values));
        				result.setResultState(ResultState.AMENDED); 
        				result.addComment("Coordinates converted from " + decimalLatitude + "," + decimalLongitude + " " + geodeticDatum + " to " + transform.getDecimalLatitudeString() + ", "  + transform.getDecimalLongitudeString() + " " + targetGeodeticDatum);
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
     * Propose amendment to the value of dwc:countryCode if it can be interpreted as an ISO country code.
     *
     * #48 Amendment SingleRecord Conformance: countrycode standardized
     *
     * Provides: AMENDMENT_COUNTRYCODE_STANDARDIZED
     * Version: 2023-03-07
     *
     * @param countryCode the provided dwc:countryCode to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Amendment(label="AMENDMENT_COUNTRYCODE_STANDARDIZED", description="Propose amendment to the value of dwc:countryCode if it can be interpreted as an ISO country code.")
    @Provides("fec5ffe6-3958-4312-82d9-ebcca0efb350")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/fec5ffe6-3958-4312-82d9-ebcca0efb350/2023-03-07")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISTITES_NOT_MET if the value of dwc:countryCode is EMPTY; AMENDED the value of dwc:countryCode if it can be unambiguously interpreted from bdq:sourceAuthority; otherwise NOT_AMENDED bdq:sourceAuthority is 'ISO 3166-1-alpha-2' [https://restcountries.eu/#api-endpoints-list-of-codes, https://www.iso.org/obp/ui/#search]")
    public static DQResponse<AmendmentValue> amendmentCountrycodeStandardized(@ActedUpon("dwc:countryCode") String countryCode) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        // Specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISTITES_NOT_MET if the 
        // value of dwc:countryCode is EMPTY; AMENDED the value of 
        // dwc:countryCode if it can be unambiguously interpreted from 
        // bdq:sourceAuthority; otherwise NOT_AMENDED bdq:sourceAuthority 

        // TODO: Specification needs work, shouldn't be parameterized.
        
        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority
        //"ISO 3166-1-alpha-2" [https://restcountries.eu/#api-endpoints-list-of-codes, 
        // https://www.iso.org/obp/ui/#search] 
        
        if (GEOUtil.isEmpty(countryCode)) {
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("provided value for dwc:countryCode is empty.");
        } else { 
        	if (CountryLookup.codeTwoLetterMatched(countryCode)) { 
        		result.setResultState(ResultState.NOT_AMENDED);
        		result.addComment("provided value for dwc:countryCode ["+countryCode+"] is a known ISO 3166-1-alpha-2 country code.");
        	} else { 
        		result.addComment("provided value for dwc:countryCode ["+countryCode+"] is not a known ISO 3166-1-alpha-2 country code.");
        		if (CountryLookup.codeTwoLetterMatched(countryCode.trim().toUpperCase())) { 
        			result.addComment("changed case for dwc:countryCode ["+countryCode+"] to match a known ISO 3166-1-alpha-2 country code.");
        			result.setResultState(ResultState.AMENDED);
        			Map<String, String> values = new HashMap<>();
        			values.put("dwc:countryCode", countryCode.trim().toUpperCase());
        			result.setValue(new AmendmentValue(values));
        		} else { 
        			String match = CountryLookup.lookupCode2FromCodeName(countryCode.trim());
        			if (match!=null) { 
        				result.addComment("Match found for ISO 3166-1-alpha-2 country code.");
        				result.setResultState(ResultState.AMENDED);
        				Map<String, String> values = new HashMap<>();
        				values.put("dwc:countryCode", match);
        				result.setValue(new AmendmentValue(values));
        			} else { 
        				result.setResultState(ResultState.NOT_AMENDED);
        				result.addComment("unable to amend the provided value for dwc:countryCode ["+countryCode+"] to a ISO 3166-1-alpha-2 country code.");
        			}
        		}
        	}
        }
        

        return result;
    }

    /**
     * Do the geographic coordinates fall on or within the boundaries of the territory given in
     * dwc:countryCode or its Exclusive Economic Zone?
     *
     * Uses the default value for bdq:spatialBufferInMeters
     *
     * #50 Validation SingleRecord Consistency: coordinates countrycode inconsistent
     *
     * Provides: #50 VALIDATION_COORDINATES_COUNTRYCODE_CONSISTENT
     * Version: 2023-02-27
     *
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate
     * @param countryCode the provided dwc:countryCode to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     * @param sourceAuthority a {@link java.lang.String} object.
     */
    @Validation(label="VALIDATION_COORDINATES_COUNTRYCODE_CONSISTENT", description="Do the geographic coordinates fall on or within the boundaries of the territory given in dwc:countryCode or its Exclusive Economic Zone?")
    @Provides("adb27d29-9f0d-4d52-b760-a77ba57a69c9")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/adb27d29-9f0d-4d52-b760-a77ba57a69c9/2023-02-27")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if one or more of dwc:decimalLatitude, dwc:decimalLongitude, or dwc:countryCode are EMPTY or invalid; COMPLIANT if the geographic coordinates fall on or within the boundary defined by the union of the boundary of the country from dwc:countryCode plus it's Exclusive Economic Zone, if any, plus an exterior buffer given by bdq:spatialBufferInMeters; otherwise NOT_COMPLIANT bdq:sourceAuthority default = 'ADM1 boundaries' [https://gadm.org] UNION with 'EEZs' [https://marineregions.org],bdq:spatialBufferInMeters default = '3000'")
    public static DQResponse<ComplianceValue> validationCoordinatesCountrycodeConsistent(
    		@ActedUpon("dwc:decimalLatitude") String decimalLatitude, 
    		@ActedUpon("dwc:decimalLongitude") String decimalLongitude, 
    		@ActedUpon("dwc:countryCode") String countryCode,
    		@Parameter(name="bdq:sourceAuthority") String sourceAuthority
    		) {
    	String spatialBufferInMeters = "3000";
    	return validationCoordinatesCountrycodeConsistent(decimalLatitude, decimalLongitude, countryCode, spatialBufferInMeters, sourceAuthority);
    }
    
    /**
     * Do the geographic coordinates fall on or within the boundaries of the territory given in dwc:countryCode or its Exclusive Economic Zone?
     *
     * #50 Validation SingleRecord Consistency: coordinates countrycode inconsistent
     *
     * Provides: #50 VALIDATION_COORDINATES_COUNTRYCODE_CONSISTENT
     * Version: 2023-02-27
     *
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate
     * @param countryCode the provided dwc:countryCode to evaluate
     * @param spatialBufferInMeters the value of bdq:spatialBufferInMeters to apply.
     * @return DQResponse the response of type ComplianceValue  to return
     * @param sourceAuthority a {@link java.lang.String} object.
     */
    @Validation(label="VALIDATION_COORDINATES_COUNTRYCODE_CONSISTENT", description="Do the geographic coordinates fall on or within the boundaries of the territory given in dwc:countryCode or its Exclusive Economic Zone?")
    @Provides("adb27d29-9f0d-4d52-b760-a77ba57a69c9")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/adb27d29-9f0d-4d52-b760-a77ba57a69c9/2023-02-27")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if one or more of dwc:decimalLatitude, dwc:decimalLongitude, or dwc:countryCode are EMPTY or invalid; COMPLIANT if the geographic coordinates fall on or within the boundary defined by the union of the boundary of the country from dwc:countryCode plus it's Exclusive Economic Zone, if any, plus an exterior buffer given by bdq:spatialBufferInMeters; otherwise NOT_COMPLIANT bdq:sourceAuthority default = 'ADM1 boundaries' [https://gadm.org] UNION with 'EEZs' [https://marineregions.org],bdq:spatialBufferInMeters default = '3000'")
    public static DQResponse<ComplianceValue> validationCoordinatesCountrycodeConsistent(
    		@ActedUpon("dwc:decimalLatitude") String decimalLatitude, 
    		@ActedUpon("dwc:decimalLongitude") String decimalLongitude, 
    		@ActedUpon("dwc:countryCode") String countryCode,
    		@Parameter(name="bdq:spatialBufferInMeters") String spatialBufferInMeters, 
    		@Parameter(name="bdq:sourceAuthority") String sourceAuthority
    		) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if one 
        // or more of dwc:decimalLatitude, dwc:decimalLongitude, or 
        // dwc:countryCode are EMPTY or invalid; COMPLIANT if the geographic 
        // coordinates fall on or within the boundary defined by the 
        // union of the boundary of the country from dwc:countryCode 
        // plus it's Exclusive Economic Zone, if any, plus an exterior 
        // buffer given by bdq:spatialBufferInMeters; otherwise NOT_COMPLIANT 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority; bdq:spatialBufferInMeters
        // bdq:sourceAuthority default = "ADM1 boundaries" [https://gadm.org] 
        // UNION with "EEZs" [https://marineregions.org]
        // bdq:spatialBufferInMeters default = "3000" 

 
        if (GEOUtil.isEmpty(sourceAuthority)) { 
        	// TODO: Value needs to be determined in the issue.
        	sourceAuthority = "ADM1 boundaries UNION EEZ";
        }

        try { 
        	GeoRefSourceAuthority sourceAuthorityObject = new GeoRefSourceAuthority(sourceAuthority);
        	if (sourceAuthorityObject.getAuthority().equals(EnumGeoRefSourceAuthority.INVALID)) { 
        		throw new SourceAuthorityException("Invalid Source Authority");
        	}



        	if (GEOUtil.isEmpty(spatialBufferInMeters)) { 
        		spatialBufferInMeters = "3000";
        	}

        	Double buffer_km = 3d;
        	try { 
        		buffer_km = Double.parseDouble(spatialBufferInMeters);
        		buffer_km = buffer_km / 1000d;
        	} catch (Exception e) {
        		buffer_km = 3d;
        	}

        	if (GEOUtil.isEmpty(decimalLatitude)) { 
        		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        		result.addComment("The value provided for dwc:decimalLatitude is empty");
        	} else if (GEOUtil.isEmpty(decimalLongitude)) { 
        		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        		result.addComment("The value provided for dwc:decimalLongitude is empty");
        	} else if (GEOUtil.isEmpty(countryCode)) { 
        		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        		result.addComment("The value provided for dwc:countryCode is empty");
        	} else { 
        		if (!sourceAuthority.equals("ADM1 boundaries UNION EEZ")) { 
        			result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);
        			result.addComment("Unsupported or unrecognized source authority.");
        		} else { 
        			String countryCode3 = countryCode;
        			if (!countryCode.matches("^[A-Z]$")) {
        				// expected case, two letter country code, find the three letter code used in the datasets.
        				countryCode3 = CountryLookup.lookupCode3FromCodeName(countryCode);
        			}
        			if (countryCode3==null) { 
        				result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        				result.addComment("Unable to look up country from provided country code ["+countryCode+"].");
        			} else {
        				try { 
        					Double lat = Double.parseDouble(decimalLatitude);
        					Double lng = Double.parseDouble(decimalLongitude);
        					result.setResultState(ResultState.RUN_HAS_RESULT);
        					if (GEOUtil.isPointNearCountryPlusEEZ(countryCode3, lat, lng, buffer_km)) { 
        						result.setValue(ComplianceValue.COMPLIANT);
        						result.addComment("Provided coordinate lies within the bounds of the country specified by the country code.");
        					} else { 
        						result.setValue(ComplianceValue.NOT_COMPLIANT);
        						result.addComment("Provided coordinate decimalLatitude=["+decimalLatitude+"], decimalLongitude=["+decimalLongitude+"] lies outside the bounds of the country specified by the country code ["+countryCode+"].");
        					}
        				} catch (NumberFormatException e) { 
        					result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        					result.addComment("Error parsing numeric latitude/longitude from provided dwc:decimalLatitude ["+decimalLatitude+"] or dwc:decimalLongitude ["+ decimalLongitude +"].");
        				}
        			}
        		}
        	}
        } catch (SourceAuthorityException e) { 
        	result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);
        	result.addComment("Error with specified Source Authority: " + e.getMessage());
        }
        
        return result;
    }


    /**
     *  Propose amendments of the values of dwc:minimumDepthInMeters and
     *  dwc:maximumDepthInMeters if they can be interpreted from dwc:verbatimDepth
     * 
     * #55 Amendment SingleRecord Completeness: mindepth-maxdepth from verbatim
     *
     * Provides: AMENDMENT_MINDEPTH-MAXDEPTH_FROM_VERBATIM
     * Version: 2024-07-31
     *
     * @param verbatimDepth the provided dwc:verbatimDepth to evaluate
     * @param maximumDepthInMeters the provided dwc:maximumDepthInMeters to evaluate
     * @param minimumDepthInMeters the provided dwc:minimumDepthInMeters to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Validation(label="AMENDMENT_MINDEPTH-MAXDEPTH_FROM_VERBATIM", description="Propose amendments of the values of dwc:minimumDepthInMeters and dwc:maximumDepthInMeters if they can be interpreted from dwc:verbatimDepth.")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if dwc:minimumDepthInMeters and dwc:maximumDepthInMeters are EMPTY and either dwc:verbatimDepth is EMPTY or the value is not unambiguously interpretable; FILLED_IN the value of dwc:minimumDepthInMeters and dwc:maximumDepthInMeters if they are EMPTY and could be unambiguously determined from dwc:verbatimDepth; otherwise NOT_AMENDED.")
    @Provides("c5658b83-4471-4f57-9d94-bf7d0a96900c")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/c5658b83-4471-4f57-9d94-bf7d0a96900c/2024-07-31")
    public static DQResponse<AmendmentValue> amendmentMindepthMaxdepthFromVerbatim(
    		@ActedUpon("dwc:verbatimDepth") String verbatimDepth, 
    		@ActedUpon("dwc:maximumDepthInMeters") String maximumDepthInMeters, 
    		@ActedUpon("dwc:minimumDepthInMeters") String minimumDepthInMeters) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        // Specification
		// INTERNAL_PREREQUISITES_NOT_MET if dwc:minimumDepthInMeters and
		// dwc:maximumDepthInMeters are EMPTY and either dwc:verbatimDepth is EMPTY or
		// the value is not unambiguously interpretable; FILLED_IN the value of
		// dwc:minimumDepthInMeters and dwc:maximumDepthInMeters if they are EMPTY and
		// could be unambiguously determined from dwc:verbatimDepth; otherwise
		// NOT_AMENDED.

        if (GEOUtil.isEmpty(verbatimDepth) && GEOUtil.isEmpty(maximumDepthInMeters) && GEOUtil.isEmpty(minimumDepthInMeters)) { 
        	result.addComment("No Value provided for dwc:verbatimDepth");
        	result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        } else {
        	if (GEOUtil.isEmpty(maximumDepthInMeters) && GEOUtil.isEmpty(minimumDepthInMeters)) { 

        		// M Ambiguous, probably not meters, probably miles
        		
        		// TODO: pattern "to {number} {units}"
       
        		logger.debug(verbatimDepth);
        		if (verbatimDepth.matches("^[0-9]+([.]{0,1}[0-9]*){0,1} *(m|m[.]|[mM](eter(s){0,1}))$")) { 
        			String cleaned = verbatimDepth.replaceAll("[ Mmetrs]+", "").trim();
        			cleaned = cleaned.replaceAll("[.]$","");
        			result.addComment("Interpreted equal minimum and maximum depths in meters from dwc:verbatimDepth ["+ verbatimDepth +"] interpreted as a depth range in meters ");
        			Map<String, String> values = new HashMap<>();
        			values.put("dwc:minimumDepthInMeters", cleaned);        		
        			values.put("dwc:maximumDepthInMeters", cleaned);
        			result.setValue(new AmendmentValue(values));
        			result.setResultState(ResultState.FILLED_IN);
        		} else if (verbatimDepth.matches("^to [0-9]+([.]{0,1}[0-9]*){0,1} *(m|m[.]|[mM](eter(s){0,1}))$")) { 
        			// pattern "to 1 m" change to "0-1m"
        			String cleaned = verbatimDepth.replaceAll("^to ","");
        			cleaned = cleaned.replaceAll("[ Mmetrs]+", "").trim();
        			cleaned = cleaned.replaceAll("[.]$","");
        			result.addComment("Interpreted minimum of 0 and and maximum depths in meters from dwc:verbatimDepth ["+ verbatimDepth +"] interpreted as a depth range in meters ");
        			Map<String, String> values = new HashMap<>();
        			values.put("dwc:minimumDepthInMeters", "0");        		
        			values.put("dwc:maximumDepthInMeters", cleaned);
        			result.setValue(new AmendmentValue(values));
        			result.setResultState(ResultState.FILLED_IN);        			
        		} else if (verbatimDepth.matches("^[0-9]+([.]{0,1}[0-9]*){0,1}[ to-]{0,4}[0-9]+([.]{0,1}[0-9]*){0,1} *(m|m[.]|[mM](eter(s){0,1}))$")) { 
        			//1-2m  1.1 to 2.2 m
        			String cleaned = verbatimDepth.replaceAll(" ", "");
        			cleaned = cleaned.replace("to","-");
        			cleaned = cleaned.replaceAll("[mMetrs]","");
        			cleaned = cleaned.replaceAll("[.]$","");
        			String[] bits = cleaned.split("-");
        			result.addComment("Interpreted minimum and maximum depths in meters from dwc:verbatimDepth ["+ verbatimDepth +"] interpreted as a depth range in meters ");
        			float min = Float.parseFloat(bits[0]);
        			float max = min;
        			if (bits.length>1) { 
        				max = Float.parseFloat(bits[1]);
        			}
        			Map<String, String> values = new HashMap<>();
        			if (bits.length==1) { 
        				values.put("dwc:minimumDepthInMeters", bits[0]);        		
        				values.put("dwc:maximumDepthInMeters", bits[0]);
        			} else if (min<max) { 
        				values.put("dwc:minimumDepthInMeters", bits[0]);        		
        				values.put("dwc:maximumDepthInMeters", bits[1]);
        			} else { 
        				values.put("dwc:minimumDepthInMeters", bits[1]);        		
        				values.put("dwc:maximumDepthInMeters", bits[0]);
        			}
        			result.setValue(new AmendmentValue(values));
        			result.setResultState(ResultState.FILLED_IN);
        		} else if (verbatimDepth.matches("^[0-9]+([.]{0,1}[0-9]*){0,1} *cm$")) { 
        			// Centimeters
        			String cleaned = verbatimDepth.replaceAll("[ cm]+", "").trim();
        			cleaned = cleaned.replaceAll("[.]$","");
        			result.addComment("Interpreted equal minimum and maximum depths in meters from dwc:verbatimDepth ["+ verbatimDepth +"] interpreted as a depth range in cm");
        			Map<String, String> values = new HashMap<>();
        			float min = Float.parseFloat(cleaned);
        			values.put("dwc:minimumDepthInMeters", Double.toString(min / 100d));        		
        			values.put("dwc:maximumDepthInMeters", Double.toString(min / 100d));
        			result.setValue(new AmendmentValue(values));
        			result.setResultState(ResultState.FILLED_IN);
        		} else if (verbatimDepth.matches("^[0-9]+([.]{0,1}[0-9]*){0,1} *([fF]ms|[fF]ms.|[fF]ath(om){0,1}s{0,1}){1}$")) { 
        			// Fathoms
        			String cleaned = verbatimDepth.replaceAll("[ fFathoms]+", "").trim();
        			cleaned = cleaned.replaceAll("[.]$","");
        			result.addComment("Interpreted equal minimum and maximum depths in meters from dwc:verbatimDepth ["+ verbatimDepth +"] interpreted as a depth in fathoms ");
        			cleaned = cleaned.replaceAll("[^0-9.]", "");
        			double min = Double.parseDouble(cleaned);
        			// using 6 imperial feet to one fathom, not 6 US survey feet to one fathom.
        			// this will introduce an error on older US charts, but the error is negligible.
        			// difference is between 1.8288 and 1.828804, this works out to a 
        			// difference at 15,000 fathoms of 0.06 meters.
        			min = min * GEOUtil.FATHOMS_TO_METERS;
        			Map<String, String> values = new HashMap<>();
        			values.put("dwc:minimumDepthInMeters", Double.toString(min));        		
        			values.put("dwc:maximumDepthInMeters", Double.toString(min));
        			result.setValue(new AmendmentValue(values));
        			result.setResultState(ResultState.FILLED_IN);
        		} else if (verbatimDepth.matches("^[0-9]+([.]{0,1}[0-9]*){0,1}[ to-]{0,4}[0-9]+([.]{0,1}[0-9]*){0,1} *([fF]ms|[fF]ms.|[fF]ath(om){0,1}s{0,1}){1}$")) { 
        			//1-2m  1.1 to 2.2 m
        			String cleaned = verbatimDepth.replaceAll(" ", "");
        			cleaned = cleaned.replace("to","-");
        			cleaned = cleaned.replaceAll("[fFathoms]","");
        			cleaned = cleaned.replaceAll("[.]$","");
        			String[] bits = cleaned.split("-");
        			result.addComment("Interpreted minimum and maximum depths in meters from dwc:verbatimDepth ["+ verbatimDepth +"] interpreted as a depth range in fathoms ");
        			double min = Double.parseDouble(bits[0]);
        			double max = min;
        			if (bits.length>1) { 
        				max = Double.parseDouble(bits[1]);
        			}
        			Map<String, String> values = new HashMap<>();
        			if (bits.length==1) { 
        				String meters = Double.toString(min * GEOUtil.FATHOMS_TO_METERS);
        				values.put("dwc:minimumDepthInMeters", meters);        		
        				values.put("dwc:maximumDepthInMeters", meters);
        			} else if (min<max) { 
        				String minMeters = Double.toString(min * GEOUtil.FATHOMS_TO_METERS);
        				String maxMeters = Double.toString(max * GEOUtil.FATHOMS_TO_METERS);
        				values.put("dwc:minimumDepthInMeters", minMeters);        		
        				values.put("dwc:maximumDepthInMeters", maxMeters);
        			} else { 
        				String minMeters = Double.toString(min * GEOUtil.FATHOMS_TO_METERS);
        				String maxMeters = Double.toString(max * GEOUtil.FATHOMS_TO_METERS);
        				values.put("dwc:minimumDepthInMeters", maxMeters);        		
        				values.put("dwc:maximumDepthInMeters", minMeters);
        			}
        			result.setValue(new AmendmentValue(values));
        			result.setResultState(ResultState.FILLED_IN);        			
        		} else if (verbatimDepth.matches("^[0-9]+([.]{0,1}[0-9]*){0,1} *([fF]eet|[Ff]oot|[fF]t[.]{0,1}){1}$")) { 
        			String cleaned = verbatimDepth.replaceAll("[ fFote]+", "").trim();
        			cleaned = cleaned.replaceAll("[.]$","");
        			result.addComment("Interpreted equal minimum and maximum depths in meters from dwc:verbatimDepth ["+ verbatimDepth +"] interpreted as a depth in feet ");
        			cleaned = cleaned.replaceAll("[^0-9.]", "");
        			double min = Double.parseDouble(cleaned);
        			min = min * GEOUtil.FEET_TO_METERS;
        			Map<String, String> values = new HashMap<>();
        			values.put("dwc:minimumDepthInMeters", Double.toString(min));        		
        			values.put("dwc:maximumDepthInMeters", Double.toString(min));
        			result.setValue(new AmendmentValue(values));
        			result.setResultState(ResultState.FILLED_IN);
        		} else if (verbatimDepth.matches("^[0-9]+([.]{0,1}[0-9]*){0,1}[ to-]{0,4}[0-9]+([.]{0,1}[0-9]*){0,1} *([fF]eet|[Ff]oot|[fF]t[.]{0,1}){1}$")) { 
        			//1.1 to 2.2 ft.
        			String cleaned = verbatimDepth.replaceAll(" ", "");
        			cleaned = cleaned.replace("to","-");
        			cleaned = cleaned.replaceAll("[fFote]","");
        			cleaned = cleaned.replaceAll("[.]$","");
        			String[] bits = cleaned.split("-");
        			result.addComment("Interpreted minimum and maximum depths in meters from dwc:verbatimDepth ["+ verbatimDepth +"] interpreted as a depth range in fathoms ");
        			double min = Double.parseDouble(bits[0]);
        			double max = min;
        			if (bits.length>1) { 
        				max = Double.parseDouble(bits[1]);
        			}
        			Map<String, String> values = new HashMap<>();
        			if (bits.length==1) { 
        				String meters = Double.toString(min * GEOUtil.FEET_TO_METERS);
        				values.put("dwc:minimumDepthInMeters", meters);        		
        				values.put("dwc:maximumDepthInMeters", meters);
        			} else if (min<max) { 
        				String minMeters = Double.toString(min * GEOUtil.FEET_TO_METERS);
        				String maxMeters = Double.toString(max * GEOUtil.FEET_TO_METERS);
        				values.put("dwc:minimumDepthInMeters", minMeters);        		
        				values.put("dwc:maximumDepthInMeters", maxMeters);
        			} else { 
        				String minMeters = Double.toString(min * GEOUtil.FEET_TO_METERS);
        				String maxMeters = Double.toString(max * GEOUtil.FEET_TO_METERS);
        				values.put("dwc:minimumDepthInMeters", maxMeters);        		
        				values.put("dwc:maximumDepthInMeters", minMeters);
        			}
        			result.setValue(new AmendmentValue(values));
        			result.setResultState(ResultState.FILLED_IN);
        		}  else { 
        			result.addComment("Unable to Interpret provided dwc:verbatimDepth ["+ verbatimDepth +"].");
        			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        		}
        		
        		
        	} else { 
        		result.addComment("At least one of dwc:minimumDepthInMeters and dwc:maximumDepthInMeters contains a value.");
        		result.setResultState(ResultState.NOT_AMENDED);
        	}
        }
        return result;
    }

    /**
     * Do the geographic coordinates fall on or within the boundary from the
     * bdq:sourceAuthority for the given dwc:stateProvince or within the distance
     * given by bdq:spatialBufferInMeters outside that boundary?
     * 
     * #56 Validation SingleRecord Consistency: coordinates state-province
     * inconsistent
     *
     * Provides: VALIDATION_COORDINATES-STATEPROVINCE_CONSISTENT
     * Version: 2024-04-16
     *
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate
     * @param stateProvince the provided dwc:stateProvince to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_COORDINATES-STATEPROVINCE_CONSISTENT", description="Do the geographic coordinates fall on or within the boundary from the bdq:sourceAuthority for the given dwc:stateProvince or within the distance given by bdq:spatialBufferInMeters outside that boundary?")
    @Provides("f18a470b-3fe1-4aae-9c65-a6d3db6b550c")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/f18a470b-3fe1-4aae-9c65-a6d3db6b550c/2024-04-16")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if the values of dwc:decimalLatitude, dwc:decimalLongitude, or dwc:stateProvince are EMPTY or invalid; COMPLIANT if the geographic coordinates fall on or within the boundary from the bdq:sourceAuthority for the given dwc:stateProvince (after coordinate reference system transformations, if any, have been accounted for), or within the distance given by bdq:spatialBufferInMeters outside that boundary; otherwise NOT_COMPLIANT.")
    public static DQResponse<ComplianceValue> validationCoordinatesStateprovinceConsistent(
    		@ActedUpon("dwc:decimalLatitude") String decimalLatitude, 
    		@ActedUpon("dwc:decimalLongitude") String decimalLongitude, 
    		@ActedUpon("dwc:stateProvince") String stateProvince,
    		@Parameter(name="bdq:sourceAuthority") String sourceAuthority,
    		@Parameter(name="bdq:spatialBufferInMeters") String spatialBufferInMeters
    		) 
    {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
		// EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available;
		// INTERNAL_PREREQUISITES_NOT_MET if the values of dwc:decimalLatitude,
		// dwc:decimalLongitude, or dwc:stateProvince are EMPTY or invalid; COMPLIANT if
		// the geographic coordinates fall on or within the boundary from the
		// bdq:sourceAuthority for the given dwc:stateProvince (after coordinate
		// reference system transformations, if any, have been accounted for), or within
		// the distance given by bdq:spatialBufferInMeters outside that boundary;
		// otherwise NOT_COMPLIANT.

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:spatialBufferInMeters default = "3000"
        // bdq:sourceAuthority default = "ADM1 boundaries" {[https://gadm.org]}
        
        // ADM0 is country level boundaries
        // ADM1 is state/province level boundaries
        // ADM2 is county level boundaries.
 
		// The geographic determination service is expected to return a list of names of
		// first-level administrative divisions for geometries that the geographic point
		// falls on or within, including a 3 km buffer around the administrative
		// geometry. A match on any of those names should constitute a consistency, and
		// dwc:countryCode should not be needed to make this determination, that is,
		// this test does not attempt to disambiguate potential duplicate first-level
		// administrative division names. The level of buffering may be related to the
		// scale of the underlying GIS layer being used. At a global scale, typical map
		// scales used for borders and coastal areas are either 1:3M or 1:1M (Dooley
		// 2005, Chapter 4). Horizontal accuracy at those scales is around 1.5-2.5km and
		// 0.5-0.85 km respectively (Chapman & Wieczorek 2020).
        
        if (GEOUtil.isEmpty(sourceAuthority)) { 
        	// TODO: Add support for GADM data (can't redistrbute).
        	sourceAuthority = "ADM1 boundaries";
        }

        try { 
        	GeoRefSourceAuthority sourceAuthorityObject = new GeoRefSourceAuthority(sourceAuthority);
        	if (sourceAuthorityObject.getAuthority().equals(EnumGeoRefSourceAuthority.INVALID)) { 
        		throw new SourceAuthorityException("Invalid Source Authority");
        	}
        	
        	if (GEOUtil.isEmpty(spatialBufferInMeters)) { 
        		spatialBufferInMeters = "3000";
        	}

        	Double buffer_km = 3d;
        	try { 
        		buffer_km = Double.parseDouble(spatialBufferInMeters);
        		buffer_km = buffer_km / 1000d;
        	} catch (Exception e) {
        		buffer_km = 3d;
        	}

        	if (GEOUtil.isEmpty(decimalLatitude)) { 
        		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        		result.addComment("The value provided for dwc:decimalLatitude is empty");
        	} else if (GEOUtil.isEmpty(decimalLongitude)) { 
        		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        		result.addComment("The value provided for dwc:decimalLongitude is empty");
        	} else if (GEOUtil.isEmpty(stateProvince)) { 
        		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        		result.addComment("The value provided for dwc:stateProvince is empty");
        	} else { 
        		try { 
        			Double lat = Double.parseDouble(decimalLatitude);
        			Double lng = Double.parseDouble(decimalLongitude);
        			result.setResultState(ResultState.RUN_HAS_RESULT);
        			if (GEOUtil.isPointNearPrimaryAllowDuplicates(stateProvince, lat, lng, buffer_km)) { 
        				result.setValue(ComplianceValue.COMPLIANT);
        				result.addComment("Provided coordinate lies within the bounds of the provided stateProvince.");
        			} else { 
        				result.setValue(ComplianceValue.NOT_COMPLIANT);
        				result.addComment("Provided coordinate decimalLatitude=["+decimalLatitude+"], decimalLongitude=["+decimalLongitude+"] lies outside the bounds of the provided stateProvince ["+stateProvince+"].");
        			}
        		} catch (NumberFormatException e) { 
        			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        			result.addComment("Error parsing numeric latitude/longitude from provided dwc:decimalLatitude ["+decimalLatitude+"] or dwc:decimalLongitude ["+ decimalLongitude +"].");
        		}
        	}
        } catch (SourceAuthorityException e) { 
        	result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);
        	result.addComment("Error with specified Source Authority: " + e.getMessage());
        }
        
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
    public static DQResponse<ComplianceValue> validationGeodeticdatumStandard(
    		@ActedUpon("dwc:geodeticDatum") String geodeticDatum) {
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
					matched = GEOUtil.isCoordinateSystemCodeKnown("EPSG:"+geodeticDatum);
				} else { 
					matched = GEOUtil.isCoordinateSystemCodeKnown(geodeticDatum);
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
     * Propose amendment to the value of dwc:geodeticDatum using bdq:sourceAuthority.
     *
     * Provides: 60 AMENDMENT_GEODETICDATUM_STANDARDIZED
     * Version:  	2024-07-24
     *
     * @param geodeticDatum the provided dwc:geodeticDatum to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Amendment(label="AMENDMENT_GEODETICDATUM_STANDARDIZED", description="Propose amendment to the value of dwc:geodeticDatum using bdq:sourceAuthority.")
    @Provides("0345b325-836d-4235-96d0-3b5caf150fc0")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/0345b325-836d-4235-96d0-3b5caf150fc0/ 	2024-07-24")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority was not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:geodeticDatum is EMPTY; AMENDED the value of dwc:geodeticDatum if it could be unambiguously interpreted as a value in bdq:sourceAuthority; otherwise NOT_AMENDED bdq:sourceAuthority = \"EPSG\" {[https://epsg.org]} {API for EPSG codes [https://apps.epsg.org/api/swagger/ui/index#/Datum]}")
    public static DQResponse<AmendmentValue> amendmentGeodeticdatumStandardized(
    		@ActedUpon("dwc:geodeticDatum") String geodeticDatum) {
    	DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

    	// Specification
		// EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority was not available;
		// INTERNAL_PREREQUISITES_NOT_MET if dwc:geodeticDatum is EMPTY; AMENDED the
		// value of dwc:geodeticDatum if it could be unambiguously interpreted as a
		// value in bdq:sourceAuthority; otherwise NOT_AMENDED 
    	// bdq:sourceAuthority =
		// "EPSG" {[https://epsg.org]} {API for EPSG codes
		// [https://apps.epsg.org/api/swagger/ui/index#/Datum]}

        if (GEOUtil.isEmpty(geodeticDatum)) { 
        	result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        	result.addComment("Provided dwc:geodeticDatum is empty.");
        } else { 
        	if (geodeticDatum.trim().equals("^4326$")) {
        		// assume EPSG code is intended
        		result.setResultState(ResultState.AMENDED);
        		String amended = "EPSG:4326";
        		result.addComment("Added missing EPSG namespace to dwc:geodeticDatum ["+geodeticDatum+"] making it ["+amended+"]");
        		Map<String,String> value = new HashMap<String,String>();
        		value.put("dwc:geodeticDatum",amended);
        		result.setValue(new AmendmentValue(value));
        	} else if (geodeticDatum.trim().matches("^epsg:[0-9]+$")) {
        		result.setResultState(ResultState.AMENDED);
        		String amended = geodeticDatum.toUpperCase().trim();
        		result.addComment("Corrected case in provided dwc:geodeticDatum ["+geodeticDatum+"] to ["+amended+"]");
        		Map<String,String> value = new HashMap<String,String>();
        		value.put("dwc:geodeticDatum",amended);
        		result.setValue(new AmendmentValue(value));
        	} else if (geodeticDatum.trim().matches("^epsg[0-9]+$")) {
        		// handle missing :
        		result.setResultState(ResultState.AMENDED);
        		String amended = geodeticDatum.toUpperCase().trim();
        		amended = amended.replace("EPSG", "EPSG:");
        		result.addComment("Corrected case in provided dwc:geodeticDatum ["+geodeticDatum+"] to ["+amended+"]");
        		Map<String,String> value = new HashMap<String,String>();
        		value.put("dwc:geodeticDatum",amended);
        		result.setValue(new AmendmentValue(value));
        	} else if (geodeticDatum.trim().toUpperCase().replace(" ", "").equals("WGS84")) {
        		// WGS84 to EPSG code for geographic SRS with WGS84 as the datum.
        		// dwc:decimalLatitude/decimalLongitude use a coordinate system that is: 
        		// Ellipsoidal 2D CS. Axes: latitude, longitude. Orientations: north, east. UoM: degree 
        		result.setResultState(ResultState.AMENDED);
        		String amended = "EPSG:4326";
        		result.addComment("Corrected provided dwc:geodeticDatum ["+geodeticDatum+"] to ["+amended+"] applied to geographic coordinate system of dwc:decimalLatitude and dwc:decimalLongitude");
        		Map<String,String> value = new HashMap<String,String>();
        		value.put("dwc:geodeticDatum",amended);
        		result.setValue(new AmendmentValue(value));
        	} else { 
        		result.setResultState(ResultState.NOT_AMENDED);
        		result.addComment("Provided dwc:geodeticDatum ["+geodeticDatum+"] not interpreted to an EPSG code for a geographic CRS.");
        	}
        }
        
        return result;
    }

    /**
     * Does the ISO country code determined from the value of dwc:country equal the value of dwc:countryCode?
     *
     * #62 Validation SingleRecord Consistency: country countrycode inconsistent
     *
     * Provides: VALIDATION_COUNTRY_COUNTRYCODE_CONSISTENT
     * Version: 2022-05-02
     *
     * @param country the provided dwc:country to evaluate
     * @param countryCode the provided dwc:countryCode to evaluate
     * @param countryCode the provided dwc:countryCode to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_COUNTRY_COUNTRYCODE_CONSISTENT", description="Does the ISO country code determined from the value of dwc:country equal the value of dwc:countryCode?")
    @Provides("b23110e7-1be7-444a-a677-cdee0cf4330c")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/b23110e7-1be7-444a-a677-cdee0cf4330c/2022-05-02")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if either of the terms dwc:country or dwc:countryCode are EMPTY; COMPLIANT if the value of the country code determined from the value of dwc:country is equal to the value of dwc:countryCode; otherwise NOT_COMPLIANT bdq:sourceAuthority is 'ISO 3166-1-alpha-2' [https://restcountries.eu/#api-endpoints-list-of-codes, https://www.iso.org/obp/ui/#search]")
    public static DQResponse<ComplianceValue> validationCountryCountrycodeConsistent(
    		@ActedUpon("dwc:country") String country, 
    		@ActedUpon("dwc:countryCode") String countryCode) 
    {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if either 
        // of the terms dwc:country or dwc:countryCode are EMPTY; COMPLIANT 
        // if the value of the country code determined from the value 
        // of dwc:country is equal to the value of dwc:countryCode; 
        // otherwise NOT_COMPLIANT 
        // bdq:sourceAuthority is "ISO 3166-1-alpha-2" 
        // [https://restcountries.eu/#api-endpoints-list-of-codes, 
        // https://www.iso.org/obp/ui/#search] 
        
        // Notes
        // The country code determination service should be able to match the name of a country 
        // in the original language. This test will fail if there is leading or trailing 
        // whitespace or there are leading or trailing non-printing characters.
        
        if (GEOUtil.isEmpty(countryCode)) { 
        	result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        	result.addComment("the provided value for dwc:countryCode is empty");
        } else if (GEOUtil.isEmpty(country)) { 
        	result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        	result.addComment("the provided value for dwc:country is empty");
        } else { 
        	String foundName = CountryLookup.lookupCountryFromCode(countryCode);
        	if (foundName==null) { 
        		result.setResultState(ResultState.RUN_HAS_RESULT);
        		result.setValue(ComplianceValue.NOT_COMPLIANT);
        		result.addComment("Provided value for dwc:countryCode ["+countryCode+"] did not match a ISO 3166-1-alpha-2 country code");
        	} else { 
        		if (foundName.equals(country)) { 
        			result.setResultState(ResultState.RUN_HAS_RESULT);
        			result.setValue(ComplianceValue.COMPLIANT);
        			result.addComment("Provided value for dwc:countryCode ["+countryCode+"] is the ISO 3166-1-alpha-2 country code for the provided dwc:country ["+country+"].");
        		} else { 
        			GettyLookup getty = new GettyLookup();
        			try { 
        				List<String> names = getty.getNamesForCountry(country);
        				boolean found = false;
        				Iterator<String> i = names.iterator();
        				while (i.hasNext() && !found) { 
        					if (foundName.equals(i.next())) { 
        						found = true;
        						result.setResultState(ResultState.RUN_HAS_RESULT);
        						result.setValue(ComplianceValue.COMPLIANT);
        						result.addComment("Provided value for dwc:countryCode ["+countryCode+"] is the ISO 3166-1-alpha-2 country code for the provided dwc:country ["+country+"].");
        					}
        				}
        				if (!found) { 
        					result.setResultState(ResultState.RUN_HAS_RESULT);
        					result.setValue(ComplianceValue.NOT_COMPLIANT);
        					result.addComment("Provided value for dwc:countryCode ["+countryCode+"] is not matched to the provided dwc:country ["+ country +"].");
        				}
        			} catch (GeorefServiceException e) { 
        				logger.debug(e.getMessage());
        				result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);
        				result.addComment("Error looking up alternatives for country name: " + e.getMessage() );
        			}
        		}
        	}
         }

        return result;
    }

    /**
	 * Propose amendment(s) to the values of dwc:minimumElevationInMeters and
	 * dwc:maximumElevationInMeters if they can be interpreted from
	 * dwc:verbatimElevation.
	 * 
	 * #68 Amendment SingleRecord Completeness: minelevation-maxelevation from
	 * verbatim
	 *
	 * Provides: 68 AMENDMENT_MINELEVATION-MAXELEVATION_FROM_VERBATIM
	 * Version: 2024-07-31
	 *
	 * @param minimumElevationInMeters the provided dwc:minimumElevationInMeters to
	 *                                 evaluate
	 * @param maximumElevationInMeters the provided dwc:maximumElevationInMeters to
	 *                                 evaluate
	 * @param verbatimElevation        the provided dwc:verbatimElevation to
	 *                                 evaluate
	 * @return DQResponse the response of type AmendmentValue to return
	 */
    
    @Validation(label="AMENDMENT_MINELEVATION-MAXELEVATION_FROM_VERBATIM", description="Propose amendment(s) to the values of dwc:minimumElevationInMeters and dwc:maximumElevationInMeters if they can be interpreted from dwc:verbatimElevation.")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if dwc:minimumElevationInMeters and dwc:maximumElevationInMeters are EMPTY and either dwc:verbatimElevation is EMPTY or the value is not unambiguously interpretable; FILLED_IN the values of dwc:minimumElevationInMeters and dwc:maximumElevationInMeters if they are EMPTY and could be unambiguously interpreted from dwc:verbatimElevation; otherwise NOT_AMENDED")
    @Provides("2d638c8b-4c62-44a0-a14d-fa147bf9823d")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/2d638c8b-4c62-44a0-a14d-fa147bf9823d/2024-07-31")
    public static DQResponse<AmendmentValue> amendmentMinelevationMaxelevationFromVerbatim(
    		@ActedUpon("dwc:verbatimElevation") String verbatimElevation,
    		@ActedUpon("dwc:maximumElevationInMeters") String maximumElevationInMeters, 
    		@ActedUpon("dwc:minimumElevationInMeters") String minimumElevationInMeters
    	) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        // Specification
		// INTERNAL_PREREQUISITES_NOT_MET if dwc:minimumElevationInMeters and
		// dwc:maximumElevationInMeters are EMPTY and either dwc:verbatimElevation is
		// EMPTY or the value is not unambiguously interpretable; FILLED_IN the values
		// of dwc:minimumElevationInMeters and dwc:maximumElevationInMeters if they are
		// EMPTY and could be unambiguously interpreted from dwc:verbatimElevation;
		// otherwise NOT_AMENDED

        if (GEOUtil.isEmpty(verbatimElevation) && GEOUtil.isEmpty(maximumElevationInMeters) && GEOUtil.isEmpty(minimumElevationInMeters)) { 
        	result.addComment("No Value provided for dwc:verbatimElevation");
        	result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        } else {
        	if (GEOUtil.isEmpty(maximumElevationInMeters) && GEOUtil.isEmpty(minimumElevationInMeters)) { 
        		// M is ambiguous, probably not meters, likely miles, but not interpreted.
        		logger.debug(verbatimElevation);
        		if (verbatimElevation.matches("^[0-9]+([.]{0,1}[0-9]*){0,1} *(m|m[.]|[mM](eter(s){0,1}))$")) {
        			// Meters, single value e.g. 51 m
        			String cleaned = verbatimElevation.replaceAll("[ Mmetrs]+", "").trim();
        			cleaned = cleaned.replaceAll("[.]$","");
        			result.addComment("Interpreted equal minimum and maximum elevations in meters from dwc:verbatimElevation ["+ verbatimElevation +"] interpreted as a elevation range in meters ");
        			Map<String, String> values = new HashMap<>();
        			values.put("dwc:minimumElevationInMeters", cleaned);        		
        			values.put("dwc:maximumElevationInMeters", cleaned);
        			result.setValue(new AmendmentValue(values));
        			result.setResultState(ResultState.FILLED_IN);
        		} else if (verbatimElevation.matches("^[0-9]+([.]{0,1}[0-9]*){0,1}[ to-]{0,4}[0-9]+([.]{0,1}[0-9]*){0,1} *(m|m[.]|[mM](eter(s){0,1}))$")) { 
        			//Meters, range, e.g. 1-2m  1.1 to 2.2 m
        			String cleaned = verbatimElevation.replaceAll(" ", "");
        			cleaned = cleaned.replace("to","-");
        			cleaned = cleaned.replaceAll("[mMetrs]","");
        			cleaned = cleaned.replaceAll("[.]$","");
        			String[] bits = cleaned.split("-");
        			result.addComment("Interpreted minimum and maximum elevations in meters from dwc:verbatimElevation ["+ verbatimElevation +"] interpreted as a elevation range in meters ");
        			float min = Float.parseFloat(bits[0]);
        			float max = min;
        			if (bits.length>1) { 
        				max = Float.parseFloat(bits[1]);
        			}
        			Map<String, String> values = new HashMap<>();
        			if (bits.length==1) { 
        				values.put("dwc:minimumElevationInMeters", bits[0]);        		
        				values.put("dwc:maximumElevationInMeters", bits[0]);
        			} else if (min<max) { 
        				values.put("dwc:minimumElevationInMeters", bits[0]);        		
        				values.put("dwc:maximumElevationInMeters", bits[1]);
        			} else { 
        				values.put("dwc:minimumElevationInMeters", bits[1]);        		
        				values.put("dwc:maximumElevationInMeters", bits[0]);
        			}
        			result.setValue(new AmendmentValue(values));
        			result.setResultState(ResultState.FILLED_IN);
        		} else if (verbatimElevation.matches("^[0-9]+([.]{0,1}[0-9]*){0,1} *([fF]eet|[Ff]oot|[fF]t[.]{0,1}){1}$")) { 
        			// Feet, single value, e.g. 15 ft
        			String cleaned = verbatimElevation.replaceAll("[ fFote]+", "").trim();
        			cleaned = cleaned.replaceAll("[.]$","");
        			result.addComment("Interpreted equal minimum and maximum elevations in meters from dwc:verbatimElevation ["+ verbatimElevation +"] interpreted as a elevation in feet ");
        			cleaned = cleaned.replaceAll("[^0-9.]", "");
        			double min = Double.parseDouble(cleaned);
        			min = min * GEOUtil.FEET_TO_METERS;
        			Map<String, String> values = new HashMap<>();
        			values.put("dwc:minimumElevationInMeters", Double.toString(min));        		
        			values.put("dwc:maximumElevationInMeters", Double.toString(min));
        			result.setValue(new AmendmentValue(values));
        			result.setResultState(ResultState.FILLED_IN);
        		} else if (verbatimElevation.matches("^[0-9]+([.]{0,1}[0-9]*){0,1}[ to-]{0,4}[0-9]+([.]{0,1}[0-9]*){0,1} *([fF]eet|[Ff]oot|[fF]t[.]{0,1}){1}$")) { 
        			//Feet, ranges, e.g. 1.1 to 2.2 ft.
        			String cleaned = verbatimElevation.replaceAll(" ", "");
        			cleaned = cleaned.replace("to","-");
        			cleaned = cleaned.replaceAll("[fFote]","");
        			cleaned = cleaned.replaceAll("[.]$","");
        			String[] bits = cleaned.split("-");
        			result.addComment("Interpreted minimum and maximum elevations in meters from dwc:verbatimElevation ["+ verbatimElevation +"] interpreted as a elevation range in fathoms ");
        			double min = Double.parseDouble(bits[0]);
        			double max = min;
        			if (bits.length>1) { 
        				max = Double.parseDouble(bits[1]);
        			}
        			Map<String, String> values = new HashMap<>();
        			if (bits.length==1) { 
        				String meters = Double.toString(min * GEOUtil.FEET_TO_METERS);
        				values.put("dwc:minimumElevationInMeters", meters);        		
        				values.put("dwc:maximumElevationInMeters", meters);
        			} else if (min<max) { 
        				String minMeters = Double.toString(min * GEOUtil.FEET_TO_METERS);
        				String maxMeters = Double.toString(max * GEOUtil.FEET_TO_METERS);
        				values.put("dwc:minimumElevationInMeters", minMeters);        		
        				values.put("dwc:maximumElevationInMeters", maxMeters);
        			} else { 
        				String minMeters = Double.toString(min * GEOUtil.FEET_TO_METERS);
        				String maxMeters = Double.toString(max * GEOUtil.FEET_TO_METERS);
        				values.put("dwc:minimumElevationInMeters", maxMeters);        		
        				values.put("dwc:maximumElevationInMeters", minMeters);
        			}
        			result.setValue(new AmendmentValue(values));
        			result.setResultState(ResultState.FILLED_IN);
        		} else if (verbatimElevation.matches("^[0-9]+([.]{0,1}[0-9]*){0,1} *([yY]ards|[Yy]ard|[yY]d[.]{0,1}}[yY]ds[.]{0,1}){1}$")) {
        			// Yards, single value, e.g. 15 yd
        			String cleaned = verbatimElevation.replaceAll("[ Yyards]+", "").trim();
        			cleaned = cleaned.replaceAll("[.]$","");
        			result.addComment("Interpreted equal minimum and maximum elevations in meters from dwc:verbatimElevation ["+ verbatimElevation +"] interpreted as a elevation in yards ");
        			cleaned = cleaned.replaceAll("[^0-9.]", "");
        			double min = Double.parseDouble(cleaned);
        			min = min * GEOUtil.YARDS_TO_METERS;
        			Map<String, String> values = new HashMap<>();
        			values.put("dwc:minimumElevationInMeters", Double.toString(min));        		
        			values.put("dwc:maximumElevationInMeters", Double.toString(min));
        			result.setValue(new AmendmentValue(values));
        			result.setResultState(ResultState.FILLED_IN);
        		} else if (verbatimElevation.matches("^[0-9]+([.]{0,1}[0-9]*){0,1}[ to-]{0,4}[0-9]+([.]{0,1}[0-9]*){0,1} *([yY]ards|[Yy]ard|[yY]d[.]{0,1}}[yY]ds[.]{0,1}){1}$")) { 
        			//Yards, range e.g. 1000-1500 yd
        			String cleaned = verbatimElevation.replaceAll(" ", "");
        			cleaned = cleaned.replace("to","-");
        			cleaned = cleaned.replaceAll("[ Yyards]","");
        			cleaned = cleaned.replaceAll("[.]$","");
        			String[] bits = cleaned.split("-");
        			result.addComment("Interpreted minimum and maximum elevations in meters from dwc:verbatimElevation ["+ verbatimElevation +"] interpreted as a elevation range in yards ");
        			double min = Double.parseDouble(bits[0]);
        			double max = min;
        			if (bits.length>1) { 
        				max = Double.parseDouble(bits[1]);
        			}
        			Map<String, String> values = new HashMap<>();
        			if (bits.length==1) { 
        				String meters = Double.toString(min * GEOUtil.YARDS_TO_METERS);
        				values.put("dwc:minimumElevationInMeters", meters);        		
        				values.put("dwc:maximumElevationInMeters", meters);
        			} else if (min<max) { 
        				String minMeters = Double.toString(min * GEOUtil.YARDS_TO_METERS);
        				String maxMeters = Double.toString(max * GEOUtil.YARDS_TO_METERS);
        				values.put("dwc:minimumElevationInMeters", minMeters);        		
        				values.put("dwc:maximumElevationInMeters", maxMeters);
        			} else { 
        				String minMeters = Double.toString(min * GEOUtil.YARDS_TO_METERS);
        				String maxMeters = Double.toString(max * GEOUtil.YARDS_TO_METERS);
        				values.put("dwc:minimumElevationInMeters", maxMeters);        		
        				values.put("dwc:maximumElevationInMeters", minMeters);
        			}
        			result.setValue(new AmendmentValue(values));
        			result.setResultState(ResultState.FILLED_IN); 
        		} else if (verbatimElevation.matches("^[0-9]+([.]{0,1}[0-9]*){0,1} *([mM]iles|[Mm]ile|[Mm]i[.]{0,1}}){1}$")) {
        			// Miles, single value, e.g. 15 miles
        			String cleaned = verbatimElevation.replaceAll("[ mMiles]+", "").trim();
        			cleaned = cleaned.replaceAll("[.]$","");
        			result.addComment("Interpreted equal minimum and maximum elevations in meters from dwc:verbatimElevation ["+ verbatimElevation +"] interpreted as a elevation in yards ");
        			cleaned = cleaned.replaceAll("[^0-9.]", "");
        			double min = Double.parseDouble(cleaned);
        			min = min * GEOUtil.MILES_TO_METERS;
        			Map<String, String> values = new HashMap<>();
        			values.put("dwc:minimumElevationInMeters", Double.toString(min));        		
        			values.put("dwc:maximumElevationInMeters", Double.toString(min));
        			result.setValue(new AmendmentValue(values));
        			result.setResultState(ResultState.FILLED_IN);
        		} else if (verbatimElevation.matches("^[0-9]+([.]{0,1}[0-9]*){0,1}[ to-]{0,4}[0-9]+([.]{0,1}[0-9]*){0,1} *([mM]iles|[Mm]ile|[Mm]i[.]{0,1}){1}$")) { 
        			//Miles, range e.g. 1000-1500 mi
        			String cleaned = verbatimElevation.replaceAll(" ", "");
        			cleaned = cleaned.replace("to","-");
        			cleaned = cleaned.replaceAll("[ mMiles]","");
        			cleaned = cleaned.replaceAll("[.]$","");
        			String[] bits = cleaned.split("-");
        			result.addComment("Interpreted minimum and maximum elevations in meters from dwc:verbatimElevation ["+ verbatimElevation +"] interpreted as a elevation range in miles ");
        			double min = Double.parseDouble(bits[0]);
        			double max = min;
        			if (bits.length>1) { 
        				max = Double.parseDouble(bits[1]);
        			}
        			Map<String, String> values = new HashMap<>();
        			if (bits.length==1) { 
        				String meters = Double.toString(min * GEOUtil.MILES_TO_METERS);
        				values.put("dwc:minimumElevationInMeters", meters);        		
        				values.put("dwc:maximumElevationInMeters", meters);
        			} else if (min<max) { 
        				String minMeters = Double.toString(min * GEOUtil.MILES_TO_METERS);
        				String maxMeters = Double.toString(max * GEOUtil.MILES_TO_METERS);
        				values.put("dwc:minimumElevationInMeters", minMeters);        		
        				values.put("dwc:maximumElevationInMeters", maxMeters);
        			} else { 
        				String minMeters = Double.toString(min * GEOUtil.MILES_TO_METERS);
        				String maxMeters = Double.toString(max * GEOUtil.MILES_TO_METERS);
        				values.put("dwc:minimumElevationInMeters", maxMeters);        		
        				values.put("dwc:maximumElevationInMeters", minMeters);
        			}
        			result.setValue(new AmendmentValue(values));
        			result.setResultState(ResultState.FILLED_IN);        			
        		}  else { 
        			result.addComment("Unable to Interpret provided dwc:verbatimElevation ["+ verbatimElevation +"].");
        			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        		}
        		
        		
        	} else { 
        		result.addComment("At least one of dwc:minimumElevationInMeters and dwc:maximumElevationInMeters contains a value.");
        		result.setResultState(ResultState.NOT_AMENDED);
        	}
        }
        
        return result;
    }


    /**
     * Propose amendment to the value of dwc:countryCode if dwc:decimalLatitude and dwc:decimalLongitude fall within a boundary from the bdq:sourceAuthority that is attributable to a single valid country code.
     *
     * #73 Amendment SingleRecord Completeness: countrycode from coordinates
     *
     * Provides: AMENDMENT_COUNTRYCODE_FROM_COORDINATES
     * Version: 2022-05-02
     *
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate
     * @param geodeticDatum the provided dwc:geodeticDatum to evaluate
     * @param countryCode the provided dwc:countryCode to evaluate
     * @param coordinatePrecision the provided dwc:coordinatePrecision to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Amendment(label="AMENDMENT_COUNTRYCODE_FROM_COORDINATES", description="Propose amendment to the value of dwc:countryCode if dwc:decimalLatitude and dwc:decimalLongitude fall within a boundary from the bdq:sourceAuthority that is attributable to a single valid country code.")
    @Provides("8c5fe9c9-4ba9-49ef-b15a-9ccd0424e6ae")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/8c5fe9c9-4ba9-49ef-b15a-9ccd0424e6ae/2022-05-02")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority[countryshapes] is not available; INTERNAL_PREREQUISITES_NOT_MET if either dwc:decimalLatitude or dwc:decimalLongitude is EMPTY or uninterpretable, or if dwc:countryCode is NOT_EMPTY; FILLED_IN dwc:countryCode if dwc:decimalLatitude and dwc:decimalLongitude fall within a boundary from the bdq:sourceAuthority[countryshapes] that is attributable to a single valid country code; otherwise NOT_AMENDED. bdq:sourceAuthority default = 'ADM1 boundaries' [https://gadm.org] UNION with 'EEZs' [https://marineregions.org],bdq:sourceAuthority[countryCode] is 'ISO 3166 country codes' [https://www.iso.org/iso-3166-country-codes.html]")
    public static DQResponse<AmendmentValue> amendmentCountrycodeFromCoordinates(
    		@Consulted("dwc:decimalLatitude") String decimalLatitude, 
    		@Consulted("dwc:decimalLongitude") String decimalLongitude, 
    		@Consulted("dwc:geodeticDatum") String geodeticDatum, 
    		@ActedUpon("dwc:countryCode") String countryCode, 
    		@Consulted("dwc:coordinatePrecision") String coordinatePrecision) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority[countryshapes] 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if either 
        // dwc:decimalLatitude or dwc:decimalLongitude is EMPTY or 
        // uninterpretable, or if dwc:countryCode is NOT_EMPTY; FILLED_IN 
        // dwc:countryCode if dwc:decimalLatitude and dwc:decimalLongitude 
        // fall within a boundary from the bdq:sourceAuthority[countryshapes] 
        // that is attributable to a single valid country code; otherwise 
        // NOT_AMENDED. 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority default = "ADM1 boundaries" 
        // [https://gadm.org] UNION with "EEZs" [https://marineregions.org],bdq:sourceAuthority[countryCode] 
        // is "ISO 3166 country codes" [https://www.iso.org/iso-3166-country-codes.html] 

        if (!GEOUtil.isEmpty(countryCode)) {
        	result.addComment("Not altering existing countryCode value.");
        	result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        } else if (GEOUtil.isEmpty(decimalLatitude)) { 
        	result.addComment("No value supplied for dwc:decimalLatitude, unable to propose a dwc:countryCode .");
        	result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        } else if (GEOUtil.isEmpty(decimalLongitude)) {
        	result.addComment("No value supplied for dwc:decimalLongitude, unable to propose a dwc:countryCode .");
        	result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        } else { 
        	
        	// TODO: Parameter
        	//551 #73 Fail got FILLED_IN expected EXTERNAL_PREREQUISITES_NOT_MET Propose filling in empty countryCode with value [AU] which contains the coordinate specified by dwc:decimalLatitude [-25.23], dwc:decimalLongitude [135.43].
        	// TODO: uninterpretable lat/long
        	//559 #73 Fail got NOT_AMENDED expected INTERNAL_PREREQUISITES_NOT_MET No unique dwc:contryCode found containing the coordinate specified by dwc:decimalLatitude [x], dwc:decimalLongitude [135.87].
        	//562 #73 Fail got NOT_AMENDED expected INTERNAL_PREREQUISITES_NOT_MET No unique dwc:contryCode found containing the coordinate specified by dwc:decimalLatitude [-25.23], dwc:decimalLongitude [x].
        	
        	
        	String countryCode3 = GEOUtil.getCountryForPoint(decimalLatitude, decimalLongitude);
        	if (countryCode3== null) { 
        		result.addComment("No unique dwc:contryCode found containing the coordinate specified by dwc:decimalLatitude ["+decimalLatitude+"], dwc:decimalLongitude ["+decimalLongitude+"].");
        		result.setResultState(ResultState.NOT_AMENDED);
        	} else { 
        		String countryCode2 = CountryLookup.lookupCode2FromCodeName(countryCode3);
        		if (countryCode2==null) {  
        			result.addComment("Error finding dwc:contryCode in ISO 2 letter form from 3 letter form ["+countryCode3+"] found containing the coordinate specified by dwc:decimalLatitude ["+decimalLatitude+"], dwc:decimalLongitude ["+decimalLongitude+"].");
        			result.setResultState(ResultState.NOT_AMENDED);
        		} else { 
        			result.addComment("Propose filling in empty countryCode with value ["+countryCode2+"] which contains the coordinate specified by dwc:decimalLatitude ["+decimalLatitude+"], dwc:decimalLongitude ["+decimalLongitude+"].");
        			result.setResultState(ResultState.FILLED_IN);
        			Map<String, String> values = new HashMap<>();
        			values.put("dwc:countryCode", countryCode2) ;
        			result.setValue(new AmendmentValue(values));
        		}
        	}
        }
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
     * Version: 2023-06-20
     *
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_COORDINATES_NOTZERO", description="Are the values of either dwc:decimalLatitude or dwc:decimalLongitude numbers that are not equal to 0?")
    @Provides("1bf0e210-6792-4128-b8cc-ab6828aa4871")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/1bf0e210-6792-4128-b8cc-ab6828aa4871/2023-06-20")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if dwc:decimalLatitude is EMPTY or is not interpretable as a number, or dwc:decimalLongitude is EMPTY or is not interpretable as a number; COMPLIANT if either the value of dwc:decimalLatitude is not = 0 or the value of dwc:decimalLongitude is not = 0; otherwise NOT_COMPLIANT ")
    public static DQResponse<ComplianceValue> validationCoordinatesNotzero(
    		@ActedUpon("dwc:decimalLatitude") String decimalLatitude, 
    		@ActedUpon("dwc:decimalLongitude") String decimalLongitude) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:decimalLatitude is 
        // EMPTY or is not interpretable as a number, or dwc:decimalLongitude 
        // is EMPTY or is not interpretable as a number; COMPLIANT 
        // if either the value of dwc:decimalLatitude is not = 0 or 
        // the value of dwc:decimalLongitude is not = 0; otherwise 
        // NOT_COMPLIANT 
        
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
    		result.addComment("provided value for dwc:decimalLatitude ["+decimalLatitude+"] contains non-numeric characters.");
    	} else if (!GEOUtil.isNumericCharacters(decimalLongitude)) { 
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("provided value for dwc:decimalLongitude ["+decimalLongitude+"] contains non-numeric characters.");
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
     * If dwc:coordinateUncertaintyInMeters is not empty and there are not empty values for dwc:latitude and dwc:longitude,
     * amend dwc:coordinateUncertaintyInMeters by adding a maximum datum shift.
     *
     * Provides: 102 AMENDMENT_GEODETICDATUM_ASSUMEDDEFAULT
     * Version: 2023-06-23
     *
     * @param geodeticDatum the provided dwc:geodeticDatum to evaluate
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate
     * @param defaultGeodeticDatum to use as default, if not specified, uses EPSG:4326
     * @return DQResponse the response of type AmendmentValue to return
     * @param coordinateUncertaintyInMeters a {@link java.lang.String} object.
     */
    @Amendment(label="AMENDMENT_GEODETICDATUM_ASSUMEDDEFAULT", description="Propose amendment to dwc:geodeticDatum using the value of bdq:defaultGeodeticDatum if dwc:geodeticDatum is empty. If dwc:coordinateUncertaintyInMeters is not empty and there are not empty values for dwc:latitude and dwc:longitude, amend dwc:coordinateUncertaintyInMeters by adding a maximum datum shift.")
    @Provides("7498ca76-c4d4-42e2-8103-acacccbdffa7")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/7498ca76-c4d4-42e2-8103-acacccbdffa7/2023-06-23")
    @Specification("If dwc:geodeticDatum is EMPTY, fill in the value of dwc:geodeticDatum with the value of bdq:defaultGeodeticDatum, report FILLED_IN and, if dwc:coordinateUncertaintyInMeters, dwc:decimalLatitude and dwc:decimalLongitude are NOT_EMPTY, amend the value of dwc:coordinateUncertaintyInMeters by adding the maximum datum shift between the specified bdq:defaultGeodeticDatum and any other datum at the provided dwc:decimalLatitude and dwc:decimalLongitude and instead report AMENDED; otherwise NOT_AMENDED. bdq:defaultGeodeticDatum default='EPSG:4326'")
    public static DQResponse<AmendmentValue> amendmentGeodeticdatumAssumeddefault(
    		@ActedUpon("dwc:coordinateUncertaintyInMeters") String coordinateUncertaintyInMeters, 
    		@ActedUpon("dwc:geodeticDatum") String geodeticDatum,
    		@ActedUpon("dwc:decimalLatitude") String decimalLatitude,
    		@ActedUpon("dwc:decimalLongitude") String decimalLongitude,
    		@Parameter(name="bdq:defaultGeodeticDatum") String defaultGeodeticDatum) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        // Specification
        // If dwc:geodeticDatum is EMPTY, fill in the value of dwc:geodeticDatum 
        // with the value of bdq:defaultGeodeticDatum, report FILLED_IN 
        // and, if dwc:coordinateUncertaintyInMeters, dwc:decimalLatitude 
        // and dwc:decimalLongitude are NOT_EMPTY, amend the value 
        // of dwc:coordinateUncertaintyInMeters by adding the maximum 
        // datum shift between the specified bdq:defaultGeodeticDatum 
        // and any other datum at the provided dwc:decimalLatitude 
        // and dwc:decimalLongitude and instead report AMENDED; otherwise 
        // NOT_AMENDED. 
        // 

        // Parameters. This test is defined as parameterized.
        // bdq:defaultGeodeticDatum default="EPSG:4326" 

        if (GEOUtil.isEmpty(defaultGeodeticDatum)) {
        	defaultGeodeticDatum = "EPSG:4326";
        }
        
        if (GEOUtil.isEmpty(geodeticDatum)) { 
        	result.addComment("Propose filling in empty geodepticDatum with the default value EPSG:4326 (2D Lat/Long Unit=degrees, CRS=WGS84)");
        	result.setResultState(ResultState.FILLED_IN);
    		Map<String, String> values = new HashMap<>();
    		values.put("dwc:geodeticDatum", defaultGeodeticDatum) ;
    		result.setValue(new AmendmentValue(values));
    		if (!GEOUtil.isEmpty(coordinateUncertaintyInMeters) && !GEOUtil.isEmpty(decimalLatitude) && !GEOUtil.isEmpty(decimalLongitude)) {
    			// ... and dwc:decimalLongitude are NOT_EMPTY, amend the value 
    			// of dwc:coordinateUncertaintyInMeters by adding the maximum 
    			// datum shift between the specified bdq:defaultGeodeticDatum 
    			// and any other datum at the provided dwc:decimalLatitude 
    			// and dwc:decimalLongitude and instead report AMENDED; 
    			Integer existingUncertainty = ((Double)Math.ceil(Double.parseDouble(coordinateUncertaintyInMeters))).intValue();
    			Double latVal = Double.parseDouble(decimalLatitude);
    			Double longVal = Double.parseDouble(decimalLongitude);
    			Integer addedUncertainty = UnknownToWGS84Error.getErrorAtCoordinate(latVal, longVal);
    			Integer totalUncertainty = existingUncertainty + addedUncertainty;
        		result.addComment("Adding an uncertainty of ["+ addedUncertainty.toString() +"] meters to existing dwc coordinateUncertaintyInMeters of ["+coordinateUncertaintyInMeters+"] meters to account for the difference between any unknown datum and the assigned value of dwc:geodeticDatim of WGS84 for the provided cooordinates.");
        		result.setResultState(ResultState.AMENDED);
        		values.put("dwc:coordinateUncertaintyInMeters", totalUncertainty.toString());
        		result.setValue(new AmendmentValue(values));
    		}
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
     * @param minimumValidDepthInMeters a {@link java.lang.Double} object.
     * @param maximumValidDepthInMeters a {@link java.lang.Double} object.
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
     * Version: 2022-03-22
     *
     * @param coordinateUncertaintyInMeters the provided dwc:coordinateUncertaintyInMeters to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_COORDINATEUNCERTAINTY_INRANGE", description="Is the value of dwc:coordinateUncertaintyInMeters a number between 1 and 20,037,509?")
    @Provides("c6adf2ea-3051-4498-97f4-4b2f8a105f57")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/c6adf2ea-3051-4498-97f4-4b2f8a105f57/2022-03-22")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if dwc:coordinateUncertaintyInMeters is EMPTY; COMPLIANT if the value of  dwc:coordinateUncertaintyInMeters can be interpreted as a number between 1 and 20037509 inclusive; otherwise NOT_COMPLIANT ")
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
    public static DQResponse<ComplianceValue> validationDecimallatitudeNotempty(
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

    /**
     * Does the value of dwc:stateProvince occur in bdq:sourceAuthority?
     *
     * Provides: #199 VALIDATION_STATEPROVINCE_FOUND
     * Version: 2022-09-05
     *
     * @param stateProvince the provided dwc:stateProvince to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     * @param sourceAuthority a {@link java.lang.String} object.
     */
    @Validation(label="VALIDATION_STATEPROVINCE_FOUND", description="Does the value of dwc:stateProvince occur in bdq:sourceAuthority?")
    @Provides("4daa7986-d9b0-4dd5-ad17-2d7a771ea71a")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/4daa7986-d9b0-4dd5-ad17-2d7a771ea71a/2022-09-05")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:stateProvince is EMPTY; COMPLIANT if the value of dwc:stateProvince occurs as an administrative entity that is a child to at least one entity representing an ISO country-like entity in the bdq:sourceAuthority; otherwise NOT_COMPLIANT bdq:sourceAuthority default = 'The Getty Thesaurus of Geographic Names (TGN)' [https://www.getty.edu/research/tools/vocabularies/tgn/index.html]")
    public static DQResponse<ComplianceValue> validationStateprovinceFound(
    		@ActedUpon("dwc:stateProvince") String stateProvince,
    		@Parameter(name="bdq:sourceAuthority") String sourceAuthority
    		) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:stateProvince 
        // is EMPTY; COMPLIANT if the value of dwc:stateProvince occurs 
        // as an administrative entity that is a child to at least 
        // one entity representing an ISO country-like entity in the 
        // bdq:sourceAuthority; otherwise NOT_COMPLIANT
        // 

        // Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority
        // default = "The Getty Thesaurus of Geographic Names (TGN)" 
        // [https://www.getty.edu/research/tools/vocabularies/tgn/index.html] 

        if (sourceAuthority==null) { 
        	sourceAuthority = GettyLookup.GETTY_TGN;
        }

        if (GEOUtil.isEmpty(stateProvince)) { 
        	result.addComment("dwc:stateProvince is empty");
        	result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        } else { 
        	if (sourceAuthority.equalsIgnoreCase(GettyLookup.GETTY_TGN)) {
        		Boolean cached = GeoUtilSingleton.getInstance().getTgnPrimaryEntry(stateProvince);
        		if (cached!=null) { 
        			if (cached) {
        				result.addComment("the value provided for dwc:stateProvince [" + stateProvince + "] exists as a primary administrative divsion in the Getty Thesaurus of Geographic Names (TGN).");
        				result.setResultState(ResultState.RUN_HAS_RESULT);
        				result.setValue(ComplianceValue.COMPLIANT);
        			} else { 
        				result.addComment("the value provided for dwc:stateProvince [" + stateProvince + "] is not a primary administrative division in the Getty Thesaurus of Geographic Names (TGN).");
        				result.setResultState(ResultState.RUN_HAS_RESULT);
        				result.setValue(ComplianceValue.NOT_COMPLIANT);
        			}
        		} else { 
        			GettyLookup lookup = new GettyLookup();
        			if (lookup.lookupPrimary(stateProvince)==null) { 
        				result.addComment("Error looking up stateProvince in " + sourceAuthority);
        				result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);
        			} else if (lookup.lookupPrimary(stateProvince)) { 
        				result.addComment("the value provided for dwc:stateProvince [" + stateProvince + "] exists as a primary administrative divsion in the Getty Thesaurus of Geographic Names (TGN).");
        				result.addComment(lookup.getPrimaryObject(stateProvince).getParentageString());
        				result.setResultState(ResultState.RUN_HAS_RESULT);
        				result.setValue(ComplianceValue.COMPLIANT);
        				GeoUtilSingleton.getInstance().addTgnPrimary(stateProvince, true);
        			} else { 
        				result.addComment("the value provided for dwc:stateProvince [" + stateProvince + "] is not a primary administrative divsion in the Getty Thesaurus of Geographic Names (TGN).");
        				result.setResultState(ResultState.RUN_HAS_RESULT);
        				result.setValue(ComplianceValue.NOT_COMPLIANT);
        				GeoUtilSingleton.getInstance().addTgnPrimary(stateProvince, false);
        			}
        		}
        	} else { 
        		result.addComment("Unknown bdq:sourceAuthority");
        		result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);
        	}
        }
        return result;
    }
        

    /**
     *  Are the combination of the values of dwc:country, dwc:stateProvince consistent 
     *  with the values in the bdq:sourceAuthority?
     *
     * Provides: #200 VALIDATION_COUNTRYSTATEPROVINCE_CONSISTENT
     * Version: 2023-09-18
     *
     * @param country the provided dwc:country to evaluate
     * @param stateProvince the provided dwc:stateProvince to evaluate
     * @param sourceAuthority the sourceAuthority to consult
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_COUNTRYSTATEPROVINCE_CONSISTENT", description=" 	Are the combination of the values of dwc:country, dwc:stateProvince consistent with the values in the bdq:sourceAuthority?")
    @Provides("e654f562-44f8-43fd-983b-2aaba4c6dda9")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/e654f562-44f8-43fd-983b-2aaba4c6dda9/2023-09-18")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if the terms dwc:country or dwc:stateProvince are EMPTY; COMPLIANT if the value of dwc:stateProvince occurs as an administrative entity that is a child to the entity matching the value of dwc:country in the bdq:sourceAuthority, and the match to dwc:country is an ISO country-like entity in the bdq:sourceAuthority; otherwise NOT_COMPLIANT  	bdq:sourceAuthority default = \"The Getty Thesaurus of Geographic Names (TGN)\" {[https://www.getty.edu/research/tools/vocabularies/tgn/index.html]}")
    public static DQResponse<ComplianceValue> validationCountrystateprovinceConsistent(
    		@ActedUpon("dwc:country") String country, 
    		@ActedUpon("dwc:stateProvince") String stateProvince,
    		@Parameter(name="bdq:sourceAuthority") String sourceAuthority
    		) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
		// EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available;
		// INTERNAL_PREREQUISITES_NOT_MET if the terms dwc:country or dwc:stateProvince
		// are EMPTY; COMPLIANT if the value of dwc:stateProvince occurs as an
		// administrative entity that is a child to the entity matching the value of
		// dwc:country in the bdq:sourceAuthority, and the match to dwc:country is an
		// ISO country-like entity in the bdq:sourceAuthority; otherwise NOT_COMPLIANT

        // Parameters. This test is defined as parameterized.
		// bdq:sourceAuthority default = "The Getty Thesaurus of Geographic Names
		// (TGN)" {[https://www.getty.edu/research/tools/vocabularies/tgn/index.html]}

        if (GEOUtil.isEmpty(sourceAuthority)) {
        	sourceAuthority = "The Getty Thesaurus of Geographic Names (TGN)";
        }

        try {
        	GeoRefSourceAuthority sourceAuthorityObject = new GeoRefSourceAuthority(sourceAuthority);
        	if (sourceAuthorityObject.getAuthority().equals(EnumGeoRefSourceAuthority.INVALID)) {
        		throw new SourceAuthorityException("Invalid Source Authority");
        	}

        	if (GEOUtil.isEmpty(country) || GEOUtil.isEmpty(stateProvince)) { 
        		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        		if (GEOUtil.isEmpty(country)) { 
        			result.addComment("Provided dwc:country is empty.");
        		} 
        		if (GEOUtil.isEmpty(stateProvince)) { 
        			result.addComment("Provided dwc:stateProvince is empty.");
        		} 
        	} else { 

        		if (sourceAuthorityObject.getAuthority().equals(EnumGeoRefSourceAuthority.GETTY_TGN)) {
        			GettyLookup lookup = GeoUtilSingleton.getInstance().getGettyLookup();
        			String countryToLookup = country;
        			String preferredCountry =  lookup.getPreferredCountryName(country);
        			if (preferredCountry==null) { 
        				List<String> names = lookup.getNamesForCountry(country);
        				Iterator<String> i = names.iterator();
        				boolean found = false;
        				while (i.hasNext() && !found) {
        					String name = i.next();
        					preferredCountry = lookup.getPreferredCountryName(name);
        					if (preferredCountry!=null) { 
        						countryToLookup=preferredCountry;
        						logger.debug(preferredCountry);
        						found = true;
        					}
        				}
        			}
        			logger.debug(country);
        			logger.debug(countryToLookup);
        			if (lookup.lookupCountry(countryToLookup)) { 
        				logger.debug(stateProvince);
        				logger.debug(lookup.lookupPrimary(stateProvince));
        				if (lookup.lookupPrimary(stateProvince)) {  
        					if (preferredCountry==null) { 
        						preferredCountry = countryToLookup;
        					}
        					GettyTGNObject primaryObject = lookup.getPrimaryObject(stateProvince);
        					String primaryParentage = primaryObject.getParentageString();
        					logger.debug(primaryParentage);
        					if (primaryParentage==null) { 
        						result.setResultState(ResultState.RUN_HAS_RESULT);
        						result.setValue(ComplianceValue.NOT_COMPLIANT);
        						result.addComment("Parentage not found for dwc:stateProvince ["+stateProvince+"] in the Getty TGN");
        					} else { 
        						if (primaryParentage.contains(preferredCountry)) { 
        							result.setResultState(ResultState.RUN_HAS_RESULT);
        							result.setValue(ComplianceValue.COMPLIANT);
        							result.addComment("The dwc:country ["+country+"] as ["+preferredCountry+"] was found in the parentage ["+primaryParentage+"] of dwc:stateProvince ["+stateProvince+"] in the Getty TGN");
        						} else { 
        							result.setResultState(ResultState.RUN_HAS_RESULT);
        							result.setValue(ComplianceValue.NOT_COMPLIANT);
        							result.addComment("The dwc:country ["+country+"] as ["+preferredCountry+"] was not found in the parentage ["+primaryParentage+"] of dwc:stateProvince ["+stateProvince+"] in the Getty TGN");
        						}
        					}
        				} else { 
        					result.setResultState(ResultState.RUN_HAS_RESULT);
        					result.setValue(ComplianceValue.NOT_COMPLIANT);
        					result.addComment("Provided value of dwc:stateProvince ["+stateProvince+"] is not a subdivision of a nation level entity known to the Getty TGN");
        				}
        			} else { 
        				result.setResultState(ResultState.RUN_HAS_RESULT);
        				result.setValue(ComplianceValue.NOT_COMPLIANT);
        				result.addComment("Provided value of dwc:country ["+country+"] is not a nation level entity known to the Getty TGN");
        			}
        		} else { 
        			throw new SourceAuthorityException("Unsupported Source Authority");
        		}


        	}
        } catch (SourceAuthorityException e) {
        	result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);
        	result.addComment("Error with specified Source Authority: " + e.getMessage());
        } catch (GeorefServiceException e) {
        	result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);
        	result.addComment("Error with specified Source Authority: " + e.getMessage());
        }

		return result;
    }

    /**
     * Is the combination of the values of the terms dwc:country, dwc:stateProvince unique in the bdq:sourceAuthority?
     *
     * Provides: VALIDATION_COUNTRYSTATEPROVINCE_UNAMBIGUOUS
     * Version: 2023-09-18
     *
     * @param country the provided dwc:country to evaluate
     * @param stateProvince the provided dwc:stateProvince to evaluate
     * @param sourceAuthority the source authority to consult
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_COUNTRYSTATEPROVINCE_UNAMBIGUOUS", description="Is the combination of the values of the terms dwc:country, dwc:stateProvince unique in the bdq:sourceAuthority?")
    @Provides("d257eb98-27cb-48e5-8d3c-ab9fca4edd11")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/d257eb98-27cb-48e5-8d3c-ab9fca4edd11/2023-09-18")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if the terms dwc:country and dwc:stateProvince are EMPTY; COMPLIANT if the combination of values of dwc:country and dwc:stateProvince are unambiguously resolved to a single result with a child-parent relationship in the bdq:sourceAuthority and the entity matching the value of dwc:country in the bdq:sourceAuthority is an ISO country-like entity in the bdq:sourceAuthority; otherwise NOT_COMPLIANT bdq:sourceAuthority default = 'The Getty Thesaurus of Geographic Names (TGN)' [https://www.getty.edu/research/tools/vocabularies/tgn/index.html]")
    public static DQResponse<ComplianceValue> validationCountrystateprovinceUnambiguous(
    		@ActedUpon("dwc:country") String country, 
    		@ActedUpon("dwc:stateProvince") String stateProvince,
    		@Parameter(name="bdq:sourceAuthority") String sourceAuthority
    ) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
		// EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available;
		// INTERNAL_PREREQUISITES_NOT_MET if the terms dwc:country and dwc:stateProvince
		// are EMPTY; COMPLIANT if the combination of values of dwc:country and
		// dwc:stateProvince are unambiguously resolved to a single result with a
		// child-parent relationship in the bdq:sourceAuthority and the entity matching
		// the value of dwc:country in the bdq:sourceAuthority is an ISO country-like
		// entity in the bdq:sourceAuthority; otherwise NOT_COMPLIANT

        // Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority default = "The Getty Thesaurus of Geographic Names (TGN)" 
        // {[https://www.getty.edu/research/tools/vocabularies/tgn/index.html]}

        if (GEOUtil.isEmpty(sourceAuthority)) {
        	sourceAuthority = "The Getty Thesaurus of Geographic Names (TGN)";
        }

        try {
        	GeoRefSourceAuthority sourceAuthorityObject = new GeoRefSourceAuthority(sourceAuthority);
        	if (sourceAuthorityObject.getAuthority().equals(EnumGeoRefSourceAuthority.INVALID)) {
        		throw new SourceAuthorityException("Invalid Source Authority");
        	}

        	if (GEOUtil.isEmpty(country) && GEOUtil.isEmpty(stateProvince)) { 
        		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        		result.addComment("Provided dwc:country and dwc:stateProvince are both empty.");
        	} else { 

        		if (sourceAuthorityObject.getAuthority().equals(EnumGeoRefSourceAuthority.GETTY_TGN)) {
        			GettyLookup lookup = GeoUtilSingleton.getInstance().getGettyLookup();
        			String countryToLookup = country;
        			String preferredCountry =  lookup.getPreferredCountryName(country);
        			if (preferredCountry==null) { 
        				List<String> names = lookup.getNamesForCountry(country);
        				Iterator<String> i = names.iterator();
        				boolean found = false;
        				while (i.hasNext() && !found) {
        					String name = i.next();
        					preferredCountry = lookup.getPreferredCountryName(name);
        					if (preferredCountry!=null) { 
        						countryToLookup=preferredCountry;
        						logger.debug(preferredCountry);
        						found = true;
        					}
        				}
        			}
        			
        			
        			if (GEOUtil.isEmpty(country)) { 
        				// If country is empty, does stateProvince match a single entry? 
        				if (lookup.lookupPrimary(stateProvince)) {  
        					result.setResultState(ResultState.RUN_HAS_RESULT);
        					result.setValue(ComplianceValue.COMPLIANT);
        					result.addComment("Provided value of dwc:country is empty and dwc:stateProvince ["+stateProvince+"] matches a single primary division level entity known to the Getty TGN");
        				} else { 
        					result.setResultState(ResultState.RUN_HAS_RESULT);
        					result.setValue(ComplianceValue.NOT_COMPLIANT);
        					result.addComment("Provided value of dwc:country is empty and dwc:stateProvince ["+stateProvince+"] is not a unique match to a primary division level entity known to the Getty TGN");
        				}
        			} else if (GEOUtil.isEmpty(stateProvince)) { 
        				// If stateProvince is empty, does country match a single entity
        				if (lookup.lookupCountry(countryToLookup)) { 
        					result.setResultState(ResultState.RUN_HAS_RESULT);
        					result.setValue(ComplianceValue.COMPLIANT);
        					result.addComment("Provided value of dwc:stateProvince is empty and dwc:country ["+country+"] matches a single nation level entity known to the Getty TGN");
        				} else { 
        					result.setResultState(ResultState.RUN_HAS_RESULT);
        					result.setValue(ComplianceValue.NOT_COMPLIANT);
        					result.addComment("Provided value of dwc:stateProvince is empty and dwc:country ["+country+"] is not a unique match to a nation level entity known to the Getty TGN");
        				}
        			} else { 
        				logger.debug(country);
        				logger.debug(countryToLookup);
        				if (lookup.lookupCountry(countryToLookup)) { 
        					logger.debug(stateProvince);
        					logger.debug(lookup.lookupPrimary(stateProvince));
        					if (lookup.lookupPrimary(stateProvince)) {  
        						if (preferredCountry==null) { 
        							preferredCountry = countryToLookup;
        						}
        						GettyTGNObject primaryObject = lookup.getPrimaryObject(stateProvince);
        						String primaryParentage = primaryObject.getParentageString();
        						logger.debug(primaryParentage);
        						if (primaryParentage==null) { 
        							result.setResultState(ResultState.RUN_HAS_RESULT);
        							result.setValue(ComplianceValue.NOT_COMPLIANT);
        							result.addComment("Parentage not found for dwc:stateProvince ["+stateProvince+"] in the Getty TGN");
        						} else { 
        							if (primaryParentage.contains(preferredCountry)) { 
        								result.setResultState(ResultState.RUN_HAS_RESULT);
        								result.setValue(ComplianceValue.COMPLIANT);
        								result.addComment("The dwc:country ["+country+"] as ["+preferredCountry+"] was found in the parentage ["+primaryParentage+"] of dwc:stateProvince ["+stateProvince+"] in the Getty TGN");
        							} else { 
        								result.setResultState(ResultState.RUN_HAS_RESULT);
        								result.setValue(ComplianceValue.NOT_COMPLIANT);
        								result.addComment("The dwc:country ["+country+"] as ["+preferredCountry+"] was not found in the parentage ["+primaryParentage+"] of dwc:stateProvince ["+stateProvince+"] in the Getty TGN");
        							}
        						}
        					} else { 
        						result.setResultState(ResultState.RUN_HAS_RESULT);
        						result.setValue(ComplianceValue.NOT_COMPLIANT);
        						result.addComment("Provided value of dwc:stateProvince ["+stateProvince+"] is not a subdivision of a nation level entity known to the Getty TGN");
        					}
        				} else { 
        					result.setResultState(ResultState.RUN_HAS_RESULT);
        					result.setValue(ComplianceValue.NOT_COMPLIANT);
        					result.addComment("Provided value of dwc:country ["+country+"] is not a nation level entity known to the Getty TGN");
        				}
        			} 
        		} else { 
        			throw new SourceAuthorityException("Unsupported Source Authority");
        		}


        	}
        } catch (SourceAuthorityException e) {
        	result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);
        	result.addComment("Error with specified Source Authority: " + e.getMessage());
        } catch (GeorefServiceException e) {
        	result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);
        	result.addComment("Error with specified Source Authority: " + e.getMessage());
        }

        
        return result;
    }

    /**
    * Does the marine/non-marine biome of a taxon from the bdq:sourceAuthority match the biome at the location given by the coordinates?
    *
    * Provides: 51 VALIDATION_COORDINATES_TERRESTRIALMARINE
    * Version: 2024-04-15
    *
    * @param decimalLatitude the provided dwc:decimalLatitude to evaluate as ActedUpon.
    * @param decimalLongitude the provided dwc:decimalLongitude to evaluate as ActedUpon.
    * @param scientificName the provided dwc:scientificName to evaluate as Consulted.
    * @return DQResponse the response of type ComplianceValue  to return
    */
    @Validation(label="VALIDATION_COORDINATES_TERRESTRIALMARINE", description="Does the marine/non-marine biome of a taxon from the bdq:sourceAuthority match the biome at the location given by the coordinates?")
    @Provides("b9c184ce-a859-410c-9d12-71a338200380")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/b9c184ce-a859-410c-9d12-71a338200380/2024-04-15")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if either bdq:taxonomyIsMarine or bdq:geospatialLand are not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:dcientificName is EMPTY or the marine/non-marine status of the taxon is not interpretable from bdq:taxonomyIsMarine or the values of dwc:decimalLatitude or dwc:decimalLongitude are EMPTY; COMPLIANT if the taxon marine/non-marine status from bdq:taxonomyIsMarine matches the marine/non-marine status of dwc:decimalLatitude and dwc:decimalLongitude on the boundaries given by bdq:geospatialLand plus an exterior buffer given by bdq:spatialBufferInMeters; otherwise NOT_COMPLIANT bdq:taxonIsMarine default = 'World Register of Marine Species (WoRMS') {[https://www.marinespecies.org/]} {Web service [https://www.marinespecies.org/aphia.php?p=webservice]},{bdq:geospatialLand default = The spatial union of 'NaturalEarth 10m-physical-vectors for Land' [https://www.naturalearthdata.com/http//www.naturalearthdata.com/download/10m/physical/ne_10m_land.zip] and 'NaturalEarth Minor Islands' [https://www.naturalearthdata.com/http//www.naturalearthdata.com/download/10m/physical/ne_10m_minor_islands.zip]},bdq:spatialBufferInMeters default = '3000'")
    public DQResponse<ComplianceValue> validationCoordinatesTerrestrialmarine(
        @ActedUpon("dwc:decimalLatitude") String decimalLatitude, 
        @ActedUpon("dwc:decimalLongitude") String decimalLongitude, 
        @Consulted("dwc:scientificName") String scientificName
    ) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if either bdq:taxonomyIsMarine 
        // or bdq:geospatialLand are not available; INTERNAL_PREREQUISITES_NOT_MET 
        // if dwc:dcientificName is EMPTY or the marine/non-marine 
        // status of the taxon is not interpretable from bdq:taxonomyIsMarine 
        // or the values of dwc:decimalLatitude or dwc:decimalLongitude 
        // are EMPTY; COMPLIANT if the taxon marine/non-marine status 
        // from bdq:taxonomyIsMarine matches the marine/non-marine 
        // status of dwc:decimalLatitude and dwc:decimalLongitude on 
        // the boundaries given by bdq:geospatialLand plus an exterior 
        // buffer given by bdq:spatialBufferInMeters; otherwise NOT_COMPLIANT 
        // bdq:taxonIsMarine default = "World Register of Marine Species 
        // (WoRMS") {[https://www.marinespecies.org/]} {Web service 
        // [https://www.marinespecies.org/aphia.php?p=webservice]},{bdq:geospatialLand 
        // default = The spatial union of "NaturalEarth 10m-physical-vectors 
        // for Land" [https://www.naturalearthdata.com/http//www.naturalearthdata.com/download/10m/physical/ne_10m_land.zip] 
        // and "NaturalEarth Minor Islands" [https://www.naturalearthdata.com/http//www.naturalearthdata.com/download/10m/physical/ne_10m_minor_islands.zip]},bdq:spatialBufferInMeters 
        // default = "3000" 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:taxonIsMarine,bdq:geospatialLand,bdq:spatialBufferInMeters

        return result;
    }

    // TODO: Specification needs source authority to be added.
    /**
    * Propose amendment of the signs of dwc:decimalLatitude and/or dwc:decimalLongitude to align the location with the dwc:countryCode.
    *
    * Provides: 54 AMENDMENT_COORDINATES_TRANSPOSED
    * Version: 2023-09-17
    *
    * @param decimalLatitude the provided dwc:decimalLatitude to evaluate as ActedUpon.
    * @param decimalLongitude the provided dwc:decimalLongitude to evaluate as ActedUpon.
    * @param countryCode the provided dwc:countryCode to evaluate as Consulted.
    * @return DQResponse the response of type AmendmentValue to return
    */
    @Amendment(label="AMENDMENT_COORDINATES_TRANSPOSED", description="Propose amendment of the signs of dwc:decimalLatitude and/or dwc:decimalLongitude to align the location with the dwc:countryCode.")
    @Provides("f2b4a50a-6b2f-4930-b9df-da87b6a21082")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/f2b4a50a-6b2f-4930-b9df-da87b6a21082/2023-09-17")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if any of dwc:decimalLatitude or dwc:decimalLongitude or dwc:countryCode are EMPTY; AMENDED dwc:decimalLatitude and dwc:decimalLongitude if the coordinates were transposed or one or more of the signs of the coordinates were reversed to align the location with dwc:countryCode; otherwise NOT_AMENDED ")
    public static DQResponse<AmendmentValue> amendmentCoordinatesTransposed(
        @ActedUpon("dwc:decimalLatitude") String decimalLatitude, 
        @ActedUpon("dwc:decimalLongitude") String decimalLongitude, 
        @Consulted("dwc:countryCode") String countryCode,
    	@Parameter(name="bdq:sourceAuthority") String sourceAuthority
    ) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if any of dwc:decimalLatitude 
        // or dwc:decimalLongitude or dwc:countryCode are EMPTY; AMENDED 
        // dwc:decimalLatitude and dwc:decimalLongitude if the coordinates 
        // were transposed or one or more of the signs of the coordinates 
        // were reversed to align the location with dwc:countryCode; 
        // otherwise NOT_AMENDED 

        if (GEOUtil.isEmpty(sourceAuthority)) { 
        	// TODO: Value needs to be set in the issue
        	sourceAuthority = "ADM1 boundaries UNION EEZ";
        }

        try { 
        	GeoRefSourceAuthority sourceAuthorityObject = new GeoRefSourceAuthority(sourceAuthority);
        	if (sourceAuthorityObject.getAuthority().equals(EnumGeoRefSourceAuthority.INVALID)) { 
        		throw new SourceAuthorityException("Invalid Source Authority");
        	}

        	if (GEOUtil.isEmpty(decimalLatitude)) { 
        		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        		result.addComment("The value provided for dwc:decimalLatitude is empty");
        	} else if (GEOUtil.isEmpty(decimalLongitude)) { 
        		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        		result.addComment("The value provided for dwc:decimalLongitude is empty");
        	} else if (GEOUtil.isEmpty(countryCode)) { 
        		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        		result.addComment("The value provided for dwc:countryCode is empty");
        	} else { 
        		if (!sourceAuthority.equals("ADM1 boundaries UNION EEZ")) { 
        			result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);
        			result.addComment("Unsupported or unrecognized source authority.");
        		} else { 
        			String countryCode3 = countryCode;
        			if (!countryCode.matches("^[A-Z]$")) {
        				// expected case, two letter country code, find the three letter code used in the datasets.
        				countryCode3 = CountryLookup.lookupCode3FromCodeName(countryCode);
        			}
        			if (countryCode3==null) { 
        				result.setResultState(ResultState.NOT_AMENDED);
        				result.addComment("Unable to look up country from provided country code ["+countryCode+"].");
        			} else {
        				Double buffer_km = 3d;
        				try { 
        					Double lat = Double.parseDouble(decimalLatitude);
        					Double lng = Double.parseDouble(decimalLongitude);
        					if (GEOUtil.isPointNearCountryPlusEEZ(countryCode3, lat, lng, buffer_km)) { 
        						result.setResultState(ResultState.NOT_AMENDED);
        						result.addComment("Provided coordinate lies within the bounds of the country specified by the country code.");
        					} else {
        						result.addComment("Provided decimalLatitude and decimalLongitude fall outside the bounds of the country specified by the countryCode");
        						// Point is outside country, try transpositions: 
        						if (lng <= 90 && GEOUtil.isPointNearCountryPlusEEZ(countryCode3, lng, lat, buffer_km)) { 
        							// lat/long switched
        							result.setResultState(ResultState.AMENDED);
        							result.addComment("Coordinate with dwc:decimalLatitude and dwc:decimalLongitude transposed lies within the bounds of the country specified by the country code.");
        	        				Map<String, String> values = new HashMap<>();
        	        				values.put("dwc:decimalLatitude", decimalLongitude);
        	        				values.put("dwc:decimalLongitude", decimalLatitude);
        	        				result.setValue(new AmendmentValue(values));
        						} else if (GEOUtil.isPointNearCountryPlusEEZ(countryCode3, -lat, lng, buffer_km)) {
        							// lat sign switched
        							result.setResultState(ResultState.AMENDED);
        							result.addComment("Coordinate with dwc:decimalLatitude and dwc:decimalLongitude transposed lies within the bounds of the country specified by the country code.");
        	        				Map<String, String> values = new HashMap<>();
        	        				String switchedLat = decimalLatitude;
        	        				if (decimalLatitude.contains("-")) { 
        	        					switchedLat = decimalLatitude.replace("-", "");
        	        				} else { 
        	        					switchedLat = "-" + decimalLatitude.trim();
        	        				}
        	        				values.put("dwc:decimalLatitude", switchedLat);
        	        				values.put("dwc:decimalLongitude", decimalLongitude);
        	        				result.setValue(new AmendmentValue(values));
        						} else if (GEOUtil.isPointNearCountryPlusEEZ(countryCode3, lat, -lng, buffer_km)) {
        							// long sign switched
        							result.setResultState(ResultState.AMENDED);
        							result.addComment("Coordinate with dwc:decimalLatitude and dwc:decimalLongitude transposed lies within the bounds of the country specified by the country code.");
        	        				Map<String, String> values = new HashMap<>();
        	        				String switchedLong = decimalLongitude;
        	        				if (decimalLongitude.contains("-")) { 
        	        					switchedLong = decimalLongitude.replace("-", "");
        	        				} else { 
        	        					switchedLong = "-" + decimalLongitude.trim();
        	        				}
        	        				values.put("dwc:decimalLatitude", decimalLatitude);
        	        				values.put("dwc:decimalLongitude", switchedLong);
        	        				result.setValue(new AmendmentValue(values));      
        						} else if (GEOUtil.isPointNearCountryPlusEEZ(countryCode3, -lat, -lng, buffer_km)) {
        							// lat and long sign switched
        							result.setResultState(ResultState.AMENDED);
        							result.addComment("Coordinate with dwc:decimalLatitude and dwc:decimalLongitude transposed lies within the bounds of the country specified by the country code.");
        	        				Map<String, String> values = new HashMap<>();
        	        				String switchedLat = decimalLatitude;
        	        				if (decimalLatitude.contains("-")) { 
        	        					switchedLat = decimalLatitude.replace("-", "");
        	        				} else { 
        	        					switchedLat = "-" + decimalLatitude.trim();
        	        				}
        	        				String switchedLong = decimalLongitude;
        	        				if (decimalLongitude.contains("-")) { 
        	        					switchedLong = decimalLongitude.replace("-", "");
        	        				} else { 
        	        					switchedLong = "-" + decimalLongitude.trim();
        	        				}
        	        				values.put("dwc:decimalLatitude", switchedLat);
        	        				values.put("dwc:decimalLongitude", switchedLong);
        	        				result.setValue(new AmendmentValue(values));          	        				
        						} else { 
        							result.setResultState(ResultState.NOT_AMENDED);
        							result.addComment("Transformations of provided decimalLatitude and decimalLongitude do not place the coordinate inside the boundary for the countryCode.");
        						}
        					}
        				} catch (NumberFormatException e) { 
        					result.setResultState(ResultState.NOT_AMENDED);
        					result.addComment("Error parsing numeric latitude/longitude from provided dwc:decimalLatitude ["+decimalLatitude+"] or dwc:decimalLongitude ["+ decimalLongitude +"].");
        				}
        			}
        		}
        	}
        } catch (SourceAuthorityException e) { 
        	result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);
        	result.addComment("Error with specified Source Authority: " + e.getMessage());
        }
        
        return result;
    }

// TODO: Implementation of VALIDATION_COUNTRYCODE_STANDARD is not up to date with current version: https://rs.tdwg.org/bdq/terms/0493bcfb-652e-4d17-815b-b0cce0742fbe/2024-04-15 see line: 90
// TODO: Implementation of VALIDATION_COUNTRY_FOUND is not up to date with current version: https://rs.tdwg.org/bdq/terms/69b2efdc-6269-45a4-aecb-4cb99c2ae134/2024-04-15 see line: 148
// TODO: Implementation of VALIDATION_MINDEPTH_LESSTHAN_MAXDEPTH is not up to date with current version: https://rs.tdwg.org/bdq/terms/8f1e6e58-544b-4365-a569-fb781341644e/2023-09-18 see line: 247
// TODO: Implementation of VALIDATION_DECIMALLONGITUDE_INRANGE is not up to date with current version: https://rs.tdwg.org/bdq/terms/0949110d-c06b-450e-9649-7c1374d940d1/2023-09-17 see line: 321
// TODO: Implementation of AMENDMENT_COORDINATES_FROM_VERBATIM is not up to date with current version: https://rs.tdwg.org/bdq/terms/3c2590c7-af8a-4eb4-af57-5f73ba9d1f8e/2023-09-17 see line: 377
// TODO: Implementation of VALIDATION_MINELEVATION_INRANGE is not up to date with current version: https://rs.tdwg.org/bdq/terms/0bb8297d-8f8a-42d2-80c1-558f29efe798/2023-09-17 see line: 539
// TODO: Implementation of VALIDATION_LOCATION_NOTEMPTY is not up to date with current version: https://rs.tdwg.org/bdq/terms/58486cb6-1114-4a8a-ba1e-bd89cfe887e9/2023-09-18 see line: 630
// TODO: Implementation of VALIDATION_COUNTRY_NOTEMPTY is not up to date with current version: https://rs.tdwg.org/bdq/terms/6ce2b2b4-6afe-4d13-82a0-390d31ade01c/2023-09-17 see line: 705
// TODO: Implementation of AMENDMENT_COORDINATES_CONVERTED is not up to date with current version: https://rs.tdwg.org/bdq/terms/620749b9-7d9c-4890-97d2-be3d1cde6da8/2023-09-18 see line: 758
// TODO: Implementation of AMENDMENT_COUNTRYCODE_STANDARDIZED is not up to date with current version: https://rs.tdwg.org/bdq/terms/fec5ffe6-3958-4312-82d9-ebcca0efb350/2023-09-17 see line: 879
// TODO: Implementation of VALIDATION_COORDINATES_COUNTRYCODE_CONSISTENT is not up to date with current version: https://rs.tdwg.org/bdq/terms/adb27d29-9f0d-4d52-b760-a77ba57a69c9/2023-09-17 see line: 981
// TODO: Implementation of AMENDMENT_MINDEPTH-MAXDEPTH_FROM_VERBATIM is not up to date with current version: https://rs.tdwg.org/bdq/terms/c5658b83-4471-4f57-9d94-bf7d0a96900c/2024-08-03 see line: 1101
// TODO: Implementation of VALIDATION_GEODETICDATUM_STANDARD is not up to date with current version: https://rs.tdwg.org/bdq/terms/7e0c0418-fe16-4a39-98bd-80e19d95b9d1/2023-09-17 see line: 1419
// TODO: Implementation of AMENDMENT_GEODETICDATUM_STANDARDIZED is not up to date with current version: https://rs.tdwg.org/bdq/terms/0345b325-836d-4235-96d0-3b5caf150fc0/2024-08-05 see line: 1478
// TODO: Implementation of VALIDATION_COUNTRY_COUNTRYCODE_CONSISTENT is not up to date with current version: https://rs.tdwg.org/bdq/terms/b23110e7-1be7-444a-a677-cdee0cf4330c/2023-09-18 see line: 1555
// TODO: Implementation of AMENDMENT_MINELEVATION-MAXELEVATION_FROM_VERBATIM is not up to date with current version: https://rs.tdwg.org/bdq/terms/2d638c8b-4c62-44a0-a14d-fa147bf9823d/2024-08-03 see line: 1650
// TODO: Implementation of AMENDMENT_COUNTRYCODE_FROM_COORDINATES is not up to date with current version: https://rs.tdwg.org/bdq/terms/8c5fe9c9-4ba9-49ef-b15a-9ccd0424e6ae/2024-04-16 see line: 1874
// TODO: Implementation of VALIDATION_GEODETICDATUM_NOTEMPTY is not up to date with current version: https://rs.tdwg.org/bdq/terms/239ec40e-a729-4a8e-ba69-e0bf03ac1c44/2023-09-18 see line: 1951
// TODO: Implementation of VALIDATION_DECIMALLATITUDE_INRANGE is not up to date with current version: https://rs.tdwg.org/bdq/terms/b6ecda2a-ce36-437a-b515-3ae94948fe83/2023-09-18 see line: 1986
// TODO: Implementation of VALIDATION_DECIMALLONGITUDE_NOTEMPTY is not up to date with current version: https://rs.tdwg.org/bdq/terms/9beb9442-d942-4f42-8b6a-fcea01ee086a/2023-09-18 see line: 2118
// TODO: Implementation of VALIDATION_COUNTRYCODE_NOTEMPTY is not up to date with current version: https://rs.tdwg.org/bdq/terms/853b79a2-b314-44a2-ae46-34a1e7ed85e4/2023-09-18 see line: 2153
// TODO: Implementation of AMENDMENT_GEODETICDATUM_ASSUMEDDEFAULT is not up to date with current version: https://rs.tdwg.org/bdq/terms/7498ca76-c4d4-42e2-8103-acacccbdffa7/2023-09-18 see line: 2194
// TODO: Implementation of VALIDATION_MINDEPTH_INRANGE is not up to date with current version: https://rs.tdwg.org/bdq/terms/04b2c8f3-c71b-4e95-8e43-f70374c5fb92/2023-09-18 see line: 2266
// TODO: Implementation of VALIDATION_MINELEVATION_LESSTHAN_MAXELEVATION is not up to date with current version: https://rs.tdwg.org/bdq/terms/d708526b-6561-438e-aa1a-82cd80b06396/2023-09-18 see line: 2340
// TODO: Implementation of VALIDATION_COORDINATEUNCERTAINTY_INRANGE is not up to date with current version: https://rs.tdwg.org/bdq/terms/c6adf2ea-3051-4498-97f4-4b2f8a105f57/2023-09-18 see line: 2405
// TODO: Implementation of VALIDATION_MAXELEVATION_INRANGE is not up to date with current version: https://rs.tdwg.org/bdq/terms/c971fe3f-84c1-4636-9f44-b1ec31fd63c7/2023-09-18 see line: 2468
// TODO: Implementation of VALIDATION_DECIMALLATITUDE_NOTEMPTY is not up to date with current version: https://rs.tdwg.org/bdq/terms/7d2485d5-1ba7-4f25-90cb-f4480ff1a275/2023-09-18 see line: 2535
// TODO: Implementation of VALIDATION_MAXDEPTH_INRANGE is not up to date with current version: https://rs.tdwg.org/bdq/terms/3f1db29a-bfa5-40db-9fd1-fde020d81939/2023-09-18 see line: 2574
// TODO: Implementation of VALIDATION_STATEPROVINCE_FOUND is not up to date with current version: https://rs.tdwg.org/bdq/terms/4daa7986-d9b0-4dd5-ad17-2d7a771ea71a/2023-09-18 see line: 2647
}
