package com.bk.bkskup3.print;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.*;
import android.widget.*;

import com.bk.bands.DataSource;
import com.bk.bands.serializer.DocumentBean;
import com.bk.bkskup3.R;
import com.bk.bkskup3.dao.BkStore;
import com.bk.bkskup3.feedback.ErrorMessageFragment;
import com.bk.bkskup3.feedback.ErrorToast;
import com.bk.bkskup3.library.DocumentDescription;
import com.bk.bkskup3.library.DocumentLibraryException;
import com.bk.bkskup3.library.DocumentLibraryService;
import com.bk.bkskup3.library.DocumentProfile;
import com.bk.bkskup3.model.Company;
import com.bk.bkskup3.model.Invoice;
import com.bk.bkskup3.model.PurchaseDetails;
import com.bk.bkskup3.print.documents.invoice.DetailPieceInvoiceDataSource;
import com.bk.bkskup3.print.documents.invoice.GDPRDataSource;
import com.bk.bkskup3.print.documents.invoice.WeightCompactInvoiceDataSource;
import com.bk.print.service.JobStatus;
import com.bk.bkskup3.print.adapters.PrintJobItem;
import com.bk.bkskup3.print.adapters.PrintJobListAdapter;
import com.bk.bkskup3.print.context.DocumentContext;
import com.bk.bkskup3.print.context.DocumentContextBuilder;
import com.bk.bkskup3.print.documents.*;
import com.bk.bkskup3.print.documents.foodchain.FoodChainDataSource;
import com.bk.bkskup3.print.documents.invoice.ContractDataSource;
import com.bk.bkskup3.print.documents.invoice.DetailWeightInvoiceDataSource;
import com.bk.bkskup3.print.documents.invoice.PayDueDaysContractDataSource;
import com.bk.bkskup3.print.documents.invoice.PieceCompactInvoiceDataSource;
import com.bk.bkskup3.print.documents.notification.MoveNotificationDataSource;
import com.bk.bkskup3.print.event.BackendAvailableEvent;
import com.bk.bkskup3.print.event.DoPrintProfile;
import com.bk.bkskup3.print.fragment.DocumentProfileListFragment;
import com.bk.bkskup3.print.event.DocumentSelected;
import com.bk.bkskup3.print.fragment.DocumentsListFragment;
import com.bk.bkskup3.print.widgets.PrintServiceIndicator;
import com.bk.bkskup3.tasks.DependenciesForInvoicePrint;
import com.bk.bkskup3.tasks.LoadDependenciesForInvoicePrintTask;
import com.bk.bkskup3.tasks.LoadProfilesTask;
import com.bk.bkskup3.tasks.TaskResult;
import com.bk.bkskup3.work.BusActivity;
import com.bk.bkskup3.work.choice.ChoosePrinterActivity;
import com.bk.bkskup3.work.fragment.BusyFragment;
import com.bk.btcommon.model.BluetoothAddress;
import com.bk.print.service.JobDescriptor;
import com.bk.print.service.PrintService;
import com.bk.print.service.PrintServiceClient;
import com.bk.print.service.PrintServiceState;
import com.google.common.base.Strings;
import com.squareup.otto.Subscribe;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 2/24/12
 * Time: 7:58 PM
 */
public class PrintActivity extends BusActivity {
    public static final String EXTRA_INVOICE_TO_PRINT = "invoice_to_print";

    private static final String STATE_EXTRA_STATE = "state";
    private static final String STATE_EXTRA_DEPENDENCIES = "dependencies";
    private static final String STATE_EXTRA_PROFILESCACHE = "profiles_cache";
    private static final String STATE_EXTRA_SELECTEDDOC = "selected_doc";
    private static final String STATE_EXTRA_PREVSTATE = "prev_state";
    private static final String STATE_EXTRA_DOCUMENTS = "documents";
    private static final String STATE_EXTRA_PRINTJOBS = "print_jobs";

    private static final String RETAINER_FRAGMENT_TAG = "retainer";
    private static final String ERROR_MSG_FRAGMENT_TAG = "error_fragment";
    private static final String DOCUMENTS_FRAMGENT_TAG = "documents_fragment";
    private static final String PROFILES_FRAMGENT_TAG = "profiles_fragment";
    private static final String BUSY_MSG_FRAMGENT_TAG = "busy_fragment";

    private static final int CHOOSE_PRINTER_RQ_CODE = 1001;

    enum State {
        LoadingDependencies,
        LoadingProfiles,
        ShowingError,
        Idle,
    }

    public static class Retainer extends Fragment {
        public Retainer() {
            setRetainInstance(true);
        }

        public LoadDependenciesForInvoicePrintTask loadDependenciesTask;
        public LoadProfilesTask loadProfilesTask;
    }


    @Module(injects = { FoodChainDataSource.class,
                        MoveNotificationDataSource.class,
                        DetailWeightInvoiceDataSource.class,
                        ContractDataSource.class,
                        PayDueDaysContractDataSource.class,
                        PieceCompactInvoiceDataSource.class,
                        WeightCompactInvoiceDataSource.class,
                        DetailPieceInvoiceDataSource.class,
                        GDPRDataSource.class},
            library = true)
    public class DataSourceDependenciesModule {

        @Provides
        Invoice invoice() {
            return mDependencies.getInvoice();
        }

        @Provides
        PurchaseDetails purchaseDetails() {
            return mDependencies.getPurchaseDetails();
        }

        @Provides
        Company company() {
            return mDependencies.getCompany();
        }
    }


    LoadDependenciesForInvoicePrintTask.Observer mLoadDependenciesTaskObserver = new LoadDependenciesForInvoicePrintTask.Observer() {
        @Override
        public void onLoadStarted() {
            showLoading();
        }

        @Override
        public void onLoadSuccessful(DependenciesForInvoicePrint result) {

            onDependenciesLoaded(result);
        }

        @Override
        public void onLoadError(Exception e) {
            setState(State.ShowingError);
            showErrorWithRetry(e.getMessage(), mLoadDependenciesErrorListener);
        }
    };


    LoadProfilesTask.Observer mLoadProfilesTaskObserver = new LoadProfilesTask.Observer() {
        @Override
        public void onLoadStarted() {
            showBusy(R.string.loading);
        }

        @Override
        public void onLoadSuccessful(Collection<DocumentProfile> result) {
            setState(State.Idle);
            hideBusy();
            mProfilesFragment.setProfiles(result);
            mProfileCache.put(mSelectedDocCd, new ArrayList<>(result));
            showProfilesFragment();
            invalidateAvailableDocsActionBar();
        }

        @Override
        public void onLoadError(Exception e) {
            hideBusy();
            setState(State.ShowingError);
            showErrorWithRetry(e.getMessage(), mLoadProfilesErrorListener);
        }
    };

    private ErrorMessageFragment.ErrorFragmentListener mLoadDependenciesErrorListener = new ErrorMessageFragment.ForwardErrorFragmentListener() {
        @Override
        public void onAbandon() {
            mErrMsgFragment = null;
            clearState();
            finish();
        }
    };

    private ErrorMessageFragment.ErrorFragmentListener mLoadProfilesErrorListener = new ErrorMessageFragment.ForwardErrorFragmentListener() {
        @Override
        public void onAbandon() {
            mErrMsgFragment = null;
            clearState();
        }

        @Override
        public void onRetry() {
            mErrMsgFragment = null;
            loadProfiles(mSelectedDocCd);
        }
    };

    private ErrorMessageFragment.ErrorFragmentListener mPrintErrorListener = new ErrorMessageFragment.ForwardErrorFragmentListener() {
        @Override
        public void onAbandon() {
            mErrMsgFragment = null;
            clearState();
        }

        @Override
        public void onRetry() {
        }
    };


    private PrintServiceClient.PrintObserver mObserver = new PrintServiceClient.PrintObserver() {
        @Override
        public void onPrinterAddress(BluetoothAddress address) {
            mPrinterBtAddress = address;
            resolveDeviceName();
        }

        @Override
        public void onStateChanged(PrintServiceState state) {
            mServiceState = state;
            updatePrintServiceStateIndicator();
        }

        @Override
        public void onJobSubmitted(JobDescriptor job) {
            mPrintJobsAdapter.addJob(job);
            mPrintJobsAdapter.notifyDataSetChanged();
        }

        @Override
        public void onJobStarted(UUID jobId) {

            mPrintJobsAdapter.jobStarted(jobId);
            invalidateJobsActionBar();
        }

        @Override
        public void onJobProgress(UUID jobId, int progress) {
            mPrintJobsAdapter.jobProgress(jobId, progress);
        }

        @Override
        public void onJobCompleted(UUID jobId) {

            mPrintJobsAdapter.jobCompleted(jobId);
            invalidateJobsActionBar();
        }

        @Override
        public void onJobAborted(UUID jobId) {
            mPrintJobsAdapter.jobAborted(jobId);
            invalidateJobsActionBar();
        }

        @Override
        public void onJobError(UUID jobId, String errorCd) {
            mPrintJobsAdapter.jobError(jobId, errorCd);
            invalidateJobsActionBar();
        }

        @Override
        public void onJobsList(List<JobDescriptor> jobs) {
            mPrintJobsAdapter.addJobs(jobs);
        }
    };


    private int mInvoiceId;
    private PrintServiceClient mClient;
    private DocumentLibraryService mDocLibService;
    private State mState;
    private State mPreviousState;
    private DependenciesForInvoicePrint mDependencies;
    private LoadDependenciesForInvoicePrintTask mLoadDependenciesTask;
    private ObjectGraph mObjectGraph = ObjectGraph.create(new DataSourceDependenciesModule());
    private DocumentContext mDocContext;
    private PrintServiceState mServiceState;
    private BluetoothAddress mPrinterBtAddress;
    private String mPrinterDevName;
    private BluetoothAdapter mBtAdapter;
    private PrintServiceIndicator mPrinterConnectionStatus;
    private TextView mPrinterNameBox;
    private HashMap<String, ArrayList<DocumentProfile>> mProfileCache;
    private ArrayList<DocumentDescription> mDocuments;

    private DocumentsListFragment mDocumentsListFragment;
    private DocumentProfileListFragment mProfilesFragment;
    private BusyFragment mBusyFragment;

    PrintJobListAdapter mPrintJobsAdapter;

    @Inject
    BkStore mStore;

    private ServiceConnection mPrintServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {

            mClient = new PrintServiceClient(service);
            mClient.register();
            mClient.attachObserver(mObserver);
            mClient.getPrinterAddress();
            bindToDocumentLibraryService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mClient = null;
        }
    };

    private ServiceConnection mDocumentLibServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {

            DocumentLibraryService.LocalBinder binder = (DocumentLibraryService.LocalBinder) service;
            mDocLibService = binder.getService();
            continueResume();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mDocLibService = null;
        }
    };

    private View mProgressContainer;
    private View mContentContainer;
    private com.bk.widgets.actionbar.ActionBar mAvailableDocsActionBar;
    private ListView mJobsListView;
    private com.bk.widgets.actionbar.ActionBar mJobsActionBar;

    protected ErrorMessageFragment mErrMsgFragment;
    private LoadProfilesTask mLoadProfilesTask;
    private String mSelectedDocCd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.print_activity);


        mProgressContainer = findViewById(R.id.progress_container);
        mContentContainer = findViewById(R.id.content_container);
        mAvailableDocsActionBar = (com.bk.widgets.actionbar.ActionBar) findViewById(R.id.documentsActionBar);
        mJobsListView = (ListView) findViewById(R.id.jobs_list);
        mJobsActionBar = findViewById(R.id.jobsActionBar);


        mPrintJobsAdapter = new PrintJobListAdapter(this);

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        Intent intent = getIntent();
        mInvoiceId = intent.getIntExtra(EXTRA_INVOICE_TO_PRINT, 0);

        FragmentManager fm = getFragmentManager();
        Retainer retainer = (Retainer) fm.findFragmentByTag(RETAINER_FRAGMENT_TAG);
        if (retainer != null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(retainer);
            ft.commit();

            mLoadProfilesTask = retainer.loadProfilesTask;
            mLoadDependenciesTask = retainer.loadDependenciesTask;
        }

        if (savedInstanceState != null) {
            mState = (State) savedInstanceState.getSerializable(STATE_EXTRA_STATE);
            mPreviousState = (State) savedInstanceState.getSerializable(STATE_EXTRA_PREVSTATE);
            mDependencies = (DependenciesForInvoicePrint) savedInstanceState.getSerializable(STATE_EXTRA_DEPENDENCIES);
            mProfileCache = (HashMap<String, ArrayList<DocumentProfile>>) savedInstanceState.getSerializable(STATE_EXTRA_PROFILESCACHE);
            mSelectedDocCd = savedInstanceState.getString(STATE_EXTRA_SELECTEDDOC);
            mDocuments = (ArrayList<DocumentDescription>) savedInstanceState.getSerializable(STATE_EXTRA_DOCUMENTS);

            PrintJobItem[] printJobItems = (PrintJobItem[]) savedInstanceState.getSerializable(STATE_EXTRA_PRINTJOBS);
            if (printJobItems != null) {
                mPrintJobsAdapter.addAll(printJobItems);
            }

            mDocContext = new DocumentContextBuilder(getResources(), mDependencies).build();
        }


        mDocumentsListFragment = (DocumentsListFragment) fm.findFragmentByTag(DOCUMENTS_FRAMGENT_TAG);
        mProfilesFragment = (DocumentProfileListFragment) fm.findFragmentByTag(PROFILES_FRAMGENT_TAG);

        if (mDocumentsListFragment == null) {
            mDocumentsListFragment = (DocumentsListFragment) Fragment.instantiate(this, DocumentsListFragment.class.getName(), null);
            mDocumentsListFragment.setDocuments(mDocuments);
            if (mSelectedDocCd == null) {
                fm.beginTransaction().add(R.id.docs_panel, mDocumentsListFragment, DOCUMENTS_FRAMGENT_TAG).commit();
            }
        }

        if (mProfilesFragment == null) {
            mProfilesFragment = (DocumentProfileListFragment) Fragment.instantiate(this, DocumentProfileListFragment.class.getName(), null);
        }

        mBusyFragment = (BusyFragment) fm.findFragmentByTag(BUSY_MSG_FRAMGENT_TAG);

        if (mProfileCache == null) {
            mProfileCache = new HashMap<>();
        }

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.printerbox);
        View customView = actionBar.getCustomView();

        ImageButton changePrinterBtn = (ImageButton) customView.findViewById(R.id.changePrinterBtn);
        changePrinterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChangePrinter();
            }
        });
        mPrinterConnectionStatus = (PrintServiceIndicator) customView.findViewById(R.id.printerConnectionStatus);
        mPrinterNameBox = (TextView) customView.findViewById(R.id.printerNameBox);

        mJobsListView.setAdapter(mPrintJobsAdapter);

        mJobsListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mJobsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                invalidateJobsActionBar();
            }
        });

    }


    private void clearState() {
        setState(State.Idle);
    }

    public void setState(State state) {
        this.mPreviousState = mState;
        this.mState = state;
    }


    private void hideBusy() {
        if (mBusyFragment != null) {
            mBusyFragment.dismiss();
            mBusyFragment = null;
        }
    }

    private void showBusy(int busyTextResId) {
        FragmentManager fm = getFragmentManager();

        if (mBusyFragment == null) {
            mBusyFragment = BusyFragment.newInstance(getString(busyTextResId));
            mBusyFragment.setCancelable(false);
            mBusyFragment.show(fm, BUSY_MSG_FRAMGENT_TAG);
        }
    }

    private void showDocumentsFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
        ft.replace(R.id.docs_panel, mDocumentsListFragment, DOCUMENTS_FRAMGENT_TAG);
        ft.commit();

    }

    private void showProfilesFragment() {

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
        ft.replace(R.id.docs_panel, mProfilesFragment, PROFILES_FRAMGENT_TAG);
        ft.commit();
    }

    private void invalidateJobsActionBar() {
        mJobsActionBar.removeAllActions();
        if (mJobsListView.getCheckedItemCount() > 0) {
            int checkedItemPos = mJobsListView.getCheckedItemPosition();
            final PrintJobItem job = mPrintJobsAdapter.getItem(checkedItemPos);
            if (job.state == JobStatus.Printing || job.state == JobStatus.Printing) {
                mJobsActionBar.addAction(new com.bk.widgets.actionbar.ActionBar.ImageAction(R.drawable.ic_action_cancel, () -> abortPrintJob(job.jobId)));
            }
        }
    }

    private void abortPrintJob(UUID jobId) {
        mClient.abortJob(jobId);
    }

    private void invalidateAvailableDocsActionBar() {
        mAvailableDocsActionBar.removeAllActions();
        if (mSelectedDocCd != null) {
            mAvailableDocsActionBar.addAction(new com.bk.widgets.actionbar.ActionBar.ImageAction(R.drawable.ic_action_backspace, () -> {
                mSelectedDocCd = null;
                showDocumentsFragment();
                invalidateAvailableDocsActionBar();
            }));
        }
    }

    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        state.putSerializable(STATE_EXTRA_PRINTJOBS, mPrintJobsAdapter.toArray());
        state.putSerializable(STATE_EXTRA_STATE, mState);
        state.putSerializable(STATE_EXTRA_PREVSTATE, mPreviousState);
        state.putSerializable(STATE_EXTRA_DEPENDENCIES, mDependencies);
        state.putSerializable(STATE_EXTRA_PROFILESCACHE, mProfileCache);
        state.putString(STATE_EXTRA_SELECTEDDOC, mSelectedDocCd);
        state.putSerializable(STATE_EXTRA_DOCUMENTS, mDocuments);
    }

    private void bindToDocumentLibraryService() {
        bindService(new Intent(this, DocumentLibraryService.class), mDocumentLibServiceConnection, Context.BIND_AUTO_CREATE);
    }

    protected void onResume() {
        super.onResume();

        if (mClient == null) {
            showLoading();

            bindService(new Intent(this, PrintService.class), mPrintServiceConnection, Context.BIND_AUTO_CREATE);
        } else {
            if (mDocLibService == null) {
                showLoading();
                bindToDocumentLibraryService();
            } else {
                continueResume();
            }
        }
    }

    protected void continueResume() {

        if (mState == null) {
            showLoading();
            loadDependencies();
            return;
        }

        invalidateAvailableDocsActionBar();
        invalidateJobsActionBar();
        refreshJobs();


        if (mState == State.Idle) {
            showContent();
            return;
        }

        if (mState == State.LoadingDependencies) {
            if (mLoadDependenciesTask != null) {
                if (mLoadDependenciesTask.getStatus() == AsyncTask.Status.FINISHED) {
                    TaskResult<DependenciesForInvoicePrint> result = mLoadDependenciesTask.getResult();
                    if (result.isError()) {
                        setState(State.ShowingError);
                        showErrorWithRetry(result.getException().getMessage(), mLoadDependenciesErrorListener);
                    } else {
                        mLoadDependenciesTaskObserver.onLoadSuccessful(result.getResult());
                    }
                } else {
                    showLoading();
                    mLoadDependenciesTask.attachObserver(mLoadDependenciesTaskObserver);
                }
            } else {
                loadDependencies();
            }
            return;
        }

        if (mState == State.LoadingProfiles) {
            showContent();
            if (mLoadProfilesTask != null) {
                if (mLoadProfilesTask.getStatus() == AsyncTask.Status.FINISHED) {

                    TaskResult<Collection<DocumentProfile>> result = mLoadProfilesTask.getResult();
                    if (result.isError()) {
                        hideBusy();
                        setState(State.ShowingError);
                        showErrorWithRetry(result.getException().getMessage(), mLoadProfilesErrorListener);
                    } else {
                        mLoadProfilesTaskObserver.onLoadSuccessful(result.getResult());
                    }
                } else {
                    mLoadProfilesTask.attachObserver(mLoadProfilesTaskObserver);
                }
            } else {
                loadProfiles(mSelectedDocCd);
            }
            return;
        }


        if (mState == State.ShowingError) {

            switch (mPreviousState) {
                case LoadingDependencies:
                    mErrMsgFragment.setListener(mLoadDependenciesErrorListener);
                    break;
                case LoadingProfiles:
                    mErrMsgFragment.setListener(mLoadProfilesErrorListener);
                    break;
                default:
                    showContent();
                    break;

            }
        }
    }

    private void refreshJobs() {
        mClient.listJobs(mPrintJobsAdapter.getIds());
    }

    private void loadDependencies() {
        setState(State.LoadingDependencies);
        mLoadDependenciesTask = new LoadDependenciesForInvoicePrintTask(mInvoiceId, mStore);
        mLoadDependenciesTask.attachObserver(mLoadDependenciesTaskObserver);
        mLoadDependenciesTask.execute();
    }

    protected void onPause() {
        super.onPause();

        if (mLoadDependenciesTask != null)
            mLoadDependenciesTask.detachObserver();

        if (mLoadProfilesTask != null)
            mLoadProfilesTask.detachObserver();

        retainNonConfigurationInstance();

    }


    public void retainNonConfigurationInstance() {
        Retainer retainer = new Retainer();
        retainer.loadDependenciesTask = mLoadDependenciesTask;
        retainer.loadProfilesTask = mLoadProfilesTask;
        getFragmentManager().beginTransaction().add(retainer, RETAINER_FRAGMENT_TAG).commit();
    }

    private void showContent() {

        mProgressContainer.setVisibility(View.GONE);
        mContentContainer.setVisibility(View.VISIBLE);
    }


    protected void showLoading() {

        mContentContainer.setVisibility(View.GONE);
        mProgressContainer.setVisibility(View.VISIBLE);
    }

    private void onDependenciesLoaded(DependenciesForInvoicePrint dependenciesForCow) {
        mDependencies = dependenciesForCow;
        mDocuments = new ArrayList<>(mDocLibService.getDocumentsFor(mDependencies.getInvoice()));
        mDocContext = new DocumentContextBuilder(getResources(), mDependencies).build();
        setState(State.Idle);
        post(new BackendAvailableEvent());
        mClient.listJobs();
        showContent();
    }

    private void updatePrinterNameBox() {
        mPrinterNameBox.setText(mPrinterDevName);
    }

    private void resolveDeviceName() {
        if (mBtAdapter != null && mBtAdapter.isEnabled() && mPrinterBtAddress != null) {
            BluetoothDevice bluetoothDevice = mBtAdapter.getRemoteDevice(mPrinterBtAddress.getRawForm());
            mPrinterDevName = Strings.nullToEmpty(bluetoothDevice.getName());
            updatePrinterNameBox();
        }
    }

    void updatePrintServiceStateIndicator() {
        mPrinterConnectionStatus.setStatus(mServiceState);
    }

    private void onChangePrinter() {
        if (isIdle()) {
            Intent changePrinter = new Intent(this, ChoosePrinterActivity.class);
            if (mPrinterBtAddress != null) {
                changePrinter.putExtra(ChoosePrinterActivity.EXTRA_SELECTED_DEVICE_ADDRESS, mPrinterBtAddress);
            }
            startActivityForResult(changePrinter, CHOOSE_PRINTER_RQ_CODE);
        } else {
            displayError(R.string.errCantChangePrinterWhilePrinting);
        }
    }

    private void displayError(int msgId) {
        new ErrorToast(this).show(msgId);
    }

    private void previewDocument(String documentCode, DocumentProfile profile) {

        try {
            DocumentBean document = createDocument(documentCode, profile);
            Intent previewIntent = new Intent(this, PrintPreviewActivity.class);
            previewIntent.putExtra(PrintPreviewActivity.EXTRA_DOCUMENT, document);
            startActivity(previewIntent);
        } catch (DocumentLibraryException e) {
            showErrorNoRetry(e.getMessage(), mPrintErrorListener);
        }
    }

    private void showErrorNoRetry(String message, ErrorMessageFragment.ErrorFragmentListener listener) {
        showError(message, listener, false);
    }

    public DocumentLibraryService getDocLibService() {
        return mDocLibService;
    }

    public Invoice getInvoice() {
        return mDependencies.getInvoice();
    }

    public Collection<DocumentDescription> getDocuments() {
        return mDocuments;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSE_PRINTER_RQ_CODE) {
            if (resultCode == RESULT_OK) {
                onPrinterChosen(data.getParcelableExtra(ChoosePrinterActivity.EXTRA_SELECTED_DEVICE_ADDRESS));
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void onPrinterChosen(BluetoothAddress printerBtDevice) {
        mClient.setPrinterAddress(printerBtDevice);
    }

    protected void onStart() {
        super.onStart();
    }

    protected void onRestart() {
        super.onRestart();
    }

    protected void onStop() {
        super.onStop();

        if (mClient != null) {
            mClient.unregister();
            unbindService(mPrintServiceConnection);
            mClient = null;
        }

        if (mDocLibService != null) {
            unbindService(mDocumentLibServiceConnection);
            mDocLibService = null;
        }
    }


    private void showError(String message, ErrorMessageFragment.ErrorFragmentListener listener, boolean retryEnabled) {
        setState(State.ShowingError);
        mErrMsgFragment = new ErrorMessageFragment();
        mErrMsgFragment.setCaption(getResources().getString(R.string.errorCaption));
        mErrMsgFragment.setRetryEnabled(retryEnabled);
        mErrMsgFragment.setErrorMsg(message);
        mErrMsgFragment.setListener(listener);
        mErrMsgFragment.show(getFragmentManager(), ERROR_MSG_FRAGMENT_TAG);
    }

    private void showErrorWithRetry(String message, ErrorMessageFragment.ErrorFragmentListener listener) {
        showError(message, listener, true);
    }


    private boolean isIdle() {
        for (int i = 0; i < mPrintJobsAdapter.getCount(); i++) {
            JobStatus jobStatus = mPrintJobsAdapter.getItem(i).state;
            if (jobStatus == JobStatus.Waiting || jobStatus == JobStatus.Printing) {
                return false;
            }
        }
        return true;
    }

    @Subscribe
    public void onDocumentSelected(DocumentSelected event) {

        int profileCount = mDocLibService.getDocumentProfileCount(event.getDocumentCd());
        if (profileCount > 0) {
            mSelectedDocCd = event.getDocumentCd();
            List<DocumentProfile> documentProfiles = mProfileCache.get(mSelectedDocCd);
            if (documentProfiles != null) {
                mProfilesFragment.setProfiles(documentProfiles);
                showProfilesFragment();
                invalidateAvailableDocsActionBar();
            } else {
                loadProfiles(mSelectedDocCd);
            }
        } else {
            String documentCd = event.getDocumentCd();
            if (event.isLongClick()) {
                previewDocument(documentCd, null);
            } else {
                printDocument(documentCd, null);
            }
        }
    }

    @Subscribe
    public void onDoPrintProfile(DoPrintProfile event) {
        DocumentProfile profile = event.getProfile();
        String documentCd = profile.getDocumentCode();
        if (event.isLongClick()) {
            previewDocument(documentCd, profile);
        } else {
            printDocument(documentCd, profile);
        }
        mSelectedDocCd = null;
        invalidateAvailableDocsActionBar();
        showDocumentsFragment();
    }

    private void printDocument(String documentCode, DocumentProfile profile) {
        if (mServiceState != PrintServiceState.PrinterConnected) {
            displayError(R.string.errPrinterNotAvailable);
            return;
        }

        try {
            DocumentBean document = createDocument(documentCode, profile);
            mClient.submitJob(document);
        } catch (DocumentLibraryException e) {
            showErrorNoRetry(e.getMessage(), mPrintErrorListener);
        }
    }

    private DocumentBean createDocument(String documentCode, DocumentProfile profile) throws DocumentLibraryException {
        DocumentArchetype documentArchetype = mDocLibService.createArchetype(documentCode, mDocContext, profile);
        DataSource dataSource = documentArchetype.getDataSource();
        mObjectGraph.inject(dataSource);
        return documentArchetype.createDocument();
    }

    private void loadProfiles(String documentCode) {
        setState(State.LoadingProfiles);
        mLoadProfilesTask = new LoadProfilesTask(mDocLibService, documentCode);
        mLoadProfilesTask.attachObserver(mLoadProfilesTaskObserver);
        mLoadProfilesTask.execute();
    }
}
