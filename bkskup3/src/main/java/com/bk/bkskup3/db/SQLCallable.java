package com.bk.bkskup3.db;

public interface SQLCallable<T> {

    T call(SQLDatabase db) throws Exception;

}
