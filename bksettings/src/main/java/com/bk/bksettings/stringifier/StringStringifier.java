package com.bk.bksettings.stringifier;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 4/12/12
 * Time: 1:45 PM
 */
public class StringStringifier implements Stringifier
{
   @Override
   public Object unstringify(String value)
   {
      return value;
   }

   @Override
   public String stringify(Object obj)
   {
      return obj.toString();
   }

   @Override
   public boolean isApplicable(Object obj)
   {
      return obj instanceof String;
   }
}
