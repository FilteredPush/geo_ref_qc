
package edu.getty.tgn.objects;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfVocabulary", propOrder = {
    "Vocabulary"
})
public class ArrayOfVocabulary {

    @XmlElement(name = "List_Vocabulary")
    protected List<Vocabulary> listResults;

    public List<Vocabulary> getListVocabulary() {
        if (listResults == null) {
            listResults = new ArrayList<Vocabulary>();
        }
        return this.listResults;
    }

}
