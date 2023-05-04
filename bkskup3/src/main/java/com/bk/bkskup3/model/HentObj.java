package com.bk.bkskup3.model;

import java.io.Serializable;
import java.util.Date;

public class HentObj extends IdableObj implements Hent,Serializable {
    protected String alias;
    protected String hentName;
    protected String zip;
    protected String city;
    protected String street;
    protected String poBox;
    protected String fiscalNo;
    protected EAN hentNo;
    protected String wetNo;
    protected String phone;
    protected String plateNo;
    protected HentType hentType;
    protected String extras;
    protected IBAN bankAccountNo;
    protected String bankName;
    protected String personalNo;
    protected String regon;
    protected String idNo;
    protected String issuePost;
    protected Date issueDate;
    protected String cellPhoneNo;
    protected String email;
    protected Double latitude;
    protected Double longitude;
    protected String wetLicenceNo;

    public HentObj(int id) {
        super(id);
    }

    public HentObj() {
        super(0);
    }
    
    public void copyFrom(Hent srcHent)
    {
        setAlias(srcHent.getAlias());
        setHentName(srcHent.getHentName());
        setZip(srcHent.getZip());
        setCity(srcHent.getCity());
        setStreet(srcHent.getStreet());
        setPoBox(srcHent.getPoBox());
        setFiscalNo(srcHent.getFiscalNo());
        setHentNo(srcHent.getHentNo());
        setWetNo(srcHent.getWetNo());
        setPhoneNo(srcHent.getPhoneNo());
        setPlateNo(srcHent.getPlateNo());
        setHentType(srcHent.getHentType());
        setExtras(srcHent.getExtras());
        setBankAccountNo(srcHent.getBankAccountNo());
        setBankName(srcHent.getBankName());
        setPersonalNo(srcHent.getPersonalNo());
        setREGON(srcHent.getREGON());
        setPersonalIdNo(srcHent.getPersonalIdNo());
        setIssuePost(srcHent.getIssuePost());
        setIssueDate(srcHent.getIssueDate());
        setCellPhoneNo(srcHent.getCellPhoneNo());
        setEmail(srcHent.getEmail());
        setLatitude(srcHent.getLatitude());
        setLongitude(srcHent.getLongitude());
        setWetLicenceNo(srcHent.getWetLicenceNo());
    }

    public String getHentName() {
        return hentName;
    }

    public String getAlias() {
        return alias;
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

    public String getPlateNo() {
        return plateNo;
    }

    public HentType getHentType() {
        return hentType;
    }

    public String getExtras() {
        return extras;
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

    public Double getLatitude() {
        return latitude;
    }

    public String getCellPhoneNo() {
        return cellPhoneNo;
    }

    public String getWetLicenceNo() {
        return wetLicenceNo;
    }

    public Double getLongitude() {
        return longitude;
    }


    public void setAlias(String alias) {
        this.alias = alias;
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

    public void setPlateNo(String plateNo) {
        this.plateNo = plateNo;
    }

    public void setHentType(HentType hentType) {
        this.hentType = hentType;
    }

    public void setExtras(String extras) {
        this.extras = extras;
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

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setWetLicenceNo(String wetLicenceNo) {
        this.wetLicenceNo = wetLicenceNo;
    }

}
