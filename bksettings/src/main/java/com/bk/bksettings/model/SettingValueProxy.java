package com.bk.bksettings.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.bk.bksettings.runtime.SettingsException;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 2/13/12
 * Time: 9:44 PM
 */
public class SettingValueProxy
{
   private Setting setting;
   private DateFormat dtFmt = new SimpleDateFormat("yyyymmddHHmmss", Locale.ENGLISH);

   public SettingValueProxy(Setting setting)
   {
      this.setting = setting;
   }

   private static final String BOOL_TRUE = "Y";
   private static final String BOOL_FALSE = "N";
   
   private String stringifyBool(boolean value)
   {
      return value ? BOOL_TRUE : BOOL_FALSE;
   }
   
   private boolean unstringifyBool(String boolStr)
   {
      if(BOOL_FALSE.equals(boolStr))
      {
         return false;
      }
      else if(BOOL_TRUE.equals(boolStr))
      {
         return true;  
      }
      
      throw new SettingsException(boolStr + " is not a correct value for bool");
   }
   
   
   private String stringifyLong(long value)
   {
      return Long.toString(value);
   }
   
   private long unstringifyLong(String longStr)
   {
      try
      {
         return Long.parseLong(longStr);
      }
      catch (NumberFormatException e)
      {
         throw new SettingsException(e);
      }
   }
   
   
   private Date unstringifyDate(String dateStr)
   {
      try
      {
         return dtFmt.parse(dateStr);
      }
      catch (ParseException e)
      {
         throw new SettingsException(e);
      }
   }

   private String stringifyDate(Date value)
   {
      return dtFmt.format(value);
   }
   
   private String stringifyFloat(double value)
   {
      return Double.toString(value);
   }

   private double unstringifyFloat(String floatStr)
   {
      try
      {
         return Double.parseDouble(floatStr);
      }
      catch (NumberFormatException e)
      {
         throw new SettingsException(e);
      }
   }
   
   private int unstringifyInt(String intStr)
   {
      try
      {
         return Integer.parseInt(intStr);
      }
      catch (NumberFormatException e)
      {
         throw new SettingsException(e);
      }
   }
   
   private String stringifyInt(int value)
   {
      return Integer.toString(value);
   }
   
   public Object getValue()
   {
      String value = setting.getValue();
      if(value == null)
         return null;

      switch(setting.getType())
      {
         case BOOL:
            return unstringifyBool(value);
         case DATE:
            return unstringifyDate(value);
         case FLOAT:
            return unstringifyFloat(value);
         case LONG:
            return unstringifyLong(value);
         case STRING:
            return value;
         case INT:
            return unstringifyInt(value);
      }
      return null;
   }

   public void setValue(Object settingValue)
   {
      if(settingValue == null)
      {
         setting.setValue(null);
      }
      else if(settingValue instanceof String)
      {
         setting.setType(SettingType.STRING);
         setting.setValue((String) settingValue);
      }
      else if(settingValue instanceof Date)
      {
         setting.setType(SettingType.DATE);
         setting.setValue(stringifyDate((Date) settingValue));
      }
      else if(settingValue instanceof Long)
      {
         setting.setType(SettingType.LONG);
         setting.setValue(stringifyLong((Long) settingValue));
      }
      else if(settingValue instanceof  Double)
      {
         setting.setType(SettingType.FLOAT);
         setting.setValue(stringifyFloat((Double) settingValue));
      }
      else if(settingValue instanceof Boolean)
      {
         setting.setType(SettingType.BOOL);
         setting.setValue(stringifyBool((Boolean) settingValue));
      }
      else if(settingValue instanceof Integer)
      {
         setting.setType(SettingType.INT);
         setting.setValue(stringifyInt((Integer)settingValue));
      }
      else
      {
         throw new SettingsException("Unsupported setting value class: " + settingValue.getClass().getCanonicalName());
      }

   }
}
