package com.bk.bkskup3.db;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 6/4/2015
 * Time: 10:28 PM
 */
public interface  QueryFuture <T> {

    void abortAndForget();
    T get();
    boolean isDone();
}

