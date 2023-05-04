package com.bk.bkskup3.print.context.numbertotext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 9/20/12
 * Time: 9:15 PM
 */
public class NumberToTextFactory
{
   interface LocalizedNumberToText extends NumberToText
   {
      String getLanguage();
   }

   static LocalizedNumberToText[] numberToTexts =
   {
      new NumberToTextPL()
   };

   static Map<String,NumberToText> availableNumberToTexts = new HashMap<String, NumberToText>();

   static
   {
      for(LocalizedNumberToText numberToText : numberToTexts)
      {
         availableNumberToTexts.put(numberToText.getLanguage(),numberToText);
      }
   }

   public static NumberToText getNumberToText(String language)
   {
      return availableNumberToTexts.get(language);
   }

   public static String[] getAvailableLanguages()
   {
      Collection<String> languages = new ArrayList<String>(numberToTexts.length);
      for(LocalizedNumberToText numberToText : numberToTexts)
      {
         languages.add(numberToText.getLanguage());
      }
      return languages.toArray(new String[numberToTexts.length]);
   }

   private static class NumberToTextPL implements LocalizedNumberToText
   {
      String[] numerals1 =
      {
          "zero",
          "jeden",
          "dwa",
          "trzy",
          "cztery",
          "pięć",
          "sześć",
          "siedem",
          "osiem",
          "dziewieć",
          "dzieśięć",
          "jedenaście",
          "dwanaście",
          "trzynaście",
          "czternaście",
          "piętnaście",
          "szesnaście",
          "siedemnaście",
          "osiemnaście",
          "dziewiętnaście"
      };

      String[] numerals10 =
      {
          "",
          "",
          "dwadzieścia",
          "trzydzieści",
          "czterdzieści",
          "pięćdziesiąt",
          "sześćdziesiąt",
          "siedemdziesiąt",
          "osiemdziesiąt",
          "dziewięćdziesiąt"
      };

      String[] numerals100 =
      {
        "",
        "sto",
        "dwieście",
        "trzysta",
        "czterysta",
        "pięćset",
        "sześćset",
        "siedemset",
        "osiemset",
        "dziewięćset"
      };

      String[] numeral1000Declination =
      {
        "tysiąc",
        "tysiące",
        "tysięcy",
      };

      String[] numeral10_6Declination =
      {
          "milion",
          "miliony",
          "milionów"
      };

      String[] numeral10_9Declination =
      {
          "miliard",
          "miliardy",
          "miliardów"
      };


      private int declinationIndex(long number)
      {
         if(number == 1) return 0;
         else
         {
            long tensReminder = number%10;
            if(((number%100)/10) !=  1 && tensReminder >= 2 && tensReminder <= 4) return 1;
         }
         return 2;
      }

      private String translate999(long number)
      {
         Collection<String> resultStack = new ArrayList<String>(3);

         if (number >= 1000) return "";
         if(number == 0) return numerals1[0];

         long hundreds, hundredsReminder, tens, tensReminder;

         hundreds = number / 100;
         hundredsReminder = number % 100;

         if (hundreds > 0)
            resultStack.add(numerals100[(int) hundreds]);

         if (hundredsReminder > 0)
         {
            tens = hundredsReminder / 10;
            tensReminder = hundredsReminder % 10;

            if (tens > 1)
            {
               resultStack.add(numerals10[(int) tens]);
               if (tensReminder > 0) resultStack.add(numerals1[(int)tensReminder]);
            }
            else
            {
               resultStack.add(numerals1[(int)hundredsReminder]);
            }
         }

         return TextUtils.join(" ",resultStack);
      }

      @Override
      public String translate(long number)
      {
           final int BILLION = 1000000000;
           final int MILLION = 1000000;
            final int THOUSAND = 1000;
           Collection<String> resultStack = new ArrayList<String>(7);
           long billions,millions,thousands,thousandsReminder,billionsReminder,millionsReminder;

           billions = number / BILLION;
           billionsReminder = (number % BILLION);
           millions = billionsReminder/MILLION;
           millionsReminder = billionsReminder%MILLION;
           thousands = millionsReminder/THOUSAND;
           thousandsReminder = millionsReminder%THOUSAND;

            if(billions > 0)
            {
               resultStack.add(translate999(billions));
               resultStack.add(numeral10_9Declination[declinationIndex(billions)]);
            }
            if(millions > 0)
            {
               resultStack.add(translate999(millions));
               resultStack.add(numeral10_6Declination[declinationIndex(millions)]);
            }

            if(thousands > 0)
            {
               resultStack.add(translate999(thousands));
               resultStack.add(numeral1000Declination[declinationIndex(thousands)]);
            }

            resultStack.add(translate999(thousandsReminder));
            return TextUtils.join(" ",resultStack);
         }

      @Override
      public String getLanguage()
      {
         return "pl";
      }
   }
   }












