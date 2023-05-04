package com.bk.bksettings.runtime;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 7/23/12
 * Time: 6:36 PM
 */
public class AccessorException extends Exception
{
   public AccessorException(String detailMessage)
   {
      super(detailMessage);
   }

   public AccessorException(String detailMessage, Throwable throwable)
   {
      super(detailMessage, throwable);
   }

   public AccessorException(Throwable throwable)
   {
      super(throwable);
   }
}
