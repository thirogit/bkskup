package com.bk.bands.util;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 10/2/12
 * Time: 2:46 PM
 */
public class WhiteSpace
{
   public static final boolean isWhiteSpace(CharSequence s)
   {
      for (int i = s.length() - 1; i >= 0; i--)
      {
         if (!isWhiteSpace(s.charAt(i)))
         {
            return false;
         }
      }
      return true;
   }

   public static final boolean isWhiteSpace(char ch)
   {
      // most of the characters are non-control characters.
      // so check that first to quickly return false for most of the cases.
      if (ch > 0x20) return false;

      // other than we have to do four comparisons.
      return ch == 0x9 || ch == 0xA || ch == 0xD || ch == 0x20;
   }
}
