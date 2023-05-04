package com.bk.bkskup3.management;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.bk.bkskup3.BkActivity;
import com.bk.bkskup3.R;
import com.bk.bkskup3.dao.DefinitionsStore;
import com.bk.bkskup3.dao.q.QHerd;
import com.bk.bkskup3.feedback.ErrorToast;
import com.bk.bkskup3.model.Herd;
import com.bk.bkskup3.model.HerdObj;
import com.google.common.collect.Iterables;

import javax.inject.Inject;

import static com.mysema.query.support.QueryBuilder.where;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/9/2014
 * Time: 8:52 PM
 */
public abstract class HerdActivity extends BkActivity {

    public static final String STATE_EXTRA_HERDID = "state_herd_id";
    public static final String EXTRAS_HERDID = "herd_id";

    private EditText mHerdNumberEditBox;
    private EditText mHerdStreetEditBox;
    private EditText mHerdPOBoxEditBox;
    private EditText mHerdCityEditBox;
    private EditText mHerdZipEditBox;

    private int mHerdId;
    @Inject
    DefinitionsStore mDefinitionsStore;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.herd);

        mHerdNumberEditBox = (EditText) findViewById(R.id.herdNumberEditBox);
        mHerdStreetEditBox = (EditText) findViewById(R.id.herdStreetEditBox);
        mHerdPOBoxEditBox = (EditText) findViewById(R.id.herdPOBoxEditBox);
        mHerdCityEditBox = (EditText) findViewById(R.id.herdCityEditBox);
        mHerdZipEditBox = (EditText) findViewById(R.id.herdZipEditBox);

        if (savedInstanceState == null) {

            Intent intent = getIntent();
            mHerdId = intent.getIntExtra(EXTRAS_HERDID, 0);

            if (mHerdId != 0) {

                Herd herdToEdit = Iterables.getFirst(mDefinitionsStore.fetchHerds(where(QHerd.id.eq(mHerdId))),null);

                if (herdToEdit != null) {
                    setInputs(herdToEdit);
                } else {
                    mHerdId = 0;
                }
            }
        } else {
            mHerdId = savedInstanceState.getInt(STATE_EXTRA_HERDID);
        }
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
                if(validateInput()) {
                    if (validate(createHerd())) {
                        saveHerd(createHerd());
                        setResult(RESULT_OK);
                        finish();
                    }
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    protected void saveHerd(HerdObj herd) {

        if (mHerdId == 0) { //inserting new herd
            mDefinitionsStore.insertHerd(herd);
        } else { //updating
            mDefinitionsStore.updateHerd(herd);
        }
    }

    private boolean validateInput() {
        ErrorToast toast = new ErrorToast(this);

        if (mHerdStreetEditBox.getText().length() == 0) {
            toast.show(R.string.errEnterStreet);
            return false;
        }

        if (mHerdPOBoxEditBox.getText().length() == 0) {
            toast.show(R.string.errEnterPOBox);
            return false;
        }

        if (mHerdCityEditBox.getText().length() == 0) {
            toast.show(R.string.errEnterCity);
            return false;
        }

        if (mHerdZipEditBox.getText().length() == 0) {
            toast.show(R.string.errEnterZip);
            return false;
        }
        return true;
    }

    private boolean validate(HerdObj herd) {
        int herdNo = herd.getHerdNo();
        Herd existingHerd =  Iterables.getFirst(mDefinitionsStore.fetchHerds(where(QHerd.number.eq(herdNo))),null);

        if (mHerdId == 0) { //inserting new herd
            if (existingHerd == null) {
                return true;
            }
        } else { //updating
            if (existingHerd == null || mHerdId == existingHerd.getId()) {
                return true;
            }
        }

        (new ErrorToast(this)).show(getString(R.string.errDuplicateHerdNo, herdNo));

        return false;
    }


    protected void setInputs(Herd herd) {
        mHerdNumberEditBox.setText(formatHerdNumber(herd.getHerdNo()));
        mHerdStreetEditBox.setText(herd.getStreet());
        mHerdPOBoxEditBox.setText(herd.getPoBox());
        mHerdCityEditBox.setText(herd.getCity());
        mHerdZipEditBox.setText(herd.getZip());
    }

    private String formatHerdNumber(int herdNo) {
        return String.format("%03d", herdNo);
    }

    protected HerdObj createHerd() {
        HerdObj herd = new HerdObj(mHerdId);

        int herdNo = Integer.parseInt(mHerdNumberEditBox.getText().toString());
        herd.setHerdNo(herdNo);
        herd.setStreet(mHerdStreetEditBox.getText().toString());
        herd.setPoBox(mHerdPOBoxEditBox.getText().toString());
        herd.setCity(mHerdCityEditBox.getText().toString());
        herd.setZip(mHerdZipEditBox.getText().toString());


        return herd;
    }


}
