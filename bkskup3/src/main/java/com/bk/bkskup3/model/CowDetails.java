package com.bk.bkskup3.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/9/2014
 * Time: 8:57 PM
 */
public class CowDetails implements Serializable{

    private EAN cowNo;
    private CowSex sex;
    private String stockCd;
    private String classCd;
    private double weight;
    private double price;
    private CowPriceComponents priceComponents = new CowPriceComponents();

    private Double latitude;
    private Double longitude;
    private String passportNo;
    private EAN firstOwner;
    private Date passportIssueDt;
    private String healthCertNo;
    private EAN motherNo;
    private String birthPlace;
    private Date birthDt;

    public CowDetails() {
    }

    public CowDetails(Cow cow) {
        copyFrom(cow);
    }

    public void copyFrom(Cow cow) {
        setCowNo(cow.getCowNo());
        setSex(cow.getSex());
        setStockCd(cow.getStockCd());
        setClassCd(cow.getClassCd());
        setWeight(cow.getWeight());
        setPrice(cow.getPrice());
        setLatitude(cow.getLatitude());
        setLongitude(cow.getLongitude());
        setPassportNo(cow.getPassportNo());
        setFirstOwner(cow.getFirstOwner());
        setPassportIssueDt(cow.getPassportIssueDt());
        setHealthCertNo(cow.getHealthCertNo());
        setMotherNo(cow.getMotherNo());
        setBirthPlace(cow.getBirthPlace());
        setBirthDt(cow.getBirthDt());
        setPriceComponents(cow.getPriceComponents());
    }

    public void copyFrom(CowDetails details) {
        setCowNo(details.getCowNo());
        setSex(details.getSex());
        setStockCd(details.getStockCd());
        setClassCd(details.getClassCd());
        setWeight(details.getWeight());
        setPrice(details.getPrice());
        setLatitude(details.getLatitude());
        setLongitude(details.getLongitude());
        setPassportNo(details.getPassportNo());
        setFirstOwner(details.getFirstOwner());
        setPassportIssueDt(details.getPassportIssueDt());
        setHealthCertNo(details.getHealthCertNo());
        setMotherNo(details.getMotherNo());
        setBirthPlace(details.getBirthPlace());
        setBirthDt(details.getBirthDt());
        setPriceComponents(details.priceComponents);
    }

    public EAN getCowNo() {
        return cowNo;
    }

    public void setCowNo(EAN cowNo) {
        this.cowNo = cowNo;
    }

    public CowSex getSex() {
        return sex;
    }

    public void setSex(CowSex sex) {
        this.sex = sex;
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

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getPassportNo() {
        return passportNo;
    }

    public void setPassportNo(String passportNo) {
        this.passportNo = passportNo;
    }

    public EAN getFirstOwner() {
        return firstOwner;
    }

    public void setFirstOwner(EAN firstOwner) {
        this.firstOwner = firstOwner;
    }

    public Date getPassportIssueDt() {
        return passportIssueDt;
    }

    public void setPassportIssueDt(Date passportIssueDt) {
        this.passportIssueDt = passportIssueDt;
    }

    public String getHealthCertNo() {
        return healthCertNo;
    }

    public void setHealthCertNo(String healthCertNo) {
        this.healthCertNo = healthCertNo;
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

    public void setPriceComponents(CowPriceComponents priceComponents) {
        this.priceComponents.copyFrom(priceComponents);
    }

    public CowPriceComponents getPriceComponents() {
        return priceComponents;
    }
}
