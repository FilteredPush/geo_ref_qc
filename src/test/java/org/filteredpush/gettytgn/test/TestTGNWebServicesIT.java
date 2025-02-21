package org.filteredpush.gettytgn.test;
/**
 * TestTGNWebServicesIT.java
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.filteredpush.qc.georeference.util.GeoUtilSingleton;
import org.filteredpush.qc.georeference.util.GettyLookup;

import edu.getty.tgn.objects.Vocabulary;
import edu.getty.tgn.objects.Vocabulary.Subject;
import edu.getty.tgn.objects.Vocabulary.Subject.Term;
import edu.getty.tgn.service.TGNWebServices;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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
	
	@Test
	public void testLookupParent() { 
		String subjectid = "7007517"; // Massachusetts
		GettyLookup lookup = GeoUtilSingleton.getInstance().getGettyLookup();
		String result = lookup.lookupParent(subjectid);
		assertEquals("United States",result);
		
	}
	
	@Test
	public void testLookupParentage() { 
	
		String subjectid = "7007517"; // Massachusetts
		
		String baseURI = "http://vocabsservices.getty.edu/TGNService.asmx/TGNGetParents?";
		
		StringBuilder request = new StringBuilder();
		request.append(baseURI);
		request.append("subjectID=").append(subjectid);
		logger.debug(request);
    	try {
    		URL url = new URL(request.toString());
    		HttpURLConnection getty = (HttpURLConnection) url.openConnection();
    		InputStream is = getty.getInputStream();
    		
    		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			try {
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document document = builder.parse(is);
				NodeList nodes = document.getElementsByTagName("Parent_Subject_ID");
				if (nodes.getLength() > 0) { 
					logger.debug(nodes.item(0).getTextContent());
					String parentid = nodes.item(0).getTextContent();

					baseURI = "http://vocabsservices.getty.edu/TGNService.asmx/TGNGetSubject?";
					request = new StringBuilder();
					request.append(baseURI);
					request.append("subjectID=").append(parentid);
					url = new URL(request.toString());
					getty = (HttpURLConnection) url.openConnection();
					is = getty.getInputStream();
					builder = factory.newDocumentBuilder();
					document = builder.parse(is);
					nodes = document.getElementsByTagName("Term_Text");
					if (nodes.getLength()>0) { 
						logger.debug(nodes.item(0).getTextContent());

						assertEquals("United States",nodes.item(0).getTextContent());
					} else { 
						logger.debug(nodes.getLength());
						fail();
					}
				} else { 
					logger.debug(nodes.getLength());
					fail();
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
		
	}
	
}
