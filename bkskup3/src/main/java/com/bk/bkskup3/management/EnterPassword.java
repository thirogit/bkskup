package com.bk.bkskup3.management;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.bk.bkskup3.R;
import com.google.common.base.Strings;

public class EnterPassword extends Activity implements OnEditorActionListener, TextWatcher {
    public static final String EXTRA_PASSWD_HASH = "passwd_hash";

    private EditText mPasswordEntry;
    private TextView mHeaderText;
    private Button mNextButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_password);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        // Check if this was the result of hitting the enter or "done" key
        if (actionId == EditorInfo.IME_NULL
                || actionId == EditorInfo.IME_ACTION_DONE
                || actionId == EditorInfo.IME_ACTION_NEXT) {
//            handleNext();
            return true;
        }
        return false;
    }

    private void updateUi() {
        String password = mPasswordEntry.getText().toString();
        final int length = password.length();

//        mHeaderText.setText(mUiStage.alphaHint);
        mNextButton.setEnabled(length > 0);

    }

    public void afterTextChanged(Editable s) {
        // Changing the text while error displayed resets to NeedToConfirm state
//        if (mUiStage == Stage.ConfirmWrong) {
//            mUiStage = Stage.NeedToConfirm;
//        }
        updateUi();
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

}
