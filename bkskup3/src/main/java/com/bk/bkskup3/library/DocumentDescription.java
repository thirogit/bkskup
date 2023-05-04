package com.bk.bkskup3.library;

import java.io.Serializable;

/**
 * Created by SG0891787 on 10/4/2017.
 */

public class DocumentDescription implements Serializable {

    private String docName;
    private String docCode;
    private int profileCount;

    public DocumentDescription(String docCode) {
        this.docCode = docCode;
    }

    public String getDocCode() {
        return docCode;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public int getProfileCount() {
        return profileCount;
    }

    public void setProfileCount(int profileCount) {
        this.profileCount = profileCount;
    }


}
