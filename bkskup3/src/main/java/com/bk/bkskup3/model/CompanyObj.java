package com.bk.bkskup3.model;


import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 8/11/12
 * Time: 12:26 AM
 */
public class CompanyObj implements Company,Serializable
{
    protected String name;
    protected String street;
    protected String pobox;
    protected String city;
    protected String zip;
    protected EAN farmno;
    protected String cellphoneno;
    protected String phone;
    protected String emailaddress;
    protected String fiscalNo;

    public void copyFrom(Company src)
    {
        name = src.getName();
        street = src.getStreet();
        pobox = src.getPoBox();
        city = src.getCity();
        zip = src.getZip();
        farmno = src.getFarmNo();
        cellphoneno = src.getCellPhoneNo();
        phone = src.getPhoneNo();
        emailaddress = src.getEmailAddress();
        fiscalNo = src.getFiscalNo();
    }

   @Override
   public String getName()
   {
      return name;
   }

   @Override
   public String getStreet()
   {
      return street;
   }

   @Override
   public String getPoBox()
   {
      return pobox;
   }

   @Override
   public String getCity()
   {
      return city;
   }

   @Override
   public String getZip()
   {
      return zip;
   }

   @Override
   public EAN getFarmNo()
   {
      return farmno;
   }

   @Override
   public String getCellPhoneNo()
   {
      return cellphoneno;
   }

   @Override
   public String getPhoneNo()
   {
      return phone;
   }

   @Override
   public String getEmailAddress()
   {
      return emailaddress;
   }

   @Override
   public String getFiscalNo()
   {
      return fiscalNo;
   }

    public void setName(String name) {
        this.name = name;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setPoBox(String pobox) {
        this.pobox = pobox;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public void setFarmNo(EAN farmno) {
        this.farmno = farmno;
    }

    public void setCellPhoneNo(String cellphoneno) {
        this.cellphoneno = cellphoneno;
    }

    public void setPhoneNo(String phone) {
        this.phone = phone;
    }

    public void setEmailAddress(String emailaddress) {
        this.emailaddress = emailaddress;
    }

    public void setFiscalNo(String ficalNo) {
        this.fiscalNo = ficalNo;
    }
}
