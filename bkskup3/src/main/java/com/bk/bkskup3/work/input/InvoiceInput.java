package com.bk.bkskup3.work.input;

import com.bk.bkskup3.model.*;

import java.io.Serializable;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/20/2014
 * Time: 8:59 AM
 */
public class InvoiceInput implements Serializable {

    protected int mPurchaseId;
    protected int mInvoiceId;
    protected Double vatRate;
    protected PayWay payWay;
    protected String customNumber;
    protected Integer payDueDays;
    protected InvoiceHentObj hent;
    protected String transactionPlace;
    protected Date transactionDt;
    protected Date invoiceDt;
    protected InvoiceType invoiceType;
    protected String extras;
    protected Map<UUID, CowInput> mCows = new HashMap<UUID, CowInput>();
    protected Map<UUID, DeductionInput> mDeductions = new HashMap<UUID, DeductionInput>();

    public InvoiceInput() {
    }

    public void setInvoiceId(int mInvoiceId) {
        this.mInvoiceId = mInvoiceId;
    }

    public int getInvoiceId() {
        return mInvoiceId;
    }

    public Double getVatRate() {
        return vatRate;
    }

    public void setVatRate(Double vatRate) {
        this.vatRate = vatRate;
    }

    public PayWay getPayWay() {
        return payWay;
    }

    public void setPayWay(PayWay payWay) {
        this.payWay = payWay;
    }

    public String getCustomNumber() {
        return customNumber;
    }

    public void setCustomNumber(String customNumber) {
        this.customNumber = customNumber;
    }

    public Integer getPayDueDays() {
        return payDueDays;
    }

    public void setPayDueDays(Integer payDueDays) {
        this.payDueDays = payDueDays;
    }

    public InvoiceHentObj getHent() {
        return hent;
    }

    public void setHent(InvoiceHentObj hent) {
        this.hent = hent;
    }

    public String getTransactionPlace() {
        return transactionPlace;
    }

    public void setTransactionPlace(String transactionPlace) {
        this.transactionPlace = transactionPlace;
    }

    public Date getTransactionDt() {
        return transactionDt;
    }

    public void setTransactionDt(Date transactionDt) {
        this.transactionDt = transactionDt;
    }

    public Date getInvoiceDt() {
        return invoiceDt;
    }

    public void setInvoiceDt(Date invoiceDt) {
        this.invoiceDt = invoiceDt;
    }

    public InvoiceType getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(InvoiceType invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public int getPurchaseId() {
        return mPurchaseId;
    }

    public void setPurchaseId(int purchaseId) {
        this.mPurchaseId = purchaseId;
    }

    public int getCowCount() {
        return mCows.size();
    }

    private UUID obtainUinqueId() {
        UUID newId;

        do {
            newId = UUID.randomUUID();
        } while (mCows.containsKey(newId));

        return newId;

    }

    public UUID addCow(CowInput cowInput) {

        UUID id = obtainUinqueId();
        CowInput newCow = new CowInput(id);
        newCow.copyFrom(cowInput);
        mCows.put(id, newCow);
        return id;
    }

    public Collection<CowInput> getCows()
    {
        return mCows.values();
    }

    public void deleteCow(UUID id)
    {
        mCows.remove(id);
    }


    public CowInput getCow(UUID id) {
        return mCows.get(id);
    }


    public int getDeductionsCount() {
        return mDeductions.size();
    }

    public UUID addDeduction(DeductionInput deductionInput) {

        UUID id = obtainUinqueId();
        DeductionInput newDeduction = new DeductionInput(id);
        newDeduction.copyFrom(deductionInput);
        mDeductions.put(id, newDeduction);
        return id;
    }

    public Collection<DeductionInput> getDeductions()
    {
        return mDeductions.values();
    }

    public void deleteDeduction(UUID id)
    {
        mDeductions.remove(id);
    }

    public DeductionInput getDeduction(UUID id) {
        return mDeductions.get(id);
    }
}
