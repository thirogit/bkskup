package com.bk.bkskup3.work.fragment.event;

import com.bk.bkskup3.model.InvoiceType;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/6/2014
 * Time: 11:13 PM
 */
public class InvoiceTypeChanged extends FragmentEvent {
    private InvoiceType mInvoiceType;

    public InvoiceTypeChanged(InvoiceType invoiceType) {
        mInvoiceType = invoiceType;
    }

    public InvoiceType getInvoiceType() {
        return mInvoiceType;
    }
}
