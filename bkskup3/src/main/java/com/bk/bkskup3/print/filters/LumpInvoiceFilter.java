package com.bk.bkskup3.print.filters;

import com.bk.bkskup3.model.InvoiceType;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 8/16/12
 * Time: 4:26 PM
 */
public class LumpInvoiceFilter extends InvoiceTypeFilter
{
   public LumpInvoiceFilter()
   {
      super(InvoiceType.LUMP);
   }
}
