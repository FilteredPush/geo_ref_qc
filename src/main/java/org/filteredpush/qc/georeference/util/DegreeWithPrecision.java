/**
 * 
 */
package org.filteredpush.qc.georeference.util;

/**
 * Representation of degrees with a specified precision
 * to some number of decimal places.
 * 
 * @author mole
 *
 */
public class DegreeWithPrecision {

	private Double degrees;
	private Integer decimalPlaces;
	
	/**
	 * @param degrees
	 * @param decimalPlaces
	 */
	public DegreeWithPrecision(Double degrees, int decimalPlaces) {
		this.degrees = degrees;
		this.decimalPlaces = decimalPlaces;
	}

	/**
	 * @return the degrees
	 */
	public Double getDegrees() {
		return degrees;
	}

	/**
	 * @param degrees the degrees to set
	 */
	public void setDegrees(Double degrees) {
		this.degrees = degrees;
	}

	/**
	 * @return the decimalPlaces
	 */
	public int getDecimalPlaces() {
		return decimalPlaces;
	}

	/**
	 * @param decimalPlaces the decimalPlaces to set
	 */
	public void setDecimalPlaces(int decimalPlaces) {
		this.decimalPlaces = decimalPlaces;
	}
	
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
