package com.bk.bkskup3.utils;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 8/23/12
 * Time: 9:22 AM
 */
public class NumberFormatDefinition
{
   private char decimalSymbol;
   private int precision;

   public NumberFormatDefinition(char decimalSymbol, int precision)
   {
      if(precision == 0 || decimalSymbol == '\0')
         throw new IllegalArgumentException();

      this.decimalSymbol = decimalSymbol;
      this.precision = precision;
   }

   public char getDecimalSymbol()
   {
      return decimalSymbol;
   }

   public int getPrecision()
   {
      return precision;
   }
}
