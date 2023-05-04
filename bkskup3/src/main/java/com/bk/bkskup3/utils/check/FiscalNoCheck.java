package com.bk.bkskup3.utils.check;

import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 9/22/12
 * Time: 7:00 PM
 */
public class FiscalNoCheck
{
   static private Map<String,AsciiNumberCheck> checks = new HashMap<String,AsciiNumberCheck>();

   static
   {
      checks.put("PL",new FiscalNoCheckPL());
   }

   public static boolean isValid(String countryCd,String fiscalNo)
   {
      AsciiNumberCheck check = checks.get(countryCd);
      return check == null || check.isValid(fiscalNo);
   }

   public static class FiscalNoCheckPL implements AsciiNumberCheck
   {
      private static final int[] weights = {6, 	5, 	7, 	2, 	3, 	4, 	5, 	6, 	7};
      @Override
      public boolean isValid(String fiscalNo)
      {
         if(fiscalNo.length() == 10 && TextUtils.isDigitsOnly(fiscalNo))
         {
            int chkSum = 0;
            for(int i = 0;i < 9;i++)
            {
               chkSum += (fiscalNo.charAt(i) - '0')*weights[i];
            }
            return chkSum%11 == fiscalNo.charAt(9) - '0';
         }
         return false;
      }
   }
}
