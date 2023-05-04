package com.bk.bkskup3.repo.serialization.wire;

import com.bk.bkskup3.model.EAN;
import com.bk.bkskup3.model.HentObj;
import com.bk.bkskup3.model.HentType;
import com.bk.bkskup3.model.Herd;
import com.bk.bkskup3.model.HerdObj;
import com.bk.bkskup3.model.IBAN;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 2/27/12
 * Time: 10:28 PM
 */
public class JsonHerd {
    private HerdObj mHerd;

    public JsonHerd(Herd herd) {
        this.mHerd = new HerdObj(herd.getId());
        this.mHerd.copyFrom(herd);
    }

    public JsonHerd() {
        this.mHerd = new HerdObj();
    }

    @JsonIgnore
    public HerdObj getHerd() {
        return mHerd;
    }

    @JsonProperty("index")
    public int getHerdNo() {
        return mHerd.getHerdNo();
    }

    public void setHerdNo(int herdNo) {
        mHerd.setHerdNo(herdNo);
    }

    @JsonProperty("zip_code")
    public String getZip() {
        return mHerd.getZip();
    }

    @JsonProperty("city")
    public String getCity() {
        return mHerd.getCity();
    }

    @JsonProperty("street")
    public String getStreet() {
        return mHerd.getStreet();
    }

    @JsonProperty("pobox")
    public String getPoBox() {
        return mHerd.getPoBox();
    }


    public void setZip(String zip) {
        mHerd.setZip(zip);
    }

    public void setCity(String city) {
        mHerd.setCity(city);
    }

    public void setStreet(String street) {
        mHerd.setStreet(street);
    }

    public void setPoBox(String poBox) {
        mHerd.setPoBox(poBox);
    }

}
