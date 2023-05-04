package com.bk.bkskup3.model;

import java.io.Serializable;
import java.util.Date;

public class InvoiceHentObj  implements InvoiceHent,Serializable {
    protected String hentName;
    protected String zip;
    protected String city;
    protected String street;
    protected String poBox;
    protected String fiscalNo;
    protected EAN hentNo;
    protected String wetNo;
    protected String phone;
    protected IBAN bankAccountNo;
    protected String bankName;
    protected String personalNo;
    protected String regon;
    protected String idNo;
    protected String issuePost;
    protected Date issueDate;
    protected String cellPhoneNo;
    protected String email;
    protected String wetLicenceNo;


    public InvoiceHentObj() {

    }

    public InvoiceHentObj(InvoiceHent src) {
        this.copyFrom(src);
    }

    public void copyFrom(InvoiceHent src) {
        setHentName(src.getHentName());
        setZip(src.getZip());
        setCity(src.getCity());
        setStreet(src.getStreet());
        setPoBox(src.getPoBox());
        setFiscalNo(src.getFiscalNo());
        setHentNo(src.getHentNo());
        setWetNo(src.getWetNo());
        setPhoneNo(src.getPhoneNo());
        setBankAccountNo(src.getBankAccountNo());
        setBankName(src.getBankName());
        setPersonalNo(src.getPersonalNo());
        setREGON(src.getREGON());
        setPersonalIdNo(src.getPersonalIdNo());
        setIssuePost(src.getIssuePost());
        setIssueDate(src.getIssueDate());
        setCellPhoneNo(src.getCellPhoneNo());
        setEmail(src.getEmail());
        setWetLicenceNo(src.getWetLicenceNo());
    }

    public HentObj asHent()
    {
        HentObj hent = new HentObj();

        hent.setHentName(this.getHentName());
        hent.setZip(this.getZip());
        hent.setCity(this.getCity());
        hent.setStreet(this.getStreet());
        hent.setPoBox(this.getPoBox());
        hent.setFiscalNo(this.getFiscalNo());
        hent.setHentNo(this.getHentNo());
        hent.setWetNo(this.getWetNo());
        hent.setPhoneNo(this.getPhoneNo());
        hent.setBankAccountNo(this.getBankAccountNo());
        hent.setBankName(this.getBankName());
        hent.setPersonalNo(this.getPersonalNo());
        hent.setREGON(this.getREGON());
        hent.setPersonalIdNo(this.getPersonalIdNo());
        hent.setIssuePost(this.getIssuePost());
        hent.setIssueDate(this.getIssueDate());
        hent.setCellPhoneNo(this.getCellPhoneNo());
        hent.setEmail(this.getEmail());
        hent.setWetLicenceNo(this.getWetLicenceNo());

        return hent;
    }
    
    public void copyFrom(Hent srcHent)
    {
        setHentName(srcHent.getHentName());
        setZip(srcHent.getZip());
        setCity(srcHent.getCity());
        setStreet(srcHent.getStreet());
        setPoBox(srcHent.getPoBox());
        setFiscalNo(srcHent.getFiscalNo());
        setHentNo(srcHent.getHentNo());
        setWetNo(srcHent.getWetNo());
        setPhoneNo(srcHent.getPhoneNo());
        setBankAccountNo(srcHent.getBankAccountNo());
        setBankName(srcHent.getBankName());
        setPersonalNo(srcHent.getPersonalNo());
        setREGON(srcHent.getREGON());
        setPersonalIdNo(srcHent.getPersonalIdNo());
        setIssuePost(srcHent.getIssuePost());
        setIssueDate(srcHent.getIssueDate());
        setCellPhoneNo(srcHent.getCellPhoneNo());
        setEmail(srcHent.getEmail());
        setWetLicenceNo(srcHent.getWetLicenceNo());
    }

    public String getHentName() {
        return hentName;
    }

    public String getZip() {
        return zip;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public String getPoBox() {
        return poBox;
    }

    public String getFiscalNo() {
        return fiscalNo;
    }

    public EAN getHentNo() {
        return hentNo;
    }

    public String getWetNo() {
        return wetNo;
    }

    public String getPhoneNo() {
        return phone;
    }

    public IBAN getBankAccountNo() {
        return bankAccountNo;
    }

    public String getBankName() {
        return bankName;
    }

    public String getPersonalNo() {
        return personalNo;
    }

    public String getREGON() {
        return regon;
    }

    public String getPersonalIdNo() {
        return idNo;
    }

    public String getIssuePost() {
        return issuePost;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public String getEmail() {
        return email;
    }

    public String getCellPhoneNo() {
        return cellPhoneNo;
    }

    public String getWetLicenceNo() {
        return wetLicenceNo;
    }

    public void setHentName(String hentName) {
        this.hentName = hentName;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setPoBox(String poBox) {
        this.poBox = poBox;
    }

    public void setFiscalNo(String fiscalNo) {
        this.fiscalNo = fiscalNo;
    }

    public void setHentNo(EAN hentNo) {
        this.hentNo = hentNo;
    }

    public void setWetNo(String wetNo) {
        this.wetNo = wetNo;
    }

    public void setPhoneNo(String phone) {
        this.phone = phone;
    }

    public void setBankAccountNo(IBAN bankAccountNo) {
        this.bankAccountNo = bankAccountNo;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public void setPersonalNo(String personalNo) {
        this.personalNo = personalNo;
    }

    public void setREGON(String regon) {
        this.regon = regon;
    }

    public void setPersonalIdNo(String idNo) {
        this.idNo = idNo;
    }

    public void setIssuePost(String issuePost) {
        this.issuePost = issuePost;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public void setCellPhoneNo(String cellPhoneNo) {
        this.cellPhoneNo = cellPhoneNo;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setWetLicenceNo(String wetLicenceNo) {
        this.wetLicenceNo = wetLicenceNo;
    }


}
