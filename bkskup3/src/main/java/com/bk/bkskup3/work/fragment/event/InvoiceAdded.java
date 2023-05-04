package com.bk.bkskup3.work.fragment.event;

import com.bk.bkskup3.model.InvoiceObj;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/23/2014
 * Time: 4:13 PM
 */
public class InvoiceAdded extends FragmentEvent {
    private InvoiceObj invoice;

    public InvoiceAdded(InvoiceObj invoice) {
        this.invoice = invoice;
    }

    public InvoiceObj getInvoice() {
        return invoice;
    }
}
