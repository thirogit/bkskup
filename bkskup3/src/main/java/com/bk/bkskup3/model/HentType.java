package com.bk.bkskup3.model;


/**
 * Created by IntelliJ IDEA.
 * User: root
 * Date: 14.06.11
 * Time: 17:22
 */

public enum HentType
{
   COMPANY("C","company"),
   INDIVIDUAL("I","individual");

   private String hentTypeId;
   private String hentTypeStr;

    HentType(String hentTypeId, String hentTypeStr) {
        this.hentTypeId = hentTypeId;
        this.hentTypeStr = hentTypeStr;
    }

    public String getHentTypeId()
   {
      return hentTypeId;
   }

   public static HentType fromString(String hentTypeStr)
   {
      for (HentType hentType : values())
      {
         if (hentType.hentTypeId.equals(hentTypeStr) || hentType.hentTypeStr.equals(hentTypeStr))
         {
            return hentType;
         }
      }

      return null;
   }

    public java.lang.String value() {
        return name();
    }

    public static HentType fromValue(java.lang.String v) {
        return valueOf(v);
    }


}
