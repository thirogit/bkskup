package com.bk.bkskup3.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import com.bk.bkskup3.R;
import com.bk.bkskup3.model.CowSex;

public class CowSexButton extends LinearLayout {

    ToggleButton mXXBtn;
    ToggleButton mNoneBtn;
    ToggleButton mXYBtn;

    public CowSexButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.cowsex_btn, this, true);

        mXXBtn = (ToggleButton) findViewById(R.id.sex_xx_btn);
        mNoneBtn = (ToggleButton) findViewById(R.id.sex_none_btn);
        mXYBtn = (ToggleButton) findViewById(R.id.sex_xy_btn);


        mXXBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mXXBtn.isToggled()) {
                    mXXBtn.setToggled(true);

                    mNoneBtn.setToggled(false);
                    mXYBtn.setToggled(false);

                }
            }
        });

        mNoneBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mNoneBtn.isToggled()) {
                    mNoneBtn.setToggled(true);

                    mXXBtn.setToggled(false);
                    mXYBtn.setToggled(false);

                }
            }
        });

        mXYBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mXYBtn.isToggled()) {
                    mXYBtn.setToggled(true);

                    mXXBtn.setToggled(false);
                    mNoneBtn.setToggled(false);

                }
            }
        });


    }

    public CowSexButton(Context context) {
        this(context, null);
    }


    public CowSex getCowSex() {
        if (mXXBtn.isToggled())
            return CowSex.XX;
        if (mXYBtn.isToggled())
            return CowSex.XY;
        if(mNoneBtn.isToggled())
            return CowSex.NONE;

        return null;
    }

    public void setCowSex(CowSex cowsex) {

        mXXBtn.setToggled(false);
        mXYBtn.setToggled(false);
        mNoneBtn.setToggled(false);

        if (cowsex != null) {
            switch (cowsex)
            {
                case NONE:
                    mNoneBtn.setToggled(true);
                    break;
                case XX:
                    mXXBtn.setToggled(true);
                    break;
                case XY:
                    mXYBtn.setToggled(true);
                    break;
            }
        }
    }

} 