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
import com.bk.bkskup3.dao.q.QCowClass;
import com.bk.bkskup3.feedback.ErrorToast;
import com.bk.bkskup3.model.CowClassObj;
import com.bk.bkskup3.model.CowSex;
import com.bk.bkskup3.utils.Numbers;
import com.bk.bkskup3.widgets.CowSexButton;
import com.google.common.collect.Iterables;
import com.mysema.query.types.expr.BooleanExpression;

import java.util.Collection;

import javax.inject.Inject;

import static com.mysema.query.support.QueryBuilder.where;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/29/2014
 * Time: 9:31 PM
 */
public abstract class CowClassActivity extends BkActivity {

    private static final String STATE_EXTRA_PREDEFSEX = "state_predefsex";
    private static final String STATE_EXTRA_CLASSID = "state_classid";

    public static final String EXTRA_CLASS_ID = "class_id";
    public static final String EXTRA_SAVED_CLASS = "saved_class";

    private EditText mClassNameBox;
    private EditText mClassCodeBox;
    private EditText mClassPricePerKgBox;
    private CowSexButton mCowSexBtn;
    private int mClassId;

    @Inject
    DefinitionsStore mDefinitionsStore;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cowclass);

        mClassNameBox = (EditText) findViewById(R.id.cowClassNameEditBox);
        mClassCodeBox = (EditText) findViewById(R.id.classCodeEditBox);
        mClassPricePerKgBox = (EditText) findViewById(R.id.cowClassPricePerKgBox);
        mCowSexBtn = (CowSexButton) findViewById(R.id.cowClassPredefSexBtn);

        if (savedInstanceState == null) {

            Intent intent = getIntent();
            mClassId = intent.getIntExtra(EXTRA_CLASS_ID, 0);

            if (mClassId != 0) {
                Collection<CowClassObj> cowClassObjs = mDefinitionsStore.fetchClasses(where(QCowClass.id.eq(mClassId)));
                CowClassObj classToEdit = Iterables.getFirst(cowClassObjs, null);

                if (classToEdit != null) {
                    mClassId = classToEdit.getId();

                    mClassNameBox.setText(classToEdit.getClassName());
                    mClassCodeBox.setText(classToEdit.getClassCode());
                    mClassPricePerKgBox.setText(Numbers.formatPrice(classToEdit.getPricePerKg()));
                    mCowSexBtn.setCowSex(classToEdit.getPredefSex());
                } else {
                    mClassId = 0;
                }
            } else {
                mCowSexBtn.setCowSex(CowSex.NONE);
            }

        } else {
            CowSex sex = (CowSex) savedInstanceState.getSerializable(STATE_EXTRA_PREDEFSEX);
            mCowSexBtn.setCowSex(sex);
            mClassId = savedInstanceState.getInt(STATE_EXTRA_CLASSID);
        }
    }

    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_EXTRA_PREDEFSEX, mCowSexBtn.getCowSex());
        outState.putSerializable(STATE_EXTRA_CLASSID, mClassId);
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
                saveCowClass();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;

    }

    private CowClassObj createCowClassFromBoxes() {
        CowClassObj classObj = new CowClassObj(mClassId);
        classObj.setClassName(mClassNameBox.getText().toString());
        classObj.setClassCode(mClassCodeBox.getText().toString());
        classObj.setPredefSex(mCowSexBtn.getCowSex());

        String priceStr = mClassPricePerKgBox.getText().toString();
        if (priceStr.length() > 0) {
            classObj.setPricePerKg(Double.parseDouble(priceStr));
        }
        return classObj;
    }

    private void saveCowClass() {

        if (validateInput()) {
            CowClassObj classObj = createCowClassFromBoxes();
            if (validate(classObj)) {
                save(classObj);
                Intent result = new Intent();
                result.putExtra(EXTRA_SAVED_CLASS,classObj);
                setResult(RESULT_OK,result);
                finish();
            }
        }
    }

    protected void save(CowClassObj classObj) {
        if (classObj.getId() != 0) {
            mDefinitionsStore.updateClass(classObj);
        } else {
            mDefinitionsStore.insertClass(classObj);
        }
    }

    protected boolean validate(CowClassObj classObj) {

        BooleanExpression predicate = QCowClass.classCode.eq(classObj.getClassCode());
        if (classObj.getId() != 0) {
            predicate = QCowClass.id.ne(classObj.getId()).and(predicate);
        }

        CowClassObj duplicate = Iterables.getFirst(mDefinitionsStore.fetchClasses(where(predicate)), null);

        if (duplicate != null) {
            displayErrorToast(R.string.errCowClassAlreadyDefined);
            return false;
        }
        return true;
    }

    private boolean validateInput() {

        if (mClassCodeBox.getText().length() == 0) {
            displayErrorToast(R.string.errEmptyClassCode);
            return false;
        }
        return true;
    }

    private void displayErrorToast(int msgId) {
        new ErrorToast(this).show(msgId);
    }
}
