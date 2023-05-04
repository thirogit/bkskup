package com.bk.bkskup3.work;

import com.bk.bkskup3.model.EAN;
import com.bk.bkskup3.utils.check.CowNoCheck;
import com.bk.countries.Country;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 9/20/12
 * Time: 1:38 PM
 */
public class CowNoInputActivity extends EANInputActivity
{
  protected boolean validateCheckSum(EAN ean)
  {
     return CowNoCheck.isValid(ean);
  }

   protected int getEANLength(Country country)
   {
      return country.getCowNoLength();
   }

   protected boolean isAllowOverrideBadChkSum()
   {
      return true;
   }
}
