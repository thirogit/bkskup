package com.bk.bksettings.runtime;

import com.bk.bksettings.annotation.SettingField;
import com.bk.bksettings.annotation.SettingSeed;
import com.bk.bksettings.model.Setting;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/8/2014
 * Time: 3:13 PM
 */
public class SettingsIntrospector {


    static class SettingSetterImpl implements SettingSetter
    {
        SettingSeed seed;
        Object settingObj;

        SettingSetterImpl(SettingSeed seed, Object settingObj) {
            this.seed = seed;
            this.settingObj = settingObj;
        }

        @Override
        public String getSettingName() {
            return seed.getSettingName();
        }

        @Override
        public void set(Setting value) {
            seed.set(value,settingObj);
        }
    }

    private Collection<SettingSeed> mSeeds;
    private Object mSettingsObj;

    public SettingsIntrospector(Object settingsObj) {

        mSettingsObj = settingsObj;
        Class thisClass = settingsObj.getClass();
        Field classFields[] = thisClass.getDeclaredFields();
        mSeeds = new ArrayList<SettingSeed>(classFields.length);
        for (Field f : classFields)
        {
            if (f.isAnnotationPresent(SettingField.class))
            {
                mSeeds.add(new SettingSeed(f));
            }
        }
    }

    public Collection<Setting> save()
    {
        Collection<Setting> result = new ArrayList<Setting>(mSeeds.size());
        for(SettingSeed seed : mSeeds)
        {
            result.add(seed.get(mSettingsObj));
        }
        return result;
    }

    public Iterable<SettingSetter> load()
    {
        ArrayList<SettingSetter> result = new ArrayList<SettingSetter>(mSeeds.size());
        for (SettingSeed seed : mSeeds)
        {
            result.add(new SettingSetterImpl(seed,mSettingsObj));
        }

        return result;
    }


}
