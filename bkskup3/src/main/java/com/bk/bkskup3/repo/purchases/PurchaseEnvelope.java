package com.bk.bkskup3.repo.purchases;

import com.bk.bkskup3.model.HentObj;
import com.bk.bkskup3.model.PurchaseObj;

import java.io.Serializable;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 1/15/2015
 * Time: 5:13 PM
 */
public class PurchaseEnvelope implements Serializable {
    private String mAgentCd;
    private PurchaseObj mPurchase;
    private Collection<HentObj> mHents;

    public String getAgentCd() {
        return mAgentCd;
    }

    public void setAgentCd(String agentCd) {
        this.mAgentCd = agentCd;
    }

    public PurchaseObj getPurchase() {
        return mPurchase;
    }

    public void setPurchase(PurchaseObj purchase) {
        this.mPurchase = purchase;
    }

    public Collection<HentObj> getHents() {
        return mHents;
    }

    public void setHents(Collection<HentObj> hents) {
        this.mHents = hents;
    }
}
