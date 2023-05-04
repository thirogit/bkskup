package com.bk.bkskup3.dao;

import android.app.Service;
import android.content.ContentValues;
import android.database.Cursor;

import com.bk.bkskup3.db.SQLCallable;
import com.bk.bkskup3.db.SQLDatabase;
import com.bk.bkskup3.db.SQLDatabaseQueue;
import com.bk.bkskup3.library.DocumentOption;
import com.bk.bkskup3.library.DocumentPreference;
import com.bk.bkskup3.library.DocumentProfile;
import com.bk.bkskup3.library.DocumentProfileCount;
import com.bk.bkskup3.utils.JoinUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Future;

public class DocumentOptionsStore extends AbstractSQLStore {

    public static final int MAX_OPTION_VALUE_LEN = 100;

    public static final String TABLE_NAME_DOCUMENT_PREFERENCES = "DOCUMENT_PREFERENCES";
    public static final String DOCUMENT_PREFERENCES_DOCCODE = "DOCCODE";
    public static final String DOCUMENT_PREFERENCES_VISIBLE = "VISIBLE";

    public static final String TABLE_NAME_DOCUMENT_OPTIONS_PROFILE = "DOCUMENT_OPTIONS_PROFILE";
    public static final String DOCUMENT_OPTIONS_PROFILE_PROFILEID = "PROFILEID";
    public static final String DOCUMENT_OPTIONS_PROFILE_NAME = "PROFILENAME";
    public static final String DOCUMENT_OPTIONS_PROFILE_DOCCODE = "DOCCODE";


    public static final String TABLE_NAME_DOCUMENT_OPTIONS = "DOCUMENT_OPTIONS";
    public static final String DOCUMENT_OPTIONS_PROFILEID = "PROFILEID";
    public static final String DOCUMENT_OPTIONS_OPTIONNAME = "OPTIONNAME";
    public static final String DOCUMENT_OPTIONS_OPTIONVALUE = "OPTIONVALUE";
    public static final String DOCUMENT_OPTIONS_PARTINDEX = "PARTINDEX";

    public DocumentOptionsStore(SQLDatabaseQueue mDb) {
        super(mDb);
    }

    public Collection<DocumentPreference> fetchPreferences()
    {

        Future<Collection<DocumentPreference>> future = mDb.submit(new SQLCallable<Collection<DocumentPreference>>() {
            @Override
            public Collection<DocumentPreference> call(SQLDatabase db) throws Exception {
                LinkedList<DocumentPreference> preferences = new LinkedList<DocumentPreference>();
                Cursor cursor = null;
                try {

                    cursor = db.query(TABLE_NAME_DOCUMENT_PREFERENCES,
                            new String[]{DOCUMENT_PREFERENCES_DOCCODE,
                                    DOCUMENT_PREFERENCES_VISIBLE}, null, -1);

                    int docCodeColIndex = cursor.getColumnIndexOrThrow(DOCUMENT_PREFERENCES_DOCCODE);
                    int visibleColIndex = cursor.getColumnIndexOrThrow(DOCUMENT_PREFERENCES_VISIBLE);

                    while (cursor.moveToNext()) {

                        String docCode = cursor.getString(docCodeColIndex);
                        boolean visible = cursor.getInt(visibleColIndex) > 0;

                        DocumentPreference preference = new DocumentPreference(docCode);
                        preference.setVisible(visible);

                        preferences.add(preference);
                    }
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                return preferences;
            }
        });

        return get(future);


    }

    public Collection<DocumentProfileCount> fetchProfileCounts()
    {
        Future<Collection<DocumentProfileCount>> future = mDb.submit(new SQLCallable<Collection<DocumentProfileCount>>() {
            @Override
            public Collection<DocumentProfileCount> call(SQLDatabase db) {
                LinkedList<DocumentProfileCount> counts = new LinkedList<>();
                Cursor cursor = null;
                try {

                    cursor = db.query(TABLE_NAME_DOCUMENT_OPTIONS_PROFILE,
                            new String[]{DOCUMENT_OPTIONS_PROFILE_DOCCODE, "count(*)"}, null, null, DOCUMENT_OPTIONS_PROFILE_DOCCODE, -1);

                    while (cursor.moveToNext()) {

                        String docCode = cursor.getString(0);
                        int count = cursor.getInt(1);

                        DocumentProfileCount profileCount = new DocumentProfileCount(docCode);
                        profileCount.setProfileCount(count);

                        counts.add(profileCount);
                    }
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                return counts;
            }
        });

        return get(future);

    }

    public Collection<DocumentProfile> fetchProfiles(String docCode)
    {
        Future<Collection<DocumentProfile>> future = mDb.submit(new SQLCallable<Collection<DocumentProfile>>() {
            @Override
            public Collection<DocumentProfile> call(SQLDatabase db) {
                LinkedList<DocumentProfile> profiles = new LinkedList<DocumentProfile>();
                Cursor cursor = null;
                try {

                    ContentValues where = new ContentValues();
                    where.put(DOCUMENT_OPTIONS_PROFILE_DOCCODE, docCode);

                    cursor = db.query(TABLE_NAME_DOCUMENT_OPTIONS_PROFILE,
                            new String[]{DOCUMENT_OPTIONS_PROFILE_PROFILEID,
                                    DOCUMENT_OPTIONS_PROFILE_NAME,
                                    DOCUMENT_OPTIONS_PROFILE_DOCCODE}, where);

                    int profileIdColIndex = cursor.getColumnIndexOrThrow(DOCUMENT_OPTIONS_PROFILE_PROFILEID);
                    int profileNameColIndex = cursor.getColumnIndexOrThrow(DOCUMENT_OPTIONS_PROFILE_NAME);
//            int docCodeColIndex = cursor.getColumnIndexOrThrow(DOCUMENT_OPTIONS_PROFILE_DOCCODE);

                    while (cursor.moveToNext()) {

                        int profileId = cursor.getInt(profileIdColIndex);
                        String profileName = cursor.getString(profileNameColIndex);

                        DocumentProfile profile = new DocumentProfile(profileId);
                        profile.setProfileName(profileName);
                        profile.setDocumentCode(docCode);

                        profiles.add(profile);
                    }

                    for (DocumentProfile profile : profiles) {
                        Collection<DocumentOption> options = fetchOptions(db,profile.getProfileId());
                        profile.addAllOptions(options);
                    }

                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                return profiles;
            }
        });

        return get(future);


    }

    private Collection<DocumentOption> fetchOptions(SQLDatabase db,int profileId) {
        Map<String,DocumentOption> options = new HashMap<String, DocumentOption>();
        Cursor cursor = null;
        try {

            ContentValues where = new ContentValues();
            where.put(TABLE_NAME_DOCUMENT_OPTIONS,profileId);

            cursor = db.query(TABLE_NAME_DOCUMENT_OPTIONS,
                    new String[]{DOCUMENT_OPTIONS_OPTIONNAME,
                            DOCUMENT_OPTIONS_OPTIONVALUE
                    }, DOCUMENT_OPTIONS_PROFILE_PROFILEID + "=" + profileId,
                    Collections.singletonList(DOCUMENT_OPTIONS_PARTINDEX),null,-1);

            int optionNameColIndex = cursor.getColumnIndexOrThrow(DOCUMENT_OPTIONS_OPTIONNAME);
            int optionValueColIndex = cursor.getColumnIndexOrThrow(DOCUMENT_OPTIONS_OPTIONVALUE);

            while (cursor.moveToNext()) {

                String optionName = cursor.getString(optionNameColIndex);
                String optionValue = cursor.getString(optionValueColIndex);

                DocumentOption option = options.get(optionName);
                if(option != null)
                {
                    option.setOptionValue(option.getOptionValue() + optionValue);
                }
                else
                {
                    option = new DocumentOption(optionName);
                    option.setOptionValue(optionValue);
                    options.put(optionName,option);
                }
            }


        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return options.values();
    }

    public void insertPreference(DocumentPreference preference) {

        Future<Void> future = mDb.submit(new SQLCallable<Void>() {
            @Override
            public Void call(SQLDatabase db) {
                ContentValues preferenceValues = createPreferenceValues(preference);
                preferenceValues.put(DOCUMENT_PREFERENCES_DOCCODE,preference.getDocumentCode());
                db.insertOrThrow(TABLE_NAME_DOCUMENT_PREFERENCES, preferenceValues);
                return null;
            }
        });

        get(future);


    }

    private ContentValues createPreferenceValues(DocumentPreference preference) {
        ContentValues values = new ContentValues();
        values.put(DOCUMENT_PREFERENCES_VISIBLE,preference.isVisible() ? 1 : 0);
        return values;
    }

    public void updatePreference(DocumentPreference preference) {

        Future<Void> future = mDb.submit(new SQLCallable<Void>() {
            @Override
            public Void call(SQLDatabase db) {
                ContentValues values = createPreferenceValues(preference);
                ContentValues where = new ContentValues();
                where.put(DOCUMENT_PREFERENCES_DOCCODE, preference.getDocumentCode());
                db.updateOrThrow(TABLE_NAME_DOCUMENT_PREFERENCES, values, where);
                return null;
            }
        });

        get(future);


    }

    public void deleteProfile(int profileId)
    {
        Future<Void> future = mDb.submitTransaction(new SQLCallable<Void>() {
            @Override
            public Void call(SQLDatabase db) {
                deleteProfileOptions(db,profileId);

                ContentValues values = new ContentValues();
                values.put(DOCUMENT_OPTIONS_PROFILE_PROFILEID,profileId);
                db.deleteOrThrow(TABLE_NAME_DOCUMENT_OPTIONS_PROFILE,values);
                return null;
            }
        });

        get(future);


    }

    private void deleteProfileOptions(SQLDatabase db,int profileId)
    {
        ContentValues values = new ContentValues();
        values.put(DOCUMENT_OPTIONS_PROFILEID,profileId);
        db.deleteOrThrow(TABLE_NAME_DOCUMENT_OPTIONS,values);
    }

    private void insertProfileOptionPart(SQLDatabase db,int profileId,OptionValuePart option)
    {
        ContentValues optionValues = new ContentValues();
        optionValues.put(DOCUMENT_OPTIONS_OPTIONNAME,option.optionName);
        optionValues.put(DOCUMENT_OPTIONS_OPTIONVALUE,option.optionValue);
        optionValues.put(DOCUMENT_OPTIONS_PARTINDEX,option.partIndex);
        optionValues.put(DOCUMENT_OPTIONS_PROFILEID,profileId);
        db.insertOrThrow(TABLE_NAME_DOCUMENT_OPTIONS, optionValues);

    }

    public DocumentProfile insertProfile(DocumentProfile profile)
    {
        Future<DocumentProfile> future = mDb.submitTransaction(new SQLCallable<DocumentProfile>() {
            @Override
            public DocumentProfile call(SQLDatabase db) {
                ContentValues profileValues = new ContentValues();
                profileValues.put(DOCUMENT_OPTIONS_PROFILE_NAME, profile.getProfileName());
                profileValues.put(DOCUMENT_OPTIONS_PROFILE_DOCCODE, profile.getDocumentCode());
                db.insertOrThrow(TABLE_NAME_DOCUMENT_OPTIONS_PROFILE, profileValues);
                int id = db.queryMax(TABLE_NAME_DOCUMENT_OPTIONS_PROFILE, DOCUMENT_OPTIONS_PROFILE_PROFILEID, 1);

                DocumentProfile insertedProfile = new DocumentProfile(id);
                insertedProfile.setDocumentCode(profile.getDocumentCode());
                insertedProfile.setProfileName(profile.getProfileName());

                for (DocumentOption option : profile.getOptions()) {
                    insertProfileOption(db, id, option);
                    insertedProfile.addOption(option.getOptionName(), option.getOptionValue());
                }

                return insertedProfile;
            }
        });

        return get(future);

    }

    private void insertProfileOption(SQLDatabase db,int profileId, DocumentOption option) {
        Collection<OptionValuePart> parts = divideValue(option);
        for(OptionValuePart part : parts)
        {
            insertProfileOptionPart(db,profileId,part);
        }
    }

    private Collection<OptionValuePart> divideValue(DocumentOption option) {
        String optionValue = option.getOptionValue();
        Iterable<String> valueParts = JoinUtils.split(optionValue, MAX_OPTION_VALUE_LEN);
        Collection<OptionValuePart> parts = new LinkedList<>();
        int index = 0;
        for(String valuePart : valueParts)
        {
            OptionValuePart part = new OptionValuePart();
            part.optionName = option.getOptionName();
            part.optionValue = valuePart;
            part.partIndex = index;
            parts.add(part);
            index++;
        }
        return parts;
    }

    public void updateProfile(DocumentProfile profile) {

        Future<Void> future = mDb.submitTransaction(new SQLCallable<Void>() {
            @Override
            public Void call(SQLDatabase db) {
                int id = profile.getProfileId();
                deleteProfileOptions(db, id);
                for (DocumentOption option : profile.getOptions()) {
                    insertProfileOption(db, id, option);
                }
                return null;
            }
        });

        get(future);


    }

    private static class OptionValuePart
    {
        String optionName;
        String optionValue;
        int partIndex;
    }


}
