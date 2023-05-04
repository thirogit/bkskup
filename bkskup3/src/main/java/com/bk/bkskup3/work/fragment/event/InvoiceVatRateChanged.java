package com.bk.bkskup3.work.fragment.event;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/6/2014
 * Time: 11:14 PM
 */
public class InvoiceVatRateChanged extends FragmentEvent {
    private Double mVatRate;
    public InvoiceVatRateChanged(Double vatRate) {
        mVatRate = vatRate;
    }

    public Double getVatRate() {
        return mVatRate;
    }
}
