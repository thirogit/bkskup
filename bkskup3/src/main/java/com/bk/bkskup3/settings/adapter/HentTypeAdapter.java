package com.bk.bkskup3.settings.adapter;


import com.bk.bksettings.annotation.adapter.SettingAdapter;
import com.bk.bksettings.runtime.SettingsException;
import com.bk.bkskup3.model.HentType;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/26/12
 * Time: 4:03 PM
 */
public class HentTypeAdapter extends SettingAdapter<String,HentType>
{
   @Override
   public HentType unmarshal(String hentTypeStr) throws SettingsException
   {
      return HentType.fromString(hentTypeStr);
   }

   @Override
   public String marshal(HentType hentType) throws SettingsException
   {
      return hentType.getHentTypeId();
   }
}
