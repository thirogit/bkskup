package com.bk.bkskup3.tasks;

import android.os.AsyncTask;

import com.bk.bkskup3.dao.PurchasesStore;
import com.bk.bkskup3.model.*;
import com.google.common.base.Preconditions;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/14/2014
 * Time: 9:54 PM
 */
public class LoadInvoiceTask extends AsyncTask<Void, Void, TaskResult<InvoiceObj>> {

    public interface Observer {
        void onLoadStarted();

        void onLoadSuccessful(InvoiceObj result);

        void onLoadError(Exception e);
    }

    private Observer mObserver;
    private TaskResult<InvoiceObj> mResult;
    private PurchasesStore mStore;
    private int mInvoiceId;

    public LoadInvoiceTask(int invoiceId, PurchasesStore store) {
        this.mInvoiceId = invoiceId;
        this.mStore = store;
    }

    @Override
    protected TaskResult<InvoiceObj> doInBackground(Void... params) {

        InvoiceObj result = mStore.fetchInvoice(mInvoiceId);

        Preconditions.checkNotNull(result,"there is no invoice with id = " + mInvoiceId);

        return TaskResult.withResult(result);
    }

    @Override
    protected void onPreExecute() {
        onTaskStarted();
    }

    public void detachObserver() {
        mObserver = null;
    }

    public void attachObserver(Observer mObserver) {
        this.mObserver = mObserver;
    }


    private void onTaskStarted() {
        mResult = null;
        if (mObserver != null)
            mObserver.onLoadStarted();
    }

    private void onLoadSuccessful(InvoiceObj result) {
        if (mObserver != null)
            mObserver.onLoadSuccessful(result);
    }

    private void onLoadError(Exception e) {
        if (mObserver != null)
            mObserver.onLoadError(e);
    }

    protected void onPostExecute(TaskResult<InvoiceObj> result) {
        mResult = result;
        if (!result.isError()) {
            onLoadSuccessful(result.getResult());
        } else {
            onLoadError(result.getException());
        }
    }

    public TaskResult<InvoiceObj> getResult() {
        return mResult;
    }

}
