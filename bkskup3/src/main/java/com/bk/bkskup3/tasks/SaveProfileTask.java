package com.bk.bkskup3.tasks;

import android.os.AsyncTask;

import com.bk.bkskup3.library.DocumentLibraryService;
import com.bk.bkskup3.library.DocumentProfile;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/19/2014
 * Time: 4:47 PM
 */
public class SaveProfileTask extends AsyncTask<Void, Void, TaskResult<Integer>> {

    public interface Observer {
        void onSaveStarted();

        void onSaveSuccessful(Integer result);

        void onSaveError(Exception e);
    }

    private Observer mObserver;
    private DocumentLibraryService mService;
    private DocumentProfile mProfile;
    private TaskResult<Integer> mResult;

    public SaveProfileTask(DocumentLibraryService service, DocumentProfile profile) {
        this.mService = service;
        this.mProfile = profile;
    }

    @Override
    protected TaskResult<Integer> doInBackground(Void... params) {

        try {
            mService.updateProfile(mProfile);
            return TaskResult.withResult(mProfile.getProfileId());
        } catch (Exception e) {
            return TaskResult.withError(e);
        }
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
            mObserver.onSaveStarted();
    }

    private void onSuccessful() {
        if (mObserver != null)
            mObserver.onSaveSuccessful(mProfile.getProfileId());
    }

    private void onError(Exception e) {
        if (mObserver != null)
            mObserver.onSaveError(e);
    }

    protected void onPostExecute(TaskResult<Integer> result) {
        mResult =  result;
        if (!result.isError()) {
            onSuccessful();
        } else {
            onError(result.getException());
        }
    }

    public TaskResult<Integer> getResult() {
        return mResult;
    }


}
