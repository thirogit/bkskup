package com.bk.bkskup3.jsonstore;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.bk.bkskup3.R;
import com.bk.bkskup3.db.SQLScript;
import com.bk.bkskup3.jsonstore.jsonflat.JsonFlat;
import com.bk.bkskup3.jsonstore.jsonflat.PancakeStorage;
import com.bk.bkskup3.jsonstore.jsonflat.pancake.JsonPancake;
import com.bk.bkskup3.jsonstore.jsonflat.pancake.PancakeValue;
import com.bk.bkskup3.jsonstore.jsonflat.pancake.PancakeValueType;
import com.bk.bkskup3.runtime.FatalException;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 1/15/2015
 * Time: 11:17 PM
 */
public class JsonStore {

    private static final String DATABASE_NAME = "bkskup3.jsonstore";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME_JSONSTORE = "JSONSTORE";
    public static final String JSONSTORE_OID = "OBJECT_ID";
    public static final String JSONSTORE_PATH = "PROPERTY_PATH";
    public static final String JSONSTORE_TYPE = "PROPERTY_TYPE";
    public static final String JSONSTORE_VALUE = "PROPERTY_VALUE";


    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private final Context mCtx;
    private JsonFlat mPress = new JsonFlat();


    private static class DatabaseHelper extends SQLiteOpenHelper {

        private final Context mCtx;

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            mCtx = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.beginTransaction();
            try {
                new SQLScript(mCtx.getResources().openRawResource(R.raw.jsonstore)).execute(db);
                db.setTransactionSuccessful();
            } catch (Exception e) {
                throw new FatalException(e);
            } finally {
                db.endTransaction();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.delete(TABLE_NAME_JSONSTORE,null,null);
        }

    }

    public JsonStore(Context ctx) {
        this.mCtx = ctx;
    }

    public JsonStore open() {
        if (mDbHelper == null) {
            mDbHelper = new DatabaseHelper(mCtx);
            mDb = mDbHelper.getWritableDatabase();
        }
        return this;
    }

    public void close() {
        if (mDbHelper != null) {
            mDb.close();
            mDbHelper.close();

            mDbHelper = null;
            mDb = null;
        }
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

    public void storeObject(JsonObj obj) {

        begin();
        try {
            deleteObject(obj.getOid());

            JsonNode node = obj.getNode();
            final int oid = obj.getOid();
            mPress.flatten(node, new PancakeStorage() {
                @Override
                public void store(JsonPancake pancake) {
                    insertPancake(oid, pancake);
                }
            });
            commit();
        } catch (RuntimeException e) {
            end();
            throw e;
        }
    }

    private ContentValues createValues(JsonPancake pancake) {
        ContentValues values = new ContentValues();
        values.put(JSONSTORE_PATH, pancake.getPath());
        PancakeValue value = pancake.getValue();
        values.put(JSONSTORE_TYPE, value.getType().name());
        values.put(JSONSTORE_VALUE, value.getValue());

        return values;
    }

    private void insertPancake(int oid, JsonPancake pancake) {
        ContentValues values = createValues(pancake);
        values.put(JSONSTORE_OID, oid);
        mDb.insertOrThrow(TABLE_NAME_JSONSTORE, null, values);
    }

    public JsonObj loadObject(int oid) {
        Collection<JsonPancake> pancakes = new LinkedList<JsonPancake>();
        Cursor cursor = mDb.query(TABLE_NAME_JSONSTORE, new String[]{JSONSTORE_PATH, JSONSTORE_TYPE, JSONSTORE_VALUE},
                JSONSTORE_OID + "=" + oid,null, null, null, null, null);

        int colIndexPath = cursor.getColumnIndex(JSONSTORE_PATH);
        int colIndexType = cursor.getColumnIndex(JSONSTORE_TYPE);
        int colIndexValue = cursor.getColumnIndex(JSONSTORE_VALUE);

        while (cursor.moveToNext()) {
            String path = cursor.getString(colIndexPath);
            String type = cursor.getString(colIndexType);
            String value = cursor.getString(colIndexValue);
            pancakes.add(new JsonPancake(path, new PancakeValue(PancakeValueType.valueOf(type), value)));
        }

        if (pancakes.isEmpty()) {
            return null;
        }

        return new JsonObj(oid, mPress.inflate(pancakes));
    }

    public JsonObj loadFirst() {
        Cursor cursor = mDb.query(TABLE_NAME_JSONSTORE, new String[]{"MIN(" + JSONSTORE_OID + ")"}, null, null, null, null, null);
        if (cursor.moveToNext()) {
            if (!cursor.isNull(0)) {
                int minOid = cursor.getInt(0);
                cursor.close();
                return loadObject(minOid);
            }
        }
        return null;
    }

    public void deleteObject(int oid) {
        mDb.delete(TABLE_NAME_JSONSTORE, JSONSTORE_OID + "=" + oid, null);
    }


}
