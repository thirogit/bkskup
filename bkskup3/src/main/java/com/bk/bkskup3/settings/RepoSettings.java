package com.bk.bkskup3.settings;



import com.bk.bksettings.annotation.SettingAccessType;
import com.bk.bksettings.annotation.SettingAccessorType;
import com.bk.bksettings.annotation.SettingField;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 10/1/2014
 * Time: 10:51 AM
 */
public class RepoSettings implements Serializable {

    public static final String REPO_ADDRESS = "https://us-central1-bkskup.cloudfunctions.net/";

    @SettingAccessorType(SettingAccessType.FIELD)
    @SettingField("REPO_LOGIN")
    private String mRepoLogin;

    @SettingAccessorType(SettingAccessType.FIELD)
    @SettingField("REPO_PASSWORD")
    private String mRepoPassword;

    public String getRepoLogin() {
        return mRepoLogin;
    }

    public void setRepoLogin(String repoLogin) {
        this.mRepoLogin = repoLogin;
    }

    public String getRepoPassword() {
        return mRepoPassword;
    }

    public void setRepoPassword(String repoPassword) {
        this.mRepoPassword = repoPassword;
    }

    public void copyFrom(RepoSettings settings) {
        this.mRepoLogin = settings.getRepoLogin();
        this.mRepoPassword = settings.getRepoPassword();
    }
}
