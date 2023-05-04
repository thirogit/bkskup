package com.bk.bkskup3.preferences;

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
import com.bk.bkskup3.settings.InputDefaultsSettings;
import com.bk.bkskup3.widgets.CountrySpinner;
import com.bk.bkskup3.widgets.HentTypeButton;
import com.bk.bkskup3.widgets.PayWayButton;
import com.bk.countries.Country;
import com.bk.widgets.spinner.AdapterView;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 1/6/2015
 * Time: 11:40 PM
 */
public class AgentPreferencesActivity extends BkActivity {

    private static final String STATE_EXTRA_PREFERENCES = "state_preferences";

    private HentTypeButton mDefaultHentTypeBtn;
    private CountrySpinner mDefaultCountry;
    private PayWayButton mPayWayForCompanyBtn;
    private PayWayButton mPayWayForIndividualBtn;
    private EditText mPayDueDaysForCompanyBox;
    private Button mPayDueDaysForCompanyPlusBtn;
    private Button mPayDueDaysForCompanyMinusBtn;
    private EditText mPayDueDaysForIndividualBox;
    private Button mPayDueDaysForIndividualPlusBtn;
    private Button mPayDueDaysForIndividualMinusBtn;

    InputDefaultsSettings mPreferences;

    @Inject
    SettingsStore mSettingsStore;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preference);


        mDefaultHentTypeBtn = (HentTypeButton) findViewById(R.id.defaultHentTypeBtn);
        mDefaultCountry = (CountrySpinner) findViewById(R.id.defaultCountryBtn);
        mPayWayForCompanyBtn = (PayWayButton) findViewById(R.id.payWayForCompanyBtn);
        mPayWayForIndividualBtn = (PayWayButton) findViewById(R.id.payWayForIndividualBtn);
        mPayDueDaysForCompanyBox = (EditText) findViewById(R.id.payDueDaysForCompanyBox);

        mPayDueDaysForCompanyPlusBtn = (Button) findViewById(R.id.payDueDaysForCompanyPlusBtn);
        mPayDueDaysForCompanyMinusBtn = (Button) findViewById(R.id.payDueDaysForCompanyMinusBtn);
        mPayDueDaysForIndividualBox = (EditText) findViewById(R.id.payDueDaysForIndividualBox);
        mPayDueDaysForIndividualPlusBtn = (Button) findViewById(R.id.payDueDaysForIndividualPlusBtn);
        mPayDueDaysForIndividualMinusBtn = (Button) findViewById(R.id.payDueDaysForIndividualMinusBtn);


        mPayDueDaysForCompanyPlusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPayDueDays(1, mPayDueDaysForCompanyBox);
            }
        });


        mPayDueDaysForCompanyMinusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPayDueDays(-1, mPayDueDaysForCompanyBox);
            }
        });

        mPayDueDaysForIndividualPlusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPayDueDays(1, mPayDueDaysForIndividualBox);
            }
        });

        mPayDueDaysForIndividualMinusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPayDueDays(-1, mPayDueDaysForIndividualBox);
            }
        });

        mDefaultCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onDefaultCountryChange(mDefaultCountry.getCountryAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }


        });


        if (savedInstanceState == null) {
            mPreferences = mSettingsStore.loadSettings(InputDefaultsSettings.class);
        } else {
            mPreferences = (InputDefaultsSettings) savedInstanceState.getSerializable(STATE_EXTRA_PREFERENCES);
        }
        updateBoxes();
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_EXTRA_PREFERENCES, mPreferences);
    }

    private void onDefaultCountryChange(Country defaultCountry) {
        mPreferences.setDefaultCountry(defaultCountry.getCode2A());
    }

    private void addPayDueDays(int days, EditText payDueDaysBox) {
        Integer value = getPayDueDaysFromBox(payDueDaysBox);
        if (value == null)
            value = 0;

        value += days;
        setPayDueDaysBox(value, payDueDaysBox);

    }

    private void setPayDueDaysBox(Integer value, EditText payDueDaysBox) {
        if (value != null) {
            payDueDaysBox.setText(String.valueOf(Math.max(Math.min(value, 999), 0)));
        } else {
            payDueDaysBox.getText().clear();
        }
    }

    private void updateBoxes() {

        mDefaultHentTypeBtn.setHentType(mPreferences.getDefaultHentType());
        mPayWayForCompanyBtn.setPayWay(mPreferences.getDefaultPayWayForCompany());
        mPayWayForIndividualBtn.setPayWay(mPreferences.getDefaultPayWayForIndividual());
        setPayDueDaysBox(mPreferences.getDefaultPayDueDaysForCompany(), mPayDueDaysForCompanyBox);
        setPayDueDaysBox(mPreferences.getDefaultPayDueDaysForIndividual(), mPayDueDaysForIndividualBox);
        mDefaultCountry.setSelectedCountry(mPreferences.getDefaultCountry());
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
                savePreferences();
                setResult(RESULT_OK);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void savePreferences() {

        mPreferences.setDefaultHentType(mDefaultHentTypeBtn.getHentType());
        mPreferences.setDefaultPayWayForCompany(mPayWayForCompanyBtn.getPayWay());
        mPreferences.setDefaultPayWayForIndividual(mPayWayForIndividualBtn.getPayWay());
        mPreferences.setDefaultPayDueDaysForCompany(getPayDueDaysFromBox(mPayDueDaysForCompanyBox));
        mPreferences.setDefaultPayDueDaysForIndividual(getPayDueDaysFromBox(mPayDueDaysForIndividualBox));

        mSettingsStore.saveSettings(mPreferences);

    }

    private Integer getPayDueDaysFromBox(EditText payDueDaysBox) {
        Integer value = null;
        if (payDueDaysBox.getText().length() > 0) {
            value = Integer.parseInt(payDueDaysBox.getText().toString());
        }
        return value;
    }

}
