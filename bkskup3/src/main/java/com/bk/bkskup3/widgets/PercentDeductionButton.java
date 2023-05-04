package com.bk.bkskup3.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.bk.bkskup3.R;

public class PercentDeductionButton extends LinearLayout {



    static class Btn {
        public Btn(int btnId, int percent) {
            this.btnId = btnId;
            this.percent = percent;
        }

        public int btnId;
        public int percent;
    }


    static final Btn buttons[] = {
            new Btn(R.id.deduction_3_btn, 3),
            new Btn(R.id.deduction_4_btn, 4),
            new Btn(R.id.deduction_5_btn, 5),
            new Btn(R.id.deduction_6_btn, 6),
            new Btn(R.id.deduction_7_btn, 7),
            new Btn(R.id.deduction_8_btn, 8),
            new Btn(R.id.deduction_9_btn, 9),
            new Btn(R.id.deduction_10_btn, 10)
    };

    private PercentToggleListener mListener;

    public PercentDeductionButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.deduction_btn_container, this, true);


        for (Btn button : buttons) {
            ToggleButton btn = (ToggleButton) findViewById(button.btnId);
            btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    PercentDeductionButton.this.onClick((ToggleButton) v);
                }
            });
        }

    }

    private void onClick(ToggleButton toggleButton) {
        if (!toggleButton.isToggled()) {
            toggleButton.setToggled(true);
            for (Btn buttonInfo : buttons) {

                if (toggleButton.getId() != buttonInfo.btnId) {
                    ToggleButton otherBtn = (ToggleButton) findViewById(buttonInfo.btnId);
                    otherBtn.setToggled(false);
                }
            }

        }
        else
        {
            toggleButton.setToggled(false);
        }
        notifyListener();
    }

    public PercentDeductionButton(Context context) {
        this(context, null);
    }


    public int getDeductionPercent() {
        for (Btn buttonInfo : buttons) {
            ToggleButton btn = (ToggleButton) findViewById(buttonInfo.btnId);
            if (btn.isToggled()) {
                return buttonInfo.percent;
            }
        }

        return 0;

    }

    private void notifyListener()
    {
        if(mListener != null)
        {
            mListener.toggleChanged();
        }
    }

    public void setOnToggleListener(PercentToggleListener listener)
    {
        mListener = listener;
    }

    public interface PercentToggleListener {
        void toggleChanged();
    }
}