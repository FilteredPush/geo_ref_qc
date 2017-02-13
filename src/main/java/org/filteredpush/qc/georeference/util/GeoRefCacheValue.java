package org.filteredpush.kuration.util;

/**
 * Created by tianhong on 3/4/15.
 */
public class GeoRefCacheValue extends CacheValue{

    double lng;
    double lat;
    public GeoRefCacheValue (double lat, double lng){
        this.lng = lng;
        this.lat = lat;
    }

    public double getLat(){
        return lat;
    }

    public double getLng(){
        return lng;
    }

}
