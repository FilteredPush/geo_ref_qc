package org.filteredpush.qc.geo.test;

import org.filteredpush.qc.georeference.GeoLocateService;
import org.filteredpush.qc.georeference.util.GeolocationResult;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author lowery
 *
 */
public class GeoLocateServiceTestIT {
    private static GeoLocateService service = new GeoLocateService();

    @Test
    public void test() {
        List<GeolocationResult> results = service.queryGeoLocateMulti("USA", "Arizona", "", "1 mi. S of Portal", "31.899097", "-109.14083");
        assertTrue(results.size() > 0);
    }
}
