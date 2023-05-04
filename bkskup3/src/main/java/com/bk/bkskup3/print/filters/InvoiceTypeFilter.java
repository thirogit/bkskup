package com.bk.bkskup3.print.filters;

import javax.annotation.Nullable;

import com.bk.bkskup3.model.Invoice;
import com.bk.bkskup3.model.InvoiceType;
import com.google.common.base.Predicate;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 8/16/12
 * Time: 4:28 PM
 */
public class InvoiceTypeFilter implements Predicate<Invoice>
{
   private InvoiceType invoiceType;

   public InvoiceTypeFilter(InvoiceType invoiceType)
   {
      this.invoiceType = invoiceType;
   }

   @Override
   public boolean apply(@Nullable Invoice invoice)
   {
      return invoice != null && invoice.getInvoiceType() == invoiceType;
   }
}
