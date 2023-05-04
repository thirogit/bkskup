package com.bk.bkskup3.management.fragment.event;

import com.bk.bkskup3.work.fragment.event.FragmentEvent;

/**
 * Created by SG0891787 on 9/14/2017.
 */

public class DeleteProfileEvent extends FragmentEvent {

    private int mProfileId;

    public DeleteProfileEvent(int profileId) {
        this.mProfileId = profileId;
    }

    public int getProfileId() {
        return mProfileId;
    }
}
