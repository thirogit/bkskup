package com.bk.bkskup3.settings.adapter;


import com.bk.bksettings.annotation.adapter.SettingAdapter;
import com.bk.bksettings.runtime.SettingsException;
import com.bk.bkskup3.model.PayWay;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/26/12
 * Time: 3:57 PM
 */
public class PayWayAdapter extends SettingAdapter<String,PayWay>
{
   @Override
   public PayWay unmarshal(String payWayStr) throws SettingsException
   {
      return PayWay.fromString(payWayStr);
   }

   @Override
   public String marshal(PayWay payWay) throws SettingsException
   {
      return payWay.getPayWayId();
   }
}
