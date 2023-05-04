package com.bk.bands.template;


import com.bk.bands.print.FieldVisitor;
import com.bk.bands.properties.Border;
import com.bk.bands.properties.Rectangle;
import com.bk.bands.evaluate.Evaluable;
import com.bk.bands.evaluate.Evaluator;
import com.bk.bands.properties.TextStyle;
import com.bk.bands.runtime.BandsException;
import com.google.common.base.Strings;

/**
 * Created by IntelliJ IDEA.
 * User: sg0891787
 * Date: 3/5/12
 * Time: 11:11 AM
 */
public class TextField extends Field implements Evaluable
{
   private Border border;
   private String value;
   private TextStyle refStyle;
   private TextStyle ownStyle;

   public Border getBorder()
   {
      return border;
   }

   public void setBorder(Border border)
   {
      this.border = border;
   }

   public String getValue()
   {
      return value;
   }

   public void setValue(String value)
   {
      this.value = value;
   }

   public TextStyle getRefStyle()
   {
      return refStyle;
   }

   public void setRefStyle(TextStyle style)
   {
      this.refStyle = style;
   }

   public TextStyle getOwnStyle()
   {
      return ownStyle;
   }

   public void setOwnStyle(TextStyle ownStyle)
   {
      this.ownStyle = ownStyle;
   }

   public TextStyle getStyle()
   {
      if(refStyle == null)
      {
         return ownStyle;
      }
      return refStyle;
   }

   public Field evaluate(Evaluator evaluator)
   {
      TextField evaluatedField = (TextField) clone();
      evaluatedField.setValue(evaluator.evaluate(getValue()));
      return evaluatedField;
   }

   public Field clone()
   {
      TextField copy = (TextField) super.clone();
      copy.setRefStyle(getRefStyle());
      copy.setBorder(getBorder());
      copy.setOwnStyle(getOwnStyle());
      copy.setValue(getValue());
      return copy;
   }

   @Override
   protected Field instance()
   {
      return new TextField();
   }

   @Override
   public void visit(FieldVisitor fieldVisitor) throws BandsException
   {
      fieldVisitor.text(Strings.nullToEmpty(getValue()), getStyle(), new Rectangle(getPosition(), getSize()), getBorder());
   }
}
