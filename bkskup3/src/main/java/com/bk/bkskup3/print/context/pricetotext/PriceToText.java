package com.bk.bkskup3.print.context.pricetotext;

import com.bk.bkskup3.print.context.Currency;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 8/22/12
 * Time: 1:00 PM
 */
public interface PriceToText
{
   String translate(Double price,Currency currency);
}
