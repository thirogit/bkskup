package com.bk.widgets.warningedittext;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.EditText;
import com.bk.widgets.actionbar.R;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 1/8/2015
 * Time: 9:43 PM
 */
public class WarningEditText extends EditText {
    public WarningEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void showWarning(boolean show)
    {
        if(show) {
            Drawable warnIcon = getResources().getDrawable(R.drawable.warning);
            warnIcon.setBounds(0, 0, warnIcon.getIntrinsicWidth(), warnIcon.getIntrinsicHeight());
            setCompoundDrawables(null, null, warnIcon, null);
        }
        else
        {
            setCompoundDrawables(null, null, null, null);
        }
    }
}
