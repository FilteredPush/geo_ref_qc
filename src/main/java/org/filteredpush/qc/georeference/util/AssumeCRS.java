/**
 * AssumeCRS.java
 */
package org.filteredpush.qc.georeference.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility for providing the EPSG codes for geographic coordinate systems for a small set of geodeticDatum values
 * assuming that the datum is used as the horizontal datum with latitude and longitude in a coordinate reference system.
 *
 * @author mole
 * @version $Id: $Id
 */
public class AssumeCRS {
	
	private Map <String,String> lookups;

	private static final Log logger = LogFactory.getLog(AssumeCRS.class);

	/**
	 * <p>Constructor for AssumeCRS.</p>
	 */
	public AssumeCRS() {
		init();
	}
	
	private void init() { 
		lookups = new HashMap<String,String>();
		lookups.put("WGS84", "EPSG:4326");
		lookups.put("WGS 84", "EPSG:4326");
		lookups.put("WGS 1984", "EPSG:4326");
		lookups.put("NAD27", "EPSG:4267");
		lookups.put("NAD 27", "EPSG:4267");
		lookups.put("NAD 1927", "EPSG:4267");
		lookups.put("NAD83", "EPSG:4269");
		lookups.put("NAD 83", "EPSG:4269");
		lookups.put("NAD 1983", "EPSG:4269");
	} 
	
	/**
	 * <p>isTransformable.</p>
	 *
	 * @param geodeticDatum a {@link java.lang.String} object.
	 * @return a boolean.
	 */
	public boolean isTransformable(String geodeticDatum) { 
		boolean retval = false;
		if (lookups.containsKey(geodeticDatum)) {
			retval = true;
		}
		return retval;
	}
	
	/**
	 * return the EPSG code for a latitude/longitude geographic coordinate system
	 * with the specified geodetic datum string, translates strings representing
	 * datums such as 'WGS84' into an EPSG code for a CRS, assuming that the
	 * geodetic datum is combined with a geographic coordinate system into a CRS.
	 *
	 * @param geodeticDatum a {@link java.lang.String} object.
	 * @return EPSG code if one is on the short list of transforms, or null if no match.
	 */
	public String getEpsgForDatumAndGCRS(String geodeticDatum) { 
		String retval = null;
		if (lookups.containsKey(geodeticDatum)) { 
			retval = lookups.get(geodeticDatum);
		}
		return retval;
	}
}
