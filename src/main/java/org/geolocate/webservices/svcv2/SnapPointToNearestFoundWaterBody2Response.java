
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
 *         &lt;element name="WGS84Coordinate" type="{http://geo-locate.org/webservices/}GeographicPoint"/&gt;
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
    "wgs84Coordinate"
})
@XmlRootElement(name = "SnapPointToNearestFoundWaterBody2Response")
public class SnapPointToNearestFoundWaterBody2Response {

    @XmlElement(name = "WGS84Coordinate", required = true)
    protected GeographicPoint wgs84Coordinate;

    /**
     * Gets the value of the wgs84Coordinate property.
     * 
     * @return
     *     possible object is
     *     {@link GeographicPoint }
     *     
     */
    public GeographicPoint getWGS84Coordinate() {
        return wgs84Coordinate;
    }

    /**
     * Sets the value of the wgs84Coordinate property.
     * 
     * @param value
     *     allowed object is
     *     {@link GeographicPoint }
     *     
     */
    public void setWGS84Coordinate(GeographicPoint value) {
        this.wgs84Coordinate = value;
    }

}
