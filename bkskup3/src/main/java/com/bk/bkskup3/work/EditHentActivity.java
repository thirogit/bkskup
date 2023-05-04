package com.bk.bkskup3.work;

import android.content.Intent;
import android.os.Bundle;

import com.bk.bkskup3.BkApplication;
import com.bk.bkskup3.R;
import com.bk.bkskup3.dao.BkStore;
import com.bk.bkskup3.dao.HentsStore;
import com.bk.bkskup3.dao.q.QHent;
import com.bk.bkskup3.model.EAN;
import com.bk.bkskup3.model.Hent;
import com.bk.bkskup3.model.HentObj;
import com.google.common.collect.Iterables;
import com.squareup.otto.InheritSubscribers;

import java.util.Collection;

import javax.inject.Inject;

import static com.mysema.query.support.QueryBuilder.where;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/19/11
 * Time: 10:37 PM
 */
@InheritSubscribers
public class EditHentActivity extends HentActivity {
    public static final String EXTRA_HENT_TO_EDIT_ID = "hent_to_edit_id";
    public static final String EXTRA_HENT_TO_EDIT_HENTNO = "hent_to_edit_hentno";

    private int mEditedHentId;

    @Inject
    HentsStore mStore;

    public void onCreate(Bundle savedState) {

        super.onCreate(savedState);
        BkApplication bkApplication = (BkApplication) getApplication();
        BkStore bkStore = bkApplication.getStore();
        mStore = bkStore.getHentsStore();
    }

    protected void onResume() {
        super.onResume();
    }

    @Override
    protected HentObj load() {

        Intent intent = getIntent();
        HentObj hent = null;

        int hentId = intent.getIntExtra(EXTRA_HENT_TO_EDIT_ID, 0);
        if (hentId != 0) {
            hent = mStore.fetchHent(hentId);
        } else {
            EAN hentNo = (EAN) intent.getSerializableExtra(EXTRA_HENT_TO_EDIT_HENTNO);
            hent = mStore.fetchHent(hentNo);
        }

        mEditedHentId = hent.getId();
        return hent;
    }


    @Override
    protected boolean validate(Hent hent) {

        EAN hentNo = hent.getHentNo();

        Collection<HentObj> hentObjs = mStore.fetchHents(where(QHent.hentNo.eq(hentNo.toString()).and(QHent.id.ne(mEditedHentId))));
        HentObj hentWithSameFarmNo = Iterables.getFirst(hentObjs, null);
        if (hentWithSameFarmNo != null) {
            displayErrorToast(getString(R.string.errDuplicatedEditHentFarmNo, hentNo.toString(), hentWithSameFarmNo.getHentName()));
            return false;
        }
        return true;
    }

    @Override
    protected HentObj save(Hent hent) {

        mStore.updateHent(hent);
        HentObj hentObj = new HentObj(hent.getId());
        hentObj.copyFrom(hent);

        return hentObj;
    }


}
