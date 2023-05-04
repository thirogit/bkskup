package com.bk.bksettings.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.bk.bksettings.annotation.adapter.SettingAdapter;
import com.bk.bksettings.model.Setting;
import com.bk.bksettings.runtime.SettingsException;
import com.google.common.reflect.TypeToken;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 4/5/12
 * Time: 12:21 PM
 */
public class AdapterBean
{
   private Class<? extends SettingAdapter> adapterClass;
   private SettingAdapter<?,?> adapterInstance;
   private BaseClassDigger digger = new BaseClassDigger();

   public Type bondedType;
   public Type valueType;

   public AdapterBean(Class<? extends SettingAdapter> adapterClass)
   {
      this.adapterClass = adapterClass;
   }

   public boolean isParameterizedType(Type type) {
           return type instanceof ParameterizedType;
       }

   public Type getTypeArgument(Type type, int i)
   {
     if (type instanceof ParameterizedType)
     {
         ParameterizedType p = (ParameterizedType) type;
         return p.getActualTypeArguments()[i];
     }
     else
         throw new IllegalArgumentException();

   }

   private SettingAdapter getAdapterInstance() throws SettingsException
   {
      try
      {
         if(adapterInstance == null)
         {
            adapterInstance = adapterClass.newInstance();
         }
      }
      catch (InstantiationException e)
      {
         throw new SettingsException(e);
      }
      catch (IllegalAccessException e)
      {
         throw new SettingsException(e);
      }

      return adapterInstance;
   }
   
   public Type getBoundType()
   {
      if(bondedType == null)
      {
         Type baseClass = digger.visit(adapterClass,SettingAdapter.class);
         if(isParameterizedType(baseClass))
            bondedType = getTypeArgument(baseClass,1);
         else
            bondedType = Object.class;
      }
      return bondedType;
   }

   public Type getValueType()
   {
      if(valueType == null)
      {
         Type baseClass = digger.visit(adapterClass,SettingAdapter.class);
         if(isParameterizedType(baseClass))
            valueType = getTypeArgument(baseClass,0);
         else
            valueType = Object.class;
      }
      return valueType;
   }
   
   public Object adaptToSimpleType(Object complexObj) throws SettingsException
   {
      Class<?> complextObjClass = complexObj.getClass();

      if(!TypeToken.of(getBoundType()).isSubtypeOf(complextObjClass))
      {
         throw new SettingsException("Adapter " + adapterClass.getCanonicalName() + " is unable to marshal " + complextObjClass.getCanonicalName() + " class");
      }
      return  getAdapterInstance().marshal(complexObj);
   }
   
   public Object adaptFromSimpleType(Object simpleObj) throws SettingsException
   {
      Class<?> simpleObjClass = simpleObj.getClass();
      if(getValueType().getClass().isAssignableFrom(simpleObjClass))
      {
         throw new SettingsException("Adapter " + adapterClass.getCanonicalName() + " is unable to unmarshal " + simpleObjClass.getCanonicalName() + " class");
      }

      return getAdapterInstance().unmarshal(simpleObj);
   }

   public void checkIfAdapterApplicableForField(Field field) throws SettingsException
   {
      Type boundType = getBoundType();
      //field.getType()


   }

   public void checkIfAdapterApplicableForSetting(Setting setting) throws SettingsException
   {
//      try
//      {
//
//         boolean  assignable = SettingAdapter.class.isAssignableFrom(adapterClass);
//
//
////         adapterClass.getMethod("unmarshal", )
//
//
//
//      }
//      catch (InstantiationException e)
//      {
//         throw new SettingsException(e);
//      }
//      catch (IllegalAccessException e)
//      {
//         throw new SettingsException(e);
//      }
   }
}
