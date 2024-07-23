/**
 * TransformationStruct.java
 */
package org.filteredpush.qc.georeference.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>TransformationStruct class.</p>
 *
 * @author mole
 *
 * Structure to hold transformations of coordinates.
 * @version $Id: $Id
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
			       
	/**
	 * <p>Constructor for TransformationStruct.</p>
	 */
	public TransformationStruct() { 
		success = false;
		comments = "";
	}
	
	/**
	 * <p>Constructor for TransformationStruct.</p>
	 *
	 * @param decimalLatitude a double.
	 * @param decimalLongitude a double.
	 * @param precision a int.
	 * @param geodeticDatum a {@link java.lang.String} object.
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
	 * <p>isSuccess.</p>
	 *
	 * @return the success
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * <p>Setter for the field <code>success</code>.</p>
	 *
	 * @param success the success to set
	 */
	public void setSuccess(boolean success) {
		this.success = success;
	}

	/**
	 * <p>Getter for the field <code>decimalLatitude</code>.</p>
	 *
	 * @return the decimalLatitude
	 */
	public double getDecimalLatitude() {
		return decimalLatitude;
	}

	/**
	 * <p>Setter for the field <code>decimalLatitude</code>.</p>
	 *
	 * @param decimalLatitude the decimalLatitude to set
	 */
	public void setDecimalLatitude(double decimalLatitude) {
		this.decimalLatitude = decimalLatitude;
	}

	/**
	 * <p>Getter for the field <code>decimalLongitude</code>.</p>
	 *
	 * @return the decimalLongitude
	 */
	public double getDecimalLongitude() {
		return decimalLongitude;
	}
	
	/**
	 * <p>getDecimalLongitudeString.</p>
	 *
	 * @return the decimalLongitude as a string to the specified precision
	 */
	public String getDecimalLongitudeString() {
		// TODO: Mask to specified precision.
		return Double.toString(decimalLongitude);
	}
	
	/**
	 * <p>getDecimalLatitudeString.</p>
	 *
	 * @return the decimalLatitude as a string to the specified precision
	 */
	public String getDecimalLatitudeString() {
		// TODO: Mask to specified precision.
		return Double.toString(decimalLatitude);
	}	
	

	/**
	 * <p>Setter for the field <code>decimalLongitude</code>.</p>
	 *
	 * @param decimalLongitude the decimalLongitude to set
	 */
	public void setDecimalLongitude(double decimalLongitude) {
		this.decimalLongitude = decimalLongitude;
	}

	/**
	 * <p>Getter for the field <code>precision</code>.</p>
	 *
	 * @return the precision
	 */
	public int getPrecision() {
		return precision;
	}

	/**
	 * <p>Setter for the field <code>precision</code>.</p>
	 *
	 * @param precision the precision to set
	 */
	public void setPrecision(int precision) {
		this.precision = precision;
	}

	/**
	 * <p>Getter for the field <code>geodeticDatum</code>.</p>
	 *
	 * @return the geodeticDatum
	 */
	public String getGeodeticDatum() {
		return geodeticDatum;
	}

	/**
	 * <p>Setter for the field <code>geodeticDatum</code>.</p>
	 *
	 * @param geodeticDatum the geodeticDatum to set
	 */
	public void setGeodeticDatum(String geodeticDatum) {
		this.geodeticDatum = geodeticDatum;
	}

	/**
	 * <p>Getter for the field <code>comments</code>.</p>
	 *
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * <p>Setter for the field <code>comments</code>.</p>
	 *
	 * @param comments the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	/**
	 * <p>appendComments.</p>
	 *
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
	 * <p>Getter for the field <code>uncertainty</code>.</p>
	 *
	 * @return the uncertainty
	 */
	public double getUncertainty() {
		return uncertainty;
	}

	/**
	 * <p>Setter for the field <code>uncertainty</code>.</p>
	 *
	 * @param uncertainty the uncertainty to set
	 */
	public void setUncertainty(double uncertainty) {
		this.uncertainty = uncertainty;
	}
}
