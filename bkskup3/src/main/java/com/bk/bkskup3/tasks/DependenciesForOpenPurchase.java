package com.bk.bkskup3.tasks;

import com.bk.bkskup3.model.Herd;
import com.bk.bkskup3.model.HerdObj;
import com.bk.bkskup3.model.PurchaseDetails;

import java.io.Serializable;
import java.util.Collection;

public class DependenciesForOpenPurchase implements Serializable {
    private Collection<HerdObj> herds;
    private Collection<PurchaseDetails> openPurchases;

    public Collection<HerdObj> getHerds() {
        return herds;
    }

    public void setHerds(Collection<HerdObj> herds) {
        this.herds = herds;
    }

    public Collection<PurchaseDetails> getOpenPurchases() {
        return openPurchases;
    }

    public void setOpenPurchases(Collection<PurchaseDetails> openPurchases) {
        this.openPurchases = openPurchases;
    }
}
