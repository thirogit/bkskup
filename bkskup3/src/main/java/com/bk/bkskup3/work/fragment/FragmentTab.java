package com.bk.bkskup3.work.fragment;

import android.app.Fragment;

/**
* Created with IntelliJ IDEA.
* User: SG0891787
* Date: 6/12/2015
* Time: 10:15 PM
*/
public class FragmentTab {
    private String mTabTitle;
    private Fragment mFragment;
    private String mTag;

    public FragmentTab(String tabTitle, String tag, Fragment fragment) {
        this.mTabTitle = tabTitle;
        this.mFragment = fragment;
        this.mTag = tag;
    }

    public String getTabTitle() {
        return mTabTitle;
    }

    public Fragment getFragment() {
        return mFragment;
    }

    public String getTag() {
        return mTag;
    }
}
