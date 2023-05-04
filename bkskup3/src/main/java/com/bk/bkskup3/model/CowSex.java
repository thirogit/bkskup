package com.bk.bkskup3.model;

/**
 * Created by IntelliJ IDEA.
 * User: root
 * Date: 14.06.11
 * Time: 17:21
 */
public enum CowSex
{
   XX(1, "XX"),
   XY(-1, "XY"),
   NONE(0, "");

   private int sexId;
   private String sexString;

   private CowSex(int sexId, String sexString)
   {
      this.sexId = sexId;
      this.sexString = sexString;
   }

   public int getSexId()
   {
      return sexId;
   }

   public String getSexString()
   {
      return sexString;
   }

   public static CowSex fromInt(int sexId)
   {

      for (CowSex sex : values())
      {
         if (sex.getSexId() == sexId)
         {
            return sex;
         }
      }
      return null;
   }

   public String value() {
        return name();
    }

    public static CowSex fromValue(String v) {
        return valueOf(v);
    }
}
