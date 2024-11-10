/** 
 * GeoRefSourceAuthority.java 
 * 
 * Copyright 2024 President and Fellows of Harvard College
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.filteredpush.qc.georeference;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Identify source authorities for geospatial data, handling both specific services
 * and services which can take some form of dataset identifier to add to queries.
 *
 * @author mole
 * @version $Id: $Id
 */
public class GeoRefSourceAuthority {
	
	private EnumGeoRefSourceAuthority authority;
	private String authoritySubDataset;
	
	private static final Log logger = LogFactory.getLog(GeoRefSourceAuthority.class);
	
	/**
	 * Create a GeoRefSourceAuthority with a default value.
	 */
	public GeoRefSourceAuthority() { 
		authority = EnumGeoRefSourceAuthority.ADM1_UNION_EEZ;
		updateDefaultSubAuthorities();
	}
	
	/**
	 * Construct a scientific name source authority descriptor where additional information on a sub data set
	 * is not needed.
	 *
	 * @param authority the authority
	 * @throws org.filteredpush.qc.georeference.SourceAuthorityException if the authority specified requires a sub data set specification
	 */
	public GeoRefSourceAuthority(EnumGeoRefSourceAuthority authority) throws SourceAuthorityException { 
		this.authority = authority;
		authoritySubDataset = null;
		updateDefaultSubAuthorities();
	}
	
	/**
	 * Utility constructor to construct a georeference source authority from a string instead of the enum.
	 *
	 * @param authorityString a value matching the name of an item in EnumGeoRefSourceAuthority
	 * @throws org.filteredpush.qc.georeference.SourceAuthorityException
	 *   if the string is not matched to the enumeration, or if the specified
	 *   source authority requires the specification of an authoritySubDataset.
	 */
	public GeoRefSourceAuthority(String authorityString) throws SourceAuthorityException {
		logger.debug(authorityString); 
		if (authorityString==null) { authorityString = ""; }
	    if (authorityString.toUpperCase().equals("ADM1 BOUNDARIES UNION EEZ")) {
	    	this.authority = EnumGeoRefSourceAuthority.ADM1_UNION_EEZ;	
	    } else if (authorityString.toUpperCase().equals("ADM1 BOUNDARIES SPATIAL UNION WITH EXCLUSIVE ECONOMIC ZONES")) {
	    	this.authority = EnumGeoRefSourceAuthority.ADM1_UNION_EEZ;	
	    } else if (authorityString.toUpperCase().equals("10M-ADMIN-1 BOUNDARIES UNION WITH EXCLUSIVE ECONOMIC ZONES")) {
	    	this.authority = EnumGeoRefSourceAuthority.ADM1_UNION_EEZ;	
	    } else if (authorityString.toUpperCase().equals("ADM1 BOUNDARIES")) {
	    	// TODO: Add support, unable to distribute.
	    	this.authority = EnumGeoRefSourceAuthority.GADM_ADM1;	
	    } else if (authorityString.toUpperCase().equals("DATAHUB.IO")) {
	    	this.authority = EnumGeoRefSourceAuthority.DATAHUB;	
	    } else if (authorityString.toUpperCase().equals("DATAHUB")) {
	    	this.authority = EnumGeoRefSourceAuthority.DATAHUB;	
	    } else if (authorityString.toUpperCase().equals("NE_10M_ADMIN_0_COUNTRIES")) {
	    	this.authority = EnumGeoRefSourceAuthority.NE_ADMIN_0;	
	    } else if (authorityString.toUpperCase().equals("10M-ADMIN-1 BOUNDARIES")) {
	    	this.authority = EnumGeoRefSourceAuthority.NE_ADMIN_1;	
	    } else if (authorityString.toUpperCase().equals("UNION OF NATURALEARTH 10M-PHYSICAL-VECTORS FOR LAND AND NATURALEARTH MINOR ISLANDS")) { 
	    	this.authority = EnumGeoRefSourceAuthority.NE_LAND_UNION_ISLANDS;
	    } else if (authorityString.toUpperCase().equals("NE_LAND_UNION_ISLANDS")) { 
	    	this.authority = EnumGeoRefSourceAuthority.NE_LAND_UNION_ISLANDS;
	    } else if (authorityString.toUpperCase().equals("THE GETTY THESAURUS OF GEOGRAPHIC NAMES (TGN)")) {
	    	this.authority = EnumGeoRefSourceAuthority.GETTY_TGN;	
	    } else if (authorityString.toUpperCase().equals("GETTY TGN")) {
	    	this.authority = EnumGeoRefSourceAuthority.GETTY_TGN;	
	    } else if (authorityString.toUpperCase().equals("GBIF CATALOGUE OF COUNTRY CENTROIDES")) {
	    	this.authority = EnumGeoRefSourceAuthority.GBIF_CENTROIDS;	
	    } else if (authorityString.toUpperCase().equals("GBIF CATALOGUE OF COUNTRY CENTROIDS")) {
	    	this.authority = EnumGeoRefSourceAuthority.GBIF_CENTROIDS;	
	    } else if (authorityString.toUpperCase().equals("CATALOGUE-OF-CENTROIDS")) {
	    	this.authority = EnumGeoRefSourceAuthority.GBIF_CENTROIDS;	
	    } else if (authorityString.toUpperCase().startsWith("HTTPS://INVALID/")) { 
	    	this.authority = EnumGeoRefSourceAuthority.INVALID;	
	    } else { 
	    	throw new SourceAuthorityException("Unable to construct a SourceAuthority from string [" + authorityString + "]");
	    }
		authoritySubDataset = null;
		updateDefaultSubAuthorities();
	}
	
	/**
	 * Construct a scientific name source authority descriptor.
	 *
	 * @param authority the authority to use
	 * @param authoritySubDataset the specific authority to use.
	 * @param authoritySubDataset the specific authority to use.
	 * @param authoritySubDataset the specific authority to use.
	 * @param authoritySubDataset the specific authority to use.
	 * @param authoritySubDataset the specific authority to use.
	 * @param authoritySubDataset the specific authority to use.
	 */
	public GeoRefSourceAuthority(EnumGeoRefSourceAuthority authority, String authoritySubDataset) {
		this.authority = authority;
		this.authoritySubDataset = authoritySubDataset;
		updateDefaultSubAuthorities();
	}
	
	/**
	 * <p>Getter for the field <code>authority</code>.</p>
	 *
	 * @return a {@link org.filteredpush.qc.georeference.EnumGeoRefSourceAuthority} object.
	 */
	public EnumGeoRefSourceAuthority getAuthority() {
		return authority;
	}

	/**
	 * <p>Getter for the field <code>authoritySubDataset</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getAuthoritySubDataset() {
		return authoritySubDataset;
	}	
	
	/**
	 * For those authorities which have sub datasets (none specified yet)
	 * Set the authoritySubDataset to the correct value for the specified authority
	 */
	private void updateDefaultSubAuthorities() { 
		switch (this.authority) {
		default:
			// don't overwrite a specified sub authority/
			break;
		}
	}

	/**
	 * <p>getName.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getName() {
		
		return authority.getName();
	}

}
