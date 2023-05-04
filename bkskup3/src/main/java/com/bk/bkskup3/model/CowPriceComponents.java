package com.bk.bkskup3.model;

import java.io.Serializable;

public class CowPriceComponents implements Serializable {
    private Double mTotalNet;
    private Double mTotalGross;
    private Double mPerKgNet;
    private Double mPerKgGross;

    public Double getTotalNet() {
        return mTotalNet;
    }

    public void setTotalNet(Double totalNet) {
        this.mTotalNet = totalNet;
    }

    public Double getTotalGross() {
        return mTotalGross;
    }

    public void setTotalGross(Double TotalGross) {
        this.mTotalGross = TotalGross;
    }

    public Double getPerKgNet() {
        return mPerKgNet;
    }

    public void setPerKgNetNet(Double perKgNet) {
        this.mPerKgNet = perKgNet;
    }

    public Double getPerKgGross() {
        return mPerKgGross;
    }

    public void setPerKgGross(Double perKgGross) {
        this.mPerKgGross = perKgGross;
    }

    public void copyFrom(CowPriceComponents src)
    {
        mPerKgGross = src.mPerKgGross;
        mPerKgNet = src.getPerKgNet();
        mTotalNet = src.getTotalNet();
        mTotalGross = src.getTotalGross();
    }
}
