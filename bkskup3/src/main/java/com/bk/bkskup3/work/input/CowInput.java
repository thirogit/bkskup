package com.bk.bkskup3.work.input;

import com.bk.bkskup3.model.CowSex;
import com.bk.bkskup3.model.EAN;
import com.bk.bkskup3.model.HentObj;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;


/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 1/2/12
 * Time: 4:24 PM
 */
public class CowInput implements Serializable {
    private UUID inputId;
    protected EAN cowNo;
    protected CowSex sex;
    protected String stockCd;
    private String classCd;
    protected Double weight;
    protected Double netPrice;
    protected Double latitude;
    protected Double longitude;
    protected String passportNo;
    protected HentObj firstOwner;
    private String healthCertNo;
    private Date passportIssueDt;
    private EAN motherNo;
    private String birthPlace;
    private Date birthDt;
    private int cowId;

    public CowInput() {
    }

    public CowInput(UUID inputId) {
        this.inputId = inputId;
    }

    public UUID getInputId() {
        return inputId;
    }

    public int getCowId() {
        return cowId;
    }

    public void setCowId(int cowId) {
        this.cowId = cowId;
    }

    public EAN getCowNo() {
        return cowNo;
    }

    public void setCowNo(EAN cowNo) {
        this.cowNo = cowNo;
    }

    public String getStockCd() {
        return stockCd;
    }

    public void setStockCd(String stockCd) {
        this.stockCd = stockCd;
    }

    public String getClassCd() {
        return classCd;
    }

    public void setClassCd(String classCd) {
        this.classCd = classCd;
    }

    public CowSex getSex() {
        return sex;
    }

    public Double getWeight() {
        return weight;
    }

    public Double getNetPrice() {
        return netPrice;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public HentObj getFirstOwner() {
        return firstOwner;
    }

    public String getPassportNo() {
        return passportNo;
    }

    public void setFirstOwner(HentObj firstOwner) {
        this.firstOwner = firstOwner;
    }

    public void setPassportNo(String passportNo) {
        this.passportNo = passportNo;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setNetPrice(Double netPrice) {
        this.netPrice = netPrice;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public void setSex(CowSex sex) {
        this.sex = sex;
    }

    public String getHealthCertNo() {
        return healthCertNo;
    }

    public void setHealthCertNo(String healthCertNo) {
        this.healthCertNo = healthCertNo;
    }

    public Date getPassportIssueDt() {
        return passportIssueDt;
    }

    public void setPassportIssueDt(Date passportIssueDt) {
        this.passportIssueDt = passportIssueDt;
    }

    public EAN getMotherNo() {
        return motherNo;
    }

    public void setMotherNo(EAN motherNo) {
        this.motherNo = motherNo;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public Date getBirthDt() {
        return birthDt;
    }

    public void setBirthDt(Date birthDt) {
        this.birthDt = birthDt;
    }

    public void copyFrom(CowInput src) {

        cowNo = src.getCowNo();
        sex = src.getSex();
        stockCd = src.getStockCd();
        classCd = src.getClassCd();
        weight = src.getWeight();
        netPrice = src.getNetPrice();
        latitude = src.getLatitude();
        longitude = src.getLongitude();
        passportNo = src.getPassportNo();
        HentObj originalFirstOwner = src.getFirstOwner();
        if (originalFirstOwner != null) {
            firstOwner = new HentObj(originalFirstOwner.getId());
            firstOwner.copyFrom(originalFirstOwner);
        } else {
            firstOwner = null;
        }
        healthCertNo = src.getHealthCertNo();
        passportIssueDt = src.getPassportIssueDt();
        motherNo = src.getMotherNo();
        birthPlace = src.getBirthPlace();
        birthDt = src.getBirthDt();
        cowId = src.getCowId();

    }
}
