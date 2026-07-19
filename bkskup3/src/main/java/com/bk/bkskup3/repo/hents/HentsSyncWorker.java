package com.bk.bkskup3.repo.hents;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.bk.bkskup3.BkApplication;
import com.bk.bkskup3.dao.BkStore;
import com.bk.bkskup3.dao.HentsStore;
import com.bk.bkskup3.dao.SettingsStore;
import com.bk.bkskup3.repo.serialization.mapper.RepoObjectMapper;
import com.bk.bkskup3.repo.serialization.wire.JsonHent;
import com.bk.bkskup3.settings.HentSyncState;
import com.bk.bkskup3.settings.RepoSettings;
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

public class HentsSyncWorker extends Worker {

    private static final String TAG = HentsSyncWorker.class.getSimpleName();


    public HentsSyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        BkApplication application = (BkApplication) getApplicationContext();
        BkStore store = application.getStore();

        WorkWrapper workWrapper = new WorkWrapper(store);
        try {
            Log.i(TAG,"performing hent synchronization");
            workWrapper.doWork();
            Log.i(TAG,"hent synchronization attempt completed");
        } catch (FetchException e) {
            Log.e(TAG,"an error occurred while performing hent synchronization");
            return Result.retry();
        }
        return Result.success();
    }

    static class WorkWrapper {
        private final BkStore mStore;
        private final HentsStore mHentsStore;
        private final SettingsStore mSettingsStore;

        public WorkWrapper(BkStore store) {
            this.mStore = store;
            this.mHentsStore = mStore.getHentsStore();
            this.mSettingsStore = mStore.getSettingsStore();
        }

        private void saveUpdates(List<JsonHent> hentUpdates) {
            for (JsonHent jsonHent : hentUpdates) {
                mHentsStore.saveHent(jsonHent.getHent());
            }
        }

        public void doWork() throws FetchException {
            final RepoSettings repoSettings =  mSettingsStore.loadSettings(RepoSettings.class);

            if(Strings.isNullOrEmpty(repoSettings.getRepoLogin())) {
                throw new FetchException("missing repo login");
            }

            if(Strings.isNullOrEmpty(repoSettings.getRepoPassword())) {
                throw new FetchException("missing repo password");
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
                        throw new FetchException("server returned empty response");
                    }
                }
                else
                {
                    throw new FetchException(response.errorBody().string());
                }
            } catch (Exception e) {
                throw new FetchException(e.getMessage());
            }
        }
    }

    static class FetchException extends Exception {
        public FetchException(String message) {
            super(message);
        }
    }

    public interface HentUpdatesInterface {
        @GET("fetchhentupdates")
        Call<HentFetchResult> fetchHentUpdates(@Query("since") long since);
    }

}



