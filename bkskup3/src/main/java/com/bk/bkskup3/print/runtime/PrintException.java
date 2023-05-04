package com.bk.bkskup3.print.runtime;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 4/4/12
 * Time: 1:09 PM
 */
public class PrintException extends RuntimeException
{
   public PrintException(String detailMessage)
   {
      super(detailMessage);
   }

   public PrintException(String detailMessage, Throwable throwable)
   {
      super(detailMessage, throwable);
   }

   public PrintException(Throwable throwable)
   {
      super(throwable);
   }
}
