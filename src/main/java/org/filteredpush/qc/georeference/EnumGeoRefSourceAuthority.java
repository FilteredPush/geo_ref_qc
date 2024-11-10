/** 
 * EnumGeoRefSourceAuthority.java 
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

/**
 * A list of source authorities for which implementations exist in this package.
 *
 * @author mole
 * @version $Id: $Id
 */
public enum EnumGeoRefSourceAuthority {

    ADM1_UNION_EEZ,
    NE_LAND_UNION_ISLANDS,
    NE_ADMIN_0,
    NE_ADMIN_1,
    GADM_ADM1, 
    GETTY_TGN,
    GBIF_CENTROIDS,
    DATAHUB,
    INVALID;
	

	/**
	 * <p>getName.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getName() {
		// return the exact name of the enum instance.
		return name();
	}
	
}
