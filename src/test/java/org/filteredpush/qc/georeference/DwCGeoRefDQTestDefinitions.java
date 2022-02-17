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
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationDecimallongitudeOutofrange(java.lang.String)}.
	 */
	@Test
	public void testValidationDecimallongitudeOutofrange() {
		fail("Not yet implemented");
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
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationCountryEmpty(java.lang.String)}.
	 */
	@Test
	public void testValidationCountryEmpty() {
		fail("Not yet implemented");
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
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationDecimallatitudeOutofrange(java.lang.String)}.
	 */
	@Test
	public void testValidationDecimallatitudeOutofrange() {
		fail("Not yet implemented");
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
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationCountrycodeEmpty(java.lang.String)}.
	 */
	@Test
	public void testValidationCountrycodeEmpty() {
		fail("Not yet implemented");
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
		fail("Not yet implemented");
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
		fail("Not yet implemented");
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
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link java.lang.Object#Object()}.
	 */
	@Test
	public void testObject() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link java.lang.Object#getClass()}.
	 */
	@Test
	public void testGetClass() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link java.lang.Object#hashCode()}.
	 */
	@Test
	public void testHashCode() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link java.lang.Object#equals(java.lang.Object)}.
	 */
	@Test
	public void testEquals() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link java.lang.Object#clone()}.
	 */
	@Test
	public void testClone() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link java.lang.Object#toString()}.
	 */
	@Test
	public void testToString() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link java.lang.Object#notify()}.
	 */
	@Test
	public void testNotify() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link java.lang.Object#notifyAll()}.
	 */
	@Test
	public void testNotifyAll() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link java.lang.Object#wait()}.
	 */
	@Test
	public void testWait() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link java.lang.Object#wait(long)}.
	 */
	@Test
	public void testWaitLong() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link java.lang.Object#wait(long, int)}.
	 */
	@Test
	public void testWaitLongInt() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link java.lang.Object#finalize()}.
	 */
	@Test
	public void testFinalize() {
		fail("Not yet implemented");
	}

}
