/**
 * GettyCountryLookup.java
 */
package org.filteredpush.qc.georeference.util;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.getty.tgn.objects.Vocabulary;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

/**
 * Check country names against the Getty Thesaurus of Geographic Names (TGN).
 * 
 * @author mole
 *
 */
public class GettyCountryLookup {

	private static final Log logger = LogFactory.getLog(GettyCountryLookup.class);

	/**
	 * Match a country name against the list of sovereign nations in the Getty TGN.
	 * 
	 * @param country
	 * @return true if the country is found as a sovereign nation level entity in TGN matching 
	 * any form of the name, false if the country is not found as a sovereign nation lavel entity
	 * in TGN, null on an exception querying TGN.
	 */
	public Boolean lookupCountry(String country) { 

		Boolean retval = null;
		String sovereignNationPlaceTypeID = "81011";
		String baseURI = "http://vocabsservices.getty.edu//TGNService.asmx/TGNGetTermMatch?";

		StringBuilder request = new StringBuilder();
		request.append(baseURI);
		request.append("name=").append(country);
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
				retval = true;
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
	
		return retval;
	} 
	
	/**
	 * Lookup a country by preferred name only in the Getty TGN.
	 * 
	 * @param country preferred name of sovereign nation level entity to look up.
	 * 
	 * @return true if the provided country matches a preferred name of a sovereign nation level
	 * entity in TGN, false if it does not, null on an error querying the TGN service.
	 */
	public Boolean lookupCountryExact(String country) { 

		Boolean retval = null;
		String sovereignNationPlaceTypeID = "81011";
		String baseURI = "http://vocabsservices.getty.edu//TGNService.asmx/TGNGetTermMatch?";

		StringBuilder request = new StringBuilder();
		request.append(baseURI);
		request.append("name=").append(country);
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
				if (country.equals(preferredTerm.replaceAll("\\([A-Za-z ]+\\)$", "").trim())) { 
					retval = false;
				} else {
					retval = true;
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
	
		return retval;
	} 
	
}
