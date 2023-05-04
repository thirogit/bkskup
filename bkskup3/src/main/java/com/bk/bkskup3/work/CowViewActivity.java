package com.bk.bkskup3.work;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import com.bk.bkskup3.R;
import com.bk.bkskup3.model.EAN;
import com.bk.bkskup3.model.Hent;
import com.bk.bkskup3.runtime.CowSexString;
import com.bk.bkskup3.utils.Dates;
import com.bk.bkskup3.utils.NullUtils;
import com.bk.bkskup3.utils.Numbers;
import com.bk.bkskup3.work.input.CowInput;
import com.google.common.base.Strings;

import java.util.Date;

public class CowViewActivity extends Activity {
    public static final String EXTRA_COW_INPUT = "cow_input";
    public static final String EXTRA_VAT_RATE = "vat_rate";
    private CowInput mInput;
    private Double mVatRate;
    private CowSexString mCowSexString;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cowview);

        mCowSexString = new CowSexString(this.getResources());

        Intent intent = getIntent();
        mVatRate = (Double) intent.getSerializableExtra(EXTRA_VAT_RATE);
        mInput = (CowInput) intent.getSerializableExtra(EXTRA_COW_INPUT);

        updateBoxesFromCow();
    }

    protected void updateBoxesFromCow() {
        updateCowNo();
        updateCowSex();
        updateStock();
        updateCowClass();
        updateFirstOwner();
        updatePassportNo();
        updatePricePerKg();
        updateTotalPrice();
        updateWeight();
        updatePassportNo();
        updatePassportIssueDt();
        updateHealthCertNo();
        updateMotherNo();
        updateBirthPlace();
        updateBirthDt();

    }

    private void updateStock() {
        EditText stockCdBox = getStockCdBox();
        stockCdBox.setText(mInput.getStockCd());
    }

    private void updateCowClass() {
        EditText classCdBox = getClassCdBox();
        classCdBox.setText(mInput.getClassCd());
    }

    private EditText getClassCdBox() {
        return (EditText) findViewById(R.id.cowClassCdBox);
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
        getCowSexBox().setText(mCowSexString.toString(mInput.getSex()));
    }

    private EditText getCowSexBox() {
        return (EditText) findViewById(R.id.cowSexBox);
    }

    private void updatePassportNo() {
        EditText cowPassportNoBox = getCowPassportNoBox();
        cowPassportNoBox.setText(mInput.getPassportNo());
    }


    private void updatePricePerKg() {
        EditText cowNetPricePerKgBox = getCowNetPricePerKgBox();
        EditText cowGrossPricePerKgBox = getCowGrossPricePerKgBox();

        double netPricePerKg = mInput.getNetPrice()/mInput.getWeight();

        cowNetPricePerKgBox.setText(Numbers.formatPrice(netPricePerKg));
        if (mVatRate != null) {
            cowGrossPricePerKgBox.setText(Numbers.formatPrice(netPricePerKg + netPricePerKg * getSafeVatRate()));
        }
    }

    private EditText getCowTotalGrossPrice() {
        return (EditText) findViewById(R.id.cowTotalGrossPriceBox);
    }

    private EditText getCowTotalNetPriceBox() {
        return (EditText) findViewById(R.id.cowTotalNetPriceBox);
    }

    private void updateTotalPrice() {
        EditText cowTotalGrossPriceBox = getCowTotalGrossPrice();
        EditText cowTotalNetPriceBox = getCowTotalNetPriceBox();

        double totalNetPrice = mInput.getNetPrice();
        double totalGrossPrice = totalNetPrice + totalNetPrice * getSafeVatRate();

        if (mVatRate != null) {
            cowTotalGrossPriceBox.setText(Numbers.formatPrice(totalGrossPrice));
        }
        cowTotalNetPriceBox.setText(Numbers.formatPrice(totalNetPrice));

    }

    private double getSafeVatRate() {
        return NullUtils.valueForNull(mVatRate, 0.0);
    }

    private void updateWeight() {
        Double weight = mInput.getWeight();
        EditText cowWeightBox = getCowWeightBox();

        String weightStr = "";
        if (weight != null && weight >= 0.0) {
            weightStr = Numbers.formatWeight(weight);
        }
        cowWeightBox.setText(weightStr);
        updateTotalPrice();
    }

    protected void updateFirstOwner() {
        Hent firstOwner = mInput.getFirstOwner();

        String firstOwnerStr = "";
        if (firstOwner != null) {
            firstOwnerStr = firstOwner.getAlias();

            if (Strings.isNullOrEmpty(firstOwnerStr)) {
                firstOwnerStr = firstOwner.getHentName();
            }
        }
        getFirstOwnerBox().setText(firstOwnerStr);
    }

    public void updatePassportIssueDt() {
        Date cowPassportIssueDt = mInput.getPassportIssueDt();

        String cowPassportIssueDtStr = "";
        if (cowPassportIssueDt != null) {
            cowPassportIssueDtStr = Dates.toDayDate(cowPassportIssueDt);
        }
        getCowPassportIssueDtBox().setText(cowPassportIssueDtStr);
    }


    public void updateCowNo() {
        EAN cowNo = mInput.getCowNo();
        String cowNoStr = "";
        if (cowNo != null) {
            cowNoStr = cowNo.toString();
        }
        getCowNoBox().setText(cowNoStr);
    }


    public void updateBirthDt() {
        Date birthDt = mInput.getBirthDt();
        String cowBirthDtStr = "";
        if (birthDt != null) {
            cowBirthDtStr = Dates.toDayDate(birthDt);
        }
        getCowBirthDtBox().setText(cowBirthDtStr);
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


    private EditText getFirstOwnerBox() {
        return (EditText) findViewById(R.id.fstOwnerBox);
    }

    private EditText getCowNetPricePerKgBox() {
        return (EditText) findViewById(R.id.cowNetPricePerKgBox);
    }

    private EditText getCowGrossPricePerKgBox() {
        return (EditText) findViewById(R.id.cowGrossPricePerKgBox);
    }

    private EditText getCowWeightBox() {
        return (EditText) findViewById(R.id.cowWeightBox);
    }


    private EditText getCowNoBox() {
        return (EditText) findViewById(R.id.cowNoBox);
    }


    protected void updateMotherNo() {
        EAN motherNo = mInput.getMotherNo();
        if (motherNo != null) {
            getMotherNoBox().setText(motherNo.toString());
        } else {
            getMotherNoBox().getText().clear();
        }
    }

    public EditText getStockCdBox() {
        return (EditText) findViewById(R.id.cowStockCdBox);
    }
}
