package com.bk.bkskup3.tasks;

import android.os.AsyncTask;

import com.bk.bkskup3.dao.BkStore;
import com.bk.bkskup3.dao.InvoiceNoTransactionStore;
import com.bk.bkskup3.dao.SettingsStore;
import com.bk.bkskup3.invoice.InvoiceNoTransaction;
import com.bk.bkskup3.settings.InvoiceSettings;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/19/2014
 * Time: 4:47 PM
 */
public class LoadDependenciesForInvoiceSettingsTask extends AsyncTask<Void, Void, TaskResult<DependenciesForInvoiceSettings>> {

    public interface Observer {
        void onLoadStarted();

        void onLoadSuccessful(DependenciesForInvoiceSettings result);

        void onLoadError(Exception e);
    }

    private Observer mObserver;
    private BkStore mStore;

    private TaskResult<DependenciesForInvoiceSettings> mResult;

    public LoadDependenciesForInvoiceSettingsTask(BkStore store) {
        this.mStore = store;
    }

    @Override
    protected TaskResult<DependenciesForInvoiceSettings> doInBackground(Void... params) {

        InvoiceNoTransactionStore invoiceNoTransactionStore = mStore.getInvoiceNoTransactionStore();
        SettingsStore settingsStore = mStore.getSettingsStore();
        InvoiceSettings invoiceSettings = settingsStore.loadSettings(InvoiceSettings.class);
        InvoiceNoTransaction lastTransaction = invoiceNoTransactionStore.getLastTransaction();

        DependenciesForInvoiceSettings dependencies = new DependenciesForInvoiceSettings();
        dependencies.setCurrentNoTransaction(lastTransaction);
        dependencies.setSettings(invoiceSettings);
        return TaskResult.withResult(dependencies);
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

    private void onLoadSuccessful(DependenciesForInvoiceSettings result) {
        if (mObserver != null)
            mObserver.onLoadSuccessful(result);
    }

    private void onLoadError(Exception e) {
        if (mObserver != null)
            mObserver.onLoadError(e);
    }

    protected void onPostExecute(TaskResult<DependenciesForInvoiceSettings> result) {
        mResult =  result;
        if (!result.isError()) {
            onLoadSuccessful(result.getResult());
        } else {
            onLoadError(result.getException());
        }
    }

    public TaskResult<DependenciesForInvoiceSettings> getResult() {
        return mResult;
    }
}
