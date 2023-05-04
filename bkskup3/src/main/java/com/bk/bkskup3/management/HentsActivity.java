package com.bk.bkskup3.management;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bk.bkskup3.BkApplication;
import com.bk.bkskup3.R;
import com.bk.bkskup3.dao.HentsStore;
import com.bk.bkskup3.feedback.ErrorMessageFragment;
import com.bk.bkskup3.model.Hent;
import com.bk.bkskup3.model.HentObj;
import com.bk.bkskup3.model.HentType;
import com.bk.bkskup3.tasks.LoadAllHentsTask;
import com.bk.bkskup3.tasks.TaskResult;
import com.bk.bkskup3.work.EditHentActivity;
import com.bk.bkskup3.work.NewHentActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HentsActivity extends Activity {

    private final String TAG = HentsActivity.class.getSimpleName();

    private static final String STATE_EXTRA_STATE = "state";

    private static final String ERROR_MSG_FRAGMENT_TAG = "error_fragment";
    private static final String RETAINER_FRAGMENT_TAG = "retainer";

    private static final int EDIT_HENT_REQUEST_CODE = 1001;
    private static final int NEW_HENT_REQUEST_CODE = 1002;

    enum State {
        LoadingHents,
        Idle,
        ShowingError,
        SyncingHents
    }

    public static class Retainer extends Fragment {
        public Retainer() {
            setRetainInstance(true);
        }

        public LoadAllHentsTask loadHentsTask;
        public Collection<HentObj> hentObjs;
    }

    private View mProgressContainer;
    private View mListContainer;
    private ListView mList;
    private HentsStore mStore;

    static class HentsListAdapter extends BaseAdapter {

        private boolean mNotifyOnChange = true;
        private List<HentObj> mItems = new ArrayList<HentObj>();
        private Map<Integer, HentObj> mItemsById = new HashMap<Integer, HentObj>();
        private Context context;

        HentsListAdapter(Context context) {
            this.context = context;
        }

        public Context getContext() {
            return context;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View row;
            Context context = getContext();
            Resources r = context.getResources();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (null == convertView) {
                row = inflater.inflate(R.layout.hents_list_item, null);
            } else {
                row = convertView;
            }

            Hent hent = getItem(position);
            TextView hentName = (TextView) row.findViewById(R.id.hentName);
            hentName.setText(hent.getHentName());

            TextView hentFarmNo = (TextView) row.findViewById(R.id.hentFarmNo);
            hentFarmNo.setText(hent.getHentNo().toString());


            HentType hentType = hent.getHentType();
            View hentTypeIndicator = row.findViewById(R.id.hentTypeIndicator);
            switch (hentType) {
                case COMPANY:
                    hentTypeIndicator.setBackgroundColor(r.getColor(R.color.companyHent));
                    break;
                case INDIVIDUAL:
                    hentTypeIndicator.setBackgroundColor(r.getColor(R.color.individualHent));
                    break;
            }

            return row;
        }

        public void clear() {
            mItems.clear();
            mItemsById.clear();
            notifyDataSetChangedIfNeeded();
        }

        private void notifyDataSetChangedIfNeeded() {
            if (mNotifyOnChange) notifyDataSetChanged();
        }

        public void add(HentObj hent) {

            _add(hent);
            notifyDataSetChangedIfNeeded();
        }

        private void _add(HentObj hent) {

            HentObj hentObj = mItemsById.get(hent.getId());
            if (hentObj != null) {
                throw new IllegalArgumentException("already have hent with id = " + hent.getId());
            }
            mItems.add(hent);
            mItemsById.put(hent.getId(), hent);
        }

        public void update(Hent hent) {
            HentObj hentObj = mItemsById.get(hent.getId());
            if (hentObj != null) {
                hentObj.copyFrom(hent);
                notifyDataSetInvalidated();
            }
        }

        public void addAll(Collection<? extends HentObj> items) {

            for (HentObj hent : items) {
                _add(hent);
            }
            notifyDataSetChangedIfNeeded();
        }

//        public void removeAt(int position) {
//            mItems.remove(position);
//            notifyDataSetChangedIfNeeded();
//        }

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


        public HentObj getItem(int position) {
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

    private HentsListAdapter mAdapter;
    private State mState;
    protected ErrorMessageFragment mErrMsgFragment;

    protected Collection<HentObj> mHents;
    private LoadAllHentsTask mLoadTask;


    LoadAllHentsTask.Observer mObserver = new LoadAllHentsTask.Observer() {
        @Override
        public void onLoadStarted() {
            showLoading();
        }

        @Override
        public void onLoadSuccessful(Collection<HentObj> result) {
            onHentsLoaded(result);
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
        mStore = ((BkApplication)getApplication()).getStore().getHentsStore();
        FragmentManager fm = getFragmentManager();

        Retainer retainer = (Retainer) fm.findFragmentByTag(RETAINER_FRAGMENT_TAG);
        if (retainer != null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(retainer);
            ft.commit();

            mLoadTask = retainer.loadHentsTask;
            mHents = retainer.hentObjs;
        }

        if (savedInstanceState != null) {
            mState = (State) savedInstanceState.getSerializable(STATE_EXTRA_STATE);
        }

        setContentView(R.layout.hents);


        mProgressContainer = findViewById(R.id.progress_container);
        mListContainer = findViewById(R.id.list_container);
        mList = (ListView) findViewById(R.id.hentsList);

        mAdapter = new HentsListAdapter(this);
        mList.setAdapter(mAdapter);
        mList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                invalidateOptionsMenu();
            }
        });

        if (mState == State.Idle) {
            mAdapter.addAll(mHents);
        }

    }

    private void clearState() {
        mState = State.Idle;
    }

    public void setState(State state) {
        this.mState = state;
    }

    private void onHentsLoaded(Collection<HentObj> hentObjs) {
        mHents = hentObjs;
        mState = State.Idle;
        mAdapter.clear();
        mAdapter.addAll(hentObjs);
        showHentsList();
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putSerializable(STATE_EXTRA_STATE, mState);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mLoadTask != null)
            mLoadTask.detachObserver();
        if (mErrMsgFragment != null)
            mErrMsgFragment.setListener(null);

        retainNonConfigurationInstance();

    }

    protected void onStart() {
        super.onStart();
    }

    protected void onStop() {
        super.onStop();
    }


    public void retainNonConfigurationInstance() {
        Retainer retainer = new Retainer();
        retainer.loadHentsTask = mLoadTask;
        retainer.hentObjs = mHents;
        getFragmentManager().beginTransaction().add(retainer, RETAINER_FRAGMENT_TAG).commit();
    }

    private void loadHents() {
        mState = State.LoadingHents;
        mLoadTask = new LoadAllHentsTask(mStore);
        mLoadTask.attachObserver(mObserver);
        mLoadTask.execute();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mState == null || (mState == State.Idle && mHents == null)) {
            showLoading();
            loadHents();
            return;
        }


        if (mState == State.LoadingHents) {
            if (mLoadTask != null) {

                if (mLoadTask.getStatus() == AsyncTask.Status.FINISHED) {
                    TaskResult<Collection<HentObj>> result = mLoadTask.getResult();
                    if (result.isError()) {
                        mState = State.ShowingError;
                        showError(result.getException().getMessage());
                    } else {
                        mObserver.onLoadSuccessful(result.getResult());
                    }
                } else {
                    showLoading();
                    mLoadTask.attachObserver(mObserver);
                }
            } else {
                loadHents();
            }
            return;
        }

        if (mState == State.ShowingError) {
            mErrMsgFragment = (ErrorMessageFragment) getFragmentManager().findFragmentByTag(ERROR_MSG_FRAGMENT_TAG);
            mErrMsgFragment.setListener(mOnErrorHideListener);
            return;
        }
    }


    protected void showLoading() {

        mListContainer.setVisibility(View.GONE);
        mProgressContainer.setVisibility(View.VISIBLE);
    }

    private void showHentsList() {
        mProgressContainer.setVisibility(View.GONE);
        mListContainer.setVisibility(View.VISIBLE);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        if (mList.getCheckedItemPosition() >= 0) {
            inflater.inflate(R.menu.hents_crud_menu, menu);
        } else {
            inflater.inflate(R.menu.hents_menu, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuAddHent:
                onNewHent();
                return true;

            case R.id.menuEditHent:
                onEditHent();
                break;

            case R.id.menuDownloadHents:
                onDownloadHents();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onDownloadHents() {

//        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (which == DialogInterface.BUTTON_POSITIVE) {
//                    mClient.requestStartHentSync();
//                }
//            }
//        };
//
//        Resources r = getResources();
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage(r.getString(R.string.askDownloadHents));
//        builder.setPositiveButton(android.R.string.yes, dialogClickListener);
//        builder.setNegativeButton(android.R.string.no, dialogClickListener);
//        builder.show();
    }


    protected void refreshHentList() {
        mAdapter.notifyDataSetChanged();
    }

    protected void onEditHent() {

        int position = mList.getCheckedItemPosition();
        Hent hentToEdit = mAdapter.getItem(position);
        Intent editHentIntent = new Intent(this, EditHentActivity.class);
        editHentIntent.putExtra(EditHentActivity.EXTRA_HENT_TO_EDIT_ID, hentToEdit.getId());
        startActivityForResult(editHentIntent, EDIT_HENT_REQUEST_CODE);
    }

    protected void onNewHent() {
        Intent newHentIntent = new Intent(this, NewHentActivity.class);
        startActivityForResult(newHentIntent, NEW_HENT_REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case NEW_HENT_REQUEST_CODE:

                HentObj newHent = (HentObj) data.getSerializableExtra(NewHentActivity.EXTRA_SAVED_HENT);
                mHents.add(newHent);
                mAdapter.add(newHent);
                break;

            case EDIT_HENT_REQUEST_CODE:
                HentObj editedHent = (HentObj) data.getSerializableExtra(EditHentActivity.EXTRA_SAVED_HENT);
                mAdapter.update(editedHent);
                break;
        }
    }




}