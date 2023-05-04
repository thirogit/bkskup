package com.bk.bkskup3.input;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 8/29/12
 * Time: 3:24 PM
 */
public class DecimalInputFilter extends RegExInputFilter
{
   public DecimalInputFilter(int maxPrecision)
   {
      super(".*");
      if(maxPrecision <= 0) throw new IllegalArgumentException();
      super.initWithExpression("[0-9]+(\\.[0-9]{0," + String.valueOf(maxPrecision) + "})?");
   }

}
