/**
 * 
 */
package org.filteredpush.qc.georeference.util;

/**
 * Representation of degrees with a specified precision
 * to some number of decimal places.
 *
 * @author mole
 * @version $Id: $Id
 */
public class DegreeWithPrecision {

	private Double degrees;
	private Integer decimalPlaces;
	
	/**
	 * <p>Constructor for DegreeWithPrecision.</p>
	 *
	 * @param degrees a {@link java.lang.Double} object.
	 * @param decimalPlaces a int.
	 */
	public DegreeWithPrecision(Double degrees, int decimalPlaces) {
		this.degrees = degrees;
		this.decimalPlaces = decimalPlaces;
	}

	/**
	 * <p>Getter for the field <code>degrees</code>.</p>
	 *
	 * @return the degrees
	 */
	public Double getDegrees() {
		return degrees;
	}

	/**
	 * <p>Setter for the field <code>degrees</code>.</p>
	 *
	 * @param degrees the degrees to set
	 */
	public void setDegrees(Double degrees) {
		this.degrees = degrees;
	}

	/**
	 * <p>Getter for the field <code>decimalPlaces</code>.</p>
	 *
	 * @return the decimalPlaces
	 */
	public int getDecimalPlaces() {
		return decimalPlaces;
	}

	/**
	 * <p>Setter for the field <code>decimalPlaces</code>.</p>
	 *
	 * @param decimalPlaces the decimalPlaces to set
	 */
	public void setDecimalPlaces(int decimalPlaces) {
		this.decimalPlaces = decimalPlaces;
	}
	
	/**
	 * <p>toString.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String toString() { 
		if (degrees==null) { 
			return "";
		} else { 
			if (decimalPlaces==null) { 
				return Double.toString(degrees);
			} else { 
				String format = "%1." + decimalPlaces + "f";
				return String.format(format, degrees);
			}
		}
	}
	
}
