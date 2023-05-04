package com.bk.bkskup3.management;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.bk.bkskup3.BkActivity;
import com.bk.bkskup3.R;
import com.bk.bkskup3.dao.BkStore;
import com.bk.bkskup3.dao.SettingsStore;
import com.bk.bkskup3.feedback.ErrorMessageFragment;
import com.bk.bkskup3.feedback.ErrorToast;
import com.bk.bkskup3.model.CompanyObj;
import com.bk.bkskup3.model.EAN;
import com.bk.bkskup3.settings.CompanyAsSettings;
import com.bk.bkskup3.tasks.download.DownloadCompanyTask;
import com.bk.bkskup3.tasks.TaskResult;
import com.bk.bkskup3.tasks.download.DownloadConfigurationTask;
import com.bk.bkskup3.tasks.download.DownloadTaskObserver;
import com.bk.bkskup3.work.FarmNoInputActivity;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/10/2014
 * Time: 3:46 PM
 */
public class CompanyActivity extends BkActivity {
    private static final String STATE_EXTRA_COMPANY = "company";
    private static final int REQUEST_CODE_FARMNO = 101;
    private static final String RETAINER_FRAGMENT_TAG = "retainer";
    private static final String ERRORMSG_FRAGMENT_TAG = "error_fragment";

    private EditText mCompanyName;
    private EditText mCompanyStreet;
    private EditText mCompanyPOBox;
    private EditText mCompanyCity;
    private EditText mCompanyZip;
    private EditText mFarmNo;
    private EditText mCellPhoneNo;
    private EditText mPhoneNo;
    private EditText mEmailAddress;
    private EditText mFiscalNo;

    private CompanyObj mCompany;

    private State mState;
    private View mProgressContainer;
    private View mContentContainer;
    protected ErrorMessageFragment mErrMsgFragment;
    private DownloadCompanyTask mDownloadCompanyTask;

    @Inject
    SettingsStore mSettingsStore;

    @Inject
    BkStore mStore;

    enum State {
        Downloading,
        ShowingError,
        Idle,
    }

    public static class Retainer extends Fragment {
        public Retainer() {
            setRetainInstance(true);
        }

        public DownloadCompanyTask downloadCompanyTask;
    }

    DownloadTaskObserver<CompanyObj> mDownloadCompanyTaskObserver = new DownloadTaskObserver<CompanyObj>() {
        @Override
        public void onLoadStarted() {
            hideKeyboard();
            showLoading();
        }

        @Override
        public void onLoadSuccessful(CompanyObj result) {

            onCompanyDownloaded(result);
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
            showContent();
        }
        @Override
        public void onRetry() {
            downloadCompany();
        }
    };



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.company);

        mContentContainer = findViewById(R.id.content_container);
        mProgressContainer = findViewById(R.id.progress_container);

        mCompanyName = (EditText) findViewById(R.id.companyNameEditBox);
        mCompanyStreet = (EditText) findViewById(R.id.companyStreetEditBox);
        mCompanyPOBox = (EditText) findViewById(R.id.companyPOBoxEditBox);
        mCompanyCity = (EditText) findViewById(R.id.companyCityEditBox);
        mCompanyZip = (EditText) findViewById(R.id.companyZipEditBox);
        mFarmNo = (EditText) findViewById(R.id.farmNoEditBox);
        mCellPhoneNo = (EditText) findViewById(R.id.cellPhoneNoEditBox);
        mPhoneNo = (EditText) findViewById(R.id.phoneNoEditBox);
        mEmailAddress = (EditText) findViewById(R.id.emailEditBox);
        mFiscalNo = (EditText) findViewById(R.id.fiscalNoEditBox);


        FragmentManager fm = getFragmentManager();
        Retainer retainer = (Retainer) fm.findFragmentByTag(RETAINER_FRAGMENT_TAG);
        if (retainer != null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(retainer);
            ft.commit();

            mDownloadCompanyTask = retainer.downloadCompanyTask;
        }



        if(savedInstanceState == null) {
            CompanyAsSettings companyAsSettings = mSettingsStore.loadSettings(CompanyAsSettings.class);
            mCompany = new CompanyObj();
            mCompany.copyFrom(companyAsSettings);
        } else {
            mCompany = (CompanyObj) savedInstanceState.getSerializable(STATE_EXTRA_COMPANY);
        }

        mFarmNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChangeFarmNo();
            }
        });

        if (mState == null || mState == State.Idle) {

            updateBoxes();
        }
    }

    private void showContent() {
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

    protected void showLoading() {

        mContentContainer.setVisibility(View.GONE);
        mProgressContainer.setVisibility(View.VISIBLE);
    }

    private void onCompanyDownloaded(CompanyObj companyObj) {
        mCompany.copyFrom(companyObj);
        mState = State.Idle;
        updateBoxes();
        showContent();
    }


    void hideKeyboard()
    {
        Activity activity = this;
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View currentFocus = activity.getCurrentFocus();
        if(currentFocus != null) {
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

    protected void onResume() {

        super.onResume();

        if (mState == null || mState == State.Idle) {
            showContent();
            return;
        }

        if (mState == State.Downloading) {
            if (mDownloadCompanyTask != null) {

                if (mDownloadCompanyTask.getStatus() == AsyncTask.Status.FINISHED) {
                    TaskResult<CompanyObj> result = mDownloadCompanyTask.getResult();
                    if (result.isError()) {
                        mState = State.ShowingError;
                        showError(result.getException().getMessage());
                    } else {
                        mDownloadCompanyTaskObserver.onLoadSuccessful(result.getResult());
                    }
                } else {
                    showLoading();
                    mDownloadCompanyTask.attachObserver(mDownloadCompanyTaskObserver);
                }
            } else {
                downloadCompany();
            }
            return;
        }

        if (mState == State.ShowingError) {
            mErrMsgFragment = (ErrorMessageFragment) getFragmentManager().findFragmentByTag(ERRORMSG_FRAGMENT_TAG);
            mErrMsgFragment.setListener(mOnErrorHideListener);
            return;
        }
    }

    private void downloadCompany() {
        mState = State.Downloading;
        mDownloadCompanyTask = new DownloadCompanyTask(mStore);
        mDownloadCompanyTask.attachObserver(mDownloadCompanyTaskObserver);
        mDownloadCompanyTask.execute();
    }



    public void retainNonConfigurationInstance() {
        Retainer retainer = new Retainer();
        retainer.downloadCompanyTask = mDownloadCompanyTask;
        getFragmentManager().beginTransaction().add(retainer, RETAINER_FRAGMENT_TAG).commit();
    }



    private void onChangeFarmNo() {
        Intent farmNoIntent = new Intent(this, FarmNoInputActivity.class);
        farmNoIntent.putExtra(FarmNoInputActivity.EXTRA_COUNTRY,getString(R.string.country));
        farmNoIntent.putExtra(FarmNoInputActivity.EXTRA_EAN,mCompany.getFarmNo());
        startActivityForResult(farmNoIntent,REQUEST_CODE_FARMNO);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode != RESULT_OK)
            return;

        if(requestCode == REQUEST_CODE_FARMNO)
        {
            EAN farmNo = (EAN) data.getSerializableExtra(FarmNoInputActivity.EXTRA_EAN);
            mCompany.setFarmNo(farmNo);
            updateBoxes();
        }
    }

    private void updateBoxes() {
        mCompanyName.setText(mCompany.getName());
        mCompanyStreet.setText(mCompany.getStreet());
        mCompanyPOBox.setText(mCompany.getPoBox());
        mCompanyCity.setText(mCompany.getCity());
        mCompanyZip.setText(mCompany.getZip());

        EAN farmNo = mCompany.getFarmNo();
        if (farmNo != null) {
            mFarmNo.setText(farmNo.toString());
        } else {
            mFarmNo.getText().clear();
        }

        mCellPhoneNo.setText(mCompany.getCellPhoneNo());
        mPhoneNo.setText(mCompany.getPhoneNo());
        mEmailAddress.setText(mCompany.getEmailAddress());
        mFiscalNo.setText(mCompany.getFiscalNo());
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_EXTRA_COMPANY,mCompany);
    }

    protected void onPause()
    {
        super.onPause();

        if (mDownloadCompanyTask != null)
            mDownloadCompanyTask.detachObserver();


        if(!isFinishing()) {
            updateBoxes();
            retainNonConfigurationInstance();
        }

        updateCompany();
    }

    private void updateCompany()
    {
        mCompany.setName(mCompanyName.getText().toString());
        mCompany.setStreet(mCompanyStreet.getText().toString());
        mCompany.setPoBox(mCompanyPOBox.getText().toString());
        mCompany.setCity(mCompanyCity.getText().toString());
        mCompany.setZip(mCompanyZip.getText().toString());
        mCompany.setCellPhoneNo(mCellPhoneNo.getText().toString());
        mCompany.setPhoneNo(mPhoneNo.getText().toString());
        mCompany.setEmailAddress(mEmailAddress.getText().toString());
        mCompany.setFiscalNo(mFiscalNo.getText().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cloud_save_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSave:
                updateCompany();
                if (validate()) {
                    save();
                    setResult(RESULT_OK);
                    finish();
                }
                break;
            case R.id.menuDownload:
                downloadCompany();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    private void save() {
        mSettingsStore.saveCompany(mCompany);
    }

    private void displayError(int msgId) {
       new ErrorToast(this).show(msgId);
    }

    private boolean validate() {

        if (mCompanyName.getText().length() == 0) {
            displayError(R.string.errEmptyCompanyName);
            return false;
        }

        if (mCompanyStreet.getText().length() == 0) {
            displayError(R.string.errEmptyCompanyStreet);
            return false;
        }

        if (mCompanyPOBox.getText().length() == 0) {
            displayError(R.string.errEmptyCompanyPOBox);
            return false;
        }

        if (mCompanyCity.getText().length() == 0) {
            displayError(R.string.errEmptyCompanyCity);
            return false;
        }

        if (mCompanyZip.getText().length() == 0) {
            displayError(R.string.errEmptyCompanyZip);
            return false;
        }

        if (mFarmNo.getText().length() == 0) {
            displayError(R.string.errNoCompanyFarmNo);
            return false;
        }

        if (mFiscalNo.getText().length() == 0) {
            displayError(R.string.errNoCompanyFiscalNo);
            return false;
        }

        return true;
    }

}
