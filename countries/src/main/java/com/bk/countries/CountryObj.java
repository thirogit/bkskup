package com.bk.countries;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: root
 * Date: 14.06.11
 * Time: 18:00
 */
class CountryObj implements Country, Serializable
{
   private int isoNumber;
   private boolean inEU;
   private String code2A;
   private String code3A;
   private int cowNoLength;
   private int hentNoLength;
   private int farmNoLength;

   public CountryObj(int isoNumber)
   {
      this.isoNumber = isoNumber;
   }


   @Override
   public boolean isInEU()
   {
      return inEU;
   }

   @Override
   public String getCode2A()
   {
      return code2A;
   }

   @Override
   public String getCode3A()
   {
      return code3A;
   }

   @Override
   public int getIsoNumber()
   {
      return isoNumber;
   }

   public void setInEU(boolean inEU)
   {
      this.inEU = inEU;
   }

   public void setCode2A(String code2A)
   {
      this.code2A = code2A;
   }

   public void setCode3A(String code3A)
   {
      this.code3A = code3A;
   }

   public int getCowNoLength()
   {
      return cowNoLength;
   }

   public void setCowNoLength(int cowNoLength)
   {
      this.cowNoLength = cowNoLength;
   }

   public int getFarmNoLength()
   {
      return farmNoLength;
   }

    @Override
    public int getHentNoLength() {
        return hentNoLength;
    }

    public void setFarmNoLength(int farmNoLength)
   {
      this.farmNoLength = farmNoLength;
   }

    public void setHentNoLength(int hentNoLength) {
        this.hentNoLength = hentNoLength;
    }
}
