package com.bk.bksettings.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.bk.bksettings.annotation.adapter.SettingAdapter;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME) @Target({FIELD})
public @interface SettingTypeAdapter
{
       Class<? extends SettingAdapter> value();
}
