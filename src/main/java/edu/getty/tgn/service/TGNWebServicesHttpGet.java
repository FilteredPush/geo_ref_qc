
package edu.getty.tgn.service;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import jakarta.xml.bind.annotation.XmlSeeAlso;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 3.0.2
 * Generated source version: 3.0
 *
 * @author mole
 * @version $Id: $Id
 */
@WebService(name = "TGNWebServicesHttpGet", targetNamespace = "http://vocabsservices.getty.edu/")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    ObjectFactory.class
})
public interface TGNWebServicesHttpGet {


    /**
     * Returns full set of TGN Nations (internal Getty use only)
     *
     * @param nationText a {@link java.lang.String} object.
     * @param nationId a {@link java.lang.String} object.
     * @return a {@link edu.getty.tgn.service.ArrayOfListResults} object.
     */
    @WebMethod(operationName = "TGNGetNations")
    @WebResult(name = "ArrayOfList_Results", targetNamespace = "http://vocabsservices.getty.edu/", partName = "Body")
    public ArrayOfListResults tgnGetNations(
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "nation_id")
        String nationId,
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "nation_text")
        String nationText);

    /**
     * Returns full set of ULAN Place Types (internal Getty use only)
     *
     * @param ptypeText a {@link java.lang.String} object.
     * @param ptypeId a {@link java.lang.String} object.
     * @return a {@link edu.getty.tgn.service.ArrayOfListResults} object.
     */
    @WebMethod(operationName = "TGNGetPtypes")
    @WebResult(name = "ArrayOfList_Results", targetNamespace = "http://vocabsservices.getty.edu/", partName = "Body")
    public ArrayOfListResults tgnGetPtypes(
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "ptype_id")
        String ptypeId,
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "ptype_text")
        String ptypeText);

}
