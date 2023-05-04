package com.bk.bkskup3.model;

/**
 * Created by IntelliJ IDEA.
 * User: root
 * Date: 14.06.11
 * Time: 17:22
 */
public enum PayWay
{

   CASH("$"),
   TRANSFER("@");

   private String payWayId;

   private PayWay(String payWayId)
   {
      this.payWayId = payWayId;
   }

   public String getPayWayId()
   {
      return payWayId;
   }

   public static PayWay fromString(String payWayStr)
   {
      for(PayWay payWay : values())
      {
         if(payWay.getPayWayId().equals(payWayStr))
            return payWay;
      }
      return null;
   }
}