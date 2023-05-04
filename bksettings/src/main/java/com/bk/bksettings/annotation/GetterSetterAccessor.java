package com.bk.bksettings.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.bk.bksettings.runtime.AccessorException;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 4/17/12
 * Time: 2:23 PM
 */
public class GetterSetterAccessor implements Accessor {
   private Field field;

   public GetterSetterAccessor(Field field)
   {
      this.field = field;
   }

   private String getSetterName()
   {
      return "set" + NameUtil.capitalize(field.getName());
   }
   
   private String getGetterName()
   {
      return "get" + NameUtil.capitalize(field.getName());
   }
   
   
   @Override
   public Object get(Object thisObj) throws AccessorException
   {
      String getterName = getGetterName();

      try
      {
         Method getter = thisObj.getClass().getMethod(getterName);

      //      Void.class

       //  if (field.getClass().isAssignableFrom(getter.getReturnType().))
       //  {
       //     records = new LinkedList<Record>();
       //     records.add((Record)getter.invoke(modelInstance, null));
       //  }

         return getter.invoke(thisObj, (Object[])null);
      }
      catch (Exception e)
      {
         throw new AccessorException(e);
      }
   }
   
   @Override
   public void set(Object thisObj, Object fieldValue) throws AccessorException
   {
      String setterName = getSetterName();
      try
      {
         if(fieldValue != null)
         {
            Method setter = thisObj.getClass().getMethod(setterName, fieldValue.getClass());

            setter.invoke(thisObj, fieldValue);
         }

      }
      catch (Exception e)
      {
         throw new AccessorException(e);
      }
   }
}
