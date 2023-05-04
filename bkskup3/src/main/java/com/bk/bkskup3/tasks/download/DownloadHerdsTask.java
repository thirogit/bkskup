package com.bk.bkskup3.tasks.download;

import com.bk.bkskup3.dao.BkStore;
import com.bk.bkskup3.model.CompanyObj;
import com.bk.bkskup3.model.Herd;
import com.bk.bkskup3.model.HerdObj;
import com.bk.bkskup3.repo.serialization.wire.JsonCompany;
import com.bk.bkskup3.repo.serialization.wire.JsonHerd;

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
public class DownloadHerdsTask extends DownloadConfigurationTask<List<HerdObj>> {

    public DownloadHerdsTask(BkStore store) {
        super(store);
    }

    public interface FetchConfigurationInterface {
        @GET("fetchconfiguration?herds")
        Call<List<JsonHerd>> fetchHerds();
    }

    @Override
    protected List<HerdObj> download(Retrofit retrofit) throws Exception {
        FetchConfigurationInterface uploadInterface = retrofit.create(FetchConfigurationInterface.class);

        Call<List<JsonHerd>> herdsCall = uploadInterface.fetchHerds();
        Response<List<JsonHerd>> result = herdsCall.execute();

        List<JsonHerd> jsonHerds = result.body();
        ArrayList<HerdObj> herds = new ArrayList<>(jsonHerds.size());
        for(JsonHerd jsonHerd : jsonHerds)
        {
            herds.add(jsonHerd.getHerd());
        }
        return herds;
    }






}
