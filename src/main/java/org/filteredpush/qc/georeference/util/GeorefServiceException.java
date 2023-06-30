/**
 * GeorefServiceException.java
 */
package org.filteredpush.qc.georeference.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Exception for failures on using services by georeference utility classes.
 * 
 * @author mole
 *
 */
public class GeorefServiceException extends Exception {

	private static final Log logger = LogFactory.getLog(GeorefServiceException.class);

	/**
	 * 
	 */
	public GeorefServiceException() {
	}

	/**
	 * @param message
	 */
	public GeorefServiceException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public GeorefServiceException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public GeorefServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public GeorefServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
