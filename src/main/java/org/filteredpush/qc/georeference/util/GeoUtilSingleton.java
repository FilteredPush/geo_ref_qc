/**
 * GeoUtilSingleton.java
 */
package org.filteredpush.qc.georeference.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author mole
 *
 */
public class GeoUtilSingleton {

	private static final Log logger = LogFactory.getLog(GeoUtilSingleton.class);

	private static final GeoUtilSingleton instance = new GeoUtilSingleton();
	private Map<String,Boolean> tgnCountries;
	
	
	private GeoUtilSingleton() { 
		init();
	}
	
	private void init() { 
		tgnCountries = new HashMap<String,Boolean>();
	}
	
	public static synchronized GeoUtilSingleton getInstance() {
		return instance;
	}

	/**
	 * Check cache of TGNCountry matches against a provided country.
	 * 
	 * @param country to check for previous lookup
	 * @return null if no match was found, otherwise the cached true or false value for
	 * a match on that country.
	 */
	public Boolean getTgnCountriesEntry(String country) { 
		Boolean retval = null;
		if (tgnCountries.containsKey(country)) { 
			Boolean value = tgnCountries.get(country);
			if (value!=null) { 
				retval = value;
			}
		}
		return retval;
	}
	
	/**
	 * Cache a match on a country name.
	 * 
	 * @param country key to cache
	 * @param match true or false to store in the cache, if null
	 *  not added to the cache.
	 */
	public void addTgnCountry(String country, Boolean match) { 
		if (match!=null) { 
			tgnCountries.put(country, match);
		}
	}
	
}
