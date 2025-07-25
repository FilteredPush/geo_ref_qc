/* NOTE: requires the ffdq-api dependecy in the maven pom.xml */

package org.filteredpush.qc.georeference;

import java.io.IOException;
import java.text.NumberFormat;
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
import org.filteredpush.qc.georeference.util.GISDataLoader;
import org.filteredpush.qc.georeference.util.GeoUtilSingleton;
import org.filteredpush.qc.georeference.util.GeorefServiceException;
import org.filteredpush.qc.georeference.util.GettyLookup;
import org.filteredpush.qc.georeference.util.TransformationStruct;
import org.filteredpush.qc.georeference.util.UnknownToWGS84Error;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.operation.TransformException;
import org.filteredpush.qc.sciname.EnumSciNameSourceAuthority;
import org.filteredpush.qc.sciname.SciNameSourceAuthority;
import org.filteredpush.qc.sciname.SciNameUtils;
import org.filteredpush.qc.sciname.services.GBIFService;
import org.filteredpush.qc.sciname.services.WoRMSService;
import org.gbif.nameparser.api.UnparsableNameException;

import edu.getty.tgn.service.GettyTGNObject;
import edu.harvard.mcz.nametools.NameAuthorshipParse;
import edu.harvard.mcz.nametools.NameComparison;
import edu.harvard.mcz.nametools.NameUsage;

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
 * #51	VALIDATION_COORDINATES_TERRESTRIALMARINE b9c184ce-a859-410c-9d12-71a338200380
 *
 * #43	AMENDMENT_COORDINATES_CONVERTED 620749b9-7d9c-4890-97d2-be3d1cde6da8
 * #102 AMENDMENT_GEODETICDATUM_ASSUMEDDEFAULT 7498ca76-c4d4-42e2-8103-acacccbdffa7
 * #48	AMENDMENT_COUNTRYCODE_STANDARDIZED fec5ffe6-3958-4312-82d9-ebcca0efb350
 * #73	AMENDMENT_COUNTRYCODE_FROM_COORDINATES 8c5fe9c9-4ba9-49ef-b15a-9ccd0424e6ae
 * #68	AMENDMENT_MINELEVATION-MAXELEVATION_FROM_VERBATIM 2d638c8b-4c62-44a0-a14d-fa147bf9823d
 * #55	AMENDMENT_MINDEPTH-MAXDEPTH_FROM_VERBATIM c5658b83-4471-4f57-9d94-bf7d0a96900c
 * #60	AMENDMENT_GEODETICDATUM_STANDARDIZED 0345b325-836d-4235-96d0-3b5caf150fc0
 * #54	AMENDMENT_COORDINATES_TRANSPOSED f2b4a50a-6b2f-4930-b9df-da87b6a21082
 *
 * For #72, see rec_occur_qc DwCMetadataDQ
 * #72 ISSUE_DATAGENERALIZATIONS_NOTEMPTY 13d5a10e-188e-40fd-a22c-dbaa87b91df2
 *
 * @author mole
 * @version $Id: $Id
 */
@Mechanism(value="71fa3762-0dfa-43c7-a113-d59797af02e8",label="Kurator: Spatial Data Validator - DwCGeoRefDQ:v2.1.2-SNAPSHOT")
public class DwCGeoRefDQ{
	
	private static final Log logger = LogFactory.getLog(DwCGeoRefDQ.class);
    
    /**
     * Is the value of dwc:countryCode a valid ISO 3166-1-alpha-2 country code?
     *
     * Provides: #20 VALIDATION_COUNTRYCODE_STANDARD
     * Version: 2024-09-19
     *
     * @param countryCode the provided dwc:countryCode to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_COUNTRYCODE_STANDARD", description="Is the value of dwc:countryCode a valid ISO 3166-1-alpha-2 country code?")
    @Provides("0493bcfb-652e-4d17-815b-b0cce0742fbe")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/0493bcfb-652e-4d17-815b-b0cce0742fbe/2024-09-19")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if the dwc:countryCode is bdq:Empty; COMPLIANT if dwc:countryCode can be unambiguously interpreted as a valid ISO 3166-1-alpha-2 country code in the bdq:sourceAuthority; otherwise NOT_COMPLIANT. bdq:sourceAuthority default = 'ISO 3166 Country Codes' {[https://www.iso.org/iso-3166-country-codes.html]} {ISO 3166-1-alpha-2 Country Code search [https://www.iso.org/obp/ui/#search]}")
    public static DQResponse<ComplianceValue> validationCountrycodeStandard(
    		@ActedUpon("dwc:countryCode") String countryCode) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if the 
        // dwc:countryCode is bdq:Empty; COMPLIANT if dwc:countryCode 
        // can be unambiguously interpreted as a valid ISO 3166-1-alpha-2 
        // country code in the bdq:sourceAuthority; otherwise NOT_COMPLIANT 
        //
        // bdq:sourceAuthority default = "ISO 3166 Country Codes" {[https://www.iso.org/iso-3166-country-codes.html]} 
        // {ISO 3166-1-alpha-2 Country Code search [https://www.iso.org/obp/ui/#search]} 
        
        // ZZ (unknown) and XZ (High Seas) in User defined codes should
        // be treated as compliant.
        
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
     * Does the value of dwc:country occur in the bdq:sourceAuthority?
     *
     * #21 Validation SingleRecord Conformance: country notstandard
     *
     * Provides: 21 VALIDATION_COUNTRY_FOUND
     * Version: 2024-08-19
     *
     * @param country the provided dwc:country to evaluate
     * @param sourceAuthority the source authority to consult, if null uses ""The Getty Thesaurus of Geographic Names (TGN)",
     * 	additional supported values for sourceAuthority are "NaturalEarth" and "datahub.io".
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_COUNTRY_FOUND", description="Does the value of dwc:country occur in the bdq:sourceAuthority?")
    @Provides("69b2efdc-6269-45a4-aecb-4cb99c2ae134")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/69b2efdc-6269-45a4-aecb-4cb99c2ae134/2024-08-19")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:country is bdq:Empty; COMPLIANT if value of dwc:country is a place type equivalent to administrative entity of 'nation' in the bdq:sourceAuthority; otherwise NOT_COMPLIANT. bdq:sourceAuthority default = 'The Getty Thesaurus of Geographic Names (TGN)' {[https://www.getty.edu/research/tools/vocabularies/tgn/index.html]}")
    public static DQResponse<ComplianceValue> validationCountryFound(
    		@ActedUpon("dwc:country") String country,
    		@Parameter(name="bdq:sourceAuthority") String sourceAuthority
    		) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:country 
        // is bdq:Empty; COMPLIANT if value of dwc:country is a place 
        // type equivalent to administrative entity of "nation" in 
        // the bdq:sourceAuthority; otherwise NOT_COMPLIANT 
        // 

        // Parameters; This test is defined as parameterized.
        // bdq:sourceAuthority default = "The Getty Thesaurus of Geographic Names (TGN)" 
        // {[https://www.getty.edu/research/tools/vocabularies/tgn/index.html]} 
        
        if (GEOUtil.isEmpty(sourceAuthority)) { 
        	sourceAuthority = GettyLookup.GETTY_TGN;
        }

        try { 
        	GeoRefSourceAuthority sourceAuthorityObject = new GeoRefSourceAuthority(sourceAuthority);
        	if (sourceAuthorityObject.getAuthority().equals(EnumGeoRefSourceAuthority.INVALID)) { 
        		throw new SourceAuthorityException("Invalid Source Authority");
        	}
        	if (GEOUtil.isEmpty(country)) { 
        		result.addComment("dwc:country is empty");
        		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        	} else { 
        		if (sourceAuthorityObject.getAuthority().equals(EnumGeoRefSourceAuthority.GETTY_TGN)) {    
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
        				if (lookup.lookupCountry(country)==null) { 
        					result.addComment("Error looking up country in " + sourceAuthority);
        					result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);
        				} else if (lookup.lookupCountry(country)) { 
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
        		} else if (sourceAuthorityObject.getAuthority().equals(EnumGeoRefSourceAuthority.NE_ADMIN_0)) {    
        			// ne_10m_admin_0_countries
        			if (GEOUtil.isCountryKnown(country)) {
        				result.addComment("the value provided for dwc:country [" + country + "] exists as a country name in the NaturalEarth admin regions.");
        				result.setResultState(ResultState.RUN_HAS_RESULT);
        				result.setValue(ComplianceValue.COMPLIANT);
        			} else { 
        				result.addComment("the value provided for dwc:country [" + country + "] is not a country name in the NaturalEarth admin regions.");
        				result.setResultState(ResultState.RUN_HAS_RESULT);
        				result.setValue(ComplianceValue.NOT_COMPLIANT);
        			}
        		} else if (sourceAuthorityObject.getAuthority().equals(EnumGeoRefSourceAuthority.DATAHUB)) { 
        			// datahub.io
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
        			throw new SourceAuthorityException("Unsupported sourceAuthority: ["+sourceAuthority+"]");
        		}
        	}
        } catch (SourceAuthorityException e) { 
        	result.addComment("Error with bdq:sourceAuthority: " +  e.getMessage());
        	result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);
        }
        return result;
    }

    
    /**
     * Is the value of dwc:minimumDepthInMeters a number that is less than or equal to the value of dwc:maximumDepthInMeters?
     *
     * #24 Validation SingleRecord Conformance: mindepth greaterthan maxdepth
     *
     * Provides: 24 VALIDATION_MINDEPTH_LESSTHAN_MAXDEPTH
     * Version: 2023-09-18
     *
     * @param maximumDepthInMeters the provided dwc:maximumDepthInMeters to evaluate
     * @param minimumDepthInMeters the provided dwc:minimumDepthInMeters to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_MINDEPTH_LESSTHAN_MAXDEPTH", description="Is the value of dwc:minimumDepthInMeters a number that is less than or equal to the value of dwc:maximumDepthInMeters?")
    @Provides("8f1e6e58-544b-4365-a569-fb781341644e")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/8f1e6e58-544b-4365-a569-fb781341644e/2023-09-18")
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
        // 

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
     * Provides: 30 VALIDATION_DECIMALLONGITUDE_INRANGE
     * Version: 2023-09-17
     *
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_DECIMALLONGITUDE_INRANGE", description="Is the value of dwc:decimalLongitude a number between -180 and 180 inclusive?")
    @Provides("0949110d-c06b-450e-9649-7c1374d940d1")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/0949110d-c06b-450e-9649-7c1374d940d1/2023-09-17")
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
     * Proposes an amendment to the values of dwc:decimalLatitude, dwc:decimalLongitude, and dwc:geodeticDatum from geographic coordinate information in the verbatim coordinates terms.
     *
     * Provides: 32 AMENDMENT_COORDINATES_FROM_VERBATIM
     * Version: 2024-08-20
     *
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate as ActedUpon.
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate as ActedUpon.
     * @param geodeticDatum the provided dwc:geodeticDatum to evaluate as ActedUpon.
     * @param verbatimCoordinateSystem the provided dwc:verbatimCoordinateSystem to evaluate as Consulted.
     * @param verbatimCoordinates the provided dwc:verbatimCoordinates to evaluate as Consulted.
     * @param verbatimLongitude the provided dwc:verbatimLongitude to evaluate as Consulted.
     * @param verbatimSRS the provided dwc:verbatimSRS to evaluate as Consulted.
     * @param verbatimLatitude the provided dwc:verbatimLatitude to evaluate as Consulted.
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Amendment(label="AMENDMENT_COORDINATES_FROM_VERBATIM", description="Proposes an amendment to the values of dwc:decimalLatitude, dwc:decimalLongitude, and dwc:geodeticDatum from geographic coordinate information in the verbatim coordinates terms.")
    @Provides("3c2590c7-af8a-4eb4-af57-5f73ba9d1f8e")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/3c2590c7-af8a-4eb4-af57-5f73ba9d1f8e/2024-08-20")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if 1) either dwc:decimalLatitude or dwc:decimalLongitude are bdq:NotEmpty, or 2) dwc:verbatimCoordinates and one of dwc:verbatimLatitude and dwc:verbatimLongitude are bdq:Empty; FILLED_IN the values of dwc:decimalLatitude, dwc:decimalLongitude and dwc:geodeticDatum (provided that the dwc:verbatimCoordinates can be unambiguously interpreted as geographic coordinates) from 1) dwc:verbatimLatitude, dwc:verbatimLongitude and dwc:verbatimSRS or 2) dwc:verbatimCoordinates and dwc:verbatimSRS; otherwise NOT_AMENDED.. ")
    public static DQResponse<AmendmentValue> amendmentCoordinatesFromVerbatim(
    		@ActedUpon("dwc:decimalLatitude") String decimalLatitude, 
    		@ActedUpon("dwc:decimalLongitude") String decimalLongitude, 
    		@ActedUpon("dwc:geodeticDatum") String geodeticDatum, 
    		@Consulted("dwc:verbatimCoordinates") String verbatimCoordinates, 
    		@Consulted("dwc:verbatimLatitude") String verbatimLatitude,
    		@Consulted("dwc:verbatimLongitude") String verbatimLongitude, 
    		@Consulted("dwc:verbatimSRS") String verbatimSRS, 
    		@Consulted("dwc:verbatimCoordinateSystem") String verbatimCoordinateSystem
    		)
    {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if 1) either dwc:decimalLatitude 
        // or dwc:decimalLongitude are bdq:NotEmpty, or 2) dwc:verbatimCoordinates 
        // and one of dwc:verbatimLatitude and dwc:verbatimLongitude 
        // are bdq:Empty; FILLED_IN the values of dwc:decimalLatitude, 
        // dwc:decimalLongitude and dwc:geodeticDatum (provided that 
        // the dwc:verbatimCoordinates can be unambiguously interpreted 
        // as geographic coordinates) from 1) dwc:verbatimLatitude, 
        // dwc:verbatimLongitude and dwc:verbatimSRS or 2) dwc:verbatimCoordinates 
        // and dwc:verbatimSRS; otherwise NOT_AMENDED. 
        
        // Note: Specification only allows for conversion of geographic coordinates, no 
        // transformations.
        
        boolean done = false;
 
        if (!GEOUtil.isEmpty(decimalLatitude) || !GEOUtil.isEmpty(decimalLongitude)) { 
        	result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
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
        // if verbatimSRS can be identified to be not consistent with EPSG:4326, fail
        // if verbatimCoordinateSystem can be identified to not be geographic, fail.
        
        boolean interpreted = false;
        
        if (!done && !GEOUtil.isEmpty(verbatimCoordinates)) { 
        	if (verbatimCoordinates.matches("^[0-9]{2}[A-Z] [A-Z]{2} [0-9]{3,5} [0-9]{3,5}")) {
        		// MGRS, USNG
        		result.addComment("Provided value for verbatimCoordinates ["+verbatimCoordinates+"] appears to be a MGRS or USNG coordinate, not converting.");
        		result.setResultState(ResultState.NOT_AMENDED);
        	} else if (verbatimCoordinates.matches("^[0-9]{2}[A-Z] [0-9]{7} [0-9]{7}")) { 
        		// UTM/UPS
        		result.addComment("Provided value for verbatimCoordinates ["+verbatimCoordinates+"] appears to be a UTM or UPS coordinate, not converting.");
        		result.setResultState(ResultState.NOT_AMENDED);
        		// Specification does not Support transformation to EPSG:4326
//        		GeolocationResult conversion =  GEOUtil.convertUTMToLatLong(verbatimCoordinates,false);
//        		if (conversion==null) { 
//        			result.addComment("Provided value for verbatimCoordinates ["+verbatimCoordinates+"] appears to be a UTM or UPS coordinate, but unable to convert.");
//        			result.setResultState(ResultState.NOT_AMENDED);
//        		} else { 
//        			result.setResultState(ResultState.FILLED_IN);
//        			String newDecimalLatitude = conversion.getLatitude().toString();
//        			String newDecimalLongitude = conversion.getLongitude().toString();
//        			Map<String,String> map = new HashMap();
//        			map.put("dwc:decimalLatitude", newDecimalLatitude);
//        			map.put("dwc:decimalLongitude", newDecimalLongitude);
//        			result.setValue(new AmendmentValue(map));
//        			result.addComment("Interpreted decimalLatitude ["+newDecimalLatitude+"] and decimalLongitude ["+newDecimalLongitude+"] from provided verbatimCoordinates ["+verbatimCoordinates+"] as UTM coordinate, assuming a WGS84 datum and MGRS zone letters");
//        			interpreted = true;
//        		}
        	} else { 
        		if (verbatimCoordinates.contains(";")) {
        			verbatimCoordinates = verbatimCoordinates.replace(";", ",");
        		}
        		logger.debug(verbatimCoordinates);
        		String modifiedverbatimCoordinates = GEOUtil.simplifyVerbatimCoordinate(verbatimCoordinates);
        		logger.debug(modifiedverbatimCoordinates);
        		if (modifiedverbatimCoordinates.contains(",")) {
        			logger.debug(modifiedverbatimCoordinates);
        			String[] bits = modifiedverbatimCoordinates.split(",");
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
        	        		if (!GEOUtil.isEmpty(verbatimSRS)) { 
        	        			map.put("dwc:geodeticDatum", verbatimSRS);
        	        		}
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
        		if (!GEOUtil.isEmpty(verbatimSRS)) { 
        	       map.put("dwc:geodeticDatum", verbatimSRS);
        	    }
        		result.setValue(new AmendmentValue(map));
        		result.addComment("Interpreted decimalLatitude ["+newDecimalLatitude+"] and decimalLongitude ["+newDecimalLongitude+"] from provided verbatimLatitude ["+verbatimLatitude+"] and verbatimLongtude ["+verbatimLongitude+"]");
        	    interpreted = true;
        	}
        		
        }
        
        if (result.getResultState().equals(ResultState.NOT_RUN) && interpreted == false) { 
        	result.setResultState(ResultState.NOT_AMENDED);
        	result.addComment("Unable to interpret provided verbatim coordinate values: verbatimCoordinates["+verbatimCoordinates+"], verbatimLatitude ["+verbatimLatitude+"], verbatimLongitude ["+verbatimLongitude+"].");
        }
        
        return result;
    }

    /**
     * Is the value of dwc:minimumElevationInMeters within the Parameter range?
     *
     * Provides: #39 VALIDATION_MINELEVATION_INRANGE
     * Version: 2023-09-17
     *
     * @param minimumElevationInMeters the provided dwc:minimumElevationInMeters to evaluate
     * @param minimumValidElevationInMeters minimum valid value to test against, if null, defaults to -430
     * @param maximumValidElevationInMeters maximum valid value to test against, if null, defaults to 8550
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_MINELEVATION_INRANGE", description="Is the value of dwc:minimumElevationInMeters within the Parameter range?")
    @Provides("0bb8297d-8f8a-42d2-80c1-558f29efe798")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/0bb8297d-8f8a-42d2-80c1-558f29efe798/2023-09-17")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if dwc:minimumElevationInMeters is EMPTY or the value is not a number; COMPLIANT if the value of dwc:minimumElevationInMeters is within the range of bdq:minimumValidElevationInMeters to bdq:maximumValidElevationInMeters inclusive; otherwise NOT_COMPLIANT bdq:minimumValidElevationInMeters default = '-430',bdq:maximumValidElevationInMeters default = '8850'")
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
        // bdq:minimumValidElevationInMeters,bdq:maximumValidElevationInMeters
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
     * Provides: 40 VALIDATION_LOCATION_NOTEMPTY
     * Version: 2023-09-18
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
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/58486cb6-1114-4a8a-ba1e-bd89cfe887e9/2023-09-18")
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
     * Version: 2024-09-27
     *
     * @param country the provided dwc:country to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_COUNTRY_NOTEMPTY", description="Is there a value in dwc:country?")
    @Provides("6ce2b2b4-6afe-4d13-82a0-390d31ade01c")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/6ce2b2b4-6afe-4d13-82a0-390d31ade01c/2024-09-27")
    @Specification("COMPLIANT if dwc:country is bdq:NotEmpty or dwc:countryCode has a value of 'XZ' and either dwc:country is bdq:Empty or has a value of 'High seas'; otherwise NOT_COMPLIANT ?. ")
    public static DQResponse<ComplianceValue> validationCountryNotempty(
    		@ActedUpon("dwc:country") String country,
            @Consulted("dwc:countryCode") String countryCode
    ) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // COMPLIANT if dwc:country is bdq:NotEmpty or dwc:countryCode 
        // has a value of "XZ" and either dwc:country is bdq:Empty 
        // or has a value of "High seas"; otherwise NOT_COMPLIANT 
        //

        result.setResultState(ResultState.RUN_HAS_RESULT);
        if (GEOUtil.isEmpty(country))
		{ 
            if (GEOUtil.isEmpty(countryCode)) { 
            	result.setValue(ComplianceValue.NOT_COMPLIANT);
            	result.addComment("The value provided for dwc:country is empty");
            } else { 
            	if (countryCode.equals("XZ")) { 
            		result.setValue(ComplianceValue.COMPLIANT);
            		result.addComment("dwc:country does not contain a value, but dwc:countryCode contains the High Seas code XZ.");
            	} else { 
            		result.setValue(ComplianceValue.NOT_COMPLIANT);
            		result.addComment("The value provided for dwc:country is empty, and dwc:countryCode ["+countryCode+"] is not the High Seas code XZ.");
            	}
            }
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
     * Tagged as imature/incomplete.  This implementation is not working.
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
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/620749b9-7d9c-4890-97d2-be3d1cde6da8/2023-06-24")
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
        
        if (1==1) { 
        	result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);
        	result.addComment("Coordinate transformation backing not configured.");
        }
        /*
        } else if (GEOUtil.isEmpty(geodeticDatum)) { 
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
        */
        return result;
    }

    /**
     * Proposes an amendment to the value of dwc:countryCode if it can 
     * be interpreted as an ISO 3166-1-alpha-2 country code.
     *
     * #48 Amendment SingleRecord Conformance: countrycode standardized
     *
     * Provides: AMENDMENT_COUNTRYCODE_STANDARDIZED
     * Version: 2024-11-09
     *
     * @param countryCode the provided dwc:countryCode to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Amendment(label="AMENDMENT_COUNTRYCODE_STANDARDIZED", description="Proposes an amendment to the value of dwc:countryCode if it can be interpreted as an ISO 3166-1-alpha-2 country code.")
    @Provides("fec5ffe6-3958-4312-82d9-ebcca0efb350")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/fec5ffe6-3958-4312-82d9-ebcca0efb350/2024-11-09")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISTITES_NOT_MET if the value of dwc:countryCode is bdq:Empty; AMENDED the value of dwc:countryCode if it can be unambiguously interpreted to a value in the bdq:sourceAuthority; otherwise NOT_AMENDED. bdq:sourceAuthority default = 'ISO 3166-1-alpha-2' {[https://www.iso.org/iso-3166-country-codes.html]} {ISO 3166-1-alpha-2 Country Code search [https://www.iso.org/obp/ui/#search]}")
    public static DQResponse<AmendmentValue> amendmentCountrycodeStandardized(
    		@ActedUpon("dwc:countryCode") String countryCode) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        // Specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority
        // is not available; INTERNAL_PREREQUISTITES_NOT_MET if the
        // value of dwc:countryCode is bdq:Empty; AMENDED the value of
        // dwc:countryCode if it can be unambiguously interpreted to a
        // value in the bdq:sourceAuthority; otherwise NOT_AMENDED
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISTITES_NOT_MET if the 
        // value of dwc:countryCode is EMPTY; AMENDED the value of 
        // dwc:countryCode if it can be unambiguously interpreted from 
        // bdq:sourceAuthority; otherwise NOT_AMENDED bdq:sourceAuthority 
        //
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
     * Do the geographic coordinates fall on or within the boundaries of the territory given in dwc:countryCode or its Exclusive Economic Zone?
     *
     * #50 Validation SingleRecord Consistency: coordinates countrycode inconsistent
     *
     * Provides: #50 VALIDATION_COORDINATESCOUNTRYCODE_CONSISTENT
     * Version: 2024-08-30
     *
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate
     * @param countryCode the provided dwc:countryCode to evaluate
     * @param spatialBufferInMeters the value of bdq:spatialBufferInMeters to apply.
     * @param sourceAuthority the spatial source authority to consult.
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_COORDINATESCOUNTRYCODE_CONSISTENT", description="Do the geographic coordinates fall on or within the boundaries of the territory given in dwc:countryCode or its Exclusive Economic Zone?")
    @Provides("adb27d29-9f0d-4d52-b760-a77ba57a69c9")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/adb27d29-9f0d-4d52-b760-a77ba57a69c9/2024-08-30")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if one or more of dwc:decimalLatitude, dwc:decimalLongitude, or dwc:countryCode are bdq:Empty or invalid; COMPLIANT if the geographic coordinates fall on or within the boundary defined by the union of the boundary of the country from dwc:countryCode plus it's Exclusive Economic Zone as found in the bdq:sourceAuthority, if any, plus an exterior buffer given by bdq:spatialBufferInMeters; otherwise NOT_COMPLIANT. bdq:sourceAuthority default = '10m-admin-1 boundaries UNION with Exclusive Economic Zones' {[https://www.naturalearthdata.com/downloads/10m-cultural-vectors/10m-admin-1-states-provinces/] spatial UNION [https://www.marineregions.org/downloads.php#marbound]},bdq:spatialBufferInMeters default = '3000'")
    public static DQResponse<ComplianceValue> validationCoordinatesCountrycodeConsistent(
    		@ActedUpon("dwc:decimalLatitude") String decimalLatitude, 
    		@ActedUpon("dwc:decimalLongitude") String decimalLongitude, 
    		@ActedUpon("dwc:countryCode") String countryCode,
    		@Parameter(name="bdq:spatialBufferInMeters") String spatialBufferInMeters, 
    		@Parameter(name="bdq:sourceAuthority") String sourceAuthority
    		) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if one 
        // or more of dwc:decimalLatitude, dwc:decimalLongitude, or 
        // dwc:countryCode are bdq:Empty or invalid; COMPLIANT if the 
        // geographic coordinates fall on or within the boundary defined 
        // by the union of the boundary of the country from dwc:countryCode 
        // plus it's Exclusive Economic Zone as found in the bdq:sourceAuthority, 
        // if any, plus an exterior buffer given by bdq:spatialBufferInMeters; 
        // otherwise NOT_COMPLIANT 

        // Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority default = 
        // "10m-admin-1 boundaries UNION with Exclusive Economic Zones" 
        //  {[https://www.naturalearthdata.com/downloads/10m-cultural-vectors/10m-admin-1-states-provinces/] 
        // spatial UNION [https://www.marineregions.org/downloads.php#marbound]},
        // bdq:spatialBufferInMeters default = "3000" 
        
 
        if (GEOUtil.isEmpty(sourceAuthority)) { 
        	sourceAuthority = "10m-admin-1 boundaries UNION with Exclusive Economic Zones";
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
        		if (!sourceAuthorityObject.getAuthority().equals(EnumGeoRefSourceAuthority.ADM1_UNION_EEZ)) { 
        			throw new SourceAuthorityException("Unsupported sourceAuthority: ["+sourceAuthority+"]");
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
     *  Proposes amendments of the values of dwc:minimumDepthInMeters and
     *  dwc:maximumDepthInMeters if they can be interpreted from dwc:verbatimDepth.
     *
     * #55 Amendment SingleRecord Completeness: mindepth-maxdepth from verbatim
     *
     * Provides: 55 AMENDMENT_MINDEPTHMAXDEPTH_FROM_VERBATIM
     * Version: 2024-08-30
     *
     * @param verbatimDepth the provided dwc:verbatimDepth to evaluate
     * @param maximumDepthInMeters the provided dwc:maximumDepthInMeters to evaluate
     * @param minimumDepthInMeters the provided dwc:minimumDepthInMeters to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Amendment(label="AMENDMENT_MINDEPTHMAXDEPTH_FROM_VERBATIM", description="Proposes amendments of the values of dwc:minimumDepthInMeters and dwc:maximumDepthInMeters if they can be interpreted from dwc:verbatimDepth.")
    @Provides("c5658b83-4471-4f57-9d94-bf7d0a96900c")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/c5658b83-4471-4f57-9d94-bf7d0a96900c/2024-08-30")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if dwc:minimumDepthInMeters or dwc:maximumDepthInMeters are bdq:NotEmpty or dwc:verbatimDepth is bdq:Empty; FILLED_IN the value of dwc:minimumDepthInMeters and dwc:maximumDepthInMeters if they can be unambiguously interpreted from dwc:verbatimDepth; otherwise NOT_AMENDED.. ")
    public static DQResponse<AmendmentValue> amendmentMindepthMaxdepthFromVerbatim(
    		@ActedUpon("dwc:verbatimDepth") String verbatimDepth, 
    		@ActedUpon("dwc:maximumDepthInMeters") String maximumDepthInMeters, 
    		@ActedUpon("dwc:minimumDepthInMeters") String minimumDepthInMeters) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:minimumDepthInMeters 
        // or dwc:maximumDepthInMeters are bdq:NotEmpty or dwc:verbatimDepth 
        // is bdq:Empty; FILLED_IN the value of dwc:minimumDepthInMeters 
        // and dwc:maximumDepthInMeters if they can be unambiguously 
        // interpreted from dwc:verbatimDepth; otherwise NOT_AMENDED. 

        if (GEOUtil.isEmpty(verbatimDepth)) { 
        	result.addComment("No Value provided for dwc:verbatimDepth");
        	result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        } else if (!GEOUtil.isEmpty(maximumDepthInMeters) || !GEOUtil.isEmpty(minimumDepthInMeters)) { 
        	result.addComment("At least one of dwc:maximumDepthInMeters or dwc:minimumDepthInMeters contains a value.");
        	result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        } else {
        	if (GEOUtil.isEmpty(maximumDepthInMeters) && GEOUtil.isEmpty(minimumDepthInMeters)) { 

        		// M Ambiguous, probably not meters, probably miles
        		
        		// TODO: pattern "to {number} {units}"
        		
        		// Test for case where only a minimum or maximum is specified, this fails.
        		boolean failureCase = false;
        		boolean containsMin = false;
        		boolean containsMax = false;
        		if (verbatimDepth.matches(".*[mM](in)(imum){0,1}[ ]*[dD]epth.*")) {
        			containsMin = true;
        		}
        		if (verbatimDepth.matches(".*[mM](ax)(imum){0,1}[ ]*[dD]epth.*")) {
        			containsMax = true;
        		}
        		if(containsMin ^ containsMax) { 
        			// xor, exactly one of the two is true.
        			if (!verbatimDepth.matches(".*[0-9].*[ ,;:-].*[0-9]")) { 
        				// a minimum or a maximum was specified, but only one number was found
        				failureCase = true;
        				if (containsMin) { 
        					result.addComment("One number, specifying minimum depth was found.");
        				} else { 
        					result.addComment("One number, specifying maximum depth was found.");
        				}
        			}
        		}
        		
        		String simplified = verbatimDepth;
        		if (verbatimDepth.matches(".*[mM](in|ax)(imum){0,1}[ ]*[dD]epth.*")) {
        			simplified = verbatimDepth.replaceAll("[mM](in|ax)(imum){0,1}[ ]*[dD]epth", "").trim();
        		}
        		if (simplified.contains("=")) { 
        			simplified = simplified.replace("=", " ");
        		}
        		if (simplified.contains("  ")) { 
        			simplified = simplified.replace("  ", " ");
        		}
        		
        		// use prevent doubles with very small differences from showing up in results
        		NumberFormat numberFormat8Max = NumberFormat.getNumberInstance();
        		numberFormat8Max.setMaximumFractionDigits(8);
        		numberFormat8Max.setMinimumFractionDigits(0);
       
        		logger.debug(verbatimDepth);
        		logger.debug(simplified);
        		if (failureCase) { 
        			// handle the failure case above.
        			result.addComment("Unable to Interpret provided dwc:verbatimDepth into a depth range ["+ verbatimDepth +"].");
        			result.setResultState(ResultState.NOT_AMENDED);
        		} else if (simplified.matches("^[0-9]+([.]{0,1}[0-9]*){0,1} *(m|m[.]|[mM](eter(s){0,1}))$")) { 
        			String cleaned = simplified.replaceAll("[ Mmetrs]+", "").trim();
        			cleaned = cleaned.replaceAll("[.]$","");
        			result.addComment("Interpreted equal minimum and maximum depths in meters from dwc:verbatimDepth ["+ verbatimDepth +"] interpreted as a depth range in meters ");
        			Map<String, String> values = new HashMap<>();
        			values.put("dwc:minimumDepthInMeters", cleaned);        		
        			values.put("dwc:maximumDepthInMeters", cleaned);
        			result.setValue(new AmendmentValue(values));
        			result.setResultState(ResultState.FILLED_IN);
        		} else if (simplified.matches("^to [0-9]+([.]{0,1}[0-9]*){0,1} *(m|m[.]|[mM](eter(s){0,1}))$")) { 
        			// pattern "to 1 m" change to "0-1m"
        			String cleaned = simplified.replaceAll("^to ","");
        			cleaned = cleaned.replaceAll("[ Mmetrs]+", "").trim();
        			cleaned = cleaned.replaceAll("[.]$","");
        			result.addComment("Interpreted minimum of 0 and and maximum depths in meters from dwc:verbatimDepth ["+ verbatimDepth +"] interpreted as a depth range in meters ");
        			Map<String, String> values = new HashMap<>();
        			values.put("dwc:minimumDepthInMeters", "0");        		
        			values.put("dwc:maximumDepthInMeters", cleaned);
        			result.setValue(new AmendmentValue(values));
        			result.setResultState(ResultState.FILLED_IN);        			
        		} else if (simplified.matches("^[0-9]+([.]{0,1}[0-9]*){0,1}(m|m[.]|[mM](eter(s){0,1})){0,1}[ to,-]{0,4}[0-9]+([.]{0,1}[0-9]*){0,1} *(m|m[.]|[mM](eter(s){0,1}))$")) { 
        			//1-2m  1.1 to 2.2 m
        			String cleaned = simplified.replaceAll(" ", "");
        			cleaned = cleaned.replace("to","-");
        			cleaned = cleaned.replace(",","-");
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
        		} else if (simplified.matches("^[0-9]+([.]{0,1}[0-9]*){0,1} *cm$")) { 
        			// Centimeters
        			String cleaned = simplified.replaceAll("[ cm]+", "").trim();
        			cleaned = cleaned.replaceAll("[.]$","");
        			result.addComment("Interpreted equal minimum and maximum depths in meters from dwc:verbatimDepth ["+ verbatimDepth +"] interpreted as a depth range in cm");
        			Map<String, String> values = new HashMap<>();
        			float min = Float.parseFloat(cleaned);
        			values.put("dwc:minimumDepthInMeters", Double.toString(min / 100d));        		
        			values.put("dwc:maximumDepthInMeters", Double.toString(min / 100d));
        			result.setValue(new AmendmentValue(values));
        			result.setResultState(ResultState.FILLED_IN);
        		} else if (simplified.matches("^[0-9]+([.]{0,1}[0-9]*){0,1} *([fF]tm|[fF]ms|[fF]ms.|[fF]ath(om){0,1}s{0,1}){1}$")) { 
        			// Fathoms
        			String cleaned = simplified.replaceAll("[ fFathoms]+", "").trim();
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
        		} else if (simplified.matches("^[0-9]+([.]{0,1}[0-9]*){0,1}[ to,-]{0,4}[0-9]+([.]{0,1}[0-9]*){0,1} *([fF]tm|[fF]ms|[fF]ms.|[fF]ath(om){0,1}s{0,1}){1}$")) { 
        			//1-2m  1.1 to 2.2 m
        			String cleaned = simplified.replaceAll(" ", "");
        			cleaned = cleaned.replace("to","-");
        			cleaned = cleaned.replace(",","-");
        			cleaned = cleaned.replaceAll("[fFathoms]","");
        			cleaned = cleaned.replaceAll("[.]$","");
        			String[] bits = cleaned.split("-");
        			result.addComment("Interpreted minimum and maximum depths in meters from dwc:verbatimDepth ["+ verbatimDepth +"] interpreted as a depth range in meters ");
        			double min = Double.parseDouble(bits[0]);
        			double max = min;
        			if (bits.length>1) { 
        				max = Double.parseDouble(bits[1]);
        			}
        			Map<String, String> values = new HashMap<>();
        			if (bits.length==1) { 
        				String meters = numberFormat8Max.format(min * GEOUtil.FATHOMS_TO_METERS);
        				values.put("dwc:minimumDepthInMeters", meters);        		
        				values.put("dwc:maximumDepthInMeters", meters);
        			} else if (min<max) { 
        				String minMeters = numberFormat8Max.format(min * GEOUtil.FATHOMS_TO_METERS);
        				String maxMeters = numberFormat8Max.format(max * GEOUtil.FATHOMS_TO_METERS);
        				values.put("dwc:minimumDepthInMeters", minMeters);        		
        				values.put("dwc:maximumDepthInMeters", maxMeters);
        			} else { 
        				String minMeters = numberFormat8Max.format(min * GEOUtil.FATHOMS_TO_METERS);
        				String maxMeters = numberFormat8Max.format(max * GEOUtil.FATHOMS_TO_METERS);
        				values.put("dwc:minimumDepthInMeters", maxMeters);        		
        				values.put("dwc:maximumDepthInMeters", minMeters);
        			}
        			result.setValue(new AmendmentValue(values));
        			result.setResultState(ResultState.FILLED_IN);        			
        		} else if (simplified.matches("^[0-9]+([.]{0,1}[0-9]*){0,1} *([fF]eet|[Ff]oot|[fF]t[.]{0,1}){1}$")) { 
        			String cleaned = simplified.replaceAll("[ fFote]+", "").trim();
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
        		} else if (simplified.matches("^[0-9]+([.]{0,1}[0-9]*){0,1}[ to,-]{0,4}[0-9]+([.]{0,1}[0-9]*){0,1} *([fF]eet|[Ff]oot|[fF]t[.]{0,1}){1}$")) { 
        			//1.1 to 2.2 ft.
        			String cleaned = simplified.replaceAll(" ", "");
        			cleaned = cleaned.replace("to","-");
        			cleaned = cleaned.replace(",","-");
        			cleaned = cleaned.replaceAll("[fFote]","");
        			cleaned = cleaned.replaceAll("[.]$","");
        			String[] bits = cleaned.split("-");
        			result.addComment("Interpreted minimum and maximum depths in meters from dwc:verbatimDepth ["+ verbatimDepth +"] interpreted as a depth range in feet ");
        			double min = Double.parseDouble(bits[0]);
        			double max = min;
        			if (bits.length>1) { 
        				max = Double.parseDouble(bits[1]);
        			}
        			Map<String, String> values = new HashMap<>();
        			if (bits.length==1) { 
        				String meters = numberFormat8Max.format(min * GEOUtil.FEET_TO_METERS);
        				values.put("dwc:minimumDepthInMeters", meters);        		
        				values.put("dwc:maximumDepthInMeters", meters);
        			} else if (min<max) { 
        				String minMeters = numberFormat8Max.format(min * GEOUtil.FEET_TO_METERS);
        				String maxMeters = numberFormat8Max.format(max * GEOUtil.FEET_TO_METERS);
        				values.put("dwc:minimumDepthInMeters", minMeters);        		
        				values.put("dwc:maximumDepthInMeters", maxMeters);
        			} else { 
        				String minMeters = numberFormat8Max.format(min * GEOUtil.FEET_TO_METERS);
        				String maxMeters = numberFormat8Max.format(max * GEOUtil.FEET_TO_METERS);
        				values.put("dwc:minimumDepthInMeters", maxMeters);        		
        				values.put("dwc:maximumDepthInMeters", minMeters);
        			}
        			result.setValue(new AmendmentValue(values));
        			result.setResultState(ResultState.FILLED_IN);
        		}  else { 
        			result.addComment("Unable to Interpret provided dwc:verbatimDepth ["+ verbatimDepth +"].");
        			result.setResultState(ResultState.NOT_AMENDED);
        		}
        	} else { 
        		result.addComment("At least one of dwc:minimumDepthInMeters and dwc:maximumDepthInMeters contains a value.");
        		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
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
     * Provides: 56 VALIDATION_COORDINATESSTATEPROVINCE_CONSISTENT
     * Version: 2024-08-30
     *
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate
     * @param stateProvince the provided dwc:stateProvince to evaluate
     * @param sourceAuthority the provided parameter bdq:sourceAuthority use null for default value.
     * @param spatialBufferInMeters the provided parameter bdq:spatialBufferInMeters use null for default value.
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_COORDINATESSTATEPROVINCE_CONSISTENT", description="Do the geographic coordinates fall on or within the boundary from the bdq:sourceAuthority for the given dwc:stateProvince or within the distance given by bdq:spatialBufferInMeters outside that boundary?")
    @Provides("f18a470b-3fe1-4aae-9c65-a6d3db6b550c")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/f18a470b-3fe1-4aae-9c65-a6d3db6b550c/2024-08-30")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if the values of dwc:decimalLatitude or dwc:decimalLongitude are bdq:Empty or invalid, or dwc:stateProvince is bdq:Empty or not found in the bdq:sourceAuthority; COMPLIANT if the geographic coordinates fall on or within the boundary in the bdq:sourceAuthority for the given dwc:stateProvince (after coordinate reference system transformations, if any, have been accounted for), or within the distance given by bdq:spatialBufferInMeters outside that boundary; otherwise NOT_COMPLIANT.. bdq:sourceAuthority default = '10m-admin-1 boundaries' {[https://www.naturalearthdata.com/downloads/10m-cultural-vectors/10m-admin-1-states-provinces/]},bdq:spatialBufferInMeters default = '3000'")
    public static DQResponse<ComplianceValue> validationCoordinatesStateprovinceConsistent(
    		@ActedUpon("dwc:decimalLatitude") String decimalLatitude, 
    		@ActedUpon("dwc:decimalLongitude") String decimalLongitude, 
    		@ActedUpon("dwc:stateProvince") String stateProvince,
    		@Parameter(name="bdq:sourceAuthority") String sourceAuthority,
    		@Parameter(name="bdq:spatialBufferInMeters") String spatialBufferInMeters
    		) 
    {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if the 
        // values of dwc:decimalLatitude or dwc:decimalLongitude are 
        // bdq:Empty or invalid, or dwc:stateProvince is bdq:Empty 
        // or not found in the bdq:sourceAuthority; COMPLIANT if the 
        // geographic coordinates fall on or within the boundary in 
        // the bdq:sourceAuthority for the given dwc:stateProvince 
        // (after coordinate reference system transformations, if any, 
        // have been accounted for), or within the distance given by 
        // bdq:spatialBufferInMeters outside that boundary; otherwise 
        // NOT_COMPLIANT. 

        // Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority
        // bdq:spatialBufferInMeters
        // bdq:sourceAuthority default = "10m-admin-1 boundaries" 
        // {[https://www.naturalearthdata.com/downloads/10m-cultural-vectors/10m-admin-1-states-provinces/]},
        // bdq:spatialBufferInMeters default = "3000" 
        
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
        	sourceAuthority = "10m-admin-1 boundaries";
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
        			logger.debug(stateProvince);
        			if (GEOUtil.isPrimaryAloneKnown(stateProvince)) { 
        				Double lat = Double.parseDouble(decimalLatitude);
        				Double lng = Double.parseDouble(decimalLongitude);
        				result.setResultState(ResultState.RUN_HAS_RESULT);
        				logger.debug(GEOUtil.isPointNearPrimaryAllowDuplicates(stateProvince, lat, lng, buffer_km)); 
        				if (GEOUtil.isPointNearPrimaryAllowDuplicates(stateProvince, lat, lng, buffer_km)) { 
        					result.setValue(ComplianceValue.COMPLIANT);
        					result.addComment("Provided coordinate decimalLatitude=["+decimalLatitude+"], decimalLongitude=["+decimalLongitude+"] lies within the bounds of the provided stateProvince ["+stateProvince+"] (plus a spatial buffer of ["+spatialBufferInMeters+"]m).");
        				} else { 
        					result.setValue(ComplianceValue.NOT_COMPLIANT);
        					result.addComment("Provided coordinate decimalLatitude=["+decimalLatitude+"], decimalLongitude=["+decimalLongitude+"] lies outside the bounds of the provided stateProvince ["+stateProvince+"] (plus a spatial buffer of ["+spatialBufferInMeters+"]m).");
        				}
        			} else { 
        				result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        				result.addComment("Provided dwc:stateProvince ["+stateProvince+"] not found in primary division sourceAuthority.");
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
    * Is the value of dwc:geodeticDatum valid according to the bdq:sourceAuthority?
     * Does the value of dwc:geodeticDatum occur as a valid geographic CRS, 
     * geodetic Datum or ellipsoid in bdq:sourceAuthority?
     *
     * #59 Validation SingleRecord Conformance: geodeticdatum notstandard
     *
     * Provides: 59 VALIDATION_GEODETICDATUM_STANDARD
     * Version: 2025-03-03
     *
     * @param geodeticDatum the provided dwc:geodeticDatum to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_GEODETICDATUM_STANDARD", description="Does the value of dwc:geodeticDatum occur as a valid geographic CRS, geodetic Datum or ellipsoid in bdq:sourceAuthority?")
    @Provides("7e0c0418-fe16-4a39-98bd-80e19d95b9d1")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/7e0c0418-fe16-4a39-98bd-80e19d95b9d1/2025-03-03")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available, INTERNAL_PREREQUISITES_NOT_MET if dwc:geodeticDatum is bdq:Empty; COMPLIANT if the value of dwc:geodeticDatum is a valid code from the bdq:sourceAuthority (in the form Authority:Number) for a Datum, or ellipsoid, or for a CRS appropriate for a 2D geographic coordinate in degrees, or is the value 'not recorded'; otherwise NOT_COMPLIANT. bdq:sourceAuthority = 'EPSG' {[https://epsg.org]} {API for EPSG codes [https://apps.epsg.org/api/swagger/ui/index]}")
    public static DQResponse<ComplianceValue> validationGeodeticdatumStandard(
    		@ActedUpon("dwc:geodeticDatum") String geodeticDatum) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available, INTERNAL_PREREQUISITES_NOT_MET if dwc:geodeticDatum 
        // is bdq:Empty; COMPLIANT if the value of dwc:geodeticDatum 
        // is a valid code from the bdq:sourceAuthority (in the form 
        // Authority:Number) for a Datum, or ellipsoid, or for a CRS 
        // appropriate for a 2D geographic coordinate in degrees, or 
        // is the value "not recorded"; otherwise NOT_COMPLIANT 
        //
        // bdq:sourceAuthority = "EPSG" {[https://epsg.org]} {API for 
        // EPSG codes [https://apps.epsg.org/api/swagger/ui/index]} 
        
        if (GEOUtil.isEmpty(geodeticDatum)) { 
        	result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
			result.addComment("The value provided for dwc:geodeticDatum is empty");
        } else if (geodeticDatum.equals("not recorded")) { 
			result.setResultState(ResultState.RUN_HAS_RESULT);
			result.setValue(ComplianceValue.COMPLIANT);
			result.addComment("The value of dwc:geodeticDatum is the value 'not recorded'.");
        } else if (geodeticDatum.equals("unknown")) { 
			result.setResultState(ResultState.RUN_HAS_RESULT);
			result.setValue(ComplianceValue.NOT_COMPLIANT);
			result.addComment("The value of dwc:geodeticDatum [unknown] is not compliant, for unknowns the value 'not recorded' may apply.");
        } else { 
        	boolean matched = false;
        	result.setResultState(ResultState.RUN_HAS_RESULT);
        	String lookup = geodeticDatum;
        	// Specification indicates the code must be in the form Authority:number
        	//if (geodeticDatum.matches("^[0-9]+$")) { 
        		// just a number, prepend EPSG: pseudo-namespace
        	//	lookup = "EPSG:" + geodeticDatum;
        	//} 
        	matched = GEOUtil.isValidEPSGCodeForDwCgeodeticDatum(lookup);
        	if (matched) { 
        		result.setValue(ComplianceValue.COMPLIANT);
        		result.addComment("The value of dwc:geodeticDatum is consistent the definition of dwc:geodeticDatum.");
        	} else { 
        		boolean caseMatch = GEOUtil.isValidEPSGCodeForDwCgeodeticDatum(lookup.toUpperCase());
        		if (caseMatch) { 
        			result.setValue(ComplianceValue.NOT_COMPLIANT);
        			result.addComment("The value of dwc:geodeticDatum [" + geodeticDatum + "] appears to be an EPSG code consistent with the definition of dwc:geodeticDatum, but should be ["+lookup.toUpperCase()+"].");
        		} else { 
        			result.setValue(ComplianceValue.NOT_COMPLIANT);
        			result.addComment("The value of dwc:geodeticDatum [" + geodeticDatum + "] is an EPSG code but not consistent with the definition of dwc:geodeticDatum");
        		}
        	} 
        }

        return result;
    }

    /**
     * Proposes an amendment to the value of dwc:geodeticDatum using the bdq:sourceAuthority.
     *
     * Provides: 60 AMENDMENT_GEODETICDATUM_STANDARDIZED
     * Version: 2025-03-03
     *
     * @param geodeticDatum the provided dwc:geodeticDatum to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Amendment(label="AMENDMENT_GEODETICDATUM_STANDARDIZED", description="Proposes an amendment to the value of dwc:geodeticDatum using the bdq:sourceAuthority.")
    @Provides("0345b325-836d-4235-96d0-3b5caf150fc0")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/0345b325-836d-4235-96d0-3b5caf150fc0/2025-03-03")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:geodeticDatum is bdq:Empty; AMENDED the value of dwc:geodeticDatum if it could be unambiguously interpreted as a valid code from the bdq:sourceAuthority (in the form Authority:Number) for a Datum, Ellipsoid or a CRS appropriate for a 2D geographic coordinate in degrees, or as the value 'not recorded'; otherwise NOT_AMENDED. bdq:sourceAuthority = 'EPSG' {[https://epsg.org]} {API for EPSG codes [https://apps.epsg.org/api/swagger/ui/index#/Datum]}")
    public static DQResponse<AmendmentValue> amendmentGeodeticdatumStandardized(
    		@ActedUpon("dwc:geodeticDatum") String geodeticDatum) {
    	DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

    	// Specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:geodeticDatum 
        // is bdq:Empty; AMENDED the value of dwc:geodeticDatum if 
        // it could be unambiguously interpreted as a valid code from 
        // the bdq:sourceAuthority (in the form Authority:Number) for 
        // a Datum, Ellipsoid or a CRS appropriate for a 2D geographic 
        // coordinate in degrees, or as the value "not recorded"; otherwise 
        // NOT_AMENDED 
    	// 
        // bdq:sourceAuthority = "EPSG" {[https://epsg.org]} {API for 
        // EPSG codes [https://apps.epsg.org/api/swagger/ui/index#/Datum]} 
        // 
    	
    	// NOTE: "not recorded" is an acceptable value, per the georeferencing best practicices guide.
    	// However, the suggested "unknown" in the notes on dwc:geodeticDatum should not be used, and
    	// and has a change request filed against Darwin Core for consistency with the best practices guide.
    	
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
        	} else if (!geodeticDatum.equals("not recorded") && geodeticDatum.trim().toUpperCase().replace(" ", "").equals("NOTRECORDED")) {
        		result.setResultState(ResultState.AMENDED);
        		String amended = "not recorded";
        		result.addComment("Corrected provided dwc:geodeticDatum ["+geodeticDatum+"] to ["+amended+"] for value expected by the guide to georeferencing best practices.");
        		Map<String,String> value = new HashMap<String,String>();
        		value.put("dwc:geodeticDatum",amended);
        		result.setValue(new AmendmentValue(value));
        	} else { 
        		if(GEOUtil.isKnownNameForDwCgeodeticDatumCaseInsensitive(geodeticDatum)) {
        			String amended = GEOUtil.getEPSGCodeForString(geodeticDatum);
        			if (amended == null) { 
        				// failover case, shouldn't end up here as test above should catch this.
        				result.setResultState(ResultState.NOT_AMENDED);
        				result.addComment("Provided dwc:geodeticDatum ["+geodeticDatum+"] unable to be interpreted to an EPSG code for a geographic CRS.");
        			} else { 
        				result.setResultState(ResultState.AMENDED);
        				result.addComment("Corrected provided dwc:geodeticDatum ["+geodeticDatum+"] to ["+amended+"] from EPSG code to name mapping assuming 2D geographic CRS.");
        				Map<String,String> value = new HashMap<String,String>();
        				value.put("dwc:geodeticDatum",amended);
        				result.setValue(new AmendmentValue(value));
        			}
        		} else { 
        			result.setResultState(ResultState.NOT_AMENDED);
        			result.addComment("Provided dwc:geodeticDatum ["+geodeticDatum+"] not interpreted to an EPSG code for a geographic CRS.");
        		}
        	}
        }
        
        return result;
    }

    /**
     * Does the ISO country code, determined from the value of dwc:country, equal the value of dwc:countryCode?
     *
     * #62 Validation SingleRecord Consistency: country countrycode inconsistent
     *
     * Provides: VALIDATION_COUNTRY_COUNTRYCODE_CONSISTENT
     * Version: 2022-05-02
     *
     * @param country the provided dwc:country to evaluate
     * @param countryCode the provided dwc:countryCode to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_COUNTRYCOUNTRYCODE_CONSISTENT", description="Does the ISO country code, determined from the value of dwc:country, equal the value of dwc:countryCode?")
    @Provides("b23110e7-1be7-444a-a677-cdee0cf4330c")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/b23110e7-1be7-444a-a677-cdee0cf4330c/2024-09-25")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if either of the terms dwc:country or dwc:countryCode are bdq:Empty; COMPLIANT if the values of dwc:country and dwc:countryCode match national-level country name and matching country code respectively in the bdq:sourceAuthority. bdq:sourceAuthority default = 'The Getty Thesaurus of Geographic Names (TGN)' {[https://www.getty.edu/research/tools/vocabularies/tgn/index.html]}")
    public static DQResponse<ComplianceValue> validationCountryCountrycodeConsistent(
    		@ActedUpon("dwc:country") String country, 
    		@ActedUpon("dwc:countryCode") String countryCode) 
    {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if either 
        // of the terms dwc:country or dwc:countryCode are bdq:Empty; 
        // COMPLIANT if the values of dwc:country and dwc:countryCode 
        // match national-level country name and matching country code 
        // respectively in the bdq:sourceAuthority 
        // bdq:sourceAuthority default = "The Getty Thesaurus of Geographic 
        // Names (TGN)" {[https://www.getty.edu/research/tools/vocabularies/tgn/index.html]} 
        // 
        
        // Notes
        // The country code determination service should be able to match the name of a country 
        // in the original or any language in the source authority. When dwc:countryCode="XZ" to 
        // mark the high seas, country should be empty until a time when a dwc:country="High seas" 
        // or similar is adopted. This test must return NOT_COMPLIANT if there is leading or 
        // trailing whitespace or there are leading or trailing non-printing characters.
        
        if (GEOUtil.isEmpty(countryCode)) { 
        	result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        	result.addComment("the provided value for dwc:countryCode is empty");
        } else if (GEOUtil.isEmpty(country)) { 
        	result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        	result.addComment("the provided value for dwc:country is empty");
        } else if (countryCode.equals("XZ") && !GEOUtil.isEmpty(country)) { 
        		result.setResultState(ResultState.RUN_HAS_RESULT);
        		result.setValue(ComplianceValue.NOT_COMPLIANT);
        		result.addComment("the provided value for dwc:country is not empty, but dwc:countryCode=XZ for high seas");
        // ZZ not included in specification, commenting out.
        //} else if (countryCode.equals("ZZ") && GEOUtil.isEmpty(country)) { 
        //		result.setResultState(ResultState.RUN_HAS_RESULT);
        //		result.setValue(ComplianceValue.COMPLIANT);
        //		result.addComment("the provided value for dwc:country is empty, and dwc:countryCode=ZZ for empty");
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
     * Proposes an amendment or amendments to the values of dwc:minimumElevationInMeters
     * and dwc:maximumElevationInMeters if they can be interpreted from dwc:verbatimElevation.
     *
     * #68 Amendment SingleRecord Completeness: minelevation-maxelevation from
     * verbatim
     *
     * Provides: 68 AMENDMENT_MINELEVATION-MAXELEVATION_FROM_VERBATIM
     * Version: 2024-08-30
     *
     * @param minimumElevationInMeters the provided dwc:minimumElevationInMeters to evaluate as ActedUpon.
     * @param maximumElevationInMeters the provided dwc:maximumElevationInMeters to evaluate as ActedUpon.
     * @param verbatimElevation the provided dwc:verbatimElevation to evaluate as Consulted.
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Amendment(label="AMENDMENT_MINELEVATIONMAXELEVATION_FROM_VERBATIM", description="Proposes an amendment or amendments to the values of dwc:minimumElevationInMeters and dwc:maximumElevationInMeters if they can be interpreted from dwc:verbatimElevation.")
    @Provides("2d638c8b-4c62-44a0-a14d-fa147bf9823d")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/2d638c8b-4c62-44a0-a14d-fa147bf9823d/2024-08-30")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if dwc:minimumElevationInMeters or dwc:maximumElevationInMeters are bdq:NotEmpty or dwc:verbatimElevation is bdq:Empty; FILLED_IN the values of dwc:minimumElevationInMeters and dwc:maximumElevationInMeters if they can be unambiguously interpreted from dwc:verbatimElevation; otherwise NOT_AMENDED. ")
    public static DQResponse<AmendmentValue> amendmentMinelevationMaxelevationFromVerbatim(
    		@ActedUpon("dwc:verbatimElevation") String verbatimElevation,
    		@ActedUpon("dwc:maximumElevationInMeters") String maximumElevationInMeters, 
    		@ActedUpon("dwc:minimumElevationInMeters") String minimumElevationInMeters
    	) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:minimumElevationInMeters 
        // or dwc:maximumElevationInMeters are bdq:NotEmpty or dwc:verbatimElevation 
        // is bdq:Empty; FILLED_IN the values of dwc:minimumElevationInMeters 
        // and dwc:maximumElevationInMeters if they can be unambiguously 
        // interpreted from dwc:verbatimElevation; otherwise NOT_AMENDED 
        //

        if (GEOUtil.isEmpty(verbatimElevation)) {
        	result.addComment("No Value provided for dwc:verbatimElevation");
        	result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        } else if (!GEOUtil.isEmpty(maximumElevationInMeters) || !GEOUtil.isEmpty(minimumElevationInMeters)) { 
        	result.addComment("At least one of dwc:minimumElevationInMeters ["+minimumElevationInMeters+"] and dwc:maximumElevationInMeters ["+maximumElevationInMeters+"] contains a value");
        	result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        } else {
        	if (GEOUtil.isEmpty(maximumElevationInMeters) && GEOUtil.isEmpty(minimumElevationInMeters)) { 
        		
        		// use prevent doubles with very small differences from showing up in results
        		NumberFormat numberFormat8Max = NumberFormat.getNumberInstance();
        		numberFormat8Max.setMaximumFractionDigits(8);
        		numberFormat8Max.setMinimumFractionDigits(0);
        		
        		logger.debug(verbatimElevation);
        		String simplified = verbatimElevation;
        		if (verbatimElevation.matches(".*[mM](in|ax)(imum){0,1}[ ]*[eE]levation.*")) {
        			simplified = verbatimElevation.replaceAll("[mM](in|ax)(imum){0,1}[ ]*[eE]levation", "").trim();
        		}
        		if (simplified.contains("=")) { 
        			simplified = simplified.replace("=", " ");
        		}
        		if (simplified.contains("  ")) { 
        			simplified = simplified.replace("  ", " ");
        		}
        		
        		logger.debug(simplified);
        		
        		// M is ambiguous, probably not meters, likely miles, but not interpreted.
        		if (simplified.matches("^[0-9]+([.]{0,1}[0-9]*){0,1} *(m|m[.]|[mM](eter(s){0,1}))$")) {
        			// Meters, single value e.g. 51 m
        			String cleaned = simplified.replaceAll("[ Mmetrs]+", "").trim();
        			cleaned = cleaned.replaceAll("[.]$","");
        			result.addComment("Interpreted equal minimum and maximum elevations in meters from dwc:verbatimElevation ["+ verbatimElevation +"] interpreted as a elevation range in meters ");
        			Map<String, String> values = new HashMap<>();
        			values.put("dwc:minimumElevationInMeters", cleaned);        		
        			values.put("dwc:maximumElevationInMeters", cleaned);
        			result.setValue(new AmendmentValue(values));
        			result.setResultState(ResultState.FILLED_IN);
        		} else if (simplified.matches("^[0-9]+([.]{0,1}[0-9]*){0,1}[ to-]{0,4}[0-9]+([.]{0,1}[0-9]*){0,1} *(m|m[.]|[mM](eter(s){0,1}))$")) { 
        			//Meters, range, e.g. 1-2m  1.1 to 2.2 m
        			String cleaned = simplified.replaceAll(" ", "");
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
        		} else if (simplified.matches("^[0-9]+([.]{0,1}[0-9]*){0,1} *([fF]eet|[Ff]oot|[fF]t[.]{0,1}){1}$")) { 
        			// Feet, single value, e.g. 15 ft
        			String cleaned = simplified.replaceAll("[ fFote]+", "").trim();
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
        		} else if (simplified.matches("^[0-9]+([.]{0,1}[0-9]*){0,1}[ to-]{0,4}[0-9]+([.]{0,1}[0-9]*){0,1} *([fF]eet|[Ff]oot|[fF]t[.]{0,1}){1}$")) { 
        			//Feet, ranges, e.g. 1.1 to 2.2 ft.
        			String cleaned = simplified.replaceAll(" ", "");
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
        				String meters = numberFormat8Max.format(min * GEOUtil.FEET_TO_METERS);
        				values.put("dwc:minimumElevationInMeters", meters);        		
        				values.put("dwc:maximumElevationInMeters", meters);
        			} else if (min<max) { 
        				String minMeters = numberFormat8Max.format(min * GEOUtil.FEET_TO_METERS);
        				String maxMeters = numberFormat8Max.format(max * GEOUtil.FEET_TO_METERS);
        				values.put("dwc:minimumElevationInMeters", minMeters);        		
        				values.put("dwc:maximumElevationInMeters", maxMeters);
        			} else { 
        				String minMeters = numberFormat8Max.format(min * GEOUtil.FEET_TO_METERS);
        				String maxMeters = numberFormat8Max.format(max * GEOUtil.FEET_TO_METERS);
        				values.put("dwc:minimumElevationInMeters", maxMeters);        		
        				values.put("dwc:maximumElevationInMeters", minMeters);
        			}
        			result.setValue(new AmendmentValue(values));
        			result.setResultState(ResultState.FILLED_IN);
        		} else if (simplified.matches("^[0-9]+([.]{0,1}[0-9]*){0,1} *([yY]ards|[Yy]ard|[yY]d[.]{0,1}}[yY]ds[.]{0,1}){1}$")) {
        			// Yards, single value, e.g. 15 yd
        			String cleaned = simplified.replaceAll("[ Yyards]+", "").trim();
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
        		} else if (simplified.matches("^[0-9]+([.]{0,1}[0-9]*){0,1}[ to-]{0,4}[0-9]+([.]{0,1}[0-9]*){0,1} *([yY]ards|[Yy]ard|[yY]d[.]{0,1}}[yY]ds[.]{0,1}){1}$")) { 
        			//Yards, range e.g. 1000-1500 yd
        			String cleaned = simplified.replaceAll(" ", "");
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
        				String meters = numberFormat8Max.format(min * GEOUtil.YARDS_TO_METERS);
        				values.put("dwc:minimumElevationInMeters", meters);        		
        				values.put("dwc:maximumElevationInMeters", meters);
        			} else if (min<max) { 
        				String minMeters = numberFormat8Max.format(min * GEOUtil.YARDS_TO_METERS);
        				String maxMeters = numberFormat8Max.format(max * GEOUtil.YARDS_TO_METERS);
        				values.put("dwc:minimumElevationInMeters", minMeters);        		
        				values.put("dwc:maximumElevationInMeters", maxMeters);
        			} else { 
        				String minMeters = numberFormat8Max.format(min * GEOUtil.YARDS_TO_METERS);
        				String maxMeters = numberFormat8Max.format(max * GEOUtil.YARDS_TO_METERS);
        				values.put("dwc:minimumElevationInMeters", maxMeters);        		
        				values.put("dwc:maximumElevationInMeters", minMeters);
        			}
        			result.setValue(new AmendmentValue(values));
        			result.setResultState(ResultState.FILLED_IN); 
        		} else if (simplified.matches("^[0-9]+([.]{0,1}[0-9]*){0,1} *([mM]iles|[Mm]ile|[Mm]i[.]{0,1}}){1}$")) {
        			// Miles, single value, e.g. 15 miles
        			String cleaned = simplified.replaceAll("[ mMiles]+", "").trim();
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
        		} else if (simplified.matches("^[0-9]+([.]{0,1}[0-9]*){0,1}[ to-]{0,4}[0-9]+([.]{0,1}[0-9]*){0,1} *([mM]iles|[Mm]ile|[Mm]i[.]{0,1}){1}$")) { 
        			//Miles, range e.g. 1000-1500 mi
        			String cleaned = simplified.replaceAll(" ", "");
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
        			result.setResultState(ResultState.NOT_AMENDED);
        		}
        		
        	} else { 
        		result.addComment("At least one of dwc:minimumElevationInMeters and dwc:maximumElevationInMeters contains a value.");
        		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        	}
        }
        
        return result;
    }

    /**
     * Proposes an amendment to the value of dwc:countryCode if dwc:decimalLatitude and dwc:decimalLongitude fall within a boundary from the bdq:countryShapes that is attributable to a single valid country code.
     *
     * #73 Amendment SingleRecord Completeness: countrycode from coordinates
     *
     * Provides: AMENDMENT_COUNTRYCODE_FROM_COORDINATES
     * Version: 2024-08-18
     *
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate
     * @param countryCode the provided dwc:countryCode to evaluate
     * @param sourceAuthority the spatial source authority to consult.
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Amendment(label="AMENDMENT_COUNTRYCODE_FROM_COORDINATES", description="Proposes an amendment to the value of dwc:countryCode if dwc:decimalLatitude and dwc:decimalLongitude fall within a boundary from the bdq:countryShapes that is attributable to a single valid country code.")
    @Provides("8c5fe9c9-4ba9-49ef-b15a-9ccd0424e6ae")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/8c5fe9c9-4ba9-49ef-b15a-9ccd0424e6ae/2024-08-18")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if either dwc:decimalLatitude or dwc:decimalLongitude is bdq:Empty, or if dwc:countryCode is bdq:NotEmpty; FILLED_IN dwc:countryCode if dwc:decimalLatitude and dwc:decimalLongitude fall within a boundary in the bdq:sourceAuthority that is attributable to a single valid country code; otherwise NOT_AMENDED.. bdq:sourceAuthority default = '10m-admin-1 boundaries UNION with Exclusive Economic Zones' {[https://www.naturalearthdata.com/downloads/10m-cultural-vectors/10m-admin-1-states-provinces/] spatial UNION [https://www.marineregions.org/downloads.php#marbound]}")
    public static DQResponse<AmendmentValue> amendmentCountrycodeFromCoordinates(
    		@Consulted("dwc:decimalLatitude") String decimalLatitude, 
    		@Consulted("dwc:decimalLongitude") String decimalLongitude, 
    		@ActedUpon("dwc:countryCode") String countryCode, 
    		@Parameter(name="bdq:sourceAuthority") String sourceAuthority
    	) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        // Specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if either 
        // dwc:decimalLatitude or dwc:decimalLongitude is bdq:Empty, 
        // or if dwc:countryCode is bdq:NotEmpty; FILLED_IN dwc:countryCode 
        // if dwc:decimalLatitude and dwc:decimalLongitude fall within 
        // a boundary in the bdq:sourceAuthority that is attributable 
        // to a single valid country code; otherwise NOT_AMENDED. 
        // 

        // Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority default = "10m-admin-1 boundaries UNION with Exclusive Economic Zones" 
        // {[https://www.naturalearthdata.com/downloads/10m-cultural-vectors/10m-admin-1-states-provinces/] 
        // spatial UNION [https://www.marineregions.org/downloads.php#marbound]} 

        if (GEOUtil.isEmpty(sourceAuthority)) { 
        	sourceAuthority = "10m-admin-1 boundaries UNION with Exclusive Economic Zones";
        }
        
        try { 
        	GeoRefSourceAuthority sourceAuthorityObject = new GeoRefSourceAuthority(sourceAuthority);
        	if (sourceAuthorityObject.getAuthority().equals(EnumGeoRefSourceAuthority.INVALID)) { 
        		throw new SourceAuthorityException("Invalid Source Authority");
        	}

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

        		String countryCode3 = GEOUtil.getCountryForPoint(decimalLatitude, decimalLongitude);
        		if (countryCode3== null) { 
        			if (GEOUtil.isHighSeas(decimalLatitude, decimalLongitude)) { 
						result.addComment("Propose filling in empty countryCode with value XZ for high seas, which contains the coordinate specified by dwc:decimalLatitude ["+decimalLatitude+"], dwc:decimalLongitude ["+decimalLongitude+"].");
						result.setResultState(ResultState.FILLED_IN);
						Map<String, String> values = new HashMap<>();
        				values.put("dwc:countryCode", "XZ") ;
						result.setValue(new AmendmentValue(values));
					} else { 
						result.addComment("No unique dwc:contryCode found containing the coordinate specified by dwc:decimalLatitude ["+decimalLatitude+"], dwc:decimalLongitude ["+decimalLongitude+"].");
						result.setResultState(ResultState.NOT_AMENDED);
					}
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

        } catch (SourceAuthorityException e) { 
        	result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);
        	result.addComment("Error with specified Source Authority: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * Is there a value in dwc:geodeticDatum?
     *
     * #78 Validation SingleRecord Completeness: geodeticdatum empty
     *
     * Provides: VALIDATION_GEODETICDATUM_NOTEMPTY
     * Version: 2023-09-18
     *
     * @param geodeticDatum the provided dwc:geodeticDatum to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_GEODETICDATUM_NOTEMPTY", description="Is there a value in dwc:geodeticDatum?")
    @Provides("239ec40e-a729-4a8e-ba69-e0bf03ac1c44")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/239ec40e-a729-4a8e-ba69-e0bf03ac1c44/2023-09-18")
    @Specification("COMPLIANT if dwc:geodeticDatum is bdq:NotEmpty; otherwise NOT_COMPLIANT. ")
    public static DQResponse<ComplianceValue> validationGeodeticdatumNotempty(@ActedUpon("dwc:geodeticDatum") String geodeticDatum) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // COMPLIANT if dwc:geodeticDatum is bdq:NotEmpty; otherwise 
        // NOT_COMPLIANT 
        
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
     * Provides: 79 VALIDATION_DECIMALLATITUDE_INRANGE
     * Version: 2023-09-18
     *
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_DECIMALLATITUDE_INRANGE", description="Is the value of dwc:decimalLatitude a number between -90 and 90 inclusive?")
    @Provides("b6ecda2a-ce36-437a-b515-3ae94948fe83")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/b6ecda2a-ce36-437a-b515-3ae94948fe83/2023-09-18")
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
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/1bf0e210-6792-4128-b8cc-ab6828aa4871/2023-06-20")
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
     * Version: 2023-09-18
     *
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_DECIMALLONGITUDE_NOTEMPTY", description="Is there a value in dwc:decimalLongitude?")
    @Provides("9beb9442-d942-4f42-8b6a-fcea01ee086a")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/9beb9442-d942-4f42-8b6a-fcea01ee086a/2023-09-18")
    @Specification("COMPLIANT if dwc:decimalLongitude is bdq:NotEmpty; otherwise NOT_COMPLIANT. ")
    public static DQResponse<ComplianceValue> validationDecimallongitudeNotempty(
    		@ActedUpon("dwc:decimalLongitude") String decimalLongitude) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // COMPLIANT if dwc:decimalLongitude is bdq:NotEmpty; otherwise 
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
     * Version: 2024-11-10
     *
     * @param countryCode the provided dwc:countryCode to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_COUNTRYCODE_NOTEMPTY", description="Is there a value in dwc:countryCode?")
    @Provides("853b79a2-b314-44a2-ae46-34a1e7ed85e4")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/853b79a2-b314-44a2-ae46-34a1e7ed85e4/2024-11-10")
    @Specification("COMPLIANT if dwc:countryCode is bdq:NotEmpty; otherwise NOT_COMPLIANT.")
    public static DQResponse<ComplianceValue> validationCountrycodeNotempty(
    		@ActedUpon("dwc:countryCode") String countryCode) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // COMPLIANT if dwc:countryCode is bdq:NotEmpty; otherwise NOT_COMPLIANT 
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
     * Proposes an amendment to fill in dwc:geodeticDatum using a parmeterized value if the dwc:geodeticDatum is empty.
     * 
     * If dwc:coordinateUncertaintyInMeters is not empty and there are not empty values for dwc:latitude and dwc:longitude,
     * amend dwc:coordinateUncertaintyInMeters by adding a maximum datum shift.
     *
     * Provides: 102 AMENDMENT_GEODETICDATUM_ASSUMEDDEFAULT
     * Version: 2024-11-12
     *
     * @param coordinateUncertaintyInMeters the provided dwc:cooordinateUncertaintyInMeters to evaluate.
     * @param geodeticDatum the provided dwc:geodeticDatum to evaluate
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate
     * @param defaultGeodeticDatum to use as default, if not specified, uses EPSG:4326
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Amendment(label="AMENDMENT_GEODETICDATUM_ASSUMEDDEFAULT", description="Proposes an amendment to fill in dwc:geodeticDatum using a prameterized value if the dwc:geodeticDatum is empty.")
    @Provides("7498ca76-c4d4-42e2-8103-acacccbdffa7")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/7498ca76-c4d4-42e2-8103-acacccbdffa7/2024-11-12")
    @Specification("If dwc:geodeticDatum is bdq:Empty, fill in dwc:geodeticDatum using the value of bdq:defaultGeodeticDatum, report FILLED_IN and, if dwc:coordinateUncertaintyInMeters, dwc:decimalLatitude and dwc:decimalLongitude are bdq:NotEmpty, amend the value of dwc:coordinateUncertaintyInMeters by adding the maximum datum shift between the specified bdq:defaultGeodeticDatum and any other datum at the provided dwc:decimalLatitude and dwc:decimalLongitude and instead report AMENDED; otherwise NOT_AMENDED.. bdq:defaultGeodeticDatum default = 'EPSG:4326'")
    public static DQResponse<AmendmentValue> amendmentGeodeticdatumAssumeddefault(
    		@ActedUpon("dwc:coordinateUncertaintyInMeters") String coordinateUncertaintyInMeters, 
    		@ActedUpon("dwc:geodeticDatum") String geodeticDatum,
    		@ActedUpon("dwc:decimalLatitude") String decimalLatitude,
    		@ActedUpon("dwc:decimalLongitude") String decimalLongitude,
    		@Parameter(name="bdq:defaultGeodeticDatum") String defaultGeodeticDatum) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        // Specification
        // If dwc:geodeticDatum is bdq:Empty, fill in dwc:geodeticDatum 
        // using the value of bdq:defaultGeodeticDatum, report FILLED_IN 
        // and, if dwc:coordinateUncertaintyInMeters, dwc:decimalLatitude 
        // and dwc:decimalLongitude are bdq:NotEmpty, amend the value 
        // of dwc:coordinateUncertaintyInMeters by adding the maximum 
        // datum shift between the specified bdq:defaultGeodeticDatum 
        // and any other datum at the provided dwc:decimalLatitude 
        // and dwc:decimalLongitude and instead report AMENDED; otherwise 
        // NOT_AMENDED. 

        // Parameters. This test is defined as parameterized.
        // bdq:defaultGeodeticDatum default = "EPSG:4326" 

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
     * Version: 2023-09-18
     *
     * @param minimumDepthInMeters the provided dwc:minimumDepthInMeters to evaluate
     * @param minimumValidDepthInMeters a {@link java.lang.Double} object.
     * @param maximumValidDepthInMeters a {@link java.lang.Double} object.
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_MINDEPTH_INRANGE", description="Is the value of dwc:minimumDepthInMeters within the Parameter range?")
    @Provides("04b2c8f3-c71b-4e95-8e43-f70374c5fb92")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/04b2c8f3-c71b-4e95-8e43-f70374c5fb92/2023-09-18")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if dwc:minimumDepthInMeters is bdq:Empty, or the value is not interpretable as number greater than or equal to zero; COMPLIANT if the value of dwc:minimumDepthInMeters is within the range of bdq:minimumValidDepthInMeters to bdq:maximumValidDepthInMeters inclusive; otherwise NOT_COMPLIANT. bdq:minimumValidDepthInMeters default='0',bdq:maximumValidDepthInMeters default='11000'")
    public static DQResponse<ComplianceValue> validationMindepthInrange(
    		@ActedUpon("dwc:minimumDepthInMeters") String minimumDepthInMeters,
    		@Parameter(name="bdq:minimumValidDepthInMeters") Double minimumValidDepthInMeters,
    		@Parameter(name="bdq:maximumValidDepthInMeters") Double maximumValidDepthInMeters
    		) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:minimumDepthInMeters 
        // is bdq:Empty, or the value is not interpretable as number 
        // greater than or equal to zero; COMPLIANT if the value of 
        // dwc:minimumDepthInMeters is within the range of bdq:minimumValidDepthInMeters 
        // to bdq:maximumValidDepthInMeters inclusive; otherwise NOT_COMPLIANT 
        // 

        // Parameters. This test is defined as parameterized.
        // bdq:minimumValidDepthInMeters default="0",
        // bdq:maximumValidDepthInMeters default="11000" 
        
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
     * Version: 2023-09-18
     *
     * @param minimumElevationInMeters the provided dwc:minimumElevationInMeters to evaluate
     * @param maximumElevationInMeters the provided dwc:maximumElevationInMeters to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_MINELEVATION_LESSTHAN_MAXELEVATION", description="Is the value of dwc:minimumElevationInMeters a number less than or equal to the value of dwc:maximumElevationInMeters?")
    @Provides("d708526b-6561-438e-aa1a-82cd80b06396")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/d708526b-6561-438e-aa1a-82cd80b06396/2023-09-18")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if dwc:maximumlevationInMeters or dwc:minimumElevationInMeters is bdq:Empty, or if either is not a number; COMPLIANT if the value of dwc:minimumElevationInMeters is a number less than or equal to the value of the number dwc:maximumElevationInMeters, otherwise NOT_COMPLIANT. ")
    public static DQResponse<ComplianceValue> validationMinelevationLessthanMaxelevation(
    		@ActedUpon("dwc:minimumElevationInMeters") String minimumElevationInMeters, 
    		@ActedUpon("dwc:maximumElevationInMeters") String maximumElevationInMeters) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();
        
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:maximumlevationInMeters 
        // or dwc:minimumElevationInMeters is bdq:Empty, or if either 
        // is not a number; COMPLIANT if the value of dwc:minimumElevationInMeters 
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
     * Version: 2023-09-18
     *
     * @param coordinateUncertaintyInMeters the provided dwc:coordinateUncertaintyInMeters to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_COORDINATEUNCERTAINTY_INRANGE", description="Is the value of dwc:coordinateUncertaintyInMeters a number between 1 and 20,037,509?")
    @Provides("c6adf2ea-3051-4498-97f4-4b2f8a105f57")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/c6adf2ea-3051-4498-97f4-4b2f8a105f57/2023-09-18")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if dwc:coordinateUncertaintyInMeters is bdq:Empty; COMPLIANT if the value of  dwc:coordinateUncertaintyInMeters is interpreted as a number between 1 and 20037509 inclusive; otherwise NOT_COMPLIANT. ")
    public static DQResponse<ComplianceValue> validationCoordinateuncertaintyInrange(
    		@ActedUpon("dwc:coordinateUncertaintyInMeters") String coordinateUncertaintyInMeters) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:coordinateUncertaintyInMeters 
        // is bdq:Empty; COMPLIANT if the value of dwc:coordinateUncertaintyInMeters 
        // is interpreted as a number between 1 and 20037509 inclusive; 
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
     * Provides: #112 VALIDATION_MAXELEVATION_INRANGE
     * Version: 2023-09-18
     *
     * @param maximumElevationInMeters the provided dwc:maximumElevationInMeters to evaluate
     * @param minimumValidElevationInMeters minimum valid value to test against, if null, defaults to -430
     * @param maximumValidElevationInMeters maximum valid value to test against, if null, defaults to 8550
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_MAXELEVATION_INRANGE", description="Is the value of dwc:maximumElevationInMeters of a single record within a valid range")
    @Provides("c971fe3f-84c1-4636-9f44-b1ec31fd63c7")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/c971fe3f-84c1-4636-9f44-b1ec31fd63c7/2023-09-18")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if dwc:maximumElevationInMeters is EMPTY or the value cannot be interpreted as a number; COMPLIANT if the value of dwc:maximumElevationInMeters is within the range of bdq:minimumValidElevationInMeters to bdq:maximumValidElevationInMeters inclusive; otherwise NOT_COMPLIANT bdq:minimumValidElevationInMeters default = '-430',bdq:maximumValidElevationInMeters default = '8850'")
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

        // Parameters. This test is defined as parameterized.
        // bdq:minimumValidElevationInMeters,bdq:maximumValidElevationInMeters
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
     * Version: 2023-09-18
     *
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_DECIMALLATITUDE_NOTEMPTY", description="Is there a value in dwc:decimalLatitude?")
    @Provides("7d2485d5-1ba7-4f25-90cb-f4480ff1a275")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/7d2485d5-1ba7-4f25-90cb-f4480ff1a275/2023-09-18")
    @Specification("COMPLIANT if dwc:decimalLatitude is bdq:NotEmpty; otherwise NOT_COMPLIANT. ")
    public static DQResponse<ComplianceValue> validationDecimallatitudeNotempty(
    		@ActedUpon("dwc:decimalLatitude") String decimalLatitude) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // COMPLIANT if dwc:decimalLatitude is bdq:NotEmpty; otherwise 
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
     * Provides: 187 VALIDATION_MAXDEPTH_INRANGE
     * Version: 2023-09-18
     *
     * @param maximumDepthInMeters the provided dwc:maximumDepthInMeters to evaluate
     * @param minimumValidDepthInMeters the minimum valid depth, defaults to 0 if null.
     * @param maximumValidDepthInMeters the maximum valid depth, defaults to 110000 if null.
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_MAXDEPTH_INRANGE", description="Is the value of dwc:maximumDepthInMeters within the Parameter range?")
    @Provides("3f1db29a-bfa5-40db-9fd1-fde020d81939")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/3f1db29a-bfa5-40db-9fd1-fde020d81939/2023-09-18")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if dwc:maximumDepthInMeters is bdq:Empty or is not interpretable as a number greater than or equal to zero; COMPLIANT if the value of dwc:maximumDepthInMeters is within the range of bdq:minimumValidDepthInMeters to bdq:maximumValidDepthInMeters inclusive; otherwise NOT_COMPLIANT. bdq:minimumValidDepthInMeters default='0',bdq:maximumValidDepthInMeters default='11000'")
    public static DQResponse<ComplianceValue> validationMaxdepthInrange(
    		@ActedUpon("dwc:maximumDepthInMeters") String maximumDepthInMeters, 
    		@Parameter(name="bdq:minimumValidDepthInMeters") Double minimumValidDepthInMeters,
    		@Parameter(name="bdq:maximumValidDepthInMeters") Double maximumValidDepthInMeters
    		) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:maximumDepthInMeters 
        // is bdq:Empty or is not interpretable as a number greater 
        // than or equal to zero; COMPLIANT if the value of dwc:maximumDepthInMeters 
        // is within the range of bdq:minimumValidDepthInMeters to 
        // bdq:maximumValidDepthInMeters inclusive; otherwise NOT_COMPLIANT 
        // 

        // Parameters. This test is defined as parameterized.
        // bdq:minimumValidDepthInMeters
        // bdq:maximumValidDepthInMeters
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
     * Does the value of dwc:stateProvince occur in the bdq:sourceAuthority?
     *
     * Provides: #199 VALIDATION_STATEPROVINCE_FOUND
     * Version: 2024-09-18
     *
     * @param stateProvince the provided dwc:stateProvince to evaluate
     * @param sourceAuthority the provided parameter bdq:sourceAuthority use null for default value.
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_STATEPROVINCE_FOUND", description="Does the value of dwc:stateProvince occur in the bdq:sourceAuthority?")
    @Provides("4daa7986-d9b0-4dd5-ad17-2d7a771ea71a")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/4daa7986-d9b0-4dd5-ad17-2d7a771ea71a/2024-09-18")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:stateProvince is bdq:Empty; COMPLIANT if the value of dwc:stateProvince occurs as an administrative entity that is a child to at least one entity representing an ISO 3166 country-like entity in the bdq:sourceAuthority; otherwise NOT_COMPLIANT. bdq:sourceAuthority default = 'The Getty Thesaurus of Geographic Names (TGN)' {[https://www.getty.edu/research/tools/vocabularies/tgn/index.html]}")
    public static DQResponse<ComplianceValue> validationStateprovinceFound(
    		@ActedUpon("dwc:stateProvince") String stateProvince,
    		@Parameter(name="bdq:sourceAuthority") String sourceAuthority
    		) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:stateProvince 
        // is bdq:Empty; COMPLIANT if the value of dwc:stateProvince 
        // occurs as an administrative entity that is a child to at 
        // least one entity representing an ISO 3166 country-like entity 
        // in the bdq:sourceAuthority; otherwise NOT_COMPLIANT 
        // 

        // Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority default = "The Getty Thesaurus of Geographic 
        // Names (TGN)" {[https://www.getty.edu/research/tools/vocabularies/tgn/index.html]} 

        if (sourceAuthority==null) { 
        	sourceAuthority = GettyLookup.GETTY_TGN;
        }

        try { 
        	GeoRefSourceAuthority sourceAuthorityObject = new GeoRefSourceAuthority(sourceAuthority);
        	if (sourceAuthorityObject.getAuthority().equals(EnumGeoRefSourceAuthority.INVALID)) { 
        		throw new SourceAuthorityException("Invalid Source Authority");
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
        			throw new SourceAuthorityException("Unsupported bdq:sourceAuthority [" + sourceAuthority + "]");
        		}
        	}
        } catch (SourceAuthorityException e) {
        	result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);
        	result.addComment("Error with specified Source Authority: " + e.getMessage());
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
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/e654f562-44f8-43fd-983b-2aaba4c6dda9/2023-09-18")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if the terms dwc:country or dwc:stateProvince are EMPTY; COMPLIANT if the value of dwc:stateProvince occurs as an administrative entity that is a child to the entity matching the value of dwc:country in the bdq:sourceAuthority, and the match to dwc:country is an ISO country-like entity in the bdq:sourceAuthority; otherwise NOT_COMPLIANT  	bdq:sourceAuthority default = \"The Getty Thesaurus of Geographic Names (TGN)\" {[https://www.getty.edu/research/tools/vocabularies/tgn/index.html]}")
    public static DQResponse<ComplianceValue> validationCountrystateprovinceConsistent(
    		@ActedUpon("dwc:country") String country, 
    		@ActedUpon("dwc:stateProvince") String stateProvince,
    		@Parameter(name="bdq:sourceAuthority") String sourceAuthority
    		) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
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
        					logger.debug("[" + primaryParentage + "]");
        					
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
        			throw new SourceAuthorityException("Unsupported Source Authority ["+sourceAuthority+"]");
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
     * Provides: 201 VALIDATION_COUNTRYSTATEPROVINCE_UNAMBIGUOUS
     * Version: 2024-09-18
     *
     * @param country the provided dwc:country to evaluate
     * @param stateProvince the provided dwc:stateProvince to evaluate
     * @param sourceAuthority the source authority to consult, use null for the default value.
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_COUNTRYSTATEPROVINCE_UNAMBIGUOUS", description="Is the combination of the values of the terms dwc:country, dwc:stateProvince unique in the bdq:sourceAuthority?")
    @Provides("d257eb98-27cb-48e5-8d3c-ab9fca4edd11")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/d257eb98-27cb-48e5-8d3c-ab9fca4edd11/2024-09-18")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if the terms dwc:country and dwc:stateProvince are bdq:Empty; COMPLIANT if the combination of values of dwc:country and dwc:stateProvince are unambiguously resolved to a single result with a child-parent relationship in the bdq:sourceAuthority and the entity matching the value of dwc:country in the bdq:sourceAuthority is an ISO 3166 country-like administrative entity in the bdq:sourceAuthority; otherwise NOT_COMPLIANT. bdq:sourceAuthority default = 'The Getty Thesaurus of Geographic Names (TGN)' {[https://www.getty.edu/research/tools/vocabularies/tgn/index.html]}")
    public static DQResponse<ComplianceValue> validationCountrystateprovinceUnambiguous(
    		@ActedUpon("dwc:country") String country, 
    		@ActedUpon("dwc:stateProvince") String stateProvince,
    		@Parameter(name="bdq:sourceAuthority") String sourceAuthority
    ) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if the 
        // terms dwc:country and dwc:stateProvince are bdq:Empty; COMPLIANT 
        // if the combination of values of dwc:country and dwc:stateProvince 
        // are unambiguously resolved to a single result with a child-parent 
        // relationship in the bdq:sourceAuthority and the entity matching 
        // the value of dwc:country in the bdq:sourceAuthority is an 
        // ISO 3166 country-like administrative entity in the bdq:sourceAuthority; 
        // otherwise NOT_COMPLIANT 
        // 

        // Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority
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
        				logger.debug(stateProvince);
        				if (lookup.lookupUniquePrimary(stateProvince)) {  
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
        					// logger.debug(lookup.lookupPrimary(stateProvince));
        					List<GettyTGNObject> primaryMatches = lookup.getPrimaryObjects(stateProvince);
        					if (primaryMatches!=null && primaryMatches.size()>0) {  
        						if (preferredCountry==null) { 
        							preferredCountry = countryToLookup;
        						}
        						Iterator<GettyTGNObject> ipm  = primaryMatches.iterator();
        						int matchCount = 0;
        						boolean hasSomeParentage = false;
        						StringBuffer primaryParentages = new StringBuffer();
        						String primaryParentage = "";
        						while (ipm.hasNext()) { 
        							GettyTGNObject primaryObject = ipm.next();
        							primaryParentage = primaryObject.getParentageString();
        							logger.debug(primaryParentage);
        							if (primaryParentage!=null) { 
        								hasSomeParentage = true;
        								if (primaryParentage.contains(preferredCountry)) { 
        									matchCount++;
        									primaryParentages.append(primaryParentage);
        								}
        							} 
        						}
        						if (hasSomeParentage==false) { 
        							result.setResultState(ResultState.RUN_HAS_RESULT);
        							result.setValue(ComplianceValue.NOT_COMPLIANT);
        							result.addComment("Parentage not found for dwc:stateProvince ["+stateProvince+"] in the Getty TGN");
        						} else { 
        							if (matchCount==1) { 
        								result.setResultState(ResultState.RUN_HAS_RESULT);
        								result.setValue(ComplianceValue.COMPLIANT);
        								result.addComment("The dwc:country ["+country+"] as ["+preferredCountry+"] was found in the parentage ["+primaryParentage+"] of dwc:stateProvince ["+stateProvince+"] in the Getty TGN");
        							} else if (matchCount==0) { 
        								result.setResultState(ResultState.RUN_HAS_RESULT);
        								result.setValue(ComplianceValue.NOT_COMPLIANT);
        								result.addComment("The combination of dwc:country ["+country+"] as ["+preferredCountry+"] with dwc:stateProvince ["+stateProvince+"] was not found in the Getty TGN");
        							} else { 
        								result.setResultState(ResultState.RUN_HAS_RESULT);
        								result.setValue(ComplianceValue.NOT_COMPLIANT);
        								result.addComment("Non-unique match");
        								result.addComment("The dwc:country ["+country+"] as ["+preferredCountry+"] was found in the "+matchCount+" parentages ["+primaryParentages.toString()+"] of dwc:stateProvince ["+stateProvince+"] in the Getty TGN");
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
        			throw new SourceAuthorityException("Unsupported Source Authority: ["+sourceAuthority+"]");
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
     * Does the marine/non-marine biome of a taxon from the bdq:sourceAuthority
     * match the biome at the location given by the coordinates?
     *
     * Provides: 51 VALIDATION_COORDINATESTERRESTRIALMARINE_CONSISTENT
     * Version: 2024-08-30
     *
     * @param decimalLatitude  the provided dwc:decimalLatitude to evaluate as ActedUpon.
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate as ActedUpon.
     * @param scientificName   the provided dwc:scientificName to evaluate as Consulted.
     * @param taxonIsMarine the provided parameter bdq:taxonIsMarine use null for default value.
     * @param geospatialLand the provided parameter bdq:geospatialLand use null for default value.
     * @param assumptionOnUnknownBiome the provided parameter bdq:assumptionOnUnknownBiome use null for default value.
     * @param spatialBufferInMeters the provided parameter bdq:spatialBufferInMeters use null for default value.
     * @return DQResponse the response of type ComplianceValue to return
     */
    @Validation(label="VALIDATION_COORDINATESTERRESTRIALMARINE_CONSISTENT", description="Does the marine/non-marine biome of a taxon from the bdq:sourceAuthority match the biome at the location given by the coordinates?")
    @Provides("b9c184ce-a859-410c-9d12-71a338200380")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/b9c184ce-a859-410c-9d12-71a338200380/2024-08-30")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if either bdq:taxonIsMarine or bdq:geospatialLand are not available; INTERNAL_PREREQUISITES_NOT_MET if (1) dwc:scientificName is bdq:Empty or (2)  the values of dwc:decimalLatitude or dwc:decimalLongitude are bdq:Empty or (3) if bdq:assumptionOnUnknownBiome is noassumption and the marine/nonmarine status of the taxon is not interpretable from bdq:taxonIsMarine; COMPLIANT if (1) the taxon marine/nonmarine status from bdq:taxonIsMarine matches the marine/nonmarine status of dwc:decimalLatitude and dwc:decimalLongitude on the boundaries given by bdq:geospatialLand plus an exterior buffer given by bdq:spatialBufferInMeters or (2)  if the marine/nonmarine status of the taxon is not interpretable from bdq:taxonIsMarine and bdq:assumptionOnUnknownBiome matches the marine/nonmarine status of dwc:decimalLatitude and dwc:decimalLongitude on the boundaries given by bdq:geospatialLand plus an exterior buffer given by bdq:spatialBufferInMeters; otherwise NOT_COMPLIANT. bdq:taxonIsMarine default = 'World Register of Marine Species (WoRMS)' {[https://www.marinespecies.org/]} {Web service [https://www.marinespecies.org/aphia.php?p=webservice]},bdq:geospatialLand default = 'Union of NaturalEarth 10m-physical-vectors for Land and NaturalEarth Minor Islands' {[https://www.naturalearthdata.com/http//www.naturalearthdata.com/download/10m/physical/ne_10m_land.zip], [https://www.naturalearthdata.com/http//www.naturalearthdata.com/download/10m/physical/ne_10m_minor_islands.zip]},bdq:spatialBufferInMeters default = '3000',bdq:assumptionOnUnknownBiome default = 'noassumption'")
    public static DQResponse<ComplianceValue> validationCoordinatesTerrestrialmarine(
        @ActedUpon("dwc:decimalLatitude") String decimalLatitude, 
        @ActedUpon("dwc:decimalLongitude") String decimalLongitude, 
        @Consulted("dwc:scientificName") String scientificName,
        @Parameter(name="bdq:taxonIsMarine") String taxonIsMarine,
        @Parameter(name="bdq:geospatialLand") String geospatialLand,
        @Parameter(name="bdq:assumptionOnUnknownBiome") String assumptionOnUnknownBiome,
        @Parameter(name="bdq:spatialBufferInMeters") String spatialBufferInMeters
    ) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // EXTERNAL_PREREQUISITES_NOT_MET if either bdq:taxonIsMarine 
        // or bdq:geospatialLand are not available; INTERNAL_PREREQUISITES_NOT_MET 
        // if (1) dwc:scientificName is bdq:Empty or (2) the values 
        // of dwc:decimalLatitude or dwc:decimalLongitude are bdq:Empty 
        // or (3) if bdq:assumptionOnUnknownBiome is noassumption and 
        // the marine/nonmarine status of the taxon is not interpretable 
        // from bdq:taxonIsMarine; COMPLIANT if (1) the taxon marine/nonmarine 
        // status from bdq:taxonIsMarine matches the marine/nonmarine 
        // status of dwc:decimalLatitude and dwc:decimalLongitude on 
        // the boundaries given by bdq:geospatialLand plus an exterior 
        // buffer given by bdq:spatialBufferInMeters or (2) if the 
        // marine/nonmarine status of the taxon is not interpretable 
        // from bdq:taxonIsMarine and bdq:assumptionOnUnknownBiome 
        // matches the marine/nonmarine status of dwc:decimalLatitude 
        // and dwc:decimalLongitude on the boundaries given by bdq:geospatialLand 
        // plus an exterior buffer given by bdq:spatialBufferInMeters; 
        // otherwise NOT_COMPLIANT 
        
        // Parameters. This test is defined as parameterized.
        // bdq:taxonIsMarine,bdq:geospatialLand,bdq:spatialBufferInMeters,
        // bdq:assumptionOnUnknownBiome
        //
        // bdq:taxonIsMarine default = "World Register of Marine Species (WoRMS)" 
        // {[https://www.marinespecies.org/]} {Web service 
        // [https://www.marinespecies.org/aphia.php?p=webservice]},
        // bdq:geospatialLand default = "Union of NaturalEarth 10m-physical-vectors for Land and NaturalEarth Minor Islands" 
        // {[https://www.naturalearthdata.com/http//www.naturalearthdata.com/download/10m/physical/ne_10m_land.zip], 
        // [https://www.naturalearthdata.com/http//www.naturalearthdata.com/download/10m/physical/ne_10m_minor_islands.zip]},
        // bdq:spatialBufferInMeters default = "3000",
        // bdq:assumptionOnUnknownBiome default = "noassumption" 

        if (GEOUtil.isEmpty(taxonIsMarine)) { 
        	taxonIsMarine = "World Register of Marine Species (WoRMS)";
        }
        if (GEOUtil.isEmpty(geospatialLand)) { 
        	geospatialLand = "Union of NaturalEarth 10m-physical-vectors for Land and NaturalEarth Minor Islands";
        }
        if (GEOUtil.isEmpty(spatialBufferInMeters)) { 
        	spatialBufferInMeters = "3000";
        }
        if (GEOUtil.isEmpty(assumptionOnUnknownBiome)) { 
        	assumptionOnUnknownBiome = "noassumption";
        }

        try { 
        	logger.debug(geospatialLand);
        	logger.debug(taxonIsMarine);
        	GeoRefSourceAuthority sourceAuthoritySpatial = new GeoRefSourceAuthority(geospatialLand);
        	SciNameSourceAuthority sourceAuthorityTaxon = new SciNameSourceAuthority(taxonIsMarine);
        	if (sourceAuthoritySpatial.getAuthority().equals(EnumGeoRefSourceAuthority.INVALID)) { 
        		throw new SourceAuthorityException("Invalid Source Authority");
        	}
        	if (sourceAuthorityTaxon.getAuthority().equals(EnumSciNameSourceAuthority.INVALID)) { 
        		throw new SourceAuthorityException("Invalid Source Authority");
        	}
        	if (GEOUtil.isEmpty(scientificName)) { 
        		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        		result.addComment("The value provided for dwc:scientificName is empty");
        	} else { 
        		
        		Boolean marine = null;
        		Boolean nonMarine = null;
        		try {
					// TaxonomicDataApi wormsService =  new TaxonomicDataApi();
					// wormsService.setApiClient(new ApiClient());
					WoRMSService wormsService = new WoRMSService(false);
				
					//List<AphiaRecord> results = wormsService.aphiaRecordsByName(scientificName, false, false, 1);
					
					NameUsage toValidate = new NameUsage();
	        		try { 
	        			NameAuthorshipParse parsedName = SciNameUtils.getNameWithoutAuthorship(scientificName);
	        			toValidate.setScientificName(parsedName.getNameWithoutAuthorship());
	        			toValidate.setAuthorship(parsedName.getAuthorship());
	        			logger.debug(parsedName.getNameWithoutAuthorship());
	        			logger.debug(parsedName.getAuthorship());
	        		} catch (UnparsableNameException e) { 
	        			result.addComment("Unable to parse authorship out of provided scientificName, trying GNI and GBIF parser service with ["+scientificName+"].");
	        			logger.debug(e.getMessage(), e);
	        			// If local parse fails, try a parse via the GBIF service.
	        			// This supports embedding the library in coldfusion for MCZbase, where 
	        			// there is a library version conflict with the guava version provided
	        			// by coldfusion and the version needed by the GBIF name parser.
	        			try { 
	        				NameAuthorshipParse parsedName = GBIFService.parseAuthorshipFromNameString(scientificName);
	        				toValidate.setScientificName(parsedName.getNameWithoutAuthorship());
	        				toValidate.setAuthorship(parsedName.getAuthorship());
	        			} catch (Exception ex) {
	        				// simple failover handler, try the full name
	        				result.addComment("Unable to parse authorship out of provided scientificName, looking up full name.");
	        				result.addComment(e.getMessage());	
	        				toValidate.setScientificName(scientificName);
	        			} 
	        		} catch (Exception e) {
	        			// could be thrown from parser.close()
	        			logger.error(e.getMessage(), e);
	        			if (GEOUtil.isEmpty(toValidate.getScientificName())) { 
	        				toValidate.setScientificName(scientificName);
	        			}
	        		}
					
	        		logger.debug(toValidate.getScientificName());
					NameUsage validationResponse =  wormsService.validate(toValidate);
					logger.debug(validationResponse==null);
					
					if (validationResponse!=null) { 
						// We got at least one result
						logger.debug(validationResponse.getMatchDescription());
						boolean matched = false;
						if (validationResponse.getMatchDescription().equals(NameComparison.MATCH_EXACT)) { 
							// exact match 
							matched = true;
						} else if (NameComparison.isPlausibleAuthorMatch(validationResponse.getMatchDescription())) {
							// match where author matching is plausibly the same name, includes author added
							matched = true;
						} else if (validationResponse.getMatchDescription().equals(NameComparison.SNMATCH_SUBGENUS)) { 
							// matching except subgenus added in one case
							matched = true;
						} else if (
							validationResponse.getMatchDescription().startsWith(NameComparison.MATCH_MULTIPLE) 
							&& GEOUtil.isEmpty(toValidate.getAuthorship())
							&& validationResponse.getNameMatchDescription().equals(NameComparison.MATCH_EXACT)
						) { 
							matched = true;
						}
						
						if (matched) { 
							WoRMSService svs = new WoRMSService(false);
							Map<String,String> habitats = validationResponse.getExtension();
							
							if (habitats==null) {
								logger.debug("habitats returned as null");
							}
							if (habitats!=null) { 
								logger.debug(habitats.size());
								// If brackish, set both marine and nonMarine as true, points near boundaries
								if (habitats.containsKey("brackish") && habitats.get("brackish").equals("true")) {
									logger.debug("brackish=true, thus marine=true and nonMarine=true");
									marine = true;
									nonMarine = true;
								} else { 
									// marine flags marine
									if (habitats.containsKey("marine") && habitats.get("marine")=="true") {
										marine = true;
										logger.debug("marine=true");
									} else if (habitats.containsKey("marine") && habitats.get("marine").equals("false")) {
										marine = false;
										logger.debug("marine=false");
									}
									// terrestrial and freshwater flag non-marine
									if (habitats.containsKey("freshwater") && habitats.get("freshwater")=="true") {
										nonMarine = true;
										logger.debug("nonMarine=true");
									} else if (habitats.containsKey("freshwater") && habitats.get("freshwater").equals("false")) {
										if (habitats.containsKey("terrestrial") && habitats.get("terrestrial").equals("true")) { 
											nonMarine = true;
											logger.debug("nonMarine=true (freshwater false, terrestrial true)");
										} else if (habitats.containsKey("terrestrial") && habitats.get("terrestrial").equals("false")) {
											nonMarine = false;
											logger.debug("nonMarine=false (freshwater and terrestrial both false)");
										}
									}
								} 
							}
						}
					} else {
						logger.debug("Response from name lookup is null");
					}
				} catch (IOException e) {
					throw new SourceAuthorityException("Error accessing Source Authority: " + e.getMessage());
				} catch (org.filteredpush.qc.sciname.services.ServiceException e) {
					throw new SourceAuthorityException("Error accessing Source Authority: " + e.getMessage());
				}
        		
        		if (marine==null && nonMarine==null) { 
        			if (assumptionOnUnknownBiome.toLowerCase().equals("marine")) { 
        				marine = true;
        				nonMarine = false;
        			}
        			if (assumptionOnUnknownBiome.toLowerCase().equals("nonmarine")) { 
        				marine = false;
        				nonMarine = true;
        			}
        		}
        		logger.debug(assumptionOnUnknownBiome);
        		logger.debug("marine=" + marine);
        		logger.debug("nonMarine="+ nonMarine);
        		
        		// For now, fail if both are null, may be stronger to fail if either is, see assumption below.
        		if (marine==null && nonMarine==null) { 
        			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        			result.addComment("Unable to tell from sourceAuthority taxonIsMarine if the provided dwc:scientificName ["+scientificName+"] is a marine or non-marine taxon. ");
        		} else { 
        			if (marine!=null && marine && nonMarine!=null && nonMarine) { 
        				result.setResultState(ResultState.RUN_HAS_RESULT);
        				result.setValue(ComplianceValue.COMPLIANT);
        				result.addComment("Provided scientificName ["+scientificName+"] known from both marine and non marine habitats, any location is consistent for this test.");
        			} else { 
        				// If either marine or non-marine were null, assume false, may be bad assumption
        				if (marine==null) { 
        					marine = false;
        				}
        				if (nonMarine==null) { 
        					nonMarine = false;
        				}
        				// evaluate against geospatial data
       					Double lat = Double.parseDouble(decimalLatitude);
       					Double lon = Double.parseDouble(decimalLongitude);
       					Double buffer_meters = Double.parseDouble(spatialBufferInMeters);
        				if (marine) { 
        					
        					if (GEOUtil.isOnOrNearLand(lon, lat, false, buffer_meters)) { 
        						result.setResultState(ResultState.RUN_HAS_RESULT);
        						result.setValue(ComplianceValue.NOT_COMPLIANT);
        						result.addComment("Provided scientificName ["+scientificName+"] is known from marine habitats, but the provided coordinate is non-marine.");
        					} else { 
        						result.setResultState(ResultState.RUN_HAS_RESULT);
        						result.setValue(ComplianceValue.COMPLIANT);
        						result.addComment("Provided scientificName ["+scientificName+"] is known from marine habitats and the provided coordinate is marine.");
        					}
        				} else { 
        					if (GEOUtil.isOnOrNearLand(lon, lat, true, buffer_meters)) { 
        						// test with invert sense is:  isMarine, thus non-marine habitat and marine location
        						result.setResultState(ResultState.RUN_HAS_RESULT);
        						result.setValue(ComplianceValue.NOT_COMPLIANT);
        						result.addComment("Provided scientificName ["+scientificName+"] is known from non-marine habitats, but the provided coordinate is marine.");
        					} else { 
        						result.setResultState(ResultState.RUN_HAS_RESULT);
        						result.setValue(ComplianceValue.COMPLIANT);
        						result.addComment("Provided scientificName ["+scientificName+"] is known from non-marine habitats and the provided coordinate is non-marine.");
        					}	
        				}
        			}
        		}
        		
        	}
        } catch (SourceAuthorityException e) { 
        	result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);
        	result.addComment("Error with specified Source Authority geospatialLand: " + e.getMessage());
        } catch (org.filteredpush.qc.sciname.SourceAuthorityException e) {
        	result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);
        	result.addComment("Error with specified Source Authority taxonIsMarine: " + e.getMessage());
		}
        		
        return result;
    }

    /**
     * Propose amendment of the signs of dwc:decimalLatitude and/or dwc:decimalLongitude to align the location with the dwc:countryCode.
     *
     * Provides: 54 AMENDMENT_COORDINATES_TRANSPOSED
     * Version: 2024-11-11
     *
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate as ActedUpon.
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate as ActedUpon.
     * @param countryCode the provided dwc:countryCode to evaluate as Consulted.
     * @param sourceAuthority the spatial source authority for countries.
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Amendment(label="AMENDMENT_COORDINATES_TRANSPOSED", description="Propose amendment of the signs of dwc:decimalLatitude and/or dwc:decimalLongitude to align the location with the dwc:countryCode.")
    @Provides("f2b4a50a-6b2f-4930-b9df-da87b6a21082")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/f2b4a50a-6b2f-4930-b9df-da87b6a21082/2024-11-11")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if any of dwc:decimalLatitude or dwc:decimalLongitude or dwc:countryCode are bdq:Empty; AMENDED dwc:decimalLatitude and dwc:decimalLongitude if the coordinates were transposed or one or more of the signs of the coordinates were reversed to align the location with dwc:countryCode according to the bdq:sourceAuthority; otherwise NOT_AMENDED. bdq:sourceAuthority default = '10m-admin-1 boundaries UNION with Exclusive Economic Zones' {[https://www.naturalearthdata.com/downloads/10m-cultural-vectors/10m-admin-1-states-provinces/] spatial UNION [https://www.marineregions.org/downloads.php#marbound]}")
    public static DQResponse<AmendmentValue> amendmentCoordinatesTransposed(
        @ActedUpon("dwc:decimalLatitude") String decimalLatitude, 
        @ActedUpon("dwc:decimalLongitude") String decimalLongitude, 
        @Consulted("dwc:countryCode") String countryCode,
    	@Parameter(name="bdq:sourceAuthority") String sourceAuthority
    ) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if any of dwc:decimalLatitude
        // or dwc:decimalLongitude or dwc:countryCode are bdq:Empty; AMENDED
        // dwc:decimalLatitude and dwc:decimalLongitude if the coordinates
        // were transposed or one or more of the signs of the coordinates
        // were reversed to align the location with dwc:countryCode 
        // according to the bdq:sourceAuthority; otherwise NOT_AMENDED 
        
        // Parameters
        // bdq:sourceAuthority default = "10m-admin-1 boundaries UNION with Exclusive Economic Zones" 
        // {[https://www.naturalearthdata.com/downloads/10m-cultural-vectors/10m-admin-1-states-provinces/] spatial UNION [https://www.marineregions.org/downloads.php#marbound]}

        String DEFAULT_SOURCE_AUTHORITY =  "10m-admin-1 boundaries UNION with Exclusive Economic Zones";
        if (GEOUtil.isEmpty(sourceAuthority)) { 
        	sourceAuthority = DEFAULT_SOURCE_AUTHORITY;
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
        		if (!sourceAuthorityObject.getAuthority().equals(EnumGeoRefSourceAuthority.ADM1_UNION_EEZ)) { 
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
    
    /**
     * Are the supplied geographic coordinates within a defined buffer of the center of the country?
     *
     * Provides: 287 ISSUE_COORDINATES_CENTEROFCOUNTRY
     * Version: 2024-08-28
     *
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate as ActedUpon.
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate as ActedUpon.
     * @param countryCode the provided dwc:countryCode to evaluate as Consulted.
     * @param coordinateUncertaintyInMeters the provided dwc:coordinateUncertaintyInMeters to evaluate as Consulted.
     * @param spatialBufferInMeters the distance in meters within which a point must fall from 
     *   a centroid to be considered an issue.
     * @param sourceAuthority the spatial source authority to consult for country centroids
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Issue(label="ISSUE_COORDINATES_CENTEROFCOUNTRY", description="Are the supplied geographic coordinates within a defined buffer of the center of the country?")
    @Provides("256e51b3-1e08-4349-bb7e-5186631c3f8e")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/256e51b3-1e08-4349-bb7e-5186631c3f8e/2024-08-28")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if any of dwc:countryCode, dwc:decimalLatitude, dwc:decimalLongitude are bdq:Empty; POTENTIAL_ISSUE if (1) the geographic coordinates are within the distance given by bdq:spatialBufferInMeters from the center of the supplied dwc:countryCode as represented in the bdq:sourceAuthority (or one of the centers, if the bdq:sourceAuthority provides more than one per country code) and (2) the dwc:coordinateUncertaintyInMeters is bdq:Empty or less than half the square root of the area of the country; otherwise NOT_ISSUE.. bdq:spatialBufferInMeters default = '5000',bdq:sourceAuthority default = 'GBIF Catalogue of Country Centroides' {[https://raw.githubusercontent.com/jhnwllr/catalogue-of-centroids/master/PCLI.tsv]}")
    public static DQResponse<IssueValue> issueCoordinatesCenterofcountry(
        @ActedUpon("dwc:decimalLatitude") String decimalLatitude, 
        @ActedUpon("dwc:decimalLongitude") String decimalLongitude, 
        @Consulted("dwc:countryCode") String countryCode,
        @Consulted("dwc:coordinateUncertaintyInMeters") String coordinateUncertaintyInMeters,
    	@Parameter(name="bdq:spatialBufferInMeters") String spatialBufferInMeters,
    	@Parameter(name="bdq:sourceAuthority") String sourceAuthority
    ) {
		DQResponse<IssueValue> result = new DQResponse<IssueValue>();

        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if any 
        // of dwc:countryCode, dwc:decimalLatitude, dwc:decimalLongitude 
        // are bdq:Empty; POTENTIAL_ISSUE if (1) the geographic coordinates 
        // are within the distance given by bdq:spatialBufferInMeters 
        // from the center of the supplied dwc:countryCode as represented 
        // in the bdq:sourceAuthority (or one of the centers, if the 
        // bdq:sourceAuthority provides more than one per country code) 
        // and (2) the dwc:coordinateUncertaintyInMeters is bdq:Empty 
        // or less than half the square root of the area of the country; 
        // otherwise NOT_ISSUE. 
		
		// Parameters
		// bdq:spatialBufferInMeters default = "5000"
		// bdq:sourceAuthority default = "GBIF Catalogue of Country Centroides" 
		// {[https://raw.githubusercontent.com/jhnwllr/catalogue-of-centroids/master/PCLI.tsv]}

		if (GEOUtil.isEmpty(sourceAuthority)) {
			sourceAuthority = "GBIF Catalogue of Country Centroides";
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

        		if (!GEOUtil.isEmpty(coordinateUncertaintyInMeters))  {
        			try { 
        				Double uncertaintyRadiusMeters = Double.parseDouble(coordinateUncertaintyInMeters);
        				Double area = GISDataLoader.getAreaOfCountry(countryCode);
        				if (area!=null) { 
        					Double countryRadiusMeters = (Math.sqrt(area)/2d)*1000d;
        					logger.debug(area);
        					logger.debug(uncertaintyRadiusMeters);
        					logger.debug(countryRadiusMeters);
        					if (countryRadiusMeters <= uncertaintyRadiusMeters) {
        						result.setResultState(ResultState.RUN_HAS_RESULT);
        						result.setValue(IssueValue.NOT_ISSUE);
        						result.addComment("Coordinate uncertainty in meters ["+coordinateUncertaintyInMeters+"] is large relative to the size of the country ["+countryCode+"] ["+Double.toString(countryRadiusMeters)+"]");
        					}
        				}
        			} catch (NumberFormatException ex) { 
        				logger.debug(ex.getMessage());
        				result.addComment("Unable to parse a number out of dwc:coordinateUncertaintyInMeters ["+coordinateUncertaintyInMeters+"]");
        			}
        		}

        		if (result.getResultState()==ResultState.NOT_RUN)  { 
        			try { 

        				Double dLongitude = Double.parseDouble(decimalLongitude);
        				Double dLatitude = Double.parseDouble(decimalLatitude);

        				if (GISDataLoader.isPointNearCentroid(dLongitude, dLatitude, countryCode, buffer_km)) { 
        					result.setResultState(ResultState.RUN_HAS_RESULT);
        					result.setValue(IssueValue.POTENTIAL_ISSUE);
        					result.addComment("Provided dwc:decimalLatitude ["+decimalLatitude+"] and dwc:decimalLongitude ["+decimalLongitude+"] are within ["+spatialBufferInMeters+"]m of the centroid of the dwc:countryCode ["+countryCode+"], and may reflect a georeference for the entire country.");
        				} else { 
        					result.setResultState(ResultState.RUN_HAS_RESULT);
        					result.setValue(IssueValue.NOT_ISSUE);
        					result.addComment("Provided dwc:decimalLatitude ["+decimalLatitude+"] and dwc:decimalLongitude ["+decimalLongitude+"] are not near the centroid of the dwc:countryCode ["+countryCode+"].");
        				}

        			} catch (NumberFormatException ex) { 
        				result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        				result.addComment("Unable to interpret provided dwc:decimalLatitude ["+decimalLatitude+"] or dwc:decimalLongitude ["+decimalLongitude+"]");
        			}
        		}

        		if (sourceAuthorityObject.getAuthority().equals(EnumGeoRefSourceAuthority.GBIF_CENTROIDS)) { 
        		} else { 
        			throw new SourceAuthorityException("Unsupported source authority ["+sourceAuthority+"]");
        		}
        	}
        } catch (SourceAuthorityException e) { 
        	result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);
        	result.addComment("Error with specified Source Authority: " + e.getMessage());
        }
        
        return result;
    }

}
