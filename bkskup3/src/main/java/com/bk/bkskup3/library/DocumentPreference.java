package com.bk.bkskup3.library;

public class DocumentPreference {
    private String documentCode;
    private boolean visible;

    public DocumentPreference(String documentCode) {
        this.documentCode = documentCode;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getDocumentCode() {
        return documentCode;
    }
}
