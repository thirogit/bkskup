package com.bk.bkskup3.widgets;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class CalculatorEditText extends EditText {


    public CalculatorEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
//        setCustomSelectionActionModeCallback(new NoTextSelectionMode());
        setInputType(InputType.TYPE_NULL );
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//       if (event.getActionMasked() == MotionEvent.ACTION_UP) {
//            // Hack to prevent keyboard and insertion handle from showing.
//           cancelLongPress();
//        }
//        return super.onTouchEvent(event);
//    }


    class NoTextSelectionMode implements ActionMode.Callback {
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Prevents the selection action mode on double tap.
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {}

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }
    }
}
