package com.bk.bkskup3.tasks;

import android.os.AsyncTask;

import com.bk.bkskup3.library.DocumentLibraryService;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/19/2014
 * Time: 4:47 PM
 */
public class DeleteProfileTask extends AsyncTask<Void, Void, TaskResult<Integer>> {

    public interface Observer {
        void onDeleteStarted();

        void onDeleteSuccessful(Integer result);

        void onDeleteError(Exception e);
    }

    private Observer mObserver;
    private DocumentLibraryService mService;
    private int mProfileId;
    private TaskResult<Integer> mResult;

    public DeleteProfileTask(DocumentLibraryService service, int profileId) {
        this.mService = service;
        this.mProfileId = profileId;
    }

    @Override
    protected TaskResult<Integer> doInBackground(Void... params) {

        try {
            mService.deleteProfile(mProfileId);
            return TaskResult.withResult(mProfileId);
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
            mObserver.onDeleteStarted();
    }

    private void onSuccessful() {
        if (mObserver != null)
            mObserver.onDeleteSuccessful(mProfileId);
    }

    private void onError(Exception e) {
        if (mObserver != null)
            mObserver.onDeleteError(e);
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

    public int getProfileId() {
        return mProfileId;
    }
}
