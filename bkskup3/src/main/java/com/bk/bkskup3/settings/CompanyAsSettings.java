package com.bk.bkskup3.settings;

import com.bk.bksettings.annotation.SettingAccessType;
import com.bk.bksettings.annotation.SettingAccessorType;
import com.bk.bksettings.annotation.SettingField;
import com.bk.bksettings.annotation.SettingTypeAdapter;
import com.bk.bkskup3.dao.SettingsStore;
import com.bk.bkskup3.model.Company;
import com.bk.bkskup3.model.EAN;
import com.bk.bkskup3.settings.adapter.EANAdapter;
import com.bk.bkskup3.utils.JoinUtils;

import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 8/11/12
 * Time: 12:34 AM
 */
public class CompanyAsSettings implements Company {

    @SettingAccessorType(SettingAccessType.FIELD)
    @SettingField("COMPANY_NAME_1")
    protected String name_1;

    @SettingAccessorType(SettingAccessType.FIELD)
    @SettingField("COMPANY_NAME_2")
    protected String name_2;

    @SettingAccessorType(SettingAccessType.FIELD)
    @SettingField("COMPANY_NAME_3")
    protected String name_3;

    @SettingAccessorType(SettingAccessType.FIELD)
    @SettingField("COMPANY_NAME_4")
    protected String name_4;

    @SettingField("COMPANY_STREET")
    protected String street;
    @SettingField("COMPANY_POBOX")
    protected String poBox;
    @SettingField("COMPANY_CITY")
    protected String city;
    @SettingField("COMPANY_ZIP")
    protected String zip;
    @SettingField("COMPANY_FARMNO")
    @SettingTypeAdapter(EANAdapter.class)
    protected EAN farmNo;
    @SettingField("COMPANY_CELLPHONENO")
    protected String cellPhoneNo;
    @SettingField("COMPANY_PHONENO")
    protected String phoneNo;
    @SettingField("COMPANY_EMAIL")
    protected String emailAddress;
    @SettingField("COMPANY_NIP")
    protected String fiscalNo;

    public void copyFrom(Company company) {
        setName(company.getName());
        street = company.getStreet();
        poBox = company.getPoBox();
        city = company.getCity();
        zip = company.getZip();
        farmNo = company.getFarmNo();
        cellPhoneNo = company.getCellPhoneNo();
        phoneNo = company.getPhoneNo();
        emailAddress = company.getEmailAddress();
        fiscalNo = company.getFiscalNo();
    }

    public String getName() {
        return JoinUtils.join(name_1, name_2, name_3, name_4);
    }

    interface Setter {
        void set(String value);
    }

    public void setName(String name) {

        Iterable<String> nameParts = JoinUtils.split(name, SettingsStore.MAX_SETTING_VALUE_LEN);

        Setter[] nameSetters =
                {
                        new Setter() {
                            @Override
                            public void set(String value) {
                                name_1 = value;
                            }
                        },
                        new Setter() {
                            @Override
                            public void set(String value) {
                                name_2 = value;
                            }
                        },
                        new Setter() {
                            @Override
                            public void set(String value) {
                                name_3 = value;
                            }
                        },
                        new Setter() {
                            @Override
                            public void set(String value) {
                                name_4 = value;
                            }
                        }
                };

        Iterator<String> it = nameParts.iterator();
        for (Setter setter : nameSetters) {
            if (it.hasNext())
                setter.set(it.next());
            else
                setter.set(null);
        }
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPoBox() {
        return poBox;
    }

    public void setPoBox(String poBox) {
        this.poBox = poBox;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public EAN getFarmNo() {
        return farmNo;
    }

    public void setFarmNo(EAN farmNo) {
        this.farmNo = farmNo;
    }

    public String getCellPhoneNo() {
        return cellPhoneNo;
    }

    public void setCellPhoneNo(String cellPhoneNo) {
        this.cellPhoneNo = cellPhoneNo;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getFiscalNo() {
        return fiscalNo;
    }

    public void setFiscalNo(String fiscalNo) {
        this.fiscalNo = fiscalNo;
    }
}
