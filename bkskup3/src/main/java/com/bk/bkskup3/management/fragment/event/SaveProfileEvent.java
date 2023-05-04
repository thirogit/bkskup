package com.bk.bkskup3.management.fragment.event;

import com.bk.bkskup3.work.fragment.event.FragmentEvent;

/**
 * Created by SG0891787 on 9/14/2017.
 */

public class SaveProfileEvent extends FragmentEvent {
    private int mProfieId;

    public SaveProfileEvent(int profieId) {
        this.mProfieId = profieId;
    }

    public int getProfieId() {
        return mProfieId;
    }
}
