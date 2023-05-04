package com.bk.bkskup3.repo.hents;

/**
 * Created by SG0891787 on 3/16/2018.
 */

import android.util.Log;

import com.bk.bkskup3.BkApplication;
import com.bk.bkskup3.dao.BkStore;
import com.bk.bkskup3.tasks.TaskResult;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class HentsSyncService extends JobService {

    private static final String TAG = "HentsSyncService";


    private FetchHentsUpdateChunkTask mFetchTask;
    private BkStore mStore;

    private FetchHentsUpdateChunkTask.Observer mObserver = new FetchHentsUpdateChunkTask.Observer() {
        @Override
        public void onTaskStarted() {

        }

        @Override
        public void onTaskSuccessful(TaskResult<Void> result) {
            mFetchTask = null;
        }

        @Override
        public void onTaskError(Exception e) {
            mFetchTask = null;
            Log.e(TAG,"fetch task finished with error: " + e.getMessage());
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        BkApplication application = (BkApplication) getApplication();
        mStore = application.getStore();
        Log.i(TAG, "Service created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Service destroyed");
    }

    @Override
    public boolean onStartJob(JobParameters job) {

        if (mFetchTask == null) {

            mFetchTask = new FetchHentsUpdateChunkTask(mStore);
            mFetchTask.attachObserver(mObserver);
            mFetchTask.execute();
            return false;
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
