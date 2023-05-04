package com.bk.bkskup3.work.fragment.event;

import com.bk.bkskup3.library.DocumentProfile;

import java.util.Collection;

/**
 * Created by SG0891787 on 9/6/2017.
 */

public class ProfilesLoadedEvent extends FragmentEvent {
    private Collection<DocumentProfile> profiles;

    public ProfilesLoadedEvent(Collection<DocumentProfile> profiles) {
        this.profiles = profiles;
    }

    public Collection<DocumentProfile> getProfiles() {
        return profiles;
    }
}
