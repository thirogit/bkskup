package com.bk.bkskup3.runtime;


import com.bk.bkskup3.model.Cow;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 9/1/12
 * Time: 11:39 PM
 */
public interface CowCalculator
{
   double getNetPrice();
   double getGrossPrice();
   double getTaxValue();
   double getNetPricePerKg();
   double getGrossPricePerKg();
   Cow getCow();


}
