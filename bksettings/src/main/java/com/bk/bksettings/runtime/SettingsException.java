package com.bk.bksettings.runtime;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 2/13/12
 * Time: 10:52 PM
 */
public class SettingsException extends RuntimeException
{
   public SettingsException(String detailMessage)
   {
      super(detailMessage);
   }

   public SettingsException(String detailMessage, Throwable throwable)
   {
      super(detailMessage, throwable);
   }

   public SettingsException(Throwable throwable)
   {
      super(throwable);
   }
}
