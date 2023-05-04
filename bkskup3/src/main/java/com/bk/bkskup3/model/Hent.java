package com.bk.bkskup3.model;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: root
 * Date: 28.06.11
 * Time: 19:56
 */
public interface Hent extends Idable
{
   String getAlias();


   String getHentName();


   String getZip();


   String getCity();


   String getStreet();


   String getPoBox();


   String getFiscalNo();


   EAN getHentNo();


   String getWetNo();


   String getPhoneNo();


   String getPlateNo();


   HentType getHentType();


   String getExtras();


   IBAN getBankAccountNo();


   String getBankName();


   String getPersonalNo();


   String getREGON();


   String getPersonalIdNo();


   String getIssuePost();


   Date getIssueDate();


   String getCellPhoneNo();


   String getEmail();


   Double getLatitude();


   Double getLongitude();


   String getWetLicenceNo();

}
