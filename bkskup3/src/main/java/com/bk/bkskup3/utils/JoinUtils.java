package com.bk.bkskup3.utils;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 1/4/14
 * Time: 6:38 PM
 */
public class JoinUtils {

    public static String join(String... s)
    {
        String result = "";
        for(String token : s)
        {
            result += Strings.nullToEmpty(token);
        }
        return Strings.emptyToNull(result);
    }

    public static Iterable<String> split(String s, int maxChars)
    {
        return Splitter.fixedLength(maxChars).split(s);
    }

}
