package com.bk.bkskup3.work;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.bk.bkskup3.R;
import com.bk.bkskup3.feedback.ErrorToast;
import com.bk.bkskup3.model.IBAN;
import com.bk.bkskup3.utils.check.IBANCheck;
import com.bk.bkskup3.widgets.CountrySpinner;
import com.bk.countries.Country;
import com.bk.widgets.actionbar.ActionBar;
import com.bk.widgets.spinner.AdapterView;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 8/12/12
 * Time: 12:13 AM
 */
public class AccountNoInputActivity extends Activity {
    public static final String EXTRA_ACCOUNTNO = "account_no";
    public static final String EXTRA_COUNTRY = "country";

    private final static int DEFAULT_IBAN_LENGTH = 28;
    private final static char BACKSPACE_CHAR = '\u0008';

    class LengthValidationResult {
        boolean valid;
        int requiredLength;

        LengthValidationResult(boolean valid, int requiredLength) {
            this.valid = valid;
            this.requiredLength = requiredLength;
        }

        public boolean isValid() {
            return valid;
        }

        public int getRequiredLength() {
            return requiredLength;
        }
    }

    class DigitClickListener implements View.OnClickListener {
        private char digitChar;

        DigitClickListener(char digitChar) {
            this.digitChar = digitChar;
        }

        @Override
        public void onClick(View v) {
            onDigitClick(digitChar);
        }
    }

    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.iban_input);

        Button clearBtn = (Button) findViewById(R.id.clear);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClear();
            }
        });

        Button backspaceBtn = (Button) findViewById(R.id.backspace);
        backspaceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackspace();
            }
        });

        Button digit0Btn = (Button) findViewById(R.id.digit0);
        digit0Btn.setOnClickListener(new DigitClickListener('0'));

        Button digit1Btn = (Button) findViewById(R.id.digit1);
        digit1Btn.setOnClickListener(new DigitClickListener('1'));

        Button digit2Btn = (Button) findViewById(R.id.digit2);
        digit2Btn.setOnClickListener(new DigitClickListener('2'));

        Button digit3Btn = (Button) findViewById(R.id.digit3);
        digit3Btn.setOnClickListener(new DigitClickListener('3'));

        Button digit4Btn = (Button) findViewById(R.id.digit4);
        digit4Btn.setOnClickListener(new DigitClickListener('4'));

        Button digit5Btn = (Button) findViewById(R.id.digit5);
        digit5Btn.setOnClickListener(new DigitClickListener('5'));

        Button digit6Btn = (Button) findViewById(R.id.digit6);
        digit6Btn.setOnClickListener(new DigitClickListener('6'));

        Button digit7Btn = (Button) findViewById(R.id.digit7);
        digit7Btn.setOnClickListener(new DigitClickListener('7'));

        Button digit8Btn = (Button) findViewById(R.id.digit8);
        digit8Btn.setOnClickListener(new DigitClickListener('8'));

        Button digit9Btn = (Button) findViewById(R.id.digit9);
        digit9Btn.setOnClickListener(new DigitClickListener('9'));

        ActionBar actionBar = (ActionBar) findViewById(R.id.actionBar);
        ActionBar.TextAction okAction = new ActionBar.TextAction(R.string.ok, new ActionBar.ActionListener() {
            @Override
            public void onAction() {
                onOk();
            }
        });

        ActionBar.TextAction cancelAction = new ActionBar.TextAction(android.R.string.cancel, new ActionBar.ActionListener() {
            @Override
            public void onAction() {
                onCancel();
            }
        });

        actionBar.addAction(okAction);
        actionBar.addAction(cancelAction);
        actionBar.setTitle(getTitle());

        Intent intent = getIntent();


        final CountrySpinner countrySpinner = getCountrySpinner();
        if (intent != null) {
            IBAN iban = (IBAN) intent.getSerializableExtra(EXTRA_ACCOUNTNO);
            if (iban != null) {
                String countryCode = iban.getCountry().getCode2A();
                String numberPart = iban.getNumber();

                    countrySpinner.setSelectedCountry(countryCode);
                    EditText inputBox = getInputBox();
                    inputBox.setText(numberPart);
                    inputBox.setSelection(numberPart.length());

            } else {
                String countryCd = intent.getStringExtra(EXTRA_COUNTRY);
                if (countryCd != null) {
                    countrySpinner.setSelectedCountry(countryCd);
                }
            }
        }

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onCountryChange(countrySpinner.getCountryAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        onCountryChange(countrySpinner.getSelectedCountry());

    }

    private void onBackspace() {
        EditText inputBox = getInputBox();
        Editable text = inputBox.getText();
        int length = text.length();
        text.delete(length-1,length);
    }

    private void onCountryChange(Country country) {
        int numberPartLength = 26;
        if (country != null) {
            numberPartLength = getIBANNumberPartLength(country);
        }
        EditText inputBox = getInputBox();
        inputBox.setFilters(new InputFilter[]{new InputFilter.LengthFilter(numberPartLength)});


        Editable text = inputBox.getText();
        if (text.length() > numberPartLength) {
            text.delete(0, text.length() - numberPartLength);
        }
    }

    private void onClear() {
        getInputBox().getText().clear();
    }

    protected int getIBANLength(Country country) {
        return DEFAULT_IBAN_LENGTH;
    }

    protected int getIBANNumberPartLength(Country country) {
        return getIBANLength(country) - 2;
    }

    protected LengthValidationResult validateLengthForCountry(Country country, String numberPart) {
        int ibanNumberPaLen = getIBANNumberPartLength(country);
        return new LengthValidationResult(numberPart.length() == ibanNumberPaLen, ibanNumberPaLen);
    }

    protected boolean validateCheckSum(IBAN iban) {
        return IBANCheck.validate(iban);
    }

    protected boolean isAllowOverrideBadChkSum() {
        return false;
    }

    EditText getInputBox() {
        return (EditText) findViewById(R.id.inputBox);
    }

    CountrySpinner getCountrySpinner() {
        return (CountrySpinner) findViewById(R.id.countrySpinner);
    }

    void onDigitClick(char digit) {
        EditText inputBox = getInputBox();
//        inputBox.onKeyDown(digit, new KeyEvent(KeyEvent.ACTION_DOWN, digit));
//        inputBox.onKeyUp(digit, new KeyEvent(KeyEvent.ACTION_UP, digit));
        Editable text = inputBox.getText();
        text.append(digit);
    }


    String getInputNumberPart() {
        EditText inputBox = getInputBox();
        return inputBox.getText().toString();
    }

    void onOk() {
        Country selectedCountry = getCountrySpinner().getSelectedCountry();
        if (selectedCountry == null) {
            displayErrorToast(R.string.errNoCountryCode);
            return;
        }

        String numberPart = getInputNumberPart();

        LengthValidationResult validationResult = validateLengthForCountry(selectedCountry, numberPart);
        if (!validationResult.isValid()) {
            new ErrorToast(this).show(getString(R.string.errInvalidEANLength, validationResult.getRequiredLength()));
            return;
        }

        String input = selectedCountry.getCode2A() + numberPart;
        final IBAN iban = IBAN.fromString(input);

        if (validateCheckSum(iban)) {
            finishWithIBAN(iban);
        } else {
            if (isAllowOverrideBadChkSum()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.askOverrideBadChkSum);
                builder.setPositiveButton(R.string.yesAllCaps, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishWithIBAN(iban);
                    }
                });

                builder.setNegativeButton(R.string.noAllCaps, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            } else {
                displayErrorToast(R.string.errInvalidChkSumDigit);
            }
        }
    }

    private void displayErrorToast(int errTextResId) {
        new ErrorToast(this).show(errTextResId);
    }

    void finishWithIBAN(IBAN iban) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_ACCOUNTNO, iban);
        resultIntent.putExtra(EXTRA_COUNTRY, iban.getCountry().getCode2A());
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    void onCancel() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
