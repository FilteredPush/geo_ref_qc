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
 * @version $Id: $Id
 */
public class GeorefServiceException extends Exception {

	private static final Log logger = LogFactory.getLog(GeorefServiceException.class);

	/**
	 * <p>Constructor for GeorefServiceException.</p>
	 */
	public GeorefServiceException() {
	}

	/**
	 * <p>Constructor for GeorefServiceException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 */
	public GeorefServiceException(String message) {
		super(message);
	}

	/**
	 * <p>Constructor for GeorefServiceException.</p>
	 *
	 * @param cause a {@link java.lang.Throwable} object.
	 */
	public GeorefServiceException(Throwable cause) {
		super(cause);
	}

	/**
	 * <p>Constructor for GeorefServiceException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 * @param cause a {@link java.lang.Throwable} object.
	 */
	public GeorefServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * <p>Constructor for GeorefServiceException.</p>
	 *
	 * @param message a {@link java.lang.String} object.
	 * @param cause a {@link java.lang.Throwable} object.
	 * @param enableSuppression a boolean.
	 * @param writableStackTrace a boolean.
	 */
	public GeorefServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
