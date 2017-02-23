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
		assertEquals("Afghanistan", CountryLookup.lookupCountry("AF"));
		assertEquals("Falkland Islands", CountryLookup.lookupCountry("FK"));
		assertEquals("Falkland Islands", CountryLookup.lookupCountry("FLK"));
		assertEquals("Falkland Islands", CountryLookup.lookupCountry("238"));
		assertEquals("Vietnam", CountryLookup.lookupCountry("VN"));
		assertNull(CountryLookup.lookupCountry(null));
		assertNull(CountryLookup.lookupCountry("United States"));
		assertNull(CountryLookup.lookupCountry("ZZZZ"));
	}

}
