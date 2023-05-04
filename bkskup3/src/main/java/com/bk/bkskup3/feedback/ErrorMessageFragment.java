package com.bk.bkskup3.feedback;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;

import com.bk.bkskup3.R;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 9/17/13
 * Time: 9:31 PM
 */

public class ErrorMessageFragment extends DialogFragment {
    private static final String STATE_CAPTION = "caption";
    private static final String STATE_MSG = "msg";
    private static final String STATE_RETRY = "retry";

    private String mErrorMsg;
    private String mCaption;
    private ErrorFragmentListener mListener;
    private boolean mRetryEnabled = true;

    public ErrorMessageFragment() {
    }

    public String getErrorMsg() {
        return mErrorMsg;
    }

    public void setErrorMsg(String mErrorMsg) {
        this.mErrorMsg = mErrorMsg;
    }

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String mCaption) {
        this.mCaption = mCaption;
    }

    public void setRetryEnabled(boolean retryEnabled) {
        this.mRetryEnabled = retryEnabled;
    }

    public boolean isRetryEnabled() {
        return mRetryEnabled;
    }

    public ErrorFragmentListener getListener() {
        return mListener;
    }

    public void setListener(ErrorFragmentListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mErrorMsg = savedInstanceState.getString(STATE_MSG);
            mCaption = savedInstanceState.getString(STATE_CAPTION);
            mRetryEnabled = savedInstanceState.getBoolean(STATE_RETRY);
        }
    }

    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putString(STATE_CAPTION, mCaption);
        state.putString(STATE_MSG, mErrorMsg);
        state.putBoolean(STATE_RETRY,mRetryEnabled);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.error_msg_dialog, container);

        getDialog().setTitle(mCaption);
        getDialog().setCanceledOnTouchOutside(false);

        TextView errMsgTextView = (TextView) view.findViewById(R.id.errMsgTextView);
        errMsgTextView.setText(mErrorMsg);

        Button abandonBtn = (Button) view.findViewById(R.id.btnAbandon);
        abandonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mListener != null) {
                    mListener.onAbandon();
                }
            }
        });


        Button retryBtn = (Button) view.findViewById(R.id.btnRetry);
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mListener != null) {
                    mListener.onRetry();
                }
            }
        });



        return view;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), getTheme()) {
            @Override
            public void onBackPressed() {
                if (mListener != null) {
                    mListener.onAbandon();
                }
            }
        };

        return dialog;
    }

    // This is to work around what is apparently a bug. If you don't have it
    // here the dialog will be dismissed on rotation, so tell it not to dismiss.
    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public interface ErrorFragmentListener {
        void onAbandon();

        void onRetry();
    }

    public static class ForwardErrorFragmentListener implements ErrorFragmentListener {

        @Override
        public void onAbandon() {

        }

        @Override
        public void onRetry() {

        }
    }


}
