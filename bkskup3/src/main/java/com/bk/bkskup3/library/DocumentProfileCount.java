package com.bk.bkskup3.library;

public class DocumentProfileCount {
    private String documentCode;
    private int profileCount;

    public DocumentProfileCount(String documentCode) {
        this.documentCode = documentCode;
    }

    public int getProfileCount() {
        return profileCount;
    }

    public void setProfileCount(int profileCount) {
        this.profileCount = profileCount;
    }

    public String getDocumentCode() {
        return documentCode;
    }
}
