package com.bk.bands.xml;

import com.bk.bands.properties.Color;
import com.bk.bands.properties.HorizontalAlign;
import com.bk.bands.properties.TextStyle;
import com.bk.bands.properties.VerticalAlign;
import org.simpleframework.xml.*;
import org.simpleframework.xml.convert.Convert;

@Default(DefaultType.PROPERTY)
public class XmlStyle {


   private TextStyle style;

   public XmlStyle(TextStyle style)
   {
      this.style = style;
   }

   public XmlStyle()
   {
      this.style = new TextStyle();
   }

    @Transient
   public TextStyle getStyle()
   {
      return style;
   }

    public Color getTextColor() {
        return style.getTextColor();
    }

    public void setTextColor(Color value) {
        style.setTextColor(value);
    }

    public HorizontalAlign getHorAlign() {
        return style.getHorAlign();
    }

    public void setHorAlign(HorizontalAlign value) {
        style.setHorAlign(value);
    }

    public VerticalAlign getVerAlign() {
        return style.getVerAlign();
    }

    public void setVerAlign(VerticalAlign value) {
        style.setVerAlign(value);
    }

    public XmlFont getFont() {
        return new XmlFont(style.getFont());
    }

    public void setFont(XmlFont value) {
        style.setFont(value.getFont());
    }

    @Element(required = false)
    public Color getBackColor() {
        return style.getBackColor();
    }
    @Element(required = false)
    public void setBackColor(Color value) {
        style.setBackColor(value);
    }

}
