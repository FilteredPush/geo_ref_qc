//
// This file was generated by the Eclipse Implementation of JAXB, v4.0.3 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
//


package edu.getty.tgn.objects;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>{@code
 * <complexType>
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Count" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         <element name="Subject" maxOccurs="unbounded">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <sequence>
 *                   <element name="Preferred_Term">
 *                     <complexType>
 *                       <simpleContent>
 *                         <extension base="<http://www.w3.org/2001/XMLSchema>string">
 *                           <attribute name="termid" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *                         </extension>
 *                       </simpleContent>
 *                     </complexType>
 *                   </element>
 *                   <element name="Preferred_Parent" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   <element name="Subject_ID" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *                   <element name="Term" maxOccurs="unbounded" minOccurs="0">
 *                     <complexType>
 *                       <simpleContent>
 *                         <extension base="<http://www.w3.org/2001/XMLSchema>string">
 *                           <attribute name="termid" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *                         </extension>
 *                       </simpleContent>
 *                     </complexType>
 *                   </element>
 *                 </sequence>
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 *
 * @author mole
 * @version $Id: $Id
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "count",
    "subject"
})
@XmlRootElement(name = "Vocabulary")
public class Vocabulary {

    @XmlElement(name = "Count", required = true)
    protected BigInteger count;
    @XmlElement(name = "Subject", required = true)
    protected List<Vocabulary.Subject> subject;

    /**
     * Gets the value of the count property.
     *
     * @return a {@link java.math.BigInteger} object.
     */
    public BigInteger getCount() {
        return count;
    }

    /**
     * Sets the value of the count property.
     *
     * @param value
     *     allowed object is
     *     {@link java.math.BigInteger}
     */
    public void setCount(BigInteger value) {
        this.count = value;
    }

    /**
     * Gets the value of the subject property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the subject property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSubject().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link edu.getty.tgn.objects.Vocabulary.Subject}
     *
     * @return a {@link java.util.List} object.
     */
    public List<Vocabulary.Subject> getSubject() {
        if (subject == null) {
            subject = new ArrayList<>();
        }
        return this.subject;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>{@code
     * <complexType>
     *   <complexContent>
     *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       <sequence>
     *         <element name="Preferred_Term">
     *           <complexType>
     *             <simpleContent>
     *               <extension base="<http://www.w3.org/2001/XMLSchema>string">
     *                 <attribute name="termid" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
     *               </extension>
     *             </simpleContent>
     *           </complexType>
     *         </element>
     *         <element name="Preferred_Parent" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         <element name="Subject_ID" type="{http://www.w3.org/2001/XMLSchema}integer"/>
     *         <element name="Term" maxOccurs="unbounded" minOccurs="0">
     *           <complexType>
     *             <simpleContent>
     *               <extension base="<http://www.w3.org/2001/XMLSchema>string">
     *                 <attribute name="termid" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
     *               </extension>
     *             </simpleContent>
     *           </complexType>
     *         </element>
     *       </sequence>
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "preferredTerm",
        "preferredParent",
        "subjectID",
        "term"
    })
    public static class Subject {

        @XmlElement(name = "Preferred_Term", required = true)
        protected Vocabulary.Subject.PreferredTerm preferredTerm;
        @XmlElement(name = "Preferred_Parent", required = true)
        protected String preferredParent;
        @XmlElement(name = "Subject_ID", required = true)
        protected BigInteger subjectID;
        @XmlElement(name = "Term")
        protected List<Vocabulary.Subject.Term> term;

        /**
         * Gets the value of the preferredTerm property.
         * 
         * @return
         *     possible object is
         *     {@link Vocabulary.Subject.PreferredTerm }
         *     
         */
        public Vocabulary.Subject.PreferredTerm getPreferredTerm() {
            return preferredTerm;
        }

        /**
         * Sets the value of the preferredTerm property.
         * 
         * @param value
         *     allowed object is
         *     {@link Vocabulary.Subject.PreferredTerm }
         *     
         */
        public void setPreferredTerm(Vocabulary.Subject.PreferredTerm value) {
            this.preferredTerm = value;
        }

        /**
         * Gets the value of the preferredParent property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPreferredParent() {
            return preferredParent;
        }

        /**
         * Sets the value of the preferredParent property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPreferredParent(String value) {
            this.preferredParent = value;
        }

        /**
         * Gets the value of the subjectID property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getSubjectID() {
            return subjectID;
        }

        /**
         * Sets the value of the subjectID property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setSubjectID(BigInteger value) {
            this.subjectID = value;
        }

        /**
         * Gets the value of the term property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the Jakarta XML Binding object.
         * This is why there is not a {@code set} method for the term property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getTerm().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Vocabulary.Subject.Term }
         * 
         * 
         * @return
         *     The value of the term property.
         */
        public List<Vocabulary.Subject.Term> getTerm() {
            if (term == null) {
                term = new ArrayList<>();
            }
            return this.term;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>{@code
         * <complexType>
         *   <simpleContent>
         *     <extension base="<http://www.w3.org/2001/XMLSchema>string">
         *       <attribute name="termid" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
         *     </extension>
         *   </simpleContent>
         * </complexType>
         * }</pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "value"
        })
        public static class PreferredTerm {

            @XmlValue
            protected String value;
            @XmlAttribute(name = "termid")
            @XmlSchemaType(name = "anySimpleType")
            protected String termid;

            /**
             * Gets the value of the value property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getValue() {
                return value;
            }

            /**
             * Sets the value of the value property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setValue(String value) {
                this.value = value;
            }

            /**
             * Gets the value of the termid property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getTermid() {
                return termid;
            }

            /**
             * Sets the value of the termid property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setTermid(String value) {
                this.termid = value;
            }

        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>{@code
         * <complexType>
         *   <simpleContent>
         *     <extension base="<http://www.w3.org/2001/XMLSchema>string">
         *       <attribute name="termid" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
         *     </extension>
         *   </simpleContent>
         * </complexType>
         * }</pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "value"
        })
        public static class Term {

            @XmlValue
            protected String value;
            @XmlAttribute(name = "termid")
            @XmlSchemaType(name = "anySimpleType")
            protected String termid;

            /**
             * Gets the value of the value property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getValue() {
                return value;
            }

            /**
             * Sets the value of the value property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setValue(String value) {
                this.value = value;
            }

            /**
             * Gets the value of the termid property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getTermid() {
                return termid;
            }

            /**
             * Sets the value of the termid property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setTermid(String value) {
                this.termid = value;
            }

        }

    }

}
