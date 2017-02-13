/** GeolocationAlternative.java
 * 
 * Copyright 2015 President and Fellows of Harvard College
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.filteredpush.kuration.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Structure to hold transpositions and sign changes of latitude and longitude values 
 * along with metadata about potential matching against GIS data.
 * 
 * @author mole
 *
 */
public class GeolocationAlternative {
	
	private double latitude;
	private double longitude;
	private String alternative;
	private boolean matched;
	private String comment;
	
	public static List<GeolocationAlternative> constructListOfAlternatives(double latitude, double longitude) { 
		ArrayList<GeolocationAlternative> result = new ArrayList<GeolocationAlternative>();
		result.add(new GeolocationAlternative(latitude, longitude, "Original"));
		if (Math.abs(longitude)<=90) {
			// Transposition of latitude and longitude would be in range
		    result.add(new GeolocationAlternative(longitude, latitude, "Transposed"));
		    result.add(new GeolocationAlternative(longitude, -latitude, "Transposed, latitude sign changed"));
		    result.add(new GeolocationAlternative(-longitude, latitude, "Transposed, longitude sign changed"));
		    result.add(new GeolocationAlternative(-longitude, -latitude, "Transposed and signs changed"));
		}
		result.add(new GeolocationAlternative(-latitude, longitude, "Latitude sign changed"));
		result.add(new GeolocationAlternative(latitude, -longitude, "Longitude sign changed"));
		result.add(new GeolocationAlternative(-latitude, longitude, "Signs changed"));
		if (Math.abs(latitude)<10 || Math.abs(longitude)<10) {
			// Latitude and longitude are small, might be a decimal point error
		    result.add(new GeolocationAlternative(latitude*10d, longitude*10d, "Multiplied by 10"));
		}
		if (Math.abs(latitude)>90 || Math.abs(longitude)>180) {
			// Latitude and longitude are large, might be a decimal point error
		    result.add(new GeolocationAlternative(latitude/10d, longitude/10d, "Divided by 10"));
		}
		
		return result;
	}
	
	/**
	 * Construct a geolocation alternative specifying the latitude, longitude, and which
	 * variation (transposition, sign change, etc.) is represented by this alternative.
	 * 
	 * @param latitude
	 * @param longitude
	 * @param whichAlternative
	 */
	GeolocationAlternative(double latitude, double longitude, String whichAlternative) { 
		this.latitude = latitude;
		this.longitude = longitude;
		this.alternative = whichAlternative;
		matched = false;
		comment = null;
	}

	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the alternative
	 */
	public String getAlternative() {
		return alternative;
	}

	/**
	 * @param alternative the alternative to set
	 */
	public void setAlternative(String alternative) {
		this.alternative = alternative;
	}

	/**
	 * @return the matched
	 */
	public boolean isMatched() {
		return matched;
	}

	/**
	 * @param matched the matched to set
	 */
	public void setMatched(boolean matched) {
		this.matched = matched;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
	
}
