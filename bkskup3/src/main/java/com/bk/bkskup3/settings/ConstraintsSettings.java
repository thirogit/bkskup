package com.bk.bkskup3.settings;


import com.bk.bksettings.annotation.SettingField;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 10/1/2014
 * Time: 10:47 AM
 */
public class ConstraintsSettings implements Serializable {
    @SettingField("REQ_PERSONAL_INFO_4_LUMP")
    private Boolean requirePersonalInfoForLump;

    @SettingField("REQ_FISCAL_NO_4_REGULAR")
    private Boolean requireFiscalNoForRegular;

    public Boolean getRequirePersonalInfoForLump()
    {
        return requirePersonalInfoForLump;
    }

    public void setRequirePersonalInfoForLump(Boolean requirePersonalInfoForLump)
    {
        this.requirePersonalInfoForLump = requirePersonalInfoForLump;
    }

    public Boolean getRequireFiscalNoForRegular()
    {
        return requireFiscalNoForRegular;
    }

    public void setRequireFiscalNoForRegular(Boolean requireFiscalNoForRegular)
    {
        this.requireFiscalNoForRegular = requireFiscalNoForRegular;
    }
}
