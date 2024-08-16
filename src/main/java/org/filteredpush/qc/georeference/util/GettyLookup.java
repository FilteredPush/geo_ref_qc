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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.emf.common.util.URI;

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
	
	private Map<String,GettyTGNObject> countryCache;
	private Map<String,GettyTGNObject> primaryCache;
	
	/** Constant <code>GETTY_TGN="The Getty Thesaurus of Geographic Names"{trunked}</code> */
	public static final String GETTY_TGN = "The Getty Thesaurus of Geographic Names (TGN)";
	
	/**
	 * Default constructor
	 */
	public GettyLookup() { 
		init();
	}
	
	/** 
	 * Set up cache objects
	 */
	private void init() { 
		countryCache = new HashMap<String,GettyTGNObject>();
		primaryCache = new HashMap<String,GettyTGNObject>();
	}

	/**
	 * Match a country name against the list of sovereign nations in the Getty TGN.
	 *
	 * @param country a {@link java.lang.String} object.
	 * @return true if the country is found as a sovereign nation level entity in TGN matching
	 * any form of the name, false if the country is not found as a sovereign nation lavel entity
	 * in TGN, null on an exception querying TGN.
	 */
	public Boolean lookupCountry(String country) { 

		Boolean retval = null;

		if (GEOUtil.isEmpty(country)) { 
			retval = false;
		} else { 
			if (countryCache.containsKey(country) && countryCache.get(country)!=null) { 
				retval =  true;
			} else { 

				String sovereignNationPlaceTypeID = "81011";
				String baseURI = "http://vocabsservices.getty.edu//TGNService.asmx/TGNGetTermMatch?";

				StringBuilder request = new StringBuilder();
				request.append(baseURI);
				String countryEncoded = URI.encodeFragment(country, false);
				request.append("name=").append(countryEncoded);
				request.append("&placetypeid=").append(sovereignNationPlaceTypeID);
				request.append("&nationid=").append("");
				logger.debug(request.toString());
				try {
					URL url = new URL(request.toString());
					HttpURLConnection getty = (HttpURLConnection) url.openConnection();
					InputStream is = getty.getInputStream();
					JAXBContext jc = JAXBContext.newInstance(Vocabulary.class);
					Unmarshaller unmarshaler = jc.createUnmarshaller();
					Vocabulary response = (Vocabulary) unmarshaler.unmarshal(is);
					System.out.println(response.getCount());
					System.out.println(response.getCount());
					if (response.getCount().compareTo(BigInteger.ONE)==0) { 
						// idiom for line above from BigInteger docs: (x.compareTo(y) <op> 0)
						// one match
						retval = true;
						if (!countryCache.containsKey(country)) { 
							countryCache.put("country", new GettyTGNObject(response.getSubject().get(0),sovereignNationPlaceTypeID));
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
		} 
	
		return retval;
	} 
	
	/**
	 * Lookup a country by preferred name only in the Getty TGN.
	 *
	 * @param country preferred name of sovereign nation level entity to look up.
	 * @return true if the provided country matches a preferred name of a sovereign nation level
	 * entity in TGN, false if it does not, null on an error querying the TGN service.
	 */
	public Boolean lookupCountryExact(String country) { 

		Boolean retval = null;

		if (GEOUtil.isEmpty(country)) { 
			retval = false;
		} else { 

			String sovereignNationPlaceTypeID = "81011";
			String baseURI = "http://vocabsservices.getty.edu//TGNService.asmx/TGNGetTermMatch?";

			StringBuilder request = new StringBuilder();
			request.append(baseURI);
			String countryEncoded = URI.encodeFragment(country, false);
			request.append("name=").append(countryEncoded);
			request.append("&placetypeid=").append(sovereignNationPlaceTypeID);
			request.append("&nationid=").append("");
			logger.debug(request.toString());
			try {
				URL url = new URL(request.toString());
				HttpURLConnection getty = (HttpURLConnection) url.openConnection();
				InputStream is = getty.getInputStream();
				JAXBContext jc = JAXBContext.newInstance(Vocabulary.class);
				Unmarshaller unmarshaler = jc.createUnmarshaller();
				Vocabulary response = (Vocabulary) unmarshaler.unmarshal(is);
				System.out.println(response.getCount());
				System.out.println(response.getCount());
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

				String sovereignNationPlaceTypeID = "81011";
				String baseURI = "http://vocabsservices.getty.edu//TGNService.asmx/TGNGetTermMatch?";

				StringBuilder request = new StringBuilder();
				request.append(baseURI);
				String countryEncoded = URI.encodeFragment(country, false);
				request.append("name=").append(countryEncoded);
				request.append("&placetypeid=").append(sovereignNationPlaceTypeID);
				request.append("&nationid=").append("");
				logger.debug(request.toString());
				try {
					URL url = new URL(request.toString());
					HttpURLConnection getty = (HttpURLConnection) url.openConnection();
					InputStream is = getty.getInputStream();
					JAXBContext jc = JAXBContext.newInstance(Vocabulary.class);
					Unmarshaller unmarshaler = jc.createUnmarshaller();
					Vocabulary response = (Vocabulary) unmarshaler.unmarshal(is);
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
							GeoUtilSingleton.getInstance().addGettyCountryLookupItem(country, retval);
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

		return retval;
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
			String placeTypeID = "81100"; //first level subdivision
			String baseURI = "http://vocabsservices.getty.edu//TGNService.asmx/TGNGetTermMatch?";

			StringBuilder request = new StringBuilder();
			request.append(baseURI);
			// enclose in quotes for exact match
			String primaryEncoded = URI.encodeFragment('"'+primaryDivision+'"', false);
			request.append("name=").append(primaryEncoded);
			request.append("&placetypeid=").append(placeTypeID);
			request.append("&nationid=").append("");
			logger.debug(request.toString());
			try {
				URL url = new URL(request.toString());
				HttpURLConnection getty = (HttpURLConnection) url.openConnection();
				InputStream is = getty.getInputStream();
				JAXBContext jc = JAXBContext.newInstance(Vocabulary.class);
				Unmarshaller unmarshaler = jc.createUnmarshaller();
				Vocabulary response = (Vocabulary) unmarshaler.unmarshal(is);
				System.out.println(response.getCount());
				System.out.println(response.getCount());
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

		return retval;
	} 
	
	
	
	public String getPreferredCountryName(String country) { 

		String retval = null;

		if (!GEOUtil.isEmpty(country)) { 

			if (countryCache.containsKey(country) && countryCache.get(country)!=null) { 
				retval = countryCache.get(country).getName();
			}
			
			String sovereignNationPlaceTypeID = "81011";
			String baseURI = "http://vocabsservices.getty.edu//TGNService.asmx/TGNGetTermMatch?";

			StringBuilder request = new StringBuilder();
			request.append(baseURI);
			request.append("name=").append(country.replace(" ", "+"));
			request.append("&placetypeid=").append(sovereignNationPlaceTypeID);
			request.append("&nationid=").append("");
			logger.debug(request.toString());
			try {
				URL url = new URL(request.toString());
				HttpURLConnection getty = (HttpURLConnection) url.openConnection();
				InputStream is = getty.getInputStream();
				JAXBContext jc = JAXBContext.newInstance(Vocabulary.class);
				Unmarshaller unmarshaler = jc.createUnmarshaller();
				Vocabulary response = (Vocabulary) unmarshaler.unmarshal(is);
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
	
	public String getParentageForPrimary(String primaryDivision) { 

		String retval = null;

		String placeTypeID = "81100";
		String baseURI = "http://vocabsservices.getty.edu//TGNService.asmx/TGNGetTermMatch?";

		StringBuilder request = new StringBuilder();
		request.append(baseURI);
		request.append("name=").append(primaryDivision);
		request.append("&placetypeid=").append(placeTypeID);
		request.append("&nationid=").append("");
		try {
			URL url = new URL(request.toString());
			HttpURLConnection getty = (HttpURLConnection) url.openConnection();
			InputStream is = getty.getInputStream();
			JAXBContext jc = JAXBContext.newInstance(Vocabulary.class);
			Unmarshaller unmarshaler = jc.createUnmarshaller();
			Vocabulary response = (Vocabulary) unmarshaler.unmarshal(is);
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
	
	public GettyTGNObject getPrimaryObject(String primaryDivision) { 

		GettyTGNObject retval = null;
		
		if (primaryCache.containsKey(primaryDivision)) { 
			retval = primaryCache.get(primaryDivision);
		} else { 

			String placeTypeID = "81100";
			String baseURI = "http://vocabsservices.getty.edu//TGNService.asmx/TGNGetTermMatch?";

			StringBuilder request = new StringBuilder();
			request.append(baseURI);
			request.append("name=").append(primaryDivision);
			request.append("&placetypeid=").append(placeTypeID);
			request.append("&nationid=").append("");
			try {
				URL url = new URL(request.toString());
				HttpURLConnection getty = (HttpURLConnection) url.openConnection();
				InputStream is = getty.getInputStream();
				JAXBContext jc = JAXBContext.newInstance(Vocabulary.class);
				Unmarshaller unmarshaler = jc.createUnmarshaller();
				Vocabulary response = (Vocabulary) unmarshaler.unmarshal(is);
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
	
}
