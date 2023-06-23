/**
 * GeoUtilSingleton.java
 */
package org.filteredpush.qc.georeference.util;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.getty.tgn.service.TGNWebServices;

/**
 * @author mole
 *
 */
public class GeoUtilSingleton {

	private static final Log logger = LogFactory.getLog(GeoUtilSingleton.class);

	private static final GeoUtilSingleton instance = new GeoUtilSingleton();
	private Map<String,Boolean> tgnCountries;
	
	private Map<String,String> tgnNations;
	
	
	private GeoUtilSingleton() { 
		init();
	}
	
	private void init() { 
		tgnCountries = new HashMap<String,Boolean>();
		tgnNations = new HashMap<String,String>();
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
	
	/** 
	 * Check provided country against the TGN list of nations, obtained and cached from the TGNGetNations service.
	 * 
	 * @param country to check
	 * 
	 * @return true if country is an exact match to a value in the TGNGetNations list, false if not, null if 
	 *   obtaining the list results in an exception.
	 */
	public Boolean isTgnNation(String country) { 
		Boolean retval = false;
		if (tgnNations.size()==0) { 
	    	TGNWebServices tgn = new TGNWebServices();
	    	try {
				edu.getty.tgn.service.ArrayOfListResults serviceReturn = tgn.getTGNWebServicesSoap().tgnGetNations("", "");
				List<edu.getty.tgn.service.ListResults> retList =  serviceReturn.getListResults();
				Iterator<edu.getty.tgn.service.ListResults> i = retList.iterator();
				while (i.hasNext()) { 
					edu.getty.tgn.service.ListResults row = i.next();
					tgnNations.put(row.getListValue(), row.getListId());
				}
			} catch (Exception e) {
				retval = null;
				logger.debug(e.getMessage(),e);
			}
		} 
		if (retval!=null) { 
			retval = tgnNations.containsKey(country);
		}
		
		return retval;
	}
	
}
