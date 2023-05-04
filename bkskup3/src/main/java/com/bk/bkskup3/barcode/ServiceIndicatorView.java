package com.bk.bkskup3.barcode;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.bk.bkskup3.R;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/10/13
 * Time: 9:09 AM
 */
public class ServiceIndicatorView extends ImageView
{

   private BarcodeServiceState mState = BarcodeServiceState.NoService;

   public ServiceIndicatorView(Context context)
   {
      super(context);
   }

   public ServiceIndicatorView(Context context, AttributeSet attrs)
   {
      super(context, attrs);
   }

   public BarcodeServiceState getState()
   {
      return mState;
   }

   public void setState(BarcodeServiceState newState)
   {
      switch(newState)
      {
         case ScannerConnecting:
            setBackgroundResource(R.drawable.barcode_bt);
            break;
         default:
         case NoService:
            setBackgroundResource(R.drawable.barcode_red);
            break;
         case ScannerConnected:
            setBackgroundResource(R.drawable.barcode);
            break;
         case ScannerNotConnected:
            setBackgroundResource(R.drawable.barcode_no);
            break;
      }
   }


}
