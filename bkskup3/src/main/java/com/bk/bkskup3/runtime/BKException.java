package com.bk.bkskup3.runtime;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 2/16/12
 * Time: 7:01 PM
 */
public class BKException extends Exception
{
   public BKException(String detailMessage)
   {
      super(detailMessage);
   }

   public BKException(String detailMessage, Throwable throwable)
   {
      super(detailMessage, throwable);
   }

   public BKException(Throwable throwable)
   {
      super(throwable);
   }
}
