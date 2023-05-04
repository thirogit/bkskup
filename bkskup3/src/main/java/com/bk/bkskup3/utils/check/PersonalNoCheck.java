package com.bk.bkskup3.utils.check;

import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 9/22/12
 * Time: 7:10 PM
 */
public class PersonalNoCheck
{
   static private Map<String,AsciiNumberCheck> checks = new HashMap<String,AsciiNumberCheck>();

   static
   {
      checks.put("PL",new PersonalNumberCheckPL());
   }

   public static boolean isValid(String countryCd,String personalNo)
   {
      AsciiNumberCheck check = checks.get(countryCd);
      return check == null || check.isValid(personalNo);
   }

   public static class PersonalNumberCheckPL implements AsciiNumberCheck
   {
      private static final int weights[] = {1, 3, 7, 9, 1, 3, 7, 9, 1, 3};
      @Override
      public boolean isValid(String personalNo)
      {
         if(personalNo.length() == 11 && TextUtils.isDigitsOnly(personalNo))
         {
            int chkSum = 0;
            for(int i = 0;i < 10;i++)
            {
               chkSum += (personalNo.charAt(i)-'0')*weights[i];
            }

            return (10 - (chkSum%10))%10 == (personalNo.charAt(10)-'0');
         }
         return false;
      }
   }
}
