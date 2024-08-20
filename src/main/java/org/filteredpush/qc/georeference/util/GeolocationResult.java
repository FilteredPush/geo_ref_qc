/**
 *
 */
package org.filteredpush.qc.georeference.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.geolocate.webservices.svcv2.GeorefResult;
import org.geolocate.webservices.svcv2.GeorefResultSet;

/**
 * Representation of a single georeference assertion as made by GeoLocate's service.
 *
 * @author mole
 * @version $Id: $Id
 */
public class GeolocationResult implements Serializable {

	private static final long serialVersionUID = 5250497528282353757L;

	private static final Log logger = LogFactory.getLog(GeolocationResult.class);

	/** Constant <code>MIN_SCORE_THRESHOLD=25</code> */
	public static final int MIN_SCORE_THRESHOLD = 25;

	private Double latitude;
	private Double longitude;
	private Integer coordinateUncertainty;
	private Integer confidence;
	private String parseString;
	/**
	 * <p>Constructor for GeolocationResult.</p>
	 *
	 * @param latitude a double.
	 * @param longitude a double.
	 * @param confidence a int.
	 * @param parseString the pattern used by geolocate to recognize the part of the text to georefence.
	 * @param coordinateUncertainty a int.
	 */
	public GeolocationResult(double latitude, double longitude, int coordinateUncertainty, int confidence,
			String parseString) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.confidence = confidence;
		this.parseString = parseString;
		this.coordinateUncertainty = coordinateUncertainty;
	}

	/**
	 * Lightweight constructor for using object to carry only latitude and longitude.
	 *
	 * @param latitude a double
	 * @param longitude a double
	 */
	public GeolocationResult(double latitude, double longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.confidence = null;
		this.parseString = null;
		this.coordinateUncertainty = null;
	}
	
	/**
	 * <p>constructFromXML.</p>
	 *
	 * @param geolocateXmlResult a {@link org.dom4j.Document} object.
	 * @return a {@link java.util.List} object.
	 */
	public static List<GeolocationResult> constructFromXML(Document geolocateXmlResult) {
		ArrayList<GeolocationResult> result = new ArrayList<GeolocationResult>();

		return result;
	}

	/**
	 * <p>constructFromGeolocateResultSet.</p>
	 *
	 * @param results a {@link org.geolocate.webservices.svcv2.GeorefResultSet} object.
	 * @return a {@link java.util.List} object.
	 */
	public static List<GeolocationResult> constructFromGeolocateResultSet(GeorefResultSet results) {
		ArrayList<GeolocationResult> resultList = new ArrayList<GeolocationResult>();
		if (results !=null && results.getNumResults()>0) {
		    int numResults = results.getNumResults();
		    Iterator<GeorefResult> i = results.getResultSet().iterator();
			while(i.hasNext()) {
			   GeorefResult row = i.next();
			   if (row.getScore()>MIN_SCORE_THRESHOLD) {
				   int uncertainty = 0;
				   try {
					   if (row.getUncertaintyRadiusMeters()!=null && !row.getUncertaintyRadiusMeters().equals("Unavailable")) {
					      uncertainty = Integer.parseInt(row.getUncertaintyRadiusMeters());
					   }
				   } catch (NumberFormatException ex) {
					   logger.debug(ex.getMessage());
				   }
				   GeolocationResult result = new GeolocationResult(
						   row.getWGS84Coordinate().getLatitude(),
						   row.getWGS84Coordinate().getLongitude(),
						   uncertainty,
						   row.getScore(),
						   row.getParsePattern()
						   );
				   resultList.add(result);
			   }
			}
		}
		return resultList;
	}

	/**
	 * <p>isLocationNearAResult.</p>
	 *
	 * @param latitude a double.
	 * @param longitude a double.
	 * @param toCompare a {@link java.util.List} object.
	 * @param thresholdDistanceMeters a int.
	 * @return a boolean.
	 */
	public static boolean isLocationNearAResult(double latitude, double longitude, List<GeolocationResult> toCompare, int thresholdDistanceMeters) {
		boolean result = false;
		if (toCompare!=null && toCompare.size()>0) {
			Iterator<GeolocationResult> i = toCompare.iterator();
			while (!result && i.hasNext()) {
				GeolocationResult candidate = i.next();
				long distance = GEOUtil.calcDistanceHaversineMeters(latitude, longitude, candidate.getLatitude(), candidate.getLongitude());
				if (candidate.getCoordinateUncertaintyMeters()>0) {
					if (distance<=candidate.getCoordinateUncertaintyMeters() || distance<=thresholdDistanceMeters) {
						result = true;
					}
				} else {
				    if (distance<=thresholdDistanceMeters) {
				    	result = true;
				    }
				}
			}
		}
		return result;
	}


	/**
	 * <p>getCachableNearAResult.</p>
	 *
	 * @param latitude a double.
	 * @param longitude a double.
	 * @param toCompare a {@link java.util.List} object.
	 * @param thresholdDistanceMeters a int.
	 * @return a {@link org.filteredpush.qc.georeference.util.GeoRefCacheValue} object.
	 */
	public static GeoRefCacheValue getCachableNearAResult(double latitude, double longitude, List<GeolocationResult> toCompare, int thresholdDistanceMeters) {
		GeoRefCacheValue result = null;
		if (toCompare!=null && toCompare.size()>0) {
			Iterator<GeolocationResult> i = toCompare.iterator();
			boolean matched = false;
			while (!matched && i.hasNext()) {
				GeolocationResult candidate = i.next();
				long distance = GEOUtil.calcDistanceHaversineMeters(latitude, longitude, candidate.getLatitude(), candidate.getLongitude());
				if (candidate.getCoordinateUncertaintyMeters()>0) {
					if (distance<=candidate.getCoordinateUncertaintyMeters()) {
						matched = true;
						result = new GeoRefCacheValue(candidate.getLatitude(),candidate.getLongitude());
					}
				} else {
				    if (distance<=thresholdDistanceMeters) {
				    	matched = true;
						result = new GeoRefCacheValue(candidate.getLatitude(),candidate.getLongitude());
				    }
				}
			}
		}
		return result;
	}

	/**
	 * <p>Getter for the field <code>latitude</code>.</p>
	 *
	 * @return the latitude
	 */
	public Double getLatitude() {
		return latitude;
	}
	/**
	 * <p>Setter for the field <code>latitude</code>.</p>
	 *
	 * @param latitude the latitude to set
	 */
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	/**
	 * <p>Getter for the field <code>longitude</code>.</p>
	 *
	 * @return the longitude
	 */
	public Double getLongitude() {
		return longitude;
	}
	/**
	 * <p>Setter for the field <code>longitude</code>.</p>
	 *
	 * @param longitude the longitude to set
	 */
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	/**
	 * <p>getCoordinateUncertaintyMeters.</p>
	 *
	 * @return the coordinateUncertainty
	 */
	public Integer getCoordinateUncertaintyMeters() {
		return coordinateUncertainty;
	}
	/**
	 * <p>setCoordinateUncertaintyMeters.</p>
	 *
	 * @param coordinateUncertainty the coordinateUncertainty to set
	 */
	public void setCoordinateUncertaintyMeters(Integer coordinateUncertainty) {
		this.coordinateUncertainty = coordinateUncertainty;
	}
	/**
	 * <p>Getter for the field <code>confidence</code>.</p>
	 *
	 * @return the confidence
	 */
	public Integer getConfidence() {
		return confidence;
	}
	/**
	 * <p>Setter for the field <code>confidence</code>.</p>
	 *
	 * @param confidence the confidence to set
	 */
	public void setConfidence(Integer confidence) {
		this.confidence = confidence;
	}
	/**
	 * <p>Getter for the field <code>parseString</code>.</p>
	 *
	 * @return the parseString
	 */
	public String getParseString() {
		return parseString;
	}
	/**
	 * <p>Setter for the field <code>parseString</code>.</p>
	 *
	 * @param parseString the parseString to set
	 */
	public void setParseString(String parseString) {
		this.parseString = parseString;
	}


}
