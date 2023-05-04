package com.bk.bkskup3.work;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bk.bkskup3.BkActivity;
import com.bk.bkskup3.R;
import com.bk.bkskup3.dao.BkStore;
import com.bk.bkskup3.feedback.ErrorMessageFragment;
import com.bk.bkskup3.feedback.ErrorToast;
import com.bk.bkskup3.model.Herd;
import com.bk.bkskup3.model.HerdObj;
import com.bk.bkskup3.model.PurchaseDetails;
import com.bk.bkskup3.tasks.DependenciesForOpenPurchase;
import com.bk.bkskup3.tasks.LoadDependenciesForOpenPurchaseTask;
import com.bk.bkskup3.tasks.TaskResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/10/2014
 * Time: 11:22 PM
 */
public class OpenNewPurchaseActivity extends BkActivity {

    private static final String RETAINER_FRAGMENT_TAG = "retainer";
    private static final String ERRORMSG_FRAGMENT_TAG = "error_fragment";

    private static final String STATE_EXTRA_STATE = "state";
    private static final String STATE_EXTRA_DEPENDENCIES = "dependencies";

    public static final String EXTRAS_HERDNO = "herd_no";


    enum State {
        LoadingDependencies,
        ShowingError,
        Idle,
    }

    public static class Retainer extends Fragment {
        public Retainer() {
            setRetainInstance(true);
        }
        public LoadDependenciesForOpenPurchaseTask loadDependenciesTask;
    }

    private HerdItemListAdapter mAdapter;
    private State mState;
    private LoadDependenciesForOpenPurchaseTask mLoadDependenciesTask;
    private DependenciesForOpenPurchase mDependencies;
    protected ErrorMessageFragment mErrMsgFragment;

    private View mProgressContainer;
    private View mContentContainer;
    private ListView mList;

    LoadDependenciesForOpenPurchaseTask.Observer mLoadDependenciesTaskObserver = new LoadDependenciesForOpenPurchaseTask.Observer() {
        @Override
        public void onLoadStarted() {
            showLoading();
        }

        @Override
        public void onLoadSuccessful(DependenciesForOpenPurchase result) {

            onDependenciesLoaded(result);
        }

        @Override
        public void onLoadError(Exception e) {
            mState = State.ShowingError;
            showError(e.getMessage());
        }
    };


    private ErrorMessageFragment.ErrorFragmentListener mOnErrorHideListener = new ErrorMessageFragment.ForwardErrorFragmentListener() {
        @Override
        public void onAbandon() {
            mState = State.Idle;
            mErrMsgFragment = null;
        }
    };

    public class HerdItem
    {
        public int herdNumber;
        public String herdAddress;
        public boolean hasOpenPurchase;
    }


    public class HerdItemListAdapter extends ArrayAdapter<HerdItem> {
        public HerdItemListAdapter(Context context) {
            super(context, R.layout.openpurchase_herd_list_item, R.id.herdNumber);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row;
            if (null == convertView) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.openpurchase_herd_list_item, null);
            } else {
                row = convertView;
            }

            HerdItem item = getItem(position);

            TextView herdNumberLbl = (TextView) row.findViewById(R.id.herdNumber);
            herdNumberLbl.setText(String.format("%03d", item.herdNumber));

            int doesnt_have_openpurchases_color = getResources().getColor(R.color.darker_green);
            int has_open_purchases_color = getResources().getColor(R.color.bloody_red);


            View hasOpenPurchaseIndicator = row.findViewById(R.id.hasOpenPurchaseIndicator);
            if(item.hasOpenPurchase)
            {
                hasOpenPurchaseIndicator.setBackgroundColor(has_open_purchases_color);
            }
            else {
                hasOpenPurchaseIndicator.setBackgroundColor(doesnt_have_openpurchases_color);
            }

            TextView herdAddressLbl = (TextView) row.findViewById(R.id.herdAddress);

            herdAddressLbl.setText(item.herdAddress);

            return row;
        }
    }


    @Inject
    BkStore mStore;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opennewpurchase);
        ensureContent();

        FragmentManager fm = getFragmentManager();
        Retainer retainer = (Retainer) fm.findFragmentByTag(RETAINER_FRAGMENT_TAG);
        if (retainer != null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(retainer);
            ft.commit();

            mLoadDependenciesTask = retainer.loadDependenciesTask;
        }

        if (savedInstanceState != null) {
            mDependencies = (DependenciesForOpenPurchase) savedInstanceState.getSerializable(STATE_EXTRA_DEPENDENCIES);
            mState = (State) savedInstanceState.getSerializable(STATE_EXTRA_STATE);
        }

        if (mState == State.Idle) {
            refreshList();
        }

    }

    private void refreshList() {
         mAdapter.clear();

        Collection<HerdObj> herds = mDependencies.getHerds();

        Set<Integer> herdsWithOpenPurchases = new HashSet<Integer>();
        Collection<PurchaseDetails> openPurchases = mDependencies.getOpenPurchases();
        if(openPurchases != null) {
            for (PurchaseDetails openPurchase : openPurchases) {
                herdsWithOpenPurchases.add(openPurchase.getHerdNo());
            }
        }


        Collection<HerdItem> items = new ArrayList<HerdItem>(herds.size());
        for(Herd herd : herds)
        {
            HerdItem item = new HerdItem();
            item.herdNumber = herd.getHerdNo();
            item.herdAddress = herd.getStreet() + " " + herd.getPoBox() + "\n" + herd.getZip() + " " + herd.getCity();
            item.hasOpenPurchase = herdsWithOpenPurchases.contains(herd.getHerdNo());
            items.add(item);

        }
        mAdapter.addAll(items);

    }

    private void ensureContent() {

        mContentContainer = findViewById(R.id.content_container);
        mProgressContainer = findViewById(R.id.progress_container);
        mList = (ListView) findViewById(R.id.list);

        mAdapter = new HerdItemListAdapter(this);
        mList.setAdapter(mAdapter);
        mList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onHerdSelected();
            }
        });
    }

    private void onHerdSelected() {
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();

        if (mList.getCheckedItemPosition() >= 0) {

                inflater.inflate(R.menu.done_finish_menu, menu);

        }


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
//            case R.id.menuFinish:
//                setResult(RESULT_CANCELED);
//                finish();
//                return true;
            case R.id.menuContinue:

                HerdItem selectedHerd = getSelectedHerdItem();
                if (selectedHerd != null)
                {
                    Intent result = new Intent();
                    result.putExtra(EXTRAS_HERDNO,selectedHerd.herdNumber);
                    setResult(RESULT_OK, result);
                    finish();
                }
                else
                {
                    new ErrorToast(this).show(R.string.errHerdNotSelected);
                }
                return true;

            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private HerdItem getSelectedHerdItem() {
        int checkedItemPos = mList.getCheckedItemPosition();
        if (checkedItemPos >= 0) {
            return mAdapter.getItem(checkedItemPos);
        }

        return null;
    }

    protected void showLoading() {

        mContentContainer.setVisibility(View.GONE);
        mProgressContainer.setVisibility(View.VISIBLE);
    }

    private void onDependenciesLoaded(DependenciesForOpenPurchase dependencies) {
        mDependencies = dependencies;
        mState = State.Idle;
        refreshList();
        showList();
    }

    private void showList() {
        mProgressContainer.setVisibility(View.GONE);
        mContentContainer.setVisibility(View.VISIBLE);
    }

    private void showError(String error) {
        mErrMsgFragment = new ErrorMessageFragment();
        mErrMsgFragment.setCaption(getResources().getString(R.string.errorCaption));
        mErrMsgFragment.setErrorMsg(error);
        mErrMsgFragment.setListener(mOnErrorHideListener);
        mErrMsgFragment.show(getFragmentManager(), ERRORMSG_FRAGMENT_TAG);
    }

    protected void onSaveInstanceState(android.os.Bundle state) {
        super.onSaveInstanceState(state);
        state.putSerializable(STATE_EXTRA_STATE, mState);
        state.putSerializable(STATE_EXTRA_DEPENDENCIES, mDependencies);

    }

    protected void onResume() {
        super.onResume();


        if (mState == State.Idle) {
            showList();
            return;
        }

        if (mState == null) {

            showLoading();
            loadDependencies();
            return;
        }

        if (mState == State.LoadingDependencies) {
            if (mLoadDependenciesTask != null) {

                if (mLoadDependenciesTask.getStatus() == AsyncTask.Status.FINISHED) {
                    TaskResult<DependenciesForOpenPurchase> result = mLoadDependenciesTask.getResult();
                    if (result.isError()) {
                        mState = State.ShowingError;
                        showError(result.getException().getMessage());
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

        if (mState == State.ShowingError) {
            mErrMsgFragment = (ErrorMessageFragment) getFragmentManager().findFragmentByTag(ERRORMSG_FRAGMENT_TAG);
            mErrMsgFragment.setListener(mOnErrorHideListener);
            return;
        }
    }

    private void loadDependencies() {
        mState = State.LoadingDependencies;
        mLoadDependenciesTask = new LoadDependenciesForOpenPurchaseTask(mStore);
        mLoadDependenciesTask.attachObserver(mLoadDependenciesTaskObserver);
        mLoadDependenciesTask.execute();
    }

    protected void onPause() {
        super.onPause();

        if (mLoadDependenciesTask != null)
            mLoadDependenciesTask.detachObserver();

        if(!isFinishing()) {
            retainNonConfigurationInstance();
        }
    }


    public void retainNonConfigurationInstance() {
        Retainer retainer = new Retainer();
        retainer.loadDependenciesTask = mLoadDependenciesTask;
        getFragmentManager().beginTransaction().add(retainer, RETAINER_FRAGMENT_TAG).commit();
    }




}
