package com.bk.bkskup3.work.fragment.event;

import com.bk.bkskup3.library.DocumentProfile;

/**
 * Created by SG0891787 on 9/5/2017.
 */

public class NewProfileEvent extends FragmentEvent {

    private DocumentProfile profile;

    public NewProfileEvent(DocumentProfile profile) {
        this.profile = profile;
    }

    public DocumentProfile getProfile() {
        return profile;
    }
}
