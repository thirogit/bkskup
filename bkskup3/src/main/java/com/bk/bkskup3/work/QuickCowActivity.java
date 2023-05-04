package com.bk.bkskup3.work;

import java.util.*;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;

import com.bk.bkskup3.BkActivity;
import com.bk.bkskup3.dao.BkStore;
import com.bk.bkskup3.work.service.DuplicateCowInInvoiceException;
import com.bk.bkskup3.work.service.DuplicateCowInPurchaseException;
import com.bk.bkskup3.work.service.InvoiceService;
import com.bk.widgets.LaserView;
import com.bk.bkskup3.R;
import com.bk.bkskup3.barcode.BarcodeServiceClient;
import com.bk.bkskup3.barcode.BarcodeServiceState;
import com.bk.bkskup3.barcode.ServiceIndicatorButton;
import com.bk.bkskup3.feedback.ErrorMessageFragment;
import com.bk.bkskup3.feedback.ErrorToast;
import com.bk.bkskup3.input.InputPrecisions;
import com.bk.bkskup3.model.*;
import com.bk.bkskup3.tasks.*;
import com.bk.bkskup3.utils.MoneyRounding;
import com.bk.bkskup3.utils.NullUtils;
import com.bk.bkskup3.utils.Numbers;
import com.bk.bkskup3.utils.check.CowNoCheck;
import com.bk.bkskup3.widgets.CowSexButton;
import com.bk.bkskup3.widgets.ToggleListView;
import com.bk.bkskup3.work.input.CowInput;
import com.bk.widgets.actionbar.ActionBar;
import com.google.common.base.Strings;

import javax.inject.Inject;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/26/13
 * Time: 9:34 PM
 */
public class QuickCowActivity extends BkActivity {
    private static final int INPUT_NETPRICEPERKG_REQUEST_CODE = 1001;
    private static final int INPUT_GROSSPRICEPERKG_REQUEST_CODE = 1002;
    private static final int INPUT_WEIGHT_REQUEST_CODE = 1003;

    public static final String EXTRA_COW_INPUT_ID = "cow_input_id";
    public static final String EXTRA_VAT_RATE = "vat_rate";
    public static final int RESULT_FILL = RESULT_FIRST_USER;

    private static final String RETAINER_FRAGMENT_TAG = "retainer";
    private static final String ERRORMSG_FRAGMENT_TAG = "error_fragment";

    private static final String STATE_EXTRA_STATE = "state";
    private static final String STATE_EXTRA_DEPENDENCIES = "dependencies";
    private static final String STATE_EXTRA_VAT_RATE = "vat_rate";
    private static final String STATE_EXTRA_NET_PRICEPERKG = "net_price_per_kg";
    private static final String STATE_EXTRA_WEIGHT = "weight";
    private static final String STATE_EXTRA_COWNOBC = "cow_no_bc";
    private static final String STATE_EXTRA_COWID = "cow_id";
    private static final String STATE_EXTRA_COW_SEX = "cow_sex";
    private static final String STATE_EXTRA_COW_CLASS = "cow_class";
    private static final String STATE_EXTRA_SCROLLX = "stock_scroll_x";
    private static final String STATE_EXTRA_LASER = "laser";

    enum LaserState {
        CowNo_Active,
        None_Active
    }

    enum State {
        LoadingDependencies,
        ShowingError,
        Idle,
    }

    public static class Retainer extends Fragment {
        public Retainer() {
            setRetainInstance(true);
        }

        public LoadDependenciesForCowTask loadDependenciesTask;
    }

    private BarcodeServiceClient mBarcodeClient = new BarcodeServiceClient(this);

    private UUID mCowId;
    private Double mNetPricePerKg;
    private Double mWeight;
    private String mCowNoBC;
    private Double mVatRate;
    private String mClassCd;
    private CowSex mSex;
    private InvoiceService mService;

    @Inject
    BkStore mStore;

    private LaserState mLaserState = LaserState.None_Active;

    private LoadDependenciesForCowTask mLoadDependenciesTask;
    LoadDependenciesForCowTask.Observer mLoadDependenciesTaskObserver = new LoadDependenciesForCowTask.Observer() {
        @Override
        public void onLoadStarted() {
            showLoading();
        }

        @Override
        public void onLoadSuccessful(DependenciesForCow result) {

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

    protected ErrorMessageFragment mErrMsgFragment;
    private DependenciesForCow mDependencies;
    private State mState;

    private View mProgressContainer;
    private View mContentContainer;
    private ToggleListView mClassListToggle;
    private ServiceIndicatorButton mScanCowNoBtn;

    @Override
    public void onStart() {
        super.onStart();
        mBarcodeClient.start();
    }

    private void bindToInvoiceService() {
        if (mService == null) {
            bindService(new Intent(this, InvoiceService.class), mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBarcodeClient.stop();
        if (mService != null) {
            unbindService(mConnection);
            mService = null;
        }
    }

    private void ensureContent() {
        mContentContainer = findViewById(R.id.content_container);
        mProgressContainer = findViewById(R.id.progress_container);

        EditText cowWeightBox = getCowWeightBox();
        cowWeightBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputCowWeight();
            }
        });

        EditText cowNetPricePerKgBox = getCowNetPricePerKgBox();
        cowNetPricePerKgBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputCowNetPrice();
            }
        });

        EditText cowGrossPricePerKgBox = getCowGrossPricePerKgBox();
        cowGrossPricePerKgBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputCowGrossPrice();
            }
        });

        ActionBar actionBar = (ActionBar) findViewById(R.id.actionBar);
        actionBar.setTitle(getTitle());

        actionBar.addAction(new ActionBar.TextAction(R.string.menuFillBtnCaption, new ActionBar.ActionListener() {
            @Override
            public void onAction() {
                finishWith(RESULT_FILL);
            }
        }));
        actionBar.addAction(new ActionBar.TextAction(R.string.menuSaveBtnCaption, new ActionBar.ActionListener() {
            @Override
            public void onAction() {
                save();
            }
        }));
        actionBar.addAction(new ActionBar.TextAction(R.string.cancel, new ActionBar.ActionListener() {
            @Override
            public void onAction() {
                cancel();
            }
        }));

        mClassListToggle = (ToggleListView) findViewById(R.id.classToggleList);

        mScanCowNoBtn = (ServiceIndicatorButton) findViewById(R.id.scanCowNoBtn);
        mScanCowNoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activateCowNoInput();
            }
        });

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quickcow);

        ensureContent();

        mBarcodeClient.attachObserver(new BarcodeServiceClient.BarcodeClientObserver() {
            @Override
            public void onBarcode(String bc) {
            }

            @Override
            public void onState(BarcodeServiceState state) {
                mScanCowNoBtn.setState(state);
            }
        });


        FragmentManager fm = getFragmentManager();
        Retainer retainer = (Retainer) fm.findFragmentByTag(RETAINER_FRAGMENT_TAG);
        if (retainer != null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(retainer);
            ft.commit();

            mLoadDependenciesTask = retainer.loadDependenciesTask;
        }


        if (savedInstanceState == null) {
            Intent intent = getIntent();
            mVatRate = (Double) intent.getSerializableExtra(EXTRA_VAT_RATE);

//            CowInput input = (UUID) intent.getSerializableExtra(EXTRA_COW_INPUT_ID);
//            if (input != null) {
//                mSex = input.getSex();
//                mClassCd = input.getClassCd();
//                mCowId = input.getInputId();
//            }
        } else {

            mDependencies = (DependenciesForCow) savedInstanceState.getSerializable(STATE_EXTRA_DEPENDENCIES);
            mState = (State) savedInstanceState.getSerializable(STATE_EXTRA_STATE);

            mCowNoBC = savedInstanceState.getString(STATE_EXTRA_COWNOBC);
            mVatRate = (Double) savedInstanceState.getSerializable(STATE_EXTRA_VAT_RATE);
            mNetPricePerKg = (Double) savedInstanceState.getSerializable(STATE_EXTRA_NET_PRICEPERKG);
            mWeight = (Double) savedInstanceState.getSerializable(STATE_EXTRA_WEIGHT);
            mSex = (CowSex) savedInstanceState.getSerializable(STATE_EXTRA_COW_SEX);
            mClassCd = savedInstanceState.getString(STATE_EXTRA_COW_CLASS);
            mCowId = (UUID) savedInstanceState.getSerializable(STATE_EXTRA_COWID);
            mLaserState = (LaserState) savedInstanceState.getSerializable(STATE_EXTRA_LASER);


        }

        if (mState == State.Idle) {
            updateLaser();
            initClassesList();
            decideIfGrossEnabled();
            updateBoxes();
            showCow();

        }
    }

    private void showCow() {
        mProgressContainer.setVisibility(View.GONE);
        mContentContainer.setVisibility(View.VISIBLE);
    }


    protected void showLoading() {

        mContentContainer.setVisibility(View.GONE);
        mProgressContainer.setVisibility(View.VISIBLE);
    }

    private void onDependenciesLoaded(DependenciesForCow dependenciesForCow) {
        mDependencies = dependenciesForCow;
        mState = State.Idle;

        decideIfGrossEnabled();
        initClassesList();
        updateBoxes();
        showCow();
        activateCowNoInput();
    }

    private void updateBoxes() {

        updateCowClass();
        updateCowSex();
        updateTotalPriceLbls();
        updateCowNoBC();
    }

    private void initClassesList() {
        Collection<CowClass> classes = mDependencies.getClasses();
        for (CowClass cowClass : classes) {
            mClassListToggle.addItem(cowClass.getClassCode());
        }
    }

    private void updateLaser() {
        if (mLaserState == null || mLaserState == LaserState.CowNo_Active) {
            activateCowNoInput();
        }
    }

    private void finishWith(int resultCode) {
        if (validate()) {

            Intent result = new Intent();
            CowInput cowInput = collectInput();
            try {
                UUID id = mService.addCow(cowInput);
                result.putExtra(EXTRA_COW_INPUT_ID, id);
            } catch (DuplicateCowInInvoiceException e) {
                displayError(R.string.errAddingCausesDuplicateInInvoice);
                return;
            } catch (DuplicateCowInPurchaseException e) {
                displayError(R.string.errAddingCausesDuplicateInPurchase);
                return;
            }

            setResult(resultCode, result);
            finish();
        }
    }

    private void decideIfGrossEnabled() {
        if (mVatRate == null) {
            TextView grossPricePerKgLabel = (TextView) findViewById(R.id.grossPricePerKgLabel);
            grossPricePerKgLabel.setBackgroundColor(Color.LTGRAY);

            TextView grossPriceTotalLabel = (TextView) findViewById(R.id.grossPriceTotalLabel);
            grossPriceTotalLabel.setBackgroundColor(Color.LTGRAY);

            EditText cowGrossPricePerKgBox = getCowGrossPricePerKgBox();
            cowGrossPricePerKgBox.setEnabled(false);

            EditText cowGrossTotalPriceBox = getGrossTotalPrieBox();
            cowGrossTotalPriceBox.setEnabled(false);
        }
    }

    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putString(STATE_EXTRA_COWNOBC, mCowNoBC);
        state.putSerializable(STATE_EXTRA_NET_PRICEPERKG, mNetPricePerKg);
        state.putSerializable(STATE_EXTRA_VAT_RATE, mVatRate);
        state.putSerializable(STATE_EXTRA_WEIGHT, mWeight);
        state.putSerializable(STATE_EXTRA_COW_CLASS, getCowClass());
        state.putSerializable(STATE_EXTRA_COW_SEX, getCowSex());
        state.putSerializable(STATE_EXTRA_COWID, mCowId);
        state.putSerializable(STATE_EXTRA_DEPENDENCIES, mDependencies);

        ToggleListView stockToggleBtn = getClassToggleList();
        state.putInt(STATE_EXTRA_SCROLLX, stockToggleBtn.getScrollX());
        state.putSerializable(STATE_EXTRA_LASER, mLaserState);
        state.putSerializable(STATE_EXTRA_STATE, mState);

    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final int position = savedInstanceState.getInt(STATE_EXTRA_SCROLLX, 0);
        final ToggleListView stockToggleBtn = getClassToggleList();
        if (position > 0) {
            stockToggleBtn.post(new Runnable() {
                public void run() {
                    stockToggleBtn.scrollTo(position, 0);
                }
            });
        }
    }

    private EditText getCowGrossPricePerKgBox() {
        return (EditText) findViewById(R.id.cowGrossPricePerKgBox);
    }

    private EditText getGrossTotalPrieBox() {
        return (EditText) findViewById(R.id.cowGrossTotalPriceBox);
    }

    private EditText getCowNetPricePerKgBox() {
        return (EditText) findViewById(R.id.cowNetPricePerKgBox);
    }

    private EditText getCowWeightBox() {
        return (EditText) findViewById(R.id.cowWeightBox);
    }

    private void inputCowWeight() {
        Intent inputWeightIntent = new Intent(this, WeightInputActivity.class);
//        inputWeightIntent.putExtra(WeightInputActivity.EXTRA_PRECISION, InputPrecisions.WEIGHT_PRECISION);
        if (mWeight != null) {
            inputWeightIntent.putExtra(WeightInputActivity.EXTRA_WEIGHT, mWeight);
        }
        startActivityForResult(inputWeightIntent, INPUT_WEIGHT_REQUEST_CODE);
    }

    private void inputCowNetPrice() {
        Intent inputNetPriceIntent = new Intent(this, DecimalInputActivity.class);
        inputNetPriceIntent.putExtra(DecimalInputActivity.EXTRA_PRECISION, InputPrecisions.PRICE_PER_KG_PRECISION);
        if (mNetPricePerKg != null) {
            inputNetPriceIntent.putExtra(DecimalInputActivity.EXTRA_VALUE, mNetPricePerKg);
        }

        inputNetPriceIntent.putExtra(DecimalInputActivity.EXTRA_TITLE, getString(R.string.inputNetPricePerKgTitle));
        startActivityForResult(inputNetPriceIntent, INPUT_NETPRICEPERKG_REQUEST_CODE);
    }


    private void inputCowGrossPrice() {
        Intent inputGrossPriceIntent = new Intent(this, DecimalInputActivity.class);
        inputGrossPriceIntent.putExtra(DecimalInputActivity.EXTRA_PRECISION, InputPrecisions.PRICE_PER_KG_PRECISION);

        Double grossPricePerKg = getGrossPricePerKg();
        if (grossPricePerKg != null) {
            inputGrossPriceIntent.putExtra(DecimalInputActivity.EXTRA_VALUE, grossPricePerKg);
        }
        inputGrossPriceIntent.putExtra(DecimalInputActivity.EXTRA_TITLE, getString(R.string.inputGrossPricePerKgTitle));
        startActivityForResult(inputGrossPriceIntent, INPUT_GROSSPRICEPERKG_REQUEST_CODE);
    }

    private BarcodeServiceClient.BarcodeClientObserver mCowNoObserver = new BarcodeServiceClient.BarcodeClientObserver() {
        @Override
        public void onBarcode(String bc) {
            onCowNoBC(bc);
        }

        @Override
        public void onState(BarcodeServiceState state) {
            getCowNoLaser().setVisibility(state == BarcodeServiceState.ScannerConnected ? View.VISIBLE : View.GONE);
        }

    };

    private void updateCowNoBC() {
        TextView cowNoBox = (TextView) findViewById(R.id.cowNoView);
        cowNoBox.setText(Strings.nullToEmpty(mCowNoBC));

        if (mCowNoBC != null) {
            EAN cowNo = EAN.fromString(mCowNoBC);
            if (cowNo == null) {
                showInvalidCowNoFeedback();
            } else if (!CowNoCheck.isValid(cowNo)) {
                askInvalidCowNoChksumDigit();
            }
        } else {
            hideCowNoInputFeedbackPlaceholder();
        }
    }

    private void onCowNoBC(String cowNoBC) {
        mCowNoBC = cowNoBC;
        TextView cowNoBox = (TextView) findViewById(R.id.cowNoView);
        cowNoBox.setText(mCowNoBC);

        EAN cowNo = EAN.fromString(cowNoBC);
        if (cowNo == null) {
            showInvalidCowNoFeedback();
        } else if (!CowNoCheck.isValid(cowNo)) {
            askInvalidCowNoChksumDigit();
        } else {
            hideCowNoInputFeedbackPlaceholder();
            deactivateCowNoInput();
        }
    }

    private void askInvalidCowNoChksumDigit() {
        ViewGroup placeholder = getCowNoInputFeedbackPlaceholder();
        placeholder.addView(constructAskYesFeedBack(getString(R.string.askOverrideBadChkSum), new Runnable() {
            @Override
            public void run() {
                hideCowNoInputFeedbackPlaceholder();
            }
        }));
        showCowNoInputFeedbackPlaceholder();
    }

    private void showInvalidCowNoFeedback() {
        ViewGroup placeholder = getCowNoInputFeedbackPlaceholder();
        placeholder.addView(constructErrorFeedback(getString(R.string.errNotACowNo)));
        showCowNoInputFeedbackPlaceholder();
    }

    private void showCowNoInputFeedbackPlaceholder() {
        getCowNoInputFeedbackPlaceholder().setVisibility(View.VISIBLE);
    }

    private void hideCowNoInputFeedbackPlaceholder() {
        ViewGroup placeholder = getCowNoInputFeedbackPlaceholder();
        placeholder.removeAllViews();
        placeholder.setVisibility(View.GONE);
    }

    private ViewGroup getCowNoInputFeedbackPlaceholder() {
        return (ViewGroup) findViewById(R.id.cowNoInputFeedbackPlaceholder);
    }

    protected View constructAskYesFeedBack(String question, final Runnable onYes) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View askYesFeedbackView = inflater.inflate(R.layout.askyesfeedback, null);
        TextView feedbackMsg = (TextView) askYesFeedbackView.findViewById(R.id.feedbackMsg);
        feedbackMsg.setText(question);
        Button yesBtn = (Button) askYesFeedbackView.findViewById(R.id.yesBtn);
        yesBtn.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onYes.run();
            }
        });

        return askYesFeedbackView;
    }

    protected View constructFeedback(String feedbackMsg) {
        TextView errorMsgTxtView = new TextView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        errorMsgTxtView.setLayoutParams(layoutParams);
        errorMsgTxtView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        errorMsgTxtView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

        errorMsgTxtView.setBackgroundColor(Color.rgb(128, 0, 255));
        errorMsgTxtView.setText(feedbackMsg);
        errorMsgTxtView.setTextColor(Color.WHITE);
        return errorMsgTxtView;
    }

    protected View constructErrorFeedback(String errorMsg) {
        TextView errorMsgTxtView = new TextView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        errorMsgTxtView.setLayoutParams(layoutParams);
        errorMsgTxtView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        errorMsgTxtView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

        errorMsgTxtView.setBackgroundColor(Color.RED);
        errorMsgTxtView.setText(errorMsg);
        errorMsgTxtView.setTextColor(Color.WHITE);
        return errorMsgTxtView;
    }

    private LaserView getCowNoLaser() {
        return (LaserView) findViewById(R.id.cowNoLaser);
    }

    public void activateCowNoInput() {
        mBarcodeClient.attachObserver(mCowNoObserver);

        if (mBarcodeClient.getState() == BarcodeServiceState.ScannerConnected) {
            getCowNoLaser().setVisibility(View.VISIBLE);
        }
        mLaserState = LaserState.CowNo_Active;
    }

    private void deactivateCowNoInput() {
        mBarcodeClient.removeObserver(mCowNoObserver);
        getCowNoLaser().setVisibility(View.INVISIBLE);
        if (mLaserState == LaserState.CowNo_Active) {
            mLaserState = LaserState.None_Active;
        }
    }

    private boolean validate() {
        if (!(NullUtils.valueForNull(mNetPricePerKg, 0.0) >= 0.01)) {
            displayError(R.string.errInvalidCowPrice);
            return false;
        }

        if (!(NullUtils.valueForNull(mWeight, 0.0) >= 0.001)) {
            displayError(R.string.errInvalidCowWeight);
            return false;
        }

        if (mCowNoBC == null || EAN.fromString(mCowNoBC) == null) {
            displayError(R.string.askToInputValidCowNo);
            return false;
        }

        if (getCowClass() == null) {
            displayError(R.string.errEmptyClass);
            return false;
        }

        if (getCowSex() == null) {
            displayError(R.string.errEmptyCowSex);
            return false;
        }

        return true;
    }

    private void displayError(int msgId) {
        new ErrorToast(this).show(msgId);
    }

    private CowInput collectInput() {
        CowInput input = new CowInput(mCowId);
        input.setCowNo(EAN.fromString(mCowNoBC));
        CowSex cowSex = getCowSex();
        if (cowSex == null) {
            cowSex = CowSex.NONE;
        }
        input.setSex(cowSex);
        input.setClassCd(getCowClass());
        input.setNetPrice(mWeight * mNetPricePerKg);
        input.setWeight(mWeight);
        return input;
    }

    private void save() {
        finishWith(RESULT_OK);
    }

    private void cancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private void updateNetPricePerKg() {

        EditText cowNetPricePerKgBox = getCowNetPricePerKgBox();
        EditText cowGrossPricePerKgBox = getCowGrossPricePerKgBox();

        String netPricePerKgStr = "";
        String grossPricePerKg = "";

        if (mNetPricePerKg != null && mNetPricePerKg >= 0.0) {
            netPricePerKgStr = Numbers.formatPrice(mNetPricePerKg);
            grossPricePerKg = Numbers.formatPrice(mNetPricePerKg * (1.0 + getSafeVatRate()));
        }

        cowNetPricePerKgBox.setText(netPricePerKgStr);
        if (mVatRate != null) {
            cowGrossPricePerKgBox.setText(grossPricePerKg);
        }
        updateTotalPriceLbls();
    }

    private void updateTotalPriceLbls() {
        EditText cowTotalGrossPriceLbl = (EditText) findViewById(R.id.cowGrossTotalPriceBox);
        TextView cowTotalNetPriceLbl = (TextView) findViewById(R.id.cowNetTotalPriceBox);

        String totalNetPriceStr = "";
        String totalGrossPriceStr = "";

        if (mNetPricePerKg != null && mWeight != null) {
            double totalNetPrice = mNetPricePerKg * mWeight;
            double totalGrossPrice = totalNetPrice + totalNetPrice * getSafeVatRate();

            totalNetPriceStr = Numbers.formatPrice(totalNetPrice);
            totalGrossPriceStr = Numbers.formatPrice(totalGrossPrice);

        }
        if (mVatRate != null) {
            cowTotalGrossPriceLbl.setText(totalGrossPriceStr);
        }
        cowTotalNetPriceLbl.setText(totalNetPriceStr);
    }

    private double getSafeVatRate() {
        return NullUtils.valueForNull(mVatRate, 0).doubleValue();
    }

    private Double getGrossPricePerKg() {
        if (mNetPricePerKg != null) {
            return mNetPricePerKg * (1.0 + getSafeVatRate());
        }
        return null;
    }


    private void updateWeight() {
        EditText cowWeightBox = getCowWeightBox();

        String weightStr = "";
        if (mWeight != null && mWeight >= 0.0) {
            weightStr = Numbers.formatWeight(mWeight);
        }
        cowWeightBox.setText(weightStr);
        updateTotalPriceLbls();
    }

    private void loadDependencies() {
        mState = State.LoadingDependencies;
        mLoadDependenciesTask = new LoadDependenciesForCowTask(mStore);
        mLoadDependenciesTask.attachObserver(mLoadDependenciesTaskObserver);
        mLoadDependenciesTask.execute();
    }

    protected void onResume() {
        super.onResume();
        bindToInvoiceService();
    }

    private void continueResume()
    {
        if (mState == null) {
            showLoading();
            loadDependencies();
            return;
        }

        if (mState == State.LoadingDependencies) {
            if (mLoadDependenciesTask != null) {

                if (mLoadDependenciesTask.getStatus() == AsyncTask.Status.FINISHED) {
                    TaskResult<DependenciesForCow> result = mLoadDependenciesTask.getResult();
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        switch (requestCode) {
            case INPUT_NETPRICEPERKG_REQUEST_CODE: {
                if (resultCode == RESULT_CANCELED) { break; }
                mNetPricePerKg = MoneyRounding.round(data.getDoubleExtra(DecimalInputActivity.EXTRA_VALUE, 0.0));
                updateNetPricePerKg();
                break;
            }
            case INPUT_GROSSPRICEPERKG_REQUEST_CODE: {
                if (resultCode == RESULT_CANCELED) { break; }
                double grossPricePerKg = data.getDoubleExtra(DecimalInputActivity.EXTRA_VALUE, 0.0);
                mNetPricePerKg = MoneyRounding.round(grossPricePerKg / (1 + getSafeVatRate()));
                updateNetPricePerKg();
                break;
            }
            case INPUT_WEIGHT_REQUEST_CODE: {
                if (resultCode == RESULT_CANCELED) { break; }
                mWeight = data.getDoubleExtra(WeightInputActivity.EXTRA_WEIGHT, 0);
                updateWeight();
                break;
            }
        }
    }

    private void updateCowSex() {
        getCowSexBtn().setCowSex(mSex);
    }

    private CowSexButton getCowSexBtn() {
        return (CowSexButton) findViewById(R.id.sexBtn);
    }

    private CowSex getCowSex() {
        return getCowSexBtn().getCowSex();
    }

    private ToggleListView getClassToggleList() {
        return (ToggleListView) findViewById(R.id.classToggleList);
    }

    private String getCowClass() {
        return getClassToggleList().getToggledItem();
    }

    private void updateCowClass() {
        getClassToggleList().toggleItem(mClassCd);
    }

    private void showError(String error) {
        mErrMsgFragment = new ErrorMessageFragment();
        mErrMsgFragment.setCaption(getResources().getString(R.string.errorCaption));
        mErrMsgFragment.setErrorMsg(error);
        mErrMsgFragment.setListener(mOnErrorHideListener);
        mErrMsgFragment.show(getFragmentManager(), ERRORMSG_FRAGMENT_TAG);
    }

    public void retainNonConfigurationInstance() {
        Retainer retainer = new Retainer();
        retainer.loadDependenciesTask = mLoadDependenciesTask;
        getFragmentManager().beginTransaction().add(retainer, RETAINER_FRAGMENT_TAG).commit();
    }

    protected void onPause() {
        super.onPause();

        if (mLoadDependenciesTask != null)
            mLoadDependenciesTask.detachObserver();

        retainNonConfigurationInstance();

    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            InvoiceService.LocalBinder binder = (InvoiceService.LocalBinder) service;
            mService = binder.getService();

            continueResume();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
        }
    };


}
