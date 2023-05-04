package com.bk.bkskup3.feedback;

import android.content.Context;
import android.widget.Toast;
import com.bk.bkskup3.R;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 8/13/12
 * Time: 11:04 AM
 */
public class ErrorToast
{
   private Context mContext;
   private Toast mToast;

   public ErrorToast(Context context)
   {
      this.mContext = context;
      this.mToast = Toast.makeText(mContext, "", Toast.LENGTH_LONG);
   }

   public void show(int msgId)
   {
      show(mContext.getResources().getString(msgId));
   }

   public void show(CharSequence msg)
   {
      mToast.setText(msg);
      mToast.getView().setBackgroundColor(mContext.getResources().getColor(R.color.validationerror));
      mToast.setDuration(Toast.LENGTH_SHORT);
      mToast.show();
   }

   public void hide()
   {
      mToast.cancel();
   }
}
