package com.bk.bkskup3.work;


import android.content.Intent;
import android.os.Bundle;

import com.bk.bkskup3.BkApplication;
import com.bk.bkskup3.R;
import com.bk.bkskup3.dao.BkStore;
import com.bk.bkskup3.dao.HentsStore;
import com.bk.bkskup3.model.EAN;
import com.bk.bkskup3.model.Hent;
import com.bk.bkskup3.model.HentObj;
import com.squareup.otto.InheritSubscribers;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/19/11
 * Time: 10:29 PM
 */
@InheritSubscribers
public class NewHentActivity extends HentActivity {
    public static final String EXTRA_HENT_FOR_INPUTS = "hent_for_inputs";

    private HentsStore mStore;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        BkApplication bkApplication = (BkApplication) getApplication();
        BkStore bkStore = bkApplication.getStore();
        mStore = bkStore.getHentsStore();
    }

    @Override
    protected HentObj load() {

        Intent intent = getIntent();
        Hent hent4Inputs = (Hent) intent.getSerializableExtra(EXTRA_HENT_FOR_INPUTS);

        HentObj hent = new HentObj();
        if(hent4Inputs != null) {
            hent.copyFrom(hent4Inputs);
        }
        if(hent.getHentType() == null)
        {
            hent.setHentType(mInputDefaults.getDefaultHentType());
        }

        return hent;
    }

    protected boolean validate(Hent hent) {

        final EAN hentNo = hent.getHentNo();

        HentObj hentWithSameHentNo = mStore.fetchHent(hentNo);

        if (hentWithSameHentNo != null) {
            displayErrorToast(getString(R.string.errDuplicatedNewHentFarmNo,hentNo.toString(), hentWithSameHentNo.getHentName()));
            return false;
        }

        return true;
    }

    @Override
    protected HentObj save(Hent hentToSave) {
        return mStore.insertHent(hentToSave);
    }
}
