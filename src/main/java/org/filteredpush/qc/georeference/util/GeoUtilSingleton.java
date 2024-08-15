/**
 * GeoUtilSingleton.java
 */
package org.filteredpush.qc.georeference.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.getty.tgn.service.TGNWebServices;

/**
 * <p>GeoUtilSingleton class.</p>
 *
 * @author mole
 * @version $Id: $Id
 */
public class GeoUtilSingleton {

	private static final Log logger = LogFactory.getLog(GeoUtilSingleton.class);

	private static final GeoUtilSingleton instance = new GeoUtilSingleton();
	private Map<String,Boolean> tgnCountries;
	private Map<String,Boolean> tgnPrimary;
	
	private Map<String,String> tgnNations;
	
	private Map<String,ArrayList<String>> gettyCountryLookup;	
	private Map<String,ArrayList<String>> gettyPrimaryLookup;	
	
	private GettyLookup gettyLookup;
	
	private GeoUtilSingleton() { 
		init();
	}
	
	private void init() { 
		tgnCountries = new HashMap<String,Boolean>();
		tgnNations = new HashMap<String,String>();
		tgnPrimary = new HashMap<String,Boolean>();
		gettyCountryLookup = new HashMap<String,ArrayList<String>>();
		gettyPrimaryLookup = new HashMap<String,ArrayList<String>>();
	}
	
	/**
	 * <p>Getter for the field <code>instance</code>.</p>
	 *
	 * @return a {@link org.filteredpush.qc.georeference.util.GeoUtilSingleton} object.
	 */
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
	 * Obtain cache entry for match on a primary division (state/province) name
	 *
	 * @param stateProvince to check
	 * @return cached true or false value, or null if not cached.
	 */
	public Boolean getTgnPrimaryEntry(String stateProvince) { 
		Boolean retval = null;
		if (tgnPrimary.containsKey(stateProvince)) { 
			Boolean value = tgnPrimary.get(stateProvince);
			if (value!=null) { 
				retval = value;
			}
		}
		return retval;
	}
	
	/**
	 * Cache a match on a primary division name.
	 *
	 * @param stateProvince key to cache
	 * @param match true or false to store in the cache, if null
	 *  not added to the cache.
	 */
	public void addTgnPrimary(String stateProvince, Boolean match) { 
		if (match!=null) { 
			tgnPrimary.put(stateProvince, match);
		}
	}
	
	/**
	 * Check provided country against the TGN list of nations, obtained and cached from the TGNGetNations service.
	 *
	 * @param country to check
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
	
	
	/**
	 * <p>getGettyCountryLookupItem.</p>
	 *
	 * @return a list of names for a country the gettyCountryLookup
	 * @param country a {@link java.lang.String} object.
	 */
	public List<String> getGettyCountryLookupItem(String country) {
		return gettyCountryLookup.get(country);
	}
	
	/**
	 * <p>isGettyCountryLookupItem.</p>
	 *
	 * @param country a {@link java.lang.String} object.
	 * @return a boolean.
	 */
	public boolean isGettyCountryLookupItem(String country) { 
		return gettyCountryLookup.containsKey(country);
	}
	
	/**
	 * <p>addGettyCountryLookupItem.</p>
	 *
	 * @param country a {@link java.lang.String} object.
	 * @param names a {@link java.util.ArrayList} object.
	 */
	public void addGettyCountryLookupItem(String country, ArrayList<String> names) {
		gettyCountryLookup.put(country, names);
	}
	
	/**
	 * <p>getGettyPrimaryLookupItem.</p>
	 *
	 * @return a list of names for a stateProvince cached in gettyPrimaryLookup
	 * @param stateProvince a {@link java.lang.String} object.
	 */
	public List<String> getGettyPrimaryLookupItem(String stateProvince) {
		return gettyPrimaryLookup.get(stateProvince);
	}
	
	/**
	 * Check if a stateProvince result is cached for the Getty TGN
	 *
	 * @param stateProvince a {@link java.lang.String} object.
	 * @return a boolean.
	 */
	public boolean isGettyPrimaryLookupItem(String stateProvince) { 
		return gettyPrimaryLookup.containsKey(stateProvince);
	}
	
	/**
	 * <p>addGettyPrimaryLookupItem.</p>
	 *
	 * @param stateProvince a {@link java.lang.String} object.
	 * @param names a {@link java.util.ArrayList} object.
	 */
	public void addGettyPrimaryLookupItem(String stateProvince, ArrayList<String> names) {
		gettyPrimaryLookup.put(stateProvince, names);
	}

	/** 
	 * Obtain an instance of a shared GettyLookup class.
	 * @return a reusable GettyLookup instance.
	 */
	public GettyLookup getGettyLookup() {
		if (gettyLookup==null) { 
			this.gettyLookup = new GettyLookup();
		}
		return this.gettyLookup;
	}
	
	
}
