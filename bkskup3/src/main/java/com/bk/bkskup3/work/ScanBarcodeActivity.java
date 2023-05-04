package com.bk.bkskup3.work;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bk.bkskup3.BkActivity;
import com.bk.widgets.LaserView;
import com.bk.bkskup3.R;
import com.bk.bkskup3.barcode.BarcodeServiceClient;
import com.bk.bkskup3.barcode.BarcodeServiceState;
import com.bk.widgets.actionbar.ActionBar;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/17/13
 * Time: 6:28 PM
 */
public class ScanBarcodeActivity extends BkActivity
{

   private BarcodeServiceClient mServiceClient = new BarcodeServiceClient(this);

   protected void onCreate(android.os.Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.barcode_input);

      final ActionBar bar = (ActionBar) findViewById(R.id.actionBar);
      bar.addAction(new ActionBar.TextAction(R.string.cancel,new ActionBar.ActionListener()
      {
        @Override
        public void onAction()
        {
           finish();
        }
      }));
      bar.setTitle(getTitle());
      bar.setIcon(getServiceStateImgResId(BarcodeServiceState.NoService));

      final LaserView laser = (LaserView) findViewById(R.id.bcOutputLaser);

      mServiceClient.attachObserver(new BarcodeServiceClient.BarcodeClientObserver()
      {
         @Override
         public void onBarcode(String bc)
         {
            TextView bcOutput = (TextView) findViewById(R.id.bcOutput);
            bcOutput.setText(bc);
            onGotBarcode(bc);
         }

         @Override
         public void onState(BarcodeServiceState state)
         {
            bar.setIcon(getServiceStateImgResId(state));
            laser.setVisibility(state == BarcodeServiceState.ScannerConnected ? View.VISIBLE : View.INVISIBLE);
         }
      });
   }

    private int getServiceStateImgResId(BarcodeServiceState newState)
    {
        switch(newState)
        {
            case ScannerConnecting:
                return R.drawable.barcode_bt;
            default:
            case NoService:
                return R.drawable.barcode_red;
            case ScannerConnected:
                return R.drawable.barcode_yes;
            case ScannerNotConnected:
                return R.drawable.barcode_no;
        }
    }

   private LinearLayout getFeedbackPlaceholder()
   {
      return (LinearLayout) findViewById(R.id.feedbackPlaceholder);
   }

   protected void onGotBarcode(String bc) {}

   protected View constructAskYesFeedBack(String question,final Runnable onYes)
   {
      LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                                     ViewGroup.LayoutParams.MATCH_PARENT);
      LinearLayout holder = new LinearLayout(this);
      holder.setOrientation(LinearLayout.HORIZONTAL);
      holder.setLayoutParams(layoutParams);

      TextView errorMsgTxtView = new TextView(this);

      errorMsgTxtView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                                   ViewGroup.LayoutParams.WRAP_CONTENT));
      errorMsgTxtView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
      errorMsgTxtView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_HORIZONTAL);

      errorMsgTxtView.setText(question);
      errorMsgTxtView.setTextColor(Color.RED);

      holder.addView(errorMsgTxtView);

      Button acceptBadChkSumBtn = new Button(this);
      acceptBadChkSumBtn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                                      ViewGroup.LayoutParams.WRAP_CONTENT));
      acceptBadChkSumBtn.setText(R.string.yesAllCaps);
      acceptBadChkSumBtn.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
      acceptBadChkSumBtn.setOnClickListener(new View.OnClickListener()
      {
        @Override
        public void onClick(View v)
        {
           if(onYes != null)
              onYes.run();
        }
      });

      holder.addView(acceptBadChkSumBtn);
      return holder;
   }

   protected View constructErrorFeedback(String errorMsg)
   {
      TextView errorMsgTxtView = new TextView(this);
      LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                   ViewGroup.LayoutParams.MATCH_PARENT);
      errorMsgTxtView.setLayoutParams(layoutParams);
      errorMsgTxtView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
      errorMsgTxtView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_HORIZONTAL);

      errorMsgTxtView.setText(errorMsg);
      errorMsgTxtView.setTextColor(Color.RED);
      return errorMsgTxtView;
   }

   protected void setFeedbackView(View feedbackView)
   {
      LinearLayout feedbackPlaceholder = getFeedbackPlaceholder();
      feedbackPlaceholder.removeAllViews();
      feedbackPlaceholder.addView(feedbackView);
   }

   protected void onStop()
   {
      super.onStop();
      mServiceClient.stop();
   }

   protected void onStart()
   {
      super.onStart();
      mServiceClient.start();
   }
}
