package com.bk.bkskup3.tasks;

import android.os.AsyncTask;

import com.bk.bkskup3.dao.BkStore;
import com.bk.bkskup3.dao.DefinitionsStore;
import com.bk.bkskup3.dao.SettingsStore;
import com.bk.bkskup3.model.CowClassObj;
import com.bk.bkskup3.model.StockObj;
import com.bk.bkskup3.settings.InputDefaultsSettings;
import com.bk.bkskup3.work.DependenciesForCow;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/7/2014
 * Time: 2:56 PM
 */
public class LoadDependenciesForCowTask extends AsyncTask<Void, Void, TaskResult<DependenciesForCow>> {


    public interface Observer {
        public abstract void onLoadStarted();

        public abstract void onLoadSuccessful(DependenciesForCow result);

        public abstract void onLoadError(Exception e);
    }

    private Observer mObserver;
    private BkStore mStore;
    private TaskResult<DependenciesForCow> mResult;

    public LoadDependenciesForCowTask(BkStore store) {
        this.mStore = store;
    }

    @Override
    protected TaskResult<DependenciesForCow> doInBackground(Void... params) {

        DefinitionsStore definitionsStore = mStore.getDefinitionsStore();
        SettingsStore settingsStore = mStore.getSettingsStore();


        DependenciesForCow result = new DependenciesForCow();
        InputDefaultsSettings inputSettings = settingsStore.loadSettings(InputDefaultsSettings.class);
        result.setInputDefaults(inputSettings);

        Collection<CowClassObj> cowClassObjs = definitionsStore.fetchAllClasses();
        for(CowClassObj classObj : cowClassObjs){
            result.addClass(classObj);
        }

        Collection<StockObj> stockObjs = definitionsStore.fetchAllStocks();
        for(StockObj stockObj : stockObjs){
            result.addStock(stockObj);
        }

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

    private void onLoadSuccessful(DependenciesForCow result) {
        if (mObserver != null)
            mObserver.onLoadSuccessful(result);
    }

    private void onLoadError(Exception e) {
        if (mObserver != null)
            mObserver.onLoadError(e);
    }

    protected void onPostExecute(TaskResult<DependenciesForCow> result) {
        mResult =  result;
        if (!result.isError()) {
            onLoadSuccessful(result.getResult());
        } else {
            onLoadError(result.getException());
        }
    }

    public TaskResult<DependenciesForCow> getResult() {
        return mResult;
    }


}
