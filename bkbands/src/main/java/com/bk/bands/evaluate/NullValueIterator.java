package com.bk.bands.evaluate;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/27/12
 * Time: 2:48 PM
 */
public class NullValueIterator implements ValueIterator
{

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
   public boolean next()
   {
      return false;
   }

   @Override
   public String evaluate(String valueExpression)
   {
      return null;
   }
}
