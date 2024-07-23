package org.filteredpush.qc.georeference.util;

/**
 * Object to hold a curation status value.
 *
 * User: cobalt
 * Date: 30.05.2013
 * Time: 15:50
 *
 * @author mole
 * @version $Id: $Id
 */
public class CurationStatus {
    private String status;

    /**
     * <p>Constructor for CurationStatus.</p>
     *
     * @param msg a {@link java.lang.String} object.
     */
    public CurationStatus(String msg){
        status = msg;
    }

    /**
     * <p>toString.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String toString(){
        return status;
    }
}
