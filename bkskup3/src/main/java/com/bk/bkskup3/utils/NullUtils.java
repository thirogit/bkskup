package com.bk.bkskup3.utils;


import com.bk.bkskup3.model.IBAN;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/16/12
 * Time: 10:05 PM
 */
public class NullUtils
{
   public static <T> T valueForNull(T value,T valueWhenNull)
   {
      if(value == null)
         return valueWhenNull;

      return value;
   }

   public static String dateTimeLongOrNull(Date dt) {
      if (dt == null)
         return null;

      return Dates.toDateTimeLong(dt);
   }

   public static String ibanOrNull(IBAN iban) {
      if (iban == null)
         return null;
      return iban.toString();
   }

   public static String nullOrToString(Object o) {
      if (o == null)
         return null;

      return o.toString();
   }

}
