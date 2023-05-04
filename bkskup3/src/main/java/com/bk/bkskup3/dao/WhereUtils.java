package com.bk.bkskup3.dao;

public final class WhereUtils {
    private WhereUtils() {}

    public static String eq(String columnName, String value)
    {
        return columnName + "=" + value;
    }

    public static String quote(String value)
    {
        return '\'' + value + '\'';
    }

}
