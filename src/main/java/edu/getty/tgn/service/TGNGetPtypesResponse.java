
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
 *         &lt;element name="TGNGetPtypesResult" type="{http://vocabsservices.getty.edu/}ArrayOfList_Results" minOccurs="0"/&gt;
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
    "tgnGetPtypesResult"
})
@XmlRootElement(name = "TGNGetPtypesResponse")
public class TGNGetPtypesResponse {

    @XmlElement(name = "TGNGetPtypesResult")
    protected ArrayOfListResults tgnGetPtypesResult;

    /**
     * Gets the value of the tgnGetPtypesResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfListResults }
     *     
     */
    public ArrayOfListResults getTGNGetPtypesResult() {
        return tgnGetPtypesResult;
    }

    /**
     * Sets the value of the tgnGetPtypesResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfListResults }
     *     
     */
    public void setTGNGetPtypesResult(ArrayOfListResults value) {
        this.tgnGetPtypesResult = value;
    }

}
