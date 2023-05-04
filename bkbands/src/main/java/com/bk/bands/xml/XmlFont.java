package com.bk.bands.xml;

import com.bk.bands.properties.Font;
import org.simpleframework.xml.Default;
import org.simpleframework.xml.DefaultType;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Transient;

@Default(DefaultType.PROPERTY)
public class XmlFont
{
   private Font font;

   public XmlFont(Font font)
   {
      this.font = font;
   }

   public XmlFont()
   {
      this.font = new Font();
   }

    @Transient
   public Font getFont()
   {
      return font;
   }

   public int getHeight()
   {
      return font.getHeight();
   }

   public void setHeight(int value)
   {
      font.setHeight(value);
   }


    @Element(name = "face")
   public String getFaceName()
   {
      return font.getFaceName();
   }

   @Element(name = "face")
   public void setFaceName(String value)
   {
      font.setFaceName(value);
   }

   public boolean isBold()
   {
      return font.isBold();
   }

   public void setBold(boolean value)
   {
      font.setBold(value);
   }

   public boolean isItalic()
   {
      return font.isItalic();
   }

   public void setItalic(boolean value)
   {
      font.setItalic(value);
   }

   public boolean isUnderline()
   {
      return font.isUnderline();
   }

   public void setUnderline(boolean value)
   {
      font.setUnderline(value);
   }

   public boolean isStrikeout()
   {
      return font.isStrikeout();
   }

   public void setStrikeout(boolean value)
   {
      font.setStrikeout(value);
   }

}
