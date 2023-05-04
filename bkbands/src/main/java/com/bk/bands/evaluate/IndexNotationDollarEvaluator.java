package com.bk.bands.evaluate;

import com.bk.bands.lookup.StrLookup;
import com.bk.bands.util.WhiteSpace;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 10/2/12
 * Time: 2:30 PM
 */
public class IndexNotationDollarEvaluator extends DollarEvaluator
{
   private StrLookup variableLookup;

   public IndexNotationDollarEvaluator(StrLookup variableLookup)
   {
      this.variableLookup = variableLookup;
   }

   @Override
   public String resolve(String variableName)
   {
      int openBracketIndex = variableName.indexOf('[');
      if(openBracketIndex > 0)
      {
         if(openBracketIndex == 0)
         {
            return "#Misplaced [";
         }
         else
         {
            String arrayVariableName = variableName.substring(0,openBracketIndex);
            if(WhiteSpace.isWhiteSpace(arrayVariableName))
            {
               return "#Empty variable name";
            }

            if(openBracketIndex+1 < variableName.length())
            {
               int closeBracketIndex = variableName.indexOf(']',openBracketIndex+1);

               if(closeBracketIndex > 0)
               {
                  String indexValue = variableName.substring(openBracketIndex+1,closeBracketIndex);
                  int index = Integer.parseInt(indexValue);
                  String value = variableLookup.lookup(arrayVariableName);
                  if(value != null && index < value.length())
                     return String.valueOf(value.charAt(index));
                  else
                     return "";
               }
            }
            return "#Missing ]";
         }

      }
      return variableLookup.lookup(variableName);
   }
}
