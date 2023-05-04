package com.bk.bkskup3.tasks;

import android.os.AsyncTask;

import com.bk.bkskup3.dao.SettingsStore;
import com.bk.bkskup3.settings.RepoSettings;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/30/2014
 * Time: 5:38 PM
 */
public class LoadRepoSettingsTask extends AsyncTask<Void, Void, TaskResult<RepoSettings>> {

    public interface Observer {
        void onLoadStarted();

        void onLoadSuccessful(RepoSettings result);

       void onLoadError(Exception e);
    }

    private SettingsStore mStore;
    private Observer mObserver;
    private TaskResult<RepoSettings> mResult;

    public LoadRepoSettingsTask(SettingsStore store) {
        this.mStore = store;
    }

    @Override
    protected TaskResult<RepoSettings> doInBackground(Void... params) {

        RepoSettings settings = mStore.loadSettings(RepoSettings.class);

        return TaskResult.withResult(settings);
    }

    public TaskResult<RepoSettings> getResult() {
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

    private void onLoadSuccessful(RepoSettings result) {
        if (mObserver != null)
            mObserver.onLoadSuccessful(result);
    }

    private void onLoadError(Exception e) {
        if (mObserver != null)
            mObserver.onLoadError(e);
    }

    protected void onPostExecute(TaskResult<RepoSettings> result) {
        mResult = result;
        if (!result.isError()) {
            onLoadSuccessful(result.getResult());
        } else {
            onLoadError(result.getException());
        }
    }


}

