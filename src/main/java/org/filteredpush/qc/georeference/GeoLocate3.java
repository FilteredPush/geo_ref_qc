package org.filteredpush.qc.georeference;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.filteredpush.qc.georeference.util.*;
import org.geotools.data.shapefile.shp.ShapefileException;

import java.awt.geom.Path2D;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class GeoLocate3 {
	private static final Log logger = LogFactory.getLog(GeoLocate3.class);
	private static final GeoLocateService service = new GeoLocateService();

    private boolean useCache = true;
    private Cache cache;
    
	private File cacheFile = null;

	private double correctedLatitude;
	private double correctedLongitude;
    private List<List> log = new LinkedList<List>();

    static int count = 0;
	private static HashMap<String, CacheValue> coordinatesCache = new HashMap<String, CacheValue>();
	private Vector<String> newFoundCoordinates;
	private static final String ColumnDelimiterInCacheFile = "\t";
	
	private final String url = "http://www.museum.tulane.edu/webservices/geolocatesvc/geolocatesvc.asmx/Georef2?";
    //private final String url = "http://lore.genomecenter.ucdavis.edu/cache/geolocate.php";
	private final String defaultNameSpace = "http://www.museum.tulane.edu/webservices/";    
	
	/*
	 * If latitude or longitude is null, it means such information is missing in the original records
	 * 
	 * @see org.kepler.actor.SpecimenQC.IGeoRefValidationService#validateGeoRef(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void validateGeoRef(String country, String stateProvince, String county, String waterBody, String verbatimDepth, String locality, String latitude, String longitude, double thresholdDistanceKm){
		logger.debug("Geolocate3.validateGeoref("+country+","+stateProvince+","+county+","+locality+")");
		
		HashMap<String, String> initialValues = new HashMap<String, String>();
		initialValues.put("decimalLatitude", latitude);
		initialValues.put("decimalLongitude", longitude);
		//initBase(new CurationStep("Validate Georeference: check dwc:decimalLatitude and dwc:decimalLongitude against the textual locality data. ", initialValues));
		//setCurationStatus(CurationComment.UNABLE_CURATED);
		correctedLatitude = -1;
		correctedLongitude = -1;
		
		// overloaded for extraction into "WAS" values by MongoSummaryWriter
        //addToServiceName("decimalLatitude:" + latitude + "#decimalLongitude:" + longitude + "#");
        //this.addInputValue("decimalLatitude", latitude);
        //this.addInputValue("decimalLongitude", longitude);
        log = new LinkedList<List>();
        
        List<GeolocationResult> potentialMatches = null;

        //first search for reference coordinates
        String key = country+" "+stateProvince+" "+county+" "+locality;
        if (useCache && coordinatesCache.containsKey(key)){
            GeoRefCacheValue cachedValue = (GeoRefCacheValue) coordinatesCache.get(key);
            GeolocationResult fromCache = new GeolocationResult(cachedValue.getLat(), cachedValue.getLng(),0,0,"");
            potentialMatches = new ArrayList<GeolocationResult>();
            potentialMatches.add(fromCache);
		    logger.debug("Geolocate3.validateGeoref found in cache " + fromCache.getLatitude() + " " + fromCache.getLongitude());
            //System.out.println("geocount = " + count++);
            //System.out.println("key = " + key);
        } else {
        	// Look up locality in Tulane's GeoLocate service
			potentialMatches = service.queryGeoLocateMulti(country, stateProvince, county, locality, latitude, longitude);

            if(potentialMatches == null || potentialMatches.size()==0){
                //setCurationStatus(CurationComment.UNABLE_DETERMINE_VALIDITY);
                //addToComment("GeoLocate service can't find coordinates of Locality. ");
            }
        }

        // start validation
        
        // Make strings null if they don't contain valid lat/long values
        if (latitude!=null && latitude.trim().length()==0) { latitude = null; } 
        if (longitude!=null && longitude.trim().length()==0) { longitude = null; } 
        if (latitude!=null) { 
           try { Double.valueOf(latitude); } catch (NumberFormatException e) { latitude = null; }
        }
        if (longitude!=null) { 
           try { Double.valueOf(longitude); } catch (NumberFormatException e) { longitude = null; }
        }
        
        // Try to fill in missing values 
        if(latitude == null || longitude == null){
        	if (potentialMatches.size()>0 && potentialMatches.get(0).getConfidence()>80 ) { 
        		if (latitude!=null && longitude==null) { 
        			// Try to fill in the longitude
        			if (GeolocationResult.isLocationNearAResult(Double.valueOf(latitude), potentialMatches.get(0).getLongitude(), potentialMatches, (int)Math.round(thresholdDistanceKm * 1000))) {
        				// if latitude plus longitude from best match is near a match, propose the longitude from the best match.
        			    //setCurationStatus(CurationComment.FILLED_IN);
        				correctedLongitude = potentialMatches.get(0).getLongitude();
        				// TODO: If we do this, then we need to add the datum, georeference source, georeference method, etc.
        				//addToComment("Added a longitude from "+getServiceName()+" as longitude was missing and geolocate had a confident match near the original line of latitude. ");
        			}
        		}
        		if (latitude==null && longitude!=null) { 
        			// Try to fill in the latitude
        			if (GeolocationResult.isLocationNearAResult(potentialMatches.get(0).getLatitude(), Double.valueOf(longitude), potentialMatches, (int)Math.round(thresholdDistanceKm * 1000))) {
        				// if latitude plus longitude from best match is near a match, propose the longitude from the best match.
        			    //setCurationStatus(CurationComment.FILLED_IN);
        				correctedLatitude = potentialMatches.get(0).getLatitude();
        				// TODO: If we do this, then we need to add the datum, georeference source, georeference method, etc.
        				//addToComment("Added a latitude from "+getServiceName()+" as latitude was missing and geolocate had a confident match near the original line of longitude. ");
        			}
        		}
        		//Both coordinates in the original record are missing
        		if (latitude==null && longitude ==null) { 
        			//setCurationStatus(CurationComment.FILLED_IN);
        			correctedLatitude = potentialMatches.get(0).getLatitude();
        			correctedLongitude = potentialMatches.get(0).getLongitude();
        			// TODO: If we do this, then we need to add the datum, georeference source, georeference method, etc.
        			//addToComment("Added a georeference using cached data or "+getServiceName()+"service since the original coordinates are missing and geolocate had a confident match. ");
        		}
        	} else { 
        		//setCurationStatus(CurationComment.UNABLE_DETERMINE_VALIDITY);
        		//addToComment("No latitude and/or longitude provided, and geolocate didn't return a good match.");
        	}
        } else {
            //calculate the distance from the returned point and original point in the record
            //If the distance is smaller than a certainty, then use the original point --- GEOService, like GeoLocate can't parse detailed locality. In this case, the original point has higher confidence
            //Otherwise, use the point returned from GeoLocate
        	//addToComment("Latitute and longitude are both present.");

            double originalLat = Double.valueOf(latitude);
            double originalLong = Double.valueOf(longitude);
            double rawLat = originalLat;
            double rawLong = originalLong;
            
            // Construct a list of alternatives
            List<GeolocationAlternative> alternatives = GeolocationAlternative.constructListOfAlternatives(originalLat, originalLong); 

            boolean flagError = false;
            boolean foundGoodMatch = false;
            
            // Check for possible error conditions
            
            // (1) Latitude and longitude out of range
            if (Math.abs(originalLat)>90) { 
                //addToComment("The original latitude is out of range.");
                flagError = true;
            }
            if (Math.abs(originalLong)>180) { 
                //addToComment("The original longitude is out of range.");
                flagError = true;
            }
            if (!flagError) { 
        	    //addToComment("Latitute is within +/-90 and longitude is within +/-180.");
            }

            // Check country and stateProvince
            if (country != null && country.length()>0) {
            	//standardize country names
            	if (country.toUpperCase().equals("USA")) {
            		country = "United States";
            	} else if (country.toUpperCase().equals("U.S.A.")) {
            		country = "United States";
            	} else if (country.toLowerCase().equals("united states of america")) {
            		country = "United States";
            	} else {
            		country = country.toUpperCase();
            		//System.out.println("not in !##"+country+"##");
            	} 

            	// (2) Locality not inside country?
            	if (GEOUtil.isCountryKnown(country)) {  
            		if (GEOUtil.isPointInCountry(country, originalLat, originalLong)) {  
            			//addToComment("Original coordinate is inside country ("+country+").");
            			//addToServiceName("Country boundary data from Natural Earth");
            		} else { 
            			//addToComment("Original coordinate is not inside country ("+country+").");
            			//addToServiceName("Country boundary data from Natural Earth");
            			flagError = true;
            		}
            	} else { 
            		//addToComment("Can't find country: " + country + " in country name list");
            	}

            	if (stateProvince!=null && stateProvince.length()>0) { 
            		// (3) Locality not inside primary division?
            		if (GEOUtil.isPrimaryKnown(country, stateProvince)) { 
            			if (GEOUtil.isPointInPrimary(country, stateProvince, originalLat, originalLong)) {
            				//addToComment("Original coordinate is inside primary division ("+stateProvince+").");
            				//addToServiceName("State/province boundary data from Natural Earth");
            			} else { 
            				//addToComment("Original coordinate is not inside primary division ("+stateProvince+").");
            				//addToServiceName("State/province boundary data from Natural Earth");
            				flagError = true;
            			} 
            		} else { 
            			//addToComment("Can't find state/province: " + stateProvince + " in primaryDivision name list");
            		}
            	}
            }

            // (4) Is locality marine? 
            Set<Path2D> setPolygon = null;
            try {
            	setPolygon = new GISDataLoader().ReadLandData();
            	//System.out.println("read data");
            } catch (ShapefileException e) {
            	logger.error(e.getMessage());
            } catch (IOException e) {
            	logger.error(e.getMessage());
            }
            boolean isMarine = false;
            if ((country==null||country.length()==0) && (stateProvince==null||stateProvince.length()==0) && (county==null||county.length()==0)) {
            	//addToComment("No country, state/province, or county provided, guessing that this is a marine locality. ");
            	// no country provided, assume locality is marine
            	isMarine = true;
            } else { 
            	if (waterBody!=null && waterBody.trim().length()>0) { 
            		if (waterBody.matches("(Indian|Pacific|Arctic|Atlantic|Ocean|Sea|Carribean|Mediteranian)")) { 
            			isMarine = true;
            	        //addToComment("A water body name that appears to be an ocean or a sea was provided, guessing that this is a marine locality. ");
            		} else { 
            	        //addToComment("A country, state/province, or county was provided with a water body that doesn't appear to be an ocean or a sea, guessing that this is a non-marine locality. ");
            		}
            	} else { 
            	    //addToComment("A country, state/province, or county was provided but no water body, guessing that this is a non-marine locality. ");
            	}
            }
            if (!GEOUtil.isInPolygon(setPolygon, originalLong, originalLat, isMarine)) {
            	if (isMarine) { 
            		//addToComment("Coordinate is on land for a supposedly marine locality.");
            		flagError = true;
            	} else { 
            		//addToComment("Coordinate is not on land for a supposedly non-marine locality.");
            		double thresholdDistanceKmFromLand = 44.448d;  // 24 nautical miles, territorial waters plus contigouus zone.
            		if (GEOUtil.isPointNearCountry(country, originalLat, originalLong, thresholdDistanceKmFromLand)) { 
            			//addToComment("Coordinate is within 24 nautical miles of country boundary, could be a nearshore marine locality.");
            		} else { 
            			//addToComment("Coordinate is further than 24 nautical miles of country boundary, country in error or marine within EEZ.");
            			flagError = true;
            		}
            	}
            }            

            // (5) Geolocate returned some result, is original locality near that result?
            if (potentialMatches!=null && potentialMatches.size()>0) { 
            	if (GeolocationResult.isLocationNearAResult(originalLat, originalLong, potentialMatches, (int)Math.round(thresholdDistanceKm * 1000))) {
            		//setCurationStatus(CurationComment.CORRECT);
            		correctedLatitude = originalLat;
            		correctedLongitude = originalLong;
            		//addToComment("Original coordinates are near (within georeference error radius or " +  thresholdDistanceKm + " km) the georeference for the locality text from the Geolocate service.  Accepting the original coordinates. ");
            		flagError = false;
            		foundGoodMatch = true;
            	} else {
            		//addToComment("Original coordinates are not near (within georeference error radius or " +  thresholdDistanceKm + " km) the georeference for the locality text from the Geolocate service. ");
            		flagError = true;
            	}
            }


            // Some error condition was found, see if any transposition returns a plausible locality
            boolean matchFound = false;
            if (flagError) {
            	Iterator<GeolocationAlternative> i = alternatives.iterator();
            	while (i.hasNext() && !matchFound) { 
            		GeolocationAlternative alt = i.next();
            		if (potentialMatches !=null && potentialMatches.size()>0) { 
            			if (GeolocationResult.isLocationNearAResult(alt.getLatitude(), alt.getLongitude(), potentialMatches, (int)Math.round(thresholdDistanceKm * 1000))) {
            				//setCurationStatus(CurationComment.CURATED);
            				correctedLatitude = alt.getLatitude();
            				correctedLongitude = alt.getLongitude();
            				//addToComment("Modified coordinates ("+alt.getAlternative()+") are near (within georeference error radius or " +  thresholdDistanceKm + " km) the georeference for the locality text from the Geolocate service.  Accepting the " + alt.getAlternative() + " coordinates. ");
            				matchFound = true;
            			}            	
            		} else {
            			if (isMarine) {
            				if (country!=null && GEOUtil.isCountryKnown(country)) { 
            					double thresholdDistanceKmFromLand = 44.448d;  // 24 nautical miles, territorial waters plus contigouus zone.
            					if (GEOUtil.isPointNearCountry(country, originalLat, originalLong, thresholdDistanceKmFromLand)) { 
            						//addToComment("Modified coordinate (" + alt.getAlternative() + ") is within 24 nautical miles of country boundary.");
            						correctedLatitude = alt.getLatitude();
            						correctedLongitude = alt.getLongitude();
            						matchFound = true;
            					}
            				}
            			} else { 
            				if (GEOUtil.isCountryKnown(country) && 
            						GEOUtil.isPointInCountry(country, alt.getLatitude(), alt.getLongitude())) { 
            					//addToComment("Modified coordinate ("+alt.getAlternative()+") is inside country ("+country+").");
            					if (GEOUtil.isPrimaryKnown(country, stateProvince) && 
            							GEOUtil.isPointInPrimary(country, stateProvince, originalLat, originalLong)) { 
            						//setCurationStatus(CurationComment.CURATED);
            						//addToComment("Modified coordinate ("+alt.getAlternative()+") is inside stateProvince ("+stateProvince+").");
            						correctedLatitude = alt.getLatitude();
            						correctedLongitude = alt.getLongitude();
            						matchFound = true;
            					}
            				}
            			}
            		}
            	}
            }
            
            if (flagError) { 
            	if (matchFound) { 
            		//setCurationStatus(CurationComment.CURATED);
                    if(useCache) { 
                    	addNewToCache(correctedLatitude, correctedLongitude, country, stateProvince, county, locality); 
                    }
            	} else { 
            		if (country!=null && GEOUtil.isCountryKnown(country)) {
            			if (isMarine) { 
            				 //addToComment("No transformation of the coordinates is near the provided country.");
            			} else {
            				if (stateProvince!=null && GEOUtil.isPrimaryKnown(country, stateProvince)) { 
            				     //addToComment("No transformation of the coordinates is inside the provided country and state/province.");
            				}
            			}
            		}
            		//setCurationStatus(CurationComment.UNABLE_CURATED);
            	}
            } else { 
            	if (foundGoodMatch) { 
            		//setCurationStatus(CurationComment.CORRECT);
                    if(useCache) { 
                    	addNewToCache(originalLat, originalLong, country, stateProvince, county, locality); 
                    }
            	} else { 
            		//setCurationStatus(CurationComment.UNABLE_DETERMINE_VALIDITY);
            	}
            }

            //System.out.println("setCurationStatus(" + curationStatus);
            //System.out.println("comment = " + comment);

            //System.out.println("originalLng = " + originalLng);
            //System.out.println("originalLat = " + originalLat);
            //System.out.println("country = " + country);
            //System.out.println("foundLng = " + foundLng);
            //System.out.println("foundLat = " + foundLat);

        }
		//logger.debug("Geolocate3.validateGeoref done " + getCurationStatus());
	}


    public void addNewToCache(double Lat, double Lng, String country, String stateProvince, String county, String locality) {
        String key = constructCachedMapKey(country, stateProvince, county, locality);
        if(!coordinatesCache.containsKey(key)){
            CacheValue newValue = new GeoRefCacheValue(Lat, Lng);
            coordinatesCache.put(key, newValue);
            logger.debug("adding georeference to cache " + key + " " + ((GeoRefCacheValue)newValue).getLat() + " "+ ((GeoRefCacheValue)newValue).getLat());
        }

    }

    private String constructCachedMapKey(String country, String state, String county, String locality){
        return country+" "+state+" "+county+" "+locality;
    }
	
}
