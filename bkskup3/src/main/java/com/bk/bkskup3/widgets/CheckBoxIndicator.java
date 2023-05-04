package com.bk.bkskup3.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CompoundButton;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 6/15/2015
 * Time: 9:43 PM
 */
public class CheckBoxIndicator extends CompoundButton {

    public CheckBoxIndicator(Context context) {
        this(context, null);
    }

    public CheckBoxIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.checkboxStyle);
    }

    public CheckBoxIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean performClick() {
        return true;
    }

}
