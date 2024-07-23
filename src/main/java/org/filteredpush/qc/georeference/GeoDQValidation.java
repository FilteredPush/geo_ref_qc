/**EventDQValidation.java
 * 
 * Copyright 2016 President and Fellows of Harvard College
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

import org.datakurator.ffdq.api.DQValidationResponse;
import org.datakurator.ffdq.api.EnumDQResultState;
import org.datakurator.ffdq.api.EnumDQValidationResult;
import org.datakurator.ffdq.api.ResultState;

/**
 * Specific implementation of return values for an F4UF validation result for org.filteredpush.qc.date
 * intended to implement an validation interface from an F4UF api package at some future point.
 *
 * @author mole
 * @version $Id: $Id
 */
public class GeoDQValidation implements DQValidationResponse {

	private ResultState resultState;
	private EnumDQValidationResult result;
	private StringBuffer resultComment;

	/**
	 * <p>Constructor for GeoDQValidation.</p>
	 */
	public GeoDQValidation() {
		setResultState(EnumDQResultState.NOT_RUN);
		setResult(null);
		resultComment = new StringBuffer();
	}
	
	/**
	 * <p>addComment.</p>
	 *
	 * @param comment a {@link java.lang.String} object.
	 */
	public void addComment(String comment) { 
		if (resultComment.length()>0) {
			resultComment.append("|");
		}
		resultComment.append(comment);
	}

	/**
	 * <p>getComment.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getComment() { 
		return resultComment.toString();
	}
	
	/**
	 * <p>Getter for the field <code>resultState</code>.</p>
	 *
	 * @return the resultState
	 */
	public ResultState getResultState() {
		return resultState;
	}

	/**
	 * <p>Setter for the field <code>resultState</code>.</p>
	 *
	 * @param resultState the resultState to set
	 */
	public void setResultState(ResultState resultState) {
		this.resultState = resultState;
	}


	/**
	 * <p>Getter for the field <code>result</code>.</p>
	 *
	 * @return the result
	 */
	public EnumDQValidationResult getResult() {
		return result;
	}

	/**
	 * <p>Setter for the field <code>result</code>.</p>
	 *
	 * @param result the result to set
	 */
	public void setResult(EnumDQValidationResult result) {
		this.result = result;
	}

}
