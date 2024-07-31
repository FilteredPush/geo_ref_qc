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
import org.geotools.referencing.CRS;
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
		
		 DQResponse<AmendmentValue> result = DwCGeoRefDQ.amendmentCoordinatesConverted("42.3836864", "-71.1474181", "100", "EPSG:4267", "0.000001");
		 assertEquals(ResultState.AMENDED.getLabel(), result.getResultState().getLabel());
		 assertFalse(GEOUtil.isEmpty(result.getComment()));
		 logger.debug(result.getComment());
		 assertEquals(result.getValue().getObject().get("dwc:geodeticDatum"), "EPSG:4326");
		 if (result.getValue().getObject().get("dwc:decimalLatitude").startsWith("42.3836864")) {  
			 if (result.getValue().getObject().get("dwc:decimalLongitude").startsWith("-71.1474181")) {  
				 fail("Coordinate not transformed");
			 } 
		 }
		 // gdaltransform -s_srs EPSG:4267 -t_srs EPSG:4326
		 //assertEquals("42.3836591678008", result.getValue().getObject().get("dwc:decimalLatitude"));
		 //assertEquals("-71.1468821356271", result.getValue().getObject().get("dwc:decimalLongitude"));
		 //https://epsg.io/transform#s_srs=4267&t_srs=4326&x=-71.1474181&y=42.3836864
		 // best transformer, 15851 CONUS + EEZ
		 // https://epsg.io/transform#s_srs=4267&t_srs=4326&ops=15851&x=-71.1474181&y=42.3836864
		 //assertEquals("42.383783", result.getValue().getObject().get("dwc:decimalLatitude"));
		 //assertEquals("-71.146916", result.getValue().getObject().get("dwc:decimalLongitude"));
		 // Returned from geotools
		 assertEquals("42.386887470038886", result.getValue().getObject().get("dwc:decimalLatitude"));
		 assertEquals("-71.14446630365468", result.getValue().getObject().get("dwc:decimalLongitude"));
		 
		 //result = DwCGeoRefDQ.amendmentCoordinatesConverted("-23.712", "139.923", "100", "EPSG:4283", "0.000001");
		 result = DwCGeoRefDQ.amendmentCoordinatesConverted("-23.712", "139.923", "100", "EPSG:4939", "0.000001");
		 logger.debug(result.getComment());
		 assertEquals(ResultState.AMENDED.getLabel(), result.getResultState().getLabel());
		 assertFalse(GEOUtil.isEmpty(result.getComment()));
		 assertEquals(result.getValue().getObject().get("dwc:geodeticDatum"), "EPSG:4326");
		 assertEquals("-23.7119864", result.getValue().getObject().get("dwc:decimalLatitude"));
		 assertEquals("139.9230077", result.getValue().getObject().get("dwc:decimalLongitude"));

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
