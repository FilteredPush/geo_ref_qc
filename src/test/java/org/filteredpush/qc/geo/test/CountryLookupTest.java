/**
 * 
 */
package org.filteredpush.qc.geo.test;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.filteredpush.qc.georeference.util.CountryLookup;
import org.junit.Test;

/**
 * @author mole
 *
 */
public class CountryLookupTest {

    private static final Log logger = LogFactory.getLog(CountryLookupTest.class);
    
	@Test
	public void test() {
		assertEquals("Afghanistan", CountryLookup.lookupCountryFromCode("AF"));
		assertEquals("Falkland Islands", CountryLookup.lookupCountryFromCode("FK"));
		assertEquals("Falkland Islands", CountryLookup.lookupCountryFromCode("FLK"));
		assertEquals("Falkland Islands", CountryLookup.lookupCountryFromCode("238"));
		assertEquals("Vietnam", CountryLookup.lookupCountryFromCode("VN"));
		assertNull(CountryLookup.lookupCountryFromCode(null));
		assertNull(CountryLookup.lookupCountryFromCode("United States"));
		assertNull(CountryLookup.lookupCountryFromCode("ZZZZ"));
	}
	
	@Test
	public void testCountryName() { 
		assertTrue(CountryLookup.countryExistsHasCode("Uganda"));
		assertFalse(CountryLookup.countryExistsHasCode("UG"));
		assertFalse(CountryLookup.countryExistsHasCode("dwc:country"));
	}
	
	@Test
	public void testCountryCodeLookup() {
		assertFalse(CountryLookup.codeTwoLetterMatched(null));
		assertFalse(CountryLookup.codeTwoLetterMatched("99"));
		assertFalse(CountryLookup.codeTwoLetterMatched("ZZZ"));
		assertFalse(CountryLookup.codeTwoLetterMatched("foo"));
		assertFalse(CountryLookup.codeTwoLetterMatched(""));
		List<String> codes = CountryLookup.getCountryCodes2();
		Iterator<String> i = codes.iterator();
		while (i.hasNext()) { 
			assertTrue(CountryLookup.codeTwoLetterMatched(i.next()));
		}
	}
	
	@Test
	public void testCountryCodeFindCode2() { 
		assertNull(CountryLookup.lookupCode2FromCodeName(null));
		assertNull(CountryLookup.lookupCode2FromCodeName("foo"));
		assertNull(CountryLookup.lookupCode2FromCodeName(""));
		assertEquals("UG",CountryLookup.lookupCode2FromCodeName("UGA"));
		assertEquals("UG",CountryLookup.lookupCode2FromCodeName("Uganda"));
		assertEquals("AF",CountryLookup.lookupCode2FromCodeName("4"));
		assertEquals("AF",CountryLookup.lookupCode2FromCodeName("004"));
	}
	

}
