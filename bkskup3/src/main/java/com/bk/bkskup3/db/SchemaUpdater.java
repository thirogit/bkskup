package com.bk.bkskup3.db;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class SchemaUpdater {

    private SQLDatabaseQueue mDb;

    public SchemaUpdater(SQLDatabaseQueue db) {
        this.mDb = db;
    }

    public void update(InputStream scriptIS, int version) throws IOException {
        if(mDb.getVersion() < version) {
            ScriptTokenizer tokenizer = new ScriptTokenizer(scriptIS);
            List<String> statements = tokenizer.parse();
            SchemaOnlyMigration migration = new SchemaOnlyMigration(statements.toArray(new String[]{}));
            mDb.updateSchema(migration,version);
        }
    }

}
