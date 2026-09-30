/**
 * RemoteServiceClient.java
 */
package org.filteredpush.qc.georeference.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Client for making HTTP GET requests to a remote service politely, when used from many threads.
 * One instance should be shared by all callers of a service.
 *
 * <ul>
 * <li>Sets connect and read timeouts, and a User-Agent identifying this library.</li>
 * <li>Limits the number of concurrent requests, and the minimum interval between the start of requests,
 * so that many concurrent callers produce a throttled stream of requests rather than a burst.</li>
 * <li>Retries plausibly transient failures (HTTP 408, 429, 500, 502, 503, 504, and connection failures)
 * with exponential backoff and jitter, honoring Retry-After, does not retry other failures.</li>
 * <li>Caches successful responses (including responses that report no matches), so a repeated
 * request is not resent.</li>
 * <li>Remembers failed requests for a period, during which the same request fails without being resent.</li>
 * <li>Sends only one request at a time for a given URL, concurrent callers requesting the same URL
 * share the result.</li>
 * <li>Has a circuit breaker, after a number of consecutive failed requests, requests fail
 * without being sent for a period, after which a single trial request is allowed, if it
 * succeeds requests resume, if it fails the circuit breaker trips again.</li>
 * </ul>
 *
 * <p>Requests that are not sent fail with a {@link ServiceUnavailableException}, HTTP error statuses
 * fail with an {@link HttpStatusException}, both are IOExceptions.</p>
 *
 * @author mole
 */
public class RemoteServiceClient {

	private static final Log logger = LogFactory.getLog(RemoteServiceClient.class);

	/** Maximum number of characters of an error response body included in a message. */
	private static final int MAX_BODY_LENGTH = 300;

	/** Value returned by {@link #parseRetryAfterMillis(String, long)} when there is no usable Retry-After. */
	public static final long NO_RETRY_AFTER = -1L;

	private final String serviceName;
	private final RemoteServiceConfig config;
	private final Semaphore permits;
	private final long minIntervalNanos;
	/** Earliest time the next request may start, guarded by this. */
	private long nextStartNanos;

	/** Least recently used cache of successful responses, keyed by URL, guarded by itself. */
	private final LinkedHashMap<String,byte[]> responseCache = new LinkedHashMap<String,byte[]>(16, 0.75f, true);
	/** Recently failed requests, keyed by URL. */
	private final Map<String,FailureRecord> failureCache = new ConcurrentHashMap<String,FailureRecord>();
	/** Requests in progress, keyed by URL. */
	private final Map<String,CompletableFuture<byte[]>> inFlight = new ConcurrentHashMap<String,CompletableFuture<byte[]>>();

	/** Consecutive failed requests, guarded by this. */
	private int consecutiveFailures;
	/** Time until which the circuit breaker is open, 0 if closed, guarded by this. */
	private long circuitOpenUntilNanos;
	/** True while a trial request is being made after the circuit breaker has been open, guarded by this. */
	private boolean trialInProgress;

	private final AtomicLong requestsSent = new AtomicLong();
	private final AtomicLong cacheHits = new AtomicLong();

	/**
	 * A remembered failure of a request.
	 */
	private static final class FailureRecord {
		private final IOException failure;
		private final long expiresNanos;
		FailureRecord(IOException failure, long expiresNanos) {
			this.failure = failure;
			this.expiresNanos = expiresNanos;
		}
	}

	/**
	 * Constructor.
	 *
	 * @param serviceName name of the service, used in log and exception messages, e.g. Getty TGN.
	 * @param config the settings to use, copied values are not affected by later changes to config.
	 */
	public RemoteServiceClient(String serviceName, RemoteServiceConfig config) {
		this.serviceName = serviceName;
		this.config = copy(config);
		this.permits = new Semaphore(this.config.getMaxConcurrentRequests(), true);
		this.minIntervalNanos = TimeUnit.MILLISECONDS.toNanos(this.config.getMinRequestIntervalMillis());
		this.nextStartNanos = System.nanoTime();
	}

	private static RemoteServiceConfig copy(RemoteServiceConfig config) {
		return new RemoteServiceConfig()
				.setUserAgent(config.getUserAgent())
				.setConnectTimeoutMillis(config.getConnectTimeoutMillis())
				.setReadTimeoutMillis(config.getReadTimeoutMillis())
				.setMaxConcurrentRequests(config.getMaxConcurrentRequests())
				.setMinRequestIntervalMillis(config.getMinRequestIntervalMillis())
				.setAcquireTimeoutMillis(config.getAcquireTimeoutMillis())
				.setMaxRetries(config.getMaxRetries())
				.setBackoffBaseMillis(config.getBackoffBaseMillis())
				.setBackoffMaxMillis(config.getBackoffMaxMillis())
				.setMaxRetryAfterMillis(config.getMaxRetryAfterMillis())
				.setCacheSize(config.getCacheSize())
				.setFailureCacheMillis(config.getFailureCacheMillis())
				.setCircuitBreakerThreshold(config.getCircuitBreakerThreshold())
				.setCircuitBreakerOpenMillis(config.getCircuitBreakerOpenMillis());
	}

	/**
	 * @return the settings used by this client (a copy, changes do not affect this client).
	 */
	public RemoteServiceConfig getConfig() {
		return copy(config);
	}

	/**
	 * @return the name of the service this client makes requests to.
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * Make an HTTP GET request, or return a cached response.
	 *
	 * @param url the URL to request.
	 * @return the body of a successful response, callers must not modify the returned array.
	 * @throws ServiceUnavailableException if the request was not sent, because the circuit breaker
	 *   is open, because the same request failed recently, or because no turn to make the request
	 *   became available in time.
	 * @throws HttpStatusException if the service responded with an error status (after any retries).
	 * @throws IOException on another failure to communicate with the service (after any retries),
	 *   including a MalformedURLException if url is not a valid URL.
	 */
	public byte[] get(String url) throws IOException {
		byte[] cached = getCachedResponse(url);
		if (cached!=null) {
			cacheHits.incrementAndGet();
			return cached;
		}
		FailureRecord failure = failureCache.get(url);
		if (failure!=null) {
			if (System.nanoTime() - failure.expiresNanos < 0L) {
				throw new ServiceUnavailableException(serviceName + " request failed recently, not resending " + url
						+ " yet: " + failure.failure.getMessage(), failure.failure);
			}
			failureCache.remove(url, failure);
		}
		CompletableFuture<byte[]> mine = new CompletableFuture<byte[]>();
		CompletableFuture<byte[]> existing = inFlight.putIfAbsent(url, mine);
		if (existing!=null) {
			// another thread is making this request, share its result
			return await(existing);
		}
		try {
			byte[] result = fetchWithRetries(url);
			putCachedResponse(url, result);
			mine.complete(result);
			return result;
		} catch (IOException e) {
			rememberFailure(url, e);
			mine.completeExceptionally(e);
			throw e;
		} catch (RuntimeException e) {
			mine.completeExceptionally(e);
			throw e;
		} finally {
			inFlight.remove(url, mine);
		}
	}

	/**
	 * Remove a response from the cache, e.g. when a cached response could not be interpreted.
	 *
	 * @param url the URL of the response to remove.
	 */
	public void evict(String url) {
		synchronized (responseCache) {
			responseCache.remove(url);
		}
	}

	/**
	 * Clear the response and failure caches and reset the circuit breaker.
	 */
	public void reset() {
		synchronized (responseCache) {
			responseCache.clear();
		}
		failureCache.clear();
		synchronized (this) {
			consecutiveFailures = 0;
			circuitOpenUntilNanos = 0L;
			trialInProgress = false;
		}
	}

	/**
	 * @return true if the circuit breaker is open, so that requests will fail without being sent.
	 */
	public synchronized boolean isCircuitOpen() {
		return circuitOpenUntilNanos!=0L && System.nanoTime() - circuitOpenUntilNanos < 0L;
	}

	/**
	 * @return the number of HTTP requests sent to the service, including retries.
	 */
	public long getRequestsSent() {
		return requestsSent.get();
	}

	/**
	 * @return the number of calls to {@link #get(String)} answered from the response cache.
	 */
	public long getCacheHits() {
		return cacheHits.get();
	}

	/**
	 * @return the number of responses in the response cache.
	 */
	public int getCacheSize() {
		synchronized (responseCache) {
			return responseCache.size();
		}
	}

	private byte[] getCachedResponse(String url) {
		if (config.getCacheSize()==0) {
			return null;
		}
		synchronized (responseCache) {
			return responseCache.get(url);
		}
	}

	private void putCachedResponse(String url, byte[] response) {
		int max = config.getCacheSize();
		if (max==0) {
			return;
		}
		synchronized (responseCache) {
			responseCache.put(url, response);
			Iterator<String> i = responseCache.keySet().iterator();
			while (responseCache.size() > max && i.hasNext()) {
				i.next();
				i.remove();
			}
		}
	}

	/**
	 * Remember a failed request, so that it is not resent for a period.  Requests that were not
	 * sent, or that could not be sent (e.g. a malformed URL), are not remembered.
	 */
	private void rememberFailure(String url, IOException e) {
		if (config.getFailureCacheMillis()==0L || e instanceof ServiceUnavailableException
				|| e instanceof MalformedURLException || isInterruption(e)) {
			return;
		}
		failureCache.put(url, new FailureRecord(e, System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(config.getFailureCacheMillis())));
	}

	private static byte[] await(CompletableFuture<byte[]> future) throws IOException {
		try {
			return future.get();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new InterruptedIOException("Interrupted while waiting for a response");
		} catch (ExecutionException e) {
			Throwable cause = e.getCause();
			if (cause instanceof IOException) {
				throw (IOException) cause;
			}
			if (cause instanceof RuntimeException) {
				throw (RuntimeException) cause;
			}
			throw new IOException(cause);
		}
	}

	/**
	 * Make a request, retrying transient failures, subject to the circuit breaker.
	 */
	private byte[] fetchWithRetries(String url) throws IOException {
		boolean trial = checkCircuit(url);
		boolean resolved = false;
		try {
			for (int attempt=0; ; attempt++) {
				try {
					byte[] result = fetchOnce(url);
					recordSuccess();
					resolved = true;
					return result;
				} catch (IOException e) {
					if (!isRetryable(e)) {
						if (e instanceof HttpStatusException) {
							// the service is responding, a non-transient error is not a service failure
							recordSuccess();
							resolved = true;
						}
						logger.error(serviceName + " request failed, not retrying: " + e.getMessage());
						throw e;
					}
					String attempts = " (attempt " + (attempt+1) + " of " + (config.getMaxRetries()+1) + ")";
					if (attempt >= config.getMaxRetries()) {
						recordFailure();
						resolved = true;
						logger.error(serviceName + " request failed, giving up" + attempts + ": " + e.getMessage());
						throw e;
					}
					long retryAfter = (e instanceof HttpStatusException) ?
							parseRetryAfterMillis(((HttpStatusException)e).getRetryAfter(), System.currentTimeMillis()) : NO_RETRY_AFTER;
					long delay = delayBeforeRetryMillis(attempt, retryAfter, ThreadLocalRandom.current().nextDouble());
					if (delay < 0L) {
						recordFailure();
						resolved = true;
						logger.error(serviceName + " request failed, Retry-After exceeds maximum wait of "
								+ config.getMaxRetryAfterMillis() + " ms, not retrying: " + e.getMessage());
						throw e;
					}
					logger.warn(serviceName + " request failed" + attempts + ", retrying in " + delay + " ms: " + e.getMessage());
					try {
						Thread.sleep(delay);
					} catch (InterruptedException ie) {
						Thread.currentThread().interrupt();
						throw new InterruptedIOException("Interrupted while waiting to retry " + url);
					}
				}
			}
		} finally {
			if (trial && !resolved) {
				releaseTrial();
			}
		}
	}

	/**
	 * Check the circuit breaker before making a request.
	 *
	 * @return true if this request is a trial request after the circuit breaker has been open.
	 * @throws ServiceUnavailableException if the circuit breaker is open, or a trial request is in progress.
	 */
	private synchronized boolean checkCircuit(String url) throws ServiceUnavailableException {
		if (circuitOpenUntilNanos==0L) {
			return false;
		}
		long remaining = circuitOpenUntilNanos - System.nanoTime();
		if (remaining > 0L) {
			throw new ServiceUnavailableException(serviceName + " unavailable after " + consecutiveFailures
					+ " consecutive failed requests, not sending requests for another "
					+ TimeUnit.NANOSECONDS.toMillis(remaining) + " ms, not sent: " + url);
		}
		if (trialInProgress) {
			throw new ServiceUnavailableException(serviceName + " unavailable after " + consecutiveFailures
					+ " consecutive failed requests, waiting for the result of a trial request, not sent: " + url);
		}
		trialInProgress = true;
		return true;
	}

	private synchronized void recordSuccess() {
		if (circuitOpenUntilNanos!=0L) {
			logger.info(serviceName + " responding again, resuming requests");
		}
		consecutiveFailures = 0;
		circuitOpenUntilNanos = 0L;
		trialInProgress = false;
	}

	private synchronized void recordFailure() {
		consecutiveFailures++;
		trialInProgress = false;
		int threshold = config.getCircuitBreakerThreshold();
		if (threshold > 0 && consecutiveFailures >= threshold) {
			circuitOpenUntilNanos = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(config.getCircuitBreakerOpenMillis());
			if (circuitOpenUntilNanos==0L) {
				circuitOpenUntilNanos = 1L;
			}
			logger.error(serviceName + " unavailable after " + consecutiveFailures + " consecutive failed requests, not sending requests for "
					+ config.getCircuitBreakerOpenMillis() + " ms");
		}
	}

	private synchronized void releaseTrial() {
		trialInProgress = false;
	}

	/**
	 * Make a single request, waiting for a turn to make it.
	 */
	private byte[] fetchOnce(String url) throws IOException {
		boolean acquired;
		try {
			acquired = permits.tryAcquire(config.getAcquireTimeoutMillis(), TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new InterruptedIOException("Interrupted while waiting to make request to " + url);
		}
		if (!acquired) {
			throw new ServiceUnavailableException("Timed out after " + config.getAcquireTimeoutMillis()
					+ " ms waiting for a turn to make a request to " + serviceName + ", not sent: " + url);
		}
		try {
			waitForStartSlot(url);
			HttpURLConnection connection = openConnection(url);
			requestsSent.incrementAndGet();
			int status = connection.getResponseCode();
			if (status >= 200 && status < 300) {
				try (InputStream is = connection.getInputStream()) {
					return readFully(is);
				}
			}
			String body = null;
			try (InputStream es = connection.getErrorStream()) {
				if (es!=null) {
					body = new String(readFully(es), StandardCharsets.UTF_8).replaceAll("\\s+", " ").trim();
					if (body.length() > MAX_BODY_LENGTH) {
						body = body.substring(0, MAX_BODY_LENGTH) + "...";
					}
				}
			} catch (IOException e) {
				logger.debug(e.getMessage());
			}
			String retryAfter = connection.getHeaderField("Retry-After");
			StringBuilder message = new StringBuilder(serviceName).append(" HTTP ").append(status);
			message.append(" from ").append(url);
			if (retryAfter!=null) {
				message.append("; Retry-After: ").append(retryAfter);
			}
			if (body!=null && body.length()>0) {
				message.append("; Response body: ").append(body);
			}
			throw new HttpStatusException(message.toString(), status, retryAfter, url);
		} finally {
			permits.release();
		}
	}

	/**
	 * Open a connection with the configured timeouts and User-Agent.
	 *
	 * @param url the URL to request.
	 * @return an HttpURLConnection, not yet connected.
	 * @throws IOException on an error opening the connection, including a MalformedURLException.
	 */
	HttpURLConnection openConnection(String url) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setConnectTimeout(config.getConnectTimeoutMillis());
		connection.setReadTimeout(config.getReadTimeoutMillis());
		connection.setRequestProperty("User-Agent", config.getUserAgent());
		return connection;
	}

	private void waitForStartSlot(String url) throws InterruptedIOException {
		if (minIntervalNanos <= 0L) {
			return;
		}
		long waitNanos;
		synchronized (this) {
			long now = System.nanoTime();
			long start = (nextStartNanos - now > 0L) ? nextStartNanos : now;
			nextStartNanos = start + minIntervalNanos;
			waitNanos = start - now;
		}
		if (waitNanos > 0L) {
			try {
				TimeUnit.NANOSECONDS.sleep(waitNanos);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new InterruptedIOException("Interrupted while waiting to make request to " + url);
			}
		}
	}

	private static byte[] readFully(InputStream is) throws IOException {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[8192];
		int read;
		while ((read = is.read(buffer)) != -1) {
			result.write(buffer, 0, read);
		}
		return result.toByteArray();
	}

	private static boolean isInterruption(IOException e) {
		return e instanceof InterruptedIOException && !(e instanceof SocketTimeoutException);
	}

	/**
	 * Is a failure plausibly transient, and so worth retrying.
	 *
	 * @param e the failure.
	 * @return true for HTTP 408, 429, 500, 502, 503, 504, and for connection level failures, false
	 *   for other HTTP statuses, malformed URLs, interruptions, and requests that were not sent.
	 */
	public static boolean isRetryable(IOException e) {
		if (e instanceof HttpStatusException) {
			return isRetryableStatus(((HttpStatusException) e).getStatusCode());
		}
		if (e instanceof ServiceUnavailableException || e instanceof MalformedURLException || isInterruption(e)) {
			return false;
		}
		return true;
	}

	/**
	 * Is an HTTP status code one which indicates a plausibly transient failure worth retrying.
	 *
	 * @param statusCode the HTTP status code.
	 * @return true for 408, 429, 500, 502, 503, and 504, otherwise false.
	 */
	public static boolean isRetryableStatus(int statusCode) {
		switch (statusCode) {
		case 408:
		case 429:
		case 500:
		case 502:
		case 503:
		case 504:
			return true;
		default:
			return false;
		}
	}

	/**
	 * Compute an exponential backoff delay with jitter.  The exponential delay for an attempt is
	 * baseMillis * 2^attempt, capped at maxMillis, the returned delay is between half of that
	 * delay and that delay, depending on the jitter value.
	 *
	 * @param attempt zero based index of the retry (0 for the first retry).
	 * @param baseMillis the base delay in milliseconds.
	 * @param maxMillis the maximum delay in milliseconds.
	 * @param jitter a value in the range [0,1), typically random.
	 * @return the delay in milliseconds before making the retry.
	 */
	public static long computeBackoffMillis(int attempt, long baseMillis, long maxMillis, double jitter) {
		if (baseMillis <= 0L || maxMillis <= 0L) {
			return 0L;
		}
		int shift = Math.max(0, Math.min(attempt, 30));
		long exponential = baseMillis << shift;
		if (exponential <= 0L || exponential > maxMillis || (exponential >> shift) != baseMillis) {
			// capped, or overflowed
			exponential = maxMillis;
		}
		double boundedJitter = Math.max(0d, Math.min(jitter, 1d));
		long half = exponential / 2L;
		return half + (long) ((exponential - half) * boundedJitter);
	}

	/**
	 * Parse the value of a Retry-After header, which may be either a number of seconds or an HTTP date.
	 *
	 * @param value the value of the Retry-After header, may be null.
	 * @param nowMillis the current time in milliseconds since the epoch.
	 * @return the number of milliseconds to wait, or {@link #NO_RETRY_AFTER} if value is null or
	 *   not parsable.
	 */
	public static long parseRetryAfterMillis(String value, long nowMillis) {
		if (value==null || value.trim().length()==0) {
			return NO_RETRY_AFTER;
		}
		String trimmed = value.trim();
		if (trimmed.matches("^[0-9]+$")) {
			try {
				long seconds = Long.parseLong(trimmed);
				if (seconds > Long.MAX_VALUE / 1000L) {
					return Long.MAX_VALUE;
				}
				return seconds * 1000L;
			} catch (NumberFormatException e) {
				return Long.MAX_VALUE;
			}
		}
		try {
			ZonedDateTime date = ZonedDateTime.parse(trimmed, DateTimeFormatter.RFC_1123_DATE_TIME);
			return Math.max(0L, date.toInstant().toEpochMilli() - nowMillis);
		} catch (DateTimeParseException e) {
			return NO_RETRY_AFTER;
		}
	}

	/**
	 * Determine the delay before the next retry, the larger of the exponential backoff and
	 * any Retry-After requested by the service.
	 *
	 * @param attempt zero based index of the retry (0 for the first retry).
	 * @param retryAfterMillis the delay requested by the service, or {@link #NO_RETRY_AFTER}.
	 * @param jitter a value in the range [0,1), typically random.
	 * @return the delay in milliseconds, or -1 if the service requested a delay longer than
	 *   the configured maximum Retry-After, in which case the request should not be retried.
	 */
	long delayBeforeRetryMillis(int attempt, long retryAfterMillis, double jitter) {
		long backoff = computeBackoffMillis(attempt, config.getBackoffBaseMillis(), config.getBackoffMaxMillis(), jitter);
		if (retryAfterMillis == NO_RETRY_AFTER) {
			return backoff;
		}
		if (retryAfterMillis > config.getMaxRetryAfterMillis()) {
			return -1L;
		}
		return Math.max(backoff, retryAfterMillis);
	}

}
