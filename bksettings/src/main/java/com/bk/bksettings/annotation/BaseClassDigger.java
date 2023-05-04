package com.bk.bksettings.annotation;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 7/30/12
 * Time: 9:52 PM
 */
public class BaseClassDigger extends TypeVisitor<Type, Class>
{
   public Type onClass(Class c, Class sup)
   {
      // t is a raw type
      if (sup == c)
      {
         return sup;
      }

      Type r;

      Type sc = c.getGenericSuperclass();
      if (sc != null)
      {
         r = visit(sc, sup);
         if (r != null) return r;
      }

      for (Type i : c.getGenericInterfaces())
      {
         r = visit(i, sup);
         if (r != null) return r;
      }

      return null;
   }

   public Type onParameterizdType(ParameterizedType p, Class sup)
   {
      Class raw = (Class) p.getRawType();
      if (raw == sup)
      {
         // p is of the form sup<...>
         return p;
      }
      else
      {
         // recursively visit super class/interfaces
//         Type r = raw.getGenericSuperclass();
//         if (r != null)
//         {
//            r = visit(bind(r, raw, p), sup);
//         }
//         if (r != null)
//         {
//            return r;
//         }
//         for (Type i : raw.getGenericInterfaces())
//         {
//            r = visit(bind(i, raw, p), sup);
//            if (r != null) return r;
//         }
         return null;
      }
   }

}
