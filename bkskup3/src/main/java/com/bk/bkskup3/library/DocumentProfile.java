package com.bk.bkskup3.library;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

public class DocumentProfile implements Serializable {

    private int profileId;
    private String profileName;
    private String documentCode;
    private HashMap<String,DocumentOption> mOptions = new HashMap<String, DocumentOption>();

    public DocumentProfile(int profileId) {
        this.profileId = profileId;
    }

    public int getProfileId() {
        return profileId;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public void setDocumentCode(String documentCode) {
        this.documentCode = documentCode;
    }

    public String getDocumentCode() {
        return documentCode;
    }

    public void addOption(String code,String value)
    {
        mOptions.put(code,new DocumentOption(code,value));
    }

    public DocumentOption getOption(String name)
    {
        return mOptions.get(name);
    }

    public String getOptionValue(String name)
    {
        DocumentOption option = mOptions.get(name);
        if(option != null)
        {
            return option.getOptionValue();
        }
        return null;
    }

    public Collection<DocumentOption> getOptions()
    {
        return mOptions.values();
    }

    public void addAllOptions(Collection<DocumentOption> options)
    {
        for (DocumentOption option : options)
        {
            mOptions.put(option.getOptionName(),option);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DocumentProfile profile = (DocumentProfile) o;

        return profileId == profile.profileId;

    }

    @Override
    public int hashCode() {
        return profileId;
    }
}
