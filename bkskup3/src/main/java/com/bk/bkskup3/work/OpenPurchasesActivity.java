package com.bk.bkskup3.work;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

import java.util.*;

import javax.inject.Inject;

import static com.mysema.query.support.QueryBuilder.where;

/**
 * Created by IntelliJ IDEA.
 * User: root
 * Date: 24.06.11
 * Time: 10:01
 */
public class OpenPurchasesActivity extends BkActivity {

    private static final String TAG = OpenPurchasesActivity.class.getSimpleName();

    public static class Retainer extends Fragment {
        public Retainer() {
            setRetainInstance(true);
        }

        public LoadPurchasesTask loadPurchasesTask;
        public Collection<PurchaseObj> purchaseObjs;
    }

    static class OpenPurchasesListAdapter extends BaseAdapter {

        private boolean mNotifyOnChange = true;
        private List<PurchaseObj> mItems = new ArrayList<PurchaseObj>();
        private Context context;

        OpenPurchasesListAdapter(Context context) {
            this.context = context;
        }

        public Context getContext() {
            return context;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View row;
            LayoutInflater inflater = (LayoutInflater.from(getContext()));

            if (null == convertView) {
                row = inflater.inflate(R.layout.open_purchases_list_item, null);
            } else {
                row = convertView;
            }

            Purchase purchase = getItem(position);

            TextView purchaseHerdCell = (TextView) row.findViewById(R.id.purchaseHerdCell);
            TextView purchaseStartDtCell = (TextView) row.findViewById(R.id.purchaseStartDtCell);
            TextView purchaseCowAmountCell = (TextView) row.findViewById(R.id.purchaseCowAmountCell);

            purchaseHerdCell.setText(String.format("%03d", purchase.getHerdNo()));
            purchaseStartDtCell.setText(Dates.toDateTimeShort(purchase.getPurchaseStart()));
            purchaseCowAmountCell.setText(String.valueOf(purchase.getCowCount()));

            return row;
        }

        public void clear() {
            mItems.clear();
            notifyDataSetChangedIfNeeded();
        }

        private void notifyDataSetChangedIfNeeded() {
            if (mNotifyOnChange) notifyDataSetChanged();
        }

        public void add(PurchaseObj purchase) {
            mItems.add(purchase);
            notifyDataSetChangedIfNeeded();
        }

        public void addAll(Collection<? extends PurchaseObj> items) {
            mItems.addAll(items);
            notifyDataSetChangedIfNeeded();
        }

        public void removeAt(int position) {
            mItems.remove(position);
            notifyDataSetChangedIfNeeded();
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            mNotifyOnChange = true;
        }

        public void setNotifyOnChange(boolean notifyOnChange) {
            mNotifyOnChange = notifyOnChange;
        }

        public int getCount() {
            return mItems.size();
        }


        public PurchaseObj getItem(int position) {
            return mItems.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getView(position, convertView, parent);
        }

    }


    private static final String RETAINER_FRAGMENT_TAG = "retainer";
    private static final String ERROR_MSG_FRAGMENT_TAG = "error_fragment";
    private static final String CLOSE_PURCHASE_FRAGMENT_TAG = "close_purchase";
    private static final int VIEW_PURACHE_REQUEST_CODE = 1001;
    private static final String STATE_EXTRA_STATE = "state";

    Collection<PurchaseObj> mPurchaseObjs;
    ListView mPurchasesList;
    OpenPurchasesListAdapter mPurchasesAdapter;

    private View mProgressContainer;
    private View mListContainer;
    private View mEmptyView;


    private LoadPurchasesTask mLoadPurchasesTask;
    private State mState;
    protected ErrorMessageFragment mErrMsgFragment;
    private ClosePurchaseActivityFragment mClosePurchaseFragment;

    @Inject
    PurchasesStore mPurchasesStore;

    enum State {
        LoadingOpenPurchases,
        Idle,
        ShowingError
    }

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
            clearState();
            mErrMsgFragment = null;
        }
    };



    private void onPurchasesLoadCompleted() {
        setState(State.Idle);
        TaskResult<Collection<PurchaseObj>> result = mLoadPurchasesTask.getResult();
        mPurchaseObjs = result.getResult();
        mLoadPurchasesTask = null;
        showList();
        refreshPurchasesList();
    }

    private void clearState() {
        mState = State.Idle;
    }

    public void setState(State state) {
        this.mState = state;
    }

    private void showError(String error) {
        setState(State.ShowingError);
        mErrMsgFragment = new ErrorMessageFragment();
        mErrMsgFragment.setCaption(getResources().getString(R.string.errorCaption));
        mErrMsgFragment.setErrorMsg(error);
        mErrMsgFragment.setListener(mOnErrorHideListener);
        mErrMsgFragment.show(getFragmentManager(), ERROR_MSG_FRAGMENT_TAG);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_purchases);

        Log.d(TAG, "creating");

        FragmentManager fm = getFragmentManager();
        Retainer retainer = (Retainer) fm.findFragmentByTag(RETAINER_FRAGMENT_TAG);
        if (retainer != null) {

            Log.d(TAG, "retaining");

            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(retainer);
            ft.commit();

            mLoadPurchasesTask = retainer.loadPurchasesTask;
            mPurchaseObjs = retainer.purchaseObjs;
        } else {
            Log.d(TAG, "no retainer");
        }

        mClosePurchaseFragment = (ClosePurchaseActivityFragment) fm.findFragmentByTag(CLOSE_PURCHASE_FRAGMENT_TAG);
        if (mClosePurchaseFragment == null) {
            mClosePurchaseFragment = (ClosePurchaseActivityFragment) Fragment.instantiate(this, ClosePurchaseActivityFragment.class.getName());
            fm.beginTransaction().add(mClosePurchaseFragment, CLOSE_PURCHASE_FRAGMENT_TAG).commit();
        }

        if (savedInstanceState != null) {
            mState = (State) savedInstanceState.getSerializable(STATE_EXTRA_STATE);
            Log.d(TAG, "restoring state " + mState);
        }

        ensureContent();

    }


    private void ensureContent() {

        mProgressContainer = findViewById(R.id.progress_container);
        mListContainer = findViewById(R.id.list_container);
        mEmptyView = findViewById(android.R.id.empty);
        if (mEmptyView != null) {
            mEmptyView.setVisibility(View.GONE);
        }

        mPurchasesList = (ListView) findViewById(android.R.id.list);


        mPurchasesAdapter = new OpenPurchasesListAdapter(this);

        mPurchasesList.setAdapter(mPurchasesAdapter);
        mPurchasesList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mPurchasesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                invalidateOptionsMenu();
            }
        });
    }

    public void refreshPurchasesList() {
        mPurchasesAdapter.clear();
        mPurchasesAdapter.addAll(mPurchaseObjs);
        mPurchasesAdapter.notifyDataSetChanged();
    }

    private void onPurchaseSelect(Purchase purchase) {
        Intent purchaseIntent = new Intent(this, PurchaseEditActivity.class);
        purchaseIntent.putExtra(PurchaseEditActivity.EXTRA_PURCHASE_ID, purchase.getId());
        startActivityForResult(purchaseIntent, VIEW_PURACHE_REQUEST_CODE);
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


    private void startPurchasesLoad() {
        mState = State.LoadingOpenPurchases;
        showLoading();

        mLoadPurchasesTask = new LoadPurchasesTask(mPurchasesStore,where(QPurchase.state.eq(PurchaseState.OPEN)));
        mLoadPurchasesTask.attachObserver(mLoadObserver);
        mLoadPurchasesTask.execute();
    }


    @Override
    public void onResume() {
        super.onResume();

        if (mState == null) {
            Log.d(TAG, "resume with no state, starting load");
            startPurchasesLoad();
            return;
        }

        if (mState == State.Idle) {

            if (mPurchaseObjs != null) {
                Log.d(TAG, "resume with Idle state, using retained purchaseObjs");
                showList();
                refreshPurchasesList();
            } else {
                Log.d(TAG, "resume with Idle state, no purchaseObjs");
                startPurchasesLoad();
            }
            return;
        }

        if (mState == State.LoadingOpenPurchases) {
            if (mLoadPurchasesTask != null) {
                Log.d(TAG, "resume with loading state, using retained task");
                if (mLoadPurchasesTask.getStatus() == AsyncTask.Status.FINISHED) {
                    onPurchasesLoadCompleted();
                } else {
                    showLoading();
                    mLoadPurchasesTask.attachObserver(mLoadObserver);
                }
            } else {
                Log.d(TAG, "resume with loading state, starting load");
                startPurchasesLoad();
            }
            return;
        }

        if (mState == State.ShowingError) {
            Log.d(TAG, "resume with error state");
            mErrMsgFragment = (ErrorMessageFragment) getFragmentManager().findFragmentByTag(ERROR_MSG_FRAGMENT_TAG);
            mErrMsgFragment.setListener(mOnErrorHideListener);
            return;
        }
    }


    @Override
    public void onPause() {
        super.onPause();

        if (mLoadPurchasesTask != null)
            mLoadPurchasesTask.detachObserver();
        if (mErrMsgFragment != null)
            mErrMsgFragment.setListener(null);

        retainNonConfigurationInstance();
    }

    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putSerializable(STATE_EXTRA_STATE, mState);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case VIEW_PURACHE_REQUEST_CODE:
                startPurchasesLoad();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        if (mPurchasesList.getCheckedItemPosition() >= 0) {
            inflater.inflate(R.menu.open_purchase_menu, menu);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.menuFinish:
//                setResult(RESULT_OK);
//                finish();
//                return true;

            case R.id.menuOpenPurchase:
                onPurchaseSelect(getSelectedPurchase());
                break;

            case R.id.menuClosePurchase:
                onPurchaseClose(mPurchasesList.getCheckedItemPosition());
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void onPurchaseClose(final int purchasePosition) {

        final PurchaseObj purchase = mPurchasesAdapter.getItem(purchasePosition);

        mClosePurchaseFragment.closePurchase(purchase,new Runnable() {
            @Override
            public void run() {
                mPurchasesAdapter.removeAt(purchasePosition);
                mPurchaseObjs.remove(purchase);
                mPurchasesList.clearChoices();
                invalidateOptionsMenu();
            }
        });

    }


    private PurchaseObj getSelectedPurchase() {
        int checkedPos = mPurchasesList.getCheckedItemPosition();
        if (checkedPos >= 0)
            return mPurchasesAdapter.getItem(checkedPos);
        return null;
    }


    public void retainNonConfigurationInstance() {
        Retainer retainer = new Retainer();
        retainer.loadPurchasesTask = mLoadPurchasesTask;
        retainer.purchaseObjs = mPurchaseObjs;
        getFragmentManager().beginTransaction().add(retainer, RETAINER_FRAGMENT_TAG).commit();
    }

}
