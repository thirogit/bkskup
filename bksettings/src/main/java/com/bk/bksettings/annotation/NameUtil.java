package com.bk.bksettings.annotation;

import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 7/23/12
 * Time: 6:03 PM
 */
public class NameUtil
{
   public static String capitalize(String s)
   {
      if (!isLower(s.charAt(0)))
      {
         return s;
      }
      StringBuilder sb = new StringBuilder(s.length());
      sb.append(toUpperCase(s.charAt(0)));
      sb.append(s.substring(1));
      return sb.toString();
   }

   protected boolean isPunct(char c)
   {
      return c == '-' || c == '.' || c == ':' || c == '_' || c == '\u00b7' || c == '\u0387' || c == '\u06dd' || c == '\u06de';
   }

   protected static boolean isDigit(char c)
   {
      return c >= '0' && c <= '9' || Character.isDigit(c);
   }

   protected static boolean isUpper(char c)
   {
      return c >= 'A' && c <= 'Z' || Character.isUpperCase(c);
   }

   protected static boolean isLower(char c)
   {
      return c >= 'a' && c <= 'z' || Character.isLowerCase(c);
   }

   protected boolean isLetter(char c)
   {
      return c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || Character.isLetter(c);
   }

   private static String toLowerCase(String s)
   {
      return s.toLowerCase(Locale.ENGLISH);
   }

   private static String toUpperCase(char c)
   {
      return String.valueOf(c).toUpperCase(Locale.ENGLISH);
   }

   private static String toUpperCase(String s)
   {
      return s.toUpperCase(Locale.ENGLISH);
   }
}
