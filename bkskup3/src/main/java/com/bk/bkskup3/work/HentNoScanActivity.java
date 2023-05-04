package com.bk.bkskup3.work;

import android.content.Intent;
import com.bk.bkskup3.R;
import com.bk.bkskup3.model.EAN;
import com.bk.bkskup3.utils.check.HentNoCheck;
import com.bk.countries.Countries;
import com.bk.countries.Country;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/17/13
 * Time: 6:28 PM
 */
public class HentNoScanActivity extends ScanBarcodeActivity
{
   public static final String EXTRA_HENTNO = "hent_no";

   protected void onGotBarcode(String bc)
   {
      final EAN hentNo = EAN.fromString(bc);

      if(hentNo == null)
      {
         setFeedbackView(constructErrorFeedback(getResources().getString(R.string.errNotAFarmNo)));
      }
      else
      {
         Country country = Countries.getCountries().getCountryIsoCode2(hentNo.getCountryCode());
         if(country.getHentNoLength() != hentNo.length())
         {
            setFeedbackView(constructErrorFeedback(getResources().getString(R.string.errBadFarmNoLengthFmt, country.getHentNoLength())));
         }
         else if(!HentNoCheck.isValid(hentNo))
         {
            setFeedbackView(constructAskYesFeedBack(getResources().getString(R.string.askOverrideBadChkSum), new Runnable()
            {
               @Override
               public void run()
               {
                  finishWithHentNo(hentNo);
               }
            }));
         }
         else
         {
            finishWithHentNo(hentNo);
         }
      }
   }

   private void finishWithHentNo(EAN hentNo)
   {
      Intent result = new Intent();
      result.putExtra(EXTRA_HENTNO, hentNo);
      setResult(RESULT_OK, result);
      finish();
   }

}
