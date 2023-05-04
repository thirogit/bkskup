package com.bk.bkskup3.print.context.pricetotext;

import java.util.HashMap;
import java.util.Map;

import com.bk.bkskup3.print.context.Currency;
import com.bk.bkskup3.print.context.numbertotext.NumberToText;
import com.bk.bkskup3.print.context.numbertotext.NumberToTextFactory;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 9/20/12
 * Time: 8:00 PM
 */
public class PriceToTextFactory
{
   static Map<String,PriceToText> priceToText = new HashMap<String, PriceToText>();

   static
   {
      String[] availableLanguages = NumberToTextFactory.getAvailableLanguages();
      for(String language : availableLanguages)
         priceToText.put(language,new PriceToTextImpl(NumberToTextFactory.getNumberToText(language)));
   }

   public static PriceToText getPriceToText(String language)
   {
      return priceToText.get(language);
   }

   static class PriceToTextImpl implements PriceToText
   {
      private NumberToText numberToText;

      PriceToTextImpl(NumberToText numberToText)
      {
         this.numberToText = numberToText;
      }

      @Override
      public String translate(Double price,Currency currency)
      {
         if (price == null)
         {
            return "";
         }

          long whole = price.longValue();
          double fraction = (price-whole);
          long cents = (int)Math.round(fraction*100.0);

         return numberToText.translate(whole) + ' ' + currency.getCurrencySymbol() + ' ' + numberToText.translate(cents) + ' ' + currency.getFractionSymbol();
      }

   }
}
