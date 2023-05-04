package com.bk.bkskup3.settings;

import com.bk.bksettings.annotation.SettingField;
import com.bk.bkskup3.model.Agent;

import java.io.Serializable;


/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 9/30/2014
 * Time: 11:01 PM
 */
public class AgentAsSettings implements Agent, Serializable {

    public static final String ACTION_AGENT_CHANGED = AgentAsSettings.class.getName() + ".CHANGED";
    public static final String EXTRA_LATEST_AGENT = "latest_agent";

    @SettingField("AGENT_NAME")
    protected String name;
    @SettingField("AGENT_CODE")
    protected String code;
    @SettingField("AGENT_PLATE")
    protected String plateNo;
    @SettingField("AGENT_WETNO")
    protected String wetNo;


    public void copyFrom(Agent agent)
    {
        this.code = agent.getCode();
        this.name = agent.getName();
        this.plateNo = agent.getPlateNo();
        this.wetNo = agent.getWetNo();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getPlateNo() {
        return plateNo;
    }


    public String getWetNo() {
        return wetNo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setPlateNo(String plateNo) {
        this.plateNo = plateNo;
    }

    public void setWetNo(String wetNo) {
        this.wetNo = wetNo;
    }


}
