package com.bk.bkskup3.model;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: root
 * Date: 12.06.11
 * Time: 19:50
 */
public class AgentObj implements Agent,Serializable {
    protected String agentname;
    protected String agentcode;
    protected String plate;
    protected String wetNo;


    public AgentObj() {
    }

    public AgentObj(Agent src)
    {
        agentcode = src.getCode();
        agentname = src.getName();
        plate = src.getPlateNo();
        wetNo = src.getWetNo();
    }

    public String getName() {
        return agentname;
    }


    public String getCode() {
        return agentcode;
    }


    public String getPlateNo() {
        return plate;
    }

    @Override
    public String getWetNo() {
        return wetNo;
    }


    public void setAgentName(String agentname) {
        this.agentname = agentname;
    }

    public void setAgentCode(String agentcode) {
        this.agentcode = agentcode;
    }

    public void setPlateNo(String plate) {
        this.plate = plate;
    }

    public void setWetNo(String wetNo) {
        this.wetNo = wetNo;
    }
}
