package com.bk.bkskup3.work.fragment.event;

import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/3/2014
 * Time: 4:03 PM
 */
public class CowDeletedEvent extends FragmentEvent{
    private UUID mCowId;

    public CowDeletedEvent(UUID cowId) {
        this.mCowId = cowId;
    }

    public UUID getDeletedCowId() {
        return mCowId;
    }
}
