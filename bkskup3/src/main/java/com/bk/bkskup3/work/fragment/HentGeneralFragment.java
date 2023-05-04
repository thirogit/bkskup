package com.bk.bkskup3.work.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.bk.bkskup3.R;
import com.bk.bkskup3.barcode.BarcodeServiceClient;
import com.bk.bkskup3.barcode.BarcodeServiceState;
import com.bk.bkskup3.barcode.ServiceIndicatorButton;
import com.bk.bkskup3.model.EAN;
import com.bk.bkskup3.model.HentType;
import com.bk.bkskup3.widgets.HentTypeButton;
import com.bk.bkskup3.work.HentNoInputActivity;
import com.bk.bkskup3.work.HentNoScanActivity;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/22/2014
 * Time: 11:40 AM
 */
public class HentGeneralFragment extends HentFragment {

    private static final int CHANGE_HENTNO_REQUEST_CODE = 1001;
    private static final int SCAN_HENTNO_REQUEST_CODE = 1002;

    private BarcodeServiceClient mBCServiceClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.hent_general, container, false);
    }

    public void onViewCreated(View view, Bundle state) {
        super.onViewCreated(view, state);
        ensureContent();

    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateInputs();
    }

    private void ensureContent() {

        EditText nameBox = getNameBox();
        nameBox.requestFocus();
        nameBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                onNameBoxKillFocus();
            }
        });

        getFarmNoBox().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChangeHentNo();
            }
        });


        final ServiceIndicatorButton bcServiceIndBtn = getBCServiceIndBtn();

        mBCServiceClient = new BarcodeServiceClient(getActivity());
        mBCServiceClient.attachObserver(new BarcodeServiceClient.BarcodeClientObserver() {
            @Override
            public void onBarcode(String bc) {
            }

            @Override
            public void onState(BarcodeServiceState state) {
                bcServiceIndBtn.setState(state);
            }
        });

        bcServiceIndBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onScanHentNo();
            }
        });


    }

    protected EditText getFarmNoBox() {
        return (EditText) findViewById(R.id.hentHentNoEditBox);
    }

    private View findViewById(int viewId) {
        return getView().findViewById(viewId);
    }


    protected EditText getNameBox() {
        return (EditText) findViewById(R.id.hentNameEditBox);
    }

    private void onNameBoxKillFocus() {
        String alias = getAlias();
        String name = getHentName();
        if (alias.isEmpty() && !name.isEmpty()) {
            getAliasBox().setText(name.toUpperCase());
        }
    }

    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
//        state.putSerializable(STATE_EXTRA_HENTNO, getHentNo());
//        state.putString(STATE_EXTRA_HENTNAME, getHentName());
//        state.putString(STATE_EXTRA_ALIAS, getAlias());
//        state.putString(STATE_EXTRA_STREET, getStreet());
//        state.putString(STATE_EXTRA_POBOX, getPOBox());
//        state.putString(STATE_EXTRA_CITY, getCity());
//        state.putString(STATE_EXTRA_ZIPCODE, getZipCode());
//        state.putSerializable(STATE_EXTRA_HENTTYPE, getHentType());
    }

    protected EditText getAliasBox() {
        return (EditText) findViewById(R.id.hentAliasEditBox);
    }

    public String getAlias() {
        return getAliasBox().getText().toString();
    }

    public void updateAlias() {
        getAliasBox().setText(mInput.getAlias());
    }

    public String getHentName() {
        return getNameBox().getText().toString();
    }

    public void updateHentName() {
        getNameBox().setText(mInput.getHentName());
    }

    public String getZipCode() {
        return getZipCodeBox().getText().toString();
    }

    protected EditText getZipCodeBox() {
        return (EditText) findViewById(R.id.hentZipEditBox);
    }

    public void updateZipCode() {
        getZipCodeBox().setText(mInput.getZip());
    }

    public String getCity() {
        return getCityBox().getText().toString();
    }

    protected EditText getCityBox() {
        return (EditText) findViewById(R.id.hentCityEditBox);
    }

    public void updateCity() {
        getCityBox().setText(mInput.getCity());
    }

    public String getStreet() {
        return getStreetBox().getText().toString();
    }

    protected EditText getStreetBox() {
        return (EditText) findViewById(R.id.hentStreetEditBox);
    }

    public void updateStreet() {
        getStreetBox().setText(mInput.getStreet());
    }

    public String getPOBox() {
        return getPOBoxBox().getText().toString();
    }

    protected EditText getPOBoxBox() {
        return (EditText) findViewById(R.id.hentPOBoxEditBox);
    }

    public void updatePoBox() {
        getPOBoxBox().setText(mInput.getPoBox());
    }

    private void onChangeHentNo() {
        Intent changeHentNoIntent = new Intent(getActivity(), HentNoInputActivity.class);
        EAN hentNo = mInput.getHentNo();

        if (hentNo != null) {
            changeHentNoIntent.putExtra(HentNoInputActivity.EXTRA_EAN, hentNo);
        } else {
            changeHentNoIntent.putExtra(HentNoInputActivity.EXTRA_COUNTRY, mInputDefaults.getDefaultCountry());
        }

        startActivityForResult(changeHentNoIntent, CHANGE_HENTNO_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CHANGE_HENTNO_REQUEST_CODE: {
                    EAN hentNo = (EAN) data.getSerializableExtra(HentNoInputActivity.EXTRA_EAN);
                    if (hentNo != null) {
                        mInput.setHentNo(hentNo);
                        updateHentNo();
                    }
                    break;
                }
                case SCAN_HENTNO_REQUEST_CODE: {
                    EAN hentNo = (EAN) data.getSerializableExtra(HentNoScanActivity.EXTRA_HENTNO);
                    if (hentNo != null) {
                        mInput.setHentNo(hentNo);
                        updateHentNo();
                    }
                    break;
                }
            }
        }
    }


//    public EAN getHentNo() {
//        return mHentFarmNo;
//    }

    public void updateHentNo() {
        final EditText farmNoBox = getFarmNoBox();
        EAN hentFarmNo = mInput.getHentNo();
        if (hentFarmNo == null) {
            farmNoBox.getText().clear();
        } else {
            farmNoBox.setText(hentFarmNo.toString());
        }
    }

    protected HentTypeButton getHentTypeButton() {
        return (HentTypeButton) findViewById(R.id.hentTypeBtn);
    }


    public void updateHentType() {
        getHentTypeButton().setHentType(mInput.getHentType());
    }

    public HentType getHentType() {
        return getHentTypeButton().getHentType();
    }

    public void onStart() {
        super.onStart();
        mBCServiceClient.start();
    }

    public void onStop() {
        super.onStop();
        mBCServiceClient.stop();
    }


    private void onScanHentNo() {
        startActivityForResult(new Intent(getActivity(), HentNoScanActivity.class), SCAN_HENTNO_REQUEST_CODE);
    }

    public void updateInputs() {
        if (getView() != null) {
            updateHentName();
            updateAlias();
            updateStreet();
            updatePoBox();
            updateCity();
            updateZipCode();
            updateHentNo();
            updateHentType();
        }
    }

    @Override
    public void save() {

        if (getView() != null) {
            mInput.setHentName(getHentName());
            mInput.setAlias(getAlias());
            mInput.setStreet(getStreet());
            mInput.setPoBox(getPOBox());
            mInput.setCity(getCity());
            mInput.setZip(getZipCode());
            mInput.setHentType(getHentType());
        }
    }

    private ServiceIndicatorButton getBCServiceIndBtn() {
        return (ServiceIndicatorButton) findViewById(R.id.bcServiceBtn);
    }

}
