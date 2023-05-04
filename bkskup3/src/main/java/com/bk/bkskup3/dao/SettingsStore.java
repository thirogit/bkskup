package com.bk.bkskup3.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.bk.bksettings.model.Setting;
import com.bk.bksettings.model.SettingType;
import com.bk.bksettings.runtime.SettingSetter;
import com.bk.bksettings.runtime.SettingsException;
import com.bk.bksettings.runtime.SettingsIntrospector;
import com.bk.bkskup3.db.SQLCallable;
import com.bk.bkskup3.db.SQLDatabase;
import com.bk.bkskup3.db.SQLDatabaseQueue;
import com.bk.bkskup3.model.Agent;
import com.bk.bkskup3.model.Company;
import com.bk.bkskup3.settings.AgentAsSettings;
import com.bk.bkskup3.settings.CompanyAsSettings;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.Future;

public class SettingsStore extends AbstractSQLStore {

    public static final String TABLE_NAME_SETTINGS = "SETTINGS";
    public static final String SETTINGS_NAME = "NAME";
    public static final String SETTINGS_TYPE = "TYPE";
    public static final String SETTINGS_VALUE = "VALUE";
    public static final int MAX_SETTING_VALUE_LEN = 70;


    public SettingsStore(SQLDatabaseQueue mDb) {
        super(mDb);
    }

    public <T> void saveSettings(T settingsObj) throws SettingsException {

        SettingsIntrospector introspector = new SettingsIntrospector(settingsObj);
        Collection<Setting> settings = introspector.save();

        for (Setting setting : settings) {


            Future<Void> future = mDb.submitTransaction(new SQLCallable<Void>() {
                @Override
                public Void call(SQLDatabase db) {
                    ContentValues settingValues = new ContentValues();

                    settingValues.put(SETTINGS_TYPE, setting.getType().getId());
                    settingValues.put(SETTINGS_VALUE, setting.getValue());

                    ContentValues settingCondition = new ContentValues();
                    settingCondition.put(SETTINGS_NAME, setting.getName());

                    if (db.queryCount(TABLE_NAME_SETTINGS, settingCondition) > 0) {
                        db.updateOrThrow(TABLE_NAME_SETTINGS, settingValues, settingCondition);
                    } else {
                        settingValues.put(SETTINGS_NAME, setting.getName());
                        db.insertOrThrow(TABLE_NAME_SETTINGS, settingValues);
                    }
                    return null;
                }
            });

            get(future);


        }
    }

    public <T> T loadSettings(Class<T> settingsClass) throws SettingsException {
        Collection<Setting> result = new LinkedList<Setting>();

        try {
            T settingsObj = settingsClass.newInstance();
            SettingsIntrospector introspector = new SettingsIntrospector(settingsObj);

            for (SettingSetter setter : introspector.load()) {
                Setting setting = fetchSetting(setter.getSettingName());
                if (setting != null) {
                    setter.set(setting);
                }
            }

            return settingsObj;

        } catch (InstantiationException e) {
            throw new SettingsException(e);
        } catch (IllegalAccessException e) {
            throw new SettingsException(e);
        }

    }

    public Setting fetchSetting(String settingName) throws SettingsException {

        Future<Setting> fetchFuture = mDb.submit(new SQLCallable<Setting>() {
            @Override
            public Setting call(SQLDatabase db) throws Exception {
                ContentValues where = new ContentValues();
                where.put(SETTINGS_NAME, settingName);
                Cursor settingsCursor =
                        db.query(TABLE_NAME_SETTINGS,
                                new String[]{SETTINGS_TYPE, SETTINGS_VALUE},
                                where);

                int settingTypeColIndex = settingsCursor.getColumnIndexOrThrow(SETTINGS_TYPE);
                int settingValueColIndex = settingsCursor.getColumnIndexOrThrow(SETTINGS_VALUE);

                if (settingsCursor.moveToNext()) {
                    Setting setting = new Setting(settingName);
                    String settingTypeStr = settingsCursor.getString(settingTypeColIndex);
                    SettingType settingType = SettingType.fromString(settingTypeStr);

                    if (settingType == null) {
                        throw new SettingsException("Unknown setting type: " + settingTypeStr);
                    }

                    setting.setType(settingType);
                    setting.setValue(settingsCursor.getString(settingValueColIndex));
                    return setting;
                }
                return null;
            }
        });
        return get(fetchFuture);
    }

    public Agent getAgent() {
        return loadSettings(AgentAsSettings.class);
    }

    public Company getCompany() {
        return loadSettings(CompanyAsSettings.class);
    }

    public void saveCompany(Company companyUpdate) {
        CompanyAsSettings companyAsSettings = new CompanyAsSettings();
        companyAsSettings.copyFrom(companyUpdate);
        saveSettings(companyAsSettings);
    }

    public void saveAgent(Agent agent) {
        AgentAsSettings agentAsSettings = new AgentAsSettings();
        agentAsSettings.copyFrom(agent);
        saveSettings(agentAsSettings);
    }

}
