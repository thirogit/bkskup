package com.bk.bkskup3.tasks;

import android.os.AsyncTask;

import com.bk.bkskup3.dao.HentsStore;
import com.bk.bkskup3.model.HentObj;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/30/2014
 * Time: 5:38 PM
 */
public class LoadAllHentsTask extends AsyncTask<Void, Void, TaskResult<Collection<HentObj>>> {

    public interface Observer {
        void onLoadStarted();

        void onLoadSuccessful(Collection<HentObj> result);

       void onLoadError(Exception e);
    }

    private HentsStore mStore;
    private Observer mObserver;
    private TaskResult<Collection<HentObj>> mResult;

    public LoadAllHentsTask(HentsStore store) {
        this.mStore = store;
    }

    @Override
    protected TaskResult<Collection<HentObj>> doInBackground(Void... params) {

        Collection<HentObj> hentObjs = mStore.fetchAllHents();

        return TaskResult.withResult(hentObjs);
    }

    public TaskResult<Collection<HentObj>> getResult() {
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

    private void onLoadSuccessful(Collection<HentObj> result) {
        if (mObserver != null)
            mObserver.onLoadSuccessful(result);
    }

    private void onLoadError(Exception e) {
        if (mObserver != null)
            mObserver.onLoadError(e);
    }

    protected void onPostExecute(TaskResult<Collection<HentObj>> result) {
        mResult = result;
        if (!result.isError()) {
            onLoadSuccessful(result.getResult());
        } else {
            onLoadError(result.getException());
        }
    }


}

