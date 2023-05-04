package com.bk.bkskup3.repo.serialization.wire;

import com.bk.bkskup3.model.PurchaseDetails;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.Collection;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 1/12/2015
 * Time: 11:56 PM
 */
@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class JsonPurchase {
    String mAgentCd;
    PurchaseDetails mPurchaseDetails = new PurchaseDetails();
    Collection<JsonInvoice> mInvoices;
    Collection<JsonHent> mPurchaseHents;

    public JsonPurchase() {
    }

    public JsonPurchase(PurchaseDetails purchaseDetails) {
        this.mPurchaseDetails.copyFrom(purchaseDetails);
    }

    public String getAgentCd() {
        return mAgentCd;
    }

    public void setAgentCd(String agentCd) {
        this.mAgentCd = agentCd;
    }

    public JsonDateTime getStartDt() {
        return new JsonDateTime(mPurchaseDetails.getPurchaseStart());
    }

    public void setStartDt(JsonDateTime jsonPurchaseStart) {

        Date purchaseStart = null;

        if (jsonPurchaseStart != null)
            purchaseStart = jsonPurchaseStart.getDate();

        this.mPurchaseDetails.setPurchaseStart(purchaseStart);
    }

    public JsonDateTime getEndDt() {
        return new JsonDateTime(mPurchaseDetails.getPurchaseEnd());
    }

    public void setEndDt(JsonDateTime jsonPurchaseEnd) {

        Date purchaseEnd = null;

        if (jsonPurchaseEnd != null)
            purchaseEnd = jsonPurchaseEnd.getDate();

        this.mPurchaseDetails.setPurchaseEnd(purchaseEnd);

    }

    public String getPlateNo() {
        return mPurchaseDetails.getPlateNo();
    }

    public void setPlateNo(String plateNo) {
        this.mPurchaseDetails.setPlateNo(plateNo);
    }

    public int getHerdNo() {
        return mPurchaseDetails.getHerdNo();
    }

    public void setHerdNo(int herdNo) {
        this.mPurchaseDetails.setHerdNo(herdNo);
    }

    public Collection<JsonInvoice> getInvoices() {
        return mInvoices;
    }

    public void setInvoices(Collection<JsonInvoice> invoices) {
        this.mInvoices = invoices;
    }

    public Collection<JsonHent> getPurchaseHents() {
        return mPurchaseHents;
    }

    public void setPurchaseHents(Collection<JsonHent> purchaseHents) {
        this.mPurchaseHents = purchaseHents;
    }
}
