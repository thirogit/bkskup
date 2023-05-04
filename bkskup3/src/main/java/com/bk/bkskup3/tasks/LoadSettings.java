package com.bk.bkskup3.tasks;

import android.os.AsyncTask;

import com.bk.bkskup3.dao.SettingsStore;
import com.bk.bkskup3.settings.ConstraintsSettings;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/14/2014
 * Time: 10:18 PM
 */
public class LoadSettings extends AsyncTask<Void, Void, TaskResult<ConstraintsSettings>> {

    private SettingsStore mSettingsStore;


    @Override
    protected TaskResult<ConstraintsSettings> doInBackground(Void... params) {

        return TaskResult.withResult(mSettingsStore.loadSettings(ConstraintsSettings.class));
    }
}
