package com.bk.bkskup3.work;

import android.app.Activity;
import android.content.Intent;

import com.bk.bkskup3.BkApplication;
import com.bk.bkskup3.R;
import com.bk.bkskup3.dao.HentsStore;
import com.bk.bkskup3.model.EAN;
import com.bk.bkskup3.model.HentObj;
import com.bk.countries.Countries;
import com.bk.countries.Country;

import javax.inject.Inject;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/19/13
 * Time: 11:42 PM
 */
public class ScanHentActivity extends ScanBarcodeActivity {
    public static final int RESULT_ADD_NEW_HENT = Activity.RESULT_FIRST_USER + 1001;
    public static final String EXTRA_HENT = "hent";
    public static final String EXTRA_NEWHENT_HENTNO = "hent_no";

    @Inject
    HentsStore mHentsStore;

    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    protected void onGotBarcode(String bc) {
        final EAN hentNo = EAN.fromString(bc);

        if (hentNo == null) {
            setFeedbackView(constructErrorFeedback(getResources().getString(R.string.errNotAFarmNo)));
        } else {
            Country country = Countries.getCountries().getCountryIsoCode2(hentNo.getCountryCode());
            if (country.getHentNoLength() != hentNo.length()) {
                setFeedbackView(constructErrorFeedback(getResources().getString(R.string.errBadFarmNoLengthFmt, country.getHentNoLength())));
            } else {
                HentObj hent = findHentWithHentNo(hentNo);

                if (hent != null) {
                    finishWithHent(hent);
                } else {
                    setFeedbackView(constructAskYesFeedBack(getResources().getString(R.string.askAddNewHentWhenNotFound), new Runnable() {
                        @Override
                        public void run() {
                            finishWithAddNewHent(hentNo);
                        }
                    }));

                }
            }
        }
    }

    private HentObj findHentWithHentNo(EAN hentNo) {
        return mHentsStore.fetchHent(hentNo);
    }

    private void finishWithAddNewHent(EAN hentNo) {
        Intent result = new Intent();
        result.putExtra(EXTRA_NEWHENT_HENTNO, hentNo);
        setResult(RESULT_ADD_NEW_HENT, result);
        finish();
    }

    private void finishWithHent(HentObj hent) {
        Intent result = new Intent();
        result.putExtra(EXTRA_HENT, hent);
        setResult(RESULT_OK, result);
        finish();
    }

}
