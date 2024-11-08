
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
 *         &lt;element name="FindWaterBodiesWithinLocalityResult" type="{http://geo-locate.org/webservices/}ArrayOfString" minOccurs="0"/&gt;
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
    "findWaterBodiesWithinLocalityResult"
})
@XmlRootElement(name = "FindWaterBodiesWithinLocalityResponse")
public class FindWaterBodiesWithinLocalityResponse {

    @XmlElement(name = "FindWaterBodiesWithinLocalityResult")
    protected ArrayOfString findWaterBodiesWithinLocalityResult;

    /**
     * Gets the value of the findWaterBodiesWithinLocalityResult property.
     *
     * @return a {@link org.geolocate.webservices.svcv2.ArrayOfString} object.
     */
    public ArrayOfString getFindWaterBodiesWithinLocalityResult() {
        return findWaterBodiesWithinLocalityResult;
    }

    /**
     * Sets the value of the findWaterBodiesWithinLocalityResult property.
     *
     * @param value
     *     allowed object is
     *     {@link org.geolocate.webservices.svcv2.ArrayOfString}
     */
    public void setFindWaterBodiesWithinLocalityResult(ArrayOfString value) {
        this.findWaterBodiesWithinLocalityResult = value;
    }

}
