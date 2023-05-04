package com.bk.bkskup3.work;

import android.app.*;
import android.content.*;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.widget.TextView;
import com.bk.bkskup3.R;
import com.bk.bkskup3.dao.BkStore;
import com.bk.bkskup3.feedback.ErrorMessageFragment;
import com.bk.bkskup3.feedback.ErrorToast;
import com.bk.bkskup3.model.*;
import com.bk.bkskup3.settings.ConstraintsSettings;
import com.bk.bkskup3.settings.InputDefaultsSettings;
import com.bk.bkskup3.settings.InvoiceSettings;
import com.bk.bkskup3.tasks.DependenciesForInvoice;
import com.bk.bkskup3.utils.MoneyRounding;
import com.bk.bkskup3.utils.NullUtils;
import com.bk.bkskup3.utils.Numbers;
import com.bk.bkskup3.work.fragment.*;
import com.bk.bkskup3.work.fragment.event.*;
import com.bk.bkskup3.work.input.CowInput;
import com.bk.bkskup3.work.input.DeductionInput;
import com.bk.bkskup3.tasks.LoadDependenciesForInvoiceTask;
import com.bk.bkskup3.tasks.InitInvoiceServiceTask;
import com.bk.bkskup3.tasks.TaskResult;
import com.bk.bkskup3.work.service.InvoiceService;
import com.google.common.base.Strings;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/18/11
 * Time: 7:03 PM
 */
public abstract class InvoiceActivity extends BusActivity {
    public static final int RESULT_SAVE_AND_PRINT = RESULT_FIRST_USER + 1;
    public static final String EXTRA_INVOICE_ID = "invoice_id";
    public static final String EXTRA_PURCHASE_ID = "purchase_id";
    public static final String EXTRA_INVOICE = "invoice";

    private static final String STATE_EXTRA_STATE = "state";
//    private static final String STATE_EXTRA_INVOICEINPUT = "invoice_input";
    private static final String STATE_EXTRA_SETTINGS = "settings";

    private static final String DOC_FRAGMENT_TAG = "doc";
    private static final String COW_LIST_FRAGMENT_TAG = "cow_list";
    private static final String INVOICEHENT_FRAGMENT_TAG = "invoice_hent";
    private static final String ERRORMSG_FRAGMENT_TAG = "error_fragment";
    private static final String RETAINER_FRAGMENT_TAG = "retainer";
    private static final String DEDUCTIONS_FRAGMENT_TAG = "invoice_deductions";

//    private static final String TAG = "InvoiceFragment";

    public static class Retainer extends Fragment {
        public Retainer() {
            setRetainInstance(true);
        }

        public LoadDependenciesForInvoiceTask loadDependenciesTask;
        public InitInvoiceServiceTask initInvoiceServiceTask;
    }


    enum State {
        LoadingDependencies,
        LoadingInvoice,
        ShowingError,
        Idle,
        Saving
    }

    private int mInvoiceId;
    private int mPurchaseId;
    private DependenciesForInvoice mDependencies;
    private State mState;
    protected ErrorMessageFragment mErrMsgFragment;
    private LoadDependenciesForInvoiceTask mLoadDependenciesTask;
    private InitInvoiceServiceTask mInitInvoiceServiceTask;

    private InvoiceEditDocFragment mDocFragment;
    private InvoiceEditCowsFragment mCowsFragment;
    private InvoiceEditHentFragment mHentFragment;
    private InvoiceEditDeductionFragment mDeductionFragment;
    private ViewPagerAdapter mTabPagerAdapter;

    private View mProgressContainer;
    private View mContentContainer;
    private ViewPager mTabPager;

    InvoiceService mService;

    @Inject
    BkStore mStore;


    private ErrorMessageFragment.ErrorFragmentListener mOnErrorHideListener = new ErrorMessageFragment.ForwardErrorFragmentListener() {
        @Override
        public void onAbandon() {
            clearState();
            mErrMsgFragment = null;
        }
    };

    LoadDependenciesForInvoiceTask.Observer mLoadSettingTaskObserver = new LoadDependenciesForInvoiceTask.Observer() {
        @Override
        public void onLoadStarted() {
            showLoading();
        }

        @Override
        public void onLoadSuccessful(DependenciesForInvoice result) {

            onSettingsLoaded(result);
        }

        @Override
        public void onLoadError(Exception e) {
            setState(State.ShowingError);
            showError(e.getMessage());
        }
    };

    InitInvoiceServiceTask.Observer mLoadInvoiceTaskObserver = new InitInvoiceServiceTask.Observer() {
        @Override
        public void onInitStarted() {
        }

        @Override
        public void onInitSuccessful() {
            onInvoiceLoaded();
        }

        @Override
        public void onInitError(Exception e) {
            setState(State.ShowingError);
            showError(e.getMessage());
        }
    };

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        setContentView(R.layout.invoice);

        FragmentManager fm = getFragmentManager();

        mDocFragment = (InvoiceEditDocFragment) fm.findFragmentByTag(DOC_FRAGMENT_TAG);
        mCowsFragment = (InvoiceEditCowsFragment) fm.findFragmentByTag(COW_LIST_FRAGMENT_TAG);
        mHentFragment = (InvoiceEditHentFragment) fm.findFragmentByTag(INVOICEHENT_FRAGMENT_TAG);
        mDeductionFragment = (InvoiceEditDeductionFragment) fm.findFragmentByTag(DEDUCTIONS_FRAGMENT_TAG);

        if (mDocFragment == null) {
            mDocFragment = (InvoiceEditDocFragment) Fragment.instantiate(this, InvoiceEditDocFragment.class.getName(), null);
        }

        if (mCowsFragment == null) {
            mCowsFragment = (InvoiceEditCowsFragment) Fragment.instantiate(this, InvoiceEditCowsFragment.class.getName(), null);
        }

        if (mHentFragment == null) {
            mHentFragment = (InvoiceEditHentFragment) Fragment.instantiate(this, InvoiceEditHentFragment.class.getName(), null);
        }

        if (mDeductionFragment == null) {
            mDeductionFragment = (InvoiceEditDeductionFragment) Fragment.instantiate(this, InvoiceEditDeductionFragment.class.getName(), null);
        }

        Retainer retainer = (Retainer) fm.findFragmentByTag(RETAINER_FRAGMENT_TAG);
        if (retainer != null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(retainer);
            ft.commit();

            mLoadDependenciesTask = retainer.loadDependenciesTask;
            mInitInvoiceServiceTask = retainer.initInvoiceServiceTask;
        }

        if (savedState != null) {
            mState = (State) savedState.getSerializable(STATE_EXTRA_STATE);
            mDependencies = (DependenciesForInvoice) savedState.getSerializable(STATE_EXTRA_SETTINGS);
            mDocFragment.setDependencies(mDependencies);
            mHentFragment.setDependencies(mDependencies);
        }

        mInvoiceId = getIntent().getIntExtra(EXTRA_INVOICE_ID, 0);
        mPurchaseId = getIntent().getIntExtra(EXTRA_PURCHASE_ID, 0);

        mContentContainer = findViewById(R.id.content_container);
        mProgressContainer = findViewById(R.id.progress_container);
        mTabPager = (ViewPager) findViewById(R.id.pager);

        mTabPagerAdapter = new ViewPagerAdapter(fm);
        mTabPagerAdapter.addTab(getString(R.string.hentTabCaption), INVOICEHENT_FRAGMENT_TAG, mHentFragment);
        mTabPagerAdapter.addTab(getString(R.string.cowsTabCaption), COW_LIST_FRAGMENT_TAG, mCowsFragment);
        mTabPagerAdapter.addTab(getString(R.string.deductionsTabCaption), DEDUCTIONS_FRAGMENT_TAG, mDeductionFragment);
        mTabPagerAdapter.addTab(getString(R.string.docTabCaption), DOC_FRAGMENT_TAG, mDocFragment);
        mTabPager.post(new Runnable() {
            @Override
            public void run() {
                mTabPager.setAdapter(mTabPagerAdapter);
            }
        });



    }

    private void updateSummary() {
        updateSummaryNumbers();
        updateInvoiceType();
        updateSummaryVatRate();
    }

    private void onSettingsLoaded(DependenciesForInvoice settingsForInvoice) {
        mDependencies = settingsForInvoice;
        mLoadDependenciesTask = null;
        mDocFragment.setDependencies(mDependencies);
        mHentFragment.setDependencies(mDependencies);
        loadInvoice();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mService != null) {
            unbindService(mConnection);
            mService = null;
            injectFragments();
        }
    }

    private void loadInvoice() {

        if (mInvoiceId != 0) {
            setState(State.LoadingInvoice);
            mInitInvoiceServiceTask = new InitInvoiceServiceTask(mInvoiceId, mService);
            mInitInvoiceServiceTask.attachObserver(mLoadInvoiceTaskObserver);
            mInitInvoiceServiceTask.execute();
        } else {
            mService.newInvoice(mPurchaseId);

            for (DeductionDefinition definition : mDependencies.getDeductionDefinitions()) {
                mService.addDeduction(definition);
            }
            onInvoiceLoaded();
        }
    }


    private void onInvoiceLoaded() {
        setState(State.Idle);
        showTabs();
        updateSummary();
    }

    private void showTabs() {
        mProgressContainer.setVisibility(View.GONE);
        mContentContainer.setVisibility(View.VISIBLE);

//        mTabPager.post(new Runnable() {
//            @Override
//            public void run() {
//                getFragmentManager().invalidateOptionsMenu();
//            }
//        });

    }


    protected void showLoading() {

        mContentContainer.setVisibility(View.GONE);
        mProgressContainer.setVisibility(View.VISIBLE);
    }


    private void clearState() {
        mState = State.Idle;
    }

    public void setState(State state) {
        this.mState = state;
    }

    @Override
    public void onResume() {
//        Log.d(TAG,"InvoiceActivity - OnResume");
        super.onResume();

        showWaitingForInvoiceBound();

        if(mService == null) {
            bindService(new Intent(this, InvoiceService.class), mConnection, Context.BIND_AUTO_CREATE);
        }
        else {
            continueResume();
        }

    }

    private void showWaitingForInvoiceBound() {
        showLoading();
    }

    private void continueResume() {
        if (mState == null) {
            loadSettings();
            return;
        }

        if (mState == State.LoadingDependencies) {
            if (mLoadDependenciesTask != null) {

                if (mLoadDependenciesTask.getStatus() == AsyncTask.Status.FINISHED) {
                    TaskResult<DependenciesForInvoice> result = mLoadDependenciesTask.getResult();
                    if (result.isError()) {
                        setState(State.ShowingError);
                        showError(result.getException().getMessage());
                    } else {
                        mLoadSettingTaskObserver.onLoadSuccessful(result.getResult());
                    }
                } else {
                    showLoading();
                    mLoadDependenciesTask.attachObserver(mLoadSettingTaskObserver);
                }
            } else {
                loadSettings();
            }
            return;
        }

        if (mState == State.LoadingInvoice) {
            if (mInitInvoiceServiceTask != null) {

                if (mInitInvoiceServiceTask.getStatus() == AsyncTask.Status.FINISHED) {
                    TaskResult<Void> result = mInitInvoiceServiceTask.getResult();
                    if (result.isError()) {
                        setState(State.ShowingError);
                        showError(result.getException().getMessage());
                    } else {
                        mLoadInvoiceTaskObserver.onInitSuccessful();
                    }
                } else {
                    showLoading();
                    mInitInvoiceServiceTask.attachObserver(mLoadInvoiceTaskObserver);
                }
            } else {
                loadInvoice();
            }
            return;
        }

        if (mState == State.Idle) {
            showTabs();
            updateSummary();
        }

        if (mState == State.ShowingError) {
            mErrMsgFragment = (ErrorMessageFragment) getFragmentManager().findFragmentByTag(ERRORMSG_FRAGMENT_TAG);
            mErrMsgFragment.setListener(mOnErrorHideListener);
            return;
        }
    }

    private void invokeSave() {
        mDocFragment.save();
    }


    @Override
    public void onPause() {
        super.onPause();
//        Log.d(TAG,"InvoiceActivity - OnPause");

        if (mLoadDependenciesTask != null)
            mLoadDependenciesTask.detachObserver();

        if (mInitInvoiceServiceTask != null)
            mInitInvoiceServiceTask.detachObserver();

        if (mErrMsgFragment != null)
            mErrMsgFragment.setListener(null);

        retainNonConfigurationInstance();
    }

    public void retainNonConfigurationInstance() {
        Retainer retainer = new Retainer();
        retainer.loadDependenciesTask = mLoadDependenciesTask;
        retainer.initInvoiceServiceTask = mInitInvoiceServiceTask;
        getFragmentManager().beginTransaction().add(retainer, RETAINER_FRAGMENT_TAG).commit();
    }

    private void loadSettings() {
        setState(State.LoadingDependencies);
        mLoadDependenciesTask = new LoadDependenciesForInvoiceTask(mStore,mPurchaseId);
        mLoadDependenciesTask.attachObserver(mLoadSettingTaskObserver);
        mLoadDependenciesTask.execute();
    }

    @Subscribe
    public void onInvoiceTypeChanged(InvoiceTypeChanged event) {
        updateInvoiceType();
        setDefaultPayWay();
        setDefaultExtras();
    }

    private void setDefaultExtras() {
        if(!mService.IsUpdating())
        {

            InvoiceType type = mService.getInvoiceType();
            if(type != null) {
                InvoiceSettings invoiceSettings = mDependencies.getInvoiceSettings();
                switch (type) {
                    case LUMP:
                        mService.setExtras(invoiceSettings.getLumpExtrasTemplate());
                        break;
                    case REGULAR:
                        mService.setExtras(invoiceSettings.getRegularExtrasTemplate());
                        break;
                }
            }
        }
    }

    private void setDefaultPayWay() {
        InvoiceType type = mService.getInvoiceType();
        PayWay payWay = mService.getPayWay();

        if(payWay == null && type != null)
        {
            InputDefaultsSettings inputDefaults = mDependencies.getInputDefaultsSettings();
            switch (type)
            {
                case LUMP:
                    mService.setPayWay(inputDefaults.getDefaultPayWayForIndividual());
                    break;
                case REGULAR:
                    mService.setPayWay(inputDefaults.getDefaultPayWayForCompany());
                    break;
            }

            if(mService.getPayDueDays() == null && payWay == PayWay.TRANSFER) {
                switch (type) {
                    case LUMP:
                        mService.setPayDueDays(inputDefaults.getDefaultPayDueDaysForIndividual());
                        break;
                    case REGULAR:
                        mService.setPayDueDays(inputDefaults.getDefaultPayDueDaysForCompany());
                        break;
                }
            }
        }
    }


    private void updateInvoiceType() {
        TextView summaryInvoiceType = (TextView) findViewById(R.id.summaryInvoiceType);
        TextView summaryInvoiceTypeTitle = (TextView) findViewById(R.id.summaryInvoiceTypeTitle);

        InvoiceType invoiceType = mService.getInvoiceType();

            Resources r = getResources();

            String invoiceTypeStr = "-";
            int color = r.getColor(android.R.color.white);

            if (invoiceType == InvoiceType.LUMP) {
                color = r.getColor(R.color.lumpInvoiceColor);
                invoiceTypeStr = r.getString(R.string.lumpInvoiceIndicator);
            } else if(invoiceType == InvoiceType.REGULAR) {
                color = r.getColor(R.color.regularInvoiceColor);
                invoiceTypeStr = r.getString(R.string.regularInvoiceIndicator);
            }

            summaryInvoiceType.setText(invoiceTypeStr);
            summaryInvoiceType.setBackgroundColor(color);
            summaryInvoiceTypeTitle.setBackgroundColor(color);
    }

    @Subscribe
    public void onVatRateChanged(InvoiceVatRateChanged event) {
        updateSummaryVatRate();
        updateSummaryTotals();
    }

    private void updateSummaryVatRate() {
        TextView summaryVatRate = (TextView) findViewById(R.id.summaryVatRate);
        Double vatRate = mService.getVatRate();
        summaryVatRate.setText(Numbers.formatVatRate(vatRate));
    }

    private void updateSummaryCowAmount() {
        TextView summaryCowAmount = (TextView) findViewById(R.id.summaryCowAmount);
        summaryCowAmount.setText(String.valueOf(mService.getCowCount()));
    }

    private void updateSummaryTotals() {
        TextView summaryGrossTotal = (TextView) findViewById(R.id.summaryGrossTotal);
        TextView summaryNetTotal = (TextView) findViewById(R.id.summaryNetTotal);

        double netTotal = 0.0;
        for (CowInput cow : mService.getCows()) {
            netTotal += cow.getNetPrice();
        }

        double deductedAmount = 0.0;
        for(DeductionInput deduction : mService.getDeductions())
        {
            if(deduction.isEnabled()) {
                deductedAmount += MoneyRounding.roundToInteger((netTotal*deduction.getFraction()));
            }
        }

        //double netTotalAfterDeductions = netTotal - deductedAmount;

        double taxValue = MoneyRounding.round(netTotal * NullUtils.valueForNull(mService.getVatRate(), 0.0));
        double grossTotal = netTotal + taxValue;
        double grossTotalAfterDeductions = (grossTotal - deductedAmount);

        summaryGrossTotal.setText(Numbers.formatPrice(grossTotal) + "/" + Numbers.formatPrice(grossTotalAfterDeductions));
        summaryNetTotal.setText(Numbers.formatPrice(netTotal));
    }

    private void updateSummaryNumbers() {
        updateSummaryCowAmount();
        updateSummaryTotals();
    }

    @Subscribe
    public void onCowAdded(CowAddedEvent event) {
        updateSummaryNumbers();
    }

    @Subscribe
    public void onCowDeleted(CowDeletedEvent event) {
        updateSummaryNumbers();
    }

    @Subscribe
    public void onCowEdited(CowEditedEvent event) {
        updateSummaryNumbers();
    }

    @Subscribe
    public void onDeductionAdded(DeductionAddedEvent event) {
        updateSummaryTotals();
    }

    @Subscribe
    public void onDeductionDeleted(DeductionDeletedEvent event) {
        updateSummaryTotals();
    }

    @Subscribe
    public void onDeductionEdited(DeductionUpdatedEvent event) {
        updateSummaryTotals();
    }

    @Subscribe
    public void onDeductionEnabled(DeductionEnabled event) {
        updateSummaryTotals();
    }

    @Subscribe
    public void onDeductionDisabled(DeductionDisabled event) {
        updateSummaryTotals();
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
//        state.putSerializable(STATE_EXTRA_INVOICEINPUT, mInput);
        state.putSerializable(STATE_EXTRA_STATE, mState);
        state.putSerializable(STATE_EXTRA_SETTINGS, mDependencies);
    }


    protected void saveAndFinish(int resultCode) {

        invokeSave();
        if (validateInputs()) {

            mService.save();
            Intent resultIntent = new Intent();
            resultIntent.putExtra(EXTRA_INVOICE, mService.getInvoice());
            setResult(resultCode, resultIntent);
            finish();
        }
    }

//    protected boolean validatePurchaseDuplicates(InvoiceObj invoice)
//    {
//        Dao dao = new Dao(this);
//        Collection<Cow> cows = invoice.getCows();
//
//        for(Cow cow : cows)
//        {
//            CowObj cowObj = dao.fetchCow(cow.getCowNo());
//            if(mInvoiceId != 0 && mInvoiceId == cowObj.getInvoice()){
//                showErrorToast(R.string.errDuplicatedCowNoInPurchase,cowObj.getCowNo().toString());
//                return false;
//            }
//        }
//        return true;
//    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menuSave:
                if (mState == State.Idle) {
                    saveAndFinish(RESULT_OK);
                }
                break;

            case R.id.menuSaveAndPrint:
                if (mState == State.Idle) {
                    saveAndFinish(RESULT_SAVE_AND_PRINT);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmAndFinish() {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    setResult(RESULT_CANCELED);
                    finish();
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.confirmFinish));
        builder.setPositiveButton(android.R.string.yes, dialogClickListener);
        builder.setNegativeButton(android.R.string.no, dialogClickListener);
        builder.show();

    }

    public void onBackPressed() {
        confirmAndFinish();
    }


//    private void showErrorToast(int errTxtResId) {
//        new ErrorToast(this).show(errTxtResId);
//    }

    private void showErrorToast(int errTxtResId,String... params) {
        new ErrorToast(this).show(getResources().getString(errTxtResId,params));
    }




    protected boolean validateInputs() {

        InvoiceHent invoiceHent = mService.getHent();

        if (invoiceHent == null) {
            showErrorToast(R.string.errMissingInvoiceHent);
            return false;
        }

        PayWay payWay = mService.getPayWay();

        if (payWay == null) {
            showErrorToast(R.string.errMissingInvoicePayWay);
            return false;
        }

        if (mService.getCowCount() == 0) {
            showErrorToast(R.string.errNoCowsInInvoice);
            return false;
        }

        if (Strings.isNullOrEmpty(mService.getCustomNumber())) {
            showErrorToast(R.string.errMissingInvoiceNo);
            return false;
        }

        if (invoiceHent.getBankAccountNo() == null && payWay == PayWay.TRANSFER) {
            showErrorToast(R.string.errHentHasNoBankAccountNo);
            return false;
        }

        InvoiceType invoiceType = mService.getInvoiceType();
        ConstraintsSettings settings = mDependencies.getConstraintsSettings();
        if (invoiceType == InvoiceType.REGULAR && NullUtils.valueForNull(settings.getRequireFiscalNoForRegular(), false)) {
            if (Strings.isNullOrEmpty(invoiceHent.getFiscalNo())) {
                showErrorToast(R.string.errHentFiscalNoIsRequiredForRegularInvoice);
                return false;
            }
        }

        if (invoiceType == InvoiceType.LUMP && NullUtils.valueForNull(settings.getRequirePersonalInfoForLump(), false)) {

            if (Strings.isNullOrEmpty(invoiceHent.getPersonalNo())) {
                showErrorToast(R.string.errHentPersonalNoIsRequiredForLumpInvoice);
                return false;
            }

            if (Strings.isNullOrEmpty(invoiceHent.getPersonalIdNo())) {
                showErrorToast(R.string.errHentPersonalIdNoIsRequiredForLumpInvoice);
                return false;
            }

            if (Strings.isNullOrEmpty(invoiceHent.getIssuePost())) {
                showErrorToast(R.string.errHentPersonalIdIssuePostIsRequiredForLumpInvoice);
                return false;
            }

            if (invoiceHent.getIssueDate() == null) {
                showErrorToast(R.string.errHentPersonalIdIssueDateIsRequiredForLumpInvoice);
                return false;
            }
        }

        return true;
    }

    private void showError(String error) {
        mErrMsgFragment = new ErrorMessageFragment();
        mErrMsgFragment.setCaption(getResources().getString(R.string.errorCaption));
        mErrMsgFragment.setErrorMsg(error);
        mErrMsgFragment.setListener(mOnErrorHideListener);
        mErrMsgFragment.show(getFragmentManager(), ERRORMSG_FRAGMENT_TAG);
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            InvoiceService.LocalBinder binder = (InvoiceService.LocalBinder) service;
            mService = binder.getService();
            injectFragments();
            post(new InvoiceServiceBoundEvent(mService));
            continueResume();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
        }
    };

    protected void injectFragments()
    {
//        Log.d(TAG,"InvoiceActivity - injecting fragments, service = " + mService);
        mDocFragment.setService(mService);
        mCowsFragment.setService(mService);
        mHentFragment.setService(mService);
        mDeductionFragment.setService(mService);
    }

    public void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED)
            return;
    }

    public InvoiceService getInvoiceService() {
        return mService;
    }
}
