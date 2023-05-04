package com.bk.bkskup3.tasks;

import android.os.AsyncTask;

import com.bk.bkskup3.dao.BkStore;
import com.bk.bkskup3.dao.DefinitionsStore;
import com.bk.bkskup3.dao.PurchasesStore;
import com.bk.bkskup3.dao.q.QPurchase;
import com.bk.bkskup3.model.PurchaseState;

import static com.mysema.query.support.QueryBuilder.where;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/7/2014
 * Time: 2:56 PM
 */
public class LoadDependenciesForOpenPurchaseTask extends AsyncTask<Void, Void, TaskResult<DependenciesForOpenPurchase>> {


    public interface Observer {
        void onLoadStarted();

        void onLoadSuccessful(DependenciesForOpenPurchase result);

        void onLoadError(Exception e);
    }

    private Observer mObserver;
    private BkStore mStore;
    private TaskResult<DependenciesForOpenPurchase> mResult;

    public LoadDependenciesForOpenPurchaseTask(BkStore store) {
        this.mStore = store;
    }

    @Override
    protected TaskResult<DependenciesForOpenPurchase> doInBackground(Void... params) {


        DefinitionsStore definitionsStore = mStore.getDefinitionsStore();
        PurchasesStore purchasesStore = mStore.getPurchasesStore();
        DependenciesForOpenPurchase result = new DependenciesForOpenPurchase();


        result.setHerds(definitionsStore.fetchAllHerds());
        result.setOpenPurchases(purchasesStore.fetchPurchaseDetails(where(QPurchase.state.eq(PurchaseState.OPEN))));
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

    private void onLoadSuccessful(DependenciesForOpenPurchase result) {
        if (mObserver != null)
            mObserver.onLoadSuccessful(result);
    }

    private void onLoadError(Exception e) {
        if (mObserver != null)
            mObserver.onLoadError(e);
    }

    protected void onPostExecute(TaskResult<DependenciesForOpenPurchase> result) {
        mResult = result;
        if (!result.isError()) {
            onLoadSuccessful(result.getResult());
        } else {
            onLoadError(result.getException());
        }
    }

    public TaskResult<DependenciesForOpenPurchase> getResult() {
        return mResult;
    }


}
