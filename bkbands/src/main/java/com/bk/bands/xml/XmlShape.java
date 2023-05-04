package com.bk.bands.xml;

import com.bk.bands.template.Field;
import com.bk.bands.template.ShapeField;
import com.bk.bands.template.ShapeType;
import org.simpleframework.xml.Default;
import org.simpleframework.xml.DefaultType;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/7/12
 * Time: 9:02 PM
 */

@Default(DefaultType.PROPERTY)
public class XmlShape extends XmlField
{
   private ShapeField field = new ShapeField();

   public XmlShape() { }

//   public XmlShape(XmlTemplate parentTemplate)
//   {
//      super(parentTemplate);
//   }

   @Override
   protected Field delegate()
   {
      return field;
   }

   public XmlBorder getBorder()
   {
      return new XmlBorder(field.getBorder());
   }

   public void setBorder(XmlBorder value)
   {
      field.setBorder(value.getBorder());
   }

   public ShapeType getType()
   {
      return field.getType();
   }

   public void setType(ShapeType type)
   {
      field.setType(type);
   }
}
