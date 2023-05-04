package com.bk.bkskup3.work;

import android.content.Intent;
import com.bk.bkskup3.R;
import com.bk.bkskup3.model.EAN;
import com.bk.bkskup3.utils.check.CowNoCheck;
import com.bk.countries.Countries;
import com.bk.countries.Country;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/17/13
 * Time: 6:28 PM
 */
public class CowNoScanActivity extends ScanBarcodeActivity
{
   public static final String EXTRA_COWNO = "cow_no";

   protected void onGotBarcode(String bc)
   {
      final EAN cowNo = EAN.fromString(bc);

      if(cowNo == null)
      {
         setFeedbackView(constructErrorFeedback(getResources().getString(R.string.errNotACowNo)));
      }
      else
      {
         Country country = Countries.getCountries().getCountryIsoCode2(cowNo.getCountryCode());
         if(country.getCowNoLength() != cowNo.length())
         {
            setFeedbackView(constructErrorFeedback(getResources().getString(R.string.errBadCowNoLengthFmt,country.getCowNoLength())));
         }
         else if(!CowNoCheck.isValid(cowNo))
         {
            setFeedbackView(constructAskYesFeedBack(getResources().getString(R.string.askOverrideBadChkSum), new Runnable()
            {
               @Override
               public void run()
               {
                  finishWithCowNo(cowNo);
               }
            }));
         }
         else
         {
            finishWithCowNo(cowNo);
         }
      }
   }


   private void finishWithCowNo(EAN cowNo)
   {
      Intent result = new Intent();
      result.putExtra(EXTRA_COWNO, cowNo);
      setResult(RESULT_OK, result);
      finish();
   }

}
