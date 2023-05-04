package com.bk.bands.evaluate;


import com.bk.bands.lookup.StrLookup;
import com.bk.bands.lookup.StrSubstitutor;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/11/12
 * Time: 8:12 PM
 */
public abstract class DollarEvaluator implements Evaluator
{
   private StrSubstitutor dollarSubstitutor;

   public DollarEvaluator()
   {
      this.dollarSubstitutor = new StrSubstitutor(new StrLookup()
      {
         @Override
         public String lookup(String key)
         {
            return resolve(key);
         }
      });
   }

   @Override
   public String evaluate(String valueExpression)
   {
      return dollarSubstitutor.replace(valueExpression);
   }

   public abstract String resolve(String variableName);

}
