package com.bk.bkskup3.widgets;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import com.bk.bkskup3.R;
import com.bk.bkskup3.model.HentType;

public class HentTypeButton extends LinearLayout {

    ToggleButton mIndividualBtn;
    ToggleButton mCompanyBtn;

    public HentTypeButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.henttype_btn, this, true);

        mIndividualBtn = (ToggleButton) findViewById(R.id.individual_btn);
        mCompanyBtn = (ToggleButton) findViewById(R.id.company_btn);


        mIndividualBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mIndividualBtn.isToggled()) {
                    mIndividualBtn.toggle();
                    mCompanyBtn.setToggled(!mIndividualBtn.isToggled());
                }
            }
        });


        mCompanyBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mCompanyBtn.isToggled()) {
                    mCompanyBtn.toggle();
                    mIndividualBtn.setToggled(!mCompanyBtn.isToggled());
                }
            }
        });

    }

    public HentTypeButton(Context context) {
        this(context, null);
    }


    public HentType getHentType()
    {
        if(mIndividualBtn.isToggled())
            return HentType.INDIVIDUAL;
        if(mCompanyBtn.isToggled())
            return HentType.COMPANY;

        return null;
    }

    public void setHentType(HentType hentType)
    {
        if(hentType == null)
        {
            mIndividualBtn.setToggled(false);
            mCompanyBtn.setToggled(false);
        } else if(hentType == HentType.COMPANY) {
            mIndividualBtn.setToggled(false);
            mCompanyBtn.setToggled(true);
        } else if(hentType ==HentType.INDIVIDUAL)
        {
            mIndividualBtn.setToggled(true);
            mCompanyBtn.setToggled(false);
        }
    }
    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);
        ss.hentType = getHentType();

        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {

        SavedState ss = (SavedState)state;
        super.onRestoreInstanceState(ss.getSuperState());

        setHentType(ss.hentType);

    }


    public static class SavedState extends BaseSavedState {
        HentType hentType;

        SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);

            int henttypeOrdinal = -1;
            if (hentType != null) {
                henttypeOrdinal = hentType.ordinal();
            }

            out.writeInt(henttypeOrdinal);
        }

        @SuppressWarnings("hiding")
        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

        private SavedState(Parcel in) {
            super(in);
            int henttypeOrdinal = in.readInt();
            if (henttypeOrdinal >= 0) {
                hentType = HentType.values()[henttypeOrdinal];
            }
        }
    }
} 