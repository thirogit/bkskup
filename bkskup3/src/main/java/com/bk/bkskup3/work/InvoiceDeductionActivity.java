package com.bk.bkskup3.work;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.bk.bkskup3.R;
import com.bk.bkskup3.feedback.ErrorToast;
import com.bk.bkskup3.utils.DoubleFormatter;
import com.bk.bkskup3.utils.NumberFormatDefinition;
import com.bk.bkskup3.work.input.DeductionInput;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/20/2014
 * Time: 10:15 PM
 */
public abstract class InvoiceDeductionActivity extends Activity {
    private final static double FRACTION_DELTA = 0.001;

    public static final String EXTRA_DEDUCTION_INPUT = "deduction_input";

    protected EditText mDeductionReasonBox;
    protected EditText mDeductionCodeBox;
    protected EditText mDeductionFractionBox;

    protected DeductionInput mDeduction;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invoice_deduction);

        mDeductionReasonBox = (EditText) findViewById(R.id.deductionReasonBox);
        mDeductionCodeBox = (EditText) findViewById(R.id.deductionCodeBox);
        mDeductionFractionBox = (EditText) findViewById(R.id.fractionEditBox);

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

        mDeduction = (DeductionInput) getIntent().getSerializableExtra(EXTRA_DEDUCTION_INPUT);

        mDeductionReasonBox.setText(mDeduction.getReason());
        mDeductionCodeBox.setText(mDeduction.getCode());
        setFraction(mDeduction.getFraction());

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
                saveInput();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;

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

    private void saveInput() {

        if (validateInput()) {
                mDeduction.setReason(mDeductionReasonBox.getText().toString());
                mDeduction.setCode(mDeductionCodeBox.getText().toString());
                mDeduction.setFraction(getFraction());

                Intent result = new Intent();
                result.putExtra(EXTRA_DEDUCTION_INPUT,mDeduction);

                setResult(RESULT_OK,result);
                finish();
        }
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
