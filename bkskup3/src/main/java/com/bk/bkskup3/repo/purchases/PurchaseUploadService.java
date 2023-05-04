package com.bk.bkskup3.repo.purchases;

import android.util.Log;

import com.bk.bkskup3.BkApplication;
import com.bk.bkskup3.dao.BkStore;
import com.bk.bkskup3.tasks.TaskResult;
import com.crashlytics.android.Crashlytics;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class PurchaseUploadService extends JobService {

    private static final String TAG = PurchaseUploadService.class.getSimpleName();

    private BkStore mStore;


    private UploadPurchaseTask mUploadTask;

    private UploadPurchaseTask.Observer mObserver = new UploadPurchaseTask.Observer() {
        @Override
        public void onTaskStarted() {

        }

        @Override
        public void onTaskSuccessful(TaskResult<Integer> result) {

            mUploadTask = null;
            Integer purchaseId = result.getResult();
            if(purchaseId != null) {
                onPurchaseUploaded(purchaseId);
            }
        }

        @Override
        public void onTaskError(Exception e) {
            mUploadTask = null;
            Log.e(TAG,"upload task finished with error: " + e.getMessage());
            Crashlytics.logException(e);
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        mStore = ((BkApplication)getApplication()).getStore();
    }

    private void onPurchaseUploaded(int purchaseId) {
        notifyPurchaseUploaded(purchaseId);
    }

    private void notifyPurchaseUploaded(int purchaseId) {
//        Message msg = Message.obtain(null, RepoServiceCommons.MSG_NOTIFY_PURCHASE_UPLOADED);
//        msg.arg1 = purchaseId;
//        sendMsgToListeners(msg);
    }

    @Override
    public boolean onStartJob(JobParameters job) {
        if (mUploadTask == null) {

            mUploadTask = new UploadPurchaseTask(mStore);
            mUploadTask.attachObserver(mObserver);
            mUploadTask.execute();
            return false;
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
