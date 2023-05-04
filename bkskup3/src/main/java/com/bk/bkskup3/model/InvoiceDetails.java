package com.bk.bkskup3.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 1/13/2015
 * Time: 10:17 AM
 */
public class InvoiceDetails implements Serializable{

    protected Double vatRate;
    protected PayWay payWay;
    protected String customNumber;
    protected Integer payDueDays;
    protected String transactionPlace;
    protected Date transactionDt;
    protected Date invoiceDt;
    protected InvoiceType invoiceType;
    protected String extras;
    protected int purchaseId;

    public InvoiceDetails() {
    }

    public InvoiceDetails(Invoice invoice) {
        copyFrom(invoice);
    }

    private void copyFrom(Invoice invoice) {

        vatRate = invoice.getVatRate();
        payWay  = invoice.getPayWay();
        customNumber  = invoice.getCustomNumber();
        payDueDays  = invoice.getPayDueDays();
        transactionPlace  = invoice.getTransactionPlace();
        transactionDt  = invoice.getTransactionDt();
        invoiceDt  = invoice.getInvoiceDt();
        invoiceType  = invoice.getInvoiceType();
        extras  = invoice.getExtras();
        purchaseId = invoice.getPurchase();

    }

    public void copyFrom(InvoiceDetails details) {

        vatRate = details.getVatRate();
        payWay  = details.getPayWay();
        customNumber  = details.getCustomNumber();
        payDueDays  = details.getPayDueDays();
        transactionPlace  = details.getTransactionPlace();
        transactionDt  = details.getTransactionDt();
        invoiceDt  = details.getInvoiceDt();
        invoiceType  = details.getInvoiceType();
        extras  = details.getExtras();
        purchaseId = details.getPurchaseId();
    }

    public String getTransactionPlace() {
        return transactionPlace;
    }

    public Date getInvoiceDt() {
        return invoiceDt;
    }

    public Date getTransactionDt() {
        return transactionDt;
    }

    public InvoiceType getInvoiceType() {
        return invoiceType;
    }

    public String getExtras() {
        return extras;
    }

    public void setVatRate(double vatRate) {
        this.vatRate = vatRate;
    }

    public Double getVatRate() {
        return vatRate;
    }

    public void setPayWay(PayWay payWay) {
        this.payWay = payWay;
    }

    public PayWay getPayWay() {
        return payWay;
    }

    public void setCustomNumber(String customNumber) {
        this.customNumber = customNumber;
    }

    public String getCustomNumber() {
        return customNumber;
    }

    public void setPayDueDays(Integer payDueDays) {
        this.payDueDays = payDueDays;
    }

    public Integer getPayDueDays() {
        return payDueDays;
    }

    public void setTransactionPlace(String transactionPlace) {
        this.transactionPlace = transactionPlace;
    }

    public void setTransactionDt(Date transactionDt) {
        this.transactionDt = transactionDt;
    }

    public void setInvoiceDt(Date invoiceDt) {
        this.invoiceDt = invoiceDt;
    }

    public void setInvoiceType(InvoiceType invoiceType) {
        this.invoiceType = invoiceType;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public int getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(int purchaseId) {
        this.purchaseId = purchaseId;
    }
}
