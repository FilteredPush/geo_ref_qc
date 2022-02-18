/**
 * DwCGeoRefDQTestDefinitions.java
 */
package org.filteredpush.qc.georeference;

import static org.junit.Assert.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datakurator.ffdq.api.DQResponse;
import org.datakurator.ffdq.api.result.ComplianceValue;
import org.datakurator.ffdq.model.ResultState;
import org.junit.Test;

/**
 * Minimal tests of the TDWG BDQ TG2 spatial tests for data quality. 
 * 
 * @author mole
 *
 */
public class DwCGeoRefDQTestDefinitions {

	private static final Log logger = LogFactory.getLog(DwCGeoRefDQTestDefinitions.class);

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationCountrycodeNotstandard(java.lang.String)}.
	 */
	@Test
	public void testValidationCountrycodeNotstandard() {
		
        // Specification
        // EXTERNAL_PREREQUISITES_NOT_MET if the ISO 3166 service was 
        // not available; INTERNAL_PREREQUISITES_NOT_MET if the dwc:countryCode 
        // was EMPTY; COMPLIANT if dwc:countryCode is a valid ISO (ISO 
        // 3166-1-alpha-2 country codes) value; otherwise NOT_COMPLIANT 
        //
		
		DQResponse<ComplianceValue> result = DwCGeoRefDQ.validationCountrycodeNotstandard(null);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQ.validationCountrycodeNotstandard("a");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationCountrycodeNotstandard("UG");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationCountryNotstandard(java.lang.String)}.
	 */
	@Test
	public void testValidationCountryNotstandard() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationMindepthGreaterthanMaxdepth(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidationMindepthGreaterthanMaxdepth() {
		
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:minimumDepthInMeters 
        // or dwc:maximumDepthInMeters is EMPTY, or the values are 
        // not zero or a positive number; COMPLIANT if the value of 
        // dwc:minimumDepthInMeters is less than or equal to the value 
        // of dwc:maximumDepthInMeters; otherwise NOT_COMPLIANT 
		
		DQResponse<ComplianceValue> result = DwCGeoRefDQ.validationMindepthGreaterthanMaxdepth(null,null);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQ.validationMindepthGreaterthanMaxdepth("a","10");
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQ.validationMindepthGreaterthanMaxdepth("-1","10");
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());		
		
		result = DwCGeoRefDQ.validationMindepthGreaterthanMaxdepth("10","100");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationMindepthGreaterthanMaxdepth("100","10");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationDecimallongitudeOutofrange(java.lang.String)}.
	 */
	@Test
	public void testValidationDecimallongitudeOutofrange() {
		
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:decimalLongitude is 
        // EMPTY or the value is not a number; COMPLIANT if the value 
        // of dwc:decimalLongitude is between -180 and 180 degrees, 
        // inclusive; otherwise NOT_COMPLIANT 
		
		DQResponse<ComplianceValue> result = DwCGeoRefDQ.validationDecimallongitudeOutofrange(null);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQ.validationDecimallongitudeOutofrange("a");
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQ.validationDecimallongitudeOutofrange("10");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationDecimallongitudeOutofrange("190");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#amendmentCoordinatesFromVerbatim(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAmendmentCoordinatesFromVerbatim() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationMinelevationOutofrange(java.lang.String)}.
	 */
	@Test
	public void testValidationMinelevationOutofrange() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationLocationEmpty(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidationLocationEmpty() {
		
        // Specification
        // COMPLIANT if at least one term needed to determine the location 
        // of the entity exists and is not EMPTY; otherwise NOT_COMPLIANT 
        //
		
		DQResponse<ComplianceValue> result = DwCGeoRefDQ.validationLocationEmpty(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationLocationEmpty("foo", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationLocationEmpty(null, "foo", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationLocationEmpty(null, null, "foo", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationLocationEmpty(null, null, null, "foo", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationLocationEmpty(null, null, null, null, "foo", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationLocationEmpty(null, null, null, null, null, "foo", null, null, null, null, null, null, null, null, null, null, null, null, null, null);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationLocationEmpty(null, null, null, null, null, null, "foo", null, null, null, null, null, null, null, null, null, null, null, null, null);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationLocationEmpty(null, null, null, null, null, null, null, "foo", null, null, null, null, null, null, null, null, null, null, null, null);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationLocationEmpty(null, null, null, null, null, null, null, null, "foo", null, null, null, null, null, null, null, null, null, null, null);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationLocationEmpty(null, null, null, null, null, null, null, null, null, "foo", null, null, null, null, null, null, null, null, null, null);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationLocationEmpty(null, null, null, null, null, null, null, null, null, null, "foo", null, null, null, null, null, null, null, null, null);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationLocationEmpty(null, null, null, null, null, null, null, null, null, null, null, "foo", null, null, null, null, null, null, null, null);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationLocationEmpty(null, null, null, null, null, null, null, null, null, null, null, null, "foo", null, null, null, null, null, null, null);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationLocationEmpty(null, null, null, null, null, null, null, null, null, null, null, null, null, "foo", null, null, null, null, null, null);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationLocationEmpty(null, null, null, null, null, null, null, null, null, null, null, null, null, null, "foo", null, null, null, null, null);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationLocationEmpty(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "foo", null, null, null, null);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationLocationEmpty(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "foo", null, null, null);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationLocationEmpty(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "foo", null, null);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationLocationEmpty(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "foo", null);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationLocationEmpty(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "foo");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationCountryEmpty(java.lang.String)}.
	 */
	@Test
	public void testValidationCountryEmpty() {
		
        // Specification
        // COMPLIANT if dwc:country is not EMPTY; otherwise NOT_COMPLIANT 
        //
		
		DQResponse<ComplianceValue> result = DwCGeoRefDQ.validationCountryEmpty(null);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationCountryEmpty("foo");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#amendmentCoordinatesConverted(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAmendmentCoordinatesConverted() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#amendmentCountrycodeStandardized(java.lang.String)}.
	 */
	@Test
	public void testAmendmentCountrycodeStandardized() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationCoordinatesCountrycodeInconsistent(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidationCoordinatesCountrycodeInconsistent() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationCoordinatesTerrestrialmarine(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidationCoordinatesTerrestrialmarine() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#amendmentCoordinatesTransposed(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAmendmentCoordinatesTransposed() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#amendmentMindepthMaxdepthFromVerbatim(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAmendmentMindepthMaxdepthFromVerbatim() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationCoordinatesStateprovinceInconsistent(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidationCoordinatesStateprovinceInconsistent() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationGeodeticdatumNotstandard(java.lang.String)}.
	 */
	@Test
	public void testValidationGeodeticdatumNotstandard() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#amendmentGeodeticdatumStandardized(java.lang.String)}.
	 */
	@Test
	public void testAmendmentGeodeticdatumStandardized() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationCountryCountrycodeInconsistent(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidationCountryCountrycodeInconsistent() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#amendmentMinelevationMaxelevationFromVerbatim(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAmendmentMinelevationMaxelevationFromVerbatim() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#amendmentCountrycodeFromCoordinates(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAmendmentCountrycodeFromCoordinates() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationGeodeticdatumEmpty(java.lang.String)}.
	 */
	@Test
	public void testValidationGeodeticdatumEmpty() {
		
        // Specification
        // COMPLIANT if dwc:geodeticDatum is not EMPTY; otherwise NOT_COMPLIANT 
        //
		
		DQResponse<ComplianceValue> result = DwCGeoRefDQ.validationGeodeticdatumEmpty(null);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationGeodeticdatumEmpty("foo");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
	}

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationDecimallatitudeOutofrange(java.lang.String)}.
	 */
	@Test
	public void testValidationDecimallatitudeOutofrange() {
		
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:decimalLatitude is 
        // EMPTY or the value is not a number; COMPLIANT if the value 
        // of dwc:decimalLatitude is between -90 and 90 degrees, inclusive; 
        // otherwise NOT_COMPLIANT 
		
		DQResponse<ComplianceValue> result = DwCGeoRefDQ.validationDecimallatitudeOutofrange(null);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQ.validationDecimallatitudeOutofrange("a");
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQ.validationDecimallatitudeOutofrange("10");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationDecimallatitudeOutofrange("90.01");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
	}

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationCoordinatesZero(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidationCoordinatesZero() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationGeographyAmbiguous(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidationGeographyAmbiguous() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationDecimallongitudeEmpty(java.lang.String)}.
	 */
	@Test
	public void testValidationDecimallongitudeEmpty() {
		
        // Specification
        // COMPLIANT if dwc:decimalLongitude is not EMPTY; otherwise 
        // NOT_COMPLIANT 
		
		DQResponse<ComplianceValue> result = DwCGeoRefDQ.validationDecimallongitudeEmpty(null);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationDecimallongitudeEmpty("foo");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationCountrycodeEmpty(java.lang.String)}.
	 */
	@Test
	public void testValidationCountrycodeEmpty() {
		
        // Specification
        // COMPLIANT if dwc:countryCode is not EMPTY; otherwise NOT_COMPLIANT 
        //
	
		DQResponse<ComplianceValue> result = DwCGeoRefDQ.validationCountrycodeEmpty(null);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationCountrycodeEmpty("foo");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#amendmentGeodeticdatumAssumeddefault(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAmendmentGeodeticdatumAssumeddefault() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationMindepthOutofrange(java.lang.String)}.
	 */
	@Test
	public void testValidationMindepthOutofrange() {
		
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:minimumDepthInMeters 
        // is EMPTY, or the value is not zero or a positive number; 
        // COMPLIANT if the value of dwc:minimumDepthInMeters is within 
        //the Parameter range; otherwise NOT_COMPLIANT 
		
		DQResponse<ComplianceValue> result = DwCGeoRefDQ.validationMaxdepthOutofrange(null);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQ.validationMaxdepthOutofrange("a");
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQ.validationMaxdepthOutofrange("10");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationMaxdepthOutofrange("-1");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationMaxdepthOutofrange("11001");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationMinelevationGreaterthanMaxelevation(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidationMinelevationGreaterthanMaxelevation() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationCoordinateuncertaintyOutofrange(java.lang.String)}.
	 */
	@Test
	public void testValidationCoordinateuncertaintyOutofrange() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationMaxelevationOutofrange(java.lang.String)}.
	 */
	@Test
	public void testValidationMaxelevationOutofrange() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#amendmentGeographyStandardized(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAmendmentGeographyStandardized() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationDecimallatitudeEmpty(java.lang.String)}.
	 */
	@Test
	public void testValidationDecimallatitudeEmpty() {
		
        // Specification
        // COMPLIANT if dwc:decimalLatitude is not EMPTY; otherwise 
        // NOT_COMPLIANT 
		
		DQResponse<ComplianceValue> result = DwCGeoRefDQ.validationDecimallatitudeEmpty(null);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationDecimallatitudeEmpty("foo");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationGeographyNotstandard(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidationGeographyNotstandard() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationMaxdepthOutofrange(java.lang.String)}.
	 */
	@Test
	public void testValidationMaxdepthOutofrange() {
		
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:maximumDepthInMeters 
        // is EMPTY or is not a number; COMPLIANT if the value of dwc:maximumDepthInMeters 
        // is within the Parameter range of bdq:minimumValidDepthInMeters 
        // to bdq:maximumValidDepthInMeters inclusive; otherwise NOT_COMPLIANT 
        //
		
		DQResponse<ComplianceValue> result = DwCGeoRefDQ.validationMaxdepthOutofrange(null);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQ.validationMaxdepthOutofrange("a");
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQ.validationMaxdepthOutofrange("10");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationMaxdepthOutofrange("-1");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationMaxdepthOutofrange("11001");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
	}

}
