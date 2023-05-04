package com.bk.bkskup3.work.fragment;

import android.app.Fragment;
import android.util.Log;

import com.bk.bkskup3.model.HentObj;
import com.bk.bkskup3.settings.InputDefaultsSettings;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/22/2014
 * Time: 12:12 PM
 */
public abstract class HentFragment extends Fragment {

    private static final String TAG = HentFragment.class.getName();
    protected HentObj mInput;
    protected InputDefaultsSettings mInputDefaults;

    public void setHent(HentObj hent) {
        mInput = hent;
    }

    public void setInputDefaults(InputDefaultsSettings inputDefaults) {
        this.mInputDefaults = inputDefaults;
    }

    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "on destroy view "+ this.getTag());
        save();
    }


    public abstract void save();
}
