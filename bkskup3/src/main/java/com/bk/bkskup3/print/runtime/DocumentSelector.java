package com.bk.bkskup3.print.runtime;

import com.bk.bkskup3.model.Invoice;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 7/28/12
 * Time: 12:15 AM
 */
public interface DocumentSelector
{
   boolean isValidForInvoice(Invoice invoice);
}
