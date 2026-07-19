package com.bk.bkskup3;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.bk.bkskup3.dao.BkStore;
import com.bk.bkskup3.dao.DefinitionsStore;
import com.bk.bkskup3.dao.DocumentOptionsStore;
import com.bk.bkskup3.dao.HentsStore;
import com.bk.bkskup3.dao.InvoiceNoTransactionStore;
import com.bk.bkskup3.dao.PurchasesStore;
import com.bk.bkskup3.dao.SettingsStore;
import com.bk.bkskup3.db.SQLDatabaseQueue;
import com.bk.bkskup3.db.SQLDatabaseWrapper;
import com.bk.bkskup3.db.SchemaUpdater;
import com.bk.bkskup3.library.DocumentLibraryService;
import com.bk.bkskup3.management.AgentActivity;
import com.bk.bkskup3.management.ClassesActivity;
import com.bk.bkskup3.management.CompanyActivity;
import com.bk.bkskup3.management.DeductionsActivity;
import com.bk.bkskup3.management.DocumentProfilesActivity;
import com.bk.bkskup3.management.EditCowClassActivity;
import com.bk.bkskup3.management.EditDeductionActivity;
import com.bk.bkskup3.management.EditHerdActivity;
import com.bk.bkskup3.management.EditStockActivity;
import com.bk.bkskup3.management.HerdsManagementActivity;
import com.bk.bkskup3.management.InvoiceSettingsActivity;
import com.bk.bkskup3.management.ManagementActivity;
import com.bk.bkskup3.management.NewCowClassActivity;
import com.bk.bkskup3.management.NewDeductionActivity;
import com.bk.bkskup3.management.NewHerdActivity;
import com.bk.bkskup3.management.NewStockActivity;
import com.bk.bkskup3.management.StocksActivity;
import com.bk.bkskup3.management.TaxRatesActivity;
import com.bk.bkskup3.preferences.AgentPreferencesActivity;
import com.bk.bkskup3.print.PrintActivity;
import com.bk.bkskup3.repo.hents.HentsSyncWorker;
import com.bk.bkskup3.utils.Intents;
import com.bk.bkskup3.work.CowNoScanActivity;
import com.bk.bkskup3.work.EditCowActivity;
import com.bk.bkskup3.work.EditHentActivity;
import com.bk.bkskup3.work.EditInvoiceActivity;
import com.bk.bkskup3.work.FindHentActivity;
import com.bk.bkskup3.work.HentNoScanActivity;
import com.bk.bkskup3.work.InvoiceActivity;
import com.bk.bkskup3.work.InvoiceViewActivity;
import com.bk.bkskup3.work.NewCowActivity;
import com.bk.bkskup3.work.NewHentActivity;
import com.bk.bkskup3.work.NewInvoiceActivity;
import com.bk.bkskup3.work.OpenNewPurchaseActivity;
import com.bk.bkskup3.work.OpenPurchasesActivity;
import com.bk.bkskup3.work.PurchaseEditActivity;
import com.bk.bkskup3.work.PurchaseViewActivity;
import com.bk.bkskup3.work.PurchasesHistoryActivity;
import com.bk.bkskup3.work.QuickCowActivity;
import com.bk.bkskup3.work.ScanHentActivity;
import com.bk.print.service.PrintService;
import com.couchbase.lite.CouchbaseLite;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.common.ExceptionUtil;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;

/**
 * Created by IntelliJ IDEA.
 * User: root
 * Date: 22.06.11
 * Time: 21:09
 */

public class BkApplication extends Application {
    @Module(injects = {
            AgentActivity.class,
            ClassesActivity.class,
            PrintActivity.class,
            CompanyActivity.class,
            NewCowClassActivity.class,
            EditCowClassActivity.class,
            NewDeductionActivity.class,
            EditDeductionActivity.class,
            DeductionsActivity.class,
            NewHerdActivity.class,
            EditHerdActivity.class,
            InvoiceViewActivity.class,
            QuickCowActivity.class,
            OpenPurchasesActivity.class,
            NewStockActivity.class,
            EditStockActivity.class,
            HerdsManagementActivity.class,
            ManagementActivity.class,
            WelcomeActivity.class,
            ScanHentActivity.class,
            PurchasesHistoryActivity.class,
            OpenNewPurchaseActivity.class,
            NewCowActivity.class,
            EditCowActivity.class,
            AgentPreferencesActivity.class,
            TaxRatesActivity.class,
            InvoiceActivity.class,
            FindHentActivity.class,
            PurchaseViewActivity.class,
            PurchaseEditActivity.class,
            NewInvoiceActivity.class,
            EditInvoiceActivity.class,
            StocksActivity.class,
            InvoiceSettingsActivity.class,
            DocumentProfilesActivity.class,
            NewHentActivity.class,
            EditHentActivity.class,
            CowNoScanActivity.class,
            HentNoScanActivity.class,
    },
            library = true)
    public class StoreDependenciesModule {

        private BkStore bkStore;

        public StoreDependenciesModule(BkStore bkStore) {
            this.bkStore = bkStore;
        }

        @Provides
        BkStore bkStore() {
            return bkStore;
        }

        @Provides
        public HentsStore hentsStore() {
            return bkStore.getHentsStore();
        }

        @Provides
        public PurchasesStore purchasesStore() {
            return bkStore.getPurchasesStore();
        }

        @Provides
        public DefinitionsStore definitionsStore() {
            return bkStore.getDefinitionsStore();
        }

        @Provides
        public DocumentOptionsStore documentOptionsStore() {
            return bkStore.getDocumentOptionsStore();
        }

        @Provides
        public InvoiceNoTransactionStore invoiceNoTransactionStore() {
            return bkStore.getInvoiceNoTransactionStore();
        }

        @Provides
        public SettingsStore settingsStore() {
            return bkStore.getSettingsStore();
        }

    }


    SQLDatabaseQueue bkDb;
    BkStore bkStore;
    ObjectGraph mObjectGraph;

    public BkStore getStore() {
        return bkStore;
    }


    public void onCreate() {
        super.onCreate();

        CouchbaseLite.init(this);
        Stetho.initializeWithDefaults(this);

        SQLiteDatabase db = this.openOrCreateDatabase("bkskup3", MODE_PRIVATE, null);
        SQLDatabaseWrapper dbWrapper = new SQLDatabaseWrapper(db);

        bkDb = new SQLDatabaseQueue(dbWrapper, new ThreadFactory() {
            @Override
            public Thread newThread(@NonNull Runnable r) {
                return new Thread(r, "SQLDatabaseQueue-bkskup3");
            }
        });
        SchemaUpdater schemaUpdater = new SchemaUpdater(bkDb);

        try {
            schemaUpdater.update(this.getResources().openRawResource(R.raw.schema1), 1);
//            schemaUpdater.update(this.getResources().openRawResource(R.raw.schema2), 2);
        } catch (IOException e) {
            ExceptionUtil.propagate(e);
        }
        bkStore = new BkStore(bkDb);

//        startService(Intents.makeExplicit(this.getBaseContext(),new Intent(BarcodeService.class.getName())));
        startService(Intents.makeExplicit(this.getBaseContext(), new Intent(PrintService.class.getName())));
        startService(new Intent(this, DocumentLibraryService.class));

        Constraints constraints = (new Constraints.Builder())
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build();


        PeriodicWorkRequest hentSyncWorkRequest =
                new PeriodicWorkRequest.Builder(HentsSyncWorker.class, 30, TimeUnit.MINUTES)
                        .setBackoffCriteria(BackoffPolicy.LINEAR, Duration.ofMinutes(1))
                        .setConstraints(constraints)
                        .build();

        WorkManager
                .getInstance(this)
                .enqueueUniquePeriodicWork("hent-synch", ExistingPeriodicWorkPolicy.KEEP, hentSyncWorkRequest);



        mObjectGraph = ObjectGraph.create(new StoreDependenciesModule(bkStore));


    }

    public void inject(Activity bkActivity) {
        mObjectGraph.inject(bkActivity);
    }
}
