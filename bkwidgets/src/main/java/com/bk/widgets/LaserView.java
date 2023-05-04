package com.bk.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import com.bk.widgets.actionbar.R;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/3/13
 * Time: 11:34 PM
 */
public class LaserView extends View
{
   private final Paint paint;
   private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
   private static final long ANIMATION_DELAY = 80L;
   private final int laserColor;
   private int scannerAlpha;

   private static final int MSG_WHAT_DRAW = 1001;

   private Handler drawTimer = new Handler()
   {
      @Override
      public void handleMessage(Message msg)
      {
         postInvalidate();
      }
   };

   public LaserView(Context context, AttributeSet attrs)
   {
      super(context, attrs);

      paint = new Paint();
      Resources resources = getResources();
      laserColor = resources.getColor(R.color.laser);
      scannerAlpha = 0;
   }

   protected void onDetachedFromWindow()
   {
      super.onDetachedFromWindow();
      drawTimer.removeMessages(MSG_WHAT_DRAW);
   }

   @Override
   public void onDraw(Canvas canvas)
   {
      drawTimer.removeMessages(MSG_WHAT_DRAW);

      int width = getWidth();
      int height = getHeight();

      paint.setColor(laserColor);
      paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
      scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
      int middle = height / 2;
      canvas.drawRect(2, middle - 1, width - 1, middle + 2, paint);

      drawTimer.sendEmptyMessageDelayed(MSG_WHAT_DRAW,ANIMATION_DELAY);
   }


}
