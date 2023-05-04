package com.bk.bkskup3.dao;

import android.database.SQLException;

import com.bk.bkskup3.db.SQLDatabaseQueue;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public abstract class AbstractSQLStore {

    protected SQLDatabaseQueue mDb;

    public AbstractSQLStore(SQLDatabaseQueue mDb) {
        this.mDb = mDb;
    }

    protected  <T> T get(Future<T> future) {
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new SQLException(e.getMessage(), e);
        }
    }
}
