package com.bk.bkskup3.model;


import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: root
 * Date: 14.06.11
 * Time: 17:14
 */


public class CowClassObj extends IdableObj implements CowClass,Serializable {
    protected String className;
    protected String classCode;
    protected CowSex predefSex;
    protected Double pricePerKg;

    public CowClassObj(int id) {
        super(id);
    }

    public CowClassObj() {
        super(0);
    }

    public String getClassName() {
        return className;
    }

    public String getClassCode() {
        return classCode;
    }

    public CowSex getPredefSex() {
        return predefSex;
    }

    public Double getPricePerKg() {
        return pricePerKg;
    }


    public void setClassName(String className) {
        this.className = className;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public void setPredefSex(CowSex predefSex) {
        if (predefSex != null)
            this.predefSex = predefSex;
        else
            this.predefSex = CowSex.NONE;
    }

    public void setPricePerKg(Double pricePerKg) {
        this.pricePerKg = pricePerKg;
    }


    public void copyFrom(CowClass classObj) {
        setClassName(classObj.getClassName());
        setClassCode(classObj.getClassCode());
        setPredefSex(classObj.getPredefSex());
        setPricePerKg(classObj.getPricePerKg());
    }
}
