package com.bk.bksettings.annotation.adapter;

import com.bk.bksettings.runtime.SettingsException;

public abstract class SettingAdapter<ValueType, BoundType>
{
   protected SettingAdapter() {}

   public abstract BoundType unmarshal(ValueType v) throws SettingsException;
   public abstract ValueType marshal(BoundType v) throws SettingsException;
}