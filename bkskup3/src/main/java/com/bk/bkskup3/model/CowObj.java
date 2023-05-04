package com.bk.bkskup3.model;


import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: root
 * Date: 14.06.11
 * Time: 17:34
 */
public class CowObj extends IdableObj implements Cow {

    private int invoice;
    private CowDetails details = new CowDetails();

    public CowObj(int cowId) {
        super(cowId);
    }

    public void copyFrom(Cow cow)
    {
        this.invoice = cow.getInvoice();
        this.details.copyFrom(cow);
    }

    public EAN getCowNo() {
        return details.getCowNo();
    }

    public CowSex getSex() {
        return details.getSex();
    }

    @Override
    public String getStockCd() {
        return details.getStockCd();
    }

    @Override
    public String getClassCd() {
        return details.getClassCd();
    }


    public double getWeight() {
        return details.getWeight();
    }

    public double getPrice() {
        return details.getPrice();
    }

    public int getInvoice() {
        return invoice;
    }

    public Double getLatitude() {
        return details.getLatitude();
    }

    public Double getLongitude() {
        return details.getLongitude();
    }

    @Override
    public EAN getFirstOwner() {
        return details.getFirstOwner();
    }

    @Override
    public String getPassportNo() {
        return details.getPassportNo();
    }

    @Override
    public Date getPassportIssueDt() {
        return details.getPassportIssueDt();
    }

    @Override
    public String getHealthCertNo() {
        return details.getHealthCertNo();
    }

    @Override
    public EAN getMotherNo() {
        return details.getMotherNo();
    }

    @Override
    public String getBirthPlace() {
        return details.getBirthPlace();
    }

    @Override
    public Date getBirthDt() {
        return details.getBirthDt();
    }

    @Override
    public CowPriceComponents getPriceComponents() {
        return details.getPriceComponents();
    }

    public void setCowNo(EAN cowNo) {
        details.setCowNo(cowNo);
    }

    public void setSex(CowSex sex) {
        details.setSex(sex);
    }

    public void setStockCd(String stockCd) {
        details.setStockCd(stockCd);
    }

    public void setClassCd(String classCd) {
        details.setClassCd(classCd);
    }

    public void setWeight(double weight) {
        details.setWeight(weight);
    }

    public void setPrice(double price) {
        details.setPrice(price);
    }

    public void setInvoice(int invoice) {
        this.invoice = invoice;
    }

    public void setLatitude(Double latitude) {
        details.setLatitude(latitude);
    }

    public void setLongitude(Double longitude) {
        details.setLongitude(longitude);
    }

    public void setPassportNo(String passportNo) {
        details.setPassportNo(passportNo);
    }

    public void setFirstOwner(EAN firstOwner) {
        details.setFirstOwner(firstOwner);
    }

    public void setPassportIssueDt(Date passportIssueDt) {
        details.setPassportIssueDt(passportIssueDt);
    }

    public void setHealthCertNo(String healthCertNo) {
        details.setHealthCertNo(healthCertNo);
    }

    public void setMotherNo(EAN motherNo) {
        details.setMotherNo(motherNo);
    }

    public void setBirthPlace(String birthPlace) {
        details.setBirthPlace(birthPlace);
    }

    public void setBirthDt(Date birthDt) {
        details.setBirthDt(birthDt);
    }
}
