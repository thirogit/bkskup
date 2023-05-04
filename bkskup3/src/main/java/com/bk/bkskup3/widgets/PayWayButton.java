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
import com.bk.bkskup3.model.PayWay;

public class PayWayButton extends LinearLayout {

    public interface OnPayWayListener {
        void onCash();

        void onTransfer();
    }

    ToggleButton mCashBtn;
    ToggleButton mTransferBtn;
    OnPayWayListener mListener;

    public PayWayButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.payway_btn, this, true);

        mCashBtn = (ToggleButton) findViewById(R.id.cash_btn);
        mTransferBtn = (ToggleButton) findViewById(R.id.transfer_btn);


        mCashBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mCashBtn.isToggled()) {
                    mCashBtn.toggle();
                    mTransferBtn.setToggled(!mCashBtn.isToggled());
                    if (mListener != null) mListener.onCash();
                }
            }
        });


        mTransferBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mTransferBtn.isToggled()) {
                    mTransferBtn.toggle();
                    mCashBtn.setToggled(!mTransferBtn.isToggled());
                    if (mListener != null) mListener.onTransfer();
                }
            }
        });

    }

    public PayWayButton(Context context) {
        this(context, null);
    }


    public PayWay getPayWay() {
        if (mTransferBtn.isToggled())
            return PayWay.TRANSFER;
        if (mCashBtn.isToggled())
            return PayWay.CASH;

        return null;
    }

    public void setPayWay(PayWay payway) {
        if (payway == null) {
            mTransferBtn.setToggled(false);
            mCashBtn.setToggled(false);
        } else if (payway == PayWay.CASH) {
            mTransferBtn.setToggled(false);
            mCashBtn.setToggled(true);
        } else if (payway == PayWay.TRANSFER) {
            mTransferBtn.setToggled(true);
            mCashBtn.setToggled(false);
        }
    }

    public void setOnPayWayListener(OnPayWayListener listener) {
        this.mListener = listener;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);
        ss.payway = getPayWay();

        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {

        SavedState ss = (SavedState)state;
        super.onRestoreInstanceState(ss.getSuperState());

        setPayWay(ss.payway);

    }


    public static class SavedState extends BaseSavedState {
        PayWay payway;

        SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);

            int paywayOrdinal = -1;
            if (payway != null) {
                paywayOrdinal = payway.ordinal();
            }

            out.writeInt(paywayOrdinal);
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
            int paywayOrdinal = in.readInt();
            if (paywayOrdinal >= 0) {
                payway = PayWay.values()[paywayOrdinal];
            }
        }
    }

}