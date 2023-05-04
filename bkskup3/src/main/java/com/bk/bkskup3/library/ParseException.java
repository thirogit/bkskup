package com.bk.bkskup3.library;

/**
 * Created by SG0891787 on 2/15/2017.
 */

public class ParseException extends Exception {
    public ParseException(String detailMessage) {
        super(detailMessage);
    }

    public ParseException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ParseException(Throwable throwable) {
        super(throwable);
    }
}
