package com.bk.bkskup3.utils.check;

import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;
import com.bk.bkskup3.model.EAN;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 9/20/12
 * Time: 3:11 PM
 */
public class HentNoCheck
{
   static private Map<String,EANCheck> checks = new HashMap<String,EANCheck>();

      static
      {
         checks.put("PL",new HentNoCheckPL());
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

      public static class HentNoCheckPL implements EANCheck
      {
         @Override
         public boolean isValid(EAN ean)
         {
            String numberPart = ean.getNumber();
            if(numberPart.length() != 12)
               return false;

            if(!TextUtils.isDigitsOnly(numberPart))
               return false;

            int evenSum = 0,oddSum = 0,evenCount = 0;
            for(int i = 0;i < 8;i++)
            {
                char c = numberPart.charAt(i);
                int digit = c - '0';

                if(digit%2 > 0)
                {
                  oddSum += digit;
                }
               else
                {
                  evenSum += digit;
                  evenCount++;
                }
            }

            int chkSum = 23 * evenSum + 17*oddSum + evenCount;
            chkSum = chkSum%7;
            return chkSum == (numberPart.charAt(8) - '0');

         }
      }

}
