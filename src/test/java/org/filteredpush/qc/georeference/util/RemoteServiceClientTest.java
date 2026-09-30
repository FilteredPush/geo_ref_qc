/**
 * RemoteServiceClientTest.java
 */
package org.filteredpush.qc.georeference.util;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

/**
 * Tests of RemoteServiceClient, run against a local HTTP server.
 *
 * @author mole
 *
 */
public class RemoteServiceClientTest {

	private static final Log logger = LogFactory.getLog(RemoteServiceClientTest.class);

	private HttpServer server;
	private ExecutorService serverExecutor;
	private String base;
	/** Number of requests received by the test server. */
	private AtomicInteger requestCount;
	/** Status codes to return for successive requests, 200 once exhausted. */
	private ConcurrentLinkedQueue<Integer> statuses;
	/** Retry-After header to send with error responses, null for none. */
	private volatile String retryAfter;
	/** Time for the server to take to respond, in milliseconds. */
	private volatile long responseDelayMillis;
	/** Requests currently being handled, and the most handled at once. */
	private AtomicInteger concurrent;
	private AtomicInteger maxConcurrent;

	/**
	 * Start a local server.
	 *
	 * @throws IOException if the server can not be started.
	 */
	@Before
	public void setUp() throws IOException {
		requestCount = new AtomicInteger(0);
		statuses = new ConcurrentLinkedQueue<Integer>();
		retryAfter = null;
		responseDelayMillis = 0L;
		concurrent = new AtomicInteger(0);
		maxConcurrent = new AtomicInteger(0);
		server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
		serverExecutor = Executors.newFixedThreadPool(16);
		server.setExecutor(serverExecutor);
		server.createContext("/", exchange -> handle(exchange));
		server.start();
		base = "http://127.0.0.1:" + server.getAddress().getPort() + "/";
	}

	/**
	 * Stop the local server.
	 */
	@After
	public void tearDown() {
		server.stop(0);
		serverExecutor.shutdownNow();
	}

	private void handle(HttpExchange exchange) throws IOException {
		requestCount.incrementAndGet();
		int now = concurrent.incrementAndGet();
		maxConcurrent.accumulateAndGet(now, Math::max);
		try {
			if (responseDelayMillis > 0L) {
				try {
					Thread.sleep(responseDelayMillis);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
			Integer next = statuses.poll();
			int status = (next==null) ? 200 : next;
			String body = (status==200) ? "ok " + exchange.getRequestURI().getPath() : "error " + status;
			if (status!=200 && retryAfter!=null) {
				exchange.getResponseHeaders().add("Retry-After", retryAfter);
			}
			byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
			exchange.sendResponseHeaders(status, bytes.length);
			try (OutputStream os = exchange.getResponseBody()) {
				os.write(bytes);
			}
		} finally {
			concurrent.decrementAndGet();
		}
	}

	/**
	 * @return a configuration with short delays, so that tests run quickly.
	 */
	private static RemoteServiceConfig fastConfig() {
		return new RemoteServiceConfig()
				.setBackoffBaseMillis(1L)
				.setBackoffMaxMillis(5L)
				.setMinRequestIntervalMillis(0L);
	}

	private static String text(byte[] body) {
		return new String(body, StandardCharsets.UTF_8);
	}

	/**
	 * Test a successful request, and that a repeated request is answered from the cache.
	 *
	 * @throws IOException on an unexpected failure.
	 */
	@Test
	public void testGetAndCache() throws IOException {
		RemoteServiceClient client = new RemoteServiceClient("Test", fastConfig());
		assertEquals("ok /a", text(client.get(base + "a")));
		assertEquals(1, requestCount.get());
		assertEquals("ok /a", text(client.get(base + "a")));
		assertEquals(1, requestCount.get());
		assertEquals(1L, client.getCacheHits());
		assertEquals(1L, client.getRequestsSent());
		assertEquals("ok /b", text(client.get(base + "b")));
		assertEquals(2, requestCount.get());
		assertEquals(2, client.getCacheSize());

		client.evict(base + "a");
		assertEquals("ok /a", text(client.get(base + "a")));
		assertEquals(3, requestCount.get());

		client.reset();
		assertEquals(0, client.getCacheSize());
	}

	/**
	 * Test that the response cache is bounded, evicting the least recently used response, and can be disabled.
	 *
	 * @throws IOException on an unexpected failure.
	 */
	@Test
	public void testCacheSize() throws IOException {
		RemoteServiceClient client = new RemoteServiceClient("Test", fastConfig().setCacheSize(2));
		client.get(base + "a");
		client.get(base + "b");
		client.get(base + "a");  // a is now more recently used than b
		client.get(base + "c");  // evicts b
		assertEquals(3, requestCount.get());
		assertEquals(2, client.getCacheSize());
		client.get(base + "a");
		assertEquals(3, requestCount.get());
		client.get(base + "b");
		assertEquals(4, requestCount.get());

		RemoteServiceClient uncached = new RemoteServiceClient("Test", fastConfig().setCacheSize(0));
		uncached.get(base + "a");
		uncached.get(base + "a");
		assertEquals(6, requestCount.get());
		assertEquals(0, uncached.getCacheSize());
	}

	/**
	 * Test that transient failures are retried, and the request succeeds when the service recovers.
	 *
	 * @throws IOException on an unexpected failure.
	 */
	@Test
	public void testRetryTransientFailures() throws IOException {
		statuses.add(503);
		statuses.add(500);
		statuses.add(429);
		RemoteServiceClient client = new RemoteServiceClient("Test", fastConfig());
		assertEquals("ok /a", text(client.get(base + "a")));
		assertEquals(4, requestCount.get());
		assertFalse(client.isCircuitOpen());
	}

	/**
	 * Test that a persistent transient failure is retried maxRetries times, then fails with the status code.
	 */
	@Test
	public void testGiveUpAfterMaxRetries() {
		for (int i=0; i<10; i++) {
			statuses.add(503);
		}
		RemoteServiceClient client = new RemoteServiceClient("Test", fastConfig().setMaxRetries(2));
		try {
			client.get(base + "a");
			fail("Expected an HttpStatusException");
		} catch (HttpStatusException e) {
			logger.debug(e.getMessage());
			assertEquals(503, e.getStatusCode());
			assertEquals(base + "a", e.getUrl());
			assertTrue(e.getMessage().contains("HTTP 503"));
			assertTrue(e.getMessage().contains("error 503"));
		} catch (IOException e) {
			fail("Unexpected exception " + e);
		}
		assertEquals(3, requestCount.get());
	}

	/**
	 * Test that non-transient HTTP errors are not retried.
	 */
	@Test
	public void testNoRetryOnClientError() {
		for (int status : new int[] { 400, 403, 404 }) {
			int before = requestCount.get();
			statuses.add(status);
			RemoteServiceClient client = new RemoteServiceClient("Test", fastConfig());
			try {
				client.get(base + "a" + status);
				fail("Expected an HttpStatusException");
			} catch (HttpStatusException e) {
				assertEquals(status, e.getStatusCode());
			} catch (IOException e) {
				fail("Unexpected exception " + e);
			}
			assertEquals(before + 1, requestCount.get());
			assertFalse(client.isCircuitOpen());
		}
	}

	/**
	 * Test that a Retry-After is waited for, and that a Retry-After longer than the maximum is not.
	 *
	 * @throws IOException on an unexpected failure.
	 */
	@Test
	public void testRetryAfter() throws IOException {
		statuses.add(429);
		retryAfter = "1";
		RemoteServiceClient client = new RemoteServiceClient("Test", fastConfig());
		long start = System.nanoTime();
		assertEquals("ok /a", text(client.get(base + "a")));
		long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
		assertTrue("Waited only " + elapsedMillis + " ms", elapsedMillis >= 900L);
		assertEquals(2, requestCount.get());

		statuses.add(503);
		retryAfter = "120";
		try {
			client.get(base + "b");
			fail("Expected an HttpStatusException");
		} catch (HttpStatusException e) {
			assertEquals("120", e.getRetryAfter());
		}
		assertEquals(3, requestCount.get());
	}

	/**
	 * Test that a connection failure is retried, and reported as an IOException.
	 *
	 * @throws IOException if a free port can not be found.
	 */
	@Test
	public void testConnectionFailure() throws IOException {
		int port;
		try (ServerSocket socket = new ServerSocket(0)) {
			port = socket.getLocalPort();
		}
		RemoteServiceClient client = new RemoteServiceClient("Test", fastConfig().setMaxRetries(2).setConnectTimeoutMillis(2000));
		try {
			client.get("http://127.0.0.1:" + port + "/a");
			fail("Expected a ConnectException");
		} catch (ConnectException e) {
			logger.debug(e.getMessage());
		}
		assertEquals(3L, client.getRequestsSent());
	}

	/**
	 * Test that a malformed URL is not retried, and not remembered as a failure.
	 */
	@Test
	public void testMalformedUrl() {
		RemoteServiceClient client = new RemoteServiceClient("Test", fastConfig());
		for (int i=0; i<2; i++) {
			try {
				client.get("not a url");
				fail("Expected a MalformedURLException");
			} catch (MalformedURLException e) {
				logger.debug(e.getMessage());
			} catch (IOException e) {
				fail("Unexpected exception " + e);
			}
		}
		assertEquals(0L, client.getRequestsSent());
	}

	/**
	 * Test that a failed request is remembered, and not resent until the failure has expired.
	 *
	 * @throws Exception on an unexpected failure.
	 */
	@Test
	public void testFailureRemembered() throws Exception {
		statuses.add(503);
		RemoteServiceClient client = new RemoteServiceClient("Test", fastConfig().setMaxRetries(0).setFailureCacheMillis(300L));
		try {
			client.get(base + "a");
			fail("Expected an HttpStatusException");
		} catch (HttpStatusException e) {
			assertEquals(503, e.getStatusCode());
		}
		assertEquals(1, requestCount.get());
		try {
			client.get(base + "a");
			fail("Expected a ServiceUnavailableException");
		} catch (ServiceUnavailableException e) {
			logger.debug(e.getMessage());
			assertTrue(e.getCause() instanceof HttpStatusException);
		}
		assertEquals(1, requestCount.get());
		// a different request is sent
		assertEquals("ok /b", text(client.get(base + "b")));
		assertEquals(2, requestCount.get());
		// after the failure expires the request is sent again
		Thread.sleep(400L);
		assertEquals("ok /a", text(client.get(base + "a")));
		assertEquals(3, requestCount.get());

		// failures not remembered if configured not to
		statuses.add(503);
		RemoteServiceClient forgetful = new RemoteServiceClient("Test", fastConfig().setMaxRetries(0).setFailureCacheMillis(0L));
		try {
			forgetful.get(base + "c");
			fail("Expected an HttpStatusException");
		} catch (HttpStatusException e) {
			assertEquals(503, e.getStatusCode());
		}
		assertEquals("ok /c", text(forgetful.get(base + "c")));
		assertEquals(5, requestCount.get());
	}

	/**
	 * Test that the circuit breaker trips after consecutive failures, fails requests without
	 * sending them while open, and closes after a successful trial request.
	 *
	 * @throws Exception on an unexpected failure.
	 */
	@Test
	public void testCircuitBreaker() throws Exception {
		for (int i=0; i<3; i++) {
			statuses.add(503);
		}
		RemoteServiceClient client = new RemoteServiceClient("Test", fastConfig().setMaxRetries(0)
				.setFailureCacheMillis(0L).setCircuitBreakerThreshold(3).setCircuitBreakerOpenMillis(300L));
		for (int i=0; i<3; i++) {
			assertFalse(client.isCircuitOpen());
			try {
				client.get(base + "fail" + i);
				fail("Expected an HttpStatusException");
			} catch (HttpStatusException e) {
				assertEquals(503, e.getStatusCode());
			}
		}
		assertEquals(3, requestCount.get());
		assertTrue(client.isCircuitOpen());
		try {
			client.get(base + "a");
			fail("Expected a ServiceUnavailableException");
		} catch (ServiceUnavailableException e) {
			logger.debug(e.getMessage());
		}
		assertEquals(3, requestCount.get());

		// after the open period, a trial request is sent, which succeeds, closing the circuit
		Thread.sleep(400L);
		assertFalse(client.isCircuitOpen());
		assertEquals("ok /a", text(client.get(base + "a")));
		assertEquals(4, requestCount.get());
		assertEquals("ok /b", text(client.get(base + "b")));
		assertEquals(5, requestCount.get());
	}

	/**
	 * Test that a failed trial request trips the circuit breaker again immediately.
	 *
	 * @throws Exception on an unexpected failure.
	 */
	@Test
	public void testCircuitBreakerTrialFails() throws Exception {
		for (int i=0; i<3; i++) {
			statuses.add(503);
		}
		RemoteServiceClient client = new RemoteServiceClient("Test", fastConfig().setMaxRetries(0)
				.setFailureCacheMillis(0L).setCircuitBreakerThreshold(2).setCircuitBreakerOpenMillis(300L));
		for (int i=0; i<2; i++) {
			try {
				client.get(base + "fail" + i);
				fail("Expected an HttpStatusException");
			} catch (HttpStatusException e) {
				assertEquals(503, e.getStatusCode());
			}
		}
		assertTrue(client.isCircuitOpen());
		Thread.sleep(400L);
		try {
			client.get(base + "trial");
			fail("Expected an HttpStatusException");
		} catch (HttpStatusException e) {
			assertEquals(503, e.getStatusCode());
		}
		assertEquals(3, requestCount.get());
		assertTrue(client.isCircuitOpen());

		// reset closes the circuit
		client.reset();
		assertFalse(client.isCircuitOpen());
		assertEquals("ok /a", text(client.get(base + "a")));
	}

	/**
	 * Test that client errors (e.g. 404) do not count towards tripping the circuit breaker.
	 */
	@Test
	public void testCircuitBreakerIgnoresClientErrors() {
		for (int i=0; i<5; i++) {
			statuses.add(404);
		}
		RemoteServiceClient client = new RemoteServiceClient("Test", fastConfig().setFailureCacheMillis(0L).setCircuitBreakerThreshold(2));
		for (int i=0; i<5; i++) {
			try {
				client.get(base + "missing" + i);
				fail("Expected an HttpStatusException");
			} catch (HttpStatusException e) {
				assertEquals(404, e.getStatusCode());
			} catch (IOException e) {
				fail("Unexpected exception " + e);
			}
		}
		assertFalse(client.isCircuitOpen());
	}

	/**
	 * Test that no more than the maximum number of concurrent requests are made.
	 *
	 * @throws Exception on an unexpected failure.
	 */
	@Test
	public void testConcurrencyLimit() throws Exception {
		responseDelayMillis = 100L;
		final RemoteServiceClient client = new RemoteServiceClient("Test", fastConfig().setMaxConcurrentRequests(2));
		ExecutorService executor = Executors.newFixedThreadPool(8);
		try {
			List<Callable<String>> tasks = new ArrayList<Callable<String>>();
			for (int i=0; i<8; i++) {
				final int n = i;
				tasks.add(() -> text(client.get(base + "c" + n)));
			}
			List<Future<String>> results = executor.invokeAll(tasks, 60, TimeUnit.SECONDS);
			for (int i=0; i<8; i++) {
				assertEquals("ok /c" + i, results.get(i).get());
			}
		} finally {
			executor.shutdownNow();
		}
		assertEquals(8, requestCount.get());
		assertTrue("Max concurrent was " + maxConcurrent.get(), maxConcurrent.get() <= 2);
	}

	/**
	 * Test that requests are spaced by at least the minimum interval.
	 *
	 * @throws IOException on an unexpected failure.
	 */
	@Test
	public void testMinRequestInterval() throws IOException {
		RemoteServiceClient client = new RemoteServiceClient("Test", fastConfig().setMinRequestIntervalMillis(100L));
		long start = System.nanoTime();
		for (int i=0; i<4; i++) {
			client.get(base + "i" + i);
		}
		long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
		assertTrue("Four requests took only " + elapsedMillis + " ms", elapsedMillis >= 290L);
		assertEquals(4, requestCount.get());
	}

	/**
	 * Test that concurrent requests for the same URL result in a single request.
	 *
	 * @throws Exception on an unexpected failure.
	 */
	@Test
	public void testConcurrentSameUrlSingleRequest() throws Exception {
		responseDelayMillis = 300L;
		final RemoteServiceClient client = new RemoteServiceClient("Test", fastConfig());
		final CountDownLatch ready = new CountDownLatch(1);
		ExecutorService executor = Executors.newFixedThreadPool(8);
		try {
			List<Future<String>> results = new ArrayList<Future<String>>();
			for (int i=0; i<8; i++) {
				results.add(executor.submit(() -> {
					ready.await();
					return text(client.get(base + "same"));
				}));
			}
			ready.countDown();
			for (Future<String> result : results) {
				assertEquals("ok /same", result.get(60, TimeUnit.SECONDS));
			}
		} finally {
			executor.shutdownNow();
		}
		assertEquals(1, requestCount.get());
	}

	/**
	 * Test that waiting too long for a turn to make a request fails without sending the
	 * request, without tripping the circuit breaker or being remembered as a failure.
	 *
	 * @throws Exception on an unexpected failure.
	 */
	@Test
	public void testAcquireTimeout() throws Exception {
		responseDelayMillis = 500L;
		final RemoteServiceClient client = new RemoteServiceClient("Test", fastConfig().setMaxConcurrentRequests(1)
				.setAcquireTimeoutMillis(50L).setCircuitBreakerThreshold(1));
		ExecutorService executor = Executors.newSingleThreadExecutor();
		try {
			Future<String> slow = executor.submit(() -> text(client.get(base + "slow")));
			// wait for the slow request to reach the server
			long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
			while (requestCount.get()==0 && System.nanoTime() < deadline) {
				Thread.sleep(5L);
			}
			try {
				client.get(base + "blocked");
				fail("Expected a ServiceUnavailableException");
			} catch (ServiceUnavailableException e) {
				logger.debug(e.getMessage());
			}
			assertEquals("ok /slow", slow.get(10, TimeUnit.SECONDS));
		} finally {
			executor.shutdownNow();
		}
		assertFalse(client.isCircuitOpen());
		// not remembered as a failure
		responseDelayMillis = 0L;
		assertEquals("ok /blocked", text(client.get(base + "blocked")));
	}

	/**
	 * Test classification of retryable status codes and failures.
	 */
	@Test
	public void testIsRetryable() {
		for (int status : new int[] { 408, 429, 500, 502, 503, 504 }) {
			assertTrue(RemoteServiceClient.isRetryableStatus(status));
			assertTrue(RemoteServiceClient.isRetryable(new HttpStatusException("", status, null, "")));
		}
		for (int status : new int[] { 200, 301, 400, 401, 403, 404, 410, 501 }) {
			assertFalse(RemoteServiceClient.isRetryableStatus(status));
			assertFalse(RemoteServiceClient.isRetryable(new HttpStatusException("", status, null, "")));
		}
		assertTrue(RemoteServiceClient.isRetryable(new ConnectException()));
		assertTrue(RemoteServiceClient.isRetryable(new java.net.SocketTimeoutException()));
		assertTrue(RemoteServiceClient.isRetryable(new IOException()));
		assertFalse(RemoteServiceClient.isRetryable(new MalformedURLException()));
		assertFalse(RemoteServiceClient.isRetryable(new ServiceUnavailableException("")));
		assertFalse(RemoteServiceClient.isRetryable(new java.io.InterruptedIOException()));
	}

	/**
	 * Test the exponential backoff computation, including its cap, jitter bounds, and overflow.
	 */
	@Test
	public void testComputeBackoffMillis() {
		assertEquals(250L, RemoteServiceClient.computeBackoffMillis(0, 500L, 8000L, 0d));
		assertEquals(500L, RemoteServiceClient.computeBackoffMillis(0, 500L, 8000L, 1d));
		assertEquals(500L, RemoteServiceClient.computeBackoffMillis(1, 500L, 8000L, 0d));
		assertEquals(1000L, RemoteServiceClient.computeBackoffMillis(1, 500L, 8000L, 1d));
		// capped
		assertEquals(8000L, RemoteServiceClient.computeBackoffMillis(10, 500L, 8000L, 1d));
		assertEquals(4000L, RemoteServiceClient.computeBackoffMillis(10, 500L, 8000L, 0d));
		// large attempt numbers do not overflow
		assertEquals(8000L, RemoteServiceClient.computeBackoffMillis(100, 500L, 8000L, 1d));
		// exponent is limited to 2^30
		assertEquals(3L << 30, RemoteServiceClient.computeBackoffMillis(62, 3L, Long.MAX_VALUE, 1d));
		// overflow of the shift is capped at the maximum
		assertEquals(Long.MAX_VALUE, RemoteServiceClient.computeBackoffMillis(2, Long.MAX_VALUE / 2L, Long.MAX_VALUE, 1d));
		// jitter out of range is bounded
		assertEquals(500L, RemoteServiceClient.computeBackoffMillis(0, 500L, 8000L, 5d));
		assertEquals(250L, RemoteServiceClient.computeBackoffMillis(0, 500L, 8000L, -1d));
		// no backoff
		assertEquals(0L, RemoteServiceClient.computeBackoffMillis(3, 0L, 8000L, 0.5d));
		assertEquals(0L, RemoteServiceClient.computeBackoffMillis(3, 500L, 0L, 0.5d));
	}

	/**
	 * Test parsing of Retry-After values, as seconds and as HTTP dates.
	 */
	@Test
	public void testParseRetryAfterMillis() {
		long now = 1000000000000L;
		assertEquals(RemoteServiceClient.NO_RETRY_AFTER, RemoteServiceClient.parseRetryAfterMillis(null, now));
		assertEquals(RemoteServiceClient.NO_RETRY_AFTER, RemoteServiceClient.parseRetryAfterMillis("", now));
		assertEquals(RemoteServiceClient.NO_RETRY_AFTER, RemoteServiceClient.parseRetryAfterMillis("soon", now));
		assertEquals(0L, RemoteServiceClient.parseRetryAfterMillis("0", now));
		assertEquals(5000L, RemoteServiceClient.parseRetryAfterMillis(" 5 ", now));
		assertEquals(Long.MAX_VALUE, RemoteServiceClient.parseRetryAfterMillis("99999999999999999999", now));
		// now is 2001-09-09T01:46:40Z
		assertEquals(20000L, RemoteServiceClient.parseRetryAfterMillis("Sun, 09 Sep 2001 01:47:00 GMT", now));
		// dates in the past mean no wait
		assertEquals(0L, RemoteServiceClient.parseRetryAfterMillis("Sun, 09 Sep 2001 01:00:00 GMT", now));
	}

	/**
	 * Test the delay before a retry, combining backoff and Retry-After.
	 */
	@Test
	public void testDelayBeforeRetryMillis() {
		RemoteServiceClient client = new RemoteServiceClient("Test", new RemoteServiceConfig()
				.setBackoffBaseMillis(500L).setBackoffMaxMillis(8000L).setMaxRetryAfterMillis(30000L));
		assertEquals(500L, client.delayBeforeRetryMillis(0, RemoteServiceClient.NO_RETRY_AFTER, 1d));
		// Retry-After longer than backoff
		assertEquals(10000L, client.delayBeforeRetryMillis(0, 10000L, 1d));
		// backoff longer than Retry-After
		assertEquals(500L, client.delayBeforeRetryMillis(0, 100L, 1d));
		// Retry-After too long
		assertEquals(-1L, client.delayBeforeRetryMillis(0, 30001L, 1d));
	}

	/**
	 * Test configuration defaults, minimums, copying, and system properties.
	 */
	@Test
	public void testConfig() {
		RemoteServiceConfig config = new RemoteServiceConfig();
		assertEquals(2, config.getMaxConcurrentRequests());
		assertEquals(3, config.getMaxRetries());
		assertTrue(config.getUserAgent().startsWith("FilteredPush-geo_ref_qc/"));
		assertTrue(config.getUserAgent().contains(RemoteServiceConfig.PROJECT_URL));

		config.setMaxConcurrentRequests(0).setMaxRetries(-1).setConnectTimeoutMillis(0).setReadTimeoutMillis(-5)
			.setAcquireTimeoutMillis(0L).setCacheSize(-1).setMinRequestIntervalMillis(-1L).setUserAgent("  ");
		assertEquals(1, config.getMaxConcurrentRequests());
		assertEquals(0, config.getMaxRetries());
		assertEquals(1, config.getConnectTimeoutMillis());
		assertEquals(1, config.getReadTimeoutMillis());
		assertEquals(1L, config.getAcquireTimeoutMillis());
		assertEquals(0, config.getCacheSize());
		assertEquals(0L, config.getMinRequestIntervalMillis());
		assertEquals(RemoteServiceConfig.defaultUserAgent(), config.getUserAgent());

		// the client takes a copy of the configuration
		RemoteServiceClient client = new RemoteServiceClient("Test", config.setMaxRetries(4));
		config.setMaxRetries(7);
		assertEquals(4, client.getConfig().getMaxRetries());
		client.getConfig().setMaxRetries(9);
		assertEquals(4, client.getConfig().getMaxRetries());
		assertEquals("Test", client.getServiceName());

		String retriesProperty = RemoteServiceConfig.PROPERTY_PREFIX + "maxRetries";
		String agentProperty = RemoteServiceConfig.PROPERTY_PREFIX + "userAgent";
		String concurrentProperty = RemoteServiceConfig.PROPERTY_PREFIX + "maxConcurrentRequests";
		try {
			System.setProperty(retriesProperty, "5");
			System.setProperty(agentProperty, "TestAgent/1.0");
			System.setProperty(concurrentProperty, "not a number");
			RemoteServiceConfig fromProperties = RemoteServiceConfig.fromSystemProperties();
			assertEquals(5, fromProperties.getMaxRetries());
			assertEquals("TestAgent/1.0", fromProperties.getUserAgent());
			// unparsable, default used
			assertEquals(2, fromProperties.getMaxConcurrentRequests());
		} finally {
			System.clearProperty(retriesProperty);
			System.clearProperty(agentProperty);
			System.clearProperty(concurrentProperty);
		}
	}

}
