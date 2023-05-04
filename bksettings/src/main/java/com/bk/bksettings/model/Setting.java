package com.bk.bksettings.model;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 1/18/12
 * Time: 10:50 AM
 */
public class Setting
{
   private String name;
   private String value;
   private SettingType type;

   public Setting(String name)
   {
      this.name = name;
   }

   public String getName()
   {
      return name;
   }

   public String getValue()
   {
      return value;
   }

   public void setValue(String value)
   {
      this.value = value;
   }

   public SettingType getType()
   {
      return type;
   }

   public void setType(SettingType type)
   {
      this.type = type;
   }

   public String toString()
   {
      return type + " " + name + ": " + value;
   }
}
