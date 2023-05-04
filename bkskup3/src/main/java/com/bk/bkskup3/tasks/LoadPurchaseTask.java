package com.bk.bkskup3.tasks;

import android.os.AsyncTask;

import com.bk.bkskup3.dao.PurchasesStore;
import com.bk.bkskup3.model.PurchaseObj;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/18/2014
 * Time: 10:32 PM
 */
public class LoadPurchaseTask extends AsyncTask<Void, Void, TaskResult<PurchaseObj>> {


    public interface Observer {
        public abstract void onLoadStarted();

        public abstract void onLoadSuccessful(PurchaseObj result);

        public abstract void onLoadError(Exception e);
    }


    private int mPurchaseId;
    private PurchasesStore mStore;
    private Observer mObserver;
    private TaskResult<PurchaseObj> mResult;

    public LoadPurchaseTask(PurchasesStore store, int purchaseId) {
        this.mPurchaseId = purchaseId;
        this.mStore = store;
    }

    @Override
    protected TaskResult<PurchaseObj> doInBackground(Void... params) {

        try {
            PurchaseObj purchaseObj = mStore.fetchPurchase(mPurchaseId);
            return TaskResult.withResult(purchaseObj);
        } catch (Exception e) {
            return TaskResult.withError(e);
        }
    }

    public TaskResult<PurchaseObj> getResult() {
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

    private void onLoadSuccessful(PurchaseObj result) {
        if (mObserver != null)
            mObserver.onLoadSuccessful(result);
    }

    private void onLoadError(Exception e) {
        if (mObserver != null)
            mObserver.onLoadError(e);
    }

    protected void onPostExecute(TaskResult<PurchaseObj> result) {
        mResult =  result;
        if (!result.isError()) {
            onLoadSuccessful(result.getResult());
        } else {
            onLoadError(result.getException());
        }
    }

}
