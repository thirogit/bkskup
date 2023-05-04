package com.bk.bkskup3.work.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Parcelable;
//import android.support.v13.app.FragmentCompat;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
* Created with IntelliJ IDEA.
* User: SG0891787
* Date: 6/12/2015
* Time: 10:15 PM
*/
public class ViewPagerAdapter extends PagerAdapter {

    private List<FragmentTab> mTabs = new ArrayList<FragmentTab>(5);
    private final FragmentManager mFragmentManager;
    private FragmentTransaction mCurTransaction = null;
    private Fragment mCurrentPrimaryItem = null;


    public ViewPagerAdapter(FragmentManager fm) {
        mFragmentManager = fm;
    }

    public void addTab(String title, String tag, Fragment fragment) {
        mTabs.add(new FragmentTab(title, tag, fragment));
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    public Fragment getItem(int position) {
        return mTabs.get(position).getFragment();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabs.get(position).getTabTitle();
    }

    @Override
    public void startUpdate(ViewGroup container) {
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }

        // Do we already have this fragment?
        String tag = getFragmentTag(position);
        Fragment fragment = mFragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            mCurTransaction.attach(fragment);
        } else {
            fragment = getItem(position);
            mCurTransaction.add(container.getId(), fragment, tag);
        }
        if (fragment != mCurrentPrimaryItem) {
            fragment.setMenuVisibility(false);
            fragment.setUserVisibleHint(false);
//            FragmentCompat.setMenuVisibility(fragment, false);
//            FragmentCompat.setUserVisibleHint(fragment, false);
        }

        return fragment;
    }

    private String getFragmentTag(int position) {
        return mTabs.get(position).getTag();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }
        mCurTransaction.detach((Fragment) object);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment) object;
        if (fragment != mCurrentPrimaryItem) {
            if (mCurrentPrimaryItem != null) {
                mCurrentPrimaryItem.setMenuVisibility(false);
                mCurrentPrimaryItem.setUserVisibleHint(false);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);
            }
            mCurrentPrimaryItem = fragment;
        }
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        if (mCurTransaction != null) {
            mCurTransaction.commitAllowingStateLoss();
            mCurTransaction = null;
            mFragmentManager.executePendingTransactions();
        }

        container.post(new Runnable() {
            @Override
            public void run() {
                mFragmentManager.invalidateOptionsMenu();
            }
        });
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((Fragment) object).getView() == view;
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }
}
