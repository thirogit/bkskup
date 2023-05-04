package com.bk.bands.xml;

import com.bk.bands.template.Field;
import com.google.common.collect.ForwardingCollection;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;
import org.simpleframework.xml.Transient;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class XmlFields {
    @Transient
    protected List<Field> fields = new LinkedList<Field>();


    @ElementListUnion({
            @ElementList(entry = "text", inline = true, type = XmlText.class),
            @ElementList(entry = "shape", inline = true, type = XmlShape.class),
    })
    protected ForwardingCollection<XmlField> xmlFieldsDecorator = new ForwardingCollection<XmlField>() {
        private List<XmlField> xmlFields = new LinkedList<XmlField>();

        @Override
        protected List<XmlField> delegate() {
            return xmlFields;
        }

        public boolean add(XmlField element) {
            fields.add(element.getField());
            return super.add(element);
        }
    };

    public List<Field> getFields() {
        return fields;
    }

    public Collection<XmlField> getContent() {
        return xmlFieldsDecorator;
    }

}
