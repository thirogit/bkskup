package com.bk.bkskup3.db;

import android.content.ContentResolver;
import android.database.*;
import android.net.Uri;
import android.os.Bundle;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/13/2014
 * Time: 10:57 AM
 */
public class BKCursor implements Cursor{

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private Cursor delegate;

    public BKCursor(Cursor delegate) {
        this.delegate = delegate;
    }

    public Date getDate(int colIndex)
    {
        String dtStr = delegate.getString(colIndex);
        try {
            if (dtStr != null) {

                DateFormat iso8601Format = new SimpleDateFormat(DATE_FORMAT);
                return iso8601Format.parse(dtStr);
            }
        } catch (ParseException e) {
            throw new SQLException("cannot parse '" + dtStr + "' with " + DATE_FORMAT, e);
        }
        return null;
    }

    @Override
    public int getCount() {
        return delegate.getCount();
    }

    @Override
    public int getPosition() {
        return delegate.getPosition();
    }

    @Override
    public boolean move(int offset) {
        return delegate.move(offset);
    }

    @Override
    public boolean moveToPosition(int position) {
        return delegate.moveToPosition(position);
    }

    @Override
    public boolean moveToFirst() {
        return delegate.moveToFirst();
    }

    @Override
    public boolean moveToLast() {
        return delegate.moveToLast();
    }

    @Override
    public boolean moveToNext() {
        return delegate.moveToNext();
    }

    @Override
    public boolean moveToPrevious() {
        return delegate.moveToPrevious();
    }

    @Override
    public boolean isFirst() {
        return delegate.isFirst();
    }

    @Override
    public boolean isLast() {
        return delegate.isLast();
    }

    @Override
    public boolean isBeforeFirst() {
        return delegate.isBeforeFirst();
    }

    @Override
    public boolean isAfterLast() {
        return delegate.isAfterLast();
    }

    @Override
    public int getColumnIndex(String columnName) {
        return delegate.getColumnIndex(columnName);
    }

    @Override
    public int getColumnIndexOrThrow(String columnName) throws IllegalArgumentException {
        return delegate.getColumnIndexOrThrow(columnName);
    }

    @Override
    public String getColumnName(int columnIndex) {
        return delegate.getColumnName(columnIndex);
    }

    @Override
    public String[] getColumnNames() {
        return delegate.getColumnNames();
    }

    @Override
    public int getColumnCount() {
        return delegate.getColumnCount();
    }

    @Override
    public byte[] getBlob(int columnIndex) {
        return delegate.getBlob(columnIndex);
    }

    @Override
    public String getString(int columnIndex) {
        return delegate.getString(columnIndex);
    }

    @Override
    public void copyStringToBuffer(int columnIndex, CharArrayBuffer buffer) {
        delegate.copyStringToBuffer(columnIndex, buffer);
    }

    @Override
    public short getShort(int columnIndex) {
        return delegate.getShort(columnIndex);
    }

    @Override
    public int getInt(int columnIndex) {
        return delegate.getInt(columnIndex);
    }

    @Override
    public long getLong(int columnIndex) {
        return delegate.getLong(columnIndex);
    }

    @Override
    public float getFloat(int columnIndex) {
        return delegate.getFloat(columnIndex);
    }

    @Override
    public double getDouble(int columnIndex) {
        return delegate.getDouble(columnIndex);
    }

    @Override
    public int getType(int columnIndex) {
        return delegate.getType(columnIndex);
    }

    @Override
    public boolean isNull(int columnIndex) {
        return delegate.isNull(columnIndex);
    }

    @Override
    @Deprecated
    public void deactivate() {
        delegate.deactivate();
    }

    @Override
    @Deprecated
    public boolean requery() {
        return delegate.requery();
    }

    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public boolean isClosed() {
        return delegate.isClosed();
    }

    @Override
    public void registerContentObserver(ContentObserver observer) {
        delegate.registerContentObserver(observer);
    }

    @Override
    public void unregisterContentObserver(ContentObserver observer) {
        delegate.unregisterContentObserver(observer);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        delegate.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        delegate.unregisterDataSetObserver(observer);
    }

    @Override
    public void setNotificationUri(ContentResolver cr, Uri uri) {
        delegate.setNotificationUri(cr, uri);
    }

    @Override
    public Uri getNotificationUri() {
        return delegate.getNotificationUri();
    }

    @Override
    public boolean getWantsAllOnMoveCalls() {
        return delegate.getWantsAllOnMoveCalls();
    }

    @Override
    public void setExtras(Bundle extras) {
//        delegate.setExtras(extras);
    }

    @Override
    public Bundle getExtras() {
        return delegate.getExtras();
    }

    @Override
    public Bundle respond(Bundle extras) {
        return delegate.respond(extras);
    }
}
