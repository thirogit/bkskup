package com.bk.bands.properties;

public enum HorizontalAlign {

    CENTER,
    LEFT,
    RIGHT;

    public String value() {
        return name();
    }

    public static HorizontalAlign fromValue(String v) {
        return valueOf(v);
    }

}
