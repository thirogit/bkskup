package com.bk.bkskup3.model;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/3/2014
 * Time: 5:53 PM
 */
public interface InvoiceHent {

    String getHentName();

    String getZip();

    String getCity();

    String getStreet();

    String getPoBox();

    String getFiscalNo();

    EAN getHentNo();

    String getWetNo();

    String getPhoneNo();

    IBAN getBankAccountNo();

    String getBankName();

    String getPersonalNo();

    String getREGON();

    String getPersonalIdNo();

    String getIssuePost();

    Date getIssueDate();

    String getCellPhoneNo();

    String getEmail();

    String getWetLicenceNo();
}
