package com.bk.bkskup3.tasks;

import android.os.AsyncTask;
import com.bk.bkskup3.work.service.InvoiceService;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/14/2014
 * Time: 9:54 PM
 */
public class InitInvoiceServiceTask extends AsyncTask<Void, Void, TaskResult<Void>> {

    public interface Observer {
        public abstract void onInitStarted();

        public abstract void onInitSuccessful();

        public abstract void onInitError(Exception e);
    }

    private Observer mObserver;
    private TaskResult<Void> mResult;
    private InvoiceService mService;
    private int mInvoiceId;

    public InitInvoiceServiceTask(int invoiceId, InvoiceService service) {
        this.mInvoiceId = invoiceId;
        this.mService = service;
    }

    @Override
    protected TaskResult<Void> doInBackground(Void... params) {

        mService.editInvoice(mInvoiceId);
        return TaskResult.withResult(null);
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
            mObserver.onInitStarted();
    }

    private void onInitSuccessful() {
        if (mObserver != null)
            mObserver.onInitSuccessful();
    }

    private void onInitError(Exception e) {
        if (mObserver != null)
            mObserver.onInitError(e);
    }

    protected void onPostExecute(TaskResult<Void> result) {
        mResult = result;
        if (!result.isError()) {
            onInitSuccessful();
        } else {
            onInitError(result.getException());
        }
    }

    public TaskResult<Void> getResult() {
        return mResult;
    }

}
