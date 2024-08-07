
package org.geolocate.webservices.svcv2;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LocalityDescription complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="LocalityDescription"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="LocalityString" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Country" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="State" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="County" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
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
@XmlType(name = "LocalityDescription", propOrder = {
    "localityString",
    "country",
    "state",
    "county"
})
public class LocalityDescription {

    @XmlElement(name = "LocalityString")
    protected String localityString;
    @XmlElement(name = "Country")
    protected String country;
    @XmlElement(name = "State")
    protected String state;
    @XmlElement(name = "County")
    protected String county;

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

}
