
package org.geolocate.webservices.svcv2;

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
 */
@WebService(name = "geolocatesvcHttpPost", targetNamespace = "http://geo-locate.org/webservices/")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    ObjectFactory.class
})
public interface GeolocatesvcHttpPost {


    /**
     * Georeferences a locality description. returns a 'Georef_Result_Set' given Country, State, County, LocalityString and boolean georeferencing options.  <br><b>Use this one if you are unsure of which to use.</b> <br>Language key refers to an integer representing different languages libraries. Will default to basic english (languagekey=0) if invalid key is provided. <br>*GLOBAL*
     * 
     * @param country
     * @param doPoly
     * @param displacePoly
     * @param county
     * @param localityString
     * @param doUncert
     * @param polyAsLinkID
     * @param restrictToLowestAdm
     * @param state
     * @param findWaterbody
     * @param languageKey
     * @param hwyX
     * @return
     *     returns org.geolocate.webservices.svcv2.GeorefResultSet
     */
    @WebMethod(operationName = "Georef2")
    @WebResult(name = "Georef_Result_Set", targetNamespace = "http://geo-locate.org/webservices/", partName = "Body")
    public GeorefResultSet georef2(
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "Country")
        String country,
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "State")
        String state,
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "County")
        String county,
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "LocalityString")
        String localityString,
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "HwyX")
        String hwyX,
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "FindWaterbody")
        String findWaterbody,
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "RestrictToLowestAdm")
        String restrictToLowestAdm,
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "doUncert")
        String doUncert,
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "doPoly")
        String doPoly,
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "displacePoly")
        String displacePoly,
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "polyAsLinkID")
        String polyAsLinkID,
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "LanguageKey")
        String languageKey);

    /**
     * Georeferences a locality description. returns a 'Georef_Result_Set' given vLocality, vGeorgraphy and boolean georeferencing options.  VLocality and VGeogrpahy are fields specific to BioGeomancer. <br>Language key refers to an integer representing different languages libraries. Will default to basic english (0) if invalid key is provided <br>*North American Localities Only*
     * 
     * @param vGeography
     * @param doPoly
     * @param displacePoly
     * @param vLocality
     * @param doUncert
     * @param polyAsLinkID
     * @param restrictToLowestAdm
     * @param findWaterbody
     * @param languageKey
     * @param hwyX
     * @return
     *     returns org.geolocate.webservices.svcv2.GeorefResultSet
     */
    @WebMethod(operationName = "Georef3")
    @WebResult(name = "Georef_Result_Set", targetNamespace = "http://geo-locate.org/webservices/", partName = "Body")
    public GeorefResultSet georef3(
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "vLocality")
        String vLocality,
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "vGeography")
        String vGeography,
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "HwyX")
        String hwyX,
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "FindWaterbody")
        String findWaterbody,
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "RestrictToLowestAdm")
        String restrictToLowestAdm,
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "doUncert")
        String doUncert,
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "doPoly")
        String doPoly,
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "displacePoly")
        String displacePoly,
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "polyAsLinkID")
        String polyAsLinkID,
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "LanguageKey")
        String languageKey);

    /**
     * Snaps given point to nearest water body found from given locality description terms. <br>*U.S. localities only, county required*
     * 
     * @param wgs84Latitude
     * @param country
     * @param wgs84Longitude
     * @param county
     * @param localityString
     * @param state
     * @return
     *     returns org.geolocate.webservices.svcv2.GeographicPoint
     */
    @WebMethod(operationName = "SnapPointToNearestFoundWaterBody2")
    @WebResult(name = "GeographicPoint", targetNamespace = "http://geo-locate.org/webservices/", partName = "Body")
    public GeographicPoint snapPointToNearestFoundWaterBody2(
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "Country")
        String country,
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "State")
        String state,
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "County")
        String county,
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "LocalityString")
        String localityString,
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "WGS84Latitude")
        String wgs84Latitude,
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "WGS84Longitude")
        String wgs84Longitude);

    /**
     * Returns an uncertainty polygon given the unique id used to generate it.
     * 
     * @param polyGenerationKey
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "CalcUncertaintyPoly")
    @WebResult(name = "string", targetNamespace = "http://geo-locate.org/webservices/", partName = "Body")
    public String calcUncertaintyPoly(
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "PolyGenerationKey")
        String polyGenerationKey);

    /**
     * Georeferences a locality description. returns a 'Georef_Result_Set' given Country, State, County, LocalityString and boolean georeferencing options.  Also adds results from Biogeomancer to the mix. May take a long time to get results back from BG. <br>Language key refers to an integer representing different languages libraries. Will default to basic english (languagekey=0) if invalid key is provided. <br>*GLOBAL*
     * 
     * @param country
     * @param doPoly
     * @param displacePoly
     * @param county
     * @param localityString
     * @param doUncert
     * @param polyAsLinkID
     * @param restrictToLowestAdm
     * @param state
     * @param findWaterbody
     * @param languageKey
     * @param hwyX
     * @return
     *     returns org.geolocate.webservices.svcv2.GeorefResultSet
     */
    @WebMethod(operationName = "Georef2plusBG")
    @WebResult(name = "Georef_Result_Set", targetNamespace = "http://geo-locate.org/webservices/", partName = "Body")
    public GeorefResultSet georef2PlusBG(
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "Country")
        String country,
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "State")
        String state,
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "County")
        String county,
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "LocalityString")
        String localityString,
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "HwyX")
        String hwyX,
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "FindWaterbody")
        String findWaterbody,
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "RestrictToLowestAdm")
        String restrictToLowestAdm,
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "doUncert")
        String doUncert,
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "doPoly")
        String doPoly,
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "displacePoly")
        String displacePoly,
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "polyAsLinkID")
        String polyAsLinkID,
        @WebParam(name = "string", targetNamespace = "http://www.w3.org/2001/XMLSchema", partName = "LanguageKey")
        String languageKey);

}
