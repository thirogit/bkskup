package com.bk.bkskup3.tasks.download;

import com.bk.bkskup3.dao.BkStore;
import com.bk.bkskup3.model.CowClassObj;
import com.bk.bkskup3.model.StockObj;
import com.bk.bkskup3.repo.serialization.wire.JsonCowClass;
import com.bk.bkskup3.repo.serialization.wire.JsonStock;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/7/2014
 * Time: 2:56 PM
 */
public class DownloadStocksTask extends DownloadConfigurationTask<List<StockObj>> {

    public DownloadStocksTask(BkStore store) {
        super(store);
    }

    public interface FetchConfigurationInterface {
        @GET("fetchconfiguration?stocks")
        Call<List<JsonStock>> fetchClasses();
    }

    @Override
    protected List<StockObj> download(Retrofit retrofit) throws Exception {
        FetchConfigurationInterface uploadInterface = retrofit.create(FetchConfigurationInterface.class);

        Call<List<JsonStock>> classesCall = uploadInterface.fetchClasses();
        Response<List<JsonStock>> result = classesCall.execute();

        List<JsonStock> jsonStocks = result.body();
        ArrayList<StockObj> stockObjs = new ArrayList<>(jsonStocks.size());
        for(JsonStock jsonStock : jsonStocks)
        {
            stockObjs.add(jsonStock.getStock());
        }
        return stockObjs;
    }






}
