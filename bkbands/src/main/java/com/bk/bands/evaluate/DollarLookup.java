package com.bk.bands.evaluate;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/27/12
 * Time: 2:35 PM
 */
public class DollarLookup extends DollarEvaluator
{
   Map<String,String> map = new HashMap<String,String>();

   public DollarLookup(Map<String, String> map)
   {
      this.map.putAll(map);
   }

   public DollarLookup()
   {
   }

   public void put(String variableName,String variableValue)
   {
      map.put(variableName,variableValue);
   }


   @Override
   public String resolve(String variableName)
   {
      return map.get(variableName);
   }
}
