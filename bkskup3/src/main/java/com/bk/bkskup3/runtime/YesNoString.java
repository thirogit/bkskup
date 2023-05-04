package com.bk.bkskup3.runtime;

import android.content.res.Resources;
import com.bk.bkskup3.R;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/14/2014
 * Time: 10:43 PM
 */
public class YesNoString {
    private Resources mResources;

    public YesNoString(Resources resources) {
        this.mResources = resources;
    }

    public String toString(boolean b)
    {
        if(b)
            return mResources.getString(R.string.yesAllCaps);
        else
            return mResources.getString(R.string.noAllCaps);
    }
}
