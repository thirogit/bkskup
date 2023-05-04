package com.bk.bkskup3.model;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/20/11
 * Time: 12:26 AM
 */
public interface Cow extends Idable
{
   EAN getCowNo();

   CowSex getSex();

   String getStockCd();

   String getClassCd();

   double getWeight();

   double getPrice();

   int getInvoice();

   Double getLatitude();

   Double getLongitude();

   EAN getFirstOwner();

   String getPassportNo();

   Date getPassportIssueDt();

   String getHealthCertNo();

   EAN getMotherNo();

   String getBirthPlace();

   Date getBirthDt();

   CowPriceComponents getPriceComponents();
}
