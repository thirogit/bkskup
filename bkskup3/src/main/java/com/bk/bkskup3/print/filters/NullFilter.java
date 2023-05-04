package com.bk.bkskup3.print.filters;

import javax.annotation.Nullable;

import com.bk.bkskup3.model.Invoice;
import com.google.common.base.Predicate;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 8/22/12
 * Time: 11:14 AM
 */
public class NullFilter implements Predicate<Invoice>
{
   @Override
   public boolean apply(@Nullable Invoice invoice)
   {
      return true;
   }
}
