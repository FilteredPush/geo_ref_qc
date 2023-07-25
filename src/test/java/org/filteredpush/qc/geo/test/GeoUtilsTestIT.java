/**
 * GeoUtilsTestIT.java
 */
package org.filteredpush.qc.geo.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.filteredpush.qc.georeference.util.CountryLookup;
import org.filteredpush.qc.georeference.util.GEOUtil;
import org.junit.Test;

/**
 * @author mole
 *
 */
public class GeoUtilsTestIT {

	private static final Log logger = LogFactory.getLog(GeoUtilsTestIT.class);

	@Test
	public void testGetCountryForPoint() { 

		String latitude = "71.295556";
		String longitude = "-156.766389";
		assertEquals("USA", GEOUtil.getCountryForPoint(latitude, longitude));

		try {
			String countryListUri = "https://raw.githubusercontent.com/mihai-craita/countries_center_box/master/countries.csv";
			InputStream inputStream;
			inputStream = new URL(countryListUri).openStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			CSVParser records = CSVFormat.DEFAULT.parse(reader);
			Iterator<CSVRecord> i = records.iterator();
			while (i.hasNext()) { 
				CSVRecord record = i.next();
				String countryCode = record.get(2);
				logger.debug(countryCode);
				latitude = record.get(3);
				longitude = record.get(4);
				String countryCode3 = CountryLookup.lookupCode3FromCodeName(countryCode);
				if (countryCode3==null || countryCode3.equals("CYP") || countryCode3.equals("GAB") ||
						countryCode3.equals("LIE") || countryCode3.equals("MCO") || 
						countryCode3.equals("SMR") || countryCode3.equals("VAT")) 
				{	
					countryCode3=null; 
				} 
				// GAB, LIE have wrong sign in data set.  
				// CYP, MCO come up with multiple matches from buffers
				if (countryCode3!=null) { 
					assertEquals(countryCode3,GEOUtil.getCountryForPoint(latitude, longitude));
				}
			}
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
	
}
