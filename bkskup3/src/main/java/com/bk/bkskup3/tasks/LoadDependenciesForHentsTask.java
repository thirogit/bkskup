package com.bk.bkskup3.tasks;

import android.os.AsyncTask;

import com.bk.bkskup3.dao.BkStore;
import com.bk.bkskup3.dao.SettingsStore;
import com.bk.bkskup3.settings.InputDefaultsSettings;
import com.bk.bkskup3.settings.RepoSettings;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/30/2014
 * Time: 5:38 PM
 */
public class LoadDependenciesForHentsTask extends AsyncTask<Void, Void, TaskResult<DependenciesForHents>> {

    public interface Observer {
        public abstract void onLoadStarted();

        public abstract void onLoadSuccessful(DependenciesForHents result);

        public abstract void onLoadError(Exception e);
    }

    private SettingsStore mStore;
    private Observer mObserver;
    private TaskResult<DependenciesForHents> mResult;

    public LoadDependenciesForHentsTask(SettingsStore store) {
        this.mStore = store;
    }

    @Override
    protected TaskResult<DependenciesForHents> doInBackground(Void... params) {

        RepoSettings repoSettings = mStore.loadSettings(RepoSettings.class);
        InputDefaultsSettings inputDefaults = mStore.loadSettings(InputDefaultsSettings.class);

        DependenciesForHents result = new DependenciesForHents();
        result.setRepoSettings(repoSettings);
        result.setInputDefaults(inputDefaults);

        return TaskResult.withResult(result);
    }

    public TaskResult<DependenciesForHents> getResult() {
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

    private void onLoadSuccessful(DependenciesForHents result) {
        if (mObserver != null)
            mObserver.onLoadSuccessful(result);
    }

    private void onLoadError(Exception e) {
        if (mObserver != null)
            mObserver.onLoadError(e);
    }

    protected void onPostExecute(TaskResult<DependenciesForHents> result) {
        mResult = result;
        if (!result.isError()) {
            onLoadSuccessful(result.getResult());
        } else {
            onLoadError(result.getException());
        }
    }


}

