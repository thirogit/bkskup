package com.bk.bksettings.model;

import java.lang.reflect.Type;
import java.util.Date;

import com.google.common.reflect.TypeToken;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 1/18/12
 * Time: 10:50 AM
 */
public enum SettingType
{
   LONG("LONG",   Long.class),
   STRING("STRN", String.class),
   DATE("DATE",   Date.class),
   BOOL("BOOL",   Boolean.class),
   FLOAT("DBL.",  Double.class),
   INT("INT.",  Integer.class);

   private String id;
   private Class javaType;

   SettingType(String id,Class javaType)
   {
      this.id = id;
      this.javaType = javaType;
   }

   public String getId()
   {
      return id;
   }

   public Type getJavaType()
   {
      return javaType;
   }

   public static SettingType fromJavaType(Type javaType)
   {
      for(SettingType settingType : values())
      {
         if(TypeToken.of(settingType.getJavaType()).isSubtypeOf(javaType))
         {
            return settingType;
         }
      }
      return null;
   }

   public static SettingType fromString(String settingTypeStr)
   {
      for(SettingType settingType : values())
      {
         if(settingType.getId().equals(settingTypeStr))
         {
            return settingType;
         }
      }
      return null;
   }
}
