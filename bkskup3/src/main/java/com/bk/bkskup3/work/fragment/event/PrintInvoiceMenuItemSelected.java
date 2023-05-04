package com.bk.bkskup3.work.fragment.event;

public class PrintInvoiceMenuItemSelected extends FragmentEvent{
    private int mInvoiceId;

    public PrintInvoiceMenuItemSelected(int invoiceId) {
        this.mInvoiceId = invoiceId;
    }

    public int getInvoiceId() {
        return mInvoiceId;
    }
}
