
package org.geolocate.webservices.svcv2;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Country" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="State" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="County" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="LocalityString" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="WGS84Latitude" type="{http://www.w3.org/2001/XMLSchema}double"/&gt;
 *         &lt;element name="WGS84Longitude" type="{http://www.w3.org/2001/XMLSchema}double"/&gt;
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
@XmlType(name = "", propOrder = {
    "country",
    "state",
    "county",
    "localityString",
    "wgs84Latitude",
    "wgs84Longitude"
})
@XmlRootElement(name = "SnapPointToNearestFoundWaterBody2")
public class SnapPointToNearestFoundWaterBody2 {

    @XmlElement(name = "Country")
    protected String country;
    @XmlElement(name = "State")
    protected String state;
    @XmlElement(name = "County")
    protected String county;
    @XmlElement(name = "LocalityString")
    protected String localityString;
    @XmlElement(name = "WGS84Latitude")
    protected double wgs84Latitude;
    @XmlElement(name = "WGS84Longitude")
    protected double wgs84Longitude;

    /**
     * Gets the value of the country property.
     *
     * @return a {@link java.lang.String} object.
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the value of the country property.
     *
     * @param value
     *     allowed object is
     *     {@link java.lang.String}
     */
    public void setCountry(String value) {
        this.country = value;
    }

    /**
     * Gets the value of the state property.
     *
     * @return a {@link java.lang.String} object.
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the value of the state property.
     *
     * @param value
     *     allowed object is
     *     {@link java.lang.String}
     */
    public void setState(String value) {
        this.state = value;
    }

    /**
     * Gets the value of the county property.
     *
     * @return a {@link java.lang.String} object.
     */
    public String getCounty() {
        return county;
    }

    /**
     * Sets the value of the county property.
     *
     * @param value
     *     allowed object is
     *     {@link java.lang.String}
     */
    public void setCounty(String value) {
        this.county = value;
    }

    /**
     * Gets the value of the localityString property.
     *
     * @return a {@link java.lang.String} object.
     */
    public String getLocalityString() {
        return localityString;
    }

    /**
     * Sets the value of the localityString property.
     *
     * @param value
     *     allowed object is
     *     {@link java.lang.String}
     */
    public void setLocalityString(String value) {
        this.localityString = value;
    }

    /**
     * Gets the value of the wgs84Latitude property.
     *
     * @return a double.
     */
    public double getWGS84Latitude() {
        return wgs84Latitude;
    }

    /**
     * Sets the value of the wgs84Latitude property.
     *
     * @param value a double.
     */
    public void setWGS84Latitude(double value) {
        this.wgs84Latitude = value;
    }

    /**
     * Gets the value of the wgs84Longitude property.
     *
     * @return a double.
     */
    public double getWGS84Longitude() {
        return wgs84Longitude;
    }

    /**
     * Sets the value of the wgs84Longitude property.
     *
     * @param value a double.
     */
    public void setWGS84Longitude(double value) {
        this.wgs84Longitude = value;
    }

}
