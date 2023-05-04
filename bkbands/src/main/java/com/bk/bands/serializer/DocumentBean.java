package com.bk.bands.serializer;

import com.bk.bands.paper.Paper;
import com.bk.bands.properties.Dimension;
import com.bk.bands.template.Field;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/26/2014
 * Time: 7:21 PM
 */
public class DocumentBean implements Serializable {

    List<FieldBean> fields = new LinkedList<FieldBean>();
    String name;
    Dimension size;

    public List<FieldBean> getFields() {
        return fields;
    }

    public String getName() {
        return name;
    }

    public Dimension getSize() {
        return size;
    }

    public void print(Paper paper) {
        for (FieldBean field : fields) {
            field.print(paper);
        }
    }
}
