package com.bk.bands.properties;

public enum VerticalAlign {

    CENTER,
    BOTTOM,
    TOP;

    public String value() {
        return name();
    }

    public static VerticalAlign fromValue(String v) {
        return valueOf(v);
    }

}
