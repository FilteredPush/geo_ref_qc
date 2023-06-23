/**
 * 
 */
package org.filteredpush.qc.geo.test;

import static org.junit.Assert.*;

import org.filteredpush.qc.georeference.util.CountryLookup;
import org.junit.Test;

/**
 * @author mole
 *
 */
public class CountryLookupTest {

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

}
