package com.bk.bkskup3.work;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.bk.bkskup3.BkActivity;
import com.bk.bkskup3.R;
import com.bk.bkskup3.dao.HentsStore;
import com.bk.bkskup3.feedback.ErrorMessageFragment;
import com.bk.bkskup3.feedback.ErrorToast;
import com.bk.bkskup3.model.Hent;
import com.bk.bkskup3.model.HentObj;
import com.bk.bkskup3.model.HentType;
import com.bk.bkskup3.utils.TextSearchUtils;
import com.bk.bkskup3.work.fragment.LoadingDialogFragment;
import com.bk.bkskup3.tasks.FindHentTask;
import com.bk.bkskup3.tasks.TaskResult;
import com.bk.widgets.actionbar.ActionBar;

import java.util.Collection;
import java.util.LinkedList;

import javax.inject.Inject;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/31/11
 * Time: 4:52 PM
 */
public class FindHentActivity extends BkActivity {
    public static final String EXTRA_FOUND_HENT = "found_hent";
    private static final String ERRORMSG_FRAMGENT_TAG = "error";
    private static final String LOADING_FRAGMENT_TAG = "loading";
    private static final String RETAINER_FRAGMENT_TAG = "retainer";
    private static final String STATE_EXTRA_STATE = "state";
    private static final String STATE_EXTRA_SEARCHPATTERN = "search_pattern";
    private static final String STATE_EXTRA_FOUNDHENTS = "found_hents";
    private static final String STATE_EXTRA_ISFRESH = "is_fresh";

    enum State {
        Idle,
        Searching,
        ShowingError
    }

    public static class Retainer extends Fragment {
        public Retainer() {
            setRetainInstance(true);
        }

        public FindHentTask findHentTask;
    }

    @Inject
    HentsStore mHentsStore;

    private LinkedList<HentObj> mFoundHents;
    private State mState;
    private FindHentTask mFindHentTask;
    private String mSearchPattern;
    HentFindResultListAdapter mHentListAdapter;
    ListView mHentSearchResultList;
    private LoadingDialogFragment mLoadingFragment;
    protected ErrorMessageFragment mErrMsgFragment;
    private boolean mIsFresh;

    private FindHentTask.Observer mObserver = new FindHentTask.Observer() {
        @Override
        public void onStarted() {
            mState = State.Searching;
            showLoading();
        }

        @Override
        public void onFinished(Collection<HentObj> hents) {
            hideLoading();
            mState = State.Idle;
            setFoundHents(hents);
        }

        @Override
        public void onError(Exception e) {
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

    private void setFoundHents(Collection<HentObj> hents)
    {
        mFoundHents.clear();
        mFoundHents.addAll(hents);
        refreshResultList();
        decideOnVisibilityOfNoHentsFound();
        mIsFresh = false;
    }

    private void decideOnVisibilityOfNoHentsFound() {
        if(mFoundHents.size() == 0)
        {
            showNoHentsFound();
        }
        else
        {
            hideNoHentsFound();
        }
    }

    private void hideNoHentsFound() {
        getNoMatchingHentsBox().setVisibility(View.GONE);
    }

    private void showNoHentsFound() {
        getNoMatchingHentsBox().setVisibility(View.VISIBLE);

    }

    private TextView getNoMatchingHentsBox()
    {
        return (TextView) findViewById(R.id.noMatchingHentsBox);
    }

    private void refreshResultList() {

        mHentListAdapter.setNotifyOnChange(false);
        mHentListAdapter.clear();

        for (HentObj hent : mFoundHents) {
            mHentListAdapter.add(new HentFindResultListAdapter.HentFindResultItem(hent,mSearchPattern));
        }
        mHentListAdapter.setNotifyOnChange(true);
        mHentListAdapter.notifyDataSetChanged();
    }

    private void showError(String error) {
        mErrMsgFragment = new ErrorMessageFragment();
        mErrMsgFragment.setCaption(getResources().getString(R.string.errorCaption));
        mErrMsgFragment.setErrorMsg(error);
        mErrMsgFragment.setListener(mOnErrorHideListener);
        mErrMsgFragment.show(getFragmentManager(), ERRORMSG_FRAMGENT_TAG);
    }

    private void hideLoading() {
        mLoadingFragment.dismiss();
        mLoadingFragment = null;
    }

    private void showLoading() {
        mLoadingFragment = new LoadingDialogFragment();
        mLoadingFragment.setWaitText(getString(R.string.searching));
        mLoadingFragment.show(getFragmentManager(), LOADING_FRAGMENT_TAG);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.find_hent);


        ActionBar actionBar = (ActionBar) findViewById(R.id.actionBar);
        ActionBar.TextAction okAction = new ActionBar.TextAction(R.string.ok);
        okAction.setListener(new ActionBar.ActionListener() {
            @Override
            public void onAction() {
                onOk();
            }
        });

        ActionBar.TextAction cancelAction = new ActionBar.TextAction(R.string.cancel);
        cancelAction.setListener(new ActionBar.ActionListener() {
            @Override
            public void onAction() {
                onCancel();
            }
        });

        ActionBar.TextAction findAction = new ActionBar.TextAction(R.string.findBtnCaption);
        findAction.setListener(new ActionBar.ActionListener() {
            @Override
            public void onAction() {
                onFind();
            }
        });

        actionBar.addAction(findAction);
        actionBar.addAction(okAction);
        actionBar.addAction(cancelAction);
        actionBar.setTitle(getTitle());

        mHentSearchResultList = (ListView) findViewById(R.id.hentSearchResultList);
        mHentListAdapter = new HentFindResultListAdapter(this);
        mHentSearchResultList.setAdapter(mHentListAdapter);
        mHentSearchResultList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parentView, View view, int position, long id) {
                onHentSelect(mHentListAdapter.getItem(position).getHent());
                return true;
            }
        });
        mHentSearchResultList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        FragmentManager fm = getFragmentManager();
        Retainer retainer = (Retainer) fm.findFragmentByTag(RETAINER_FRAGMENT_TAG);
        if (retainer != null) {

            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(retainer);
            ft.commit();

            mFindHentTask = retainer.findHentTask;
        }

        if (savedInstanceState != null) {
            mState = (State) savedInstanceState.getSerializable(STATE_EXTRA_STATE);
            mSearchPattern = savedInstanceState.getString(STATE_EXTRA_SEARCHPATTERN);
            mFoundHents = (LinkedList<HentObj>) savedInstanceState.getSerializable(STATE_EXTRA_FOUNDHENTS);
            mIsFresh = savedInstanceState.getBoolean(STATE_EXTRA_ISFRESH,false);
        }
        else
        {
            mFoundHents = new LinkedList<HentObj>();
            mIsFresh = true;
        }

    }

    protected void onResume() {
        super.onResume();

        refreshResultList();
        if(!mIsFresh) {
            decideOnVisibilityOfNoHentsFound();
        }

        if (mState != null) {
            if (mState == State.Searching) {
                if (mFindHentTask != null) {
                    if (mFindHentTask.getStatus() == AsyncTask.Status.FINISHED) {
                        TaskResult<Collection<HentObj>> result = mFindHentTask.getResult();

                        if (result.isError()) {
                            mState = State.ShowingError;
                            showError(result.getException().getMessage());
                        } else {
                            mState = State.Idle;
                            setFoundHents(result.getResult());
                        }
                    } else {
                        showLoading();
                        mFindHentTask.attachObserver(mObserver);
                    }
                }
                return;
            }


            if (mState == State.ShowingError) {
                mErrMsgFragment = (ErrorMessageFragment) getFragmentManager().findFragmentByTag(ERRORMSG_FRAMGENT_TAG);
                mErrMsgFragment.setListener(mOnErrorHideListener);
                return;
            }
        }

    }

    protected void onPause() {
        super.onPause();
        if (mFindHentTask != null) {
            mFindHentTask.detachObserver();
        }

        if (mErrMsgFragment != null)
            mErrMsgFragment.setListener(null);

        retainNonConfigurationInstance();
    }

    public void retainNonConfigurationInstance() {
        Retainer retainer = new Retainer();
        retainer.findHentTask = mFindHentTask;
        getFragmentManager().beginTransaction().add(retainer, RETAINER_FRAGMENT_TAG).commit();
    }

    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        state.putSerializable(STATE_EXTRA_STATE, mState);

        if(mSearchPattern != null)
            state.putString(STATE_EXTRA_SEARCHPATTERN, mSearchPattern);

        state.putSerializable(STATE_EXTRA_FOUNDHENTS,mFoundHents);
        state.putBoolean(STATE_EXTRA_ISFRESH,mIsFresh);
    }

    private void onFind() {
        EditText hentNameEditBox = (EditText) findViewById(R.id.hentNameEditBox);
        String hentNameQuery = hentNameEditBox.getText().toString();
        if (hentNameQuery.length() > 2) {
            fireHentSearch(hentNameQuery);
        } else {
            new ErrorToast(this).show(R.string.errSearchPatternToShort);
        }
    }

    private void onOk() {
        if(mHentListAdapter.getCount() == 1)
        {
            onHentSelect(mHentListAdapter.getItem(0).getHent());
            return;
        }

        int checkedItemPos = mHentSearchResultList.getCheckedItemPosition();
        if (checkedItemPos >= 0) {
            onHentSelect(mHentListAdapter.getItem(checkedItemPos).getHent());
        }
    }

    private void onCancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private void onHentSelect(HentObj hent) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_FOUND_HENT, hent);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void fireHentSearch(String hentSearchPattern) {
        mSearchPattern = hentSearchPattern;
        mFindHentTask = new FindHentTask(hentSearchPattern, mHentsStore);
        mFindHentTask.attachObserver(mObserver);
        mFindHentTask.execute();
    }

    public static class HentFindResultListAdapter extends ArrayAdapter<HentFindResultListAdapter.HentFindResultItem> {

        public HentFindResultListAdapter(Context context) {
            super(context, R.layout.find_hent_list_item, R.id.hentName);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row;
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);

            if (null == convertView) {
                row = inflater.inflate(R.layout.find_hent_list_item, null);
            } else {
                row = convertView;
            }

            HentFindResultItem item = getItem(position);
            Hent hent = item.getHent();

            TextView hentNameLbl = (TextView) row.findViewById(R.id.hentName);
            String hentName = hent.getHentName();
            Spannable hentNameSpan = new SpannableString(hentName);

            String pattern = item.getQueryPattern();
            int hentNamePatternMatchIndex = TextSearchUtils.indexOfIgnoreCase(hentName, pattern);
            if (hentNamePatternMatchIndex >= 0) {
                hentNameSpan.setSpan(new ForegroundColorSpan(Color.BLACK), hentNamePatternMatchIndex, hentNamePatternMatchIndex + pattern.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                hentNameSpan.setSpan(new BackgroundColorSpan(Color.YELLOW), hentNamePatternMatchIndex, hentNamePatternMatchIndex + pattern.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            hentNameLbl.setText(hentNameSpan);

            TextView hentFarmNoLbl = (TextView) row.findViewById(R.id.hentFarmNo);
            String hentFarmNo = hent.getHentNo().toString();

            Spannable hentFarmNoSpan = new SpannableString(hentFarmNo);
            int hentNoPatternMatchIndex = TextSearchUtils.indexOfIgnoreCase(hentFarmNoSpan, pattern);
            if (hentNoPatternMatchIndex >= 0) {
                hentFarmNoSpan.setSpan(new ForegroundColorSpan(Color.BLACK), hentNoPatternMatchIndex, hentNoPatternMatchIndex + pattern.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                hentFarmNoSpan.setSpan(new BackgroundColorSpan(Color.YELLOW), hentNoPatternMatchIndex, hentNoPatternMatchIndex + pattern.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            hentFarmNoLbl.setText(hentFarmNoSpan);

            Resources r = getContext().getResources();

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

        public static class HentFindResultItem {
            private HentObj mHent;
            private String mQueryPattern;

            public HentFindResultItem(HentObj hent, String queryPattern) {
                this.mHent = hent;
                this.mQueryPattern = queryPattern;
            }

            public HentObj getHent() {
                return mHent;
            }

            public String getQueryPattern() {
                return mQueryPattern;
            }
        }
    }
}
