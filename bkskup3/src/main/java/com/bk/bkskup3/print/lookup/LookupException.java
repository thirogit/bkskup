package com.bk.bkskup3.print.lookup;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 9/2/12
 * Time: 2:19 PM
 */
public class LookupException extends RuntimeException
{

   public LookupException(String detailMessage)
   {
      super(detailMessage);
   }

   public LookupException(Throwable throwable)
   {
      super(throwable);
   }
}
