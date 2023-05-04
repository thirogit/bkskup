package com.bk.bkskup3.invoice;

import android.app.Service;

import com.bk.bkskup3.BkApplication;
import com.bk.bkskup3.dao.InvoiceNoTransactionStore;

/**
 * Created by sg0891787 on 9/1/2017.
 */

public class InvoiceNoGenerator {

    private InvoiceNoTransactionStore mStore;

    public InvoiceNoGenerator(Service service)
    {
        BkApplication bkApplication = (BkApplication) service.getApplication();
        mStore = bkApplication.getStore().getInvoiceNoTransactionStore();
    }

    public InvoiceNoTransaction acquireNextInvoiceNo()
    {
        InvoiceNoTransaction invoiceNoTransaction = mStore.newTransaction();
        return invoiceNoTransaction;
    }

    public void commitInvoiceNo(int transactionId)
    {
        mStore.commitTransaction(transactionId);
    }
}
