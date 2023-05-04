package com.bk.bands.template;

import java.util.LinkedList;
import java.util.List;

import com.bk.bands.print.FieldVisitor;
import com.bk.bands.properties.Dimension;
import com.bk.bands.runtime.BandsException;
import com.bk.bands.util.DimensionCalculator;


/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/8/12
 * Time: 7:23 PM
 */
public class StackField extends Field
{
   List<Field> fieldStack = new LinkedList<Field>();
   private DimensionCalculator dimCalculator = new DimensionCalculator(fieldStack);

   public void pushField(Field field)
   {
      dimCalculator.calculate();
      fieldStack.add(field);
      field.moveTo(field.getPosition().x,dimCalculator.getSouth());
      dimCalculator.calculate();
      setSize(new Dimension(dimCalculator.getWidth(),dimCalculator.getHeight()));
   }

   @Override
   protected Field instance()
   {
      return new StackField();
   }

   public Field clone()
   {
      StackField copy = (StackField) super.clone();

      for(Field field : fieldStack)
      {
         copy.fieldStack.add(field.clone());
      }
      return copy;
   }

   @Override
   public void visit(FieldVisitor fieldVisitor) throws BandsException
   {
      fieldVisitor.pushViewPort(getPosition());
      for(Field field : fieldStack)
      {
         field.visit(fieldVisitor);
      }
      fieldVisitor.popViewPort();
   }
}
