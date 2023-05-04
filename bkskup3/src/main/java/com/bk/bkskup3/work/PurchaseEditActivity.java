package com.bk.bkskup3.work;

import android.app.*;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.bk.bkskup3.R;
import com.bk.bkskup3.dao.PurchasesStore;
import com.bk.bkskup3.feedback.ErrorMessageFragment;
import com.bk.bkskup3.model.InvoiceObj;
import com.bk.bkskup3.model.PurchaseObj;
import com.bk.bkskup3.print.PrintActivity;
import com.bk.bkskup3.work.fragment.*;
import com.bk.bkskup3.tasks.LoadPurchaseTask;
import com.bk.bkskup3.tasks.TaskResult;
import com.bk.bkskup3.work.fragment.event.*;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/12/2014
 * Time: 12:03 PM
 */
public class PurchaseEditActivity extends BusActivity {

    private static final int REQUEST_CODE_NEWINVOICE = 101;
    private static final int REQUEST_CODE_EDITINVOICE = 102;

    public static final String EXTRA_PURCHASE_ID = "purchase_id";

    private static final String STATE_EXTRA_STATE = "state";
    private static final String STATE_EXTRA_SELECTEDTAB = "selected_tab";

    private static final String LOADING_FRAGMENT_TAG = "loading";
    private static final String SUMMARY_FRAGMENT_TAB_TAG = "summary";
    private static final String INVOICES_FRAGMENT_TAB_TAG = "invoices";
    private static final String ERROR_MSG_FRAGMENT_TAG = "error_fragment";
    private static final String RETAINER_FRAGMENT_TAG = "retainer";

    protected Fragment mLoadingFragment;
    protected PurchaseObj mPurchase;
    private LoadPurchaseTask mLoadTask;
    private State mState;
    protected ErrorMessageFragment mErrMsgFragment;
    private PurchaseEditInvoicesFragment mInvoicesFragment;
    private PurchaseEditSummaryFragment mSummaryFragment;
    private int mIntentPurchaseId;
    private int mSelectedTab;

    @Inject
    PurchasesStore mPurchasesStore;


    enum State {
        LoadingPurchase,
        Idle,
        ShowingError
    }

    public static class Retainer extends Fragment {
        public Retainer() {
            setRetainInstance(true);
        }

        public LoadPurchaseTask loadPurchaseTask;
        public PurchaseObj purchaseObj;
    }

    LoadPurchaseTask.Observer mObserver = new LoadPurchaseTask.Observer() {
        @Override
        public void onLoadStarted() {

        }

        @Override
        public void onLoadSuccessful(PurchaseObj result) {
            onPurchasesLoadCompleted();
        }

        @Override
        public void onLoadError(Exception e) {
            showError(e.getMessage());
        }
    };

    private ErrorMessageFragment.ErrorFragmentListener mOnErrorHideListener = new ErrorMessageFragment.ForwardErrorFragmentListener() {
        @Override
        public void onAbandon() {
            clearState();
            mErrMsgFragment = null;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getFragmentManager();

        mInvoicesFragment = (PurchaseEditInvoicesFragment) fm.findFragmentByTag(INVOICES_FRAGMENT_TAB_TAG);
        mSummaryFragment = (PurchaseEditSummaryFragment) fm.findFragmentByTag(SUMMARY_FRAGMENT_TAB_TAG);

        if (mInvoicesFragment == null)
            mInvoicesFragment = (PurchaseEditInvoicesFragment) Fragment.instantiate(this, PurchaseEditInvoicesFragment.class.getName(), null);

        if (mSummaryFragment == null)
            mSummaryFragment = (PurchaseEditSummaryFragment) Fragment.instantiate(this, PurchaseEditSummaryFragment.class.getName(), null);

        mIntentPurchaseId = getIntent().getIntExtra(EXTRA_PURCHASE_ID, 0);

        Retainer retainer = (Retainer) fm.findFragmentByTag(RETAINER_FRAGMENT_TAG);
        if (retainer != null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(retainer);
            ft.commit();

            mLoadTask = retainer.loadPurchaseTask;
            mPurchase = retainer.purchaseObj;
        }

        if (savedInstanceState != null) {
            mState = (State) savedInstanceState.getSerializable(STATE_EXTRA_STATE);
            mSelectedTab = savedInstanceState.getInt(STATE_EXTRA_SELECTEDTAB, 0);
        }

        if (mState == State.Idle && mPurchase != null) {
            injectFragments();
            updateActivityTitle();
            showTabs();
        }
    }

    private void updateActivityTitle() {
        if (mPurchase != null) {
            setTitle(getString(R.string.viewing_purchase, mPurchase.getHerdNo()));
        }
    }

    private void clearState() {
        mState = State.Idle;
    }

    public void setState(State state) {
        this.mState = state;
    }

    private void onPurchasesLoadCompleted() {
        setState(State.Idle);
        hideBusyIndicator();
        TaskResult<PurchaseObj> result = mLoadTask.getResult();
        mPurchase = result.getResult();
        mLoadTask = null;
        injectFragments();
        showTabs();
        post(new PurchaseLoaded(mPurchase));
        invalidateOptionsMenu();
        updateActivityTitle();
    }

    private void injectFragments() {
        mInvoicesFragment.setPurchase(mPurchase);
        mSummaryFragment.setPurchase(mPurchase);
    }

    protected void showTabs() {
        ActionBar bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

        ActionBar.Tab invoiceTab = bar.newTab()
                .setText(R.string.invoicesTabCaption)
                .setTabListener(new PurchaseTabListener<PurchaseEditInvoicesFragment>(INVOICES_FRAGMENT_TAB_TAG, mInvoicesFragment));
        bar.addTab(invoiceTab, true);

        ActionBar.Tab summaryTab = bar.newTab()
                .setText(R.string.summaryTabCaption)
                .setTabListener(new PurchaseTabListener<PurchaseEditSummaryFragment>(SUMMARY_FRAGMENT_TAB_TAG, mSummaryFragment));
        bar.addTab(summaryTab);

//        bar.setSelectedNavigationItem(0);
//        if (savedInstanceState != null)
//        {
//            bar.setSelectedNavigationItem(savedInstanceState.getInt(SAVE_STATE_SELECTED_TAB, 0));
//        }
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putSerializable(STATE_EXTRA_STATE, mState);
        state.putInt(STATE_EXTRA_SELECTEDTAB, mSelectedTab);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mLoadTask != null)
            mLoadTask.detachObserver();
        if (mErrMsgFragment != null)
            mErrMsgFragment.setListener(null);

        retainNonConfigurationInstance();

        mSelectedTab = getActionBar().getSelectedNavigationIndex();
    }

    public void retainNonConfigurationInstance() {
        Retainer retainer = new Retainer();
        retainer.loadPurchaseTask = mLoadTask;
        retainer.purchaseObj = mPurchase;
        getFragmentManager().beginTransaction().add(retainer, RETAINER_FRAGMENT_TAG).commit();
    }

    private void startPurchasesLoad() {
        setState(State.LoadingPurchase);
        showBusyIndicator();
        mLoadTask = new LoadPurchaseTask(mPurchasesStore, mIntentPurchaseId);
        mLoadTask.attachObserver(mObserver);
        mLoadTask.execute();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mState == null) {
            startPurchasesLoad();
            return;
        }

        if (mState == State.Idle) {
            if (mPurchase == null) {
                startPurchasesLoad();
            } else {
                getActionBar().setSelectedNavigationItem(mSelectedTab);
            }
            return;
        }

        if (mState == State.LoadingPurchase)

        {
            if (mLoadTask != null) {
                if (mLoadTask.getStatus() == AsyncTask.Status.FINISHED) {
                    TaskResult<PurchaseObj> result = mLoadTask.getResult();
                    if (result.isError()) {
                        showError(result.getException().getMessage());
                    } else {
                        mPurchase = result.getResult();
                        injectFragments();
                        showTabs();
                        setState(State.Idle);
                    }
                } else {
                    mLoadTask.attachObserver(mObserver);
                    showBusyIndicator();
                }

            } else {
                startPurchasesLoad();
            }
            return;
        }

        if (mState == State.ShowingError)

        {
            mErrMsgFragment = (ErrorMessageFragment) getFragmentManager().findFragmentByTag(ERROR_MSG_FRAGMENT_TAG);
            mErrMsgFragment.setListener(mOnErrorHideListener);
            return;
        }
    }


    protected void showBusyIndicator() {

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (mLoadingFragment == null) {
            mLoadingFragment = Fragment.instantiate(this, LoadingFragment.class.getName(), null);
            ft.add(android.R.id.content, mLoadingFragment, LOADING_FRAGMENT_TAG);
        } else {
            ft.attach(mLoadingFragment);
        }
        ft.commit();
    }

    protected void hideBusyIndicator() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (mLoadingFragment != null) {
            ft.detach(mLoadingFragment);
            ft.commit();
        }
    }

    private void showError(String error) {
        setState(State.ShowingError);
        mErrMsgFragment = new ErrorMessageFragment();
        mErrMsgFragment.setCaption(getResources().getString(R.string.errorCaption));
        mErrMsgFragment.setErrorMsg(error);
        mErrMsgFragment.setListener(mOnErrorHideListener);
        mErrMsgFragment.show(getFragmentManager(), ERROR_MSG_FRAGMENT_TAG);
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == Activity.RESULT_CANCELED)
            return;

        int invoiceId = 0;

        switch(requestCode)
        {
            case REQUEST_CODE_NEWINVOICE:
                InvoiceObj newInvoice = (InvoiceObj) data.getSerializableExtra(InvoiceActivity.EXTRA_INVOICE);
                invoiceId = newInvoice.getId();
                if(mPurchase != null) {
                    mPurchase.addInvoice(newInvoice);
                    post(new InvoiceAdded(newInvoice));
                    break;
                }

            case REQUEST_CODE_EDITINVOICE:
                InvoiceObj updatedInvoice = (InvoiceObj) data.getSerializableExtra(InvoiceActivity.EXTRA_INVOICE);
                invoiceId = updatedInvoice.getId();
                if(mPurchase != null) {
                    mPurchase.removeInvoice(updatedInvoice.getId());
                    mPurchase.addInvoice(updatedInvoice);
                    post(new InvoiceUpdated(updatedInvoice));
                }

                break;
        }

        if(resultCode == InvoiceActivity.RESULT_SAVE_AND_PRINT )
        {
            startPrintActivity(invoiceId);
        }

    }


    @Subscribe
    public void onNewInvoiceMenuItemSelected(NewInvoiceMenuItemSelected event)
    {
        startNewInvoiceActivity();
    }

    @Subscribe
    public void onPrintInvoiceMenuItemSelected(PrintInvoiceMenuItemSelected event)
    {
        startPrintActivity(event.getInvoiceId());
    }

    @Subscribe
    public void onEditInvoiceMenuItemSelected(EditInvoiceMenuItemSelected event)
    {
        startEditInvoiceActivity(event.getInvoiceId());
    }


    private void startEditInvoiceActivity(int invoiceId)
    {
        Intent intent = new Intent(this, EditInvoiceActivity.class);
        intent.putExtra(EditInvoiceActivity.EXTRA_PURCHASE_ID,mIntentPurchaseId);
        intent.putExtra(EditInvoiceActivity.EXTRA_INVOICE_ID,invoiceId);
        startActivityForResult(intent, REQUEST_CODE_EDITINVOICE);

    }

    private void startNewInvoiceActivity() {
        Intent intent = new Intent(this, NewInvoiceActivity.class);
        intent.putExtra(NewInvoiceActivity.EXTRA_PURCHASE_ID, mPurchase.getId());
        startActivityForResult(intent, REQUEST_CODE_NEWINVOICE);
    }

    private void startPrintActivity(int invoiceId)
    {
        Intent intent = new Intent(this, PrintActivity.class);
        intent.putExtra(PrintActivity.EXTRA_INVOICE_TO_PRINT,invoiceId);
        startActivity(intent);
    }

    private class PurchaseTabListener<T extends PurchaseFragment> implements ActionBar.TabListener {
        private final String mTag;
        private PurchaseFragment mFragment;

        public PurchaseTabListener(String tag, PurchaseFragment fragment) {
            mTag = tag;
            mFragment = fragment;

        }

        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            ft.replace(android.R.id.content, mFragment, mTag);
        }

        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        }

        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        }
    }


}
