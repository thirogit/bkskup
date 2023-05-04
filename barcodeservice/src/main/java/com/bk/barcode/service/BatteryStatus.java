package com.bk.barcode.service;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/13/13
 * Time: 8:04 PM
 */
public class BatteryStatus
{
   private BatteryLevel batteryLevel = BatteryLevel.CRITICAL;
   private boolean acOn = false;

   public BatteryStatus(BatteryLevel batteryLevel, boolean acOn)
   {
      this.batteryLevel = batteryLevel;
      this.acOn = acOn;
   }

   public BatteryLevel getBatteryLevel()
   {
      return batteryLevel;
   }

   public void setBatteryLevel(BatteryLevel batteryLevel)
   {
      this.batteryLevel = batteryLevel;
   }

   public boolean isACOn()
   {
      return acOn;
   }

   public void setACOn(boolean acOn)
   {
      this.acOn = acOn;
   }
}
