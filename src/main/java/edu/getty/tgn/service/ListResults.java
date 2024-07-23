
package edu.getty.tgn.service;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for List_Results complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="List_Results"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="list_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="list_value" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
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
@XmlType(name = "List_Results", propOrder = {
    "listId",
    "listValue"
})
public class ListResults {

    @XmlElement(name = "list_id")
    protected String listId;
    @XmlElement(name = "list_value")
    protected String listValue;

    /**
     * Gets the value of the listId property.
     *
     * @return a {@link java.lang.String} object.
     */
    public String getListId() {
        return listId;
    }

    /**
     * Sets the value of the listId property.
     *
     * @param value
     *     allowed object is
     *     {@link java.lang.String}
     */
    public void setListId(String value) {
        this.listId = value;
    }

    /**
     * Gets the value of the listValue property.
     *
     * @return a {@link java.lang.String} object.
     */
    public String getListValue() {
        return listValue;
    }

    /**
     * Sets the value of the listValue property.
     *
     * @param value
     *     allowed object is
     *     {@link java.lang.String}
     */
    public void setListValue(String value) {
        this.listValue = value;
    }

}
