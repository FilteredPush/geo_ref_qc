/**
 * HttpStatusException.java
 */
package org.filteredpush.qc.georeference.util;

import java.io.IOException;

/**
 * Thrown by {@link RemoteServiceClient} when a remote service responds with an HTTP status 
 * other than success, carrying the status code and any Retry-After header.
 * 
 * @author mole
 */
public class HttpStatusException extends IOException {

	private static final long serialVersionUID = -5064939217916315164L;
	
	private final int statusCode;
	private final String retryAfter;
	private final String url;

	/**
	 * Constructor.
	 * 
	 * @param message description of the failure.
	 * @param statusCode the HTTP status code returned.
	 * @param retryAfter the value of the Retry-After header, or null if none.
	 * @param url the URL requested.
	 */
	public HttpStatusException(String message, int statusCode, String retryAfter, String url) {
		super(message);
		this.statusCode = statusCode;
		this.retryAfter = retryAfter;
		this.url = url;
	}

	/**
	 * @return the HTTP status code returned by the service.
	 */
	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * @return the value of the Retry-After header returned by the service, or null if none.
	 */
	public String getRetryAfter() {
		return retryAfter;
	}

	/**
	 * @return the URL that was requested.
	 */
	public String getUrl() {
		return url;
	}

}
