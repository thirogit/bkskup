package com.bk.bkskup3.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.bk.bkskup3.utils.Dates;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SQLDatabase {

    private SQLiteDatabase mDb;

    public int getVersion() {
        return this.mDb.getVersion();
    }

    public SQLDatabase open(File file) {
        mDb = SQLiteDatabase.openOrCreateDatabase(file, null);
        return this;
    }

    public void close() {
        mDb.close();
    }

    public int queryCount(String tableName, ContentValues queryConditions) {
        return queryCount(tableName, where(queryConditions));
    }

    public void beginTransaction() {
        this.mDb.beginTransaction();
    }

    public void endTransaction() {
        this.mDb.endTransaction();
    }

    public void setTransactionSuccessful() {
        this.mDb.setTransactionSuccessful();
    }


    public void execSQL(String sql) throws SQLException {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(sql), "Input SQL");
        this.mDb.execSQL(sql);
    }

    public BKCursor rawQuery(String sql, String[] values) {
        return new BKCursor(this.mDb.rawQuery(sql, values));
    }


    protected String where(ContentValues conditions) {
        StringBuilder whereCondition = new StringBuilder();

        if (conditions != null) {
            Iterator<Map.Entry<String, Object>> queryConditionIt = conditions.valueSet().iterator();
            while (queryConditionIt.hasNext()) {
                Map.Entry<String, Object> queryConditionPair = queryConditionIt.next();
                whereCondition.append(valueEquality(queryConditionPair));
                if (queryConditionIt.hasNext()) {
                    whereCondition.append(" AND ");
                }
            }
        }
        return whereCondition.toString();
    }

    public BKCursor query(String table, String[] columns, String where, long limit) {
        return query(table, columns, where, null, null, limit);
    }

    public BKCursor query(String table, String[] columns, String where, List<String> orderBy, String groupBy, long limit) {
        String limitClause = null;
        if (limit > 0)
            limitClause = String.valueOf(limit);

        String orderByClause = null;

        if (orderBy != null && !orderBy.isEmpty()) {
            orderByClause = Joiner.on(',').join(orderBy);
        }

        return new BKCursor(mDb.query(table,
                columns,
                where,
                null,
                groupBy,
                null,
                orderByClause,
                limitClause));
    }

    public BKCursor query(String table, String[] columns, ContentValues conditions) {
        return new BKCursor(mDb.query(table,
                columns,
                where(conditions),
                null,
                null,
                null,
                null));
    }

    public int queryCount(String tableName, String condition) {
        return queryScalar(tableName, "COUNT(*)", condition, 0);
    }

    public int queryMax(String tableName, String columnName, int whenNoData) {
        return queryScalar(tableName, "MAX(" + columnName + ')', null, whenNoData);
    }

    public int queryScalar(String tableName, String columnName, String condition, int whenNoData) {
        int result = whenNoData;
        Cursor scalarCursor = mDb.query(tableName, new String[]{columnName}, condition, null, null, null, null, null);
        if (scalarCursor.moveToFirst()) {
            result = scalarCursor.getInt(0);
        }
        scalarCursor.close();
        return result;

    }

    private String valueEquality(Map.Entry<String, Object> valuePair) {
        String key = valuePair.getKey();
        Object value = valuePair.getValue();

        String valueStr;

        while (true) {
            if (value == null) {
                valueStr = "NULL";
                break;
            } else if (value instanceof String) {
                value = Strings.emptyToNull((String) value);
                if (value == null) continue;
                valueStr = '\'' + value.toString().replace("'","''") + '\'';
                break;
            } else if (value instanceof Date) {
                value = Dates.toDateTimeLong((Date) value);
                continue;
            } else {
                valueStr = value.toString();
                break;
            }
        }

        return key + '=' + valueStr;
    }

    public void updateOrThrow(String table, ContentValues values, ContentValues whereConditions) throws SQLException {
        StringBuilder whereCondition = new StringBuilder();

        Iterator<Map.Entry<String, Object>> whereConditionIt = whereConditions.valueSet().iterator();
        while (whereConditionIt.hasNext()) {
            Map.Entry<String, Object> whereConditionPair = whereConditionIt.next();
            whereCondition.append(valueEquality(whereConditionPair));
            if (whereConditionIt.hasNext()) {
                whereCondition.append(" AND ");
            }
        }
        updateOrThrow(table, values, whereCondition.toString());
    }


    public void updateOrThrow(final String table,
                              final ContentValues values,
                              final String whereClause) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ").append(table).append(" SET ");


        final Iterator<Map.Entry<String, Object>> valuesIterator = values.valueSet().iterator();

        while (valuesIterator.hasNext()) {
            Map.Entry<String, Object> valuePair = valuesIterator.next();
            sql.append(valueEquality(valuePair));
            if (valuesIterator.hasNext()) {
                sql.append(", ");
            }

        }

        sql.append(" WHERE ").append(whereClause).append(';');

        mDb.execSQL(sql.toString());
    }

    public void deleteOrThrow(String tableName, ContentValues conditions) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ").append(tableName).append(" WHERE ").append(where(conditions)).append(';');
        mDb.execSQL(sql.toString());
    }

    public void insertOrThrow(String tableName, ContentValues values) {
        mDb.insertOrThrow(tableName, null, values);
    }

    public void begin() {
        mDb.beginTransaction();
    }

    public void commit() {
        mDb.setTransactionSuccessful();
        mDb.endTransaction();
    }

    public void end() {
        mDb.endTransaction();
    }

}

