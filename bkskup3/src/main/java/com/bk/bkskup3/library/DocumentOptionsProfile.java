package com.bk.bkskup3.library;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DocumentOptionsProfile {
    int profileId;
    String profileName;

    Map<String,DocumentOption> options = new HashMap<String, DocumentOption>();

    public DocumentOptionsProfile(int profileId) {
        this.profileId = profileId;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public void addOption(String code,String value)
    {
       options.put(code,new DocumentOption(code,value));
    }

    public Collection<DocumentOption> getOptions()
    {
        return options.values();
    }
}
