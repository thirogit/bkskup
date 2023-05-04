package com.bk.bands.template;


import com.bk.bands.print.FieldVisitor;
import com.bk.bands.properties.Border;
import com.bk.bands.properties.Rectangle;
import com.bk.bands.runtime.BandsException;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/7/12
 * Time: 9:05 PM
 */
public class ShapeField extends Field
{
   private ShapeType type;
   private Border border;

   public Border getBorder()
   {
      return border;
   }

   public void setBorder(Border border)
   {
      this.border = border;
   }

   public ShapeType getType()
   {
      return type;
   }

   public void setType(ShapeType type)
   {
      this.type = type;
   }

   @Override
   protected Field instance()
   {
      return new ShapeField();
   }

   public Field clone()
   {
      ShapeField copy = (ShapeField) super.clone();
      copy.setBorder(getBorder());
      copy.setType(getType());
      return copy;
   }

   @Override
   public void visit(FieldVisitor fieldVisitor) throws BandsException
   {
      switch(type)
      {
         case RECTANGLE:
            fieldVisitor.rectangle(new Rectangle(getPosition(), getSize()), getBorder());
            break;
         case CIRCLE:
            fieldVisitor.oval(new Rectangle(getPosition(), getSize()), getBorder());
            break;
         case ROUND_RECTANGLE:
            break;
      }

   }
}
