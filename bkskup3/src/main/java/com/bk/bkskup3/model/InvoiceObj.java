package com.bk.bkskup3.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import com.google.common.collect.ImmutableList;

/**
 * Created by IntelliJ IDEA.
 * User: root
 * Date: 19.06.11
 * Time: 18:00
 */
public class InvoiceObj extends IdableObj implements Invoice, Serializable {

    protected InvoiceDetails mDetails = new InvoiceDetails();
    protected InvoiceHentObj hent;
    protected Collection<CowObj> cows;
    protected Collection<InvoiceDeduction> deductions;

    public InvoiceObj(int id) {
        super(id);
        cows = new LinkedList<CowObj>();
        deductions = new LinkedList<InvoiceDeduction>();
    }

    public void copyFrom(Invoice src) {
        this.mDetails.vatRate = src.getVatRate();
        this.mDetails.payWay = src.getPayWay();
        this.mDetails.customNumber = src.getCustomNumber();
        this.mDetails.payDueDays = src.getPayDueDays();
        this.mDetails.purchaseId = src.getPurchase();
        this.hent = new InvoiceHentObj(src.getInvoiceHent());
        this.mDetails.transactionPlace = src.getTransactionPlace();
        this.mDetails.transactionDt = src.getTransactionDt();
        this.mDetails.invoiceDt = src.getInvoiceDt();
        this.mDetails.invoiceType = src.getInvoiceType();
        this.mDetails.extras = src.getExtras();

        this.cows.clear();

        for (Cow srcCow : src.getCows()) {
            CowObj cow = new CowObj(srcCow.getId());
            cow.copyFrom(srcCow);
            this.cows.add(cow);
        }

        for (InvoiceDeduction srcDeduction : src.getDeductions()) {
            InvoiceDeductionObj deduction = new InvoiceDeductionObj();
            deduction.copyFrom(srcDeduction);
            this.deductions.add(deduction);
        }

    }

    public Double getVatRate() {
        return mDetails.vatRate;
    }


    public PayWay getPayWay() {
        return mDetails.payWay;
    }


    public String getCustomNumber() {
        return mDetails.customNumber;
    }


    public Integer getPayDueDays() {
        return mDetails.payDueDays;
    }

    @Override
    public int getPurchase() {
        return mDetails.purchaseId;
    }

    public InvoiceHent getInvoiceHent() {
        return hent;
    }

    public Collection<Cow> getCows() {
        ImmutableList.Builder<Cow> builder = ImmutableList.builder();
        return builder.addAll(cows).build();
    }

    @Override
    public Collection<InvoiceDeduction> getDeductions() {
        ImmutableList.Builder<InvoiceDeduction> builder = ImmutableList.builder();
        return builder.addAll(deductions).build();
    }

    public int getDeductionsCount() {
        return deductions.size();
    }

    public int getCowCount() {
        return cows.size();
    }

    public double getTotalNet() {
        double result = 0.0;
        for (Cow cow : cows) {
            result += cow.getPrice();
        }
        return result;
    }

    public double getTotalGross() {
        double totalNet = getTotalNet();
        return totalNet * mDetails.vatRate + totalNet;
    }

    public String getTransactionPlace() {
        return mDetails.transactionPlace;
    }

    @Override
    public Date getInvoiceDt() {
        return mDetails.invoiceDt;
    }

    public Date getTransactionDt() {
        return mDetails.transactionDt;
    }

    public InvoiceType getInvoiceType() {
        return mDetails.invoiceType;
    }

    @Override
    public String getExtras() {
        return mDetails.extras;
    }

    public InvoiceDetails getDetails() {
        return mDetails;
    }

    public void setVatRate(double vatRate) {
        this.mDetails.vatRate = vatRate;
    }

    public void setPayWay(PayWay payWay) {
        this.mDetails.payWay = payWay;
    }

    public void setCustomNumber(String customNumber) {
        this.mDetails.customNumber = customNumber;
    }

    public void setPayDueDays(Integer payDueDays) {
        this.mDetails.payDueDays = payDueDays;
    }

    public void setPurchase(int purchase) {
        this.mDetails.purchaseId = purchase;
    }

    public void setHent(InvoiceHent hent) {
        if (hent == null) {
            this.hent = null;
        } else {
            this.hent = new InvoiceHentObj(hent);
        }
    }

    public void setTransactionPlace(String transactionPlace) {
        this.mDetails.transactionPlace = transactionPlace;
    }

    public void setTransactionDt(Date transactionDt) {
        this.mDetails.transactionDt = transactionDt;
    }

    public void setInvoiceDt(Date invoiceDt) {
        this.mDetails.invoiceDt = invoiceDt;
    }

    public void setInvoiceType(InvoiceType invoiceType) {
        this.mDetails.invoiceType = invoiceType;
    }

    public void setExtras(String extras) {
        this.mDetails.extras = extras;
    }

    public void addCow(CowObj cow) {
        cows.add(cow);
    }

    public void addCows(Collection<CowObj> cows) {
        this.cows.addAll(cows);
    }

    public void addDeduction(InvoiceDeductionObj deduction) {
        this.deductions.add(deduction);
    }

    public void addDeductions(Collection<InvoiceDeductionObj> deductions) {
        this.deductions.addAll(deductions);
    }
}
