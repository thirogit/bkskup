package com.bk.bkskup3.model;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/22/2014
 * Time: 11:52 PM
 */
public class InvoiceDeductionObj implements InvoiceDeduction,Serializable {

    private DeductionDetails details = new DeductionDetails();
    private boolean enabled;

    @Override
    public String getCode() {
        return details.getCode();
    }

    @Override
    public String getReason() {
        return details.getReason();
    }

    @Override
    public Double getFraction() {
        return details.getFraction();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setCode(String code) {
        this.details.setCode(code);
    }

    public void setReason(String reason) {
        this.details.setReason(reason);
    }

    public void setFraction(Double fraction) {
        this.details.setFraction(fraction);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void copyFrom(InvoiceDeduction deduction) {

        this.setCode(deduction.getCode());
        this.setFraction(deduction.getFraction());
        this.setEnabled(deduction.isEnabled());
        this.setReason(deduction.getReason());

    }
}
