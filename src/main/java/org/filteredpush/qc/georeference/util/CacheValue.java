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
 */
public class CacheValue {

    CurationStatus status;
    String comment;
    String source;
    public CacheValue setStatus(CurationStatus status){
        this.status = status;
        return this;
    }

    public CacheValue setComment(String comment){
        this.comment = comment;
        return this;
    }

    public CacheValue setSource(String source){
        this.source = source;
        return this;
    }

    public CurationStatus getStatus(){
        return status;
    }

    public String getComment(){
        return comment;
    }

    public String getSource(){
        return source;
    }

    public boolean equals(CacheValue newValue){
        if(comment.equals(newValue.getComment()) && source.equals(newValue.getSource()) && status.equals(newValue.getStatus())) return true;
        else return false;
    }
}
