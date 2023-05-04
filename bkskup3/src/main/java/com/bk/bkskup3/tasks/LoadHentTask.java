package com.bk.bkskup3.tasks;

import android.os.AsyncTask;

import com.bk.bkskup3.dao.HentsStore;
import com.bk.bkskup3.model.EAN;
import com.bk.bkskup3.model.HentObj;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/19/2014
 * Time: 10:07 PM
 */
public class LoadHentTask extends AsyncTask<Void, Void, TaskResult<HentObj>> {

    public interface Observer {
        void onLoadStarted();

        void onLoadSuccessful(HentObj result);

        void onLoadError(Exception e);
    }

    private EAN mHentNo;
    private HentsStore mStore;
    private Observer mObserver;
    private TaskResult<HentObj> mResult;

    public LoadHentTask(EAN hentNo,HentsStore store) {
        this.mStore = store;
        this.mHentNo = hentNo;
    }

    @Override
    protected TaskResult<HentObj> doInBackground(Void... params) {
        HentObj hentObj = mStore.fetchHent(mHentNo);
        return TaskResult.withResult(hentObj);
    }

    public TaskResult<HentObj> getResult() {
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

    private void onLoadSuccessful(HentObj result) {
        if (mObserver != null)
            mObserver.onLoadSuccessful(result);
    }

    private void onLoadError(Exception e) {
        if (mObserver != null)
            mObserver.onLoadError(e);
    }

    protected void onPostExecute(TaskResult<HentObj> result) {
        mResult =  result;
        if (!result.isError()) {
            onLoadSuccessful(result.getResult());
        } else {
            onLoadError(result.getException());
        }
    }


}
