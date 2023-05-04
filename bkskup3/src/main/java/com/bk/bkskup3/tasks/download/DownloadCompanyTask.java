package com.bk.bkskup3.tasks.download;

import android.os.AsyncTask;

import com.bk.bkskup3.dao.BkStore;
import com.bk.bkskup3.dao.DefinitionsStore;
import com.bk.bkskup3.dao.SettingsStore;
import com.bk.bkskup3.model.Company;
import com.bk.bkskup3.model.CompanyObj;
import com.bk.bkskup3.model.CowClassObj;
import com.bk.bkskup3.model.StockObj;
import com.bk.bkskup3.repo.purchases.PurchaseEnvelope;
import com.bk.bkskup3.repo.serialization.mapper.RepoObjectMapper;
import com.bk.bkskup3.repo.serialization.wire.JsonCompany;
import com.bk.bkskup3.repo.serialization.wire.JsonPurchase;
import com.bk.bkskup3.settings.InputDefaultsSettings;
import com.bk.bkskup3.settings.RepoSettings;
import com.bk.bkskup3.tasks.TaskResult;
import com.bk.bkskup3.work.DependenciesForCow;
import com.google.common.base.Strings;

import java.io.IOException;
import java.util.Collection;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/7/2014
 * Time: 2:56 PM
 */
public class DownloadCompanyTask extends DownloadConfigurationTask<CompanyObj> {


    public DownloadCompanyTask(BkStore store) {
        super(store);
    }

    public interface FetchConfigurationInterface {
        @GET("fetchconfiguration?company")
        Call<JsonCompany> fetchCompany();
    }

    @Override
    protected CompanyObj download(Retrofit retrofit) throws Exception {
        FetchConfigurationInterface uploadInterface = retrofit.create(FetchConfigurationInterface.class);

        Call<JsonCompany> companyCall = uploadInterface.fetchCompany();
        Response<JsonCompany> result = companyCall.execute();
        CompanyObj companyObj = new CompanyObj();
        companyObj.copyFrom(result.body().getCompany());
        return companyObj;

    }






}
