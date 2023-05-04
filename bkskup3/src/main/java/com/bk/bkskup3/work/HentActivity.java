package com.bk.bkskup3.work;

import android.app.*;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.bk.bkskup3.R;
import com.bk.bkskup3.dao.SettingsStore;
import com.bk.bkskup3.feedback.ErrorToast;
import com.bk.bkskup3.model.*;
import com.bk.bkskup3.settings.InputDefaultsSettings;
import com.bk.bkskup3.utils.check.FiscalNoCheck;
import com.bk.bkskup3.utils.check.IBANCheck;
import com.bk.bkskup3.utils.check.PersonalNoCheck;
import com.bk.bkskup3.work.fragment.*;
import com.google.common.base.Strings;

import javax.inject.Inject;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/19/11
 * Time: 10:29 PM
 */
public abstract class HentActivity extends BusActivity {

    public static final String EXTRA_SAVED_HENT = "saved_hent";
    private static final String GENERAL_FRAGMENT_TAB_TAG = "general";
    private static final String CONTACT_FRAGMENT_TAB_TAG = "contact";
    private static final String BANK_FRAGMENT_TAB_TAG = "bank";
    private static final String ID_FRAGMENT_TAB_TAG = "identification";

    private static final String STATE_EXTRA_HENT = "state_hent";
    private static final String STATE_EXTRA_INPUT_DEFAULTS = "state_input_defaults";
    private static final String STATE_EXTRA_SELECTEDTAB = "selected_tab";

    HentGeneralFragment mGeneralFragment;
    HentBankFragment mBankFragment;
    HentIdentificationFragment mIdentificationFragment;
    HentContactFragment mContactFragment;
    protected HentObj mHent;
    private int mSelectedTab;
    protected InputDefaultsSettings mInputDefaults;

    @Inject
    SettingsStore mSettingStore;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        if (savedState == null) {
            mInputDefaults = mSettingStore.loadSettings(InputDefaultsSettings.class);
            mHent = load();
        } else {
            mSelectedTab = savedState.getInt(STATE_EXTRA_SELECTEDTAB, 0);
            mHent = (HentObj) savedState.getSerializable(STATE_EXTRA_HENT);
            mInputDefaults = (InputDefaultsSettings) savedState.getSerializable(STATE_EXTRA_INPUT_DEFAULTS);
        }

        FragmentManager fm = getFragmentManager();
        mGeneralFragment = (HentGeneralFragment) fm.findFragmentByTag(GENERAL_FRAGMENT_TAB_TAG);
        mBankFragment = (HentBankFragment) fm.findFragmentByTag(BANK_FRAGMENT_TAB_TAG);
        mIdentificationFragment = (HentIdentificationFragment) fm.findFragmentByTag(ID_FRAGMENT_TAB_TAG);
        mContactFragment = (HentContactFragment) fm.findFragmentByTag(CONTACT_FRAGMENT_TAB_TAG);

        if (mGeneralFragment == null) {
            mGeneralFragment = (HentGeneralFragment) Fragment.instantiate(this, HentGeneralFragment.class.getName());
        }
        if (mBankFragment == null) {
            mBankFragment = (HentBankFragment) Fragment.instantiate(this, HentBankFragment.class.getName());
        }

        if (mIdentificationFragment == null) {
            mIdentificationFragment = (HentIdentificationFragment) Fragment.instantiate(this, HentIdentificationFragment.class.getName());
        }

        if (mContactFragment == null) {
            mContactFragment = (HentContactFragment) Fragment.instantiate(this, HentContactFragment.class.getName());
        }

        injectFragments();
        createTabs();
    }


    private void createTabs() {
        ActionBar bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

        bar.addTab(bar.newTab()
                .setText(R.string.generalTabCaption)
                .setTabListener(new HentTabListener<HentGeneralFragment>(GENERAL_FRAGMENT_TAB_TAG, mGeneralFragment)));
        bar.addTab(bar.newTab()
                .setText(R.string.contactTabCaption)
                .setTabListener(new HentTabListener<HentContactFragment>(CONTACT_FRAGMENT_TAB_TAG, mContactFragment)));

        bar.addTab(bar.newTab()
                .setText(R.string.identificationTabCaption)
                .setTabListener(new HentTabListener<HentIdentificationFragment>(ID_FRAGMENT_TAB_TAG, mIdentificationFragment)));

        bar.addTab(bar.newTab()
                .setText(R.string.bankTabCaption)
                .setTabListener(new HentTabListener<HentBankFragment>(BANK_FRAGMENT_TAB_TAG, mBankFragment)));

    }

    protected void onPause() {
        super.onPause();
        invokeSave();
        mSelectedTab = getActionBar().getSelectedNavigationIndex();
    }

    protected abstract HentObj load();

    protected abstract boolean validate(Hent hent);

    protected abstract HentObj save(Hent hent);


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.menuCancel:
//                setResult(RESULT_CANCELED);
//                finish();
//                break;
            case R.id.menuSave:
                invokeSave();
                if (validateInputs()) {
                    if (validate(mHent)) {
                        HentObj savedHent = save(mHent);
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra(EXTRA_SAVED_HENT, savedHent);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onResume() {
        super.onResume();
        getActionBar().setSelectedNavigationItem(mSelectedTab);
    }

    private void injectFragments() {
        mGeneralFragment.setHent(mHent);
        mGeneralFragment.setInputDefaults(mInputDefaults);

        mBankFragment.setHent(mHent);
        mBankFragment.setInputDefaults(mInputDefaults);

        mIdentificationFragment.setHent(mHent);
        mBankFragment.setInputDefaults(mInputDefaults);

        mContactFragment.setHent(mHent);
        mBankFragment.setInputDefaults(mInputDefaults);
    }

    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putInt(STATE_EXTRA_SELECTEDTAB, mSelectedTab);
        state.putSerializable(STATE_EXTRA_HENT, mHent);
        state.putSerializable(STATE_EXTRA_INPUT_DEFAULTS, mInputDefaults);
    }


    protected void invokeSave() {
        mGeneralFragment.save();
        mBankFragment.save();
        mIdentificationFragment.save();
        mContactFragment.save();
    }

    private boolean validateInputs() {
        String hentName = mHent.getHentName();
        if (Strings.isNullOrEmpty(hentName)) {
            displayErrorToast(R.string.errEmptyHentName);
            return false;
        }

        String hentAlias = mHent.getAlias();
        if (Strings.isNullOrEmpty(hentAlias)) {
            displayErrorToast(R.string.errEmptyAlias);
            return false;
        }

        String hentStreet = mHent.getStreet();
        if (Strings.isNullOrEmpty(hentStreet)) {
            displayErrorToast(R.string.errEmptyStreet);
            return false;
        }

        String hentPOBox = mHent.getPoBox();
        if (Strings.isNullOrEmpty(hentPOBox)) {
            displayErrorToast(R.string.errEmptyPOBox);
            return false;
        }

        String hentCity = mHent.getCity();
        if (Strings.isNullOrEmpty(hentCity)) {
            displayErrorToast(R.string.errEmptyCity);
            return false;
        }

        String hentZipCode = mHent.getZip();
        if (Strings.isNullOrEmpty(hentZipCode)) {
            displayErrorToast(R.string.errEmptyZipCode);
            return false;
        }

        EAN hentNo = mHent.getHentNo();
        if (hentNo == null) {
            displayErrorToast(R.string.errEmptyFarmNo);
            return false;
        }

        HentType hentType = mHent.getHentType();
        if (hentType == null) {
            displayErrorToast(R.string.errChooseHentType);
            return false;
        }

        IBAN iban = mHent.getBankAccountNo();
        if (iban != null && !IBANCheck.validate(iban)) {
            displayErrorToast(R.string.errInvalidAccountNo);
            return false;
        }


        String fiscalNo = mHent.getFiscalNo();
        if (!Strings.isNullOrEmpty(fiscalNo)) {
            if (!FiscalNoCheck.isValid(hentNo.getCountryCode(), fiscalNo)) {
                displayErrorToast(R.string.errInvalidFiscalNo);
                return false;
            }
        }

        String personalNo = mHent.getPersonalNo();
        if (!Strings.isNullOrEmpty(personalNo)) {
            if (personalNo != null && !PersonalNoCheck.isValid(hentNo.getCountryCode(), personalNo)) {
                displayErrorToast(R.string.errInvalidPersonalNo);
                return false;
            }
        }

        return true;
    }

    protected void displayErrorToast(int errResId) {
        displayErrorToast(getString(errResId));
    }

    protected void displayErrorToast(String err) {
        new ErrorToast(this).show(err);
    }

    public class HentTabListener<T extends HentFragment> implements ActionBar.TabListener {
        private final String mTag;
        private HentFragment mFragment;

        public HentTabListener(String tag, HentFragment fragment) {
            mTag = tag;
            mFragment = fragment;
        }

        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            ft.replace(android.R.id.content, mFragment, mTag);
        }

        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            ft.remove(mFragment);
        }

        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        }
    }


}
