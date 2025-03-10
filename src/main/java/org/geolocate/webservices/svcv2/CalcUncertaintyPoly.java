
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
 *         &lt;element name="PolyGenerationKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
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
    "polyGenerationKey"
})
@XmlRootElement(name = "CalcUncertaintyPoly")
public class CalcUncertaintyPoly {

    @XmlElement(name = "PolyGenerationKey")
    protected String polyGenerationKey;

    /**
     * Gets the value of the polyGenerationKey property.
     *
     * @return a {@link java.lang.String} object.
     */
    public String getPolyGenerationKey() {
        return polyGenerationKey;
    }

    /**
     * Sets the value of the polyGenerationKey property.
     *
     * @param value
     *     allowed object is
     *     {@link java.lang.String}
     */
    public void setPolyGenerationKey(String value) {
        this.polyGenerationKey = value;
    }

}
