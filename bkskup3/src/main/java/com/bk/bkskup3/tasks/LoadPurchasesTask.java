package com.bk.bkskup3.tasks;

import android.os.AsyncTask;

import com.bk.bkskup3.dao.PurchasesStore;
import com.bk.bkskup3.model.PurchaseObj;
import com.mysema.query.support.Query;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/17/2014
 * Time: 10:43 AM
 */
public class LoadPurchasesTask extends AsyncTask<Void, Void, TaskResult<Collection<PurchaseObj>>> {

    public interface Observer {
        public abstract void onLoadStarted();

        public abstract void onLoadSuccessful();

        public abstract void onLoadError(Exception e);
    }

    private Observer mObserver;
    private PurchasesStore mStore;
    private TaskResult<Collection<PurchaseObj>> mResult;
    private Query mPurchaseQ;

    public LoadPurchasesTask(PurchasesStore store,Query purchaseQ) {
        this.mStore = store;
        this.mPurchaseQ = purchaseQ;
    }

    @Override
    protected TaskResult<Collection<PurchaseObj>> doInBackground(Void... params) {

        try {
            Collection<PurchaseObj> purchaseObjs = mStore.fetchPurchases(mPurchaseQ);
            return TaskResult.withResult(purchaseObjs);
        } catch (Exception e) {
            return TaskResult.withError(e);
        }
    }

    public TaskResult<Collection<PurchaseObj>> getResult() {
        return mResult;
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

    private void onLoadSuccessful(Collection<PurchaseObj> result) {
        if (mObserver != null)
            mObserver.onLoadSuccessful();
    }

    private void onLoadError(Exception e) {
        if (mObserver != null)
            mObserver.onLoadError(e);
    }

    protected void onPostExecute(TaskResult<Collection<PurchaseObj>> result) {
        mResult =  result;
        if (!result.isError()) {
            onLoadSuccessful(result.getResult());
        } else {
            onLoadError(result.getException());
        }
    }


}
