/**
 * DwCGeoRefDQTestDefinitions.java
 */
package org.filteredpush.qc.georeference;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datakurator.ffdq.annotations.ActedUpon;
import org.datakurator.ffdq.annotations.Issue;
import org.datakurator.ffdq.annotations.Provides;
import org.datakurator.ffdq.api.DQResponse;
import org.datakurator.ffdq.api.result.AmendmentValue;
import org.datakurator.ffdq.api.result.ComplianceValue;
import org.datakurator.ffdq.api.result.IssueValue;
import org.datakurator.ffdq.model.ResultState;
import org.filteredpush.qc.georeference.util.GEOUtil;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.junit.Test;

/**
 * Minimal tests of the TDWG BDQ TG2 spatial tests for data quality. 
 * 
 * @author mole
 *
 * TODO: Split out any tests that invoke remote services into an integration test ...IT.java class.
 * TODO: Rename to follow expected maven surefire pattern of Test.java to verify in build
 * TODO: As implemented, move to approriate class.
 *
 */
public class DwCGeoRefDQTestTemporary {

	private static final Log logger = LogFactory.getLog(DwCGeoRefDQTestTemporary.class);


	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#amendmentCoordinatesFromVerbatim(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAmendmentCoordinatesFromVerbatim() {
		fail("Not yet implemented");
	}


	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#amendmentCoordinatesConverted(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAmendmentCoordinatesConverted() {
		
		 DQResponse<AmendmentValue> result = DwCGeoRefDQ.amendmentCoordinatesConverted("42.3836864972", "-71.1474180222", "100", "EPSG:4267", "0.000001");
		 assertEquals(ResultState.AMENDED.getLabel(), result.getResultState().getLabel());
		 assertFalse(GEOUtil.isEmpty(result.getComment()));
		 assertEquals(result.getValue().getObject().get("dwc:geodeticDatum"), "EPSG:4326");
		 assertEquals("42.3837831", result.getValue().getObject().get("dwc:decimalLatitude"));
		 assertEquals("-71.1469160", result.getValue().getObject().get("dwc:decimalLongitude"));
		 
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
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#amendmentGeodeticdatumStandardized(java.lang.String)}.
	 */
	@Test
	public void testAmendmentGeodeticdatumStandardized() {
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
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationCoordinatesNotzero(java.lang.String, java.lang.String)}.
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
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#amendmentGeographyStandardized(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAmendmentGeographyStandardized() {
		fail("Not yet implemented");
	}


	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationGeographyNotstandard(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidationGeographyNotstandard() {
		fail("Not yet implemented");
	}




}
