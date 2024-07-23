/** 
 * CacheValue.java 
 * 
 * Copyright 2015 President and Fellows of Harvard College
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
package org.filteredpush.qc.georeference.util;

/**
 * Created by tianhong on 3/4/15.
 *
 * @author mole
 * @version $Id: $Id
 */
public class CacheValue {

    CurationStatus status;
    String comment;
    String source;
    /**
     * <p>Setter for the field <code>status</code>.</p>
     *
     * @param status a {@link org.filteredpush.qc.georeference.util.CurationStatus} object.
     * @return a {@link org.filteredpush.qc.georeference.util.CacheValue} object.
     */
    public CacheValue setStatus(CurationStatus status){
        this.status = status;
        return this;
    }

    /**
     * <p>Setter for the field <code>comment</code>.</p>
     *
     * @param comment a {@link java.lang.String} object.
     * @return a {@link org.filteredpush.qc.georeference.util.CacheValue} object.
     */
    public CacheValue setComment(String comment){
        this.comment = comment;
        return this;
    }

    /**
     * <p>Setter for the field <code>source</code>.</p>
     *
     * @param source a {@link java.lang.String} object.
     * @return a {@link org.filteredpush.qc.georeference.util.CacheValue} object.
     */
    public CacheValue setSource(String source){
        this.source = source;
        return this;
    }

    /**
     * <p>Getter for the field <code>status</code>.</p>
     *
     * @return a {@link org.filteredpush.qc.georeference.util.CurationStatus} object.
     */
    public CurationStatus getStatus(){
        return status;
    }

    /**
     * <p>Getter for the field <code>comment</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getComment(){
        return comment;
    }

    /**
     * <p>Getter for the field <code>source</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getSource(){
        return source;
    }

    /**
     * <p>equals.</p>
     *
     * @param newValue a {@link org.filteredpush.qc.georeference.util.CacheValue} object.
     * @return a boolean.
     */
    public boolean equals(CacheValue newValue){
        if(comment.equals(newValue.getComment()) && source.equals(newValue.getSource()) && status.equals(newValue.getStatus())) return true;
        else return false;
    }
}
