package com.bk.bkskup3.print.widgets;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.bk.bkskup3.R;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 8/15/12
 * Time: 6:32 PM
 */
public class PrinterConnectionIndicator extends ImageView
{
   public enum ConnectionStatus
   {
      Good,
      Bad,
      Connecting
   };

   private ConnectionStatus mStatus = ConnectionStatus.Bad;


   public PrinterConnectionIndicator(Context context)
   {
      this(context, null);
   }

   public PrinterConnectionIndicator(Context context, AttributeSet attrs)
   {
      super(context, attrs);
   }

   public ConnectionStatus getStatus()
   {
      return mStatus;
   }

   public void setStatus(ConnectionStatus status)
   {
      updateIndicator(status);
   }

   private void updateIndicator(ConnectionStatus status)
   {

      if(mStatus == ConnectionStatus.Connecting)
      {
         ((AnimationDrawable)getBackground()).stop();
      }

      this.mStatus = status;

      switch (mStatus)
      {
         case Bad:
            setBackgroundResource(R.drawable.printer_bad);
            invalidate();
            break;
         case Good:
            setBackgroundResource(R.drawable.printer_good);
            invalidate();
            break;
         case Connecting:
            setBackgroundResource(R.drawable.printer_connecting);
            ((AnimationDrawable)getBackground()).start();
            invalidate();
            break;
      }
   }
}
