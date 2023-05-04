package com.bk.bkskup3.work.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.bk.bkskup3.R;
import com.bk.bkskup3.model.EAN;
import com.bk.bkskup3.model.IBAN;
import com.bk.bkskup3.widgets.AccountNoEditText;
import com.bk.bkskup3.work.AccountNoInputActivity;
import com.bk.bkskup3.work.OcrNumberActivity;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/22/2014
 * Time: 11:40 AM
 */
public class HentBankFragment extends HentFragment {

//    private static final String STATE_EXTRA_IBAN = "bank_account_no";
//    private static final String STATE_EXTRA_BANKNAME = "bank_name";
    private static final int REQUEST_CODE_INPUT_ACCOUNTNO = 4001;
    private static final int REQUEST_CODE_OCR_ACCOUNTNO = 4002;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.hent_bank, container, false);
    }

    public void onViewCreated(View view, Bundle state) {
        super.onViewCreated(view, state);
        ensureContent();
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateInputs();
    }

    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
//        state.putSerializable(STATE_EXTRA_IBAN, getIBAN());
//        state.putString(STATE_EXTRA_BANKNAME, getBankName());

    }

    private void ensureContent() {
       getAccountNoBox().setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startIBANInputActivity();
           }
       });

//       getOcrAccountNoBtn().setOnClickListener(new View.OnClickListener() {
//           @Override
//           public void onClick(View v) {
//               startOcrAccountNoActivity();
//           }
//       });

    }

    private void startIBANInputActivity() {
        Intent inputIntent = new Intent(getActivity(), AccountNoInputActivity.class);
        inputIntent.putExtra(AccountNoInputActivity.EXTRA_ACCOUNTNO,mInput.getBankAccountNo());

        EAN hentNo = mInput.getHentNo();
        if(hentNo != null) {
            inputIntent.putExtra(AccountNoInputActivity.EXTRA_COUNTRY, hentNo.getCountryCode());
        }

        startActivityForResult(inputIntent,REQUEST_CODE_INPUT_ACCOUNTNO);
    }

    private void startOcrAccountNoActivity() {
        Intent inputIntent = new Intent(getActivity(), OcrNumberActivity.class);
//        inputIntent.putExtra(AccountNoInputActivity.EXTRA_ACCOUNTNO,mInput.getBankAccountNo());

//        EAN hentNo = mInput.getHentNo();
//        if(hentNo != null) {
//            inputIntent.putExtra(AccountNoInputActivity.EXTRA_COUNTRY, hentNo.getCountryCode());
//        }

        startActivityForResult(inputIntent,REQUEST_CODE_OCR_ACCOUNTNO);
    }

    protected String getBankName() {
        return getBankNameBox().getText().toString();
    }

    protected EditText getBankNameBox() {
        return (EditText) findViewById(R.id.hentBankName);
    }

    protected void updateBankName() {
        getBankNameBox().setText(mInput.getBankName());
    }


    protected AccountNoEditText getAccountNoBox() {
        return (AccountNoEditText) findViewById(R.id.hentAccountNo);
    }

    protected ImageButton getOcrAccountNoBtn() {
        return (ImageButton) findViewById(R.id.ocrAccountNoBtn);
    }

    protected void updateIBAN() {
        getAccountNoBox().setIBAN(mInput.getBankAccountNo());
    }

    private View findViewById(int viewId) {
        return getView().findViewById(viewId);
    }


    public void updateInputs() {
        if (getView() != null) {
            updateIBAN();
            updateBankName();
        }
    }

    public void save() {
        if (getView() != null) {
            mInput.setBankName(getBankName());
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_INPUT_ACCOUNTNO) {
            IBAN accountNo = (IBAN) data.getSerializableExtra(AccountNoInputActivity.EXTRA_ACCOUNTNO);
            mInput.setBankAccountNo(accountNo);
            updateIBAN();
        }
    }


}