/**
 * DwCGeoRefDQDefinitionsTest.java
 */
package org.filteredpush.qc.georeference;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datakurator.ffdq.api.DQResponse;
import org.datakurator.ffdq.api.result.AmendmentValue;
import org.datakurator.ffdq.api.result.ComplianceValue;
import org.datakurator.ffdq.model.ResultState;
import org.filteredpush.qc.georeference.util.CountryLookup;
import org.filteredpush.qc.georeference.util.GEOUtil;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.junit.Test;

/**
 * @author mole
 *
 */
public class DwCGeoRefDQDefinitionsTest {

	private static final Log logger = LogFactory.getLog(DwCGeoRefDQDefinitionsTest.class);
	
	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationCoordinatesCountrycodeConsistent(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidationCoordinatesCountrycodeConsistent() {
		
		String countryCode = "";
		String decimalLatitude = "";
		String decimalLongitude = "";
		DQResponse<ComplianceValue> result = DwCGeoRefDQ.validationCoordinatesCountrycodeConsistent(decimalLatitude, decimalLongitude, countryCode, null);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		assertNull(result.getValue());
		
		countryCode = "US";
		decimalLatitude = "1";
		decimalLongitude = "1";
		result = DwCGeoRefDQ.validationCoordinatesCountrycodeConsistent(decimalLatitude, decimalLongitude, countryCode, null);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		countryCode = "US";
		decimalLatitude = "30";
		decimalLongitude = "-90";
		result = DwCGeoRefDQ.validationCoordinatesCountrycodeConsistent(decimalLatitude, decimalLongitude, countryCode, null);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		countryCode = "CL";
		decimalLatitude = "-24.40";
		decimalLongitude = "-72.71";
		result = DwCGeoRefDQ.validationCoordinatesCountrycodeConsistent(decimalLatitude, decimalLongitude, countryCode, null);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		countryCode = "CL";
		decimalLatitude = "-28.50";
		decimalLongitude = "-79.53";
		result = DwCGeoRefDQ.validationCoordinatesCountrycodeConsistent(decimalLatitude, decimalLongitude, countryCode, null);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		List<String> codes = CountryLookup.getCountryCodes2();
		Iterator<String> i = codes.iterator();
		boolean done = false;  // too slow....
		while (i.hasNext() && ! done) { 
			countryCode = i.next();
			String country3 = CountryLookup.lookupCode3FromCodeName(countryCode);
			logger.debug(country3);
			if (country3!=null && CountryLookup.countryExistsHasCode(CountryLookup.lookupCountryFromCode(country3))) { 
				decimalLatitude = "64.896";
				decimalLongitude = "-0.555";
				result = DwCGeoRefDQ.validationCoordinatesCountrycodeConsistent(decimalLatitude, decimalLongitude, countryCode, null);
				logger.debug(result.getComment());
				assertFalse(GEOUtil.isEmpty(result.getComment()));
				assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
				assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
			}
			done = true;
		}

		
	}
	
	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationCountryCountrycodeConsistent(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidationCountryCountrycodeConsistent() {
		
		String country="";
		String countryCode = "";
		DQResponse<ComplianceValue> result = DwCGeoRefDQ.validationCountryCountrycodeConsistent(country, countryCode);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		assertNull(result.getValue());
		
		country=null;
		countryCode = "ZZ";
		result = DwCGeoRefDQ.validationCountryCountrycodeConsistent(country, countryCode);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		assertNull(result.getValue());
		
		country=null;
		countryCode = "UG";
		result = DwCGeoRefDQ.validationCountryCountrycodeConsistent(country, countryCode);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		assertNull(result.getValue());
		
		country="Uganda";
		countryCode = "UG";
		result = DwCGeoRefDQ.validationCountryCountrycodeConsistent(country, countryCode);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
		// Other cases where lookup on service is used in integration test.
		
	}

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
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQ.validationCoordinatesNotzero("A","B");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQ.validationCoordinatesNotzero("1.4","3.6");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
		result = DwCGeoRefDQ.validationCoordinatesNotzero("0","0");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		result = DwCGeoRefDQ.validationCoordinatesNotzero("0","3.6");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
		result = DwCGeoRefDQ.validationCoordinatesNotzero("1.4","0");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
		result = DwCGeoRefDQ.validationCoordinatesNotzero("1.4","B");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		
		result = DwCGeoRefDQ.validationCoordinatesNotzero("A","-26.42552");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
	
		result = DwCGeoRefDQ.validationCoordinatesNotzero("A","B");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
	
		result = DwCGeoRefDQ.validationCoordinatesNotzero("1.4","0");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
		result = DwCGeoRefDQ.validationCoordinatesNotzero("0","-26.42552");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
	
		// test does not evaluate sanity of numbers
		result = DwCGeoRefDQ.validationCoordinatesNotzero("1000.00002","-5000.00133");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
		result = DwCGeoRefDQ.validationCoordinatesNotzero(Integer.toString(Integer.MAX_VALUE), Integer.toString(Integer.MAX_VALUE));
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
		// given note: "A record with 0.0 is interpreted as the string "0""
		
		// treat -0 as the same as 0
		result = DwCGeoRefDQ.validationCoordinatesNotzero("-0","-0");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		result = DwCGeoRefDQ.validationCoordinatesNotzero("0.0","0.0");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
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
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQ.validationCountrycodeStandard("a");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationCountrycodeStandard("UG");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
        // From notes: "This test will fail if there is leading or trailing whitespace or there are leading or trailing non-printing characters."
		result = DwCGeoRefDQ.validationCountrycodeStandard(" UG ");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationCountrycodeStandard("ug");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
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
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQDefaults.validationMaxdepthInrange("a");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQDefaults.validationMaxdepthInrange("10");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQDefaults.validationMaxdepthInrange("0");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		// negative depth values not allowed
		result = DwCGeoRefDQDefaults.validationMaxdepthInrange("-1");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQDefaults.validationMaxdepthInrange("11001");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		// Testing parameters 
		
		result = DwCGeoRefDQ.validationMaxdepthInrange("11001", 0d, 12000d);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationMaxdepthInrange("10", 100d, null);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		// specifying a negative minimum will still result in internal prerequisites not met`
		result = DwCGeoRefDQ.validationMaxdepthInrange("-10", -100d, 200d);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQDefaults.validationMaxdepthInrange("11001");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		if (Integer.MAX_VALUE>11000) {
			result = DwCGeoRefDQDefaults.validationMaxdepthInrange(Integer.toString(Integer.MAX_VALUE));
			logger.debug(result.getComment());
			assertFalse(GEOUtil.isEmpty(result.getComment()));
			assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
			assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		}
		if (Integer.MIN_VALUE<0) {
			result = DwCGeoRefDQDefaults.validationMaxdepthInrange(Integer.toString(Integer.MIN_VALUE));
			logger.debug(result.getComment());
			assertFalse(GEOUtil.isEmpty(result.getComment()));
			assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
			assertNull(result.getValue());
		}
		
		for (int i=0; i<11001; i++) { 
			result = DwCGeoRefDQDefaults.validationMaxdepthInrange(Integer.toString(i));
			assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
			assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		}
		
		// Testing parameters 
		
		result = DwCGeoRefDQ.validationMaxdepthInrange("11001", 0d, 12000d);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationMaxdepthInrange("10", 100d, null);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		// specifying a negative minimum will still result in internal prerequisites not met`
		result = DwCGeoRefDQ.validationMaxdepthInrange("-10", -100d, 200d);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
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
		assertFalse(GEOUtil.isEmpty(result.getComment()));
	    assertEquals(ResultState.NOT_AMENDED.getLabel(), result.getResultState().getLabel());
	    
	    coordinateUncertantyInMeters = "";
		geodeticDatum = "";
	    result = DwCGeoRefDQ.amendmentGeodeticdatumAssumeddefault(coordinateUncertantyInMeters, geodeticDatum, null);
	    logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
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
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		
		result = DwCGeoRefDQDefaults.validationMindepthInrange("a");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQDefaults.validationMindepthInrange("10");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQDefaults.validationMindepthInrange("10.5");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		// negative depth values not allowed
		result = DwCGeoRefDQDefaults.validationMindepthInrange("-1");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		// test -0d
		result = DwCGeoRefDQDefaults.validationMindepthInrange("-0");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		// negative depth values not allowed, min integer is negative.
		result = DwCGeoRefDQDefaults.validationMindepthInrange(Integer.toString(Integer.MIN_VALUE));
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		// similarly negative infinity
		result = DwCGeoRefDQDefaults.validationMindepthInrange(Double.toString(Double.NEGATIVE_INFINITY));
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQDefaults.validationMindepthInrange("11001");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQDefaults.validationMindepthInrange("10",1d,100d);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		// negative depth values not allowed, even if specified range goes into negative
		result = DwCGeoRefDQDefaults.validationMindepthInrange("-1",-10d,100d);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQDefaults.validationMindepthInrange("1",2d,100d);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationMindepthInrange("110",10d,100d);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
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
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQ.validationMinelevationLessthanMaxelevation("a","10");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQ.validationMinelevationLessthanMaxelevation("-1","10");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationMinelevationLessthanMaxelevation("10","100");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationMinelevationLessthanMaxelevation("100","10");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationMinelevationLessthanMaxelevation("a","10");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQ.validationMinelevationLessthanMaxelevation("10","XX");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQ.validationMinelevationLessthanMaxelevation(null,null);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQ.validationMinelevationLessthanMaxelevation("-1","10");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationMinelevationLessthanMaxelevation("10","100");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationMinelevationLessthanMaxelevation("100","10");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		String minElevation = Integer.toString(Integer.MIN_VALUE);
		String maxElevation = Integer.toString(Integer.MAX_VALUE);
		result = DwCGeoRefDQ.validationMinelevationLessthanMaxelevation(minElevation,maxElevation);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		minElevation = Integer.toString(Integer.MAX_VALUE);
		maxElevation = Integer.toString(Integer.MIN_VALUE);
		result = DwCGeoRefDQ.validationMinelevationLessthanMaxelevation(minElevation,maxElevation);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		minElevation = "10m";
		maxElevation = "15m";
		result = DwCGeoRefDQ.validationMinelevationLessthanMaxelevation(minElevation,maxElevation);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
	
		minElevation = " 10 ";
		maxElevation = " 15 ";
		result = DwCGeoRefDQ.validationMinelevationLessthanMaxelevation(minElevation,maxElevation);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		
		minElevation = " 15 ";
		maxElevation = " 10 ";
		result = DwCGeoRefDQ.validationMinelevationLessthanMaxelevation(minElevation,maxElevation);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		
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
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		
		result = DwCGeoRefDQ.validationCoordinateuncertaintyInrange("a");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQ.validationCoordinateuncertaintyInrange("100");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		
		result = DwCGeoRefDQ.validationCoordinateuncertaintyInrange("1");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		
		result = DwCGeoRefDQ.validationCoordinateuncertaintyInrange("20037509");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		
		result = DwCGeoRefDQ.validationCoordinateuncertaintyInrange("0");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		
		result = DwCGeoRefDQ.validationCoordinateuncertaintyInrange("20037510");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationCoordinateuncertaintyInrange("-1");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationCoordinateuncertaintyInrange(Integer.toString(Integer.MIN_VALUE));
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
	}
	
	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationDecimallongitudeNotempty(java.lang.String)}.
	 */
	@Test
	public void testValidationDecimallongitudeEmpty() {
		
        // Specification
        // COMPLIANT if dwc:decimalLongitude is not EMPTY; otherwise 
        // NOT_COMPLIANT 
		
		DQResponse<ComplianceValue> result = DwCGeoRefDQ.validationDecimallongitudeNotempty(null);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationDecimallongitudeNotempty("foo");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
	}
	

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationDecimallatitudeNotEmpty(java.lang.String)}.
	 */
	@Test
	public void testValidationDecimallatitudeEmpty() {
		
        // Specification
        // COMPLIANT if dwc:decimalLatitude is not EMPTY; otherwise 
        // NOT_COMPLIANT 
		
		DQResponse<ComplianceValue> result = DwCGeoRefDQ.validationDecimallatitudeNotEmpty(null);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationDecimallatitudeNotEmpty("foo");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
	}
	
	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationMaxelevationInrange(java.lang.String)}.
	 */
	@Test
	public void testValidationMaxelevationOutofrange() {
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:maximumElevationInMeters 
        // is EMPTY or the value cannot be interpreted as a number; 
        // COMPLIANT if the value of dwc:maximumElevationInMeters is 
        // within the range of bdq:minimumValidElevationInMeters to 
        // bdq:maximumValidElevationInMeters inclusive; otherwise NOT_COMPLIANT 

        // Parameters. This test is defined as parameterized.
        // Default values: bdq:minimumValidElevationInMeters="-430"; bdq:maximumValidElevationInMeters="8850"
		
		DQResponse<ComplianceValue> result = DwCGeoRefDQDefaults.validationMaxelevationInrange(null);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQDefaults.validationMaxelevationInrange("a");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQDefaults.validationMaxelevationInrange("10m");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQDefaults.validationMaxelevationInrange("8000");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQDefaults.validationMaxelevationInrange("0");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQDefaults.validationMaxelevationInrange("-400");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		System.out.println(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
		result = DwCGeoRefDQDefaults.validationMaxelevationInrange("8850");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQDefaults.validationMaxelevationInrange("23.456");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());		
		
		result = DwCGeoRefDQDefaults.validationMaxelevationInrange("-430");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQDefaults.validationMaxelevationInrange("9000");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQDefaults.validationMaxelevationInrange("8851");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQDefaults.validationMaxelevationInrange("-431");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQDefaults.validationMaxelevationInrange("-500");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationMaxelevationInrange("-600",-1000d,100d);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		System.out.println(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
		result = DwCGeoRefDQ.validationMaxelevationInrange("100",10d,110d);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		System.out.println(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
		result = DwCGeoRefDQ.validationMaxelevationInrange("100",10d,20d);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		System.out.println(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		result = DwCGeoRefDQDefaults.validationMaxelevationInrange(Integer.toString(Integer.MAX_VALUE));
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQDefaults.validationMaxelevationInrange(Integer.toString(Integer.MIN_VALUE));
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
	}
	
	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationDecimallongitudeInrange(java.lang.String)}.
	 */
	@Test
	public void testValidationDecimallongitudeOutofrange() {
		
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:decimalLongitude is 
        // EMPTY or the value is not a number; COMPLIANT if the value 
        // of dwc:decimalLongitude is between -180 and 180 degrees, 
        // inclusive; otherwise NOT_COMPLIANT 
		
		DQResponse<ComplianceValue> result = DwCGeoRefDQ.validationDecimallongitudeInrange(null);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQ.validationDecimallongitudeInrange("a");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQ.validationDecimallongitudeInrange("10");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationDecimallongitudeInrange("190");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
	}
	
	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationGeodeticdatumNotempty(java.lang.String)}.
	 */
	@Test
	public void testValidationGeodeticdatumEmpty() {
		
        // Specification
        // COMPLIANT if dwc:geodeticDatum is not EMPTY; otherwise NOT_COMPLIANT 
        //
		
		DQResponse<ComplianceValue> result = DwCGeoRefDQ.validationGeodeticdatumNotempty(null);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationGeodeticdatumNotempty(" ");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationGeodeticdatumNotempty("foo");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationGeodeticdatumNotempty("EPSG:4326");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
	}
	
	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationMinelevationInrange(java.lang.String)}.
	 */
	@Test
	public void testValidationMinelevationInrange() {
		
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:minimumElevationInMeters 
        // is EMPTY or the value is not a number; COMPLIANT if the 
        // value of dwc:minimumElevationInMeters is within the Parameter 
        //range; otherwise NOT_COMPLIANT 

        // Parameters. This test is defined as parameterized.
        // Default values: bdq:minimumValidElevationInMeters="-430"; bdq:maximumValidElevationInMeters="8850"
		
		DQResponse<ComplianceValue> result = DwCGeoRefDQDefaults.validationMinelevationInrange(null);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQDefaults.validationMinelevationInrange("a");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQDefaults.validationMinelevationInrange("10m");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQDefaults.validationMinelevationInrange("8000");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQDefaults.validationMinelevationInrange("-400");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		System.out.println(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
		result = DwCGeoRefDQDefaults.validationMinelevationInrange("9000");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQDefaults.validationMinelevationInrange("-500");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationMinelevationInrange("100",10d,50d);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationMinelevationInrange("-500",-600d, 100d);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationMinelevationInrange("500",-600d, 100d);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
        // Default values: bdq:minimumValidElevationInMeters="-430"; bdq:maximumValidElevationInMeters="8850"
		result = DwCGeoRefDQDefaults.validationMinelevationInrange("8850");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		
		result = DwCGeoRefDQDefaults.validationMinelevationInrange("-430");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		System.out.println(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
		result = DwCGeoRefDQDefaults.validationMinelevationInrange("8851");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQDefaults.validationMinelevationInrange("-431");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		System.out.println(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		
	}
	
	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationGeodeticdatumStandard(java.lang.String)}.
	 */
	@Test
	public void testValidationGeodeticdatumStandard() {
		
		// NOT Tested, implementation uses library not lookup.
        // EXTERNAL_PREREQUISITES_NOT_MET if the bdq:sourceAuthority 
        // is not available, 
		
		// INTERNAL_PREREQUISITES_NOT_MET if dwc:geodeticDatum 
        // is EMPTY; 
		// COMPLIANT if the value of dwc:geodeticDatum is 
        // a valid EPSG CRS Code (with or without the "epsg" namespace 
        // prepended), or an unambiguous alphanumeric CRS or datum 
        // code; otherwise NOT_COMPLIANT 
		
		String geodeticDatum = null;
		DQResponse<ComplianceValue> result = DwCGeoRefDQ.validationGeodeticdatumStandard(geodeticDatum);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		
		geodeticDatum = "WGS84";
		result = DwCGeoRefDQ.validationGeodeticdatumStandard(geodeticDatum);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		geodeticDatum = "WGS 84";
		result = DwCGeoRefDQ.validationGeodeticdatumStandard(geodeticDatum);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		geodeticDatum = "WGS 1984";
		result = DwCGeoRefDQ.validationGeodeticdatumStandard(geodeticDatum);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		geodeticDatum = "EPSG:4326";
		result = DwCGeoRefDQ.validationGeodeticdatumStandard(geodeticDatum);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		try { 
			Set<String> codes = ReferencingFactoryFinder.getCRSAuthorityFactory("EPSG", null).getAuthorityCodes(org.opengis.referencing.crs.ProjectedCRS.class);
			logger.debug(codes.size());
			Iterator<String> i = codes.iterator();
			while (i.hasNext()) { 
				String code = i.next();
				logger.debug(code);
				if (!code.equals("5820")) {
					result = DwCGeoRefDQ.validationGeodeticdatumStandard(code);
					logger.debug(result.getComment());
					assertFalse(GEOUtil.isEmpty(result.getComment()));
					assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
					assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
				} 
			}
		} catch (Exception e) { 
			fail(e.getMessage());
		}
	}
	
	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationDecimallatitudeInrange(java.lang.String)}.
	 */
	@Test
	public void testValidationDecimallatitudeOutofrange() {
		
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:decimalLatitude is 
        // EMPTY or the value is not a number; COMPLIANT if the value 
        // of dwc:decimalLatitude is between -90 and 90 degrees, inclusive; 
        // otherwise NOT_COMPLIANT 
		
		DQResponse<ComplianceValue> result = DwCGeoRefDQ.validationDecimallatitudeInrange(null);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQ.validationDecimallatitudeInrange("a");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQ.validationDecimallatitudeInrange("10");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationDecimallatitudeInrange("90.01");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
	}
	

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationCountrycodeNotempty(java.lang.String)}.
	 */
	@Test
	public void testValidationCountrycodeEmpty() {
		
        // Specification
        // COMPLIANT if dwc:countryCode is not EMPTY; otherwise NOT_COMPLIANT 
        //
	
		DQResponse<ComplianceValue> result = DwCGeoRefDQ.validationCountrycodeNotempty(null);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationCountrycodeNotempty("foo");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
	}
	
	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationMindepthLessthanMaxdepth(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidationMindepthGreaterthanMaxdepth() {
		
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:minimumDepthInMeters 
        // or dwc:maximumDepthInMeters is EMPTY, or the values are 
        // not zero or a positive number; COMPLIANT if the value of 
        // dwc:minimumDepthInMeters is less than or equal to the value 
        // of dwc:maximumDepthInMeters; otherwise NOT_COMPLIANT 
		
		DQResponse<ComplianceValue> result = DwCGeoRefDQ.validationMindepthLessthanMaxdepth(null,null);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQ.validationMindepthLessthanMaxdepth("a","10");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQ.validationMindepthLessthanMaxdepth("-1","10");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());		
		
		result = DwCGeoRefDQ.validationMindepthLessthanMaxdepth("10","100");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationMindepthLessthanMaxdepth("100","10");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
	}
	
	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationLocationNotempty(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidationLocationNotempty() {
		
        // Specification
        // COMPLIANT if at least one term needed to determine the location 
        // of the entity exists and is not EMPTY; otherwise NOT_COMPLIANT 
        //
		
		DQResponse<ComplianceValue> result = DwCGeoRefDQ.validationLocationNotempty(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationLocationNotempty("foo", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationLocationNotempty(null, "foo", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationLocationNotempty(null, null, "foo", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationLocationNotempty(null, null, null, "foo", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationLocationNotempty(null, null, null, null, "foo", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationLocationNotempty(null, null, null, null, null, "foo", null, null, null, null, null, null, null, null, null, null, null, null, null, null);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationLocationNotempty(null, null, null, null, null, null, "foo", null, null, null, null, null, null, null, null, null, null, null, null, null);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationLocationNotempty(null, null, null, null, null, null, null, "foo", null, null, null, null, null, null, null, null, null, null, null, null);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationLocationNotempty(null, null, null, null, null, null, null, null, "foo", null, null, null, null, null, null, null, null, null, null, null);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationLocationNotempty(null, null, null, null, null, null, null, null, null, "foo", null, null, null, null, null, null, null, null, null, null);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationLocationNotempty(null, null, null, null, null, null, null, null, null, null, "foo", null, null, null, null, null, null, null, null, null);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationLocationNotempty(null, null, null, null, null, null, null, null, null, null, null, "foo", null, null, null, null, null, null, null, null);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationLocationNotempty(null, null, null, null, null, null, null, null, null, null, null, null, "foo", null, null, null, null, null, null, null);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationLocationNotempty(null, null, null, null, null, null, null, null, null, null, null, null, null, "foo", null, null, null, null, null, null);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationLocationNotempty(null, null, null, null, null, null, null, null, null, null, null, null, null, null, "foo", null, null, null, null, null);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationLocationNotempty(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "foo", null, null, null, null);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationLocationNotempty(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "foo", null, null, null);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationLocationNotempty(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "foo", null, null);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationLocationNotempty(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "foo", null);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationLocationNotempty(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "foo");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationLocationNotempty("A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "A", "foo");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationCountryNotEmpty(java.lang.String)}.
	 */
	@Test
	public void testValidationCountryEmpty() {
		
        // Specification
        // COMPLIANT if dwc:country is not EMPTY; otherwise NOT_COMPLIANT 
        //
		
		DQResponse<ComplianceValue> result = DwCGeoRefDQ.validationCountryNotEmpty(null);
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCGeoRefDQ.validationCountryNotEmpty("foo");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
	}
	
	/**
	 * Test method for {@link org.filteredpush.qc.georeference.DwCGeoRefDQ#validationCountryNotstandard(java.lang.String)}.
	 */
	@Test
	public void testValidationCountryFound() {
		
		DQResponse<ComplianceValue> result = DwCGeoRefDQ.validationCountryFound(null,"datahub.io");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCGeoRefDQ.validationCountryFound("Uganda","datahub.io");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
		result = DwCGeoRefDQ.validationCountryFound("dwc:country","datahub.io");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		result = DwCGeoRefDQ.validationCountryFound("Uganda","NaturalEarth");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
		result = DwCGeoRefDQ.validationCountryFound("dwc:country","NaturalEarth");
		logger.debug(result.getComment());
		assertFalse(GEOUtil.isEmpty(result.getComment()));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
	}
	
}
