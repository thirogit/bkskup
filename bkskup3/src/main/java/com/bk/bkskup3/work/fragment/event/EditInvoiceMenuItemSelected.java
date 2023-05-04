package com.bk.bkskup3.work.fragment.event;

public class EditInvoiceMenuItemSelected extends FragmentEvent{
    private int mInvoiceId;

    public EditInvoiceMenuItemSelected(int invoiceId) {
        this.mInvoiceId = invoiceId;
    }

    public int getInvoiceId() {
        return mInvoiceId;
    }
}
