package com.bk.bands.xml;

/**
 * Created by SG0891787 on 2/15/2017.
 */

public class UnmarshallException extends Exception {
    public UnmarshallException(String detailMessage) {
        super(detailMessage);
    }

    public UnmarshallException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public UnmarshallException(Throwable throwable) {
        super(throwable);
    }
}
