package com.bk.bksettings.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import com.bk.bksettings.model.Setting;
import com.bk.bksettings.model.SettingValueProxy;
import com.bk.bksettings.runtime.AccessorException;
import com.bk.bksettings.runtime.SettingsException;
import com.bk.bksettings.model.SettingType;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/26/12
 * Time: 4:36 PM
 */
public class SettingSeed {
    private Field field;
    private String name;
    private AdapterBean adapter;
    Accessor accessor;

    public SettingSeed(Field field) {
        this.field = field;

        SettingField settingAnnotation = this.field.getAnnotation(SettingField.class);
        if (settingAnnotation == null) {
            throw new IllegalArgumentException("Field " + field.getName() + " is not annotated with " + SettingField.class.getSimpleName());
        }

        this.name = settingAnnotation.value();

        SettingTypeAdapter adapterAnnotation = this.field.getAnnotation(SettingTypeAdapter.class);
        if (adapterAnnotation != null) {
            this.adapter = new AdapterBean(adapterAnnotation.value());
        }

        SettingAccessorType accessorTypeAnnotation = this.field.getAnnotation(SettingAccessorType.class);
        if (accessorTypeAnnotation == null) {
            accessor = new GetterSetterAccessor(this.field);
        }
        else
        {
            switch (accessorTypeAnnotation.value())
            {
                case PROPERTY:
                    accessor = new GetterSetterAccessor(this.field);
                break;
                case FIELD:
                    accessor = new DirectAccessor(this.field);
            }
        }


    }

    public String getSettingName() {
        return name;
    }

    public void set(Setting setting, Object settingsObj) throws SettingsException {
        try {
            SettingValueProxy proxy = new SettingValueProxy(setting);
            Object value = proxy.getValue();
            if (hasAdapter()) {
                adapter.checkIfAdapterApplicableForSetting(setting);
                if (value != null) {
                    accessor.set(settingsObj, adapter.adaptFromSimpleType(value));
                }
            } else {
                if (!field.getType().equals(setting.getType().getJavaType())) {
                    throw new SettingsException("Field " + field.getName() + " of type " + field.getType().getName() + " is not assignable from " + setting.getType().getId());
                }
                accessor.set(settingsObj, value);
            }
        } catch (AccessorException e) {
            throw new SettingsException(e);
        }
    }

    public boolean hasAdapter() {
        return adapter != null;
    }


    public Setting get(Object settingObj) throws SettingsException {
        try {
            Setting setting = new Setting(getSettingName());
            setting.setType(getSettingType());
            SettingValueProxy proxy = new SettingValueProxy(setting);
            Object value = accessor.get(settingObj);
            if (hasAdapter() && value != null) {
                adapter.checkIfAdapterApplicableForField(field);
                Object adaptedSettingValue = adapter.adaptToSimpleType(value);
                proxy.setValue(adaptedSettingValue);
            } else {
                proxy.setValue(value);
            }
            return setting;
        } catch (AccessorException e) {
            throw new SettingsException(e);
        }
    }

    private SettingType getSettingType() {
        Type boundType;
        if (hasAdapter()) {
            boundType = adapter.getValueType();
        } else {
            boundType = field.getType();
        }
        return SettingType.fromJavaType(boundType);
    }


}
