package com.bk.bands.xml;


import com.bk.bands.properties.Color;
import com.bk.bands.properties.Border;
import org.simpleframework.xml.Default;
import org.simpleframework.xml.DefaultType;
import org.simpleframework.xml.Transient;
import org.simpleframework.xml.convert.Convert;


@Default(DefaultType.PROPERTY)
public class XmlBorder {


   private Border border;

   public XmlBorder()
   {
      border = new Border();
   }

   public XmlBorder(Border border)
   {
      this.border = border;
   }

    public int getLineWidth() {
        return border.getLineWidth();
    }

    public void setLineWidth(int lineWidth) {
        border.setLineWidth(lineWidth);
    }

    public Color getLineColor() {
        return border.getLineColor();
    }

    public void setLineColor(Color lineColor) {
        border.setLineColor(lineColor);
    }

    @Transient
   public Border getBorder()
   {
      return border;
   }
}
