package com.bk.bkskup3.db;

import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 1/16/2015
 * Time: 10:47 PM
 */
public class SQLScript {

    private InputStream mScriptInput;

    public SQLScript(InputStream mScriptInput) {
        this.mScriptInput = mScriptInput;
    }

    public void execute(SQLiteDatabase db) throws IOException {
        ScriptTokenizer scriptTokenizer = new ScriptTokenizer(mScriptInput);
        List<String> scriptSQLs = scriptTokenizer.parse();
        for (String sqlStmt : scriptSQLs) {
            db.execSQL(sqlStmt);
        }
    }

}
