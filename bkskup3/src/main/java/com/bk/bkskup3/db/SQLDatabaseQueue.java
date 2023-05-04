package com.bk.bkskup3.db;

import android.database.SQLException;

import com.google.common.base.Preconditions;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SQLDatabaseQuue provides the ability to ensure that the
 * only a single thread accesses the SQLDatabase. Tasks submitted to this
 * queue are guaranteed to be executed in the order they are received
 */
public class SQLDatabaseQueue {

    private final SQLDatabase db;
    private final ExecutorService queue;
    private final Logger logger = Logger.getLogger(SQLDatabase.class.getCanonicalName());
    private AtomicBoolean acceptTasks = new AtomicBoolean(true);
    private String sqliteVersion = null;

    public SQLDatabaseQueue(final File file) {
        queue = Executors.newSingleThreadExecutor(new ThreadFactory(file));
        this.db = new SQLDatabase();
        queue.execute(new Runnable() {
            @Override
            public void run() {
                db.open(file);
            }
        });
    }

    /**
     * Updates the schema of the database.
     * @param migration Object which performs migration; should not check or set version
     * @param version The version of the schema
     */
    public void updateSchema(final Migration migration, final int version){
        queue.execute(new UpdateSchemaCallable(migration, version)); // Fire and forget
    }

    /**
     * Returns the current version of the database.
     * @return The current version of the database.
     * @throws SQLException Throws if there was an error getting the current database version
     */
    public int getVersion() throws SQLException {
        try {
            return this.submit(new VersionCallable()).get();
        } catch (InterruptedException | ExecutionException e) {
            logger.log(Level.SEVERE, "Failed to get database version", e);
            throw new SQLException(e.getMessage(),e);
        }
    }

    /**
     * Submits a database task for execution
     * @param callable The task to be performed
     * @param <T> The type of object that is returned from the task
     * @throws RejectedExecutionException Thrown when the queue has been shutdown
     * @return Future representing the task to be executed.
     */
    public <T> Future<T> submit(SQLCallable<T> callable){
        return this.submitTaskToQueue(new SQLQueueCallable<T>(db, callable));
    }

    /**
     * Submits a database task for execution in a transaction
     * @param callable The task to be performed
     * @param <T> The type of object that is returned from the task
     * @throws RejectedExecutionException thrown when the queue has been shutdown
     * @return Future representing the task to be executed.
     */
    public <T> Future<T> submitTransaction(SQLCallable<T> callable){
        return this.submitTaskToQueue(new SQLQueueCallable<T>(db, callable, true));
    }

    /**
     * Shuts down this database queue and closes
     * the underlying database connection. Any tasks
     * previously submitted for execution will still be
     * executed, however the queue will not accept additional
     * tasks
     */
    public void shutdown() {
        // If shutdown has already been called then we don't need to shutdown again
        if (acceptTasks.getAndSet(false)) {
            //pass straight to queue, tasks passed via submitTaskToQueue will now be blocked.
            Future<?> close = queue.submit(new Runnable() {
                @Override
                public void run() {
                    db.close();
                }
            });
            queue.shutdown();
            try {
                close.get();
                queue.awaitTermination(5, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, "Interrupted while waiting for queue to terminate", e);
            } catch (ExecutionException e) {
                logger.log(Level.SEVERE, "Failed to close database", e);
            }
        } else {
            logger.log(Level.WARNING, "SQLDatabase is already closed.");
        }


    }

    /**
     * Checks if {@link SQLDatabaseQueue#shutdown()} has been called
     * @return true if {@link SQLDatabaseQueue#shutdown()} has been called.
     */
    public boolean isShutdown() {
        return queue.isShutdown();
    }

    /**
     * Adds a task to the queue, checking if the queue is still open
     * to accepting tasks
     * @param callable The task to submit to the queue
     * @param <T> The type of object that the callable returns
     * @return Future representing the task to be executed.
     * @throws RejectedExecutionException If the queue has been shutdown.
     */
    private <T> Future<T> submitTaskToQueue(SQLQueueCallable<T> callable){
        if(acceptTasks.get()){
            return queue.submit(callable);
        } else {
            throw new RejectedExecutionException("SQLDatabase is closed");
        }
    }

    /**
     * Returns the SQLite Version.
     * @return The SQLite version or "Unknown" if the version could not be determined.
     */
    public synchronized String getSQLiteVersion() {

        if (this.sqliteVersion == null) {
            try {
                this.sqliteVersion = this.submit(new SQLiteVersionCallable()).get();
                return sqliteVersion;
            } catch (InterruptedException e) {
                logger.log(Level.WARNING, "Could not determine SQLite version", e);
            } catch (ExecutionException e) {
                logger.log(Level.WARNING, "Could not determine SQLite version", e);
            }
            this.sqliteVersion = "unknown";
        }

        return this.sqliteVersion;


    }

    private static class SQLiteVersionCallable implements SQLCallable<String> {
        @Override
        public String call(SQLDatabase db) throws Exception {
            BKCursor cursor = db.rawQuery("SELECT sqlite_version()", null);

            StringBuilder stringBuilder = new StringBuilder();
            while (cursor.moveToNext()) {
                stringBuilder.append(cursor.getString(0));
            }
            return stringBuilder.toString();
        }
    }

    private static class VersionCallable implements SQLCallable<Integer> {
        @Override
        public Integer call(SQLDatabase db) throws Exception {
            return db.getVersion();
        }
    }

    private static class ThreadFactory implements java.util.concurrent.ThreadFactory {
        private final File file;

        public ThreadFactory(File file) {
            this.file = file;
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "SQLDatabaseQueue - "+ file);
        }
    }

    private class UpdateSchemaCallable implements Runnable {
        private final Migration migration;
        private final int version;

        public UpdateSchemaCallable(Migration migration, int version) {
            this.migration = migration;
            this.version = version;
        }

        @Override
        public void run() {
            try {
                updateSchema(db, migration, version);
            } catch (SQLException e){
                logger.log(Level.SEVERE, "Failed to update database schema",e);
            }
        }

        private void updateSchema(SQLDatabase database, Migration migration, int version)
                throws SQLException {
            Preconditions.checkArgument(version > 0, "Schema version number must be positive");
            // ensure foreign keys are enforced in the case that we are up to date and no migration happen
            database.execSQL("PRAGMA foreign_keys = ON;");
            int dbVersion = database.getVersion();
            if(dbVersion < version) {
                // switch off foreign keys during the migration - so that we don't get caught out by
                // "ON DELETE CASCADE" constraints etc
                database.execSQL("PRAGMA foreign_keys = OFF;");
                database.beginTransaction();
                try {
                    try {
                        migration.runMigration(database);
                        database.execSQL("PRAGMA user_version = " + version + ";");

                        database.setTransactionSuccessful();
                    } catch (Exception ex) {
                        // don't set the transaction successful, so it'll rollback
                        throw new SQLException(
                                String.format("Migration from %1$d to %2$d failed.", dbVersion, version),
                                ex);
                    }
                } finally {
                    database.endTransaction();
                    // re-enable foreign keys
                    database.execSQL("PRAGMA foreign_keys = ON;");
                }

            }
        }
    }
}
