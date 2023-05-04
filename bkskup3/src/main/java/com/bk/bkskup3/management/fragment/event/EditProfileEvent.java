package com.bk.bkskup3.management.fragment.event;

import com.bk.bkskup3.library.DocumentProfile;
import com.bk.bkskup3.work.fragment.event.FragmentEvent;

/**
 * Created by SG0891787 on 9/11/2017.
 */

public class EditProfileEvent extends FragmentEvent {

    DocumentProfile mProfile;

    public EditProfileEvent(DocumentProfile profile) {
        mProfile = profile;
    }

    public DocumentProfile getProfile() {
        return mProfile;
    }
}
