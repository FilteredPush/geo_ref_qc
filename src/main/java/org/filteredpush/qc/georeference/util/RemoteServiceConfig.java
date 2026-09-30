/**
 * RemoteServiceConfig.java
 */
package org.filteredpush.qc.georeference.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Settings for a {@link RemoteServiceClient}, controlling timeouts, the rate and concurrency
 * of requests, retries, caching, and the circuit breaker.
 *
 * <p>Settings can be provided as java system properties with the prefix geo_ref_qc. (e.g.
 * <code>-Dgeo_ref_qc.maxRetries=5</code>), read by {@link #fromSystemProperties()}, or set with
 * the fluent setters on an instance.  Values below the minimum for a setting are raised to
 * that minimum.</p>
 * <table>
 * <caption>Settings</caption>
 * <tr><th>System property</th><th>Default</th><th>Meaning</th></tr>
 * <tr><td>geo_ref_qc.userAgent</td><td>FilteredPush-geo_ref_qc/{version} (+https://github.com/FilteredPush/geo_ref_qc)</td>
 *     <td>User-Agent sent with each request.</td></tr>
 * <tr><td>geo_ref_qc.connectTimeoutMillis</td><td>10000</td><td>Connect timeout.</td></tr>
 * <tr><td>geo_ref_qc.readTimeoutMillis</td><td>30000</td><td>Read timeout.</td></tr>
 * <tr><td>geo_ref_qc.maxConcurrentRequests</td><td>2</td><td>Maximum concurrent requests to the service.</td></tr>
 * <tr><td>geo_ref_qc.minRequestIntervalMillis</td><td>100</td><td>Minimum interval between the start of requests, 0 for none.</td></tr>
 * <tr><td>geo_ref_qc.acquireTimeoutMillis</td><td>60000</td><td>Longest wait for a turn to make a request before failing.</td></tr>
 * <tr><td>geo_ref_qc.maxRetries</td><td>3</td><td>Retries after a transient failure (HTTP 408, 429, 500, 502, 503, 504,
 *     or a connection failure), total attempts are maxRetries+1.</td></tr>
 * <tr><td>geo_ref_qc.backoffBaseMillis</td><td>500</td><td>Base delay for exponential backoff between retries.</td></tr>
 * <tr><td>geo_ref_qc.backoffMaxMillis</td><td>8000</td><td>Maximum delay for exponential backoff between retries.</td></tr>
 * <tr><td>geo_ref_qc.maxRetryAfterMillis</td><td>30000</td><td>Longest Retry-After that will be honored, a longer
 *     Retry-After fails without retrying.</td></tr>
 * <tr><td>geo_ref_qc.cacheSize</td><td>5000</td><td>Maximum number of successful responses cached, 0 disables the cache.</td></tr>
 * <tr><td>geo_ref_qc.failureCacheMillis</td><td>60000</td><td>How long a failed request is remembered, during which the
 *     same request fails without being sent, 0 to not remember failures.</td></tr>
 * <tr><td>geo_ref_qc.circuitBreakerThreshold</td><td>5</td><td>Number of consecutive failed requests after which requests
 *     fail without being sent, 0 to disable the circuit breaker.</td></tr>
 * <tr><td>geo_ref_qc.circuitBreakerOpenMillis</td><td>60000</td><td>How long requests fail without being sent once the
 *     circuit breaker has tripped, after which a single trial request is allowed.</td></tr>
 * </table>
 *
 * @author mole
 */
public class RemoteServiceConfig {

	private static final Log logger = LogFactory.getLog(RemoteServiceConfig.class);

	/** Prefix for the system properties read by {@link #fromSystemProperties()}. */
	public static final String PROPERTY_PREFIX = "geo_ref_qc.";

	/** Project URL included in the default User-Agent. */
	public static final String PROJECT_URL = "https://github.com/FilteredPush/geo_ref_qc";

	private String userAgent = defaultUserAgent();
	private int connectTimeoutMillis = 10000;
	private int readTimeoutMillis = 30000;
	private int maxConcurrentRequests = 2;
	private long minRequestIntervalMillis = 100L;
	private long acquireTimeoutMillis = 60000L;
	private int maxRetries = 3;
	private long backoffBaseMillis = 500L;
	private long backoffMaxMillis = 8000L;
	private long maxRetryAfterMillis = 30000L;
	private int cacheSize = 5000;
	private long failureCacheMillis = 60000L;
	private int circuitBreakerThreshold = 5;
	private long circuitBreakerOpenMillis = 60000L;

	/**
	 * Create a configuration with the default settings, ignoring any system properties.
	 */
	public RemoteServiceConfig() {
	}

	/**
	 * Create a configuration with the default settings, overridden by any geo_ref_qc.*
	 * system properties.
	 *
	 * @return a new configuration.
	 */
	public static RemoteServiceConfig fromSystemProperties() {
		RemoteServiceConfig result = new RemoteServiceConfig();
		String ua = System.getProperty(PROPERTY_PREFIX + "userAgent");
		if (ua!=null && ua.trim().length()>0) {
			result.setUserAgent(ua);
		}
		result.setConnectTimeoutMillis((int) readLong("connectTimeoutMillis", result.connectTimeoutMillis));
		result.setReadTimeoutMillis((int) readLong("readTimeoutMillis", result.readTimeoutMillis));
		result.setMaxConcurrentRequests((int) readLong("maxConcurrentRequests", result.maxConcurrentRequests));
		result.setMinRequestIntervalMillis(readLong("minRequestIntervalMillis", result.minRequestIntervalMillis));
		result.setAcquireTimeoutMillis(readLong("acquireTimeoutMillis", result.acquireTimeoutMillis));
		result.setMaxRetries((int) readLong("maxRetries", result.maxRetries));
		result.setBackoffBaseMillis(readLong("backoffBaseMillis", result.backoffBaseMillis));
		result.setBackoffMaxMillis(readLong("backoffMaxMillis", result.backoffMaxMillis));
		result.setMaxRetryAfterMillis(readLong("maxRetryAfterMillis", result.maxRetryAfterMillis));
		result.setCacheSize((int) readLong("cacheSize", result.cacheSize));
		result.setFailureCacheMillis(readLong("failureCacheMillis", result.failureCacheMillis));
		result.setCircuitBreakerThreshold((int) readLong("circuitBreakerThreshold", result.circuitBreakerThreshold));
		result.setCircuitBreakerOpenMillis(readLong("circuitBreakerOpenMillis", result.circuitBreakerOpenMillis));
		return result;
	}

	/**
	 * Read a numeric system property.
	 *
	 * @param name the property name, without the geo_ref_qc. prefix.
	 * @param defaultValue value to return if the property is not set or is not a number.
	 * @return the value of the property, or defaultValue.
	 */
	private static long readLong(String name, long defaultValue) {
		String value = System.getProperty(PROPERTY_PREFIX + name);
		if (value==null || value.trim().length()==0) {
			return defaultValue;
		}
		try {
			return Long.parseLong(value.trim());
		} catch (NumberFormatException e) {
			logger.warn("Unable to parse value [" + value + "] for " + PROPERTY_PREFIX + name + ", using default " + defaultValue);
			return defaultValue;
		}
	}

	/**
	 * Build the default User-Agent, identifying this library, its version, and its project URL.
	 *
	 * @return the default User-Agent.
	 */
	public static String defaultUserAgent() {
		Package p = RemoteServiceConfig.class.getPackage();
		String version = (p==null) ? null : p.getImplementationVersion();
		if (version==null) {
			version = "unknown";
		}
		return "FilteredPush-geo_ref_qc/" + version + " (+" + PROJECT_URL + ")";
	}

	/** @return the User-Agent sent with each request. */
	public String getUserAgent() { return userAgent; }
	/**
	 * @param userAgent the User-Agent to send, if null or blank the default is used.
	 * @return this configuration.
	 */
	public RemoteServiceConfig setUserAgent(String userAgent) {
		this.userAgent = (userAgent==null || userAgent.trim().length()==0) ? defaultUserAgent() : userAgent.trim();
		return this;
	}

	/** @return the connect timeout in milliseconds. */
	public int getConnectTimeoutMillis() { return connectTimeoutMillis; }
	/**
	 * @param millis the connect timeout in milliseconds, at least 1.
	 * @return this configuration.
	 */
	public RemoteServiceConfig setConnectTimeoutMillis(int millis) { this.connectTimeoutMillis = Math.max(1, millis); return this; }

	/** @return the read timeout in milliseconds. */
	public int getReadTimeoutMillis() { return readTimeoutMillis; }
	/**
	 * @param millis the read timeout in milliseconds, at least 1.
	 * @return this configuration.
	 */
	public RemoteServiceConfig setReadTimeoutMillis(int millis) { this.readTimeoutMillis = Math.max(1, millis); return this; }

	/** @return the maximum number of concurrent requests. */
	public int getMaxConcurrentRequests() { return maxConcurrentRequests; }
	/**
	 * @param max the maximum number of concurrent requests, at least 1.
	 * @return this configuration.
	 */
	public RemoteServiceConfig setMaxConcurrentRequests(int max) { this.maxConcurrentRequests = Math.max(1, max); return this; }

	/** @return the minimum interval between the start of requests in milliseconds. */
	public long getMinRequestIntervalMillis() { return minRequestIntervalMillis; }
	/**
	 * @param millis the minimum interval between the start of requests in milliseconds, 0 for none.
	 * @return this configuration.
	 */
	public RemoteServiceConfig setMinRequestIntervalMillis(long millis) { this.minRequestIntervalMillis = Math.max(0L, millis); return this; }

	/** @return the longest wait for a turn to make a request, in milliseconds. */
	public long getAcquireTimeoutMillis() { return acquireTimeoutMillis; }
	/**
	 * @param millis the longest wait for a turn to make a request, in milliseconds, at least 1.
	 * @return this configuration.
	 */
	public RemoteServiceConfig setAcquireTimeoutMillis(long millis) { this.acquireTimeoutMillis = Math.max(1L, millis); return this; }

	/** @return the maximum number of retries after a transient failure. */
	public int getMaxRetries() { return maxRetries; }
	/**
	 * @param retries the maximum number of retries after a transient failure, 0 for none.
	 * @return this configuration.
	 */
	public RemoteServiceConfig setMaxRetries(int retries) { this.maxRetries = Math.max(0, retries); return this; }

	/** @return the base delay for exponential backoff in milliseconds. */
	public long getBackoffBaseMillis() { return backoffBaseMillis; }
	/**
	 * @param millis the base delay for exponential backoff in milliseconds.
	 * @return this configuration.
	 */
	public RemoteServiceConfig setBackoffBaseMillis(long millis) { this.backoffBaseMillis = Math.max(0L, millis); return this; }

	/** @return the maximum delay for exponential backoff in milliseconds. */
	public long getBackoffMaxMillis() { return backoffMaxMillis; }
	/**
	 * @param millis the maximum delay for exponential backoff in milliseconds.
	 * @return this configuration.
	 */
	public RemoteServiceConfig setBackoffMaxMillis(long millis) { this.backoffMaxMillis = Math.max(0L, millis); return this; }

	/** @return the longest Retry-After, in milliseconds, that will be honored. */
	public long getMaxRetryAfterMillis() { return maxRetryAfterMillis; }
	/**
	 * @param millis the longest Retry-After, in milliseconds, that will be honored.
	 * @return this configuration.
	 */
	public RemoteServiceConfig setMaxRetryAfterMillis(long millis) { this.maxRetryAfterMillis = Math.max(0L, millis); return this; }

	/** @return the maximum number of successful responses cached. */
	public int getCacheSize() { return cacheSize; }
	/**
	 * @param size the maximum number of successful responses cached, 0 disables the cache.
	 * @return this configuration.
	 */
	public RemoteServiceConfig setCacheSize(int size) { this.cacheSize = Math.max(0, size); return this; }

	/** @return how long a failed request is remembered, in milliseconds. */
	public long getFailureCacheMillis() { return failureCacheMillis; }
	/**
	 * @param millis how long a failed request is remembered, in milliseconds, 0 to not remember failures.
	 * @return this configuration.
	 */
	public RemoteServiceConfig setFailureCacheMillis(long millis) { this.failureCacheMillis = Math.max(0L, millis); return this; }

	/** @return the number of consecutive failed requests that trips the circuit breaker. */
	public int getCircuitBreakerThreshold() { return circuitBreakerThreshold; }
	/**
	 * @param threshold the number of consecutive failed requests that trips the circuit breaker, 0 to disable it.
	 * @return this configuration.
	 */
	public RemoteServiceConfig setCircuitBreakerThreshold(int threshold) { this.circuitBreakerThreshold = Math.max(0, threshold); return this; }

	/** @return how long, in milliseconds, requests fail without being sent once the circuit breaker has tripped. */
	public long getCircuitBreakerOpenMillis() { return circuitBreakerOpenMillis; }
	/**
	 * @param millis how long, in milliseconds, requests fail without being sent once the circuit breaker has tripped.
	 * @return this configuration.
	 */
	public RemoteServiceConfig setCircuitBreakerOpenMillis(long millis) { this.circuitBreakerOpenMillis = Math.max(0L, millis); return this; }

}
