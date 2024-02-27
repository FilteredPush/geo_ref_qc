package org.filterepush.gettytgn.test;
/**
 * TestTGNWebServicesIT.java
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.getty.tgn.objects.Vocabulary;
import edu.getty.tgn.objects.Vocabulary.Subject;
import edu.getty.tgn.objects.Vocabulary.Subject.Term;
import edu.getty.tgn.service.TGNWebServices;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import org.junit.Test;

/**
 * @author mole
 *
 */
public class TestTGNWebServicesIT {

	private static final Log logger = LogFactory.getLog(TestTGNWebServicesIT.class);
	
	@Test
	public void testTGNGetNations() { 
    	TGNWebServices tgn = new TGNWebServices();
    	try {
			edu.getty.tgn.service.ArrayOfListResults retval = tgn.getTGNWebServicesSoap().tgnGetNations("", "");
			List<edu.getty.tgn.service.ListResults> retList =  retval.getListResults();
			assertTrue(retList.size()>0);
			Iterator<edu.getty.tgn.service.ListResults> i = retList.iterator();
			while (i.hasNext()) { 
				edu.getty.tgn.service.ListResults row = i.next();
				System.out.println(row.getListValue());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Test 
	public void testVocabularyObject() { 
		
		String country = "Uganda";
    	String sovereignNationPlaceTypeID = "81011";
		String baseURI = "http://vocabsservices.getty.edu//TGNService.asmx/TGNGetTermMatch?";
		
		StringBuilder request = new StringBuilder();
		request.append(baseURI);
		request.append("name=").append(country);
		request.append("&placetypeid=").append(sovereignNationPlaceTypeID);
		request.append("&nationid=").append("");
    	try {
    		URL url = new URL(request.toString());
    		HttpURLConnection getty = (HttpURLConnection) url.openConnection();
    		InputStream is = getty.getInputStream();
    		JAXBContext jc = JAXBContext.newInstance(Vocabulary.class);
			Unmarshaller unmarshaler = jc.createUnmarshaller();
			Vocabulary response = (Vocabulary) unmarshaler.unmarshal(is);
			System.out.println(response.getCount());
			assertEquals(BigInteger.ONE,response.getCount());
			List<Subject> subjects = response.getSubject();
			Iterator<Subject> i = subjects.iterator();
			while (i.hasNext()) {
				Subject subject = i.next();
				System.out.println(subject.getPreferredTerm().getValue());
				assertEquals("Uganda (nation)", subject.getPreferredTerm().getValue());
				System.out.println(subject.getSubjectID());
				System.out.println(subject.getPreferredParent());
				List<Term> terms = subject.getTerm();
				Iterator<Term> it = terms.iterator();
				while (it.hasNext()) { 
					Term term = it.next();
					System.out.println(term.getValue());
				}
			}
			country = "República+de+Uganda";	
			request = new StringBuilder();
			request.append(baseURI);
			request.append("name=").append(country);
			request.append("&placetypeid=").append(sovereignNationPlaceTypeID);
			request.append("&nationid=").append("");
			
    		url = new URL(request.toString());
    		getty = (HttpURLConnection) url.openConnection();
    		is = getty.getInputStream();
			response = (Vocabulary) unmarshaler.unmarshal(is);
			System.out.println(response.getCount());
			assertEquals(BigInteger.ONE,response.getCount());
			subjects = response.getSubject();
			i = subjects.iterator();
			while (i.hasNext()) {
				Subject subject = i.next();
				System.out.println(subject.getPreferredTerm().getValue());
				assertEquals("Uganda (nation)", subject.getPreferredTerm().getValue());
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
