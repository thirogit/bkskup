package com.bk.bkskup3.settings;


import com.bk.bksettings.annotation.SettingField;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 10/1/2014
 * Time: 10:51 AM
 */
public class TaxSettings implements Serializable {

    @SettingField("VAT_RATE_FOR_COMPANY")
    private Double vatRateForCompany;

    @SettingField("VAT_RATE_FOR_INDIVIDUAL")
    private Double vatRateForIndividual;

    public Double getVatRateForCompany()
    {
        return vatRateForCompany;
    }

    public void setVatRateForCompany(Double vatRateForCompany)
    {
        this.vatRateForCompany = vatRateForCompany;
    }

    public Double getVatRateForIndividual()
    {
        return vatRateForIndividual;
    }

    public void setVatRateForIndividual(Double vatRateForIndividual)
    {
        this.vatRateForIndividual = vatRateForIndividual;
    }
}
