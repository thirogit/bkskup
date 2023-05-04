package com.bk.bkskup3.model;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/22/2014
 * Time: 11:45 PM
 */
public class DeductionDetails implements Serializable {
    private String code;
    private String reason;
    private Double fraction;


    public String getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }

    public Double getFraction() {
        return fraction;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setFraction(Double fraction) {
        this.fraction = fraction;
    }

    public void copyFrom(DeductionDetails src)
    {
        this.setCode(src.getCode());
        this.setFraction(src.getFraction());
        this.setReason(src.getReason());
    }

}
