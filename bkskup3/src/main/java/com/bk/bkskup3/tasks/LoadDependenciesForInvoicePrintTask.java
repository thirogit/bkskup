package com.bk.bkskup3.tasks;

import android.os.AsyncTask;

import com.bk.bkskup3.dao.BkStore;
import com.bk.bkskup3.dao.DefinitionsStore;
import com.bk.bkskup3.dao.PurchasesStore;
import com.bk.bkskup3.dao.SettingsStore;
import com.bk.bkskup3.model.AgentObj;
import com.bk.bkskup3.model.CompanyObj;
import com.bk.bkskup3.model.CowClassObj;
import com.bk.bkskup3.model.InvoiceObj;
import com.bk.bkskup3.model.PurchaseDetails;
import com.bk.bkskup3.model.StockObj;
import com.bk.bkskup3.settings.CompanyAsSettings;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/7/2014
 * Time: 2:56 PM
 */
public class LoadDependenciesForInvoicePrintTask extends AsyncTask<Void, Void, TaskResult<DependenciesForInvoicePrint>> {


    public interface Observer {
        void onLoadStarted();

        void onLoadSuccessful(DependenciesForInvoicePrint result);

        void onLoadError(Exception e);
    }

    private Observer mObserver;
    private BkStore mStore;
    private TaskResult<DependenciesForInvoicePrint> mResult;
    private int mInvoiceId;

    public LoadDependenciesForInvoicePrintTask(int invoiceId, BkStore store) {
        this.mStore = store;
        this.mInvoiceId = invoiceId;
    }

    @Override
    protected TaskResult<DependenciesForInvoicePrint> doInBackground(Void... params) {

        SettingsStore settingsStore = mStore.getSettingsStore();
        PurchasesStore purchasesStore = mStore.getPurchasesStore();
        DefinitionsStore definitionsStore = mStore.getDefinitionsStore();
        DependenciesForInvoicePrint result = new DependenciesForInvoicePrint();
        CompanyAsSettings companyAsSettings = settingsStore.loadSettings(CompanyAsSettings.class);
        CompanyObj companyObj = new CompanyObj();
        companyObj.copyFrom(companyAsSettings);
        result.setCompany(companyObj);

        InvoiceObj invoiceObj = purchasesStore.fetchInvoice(mInvoiceId);
        result.setInvoice(invoiceObj);

        PurchaseDetails purchaseDetails = purchasesStore.fetchPurchaseDetails(invoiceObj.getPurchase());
        result.setPurchaseDetails(purchaseDetails);


        result.setAgent(new AgentObj(settingsStore.getAgent()));

        Collection<CowClassObj> cowClasses = definitionsStore.fetchAllClasses();
        result.setClasses(cowClasses);

        Collection<StockObj> stocksObjs = definitionsStore.fetchAllStocks();
        result.setStocks(stocksObjs);

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

    private void onLoadSuccessful(DependenciesForInvoicePrint result) {
        if (mObserver != null)
            mObserver.onLoadSuccessful(result);
    }

    private void onLoadError(Exception e) {
        if (mObserver != null)
            mObserver.onLoadError(e);
    }

    protected void onPostExecute(TaskResult<DependenciesForInvoicePrint> result) {
        mResult = result;
        if (!result.isError()) {
            onLoadSuccessful(result.getResult());
        } else {
            onLoadError(result.getException());
        }
    }

    public TaskResult<DependenciesForInvoicePrint> getResult() {
        return mResult;
    }


}
