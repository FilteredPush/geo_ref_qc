/**
 * TransformationStruct.java
 */
package org.filteredpush.qc.georeference.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author mole
 * 
 * Structure to hold transformations of coordinates.
 *
 */
public class TransformationStruct {

	private static final Log logger = LogFactory.getLog(TransformationStruct.class);
	
	private boolean success = false;
	private double decimalLatitude;
	private double decimalLongitude;
	private int precision;
	private String geodeticDatum;
	private String comments;
	private double uncertainty;
			       
	public TransformationStruct() { 
		success = false;
		comments = "";
	}
	
	/**
	 * @param decimalLatitude
	 * @param decimalLongitude
	 * @param precision
	 * @param geodeticDatum
	 */
	public TransformationStruct(double decimalLatitude, double decimalLongitude, int precision, String geodeticDatum) {
		super();
		success = false;
		comments = "";
		this.decimalLatitude = decimalLatitude;
		this.decimalLongitude = decimalLongitude;
		this.precision = precision;
		this.geodeticDatum = geodeticDatum;
	}

	/**
	 * @return the success
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * @param success the success to set
	 */
	public void setSuccess(boolean success) {
		this.success = success;
	}

	/**
	 * @return the decimalLatitude
	 */
	public double getDecimalLatitude() {
		return decimalLatitude;
	}

	/**
	 * @param decimalLatitude the decimalLatitude to set
	 */
	public void setDecimalLatitude(double decimalLatitude) {
		this.decimalLatitude = decimalLatitude;
	}

	/**
	 * @return the decimalLongitude
	 */
	public double getDecimalLongitude() {
		return decimalLongitude;
	}
	
	/**
	 * @return the decimalLongitude as a string to the specified precision
	 */
	public String getDecimalLongitudeString() {
		// TODO: Mask to specified precision.
		return Double.toString(decimalLongitude);
	}
	
	/**
	 * @return the decimalLatitude as a string to the specified precision
	 */
	public String getDecimalLatitudeString() {
		// TODO: Mask to specified precision.
		return Double.toString(decimalLatitude);
	}	
	

	/**
	 * @param decimalLongitude the decimalLongitude to set
	 */
	public void setDecimalLongitude(double decimalLongitude) {
		this.decimalLongitude = decimalLongitude;
	}

	/**
	 * @return the precision
	 */
	public int getPrecision() {
		return precision;
	}

	/**
	 * @param precision the precision to set
	 */
	public void setPrecision(int precision) {
		this.precision = precision;
	}

	/**
	 * @return the geodeticDatum
	 */
	public String getGeodeticDatum() {
		return geodeticDatum;
	}

	/**
	 * @param geodeticDatum the geodeticDatum to set
	 */
	public void setGeodeticDatum(String geodeticDatum) {
		this.geodeticDatum = geodeticDatum;
	}

	/**
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * @param comments the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	/**
	 * @param comment string to append to the end of comments
	 */
	public void appendComments(String comment) {
		if (comments==null) { 
			comments = comment;
		} else  {
		   comments = comments + comments;
		}
	}

	/**
	 * @return the uncertainty
	 */
	public double getUncertainty() {
		return uncertainty;
	}

	/**
	 * @param uncertainty the uncertainty to set
	 */
	public void setUncertainty(double uncertainty) {
		this.uncertainty = uncertainty;
	}
}
