package com.bk.bands.template;


import com.bk.bands.print.FieldVisitor;
import com.bk.bands.properties.Dimension;
import com.bk.bands.properties.Point;
import com.bk.bands.runtime.BandsException;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/6/12
 * Time: 8:12 PM
 */
public abstract class Field
{
   private Point position = new Point();
   private Dimension size = new Dimension();

   private String label;

   public Point getPosition()
   {
      return position;
   }

   public void setPosition(Point position)
   {
      this.position.setLocation(position);
   }

   public Dimension getSize()
   {
      return size;
   }

   public void setSize(Dimension size)
   {
      this.size.setSize(size);
   }

   public String getLabel()
   {
      return label;
   }

   public void setLabel(String label)
   {
      this.label = label;
   }

   public void moveBy(int dx,int dy)
   {
      position.move(position.x+dx,position.y+dy);
   }

   public void moveTo(int x,int y)
   {
      position.move(x,y);
   }

   public Field clone()
   {
      Field copy = instance();
      copy.setLabel(getLabel());
      copy.setPosition(getPosition());
      copy.setSize(getSize());
      return copy;
   }

   protected abstract Field instance();

   public abstract void visit(FieldVisitor fieldVisitor) throws BandsException;


}
