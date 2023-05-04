package com.bk.bkskup3.model;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/3/2014
 * Time: 11:17 PM
 */
public class DeductionDefinitionObj extends IdableObj implements DeductionDefinition, Serializable {

    private boolean mEnabledByDefault;
    private DeductionDetails mDetails = new DeductionDetails();

    public DeductionDefinitionObj(int id) {
        super(id);
    }

    public DeductionDefinitionObj() {
        this(0);
    }


    @Override
    public String getCode() {
        return mDetails.getCode();
    }

    @Override
    public String getReason() {
        return mDetails.getReason();
    }

    @Override
    public Double getFraction() {
        return mDetails.getFraction();
    }

    @Override
    public boolean isEnabledByDefault() {
        return mEnabledByDefault;
    }

    public void setCode(String code) {
        this.mDetails.setCode(code);
    }

    public void setReason(String reason) {
        this.mDetails.setReason(reason);
    }

    public void setFraction(Double fraction) {
        this.mDetails.setFraction(fraction);
    }

    public void setEnabledByDefault(boolean enabled) {
        this.mEnabledByDefault = enabled;
    }

    public void copyFrom(DeductionDefinition deduction) {
        this.setCode(deduction.getCode());
        this.setReason(deduction.getReason());
        this.setFraction(deduction.getFraction());
        this.setEnabledByDefault(deduction.isEnabledByDefault());
    }
}
