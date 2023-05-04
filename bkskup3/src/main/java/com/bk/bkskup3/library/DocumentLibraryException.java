package com.bk.bkskup3.library;

/**
 * Created by SG0891787 on 2/15/2017.
 */

public class DocumentLibraryException extends Exception {
    public DocumentLibraryException(String detailMessage) {
        super(detailMessage);
    }

    public DocumentLibraryException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public DocumentLibraryException(Throwable throwable) {
        super(throwable);
    }
}
