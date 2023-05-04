package com.bk.bkskup3;

import android.app.Activity;
import android.os.Bundle;

public class BkActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BkApplication bkApplication = (BkApplication) getApplication();
        bkApplication.inject(this);
    }
}
