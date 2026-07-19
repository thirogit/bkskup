package com.bk.bkskup3.dao;

import com.bk.bkskup3.db.SQLDatabaseQueue;

import java.time.LocalDateTime;

public class PurchaseUploadLogStore extends AbstractSQLStore {
    public PurchaseUploadLogStore(SQLDatabaseQueue mDb) {
        super(mDb);
    }


    public UploadLogEntry fetchUploadLogEntry(int purchaseId) {
        return null;
    }

    public void upsertUploadLogEntry(UploadLogEntry entry) {

    }

    public enum UploadStatus {
        Failed,
        Done,
        Retrying
    }

    public static class UploadLogEntry {

        private int purchaseId;
        private UploadStatus status;

        private String uploadError;

        private LocalDateTime whenCreated;

        private LocalDateTime whenLastUploadAttempt;

    }
}
