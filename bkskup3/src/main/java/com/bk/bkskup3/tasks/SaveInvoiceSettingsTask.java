package com.bk.bkskup3.tasks;

import android.os.AsyncTask;

import com.bk.bkskup3.dao.BkStore;
import com.bk.bkskup3.dao.InvoiceNoTransactionStore;
import com.bk.bkskup3.dao.SettingsStore;
import com.bk.bkskup3.settings.InvoiceSettings;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/19/2014
 * Time: 4:47 PM
 */
public class SaveInvoiceSettingsTask extends AsyncTask<Object, Void, TaskResult<Void>> {

    public interface Observer {
        void onSaveStarted();

        void onSaveSuccessful();

        void onSaveError(Exception e);
    }

    private Observer mObserver;
    private TaskResult<Void> mResult;
    private BkStore mStore;


    public SaveInvoiceSettingsTask(BkStore store) {
        this.mStore = store;
    }

    @Override
    protected TaskResult<Void> doInBackground(Object... params) {

        try {

            InvoiceSettings settings = (InvoiceSettings) params[0];
            Integer invoiceNo = (Integer) params[1];

            InvoiceNoTransactionStore invoiceNoTransactionStore = mStore.getInvoiceNoTransactionStore();
            SettingsStore settingsStore = mStore.getSettingsStore();

            settingsStore.saveSettings(settings);
            invoiceNoTransactionStore.resetTransaction(invoiceNo);

            return TaskResult.withResult(null);
        } catch (Exception e) {
            return TaskResult.withError(e);
        }
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
            mObserver.onSaveStarted();
    }

    private void onSuccessful() {
        if (mObserver != null)
            mObserver.onSaveSuccessful();
    }

    private void onError(Exception e) {
        if (mObserver != null)
            mObserver.onSaveError(e);
    }

    protected void onPostExecute(TaskResult<Void> result) {
        mResult =  result;
        if (!result.isError()) {
            onSuccessful();
        } else {
            onError(result.getException());
        }
    }

    public TaskResult<Void> getResult() {
        return mResult;
    }


}
