
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
 *         &lt;element name="Result" type="{http://geo-locate.org/webservices/}Georef_Result_Set"/&gt;
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
    "result"
})
@XmlRootElement(name = "Georef2Response")
public class Georef2Response {

    @XmlElement(name = "Result", required = true)
    protected GeorefResultSet result;

    /**
     * Gets the value of the result property.
     *
     * @return a {@link org.geolocate.webservices.svcv2.GeorefResultSet} object.
     */
    public GeorefResultSet getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     *
     * @param value
     *     allowed object is
     *     {@link org.geolocate.webservices.svcv2.GeorefResultSet}
     */
    public void setResult(GeorefResultSet value) {
        this.result = value;
    }

}
