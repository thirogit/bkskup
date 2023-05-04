package com.bk.bkskup3.management;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bk.bkskup3.BkActivity;
import com.bk.bkskup3.R;
import com.bk.bkskup3.dao.SettingsStore;
import com.bk.bkskup3.feedback.ErrorToast;
import com.bk.bkskup3.settings.TaxSettings;
import com.bk.bkskup3.utils.DoubleFormatter;
import com.bk.bkskup3.utils.NullUtils;
import com.bk.bkskup3.utils.NumberFormatDefinition;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/10/2014
 * Time: 9:38 PM
 */
public class TaxRatesActivity extends BkActivity {

    public static final double RATE_DELTA = 0.01;

    private EditText mLumpInvVatRateEditBox;
    private Button mLumpInvVatRatePlusBtn;
    private Button mLumpInvVatRateMinusBtn;
    private Button mRegInvVatRateMinusBtn;
    private Button mRegInvVatRatePlusBtn;
    private EditText mRegInvVatRateEditBox;

    @Inject
    SettingsStore mSettingsStore;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vatrates);

        mLumpInvVatRateEditBox = (EditText) findViewById(R.id.lumpInvVatRateEditBox);
        View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                EditText editBox = (EditText) v;
                if (hasFocus) {
                    editBox.selectAll();
                } else {
                    editBox.setSelection(0, 0);
                    formatInput(editBox);
                }
            }
        };
        mLumpInvVatRateEditBox.setOnFocusChangeListener(focusChangeListener);


        mRegInvVatRateEditBox = (EditText) findViewById(R.id.regInvVatRateEditBox);
        mRegInvVatRateEditBox.setOnFocusChangeListener(focusChangeListener);

        mLumpInvVatRatePlusBtn = (Button) findViewById(R.id.lumpInvVatRatePlusBtn);
        mLumpInvVatRatePlusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseInput(mLumpInvVatRateEditBox, RATE_DELTA);
            }
        });
        mLumpInvVatRateMinusBtn = (Button) findViewById(R.id.lumpInvVatRateMinusBtn);
        mLumpInvVatRateMinusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseInput(mLumpInvVatRateEditBox, -RATE_DELTA);
            }
        });


        mRegInvVatRatePlusBtn = (Button) findViewById(R.id.regInvVatRatePlusBtn);
        mRegInvVatRatePlusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseInput(mRegInvVatRateEditBox, RATE_DELTA);
            }
        });
        mRegInvVatRateMinusBtn = (Button) findViewById(R.id.regInvVatRateMinusBtn);
        mRegInvVatRateMinusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseInput(mRegInvVatRateEditBox, -RATE_DELTA);
            }
        });


        TaxSettings taxSettings = mSettingsStore.loadSettings(TaxSettings.class);
        setInputs(taxSettings);
    }

    private void formatInput(EditText input) {
        Double value = parseInput(input);
        input.setText(formatVatRate(value));
    }

    private void setInputs(TaxSettings taxSettings) {
        mLumpInvVatRateEditBox.setText(formatVatRate(taxSettings.getVatRateForIndividual()));
        mRegInvVatRateEditBox.setText(formatVatRate(taxSettings.getVatRateForCompany()));
    }

    private Double parseInput(EditText input) {
        Double value = null;
        if (input.getText().length() > 0) {
            value = Double.parseDouble(input.getText().toString());
            value = value/100.0;
        }
        return value;
    }

    private void increaseInput(EditText input, double delta) {

        double value = NullUtils.valueForNull(parseInput(input), 0.0);

        value += delta;
        value = Math.max(0.0, value);
        value = Math.min(100.0, value);

        input.setText(formatVatRate(value));
    }

    private String formatVatRate(Double vatRate) {
        if(vatRate == null)
            return "";

        NumberFormatDefinition vatRateFormat = new NumberFormatDefinition('.', 1);
        return new DoubleFormatter(vatRateFormat).format(vatRate*100.0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSave:
                if (validate()) {
                    saveTaxRates();
                    setResult(RESULT_OK);
                    finish();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveTaxRates() {
        TaxSettings taxSettings = new TaxSettings();
        Double vatRateForCompany = parseInput(mRegInvVatRateEditBox);
        taxSettings.setVatRateForCompany(vatRateForCompany);
        Double vatRateForIndividual = parseInput(mLumpInvVatRateEditBox);
        taxSettings.setVatRateForIndividual(vatRateForIndividual);
        mSettingsStore.saveSettings(taxSettings);
    }

    private boolean validate() {

        if (parseInput(mLumpInvVatRateEditBox) == null) {
            displayErrorToast(R.string.errMissingVatRateForLumpInvoice);
            return false;
        }

        if (parseInput(mRegInvVatRateEditBox) == null) {
            displayErrorToast(R.string.errMissingVatRateForRegularInvoice);
            return false;
        }

        return true;
    }

    private void displayErrorToast(int msgId) {
        new ErrorToast(this).show(msgId);
    }


}
