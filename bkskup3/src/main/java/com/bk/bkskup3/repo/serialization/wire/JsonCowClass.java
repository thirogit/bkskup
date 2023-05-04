package com.bk.bkskup3.repo.serialization.wire;

import com.bk.bkskup3.model.CowClass;
import com.bk.bkskup3.model.CowClassObj;
import com.bk.bkskup3.model.CowSex;
import com.bk.bkskup3.model.HerdObj;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 2/29/12
 * Time: 2:53 PM
 */

public class JsonCowClass {
    private CowClassObj mClass;

    public JsonCowClass(CowClass herd) {
        this.mClass = new CowClassObj(herd.getId());
        this.mClass.copyFrom(herd);
    }

    public JsonCowClass() {
        this.mClass = new CowClassObj();
    }

    @JsonIgnore
    public CowClassObj getCowClass() {
        return mClass;
    }


    @JsonProperty("name")
    public String getClassName() {
        return mClass.getClassName();
    }

    @JsonProperty("code")
    public String getClassCode() {
        return mClass.getClassCode();
    }

    @JsonProperty("default_sex")
    public CowSex getPredefSex() {
        return mClass.getPredefSex();
    }

    @JsonProperty("price_per_kg")
    public Double getPricePerKg() {
        return mClass.getPricePerKg();
    }


    public void setClassName(String className) {
        mClass.setClassName(className);
    }

    public void setClassCode(String classCode) {
        mClass.setClassCode(classCode);
    }

    public void setPredefSex(CowSex predefSex) {
        mClass.setPredefSex(predefSex);
    }

    public void setPricePerKg(Double pricePerKg) {
        mClass.setPricePerKg(pricePerKg);
    }


}
