/* NOTE: requires the ffdq-api dependecy in the maven pom.xml */

package org.filteredpush.qc.georeference;

import org.datakurator.ffdq.annotations.*;
import org.datakurator.ffdq.api.DQResponse;
import org.datakurator.ffdq.model.ResultState;
import org.datakurator.ffdq.api.result.*;

@Mechanism(value="71fa3762-0dfa-43c7-a113-d59797af02e8",label="Kurator: Date Validator - DwCGeoRefDQ:v2.0.0")
public class DwCGeoRefDQ_stubs {

    /**
     * Does the value of dwc:country occur in bdq:sourceAuthority?
     *
     * Provides: VALIDATION_COUNTRY_STANDARD
     *
     * @param country the provided dwc:country to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_COUNTRY_STANDARD", description="Does the value of dwc:country occur in bdq:sourceAuthority?")
    @Provides("69b2efdc-6269-45a4-aecb-4cb99c2ae134")
    public DQResponse<ComplianceValue> validationCountryStandard(@ActedUpon("dwc:country") String country) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:country 
        // was EMPTY; COMPLIANT if value of dwc:country is a place 
        // type equivalent to "nation" by the bdq:sourceAuthority; 
        // otherwise NOT_COMPLIANT bdq:sourceAuthority default = "The 
        // Getty Thesaurus of Geographic Names (TGN)" [https://www.getty.edu/research/tools/vocabularies/tgn/index.html] 
        // 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority

        return result;
    }

    /**
     * Is the value of dwc:minimumDepthInMeters a number that is less than or equal to the value of dwc:maximumDepthInMeters?
     *
     * Provides: VALIDATION_MINDEPTH_LESSTHAN_MAXDEPTH
     *
     * @param maximumDepthInMeters the provided dwc:maximumDepthInMeters to evaluate
     * @param minimumDepthInMeters the provided dwc:minimumDepthInMeters to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_MINDEPTH_LESSTHAN_MAXDEPTH", description="Is the value of dwc:minimumDepthInMeters a number that is less than or equal to the value of dwc:maximumDepthInMeters?")
    @Provides("8f1e6e58-544b-4365-a569-fb781341644e")
    public DQResponse<ComplianceValue> validationMindepthLessthanMaxdepth(@ActedUpon("dwc:maximumDepthInMeters") String maximumDepthInMeters, @ActedUpon("dwc:minimumDepthInMeters") String minimumDepthInMeters) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:minimumDepthInMeters 
        // or dwc:maximumDepthInMeters is EMPTY, or if either are interpretable 
        // as not zero or a positive number; COMPLIANT if the value 
        // of dwc:minimumDepthInMeters is less than or equal to the 
        // value of dwc:maximumDepthInMeters; otherwise NOT_COMPLIANT 
        // 

        return result;
    }

    /**
     * Is the value of dwc:decimalLongitude a number between -180 and 180 inclusive?
     *
     * Provides: VALIDATION_DECIMALLONGITUDE_INRANGE
     *
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_DECIMALLONGITUDE_INRANGE", description="Is the value of dwc:decimalLongitude a number between -180 and 180 inclusive?")
    @Provides("0949110d-c06b-450e-9649-7c1374d940d1")
    public DQResponse<ComplianceValue> validationDecimallongitudeInrange(@ActedUpon("dwc:decimalLongitude") String decimalLongitude) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:decimalLongitude is 
        // EMPTY or the value is not a number; COMPLIANT if the value 
        // of dwc:decimalLongitude is between -180 and 180 degrees, 
        // inclusive; otherwise NOT_COMPLIANT 

        return result;
    }

    /**
     * Propose amendment to the values of dwc:decimalLatitude and dwc:decimalLongitude from information in the verbatim coordinates terms.
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
    @Amendment(label="AMENDMENT_COORDINATES_FROM_VERBATIM", description="Propose amendment to the values of dwc:decimalLatitude and dwc:decimalLongitude from information in the verbatim coordinates terms.")
    @Provides("3c2590c7-af8a-4eb4-af57-5f73ba9d1f8e")
    public DQResponse<AmendmentValue> amendmentCoordinatesFromVerbatim(@ActedUpon("dwc:verbatimCoordinateSystem") String verbatimCoordinateSystem, @ActedUpon("dwc:decimalLatitude") String decimalLatitude, @ActedUpon("dwc:decimalLongitude") String decimalLongitude, @ActedUpon("dwc:verbatimCoordinates") String verbatimCoordinates, @ActedUpon("dwc:verbatimLongitude") String verbatimLongitude, @ActedUpon("dwc:verbatimSRS") String verbatimSRS, @ActedUpon("dwc:verbatimLatitude") String verbatimLatitude) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if either dwc:decimalLatitude 
        // or dwc:decimalLongitude was not EMPTY, or either dwc:verbatimLatitude 
        // and dwc:verbatimLongitude, or dwc:verbatimCoordinates were 
        // not unambiguously interpretable into valid coordinates; 
        // FILLED_IN the values of dwc:decimalLatitude and dwc:decimalLongitude 
        // if unambiguous values can be interpreted from dwc:verbatimCoordinates 
        // or dwc:verbatimLatitude and dwc:verbatimLongitude, plus 
        // dwc:verbatimCoordinateSystem and dwc:verbatimSRS; otherwise 
        // NOT_AMENDED 

        return result;
    }

    /**
     * Is the value of dwc:minimumElevationInMeters within the Parameter range?
     *
     * Provides: VALIDATION_MINELEVATION_INRANGE
     *
     * @param minimumElevationInMeters the provided dwc:minimumElevationInMeters to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_MINELEVATION_INRANGE", description="Is the value of dwc:minimumElevationInMeters within the Parameter range?")
    @Provides("0bb8297d-8f8a-42d2-80c1-558f29efe798")
    public DQResponse<ComplianceValue> validationMinelevationInrange(@ActedUpon("dwc:minimumElevationInMeters") String minimumElevationInMeters) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:minimumElevationInMeters 
        // is EMPTY or the value is not a number; COMPLIANT if the 
        // value of dwc:minimumElevationInMeters is within the range 
        // of bdq:minimumValidElevationInMeters to bdq:maximumValidElevationInMeters 
        // inclusive; otherwise NOT_COMPLIANT 

        //TODO: Parameters. This test is defined as parameterized.
        // Default values: bdq:minimumValidElevationInMeters="-430"; bdq:maximumValidElevationInMeters="8850"

        return result;
    }

    /**
     * Is there a value in any of the Darwin Core spatial terms that could specify a location?
     *
     * Provides: VALIDATION_LOCATION_NOTEMPTY
     *
     * @param continent the provided dwc:continent to evaluate
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate
     * @param waterBody the provided dwc:waterBody to evaluate
     * @param islandGroup the provided dwc:islandGroup to evaluate
     * @param verbatimLocality the provided dwc:verbatimLocality to evaluate
     * @param higherGeography the provided dwc:higherGeography to evaluate
     * @param country the provided dwc:country to evaluate
     * @param municipality the provided dwc:municipality to evaluate
     * @param verbatimLatitude the provided dwc:verbatimLatitude to evaluate
     * @param locality the provided dwc:locality to evaluate
     * @param locationID the provided dwc:locationID to evaluate
     * @param island the provided dwc:island to evaluate
     * @param county the provided dwc:county to evaluate
     * @param verbatimCoordinates the provided dwc:verbatimCoordinates to evaluate
     * @param verbatimLongitude the provided dwc:verbatimLongitude to evaluate
     * @param countryCode the provided dwc:countryCode to evaluate
     * @param higherGeographyID the provided dwc:higherGeographyID to evaluate
     * @param stateProvince the provided dwc:stateProvince to evaluate
     * @param footprintWKT the provided dwc:footprintWKT to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_LOCATION_NOTEMPTY", description="Is there a value in any of the Darwin Core spatial terms that could specify a location?")
    @Provides("58486cb6-1114-4a8a-ba1e-bd89cfe887e9")
    public DQResponse<ComplianceValue> validationLocationNotempty(@ActedUpon("dwc:continent") String continent, @ActedUpon("dwc:decimalLatitude") String decimalLatitude, @ActedUpon("dwc:decimalLongitude") String decimalLongitude, @ActedUpon("dwc:waterBody") String waterBody, @ActedUpon("dwc:islandGroup") String islandGroup, @ActedUpon("dwc:verbatimLocality") String verbatimLocality, @ActedUpon("dwc:higherGeography") String higherGeography, @ActedUpon("dwc:country") String country, @ActedUpon("dwc:municipality") String municipality, @ActedUpon("dwc:verbatimLatitude") String verbatimLatitude, @ActedUpon("dwc:locality") String locality, @ActedUpon("dwc:locationID") String locationID, @ActedUpon("dwc:island") String island, @ActedUpon("dwc:county") String county, @ActedUpon("dwc:verbatimCoordinates") String verbatimCoordinates, @ActedUpon("dwc:verbatimLongitude") String verbatimLongitude, @ActedUpon("dwc:countryCode") String countryCode, @ActedUpon("dwc:higherGeographyID") String higherGeographyID, @ActedUpon("dwc:stateProvince") String stateProvince, @ActedUpon("dwc:footprintWKT") String footprintWKT) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // COMPLIANT if at least one term needed to determine the location 
        // of the entity exists and is not EMPTY; otherwise NOT_COMPLIANT 
        // 

        return result;
    }

    /**
     * Propose amendment to the value of dwc:geodeticDatum and potentially to dwc:decimalLatitude and/or dwc:decimalLongitude based on a conversion between spatial reference systems.
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
    @Amendment(label="AMENDMENT_COORDINATES_CONVERTED", description="Propose amendment to the value of dwc:geodeticDatum and potentially to dwc:decimalLatitude and/or dwc:decimalLongitude based on a conversion between spatial reference systems.")
    @Provides("620749b9-7d9c-4890-97d2-be3d1cde6da8")
    public DQResponse<AmendmentValue> amendmentCoordinatesConverted(@ActedUpon("dwc:decimalLatitude") String decimalLatitude, @ActedUpon("dwc:decimalLongitude") String decimalLongitude, @ActedUpon("dwc:coordinateUncertaintyInMeters") String coordinateUncertaintyInMeters, @ActedUpon("dwc:geodeticDatum") String geodeticDatum, @ActedUpon("dwc:coordinatePrecision") String coordinatePrecision) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:decimalLatitude or 
        // dwc:decimalLongitude or dwc:geodeticDatum are EMPTY or not 
        // interpretable; AMENDED the values of dwc:decimalLatitude, 
        // dwc:decimalLongitude and dwc:geodeticDatum by a conversion 
        // between spatial reference systems; otherwise NOT_AMENDED 
        // 

        return result;
    }

    /**
     * Propose amendment to the value of dwc:countryCode if it can be interpreted as an ISO country code.
     *
     * Provides: AMENDMENT_COUNTRYCODE_STANDARDIZED
     *
     * @param countryCode the provided dwc:countryCode to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Amendment(label="AMENDMENT_COUNTRYCODE_STANDARDIZED", description="Propose amendment to the value of dwc:countryCode if it can be interpreted as an ISO country code.")
    @Provides("fec5ffe6-3958-4312-82d9-ebcca0efb350")
    public DQResponse<AmendmentValue> amendmentCountrycodeStandardized(@ActedUpon("dwc:countryCode") String countryCode) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the ISO 3166 service is 
        // not available; INTERNAL_PREREQUISTITES_NOT_MET if the value 
        // of dwc:countryCode is EMPTY; AMENDED the value of dwc:countryCode 
        // if it can be unambiguously interpreted from bdq:sourceAuthority; 
        // otherwise NOT_AMENDED bdq:sourceAuthority is "ISO 3166-1-alpha-2" 
        // [https://restcountries.eu/#api-endpoints-list-of-codes, 
        // https://www.iso.org/obp/ui/#search] 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority

        return result;
    }

    /**
     * Do the geographic coordinates fall on or within the boundaries of the territory given in dwc:countryCode or its Exclusive Economic Zone?
     *
     * Provides: VALIDATION_COORDINATES_COUNTRYCODE_CONSISTENT
     *
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate
     * @param countryCode the provided dwc:countryCode to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_COORDINATES_COUNTRYCODE_CONSISTENT", description="Do the geographic coordinates fall on or within the boundaries of the territory given in dwc:countryCode or its Exclusive Economic Zone?")
    @Provides("adb27d29-9f0d-4d52-b760-a77ba57a69c9")
    public DQResponse<ComplianceValue> validationCoordinatesCountrycodeConsistent(@ActedUpon("dwc:decimalLatitude") String decimalLatitude, @ActedUpon("dwc:decimalLongitude") String decimalLongitude, @ActedUpon("dwc:countryCode") String countryCode) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if one 
        // or more of dwc:decimalLatitude, dwc:decimalLongitude, or 
        // dwc:countryCode are EMPTY or contain values that cannot 
        // be interpreted; COMPLIANT if the geographic coordinates 
        // fall on or within the boundary defined by the union of the 
        // boundary of the country from dwc:countryCode plus it's Exclusive 
        // Economic Zone, if any, plus an exterior buffer given by 
        // bdq:spatialBufferInMeters; otherwise NOT_COMPLIANT <ul><li>bdq:sourceAuthority 
        // default = "ADM1 boundaries" [https://gadm.org] UNION with 
        // "EEZs" [https://marineregions.org] </li><li>bdq:spatialBufferInMeters 
        // default = "3000"</li></ul> 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority; bdq:spatialBufferInMeters

        return result;
    }

    /**
     * Does the marine/non-marine biome of a taxon from the bdq:sourceAuthority match the biome at the location given by the coordinates?
     *
     * Provides: VALIDATION_COORDINATES_TERRESTRIALMARINE
     *
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate
     * @param scientificName the provided dwc:scientificName to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_COORDINATES_TERRESTRIALMARINE", description="Does the marine/non-marine biome of a taxon from the bdq:sourceAuthority match the biome at the location given by the coordinates?")
    @Provides("b9c184ce-a859-410c-9d12-71a338200380")
    public DQResponse<ComplianceValue> validationCoordinatesTerrestrialmarine(@ActedUpon("dwc:decimalLatitude") String decimalLatitude, @ActedUpon("dwc:decimalLongitude") String decimalLongitude, @ActedUpon("dwc:scientificName") String scientificName) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if either bdq:sourceAuthority[taxonomyismarine] 
        // or bdq:sourceAuthority[geospatialland] are not available; 
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:dcientificName was 
        // EMPTY or the marine/non-marine status of the taxon is not 
        // interpretable from bdq:sourceAuthority[taxonomyismarine] 
        // or the values of dwc:decimalLatitude or dwc:decimalLongitude 
        // are EMPTY; COMPLIANT if the taxon marine/non-marine status 
        // from bdq:sourceAuthority[taxonomyismarine] matches the marine/non-marine 
        // status of dwc:decimalLatitude and dwc:decimalLongitude on 
        // the boundaries given by bdq:sourceAuthority[geospatialland] 
        // plus an exterior buffer given by bdq:spatialBufferInMeters; 
        // otherwise NOT_COMPLIANT <ul><li>bdq:sourceAuthority[taxonismarine] 
        // default = "WORMS" [https://www.marinespecies.org/aphia.php?p=webservice] 
        // </li><li>bdq:sourceAuthority[geospatialland] default = the 
        // union of "NaturalEarth 10m-physical-vectors for Land" [https://www.naturalearthdata.com/http//www.naturalearthdata.com/download/10m/physical/ne_10m_land.zip] 
        // and "NaturalEarth Minor Islands" [https://www.naturalearthdata.com/http//www.naturalearthdata.com/download/10m/physical/ne_10m_minor_islands.zip]</li><li>bdq:spatialBufferInMeters 
        // default = "3000" </li></ul> 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority[taxonismarine,geospatialland]; bdq:spatialBufferInMeters

        return result;
    }

    /**
     * Propose amendment of the signs of dwc:decimalLatitude and/or dwc:decimalLongitude to align the location with the dwc:countryCode.
     *
     * Provides: AMENDMENT_COORDINATES_TRANSPOSED
     *
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate
     * @param countryCode the provided dwc:countryCode to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Amendment(label="AMENDMENT_COORDINATES_TRANSPOSED", description="Propose amendment of the signs of dwc:decimalLatitude and/or dwc:decimalLongitude to align the location with the dwc:countryCode.")
    @Provides("f2b4a50a-6b2f-4930-b9df-da87b6a21082")
    public DQResponse<AmendmentValue> amendmentCoordinatesTransposed(@ActedUpon("dwc:decimalLatitude") String decimalLatitude, @ActedUpon("dwc:decimalLongitude") String decimalLongitude, @ActedUpon("dwc:countryCode") String countryCode) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if any of dwc:decimalLatitude 
        // or dwc:decimalLongitude or dwc:countryCode are EMPTY; AMENDED 
        // dwc:decimalLatitude and dwc:decimalLongitude if the coordinates 
        // were transposed or one or more of the signs of the coordinates 
        // were reversed to align the location with dwc:countryCode; 
        // otherwise NOT_AMENDED 

        return result;
    }

    /**
     * Propose amendments of the values of dwc:minimumDepthInMeters and/or dwc:maximumDepthInMeters if they can be interpreted from dwc:verbatimDepth.
     *
     * Provides: AMENDMENT_MINDEPTH-MAXDEPTH_FROM_VERBATIM
     *
     * @param verbatimDepth the provided dwc:verbatimDepth to evaluate
     * @param maximumDepthInMeters the provided dwc:maximumDepthInMeters to evaluate
     * @param minimumDepthInMeters the provided dwc:minimumDepthInMeters to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Amendment(label="AMENDMENT_MINDEPTH-MAXDEPTH_FROM_VERBATIM", description="Propose amendments of the values of dwc:minimumDepthInMeters and/or dwc:maximumDepthInMeters if they can be interpreted from dwc:verbatimDepth.")
    @Provides("c5658b83-4471-4f57-9d94-bf7d0a96900c")
    public DQResponse<AmendmentValue> amendmentMindepthMaxdepthFromVerbatim(@ActedUpon("dwc:verbatimDepth") String verbatimDepth, @ActedUpon("dwc:maximumDepthInMeters") String maximumDepthInMeters, @ActedUpon("dwc:minimumDepthInMeters") String minimumDepthInMeters) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:verbatimDepth is EMPTY 
        // or the value is not unambiguously interpretable or dwc:minimumDepthInMeters 
        // and dwc:maximumDepthInMeters are not EMPTY; FILLED_IN the 
        // value of dwc:minimumDepthInMeters and/or dwc:maximumDepthInMeters 
        // if they could be unambiguously determined from dwc:verbatimDepth; 
        // otherwise NOT_AMENDED 

        return result;
    }

    /**
     * Do the geographic coordinates fall on or within the boundary from the bdq:sourceAuthority for the given dwc:stateProvince or within the distance given by bdq:spatialBufferInMeters outside that boundary?
     *
     * Provides: VALIDATION_COORDINATES_STATEPROVINCE_CONSISTENT
     *
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate
     * @param stateProvince the provided dwc:stateProvince to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_COORDINATES_STATEPROVINCE_CONSISTENT", description="Do the geographic coordinates fall on or within the boundary from the bdq:sourceAuthority for the given dwc:stateProvince or within the distance given by bdq:spatialBufferInMeters outside that boundary?")
    @Provides("f18a470b-3fe1-4aae-9c65-a6d3db6b550c")
    public DQResponse<ComplianceValue> validationCoordinatesStateprovinceConsistent(@ActedUpon("dwc:decimalLatitude") String decimalLatitude, @ActedUpon("dwc:decimalLongitude") String decimalLongitude, @ActedUpon("dwc:stateProvince") String stateProvince) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // was not available; INTERNAL_PREREQUISITES_NOT_MET if the 
        // values of dwc:decimalLatitude, dwc:decimalLongitude, or 
        // dwc:stateProvince are EMPTY; COMPLIANT if the geographic 
        // coordinates fall on or within the boundary from the bdq:sourceAuthority 
        // for the given dwc:stateProvince (after coordinate reference 
        // system transformations, if any, have been accounted for), 
        // or within the distance given by bdq:spatialBufferInMeters 
        // outside that boundary; otherwise NOT_COMPLIANT. <ul><li>bdq:sourceAuthority 
        // default = "ADM1 boundaries" [https://gadm.org]</li><li> 
        // bdq:spatialBufferInMeters default = "3000" </li></ul> 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority; bdq:spatialBufferInMeters

        return result;
    }

    /**
     * Does the value of dwc:geoteticDatum occur in bdq:sourceAuthority?
     *
     * Provides: VALIDATION_GEODETICDATUM_STANDARD
     *
     * @param geodeticDatum the provided dwc:geodeticDatum to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_GEODETICDATUM_STANDARD", description="Does the value of dwc:geoteticDatum occur in bdq:sourceAuthority?")
    @Provides("7e0c0418-fe16-4a39-98bd-80e19d95b9d1")
    public DQResponse<ComplianceValue> validationGeodeticdatumStandard(@ActedUpon("dwc:geodeticDatum") String geodeticDatum) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available, INTERNAL_PREREQUISITES_NOT_MET if dwc:geodeticDatum 
        // is EMPTY; COMPLIANT if the value of dwc:geodeticDatum is 
        // a valid EPSG CRS Code (with or without the "epsg" namespace 
        // prepended), or an unambiguous alphanumeric CRS or datum 
        // code; otherwise NOT_COMPLIANT bdq:sourceAuthority is "epsg" 
        // [https://epsg.io] 

        return result;
    }

    /**
     * Propose amendment to the value of dwc:geodeticDatum using bdq:sourceAuthority.
     *
     * Provides: AMENDMENT_GEODETICDATUM_STANDARDIZED
     *
     * @param geodeticDatum the provided dwc:geodeticDatum to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Amendment(label="AMENDMENT_GEODETICDATUM_STANDARDIZED", description="Propose amendment to the value of dwc:geodeticDatum using bdq:sourceAuthority.")
    @Provides("0345b325-836d-4235-96d0-3b5caf150fc0")
    public DQResponse<AmendmentValue> amendmentGeodeticdatumStandardized(@ActedUpon("dwc:geodeticDatum") String geodeticDatum) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // was not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:geodeticDatum 
        // is EMPTY; AMENDED the value of dwc:geodeticDatum if it could 
        // be unambiguously interpreted as a value in bdq:sourceAuthority; 
        // otherwise NOT_AMENDED bdq:sourceAuthority = GBIF geodeticDatum 
        // thesaurus, when available 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority

        return result;
    }

    /**
     * Does the ISO country code determined from the value of dwc:country equal the value of dwc:countryCode?
     *
     * Provides: VALIDATION_COUNTRY_COUNTRYCODE_CONSISTENT
     *
     * @param country the provided dwc:country to evaluate
     * @param countryCode the provided dwc:countryCode to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_COUNTRY_COUNTRYCODE_CONSISTENT", description="Does the ISO country code determined from the value of dwc:country equal the value of dwc:countryCode?")
    @Provides("b23110e7-1be7-444a-a677-cdee0cf4330c")
    public DQResponse<ComplianceValue> validationCountryCountrycodeConsistent(@ActedUpon("dwc:country") String country, @ActedUpon("dwc:countryCode") String countryCode) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if either 
        // of the terms dwc:country or dwc:countryCode are EMPTY; COMPLIANT 
        // if the value of the country code determined from the value 
        // of dwc:country is equal to the value of dwc:countryCode; 
        // otherwise NOT_COMPLIANT bdq:sourceAuthority is "ISO 3166-1-alpha-2" 
        // [https://restcountries.eu/#api-endpoints-list-of-codes, 
        // https://www.iso.org/obp/ui/#search] 

        return result;
    }

    /**
     * Propose amendment(s) to the values of dwc:minimumElevationInMeters and/or dwc:maximumElevationInMeters if they can be interpreted from dwc:verbatimElevation.
     *
     * Provides: AMENDMENT_MINELEVATION-MAXELEVATION_FROM_VERBATIM
     *
     * @param minimumElevationInMeters the provided dwc:minimumElevationInMeters to evaluate
     * @param maximumElevationInMeters the provided dwc:maximumElevationInMeters to evaluate
     * @param verbatimElevation the provided dwc:verbatimElevation to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Amendment(label="AMENDMENT_MINELEVATION-MAXELEVATION_FROM_VERBATIM", description="Propose amendment(s) to the values of dwc:minimumElevationInMeters and/or dwc:maximumElevationInMeters if they can be interpreted from dwc:verbatimElevation.")
    @Provides("2d638c8b-4c62-44a0-a14d-fa147bf9823d")
    public DQResponse<AmendmentValue> amendmentMinelevationMaxelevationFromVerbatim(@ActedUpon("dwc:minimumElevationInMeters") String minimumElevationInMeters, @ActedUpon("dwc:maximumElevationInMeters") String maximumElevationInMeters, @ActedUpon("dwc:verbatimElevation") String verbatimElevation) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:verbatimElevation 
        // is EMPTY or the value is not unambiguously interpretable 
        // or dwc:minimumElevationInMeters and/or dwc:maximumElevationInMeters 
        // are not EMPTY; FILLED_IN the values of dwc:minimumElevationInMeters 
        // and/or dwc:maximumElevationInMeters if they could be unambiguously 
        // interpreted from dwc:verbatimElevation; otherwise NOT_AMENDED 
        // 

        return result;
    }


    /**
     * Propose amendment to the value of dwc:countryCode if dwc:decimalLatitude and dwc:decimalLongitude fall within a boundary from the bdq:sourceAuthority that is attributable to a single valid country code.
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
    @Amendment(label="AMENDMENT_COUNTRYCODE_FROM_COORDINATES", description="Propose amendment to the value of dwc:countryCode if dwc:decimalLatitude and dwc:decimalLongitude fall within a boundary from the bdq:sourceAuthority that is attributable to a single valid country code.")
    @Provides("8c5fe9c9-4ba9-49ef-b15a-9ccd0424e6ae")
    public DQResponse<AmendmentValue> amendmentCountrycodeFromCoordinates(@ActedUpon("dwc:decimalLatitude") String decimalLatitude, @ActedUpon("dwc:decimalLongitude") String decimalLongitude, @ActedUpon("dwc:geodeticDatum") String geodeticDatum, @ActedUpon("dwc:countryCode") String countryCode, @ActedUpon("dwc:coordinatePrecision") String coordinatePrecision) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority[countryShapes] 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if either 
        // dwc:decimalLatitude or dwc:decimalLongitude is EMPTY or 
        // uninterpretable, or if dwc:countryCode is NOT_EMPTY; FILLED_IN 
        // dwc:countryCode if dwc:decimalLatitude and dwc:decimalLongitude 
        // fall within a boundary from the bdq:sourceAuthority[countryShapes] 
        // that is attributable to a single valid country code; otherwise 
        // NOT_AMENDED. <ul><li>bdq:sourceAuthority default = "ADM1 
        // boundaries" [https://gadm.org] UNION with "EEZs" [https://marineregions.org] 
        // <li>bdq:sourceAuthority[countryCode] is "ISO 3166 country 
        // codes" [https://www.iso.org/iso-3166-country-codes.html] 
        // </li></ul> 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority

        return result;
    }

    /**
     * Is there a value in dwc:geodeticDatum?
     *
     * Provides: VALIDATION_GEODETICDATUM_NOTEMPTY
     *
     * @param geodeticDatum the provided dwc:geodeticDatum to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_GEODETICDATUM_NOTEMPTY", description="Is there a value in dwc:geodeticDatum?")
    @Provides("239ec40e-a729-4a8e-ba69-e0bf03ac1c44")
    public DQResponse<ComplianceValue> validationGeodeticdatumNotempty(@ActedUpon("dwc:geodeticDatum") String geodeticDatum) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // COMPLIANT if dwc:geodeticDatum is not EMPTY; otherwise NOT_COMPLIANT 
        // 

        return result;
    }

    /**
     * Is the value of dwc:decimalLatitude a number between -90 and 90 inclusive?
     *
     * Provides: VALIDATION_DECIMALLATITUDE_INRANGE
     *
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_DECIMALLATITUDE_INRANGE", description="Is the value of dwc:decimalLatitude a number between -90 and 90 inclusive?")
    @Provides("b6ecda2a-ce36-437a-b515-3ae94948fe83")
    public DQResponse<ComplianceValue> validationDecimallatitudeInrange(@ActedUpon("dwc:decimalLatitude") String decimalLatitude) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:decimalLatitude is 
        // EMPTY or the value is not interpretable as a number; COMPLIANT 
        // if the value of dwc:decimalLatitude is between -90 and 90, 
        // inclusive; otherwise NOT_COMPLIANT 

        return result;
    }


    /**
     * Is the combination of the values of the terms dwc:continent, dwc:country, dwc:countryCode, dwc:stateProvince, dwc:county, dwc:municipality consistent with the bdq:sourceAuthority?
     *
     * Provides: VALIDATION_GEOGRAPHY_CONSISTENT
     *
     * @param continent the provided dwc:continent to evaluate
     * @param county the provided dwc:county to evaluate
     * @param country the provided dwc:country to evaluate
     * @param countryCode the provided dwc:countryCode to evaluate
     * @param municipality the provided dwc:municipality to evaluate
     * @param stateProvince the provided dwc:stateProvince to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_GEOGRAPHY_CONSISTENT", description="Is the combination of the values of the terms dwc:continent, dwc:country, dwc:countryCode, dwc:stateProvince, dwc:county, dwc:municipality consistent with the bdq:sourceAuthority?")
    @Provides("78640f09-8353-411a-800e-9b6d498fb1c9")
    public DQResponse<ComplianceValue> validationGeographyConsistent(@ActedUpon("dwc:continent") String continent, @ActedUpon("dwc:county") String county, @ActedUpon("dwc:country") String country, @ActedUpon("dwc:countryCode") String countryCode, @ActedUpon("dwc:municipality") String municipality, @ActedUpon("dwc:stateProvince") String stateProvince) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if all 
        // of the terms dwc:continent, dwc:country, dwc:countryCode, 
        // dwc:stateProvince, dwc:county, dwc:municipality are EMPTY; 
        // COMPLIANT if the combination of values of dwc:continent, 
        // dwc:country, dwc:countryCode, dwc:stateProvince, dwc:county, 
        // dwc:municipality are consistent with the bdq:sourceAuthority; 
        // otherwise NOT_COMPLIANT bdq:sourceAuthority default = "The 
        // Getty Thesaurus of Geographic Names (TGN)" [https://www.getty.edu/research/tools/vocabularies/tgn/index.html] 
        // 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority

        return result;
    }


    /**
     * Is the value of dwc:maximumElevationInMeters within the Parameter range?
     *
     * Provides: VALIDATION_MAXELEVATION_INRANGE
     *
     * @param maximumElevationInMeters the provided dwc:maximumElevationInMeters to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_MAXELEVATION_INRANGE", description="Is the value of dwc:maximumElevationInMeters within the Parameter range?")
    @Provides("c971fe3f-84c1-4636-9f44-b1ec31fd63c7")
    public DQResponse<ComplianceValue> validationMaxelevationInrange(@ActedUpon("dwc:maximumElevationInMeters") String maximumElevationInMeters) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:maximumElevationInMeters 
        // is EMPTY or the value cannot be interpreted as a number; 
        // COMPLIANT if the value of dwc:maximumElevationInMeters is 
        // within the range of bdq:minimumValidElevationInMeters to 
        // bdq:maximumValidElevationInMeters inclusive; otherwise NOT_COMPLIANT 
        // 

        //TODO: Parameters. This test is defined as parameterized.
        // Default values: bdq:minimumValidElevationInMeters="-430"; bdq:maximumValidElevationInMeters="8850"

        return result;
    }

    /**
     * Propose amendment to one or more of the values dwc:continent, dwc:country, dwc:countryCode, dwc:stateProvince, dwc:county, dwc:municipality using bdq:sourceAuthority.
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
    @Amendment(label="AMENDMENT_GEOGRAPHY_STANDARDIZED", description="Propose amendment to one or more of the values dwc:continent, dwc:country, dwc:countryCode, dwc:stateProvince, dwc:county, dwc:municipality using bdq:sourceAuthority.")
    @Provides("ba2d868c-afa3-409a-836b-fdcea9f75945")
    public DQResponse<AmendmentValue> amendmentGeographyStandardized(@ActedUpon("dwc:continent") String continent, @ActedUpon("dwc:county") String county, @ActedUpon("dwc:country") String country, @ActedUpon("dwc:countryCode") String countryCode, @ActedUpon("dwc:municipality") String municipality, @ActedUpon("dwc:stateProvince") String stateProvince) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available or if the combination of the values of 
        // dwc:continent, dwc:country, dwc:countryCode, dwc:stateProvince, 
        // dwc:county, dwc:municipality could not be unambiguously 
        // resolved from the bdq:sourceAuthority; AMENDED if the values 
        // of dwc:continent, dwc:country, dwc:countryCode, dwc:stateProvince, 
        // dwc:county, dwc:municipality could be unambiguously interpreted 
        // from values in bdq:sourceAuthority; otherwise NOT_AMENDED. 
        // bdq:sourceAuthority default = "The Getty Thesaurus of Geographic 
        // Names (TGN)" [https://www.getty.edu/research/tools/vocabularies/tgn/index.html] 
        // 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority

        return result;
    }

    /**
     * Can the individual values of the terms dwc:continent, dwc:country, dwc:countryCode, dwc:stateProvince, dwc:county, dwc:municipality be unambiguously resolved from bdq:sourceAuthority?
     *
     * Provides: VALIDATION_GEOGRAPHY_STANDARD
     *
     * @param continent the provided dwc:continent to evaluate
     * @param county the provided dwc:county to evaluate
     * @param country the provided dwc:country to evaluate
     * @param countryCode the provided dwc:countryCode to evaluate
     * @param municipality the provided dwc:municipality to evaluate
     * @param stateProvince the provided dwc:stateProvince to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_GEOGRAPHY_STANDARD", description="Can the individual values of the terms dwc:continent, dwc:country, dwc:countryCode, dwc:stateProvince, dwc:county, dwc:municipality be unambiguously resolved from bdq:sourceAuthority?")
    @Provides("9d6f53c0-775b-4579-b7a4-5e5f093aa512")
    public DQResponse<ComplianceValue> validationGeographyStandard(@ActedUpon("dwc:continent") String continent, @ActedUpon("dwc:county") String county, @ActedUpon("dwc:country") String country, @ActedUpon("dwc:countryCode") String countryCode, @ActedUpon("dwc:municipality") String municipality, @ActedUpon("dwc:stateProvince") String stateProvince) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if all 
        // of the terms dwc:continent, dwc:country, dwc:countryCode, 
        // dwc:stateProvince, dwc:county, dwc:municipality are EMPTY; 
        // COMPLIANT if the individual values of dwc:continent, dwc:country, 
        // dwc:countryCode, dwc:stateProvince, dwc:county, dwc:municipality 
        // can be unambiguously resolved from the bdq:sourceAuthority; 
        // otherwise NOT_COMPLIANT bdq:sourceAuthority default = "The 
        // Getty Thesaurus of Geographic Names (TGN)" [https://www.getty.edu/research/tools/vocabularies/tgn/index.html]l 
        // 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority

        return result;
    }

}
