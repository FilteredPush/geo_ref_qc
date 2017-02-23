package org.filteredpush.qc.georeference.util;

/**
 * Object to hold a curation status value.
 * 
* User: cobalt
* Date: 30.05.2013
* Time: 15:50
*/
public class CurationStatus {
    private String status;

    public CurationStatus(String msg){
        status = msg;
    }

    public String toString(){
        return status;
    }
}