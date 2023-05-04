package com.bk.bands.xml;

import com.bk.bands.template.Band;
import org.simpleframework.xml.*;

@Default(DefaultType.PROPERTY)
public class XmlBand {
    private Band band;

    protected XmlFields header;
    protected XmlFields fields;
    protected XmlFields footer;

    public XmlBand() {
        band = new Band();
    }

    @Attribute
    public String getName() {
        return band.getName();
    }

    @Attribute
    public void setName(String name) {
        band.setName(name);
    }

    public XmlFields getFields() {
        return fields;
    }

    public void setFields(XmlFields fields) {
        this.fields = fields;
        this.band.setFields(fields.getFields());
    }

    @Element(required = false)
    public XmlFields getHeader() {
        return header;
    }

    @Element(required = false)
    public void setHeader(XmlFields header) {
        this.header = header;
        this.band.setHeader(header.getFields());
    }

    @Element(required = false)
    public XmlFields getFooter() {
        return footer;
    }

    @Element(required = false)
    public void setFooter(XmlFields footer) {
        this.footer = footer;
        this.band.setFooter(footer.getFields());
    }

    @Transient
    public Band getBand() {
        return band;
    }


}
