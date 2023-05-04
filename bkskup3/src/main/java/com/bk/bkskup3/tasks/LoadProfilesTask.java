package com.bk.bkskup3.tasks;

import android.os.AsyncTask;

import com.bk.bkskup3.library.DocumentLibraryException;
import com.bk.bkskup3.library.DocumentLibraryService;
import com.bk.bkskup3.library.DocumentProfile;
import com.google.firebase.crash.FirebaseCrash;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/19/2014
 * Time: 4:47 PM
 */
public class LoadProfilesTask extends AsyncTask<Void, Void, TaskResult<Collection<DocumentProfile>>> {

    public interface Observer {
        void onLoadStarted();

        void onLoadSuccessful(Collection<DocumentProfile> result);

        void onLoadError(Exception e);
    }

    private Observer mObserver;
    private DocumentLibraryService mService;
    private TaskResult<Collection<DocumentProfile>> mResult;
    private String mDocumentCode;

    public LoadProfilesTask(DocumentLibraryService service, String documentCode) {
        this.mService = service;
        this.mDocumentCode = documentCode;
    }

    @Override
    protected TaskResult<Collection<DocumentProfile>> doInBackground(Void... params) {

        try {
            Collection<DocumentProfile> documentProfiles = mService.getDocumentProfiles(mDocumentCode);
            return TaskResult.withResult(documentProfiles);
        } catch (DocumentLibraryException e) {
            FirebaseCrash.report(e);
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
            mObserver.onLoadStarted();
    }

    private void onLoadSuccessful(Collection<DocumentProfile> result) {
        if (mObserver != null)
            mObserver.onLoadSuccessful(result);
    }

    private void onLoadError(Exception e) {
        if (mObserver != null)
            mObserver.onLoadError(e);
    }

    protected void onPostExecute(TaskResult<Collection<DocumentProfile>> result) {
        mResult =  result;
        if (!result.isError()) {
            onLoadSuccessful(result.getResult());
        } else {
            onLoadError(result.getException());
        }
    }

    public TaskResult<Collection<DocumentProfile>> getResult() {
        return mResult;
    }
}
