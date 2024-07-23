package org.filteredpush.qc.georeference.util;

/**
 * Created by tianhong on 3/4/15.
 *
 * @author mole
 * @version $Id: $Id
 */
public class GeoRefCacheValue extends CacheValue {

    double lng;
    double lat;
    /**
     * <p>Constructor for GeoRefCacheValue.</p>
     *
     * @param lat a double.
     * @param lng a double.
     */
    public GeoRefCacheValue (double lat, double lng){
        this.lng = lng;
        this.lat = lat;
    }

    /**
     * <p>Getter for the field <code>lat</code>.</p>
     *
     * @return a double.
     */
    public double getLat(){
        return lat;
    }

    /**
     * <p>Getter for the field <code>lng</code>.</p>
     *
     * @return a double.
     */
    public double getLng(){
        return lng;
    }

}
