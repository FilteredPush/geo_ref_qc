
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
 * @author mole
 * @version $Id: $Id
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ArrayOfListResults_QNAME = new QName("http://vocabsservices.getty.edu/", "ArrayOfList_Results");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: edu.getty.tgn.service
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link edu.getty.tgn.service.TGNGetNations}
     *
     * @return a {@link edu.getty.tgn.service.TGNGetNations} object.
     */
    public TGNGetNations createTGNGetNations() {
        return new TGNGetNations();
    }

    /**
     * Create an instance of {@link edu.getty.tgn.service.TGNGetNationsResponse}
     *
     * @return a {@link edu.getty.tgn.service.TGNGetNationsResponse} object.
     */
    public TGNGetNationsResponse createTGNGetNationsResponse() {
        return new TGNGetNationsResponse();
    }

    /**
     * Create an instance of {@link edu.getty.tgn.service.ArrayOfListResults}
     *
     * @return a {@link edu.getty.tgn.service.ArrayOfListResults} object.
     */
    public ArrayOfListResults createArrayOfListResults() {
        return new ArrayOfListResults();
    }

    /**
     * Create an instance of {@link edu.getty.tgn.service.TGNGetPtypes}
     *
     * @return a {@link edu.getty.tgn.service.TGNGetPtypes} object.
     */
    public TGNGetPtypes createTGNGetPtypes() {
        return new TGNGetPtypes();
    }

    /**
     * Create an instance of {@link edu.getty.tgn.service.TGNGetPtypesResponse}
     *
     * @return a {@link edu.getty.tgn.service.TGNGetPtypesResponse} object.
     */
    public TGNGetPtypesResponse createTGNGetPtypesResponse() {
        return new TGNGetPtypesResponse();
    }

    /**
     * Create an instance of {@link edu.getty.tgn.service.ListResults}
     *
     * @return a {@link edu.getty.tgn.service.ListResults} object.
     */
    public ListResults createListResults() {
        return new ListResults();
    }

    /**
     * Create an instance of {@link jakarta.xml.bind.JAXBElement}{@code <}{@link edu.getty.tgn.service.ArrayOfListResults}{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return a {@link jakarta.xml.bind.JAXBElement} object.
     */
    @XmlElementDecl(namespace = "http://vocabsservices.getty.edu/", name = "ArrayOfList_Results")
    public JAXBElement<ArrayOfListResults> createArrayOfListResults(ArrayOfListResults value) {
        return new JAXBElement<ArrayOfListResults>(_ArrayOfListResults_QNAME, ArrayOfListResults.class, null, value);
    }

}
