/**
 * DwCGeoRefDQDefaults.java
 */
package org.filteredpush.qc.georeference;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datakurator.ffdq.annotations.ActedUpon;
import org.datakurator.ffdq.annotations.Amendment;
import org.datakurator.ffdq.annotations.Provides;
import org.datakurator.ffdq.annotations.ProvidesVersion;
import org.datakurator.ffdq.annotations.Specification;
import org.datakurator.ffdq.annotations.Validation;
import org.datakurator.ffdq.api.DQResponse;
import org.datakurator.ffdq.api.result.AmendmentValue;
import org.datakurator.ffdq.api.result.ComplianceValue;

/**
 * @author mole
 *
 */
public class DwCGeoRefDQDefaults extends DwCGeoRefDQ {

	private static final Log logger = LogFactory.getLog(DwCGeoRefDQDefaults.class);

    /**
     * Is the value of dwc:maximumDepthInMeters within the Parameter range?  Uses the
     * default parameter values of 0 to 110000.
     *
     * Provides: VALIDATION_MAXDEPTH_INRANGE
     *
     * @param maximumDepthInMeters the provided dwc:maximumDepthInMeters to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_MAXDEPTH_INRANGE", description="Is the value of dwc:maximumDepthInMeters within the Parameter range?")
    @Provides("3f1db29a-bfa5-40db-9fd1-fde020d81939")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/3f1db29a-bfa5-40db-9fd1-fde020d81939/2022-09-08")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if dwc:maximumDepthInMeters is EMPTY or is not interpretable as a number greater than or equal to zero; COMPLIANT if the value of dwc:maximumDepthInMeters is within the range of bdq:minimumValidDepthInMeters to bdq:maximumValidDepthInMeters inclusive; otherwise NOT_COMPLIANT ")
    public static DQResponse<ComplianceValue> validationMaxdepthInrange(
    		@ActedUpon("dwc:maximumDepthInMeters") String maximumDepthInMeters) { 
    	return (DwCGeoRefDQ.validationMaxdepthInrange(maximumDepthInMeters, 0d, 11000d));
    }
	
    /**
    * Propose amendment to dwc:geodeticDatum using the value of bdq:defaultGeodeticDatum if dwc:geodeticDatum is empty.
    * uses the default parameter value.
    *
    * Provides: 102 AMENDMENT_GEODETICDATUM_ASSUMEDDEFAULT
    *
    * @param coordinateUncertantyInMeters the provided dwc:coordinateUncertantyInMeters to evaluate
    * @param geodeticDatum the provided dwc:geodeticDatum to evaluate
    * @return DQResponse the response of type AmendmentValue to return
    */
   @Amendment(label="AMENDMENT_GEODETICDATUM_ASSUMEDDEFAULT", description="Propose amendment to dwc:geodeticDatum using the value of bdq:defaultGeodeticDatum if dwc:geodeticDatum is empty.")
   @Provides("7498ca76-c4d4-42e2-8103-acacccbdffa7")
   public DQResponse<AmendmentValue> amendmentGeodeticdatumAssumeddefault(
   		@ActedUpon("dwc:coordinateUncertaintyInMeters") String coordinateUncertaintyInMeters, 
   		@ActedUpon("dwc:geodeticDatum") String geodeticDatum) 
   {
	   return DwCGeoRefDQ.amendmentGeodeticdatumAssumeddefault(coordinateUncertaintyInMeters, geodeticDatum, null);
   }
   
    /**
     * Is the value of dwc:minimumDepthInMeters within the Parameter range?
     * uses the default minimum and maximum depth values.
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
    		@ActedUpon("dwc:minimumDepthInMeters") String minimumDepthInMeters) { 
    	return DwCGeoRefDQ.validationMindepthInrange(minimumDepthInMeters, 0d, 11000d);
    }
    
    /**
     * Is the value of dwc:maximumElevationInMeters within the Parameter range?
     *
     * Provides: #112 VALIDATION_MAXELEVATION_INRANGE
     *
     * @param maximumElevationInMeters the provided dwc:maximumElevationInMeters to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_MAXELEVATION_INRANGE", description="Is the value of dwc:maximumElevationInMeters within the Parameter range?")
    @Provides("c971fe3f-84c1-4636-9f44-b1ec31fd63c7")
    public static DQResponse<ComplianceValue> validationMaxelevationInrange(
    		@ActedUpon("dwc:maximumElevationInMeters") String maximumElevationInMeters) {
    	return DwCGeoRefDQ.validationMaxelevationInrange(maximumElevationInMeters,-430d, 8850d);
    }
    
    
    /**
     * Is the value of dwc:minimumElevationInMeters within the Parameter range?
     *
     * Provides: #39 VALIDATION_MINELEVATION_INRANGE
     *
     * @param minimumElevationInMeters the provided dwc:minimumElevationInMeters to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_MINELEVATION_INRANGE", description="Is the value of dwc:minimumElevationInMeters within the Parameter range?")
    @Provides("0bb8297d-8f8a-42d2-80c1-558f29efe798")
    public static DQResponse<ComplianceValue> validationMinelevationInrange(
    		@ActedUpon("dwc:minimumElevationInMeters") String minimumElevationInMeters) {
    	return DwCGeoRefDQ.validationMinelevationInrange(minimumElevationInMeters, -430d, 8850d);
    }
    
}
