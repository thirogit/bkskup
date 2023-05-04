package com.bk.bkskup3.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import com.bk.bkskup3.R;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/24/2014
 * Time: 10:59 PM
 */
public class ToggleButton extends Button {

    private boolean mToggled = false;

    private static final int[] STATE_TOGGLED = {R.attr.state_toggled};

    public ToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean isToggled() {
        return mToggled;
    }

    public void setToggled(boolean toggled) {
        this.mToggled = toggled;
        refreshDrawableState();
    }

    public void toggle()
    {
        mToggled = !mToggled;
        refreshDrawableState();
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (mToggled) {
            mergeDrawableStates(drawableState, STATE_TOGGLED);
        }
        return drawableState;
    }
}
