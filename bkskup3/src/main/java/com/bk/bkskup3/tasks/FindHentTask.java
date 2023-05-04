package com.bk.bkskup3.tasks;

import android.os.AsyncTask;

import com.bk.bkskup3.dao.HentsStore;
import com.bk.bkskup3.dao.q.QHent;
import com.bk.bkskup3.model.HentObj;

import java.util.Collection;

import static com.mysema.query.support.QueryBuilder.where;

/**
* Created with IntelliJ IDEA.
* User: SG0891787
* Date: 11/25/2014
* Time: 9:15 PM
*/
public class FindHentTask extends AsyncTask<Void, Void, TaskResult<Collection<HentObj>>> {

    public interface Observer {
        public void onStarted();

        public void onFinished(Collection<HentObj> hents);

        public void onError(Exception e);
    }

    private String mSearchPattern;
    private Observer mObserver;
    private TaskResult<Collection<HentObj>> mResult;
    private HentsStore mStore;

    public FindHentTask(String searchPattern, HentsStore store) {
        this.mSearchPattern = searchPattern;
        this.mStore = store;
    }

    @Override
    protected void onPreExecute() {
        mResult = null;
        if (mObserver != null)
            mObserver.onStarted();
    }

    public void detachObserver() {
        mObserver = null;
    }


    public void attachObserver(Observer mObserver) {
        this.mObserver = mObserver;
    }

    private void onFinished(Collection<HentObj> hents) {
        if (mObserver != null)
            mObserver.onFinished(hents);
    }

    private void onError(Exception e) {
        if (mObserver != null)
            mObserver.onError(e);
    }

    protected void onPostExecute(TaskResult<Collection<HentObj>> result) {
        mResult = result;
        if (!result.isError()) {
            onFinished(mResult.getResult());
        } else {
            onError(result.getException());
        }
    }

    public TaskResult<Collection<HentObj>> getResult() {
        return mResult;
    }

    @Override
    protected TaskResult<Collection<HentObj>> doInBackground(Void... params) {

        String likeExpression = '%' + mSearchPattern + '%';
        Collection<HentObj> hents = mStore.fetchHents(where(QHent.hentNo.like(likeExpression).or(QHent.hentName.like(likeExpression.toLowerCase()).or(QHent.hentName.like(likeExpression.toUpperCase())))));
        return TaskResult.withResult(hents);
    }

}
