package com.bk.bkskup3.feedback;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
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
//      LayoutInflater li = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE );
//      View toastView = li.inflate(R.layout.toast_hint_layout, null);
//      TextView text = (TextView) toastView.findViewById(R.id.hint_text_tv);
//      text.setText(resID);

      mToast.setText(msg);
//      mToast.getView().setBackgroundColor(mContext.getResources().getColor(R.color.validationerror));
      mToast.setDuration(Toast.LENGTH_SHORT);
      mToast.show();
   }

   public void hide()
   {
      mToast.cancel();
   }
}
