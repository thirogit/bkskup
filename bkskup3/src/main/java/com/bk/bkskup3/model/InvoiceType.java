package com.bk.bkskup3.model;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 6/26/12
 * Time: 9:54 AM
 */

public enum InvoiceType
{
   LUMP("L"),
   REGULAR("V");

   private String invoiceTypeId;

   private InvoiceType(String invoiceTypeId)
   {
      this.invoiceTypeId = invoiceTypeId;
   }

   public String getInvoiceTypeId()
   {
      return invoiceTypeId;
   }

   public static InvoiceType fromString(String invoiceTypeStr)
   {
      for(InvoiceType invoiceType : values())
      {
         if(invoiceType.getInvoiceTypeId().equals(invoiceTypeStr))
            return invoiceType;
      }
      return null;
   }
}
