package com.bk.bkskup3.repo.serialization.wire;

import com.bk.bkskup3.model.InvoiceDeduction;
import com.bk.bkskup3.model.InvoiceDeductionObj;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 1/19/2015
 * Time: 12:12 AM
 */
public class JsonInvoiceDeduction {

    private InvoiceDeductionObj mDeduction = new InvoiceDeductionObj();

    public JsonInvoiceDeduction() {
    }

    public JsonInvoiceDeduction(InvoiceDeduction deduction)
    {
        mDeduction.copyFrom(deduction);
    }

    public String getCode()
    {
        return mDeduction.getCode();
    }

    public String getReason()
    {
        return mDeduction.getReason();
    }

    public Double getFraction()
    {
        return mDeduction.getFraction();
    }

    public boolean isEnabled()
    {
        return mDeduction.isEnabled();
    }

    public void setCode(String code) {
        this.mDeduction.setCode(code);
    }

    public void setReason(String reason) {
        this.mDeduction.setReason(reason);
    }

    public void setFraction(Double fraction) {
        this.mDeduction.setFraction(fraction);
    }

    public void setEnabled(boolean enabled) {
        this.mDeduction.setEnabled(enabled);
    }







}
