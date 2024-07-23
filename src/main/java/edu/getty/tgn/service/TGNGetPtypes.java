
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
 *         &lt;element name="ptype_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ptype_text" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
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
    "ptypeId",
    "ptypeText"
})
@XmlRootElement(name = "TGNGetPtypes")
public class TGNGetPtypes {

    @XmlElement(name = "ptype_id")
    protected String ptypeId;
    @XmlElement(name = "ptype_text")
    protected String ptypeText;

    /**
     * Gets the value of the ptypeId property.
     *
     * @return a {@link java.lang.String} object.
     */
    public String getPtypeId() {
        return ptypeId;
    }

    /**
     * Sets the value of the ptypeId property.
     *
     * @param value
     *     allowed object is
     *     {@link java.lang.String}
     */
    public void setPtypeId(String value) {
        this.ptypeId = value;
    }

    /**
     * Gets the value of the ptypeText property.
     *
     * @return a {@link java.lang.String} object.
     */
    public String getPtypeText() {
        return ptypeText;
    }

    /**
     * Sets the value of the ptypeText property.
     *
     * @param value
     *     allowed object is
     *     {@link java.lang.String}
     */
    public void setPtypeText(String value) {
        this.ptypeText = value;
    }

}
