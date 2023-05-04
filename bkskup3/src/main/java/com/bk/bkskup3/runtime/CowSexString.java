package com.bk.bkskup3.runtime;

import android.content.res.Resources;
import com.bk.bkskup3.R;
import com.bk.bkskup3.model.CowSex;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/29/2014
 * Time: 9:14 PM
 */
public class CowSexString {

    private Resources mResources;

    public CowSexString(Resources resources) {
        this.mResources = resources;
    }

    public String toString(CowSex sex) {
        int cowSexResId = R.string.cowSexNONE;

        if (sex == CowSex.XX)
            cowSexResId = R.string.cowSexXX;
        else if (sex == CowSex.XY)
            cowSexResId = R.string.cowSexXY;

        return mResources.getString(cowSexResId);
    }


}
