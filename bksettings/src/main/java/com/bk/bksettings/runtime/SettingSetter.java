package com.bk.bksettings.runtime;

import com.bk.bksettings.model.Setting;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/8/2014
 * Time: 3:57 PM
 */
public interface SettingSetter {
    String getSettingName();
    void set(Setting value);
}
