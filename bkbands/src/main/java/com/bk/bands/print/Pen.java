package com.bk.bands.print;


import com.bk.bands.properties.Color;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/8/12
 * Time: 8:49 PM
 */
public class Pen
{
   private int lineWidth = 1;
   private Color lineColor = Color.BLACK;

   public Pen()
   {
   }

   public Pen(int lineWidth, Color lineColor)
   {
      this.lineWidth = lineWidth;
      this.lineColor = lineColor;
   }

   public int getLineWidth()
   {
      return lineWidth;
   }

   public void setLineWidth(int lineWidth)
   {
      this.lineWidth = lineWidth;
   }

   public Color getLineColor()
   {
      return lineColor;
   }

   public void setLineColor(Color lineColor)
   {
      this.lineColor = lineColor;
   }
}
