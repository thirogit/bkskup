package com.bk.bkskup3.utils.check;

import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;
import com.bk.bkskup3.model.EAN;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 9/20/12
 * Time: 1:39 PM
 */
public class CowNoCheck
{
   static private Map<String,EANCheck> checks = new HashMap<String,EANCheck>();

   static
   {
      checks.put("PL",new CowNoCheckPL());
   }

   public static boolean isValid(EAN ean)
   {
      if(TextUtils.isGraphic(ean.getCountryCode()) && TextUtils.isDigitsOnly(ean.getNumber()))
      {
         EANCheck check = checks.get(ean.getCountryCode());
         return check == null || check.isValid(ean);
      }
      return false;
   }

   public static class CowNoCheckPL implements EANCheck
   {
      @Override
      public boolean isValid(EAN ean)
      {
         String numberPart = ean.getNumber();
         if(numberPart.length() != 12)
            return false;

         if(!TextUtils.isDigitsOnly(numberPart))
            return false;

         int chkSum = 0;

         for(int i = 0;i < 11;i++)
         {
            char c = numberPart.charAt(i);
            chkSum += (c - '0')*((i%2 > 0) ? 1 : 3);
         }

         chkSum = 10 - chkSum%10;


         return chkSum == numberPart.charAt(11)-'0';

      }
   }
}
