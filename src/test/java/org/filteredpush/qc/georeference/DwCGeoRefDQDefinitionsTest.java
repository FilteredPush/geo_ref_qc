/**
 * DwCGeoRefDQDefinitionsTest.java
 */
package org.filteredpush.qc.georeference;

import static org.junit.Assert.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datakurator.ffdq.annotations.ActedUpon;
import org.datakurator.ffdq.annotations.Parameter;
import org.datakurator.ffdq.api.DQResponse;
import org.datakurator.ffdq.api.result.AmendmentValue;
import org.datakurator.ffdq.api.result.ComplianceValue;
import org.datakurator.ffdq.api.result.IssueValue;
import org.datakurator.ffdq.model.ResultState;
import org.junit.Test;

/**
 * @author mole
 *
 */
public class DwCGeoRefDQDefinitionsTest {

	private static final Log logger = LogFactory.getLog(DwCGeoRefDQDefinitionsTest.class);

	@Test
	public void testValidationCoordinatesNotzero() { 
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:decimalLatitude is 
        // EMPTY or is not interpretable as a number, or dwc:decimalLongitude 
        // is EMPTY or is not interpretable as a number; COMPLIANT 
        // if either the value of dwc:decimalLatitude is not = 0 or 
        // the value of dwc:decimalLongitude is not = 0; otherwise 
        // NOT_COMPLIANT 
		
		DQResponse<ComplianceValue> result = DwCGeoRefDQ.validationCoordinatesNotzero(null,null);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		assertNotNull(result.getComment());
		
		result = DwCGeoRefDQ.validationCoordinatesNotzero("A","B");
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		assertNotNull(result.getComment());
		
		result = DwCGeoRefDQ.validationCoordinatesNotzero("1.4","3.6");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
		result = DwCGeoRefDQ.validationCoordinatesNotzero("0","0");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		assertNotNull(result.getComment());
		
		result = DwCGeoRefDQ.validationCoordinatesNotzero("0","3.6");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
		result = DwCGeoRefDQ.validationCoordinatesNotzero("1.4","0");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
		result = DwCGeoRefDQ.validationCoordinatesNotzero("1.4","B");
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		
		result = DwCGeoRefDQ.validationCoordinatesNotzero("A","-26.42552");
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
	
		result = DwCGeoRefDQ.validationCoordinatesNotzero("A","B");
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
	
		result = DwCGeoRefDQ.validationCoordinatesNotzero("1.4","0");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
		result = DwCGeoRefDQ.validationCoordinatesNotzero("0","-26.42552");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
	
		// test does not evaluate sanity of numbers
		result = DwCGeoRefDQ.validationCoordinatesNotzero("1000.00002","-5000.00133");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
		result = DwCGeoRefDQ.validationCoordinatesNotzero(Integer.toString(Integer.MAX_VALUE), Integer.toString(Integer.MAX_VALUE));
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
		// given note: "A record with 0.0 is interpreted as the string "0""
		
		// treat -0 as the same as 0
		result = DwCGeoRefDQ.validationCoordinatesNotzero("-0","-0");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		result = DwCGeoRefDQ.validationCoordinatesNotzero("0.0","0.0");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
	}
	
	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationCountrycodeStandard(java.lang.String)}.
	 */
	@Test
	public void testValidationCountrycodeStandard() {
		
        // Specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:SourceAuthority 
        // is not available; INTERNAL_PREREQUISITES_NOT_MET if the 
        // dwc:countryCode was EMPTY; COMPLIANT if the value of dwc:countryCode 
        // is found in bdq:sourceAuthority; otherwise NOT_COMPLIANT 
		
		DQResponse<ComplianceValue> result = DwCGeoRefDQ.validationCountrycodeStandard(null);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQ.validationCountrycodeStandard("a");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationCountrycodeStandard("UG");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
        // From notes: "This test will fail if there is leading or trailing whitespace or there are leading or trailing non-printing characters."
		result = DwCGeoRefDQ.validationCountrycodeStandard(" UG ");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationCountrycodeStandard("ug");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
	}	

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationMaxdepthInrange(java.lang.String)}.
	 */
	@Test
	public void testValidationMaxdepthOutofrange() {
		
		DQResponse<ComplianceValue> result = DwCGeoRefDQDefaults.validationMaxdepthInrange(null);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQDefaults.validationMaxdepthInrange("a");
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQDefaults.validationMaxdepthInrange("10");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQDefaults.validationMaxdepthInrange("0");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		// negative depth values not allowed
		result = DwCGeoRefDQDefaults.validationMaxdepthInrange("-1");
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQDefaults.validationMaxdepthInrange("11001");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		// Testing parameters 
		
		result = DwCGeoRefDQ.validationMaxdepthInrange("11001", 0d, 12000d);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationMaxdepthInrange("10", 100d, null);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		// specifying a negative minimum will still result in internal prerequisites not met`
		result = DwCGeoRefDQ.validationMaxdepthInrange("-10", -100d, 200d);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
	}
	
	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#amendmentGeodeticdatumAssumeddefault(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAmendmentGeodeticdatumAssumeddefault() {
		
        // Specification
        // FILLED_IN the value of dwc:geodeticDatum to the value of 
        // bdq:defaultGeodeticDatum if dwc:geodeticDatum is EMPTY; 
        // otherwise NOT_AMENDED Source Authority is "epsg" [https://epsg.io] 
        // 
		
	    String coordinateUncertantyInMeters = "";
		String geodeticDatum = "foo";
	    DQResponse<AmendmentValue> result = DwCGeoRefDQ.amendmentGeodeticdatumAssumeddefault(coordinateUncertantyInMeters, geodeticDatum, null);
	    logger.debug(result.getComment());
	    assertEquals(ResultState.NOT_AMENDED.getLabel(), result.getResultState().getLabel());
	    
	    coordinateUncertantyInMeters = "";
		geodeticDatum = "";
	    result = DwCGeoRefDQ.amendmentGeodeticdatumAssumeddefault(coordinateUncertantyInMeters, geodeticDatum, null);
	    logger.debug(result.getComment());
	    assertEquals(ResultState.FILLED_IN.getLabel(), result.getResultState().getLabel());
		assertEquals(1, result.getValue().getObject().size());
		assertEquals("EPSG:4326", result.getValue().getObject().get("dwc:geodeticDatum"));
	    
	}
	
	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationMindepthInrange(java.lang.String)}.
	 */
	@Test
	public void testValidationMindepthInrange() {
		
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:minimumDepthInMeters 
        // is EMPTY, or the value is not zero or a positive number; 
        // COMPLIANT if the value of dwc:minimumDepthInMeters is within 
        //the Parameter range; otherwise NOT_COMPLIANT 
		
		DQResponse<ComplianceValue> result = DwCGeoRefDQDefaults.validationMindepthInrange(null);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		assertNotNull(result.getComment());
		
		result = DwCGeoRefDQDefaults.validationMindepthInrange("a");
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQDefaults.validationMindepthInrange("10");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQDefaults.validationMindepthInrange("10.5");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		// negative depth values not allowed
		result = DwCGeoRefDQDefaults.validationMindepthInrange("-1");
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		// test -0d
		result = DwCGeoRefDQDefaults.validationMindepthInrange("-0");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		// negative depth values not allowed, min integer is negative.
		result = DwCGeoRefDQDefaults.validationMindepthInrange(Integer.toString(Integer.MIN_VALUE));
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		// similarly negative infinity
		result = DwCGeoRefDQDefaults.validationMindepthInrange(Double.toString(Double.NEGATIVE_INFINITY));
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQDefaults.validationMindepthInrange("11001");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQDefaults.validationMindepthInrange("10",1d,100d);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		// negative depth values not allowed, even if specified range goes into negative
		result = DwCGeoRefDQDefaults.validationMindepthInrange("-1",-10d,100d);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQDefaults.validationMindepthInrange("1",2d,100d);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationMindepthInrange("110",10d,100d);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationMinelevationLessthanMaxelevation(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidationMinelevationGreaterthanMaxelevation() {
		
		DQResponse<ComplianceValue> result = DwCGeoRefDQ.validationMinelevationLessthanMaxelevation(null,null);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQ.validationMinelevationLessthanMaxelevation("a","10");
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQ.validationMinelevationLessthanMaxelevation("-1","10");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationMinelevationLessthanMaxelevation("10","100");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationMinelevationLessthanMaxelevation("100","10");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationCoordinateuncertaintyInrange(java.lang.String)}.
	 */
	@Test
	public void testValidationConcertaintyInrange() {
		
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:coordinateUncertaintyInMeters 
        // is EMPTY; COMPLIANT if the value of dwc:coordinateUncertaintyInMeters 
        // can be interpreted as a number between 1 and 20037509 inclusive; 
        // otherwise NOT_COMPLIANT 
        //
		
		DQResponse<ComplianceValue> result = DwCGeoRefDQ.validationCoordinateuncertaintyInrange(null);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		assertNotNull(result.getComment());
		
		result = DwCGeoRefDQ.validationCoordinateuncertaintyInrange("a");
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQ.validationCoordinateuncertaintyInrange("100");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		assertNotNull(result.getComment());
		
		result = DwCGeoRefDQ.validationCoordinateuncertaintyInrange("1");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		assertNotNull(result.getComment());
		
		result = DwCGeoRefDQ.validationCoordinateuncertaintyInrange("20037509");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		assertNotNull(result.getComment());
		
		result = DwCGeoRefDQ.validationCoordinateuncertaintyInrange("0");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		assertNotNull(result.getComment());
		
		result = DwCGeoRefDQ.validationCoordinateuncertaintyInrange("20037510");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationCoordinateuncertaintyInrange("-1");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationCoordinateuncertaintyInrange(Integer.toString(Integer.MIN_VALUE));
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
	}
}
