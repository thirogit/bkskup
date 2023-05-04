package com.bk.bkskup3.work.fragment.event;

import com.bk.bkskup3.work.input.CowInput;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/3/2014
 * Time: 4:03 PM
 */
public class CowAddedEvent extends FragmentEvent{
    private CowInput mCow;

    public CowAddedEvent(CowInput cow) {
        this.mCow = cow;
    }

    public CowInput getCow() {
        return mCow;
    }
}
