
package org.geolocate.webservices.svcv2;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Georef_Result_Set complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Georef_Result_Set"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="EngineVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="NumResults" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="ExecutionTimems" type="{http://www.w3.org/2001/XMLSchema}double"/&gt;
 *         &lt;element name="ResultSet" type="{http://geo-locate.org/webservices/}Georef_Result" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Georef_Result_Set", propOrder = {
    "engineVersion",
    "numResults",
    "executionTimems",
    "resultSet"
})
public class GeorefResultSet {

    @XmlElement(name = "EngineVersion")
    protected String engineVersion;
    @XmlElement(name = "NumResults")
    protected int numResults;
    @XmlElement(name = "ExecutionTimems")
    protected double executionTimems;
    @XmlElement(name = "ResultSet")
    protected List<GeorefResult> resultSet;

    /**
     * Gets the value of the engineVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEngineVersion() {
        return engineVersion;
    }

    /**
     * Sets the value of the engineVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEngineVersion(String value) {
        this.engineVersion = value;
    }

    /**
     * Gets the value of the numResults property.
     * 
     */
    public int getNumResults() {
        return numResults;
    }

    /**
     * Sets the value of the numResults property.
     * 
     */
    public void setNumResults(int value) {
        this.numResults = value;
    }

    /**
     * Gets the value of the executionTimems property.
     * 
     */
    public double getExecutionTimems() {
        return executionTimems;
    }

    /**
     * Sets the value of the executionTimems property.
     * 
     */
    public void setExecutionTimems(double value) {
        this.executionTimems = value;
    }

    /**
     * Gets the value of the resultSet property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the resultSet property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResultSet().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GeorefResult }
     * 
     * 
     */
    public List<GeorefResult> getResultSet() {
        if (resultSet == null) {
            resultSet = new ArrayList<GeorefResult>();
        }
        return this.resultSet;
    }

}
