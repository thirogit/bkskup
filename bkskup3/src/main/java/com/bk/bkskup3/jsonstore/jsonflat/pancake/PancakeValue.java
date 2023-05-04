package com.bk.bkskup3.jsonstore.jsonflat.pancake;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 1/23/2015
 * Time: 11:55 AM
 */
public class PancakeValue {

    private PancakeValueType type;
    private String value;

    public PancakeValue(PancakeValueType type, String value) {
        this.type = type;
        this.value = value;
    }

    public PancakeValueType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return  type + ":"  + value;
    }
}

