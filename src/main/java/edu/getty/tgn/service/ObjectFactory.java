
package edu.getty.tgn.service;

import javax.xml.namespace.QName;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the edu.getty.tgn.service package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ArrayOfListResults_QNAME = new QName("http://vocabsservices.getty.edu/", "ArrayOfList_Results");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: edu.getty.tgn.service
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TGNGetNations }
     * 
     */
    public TGNGetNations createTGNGetNations() {
        return new TGNGetNations();
    }

    /**
     * Create an instance of {@link TGNGetNationsResponse }
     * 
     */
    public TGNGetNationsResponse createTGNGetNationsResponse() {
        return new TGNGetNationsResponse();
    }

    /**
     * Create an instance of {@link ArrayOfListResults }
     * 
     */
    public ArrayOfListResults createArrayOfListResults() {
        return new ArrayOfListResults();
    }

    /**
     * Create an instance of {@link TGNGetPtypes }
     * 
     */
    public TGNGetPtypes createTGNGetPtypes() {
        return new TGNGetPtypes();
    }

    /**
     * Create an instance of {@link TGNGetPtypesResponse }
     * 
     */
    public TGNGetPtypesResponse createTGNGetPtypesResponse() {
        return new TGNGetPtypesResponse();
    }

    /**
     * Create an instance of {@link ListResults }
     * 
     */
    public ListResults createListResults() {
        return new ListResults();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfListResults }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ArrayOfListResults }{@code >}
     */
    @XmlElementDecl(namespace = "http://vocabsservices.getty.edu/", name = "ArrayOfList_Results")
    public JAXBElement<ArrayOfListResults> createArrayOfListResults(ArrayOfListResults value) {
        return new JAXBElement<ArrayOfListResults>(_ArrayOfListResults_QNAME, ArrayOfListResults.class, null, value);
    }

}
