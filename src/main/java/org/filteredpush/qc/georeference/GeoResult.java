/**EventResult.java
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

import java.util.Map;

/**
 * Class to carry structured results (result plus metadata) from method calls in org.filteredpush.qc.georeference.
 *
 * @author mole
 * @version $Id: $Id
 */
public class GeoResult {
	
	public enum GeoQCResultState {
	    NOT_RUN, 
	    AMBIGUOUS, 
	    SUSPECT,
	    PROPOSED_CHANGE,
	    RANGE,
	    INTERNAL_PREREQISITES_NOT_MET;
	}
	
	private GeoQCResultState resultState;
	private Object result;
	private StringBuffer resultComment;
	
	/**
	 * <p>Constructor for GeoResult.</p>
	 */
	public GeoResult() {
		setResultState(GeoQCResultState.NOT_RUN);
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
	public GeoQCResultState getResultState() {
		return resultState;
	}

	/**
	 * <p>Setter for the field <code>resultState</code>.</p>
	 *
	 * @param resultState the resultState to set
	 */
	public void setResultState(GeoQCResultState resultState) {
		this.resultState = resultState;
	}

	/**
	 * <p>Getter for the field <code>result</code>.</p>
	 *
	 * @return the result
	 */
	public Object getResult() {
		return result;
	}

	/**
	 * <p>Setter for the field <code>result</code>.</p>
	 *
	 * @param result the result to set
	 */
	public void setResult(Object result) {
		this.result = result;
	}

}
