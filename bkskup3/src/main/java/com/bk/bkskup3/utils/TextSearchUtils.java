package com.bk.bkskup3.utils;

import com.google.common.base.Strings;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 1/20/12
 * Time: 12:45 PM
 */
public class TextSearchUtils
{

    static boolean regionMatches(CharSequence cs, boolean ignoreCase, int thisStart,
            CharSequence substring, int start, int length)    {
        if (cs instanceof String && substring instanceof String) {
            return ((String) cs).regionMatches(ignoreCase, thisStart, (String) substring, start, length);
        } else {
            return cs.toString().regionMatches(ignoreCase, thisStart, substring.toString(), start, length);
        }
    }

     public static boolean containsIgnoreCase(CharSequence str, CharSequence searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        int len = searchStr.length();
        int max = str.length() - len;
        for (int i = 0; i <= max; i++) {
            if (regionMatches(str, true, i, searchStr, 0, len)) {
                return true;
            }
        }
        return false;
    }


   public static int indexOfIgnoreCase(CharSequence str,char ch, int start)
   {
      int nlen = str.length();
      if(start >= nlen)
         return -1;

      for(int i = start; i < nlen;i++)
      {
         if(Character.toLowerCase(ch) == Character.toLowerCase(str.charAt(i)))
            return i;
      }
      return -1;
   }

    public static String substringBefore(final String str, final String separator) {
        if (Strings.isNullOrEmpty(str)) {
            return str;
        }
        if (separator.isEmpty()) {
            return "";
        }
        final int pos = str.indexOf(separator);
        if (pos == -1) {
            return str;
        }
        return str.substring(0, pos);
    }

   public static int indexOfIgnoreCase(CharSequence hayStack, CharSequence needle) {
       int nlen = needle.length();

      int start = 0;
      int end = hayStack.length();

       if (nlen == 0)
           return start;

       char c = needle.charAt(0);

       for (;;) {
           start = indexOfIgnoreCase(hayStack, c, start);


           if (start > end - nlen) {
               break;
           }

           if (start < 0) {
               return -1;
           }

           if (regionMatches(hayStack, true, start, needle, 0, nlen)) {
               return start;
           }

           start++;
       }
       return -1;
   }

}
