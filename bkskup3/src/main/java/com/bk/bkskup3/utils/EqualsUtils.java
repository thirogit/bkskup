package com.bk.bkskup3.utils;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/6/2014
 * Time: 11:05 PM
 */

public class EqualsUtils {

    private EqualsUtils() {

    }

    public static boolean equals(Object left, Object right) {
        if (left == right) {
            return true;
        } else if (left == null) {
            return right == null;
        } else {
            return left.equals(right);
        }
    }
}
