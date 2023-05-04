package com.bk.bksettings.stringifier;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.bk.bksettings.runtime.SettingsException;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 4/12/12
 * Time: 1:42 PM
 */
public class DateStringifier implements Stringifier
{
   private DateFormat dtFmt = new SimpleDateFormat("yyyyMMddHHmmss");

   @Override
   public Object unstringify(String value)
   {
      try
      {
         if(value == null) return null;
         return dtFmt.parse(value);
      }
      catch (ParseException e)
      {
         throw new SettingsException(e);
      }
   }

   @Override
   public String stringify(Object obj)
   {
      return dtFmt.format(obj);
   }

   @Override
   public boolean isApplicable(Object obj)
   {
      return obj instanceof Date;
   }
}
