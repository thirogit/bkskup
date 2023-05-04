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
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.bk.bkskup3.R;
import com.google.common.base.Strings;

public class ChangePassword extends PreferenceActivity
{
   public static final String EXTRA_PIN_MODE = "pin_mode";
   public static final String EXTRA_OLD_PASSWD_HASH = "old_passwd_hash";
   public static final String EXTRA_NEW_PASSWD_HASH = "new_passwd_hash";

   @Override
   public Intent getIntent()
   {
      Intent modIntent = new Intent(super.getIntent());
      modIntent.putExtra(EXTRA_SHOW_FRAGMENT, ChooseLockPasswordFragment.class.getName());
      modIntent.putExtra(EXTRA_NO_HEADERS, true);
      return modIntent;
   }

    protected boolean isValidFragment(String fragmentName) {
        return true;
    }

   @Override
   public void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      boolean pinMode  = getIntent().hasExtra(EXTRA_PIN_MODE);
      CharSequence msg = getText(pinMode ? R.string.changePINActivityLbl : R.string.changePasswordActivityLbl);
      showBreadCrumbs(msg, msg);
   }

   public static class ChooseLockPasswordFragment extends Fragment implements OnClickListener, OnEditorActionListener, TextWatcher
   {
      private EditText mPasswordEntry;
      private int mPasswordMinLength = 4;
      private Stage mUiStage = Stage.Introduction;
      private TextView mHeaderText;
      private String mFirstPin;
      private boolean mIsAlphaMode;
      private Button mNextButton;
      private String mOldPasswordHash;

//      private static final int CONFIRM_EXISTING_REQUEST = 58;
//      private static final int RESULT_FINISHED = RESULT_FIRST_USER;
      private static final long ERROR_MESSAGE_TIMEOUT = 3000;
      private static final int MSG_SHOW_ERROR = 1;

      private Handler mHandler = new Handler()
      {
         @Override
         public void handleMessage(Message msg)
         {
            if (msg.what == MSG_SHOW_ERROR)
            {
               updateStage((Stage) msg.obj);
            }
         }
      };

      protected enum Stage
      {

         Authentication(R.string.enter_your_old_password_header,
                        R.string.enter_your_old_pin_header,
                        R.string.ok),

         Introduction(R.string.choose_your_new_password_header,
                      R.string.choose_your_new_pin_header,
                      R.string.continueBtnCaption),

         NeedToConfirm(R.string.confirm_your_password_header,
                       R.string.confirm_your_pin_header,
                       R.string.ok),

         ConfirmWrong(R.string.confirm_passwords_dont_match,
                      R.string.confirm_pins_dont_match,
                      R.string.continueBtnCaption);

         Stage(int hintInAlpha, int hintInNumeric, int nextButtonText)
         {
            this.alphaHint = hintInAlpha;
            this.numericHint = hintInNumeric;
            this.buttonText = nextButtonText;
         }

         public final int alphaHint;
         public final int numericHint;
         public final int buttonText;
      }

      public ChooseLockPasswordFragment()
      {
      }





      @Override
      public void onCreate(Bundle savedInstanceState)
      {
         super.onCreate(savedInstanceState);
      }

      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
      {
         final Activity activity = getActivity();
         
         mPasswordMinLength = getResources().getInteger(R.integer.MIN_PASSWORD);
         final Intent activityIntent = activity.getIntent();
         mIsAlphaMode = !activityIntent.hasExtra(EXTRA_PIN_MODE);
         mOldPasswordHash = activityIntent.getStringExtra(EXTRA_OLD_PASSWD_HASH);

         View view = inflater.inflate(R.layout.choose_password, null);

         Button cancelButton = (Button) view.findViewById(R.id.cancel_button);
         cancelButton.setOnClickListener(this);
         mNextButton = (Button) view.findViewById(R.id.next_button);
         mNextButton.setOnClickListener(this);

         mPasswordEntry = (EditText) view.findViewById(R.id.password_entry);
         mPasswordEntry.setOnEditorActionListener(this);
         mPasswordEntry.addTextChangedListener(this);

         mHeaderText = (TextView) view.findViewById(R.id.headerText);

         int currentType = mPasswordEntry.getInputType();
         mPasswordEntry.setInputType(mIsAlphaMode ? currentType
                                        : (InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD));

         updateStage(Stage.Authentication);
         
          return view;
      }

      @Override
      public void onResume()
      {
         super.onResume();
         updateStage(mUiStage);
      }

      @Override
      public void onPause()
      {
         mHandler.removeMessages(MSG_SHOW_ERROR);
         super.onPause();
      }

      @Override
      public void onActivityResult(int requestCode, int resultCode, Intent data)
      {
         super.onActivityResult(requestCode, resultCode, data);
//         switch (requestCode)
//         {
//            case CONFIRM_EXISTING_REQUEST:
//               if (resultCode != Activity.RESULT_OK)
//               {
//                  getActivity().setResult(RESULT_FINISHED);
//                  getActivity().finish();
//               }
//               break;
//         }
      }

      protected void updateStage(Stage stage)
      {
         mUiStage = stage;
         updateUi();
      }

      private void handleNext()
      {
         final String password = mPasswordEntry.getText().toString();
         if (TextUtils.isEmpty(password))
         {
            return;
         }
         String errorMsg = null;
         
         if(mUiStage == Stage.Authentication)
         {
            if(authenticateWith(password))
            {
               mPasswordEntry.getText().clear();
               updateStage(Stage.Introduction);
            }
            else
            {
               errorMsg = getString(R.string.wrongPassword);
            }
         }
         else if (mUiStage == Stage.Introduction)
         {
            if (password.length() < mPasswordMinLength)
            {
               errorMsg = getString(mIsAlphaMode ? R.string.password_too_short : R.string.pin_too_short, mPasswordMinLength);
            }
            else
            {
               mFirstPin = password;
               updateStage(Stage.NeedToConfirm);
               mPasswordEntry.setText("");
            }
         }
         else if (mUiStage == Stage.NeedToConfirm)
         {
            if (mFirstPin.equals(password))
            {
               final Activity activity = getActivity();
               Intent resultIntent = new Intent();
               resultIntent.putExtra(EXTRA_NEW_PASSWD_HASH, password);
               activity.setResult(RESULT_OK,resultIntent);
               activity.finish();
            }
            else
            {
               updateStage(Stage.ConfirmWrong);
               CharSequence tmp = mPasswordEntry.getText();
               if (tmp != null)
               {
                  Selection.setSelection((Spannable) tmp, 0, tmp.length());
               }
            }
         }

         if (errorMsg != null)
         {
            showError(errorMsg, mUiStage);
         }
      }

      private boolean authenticateWith(String password)
      {
         if(Strings.isNullOrEmpty(mOldPasswordHash) || Strings.isNullOrEmpty(password))
            return false;

         return mOldPasswordHash.equals(password);
      }

      public void onClick(View v)
      {
         switch (v.getId())
         {
            case R.id.next_button:
               handleNext();
               break;

            case R.id.cancel_button:
               final Activity activity = getActivity();
               activity.setResult(RESULT_CANCELED);
               activity.finish();
               break;
         }
      }

      private void showError(String msg, final Stage next)
      {
         mHeaderText.setText(msg);
         Message mesg = mHandler.obtainMessage(MSG_SHOW_ERROR, next);
         mHandler.removeMessages(MSG_SHOW_ERROR);
         mHandler.sendMessageDelayed(mesg, ERROR_MESSAGE_TIMEOUT);
      }

      public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
      {
         // Check if this was the result of hitting the enter or "done" key
         if (actionId == EditorInfo.IME_NULL
            || actionId == EditorInfo.IME_ACTION_DONE
            || actionId == EditorInfo.IME_ACTION_NEXT)
         {
            handleNext();
            return true;
         }
         return false;
      }

      private void updateUi()
      {
         String password = mPasswordEntry.getText().toString();
         final int length = password.length();

         mHeaderText.setText(mIsAlphaMode ? mUiStage.alphaHint : mUiStage.numericHint);
         mNextButton.setEnabled(length > 0);

         mNextButton.setText(mUiStage.buttonText);
      }

      public void afterTextChanged(Editable s)
      {
         // Changing the text while error displayed resets to NeedToConfirm state
         if (mUiStage == Stage.ConfirmWrong)
         {
            mUiStage = Stage.NeedToConfirm;
         }
         updateUi();
      }

      public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

      public void onTextChanged(CharSequence s, int start, int before, int count) {}
   }
}
