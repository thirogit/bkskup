package com.bk.bkskup3.utils;

public class StringEscapeUtils {

    public StringEscapeUtils() {
    }

   private static final char ESCAPE_CHAR = '\\';

    public static String escape(String str, char charToEscape)
    {
        if (str == null) {
            return null;
        }


       int sz;
       sz = str.length();
       StringBuffer out = new StringBuffer(sz*2);
       for (int i = 0; i < sz; i++)
        {
            char ch = str.charAt(i);
           if(charToEscape == ch)
           {
              out.append(ESCAPE_CHAR);
              out.append(charToEscape);
           }
           else if(ch == ESCAPE_CHAR)
           {
              out.append(ESCAPE_CHAR);
              out.append(ESCAPE_CHAR);
           }
        }
       return out.toString();
    }


    public static String unescape(String str)
    {
        if (str == null) {
            return null;
        }
        int sz = str.length();
        StringBuffer out = new StringBuffer(sz);
        boolean hadEscape = false;
        for (int i = 0; i < sz; i++)
        {
            char ch = str.charAt(i);
           if (hadEscape)
           {
                // handle an escaped value
                hadEscape = false;
                out.append(ch);
                continue;
            } else if (ch == ESCAPE_CHAR) {
                hadEscape = true;
                continue;
            }
            out.append(ch);
        }

        if (hadEscape) {
            // then we're in the weird case of a \ at the end of the
            // string, let's output it anyway.
            out.append(ESCAPE_CHAR);
        }
       return out.toString();
    }


}

