package com.bk.bkskup3.print.widgets;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.bk.bkskup3.R;
import com.bk.print.service.PrintServiceState;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 8/15/12
 * Time: 6:32 PM
 */
public class PrintServiceIndicator extends ImageView {

    private PrintServiceState mState = PrintServiceState.NoService;

    public PrintServiceIndicator(Context context) {
        this(context, null);
    }

    public PrintServiceIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PrintServiceState getState() {
        return mState;
    }

    public void setStatus(PrintServiceState state) {
        if (mState == PrintServiceState.PrinterConnecting) {
            ((AnimationDrawable) getBackground()).stop();
        }

        this.mState = state;

        switch (mState) {
            case PrinterNotConnected:
                setBackgroundResource(R.drawable.printer_bad);
                invalidate();
                break;
            case PrinterConnected:
                setBackgroundResource(R.drawable.printer_good);
                invalidate();
                break;
            case PrinterConnecting:
                setBackgroundResource(R.drawable.printer_connecting);
                ((AnimationDrawable) getBackground()).start();
                invalidate();
                break;
            case NoService:
                setBackgroundResource(R.drawable.printer_disabled);
                invalidate();
                break;

        }
    }

}
