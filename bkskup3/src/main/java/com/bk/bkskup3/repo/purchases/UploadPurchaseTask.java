package com.bk.bkskup3.repo.purchases;

import android.os.AsyncTask;

import com.bk.bkskup3.dao.BkStore;
import com.bk.bkskup3.dao.HentsStore;
import com.bk.bkskup3.dao.PurchasesStore;
import com.bk.bkskup3.dao.SettingsStore;
import com.bk.bkskup3.dao.q.QPurchase;
import com.bk.bkskup3.model.Agent;
import com.bk.bkskup3.model.Cow;
import com.bk.bkskup3.model.CowDetails;
import com.bk.bkskup3.model.EAN;
import com.bk.bkskup3.model.Hent;
import com.bk.bkskup3.model.HentObj;
import com.bk.bkskup3.model.Invoice;
import com.bk.bkskup3.model.InvoiceDeduction;
import com.bk.bkskup3.model.InvoiceDetails;
import com.bk.bkskup3.model.InvoiceHent;
import com.bk.bkskup3.model.InvoiceHentObj;
import com.bk.bkskup3.model.PurchaseObj;
import com.bk.bkskup3.model.PurchaseState;
import com.bk.bkskup3.repo.serialization.mapper.RepoObjectMapper;
import com.bk.bkskup3.repo.serialization.wire.JsonCow;
import com.bk.bkskup3.repo.serialization.wire.JsonHent;
import com.bk.bkskup3.repo.serialization.wire.JsonInvoice;
import com.bk.bkskup3.repo.serialization.wire.JsonInvoiceDeduction;
import com.bk.bkskup3.repo.serialization.wire.JsonPurchase;
import com.bk.bkskup3.settings.RepoSettings;
import com.bk.bkskup3.runtime.InvoiceHentAdapter;
import com.bk.bkskup3.settings.AgentAsSettings;
import com.bk.bkskup3.tasks.TaskResult;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

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
import retrofit2.http.PUT;

import static com.mysema.query.support.QueryBuilder.where;

public class UploadPurchaseTask extends AsyncTask<Void, Void, TaskResult<Integer>> {

    public static class UploadResult {
        private String id;

        @JsonCreator
        public UploadResult(@JsonProperty("id") String id) {
            this.id = id;
        }

        @JsonProperty("id")
        public String getId() {
            return id;
        }
    }

    public interface PurchaseUploadInterface {
        @PUT("uploadpurchase")
        Call<UploadResult> uploadPurchase(@Body JsonPurchase purchase);
    }

    public interface Observer {
        void onTaskStarted();

        void onTaskSuccessful(TaskResult<Integer> result);

        void onTaskError(Exception e);
    }

    private BkStore mStore;
    private Observer mObserver;
    private TaskResult<Integer> mResult;

    public UploadPurchaseTask(BkStore store) {
        this.mStore = store;
    }

    private JsonPurchase jsonPurchase(PurchaseEnvelope purchaseEnvelope) {
        PurchaseObj purchaseObj = purchaseEnvelope.getPurchase();
        JsonPurchase jsonPurchase = new JsonPurchase(purchaseObj.getDetails());
        jsonPurchase.setAgentCd(purchaseEnvelope.getAgentCd());

        Collection<JsonInvoice> jsonInvoices = new LinkedList<JsonInvoice>();
        for (Invoice invoice : purchaseObj.getInvoices()) {
            JsonInvoice jsonInvoice = new JsonInvoice(new InvoiceDetails(invoice));

            Collection<JsonCow> jsonCows = new LinkedList<JsonCow>();
            for (Cow cow : invoice.getCows()) {
                jsonCows.add(new JsonCow(new CowDetails(cow)));
            }
            jsonInvoice.setCows(jsonCows);

            Collection<JsonInvoiceDeduction> jsonDeductions = new LinkedList<JsonInvoiceDeduction>();
            for (InvoiceDeduction deduction : invoice.getDeductions()) {
                jsonDeductions.add(new JsonInvoiceDeduction(deduction));
            }
            jsonInvoice.setDeductions(jsonDeductions);

            jsonInvoice.setHent(new JsonHent(new InvoiceHentAdapter(invoice.getInvoiceHent())));
            jsonInvoices.add(jsonInvoice);
        }
        jsonPurchase.setInvoices(jsonInvoices);

        Collection<JsonHent> jsonPurchaseHents = new ArrayList<JsonHent>();
        Collection<HentObj> purchaseHents = purchaseEnvelope.getHents();
        if (purchaseHents != null) {
            for (Hent purchaseHent : purchaseHents) {
                jsonPurchaseHents.add(new JsonHent(purchaseHent));
            }
        }
        jsonPurchase.setPurchaseHents(jsonPurchaseHents);
        return jsonPurchase;
    }


    private PurchaseEnvelope envelopePurchase(PurchaseObj purchaseObj) {

        HentsStore hentsStore = mStore.getHentsStore();

        Set<EAN> firstOwnerHentNos = new HashSet<EAN>();
        Map<EAN, InvoiceHent> invoiceHents = new HashMap<EAN, InvoiceHent>();

        Map<EAN, HentObj> hents = new HashMap<EAN, HentObj>();

        for (Invoice invoice : purchaseObj.getInvoices()) {
            for (Cow cow : invoice.getCows()) {
                EAN firstOwnerNo = cow.getFirstOwner();
                if (firstOwnerNo != null) {
                    firstOwnerHentNos.add(firstOwnerNo);
                }
            }

            InvoiceHent invoiceHent = invoice.getInvoiceHent();
            invoiceHents.put(invoiceHent.getHentNo(), invoiceHent);
        }

        for (EAN hentNo : firstOwnerHentNos) {
            HentObj hent = hentsStore.fetchHent(hentNo);
            if (hent != null) {
                hents.put(hentNo, hent);
            }
        }

        for (InvoiceHent invoiceHent : invoiceHents.values()) {
            EAN hentNo = invoiceHent.getHentNo();
            if (!hents.containsKey(hentNo)) {
                HentObj hent = hentsStore.fetchHent(hentNo);
                if (hent != null) {
                    hents.put(hentNo, hent);
                } else {
                    hents.put(hentNo, new InvoiceHentObj(invoiceHent).asHent());
                }
            }
        }

        SettingsStore settingsStore = mStore.getSettingsStore();

        Agent agent = settingsStore.loadSettings(AgentAsSettings.class);

        PurchaseEnvelope envelope = new PurchaseEnvelope();
        envelope.setAgentCd(agent.getCode());
        envelope.setHents(hents.values());
        envelope.setPurchase(purchaseObj);
        return envelope;
    }

    @Override
    protected TaskResult<Integer> doInBackground(Void... params) {

        try {

            SettingsStore settingsStorage = mStore.getSettingsStore();
            final RepoSettings repoSettings = settingsStorage.loadSettings(RepoSettings.class);

            if (Strings.isNullOrEmpty(repoSettings.getRepoLogin()) || Strings.isNullOrEmpty(repoSettings.getRepoPassword())) {
                return TaskResult.withError(new IllegalArgumentException("missing login or password"));
            }


            PurchasesStore purchasesStore = mStore.getPurchasesStore();
            Collection<PurchaseObj> purchasesToSend = purchasesStore.fetchPurchases(where(QPurchase.state.eq(PurchaseState.CLOSED)).limit(1));
            PurchaseObj purchaseToSend = Iterables.getFirst(purchasesToSend, null);

            if (purchaseToSend == null) {
                return TaskResult.withResult(null);
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

            PurchaseUploadInterface uploadInterface = retrofit.create(PurchaseUploadInterface.class);


            PurchaseEnvelope purchaseEnvelope = envelopePurchase(purchaseToSend);
            Call<UploadResult> uploadPurchaseCall = uploadInterface.uploadPurchase(jsonPurchase(purchaseEnvelope));

            Response<UploadResult> response = uploadPurchaseCall.execute();
            if (response.isSuccessful()) {

                purchasesStore.updatePurchaseState(purchaseToSend.getId(), PurchaseState.SENT);
                return TaskResult.withResult(purchaseToSend.getId());
            } else {
                return TaskResult.withError(new Exception(response.errorBody().string()));
            }
        } catch (Exception e) {
            return TaskResult.withError(e);
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

    private void onTaskSuccessful(Integer purchaseId) {
        if (mObserver != null)
            mObserver.onTaskSuccessful(TaskResult.withResult(purchaseId));
    }

    private void onTaskError(Exception e) {
        if (mObserver != null)
            mObserver.onTaskError(e);
    }

    protected void onPostExecute(TaskResult<Integer> result) {
        mResult = result;
        if (!result.isError()) {
            onTaskSuccessful(result.getResult());
        } else {
            onTaskError(result.getException());
        }
    }

    public TaskResult<Integer> getResult() {
        return mResult;
    }

}
