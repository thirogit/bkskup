package com.bk.bkskup3.management;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.bk.bkskup3.BkApplication;
import com.bk.bkskup3.R;
import com.bk.bkskup3.dao.BkStore;
import com.bk.bkskup3.dao.InvoiceNoTransactionStore;
import com.bk.bkskup3.feedback.ErrorMessageFragment;
import com.bk.bkskup3.feedback.ErrorToast;
import com.bk.bkskup3.invoice.InvoiceNoTransaction;
import com.bk.bkskup3.settings.InvoiceSettings;
import com.bk.bkskup3.tasks.DependenciesForInvoiceSettings;
import com.bk.bkskup3.tasks.LoadDependenciesForInvoiceSettingsTask;
import com.bk.bkskup3.tasks.SaveInvoiceSettingsTask;
import com.bk.bkskup3.tasks.TaskResult;
import com.bk.bkskup3.work.BusActivity;
import com.bk.bkskup3.work.fragment.BusyFragment;
import com.google.common.base.Strings;


/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/10/2014
 * Time: 3:46 PM
 */
public class InvoiceSettingsActivity extends BusActivity {

    private static final String RETAINER_FRAGMENT_TAG = "retainer";
    private static final String ERROR_MSG_FRAMGENT_TAG = "error_fragment";
    private static final String BUSY_MSG_FRAMGENT_TAG = "busy_fragment";

    private static final String STATE_EXTRA_INVOICESETTING = "state_invoicesetting";
    private static final String STATE_EXTRA_STATE = "state_activitystate";
    private static final String STATE_EXTRA_PREVSTATE = "state_activityprevstate";

    private EditText mInvoiceNoFmtEditBox;
    private EditText mInvoiceStartNoEditBox;
    private EditText mLmpInvoiceTemplateEditBox;
    private EditText mRegularInvoiceTemplateEditBox;

    private InvoiceSettings mInvoiceSettings;
    private InvoiceNoTransaction mLastInvoiceNoTransaction;
    private InvoiceNoTransactionStore mInvoiceNoStore;
    private BkStore mStore;
    private State mState;
    private State mPreviousState;
    protected ErrorMessageFragment mErrMsgFragment;
    protected BusyFragment mBusyFragment;
    private LoadDependenciesForInvoiceSettingsTask mLoadDependenciesTask;
    private SaveInvoiceSettingsTask mSaveInvoiceSettingsTask;
    private View mProgressContainer;
    private View mContentContainer;

    private LoadDependenciesForInvoiceSettingsTask.Observer mLoadDependenciesTaskObserver = new LoadDependenciesForInvoiceSettingsTask.Observer() {
        @Override
        public void onLoadStarted() {
            showLoading();
        }

        @Override
        public void onLoadSuccessful(DependenciesForInvoiceSettings result) {

            onDependenciesLoaded(result);
        }

        @Override
        public void onLoadError(Exception e) {
            setState(State.ShowingError);
            showErrorWithRetry(e.getMessage(), mLoadDependenciesErrorListener);
        }
    };

    private SaveInvoiceSettingsTask.Observer mSaveInvoiceSettingsTaskObserver = new SaveInvoiceSettingsTask.Observer() {
        @Override
        public void onSaveStarted() {
            showBusy(R.string.saving);
        }

        @Override
        public void onSaveSuccessful() {
            hideBusy();
            Intent intent = new Intent(InvoiceSettings.ACTION_SETTINGS_CHANGED);
            intent.putExtra(InvoiceSettings.EXTRA_LATEST_SETTINGS, mInvoiceSettings);
            sendBroadcast(intent);

            setResult(RESULT_OK);
            finish();
        }

        @Override
        public void onSaveError(Exception e) {

        }
    };

    private ErrorMessageFragment.ErrorFragmentListener mLoadDependenciesErrorListener = new ErrorMessageFragment.ForwardErrorFragmentListener() {
        @Override
        public void onAbandon() {
            mErrMsgFragment = null;
            clearState();
            finish();
        }

        @Override
        public void onRetry() {
            mErrMsgFragment = null;
            loadDependencies();
        }
    };

    private ErrorMessageFragment.ErrorFragmentListener mSaveSettingsErrorListener = new ErrorMessageFragment.ForwardErrorFragmentListener() {
        @Override
        public void onAbandon() {
            mErrMsgFragment = null;
            clearState();
            finish();
        }

        @Override
        public void onRetry() {
            mErrMsgFragment = null;
            save();
        }
    };

    enum State {
        LoadingDependencies,
        ShowingError,
        Idle,
        Saving,
    }

    public static class Retainer extends Fragment {
        public Retainer() {
            setRetainInstance(true);
        }

        public LoadDependenciesForInvoiceSettingsTask loadDependenciesTask;
        public SaveInvoiceSettingsTask saveInvoiceSettingsTask;
    }


    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.invoice_settings);

        BkApplication bkApplication = (BkApplication) getApplication();
        mStore = bkApplication.getStore();

        mContentContainer = findViewById(R.id.content_container);
        mProgressContainer = findViewById(R.id.progress_container);

        mInvoiceNoFmtEditBox = (EditText) findViewById(R.id.invoiceNoFmtEditBox);
        mInvoiceStartNoEditBox = (EditText) findViewById(R.id.invoiceStartNoEditBox);
        mLmpInvoiceTemplateEditBox = (EditText) findViewById(R.id.lumpInvoiceTemplateEditBox);
        mRegularInvoiceTemplateEditBox = (EditText) findViewById(R.id.regularInvoiceTemplateEditBox);



        FragmentManager fm = getFragmentManager();
        Retainer retainer = (Retainer) fm.findFragmentByTag(RETAINER_FRAGMENT_TAG);

        if (retainer != null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(retainer);
            ft.commit();

            mSaveInvoiceSettingsTask = retainer.saveInvoiceSettingsTask;
            mLoadDependenciesTask = retainer.loadDependenciesTask;
        }

        if (savedState != null) {
            mState = (State) savedState.getSerializable(STATE_EXTRA_STATE);
            mPreviousState = (State) savedState.getSerializable(STATE_EXTRA_PREVSTATE);
        }

    }


    private void showError(String message, ErrorMessageFragment.ErrorFragmentListener listener, boolean retryEnabled) {
        setState(State.ShowingError);
        mErrMsgFragment = new ErrorMessageFragment();
        mErrMsgFragment.setCaption(getResources().getString(R.string.errorCaption));
        mErrMsgFragment.setErrorMsg(message);
        mErrMsgFragment.setListener(listener);
        mErrMsgFragment.show(getFragmentManager(), ERROR_MSG_FRAMGENT_TAG);
    }

    private void showErrorWithRetry(String message, ErrorMessageFragment.ErrorFragmentListener listener) {
        showError(message, listener, true);
    }

    private void showErrorNoRetry(String message, ErrorMessageFragment.ErrorFragmentListener listener) {
        showError(message, listener, false);
    }

    private void clearState() {
        setState(State.Idle);
    }

    public void setState(State state) {
        this.mPreviousState = mState;
        this.mState = state;
    }

    private void afterDependenciesLoaded() {
        setState(State.Idle);
        showContent();
    }

    private void onDependenciesLoaded(DependenciesForInvoiceSettings dependencies) {

        mInvoiceSettings = dependencies.getSettings();
        mLastInvoiceNoTransaction = dependencies.getCurrentNoTransaction();
        updateBoxes();
        afterDependenciesLoaded();
    }

    private void updateBoxes() {
        mInvoiceNoFmtEditBox.setText(mInvoiceSettings.getInvoiceNoFmt());
        if (mLastInvoiceNoTransaction != null) {
            int startInvoiceNo = mLastInvoiceNoTransaction.getInvoiceNo();
            mInvoiceStartNoEditBox.setText(String.valueOf(startInvoiceNo));
        } else {
            mInvoiceStartNoEditBox.setText("");
        }
        mLmpInvoiceTemplateEditBox.setText(mInvoiceSettings.getLumpExtrasTemplate());
        mRegularInvoiceTemplateEditBox.setText(mInvoiceSettings.getRegularExtrasTemplate());
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_EXTRA_INVOICESETTING, mInvoiceSettings);
        outState.putSerializable(STATE_EXTRA_PREVSTATE, mPreviousState);
        outState.putSerializable(STATE_EXTRA_STATE, mState);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mLoadDependenciesTask != null) {
            mLoadDependenciesTask.detachObserver();
        }

        if (mSaveInvoiceSettingsTask != null) {
            mSaveInvoiceSettingsTask.detachObserver();
        }

        if (mErrMsgFragment != null)
            mErrMsgFragment.setListener(null);

        retainNonConfigurationInstance();
    }

    public void retainNonConfigurationInstance() {
        Retainer retainer = new Retainer();
        retainer.loadDependenciesTask = mLoadDependenciesTask;
        retainer.saveInvoiceSettingsTask = mSaveInvoiceSettingsTask;
        getFragmentManager().beginTransaction().add(retainer, RETAINER_FRAGMENT_TAG).commit();
    }

    protected void onResume() {
        super.onResume();
        continueResume();
    }

    private void showContent() {
        mProgressContainer.setVisibility(View.GONE);
        mContentContainer.setVisibility(View.VISIBLE);
    }

    protected void showLoading() {

        mContentContainer.setVisibility(View.GONE);
        mProgressContainer.setVisibility(View.VISIBLE);
    }

    private void loadDependencies() {
        setState(State.LoadingDependencies);
        mLoadDependenciesTask = new LoadDependenciesForInvoiceSettingsTask(mStore);
        mLoadDependenciesTask.attachObserver(mLoadDependenciesTaskObserver);
        mLoadDependenciesTask.execute();
    }

    private void continueResume() {

        if (mState == null) {
            loadDependencies();
            return;
        }

        if (mState == State.Idle) {
            showContent();
            return;
        }


        if (mState == State.LoadingDependencies) {
            if (mLoadDependenciesTask != null) {
                if (mLoadDependenciesTask.getStatus() == AsyncTask.Status.FINISHED) {
                    TaskResult<DependenciesForInvoiceSettings> result = mLoadDependenciesTask.getResult();
                    if (result.isError()) {
                        setState(State.ShowingError);
                        showErrorWithRetry(result.getException().getMessage(), mLoadDependenciesErrorListener);
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

        if (mState == State.Saving) {
            if (mSaveInvoiceSettingsTask != null) {
                showContent();
                if (mSaveInvoiceSettingsTask.getStatus() == AsyncTask.Status.FINISHED) {
                    TaskResult<Void> result = mSaveInvoiceSettingsTask.getResult();
                    if (result.isError()) {
                        hideBusy();
                        setState(State.ShowingError);
                        showErrorNoRetry(result.getException().getMessage(), mSaveSettingsErrorListener);
                    } else {
                        mSaveInvoiceSettingsTaskObserver.onSaveSuccessful();
                    }
                } else {
                    mSaveInvoiceSettingsTask.attachObserver(mSaveInvoiceSettingsTaskObserver);
                }
            } else {
                clearState();
                hideBusy();
                showContent();
            }
            return;
        }

        if (mState == State.ShowingError) {

            switch (mPreviousState) {
                case Saving:
                    mErrMsgFragment.setListener(mSaveSettingsErrorListener);
                    break;
                case LoadingDependencies:
                    mErrMsgFragment.setListener(mLoadDependenciesErrorListener);
                    break;
            }

        }

    }


    private void hideBusy() {
        if (mBusyFragment != null) {
            mBusyFragment.dismiss();
            mBusyFragment = null;
        }
    }

    private void showBusy(int busyTextResId) {
        FragmentManager fm = getFragmentManager();

        if (mBusyFragment == null) {
            mBusyFragment = BusyFragment.newInstance(getString(busyTextResId));
        }
        mBusyFragment.setCancelable(false);
        mBusyFragment.show(fm, BUSY_MSG_FRAMGENT_TAG);
    }


    private void updateFromBoxes() {
        mInvoiceSettings.setInvoiceNoFmt(mInvoiceNoFmtEditBox.getText().toString());
        mInvoiceSettings.setLumpExtrasTemplate(mLmpInvoiceTemplateEditBox.getText().toString());
        mInvoiceSettings.setRegularExtrasTemplate(mRegularInvoiceTemplateEditBox.getText().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.save_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }


    void hideKeyboard() {
        Activity activity = this;
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View currentFocus = activity.getCurrentFocus();
        if (currentFocus != null) {
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSave:
                updateFromBoxes();
                if (validate()) {
                    hideKeyboard();
                    save();
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void save() {

        updateFromBoxes();
        Editable text = mInvoiceStartNoEditBox.getText();
        String invoiceNoStr = text.toString();
        Integer startInvoiceNo = Integer.parseInt(invoiceNoStr);
        setState(State.Saving);
        mSaveInvoiceSettingsTask = new SaveInvoiceSettingsTask(mStore);
        mSaveInvoiceSettingsTask.attachObserver(mSaveInvoiceSettingsTaskObserver);
        mSaveInvoiceSettingsTask.execute(mInvoiceSettings, startInvoiceNo);

    }

    private void displayError(int msgId) {
        new ErrorToast(this).show(msgId);
    }

    private boolean validate() {
        Editable text = mInvoiceStartNoEditBox.getText();
        String invoiceNoStr = text.toString();
        if (Strings.isNullOrEmpty(invoiceNoStr)) {
            displayError(R.string.errEmptyInvoiceNo);
            return false;
        }
        return true;
    }

}
