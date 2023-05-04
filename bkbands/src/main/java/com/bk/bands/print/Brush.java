package com.bk.bands.print;

import com.bk.bands.properties.Color;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/8/12
 * Time: 8:49 PM
 */
public class Brush
{
   private Color color = Color.BLACK;


   public Brush()
   {
   }

   public Brush(Color color)
   {
      this.color = color;
   }

   public Color getColor()
   {
      return color;
   }

   public void setColor(Color color)
   {
      this.color = color;
   }
}
