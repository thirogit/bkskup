package com.bk.bkskup3.runtime;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 2/16/12
 * Time: 7:01 PM
 */
public class FatalException extends RuntimeException
{
   public FatalException(String detailMessage)
   {
      super(detailMessage);
   }

   public FatalException(String detailMessage, Throwable throwable)
   {
      super(detailMessage, throwable);
   }

   public FatalException(Throwable throwable)
   {
      super(throwable);
   }
}
