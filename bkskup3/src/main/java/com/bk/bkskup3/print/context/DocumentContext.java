package com.bk.bkskup3.print.context;


import com.bk.bkskup3.print.context.pricetotext.PriceToText;
import com.bk.bkskup3.utils.DoubleFormat;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 8/22/12
 * Time: 12:51 PM
 */
public interface DocumentContext
{
   Currency getCurrencySymbol();

   PayWays getPayWays();

   DoubleFormat getWeightFormat();

   DoubleFormat getPriceFormat();

   DoubleFormat getVATRateFormat();

   DoubleFormat getPricePerKgFormat();

   NumeralDeclination getDayDeclination();

   PriceToText getPriceToText();

   Units getUnits();

   Genders getGenders();

   Definitions getDefinitions();

   LocalizedTokens getTokens();

}
