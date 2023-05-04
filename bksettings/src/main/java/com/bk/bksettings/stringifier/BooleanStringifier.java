package com.bk.bksettings.stringifier;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 4/12/12
 * Time: 1:44 PM
 */
public class BooleanStringifier implements Stringifier
{
   private static final String TRUE = "Y";
   private static final String FALSE = "N";
   
   @Override
   public Object unstringify(String value)
   {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   @Override
   public String stringify(Object obj)
   {
      return (Boolean)obj ? TRUE : FALSE;
   }

   @Override
   public boolean isApplicable(Object obj)
   {
      return obj instanceof Boolean;
   }
}
