/**
 * GettyLookup.java
 */
package org.filteredpush.qc.georeference.util;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.emf.common.util.URI;
import org.filteredpush.qc.georeference.SourceAuthorityException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.getty.tgn.objects.Vocabulary;
import edu.getty.tgn.objects.Vocabulary.Subject;
import edu.getty.tgn.objects.Vocabulary.Subject.Term;
import edu.getty.tgn.service.GettyTGNObject;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

/**
 * Check country names against the Getty Thesaurus of Geographic Names (TGN).
 *
 * @author mole
 * @version $Id: $Id
 */
public class GettyLookup {

	private static final Log logger = LogFactory.getLog(GettyLookup.class);
	
	// Cache of countries found
	private Map<String,GettyTGNObject> countryCache;
	// Cache of first matching primary divisions found
	private Map<String,GettyTGNObject> primaryCache; 
	// Cache of unique primary divisions found
	private Map<String,GettyTGNObject> uniquePrimaryCache;
	
	/** 
	 * Constant GETTY_TGN="The Getty Thesaurus of Geographic Names"
	 */
	public static final String GETTY_TGN = "The Getty Thesaurus of Geographic Names (TGN)";
	
	/** 
	 * Pattern to identify nation entities in a Getty TGN parentage string.
	 */
	private static final Pattern NATION_PARENT_PATTERN = Pattern.compile("(?i)^\\s*(.*?)\\s*\\(nation\\)\\s*(?:\\[[^]]*\\])?\\s*$");
	
	/**
	 * Default base URL for the Getty TGN web services.  Uses http, as the service does not 
	 * respond to requests made with https.
	 */
	public static final String DEFAULT_SERVICE_BASE = "http://vocabsservices.getty.edu/TGNService.asmx/";
	
	/** 
	 * System property that can be used to set the connect timeout in milliseconds for requests 
	 * to the Getty TGN, e.g. -Dgeo_ref_qc.connectTimeoutMillis=5000
	 */
	public static final String CONNECT_TIMEOUT_PROPERTY = "geo_ref_qc.connectTimeoutMillis";
	
	/** 
	 * System property that can be used to set the read timeout in milliseconds for requests 
	 * to the Getty TGN, e.g. -Dgeo_ref_qc.readTimeoutMillis=60000
	 */
	public static final String READ_TIMEOUT_PROPERTY = "geo_ref_qc.readTimeoutMillis";
	
	/** 
	 * System property that can be used to set the User-Agent sent with requests to the Getty TGN.
	 */
	public static final String USER_AGENT_PROPERTY = "geo_ref_qc.userAgent";
	
	/** Default connect timeout for requests to the Getty TGN, in milliseconds. */
	public static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 10000;
	
	/** Default read timeout for requests to the Getty TGN, in milliseconds. */
	public static final int DEFAULT_READ_TIMEOUT_MILLIS = 30000;
	
	/** Connect timeout used for requests to the Getty TGN, in milliseconds. */
	static final int CONNECT_TIMEOUT_MILLIS = Integer.getInteger(CONNECT_TIMEOUT_PROPERTY, DEFAULT_CONNECT_TIMEOUT_MILLIS);
	
	/** Read timeout used for requests to the Getty TGN, in milliseconds. */
	static final int READ_TIMEOUT_MILLIS = Integer.getInteger(READ_TIMEOUT_PROPERTY, DEFAULT_READ_TIMEOUT_MILLIS);
	
	/** User-Agent identifying this library, sent with each request to the Getty TGN. */
	static final String USER_AGENT = System.getProperty(USER_AGENT_PROPERTY, 
			"FilteredPush-geo_ref_qc/" + implementationVersion() + " (+https://github.com/FilteredPush/geo_ref_qc)");
	
	/** 
	 * Base URL used for requests to the Getty TGN web services, package visible so that unit 
	 * tests can point requests at a local test server.
	 */
	static volatile String serviceBase = DEFAULT_SERVICE_BASE;
	
	/** JAXBContext for Getty TGN responses, thread safe and expensive to create, so created once. */
	private static JAXBContext vocabularyContext;
	
	/**
	 * Default constructor
	 */
	public GettyLookup() { 
		init();
	}
	
	/**
	 * Obtain the version of this library from the jar manifest, for use in the User-Agent.
	 * 
	 * @return the implementation version of this library, or "unknown" if not available 
	 *   (e.g. when not running from a jar).
	 */
	private static String implementationVersion() { 
		Package p = GettyLookup.class.getPackage();
		String version = (p==null) ? null : p.getImplementationVersion();
		return (version==null) ? "unknown" : version;
	}
	
	/**
	 * Obtain the shared JAXBContext for unmarshalling Getty TGN Vocabulary responses, 
	 * creating it on first use.
	 * 
	 * @return a JAXBContext for {@link Vocabulary}.
	 * @throws JAXBException if the JAXBContext cannot be created.
	 */
	private static synchronized JAXBContext getVocabularyContext() throws JAXBException { 
		if (vocabularyContext==null) { 
			vocabularyContext = JAXBContext.newInstance(Vocabulary.class);
		}
		return vocabularyContext;
	}
	
	/**
	 * Open a connection to the Getty TGN with connect and read timeouts and a User-Agent set, 
	 * so that an unresponsive service can not block the calling thread indefinitely, and so 
	 * that requests identify this library to the service.
	 * 
	 * @param request the URL to request.
	 * @return an HttpURLConnection, not yet connected.
	 * @throws IOException on an error opening the connection, including a 
	 *   MalformedURLException if request is not a valid URL.
	 */
	static HttpURLConnection openConnection(String request) throws IOException { 
		URL url = new URL(request);
		HttpURLConnection getty = (HttpURLConnection) url.openConnection();
		getty.setConnectTimeout(CONNECT_TIMEOUT_MILLIS);
		getty.setReadTimeout(READ_TIMEOUT_MILLIS);
		getty.setRequestProperty("User-Agent", USER_AGENT);
		return getty;
	}
	
	/**
	 * Request a TGN Vocabulary response (e.g. from TGNGetTermMatch) from the Getty TGN, closing 
	 * the response stream after reading it.
	 * 
	 * @param request the URL to request.
	 * @return the unmarshalled response.
	 * @throws IOException on an error communicating with the service, including an HTTP error status.
	 * @throws JAXBException on an error interpreting the response.
	 */
	static Vocabulary fetchVocabulary(String request) throws IOException, JAXBException { 
		HttpURLConnection getty = openConnection(request);
		try (InputStream is = getty.getInputStream()) { 
			Unmarshaller unmarshaler = getVocabularyContext().createUnmarshaller();
			return (Vocabulary) unmarshaler.unmarshal(is);
		}
	}
	
	/**
	 * Request an XML document (e.g. from TGNGetParents) from the Getty TGN, closing the response 
	 * stream after reading it.
	 * 
	 * @param builder to use to parse the response.
	 * @param request the URL to request.
	 * @return the parsed response.
	 * @throws IOException on an error communicating with the service, including an HTTP error status.
	 * @throws SAXException on an error parsing the response.
	 */
	static Document fetchDocument(DocumentBuilder builder, String request) throws IOException, SAXException { 
		HttpURLConnection getty = openConnection(request);
		try (InputStream is = getty.getInputStream()) { 
			return builder.parse(is);
		}
	}
	
	/** 
	 * Set up cache objects
	 */
	private void init() { 
		countryCache = Collections.synchronizedMap(new HashMap<String,GettyTGNObject>());
		primaryCache = Collections.synchronizedMap(new HashMap<String,GettyTGNObject>());
		uniquePrimaryCache = Collections.synchronizedMap(new HashMap<String,GettyTGNObject>());
	}

	/**
	 * Match a country name against the list of sovereign nations in the Getty TGN.
	 *
	 * @param country a {@link java.lang.String} object.
	 * @return true if the country is found as a sovereign nation level entity in TGN matching
	 * any form of the name, false if the country is not found as a sovereign nation lavel entity
	 * in TGN, null on an exception querying TGN.
	 * @throws SourceAuthorityException 
	 */
	public Boolean lookupCountry(String country) throws SourceAuthorityException { 

		Boolean retval = null;

		if (GEOUtil.isEmpty(country)) { 
			retval = false;
		} else { 
			if (countryCache.containsKey(country) && countryCache.get(country)!=null) { 
				retval =  true;
			} else { 

				// See http://vocabsservices.getty.edu/Schemas/TGN/tgn_place_type.xsd for place types.
				String sovereignNationPlaceTypeID = "81011";
				// See documentation in: https://www.getty.edu/research/tools/vocabularies/vocab_web_services.pdf
				String baseURI = serviceBase + "TGNGetTermMatch?";

				StringBuilder request = new StringBuilder();
				request.append(baseURI);
				String countryEncoded = URI.encodeFragment(country, false);
				request.append("name=").append(countryEncoded);
				request.append("&placetypeid=").append(sovereignNationPlaceTypeID);
				request.append("&nationid=").append("");
				logger.debug(request.toString());
				try {
					Vocabulary response = fetchVocabulary(request.toString());
					System.out.println(response.getCount());
					if (response.getCount().compareTo(BigInteger.ONE)==0) { 
						// idiom for line above from BigInteger docs: (x.compareTo(y) <op> 0)
						// one match
						retval = true;
						if (!countryCache.containsKey(country)) { 
							countryCache.put(country, new GettyTGNObject(response.getSubject().get(0),sovereignNationPlaceTypeID));
						}
					} else if (response.getCount().compareTo(BigInteger.ONE)>0) {
						// idiom for line above from BigInteger docs: (x.compareTo(y) <op> 0)
						// found multiple possible matches
						List<Subject> subjects = response.getSubject();
						Iterator<Subject> i = subjects.iterator();
						boolean matched = false;
						while (i.hasNext() && !matched) {
							Subject subject = i.next();
							logger.debug(subject.getPreferredTerm().getValue());
							if (subject.getPreferredTerm().getValue().replace("(nation)","").trim().equals(country)) { 
								matched = true;
							}
						}
						retval = matched;
					} else { 
						retval = false;
					}
				} catch (JAXBException e) {
					logger.error(e.getMessage());
					throw new SourceAuthorityException("Error interpreting json response returned from Getty TGN:" + e.getMessage());
				} catch (MalformedURLException e) {
					logger.error(e.getMessage());
				} catch (IOException e) {
					logger.error(e.getMessage());
					throw new SourceAuthorityException("Error querying the Getty TGN:" + e.getMessage());
				}	
			} 
		} 
	
		return retval;
	} 
	
	/**
	 * Lookup a country by preferred name only in the Getty TGN.
	 *
	 * @param country preferred name of sovereign nation level entity to look up.
	 * @return true if the provided country matches a preferred name of a sovereign nation level
	 * entity in TGN, false if it does not, null on an error querying the TGN service.
	 * @throws SourceAuthorityException 
	 */
	public Boolean lookupCountryExact(String country) throws SourceAuthorityException { 

		Boolean retval = null;

		if (GEOUtil.isEmpty(country)) { 
			retval = false;
		} else { 

			// See: http://vocabsservices.getty.edu/Schemas/TGN/tgn_place_type.xsd for place types
			String sovereignNationPlaceTypeID = "81011";
			// See documentation in: https://www.getty.edu/research/tools/vocabularies/vocab_web_services.pdf
			String baseURI = serviceBase + "TGNGetTermMatch?";

			StringBuilder request = new StringBuilder();
			request.append(baseURI);
			String countryEncoded = URI.encodeFragment(country, false);
			request.append("name=").append(countryEncoded);
			request.append("&placetypeid=").append(sovereignNationPlaceTypeID);
			request.append("&nationid=").append("");
			logger.debug(request.toString());
			try {
				Vocabulary response = fetchVocabulary(request.toString());
				logger.debug(response.getCount());
				if (response.getCount().compareTo(BigInteger.ONE)==0) { 
					String preferredTerm = response.getSubject().get(0).getPreferredTerm().getValue();
					String cleanedPreferredTerm = preferredTerm.replaceAll("\\([A-Za-z ]+\\)$", "").trim();
					System.out.println(cleanedPreferredTerm);
					if (country.equals(cleanedPreferredTerm)) { 
						retval = true;
					} else {
						retval = false;
					}
				} else { 
					retval = false;
				}
			} catch (JAXBException e) {
				logger.error(e.getMessage());
				throw new SourceAuthorityException("Error interpreting json response returned from Getty TGN:" + e.getMessage());
			} catch (MalformedURLException e) {
				logger.error(e.getMessage());
			} catch (IOException e) {
				logger.error(e.getMessage());
				throw new SourceAuthorityException("Error querying the Getty TGN:" + e.getMessage());
			}	

		}
		
		return retval;
	} 
	
	/**
	 * Match a country name against the list of sovereign nations in the Getty TGN.
	 *
	 * @param country a {@link java.lang.String} object.
	 * @return true if the country is found as a sovereign nation level entity in TGN matching
	 * any form of the name, false if the country is not found as a sovereign nation lavel entity
	 * in TGN, null on an exception querying TGN.
	 * @throws org.filteredpush.qc.georeference.util.GeorefServiceException if any.
	 */
	public List<String> getNamesForCountry(String country) throws GeorefServiceException { 

		ArrayList<String> retval = new ArrayList<String>();

		if (!GEOUtil.isEmpty(country)) { 
		
			if (GeoUtilSingleton.getInstance().isGettyCountryLookupItem(country)) { 
				retval = (ArrayList<String>) GeoUtilSingleton.getInstance().getGettyCountryLookupItem(country);
			} else { 

				// See: http://vocabsservices.getty.edu/Schemas/TGN/tgn_place_type.xsd for place types
				String sovereignNationPlaceTypeID = "81011";
				// See documentation in: https://www.getty.edu/research/tools/vocabularies/vocab_web_services.pdf
				String baseURI = serviceBase + "TGNGetTermMatch?";

				StringBuilder request = new StringBuilder();
				request.append(baseURI);
				String countryEncoded = URI.encodeFragment(country, false);
				request.append("name=").append(countryEncoded);
				request.append("&placetypeid=").append(sovereignNationPlaceTypeID);
				request.append("&nationid=").append("");
				logger.debug(request.toString());
				try {
					Vocabulary response = fetchVocabulary(request.toString());
					System.out.println(response.getCount());
					System.out.println(response.getCount());
					if (response.getCount().compareTo(BigInteger.ONE)==0) { 
						List<Subject> subjectList = response.getSubject();
						Iterator<Subject> i = subjectList.iterator();
						while (i.hasNext()) { 
							Subject subject = i.next();
							List<Term> terms = subject.getTerm();
							Iterator<Term> it = terms.iterator();
							while (it.hasNext()) {
								retval.add(it.next().getValue());
							}
							String preferredTerm = subject.getPreferredTerm().getValue();
						    if (!GEOUtil.isEmpty(preferredTerm)) {
						        preferredTerm = removeGettyPlaceType(preferredTerm);
						        if (!retval.contains(preferredTerm)) {
						        	retval.add(preferredTerm);
						        }
						    }
							String preferredParentage = subject.getPreferredParent();
							String nationNameFromParentage = extractNationFromParentage(preferredParentage).trim();
						    if (!GEOUtil.isEmpty(nationNameFromParentage) && !retval.contains(nationNameFromParentage)) {
						        retval.add(nationNameFromParentage);
						    }
						}
					}
				} catch (JAXBException e) {
					logger.debug(e.getMessage());
					throw new GeorefServiceException("Getty Country Lookup Failure (XMLBinding).", e.getCause());
				} catch (MalformedURLException e) {
					logger.debug(e.getMessage());
					throw new GeorefServiceException("Getty Country Lookup Failure (Malformed URI).", e.getCause());
				} catch (IOException e) {
					logger.debug(e.getMessage());
					throw new GeorefServiceException("IO Error on Getty Country Lookup.", e.getCause());
				}	
			} 
		}
		if (retval.size() > 0) { 
			GeoUtilSingleton.getInstance().addGettyCountryLookupItem(country, retval);
		} else { 
			logger.debug("No Getty TGN matches found for country: " + country);
		}

		return retval;
	}
	
	/**
	 * Remove Getty place type from a string value containing: name (type)
	 * or name [id] or name (type) [id] and return just the name.
	 *
	 * @param value a {@link java.lang.String} object, e.g. "United States (nation)" or "North America (continent)".
	 * @return a {@link java.lang.String} object, e.g. "United States" or "North America".
	 */
	private static String removeGettyPlaceType(String value) {
	    if (value == null) {
	        return null;
	    }

	    return value
	        // Remove "(nation)", "(continent)", etc.
	        .replaceFirst("\\s*\\([^)]*\\)", "")
	        // Remove "[1000063]"
	        .replaceFirst("\\s*\\[[^]]*\\]", "")
	        .trim();
	}
	
	/** 
	 * Extract the nation name from a Getty TGN parentage string, which may contain multiple
	 * atoms separated by commas, e.g. Belgium (nation) [1000063], Europe (continent) [1000003], World (facet) [7029392].
	 *
	 * @param parentage a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object, e.g. "United States", or null if no nation is found.
	 */
	private static String extractNationFromParentage(String parentage) {
	    if (GEOUtil.isEmpty(parentage)) {
	        return null;
	    }

	    String[] atoms = parentage.split("\\s*,\\s*");

	    for (String atom : atoms) {
	        Matcher matcher = NATION_PARENT_PATTERN.matcher(atom);

	        if (matcher.matches()) {
	            String nationName = matcher.group(1).trim();

	            return GEOUtil.isEmpty(nationName) ? null : nationName;
	        }
	    }

	    // Parentage did not contain a "(nation)" atom
	    return null;
	}
	
	/**
	 * Match a secondary geopolitical entity (state/province) name in the the Getty TGN, returns
	 * true if at least one such entity is found.
	 *
	 * @param primaryDivision the state/province to look up.
	 * @return true if the secondaryDivision is found as appropriate geopolitical entity in TGN matching
	 * any form of the name at least once, false if the primary division is not found in TGN,
	 * null on an exception querying TGN.
	 */
	public Boolean lookupPrimary(String primaryDivision) { 

		Boolean retval = null;

		if (GEOUtil.isEmpty(primaryDivision)) { 
			retval = false;
		} else { 
			// See: http://vocabsservices.getty.edu/Schemas/TGN/tgn_place_type.xsd for place types
			String placeTypeID = "81100"; //first level subdivision
			// See documentation in: https://www.getty.edu/research/tools/vocabularies/vocab_web_services.pdf
			String baseURI = serviceBase + "TGNGetTermMatch?";

			StringBuilder request = new StringBuilder();
			request.append(baseURI);
			// enclose in quotes for exact match
			String primaryEncoded = URI.encodeFragment('"'+primaryDivision+'"', false);
			request.append("name=").append(primaryEncoded);
			request.append("&placetypeid=").append(placeTypeID);
			request.append("&nationid=").append("");
			logger.debug(request.toString());
			try {
				Vocabulary response = fetchVocabulary(request.toString());
				logger.debug(response.getCount());
				logger.debug(response.getCount());
				if (response.getCount().compareTo(BigInteger.ONE) >= 0) { 
					// idiom for line above from BigInteger docs: (x.compareTo(y) <op> 0)
					retval = true;
					// cache the first match
					primaryCache.put(primaryDivision, new GettyTGNObject(response.getSubject().get(0),placeTypeID));
				} else { 
					retval = false;
				}
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		logger.debug(retval);

		return retval;
	} 
	
	
	/**
	 * Match a secondary geopolitical entity (state/province) name in the the Getty TGN, returns
	 * true if exactly one such entity is found.
	 *
	 * @param primaryDivision the state/province to look up.
	 * @return true if the secondaryDivision is found as appropriate geopolitical entity in TGN matching
	 * any form of the name at least once, false if the primary division is not found in TGN,
	 * null on an exception querying TGN.
	 */
	public Boolean lookupUniquePrimary(String primaryDivision) { 

		Boolean retval = null;

		if (GEOUtil.isEmpty(primaryDivision)) { 
			retval = false;
		} else { 
			if (uniquePrimaryCache.containsKey(primaryDivision)) { 
				logger.debug(uniquePrimaryCache.get(primaryDivision));
				retval = true;
			} else { 
				// See: http://vocabsservices.getty.edu/Schemas/TGN/tgn_place_type.xsd for place types
				String placeTypeID = "81100"; //first level subdivision
				// See documentation in: https://www.getty.edu/research/tools/vocabularies/vocab_web_services.pdf
				String baseURI = serviceBase + "TGNGetTermMatch?";

				StringBuilder request = new StringBuilder();
				request.append(baseURI);
				// enclose in quotes for exact match
				String primaryEncoded = URI.encodeFragment('"'+primaryDivision+'"', false);
				request.append("name=").append(primaryEncoded);
				request.append("&placetypeid=").append(placeTypeID);
				request.append("&nationid=").append("");
				logger.debug(request.toString());
				try {
					Vocabulary response = fetchVocabulary(request.toString());
					logger.debug(response.getCount());
					logger.debug(response.getCount());
					if (response.getCount().compareTo(BigInteger.ONE) == 0) { 
						// idiom for line above from BigInteger docs: (x.compareTo(y) <op> 0)
						retval = true;
						// cache the match
						uniquePrimaryCache.put(primaryDivision, new GettyTGNObject(response.getSubject().get(0),placeTypeID));
					} else { 
						retval = false;
					}
				} catch (JAXBException e) {
					logger.debug(e.getMessage(),e);
				} catch (MalformedURLException e) {
					logger.debug(e.getMessage(),e);
				} catch (IOException e) {
					logger.debug(e.getMessage(),e);
				}	
			}
		}
		logger.debug(retval);

		return retval;
	} 
	
	
	/**
	 * <p>getPreferredCountryName.</p>
	 *
	 * @param country a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	public String getPreferredCountryName(String country) { 

		String retval = null;

		if (!GEOUtil.isEmpty(country)) { 

			if (countryCache.containsKey(country) && countryCache.get(country)!=null) { 
				retval = countryCache.get(country).getName();
			}
			
			// See: http://vocabsservices.getty.edu/Schemas/TGN/tgn_place_type.xsd for place types
			String sovereignNationPlaceTypeID = "81011";
			// See documentation in: https://www.getty.edu/research/tools/vocabularies/vocab_web_services.pdf
			String baseURI = serviceBase + "TGNGetTermMatch?";

			StringBuilder request = new StringBuilder();
			request.append(baseURI);
			request.append("name=").append(country.replace(" ", "+"));
			request.append("&placetypeid=").append(sovereignNationPlaceTypeID);
			request.append("&nationid=").append("");
			logger.debug(request.toString());
			try {
				Vocabulary response = fetchVocabulary(request.toString());
				logger.debug(response.getCount());
				if (response.getCount().compareTo(BigInteger.ONE)==0) { 
					// idiom for line above from BigInteger docs: (x.compareTo(y) <op> 0)
					// found one match
					List<Subject> subjects = response.getSubject();
					Iterator<Subject> i = subjects.iterator();
					while (i.hasNext()) {
						Subject subject = i.next();
						logger.debug(subject.getPreferredTerm().getValue());
						//retval = subject.getPreferredTerm().getValue();
						retval = subject.getPreferredTerm().getValue().replace("(nation)","").trim();
						logger.debug(subject.getSubjectID());
						logger.debug(subject.getPreferredParent());
					}
				} else if (response.getCount().compareTo(BigInteger.ONE)>0) { 
					// idiom for line above from BigInteger docs: (x.compareTo(y) <op> 0)
					// found multiple possible matches
					List<Subject> subjects = response.getSubject();
					Iterator<Subject> i = subjects.iterator();
					boolean matched = false;
					while (i.hasNext() && !matched) {
						Subject subject = i.next();
						logger.debug(subject.getPreferredTerm().getValue());
						if (subject.getPreferredTerm().getValue().replace("(nation)","").trim().equals(country)) { 
							retval = subject.getPreferredTerm().getValue().replace("(nation)","").trim();
							logger.debug(subject.getSubjectID());
							logger.debug(subject.getPreferredParent());
							matched = true;
						}
					}
				} else { 
					logger.debug(response.getCount().toString());
				}
			} catch (JAXBException e) {
				logger.error(e.getMessage());
			} catch (MalformedURLException e) {
				logger.error(e.getMessage());
			} catch (IOException e) {
				logger.error(e.getMessage());
			}	
		}
		return retval;
	} 
	
	/**
	 * <p>getParentageForPrimary.</p>
	 *
	 * @param primaryDivision a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	public String getParentageForPrimary(String primaryDivision) { 

		String retval = null;

		// See: http://vocabsservices.getty.edu/Schemas/TGN/tgn_place_type.xsd for place types
		String placeTypeID = "81100";
		// See documentation in: https://www.getty.edu/research/tools/vocabularies/vocab_web_services.pdf
		String baseURI = serviceBase + "TGNGetTermMatch?";

		StringBuilder request = new StringBuilder();
		request.append(baseURI);
		request.append("name=").append(primaryDivision);
		request.append("&placetypeid=").append(placeTypeID);
		request.append("&nationid=").append("");
		try {
			Vocabulary response = fetchVocabulary(request.toString());
			System.out.println(response.getCount());
			if (response.getCount()==BigInteger.ONE) { 
				// found match
			} 
			List<Subject> subjects = response.getSubject();
			Iterator<Subject> i = subjects.iterator();
			while (i.hasNext()) {
				Subject subject = i.next();
				System.out.println(subject.getPreferredTerm().getValue());
				System.out.println(subject.getSubjectID());
				System.out.println(subject.getPreferredParent());
				GettyTGNObject stateProvinceObject = new GettyTGNObject(subject,placeTypeID);
				retval = subject.getPreferredParent();
			}
		} catch (JAXBException e) {
			logger.error(e.getMessage());
		} catch (MalformedURLException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}	
		return retval;
	}
	
	/**
	 * <p>getPrimaryObject.</p>
	 *
	 * @param primaryDivision a {@link java.lang.String} object.
	 * @return a {@link edu.getty.tgn.service.GettyTGNObject} object.
	 */
	public GettyTGNObject getPrimaryObject(String primaryDivision) { 

		GettyTGNObject retval = null;
		
		if (primaryCache.containsKey(primaryDivision)) { 
			retval = primaryCache.get(primaryDivision);
		} else { 

			// See: http://vocabsservices.getty.edu/Schemas/TGN/tgn_place_type.xsd for place types
			String placeTypeID = "81100";
			// See documentation in: https://www.getty.edu/research/tools/vocabularies/vocab_web_services.pdf
			String baseURI = serviceBase + "TGNGetTermMatch?";

			StringBuilder request = new StringBuilder();
			request.append(baseURI);
			request.append("name=").append(primaryDivision);
			request.append("&placetypeid=").append(placeTypeID);
			request.append("&nationid=").append("");
			try {
				Vocabulary response = fetchVocabulary(request.toString());
				System.out.println(response.getCount());
				if (response.getCount()==BigInteger.ONE) { 
					// found match
				} 
				List<Subject> subjects = response.getSubject();
				Iterator<Subject> i = subjects.iterator();
				while (i.hasNext()) {
					Subject subject = i.next();
					System.out.println(subject.getPreferredTerm().getValue());
					System.out.println(subject.getSubjectID());
					System.out.println(subject.getPreferredParent());
					retval = new GettyTGNObject(subject,placeTypeID);
					primaryCache.put(primaryDivision, retval);
				}
			} catch (JAXBException e) {
				logger.error(e.getMessage());
			} catch (MalformedURLException e) {
				logger.error(e.getMessage());
			} catch (IOException e) {
				logger.error(e.getMessage());
			}	
		} 
		return retval;
	}
	
	/**
	 * <p>getPrimaryObject.</p>
	 *
	 * @param primaryDivision a {@link java.lang.String} object.
	 * @return a {@link edu.getty.tgn.service.GettyTGNObject} object.
	 * @throws SourceAuthorityException 
	 */
	public List<GettyTGNObject> getPrimaryObjects(String primaryDivision) throws SourceAuthorityException { 

		List<GettyTGNObject> retval = new ArrayList<GettyTGNObject>();
		
		if (uniquePrimaryCache.containsKey(primaryDivision)) { 
			logger.debug(uniquePrimaryCache.get(primaryDivision).getName());
			retval.add(uniquePrimaryCache.get(primaryDivision));
		} else { 
			// See: http://vocabsservices.getty.edu/Schemas/TGN/tgn_place_type.xsd for place types
			String placeTypeID = "81100";
			// See documentation in: https://www.getty.edu/research/tools/vocabularies/vocab_web_services.pdf
			String baseURI = serviceBase + "TGNGetTermMatch?";

			StringBuilder request = new StringBuilder();
			request.append(baseURI);
			String primaryEncoded = URI.encodeFragment('"'+primaryDivision+'"', false);
			request.append("name=").append(primaryEncoded);
			request.append("&placetypeid=").append(placeTypeID);
			request.append("&nationid=").append("");
			try {
				Vocabulary response = fetchVocabulary(request.toString());
				System.out.println(response.getCount());
				if (response.getCount()==BigInteger.ONE) { 
					// found match
				} 
				List<Subject> subjects = response.getSubject();
				Iterator<Subject> i = subjects.iterator();
				while (i.hasNext()) {
					Subject subject = i.next();
					logger.debug(subject.getPreferredTerm().getValue());
					logger.debug(subject.getSubjectID());
					logger.debug(subject.getPreferredParent());
					retval.add(new GettyTGNObject(subject,placeTypeID));
				}
			} catch (JAXBException e) {
				logger.error(e.getMessage());
				throw new SourceAuthorityException("Error interpreting json response returned from Getty TGN:" + e.getMessage());
			} catch (MalformedURLException e) {
				logger.error(e.getMessage());
			} catch (IOException e) {
				logger.error(e.getMessage());
				throw new SourceAuthorityException("Error accessing Getty TGN:" + e.getMessage());
			}	
		} 
		return retval;
	}
	
	/**
	 * Look up the name of the immediate parent of a subject in the Getty TGN, makes two 
	 * requests, TGNGetParents to find the parent subject ID, then TGNGetSubject to find 
	 * the name of the parent.
	 * 
	 * @param subjectid the Getty TGN subject ID of the subject for which to find the parent.
	 * @return the term text for the parent, or an empty string if no parent was found or on 
	 *   an error querying the Getty TGN.
	 */
	public String lookupParent(String subjectid) { 
		
		String retval = "";
		
		String baseURI = serviceBase + "TGNGetParents?";
		
		StringBuilder request = new StringBuilder();
		request.append(baseURI);
		request.append("subjectID=").append(subjectid);
		logger.debug(request);
    	try {
    		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			try {
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document document = fetchDocument(builder, request.toString());
				NodeList nodes = document.getElementsByTagName("Parent_Subject_ID");
				if (nodes.getLength() > 0) { 
					logger.debug(nodes.item(0).getTextContent());
					String parentid = nodes.item(0).getTextContent();

					baseURI = serviceBase + "TGNGetSubject?";
					request = new StringBuilder();
					request.append(baseURI);
					request.append("subjectID=").append(parentid);
					document = fetchDocument(builder, request.toString());
					nodes = document.getElementsByTagName("Term_Text");
					if (nodes.getLength()>0) { 
						logger.debug(nodes.item(0).getTextContent());
						retval = nodes.item(0).getTextContent();
					} else { 
						logger.debug(nodes.getLength());
					}
				} else { 
					logger.debug(nodes.getLength());
				}
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
    	return retval;
    	
	}
	
	/**
	 * Find the sovereign nation level entities in the Getty TGN that match a country name.
	 * Always queries the Getty TGN, does not consult the cache of primary divisions, as a 
	 * country name may also be the name of a primary division (e.g. Georgia).
	 *
	 * @param country the name of the country to look up.
	 * @return a list of the matching nation level entities, empty if there are no matches or if
	 *   country is empty (in which case the Getty TGN is not queried).
	 * @throws SourceAuthorityException on an error querying the Getty TGN or interpreting its response.
	 */
	public List<GettyTGNObject> getCountryObjects(String country) throws SourceAuthorityException { 

		List<GettyTGNObject> retval = new ArrayList<GettyTGNObject>();
		
		// Note: uniquePrimaryCache holds state/province objects, it must not be consulted here, 
		// as a country name may also be the name of a primary division (e.g. Georgia).
		if (!GEOUtil.isEmpty(country)) { 
			// See: http://vocabsservices.getty.edu/Schemas/TGN/tgn_place_type.xsd for place types
			String placeTypeID = "81011";
			// See documentation in: https://www.getty.edu/research/tools/vocabularies/vocab_web_services.pdf
			String baseURI = serviceBase + "TGNGetTermMatch?";

			StringBuilder request = new StringBuilder();
			request.append(baseURI);
			String primaryEncoded = URI.encodeFragment('"'+country+'"', false);
			request.append("name=").append(primaryEncoded);
			request.append("&placetypeid=").append(placeTypeID);
			request.append("&nationid=").append("");
			try {
				Vocabulary response = fetchVocabulary(request.toString());
				System.out.println(response.getCount());
				if (response.getCount()==BigInteger.ONE) { 
					// found match
				} 
				List<Subject> subjects = response.getSubject();
				Iterator<Subject> i = subjects.iterator();
				while (i.hasNext()) {
					Subject subject = i.next();
					logger.debug(subject.getPreferredTerm().getValue());
					logger.debug(subject.getSubjectID());
					logger.debug(subject.getPreferredParent());
					retval.add(new GettyTGNObject(subject,placeTypeID));
				}
			} catch (JAXBException e) {
				logger.error(e.getMessage());
				throw new SourceAuthorityException("Error interpreting json response returned from Getty TGN:" + e.getMessage());
			} catch (MalformedURLException e) {
				logger.error(e.getMessage());
			} catch (IOException e) {
				logger.error(e.getMessage());
				throw new SourceAuthorityException("Error accessing Getty TGN:" + e.getMessage());
			}	
		} 
		return retval;
	}
		
	
}
