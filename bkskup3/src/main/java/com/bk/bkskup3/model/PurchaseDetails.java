package com.bk.bkskup3.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/29/2014
 * Time: 3:18 PM
 */
public class PurchaseDetails implements Serializable {

    protected Date purchaseStart;
    protected Date purchaseEnd;
    protected String plateNo;
    protected PurchaseState state;
    protected int herdNo;

    public void copyFrom(PurchaseDetails src) {
        this.purchaseStart = src.getPurchaseStart();
        this.purchaseEnd = src.getPurchaseEnd();
        this.plateNo = src.getPlateNo();
        this.state = src.getState();
        this.herdNo = src.getHerdNo();
    }

    public Date getPurchaseStart() {
        return purchaseStart;
    }

    public void setPurchaseStart(Date purchaseStart) {
        this.purchaseStart = purchaseStart;
    }

    public Date getPurchaseEnd() {
        return purchaseEnd;
    }

    public void setPurchaseEnd(Date purchaseEnd) {
        this.purchaseEnd = purchaseEnd;
    }

    public String getPlateNo() {
        return plateNo;
    }

    public void setPlateNo(String plateNo) {
        this.plateNo = plateNo;
    }

    public PurchaseState getState() {
        return state;
    }

    public void setState(PurchaseState state) {
        this.state = state;
    }

    public int getHerdNo() {
        return herdNo;
    }

    public void setHerdNo(int herdNo) {
        this.herdNo = herdNo;
    }
}
