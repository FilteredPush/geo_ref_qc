/**
 * GettyTGNObject.java
 */
package edu.getty.tgn.service;

import java.math.BigInteger;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.getty.tgn.objects.Vocabulary;
import edu.getty.tgn.objects.Vocabulary.Subject;
import jakarta.xml.bind.annotation.XmlElement;

/**
 * A structure to hold information about a Getty Thesaurus of geographic names record
 * This is a lighter weight version of Vocabulary.Subject, holding strings.
 */
public class GettyTGNObject {

	private static final Log logger = LogFactory.getLog(GettyTGNObject.class);

	private String name;  // preferredTerm without type
	private String nameWithType;  // preferredTerm
	private String subjectID;  // subjectID
	private String placeTypeID; // placeTypeID 
	private String parentageString;  // preferredParentage 
	
	/**
	 * Construct a populated GettyTGNObject from separate terms
	 * 
	 * @param name
	 * @param nameWithType
	 * @param subjectID
	 * @param placeTypeID
	 * @param parentageString
	 */
	public GettyTGNObject(String name, String nameWithType, String subjectID, String placeTypeID,
			String parentageString) {
		super();
		this.name = name;
		this.nameWithType = nameWithType;
		this.subjectID = subjectID;
		// See: http://vocabsservices.getty.edu/Schemas/TGN/tgn_place_type.xsd for list of place types
		this.placeTypeID = placeTypeID;
		this.parentageString = parentageString;
	}
	/**
	 * Construct an empty GettyTGNObject
	 */
	public GettyTGNObject() {
		// Auto-generated constructor stub
	}
	
	public GettyTGNObject(Subject subject, String placeTypeID) { 
		if (subject!=null) { 
			this.name = subject.getPreferredTerm().getValue().replace("\\([a-z ]+\\)$", "").trim();
			this.nameWithType = subject.getPreferredTerm().getValue();
			this.subjectID = subject.getSubjectID().toString();
			this.placeTypeID = placeTypeID;
			this.parentageString = subject.getPreferredParent();
			logger.debug(subjectID);
			logger.debug(placeTypeID);
		}
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the nameWithType
	 */
	public String getNameWithType() {
		return nameWithType;
	}
	/**
	 * @param nameWithType the nameWithType to set
	 */
	public void setNameWithType(String nameWithType) {
		this.nameWithType = nameWithType;
	}
	/**
	 * @return the subjectID
	 */
	public String getSubjectID() {
		return subjectID;
	}
	/**
	 * @param subjectID the subjectID to set
	 */
	public void setSubjectID(String subjectID) {
		this.subjectID = subjectID;
	}
	/**
	 * @return the placeTypeID
	 */
	public String getPlaceTypeID() {
		return placeTypeID;
	}
	/**
	 * @param placeTypeID the placeTypeID to set
	 */
	public void setPlaceTypeID(String placeTypeID) {
		this.placeTypeID = placeTypeID;
	}
	/**
	 * @return the parentageString
	 */
	public String getParentageString() {
		return parentageString;
	}
	/**
	 * @param parentageString the parentageString to set
	 */
	public void setParentageString(String parentageString) {
		this.parentageString = parentageString;
	}
	
}
