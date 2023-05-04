package com.bk.bkskup3.print.lookup;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.bk.bands.lookup.StrLookup;
import com.google.common.reflect.TypeToken;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 9/1/12
 * Time: 11:07 PM
 */
public class LookupAdapter extends StrLookup
{
   private Object objForLookup;
   private Map<String,Field> mFieldLookup;

   public LookupAdapter(Object objForLookup)
   {
      this.objForLookup = objForLookup;

      Class lookupClass = objForLookup.getClass();
      Field[] classFields = lookupClass.getDeclaredFields();

      Map<String,Field> fieldLookup = new HashMap<String, Field>(classFields.length);

      for (Field field : classFields)
      {
        LookupField lookupFieldAnnotation = field.getAnnotation(LookupField.class);
        if (lookupFieldAnnotation != null)
        {
           if(!String.class.isAssignableFrom(TypeToken.of(field.getGenericType()).getRawType()))
              throw new LookupException("Field " + field.getName() + " of class " + lookupClass.getCanonicalName() +
                                        " is not derived from String");


          field.setAccessible(true);
          fieldLookup.put(lookupFieldAnnotation.value(),field);
        }
      }

      mFieldLookup = fieldLookup;

   }


   @Override
   public String lookup(String key)
   {
      Field lookupField = mFieldLookup.get(key);
      if(lookupField != null)
      {
         try
         {
            return (String)lookupField.get(objForLookup);
         }
         catch (IllegalAccessException e)
         {
            throw new LookupException(e);
         }

      }
      return null;
   }
}
