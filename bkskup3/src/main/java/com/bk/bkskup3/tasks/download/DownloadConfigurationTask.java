package com.bk.bkskup3.tasks.download;

import android.os.AsyncTask;

import com.bk.bkskup3.dao.BkStore;
import com.bk.bkskup3.dao.SettingsStore;
import com.bk.bkskup3.repo.serialization.mapper.RepoObjectMapper;
import com.bk.bkskup3.repo.serialization.wire.JsonCompany;
import com.bk.bkskup3.settings.RepoSettings;
import com.bk.bkskup3.tasks.TaskResult;
import com.google.common.base.Strings;

import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/7/2014
 * Time: 2:56 PM
 */
public abstract class DownloadConfigurationTask<T> extends AsyncTask<Void, Void, TaskResult<T>> {

    private DownloadTaskObserver<T> mObserver;
    private BkStore mStore;
    private TaskResult<T> mResult;

    public DownloadConfigurationTask(BkStore store) {
        this.mStore = store;
    }

    @Override
    protected TaskResult<T> doInBackground(Void... params) {

        SettingsStore settingsStorage = mStore.getSettingsStore();
        final RepoSettings repoSettings = settingsStorage.loadSettings(RepoSettings.class);

        if (Strings.isNullOrEmpty(repoSettings.getRepoLogin()) || Strings.isNullOrEmpty(repoSettings.getRepoPassword())) {
            return TaskResult.withError(new IllegalArgumentException("missing login or password"));
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



        try
        {
            T result = download(retrofit);
            return TaskResult.withResult(result);
        } catch (Exception e) {
            return TaskResult.withError(e);
        }


    }

    protected abstract T download(Retrofit retrofit) throws Exception;


    @Override
    protected void onPreExecute() {
        onTaskStarted();
    }

    public void detachObserver() {
        mObserver = null;
    }

    public void attachObserver(DownloadTaskObserver mObserver) {
        this.mObserver = mObserver;
    }


    private void onTaskStarted() {
        mResult = null;
        if (mObserver != null)
            mObserver.onLoadStarted();
    }

    private void onLoadSuccessful(T result) {
        if (mObserver != null)
            mObserver.onLoadSuccessful(result);
    }

    private void onLoadError(Exception e) {
        if (mObserver != null)
            mObserver.onLoadError(e);
    }

    protected void onPostExecute(TaskResult<T> result) {
        mResult =  result;
        if (!result.isError()) {
            onLoadSuccessful(result.getResult());
        } else {
            onLoadError(result.getException());
        }
    }

    public TaskResult<T> getResult() {
        return mResult;
    }


}
