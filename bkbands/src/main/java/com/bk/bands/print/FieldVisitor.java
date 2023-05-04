package com.bk.bands.print;


import com.bk.bands.properties.Point;
import com.bk.bands.properties.Rectangle;
import com.bk.bands.properties.Border;
import com.bk.bands.properties.TextStyle;
import com.bk.bands.runtime.BandsException;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/8/12
 * Time: 8:38 PM
 */
public interface FieldVisitor
{
   void rectangle(Rectangle rect, Border border);
   void text(String text, TextStyle style, Rectangle rect, Border border);
   void oval(Rectangle rect, Border border);
   Point popViewPort();
   void pushViewPort(Point viewPort);

}
