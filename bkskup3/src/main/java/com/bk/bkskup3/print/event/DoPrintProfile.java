package com.bk.bkskup3.print.event;

import com.bk.bkskup3.library.DocumentProfile;
import com.bk.bkskup3.work.fragment.event.FragmentEvent;

/**
 * Created by SG0891787 on 10/3/2017.
 */

public class DoPrintProfile extends FragmentEvent {

    DocumentProfile profile;
    boolean longClick;

    public DoPrintProfile(DocumentProfile profile) {
        this.profile = profile;
    }

    public DocumentProfile getProfile() {
        return profile;
    }

    public boolean isLongClick() {
        return longClick;
    }

    public void setLongClick(boolean longClick) {
        this.longClick = longClick;
    }
}
