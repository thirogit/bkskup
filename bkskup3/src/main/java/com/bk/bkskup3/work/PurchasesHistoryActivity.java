package com.bk.bkskup3.work;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.animation.AnimationUtils;
import android.widget.*;

import com.bk.bkskup3.BkActivity;
import com.bk.bkskup3.R;
import com.bk.bkskup3.dao.PurchasesStore;
import com.bk.bkskup3.dao.q.QPurchase;
import com.bk.bkskup3.feedback.ErrorMessageFragment;
import com.bk.bkskup3.model.Purchase;
import com.bk.bkskup3.model.PurchaseObj;
import com.bk.bkskup3.model.PurchaseState;
import com.bk.bkskup3.tasks.LoadPurchasesTask;
import com.bk.bkskup3.tasks.TaskResult;
import com.bk.bkskup3.utils.Dates;
import com.bk.bkskup3.work.fragment.ClosePurchaseActivityFragment;
import com.google.common.base.Objects;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import static com.mysema.query.support.QueryBuilder.where;

/**
 * Created by IntelliJ IDEA.
 * User: root
 * Date: 24.06.11
 * Time: 10:01
 */
public class PurchasesHistoryActivity extends BkActivity {

    private static final int OPEN_PURACHE_REQUEST_CODE = 101;
    private static final int VIEW_PURACHE_REQUEST_CODE = 102;


    public static class MonthYear implements Serializable {
        int month;
        int year;

        public MonthYear(int year, int month) {
            this.month = month;
            this.year = year;
        }

        public int getMonth() {
            return month;
        }

        public int getYear() {
            return year;
        }

        public MonthYear plusYear() {
            if (month + 1 > 12) {
                return new MonthYear(year + 1, 1);
            }
            return new MonthYear(year, month + 1);
        }

        public MonthYear minusYear() {
            if (month - 1 < 1) {
                return new MonthYear(year - 1, 12);
            }
            return new MonthYear(year, month - 1);
        }

        @Override
        public String toString() {
            return String.format("%04d/%02d", year, month);
        }

        public Date toDate() {
            Date dt = new Date();
            dt.setMonth(month-1);
            dt.setYear(year-1900);
            dt.setDate(1);
            return dt;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MonthYear monthYear = (MonthYear) o;

            return month == monthYear.month && year == monthYear.year;

        }

        @Override
        public int hashCode() {
            return Objects.hashCode(month, year);
        }

        public static MonthYear fromDate(Date dt) {
            return new MonthYear(dt.getYear()+1900, dt.getMonth()+1);
        }
    }

    public static class PurchasesInMonth {
        public MonthYear month;
        public LoadPurchasesTask loadTask;
    }

    public static class Retainer extends Fragment {
        public Retainer() {
            setRetainInstance(true);
        }

        public Cache<MonthYear, PurchasesInMonth> purchasesInMonthCache;
    }

    private static final String TAG = PurchasesHistoryActivity.class.getSimpleName();

    private static final String CLOSE_PURCHASE_FRAGMENT_TAG = "close_purchase";
    private static final String RETAINER_FRAGMENT_TAG = "retainer";
    private static final String ERROR_MSG_FRAMGENT_TAG = "error_fragment";
    private static final String STATE_EXTRA_MONTH = "month";

    Cache<MonthYear, PurchasesInMonth> mPurchasesInMonthCache;
    MonthYear mMonth;
    LoadPurchasesTask mCurrentLoadPurchasesTask;
    Collection<PurchaseObj> mCurrentPurchaseObjs;

    private View mProgressContainer;
    private View mListContainer;
    private View mEmptyView;
    private ListView mPurchasesList;
    private ImageView mPrevMonthBtn;
    private ImageView mNextMonthBtn;
    private View mMonthSelector;
    private TextView mCurrentMonthBtn;
    DateFormatSymbols mDateSymbols = new DateFormatSymbols(Locale.getDefault());

    private int unlockPurchaseId = 0;
    private long lastTap = 0;
    private int tapCount = 0;

    @Inject
    PurchasesStore mPurchasesStore;


    private PurchasesHistoryListAdapter mPurchasesAdapter;
    private ClosePurchaseActivityFragment mClosePurchaseFragment;

    protected ErrorMessageFragment mErrMsgFragment;

    LoadPurchasesTask.Observer mLoadObserver = new LoadPurchasesTask.Observer() {
        @Override
        public void onLoadStarted() {
        }

        @Override
        public void onLoadSuccessful() {
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
            mErrMsgFragment = null;
        }
    };


    private void onPurchasesLoadCompleted() {

        TaskResult<Collection<PurchaseObj>> result = mCurrentLoadPurchasesTask.getResult();
        mCurrentLoadPurchasesTask.detachObserver();
        mCurrentPurchaseObjs = result.getResult();
        mCurrentLoadPurchasesTask = null;

        showList();
        refreshPurchasesList();
        invalidateOptionsMenu();
    }

    private void showError(String error) {
        mErrMsgFragment = new ErrorMessageFragment();
        mErrMsgFragment.setCaption(getResources().getString(R.string.errorCaption));
        mErrMsgFragment.setErrorMsg(error);
        mErrMsgFragment.setListener(mOnErrorHideListener);
        mErrMsgFragment.show(getFragmentManager(), ERROR_MSG_FRAMGENT_TAG);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "creating");
        setContentView(R.layout.purchase_history);
        ensureContent();

        FragmentManager fm = getFragmentManager();
        Retainer retainer = (Retainer) fm.findFragmentByTag(RETAINER_FRAGMENT_TAG);
        if (retainer != null) {

            Log.d(TAG, "retaining");

            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(retainer);
            ft.commit();

            mPurchasesInMonthCache = retainer.purchasesInMonthCache;
        } else {
            Log.d(TAG, "no retainer");
            mPurchasesList.clearChoices();
        }

        mClosePurchaseFragment = (ClosePurchaseActivityFragment) fm.findFragmentByTag(CLOSE_PURCHASE_FRAGMENT_TAG);
        if (mClosePurchaseFragment == null) {
            mClosePurchaseFragment = (ClosePurchaseActivityFragment) Fragment.instantiate(this, ClosePurchaseActivityFragment.class.getName());
            fm.beginTransaction().add(mClosePurchaseFragment, CLOSE_PURCHASE_FRAGMENT_TAG).commit();
        }

        if (mPurchasesInMonthCache == null) {
            mPurchasesInMonthCache = CacheBuilder.newBuilder().maximumSize(12).build();
        }

        if (savedInstanceState != null) {
            mMonth = (MonthYear) savedInstanceState.getSerializable(STATE_EXTRA_MONTH);
            Log.d(TAG, "restoring state: month=" + mMonth);
        } else {
            setCurrentMonth();
        }


        updateCurrentBtn();
    }

    private void setCurrentMonth() {
        Date now = Dates.now();
        mMonth = MonthYear.fromDate(now);
    }

    private void updateCurrentBtn() {
        String[] monthNames = mDateSymbols.getShortMonths();
        int year = mMonth.getYear();
        int month = mMonth.getMonth();
        mCurrentMonthBtn.setText(String.format("%s-%d",monthNames[month-1],year));
    }

    private void ensureContent() {

        mProgressContainer = findViewById(R.id.progress_container);
        mListContainer = findViewById(R.id.list_container);
        mEmptyView = findViewById(android.R.id.empty);
        if (mEmptyView != null) {
            mEmptyView.setVisibility(View.GONE);
        }

        mPurchasesList = (ListView) findViewById(android.R.id.list);
        mPurchasesAdapter = new PurchasesHistoryListAdapter(this);

        mPurchasesList.setAdapter(mPurchasesAdapter);
        mPurchasesList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mPurchasesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                invalidateOptionsMenu();

                PurchaseObj item = mPurchasesAdapter.getItem(position);

                if(unlockPurchaseId != item.getId()) {
                    unlockPurchaseId = item.getId();
                    tapCount = 1;
                    lastTap = Dates.now().getTime();
                } else {
                    long now = Dates.now().getTime();
                    if(now-lastTap < 1000)
                    {
                        tapCount++;
                        lastTap = now;
                    }
                    if(tapCount == 10)
                    {
                        mPurchasesStore.updatePurchaseState(unlockPurchaseId,PurchaseState.OPEN);
                        item.setState(PurchaseState.OPEN);
                        mPurchasesAdapter.notifyDataSetInvalidated();
                    }
                }
            }
        });


        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ) {
            mMonthSelector = findViewById(R.id.month_selector);
        }
        else
        {
            ActionBar actionBar = getActionBar();
            actionBar.setDisplayOptions( ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
            actionBar.setCustomView(R.layout.land_month_selector);
            mMonthSelector = actionBar.getCustomView();
        }


        mPrevMonthBtn = (ImageView) mMonthSelector.findViewById(R.id.prevMonth);
        mPrevMonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPrevMonth();
            }
        });
        mNextMonthBtn = (ImageView) mMonthSelector.findViewById(R.id.nextMonth);
        mNextMonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextMonth();
            }
        });
        mCurrentMonthBtn = (TextView) mMonthSelector.findViewById(R.id.currentMonth);
        mCurrentMonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCurrentMonth();
            }
        });
    }

    private void onCurrentMonth() {

        if (!mMonth.equals(MonthYear.fromDate(Dates.now()))) {
            setCurrentMonth();
            updateCurrentBtn();
            updateContent();
        }
    }

    private void updateContent() {
        mPurchasesList.clearChoices();
        invalidateOptionsMenu();
        loadPurchases();
    }

    private void onNextMonth() {
        setNextMonth();
        updateCurrentBtn();
        updateContent();
    }

    private void setNextMonth() {
        mMonth = mMonth.plusYear();
    }

    private void setPrevMonth() {
        mMonth = mMonth.minusYear();
    }

    private void onPrevMonth() {
        setPrevMonth();
        updateCurrentBtn();
        updateContent();
    }

    private void loadPurchases() {

        Date dt = mMonth.toDate();
        Date from = Dates.toBeginOfMonth(dt);
        Date to = Dates.toEndOfMonth(dt);

        PurchasesInMonth monthPurchases = mPurchasesInMonthCache.getIfPresent(mMonth);
        if (monthPurchases == null) {

            monthPurchases = new PurchasesInMonth();
            monthPurchases.month = mMonth;
            monthPurchases.loadTask = new LoadPurchasesTask(mPurchasesStore, where(QPurchase.startDt.between(from, to)));

            mPurchasesInMonthCache.put(mMonth, monthPurchases);
            monthPurchases.loadTask.execute();
        }
        settleUiState();
    }

    private void settleUiState() {

        if (mCurrentLoadPurchasesTask != null) {
            mCurrentLoadPurchasesTask.detachObserver();
        }

        PurchasesInMonth monthPurchases = mPurchasesInMonthCache.getIfPresent(mMonth);
        LoadPurchasesTask loadTask = monthPurchases.loadTask;
        if (loadTask.getStatus() != AsyncTask.Status.FINISHED) {
            mCurrentLoadPurchasesTask = loadTask;
            mCurrentLoadPurchasesTask.attachObserver(mLoadObserver);
            showLoading();

        } else {

            TaskResult<Collection<PurchaseObj>> loadResult = loadTask.getResult();
            if (loadResult.isError()) {
                showError(loadResult.getException().getMessage());
            } else {
                mCurrentPurchaseObjs = loadResult.getResult();
                refreshPurchasesList();
                showList();
                invalidateOptionsMenu();
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        loadPurchases();
    }

    private void refreshPurchasesList() {
        mPurchasesAdapter.clear();
        mPurchasesAdapter.addAll(mCurrentPurchaseObjs);
    }


    @Override
    public void onPause() {
        super.onPause();

        if (mCurrentLoadPurchasesTask != null)
            mCurrentLoadPurchasesTask.detachObserver();


        if (mErrMsgFragment != null)
            mErrMsgFragment.setListener(null);

        retainNonConfigurationInstance();
    }

    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putSerializable(STATE_EXTRA_MONTH, mMonth);
    }

    public void showList() {
        mProgressContainer.startAnimation(AnimationUtils.loadAnimation(
                this, android.R.anim.fade_out));
        mListContainer.startAnimation(AnimationUtils.loadAnimation(
                this, android.R.anim.fade_in));

        mProgressContainer.setVisibility(View.GONE);
        mListContainer.setVisibility(View.VISIBLE);

    }

    public void showLoading() {
        mProgressContainer.startAnimation(AnimationUtils.loadAnimation(
                this, android.R.anim.fade_in));
        mListContainer.startAnimation(AnimationUtils.loadAnimation(
                this, android.R.anim.fade_out));

        mProgressContainer.setVisibility(View.VISIBLE);
        mListContainer.setVisibility(View.GONE);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case OPEN_PURACHE_REQUEST_CODE:
                mPurchasesInMonthCache.invalidate(mMonth);
                loadPurchases();
                break;
        }
    }

    private PurchaseObj getSelectedPurchase() {
        int checkedPos = mPurchasesList.getCheckedItemPosition();
        if (checkedPos >= 0)
            return mPurchasesAdapter.getItem(checkedPos);
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(mCurrentPurchaseObjs != null) {
            PurchaseObj selectedPurchase = getSelectedPurchase();

            if (selectedPurchase != null) {
                int menuRes;
                if (selectedPurchase.getState() == PurchaseState.OPEN) {
                    menuRes = R.menu.open_purchase_menu;
                } else {
                    menuRes = R.menu.view_purchase_menu;
                }
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(menuRes, menu);
            }

        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case R.id.menuOpenPurchase:

                startOpenPurchaseActivity();
                break;

            case R.id.menuClosePurchase:
                closePurchase();
                break;

            case R.id.menuViewPurchase:
                startViewPurchaseActivity();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void closePurchase() {
        PurchaseObj purchase = getSelectedPurchase();
        mClosePurchaseFragment.closePurchase(purchase,new Runnable() {
            @Override
            public void run() {
                invalidateOptionsMenu();
                mPurchasesAdapter.notifyDataSetInvalidated();
            }
        });
    }

    private void startViewPurchaseActivity() {
        PurchaseObj purchase = getSelectedPurchase();
        Intent purchaseIntent = new Intent(this, PurchaseViewActivity.class);
        purchaseIntent.putExtra(PurchaseViewActivity.EXTRA_PURCHASE_ID, purchase.getId());
        startActivityForResult(purchaseIntent, VIEW_PURACHE_REQUEST_CODE);
    }

    private void startOpenPurchaseActivity() {
        PurchaseObj purchase = getSelectedPurchase();
        Intent purchaseIntent = new Intent(this, PurchaseEditActivity.class);
        purchaseIntent.putExtra(PurchaseEditActivity.EXTRA_PURCHASE_ID, purchase.getId());
        startActivityForResult(purchaseIntent, OPEN_PURACHE_REQUEST_CODE);
    }


    public void retainNonConfigurationInstance() {
        Retainer retainer = new Retainer();
        retainer.purchasesInMonthCache = mPurchasesInMonthCache;
        getFragmentManager().beginTransaction().add(retainer, RETAINER_FRAGMENT_TAG).commit();
    }


    static class PurchasesHistoryListAdapter extends ArrayAdapter<PurchaseObj> {

        public PurchasesHistoryListAdapter(Context context) {
            super(context, R.layout.purchase_history_list_item);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View row;
            LayoutInflater inflater = (LayoutInflater.from(getContext()));

            if (null == convertView) {
                row = inflater.inflate(R.layout.purchase_history_list_item, null);
            } else {
                row = convertView;
            }

            Purchase purchase = getItem(position);

            TextView purchaseHerdCell = (TextView) row.findViewById(R.id.purchaseHerdCell);
            TextView purchaseStartEndDtCell = (TextView) row.findViewById(R.id.purchaseStartEndDtCell);
            TextView purchaseCowAmountCell = (TextView) row.findViewById(R.id.purchaseCowAmountCell);
            ImageView purchaseStateCell = (ImageView) row.findViewById(R.id.purchaseStateCell);

            purchaseHerdCell.setText(String.format("%03d", purchase.getHerdNo()));
            String startEndStText = Dates.toDateTimeShort(purchase.getPurchaseStart());
            Date endDt = purchase.getPurchaseEnd();
            if (endDt != null) {
                startEndStText += " / ";
                startEndStText += Dates.toDateTimeShort(endDt);
            }
            purchaseStartEndDtCell.setText(startEndStText);
            purchaseCowAmountCell.setText(String.valueOf(purchase.getCowCount()));

            int stateDrawable = 0;
            PurchaseState state = purchase.getState();
            if(state != null) {
                switch (state) {
                    case OPEN:
                        stateDrawable = R.drawable.open_box;
                        break;
                    case CLOSED:
                        stateDrawable = R.drawable.lock;
                        break;
                    case SENT:
                        stateDrawable = R.drawable.checked;
                }
            }
            else
            {
                stateDrawable = R.drawable.open_box;
            }

            Resources r = getContext().getResources();
            purchaseStateCell.setImageDrawable(r.getDrawable(stateDrawable));

            return row;
        }

    }

}
