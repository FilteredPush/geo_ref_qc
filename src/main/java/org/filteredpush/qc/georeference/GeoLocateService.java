package org.filteredpush.qc.georeference;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.filteredpush.qc.georeference.util.CacheValue;
import org.filteredpush.qc.georeference.util.GEOUtil;
import org.filteredpush.qc.georeference.util.GeoRefCacheValue;
import org.filteredpush.qc.georeference.util.GeolocationResult;

import org.geolocate.webservices.Geolocatesvc;
import org.geolocate.webservices.GeolocatesvcSoap;
import org.geolocate.webservices.GeorefResult;
import org.geolocate.webservices.GeorefResultSet;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lowery on 2/24/17.
 */
public class GeoLocateService {
    private static final Log logger = LogFactory.getLog(GeoLocate3.class);

    private boolean useCache = true;
    private static HashMap<String, List<GeolocationResult>> responseCache = new HashMap<>();
    /**
     * Given country, stateProvince, county/Shire, and locality strings, return all matches found by geolocate for
     * that location.
     *
     * @param country
     * @param stateProvince
     * @param county
     * @param locality
     * @param latitude for distance comparison in log
     * @param longitude for distance comparison in log
     * @return
     */
    public List<GeolocationResult> queryGeoLocateMulti(String country, String stateProvince, String county, String locality, String latitude, String longitude) {
        List<GeolocationResult> result = new ArrayList<GeolocationResult>();

        String key = constructCachedMapKey(country, stateProvince, county, locality);

        if (useCache && responseCache.containsKey(key)){
            logger.debug("Geolocate3.validateGeoref found in cache: " + key);
            result = responseCache.get(key);
            return result;
            //System.out.println("geocount = " + count++);
            //System.out.println("key = " + key);
        }

        Geolocatesvc service = new Geolocatesvc();
        
        GeolocatesvcSoap geolocateService = service.getGeolocatesvcSoap();;

        // Test page for georef2 at: http://www.museum.tulane.edu/webservices/geolocatesvcv2/geolocatesvc.asmx?op=Georef2

        boolean hwyX = false;   // look for road/river crossing
        if (locality!=null && locality.toLowerCase().matches("bridge")) {
            hwyX = true;
        }
        boolean findWaterbody = false;  // find waterbodies
        if (locality!=null && locality.toLowerCase().matches("(lake|pond|sea|ocean)")) {
            findWaterbody = true;
        }
        boolean restrictToLowestAdm = true;
        boolean doUncert = true;  // include uncertainty radius in results
        boolean doPoly = false;   // include error polygon in results
        boolean displacePoly = false;  // displace error polygon in results
        boolean polyAsLinkID = false;
        int languageKey = 0;  // 0=english; 1=spanish

        GeorefResultSet results;
        try {
            results = geolocateService.georef2(country, stateProvince, county, locality, hwyX, findWaterbody, restrictToLowestAdm, doUncert, doPoly, displacePoly, polyAsLinkID, languageKey);
            int numResults = results.getNumResults();
            //addToComment(" found " + numResults + " possible georeferences with Geolocate engine:" + results.getEngineVersion());
            Iterator<GeorefResult> i = results.getResultSet().iterator();
            while (i.hasNext()) {
                GeorefResult res = i.next();
                try {
                    double lat2 = Double.parseDouble(latitude);
                    double lon2 = Double.parseDouble(longitude);
                    long distance = GEOUtil.calcDistanceHaversineMeters(res.getWGS84Coordinate().getLatitude(), res.getWGS84Coordinate().getLongitude(), lat2, lon2)/100;
                    //addToComment(res.getParsePattern() + " score:" + res.getScore() + " "+ res.getWGS84Coordinate().getLatitude() + " " + res.getWGS84Coordinate().getLongitude() + " km:" + distance);
                } catch (NumberFormatException e) {
                    //addToComment(res.getParsePattern() + " score:" + res.getScore() + " "+ res.getWGS84Coordinate().getLatitude() + " " + res.getWGS84Coordinate().getLongitude());
                }
            }
            result = GeolocationResult.constructFromGeolocateResultSet(results);

            if (useCache) {
                responseCache.put(key, result);
            }
        } catch (Exception e) {
            logger.debug(e.getMessage());
            //addToComment(e.getMessage());
        }

        return result;
    }

    private String constructCachedMapKey(String country, String state, String county, String locality){
        return country+" "+state+" "+county+" "+locality;
    }
}
