
package org.geolocate.webservices.svcv2;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Georef_Result complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="Georef_Result"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="WGS84Coordinate" type="{http://geo-locate.org/webservices/}GeographicPoint"/&gt;
 *         &lt;element name="ParsePattern" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Precision" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Score" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="UncertaintyRadiusMeters" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="UncertaintyPolygon" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ReferenceLocation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="DisplacedDistanceMiles" type="{http://www.w3.org/2001/XMLSchema}double"/&gt;
 *         &lt;element name="DisplacedHeadingDegrees" type="{http://www.w3.org/2001/XMLSchema}double"/&gt;
 *         &lt;element name="Debug" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 * @author mole
 * @version $Id: $Id
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Georef_Result", propOrder = {
    "wgs84Coordinate",
    "parsePattern",
    "precision",
    "score",
    "uncertaintyRadiusMeters",
    "uncertaintyPolygon",
    "referenceLocation",
    "displacedDistanceMiles",
    "displacedHeadingDegrees",
    "debug"
})
public class GeorefResult {

    @XmlElement(name = "WGS84Coordinate", required = true)
    protected GeographicPoint wgs84Coordinate;
    @XmlElement(name = "ParsePattern")
    protected String parsePattern;
    @XmlElement(name = "Precision")
    protected String precision;
    @XmlElement(name = "Score")
    protected int score;
    @XmlElement(name = "UncertaintyRadiusMeters")
    protected String uncertaintyRadiusMeters;
    @XmlElement(name = "UncertaintyPolygon")
    protected String uncertaintyPolygon;
    @XmlElement(name = "ReferenceLocation")
    protected String referenceLocation;
    @XmlElement(name = "DisplacedDistanceMiles")
    protected double displacedDistanceMiles;
    @XmlElement(name = "DisplacedHeadingDegrees")
    protected double displacedHeadingDegrees;
    @XmlElement(name = "Debug")
    protected String debug;

    /**
     * Gets the value of the wgs84Coordinate property.
     *
     * @return a {@link org.geolocate.webservices.svcv2.GeographicPoint} object.
     */
    public GeographicPoint getWGS84Coordinate() {
        return wgs84Coordinate;
    }

    /**
     * Sets the value of the wgs84Coordinate property.
     *
     * @param value
     *     allowed object is
     *     {@link org.geolocate.webservices.svcv2.GeographicPoint}
     */
    public void setWGS84Coordinate(GeographicPoint value) {
        this.wgs84Coordinate = value;
    }

    /**
     * Gets the value of the parsePattern property.
     *
     * @return a {@link java.lang.String} object.
     */
    public String getParsePattern() {
        return parsePattern;
    }

    /**
     * Sets the value of the parsePattern property.
     *
     * @param value
     *     allowed object is
     *     {@link java.lang.String}
     */
    public void setParsePattern(String value) {
        this.parsePattern = value;
    }

    /**
     * Gets the value of the precision property.
     *
     * @return a {@link java.lang.String} object.
     */
    public String getPrecision() {
        return precision;
    }

    /**
     * Sets the value of the precision property.
     *
     * @param value
     *     allowed object is
     *     {@link java.lang.String}
     */
    public void setPrecision(String value) {
        this.precision = value;
    }

    /**
     * Gets the value of the score property.
     *
     * @return a int.
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets the value of the score property.
     *
     * @param value a int.
     */
    public void setScore(int value) {
        this.score = value;
    }

    /**
     * Gets the value of the uncertaintyRadiusMeters property.
     *
     * @return a {@link java.lang.String} object.
     */
    public String getUncertaintyRadiusMeters() {
        return uncertaintyRadiusMeters;
    }

    /**
     * Sets the value of the uncertaintyRadiusMeters property.
     *
     * @param value
     *     allowed object is
     *     {@link java.lang.String}
     */
    public void setUncertaintyRadiusMeters(String value) {
        this.uncertaintyRadiusMeters = value;
    }

    /**
     * Gets the value of the uncertaintyPolygon property.
     *
     * @return a {@link java.lang.String} object.
     */
    public String getUncertaintyPolygon() {
        return uncertaintyPolygon;
    }

    /**
     * Sets the value of the uncertaintyPolygon property.
     *
     * @param value
     *     allowed object is
     *     {@link java.lang.String}
     */
    public void setUncertaintyPolygon(String value) {
        this.uncertaintyPolygon = value;
    }

    /**
     * Gets the value of the referenceLocation property.
     *
     * @return a {@link java.lang.String} object.
     */
    public String getReferenceLocation() {
        return referenceLocation;
    }

    /**
     * Sets the value of the referenceLocation property.
     *
     * @param value
     *     allowed object is
     *     {@link java.lang.String}
     */
    public void setReferenceLocation(String value) {
        this.referenceLocation = value;
    }

    /**
     * Gets the value of the displacedDistanceMiles property.
     *
     * @return a double.
     */
    public double getDisplacedDistanceMiles() {
        return displacedDistanceMiles;
    }

    /**
     * Sets the value of the displacedDistanceMiles property.
     *
     * @param value a double.
     */
    public void setDisplacedDistanceMiles(double value) {
        this.displacedDistanceMiles = value;
    }

    /**
     * Gets the value of the displacedHeadingDegrees property.
     *
     * @return a double.
     */
    public double getDisplacedHeadingDegrees() {
        return displacedHeadingDegrees;
    }

    /**
     * Sets the value of the displacedHeadingDegrees property.
     *
     * @param value a double.
     */
    public void setDisplacedHeadingDegrees(double value) {
        this.displacedHeadingDegrees = value;
    }

    /**
     * Gets the value of the debug property.
     *
     * @return a {@link java.lang.String} object.
     */
    public String getDebug() {
        return debug;
    }

    /**
     * Sets the value of the debug property.
     *
     * @param value
     *     allowed object is
     *     {@link java.lang.String}
     */
    public void setDebug(String value) {
        this.debug = value;
    }

}
