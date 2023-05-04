package com.bk.bkskup3.management;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.bk.bkskup3.BkApplication;
import com.bk.bkskup3.R;
import com.bk.bkskup3.dao.BkStore;
import com.bk.bkskup3.dao.SettingsStore;
import com.bk.bkskup3.feedback.ErrorMessageFragment;
import com.bk.bkskup3.feedback.ErrorToast;
import com.bk.bkskup3.settings.RepoSettings;
import com.bk.bkskup3.tasks.LoadRepoSettingsTask;
import com.bk.bkskup3.tasks.TaskResult;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/9/2014
 * Time: 8:52 PM
 */
public class RepoActivity extends Activity {

    private static final String STATE_EXTRA_STATE = "state_state";
    private static final String STATE_EXTRA_SETTINGS = "state_settings";
    private static final String RETAINER_FRAGMENT_TAG = "retainer";
    private static final String ERROR_MSG_FRAGMENT_TAG = "error_fragment";

    enum State
    {
        Loading,
        Idle,
        ShowingError
    }
    public static class Retainer extends Fragment {
        public Retainer() {
            setRetainInstance(true);
        }
        public LoadRepoSettingsTask loadTask;
    }


    private View mProgressContainer;
    private View mContentContainer;
    private EditText mRepoLoginEditBox;
    private EditText mRepoPasswordEditBox;
    private State mState;
    private RepoSettings mSettings;
    private LoadRepoSettingsTask mLoadTask;
    private ErrorMessageFragment mErrMsgFragment;
    private SettingsStore mSettingsStore;

    LoadRepoSettingsTask.Observer mObserver = new LoadRepoSettingsTask.Observer() {
        @Override
        public void onLoadStarted() {
            showLoading();
        }

        @Override
        public void onLoadSuccessful(RepoSettings result) {
            onSettingsLoaded(result);
        }

        @Override
        public void onLoadError(Exception e) {
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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.repo);

        BkApplication bkApplication = (BkApplication) getApplication();
        BkStore bkStore = bkApplication.getStore();
        mSettingsStore = bkStore.getSettingsStore();

        FragmentManager fm = getFragmentManager();

        Retainer retainer = (Retainer) fm.findFragmentByTag(RETAINER_FRAGMENT_TAG);
        if (retainer != null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(retainer);
            ft.commit();

            mLoadTask = retainer.loadTask;
        }

        mProgressContainer = findViewById(R.id.progress_container);
        mContentContainer =  findViewById(R.id.content_container);
        mRepoLoginEditBox = (EditText) findViewById(R.id.repoLoginEditBox);
        mRepoPasswordEditBox = (EditText) findViewById(R.id.repoPasswordEditBox);

        if (savedInstanceState != null) {
            mState = (State) savedInstanceState.getSerializable(STATE_EXTRA_STATE);
            mSettings = (RepoSettings) savedInstanceState.getSerializable(STATE_EXTRA_SETTINGS);
        }
    }

    protected void onDestroy()
    {
        super.onDestroy();
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_EXTRA_SETTINGS,mSettings);
        outState.putSerializable(STATE_EXTRA_STATE,mState);
    }

    protected void showLoading() {

        mContentContainer.setVisibility(View.GONE);
        mProgressContainer.setVisibility(View.VISIBLE);
    }

    private void showContent() {
        mProgressContainer.setVisibility(View.GONE);
        mContentContainer.setVisibility(View.VISIBLE);
    }

    private void loadSettings()
    {
        mState = State.Loading;
        mLoadTask = new LoadRepoSettingsTask(mSettingsStore);
        mLoadTask.attachObserver(mObserver);
        mLoadTask.execute();
    }

    private void onSettingsLoaded(RepoSettings result) {
        mState = State.Idle;
        mSettings = result;
        updateInputs();
        showContent();
    }

    private void showError(String error) {
        mState = State.ShowingError;
        mErrMsgFragment = new ErrorMessageFragment();
        mErrMsgFragment.setCaption(getResources().getString(R.string.errorCaption));
        mErrMsgFragment.setErrorMsg(error);
        mErrMsgFragment.setListener(mOnErrorHideListener);
        mErrMsgFragment.show(getFragmentManager(), ERROR_MSG_FRAGMENT_TAG);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mState == null || (mState == State.Idle && mSettings == null)) {
            showLoading();
            loadSettings();
            return;
        }


        if (mState == State.Loading) {
            if (mLoadTask != null) {

                if (mLoadTask.getStatus() == AsyncTask.Status.FINISHED) {
                    TaskResult<RepoSettings> result = mLoadTask.getResult();
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
                loadSettings();
            }
            return;
        }

        if (mState == State.ShowingError) {
            mErrMsgFragment = (ErrorMessageFragment) getFragmentManager().findFragmentByTag(ERROR_MSG_FRAGMENT_TAG);
            mErrMsgFragment.setListener(mOnErrorHideListener);
            return;
        }
    }


    protected void updateInputs() {
        mRepoLoginEditBox.setText(mSettings.getRepoLogin());
        mRepoPasswordEditBox.setText(mSettings.getRepoPassword());
    }

    protected RepoSettings createSettings() {
        RepoSettings settings = new RepoSettings();

        settings.setRepoLogin(mRepoLoginEditBox.getText().toString());
        settings.setRepoPassword(mRepoPasswordEditBox.getText().toString());

        return settings;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    private boolean validate() {
        ErrorToast toast = new ErrorToast(this);
        if (mRepoLoginEditBox.getText().length() == 0) {
            toast.show(R.string.errEnterRepoLogin);
            return false;
        }

        if (mRepoPasswordEditBox.getText().length() == 0) {
            toast.show(R.string.errEnterRepoPassword);
            return false;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSave:
                if (validate()) {
                    saveSettings(createSettings());
                    setResult(RESULT_OK);
                    finish();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveSettings(RepoSettings settings) {
        mSettingsStore.saveSettings(settings);
    }

}
