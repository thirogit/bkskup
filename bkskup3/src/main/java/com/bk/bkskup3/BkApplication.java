package com.bk.bkskup3;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import com.bk.barcode.service.BarcodeService;
import com.bk.bkskup3.dao.BkStore;
import com.bk.bkskup3.dao.DefinitionsStore;
import com.bk.bkskup3.dao.DocumentOptionsStore;
import com.bk.bkskup3.dao.HentsStore;
import com.bk.bkskup3.dao.InvoiceNoTransactionStore;
import com.bk.bkskup3.dao.PurchasesStore;
import com.bk.bkskup3.dao.SettingsStore;
import com.bk.bkskup3.db.SQLDatabaseQueue;
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
import com.bk.bkskup3.repo.hents.HentsSyncService;
import com.bk.bkskup3.repo.purchases.PurchaseUploadService;
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
import com.bk.bkskup3.work.ScanBarcodeActivity;
import com.bk.bkskup3.work.ScanHentActivity;
import com.bk.print.service.PrintService;
import com.facebook.stetho.Stetho;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import java.io.IOException;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import io.reactivex.exceptions.Exceptions;


/**
 * Created by IntelliJ IDEA.
 * User: root
 * Date: 22.06.11
 * Time: 21:09
 */

public class BkApplication extends Application {


    public static final int UPLOAD_PURCHASE_INTERVAL_MINUTES = 1;
    public static final int FETCH_HENTS_UPDATES_INTERVAL_MINUTES = 1;

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
            ScanHentActivity.class,


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

        Stetho.initializeWithDefaults(this);

        bkDb = new SQLDatabaseQueue(this.getDatabasePath("bkskup3"));
        SchemaUpdater schemaUpdater = new SchemaUpdater(bkDb);

        try {
            schemaUpdater.update(this.getResources().openRawResource(R.raw.schema1), 1);
        } catch (IOException e) {
            Exceptions.propagate(e);
        }
        bkStore = new BkStore(bkDb);

        startService(Intents.makeExplicit(this.getBaseContext(),new Intent(BarcodeService.class.getName())));
        startService(Intents.makeExplicit(this.getBaseContext(),new Intent(PrintService.class.getName())));
        startService(new Intent(this, DocumentLibraryService.class));

        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));

        Job hentsSyncJob = dispatcher.newJobBuilder()
                // the JobService that will be called
                .setService(HentsSyncService.class)
                // uniquely identifies the job
                .setTag("hent-sync-job")
                // one-off job
                .setRecurring(true)
                // don't persist past a device reboot
                .setLifetime(Lifetime.FOREVER)
                // start between 0 and 60 seconds from now
                .setTrigger(Trigger.executionWindow(FETCH_HENTS_UPDATES_INTERVAL_MINUTES *60, FETCH_HENTS_UPDATES_INTERVAL_MINUTES *60 + 10))
                // don't overwrite an existing job with the same tag
                .setReplaceCurrent(true)
                // retry with exponential backoff
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                // constraints that need to be satisfied for the job to run
                .setConstraints(
                        Constraint.ON_ANY_NETWORK
                )
                .setExtras(new Bundle())
                .build();


        Job uploadPurchaseJob = dispatcher.newJobBuilder()
                // the JobService that will be called
                .setService(PurchaseUploadService.class)
                // uniquely identifies the job
                .setTag("purchase-upload-job")
                // one-off job
                .setRecurring(true)
                // don't persist past a device reboot
                .setLifetime(Lifetime.FOREVER)
                // start between 0 and 60 seconds from now
                .setTrigger(Trigger.executionWindow(UPLOAD_PURCHASE_INTERVAL_MINUTES *60, UPLOAD_PURCHASE_INTERVAL_MINUTES *60 + 10))
                // don't overwrite an existing job with the same tag
                .setReplaceCurrent(true)
                // retry with exponential backoff
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                // constraints that need to be satisfied for the job to run
                .setConstraints(
                        Constraint.ON_ANY_NETWORK
                )
                .setExtras(new Bundle())
                .build();

        dispatcher.mustSchedule(hentsSyncJob);
        dispatcher.mustSchedule(uploadPurchaseJob);

        mObjectGraph = ObjectGraph.create(new StoreDependenciesModule(bkStore));


    }

    public void inject(Activity bkActivity) {
        mObjectGraph.inject(bkActivity);
    }
}
