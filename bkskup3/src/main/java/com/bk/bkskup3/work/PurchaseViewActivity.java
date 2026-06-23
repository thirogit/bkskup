package com.bk.bkskup3.work;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

import com.bk.bkskup3.R;
import com.bk.bkskup3.dao.PurchasesStore;
import com.bk.bkskup3.feedback.ErrorMessageFragment;
import com.bk.bkskup3.model.PurchaseObj;
import com.bk.bkskup3.tasks.LoadPurchaseTask;
import com.bk.bkskup3.tasks.TaskResult;
import com.bk.bkskup3.work.fragment.LoadingFragment;
import com.bk.bkskup3.work.fragment.PurchaseViewInvoicesFragment;
import com.bk.bkskup3.work.fragment.PurchaseViewSummaryFragment;
import com.bk.bkskup3.work.fragment.ViewPagerAdapter;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/12/2014
 * Time: 12:03 PM
 */
public class PurchaseViewActivity extends BusActivity {

    public static final String EXTRA_PURCHASE_ID = "purchase_id";

    private static final String STATE_EXTRA_STATE = "state";
//    private static final String STATE_EXTRA_SELECTEDTAB = "selected_tab";

    private static final String SUMMARY_FRAGMENT_TAB_TAG = "summary";
    private static final String INVOICES_FRAGMENT_TAB_TAG = "invoices";
    private static final String ERROR_MSG_FRAGMENT_TAG = "error_fragment";
    private static final String RETAINER_FRAGMENT_TAG = "retainer";

    protected PurchaseObj mPurchase;
    private LoadPurchaseTask mLoadTask;
    private State mState;

    protected ErrorMessageFragment mErrMsgFragment;
    private PurchaseViewInvoicesFragment mInvoicesFragment;
    private PurchaseViewSummaryFragment mSummaryFragment;
    private int mIntentPurchaseId;
//    private int mSelectedTab;

    @Inject
    PurchasesStore mPurchasesStore;

    private View mProgressContainer;
    private View mContentContainer;
    private ViewPager mTabPager;

    private ViewPagerAdapter mTabPagerAdapter;

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

        setContentView(R.layout.purchase);

        FragmentManager fm = getFragmentManager();

        mInvoicesFragment = (PurchaseViewInvoicesFragment) fm.findFragmentByTag(INVOICES_FRAGMENT_TAB_TAG);
        mSummaryFragment = (PurchaseViewSummaryFragment) fm.findFragmentByTag(SUMMARY_FRAGMENT_TAB_TAG);

        if (mInvoicesFragment == null)
            mInvoicesFragment = (PurchaseViewInvoicesFragment) Fragment.instantiate(this, PurchaseViewInvoicesFragment.class.getName(), null);

        if (mSummaryFragment == null)
            mSummaryFragment = (PurchaseViewSummaryFragment) Fragment.instantiate(this, PurchaseViewSummaryFragment.class.getName(), null);

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
//            mSelectedTab = savedInstanceState.getInt(STATE_EXTRA_SELECTEDTAB, 0);
        }

        mContentContainer = findViewById(R.id.content_container);
        mProgressContainer = findViewById(R.id.progress_container);
        mTabPager = findViewById(R.id.pager);

        mTabPagerAdapter = new ViewPagerAdapter(fm);
        mTabPagerAdapter.addTab(getString(R.string.invoicesTabCaption), INVOICES_FRAGMENT_TAB_TAG, mInvoicesFragment);
        mTabPagerAdapter.addTab(getString(R.string.summaryTabCaption), SUMMARY_FRAGMENT_TAB_TAG, mSummaryFragment);
        mTabPager.post(new Runnable() {
            @Override
            public void run() {
                mTabPager.setAdapter(mTabPagerAdapter);
            }
        });

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
        TaskResult<PurchaseObj> result = mLoadTask.getResult();
        mPurchase = result.getResult();
        mLoadTask = null;
        injectFragments();
        showTabs();
        invalidateOptionsMenu();
        updateActivityTitle();
    }

    private void injectFragments() {
        mInvoicesFragment.setPurchase(mPurchase);
        mSummaryFragment.setPurchase(mPurchase);
    }

    protected void showLoading() {

        mContentContainer.setVisibility(View.GONE);
        mProgressContainer.setVisibility(View.VISIBLE);
    }

    protected void showTabs() {
        mProgressContainer.setVisibility(View.GONE);
        mContentContainer.setVisibility(View.VISIBLE);
//        ActionBar bar = getActionBar();
//        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//        bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
//
//        ActionBar.Tab invoiceTab = bar.newTab()
//                .setText(R.string.invoicesTabCaption)
//                .setTabListener(new PurchaseTabListener<PurchaseViewInvoicesFragment>(INVOICES_FRAGMENT_TAB_TAG, mInvoicesFragment));
//        bar.addTab(invoiceTab, true);
//
//        ActionBar.Tab summaryTab = bar.newTab()
//                .setText(R.string.summaryTabCaption)
//                .setTabListener(new PurchaseTabListener<PurchaseViewSummaryFragment>(SUMMARY_FRAGMENT_TAB_TAG, mSummaryFragment));
//        bar.addTab(summaryTab);
//
////        bar.setSelectedNavigationItem(0);
////        if (savedInstanceState != null)
////        {
////            bar.setSelectedNavigationItem(savedInstanceState.getInt(SAVE_STATE_SELECTED_TAB, 0));
////        }
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putSerializable(STATE_EXTRA_STATE, mState);
//        state.putInt(STATE_EXTRA_SELECTEDTAB, mSelectedTab);
    }



    @Override
    public void onPause() {
        super.onPause();

        if (mLoadTask != null)
            mLoadTask.detachObserver();
        if (mErrMsgFragment != null)
            mErrMsgFragment.setListener(null);

        retainNonConfigurationInstance();

//        mSelectedTab = getActionBar().getSelectedNavigationIndex();
    }

    public void retainNonConfigurationInstance() {
        Retainer retainer = new Retainer();
        retainer.loadPurchaseTask = mLoadTask;
        retainer.purchaseObj = mPurchase;
        getFragmentManager().beginTransaction().add(retainer, RETAINER_FRAGMENT_TAG).commit();
    }

    private void startPurchasesLoad() {
        setState(State.LoadingPurchase);
        showLoading();
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
            }
//            else {
//                getActionBar().setSelectedNavigationItem(mSelectedTab);
//            }
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
                    showLoading();
                    mLoadTask.attachObserver(mObserver);
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




    private void showError(String error) {
        setState(State.ShowingError);
        mErrMsgFragment = new ErrorMessageFragment();
        mErrMsgFragment.setCaption(getResources().getString(R.string.errorCaption));
        mErrMsgFragment.setErrorMsg(error);
        mErrMsgFragment.setListener(mOnErrorHideListener);
        mErrMsgFragment.show(getFragmentManager(), ERROR_MSG_FRAGMENT_TAG);
    }

//    private class PurchaseTabListener<T extends PurchaseFragment> implements ActionBar.TabListener {
//        private final String mTag;
//        private PurchaseFragment mFragment;
//
//        public PurchaseTabListener(String tag, PurchaseFragment fragment) {
//            mTag = tag;
//            mFragment = fragment;
//
//        }
//
//        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
//            ft.replace(android.R.id.content, mFragment, mTag);
//        }
//
//        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
//        }
//
//        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
//        }
//    }
//

}
