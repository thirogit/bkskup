package com.bk.bkskup3.tasks.download;

import com.bk.bkskup3.dao.BkStore;
import com.bk.bkskup3.model.CowClassObj;
import com.bk.bkskup3.model.HerdObj;
import com.bk.bkskup3.repo.serialization.wire.JsonCowClass;
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
public class DownloadClassesTask extends DownloadConfigurationTask<List<CowClassObj>> {

    public DownloadClassesTask(BkStore store) {
        super(store);
    }

    public interface FetchConfigurationInterface {
        @GET("fetchconfiguration?classes")
        Call<List<JsonCowClass>> fetchClasses();
    }

    @Override
    protected List<CowClassObj> download(Retrofit retrofit) throws Exception {
        FetchConfigurationInterface uploadInterface = retrofit.create(FetchConfigurationInterface.class);

        Call<List<JsonCowClass>> classesCall = uploadInterface.fetchClasses();
        Response<List<JsonCowClass>> result = classesCall.execute();

        List<JsonCowClass> jsonCowClasses = result.body();
        ArrayList<CowClassObj> cowClassObjs = new ArrayList<>(jsonCowClasses.size());
        for(JsonCowClass jsonCowClass : jsonCowClasses)
        {
            cowClassObjs.add(jsonCowClass.getCowClass());
        }
        return cowClassObjs;
    }






}
