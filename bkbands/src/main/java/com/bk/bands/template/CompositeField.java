package com.bk.bands.template;

import java.util.Collection;
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
 * Time: 4:28 PM
 */
public class CompositeField extends Field
{
   private List<Field> fields;
   private DimensionCalculator dimCalculator;

   public CompositeField(List<Field> fields)
   {
      this.fields = fields;
      dimCalculator = new DimensionCalculator(fields);
      calculateSize();
   }

   public CompositeField()
   {
      fields = new LinkedList<Field>();
      dimCalculator = new DimensionCalculator(fields);
   }

   public void addField(Field field)
   {
      fields.add(field);
      calculateSize();

   }

   public void addFields(Collection<? extends Field> fields)
   {
      this.fields.addAll(fields);
      calculateSize();
   }

   private void calculateSize()
   {
      dimCalculator.calculate();
      setSize(new Dimension(dimCalculator.getWidth(), dimCalculator.getHeight()));
   }

   @Override
   protected Field instance()
   {
      return new CompositeField();
   }

   public Field clone()
   {
      CompositeField copy = (CompositeField) instance();
       for(Field field : fields)
       {
           copy.addField(field.clone());
       }
       return copy;
   }

   @Override
   public void visit(FieldVisitor fieldVisitor) throws BandsException
   {
      fieldVisitor.pushViewPort(getPosition());
      for(Field field : fields)
      {
         field.visit(fieldVisitor);
      }
      fieldVisitor.popViewPort();
   }
}
