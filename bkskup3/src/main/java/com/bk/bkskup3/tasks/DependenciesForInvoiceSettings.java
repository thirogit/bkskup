package com.bk.bkskup3.tasks;

import com.bk.bkskup3.invoice.InvoiceNoTransaction;
import com.bk.bkskup3.settings.InvoiceSettings;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/19/2014
 * Time: 4:45 PM
 */
public class DependenciesForInvoiceSettings implements Serializable {
    private InvoiceSettings mSettings;
    private InvoiceNoTransaction mCurrentNoTransaction;

    public InvoiceSettings getSettings() {
        return mSettings;
    }

    public void setSettings(InvoiceSettings settings) {
        this.mSettings = settings;
    }

    public InvoiceNoTransaction getCurrentNoTransaction() {
        return mCurrentNoTransaction;
    }

    public void setCurrentNoTransaction(InvoiceNoTransaction currentNoTransaction) {
        this.mCurrentNoTransaction = currentNoTransaction;
    }
}
