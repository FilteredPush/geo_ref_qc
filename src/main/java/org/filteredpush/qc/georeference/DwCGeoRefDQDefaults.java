/**
 * DwCGeoRefDQDefaults.java
 */
package org.filteredpush.qc.georeference;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datakurator.ffdq.annotations.ActedUpon;
import org.datakurator.ffdq.annotations.Amendment;
import org.datakurator.ffdq.annotations.Consulted;
import org.datakurator.ffdq.annotations.Issue;
import org.datakurator.ffdq.annotations.Mechanism;
import org.datakurator.ffdq.annotations.Parameter;
import org.datakurator.ffdq.annotations.Provides;
import org.datakurator.ffdq.annotations.ProvidesVersion;
import org.datakurator.ffdq.annotations.Specification;
import org.datakurator.ffdq.annotations.Validation;
import org.datakurator.ffdq.api.DQResponse;
import org.datakurator.ffdq.api.result.AmendmentValue;
import org.datakurator.ffdq.api.result.ComplianceValue;
import org.datakurator.ffdq.api.result.IssueValue;
import org.filteredpush.qc.georeference.util.GettyLookup;

/**
 * Provides methods for parameterized SPACE tests without the bdqval: parameters as method parameters,
 * with the method signatures only providing the information elements under test as method parameters.
 * Each method here is just a wrapper for the parameterized method in DwCGeoRefDQ, specifying the
 * default values for each bdqval: parameter.
 *
 * @author mole
 * @version $Id: $Id
 */
@Mechanism(value="71fa3762-0dfa-43c7-a113-d59797af02e8",label="Kurator: Spatial Data Validator - DwCGeoRefDQ:v2.1.2-SNAPSHOT")
public class DwCGeoRefDQDefaults extends DwCGeoRefDQ {

	private static final Log logger = LogFactory.getLog(DwCGeoRefDQDefaults.class);

    /**
     * Is the value of dwc:maximumDepthInMeters within the Parameter range?  Uses the
     * default parameter values of 0 to 110000.
     *
     * #187 Is the value of dwc:maximumDepthInMeters within the specified Parameter range?
     *
     * Provides: 187 VALIDATION_MAXDEPTH_INRANGE
     * Version: 2023-09-18
     *
     * @param maximumDepthInMeters the provided dwc:maximumDepthInMeters to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_MAXDEPTH_INRANGE", description="Is the value of dwc:maximumDepthInMeters within the Parameter range?")
    @Provides("3f1db29a-bfa5-40db-9fd1-fde020d81939")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/3f1db29a-bfa5-40db-9fd1-fde020d81939/2023-09-18")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if dwc:maximumDepthInMeters is bdqvalEmpty or is not interpretable as a number greater than or equal to zero; COMPLIANT if the value of dwc:maximumDepthInMeters is within the range of bdqvalminimumValidDepthInMeters to bdqvalmaximumValidDepthInMeters inclusive; otherwise NOT_COMPLIANT. bdqvalminimumValidDepthInMeters default='0',bdqvalmaximumValidDepthInMeters default='11000'")
    public static DQResponse<ComplianceValue> validationMaxdepthInrange(
    		@ActedUpon("dwc:maximumDepthInMeters") String maximumDepthInMeters) { 
    	return (DwCGeoRefDQ.validationMaxdepthInrange(maximumDepthInMeters, 0d, 11000d));
    }
	
    
   /**
    * Proposes an amendment to fill in dwc:geodeticDatum using a parmeterized value if the dwc:geodeticDatum is empty.
    * Uses the default parameter value.
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
    * @return DQResponse the response of type AmendmentValue to return
    */
   @Amendment(label="AMENDMENT_GEODETICDATUM_ASSUMEDDEFAULT", description="Proposes an amendment to fill in dwc:geodeticDatum using a parmeterized value if the dwc:geodeticDatum is empty.")
   @Provides("7498ca76-c4d4-42e2-8103-acacccbdffa7")
   @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/7498ca76-c4d4-42e2-8103-acacccbdffa7/2024-11-12")
   @Specification("If dwc:geodeticDatum is bdqvalEmpty, fill in dwc:geodeticDatum using the value of bdqvaldefaultGeodeticDatum, report FILLED_IN and, if dwc:coordinateUncertaintyInMeters, dwc:decimalLatitude and dwc:decimalLongitude are bdqvalNotEmpty, amend the value of dwc:coordinateUncertaintyInMeters by adding the maximum datum shift between the specified bdqvaldefaultGeodeticDatum and any other datum at the provided dwc:decimalLatitude and dwc:decimalLongitude and instead report AMENDED; otherwise NOT_AMENDED.. bdqvaldefaultGeodeticDatum default = 'EPSG:4326'")
   public DQResponse<AmendmentValue> amendmentGeodeticdatumAssumeddefault(
   		@ActedUpon("dwc:coordinateUncertaintyInMeters") String coordinateUncertaintyInMeters, 
   		@ActedUpon("dwc:geodeticDatum") String geodeticDatum,
   		@ActedUpon("dwc:decimalLatitude") String decimalLatitude,
   		@ActedUpon("dwc:decimalLongitude") String decimalLongitude)
   {
	   return DwCGeoRefDQ.amendmentGeodeticdatumAssumeddefault(coordinateUncertaintyInMeters, geodeticDatum, decimalLatitude, decimalLongitude, null);
   }
   
    /**
     * Is the value of dwc:minimumDepthInMeters within the Parameter range?
     * Uses the default minimum and maximum depth values.
     *
     * Provides: #107 VALIDATION_MINDEPTH_INRANGE
     * Version: 2023-09-18
     *
     * @param minimumDepthInMeters the provided dwc:minimumDepthInMeters to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_MINDEPTH_INRANGE", description="Is the value of dwc:minimumDepthInMeters within the Parameter range?")
    @Provides("04b2c8f3-c71b-4e95-8e43-f70374c5fb92")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/04b2c8f3-c71b-4e95-8e43-f70374c5fb92/2023-09-18")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if dwc:minimumDepthInMeters is bdqvalEmpty, or the value is not interpretable as number greater than or equal to zero; COMPLIANT if the value of dwc:minimumDepthInMeters is within the range of bdqvalminimumValidDepthInMeters to bdqvalmaximumValidDepthInMeters inclusive; otherwise NOT_COMPLIANT. bdqvalminimumValidDepthInMeters default='0',bdqvalmaximumValidDepthInMeters default='11000'")
    public static DQResponse<ComplianceValue> validationMindepthInrange(
    		@ActedUpon("dwc:minimumDepthInMeters") String minimumDepthInMeters) { 
    	return DwCGeoRefDQ.validationMindepthInrange(minimumDepthInMeters, 0d, 11000d);
    }
    
    
    /**
     * Is the value of dwc:maximumElevationInMeters within the default range of -430 to 8850.
     *
     * Provides: #112 VALIDATION_MAXELEVATION_INRANGE
     * Version: 2023-09-18
     *
     * Uses Default values: bdqvalminimumValidElevationInMeters="-430", bdqvalmaximumValidElevationInMeters="8850"
     *
     * @param maximumElevationInMeters the provided dwc:maximumElevationInMeters to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_MAXELEVATION_INRANGE", description="Is the value of dwc:maximumElevationInMeters of a single record within a valid range")
    @Provides("c971fe3f-84c1-4636-9f44-b1ec31fd63c7")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/c971fe3f-84c1-4636-9f44-b1ec31fd63c7/2023-09-18")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if dwc:maximumElevationInMeters is EMPTY or the value cannot be interpreted as a number; COMPLIANT if the value of dwc:maximumElevationInMeters is within the range of bdqvalminimumValidElevationInMeters to bdqvalmaximumValidElevationInMeters inclusive; otherwise NOT_COMPLIANT bdqvalminimumValidElevationInMeters default = '-430',bdqvalmaximumValidElevationInMeters default = '8850'")
    public static DQResponse<ComplianceValue> validationMaxelevationInrange(
    		@ActedUpon("dwc:maximumElevationInMeters") String maximumElevationInMeters) {
    	return DwCGeoRefDQ.validationMaxelevationInrange(maximumElevationInMeters,-430d, 8850d);
    }
    
    
    /**
     * Is the value of dwc:minimumElevationInMeters within the Parameter range?
     * Uses the default values for the parameters.
     *
     * Provides: #39 VALIDATION_MINELEVATION_INRANGE
     * Version: 2022-03-26
     *
     * @param minimumElevationInMeters the provided dwc:minimumElevationInMeters to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_MINELEVATION_INRANGE", description="Is the value of dwc:minimumElevationInMeters within the Parameter range?")
    @Provides("0bb8297d-8f8a-42d2-80c1-558f29efe798")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/0bb8297d-8f8a-42d2-80c1-558f29efe798/2023-09-17")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if dwc:minimumElevationInMeters is EMPTY or the value is not a number; COMPLIANT if the value of dwc:minimumElevationInMeters is within the range of bdqvalminimumValidElevationInMeters to bdqvalmaximumValidElevationInMeters inclusive; otherwise NOT_COMPLIANT bdqvalminimumValidElevationInMeters default = '-430',bdqvalmaximumValidElevationInMeters default = '8850'")
    public static DQResponse<ComplianceValue> validationMinelevationInrange(
    		@ActedUpon("dwc:minimumElevationInMeters") String minimumElevationInMeters) {
    	return DwCGeoRefDQ.validationMinelevationInrange(minimumElevationInMeters, -430d, 8850d);
    }
    
    
    /**
     * Does the value of dwc:country occur in the bdqvalsourceAuthority?
     * Using the default source authority "The Getty Thesaurus of Geographic Names (TGN)"
     *
     * #21 Validation SingleRecord Conformance: country notstandard
     *
     * Provides: 21 VALIDATION_COUNTRY_FOUND
     * Version: 2024-08-19
     *
     * @param country the provided dwc:country to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_COUNTRY_FOUND", description="Does the value of dwc:country occur in the bdqvalsourceAuthority?")
    @Provides("69b2efdc-6269-45a4-aecb-4cb99c2ae134")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/69b2efdc-6269-45a4-aecb-4cb99c2ae134/2024-08-19")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdqvalsourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:country is bdqvalEmpty; COMPLIANT if value of dwc:country is a place type equivalent to administrative entity of 'nation' in the bdqvalsourceAuthority; otherwise NOT_COMPLIANT. bdqvalsourceAuthority default = 'The Getty Thesaurus of Geographic Names (TGN)' {[https://www.getty.edu/research/tools/vocabularies/tgn/index.html]}")
   
    public static DQResponse<ComplianceValue> validationCountryFound(@ActedUpon("dwc:country") String country) { 
    	return DwCGeoRefDQ.validationCountryFound(country, GettyLookup.GETTY_TGN);
    }
    
    /**
     * Does the value of dwc:stateProvince occur in the bdqvalsourceAuthority?
     * Using the default stateProvince source authority of "The Getty Thesaurus of Geographic Names (TGN)"
     *
     * Provides: #199 VALIDATION_STATEPROVINCE_FOUND
     * Version: 2024-09-18
     *
     * @param stateProvince the provided dwc:stateProvince to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_STATEPROVINCE_FOUND", description="Does the value of dwc:stateProvince occur in the bdqvalsourceAuthority?")
    @Provides("4daa7986-d9b0-4dd5-ad17-2d7a771ea71a")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/4daa7986-d9b0-4dd5-ad17-2d7a771ea71a/2024-09-18")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdqvalsourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:stateProvince is bdqvalEmpty; COMPLIANT if the value of dwc:stateProvince occurs as an administrative entity that is a child to at least one entity representing an ISO 3166 country-like entity in the bdqvalsourceAuthority; otherwise NOT_COMPLIANT. bdqvalsourceAuthority default = 'The Getty Thesaurus of Geographic Names (TGN)' {[https://www.getty.edu/research/tools/vocabularies/tgn/index.html]}")
    public static DQResponse<ComplianceValue> validationStateprovinceFound(
    		@ActedUpon("dwc:stateProvince") String stateProvince
    		) {
    	return DwCGeoRefDQ.validationStateprovinceFound(stateProvince, GettyLookup.GETTY_TGN);
    }
    
    /**
     * Do the geographic coordinates fall on or within the boundaries of the territory given in dwc:countryCode or its Exclusive Economic Zone?
     *
     * Uses the default parameter values:
     * bdqvalsourceAuthority default = "ADM1 boundaries" [https://gadm.org]  UNION with "EEZs" [https://marineregions.org]
     * bdqvalspatialBufferInMeters default = "3000"
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
     */
    @Validation(label="VALIDATION_COORDINATES_COUNTRYCODE_CONSISTENT", description="Do the geographic coordinates fall on or within the boundaries of the territory given in dwc:countryCode or its Exclusive Economic Zone?")
    @Provides("adb27d29-9f0d-4d52-b760-a77ba57a69c9")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/adb27d29-9f0d-4d52-b760-a77ba57a69c9/2023-02-27")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdqvalsourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if one or more of dwc:decimalLatitude, dwc:decimalLongitude, or dwc:countryCode are EMPTY or invalid; COMPLIANT if the geographic coordinates fall on or within the boundary defined by the union of the boundary of the country from dwc:countryCode plus it's Exclusive Economic Zone, if any, plus an exterior buffer given by bdqvalspatialBufferInMeters; otherwise NOT_COMPLIANT bdqvalsourceAuthority default = 'ADM1 boundaries' [https://gadm.org] UNION with 'EEZs' [https://marineregions.org],bdqvalspatialBufferInMeters default = '3000'")
    public static DQResponse<ComplianceValue> validationCoordinatesCountrycodeConsistent(
    		@ActedUpon("dwc:decimalLatitude") String decimalLatitude, 
    		@ActedUpon("dwc:decimalLongitude") String decimalLongitude, 
    		@ActedUpon("dwc:countryCode") String countryCode
    		) 
    {
    	return DwCGeoRefDQ.validationCoordinatesCountrycodeConsistent(decimalLatitude, decimalLongitude, countryCode, "3000", null);
    }
    
    /**
     *  Are the combination of the values of dwc:country, dwc:stateProvince consistent with
     *  the values in the bdqvalsourceAuthority?  Uses the default sourceAuthority.
     *
     * Provides: #200 VALIDATION_COUNTRYSTATEPROVINCE_CONSISTENT
     * Version: 2023-09-18
     *
     * @param country the provided dwc:country to evaluate
     * @param stateProvince the provided dwc:stateProvince to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_COUNTRYSTATEPROVINCE_CONSISTENT", description=" 	Are the combination of the values of dwc:country, dwc:stateProvince consistent with the values in the bdqvalsourceAuthority?")
    @Provides("e654f562-44f8-43fd-983b-2aaba4c6dda9")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/e654f562-44f8-43fd-983b-2aaba4c6dda9/2023-09-18")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdqvalsourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if the terms dwc:country or dwc:stateProvince are EMPTY; COMPLIANT if the value of dwc:stateProvince occurs as an administrative entity that is a child to the entity matching the value of dwc:country in the bdqvalsourceAuthority, and the match to dwc:country is an ISO country-like entity in the bdqvalsourceAuthority; otherwise NOT_COMPLIANT  	bdqvalsourceAuthority default = \"The Getty Thesaurus of Geographic Names (TGN)\" {[https://www.getty.edu/research/tools/vocabularies/tgn/index.html]}")
    public static DQResponse<ComplianceValue> validationCountrystateprovinceConsistent(
    		@ActedUpon("dwc:country") String country, 
    		@ActedUpon("dwc:stateProvince") String stateProvince
    		) 
    {
    	return validationCountrystateprovinceConsistent(country,stateProvince, null);
    }
    
    
    
    /**
     * Do the geographic coordinates fall on or within the boundaries of the territory given in dwc:countryCode or its Exclusive Economic Zone?
     * Using the default parameter values.
     *
     * #50 Validation SingleRecord Consistency: coordinates countrycode inconsistent
     *
     * Provides: #50 VALIDATION_COORDINATESCOUNTRYCODE_CONSISTENT
     * Version: 2024-08-30
     *
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate
     * @param countryCode the provided dwc:countryCode to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_COORDINATESCOUNTRYCODE_CONSISTENT", description="Do the geographic coordinates fall on or within the boundaries of the territory given in dwc:countryCode or its Exclusive Economic Zone?")
    @Provides("adb27d29-9f0d-4d52-b760-a77ba57a69c9")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/adb27d29-9f0d-4d52-b760-a77ba57a69c9/2024-08-30")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdqvalsourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if one or more of dwc:decimalLatitude, dwc:decimalLongitude, or dwc:countryCode are bdqvalEmpty or invalid; COMPLIANT if the geographic coordinates fall on or within the boundary defined by the union of the boundary of the country from dwc:countryCode plus it's Exclusive Economic Zone as found in the bdqvalsourceAuthority, if any, plus an exterior buffer given by bdqvalspatialBufferInMeters; otherwise NOT_COMPLIANT. bdqvalsourceAuthority default = '10m-admin-1 boundaries UNION with Exclusive Economic Zones' {[https://www.naturalearthdata.com/downloads/10m-cultural-vectors/10m-admin-1-states-provinces/] spatial UNION [https://www.marineregions.org/downloads.php#marbound]},bdqvalspatialBufferInMeters default = '3000'")
    public static DQResponse<ComplianceValue> validationCoordinatesCountrycodeConsistent(
    		@ActedUpon("dwc:decimalLatitude") String decimalLatitude, 
    		@ActedUpon("dwc:decimalLongitude") String decimalLongitude, 
    		@ActedUpon("dwc:countryCode") String countryCode,
    		@Parameter(name="bdqvalsourceAuthority") String sourceAuthority
    		) {
    	String spatialBufferInMeters = "3000";
    	return validationCoordinatesCountrycodeConsistent(decimalLatitude, decimalLongitude, countryCode, spatialBufferInMeters, sourceAuthority);
    }
    
    
    /**
     * Do the geographic coordinates fall on or within the boundary from the
     * bdqvalsourceAuthority for the given dwc:stateProvince or within the distance
     * given by bdqvalspatialBufferInMeters outside that boundary?  Using the default
     * values for source authority and spatial buffer in meters.
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
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_COORDINATESSTATEPROVINCE_CONSISTENT", description="Do the geographic coordinates fall on or within the boundary from the bdqvalsourceAuthority for the given dwc:stateProvince or within the distance given by bdqvalspatialBufferInMeters outside that boundary?")
    @Provides("f18a470b-3fe1-4aae-9c65-a6d3db6b550c")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/f18a470b-3fe1-4aae-9c65-a6d3db6b550c/2024-08-30")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdqvalsourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if the values of dwc:decimalLatitude or dwc:decimalLongitude are bdqvalEmpty or invalid, or dwc:stateProvince is bdqvalEmpty or not found in the bdqvalsourceAuthority; COMPLIANT if the geographic coordinates fall on or within the boundary in the bdqvalsourceAuthority for the given dwc:stateProvince (after coordinate reference system transformations, if any, have been accounted for), or within the distance given by bdqvalspatialBufferInMeters outside that boundary; otherwise NOT_COMPLIANT.. bdqvalsourceAuthority default = '10m-admin-1 boundaries' {[https://www.naturalearthdata.com/downloads/10m-cultural-vectors/10m-admin-1-states-provinces/]},bdqvalspatialBufferInMeters default = '3000'")
    public static DQResponse<ComplianceValue> validationCoordinatesStateprovinceConsistent(
    		@ActedUpon("dwc:decimalLatitude") String decimalLatitude, 
    		@ActedUpon("dwc:decimalLongitude") String decimalLongitude, 
    		@ActedUpon("dwc:stateProvince") String stateProvince
    		) 
    {
    	return validationCoordinatesStateprovinceConsistent(decimalLatitude, decimalLongitude, stateProvince, null, null);
    }
    
    
    /**
     * Is the combination of the values of the terms dwc:country, 
     * dwc:stateProvince unique in the bdqvalsourceAuthority?  Uses
     * the default source authority.
     *
     * Provides: 201 VALIDATION_COUNTRYSTATEPROVINCE_UNAMBIGUOUS
     * Version: 2024-09-18
     *
     * @param country the provided dwc:country to evaluate
     * @param stateProvince the provided dwc:stateProvince to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_COUNTRYSTATEPROVINCE_UNAMBIGUOUS", description="Is the combination of the values of the terms dwc:country, dwc:stateProvince unique in the bdqvalsourceAuthority?")
    @Provides("d257eb98-27cb-48e5-8d3c-ab9fca4edd11")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/d257eb98-27cb-48e5-8d3c-ab9fca4edd11/2024-09-18")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdqvalsourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if the terms dwc:country and dwc:stateProvince are bdqvalEmpty; COMPLIANT if the combination of values of dwc:country and dwc:stateProvince are unambiguously resolved to a single result with a child-parent relationship in the bdqvalsourceAuthority and the entity matching the value of dwc:country in the bdqvalsourceAuthority is an ISO 3166 country-like administrative entity in the bdqvalsourceAuthority; otherwise NOT_COMPLIANT. bdqvalsourceAuthority default = 'The Getty Thesaurus of Geographic Names (TGN)' {[https://www.getty.edu/research/tools/vocabularies/tgn/index.html]}")
    public static DQResponse<ComplianceValue> validationCountrystateprovinceUnambiguous(
    		@ActedUpon("dwc:country") String country, 
    		@ActedUpon("dwc:stateProvince") String stateProvince
    ) {
      return validationCountrystateprovinceUnambiguous(country,stateProvince, null);
    }
    
    /**
     * Propose amendment of the signs of dwc:decimalLatitude and/or dwc:decimalLongitude 
     * to align the location with the dwc:countryCode.
     * Uses the default source authority.
     *
     * Provides: 54 AMENDMENT_COORDINATES_TRANSPOSED
     * Version: 2024-11-11
     *
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate as ActedUpon.
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate as ActedUpon.
     * @param countryCode the provided dwc:countryCode to evaluate as Consulted.
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Amendment(label="AMENDMENT_COORDINATES_TRANSPOSED", description="Propose amendment of the signs of dwc:decimalLatitude and/or dwc:decimalLongitude to align the location with the dwc:countryCode.")
    @Provides("f2b4a50a-6b2f-4930-b9df-da87b6a21082")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/f2b4a50a-6b2f-4930-b9df-da87b6a21082/2024-11-11")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if any of dwc:decimalLatitude or dwc:decimalLongitude or dwc:countryCode are bdqvalEmpty; AMENDED dwc:decimalLatitude and dwc:decimalLongitude if the coordinates were transposed or one or more of the signs of the coordinates were reversed to align the location with dwc:countryCode according to the bdqvalsourceAuthority; otherwise NOT_AMENDED. bdqvalsourceAuthority default = '10m-admin-1 boundaries UNION with Exclusive Economic Zones' {[https://www.naturalearthdata.com/downloads/10m-cultural-vectors/10m-admin-1-states-provinces/] spatial UNION [https://www.marineregions.org/downloads.php#marbound]}")
    public static DQResponse<AmendmentValue> amendmentCoordinatesTransposed(
        @ActedUpon("dwc:decimalLatitude") String decimalLatitude, 
        @ActedUpon("dwc:decimalLongitude") String decimalLongitude, 
        @Consulted("dwc:countryCode") String countryCode
    ) {
    	return amendmentCoordinatesTransposed(decimalLatitude, decimalLongitude, countryCode, null);
    }
    
    /**
     * Does the marine/non-marine biome of a taxon from the bdqvalsourceAuthority
     * match the biome at the location given by the coordinates? Using the default
     * parameter values.
     *
     * Provides: 51 VALIDATION_COORDINATESTERRESTRIALMARINE_CONSISTENT
     * Version: 2024-08-30
     *
     * @param decimalLatitude  the provided dwc:decimalLatitude to evaluate as ActedUpon.
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate as ActedUpon.
     * @param scientificName   the provided dwc:scientificName to evaluate as Consulted.
     * @return DQResponse the response of type ComplianceValue to return
     */
    @Validation(label="VALIDATION_COORDINATESTERRESTRIALMARINE_CONSISTENT", description="Does the marine/non-marine biome of a taxon from the bdqvalsourceAuthority match the biome at the location given by the coordinates?")
    @Provides("b9c184ce-a859-410c-9d12-71a338200380")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/b9c184ce-a859-410c-9d12-71a338200380/2024-08-30")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if either bdqvaltaxonIsMarine or bdqvalgeospatialLand are not available; INTERNAL_PREREQUISITES_NOT_MET if (1) dwc:scientificName is bdqvalEmpty or (2)  the values of dwc:decimalLatitude or dwc:decimalLongitude are bdqvalEmpty or (3) if bdqvalassumptionOnUnknownBiome is noassumption and the marine/nonmarine status of the taxon is not interpretable from bdqvaltaxonIsMarine; COMPLIANT if (1) the taxon marine/nonmarine status from bdqvaltaxonIsMarine matches the marine/nonmarine status of dwc:decimalLatitude and dwc:decimalLongitude on the boundaries given by bdqvalgeospatialLand plus an exterior buffer given by bdqvalspatialBufferInMeters or (2)  if the marine/nonmarine status of the taxon is not interpretable from bdqvaltaxonIsMarine and bdqvalassumptionOnUnknownBiome matches the marine/nonmarine status of dwc:decimalLatitude and dwc:decimalLongitude on the boundaries given by bdqvalgeospatialLand plus an exterior buffer given by bdqvalspatialBufferInMeters; otherwise NOT_COMPLIANT. bdqvaltaxonIsMarine default = 'World Register of Marine Species (WoRMS)' {[https://www.marinespecies.org/]} {Web service [https://www.marinespecies.org/aphia.php?p=webservice]},bdqvalgeospatialLand default = 'Union of NaturalEarth 10m-physical-vectors for Land and NaturalEarth Minor Islands' {[https://www.naturalearthdata.com/http//www.naturalearthdata.com/download/10m/physical/ne_10m_land.zip], [https://www.naturalearthdata.com/http//www.naturalearthdata.com/download/10m/physical/ne_10m_minor_islands.zip]},bdqvalspatialBufferInMeters default = '3000',bdqvalassumptionOnUnknownBiome default = 'noassumption'")
    public static DQResponse<ComplianceValue> validationCoordinatesTerrestrialmarine(
        @ActedUpon("dwc:decimalLatitude") String decimalLatitude, 
        @ActedUpon("dwc:decimalLongitude") String decimalLongitude, 
        @Consulted("dwc:scientificName") String scientificName
    ) {
    	return validationCoordinatesTerrestrialmarine(decimalLatitude, decimalLongitude, scientificName, null, null, null, null);
    }
    
    /**
     * Proposes an amendment to the value of dwc:countryCode if dwc:decimalLatitude and dwc:decimalLongitude fall within a boundary from the bdqvalcountryShapes that is attributable to a single valid country code.
     * Uses the default spatial source authority.
     *
     * #73 Amendment SingleRecord Completeness: countrycode from coordinates
     *
     * Provides: AMENDMENT_COUNTRYCODE_FROM_COORDINATES
     * Version: 2024-08-18
     *
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate
     * @param countryCode the provided dwc:countryCode to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Amendment(label="AMENDMENT_COUNTRYCODE_FROM_COORDINATES", description="Proposes an amendment to the value of dwc:countryCode if dwc:decimalLatitude and dwc:decimalLongitude fall within a boundary from the bdqvalcountryShapes that is attributable to a single valid country code.")
    @Provides("8c5fe9c9-4ba9-49ef-b15a-9ccd0424e6ae")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/8c5fe9c9-4ba9-49ef-b15a-9ccd0424e6ae/2024-08-18")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdqvalsourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if either dwc:decimalLatitude or dwc:decimalLongitude is bdqvalEmpty, or if dwc:countryCode is bdqvalNotEmpty; FILLED_IN dwc:countryCode if dwc:decimalLatitude and dwc:decimalLongitude fall within a boundary in the bdqvalsourceAuthority that is attributable to a single valid country code; otherwise NOT_AMENDED.. bdqvalsourceAuthority default = '10m-admin-1 boundaries UNION with Exclusive Economic Zones' {[https://www.naturalearthdata.com/downloads/10m-cultural-vectors/10m-admin-1-states-provinces/] spatial UNION [https://www.marineregions.org/downloads.php#marbound]}")
    
    public static DQResponse<AmendmentValue> amendmentCountrycodeFromCoordinates(
    		@Consulted("dwc:decimalLatitude") String decimalLatitude, 
    		@Consulted("dwc:decimalLongitude") String decimalLongitude, 
    		@ActedUpon("dwc:countryCode") String countryCode
    	) {
    	return DwCGeoRefDQ.amendmentCountrycodeFromCoordinates(decimalLatitude, decimalLongitude, countryCode, null);
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
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Issue(label="ISSUE_COORDINATES_CENTEROFCOUNTRY", description="Are the supplied geographic coordinates within a defined buffer of the center of the country?")
    @Provides("256e51b3-1e08-4349-bb7e-5186631c3f8e")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/256e51b3-1e08-4349-bb7e-5186631c3f8e/2024-08-28")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdqvalsourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if any of dwc:countryCode, dwc:decimalLatitude, dwc:decimalLongitude are bdqvalEmpty; POTENTIAL_ISSUE if (1) the geographic coordinates are within the distance given by bdqvalspatialBufferInMeters from the center of the supplied dwc:countryCode as represented in the bdqvalsourceAuthority (or one of the centers, if the bdqvalsourceAuthority provides more than one per country code) and (2) the dwc:coordinateUncertaintyInMeters is bdqvalEmpty or less than half the square root of the area of the country; otherwise NOT_ISSUE.. bdqvalspatialBufferInMeters default = '5000',bdqvalsourceAuthority default = 'GBIF Catalogue of Country Centroides' {[https://raw.githubusercontent.com/jhnwllr/catalogue-of-centroids/master/PCLI.tsv]}")
    public static DQResponse<IssueValue> issueCoordinatesCenterofcountry(
        @ActedUpon("dwc:decimalLatitude") String decimalLatitude, 
        @ActedUpon("dwc:decimalLongitude") String decimalLongitude, 
        @Consulted("dwc:countryCode") String countryCode,
        @Consulted("dwc:coordinateUncertaintyInMeters") String coordinateUncertaintyInMeters
    ) {
    	return DwCGeoRefDQ.issueCoordinatesCenterofcountry(decimalLatitude, decimalLongitude, countryCode, coordinateUncertaintyInMeters, null, null);
    }
}
