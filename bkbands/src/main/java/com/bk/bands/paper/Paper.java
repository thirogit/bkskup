package com.bk.bands.paper;


import com.bk.bands.properties.Border;
import com.bk.bands.properties.Rectangle;
import com.bk.bands.properties.TextStyle;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/8/12
 * Time: 8:38 PM
 */
public interface Paper
{
   void drawRectangle(Rectangle rect,Border border);
   void drawText(String text, TextStyle style, Rectangle rect, Border border);
   void drawOval(Rectangle rect, Border border);

}
