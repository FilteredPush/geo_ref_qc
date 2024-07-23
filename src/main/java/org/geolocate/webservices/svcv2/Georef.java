
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
 *         &lt;element name="LocalityDescription" type="{http://geo-locate.org/webservices/}LocalityDescription"/&gt;
 *         &lt;element name="HwyX" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="FindWaterbody" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="RestrictToLowestAdm" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="doUncert" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="doPoly" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="displacePoly" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="polyAsLinkID" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="LanguageKey" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
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
    "localityDescription",
    "hwyX",
    "findWaterbody",
    "restrictToLowestAdm",
    "doUncert",
    "doPoly",
    "displacePoly",
    "polyAsLinkID",
    "languageKey"
})
@XmlRootElement(name = "Georef")
public class Georef {

    @XmlElement(name = "LocalityDescription", required = true)
    protected LocalityDescription localityDescription;
    @XmlElement(name = "HwyX")
    protected boolean hwyX;
    @XmlElement(name = "FindWaterbody")
    protected boolean findWaterbody;
    @XmlElement(name = "RestrictToLowestAdm")
    protected boolean restrictToLowestAdm;
    protected boolean doUncert;
    protected boolean doPoly;
    protected boolean displacePoly;
    protected boolean polyAsLinkID;
    @XmlElement(name = "LanguageKey")
    protected int languageKey;

    /**
     * Gets the value of the localityDescription property.
     *
     * @return a {@link org.geolocate.webservices.svcv2.LocalityDescription} object.
     */
    public LocalityDescription getLocalityDescription() {
        return localityDescription;
    }

    /**
     * Sets the value of the localityDescription property.
     *
     * @param value
     *     allowed object is
     *     {@link org.geolocate.webservices.svcv2.LocalityDescription}
     */
    public void setLocalityDescription(LocalityDescription value) {
        this.localityDescription = value;
    }

    /**
     * Gets the value of the hwyX property.
     *
     * @return a boolean.
     */
    public boolean isHwyX() {
        return hwyX;
    }

    /**
     * Sets the value of the hwyX property.
     *
     * @param value a boolean.
     */
    public void setHwyX(boolean value) {
        this.hwyX = value;
    }

    /**
     * Gets the value of the findWaterbody property.
     *
     * @return a boolean.
     */
    public boolean isFindWaterbody() {
        return findWaterbody;
    }

    /**
     * Sets the value of the findWaterbody property.
     *
     * @param value a boolean.
     */
    public void setFindWaterbody(boolean value) {
        this.findWaterbody = value;
    }

    /**
     * Gets the value of the restrictToLowestAdm property.
     *
     * @return a boolean.
     */
    public boolean isRestrictToLowestAdm() {
        return restrictToLowestAdm;
    }

    /**
     * Sets the value of the restrictToLowestAdm property.
     *
     * @param value a boolean.
     */
    public void setRestrictToLowestAdm(boolean value) {
        this.restrictToLowestAdm = value;
    }

    /**
     * Gets the value of the doUncert property.
     *
     * @return a boolean.
     */
    public boolean isDoUncert() {
        return doUncert;
    }

    /**
     * Sets the value of the doUncert property.
     *
     * @param value a boolean.
     */
    public void setDoUncert(boolean value) {
        this.doUncert = value;
    }

    /**
     * Gets the value of the doPoly property.
     *
     * @return a boolean.
     */
    public boolean isDoPoly() {
        return doPoly;
    }

    /**
     * Sets the value of the doPoly property.
     *
     * @param value a boolean.
     */
    public void setDoPoly(boolean value) {
        this.doPoly = value;
    }

    /**
     * Gets the value of the displacePoly property.
     *
     * @return a boolean.
     */
    public boolean isDisplacePoly() {
        return displacePoly;
    }

    /**
     * Sets the value of the displacePoly property.
     *
     * @param value a boolean.
     */
    public void setDisplacePoly(boolean value) {
        this.displacePoly = value;
    }

    /**
     * Gets the value of the polyAsLinkID property.
     *
     * @return a boolean.
     */
    public boolean isPolyAsLinkID() {
        return polyAsLinkID;
    }

    /**
     * Sets the value of the polyAsLinkID property.
     *
     * @param value a boolean.
     */
    public void setPolyAsLinkID(boolean value) {
        this.polyAsLinkID = value;
    }

    /**
     * Gets the value of the languageKey property.
     *
     * @return a int.
     */
    public int getLanguageKey() {
        return languageKey;
    }

    /**
     * Sets the value of the languageKey property.
     *
     * @param value a int.
     */
    public void setLanguageKey(int value) {
        this.languageKey = value;
    }

}
