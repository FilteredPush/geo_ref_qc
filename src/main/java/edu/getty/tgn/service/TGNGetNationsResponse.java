
package edu.getty.tgn.service;

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
 *         &lt;element name="TGNGetNationsResult" type="{http://vocabsservices.getty.edu/}ArrayOfList_Results" minOccurs="0"/&gt;
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
    "tgnGetNationsResult"
})
@XmlRootElement(name = "TGNGetNationsResponse")
public class TGNGetNationsResponse {

    @XmlElement(name = "TGNGetNationsResult")
    protected ArrayOfListResults tgnGetNationsResult;

    /**
     * Gets the value of the tgnGetNationsResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfListResults }
     *     
     */
    public ArrayOfListResults getTGNGetNationsResult() {
        return tgnGetNationsResult;
    }

    /**
     * Sets the value of the tgnGetNationsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfListResults }
     *     
     */
    public void setTGNGetNationsResult(ArrayOfListResults value) {
        this.tgnGetNationsResult = value;
    }

}
