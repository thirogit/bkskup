package com.bk.bkskup3.utils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 8/22/12
 * Time: 12:52 PM
 */
public class DoubleFormat
{
   private DecimalFormat formatter = new DecimalFormat();

   public DoubleFormat(com.bk.bkskup3.print.context.NumberFormatDefinition fmtDefinition)
   {
      DecimalFormatSymbols decimalSymbols = DecimalFormatSymbols.getInstance(Locale.ENGLISH);
      decimalSymbols.setDecimalSeparator(fmtDefinition.getDecimalSymbol());
      formatter.setDecimalFormatSymbols(decimalSymbols);
      formatter.setRoundingMode(RoundingMode.HALF_UP);
      int precision = fmtDefinition.getPrecision();
      boolean allFractionDigits = precision > 0;
      precision = Math.abs(precision);

      formatter.setMaximumFractionDigits(precision);
      if(allFractionDigits)
      {
         formatter.setMinimumFractionDigits(precision);
      }
      formatter.setGroupingUsed(false);

   }

   public String format(Double number)
   {
      if(number != null)
      {
         return formatter.format(number);
      }

      return "";
   }
}
