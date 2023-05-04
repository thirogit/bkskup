package com.bk.bkskup3.repo.serialization.wire;

import com.bk.bkskup3.model.CowDetails;
import com.bk.bkskup3.model.CowSex;
import com.bk.bkskup3.model.EAN;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 1/13/2015
 * Time: 12:00 AM
 */
public class JsonCow {
    private CowDetails mDetails = new CowDetails();

    public JsonCow() {
    }

    public JsonCow(CowDetails details) {
        this.mDetails.copyFrom(details);
    }

    public EAN getCowNo() {
        return mDetails.getCowNo();
    }

    public CowSex getSex() {
        return mDetails.getSex();
    }

    public String getStockCd() {
        return mDetails.getStockCd();
    }

    public String getClassCd() {
        return mDetails.getClassCd();
    }

    public double getWeight() {
        return mDetails.getWeight();
    }

    public double getPrice() {
        return mDetails.getPrice();
    }

    public Double getLatitude() {
        return mDetails.getLatitude();
    }

    public Double getLongitude() {
        return mDetails.getLongitude();
    }

    public EAN getFirstOwner() {
        return mDetails.getFirstOwner();
    }

    public String getPassportNo() {
        return mDetails.getPassportNo();
    }

    public JsonDayDate getPassportIssueDt() {
        Date passportIssueDt = mDetails.getPassportIssueDt();
        if(passportIssueDt != null)
            return new JsonDayDate(passportIssueDt);

        return null;
    }

    public String getHealthCertNo() {
        return mDetails.getHealthCertNo();
    }

    public EAN getMotherNo() {
        return mDetails.getMotherNo();
    }

    public String getBirthPlace() {
        return mDetails.getBirthPlace();
    }

    public JsonDayDate getBirthDt() {
        Date birthDt = mDetails.getBirthDt();
        if(birthDt != null)
            return new JsonDayDate(birthDt);
        return null;
    }

    public void setCowNo(EAN cowNo) {
        mDetails.setCowNo(cowNo);
    }

    public void setSex(CowSex sex) {
        mDetails.setSex(sex);
    }

    public void setStockCd(String stockCd) {
        mDetails.setStockCd(stockCd);
    }

    public void setClassCd(String classCd) {
        mDetails.setClassCd(classCd);
    }

    public void setWeight(double weight) {
        mDetails.setWeight(weight);
    }

    public void setPrice(double price) {
        mDetails.setPrice(price);
    }

    public void setLatitude(Double latitude) {
        mDetails.setLatitude(latitude);
    }

    public void setLongitude(Double longitude) {
        mDetails.setLongitude(longitude);
    }

    public void setPassportNo(String passportNo) {
        mDetails.setPassportNo(passportNo);
    }

    public void setFirstOwner(EAN firstOwner) {
        mDetails.setFirstOwner(firstOwner);
    }

    public void setPassportIssueDt(JsonDayDate jsonPpassportIssueDt) {

        Date passportIssueDate = null;
        if(jsonPpassportIssueDt != null)
            passportIssueDate = jsonPpassportIssueDt.getDate();

        mDetails.setPassportIssueDt(passportIssueDate);
    }

    public void setHealthCertNo(String healthCertNo) {
        mDetails.setHealthCertNo(healthCertNo);
    }

    public void setMotherNo(EAN motherNo) {
        mDetails.setMotherNo(motherNo);
    }

    public void setBirthPlace(String birthPlace) {
        mDetails.setBirthPlace(birthPlace);
    }

    public void setBirthDt(JsonDayDate jsonBirthDt) {

        Date birthDate = null;
        if(jsonBirthDt != null)
            birthDate = jsonBirthDt.getDate();

        mDetails.setBirthDt(birthDate);
    }

}
