/**
 * GettyLookupTest.java
 */
package org.filteredpush.qc.georeference.util;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datakurator.ffdq.api.DQResponse;
import org.datakurator.ffdq.api.result.ComplianceValue;
import org.datakurator.ffdq.model.ResultState;
import org.filteredpush.qc.georeference.DwCGeoRefDQ;
import org.filteredpush.qc.georeference.SourceAuthorityException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import edu.getty.tgn.objects.Vocabulary;
import edu.getty.tgn.service.GettyTGNObject;
import jakarta.xml.bind.JAXBException;

/**
 * Tests of the handling of requests to the Getty TGN by GettyLookup, run against a local
 * HTTP server standing in for the Getty TGN web services, so no network access is needed.
 *
 * @author mole
 *
 */
public class GettyLookupTest {

	private static final Log logger = LogFactory.getLog(GettyLookupTest.class);

	private static final String NATION_PLACE_TYPE = "81011";
	private static final String PRIMARY_PLACE_TYPE = "81100";

	private HttpServer server;
	/** Number of requests received by the test server. */
	private AtomicInteger requestCount;
	/** User-Agent headers received by the test server. */
	private List<String> userAgents;
	/** Responses to TGNGetTermMatch keyed by placetypeid|name, name without enclosing quotes. */
	private Map<String,String> termMatchResponses;
	/** Other responses keyed by path, e.g. /TGNService.asmx/TGNGetParents */
	private Map<String,String> pathResponses;
	/** HTTP status to return for all requests, 200 for normal behavior. */
	private volatile int statusOverride;
	/** The Getty client in use before the test, restored after the test. */
	private RemoteServiceClient originalClient;

	/**
	 * Start a local server standing in for the Getty TGN and point GettyLookup at it.
	 *
	 * @throws IOException if the server can not be started.
	 */
	@Before
	public void setUp() throws IOException {
		requestCount = new AtomicInteger(0);
		userAgents = new CopyOnWriteArrayList<String>();
		termMatchResponses = Collections.synchronizedMap(new HashMap<String,String>());
		pathResponses = Collections.synchronizedMap(new HashMap<String,String>());
		statusOverride = 200;
		server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
		server.createContext("/", exchange -> handle(exchange));
		server.start();
		GettyLookup.serviceBase = "http://127.0.0.1:" + server.getAddress().getPort() + "/TGNService.asmx/";
		originalClient = GettyLookup.gettyClient;
		// a new client for each test, so that responses cached in one test are not seen in another, 
		// with short delays so that retries do not slow the tests.
		GettyLookup.gettyClient = new RemoteServiceClient(GettyLookup.SERVICE_NAME, fastConfig());
	}
	
	/**
	 * @return a configuration with the default behavior, but short delays between retries.
	 */
	static RemoteServiceConfig fastConfig() { 
		return new RemoteServiceConfig()
				.setBackoffBaseMillis(1L)
				.setBackoffMaxMillis(5L)
				.setMinRequestIntervalMillis(0L);
	}

	/**
	 * Stop the local server and restore the default Getty TGN service URL.
	 */
	@After
	public void tearDown() {
		GettyLookup.serviceBase = GettyLookup.DEFAULT_SERVICE_BASE;
		GettyLookup.gettyClient = originalClient;
		if (server!=null) {
			server.stop(0);
		}
	}

	private void handle(HttpExchange exchange) throws IOException {
		requestCount.incrementAndGet();
		userAgents.add(exchange.getRequestHeaders().getFirst("User-Agent"));
		String body = null;
		if (statusOverride==200) {
			String path = exchange.getRequestURI().getPath();
			if (path.endsWith("/TGNGetTermMatch")) {
				Map<String,String> query = parseQuery(exchange.getRequestURI().getRawQuery());
				String name = query.get("name");
				if (name!=null) {
					name = name.replaceAll("^\"|\"$", "");
				}
				body = termMatchResponses.get(query.get("placetypeid") + "|" + name);
				if (body==null) {
					body = vocabulary(0, "");
				}
			} else {
				body = pathResponses.get(path);
			}
		}
		int status = (body==null) ? (statusOverride==200 ? 404 : statusOverride) : 200;
		byte[] bytes = (body==null ? "error" : body).getBytes(StandardCharsets.UTF_8);
		exchange.getResponseHeaders().add("Content-Type", "text/xml; charset=utf-8");
		exchange.sendResponseHeaders(status, bytes.length);
		try (OutputStream os = exchange.getResponseBody()) {
			os.write(bytes);
		}
	}

	private static Map<String,String> parseQuery(String rawQuery) throws IOException {
		Map<String,String> result = new HashMap<String,String>();
		if (rawQuery!=null) {
			for (String pair : rawQuery.split("&")) {
				int i = pair.indexOf('=');
				String key = i<0 ? pair : pair.substring(0, i);
				String value = i<0 ? "" : pair.substring(i+1);
				result.put(URLDecoder.decode(key, "UTF-8"), URLDecoder.decode(value, "UTF-8"));
			}
		}
		return result;
	}

	/**
	 * Build a TGNGetTermMatch response in the form returned by the Getty TGN.
	 */
	private static String vocabulary(int count, String subjects) {
		return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
				+ "<Vocabulary xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
				+ "xsi:noNamespaceSchemaLocation=\"http://vocabsservices.getty.edu/Schemas/TGN/TGNGetTermMatch.xsd\">\n"
				+ "  <Count>" + count + "</Count>\n"
				+ subjects
				+ "</Vocabulary>\n";
	}

	private static String subject(String preferredTerm, String preferredParent, String subjectID, String term) {
		return "  <Subject>\n"
				+ "    <Preferred_Term termid=\"1\">" + preferredTerm + "</Preferred_Term>\n"
				+ "    <Preferred_Parent>" + preferredParent + "</Preferred_Parent>\n"
				+ "    <Subject_ID>" + subjectID + "</Subject_ID>\n"
				+ "    <Term termid=\"2\">" + term + "</Term>\n"
				+ "  </Subject>\n";
	}

	private void addNation(String name, String subjectID) {
		termMatchResponses.put(NATION_PLACE_TYPE + "|" + name, vocabulary(1,
				subject(name + " (nation)", name + " (nation) [" + subjectID + "], World (facet) [7029392]", subjectID, name)));
	}

	/**
	 * Test that the shared Getty client opens connections with timeouts and a User-Agent set.
	 * 
	 * @throws IOException if the connection can not be opened.
	 */
	@Test
	public void testOpenConnection() throws IOException { 
		RemoteServiceClient client = new RemoteServiceClient(GettyLookup.SERVICE_NAME, RemoteServiceConfig.fromSystemProperties());
		HttpURLConnection connection = client.openConnection(GettyLookup.serviceBase + "TGNGetTermMatch?name=Belgium");
		assertEquals(client.getConfig().getConnectTimeoutMillis(), connection.getConnectTimeout());
		assertEquals(client.getConfig().getReadTimeoutMillis(), connection.getReadTimeout());
		assertTrue(connection.getConnectTimeout() > 0);
		assertTrue(connection.getReadTimeout() > 0);
		assertEquals(client.getConfig().getUserAgent(), connection.getRequestProperty("User-Agent"));
		assertTrue(client.getConfig().getUserAgent().startsWith("FilteredPush-geo_ref_qc/"));
		// opening does not make a request
		assertEquals(0, requestCount.get());
		
		try { 
			client.openConnection("not a url");
			fail("Expected a MalformedURLException");
		} catch (MalformedURLException e) { 
			logger.debug(e.getMessage());
		}
	}

	/**
	 * Test fetching and unmarshalling a TGNGetTermMatch response, including the User-Agent sent.
	 *
	 * @throws Exception on an unexpected failure.
	 */
	@Test
	public void testFetchVocabulary() throws Exception {
		addNation("Belgium", "1000063");
		Vocabulary response = GettyLookup.fetchVocabulary(GettyLookup.serviceBase + "TGNGetTermMatch?name=Belgium&placetypeid=81011&nationid=");
		assertEquals(1, response.getCount().intValue());
		assertEquals("Belgium (nation)", response.getSubject().get(0).getPreferredTerm().getValue());
		assertEquals(1, requestCount.get());
		assertEquals(GettyLookup.gettyClient.getConfig().getUserAgent(), userAgents.get(0));

		// no matches
		response = GettyLookup.fetchVocabulary(GettyLookup.serviceBase + "TGNGetTermMatch?name=Nowhere&placetypeid=81011&nationid=");
		assertEquals(0, response.getCount().intValue());
		assertTrue(response.getSubject().isEmpty());
	}

	/**
	 * Test that an HTTP error status from the service is reported as an IOException.
	 *
	 * @throws JAXBException on an unexpected failure.
	 */
	@Test
	public void testFetchVocabularyHttpError() throws JAXBException {
		statusOverride = 503;
		try {
			GettyLookup.fetchVocabulary(GettyLookup.serviceBase + "TGNGetTermMatch?name=Belgium&placetypeid=81011&nationid=");
			fail("Expected an IOException");
		} catch (IOException e) {
			logger.debug(e.getMessage());
			assertTrue(e.getMessage().contains("503"));
		}
	}

	/**
	 * Test that a response that is not a TGN vocabulary is reported as a JAXBException.
	 *
	 * @throws IOException on an unexpected failure.
	 */
	@Test
	public void testFetchVocabularyUnparsable() throws IOException {
		pathResponses.put("/TGNService.asmx/TGNGetSubject", "this is not xml");
		try {
			GettyLookup.fetchVocabulary(GettyLookup.serviceBase + "TGNGetSubject?subjectID=1");
			fail("Expected a JAXBException");
		} catch (JAXBException e) {
			logger.debug(e.getMessage());
		}
	}

	/**
	 * Test fetching and parsing an XML document.
	 *
	 * @throws Exception on an unexpected failure.
	 */
	@Test
	public void testFetchDocument() throws Exception {
		pathResponses.put("/TGNService.asmx/TGNGetParents", "<Vocabulary><Parent_Subject_ID>7012149</Parent_Subject_ID></Vocabulary>");
		Document document = GettyLookup.fetchDocument(DocumentBuilderFactory.newInstance().newDocumentBuilder(),
				GettyLookup.serviceBase + "TGNGetParents?subjectID=7007710");
		assertEquals("7012149", document.getElementsByTagName("Parent_Subject_ID").item(0).getTextContent());
		assertEquals(GettyLookup.gettyClient.getConfig().getUserAgent(), userAgents.get(0));
	}

	/**
	 * Test lookupParent, which makes a TGNGetParents request followed by a TGNGetSubject request.
	 */
	@Test
	public void testLookupParent() {
		pathResponses.put("/TGNService.asmx/TGNGetParents", "<Vocabulary><Parent_Subject_ID>7012149</Parent_Subject_ID></Vocabulary>");
		pathResponses.put("/TGNService.asmx/TGNGetSubject", "<Vocabulary><Subject><Term_Text>United States</Term_Text></Subject></Vocabulary>");
		GettyLookup lookup = new GettyLookup();
		assertEquals("United States", lookup.lookupParent("7007710"));
		assertEquals(2, requestCount.get());

		// no parent
		pathResponses.put("/TGNService.asmx/TGNGetParents", "<Vocabulary></Vocabulary>");
		assertEquals("", lookup.lookupParent("1"));

		// a repeated lookup is answered from the cache
		assertEquals("United States", lookup.lookupParent("7007710"));
		assertEquals(3, requestCount.get());
		
		// service failure
		statusOverride = 500;
		assertEquals("", lookup.lookupParent("7007711"));
	}

	/**
	 * Test that a matched country is cached under the country name, so a repeated lookup
	 * of the same country does not make another request to the Getty TGN.
	 *
	 * @throws SourceAuthorityException on an unexpected failure.
	 */
	@Test
	public void testLookupCountryCached() throws SourceAuthorityException {
		addNation("Belgium", "1000063");
		addNation("Ghana", "1000160");
		GettyLookup lookup = new GettyLookup();
		assertTrue(lookup.lookupCountry("Belgium"));
		assertEquals(1, requestCount.get());
		assertTrue(lookup.lookupCountry("Belgium"));
		assertEquals(1, requestCount.get());
		// a different country is not answered from the cache
		assertTrue(lookup.lookupCountry("Ghana"));
		assertEquals(2, requestCount.get());
		assertTrue(lookup.lookupCountry("Ghana"));
		assertEquals(2, requestCount.get());
	}

	/**
	 * Test lookupCountry with empty values, no match, and a service failure.
	 */
	@Test
	public void testLookupCountryEdgeCases() {
		GettyLookup lookup = new GettyLookup();
		try {
			assertFalse(lookup.lookupCountry(null));
			assertFalse(lookup.lookupCountry(""));
			assertEquals(0, requestCount.get());

			assertFalse(lookup.lookupCountry("Nowhere"));
			assertEquals(1, requestCount.get());
		} catch (SourceAuthorityException e) {
			fail("Unexpected exception " + e.getMessage());
		}

		statusOverride = 503;
		try {
			lookup.lookupCountry("Belgium");
			fail("Expected a SourceAuthorityException");
		} catch (SourceAuthorityException e) {
			logger.debug(e.getMessage());
		}
	}

	/**
	 * Test that getCountryObjects returns the nation, not a cached primary division with
	 * the same name.
	 *
	 * @throws SourceAuthorityException on an unexpected failure.
	 */
	@Test
	public void testGetCountryObjectsNotFromPrimaryCache() throws SourceAuthorityException {
		termMatchResponses.put(PRIMARY_PLACE_TYPE + "|Georgia", vocabulary(1,
				subject("Georgia (state)", "Georgia (state) [7007710], United States (nation) [7012149], World (facet) [7029392]", "7007710", "Georgia")));
		addNation("Georgia", "1000156");
		GettyLookup lookup = new GettyLookup();
		assertTrue(lookup.lookupUniquePrimary("Georgia"));
		assertEquals(1, requestCount.get());

		List<GettyTGNObject> countries = lookup.getCountryObjects("Georgia");
		assertEquals(2, requestCount.get());
		assertEquals(1, countries.size());
		assertEquals("1000156", countries.get(0).getSubjectID());
		assertEquals(NATION_PLACE_TYPE, countries.get(0).getPlaceTypeID());

		// the primary division is still available from the primary division cache
		List<GettyTGNObject> primaries = lookup.getPrimaryObjects("Georgia");
		assertEquals(2, requestCount.get());
		assertEquals("7007710", primaries.get(0).getSubjectID());
	}

	/**
	 * Test getCountryObjects with an empty country, no match, and a service failure.
	 */
	@Test
	public void testGetCountryObjectsEdgeCases() {
		GettyLookup lookup = new GettyLookup();
		try {
			assertTrue(lookup.getCountryObjects(null).isEmpty());
			assertTrue(lookup.getCountryObjects("").isEmpty());
			assertEquals(0, requestCount.get());
			assertTrue(lookup.getCountryObjects("Nowhere").isEmpty());
			assertEquals(1, requestCount.get());
		} catch (SourceAuthorityException e) {
			fail("Unexpected exception " + e.getMessage());
		}

		statusOverride = 503;
		try {
			lookup.getCountryObjects("Belgium");
			fail("Expected a SourceAuthorityException");
		} catch (SourceAuthorityException e) {
			logger.debug(e.getMessage());
		}
	}

	/**
	 * Test that VALIDATION_COUNTRY_FOUND makes a single request to the Getty TGN for a
	 * country that has not been seen before, and no request for a country that has.
	 */
	@Test
	public void testValidationCountryFoundSingleRequest() {
		// Use names not used elsewhere, as results are cached in GeoUtilSingleton for the life of the JVM.
		addNation("Testlandia", "9999991");
		DQResponse<ComplianceValue> result = DwCGeoRefDQ.validationCountryFound("Testlandia", GettyLookup.GETTY_TGN);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		assertEquals(1, requestCount.get());

		result = DwCGeoRefDQ.validationCountryFound("Testlandia", GettyLookup.GETTY_TGN);
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		assertEquals(1, requestCount.get());

		result = DwCGeoRefDQ.validationCountryFound("Nottestlandia", GettyLookup.GETTY_TGN);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		assertEquals(2, requestCount.get());
	}

	/**
	 * Test that VALIDATION_STATEPROVINCE_FOUND makes a single TGNGetTermMatch request for
	 * a primary division that has not been seen before.
	 */
	@Test
	public void testValidationStateprovinceFoundSingleRequest() {
		termMatchResponses.put(PRIMARY_PLACE_TYPE + "|Testprovincia", vocabulary(1,
				subject("Testprovincia (province)", "Testprovincia (province) [9999992], Testlandia (nation) [9999991], World (facet) [7029392]", "9999992", "Testprovincia")));
		DQResponse<ComplianceValue> result = DwCGeoRefDQ.validationStateprovinceFound("Testprovincia", GettyLookup.GETTY_TGN);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		assertEquals(1, requestCount.get());
	}

	/**
	 * Test that a lookup that failed is not resent while the failure is remembered, and 
	 * that once the Getty TGN has failed repeatedly, lookups fail without sending requests.
	 */
	@Test
	public void testFailedLookupsNotResent() { 
		GettyLookup.gettyClient = new RemoteServiceClient(GettyLookup.SERVICE_NAME, fastConfig()
				.setMaxRetries(1).setCircuitBreakerThreshold(3).setCircuitBreakerOpenMillis(60000L));
		statusOverride = 503;
		GettyLookup lookup = new GettyLookup();
		try { 
			lookup.lookupCountry("Belgium");
			fail("Expected a SourceAuthorityException");
		} catch (SourceAuthorityException e) { 
			logger.debug(e.getMessage());
		}
		// one request and one retry
		assertEquals(2, requestCount.get());
		
		// the same lookup is not resent
		try { 
			lookup.lookupCountry("Belgium");
			fail("Expected a SourceAuthorityException");
		} catch (SourceAuthorityException e) { 
			logger.debug(e.getMessage());
		}
		assertEquals(2, requestCount.get());
		
		// other lookups are sent until the circuit breaker trips
		assertNull(lookup.lookupPrimary("Testprovincia"));
		assertEquals(4, requestCount.get());
		try { 
			lookup.getCountryObjects("Ghana");
			fail("Expected a SourceAuthorityException");
		} catch (SourceAuthorityException e) { 
			logger.debug(e.getMessage());
		}
		assertEquals(6, requestCount.get());
		assertTrue(GettyLookup.getGettyClient().isCircuitOpen());
		
		// then fail without being sent
		assertNull(lookup.lookupUniquePrimary("Othertestprovincia"));
		try { 
			lookup.lookupCountry("Ghana");
			fail("Expected a SourceAuthorityException");
		} catch (SourceAuthorityException e) { 
			logger.debug(e.getMessage());
			assertTrue(e.getMessage().contains("unavailable"));
		}
		assertEquals(6, requestCount.get());
		
		// VALIDATION_COUNTRY_FOUND reports the external prerequisites as not met
		DQResponse<ComplianceValue> result = DwCGeoRefDQ.validationCountryFound("Nottestlandia2", GettyLookup.GETTY_TGN);
		logger.debug(result.getComment());
		assertEquals(ResultState.EXTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		assertEquals(6, requestCount.get());
	}

}
