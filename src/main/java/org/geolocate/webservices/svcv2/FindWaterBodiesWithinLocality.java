
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
 *         &lt;element name="LocalityDescription" type="{http://geo-locate.org/webservices/}LocalityDescription"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "localityDescription"
})
@XmlRootElement(name = "FindWaterBodiesWithinLocality")
public class FindWaterBodiesWithinLocality {

    @XmlElement(name = "LocalityDescription", required = true)
    protected LocalityDescription localityDescription;

    /**
     * Gets the value of the localityDescription property.
     * 
     * @return
     *     possible object is
     *     {@link LocalityDescription }
     *     
     */
    public LocalityDescription getLocalityDescription() {
        return localityDescription;
    }

    /**
     * Sets the value of the localityDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link LocalityDescription }
     *     
     */
    public void setLocalityDescription(LocalityDescription value) {
        this.localityDescription = value;
    }

}
