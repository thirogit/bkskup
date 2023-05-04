package com.bk.bkskup3.repo.serialization.wire;

import com.bk.bkskup3.model.*;

import java.util.Collection;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 1/12/2015
 * Time: 11:58 PM
 */
public class JsonInvoice {
    private InvoiceDetails mDetails = new InvoiceDetails();
    private Collection<JsonCow> mCows;
    private Collection<JsonInvoiceDeduction> mDeductions;
    private JsonHent mHent = new JsonHent();

    public JsonInvoice() {
    }

    public JsonInvoice(InvoiceDetails details) {
        this.mDetails.copyFrom(details);
    }

    public Collection<JsonCow> getCows() {
        return mCows;
    }

    public void setCows(Collection<JsonCow> cows) {
        this.mCows = cows;
    }

    public Double getVatRate() {
        return mDetails.getVatRate();
    }

    public PayWay getPayWay() {
        return mDetails.getPayWay();
    }

    public String getCustomNumber() {
        return mDetails.getCustomNumber();
    }

    public Integer getPayDueDays() {
        return mDetails.getPayDueDays();
    }

    public JsonHent getHent() {
        return mHent;
    }

    public void setHent(JsonHent hent) {
        this.mHent = hent;
    }

    public Collection<JsonInvoiceDeduction> getDeductions() {
        return mDeductions;
    }

    public void setDeductions(Collection<JsonInvoiceDeduction> deductions) {
        this.mDeductions = deductions;
    }

    public String getTransactionPlace() {
        return mDetails.getTransactionPlace();
    }

    public JsonDayDate getInvoiceDt() {
        Date invoiceDt = mDetails.getInvoiceDt();
        if(invoiceDt != null)
            return new JsonDayDate(invoiceDt);

        return null;
    }

    public JsonDayDate getTransactionDt() {
        Date transactionDt = mDetails.getTransactionDt();
        if(transactionDt != null)
            return new JsonDayDate(transactionDt);

        return null;
    }

    public InvoiceType getInvoiceType() {
        return mDetails.getInvoiceType();
    }

    public String getExtras() {
        return mDetails.getExtras();
    }

    public void setVatRate(double vatRate) {
        this.mDetails.setVatRate(vatRate);
    }

    public void setPayWay(PayWay payWay) {
        this.mDetails.setPayWay(payWay);
    }

    public void setCustomNumber(String customNumber) {
        this.mDetails.setCustomNumber(customNumber);
    }

    public void setPayDueDays(Integer payDueDays) {
        this.mDetails.setPayDueDays(payDueDays);
    }

    public void setTransactionPlace(String transactionPlace) {
        this.mDetails.setTransactionPlace(transactionPlace);
    }

    public void setTransactionDt(JsonDayDate jsonTransactionDt) {
        Date transactionDt = null;
        if(jsonTransactionDt != null) {
            transactionDt = jsonTransactionDt.getDate();
        }
        this.mDetails.setTransactionDt(transactionDt);
    }

    public void setInvoiceDt(JsonDayDate jsonInvoiceDt) {
        Date invoiceDt = null;
        if(jsonInvoiceDt != null)
            invoiceDt = jsonInvoiceDt.getDate();
        this.mDetails.setInvoiceDt(invoiceDt);
    }

    public void setInvoiceType(InvoiceType invoiceType) {
        this.mDetails.setInvoiceType(invoiceType);
    }

    public void setExtras(String extras) {
        this.mDetails.setExtras(extras);
    }

}
