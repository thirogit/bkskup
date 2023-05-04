package com.bk.bkskup3.management.widget;

import android.content.Context;
import android.media.Image;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bk.bkskup3.R;
import com.bk.bkskup3.library.DocumentOptionType;

/**
 * Created by SG0891787 on 9/13/2017.
 */

public class DocumentDayDateOptionView extends DocumentOptionView {


    private OnClickListener mListener;

    public DocumentDayDateOptionView(Context context) {
        super(context);
    }

    public DocumentDayDateOptionView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DocumentDayDateOptionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    public DocumentDayDateOptionView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);


    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getButtonBox().setOnClickListener(v -> {
            if(mListener != null)
            {
                mListener.onClick(DocumentDayDateOptionView.this);
            }
        });
    }

    private ImageButton getButtonBox() {
        return (ImageButton) findViewById(R.id.changeDateBtn);
    }

    public void setChangeDateClickListener(@Nullable OnClickListener l) {
        mListener = l;
    }


}
