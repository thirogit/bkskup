package com.bk.bkskup3.print.filters;

import com.bk.bkskup3.model.InvoiceType;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 8/16/12
 * Time: 4:29 PM
 */
public class RegularInvoiceFilter extends InvoiceTypeFilter
{
   public RegularInvoiceFilter()
   {
      super(InvoiceType.REGULAR);
   }
}
