package com.bk.bkskup3.settings.adapter;


import com.bk.bksettings.annotation.adapter.SettingAdapter;
import com.bk.bksettings.runtime.SettingsException;
import com.bk.bkskup3.model.CowSex;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/26/12
 * Time: 3:57 PM
 */
public class CowSexAdapter extends SettingAdapter<Integer,CowSex>
{
   @Override
   public CowSex unmarshal(Integer cowSexId) throws SettingsException
   {
      return CowSex.fromInt(cowSexId);
   }

   @Override
   public Integer marshal(CowSex cowSex) throws SettingsException
   {
      return cowSex.getSexId();
   }
}
