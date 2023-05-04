package com.bk.bkskup3.work;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.bk.bkskup3.R;
import com.bk.bkskup3.dao.DefinitionsStore;
import com.bk.bkskup3.dao.PurchasesStore;
import com.bk.bkskup3.feedback.ErrorMessageFragment;
import com.bk.bkskup3.model.*;
import com.bk.bkskup3.tasks.LoadInvoiceTask;
import com.bk.bkskup3.tasks.TaskResult;
import com.bk.bkskup3.utils.MoneyRounding;
import com.bk.bkskup3.utils.NullUtils;
import com.bk.bkskup3.utils.Numbers;
import com.bk.bkskup3.work.fragment.*;
import com.bk.bkskup3.work.input.CowInput;
import com.bk.bkskup3.work.input.DeductionInput;
import com.bk.bkskup3.work.input.InvoiceInput;

import javax.inject.Inject;

public class InvoiceViewActivity extends BusActivity {

    public static final String EXTRA_INVOICE_ID = "invoice_id";
    public static final int RESULT_PRINT = RESULT_FIRST_USER+1;

    private static final String STATE_EXTRA_STATE = "state";
    private static final String STATE_EXTRA_INVOICEINPUT = "invoice_input";

    private static final String DOC_FRAGMENT_TAG = "doc";
    private static final String COW_LIST_FRAGMENT_TAG = "cow_list";
    private static final String INVOICEHENT_FRAGMENT_TAG = "invoice_hent";
    private static final String ERRORMSG_FRAGMENT_TAG = "error_fragment";
    private static final String RETAINER_FRAGMENT_TAG = "retainer";
    private static final String DEDUCTIONS_FRAGMENT_TAG = "invoice_deductions";

    public static class Retainer extends Fragment {
        public Retainer() {
            setRetainInstance(true);
        }

        public LoadInvoiceTask loadInvoiceTask;
    }

    enum State {
        LoadingInvoice,
        ShowingError,
        Idle,
    }

    private int mInvoiceId;
    private InvoiceInput mInput;
    private State mState;
    protected ErrorMessageFragment mErrMsgFragment;
    private LoadInvoiceTask mLoadInvoiceTask;

    private InvoiceViewDocFragment mDocFragment;
    private InvoiceViewCowsFragment mCowsFragment;
    private InvoiceViewHentFragment mHentFragment;
    private InvoiceViewDeductionFragment mDeductionFragment;
    private ViewPagerAdapter mTabPagerAdapter;

    private View mProgressContainer;
    private View mContentContainer;
    private ViewPager mTabPager;

    @Inject
    PurchasesStore mPurchasesStore;

    private ErrorMessageFragment.ErrorFragmentListener mOnErrorHideListener = new ErrorMessageFragment.ForwardErrorFragmentListener() {
        @Override
        public void onAbandon() {
            clearState();
            mErrMsgFragment = null;
        }
    };

    LoadInvoiceTask.Observer mLoadInvoiceTaskObserver = new LoadInvoiceTask.Observer() {
        @Override
        public void onLoadStarted() {
        }

        @Override
        public void onLoadSuccessful(InvoiceObj result) {
            onInvoiceLoaded(result);
        }

        @Override
        public void onLoadError(Exception e) {
            setState(State.ShowingError);
            showError(e.getMessage());
        }
    };

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        setContentView(R.layout.invoice);

        FragmentManager fm = getFragmentManager();

        mDocFragment = (InvoiceViewDocFragment) fm.findFragmentByTag(DOC_FRAGMENT_TAG);
        mCowsFragment = (InvoiceViewCowsFragment) fm.findFragmentByTag(COW_LIST_FRAGMENT_TAG);
        mHentFragment = (InvoiceViewHentFragment) fm.findFragmentByTag(INVOICEHENT_FRAGMENT_TAG);
        mDeductionFragment = (InvoiceViewDeductionFragment) fm.findFragmentByTag(DEDUCTIONS_FRAGMENT_TAG);

        if (mDocFragment == null) {
            mDocFragment = (InvoiceViewDocFragment) Fragment.instantiate(this, InvoiceViewDocFragment.class.getName(), null);
        }

        if (mCowsFragment == null) {
            mCowsFragment = (InvoiceViewCowsFragment) Fragment.instantiate(this, InvoiceViewCowsFragment.class.getName(), null);
        }

        if (mHentFragment == null) {
            mHentFragment = (InvoiceViewHentFragment) Fragment.instantiate(this, InvoiceViewHentFragment.class.getName(), null);
        }

        if (mDeductionFragment == null) {
            mDeductionFragment = (InvoiceViewDeductionFragment) Fragment.instantiate(this, InvoiceViewDeductionFragment.class.getName(), null);
        }

        Retainer retainer = (Retainer) fm.findFragmentByTag(RETAINER_FRAGMENT_TAG);
        if (retainer != null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(retainer);
            ft.commit();

            mLoadInvoiceTask = retainer.loadInvoiceTask;
        }


        if (state != null) {
            mInput = (InvoiceInput) state.getSerializable(STATE_EXTRA_INVOICEINPUT);
            mState = (State) state.getSerializable(STATE_EXTRA_STATE);
        }

        if (mState == State.Idle) {
            injectFragments();
            updateSummary();
        }

        mInvoiceId = getIntent().getIntExtra(EXTRA_INVOICE_ID, 0);

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

    private void loadInvoice() {

        setState(State.LoadingInvoice);
        mLoadInvoiceTask = new LoadInvoiceTask(mInvoiceId, mPurchasesStore);
        mLoadInvoiceTask.attachObserver(mLoadInvoiceTaskObserver);
        mLoadInvoiceTask.execute();
    }


    private void onInvoiceLoaded(Invoice invoice) {

        mInput = new InvoiceInput();
        mInput.setPurchaseId(invoice.getPurchase());
        mInput.setCustomNumber(invoice.getCustomNumber());
        mInput.setExtras(invoice.getExtras());
        mInput.setInvoiceDt(invoice.getInvoiceDt());
        mInput.setInvoiceType(invoice.getInvoiceType());
        mInput.setPayDueDays(invoice.getPayDueDays());
        mInput.setPayWay(invoice.getPayWay());
        mInput.setTransactionDt(invoice.getTransactionDt());
        mInput.setTransactionPlace(invoice.getTransactionPlace());
        mInput.setVatRate(invoice.getVatRate());
        InvoiceHent hent = invoice.getInvoiceHent();
        if (hent != null) {
            mInput.setHent(new InvoiceHentObj(hent));
        }

        for (InvoiceDeduction deduction : invoice.getDeductions()) {
            DeductionInput deductionInput = new DeductionInput();
            deductionInput.setEnabled(deduction.isEnabled());
            deductionInput.setFraction(deduction.getFraction());
            deductionInput.setReason(deduction.getReason());
            deductionInput.setCode(deduction.getCode());
            mInput.addDeduction(deductionInput);
        }

        for (Cow cow : invoice.getCows()) {
            CowInput cowInput = new CowInput();
            cowInput.setCowId(cow.getId());
            cowInput.setCowNo(cow.getCowNo());
            cowInput.setMotherNo(cow.getMotherNo());
            cowInput.setPassportIssueDt(cow.getPassportIssueDt());

            EAN firstOwnerNo = cow.getFirstOwner();
            if (firstOwnerNo != null) {
                HentObj firstOwner = new HentObj();
                firstOwner.setHentNo(firstOwnerNo);
                cowInput.setFirstOwner(firstOwner);
            }
            cowInput.setBirthPlace(cow.getBirthPlace());
            cowInput.setClassCd(cow.getClassCd());
            cowInput.setWeight(cow.getWeight());
            cowInput.setStockCd(cow.getStockCd());
            cowInput.setBirthDt(cow.getBirthDt());
            cowInput.setHealthCertNo(cow.getHealthCertNo());
            cowInput.setPassportNo(cow.getPassportNo());
            cowInput.setNetPrice(cow.getPrice());
            cowInput.setSex(cow.getSex());
            mInput.addCow(cowInput);
        }

        setState(State.Idle);
        injectFragments();
        showTabs();
        updateSummary();
    }

    private void showTabs() {
        mProgressContainer.setVisibility(View.GONE);
        mContentContainer.setVisibility(View.VISIBLE);

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
        super.onResume();

        if (mState == null) {
            loadInvoice();
            showLoading();
            return;
        }

        if (mState == State.LoadingInvoice) {
            if (mLoadInvoiceTask != null) {

                if (mLoadInvoiceTask.getStatus() == AsyncTask.Status.FINISHED) {
                    TaskResult<InvoiceObj> result = mLoadInvoiceTask.getResult();
                    if (result.isError()) {
                        setState(State.ShowingError);
                        showError(result.getException().getMessage());
                    } else {
                        mLoadInvoiceTaskObserver.onLoadSuccessful(result.getResult());
                    }
                } else {
                    showLoading();
                    mLoadInvoiceTask.attachObserver(mLoadInvoiceTaskObserver);
                }
            } else {
                loadInvoice();
            }
            return;
        }

        if (mState == State.Idle) {
            showTabs();
        }

        if (mState == State.ShowingError) {
            mErrMsgFragment = (ErrorMessageFragment) getFragmentManager().findFragmentByTag(ERRORMSG_FRAGMENT_TAG);
            mErrMsgFragment.setListener(mOnErrorHideListener);
            return;
        }
    }


    private void injectFragments() {
        mDocFragment.setInvoice(mInput);
        mCowsFragment.setInvoice(mInput);
        mHentFragment.setInvoice(mInput);
        mDeductionFragment.setInvoice(mInput);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mLoadInvoiceTask != null)
            mLoadInvoiceTask.detachObserver();

        if (mErrMsgFragment != null)
            mErrMsgFragment.setListener(null);

        retainNonConfigurationInstance();
    }

    public void retainNonConfigurationInstance() {
        Retainer retainer = new Retainer();
        retainer.loadInvoiceTask = mLoadInvoiceTask;
        getFragmentManager().beginTransaction().add(retainer, RETAINER_FRAGMENT_TAG).commit();
    }


    private void updateInvoiceType() {
        TextView summaryInvoiceType = (TextView) findViewById(R.id.summaryInvoiceType);
        TextView summaryInvoiceTypeTitle = (TextView) findViewById(R.id.summaryInvoiceTypeTitle);

        InvoiceType invoiceType = mInput.getInvoiceType();

        Resources r = getResources();

        String invoiceTypeStr = "-";
        int color = r.getColor(android.R.color.white);

        if (invoiceType == InvoiceType.LUMP) {
            color = r.getColor(R.color.lumpInvoiceColor);
            invoiceTypeStr = r.getString(R.string.lumpInvoiceIndicator);
        } else if (invoiceType == InvoiceType.REGULAR) {
            color = r.getColor(R.color.regularInvoiceColor);
            invoiceTypeStr = r.getString(R.string.regularInvoiceIndicator);
        }

        summaryInvoiceType.setText(invoiceTypeStr);
        summaryInvoiceType.setBackgroundColor(color);
        summaryInvoiceTypeTitle.setBackgroundColor(color);
    }


    private void updateSummaryVatRate() {
        TextView summaryVatRate = (TextView) findViewById(R.id.summaryVatRate);
        Double vatRate = mInput.getVatRate();
        summaryVatRate.setText(Numbers.formatVatRate(vatRate));
    }

    private void updateSummaryCowAmount() {
        TextView summaryCowAmount = (TextView) findViewById(R.id.summaryCowAmount);
        summaryCowAmount.setText(String.valueOf(mInput.getCowCount()));
    }

    private void updateSummaryTotals() {
        TextView summaryGrossTotal = (TextView) findViewById(R.id.summaryGrossTotal);
        TextView summaryNetTotal = (TextView) findViewById(R.id.summaryNetTotal);

        double netTotal = 0.0;
        for (CowInput cow : mInput.getCows()) {
            netTotal += cow.getNetPrice();
        }

        double deductedAmount = 0.0;
        for (DeductionInput deduction : mInput.getDeductions()) {
            if (deduction.isEnabled()) {
                deductedAmount += (netTotal * deduction.getFraction());
            }
        }

        //double netTotalAfterDeductions = netTotal - deductedAmount;

        double taxValue = MoneyRounding.round(netTotal * NullUtils.valueForNull(mInput.getVatRate(), 0.0));
        double grossTotal = netTotal + taxValue;
        double grossTotalAfterDeductions = (grossTotal - deductedAmount);

        summaryGrossTotal.setText(Numbers.formatPrice(grossTotal) + "/" + Numbers.formatPrice(grossTotalAfterDeductions));
        summaryNetTotal.setText(Numbers.formatPrice(netTotal));
    }

    private void updateSummaryNumbers() {
        updateSummaryCowAmount();
        updateSummaryTotals();
    }


    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
//        state.putInt(STATE_EXTRA_SELECTEDTAB, getActionBar().getSelectedNavigationIndex());
        state.putSerializable(STATE_EXTRA_INVOICEINPUT, mInput);
        state.putSerializable(STATE_EXTRA_STATE, mState);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuPrint: {
                Intent result = new Intent();
                result.putExtra(EXTRA_INVOICE_ID,mInvoiceId);
                setResult(RESULT_PRINT,result);
                finish();
                break;
            }

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.invoiceview_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }


    private void showError(String error) {
        mErrMsgFragment = new ErrorMessageFragment();
        mErrMsgFragment.setCaption(getResources().getString(R.string.errorCaption));
        mErrMsgFragment.setErrorMsg(error);
        mErrMsgFragment.setListener(mOnErrorHideListener);
        mErrMsgFragment.show(getFragmentManager(), ERRORMSG_FRAGMENT_TAG);
    }


}
