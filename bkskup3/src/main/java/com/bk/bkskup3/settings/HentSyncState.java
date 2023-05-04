package com.bk.bkskup3.settings;

import com.bk.bksettings.annotation.SettingAccessType;
import com.bk.bksettings.annotation.SettingAccessorType;
import com.bk.bksettings.annotation.SettingField;

public class HentSyncState {

    @SettingAccessorType(SettingAccessType.FIELD)
    @SettingField("LAST_FETCH_MAX_MODIFIED")
    private Long lastFetchMaxModified;

    public Long getLastFetchMaxModified() {
        return lastFetchMaxModified;
    }

    public void setLastFetchMaxModified(Long lastFetchMaxModified) {
        this.lastFetchMaxModified = lastFetchMaxModified;
    }
}
