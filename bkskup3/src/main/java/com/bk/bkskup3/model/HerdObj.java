package com.bk.bkskup3.model;


public class HerdObj extends IdableObj implements Herd {
    protected int herdNumber;
    protected String zip;
    protected String city;
    protected String street;
    protected String poBox;

    public HerdObj(int id) {
        super(id);
    }

    public HerdObj() {
        super(0);
    }
    
    public void copyFrom(Herd herd)
    {
        setZip(herd.getZip());
        setCity(herd.getCity());
        setStreet(herd.getStreet());
        setPoBox(herd.getPoBox());
        setHerdNo(herd.getHerdNo());
    }

    public String getZip() {
        return zip;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public String getPoBox() {
        return poBox;
    }

    public int getHerdNo() {
        return herdNumber;
    }

    public void setHerdNo(int herdNumber) {
        this.herdNumber = herdNumber;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setPoBox(String poBox) {
        this.poBox = poBox;
    }
}
