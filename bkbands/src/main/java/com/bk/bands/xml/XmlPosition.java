package com.bk.bands.xml;


import com.bk.bands.properties.Dimension;
import com.bk.bands.properties.Point;
import org.simpleframework.xml.Default;
import org.simpleframework.xml.DefaultType;
import org.simpleframework.xml.Transient;


@Default(DefaultType.PROPERTY)
public class XmlPosition
{

   private Point position;
   private Dimension size;


   public XmlPosition()
   {
      position = new Point();
      size = new Dimension();

   }

    @Transient
   public Point getPosition()
   {
      return position;
   }

    @Transient
   public Dimension getSize()
   {
      return size;
   }

   public XmlPosition(Point position, Dimension size)
   {
      this.position = position;
      this.size = size;
   }

   public int getLeft()
   {
      return position.x;
   }

   public void setLeft(int value)
   {
      position.x = value;
   }

   public int getTop()
   {
      return position.y;
   }

   public void setTop(int value)
   {
      position.y = value;
   }

   public int getWidth()
   {
      return size.width;
   }


   public void setWidth(int value)
   {
      size.width = value;
   }

   public int getHeight()
   {
      return size.height;
   }

   public void setHeight(int value)
   {
      size.height = value;
   }

}
