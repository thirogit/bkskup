package com.bk.bkskup3.work.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;

public class LoadingDialogFragment extends DialogFragment {

    private static final String STATE_WAIT_TEXT = "state_wait_text";

    private String mWaitText;


    public void setWaitText(String waitText) {
        this.mWaitText = waitText;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            mWaitText = savedInstanceState.getString(STATE_WAIT_TEXT);
        }

        ProgressDialog progressDialog = new ProgressDialog(getActivity());

        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(mWaitText);
        progressDialog.setCanceledOnTouchOutside(false);

        return progressDialog;
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_WAIT_TEXT, mWaitText);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

}