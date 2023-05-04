package com.bk.bkskup3.tasks;

import android.os.AsyncTask;

import com.bk.bkskup3.library.DocumentLibraryException;
import com.bk.bkskup3.library.DocumentLibraryService;
import com.bk.bkskup3.library.DocumentProfile;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/19/2014
 * Time: 4:47 PM
 */
public class CreateNewProfileTask extends AsyncTask<Void, Void, TaskResult<DocumentProfile>> {

    public interface Observer {
        void onSaveStarted();

        void onSaveSuccessful(DocumentProfile result);

        void onSaveError(Exception e);
    }

    private Observer mObserver;
    private DocumentLibraryService mService;
    private TaskResult<DocumentProfile> mResult;
    private String mProfileName;
    private String mDocumentCode;

    public CreateNewProfileTask(DocumentLibraryService service, String documentCode,String profileName) {
        this.mService = service;
        this.mProfileName = profileName;
        this.mDocumentCode = documentCode;
    }

    @Override
    protected TaskResult<DocumentProfile> doInBackground(Void... params) {

        try {
            DocumentProfile newProfile = mService.createNewProfile(mDocumentCode, mProfileName);
            return TaskResult.withResult(newProfile);
        } catch (DocumentLibraryException e) {
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

    private void onLoadSuccessful(DocumentProfile result) {
        if (mObserver != null)
            mObserver.onSaveSuccessful(result);
    }

    private void onLoadError(Exception e) {
        if (mObserver != null)
            mObserver.onSaveError(e);
    }

    protected void onPostExecute(TaskResult<DocumentProfile> result) {
        mResult =  result;
        if (!result.isError()) {
            onLoadSuccessful(result.getResult());
        } else {
            onLoadError(result.getException());
        }
    }

    public TaskResult<DocumentProfile> getResult() {
        return mResult;
    }
}
