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
     * Provides: VALIDATION_COUNTRY_FOUND
     * Version: 2022-08-29
     *
     * @param country the provided dwc:country to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_COUNTRY_FOUND", description="Does the value of dwc:country occur in bdq:sourceAuthority?")
    @Provides("69b2efdc-6269-45a4-aecb-4cb99c2ae134")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/69b2efdc-6269-45a4-aecb-4cb99c2ae134/2022-08-29")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:country was EMPTY; COMPLIANT if value of dwc:country is a place type equivalent to 'nation' by the bdq:sourceAuthority; otherwise NOT_COMPLIANT bdq:sourceAuthority default = 'The Getty Thesaurus of Geographic Names (TGN)' [https://www.getty.edu/research/tools/vocabularies/tgn/index.html]")
    public DQResponse<ComplianceValue> validationCountryFound(@ActedUpon("dwc:country") String country) {
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
     * Propose amendment to the values of dwc:decimalLatitude and dwc:decimalLongitude from information in the verbatim coordinates terms.
     *
     * Provides: AMENDMENT_COORDINATES_FROM_VERBATIM
     * Version: 2023-01-13
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
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/3c2590c7-af8a-4eb4-af57-5f73ba9d1f8e/2023-01-13")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if either dwc:decimalLatitude or dwc:decimalLongitude were not EMPTY, or either dwc:verbatimLatitude and dwc:verbatimLongitude, or dwc:verbatimCoordinates were not unambiguously interpretable into valid coordinates; FILLED_IN the values of dwc:decimalLatitude and dwc:decimalLongitude if unambiguous values can be interpreted from  dwc:verbatimCoordinates or dwc:verbatimLatitude and dwc:verbatimLongitude, plus dwc:verbatimCoordinateSystem and dwc:verbatimSRS; otherwise NOT_AMENDED ")
    public DQResponse<AmendmentValue> amendmentCoordinatesFromVerbatim(@ActedUpon("dwc:verbatimCoordinateSystem") String verbatimCoordinateSystem, @ActedUpon("dwc:decimalLatitude") String decimalLatitude, @ActedUpon("dwc:decimalLongitude") String decimalLongitude, @ActedUpon("dwc:verbatimCoordinates") String verbatimCoordinates, @ActedUpon("dwc:verbatimLongitude") String verbatimLongitude, @ActedUpon("dwc:verbatimSRS") String verbatimSRS, @ActedUpon("dwc:verbatimLatitude") String verbatimLatitude) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if either dwc:decimalLatitude 
        // or dwc:decimalLongitude were not EMPTY, or either dwc:verbatimLatitude 
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
     * Propose amendment to the value of dwc:geodeticDatum and potentially to dwc:decimalLatitude and/or dwc:decimalLongitude based on a conversion between spatial reference systems.
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
    public DQResponse<AmendmentValue> amendmentCoordinatesConverted(@ActedUpon("dwc:decimalLatitude") String decimalLatitude, @ActedUpon("dwc:decimalLongitude") String decimalLongitude, @ActedUpon("dwc:coordinateUncertaintyInMeters") String coordinateUncertaintyInMeters, @ActedUpon("dwc:geodeticDatum") String geodeticDatum, @ActedUpon("dwc:coordinatePrecision") String coordinatePrecision) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:decimalLatitude or 
        // dwc:decimalLongitude or dwc:geodeticDatum are EMPTY or not 
        // interpretable as a value decimal number; AMENDED the values 
        // of dwc:decimalLatitude, dwc:decimalLongitude and dwc:geodeticDatum 
        // by a conversion between spatial reference systems; otherwise 
        // NOT_AMENDED 

        return result;
    }

    /**
     * Propose amendment to the value of dwc:countryCode if it can be interpreted as an ISO country code.
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
    public DQResponse<AmendmentValue> amendmentCountrycodeStandardized(@ActedUpon("dwc:countryCode") String countryCode) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available; INTERNAL_PREREQUISTITES_NOT_MET if the 
        // value of dwc:countryCode is EMPTY; AMENDED the value of 
        // dwc:countryCode if it can be unambiguously interpreted from 
        // bdq:sourceAuthority; otherwise NOT_AMENDED bdq:sourceAuthority 
        // is "ISO 3166-1-alpha-2" [https://restcountries.eu/#api-endpoints-list-of-codes, 
        // https://www.iso.org/obp/ui/#search] 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority

        return result;
    }

    /**
     * Do the geographic coordinates fall on or within the boundaries of the territory given in dwc:countryCode or its Exclusive Economic Zone?
     *
     * Provides: VALIDATION_COORDINATES_COUNTRYCODE_CONSISTENT
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
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if one or more of dwc:decimalLatitude, dwc:decimalLongitude, or dwc:countryCode are EMPTY or invalid; COMPLIANT if the geographic coordinates fall on or within the boundary defined by the union of the boundary of the country from dwc:countryCode plus it's Exclusive Economic Zone, if any, plus an exterior buffer given by bdq:spatialBufferInMeters; otherwise NOT_COMPLIANT <ul><li>bdq:sourceAuthority default = 'ADM1 boundaries' [https://gadm.org] UNION with 'EEZs' [https://marineregions.org] </li><li>bdq:spatialBufferInMeters default = '3000'</li></ul>")
    public DQResponse<ComplianceValue> validationCoordinatesCountrycodeConsistent(@ActedUpon("dwc:decimalLatitude") String decimalLatitude, @ActedUpon("dwc:decimalLongitude") String decimalLongitude, @ActedUpon("dwc:countryCode") String countryCode) {
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
        // <ul><li>bdq:sourceAuthority default = "ADM1 boundaries" 
        // [https://gadm.org] UNION with "EEZs" [https://marineregions.org] 
        // </li><li>bdq:spatialBufferInMeters default = "3000"</li></ul> 
        // 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority; bdq:spatialBufferInMeters

        return result;
    }

    /**
     * Does the marine/non-marine biome of a taxon from the bdq:sourceAuthority match the biome at the location given by the coordinates?
     *
     * Provides: VALIDATION_COORDINATES_TERRESTRIALMARINE
     * Version: 2022-03-02
     *
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate
     * @param scientificName the provided dwc:scientificName to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_COORDINATES_TERRESTRIALMARINE", description="Does the marine/non-marine biome of a taxon from the bdq:sourceAuthority match the biome at the location given by the coordinates?")
    @Provides("b9c184ce-a859-410c-9d12-71a338200380")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/b9c184ce-a859-410c-9d12-71a338200380/2022-03-02")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if either bdq:sourceAuthority[taxonomyismarine] or bdq:sourceAuthority[geospatialland] are not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:dcientificName was EMPTY or the marine/non-marine status of the taxon is not interpretable from bdq:sourceAuthority[taxonomyismarine] or the values of dwc:decimalLatitude or dwc:decimalLongitude are EMPTY; COMPLIANT if the taxon marine/non-marine status from bdq:sourceAuthority[taxonomyismarine] matches the marine/non-marine status of dwc:decimalLatitude and dwc:decimalLongitude on the boundaries given by bdq:sourceAuthority[geospatialland] plus an exterior buffer given by bdq:spatialBufferInMeters; otherwise NOT_COMPLIANT <ul><li>bdq:sourceAuthority[taxonismarine] default = 'WORMS' [https://www.marinespecies.org/aphia.php?p=webservice] </li><li>bdq:sourceAuthority[geospatialland] default = the union of 'NaturalEarth 10m-physical-vectors for Land' [https://www.naturalearthdata.com/http//www.naturalearthdata.com/download/10m/physical/ne_10m_land.zip] and 'NaturalEarth Minor Islands' [https://www.naturalearthdata.com/http//www.naturalearthdata.com/download/10m/physical/ne_10m_minor_islands.zip]</li><li>bdq:spatialBufferInMeters default = '3000' </li></ul>")
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
     * Version: 2022-03-30
     *
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate
     * @param countryCode the provided dwc:countryCode to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Amendment(label="AMENDMENT_COORDINATES_TRANSPOSED", description="Propose amendment of the signs of dwc:decimalLatitude and/or dwc:decimalLongitude to align the location with the dwc:countryCode.")
    @Provides("f2b4a50a-6b2f-4930-b9df-da87b6a21082")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/f2b4a50a-6b2f-4930-b9df-da87b6a21082/2022-03-30")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if any of dwc:decimalLatitude or dwc:decimalLongitude or dwc:countryCode are EMPTY; AMENDED dwc:decimalLatitude and dwc:decimalLongitude if the coordinates were transposed or one or more of the signs of the coordinates were reversed to align the location with dwc:countryCode; otherwise NOT_AMENDED ")
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
     * Version: 2022-04-19
     *
     * @param verbatimDepth the provided dwc:verbatimDepth to evaluate
     * @param maximumDepthInMeters the provided dwc:maximumDepthInMeters to evaluate
     * @param minimumDepthInMeters the provided dwc:minimumDepthInMeters to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Amendment(label="AMENDMENT_MINDEPTH-MAXDEPTH_FROM_VERBATIM", description="Propose amendments of the values of dwc:minimumDepthInMeters and/or dwc:maximumDepthInMeters if they can be interpreted from dwc:verbatimDepth.")
    @Provides("c5658b83-4471-4f57-9d94-bf7d0a96900c")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/c5658b83-4471-4f57-9d94-bf7d0a96900c/2022-04-19")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if dwc:verbatimDepth is EMPTY or the value is not unambiguously interpretable or dwc:minimumDepthInMeters and dwc:maximumDepthInMeters are not EMPTY; FILLED_IN the value of dwc:minimumDepthInMeters and/or dwc:maximumDepthInMeters if they could be unambiguously determined from dwc:verbatimDepth; otherwise NOT_AMENDED ")
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
     * Version: 2023-03-19
     *
     * @param decimalLatitude the provided dwc:decimalLatitude to evaluate
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate
     * @param stateProvince the provided dwc:stateProvince to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_COORDINATES_STATEPROVINCE_CONSISTENT", description="Do the geographic coordinates fall on or within the boundary from the bdq:sourceAuthority for the given dwc:stateProvince or within the distance given by bdq:spatialBufferInMeters outside that boundary?")
    @Provides("f18a470b-3fe1-4aae-9c65-a6d3db6b550c")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/f18a470b-3fe1-4aae-9c65-a6d3db6b550c/2023-03-19")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority was not available; INTERNAL_PREREQUISITES_NOT_MET if the values of dwc:decimalLatitude, dwc:decimalLongitude, or dwc:stateProvince are EMPTY or invalid; COMPLIANT if the geographic coordinates fall on or within the boundary from the bdq:sourceAuthority for the given dwc:stateProvince (after coordinate reference system transformations, if any, have been accounted for), or within the distance given by bdq:spatialBufferInMeters outside that boundary; otherwise NOT_COMPLIANT. <ul><li>bdq:sourceAuthority default = 'ADM1 boundaries' [https://gadm.org]</li><li> bdq:spatialBufferInMeters default = '3000' </li></ul>")
    public DQResponse<ComplianceValue> validationCoordinatesStateprovinceConsistent(@ActedUpon("dwc:decimalLatitude") String decimalLatitude, @ActedUpon("dwc:decimalLongitude") String decimalLongitude, @ActedUpon("dwc:stateProvince") String stateProvince) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // was not available; INTERNAL_PREREQUISITES_NOT_MET if the 
        // values of dwc:decimalLatitude, dwc:decimalLongitude, or 
        // dwc:stateProvince are EMPTY or invalid; COMPLIANT if the 
        // geographic coordinates fall on or within the boundary from 
        // the bdq:sourceAuthority for the given dwc:stateProvince 
        // (after coordinate reference system transformations, if any, 
        // have been accounted for), or within the distance given by 
        // bdq:spatialBufferInMeters outside that boundary; otherwise 
        // NOT_COMPLIANT. <ul><li>bdq:sourceAuthority default = "ADM1 
        // boundaries" [https://gadm.org]</li><li> bdq:spatialBufferInMeters 
        // default = "3000" </li></ul> 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:sourceAuthority; bdq:spatialBufferInMeters

        return result;
    }


    /**
     * Propose amendment to the value of dwc:geodeticDatum using bdq:sourceAuthority.
     *
     * Provides: AMENDMENT_GEODETICDATUM_STANDARDIZED
     * Version: 2022-03-30
     *
     * @param geodeticDatum the provided dwc:geodeticDatum to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Amendment(label="AMENDMENT_GEODETICDATUM_STANDARDIZED", description="Propose amendment to the value of dwc:geodeticDatum using bdq:sourceAuthority.")
    @Provides("0345b325-836d-4235-96d0-3b5caf150fc0")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/0345b325-836d-4235-96d0-3b5caf150fc0/2022-03-30")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority was not available; INTERNAL_PREREQUISITES_NOT_MET if dwc:geodeticDatum is EMPTY; AMENDED the value of dwc:geodeticDatum if it could be unambiguously interpreted as a value in bdq:sourceAuthority; otherwise NOT_AMENDED bdq:sourceAuthority = GBIF geodeticDatum thesaurus, when available")
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
     * Version: 2022-05-02
     *
     * @param country the provided dwc:country to evaluate
     * @param countryCode the provided dwc:countryCode to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_COUNTRY_COUNTRYCODE_CONSISTENT", description="Does the ISO country code determined from the value of dwc:country equal the value of dwc:countryCode?")
    @Provides("b23110e7-1be7-444a-a677-cdee0cf4330c")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/b23110e7-1be7-444a-a677-cdee0cf4330c/2022-05-02")
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority is not available; INTERNAL_PREREQUISITES_NOT_MET if either of the terms dwc:country or dwc:countryCode are EMPTY; COMPLIANT if the value of the country code determined from the value of dwc:country is equal to the value of dwc:countryCode; otherwise NOT_COMPLIANT bdq:sourceAuthority is 'ISO 3166-1-alpha-2' [https://restcountries.eu/#api-endpoints-list-of-codes, https://www.iso.org/obp/ui/#search]")
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
     * Version: 2023-02-27
     *
     * @param minimumElevationInMeters the provided dwc:minimumElevationInMeters to evaluate
     * @param maximumElevationInMeters the provided dwc:maximumElevationInMeters to evaluate
     * @param verbatimElevation the provided dwc:verbatimElevation to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Amendment(label="AMENDMENT_MINELEVATION-MAXELEVATION_FROM_VERBATIM", description="Propose amendment(s) to the values of dwc:minimumElevationInMeters and/or dwc:maximumElevationInMeters if they can be interpreted from dwc:verbatimElevation.")
    @Provides("2d638c8b-4c62-44a0-a14d-fa147bf9823d")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/2d638c8b-4c62-44a0-a14d-fa147bf9823d/2023-02-27")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if dwc:verbatimElevation is EMPTY or dwc:minimumElevationInMeters and/or dwc:maximumElevationInMeters are not EMPTY; FILLED_IN the values of dwc:minimumElevationInMeters and/or dwc:maximumElevationInMeters that could be unambiguously interpreted from dwc:verbatimElevation; otherwise NOT_AMENDED' ")
    public DQResponse<AmendmentValue> amendmentMinelevationMaxelevationFromVerbatim(@ActedUpon("dwc:minimumElevationInMeters") String minimumElevationInMeters, @ActedUpon("dwc:maximumElevationInMeters") String maximumElevationInMeters, @ActedUpon("dwc:verbatimElevation") String verbatimElevation) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:verbatimElevation 
        // is EMPTY or dwc:minimumElevationInMeters and/or dwc:maximumElevationInMeters 
        // are not EMPTY; FILLED_IN the values of dwc:minimumElevationInMeters 
        // and/or dwc:maximumElevationInMeters that could be unambiguously 
        // interpreted from dwc:verbatimElevation; otherwise NOT_AMENDED" 
        // 

        return result;
    }

    /**
     * Is there a value in dwc:dataGeneralizations?
     *
     * Provides: ISSUE_DATAGENERALIZATIONS_NOTEMPTY
     * Version: 2022-05-16
     *
     * @param dataGeneralizations the provided dwc:dataGeneralizations to evaluate
     * @return DQResponse the response of type IssueValue to return
     */
    @Issue(label="ISSUE_DATAGENERALIZATIONS_NOTEMPTY", description="Is there a value in dwc:dataGeneralizations?")
    @Provides("13d5a10e-188e-40fd-a22c-dbaa87b91df2")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/13d5a10e-188e-40fd-a22c-dbaa87b91df2/2022-05-16")
    @Specification("POTENTIAL_ISSUE if dwc:dataGeneralizations is not EMPTY; otherwise NOT_ISSUE ")
    public DQResponse<IssueValue> issueDatageneralizationsNotempty(@ActedUpon("dwc:dataGeneralizations") String dataGeneralizations) {
        DQResponse<IssueValue> result = new DQResponse<IssueValue>();

        //TODO:  Implement specification
        // POTENTIAL_ISSUE if dwc:dataGeneralizations is not EMPTY; 
        // otherwise NOT_ISSUE 

        return result;
    }

    /**
     * Propose amendment to the value of dwc:countryCode if dwc:decimalLatitude and dwc:decimalLongitude fall within a boundary from the bdq:sourceAuthority that is attributable to a single valid country code.
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
    @Specification("EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority[countryShapes] is not available; INTERNAL_PREREQUISITES_NOT_MET if either dwc:decimalLatitude or dwc:decimalLongitude is EMPTY or uninterpretable, or if dwc:countryCode is NOT_EMPTY; FILLED_IN dwc:countryCode if dwc:decimalLatitude and dwc:decimalLongitude fall within a boundary from the bdq:sourceAuthority[countryShapes] that is attributable to a single valid country code; otherwise NOT_AMENDED. <ul><li>bdq:sourceAuthority default = 'ADM1 boundaries' [https://gadm.org] UNION with 'EEZs' [https://marineregions.org] <li>bdq:sourceAuthority[countryCode] is 'ISO 3166 country codes' [https://www.iso.org/iso-3166-country-codes.html] </li></ul>")
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
     * Propose amendment to dwc:geodeticDatum using the value of bdq:defaultGeodeticDatum if dwc:geodeticDatum is empty. If dwc:coordinateUncertaintyInMeters is not empty and there are not empty values for dwc:latitude and dwc:longitude, amend dwc:coordinateUncertaintyInMeters by adding a maximum datum shift.
     *
     * Provides: AMENDMENT_GEODETICDATUM_ASSUMEDDEFAULT
     * Version: 2022-09-12
     *
     * @param decimalLongitude the provided dwc:decimalLongitude to evaluate
     * @param coordinateUncertaintyInMeters the provided dwc:coordinateUncertaintyInMeters to evaluate
     * @param geodeticDatum the provided dwc:geodeticDatum to evaluate
     * @param decimatitude the provided dwc:decimatitude to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Amendment(label="AMENDMENT_GEODETICDATUM_ASSUMEDDEFAULT", description="Propose amendment to dwc:geodeticDatum using the value of bdq:defaultGeodeticDatum if dwc:geodeticDatum is empty. If dwc:coordinateUncertaintyInMeters is not empty and there are not empty values for dwc:latitude and dwc:longitude, amend dwc:coordinateUncertaintyInMeters by adding a maximum datum shift.")
    @Provides("7498ca76-c4d4-42e2-8103-acacccbdffa7")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/7498ca76-c4d4-42e2-8103-acacccbdffa7/2022-09-12")
    @Specification("If dwc:geodeticDatum is EMPTY, fill in the value of dwc:geodeticDatum with the value of bdq:defaultGeodeticDatum, report FILLED_IN and, if dwc:coordinateUncertaintyInMeters, dwc:decimalLatitude and dwc:decimalLongitude are NOT_EMPTY, amend the value of dwc:coordinateUncertaintyInMeters by adding the maximum datum shift between the specified bdq:defaultGeodeticDatum and any other datum at the provided dwc:decimalLatitude and dwc:decimalLongitude and instead report AMENDED; otherwise NOT_AMENDED. ")
    public DQResponse<AmendmentValue> amendmentGeodeticdatumAssumeddefault(@ActedUpon("dwc:decimalLongitude") String decimalLongitude, @ActedUpon("dwc:coordinateUncertaintyInMeters") String coordinateUncertaintyInMeters, @ActedUpon("dwc:geodeticDatum") String geodeticDatum, @ActedUpon("dwc:decimatitude") String decimatitude) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // If dwc:geodeticDatum is EMPTY, fill in the value of dwc:geodeticDatum 
        // with the value of bdq:defaultGeodeticDatum, report FILLED_IN 
        // and, if dwc:coordinateUncertaintyInMeters, dwc:decimalLatitude 
        // and dwc:decimalLongitude are NOT_EMPTY, amend the value 
        // of dwc:coordinateUncertaintyInMeters by adding the maximum 
        // datum shift between the specified bdq:defaultGeodeticDatum 
        // and any other datum at the provided dwc:decimalLatitude 
        // and dwc:decimalLongitude and instead report AMENDED; otherwise 
        // NOT_AMENDED. 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:defaultGeodeticDatum default value="EPSG:4326"
        
        return result;
    }


    /**
     * Is the value of dwc:coordinateUncertaintyInMeters a number between 1 and 20,037,509?
     *
     * Provides: VALIDATION_COORDINATEUNCERTAINTY_INRANGE
     * Version: 2022-03-22
     *
     * @param coordinateUncertaintyInMeters the provided dwc:coordinateUncertaintyInMeters to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_COORDINATEUNCERTAINTY_INRANGE", description="Is the value of dwc:coordinateUncertaintyInMeters a number between 1 and 20,037,509?")
    @Provides("c6adf2ea-3051-4498-97f4-4b2f8a105f57")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/c6adf2ea-3051-4498-97f4-4b2f8a105f57/2022-03-22")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if dwc:coordinateUncertaintyInMeters is EMPTY; COMPLIANT if the value of  dwc:coordinateUncertaintyInMeters can be interpreted as a number between 1 and 20037509 inclusive; otherwise NOT_COMPLIANT ")
    public DQResponse<ComplianceValue> validationCoordinateuncertaintyInrange(@ActedUpon("dwc:coordinateUncertaintyInMeters") String coordinateUncertaintyInMeters) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:coordinateUncertaintyInMeters 
        // is EMPTY; COMPLIANT if the value of dwc:coordinateUncertaintyInMeters 
        // can be interpreted as a number between 1 and 20037509 inclusive; 
        // otherwise NOT_COMPLIANT 

        return result;
    }


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
