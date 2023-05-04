package com.bk.bands.template;

import java.util.LinkedList;
import java.util.List;

import com.bk.bands.evaluate.Evaluable;
import com.bk.bands.evaluate.ValueIterator;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/7/12
 * Time: 6:23 PM
 */
public class Band {
    private String name;

    private List<Field> fields;
    private List<Field> header;
    private List<Field> footer;

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public List<Field> getHeader() {
        return header;
    }

    public void setHeader(List<Field> header) {
        this.header = header;
    }

    public List<Field> getFooter() {
        return footer;
    }

    public void setFooter(List<Field> footer) {
        this.footer = footer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Field expand(ValueIterator valueIt) {
        StackField band = new StackField();

        while (valueIt.next()) {
            List<Field> bandFields = new LinkedList<Field>();
            for (Field field : fields) {
                if (field instanceof Evaluable) {
                    bandFields.add(((Evaluable) field).evaluate(valueIt));
                }
            }
            band.pushField(new CompositeField(bandFields));
        }
        return band;
    }
}
