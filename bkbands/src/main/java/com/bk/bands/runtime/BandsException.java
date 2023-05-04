package com.bk.bands.runtime;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/10/12
 * Time: 7:13 PM
 */
public class BandsException extends RuntimeException
{
   public BandsException(String message)
   {
      super(message);
   }

   public BandsException(String message, Throwable cause)
   {
      super(message, cause);
   }

   public BandsException(Throwable cause)
   {
      super(cause);
   }
}
