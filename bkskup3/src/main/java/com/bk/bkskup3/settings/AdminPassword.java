package com.bk.bkskup3.settings;

import com.bk.bksettings.annotation.SettingField;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/10/2014
 * Time: 3:58 PM
 */
public class AdminPassword {

    @SettingField("ADMIN_PASSWORD")
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
