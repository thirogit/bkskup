package com.bk.bands.xml;

import com.bk.bands.template.Field;
import com.bk.bands.template.TextField;
import org.simpleframework.xml.Default;
import org.simpleframework.xml.DefaultType;
import org.simpleframework.xml.Element;


@Default(DefaultType.PROPERTY)
public class XmlText extends XmlField {
    private TextField field = new TextField();
    private XmlStyle refStyle;

//   public XmlText(XmlTemplate parentTemplate)
//   {
//      super(parentTemplate);
//   }
//
//   public XmlText()  { }


    @Element(required = false)
    public XmlBorder getBorder() {
        return new XmlBorder(field.getBorder());
    }

    @Element(required = false)
    public void setBorder(XmlBorder value) {
        field.setBorder(value.getBorder());
    }

    @Element(required = false)
    public String getValue() {
        return field.getValue();
    }

    @Element(required = false)
    public void setValue(String value) {
        field.setValue(value);
    }

    @Element(required = false)

    public XmlStyle getStyle() {
        return refStyle;
    }

    @Element(required = false)
    public void setStyle(XmlStyle style)
    {
        refStyle = style;
        field.setRefStyle(style.getStyle());
    }

    @Element(name = "ownstyle", required = false)
    public XmlStyle getOwnStyle() {
        return new XmlStyle(field.getOwnStyle());
    }

    @Element(name = "ownstyle", required = false)
    public void setOwnStyle(XmlStyle value) {
        field.setOwnStyle(value.getStyle());
    }

    @Override
    protected Field delegate() {
        return field;
    }
}
