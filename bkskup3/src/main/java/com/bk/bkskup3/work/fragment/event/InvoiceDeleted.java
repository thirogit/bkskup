package com.bk.bkskup3.work.fragment.event;


/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 5/11/2015
 * Time: 11:36 PM
 */
public class InvoiceDeleted extends FragmentEvent {
    private int invoiceId;

    public InvoiceDeleted(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public int getInvoiceId() {
        return invoiceId;
    }
}
