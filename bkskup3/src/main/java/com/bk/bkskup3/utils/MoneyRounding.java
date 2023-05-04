package com.bk.bkskup3.utils;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/20/12
 * Time: 10:31 PM
 */
public class MoneyRounding
{
   public static double round(double price)
   {
      int milles = (int)(price*1000.0);
      int cents = milles%10;
      int roundedCents = (milles/10) + (cents >= 5 ? 1 : 0);
      return (roundedCents/100.0);
   }

    public static double roundToInteger(double price)
    {
        return Math.floor(price + 0.5d);
    }

}
