package com.bk.bkskup3;

import android.app.ListActivity;
import android.os.Bundle;

public class BkListActivity extends ListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BkApplication bkApplication = (BkApplication) getApplication();
        bkApplication.inject(this);
    }
}
