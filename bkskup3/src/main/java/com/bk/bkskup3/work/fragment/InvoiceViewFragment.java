package com.bk.bkskup3.work.fragment;

import android.app.Fragment;
import com.bk.bkskup3.work.input.InvoiceInput;

public abstract class InvoiceViewFragment extends Fragment {

    protected InvoiceInput mInvoice;

    public void setInvoice(InvoiceInput invoice) {
        mInvoice = invoice;
    }
}
