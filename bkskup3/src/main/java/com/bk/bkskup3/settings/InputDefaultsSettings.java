package com.bk.bkskup3.settings;

import com.bk.bksettings.annotation.SettingField;
import com.bk.bksettings.annotation.SettingTypeAdapter;
import com.bk.bkskup3.model.HentType;
import com.bk.bkskup3.model.PayWay;
import com.bk.bkskup3.settings.adapter.HentTypeAdapter;
import com.bk.bkskup3.settings.adapter.PayWayAdapter;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/21/2014
 * Time: 11:43 PM
 */
public class InputDefaultsSettings implements Serializable {

    @SettingTypeAdapter(PayWayAdapter.class)
    @SettingField("DEFAULT_PAYWAY_COMPANY")
    private PayWay defaultPayWayForCompany;

    @SettingTypeAdapter(PayWayAdapter.class)
    @SettingField("DEFAULT_PAYWAY_INDIVIDUAL")
    private PayWay defaultPayWayForIndividual;

    @SettingField("DEFAULT_PAYDUEDAYS_COMPANY")
    private Integer defaultPayDueDaysForCompany;

    @SettingField("DEFAULT_PAYDUEDAYS_INDIVIDUAL")
    private Integer defaultPayDueDaysForIndividual;

    @SettingTypeAdapter(HentTypeAdapter.class)
    @SettingField("DEFAULT_HENTTYPE")
    private HentType defaultHentType;

    @SettingField("DEFAULT_COUNTRY")
    private String defaultCountry;

    public PayWay getDefaultPayWayForCompany() {
        return defaultPayWayForCompany;
    }

    public void setDefaultPayWayForCompany(PayWay defaultPayWayForCompany) {
        this.defaultPayWayForCompany = defaultPayWayForCompany;
    }

    public PayWay getDefaultPayWayForIndividual() {
        return defaultPayWayForIndividual;
    }

    public void setDefaultPayWayForIndividual(PayWay defaultPayWayForIndividual) {
        this.defaultPayWayForIndividual = defaultPayWayForIndividual;
    }

    public Integer getDefaultPayDueDaysForCompany() {
        return defaultPayDueDaysForCompany;
    }

    public void setDefaultPayDueDaysForCompany(Integer defaultPayDueDaysForCompany) {
        this.defaultPayDueDaysForCompany = defaultPayDueDaysForCompany;
    }

    public Integer getDefaultPayDueDaysForIndividual() {
        return defaultPayDueDaysForIndividual;
    }

    public void setDefaultPayDueDaysForIndividual(Integer defaultPayDueDaysForIndividual) {
        this.defaultPayDueDaysForIndividual = defaultPayDueDaysForIndividual;
    }

    public HentType getDefaultHentType() {
        return defaultHentType;
    }

    public void setDefaultHentType(HentType defaultHentType) {
        this.defaultHentType = defaultHentType;
    }

    public String getDefaultCountry() {
        return defaultCountry;
    }

    public void setDefaultCountry(String defaultCountry) {
        this.defaultCountry = defaultCountry;
    }

}
