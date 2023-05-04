package com.bk.widgets.actionbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/1/13
 * Time: 10:24 PM
 */
public class ToogleAction extends ActionBar.AbstractAction {
    public interface OnToogleListener {
        void onToogleOn();

        void onToogleOff();
    }

    String mTextOn;
    String mTextOff;
    OnToogleListener mToogleListener;
    boolean bToogle = false;


    public ToogleAction(boolean bToogle) {
        this.bToogle = bToogle;
    }

    public ToogleAction(OnToogleListener mToogleListener, boolean bToogle) {
        this.mToogleListener = mToogleListener;
        this.bToogle = bToogle;
    }


    public ToogleAction(String tag, boolean bToogle) {
        super(tag);
        this.bToogle = bToogle;
    }

    public ToogleAction(String tag, OnToogleListener mToogleListener, boolean bToogle) {
        super(tag);
        this.mToogleListener = mToogleListener;
        this.bToogle = bToogle;
    }

    public void setToogleListener(OnToogleListener toogleListener) {
        this.mToogleListener = toogleListener;
    }

    public void setTextOn(String textOn) {
        mTextOn = textOn;
    }

    public void setTextOff(String textOff) {
        mTextOff = textOff;
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup root) {
        Switch toogleSwitch = (Switch) inflater.inflate(R.layout.actionbar_toogleitem, root, false);
        if (mTextOn != null && mTextOn.length() > 0) {
            toogleSwitch.setTextOn(mTextOn);
        }
        if (mTextOff != null && mTextOff.length() > 0) {
            toogleSwitch.setTextOff(mTextOff);
        }
        toogleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mToogleListener != null) {
                    if (isChecked) {
                        mToogleListener.onToogleOn();
                    } else {
                        mToogleListener.onToogleOff();
                    }
                }
            }
        });

        toogleSwitch.setChecked(bToogle);
        return toogleSwitch;
    }
}
