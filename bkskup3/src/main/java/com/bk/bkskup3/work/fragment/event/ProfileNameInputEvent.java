package com.bk.bkskup3.work.fragment.event;

/**
 * Created by SG0891787 on 9/5/2017.
 */

public class ProfileNameInputEvent extends FragmentEvent {

    private String profileName;

    public ProfileNameInputEvent(String profileName) {
        this.profileName = profileName;
    }

    public String getProfileName() {
        return profileName;
    }
}
