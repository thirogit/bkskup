package com.bk.bkskup3.work.fragment.event;

import com.bk.bkskup3.model.PurchaseObj;

public class PurchaseLoaded extends FragmentEvent {
    private PurchaseObj mPurchase;

    public PurchaseLoaded(PurchaseObj purchase) {
        this.mPurchase = purchase;
    }

    public PurchaseObj getPurchase() {
        return mPurchase;
    }
}
