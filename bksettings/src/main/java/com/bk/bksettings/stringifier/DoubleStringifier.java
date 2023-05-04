package com.bk.bksettings.stringifier;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 4/12/12
 * Time: 1:44 PM
 */
public class DoubleStringifier implements Stringifier
{
   @Override
   public Object unstringify(String value)
   {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   @Override
   public String stringify(Object obj)
   {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   @Override
   public boolean isApplicable(Object obj)
   {
      return obj instanceof Double;
   }
}
