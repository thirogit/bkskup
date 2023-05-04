package com.bk.bkskup3.management;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.bk.bkskup3.BkActivity;
import com.bk.bkskup3.R;
import com.bk.bkskup3.dao.DefinitionsStore;
import com.bk.bkskup3.dao.q.QDeduction;
import com.bk.bkskup3.feedback.ErrorToast;
import com.bk.bkskup3.model.DeductionDefinition;
import com.bk.bkskup3.model.DeductionDefinitionObj;
import com.bk.bkskup3.utils.DoubleFormatter;
import com.bk.bkskup3.utils.NumberFormatDefinition;
import com.google.common.collect.Iterables;
import com.mysema.query.types.expr.BooleanExpression;

import javax.inject.Inject;

import static com.mysema.query.support.QueryBuilder.where;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/29/2014
 * Time: 9:31 PM
 */
public abstract class DeductionActivity extends BkActivity {

    private final static double FRACTION_DELTA = 0.001;

    public static final String EXTRA_DEDUCTION_ID = "deduction_id";

    protected EditText mDeductionReasonBox;
    protected EditText mDeductionCodeBox;
    protected EditText mDeductionFractionBox;
    protected Switch mDeductAlwaysBtn;
    protected int mDeductionId;

    @Inject
    DefinitionsStore mDefinitionsStore;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deduction);

        mDeductionReasonBox = (EditText) findViewById(R.id.deductionReasonBox);
        mDeductionCodeBox = (EditText) findViewById(R.id.deductionCodeBox);
        mDeductionFractionBox = (EditText) findViewById(R.id.fractionEditBox);
        mDeductAlwaysBtn = (Switch) findViewById(R.id.deductAlwaysSwitchBtn);

        Button plusBtn = (Button) findViewById(R.id.deductionFractionPlusBtn);
        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFractionBy(FRACTION_DELTA);
            }
        });
        Button minusBtn = (Button) findViewById(R.id.deductionFractionMinusBtn);
        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFractionBy(-FRACTION_DELTA);
            }
        });


        mDeductionId = getIntent().getIntExtra(EXTRA_DEDUCTION_ID, 0);

        if (mDeductionId != 0) {
            DeductionDefinition input = mDefinitionsStore.fetchDeduction(mDeductionId);
            mDeductionReasonBox.setText(input.getReason());
            mDeductionCodeBox.setText(input.getCode());
            setFraction(input.getFraction());
            mDeductAlwaysBtn.setChecked(input.isEnabledByDefault());
        }

    }

    protected void changeFractionBy(double delta) {
        Double fraction = getFraction();
        if (fraction == null) {
            fraction = delta;
        } else {
            fraction += delta;

            if (fraction < 0.0) {
                fraction = 0.0;
            }
        }
        setFraction(fraction);
    }

    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
//        outState.putSerializable(STATE_EXTRA_PREDEFSEX, mCowSexBtn.getCowSex());
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
                saveDeduction();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;

    }

    private DeductionDefinitionObj createDeductionFromBoxes() {
        DeductionDefinitionObj deductionObj = new DeductionDefinitionObj(mDeductionId);
        deductionObj.setReason(mDeductionReasonBox.getText().toString());
        deductionObj.setCode(mDeductionCodeBox.getText().toString());
        deductionObj.setFraction(getFraction());
        deductionObj.setEnabledByDefault(mDeductAlwaysBtn.isChecked());
        return deductionObj;
    }

    private Double getFraction() {
        Double value = null;
        if (mDeductionFractionBox.getText().length() > 0) {
            value = Double.parseDouble(mDeductionFractionBox.getText().toString()) / 100.0;
        }
        return value;
    }

    protected void setFraction(Double vatRate) {
        if (vatRate != null) {
            NumberFormatDefinition vatRateFormat = new NumberFormatDefinition('.', 1);
            String fractionStr = new DoubleFormatter(vatRateFormat).format(vatRate * 100.0);
            mDeductionFractionBox.setText(fractionStr);
        } else {
            mDeductionFractionBox.getText().clear();
        }
    }

    private void saveDeduction() {

        if (validateInput()) {
            DeductionDefinitionObj deductionObj = createDeductionFromBoxes();
            if (validate(deductionObj)) {
                save(deductionObj);
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    protected void save(DeductionDefinitionObj deductionObj) {

        if (deductionObj.getId() != 0) {
            mDefinitionsStore.updateDeduction(deductionObj);
        } else {
            mDefinitionsStore.insertDeduction(deductionObj);
        }


    }

    private boolean validate(DeductionDefinitionObj deduction) {
        BooleanExpression predicate = QDeduction.code.eq(deduction.getCode());
        if(mDeductionId != 0) {
            predicate = predicate.and(QDeduction.id.ne(mDeductionId));
        }

        DeductionDefinitionObj duplicate = Iterables.getFirst(mDefinitionsStore.fetchDeductions(where(predicate)), null);

        if (duplicate != null) {
            displayErrorToast(R.string.errDeductionAlreadyDefined);
            return false;
        }
        return true;
    }

    private boolean validateInput() {

        if (mDeductionCodeBox.getText().length() == 0) {
            displayErrorToast(R.string.errEmptyDeductionCode);
            return false;
        }

        if (mDeductionReasonBox.getText().length() == 0) {
            displayErrorToast(R.string.errEmptyDeductionReason);
            return false;
        }

        if (getFraction() == null) {
            displayErrorToast(R.string.errEmptyDeductionFraction);
            return false;
        }

        return true;
    }

    private void displayErrorToast(int msgId) {
        new ErrorToast(this).show(msgId);
    }
}
