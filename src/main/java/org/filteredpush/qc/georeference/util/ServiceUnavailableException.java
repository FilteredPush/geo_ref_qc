/**
 * ServiceUnavailableException.java
 */
package org.filteredpush.qc.georeference.util;

import java.io.IOException;

/**
 * Thrown by {@link RemoteServiceClient} when a request is not sent to a remote service, 
 * because the circuit breaker for the service is open after repeated failures, because the 
 * same request failed recently, or because no turn to make the request became available in time.
 * 
 * @author mole
 */
public class ServiceUnavailableException extends IOException {

	private static final long serialVersionUID = 4263707915722587404L;

	/**
	 * Constructor.
	 * 
	 * @param message description of why the request was not sent.
	 */
	public ServiceUnavailableException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 * 
	 * @param message description of why the request was not sent.
	 * @param cause the earlier failure that led to the request not being sent.
	 */
	public ServiceUnavailableException(String message, Throwable cause) {
		super(message, cause);
	}

}
