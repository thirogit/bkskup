package com.bk.bkskup3.repo.hents;

import android.os.AsyncTask;

import com.bk.bkskup3.dao.BkStore;
import com.bk.bkskup3.dao.HentsStore;
import com.bk.bkskup3.dao.SettingsStore;
import com.bk.bkskup3.repo.serialization.mapper.RepoObjectMapper;
import com.bk.bkskup3.repo.serialization.wire.JsonHent;
import com.bk.bkskup3.settings.HentSyncState;
import com.bk.bkskup3.settings.RepoSettings;
import com.bk.bkskup3.tasks.TaskResult;
import com.google.common.base.Strings;

import java.io.IOException;
import java.util.List;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class FetchHentsUpdateChunkTask extends AsyncTask<Void, Void, TaskResult<Void>> {

    public interface HentUpdatesInterface {
        @GET("fetchhentupdates")
        Call<HentFetchResult> fetchHentUpdates(@Query("since") long since);
    }

    public interface Observer {
        void onTaskStarted();

        void onTaskSuccessful(TaskResult<Void> result);

        void onTaskError(Exception e);
    }

    private BkStore mStore;
    private Observer mObserver;
    private TaskResult<Void> mResult;
    private HentsStore mHentsStore;
    private SettingsStore mSettingsStore;

    public FetchHentsUpdateChunkTask(BkStore store) {
        this.mStore = store;
        this.mHentsStore = mStore.getHentsStore();
        this.mSettingsStore = mStore.getSettingsStore();
    }

    @Override
    protected TaskResult<Void> doInBackground(Void... params) {


        final RepoSettings repoSettings =  mSettingsStore.loadSettings(RepoSettings.class);

        if(Strings.isNullOrEmpty(repoSettings.getRepoLogin()) || Strings.isNullOrEmpty(repoSettings.getRepoPassword()))
        {
            return TaskResult.withError(new IllegalArgumentException("missing login or password"));
        }

        HentSyncState syncState = mSettingsStore.loadSettings(HentSyncState.class);
        if (syncState == null) {
            syncState = new HentSyncState();

        }
        else
        {
            if(syncState.getLastFetchMaxModified() == null)
            {
                syncState.setLastFetchMaxModified(0L);
            }
        }

        String repoAddress = RepoSettings.REPO_ADDRESS;

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request.Builder ongoing = chain.request().newBuilder();
                String credential = Credentials.basic(repoSettings.getRepoLogin(), repoSettings.getRepoPassword());
                ongoing.addHeader("Authorization", credential);
                return chain.proceed(ongoing.build());
            }
        });


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(repoAddress)
                .addConverterFactory(JacksonConverterFactory.create(new RepoObjectMapper()))
                .client(httpClient.build())

                .build();

        HentUpdatesInterface service = retrofit.create(HentUpdatesInterface.class);
        Call<HentFetchResult> hentFetchResultCall = service.fetchHentUpdates(syncState.getLastFetchMaxModified() + 1);

        try {
            Response<HentFetchResult> response = hentFetchResultCall.execute();
            if(response.isSuccessful()) {
                HentFetchResult result = response.body();
                if(result != null) {
                    List<JsonHent> chunk = result.getChunk();
                    if (chunk != null && !chunk.isEmpty()) {
                        saveUpdates(chunk);
                        syncState.setLastFetchMaxModified(result.getMaxLastModified());
                        mSettingsStore.saveSettings(syncState);
                    }
                }
                else
                {
                    return TaskResult.withError(new NullPointerException("server returned empty response"));
                }
            }
            else
            {
                return TaskResult.withError(new Exception(response.errorBody().string()));
            }
        } catch (Exception e) {
            return TaskResult.withError(e);
        }

        return TaskResult.withResult(null);
    }

    private void saveUpdates(List<JsonHent> hentUpdates) {
        for (JsonHent jsonHent : hentUpdates) {
            mHentsStore.saveHent(jsonHent.getHent());
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
            mObserver.onTaskStarted();
    }

    private void onTaskSuccessful() {
        if (mObserver != null)
            mObserver.onTaskSuccessful(TaskResult.withResult(null));
    }

    private void onTaskError(Exception e) {
        if (mObserver != null)
            mObserver.onTaskError(e);
    }

    protected void onPostExecute(TaskResult<Void> result) {
        mResult = result;
        if (!result.isError()) {
            onTaskSuccessful();
        } else {
            onTaskError(result.getException());
        }
    }

    public TaskResult<Void> getResult() {
        return mResult;
    }

}
