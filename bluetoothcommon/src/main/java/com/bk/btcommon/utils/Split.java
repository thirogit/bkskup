package com.bk.btcommon.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/9/12
 * Time: 9:18 PM
 */
public class Split
{
   public static String[] split(String str, char separatorChar, boolean preserveAllTokens)
   {

      if (str == null)
      {
         return null;
      }
      int len = str.length();
      if (len == 0)
      {
         return new String[0];
      }
      List list = new ArrayList();
      int i = 0, start = 0;
      boolean match = false;
      boolean lastMatch = false;
      while (i < len)
      {
         if (str.charAt(i) == separatorChar)
         {
            if (match || preserveAllTokens)
            {
               list.add(str.substring(start, i));
               match = false;
               lastMatch = true;
            }
            start = ++i;
            continue;
         }
         else
         {
            lastMatch = false;
         }
         match = true;
         i++;
      }
      if (match || (preserveAllTokens && lastMatch))
      {
         list.add(str.substring(start, i));
      }
      return (String[]) list.toArray(new String[list.size()]);
   }
}
