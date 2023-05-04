package com.bk.bkskup3.runtime;

import com.bk.bkskup3.model.*;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 4/18/2015
 * Time: 12:33 AM
 */
public class InvoiceHentAdapter implements Hent {

    private InvoiceHent mInvoiceHent;

    public InvoiceHentAdapter(InvoiceHent invoiceHent) {
        this.mInvoiceHent = invoiceHent;
    }

    @Override
    public String getAlias() {
        return null;
    }

    @Override
    public String getHentName() {
        return mInvoiceHent.getHentName();
    }

    @Override
    public String getZip() {
        return mInvoiceHent.getZip();
    }

    @Override
    public String getCity() {
        return mInvoiceHent.getCity();
    }

    @Override
    public String getStreet() {
        return mInvoiceHent.getStreet();
    }

    @Override
    public String getPoBox() {
        return mInvoiceHent.getPoBox();
    }

    @Override
    public String getFiscalNo() {
        return mInvoiceHent.getFiscalNo();
    }

    @Override
    public EAN getHentNo() {
        return mInvoiceHent.getHentNo();
    }

    @Override
    public String getWetNo() {
        return mInvoiceHent.getWetNo();
    }

    @Override
    public String getPhoneNo() {
        return mInvoiceHent.getPhoneNo();
    }

    @Override
    public String getPlateNo() {
        return null;
    }

    @Override
    public HentType getHentType() {
        return null;
    }

    @Override
    public String getExtras() {
        return null;
    }

    @Override
    public IBAN getBankAccountNo() {
        return mInvoiceHent.getBankAccountNo();
    }

    @Override
    public String getBankName() {
        return mInvoiceHent.getBankName();
    }

    @Override
    public String getPersonalNo() {
        return mInvoiceHent.getPersonalNo();
    }

    @Override
    public String getREGON() {
        return mInvoiceHent.getREGON();
    }

    @Override
    public String getPersonalIdNo() {
        return mInvoiceHent.getPersonalIdNo();
    }

    @Override
    public String getIssuePost() {
        return mInvoiceHent.getIssuePost();
    }

    @Override
    public Date getIssueDate() {
        return mInvoiceHent.getIssueDate();
    }

    @Override
    public String getCellPhoneNo() {
        return mInvoiceHent.getCellPhoneNo();
    }

    @Override
    public String getEmail() {
        return mInvoiceHent.getEmail();
    }

    @Override
    public Double getLatitude() {
        return null;
    }

    @Override
    public Double getLongitude() {
        return null;
    }

    @Override
    public String getWetLicenceNo() {
        return mInvoiceHent.getWetLicenceNo();
    }

    @Override
    public int getId() {
        return 0;
    }
}
