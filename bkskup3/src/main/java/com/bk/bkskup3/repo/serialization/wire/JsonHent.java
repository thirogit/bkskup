package com.bk.bkskup3.repo.serialization.wire;

import java.util.Date;

import com.bk.bkskup3.model.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 2/27/12
 * Time: 10:28 PM
 */
public class JsonHent {
    private HentObj mHent;

    public JsonHent(Hent hent) {
        this.mHent = new HentObj(hent.getId());
        this.mHent.copyFrom(hent);
    }

    public JsonHent() {
        this.mHent = new HentObj();
    }

    @JsonIgnore
    public HentObj getHent() {
        return mHent;
    }

    @JsonProperty("alias")
    public String getAlias() {
        return mHent.getAlias();
    }

    public void setAlias(String alias) {
        mHent.setAlias(alias);
    }

    @JsonProperty("hent_name")
    public String getName() {
        return mHent.getHentName();
    }

    public void setName(String hentName) {
        mHent.setHentName(hentName);
    }

    @JsonProperty("zip_code")
    public String getZip() {
        return mHent.getZip();
    }

    @JsonProperty("city")
    public String getCity() {
        return mHent.getCity();
    }

    @JsonProperty("street")
    public String getStreet() {
        return mHent.getStreet();
    }

    @JsonProperty("pobox")
    public String getPoBox() {
        return mHent.getPoBox();
    }

    @JsonProperty("fiscal_no")
    public String getFiscalNo() {
        return mHent.getFiscalNo();
    }

    @JsonProperty("hent_no")
    public EAN getHentNo() {
        return mHent.getHentNo();
    }

    @JsonProperty("wet_id_no")
    public String getWetNo() {
        return mHent.getWetNo();
    }

    @JsonProperty("phone_no")
    public String getPhoneNo() {
        return mHent.getPhoneNo();
    }

    @JsonProperty("plate_no")
    public String getPlateNo() {
        return mHent.getPlateNo();
    }

    @JsonProperty("hent_type")
    public HentType getHentType() {
        return mHent.getHentType();
    }

    @JsonProperty("extras")
    public String getExtras() {
        return mHent.getExtras();
    }

    @JsonProperty("account_no")
    public IBAN getBankAccountNo() {
        return mHent.getBankAccountNo();
    }

    @JsonProperty("bank_name")
    public String getBankName() {
        return mHent.getBankName();
    }

    @JsonProperty("personal_no")
    public String getPersonalNo() {
        return mHent.getPersonalNo();
    }

    @JsonProperty("stats_no")
    public String getStatsNo() {
        return mHent.getREGON();
    }

    @JsonProperty("personal_id_no")
    public String getPersonalIdNo() {
        return mHent.getPersonalIdNo();
    }

    @JsonProperty("personal_id_issue_post")
    public String getIssuePost() {
        return mHent.getIssuePost();
    }

    @JsonProperty("personal_id_issue_dt")
    public JsonDayDate getIssueDate() {
        Date issueDate = mHent.getIssueDate();
        if(issueDate != null)
            return new JsonDayDate(issueDate);
        return null;
    }

    @JsonProperty("cellphone_no")
    public String getCellPhoneNo() {
        return mHent.getCellPhoneNo();
    }

    @JsonProperty("email_address")
    public String getEmail() {
        return mHent.getEmail();
    }

    @JsonProperty("latitude")
    public Double getLatitude() {
        return mHent.getLatitude();
    }

    @JsonProperty("longitude")
    public Double getLongitude() {
        return mHent.getLongitude();
    }

    @JsonProperty("wet_lic_no")
    public String getWetLicenceNo() {
        return mHent.getWetLicenceNo();
    }



    public void setZip(String zip) {
        mHent.setZip(zip);
    }

    public void setCity(String city) {
        mHent.setCity(city);
    }

    public void setStreet(String street) {
        mHent.setStreet(street);
    }

    public void setPoBox(String poBox) {
        mHent.setPoBox(poBox);
    }

    public void setFiscalNo(String fiscalNo) {
        mHent.setFiscalNo(fiscalNo);
    }

    public void setHentNo(EAN hentNo) {
        mHent.setHentNo(hentNo);
    }

    public void setWetNo(String wetNo) {
        mHent.setWetNo(wetNo);
    }

    public void setPhoneNo(String phoneNo) {
        mHent.setPhoneNo(phoneNo);
    }

    public void setPlateNo(String plateNo) {
        mHent.setPlateNo(plateNo);
    }

    public void setHentType(HentType hentType) {
        mHent.setHentType(hentType);
    }

    public void setExtras(String extras) {
        mHent.setExtras(extras);
    }

    public void setBankAccountNo(IBAN bankAccountNo) {
        mHent.setBankAccountNo(bankAccountNo);
    }

    public void setBankName(String bankName) {
        mHent.setBankName(bankName);
    }

    public void setPersonalNo(String personalNo) {
        mHent.setPersonalNo(personalNo);
    }

    public void setStatsNo(String statsNo) {
        mHent.setREGON(statsNo);
    }

    public void setPersonalIdNo(String idNo) {
        mHent.setPersonalIdNo(idNo);
    }

    public void setIssuePost(String issuePost) {
        mHent.setIssuePost(issuePost);
    }

    public void setIssueDate(JsonDayDate jsonIssueDate) {
        Date issueDate = null;
        if(jsonIssueDate != null)
            issueDate = jsonIssueDate.getDate();
        mHent.setIssueDate(issueDate);
    }

    public void setCellPhoneNo(String cellPhoneNo) {
        mHent.setCellPhoneNo(cellPhoneNo);
    }

    public void setEmail(String email) {
        mHent.setEmail(email);
    }

    public void setLatitude(Double latitude) {
        mHent.setLatitude(latitude);
    }

    public void setLongitude(Double longitude) {
        mHent.setLongitude(longitude);
    }

    public void setWetLicenceNo(String wetLicenceNo) {
        mHent.setWetLicenceNo(wetLicenceNo);
    }
}
