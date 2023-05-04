package com.bk.bkskup3.work;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bk.bkskup3.BkActivity;
import com.bk.bkskup3.R;
import com.bk.bkskup3.barcode.BarcodeServiceClient;
import com.bk.bkskup3.barcode.BarcodeServiceState;
import com.bk.bkskup3.barcode.ServiceIndicatorButton;
import com.bk.bkskup3.dao.BkStore;
import com.bk.bkskup3.feedback.ErrorMessageFragment;
import com.bk.bkskup3.feedback.ErrorToast;
import com.bk.bkskup3.input.BKDatePickerDialog;
import com.bk.bkskup3.input.InputPrecisions;
import com.bk.bkskup3.input.PassportNoInputFilter;
import com.bk.bkskup3.model.*;
import com.bk.bkskup3.utils.Dates;
import com.bk.bkskup3.utils.MoneyRounding;
import com.bk.bkskup3.utils.NullUtils;
import com.bk.bkskup3.utils.Numbers;
import com.bk.bkskup3.widgets.CowSexButton;
import com.bk.bkskup3.widgets.ToggleListView;
import com.bk.bkskup3.work.input.CowInput;
import com.bk.bkskup3.tasks.LoadDependenciesForCowTask;
import com.bk.bkskup3.tasks.TaskResult;
import com.bk.bkskup3.work.service.DuplicateCowInInvoiceException;
import com.bk.bkskup3.work.service.DuplicateCowInPurchaseException;
import com.bk.bkskup3.work.service.InvoiceService;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

import javax.annotation.Nullable;
import javax.inject.Inject;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 1/2/12
 * Time: 10:46 AM
 */
public abstract class CowActivity extends BkActivity {
    public static final String EXTRA_COW_INPUT_ID = "cow_input_id";

    private static final String STATE_EXTRA_COWINPUT = "cow_input";
    private static final String STATE_EXTRA_NETPRICEPERKG = "net_price_per_kg";
    private static final String STATE_EXTRA_STATE = "state";
    private static final String STATE_EXTRA_DEPENDENCIES = "dependencies";


    //    private static final int NEW_FIRSTOWNER_REQUEST_CODE = 1001;
//    private static final int EDIT_FIRSTOWNER_REQUEST_CODE = 1002;
//    private static final int FIND_FIRSTOWNER_REQUEST_CODE = 1003;
    private static final int CHANGE_COWNO_REQUEST_CODE = 1004;
    private static final int CHANGE_MOTHERNO_REQUEST_CODE = 1005;
    private static final int INPUT_NETPRICEPERKG_REQUEST_CODE = 1006;
    private static final int INPUT_GROSSPRICEPERKG_REQUEST_CODE = 1007;
    private static final int INPUT_WEIGHT_REQUEST_CODE = 1008;
    private static final int SCAN_COWNO_REQUEST_CODE = 1009;
//    private static final int SCAN_FIRSTOWNER_REQUEST_CODE = 1010;

    private static final String RETAINER_FRAGMENT_TAG = "retainer";
    private static final String ERRORMSG_FRAGMENT_TAG = "error_fragment";

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


    private Double mVatRate;
    private InvoiceHentObj mInvoiceHent;
    private CowInput mInput;
    private UUID mCowInputId;
    private Double mNetPricePerKg;
    private BarcodeServiceClient mServiceClient;
    private State mState;
    private LoadDependenciesForCowTask mLoadDependenciesTask;
    private DependenciesForCow mDependencies;

    private View mProgressContainer;
    private View mContentContainer;
    protected ErrorMessageFragment mErrMsgFragment;

    InvoiceService mService;

    @Inject
    BkStore mStore;


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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.cow);
        ensureContent();

        Intent intent = getIntent();

        FragmentManager fm = getFragmentManager();
        Retainer retainer = (Retainer) fm.findFragmentByTag(RETAINER_FRAGMENT_TAG);
        if (retainer != null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(retainer);
            ft.commit();

            mLoadDependenciesTask = retainer.loadDependenciesTask;
        }

        if (savedInstanceState == null) {
            mCowInputId = (UUID) intent.getSerializableExtra(EXTRA_COW_INPUT_ID);
        } else {
            mInput = (CowInput) savedInstanceState.getSerializable(STATE_EXTRA_COWINPUT);
            mCowInputId = mInput.getInputId();
            mNetPricePerKg = (Double) savedInstanceState.getSerializable(STATE_EXTRA_NETPRICEPERKG);
            mDependencies = (DependenciesForCow) savedInstanceState.getSerializable(STATE_EXTRA_DEPENDENCIES);
            mState = (State) savedInstanceState.getSerializable(STATE_EXTRA_STATE);
        }

        if (mState == State.Idle) {
            initClassesList();
            initStocksList();
            updateBoxesFromCow();
        }

        final ServiceIndicatorButton scanCowNoBtn = (ServiceIndicatorButton) findViewById(R.id.scanCowNoBtn);
        scanCowNoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onScanCowNo();
            }
        });

//        final ServiceIndicatorButton scanFirstOwnerNoBtn = (ServiceIndicatorButton) findViewById(R.id.scanFirstOwnerNoBtn);

//        scanFirstOwnerNoBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onScanFirstOwner();
//            }
//        });

        mServiceClient = new BarcodeServiceClient(this);
        mServiceClient.attachObserver(new BarcodeServiceClient.BarcodeClientObserver() {
            @Override
            public void onBarcode(String bc) {
            }

            @Override
            public void onState(BarcodeServiceState state) {
                scanCowNoBtn.setState(state);
//                scanFirstOwnerNoBtn.setState(state);
            }

        });
    }

    private void ensureContent() {
        mContentContainer = findViewById(R.id.content_container);
        mProgressContainer = findViewById(R.id.progress_container);

//        Button editFstOwnerBtn = getEditFstOwnerBtn();
//        editFstOwnerBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onEditFirstOwner();
//            }
//        });

//        Button addFirstOwnerBtn = getAddFirstOwnerBtn();
//        addFirstOwnerBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onAddNewFirstOwner();
//            }
//        });

        getCowPassportIssueDtBox().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChangePassportIssueDt();
            }
        });

        getMotherNoBox().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMotherEAN();
            }
        });

        getCowBirthDtBox().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSetBirthDt();
            }
        });

        getCowPassportNoBox().setFilters(new InputFilter[]{new PassportNoInputFilter()});

        getCowWeightBox().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doWeightInput();
            }
        });
        getCowNetPricePerKgBox().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doNetPricePerKgInput();
            }
        });
        getCowGrossPricePerKgBox().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doGrossPricePerKgInput();
            }
        });

        EditText cowNoBox = getCowNoBox();
        cowNoBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doCowNoInput();
            }
        });

//        getEditFstOwnerBtn().setEnabled(false);
//        getFirstOwnerBox().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onChooseFirstOwner();
//            }
//        });

//        Button copyFistOwnerBtn = getCopyFistOwnerBtn();
//        copyFistOwnerBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onCopyFirstOwner();
//            }
//        });

        Button copyBirthPlaceFromnvoiceHentBtn = getCopyBirthPlaceBtn();
        copyBirthPlaceFromnvoiceHentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCopyBirthPlace();
            }
        });
    }

//    private Button getCopyFistOwnerBtn() {
//        return (Button) findViewById(R.id.copyFistOwnerFromInvoiceHentBtn);
//    }

    private Button getCopyBirthPlaceBtn() {
        return (Button) findViewById(R.id.copyBirthPlaceFromInvoiceHentBtn);
    }

    private void decideIfCopyBtnsAreEnabled() {
        boolean copyFromInvoiceHentEnabled = mInvoiceHent != null;
//        getCopyFistOwnerBtn().setEnabled(copyFromInvoiceHentEnabled);
        getCopyBirthPlaceBtn().setEnabled(copyFromInvoiceHentEnabled);
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
        updateBoxesFromCow();
        initStocksList();
        initClassesList();
        showCow();
    }

    private ToggleListView getClassesList() {
        return (ToggleListView) findViewById(R.id.cowClasses);
    }

    private ToggleListView getStocksList() {
        return (ToggleListView) findViewById(R.id.cowStocks);
    }


    private void initClassesList() {

        Collection<CowClass> classes = mDependencies.getClasses();
        ToggleListView classesList = getClassesList();
        classesList.clearItems();

        if (!Strings.isNullOrEmpty(mInput.getClassCd())) {
            CowClass cowClass = Iterables.find(classes, new Predicate<CowClass>() {
                @Override
                public boolean apply(@Nullable CowClass cowClass) {
                    return mInput.getClassCd().equals(cowClass.getClassCode());
                }
            }, null);

            if (cowClass == null) {
                classesList.addItem(mInput.getClassCd());
            }
        }

        for (CowClass cowClass : classes) {
            classesList.addItem(cowClass.getClassCode());
        }

        classesList.toggleItem(mInput.getClassCd());
    }

    private void initStocksList() {
        Collection<Stock> stocks = mDependencies.getStocks();
        ToggleListView stocksList = getStocksList();
        stocksList.clearItems();

        if (!Strings.isNullOrEmpty(mInput.getStockCd())) {
            Stock stock = Iterables.find(stocks, new Predicate<Stock>() {
                @Override
                public boolean apply(@Nullable Stock stock) {
                    return mInput.getStockCd().equals(stock.getStockCode());
                }
            }, null);

            if (stock == null) {
                stocksList.addItem(mInput.getStockCd());
            }
        }

        for (Stock stock : stocks) {
            stocksList.addItem(stock.getStockCode());
        }

        stocksList.toggleItem(mInput.getStockCd());
    }


    protected void onResume() {
        super.onResume();

        if (mService == null) {
            showLoading();
            bindService(new Intent(this, InvoiceService.class), mConnection, Context.BIND_AUTO_CREATE);
        } else {
            continueResume();
        }

    }

    private void continueResume() {

        if (mState == State.Idle) {
            showCow();
            return;
        }

        if (mState == null) {

            if (mCowInputId == null) {
                mInput = new CowInput();
            } else {
                mInput = mService.getCow(mCowInputId);
            }

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

    private void loadDependencies() {
        mState = State.LoadingDependencies;
        mLoadDependenciesTask = new LoadDependenciesForCowTask(mStore);
        mLoadDependenciesTask.attachObserver(mLoadDependenciesTaskObserver);
        mLoadDependenciesTask.execute();
    }

    protected void onPause() {
        super.onPause();

        if (mLoadDependenciesTask != null)
            mLoadDependenciesTask.detachObserver();


        if (!isFinishing()) {
            updateCowFromBoxes();
            retainNonConfigurationInstance();
        }


    }


    public void retainNonConfigurationInstance() {
        Retainer retainer = new Retainer();
        retainer.loadDependenciesTask = mLoadDependenciesTask;
        getFragmentManager().beginTransaction().add(retainer, RETAINER_FRAGMENT_TAG).commit();
    }


//    private void onScanFirstOwner() {
//        startActivityForResult(new Intent(this, ScanHentActivity.class), SCAN_FIRSTOWNER_REQUEST_CODE);
//    }


    protected void onStart() {
        super.onStart();
        mServiceClient.start();
    }

    protected void onStop() {
        super.onStop();
        mServiceClient.stop();
        if (mService != null) {
            unbindService(mConnection);
            mService = null;
        }
    }

    protected void onSaveInstanceState(android.os.Bundle state) {
        super.onSaveInstanceState(state);
        state.putSerializable(STATE_EXTRA_COWINPUT, mInput);
        state.putSerializable(STATE_EXTRA_NETPRICEPERKG, mNetPricePerKg);
        state.putSerializable(STATE_EXTRA_STATE, mState);
        state.putSerializable(STATE_EXTRA_DEPENDENCIES, mDependencies);

    }

    private void doCowNoInput() {
        Intent changeCowNoIntent = new Intent(this, CowNoInputActivity.class);
        EAN cowNo = mInput.getCowNo();

        if (cowNo != null) {
            changeCowNoIntent.putExtra(CowNoInputActivity.EXTRA_EAN, cowNo);
        } else {
            changeCowNoIntent.putExtra(CowNoInputActivity.EXTRA_COUNTRY, getDefaultCountryForCow());
        }

        startActivityForResult(changeCowNoIntent, CHANGE_COWNO_REQUEST_CODE);
    }

    private void doGrossPricePerKgInput() {
        Intent inputGrossPriceIntent = new Intent(this, DecimalInputActivity.class);
        inputGrossPriceIntent.putExtra(DecimalInputActivity.EXTRA_PRECISION, InputPrecisions.PRICE_PER_KG_PRECISION);
        Double grossPricePerKg = getGrossPricePerKg();
        if (grossPricePerKg != null) {
            inputGrossPriceIntent.putExtra(DecimalInputActivity.EXTRA_VALUE, grossPricePerKg);
        }
        inputGrossPriceIntent.putExtra(DecimalInputActivity.EXTRA_TITLE, getString(R.string.inputGrossPricePerKgTitle));
        startActivityForResult(inputGrossPriceIntent, INPUT_GROSSPRICEPERKG_REQUEST_CODE);
    }

    private void doNetPricePerKgInput() {
        Intent inputNetPriceIntent = new Intent(this, DecimalInputActivity.class);
        inputNetPriceIntent.putExtra(DecimalInputActivity.EXTRA_PRECISION, InputPrecisions.PRICE_PER_KG_PRECISION);
        Double netPricePerKg = mNetPricePerKg;
        if (netPricePerKg != null) {
            inputNetPriceIntent.putExtra(DecimalInputActivity.EXTRA_VALUE, netPricePerKg);
        }

        inputNetPriceIntent.putExtra(DecimalInputActivity.EXTRA_TITLE, getString(R.string.inputNetPricePerKgTitle));
        startActivityForResult(inputNetPriceIntent, INPUT_NETPRICEPERKG_REQUEST_CODE);
    }

    private void doWeightInput() {
        Intent inputWeightIntent = new Intent(this, WeightInputActivity.class);
        // NullUtils.valueForNull(mDependencies.getInputDefaults().getWeightPrecision(),InputPrecisions.WEIGHT_PRECISION));
        Double weight = mInput.getWeight();
        if (weight != null) {
            inputWeightIntent.putExtra(WeightInputActivity.EXTRA_WEIGHT, weight);
        }
        startActivityForResult(inputWeightIntent, INPUT_WEIGHT_REQUEST_CODE);
    }

    protected void updateBoxesFromCow() {
        updateCowNo();
        updateCowSex();
        updateStock();
        updateCowClass();
//        updateFirstOwner();
        updatePassportNo();

        Double netPricePerKg = null;
        Double netPrice = mInput.getNetPrice();
        Double weight = mInput.getWeight();

        if (netPrice != null && weight != null) {
            netPricePerKg = netPrice / weight;
        }
        setNetPricePerKg(netPricePerKg);
        updateWeight();
        updatePassportNo();
        updatePassportIssueDt();
        updateHealthCertNo();
        updateMotherNo();
        updateBirthPlace();
        updateBirthDt();

    }

    private void updateStock() {
        ToggleListView stocksList = getStocksList();
        stocksList.toggleItem(mInput.getStockCd());
    }

    private void updateCowClass() {
        ToggleListView classesList = getClassesList();
        classesList.toggleItem(mInput.getClassCd());
    }

    private void updateBirthPlace() {
        EditText cowBirthPlaceBox = getCowBirthPlaceBox();
        cowBirthPlaceBox.setText(mInput.getBirthPlace());
    }

    private void updateHealthCertNo() {
        EditText cowHealthCertNoBox = getCowHealthCertNoxBox();
        cowHealthCertNoBox.setText(mInput.getHealthCertNo());
    }

    private void updateCowSex() {
        getCowSexBtn().setCowSex(mInput.getSex());
    }

    private void updatePassportNo() {
        EditText cowPassportNoBox = getCowPassportNoBox();
        cowPassportNoBox.setText(mInput.getPassportNo());
    }

    private Double getNetPricePerKg() {
        return mNetPricePerKg;
    }

    private void setNetPricePerKg(Double netPricePerKg) {
        mNetPricePerKg = netPricePerKg;
        EditText cowNetPricePerKgBox = getCowNetPricePerKgBox();
        EditText cowGrossPricePerKgBox = getCowGrossPricePerKgBox();

        String netPricePerKgStr = "";
        String grossPricePerKg = "";

        if (netPricePerKg != null && netPricePerKg >= 0.0) {
            netPricePerKgStr = Numbers.formatPrice(netPricePerKg);
            grossPricePerKg = Numbers.formatPrice(netPricePerKg + netPricePerKg * getSafeVatRate());
        }

        cowNetPricePerKgBox.setText(netPricePerKgStr);
        if (mVatRate != null) {
            cowGrossPricePerKgBox.setText(grossPricePerKg);
        }
        updateTotalPriceLbls();

    }

    private EditText getCowTotalGrossPrice() {
        return (EditText) findViewById(R.id.cowTotalGrossPriceBox);
    }

    private EditText getCowTotalNetPriceBox() {
        return (EditText) findViewById(R.id.cowTotalNetPriceBox);
    }

    private void updateTotalPriceLbls() {
        EditText cowTotalGrossPriceBox = getCowTotalGrossPrice();
        EditText cowTotalNetPriceBox = getCowTotalNetPriceBox();

        String totalNetPriceStr = "";
        String totalGrossPriceStr = "";

        Double netPricePerKg = mNetPricePerKg;
        Double weight = mInput.getWeight();

        if (netPricePerKg != null && weight != null) {
            double totalNetPrice = netPricePerKg * weight;
            double totalGrossPrice = totalNetPrice + totalNetPrice * getSafeVatRate();

            totalNetPriceStr = Numbers.formatPrice(totalNetPrice);
            totalGrossPriceStr = Numbers.formatPrice(totalGrossPrice);

        }
        if (mVatRate != null) {
            cowTotalGrossPriceBox.setText(totalGrossPriceStr);
        }
        cowTotalNetPriceBox.setText(totalNetPriceStr);

    }

    private double getSafeVatRate() {
        return NullUtils.valueForNull(mVatRate, 0.0);
    }

    private Double getGrossPricePerKg() {
        Double netPricePerKg = mNetPricePerKg;
        if (netPricePerKg != null) {
            return netPricePerKg + netPricePerKg * getSafeVatRate();
        }
        return null;
    }

    private void updateWeight() {
        Double weight = mInput.getWeight();
        EditText cowWeightBox = getCowWeightBox();

        String weightStr = "";
        if (weight != null && weight >= 0.0) {
            weightStr = Numbers.formatWeight(weight);
        }
        cowWeightBox.setText(weightStr);
        updateTotalPriceLbls();
    }

    private String getDefaultCountryForCow() {
        return mDependencies.getInputDefaults().getDefaultCountry();
    }

    protected void onMotherEAN() {
        Intent changeMotherNoIntent = new Intent(this, CowNoInputActivity.class);
        EAN motherNo = mInput.getMotherNo();

        if (motherNo != null) {
            changeMotherNoIntent.putExtra(CowNoInputActivity.EXTRA_EAN, motherNo);
        } else {
            changeMotherNoIntent.putExtra(CowNoInputActivity.EXTRA_COUNTRY, getDefaultCountryForCow());
        }

        startActivityForResult(changeMotherNoIntent, CHANGE_MOTHERNO_REQUEST_CODE);

    }

//    protected void updateFirstOwner() {
//        Hent firstOwner = mInput.getFirstOwner();
//        getEditFstOwnerBtn().setEnabled(firstOwner != null);
//
//        String firstOwnerStr = "";
//        if (firstOwner != null) {
//            firstOwnerStr = firstOwner.getAlias();
//
//            if (Strings.isNullOrEmpty(firstOwnerStr)) {
//                firstOwnerStr = firstOwner.getHentName();
//            }
//        }
//        getFirstOwnerBox().setText(firstOwnerStr);
//    }

    public void updatePassportIssueDt() {
        Date cowPassportIssueDt = mInput.getPassportIssueDt();

        String cowPassportIssueDtStr = "";
        if (cowPassportIssueDt != null) {
            cowPassportIssueDtStr = Dates.toDayDate(cowPassportIssueDt);
        }
        getCowPassportIssueDtBox().setText(cowPassportIssueDtStr);
    }

    private void onChangePassportIssueDt() {
        Date passportIssueDt = mInput.getPassportIssueDt();
        if (passportIssueDt == null) {

            Date birthDt = mInput.getBirthDt();
            if (birthDt != null) {
                passportIssueDt = Dates.plusDays(birthDt, 14);
            } else {

                passportIssueDt = Calendar.getInstance().getTime();
            }
        }

        BKDatePickerDialog seasonStrDtPickerDlg = new BKDatePickerDialog(this, passportIssueDt,
                new BKDatePickerDialog.OnDateListener() {
                    public void onDate(Date dt) {
                        mInput.setPassportIssueDt(dt);
                        updatePassportIssueDt();
                    }
                });

        seasonStrDtPickerDlg.show();
    }

    public void updateCowNo() {
        EAN cowNo = mInput.getCowNo();
        String cowNoStr = "";
        if (cowNo != null) {
            cowNoStr = cowNo.toString();
        }
        getCowNoBox().setText(cowNoStr);
    }


//    private void onChooseFirstOwner() {
//        Intent findHentIntent = new Intent(this, FindHentActivity.class);
//        startActivityForResult(findHentIntent, FIND_FIRSTOWNER_REQUEST_CODE);
//    }
//
//    private void onAddNewFirstOwner() {
//        Intent newHentIntent = new Intent(this, NewHentActivity.class);
//        startActivityForResult(newHentIntent, NEW_FIRSTOWNER_REQUEST_CODE);
//    }
//
//    private void onEditFirstOwner() {
//        Hent fistOwner = mInput.getFirstOwner();
//        if (fistOwner != null) {
////            Intent editHentIntent = new Intent(this, EditHentActivity.class);
////            editHentIntent.putExtra(EditHentActivity.EXTRA_HENT_TO_EDIT, fistOwner.getId());
////            startActivityForResult(editHentIntent, EDIT_FIRSTOWNER_REQUEST_CODE);
//        }
//    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            return;
        }

        switch (requestCode) {
//            case NEW_FIRSTOWNER_REQUEST_CODE:
//                HentObj newHent = (HentObj) data.getSerializableExtra(NewHentActivity.EXTRA_SAVED_HENT);
//                if (newHent != null) {
//                    mInput.setFirstOwner(newHent);
//                    updateFirstOwner();
//                }
//                break;
//
//            case EDIT_FIRSTOWNER_REQUEST_CODE:
//                HentObj editedHent = (HentObj) data.getSerializableExtra(EditHentActivity.EXTRA_SAVED_HENT);
//                if (editedHent != null) {
//                    mInput.setFirstOwner(editedHent);
//                    updateFirstOwner();
//                }
//                break;
//
//            case FIND_FIRSTOWNER_REQUEST_CODE:
//                HentObj foundHent = (HentObj) data.getSerializableExtra(FindHentActivity.EXTRA_FOUND_HENT);
//                if (foundHent != null) {
//                    mInput.setFirstOwner(foundHent);
//                    updateFirstOwner();
//                }
//                break;

            case CHANGE_COWNO_REQUEST_CODE: {
                EAN cowNo = (EAN) data.getSerializableExtra(CowNoInputActivity.EXTRA_EAN);
                if (cowNo != null) {
                    mInput.setCowNo(cowNo);
                    updateCowNo();
                }
                break;
            }
            case CHANGE_MOTHERNO_REQUEST_CODE:
                EAN motherNo = (EAN) data.getSerializableExtra(CowNoInputActivity.EXTRA_EAN);
                if (motherNo != null) {
                    mInput.setMotherNo(motherNo);
                    updateMotherNo();
                }
                break;

            case INPUT_GROSSPRICEPERKG_REQUEST_CODE: {
                double grossPricePerKg = data.getDoubleExtra(DecimalInputActivity.EXTRA_VALUE, 0.0);
                double netPricePerKg = grossPricePerKg / (1 + getSafeVatRate());
                setNetPricePerKg(MoneyRounding.round(netPricePerKg));
                break;
            }
            case INPUT_NETPRICEPERKG_REQUEST_CODE: {
                double netPricePerKg = data.getDoubleExtra(DecimalInputActivity.EXTRA_VALUE, 0.0);
                setNetPricePerKg(MoneyRounding.round(netPricePerKg));
                break;
            }
            case INPUT_WEIGHT_REQUEST_CODE:
                double weight = data.getDoubleExtra(WeightInputActivity.EXTRA_WEIGHT, 0.0);
                mInput.setWeight(weight);
                updateWeight();
                break;
            case SCAN_COWNO_REQUEST_CODE: {
                EAN cowNo = (EAN) data.getSerializableExtra(CowNoScanActivity.EXTRA_COWNO);
                if (cowNo != null) {
                    mInput.setCowNo(cowNo);
                    updateCowNo();
                }
                break;
            }
//            case SCAN_FIRSTOWNER_REQUEST_CODE: {
//                switch (resultCode) {
//                    case ScanHentActivity.RESULT_OK: {
//                        HentObj hent = (HentObj) data.getSerializableExtra(ScanHentActivity.EXTRA_HENT);
//                        mInput.setFirstOwner(hent);
//                        updateFirstOwner();
//                        break;
//                    }
//                    case ScanHentActivity.RESULT_ADD_NEW_HENT: {
//                        EAN newHentFarmNo = (EAN) data.getSerializableExtra(ScanHentActivity.EXTRA_NEWHENT_FARMNO);
//                        if (newHentFarmNo != null) {
//                            HentObj hent4Input = new HentObj(0);
//                            hent4Input.setHentNo(newHentFarmNo);
//                            Intent newHentIntent = new Intent(this, NewHentActivity.class);
//                            newHentIntent.putExtra(NewHentActivity.EXTRA_HENT_FOR_INPUTS, hent4Input);
//                            startActivityForResult(newHentIntent, NEW_FIRSTOWNER_REQUEST_CODE);
//                        }
//                        break;
//                    }
//                }
//                break;
//            }
        }
    }


    public void updateBirthDt() {
        Date birthDt = mInput.getBirthDt();
        String cowBirthDtStr = "";
        if (birthDt != null) {
            cowBirthDtStr = Dates.toDayDate(birthDt);
        }
        getCowBirthDtBox().setText(cowBirthDtStr);
    }

    private void onSetBirthDt() {
        Date birthDt = mInput.getBirthDt();
        if (birthDt == null) {

            Date passportIssueDt = mInput.getPassportIssueDt();
            if(passportIssueDt != null)
            {
                birthDt = Dates.plusDays(passportIssueDt,-14);
            }
            else {
                birthDt = Calendar.getInstance().getTime();
            }
        }

        BKDatePickerDialog birthDtPickerDlg = new BKDatePickerDialog(this, birthDt,
                new BKDatePickerDialog.OnDateListener() {
                    public void onDate(Date dt) {
                        mInput.setBirthDt(dt);
                        updateBirthDt();
                    }
                });

        birthDtPickerDlg.show();
    }

    private EditText getCowBirthDtBox() {
        return (EditText) findViewById(R.id.cowBirthDtBox);
    }

    private EditText getCowBirthPlaceBox() {
        return (EditText) findViewById(R.id.cowBirthPlaceBox);
    }

    private EditText getMotherNoBox() {
        return (EditText) findViewById(R.id.cowMotherNoBox);
    }

    private EditText getCowHealthCertNoxBox() {
        return (EditText) findViewById(R.id.cowHealthCertNoBox);
    }

    private EditText getCowPassportIssueDtBox() {
        return (EditText) findViewById(R.id.cowPassportIssueDtBox);
    }

    private EditText getCowPassportNoBox() {
        return (EditText) findViewById(R.id.cowPassportNoBox);
    }

//    private Button getAddFirstOwnerBtn() {
//        return (Button) findViewById(R.id.addFirstOwnerBtn);
//    }

//    private Button getEditFstOwnerBtn() {
//        return (Button) findViewById(R.id.editFstOwnerBtn);
//    }

//    private EditText getFirstOwnerBox() {
//        return (EditText) findViewById(R.id.fstOwnerBox);
//    }

    private EditText getCowNetPricePerKgBox() {
        return (EditText) findViewById(R.id.cowNetPricePerKgBox);
    }

    private EditText getCowGrossPricePerKgBox() {
        return (EditText) findViewById(R.id.cowGrossPricePerKgBox);
    }

    private EditText getCowWeightBox() {
        return (EditText) findViewById(R.id.cowWeightBox);
    }

    private CowSexButton getCowSexBtn() {
        return (CowSexButton) findViewById(R.id.cowSexBtn);
    }

    private EditText getCowNoBox() {
        return (EditText) findViewById(R.id.cowNoBox);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    protected void updateCowFromBoxes() {
        mInput.setPassportNo(getCowPassportNoBox().getText().toString());

        Double netPricePerKg = getNetPricePerKg();
        Double weight = mInput.getWeight();

        if (netPricePerKg != null && weight != null) {
            mInput.setNetPrice(netPricePerKg * weight);
        }

        mInput.setSex(getCowSexBtn().getCowSex());
        mInput.setBirthPlace(getCowBirthPlaceBox().getText().toString());
        mInput.setHealthCertNo(getCowHealthCertNoxBox().getText().toString());
        ToggleListView classesList = getClassesList();
        mInput.setClassCd(classesList.getToggledItem());
        ToggleListView stocksList = getStocksList();
        mInput.setStockCd(stocksList.getToggledItem());
    }


    protected void updateMotherNo() {
        EAN motherNo = mInput.getMotherNo();
        if (motherNo != null) {
            getMotherNoBox().setText(motherNo.toString());
        } else {
            getMotherNoBox().getText().clear();
        }
    }

    private void displayError(int msgResId) {
        new ErrorToast(this).show(msgResId);
    }

    protected boolean validateCow() {
        if (mInput.getCowNo() == null) {
            displayError(R.string.errEmptyCowNo);
            return false;
        }

        String classCd = getClassesList().getToggledItem();
        if (Strings.isNullOrEmpty(classCd)) {
            displayError(R.string.errEmptyClass);
            return false;
        }

        Double weight = mInput.getWeight();
        if (weight == null || weight < 0.001) {
            displayError(R.string.errInvalidCowWeight);
            return false;
        }

        if (mInput.getSex() == null) {
            displayError(R.string.errEmptyCowSex);
            return false;
        }


        Double price = getNetPricePerKg();
        if (price == null || price < 0.01) {
            displayError(R.string.errInvalidCowPrice);
            return false;
        }

        Date passportIssueDt = mInput.getPassportIssueDt();
        Date birthDt = mInput.getBirthDt();

        if (passportIssueDt != null && birthDt != null) {
            if (passportIssueDt.before(birthDt)) {
                displayError(R.string.errInvalidBirthDtWRTPassportIssueDt);
                getCowPassportIssueDtBox().requestFocus();
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSave:
                updateCowFromBoxes();
                if (validateCow()) {
                    Intent resultIntent = new Intent();
                    try {
                        if (mCowInputId == null) {
                            UUID cowInputId = mService.addCow(mInput);
                            resultIntent.putExtra(EXTRA_COW_INPUT_ID, cowInputId);
                        } else {
                            mService.updateCow(mInput);
                            resultIntent.putExtra(EXTRA_COW_INPUT_ID, mInput.getInputId());
                        }
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    } catch (DuplicateCowInInvoiceException e) {
                        displayError(R.string.errAddingCausesDuplicateInInvoice);
                    } catch (DuplicateCowInPurchaseException e) {
                        displayError(R.string.errAddingCausesDuplicateInPurchase);
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onCopyBirthPlace() {
        if (mInvoiceHent != null) {
            mInput.setBirthPlace(mInvoiceHent.getCity());
            updateBirthPlace();
        }
    }

//    private void onCopyFirstOwner() {
//        if (mInvoiceHent != null) {
//            mInput.setFirstOwner(mInvoiceHent.asHent());
//            updateFirstOwner();
//        }
//    }

    private void onScanCowNo() {
        startActivityForResult(new Intent(this, CowNoScanActivity.class), SCAN_COWNO_REQUEST_CODE);
    }

    private void showError(String error) {
        mErrMsgFragment = new ErrorMessageFragment();
        mErrMsgFragment.setCaption(getResources().getString(R.string.errorCaption));
        mErrMsgFragment.setErrorMsg(error);
        mErrMsgFragment.setListener(mOnErrorHideListener);
        mErrMsgFragment.show(getFragmentManager(), ERRORMSG_FRAGMENT_TAG);
    }


    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            InvoiceService.LocalBinder binder = (InvoiceService.LocalBinder) service;
            mService = binder.getService();

            mVatRate = mService.getVatRate();
            mInvoiceHent = mService.getHent();

            if (mVatRate == null) {
                TextView grossPriceTotalLabel = (TextView) findViewById(R.id.grossPriceTotalLabel);
                TextView grossPricePerKgLabel = (TextView) findViewById(R.id.grossPricePerKgLabel);
                EditText grossTotalBox = getCowTotalGrossPrice();
                grossPriceTotalLabel.setBackgroundColor(Color.LTGRAY);
                grossPricePerKgLabel.setBackgroundColor(Color.LTGRAY);
                grossTotalBox.setBackgroundColor(Color.LTGRAY);
                getCowGrossPricePerKgBox().setEnabled(false);
            }

            decideIfCopyBtnsAreEnabled();

            continueResume();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
        }
    };


}
