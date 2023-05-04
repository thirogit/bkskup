package com.bk.bkskup3.work.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;

/**
 * Created by SG0891787 on 10/5/2017.
 */
public class BusyFragment extends BusDialogFragment {

    private static String BUSY_TEXT_ARGUMENT = "busy_text";

    public static BusyFragment newInstance(String busyText) {
        BusyFragment frag = new BusyFragment();
        Bundle args = new Bundle();
        args.putString(BUSY_TEXT_ARGUMENT, busyText);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        String busyText = arguments.getString(BUSY_TEXT_ARGUMENT);

        ProgressDialog dialog = new ProgressDialog(getActivity(), getTheme());
        dialog.setIndeterminate(true);
        dialog.setMessage(busyText);
        return dialog;

    }


}
