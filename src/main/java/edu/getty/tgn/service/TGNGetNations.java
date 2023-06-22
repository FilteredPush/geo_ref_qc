
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
 *         &lt;element name="nation_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="nation_text" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
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
    "nationId",
    "nationText"
})
@XmlRootElement(name = "TGNGetNations")
public class TGNGetNations {

    @XmlElement(name = "nation_id")
    protected String nationId;
    @XmlElement(name = "nation_text")
    protected String nationText;

    /**
     * Gets the value of the nationId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNationId() {
        return nationId;
    }

    /**
     * Sets the value of the nationId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNationId(String value) {
        this.nationId = value;
    }

    /**
     * Gets the value of the nationText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNationText() {
        return nationText;
    }

    /**
     * Sets the value of the nationText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNationText(String value) {
        this.nationText = value;
    }

}
