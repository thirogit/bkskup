package com.bk.bkskup3.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.Nullable;

import com.bk.bkskup3.db.SQLCallable;
import com.bk.bkskup3.db.SQLDatabase;
import com.bk.bkskup3.db.SQLDatabaseQueue;
import com.bk.bkskup3.invoice.InvoiceNoState;
import com.bk.bkskup3.invoice.InvoiceNoTransaction;

import java.util.Collections;
import java.util.concurrent.Future;

import static com.bk.bkskup3.dao.WhereUtils.eq;

public class InvoiceNoTransactionStore extends AbstractSQLStore {

    public static final String TABLE_NAME_INVOICENO_TRANSACTION = "INVOICENO_TRANSACTION";
    public static final String INVOICENO_TRANSACTION_TRANSACTION_ID = "TRANSACTION_ID";
    public static final String INVOICENO_TRANSACTION_INVOICE_NO = "INVOICE_NO";
    public static final String INVOICENO_TRANSACTION_STATE = "STATE";


    public InvoiceNoTransactionStore(SQLDatabaseQueue mDb) {
        super(mDb);
    }

    private void updateTransaction(SQLDatabase db,InvoiceNoTransaction transaction) {
        ContentValues values = new ContentValues();
        values.put(INVOICENO_TRANSACTION_INVOICE_NO, transaction.getInvoiceNo());
        values.put(INVOICENO_TRANSACTION_STATE, transaction.getState().getId());
        db.updateOrThrow(TABLE_NAME_INVOICENO_TRANSACTION, values, eq(INVOICENO_TRANSACTION_TRANSACTION_ID, String.valueOf(transaction.getTransactionId())));
    }

    private void updateTransactionState(int transactionId, InvoiceNoState state) {

        Future<Void> future = mDb.submit(new SQLCallable<Void>() {
            @Override
            public Void call(SQLDatabase db) {
                ContentValues values = new ContentValues();
                values.put(INVOICENO_TRANSACTION_STATE, state.getId());
                db.updateOrThrow(TABLE_NAME_INVOICENO_TRANSACTION, values, eq(INVOICENO_TRANSACTION_TRANSACTION_ID, String.valueOf(transactionId)));
                return null;
            }
        });

        get(future);

    }

    public InvoiceNoTransaction resetTransaction(int startNo) {

        Future<InvoiceNoTransaction> future = mDb.submit(new SQLCallable<InvoiceNoTransaction>() {
            @Override
            public InvoiceNoTransaction call(SQLDatabase db) {
                InvoiceNoTransaction transaction = getLastTransactionInternal(db);
                if (transaction != null) {
                    InvoiceNoState state = transaction.getState();
                    if (state == InvoiceNoState.RESET) {
                        transaction.setInvoiceNo(startNo);
                        updateTransaction(db,transaction);
                    }
                }

                ContentValues values = new ContentValues();
                values.put(INVOICENO_TRANSACTION_INVOICE_NO, startNo);
                values.put(INVOICENO_TRANSACTION_STATE, InvoiceNoState.RESET.getId());
                db.insertOrThrow(TABLE_NAME_INVOICENO_TRANSACTION, values);

                return transaction;

            }
        });

        return get(future);


    }

    public InvoiceNoTransaction newTransaction() {
        InvoiceNoTransaction transaction = getLastTransaction();
        if (transaction == null) {
            return insertTransaction(new InvoiceNoTransaction(0, 1, InvoiceNoState.ACQUIRED));
        } else {
            if (transaction.getState() == InvoiceNoState.RESET || transaction.getState() == InvoiceNoState.ACQUIRED) {
                return insertTransaction(new InvoiceNoTransaction(0, transaction.getInvoiceNo(), InvoiceNoState.ACQUIRED));
            } else {
                return insertTransaction(new InvoiceNoTransaction(0, transaction.getInvoiceNo() + 1, InvoiceNoState.ACQUIRED));
            }
        }

    }

    protected InvoiceNoTransaction insertTransaction(InvoiceNoTransaction transaction) {
        Future<InvoiceNoTransaction> future = mDb.submit(new SQLCallable<InvoiceNoTransaction>() {
            @Override
            public InvoiceNoTransaction call(SQLDatabase db) {
                ContentValues values = new ContentValues();
                values.put(INVOICENO_TRANSACTION_INVOICE_NO, transaction.getInvoiceNo());
                values.put(INVOICENO_TRANSACTION_STATE, transaction.getState().getId());
                db.insertOrThrow(TABLE_NAME_INVOICENO_TRANSACTION, values);
                int transactionId = db.queryMax(TABLE_NAME_INVOICENO_TRANSACTION, INVOICENO_TRANSACTION_TRANSACTION_ID, 1);
                return new InvoiceNoTransaction(transactionId, transaction.getInvoiceNo(), transaction.getState());
            }
        });
        return get(future);
    }

    public void commitTransaction(int transactionId) {
        updateTransactionState(transactionId, InvoiceNoState.USED);
    }

    public InvoiceNoTransaction getLastTransaction() {

        Future<InvoiceNoTransaction> future = mDb.submit(new SQLCallable<InvoiceNoTransaction>() {
            @Override
            public InvoiceNoTransaction call(SQLDatabase db) {
                return getLastTransactionInternal(db);
            }
        });
        
        return get(future);


    }

    @Nullable
    private InvoiceNoTransaction getLastTransactionInternal(SQLDatabase db) {
        InvoiceNoTransaction result = null;
        Cursor cursor = db.query(TABLE_NAME_INVOICENO_TRANSACTION,
                new String[]{INVOICENO_TRANSACTION_TRANSACTION_ID, INVOICENO_TRANSACTION_INVOICE_NO, INVOICENO_TRANSACTION_STATE},
                null,
                Collections.singletonList(INVOICENO_TRANSACTION_TRANSACTION_ID + " DESC"), null, 1);

        if (cursor.moveToFirst()) {

            int transactionIdColIndex = cursor.getColumnIndex(INVOICENO_TRANSACTION_TRANSACTION_ID);
            int invoiceNoColIndex = cursor.getColumnIndex(INVOICENO_TRANSACTION_INVOICE_NO);
            int stateColIndex = cursor.getColumnIndex(INVOICENO_TRANSACTION_STATE);

            int transactionId = cursor.getInt(transactionIdColIndex);
            int invoiceNo = cursor.getInt(invoiceNoColIndex);
            String state = cursor.getString(stateColIndex);

            result = new InvoiceNoTransaction(transactionId, invoiceNo, InvoiceNoState.fromString(state));

        }
        cursor.close();
        return result;
    }

}
