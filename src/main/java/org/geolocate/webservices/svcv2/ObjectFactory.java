
package org.geolocate.webservices.svcv2;

import javax.xml.namespace.QName;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the org.geolocate.webservices.svcv2 package.
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

    private final static QName _GeorefResultSet_QNAME = new QName("http://geo-locate.org/webservices/", "Georef_Result_Set");
    private final static QName _GeographicPoint_QNAME = new QName("http://geo-locate.org/webservices/", "GeographicPoint");
    private final static QName _String_QNAME = new QName("http://geo-locate.org/webservices/", "string");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.geolocate.webservices.svcv2
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link org.geolocate.webservices.svcv2.FindWaterBodiesWithinLocality}
     *
     * @return a {@link org.geolocate.webservices.svcv2.FindWaterBodiesWithinLocality} object.
     */
    public FindWaterBodiesWithinLocality createFindWaterBodiesWithinLocality() {
        return new FindWaterBodiesWithinLocality();
    }

    /**
     * Create an instance of {@link org.geolocate.webservices.svcv2.LocalityDescription}
     *
     * @return a {@link org.geolocate.webservices.svcv2.LocalityDescription} object.
     */
    public LocalityDescription createLocalityDescription() {
        return new LocalityDescription();
    }

    /**
     * Create an instance of {@link org.geolocate.webservices.svcv2.FindWaterBodiesWithinLocalityResponse}
     *
     * @return a {@link org.geolocate.webservices.svcv2.FindWaterBodiesWithinLocalityResponse} object.
     */
    public FindWaterBodiesWithinLocalityResponse createFindWaterBodiesWithinLocalityResponse() {
        return new FindWaterBodiesWithinLocalityResponse();
    }

    /**
     * Create an instance of {@link org.geolocate.webservices.svcv2.ArrayOfString}
     *
     * @return a {@link org.geolocate.webservices.svcv2.ArrayOfString} object.
     */
    public ArrayOfString createArrayOfString() {
        return new ArrayOfString();
    }

    /**
     * Create an instance of {@link org.geolocate.webservices.svcv2.Georef}
     *
     * @return a {@link org.geolocate.webservices.svcv2.Georef} object.
     */
    public Georef createGeoref() {
        return new Georef();
    }

    /**
     * Create an instance of {@link org.geolocate.webservices.svcv2.GeorefResponse}
     *
     * @return a {@link org.geolocate.webservices.svcv2.GeorefResponse} object.
     */
    public GeorefResponse createGeorefResponse() {
        return new GeorefResponse();
    }

    /**
     * Create an instance of {@link org.geolocate.webservices.svcv2.GeorefResultSet}
     *
     * @return a {@link org.geolocate.webservices.svcv2.GeorefResultSet} object.
     */
    public GeorefResultSet createGeorefResultSet() {
        return new GeorefResultSet();
    }

    /**
     * Create an instance of {@link org.geolocate.webservices.svcv2.Georef2}
     *
     * @return a {@link org.geolocate.webservices.svcv2.Georef2} object.
     */
    public Georef2 createGeoref2() {
        return new Georef2();
    }

    /**
     * Create an instance of {@link org.geolocate.webservices.svcv2.Georef2Response}
     *
     * @return a {@link org.geolocate.webservices.svcv2.Georef2Response} object.
     */
    public Georef2Response createGeoref2Response() {
        return new Georef2Response();
    }

    /**
     * Create an instance of {@link org.geolocate.webservices.svcv2.Georef3}
     *
     * @return a {@link org.geolocate.webservices.svcv2.Georef3} object.
     */
    public Georef3 createGeoref3() {
        return new Georef3();
    }

    /**
     * Create an instance of {@link org.geolocate.webservices.svcv2.Georef3Response}
     *
     * @return a {@link org.geolocate.webservices.svcv2.Georef3Response} object.
     */
    public Georef3Response createGeoref3Response() {
        return new Georef3Response();
    }

    /**
     * Create an instance of {@link org.geolocate.webservices.svcv2.SnapPointToNearestFoundWaterBody}
     *
     * @return a {@link org.geolocate.webservices.svcv2.SnapPointToNearestFoundWaterBody} object.
     */
    public SnapPointToNearestFoundWaterBody createSnapPointToNearestFoundWaterBody() {
        return new SnapPointToNearestFoundWaterBody();
    }

    /**
     * Create an instance of {@link org.geolocate.webservices.svcv2.GeographicPoint}
     *
     * @return a {@link org.geolocate.webservices.svcv2.GeographicPoint} object.
     */
    public GeographicPoint createGeographicPoint() {
        return new GeographicPoint();
    }

    /**
     * Create an instance of {@link org.geolocate.webservices.svcv2.SnapPointToNearestFoundWaterBodyResponse}
     *
     * @return a {@link org.geolocate.webservices.svcv2.SnapPointToNearestFoundWaterBodyResponse} object.
     */
    public SnapPointToNearestFoundWaterBodyResponse createSnapPointToNearestFoundWaterBodyResponse() {
        return new SnapPointToNearestFoundWaterBodyResponse();
    }

    /**
     * Create an instance of {@link org.geolocate.webservices.svcv2.SnapPointToNearestFoundWaterBody2}
     *
     * @return a {@link org.geolocate.webservices.svcv2.SnapPointToNearestFoundWaterBody2} object.
     */
    public SnapPointToNearestFoundWaterBody2 createSnapPointToNearestFoundWaterBody2() {
        return new SnapPointToNearestFoundWaterBody2();
    }

    /**
     * Create an instance of {@link org.geolocate.webservices.svcv2.SnapPointToNearestFoundWaterBody2Response}
     *
     * @return a {@link org.geolocate.webservices.svcv2.SnapPointToNearestFoundWaterBody2Response} object.
     */
    public SnapPointToNearestFoundWaterBody2Response createSnapPointToNearestFoundWaterBody2Response() {
        return new SnapPointToNearestFoundWaterBody2Response();
    }

    /**
     * Create an instance of {@link org.geolocate.webservices.svcv2.CalcUncertaintyPoly}
     *
     * @return a {@link org.geolocate.webservices.svcv2.CalcUncertaintyPoly} object.
     */
    public CalcUncertaintyPoly createCalcUncertaintyPoly() {
        return new CalcUncertaintyPoly();
    }

    /**
     * Create an instance of {@link org.geolocate.webservices.svcv2.CalcUncertaintyPolyResponse}
     *
     * @return a {@link org.geolocate.webservices.svcv2.CalcUncertaintyPolyResponse} object.
     */
    public CalcUncertaintyPolyResponse createCalcUncertaintyPolyResponse() {
        return new CalcUncertaintyPolyResponse();
    }

    /**
     * Create an instance of {@link org.geolocate.webservices.svcv2.Georef2PlusBG}
     *
     * @return a {@link org.geolocate.webservices.svcv2.Georef2PlusBG} object.
     */
    public Georef2PlusBG createGeoref2PlusBG() {
        return new Georef2PlusBG();
    }

    /**
     * Create an instance of {@link org.geolocate.webservices.svcv2.Georef2PlusBGResponse}
     *
     * @return a {@link org.geolocate.webservices.svcv2.Georef2PlusBGResponse} object.
     */
    public Georef2PlusBGResponse createGeoref2PlusBGResponse() {
        return new Georef2PlusBGResponse();
    }

    /**
     * Create an instance of {@link org.geolocate.webservices.svcv2.GeorefResult}
     *
     * @return a {@link org.geolocate.webservices.svcv2.GeorefResult} object.
     */
    public GeorefResult createGeorefResult() {
        return new GeorefResult();
    }

    /**
     * Create an instance of {@link jakarta.xml.bind.JAXBElement}{@code <}{@link org.geolocate.webservices.svcv2.GeorefResultSet}{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return a {@link jakarta.xml.bind.JAXBElement} object.
     */
    @XmlElementDecl(namespace = "http://geo-locate.org/webservices/", name = "Georef_Result_Set")
    public JAXBElement<GeorefResultSet> createGeorefResultSet(GeorefResultSet value) {
        return new JAXBElement<GeorefResultSet>(_GeorefResultSet_QNAME, GeorefResultSet.class, null, value);
    }

    /**
     * Create an instance of {@link jakarta.xml.bind.JAXBElement}{@code <}{@link org.geolocate.webservices.svcv2.GeographicPoint}{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return a {@link jakarta.xml.bind.JAXBElement} object.
     */
    @XmlElementDecl(namespace = "http://geo-locate.org/webservices/", name = "GeographicPoint")
    public JAXBElement<GeographicPoint> createGeographicPoint(GeographicPoint value) {
        return new JAXBElement<GeographicPoint>(_GeographicPoint_QNAME, GeographicPoint.class, null, value);
    }

    /**
     * Create an instance of {@link jakarta.xml.bind.JAXBElement}{@code <}{@link java.lang.String}{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return a {@link jakarta.xml.bind.JAXBElement} object.
     */
    @XmlElementDecl(namespace = "http://geo-locate.org/webservices/", name = "string")
    public JAXBElement<String> createString(String value) {
        return new JAXBElement<String>(_String_QNAME, String.class, null, value);
    }

}
