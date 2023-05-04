package com.bk.bkskup3.tasks;

import android.os.AsyncTask;

import com.bk.bkskup3.dao.BkStore;
import com.bk.bkskup3.dao.DefinitionsStore;
import com.bk.bkskup3.dao.PurchasesStore;
import com.bk.bkskup3.dao.SettingsStore;
import com.bk.bkskup3.dao.q.QHerd;
import com.bk.bkskup3.model.Herd;
import com.bk.bkskup3.model.PurchaseDetails;
import com.bk.bkskup3.settings.ConstraintsSettings;
import com.bk.bkskup3.settings.InputDefaultsSettings;
import com.bk.bkskup3.settings.InvoiceSettings;
import com.bk.bkskup3.settings.TaxSettings;
import com.google.common.collect.Iterables;

import static com.mysema.query.support.QueryBuilder.where;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/19/2014
 * Time: 4:47 PM
 */
public class LoadDependenciesForInvoiceTask extends AsyncTask<Void, Void, TaskResult<DependenciesForInvoice>> {

    public interface Observer {
        void onLoadStarted();

        void onLoadSuccessful(DependenciesForInvoice result);

        void onLoadError(Exception e);
    }

    private Observer mObserver;
    private BkStore mStore;
    private TaskResult<DependenciesForInvoice> mResult;
    private int mPurchaseId;

    public LoadDependenciesForInvoiceTask(BkStore store,int purchaseId) {
        this.mStore = store;
        this.mPurchaseId = purchaseId;
    }

    @Override
    protected TaskResult<DependenciesForInvoice> doInBackground(Void... params) {

        SettingsStore settingsStore = mStore.getSettingsStore();
        DefinitionsStore definitionsStore = mStore.getDefinitionsStore();
        PurchasesStore purchasesStore = mStore.getPurchasesStore();

        TaxSettings taxSettings = settingsStore.loadSettings(TaxSettings.class);
        ConstraintsSettings constraintsSettings = settingsStore.loadSettings(ConstraintsSettings.class);
        InvoiceSettings invoiceSettings = settingsStore.loadSettings(InvoiceSettings.class);
        InputDefaultsSettings inputDefaultsSettings = settingsStore.loadSettings(InputDefaultsSettings.class);
        PurchaseDetails purchaseDetails = purchasesStore.fetchPurchaseDetails(mPurchaseId);
        Herd purchaseHerd = Iterables.getFirst(definitionsStore.fetchHerds(where(QHerd.number.eq(purchaseDetails.getHerdNo()))),null);

        DependenciesForInvoice dependencies = new DependenciesForInvoice();
        dependencies.setTaxSettings(taxSettings);
        dependencies.setConstraintsSettings(constraintsSettings);
        dependencies.setInvoiceSettings(invoiceSettings);
        dependencies.setInputDefaultsSettings(inputDefaultsSettings);
        dependencies.setDeductionDefinitions(definitionsStore.fetchAllDeductions());
        dependencies.setHerd(purchaseHerd);
        dependencies.setPurchaseDetails(purchaseDetails);
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

    private void onLoadSuccessful(DependenciesForInvoice result) {
        if (mObserver != null)
            mObserver.onLoadSuccessful(result);
    }

    private void onLoadError(Exception e) {
        if (mObserver != null)
            mObserver.onLoadError(e);
    }

    protected void onPostExecute(TaskResult<DependenciesForInvoice> result) {
        mResult =  result;
        if (!result.isError()) {
            onLoadSuccessful(result.getResult());
        } else {
            onLoadError(result.getException());
        }
    }

    public TaskResult<DependenciesForInvoice> getResult() {
        return mResult;
    }
}
