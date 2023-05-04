package com.bk.bkskup3.settings.adapter;

import com.bk.bksettings.annotation.adapter.SettingAdapter;
import com.bk.bksettings.runtime.SettingsException;
import com.bk.bkskup3.model.EAN;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 2/6/2015
 * Time: 11:21 PM
 */
public class EANAdapter extends SettingAdapter<String,EAN>
{
    @Override
    public EAN unmarshal(String eanStr) throws SettingsException
    {
        return EAN.fromString(eanStr);
    }

    @Override
    public String marshal(EAN ean) throws SettingsException
    {
        return ean.toString();
    }
}