package com.bk.bkskup3.settings;

import com.bk.bksettings.annotation.SettingAccessType;
import com.bk.bksettings.annotation.SettingAccessorType;
import com.bk.bksettings.annotation.SettingField;
import com.bk.bkskup3.dao.SettingsStore;
import com.bk.bkskup3.utils.JoinUtils;

import java.io.Serializable;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 8/11/12
 * Time: 12:34 AM
 */
public class InvoiceSettings implements Serializable {

    public static final String ACTION_SETTINGS_CHANGED = InvoiceSettings.class.getName() + ".CHANGED";
    public static final String EXTRA_LATEST_SETTINGS = "latest_settings";

    @SettingAccessorType(SettingAccessType.FIELD)
    @SettingField("INVOICENO_FMT")
    protected String invoiceNoFmt;

    @SettingAccessorType(SettingAccessType.FIELD)
    @SettingField("LUMP_EXTRAS_TEMPLATE_1")
    protected String lump_extras_template_1;

    @SettingAccessorType(SettingAccessType.FIELD)
    @SettingField("LUMP_EXTRAS_TEMPLATE_2")
    protected String lump_extras_template_2;

    @SettingAccessorType(SettingAccessType.FIELD)
    @SettingField("LUMP_EXTRAS_TEMPLATE_3")
    protected String lump_extras_template_3;

    @SettingAccessorType(SettingAccessType.FIELD)
    @SettingField("LUMP_EXTRAS_TEMPLATE_4")
    protected String lump_extras_template_4;

    @SettingAccessorType(SettingAccessType.FIELD)
    @SettingField("REG_EXTRAS_TEMPLATE_1")
    protected String reg_extras_template_1;

    @SettingAccessorType(SettingAccessType.FIELD)
    @SettingField("REG_EXTRAS_TEMPLATE_2")
    protected String reg_extras_template_2;

    @SettingAccessorType(SettingAccessType.FIELD)
    @SettingField("REG_EXTRAS_TEMPLATE_3")
    protected String reg_extras_template_3;

    @SettingAccessorType(SettingAccessType.FIELD)
    @SettingField("REG_EXTRAS_TEMPLATE_4")
    protected String reg_extras_template_4;


    public String getRegularExtrasTemplate() {
        return JoinUtils.join(reg_extras_template_1, reg_extras_template_2, reg_extras_template_3, reg_extras_template_4);
    }

    public String getLumpExtrasTemplate() {
        return JoinUtils.join(lump_extras_template_1, lump_extras_template_2, lump_extras_template_3, lump_extras_template_4);
    }

    public String getInvoiceNoFmt() {
        return invoiceNoFmt;
    }

    public void setInvoiceNoFmt(String invoiceNoFmt) {
        this.invoiceNoFmt = invoiceNoFmt;
    }

    interface Setter {
        void set(String value);
    }

    private void setParts(String s,Setter[] setters)
    {
        Iterable<String> nameParts = JoinUtils.split(s, SettingsStore.MAX_SETTING_VALUE_LEN);

        Iterator<String> it = nameParts.iterator();
        for (Setter setter : setters) {
            if (it.hasNext())
                setter.set(it.next());
            else
                setter.set(null);
        }

    }

    public void setLumpExtrasTemplate(String template) {


        Setter[] setters =
                {
                        new Setter() {
                            @Override
                            public void set(String value) {
                                lump_extras_template_1 = value;
                            }
                        },
                        new Setter() {
                            @Override
                            public void set(String value) {
                                lump_extras_template_2 = value;
                            }
                        },
                        new Setter() {
                            @Override
                            public void set(String value) {
                                lump_extras_template_3 = value;
                            }
                        },
                        new Setter() {
                            @Override
                            public void set(String value) {
                                lump_extras_template_4 = value;
                            }
                        }
                };

        setParts(template,setters);


    }

    public void setRegularExtrasTemplate(String template) {

        Setter[] setters =
                {
                        new Setter() {
                            @Override
                            public void set(String value) {
                                reg_extras_template_1 = value;
                            }
                        },
                        new Setter() {
                            @Override
                            public void set(String value) {
                                reg_extras_template_2 = value;
                            }
                        },
                        new Setter() {
                            @Override
                            public void set(String value) {
                                reg_extras_template_3 = value;
                            }
                        },
                        new Setter() {
                            @Override
                            public void set(String value) {
                                reg_extras_template_4 = value;
                            }
                        }
                };

        setParts(template,setters);
    }


}
