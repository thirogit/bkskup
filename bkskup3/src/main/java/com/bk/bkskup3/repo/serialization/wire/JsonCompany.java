package com.bk.bkskup3.repo.serialization.wire;


import com.bk.bkskup3.model.Company;
import com.bk.bkskup3.model.CompanyObj;
import com.bk.bkskup3.model.EAN;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


public class JsonCompany {
    CompanyObj mCompany;


    public JsonCompany() {
        mCompany = new CompanyObj();
    }

    @JsonIgnore
    public Company getCompany() {
        return mCompany;
    }

    @JsonProperty("name")
    public String getName() {
        return mCompany.getName();
    }

    public void setName(String value) {
        mCompany.setName(value);
    }

    @JsonProperty("street")
    public String getStreet() {
        return mCompany.getStreet();
    }

    public void setStreet(String value) {
        mCompany.setStreet(value);
    }

    @JsonProperty("pobox")
    public String getPoBox() {
        return mCompany.getPoBox();
    }

    public void setPoBox(String value) {
        mCompany.setPoBox(value);
    }

    @JsonProperty("city")
    public String getCity() {
        return mCompany.getCity();
    }

    public void setCity(String value) {
        mCompany.setCity(value);
    }

    @JsonProperty("zip_code")
    public String getZip() {
        return mCompany.getZip();
    }

    public void setZip(String value) {
        mCompany.setZip(value);
    }

    @JsonProperty("farm_no")
    public EAN getFarmNo() {
        return mCompany.getFarmNo();
    }

    public void setFarmNo(EAN value) {
        mCompany.setFarmNo(value);
    }

    @JsonProperty("cellphone_no")
    public String getCellPhoneNo() {
        return mCompany.getCellPhoneNo();
    }

    public void setCellPhoneNo(String value) {
        mCompany.setCellPhoneNo(value);
    }

    @JsonProperty("phone_no")
    public String getPhoneNo() {
        return mCompany.getPhoneNo();
    }

    public void setPhoneNo(String value) {
        mCompany.setPhoneNo(value);
    }

    @JsonProperty("email_address")
    public String getEmailAddress() {
        return mCompany.getEmailAddress();
    }

    public void setEmailAddress(String value) {
        mCompany.setEmailAddress(value);
    }

    @JsonProperty("fiscal_no")
    public String getFiscalNo() {
        return mCompany.getFiscalNo();
    }

    public void setFiscalNo(String value) {
        mCompany.setFiscalNo(value);
    }

}
