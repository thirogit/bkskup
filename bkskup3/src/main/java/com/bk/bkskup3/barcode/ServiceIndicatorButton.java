package com.bk.bkskup3.barcode;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Button;
import com.bk.bkskup3.R;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/17/13
 * Time: 4:57 PM
 */
public class ServiceIndicatorButton extends Button {
    private BarcodeServiceState mState;

    public ServiceIndicatorButton(Context context) {
        super(context);
        setState(BarcodeServiceState.NoService);
    }

    public ServiceIndicatorButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setState(BarcodeServiceState.NoService);
    }

    private void setIndicatorImage(int drawableResId) {
        Drawable indicatorImg = getResources().getDrawable(drawableResId);
        int imgWidth = indicatorImg.getIntrinsicWidth();
        int btnWidth = getWidth();


//        int padLeft = getCompoundPaddingLeft();
//        int padRight = getCompoundPaddingRight();
        int left = 0;
        int right = imgWidth;

//        if(btnWidth > imgWidth) {
//            left = - 8;
//            right = left + imgWidth;
//        }

        indicatorImg.setBounds(left,0,right,indicatorImg.getIntrinsicHeight());
        setCompoundDrawables(indicatorImg, null, null, null);
    }

    public BarcodeServiceState getState() {
        return mState;
    }

    public void setState(BarcodeServiceState newState) {
        switch (newState) {
            case ScannerConnecting:
                setIndicatorImage(R.drawable.barcode_bt);
                break;
            default:
            case NoService:
                setIndicatorImage(R.drawable.barcode_red);
                break;
            case ScannerConnected:
                setIndicatorImage(R.drawable.barcode_yes);
                break;
            case ScannerNotConnected:
                setIndicatorImage(R.drawable.barcode_no);
                break;
        }
    }

}
