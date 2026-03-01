package com.bk.bkskup3.db;

public interface SQLCallable<T> {

    T call(SQLDatabaseWrapper db) throws Exception;

}
