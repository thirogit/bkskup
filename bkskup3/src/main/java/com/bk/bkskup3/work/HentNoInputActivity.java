package com.bk.bkskup3.work;

import com.bk.bkskup3.model.EAN;
import com.bk.bkskup3.utils.check.HentNoCheck;
import com.bk.countries.Country;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 9/20/12
 * Time: 3:10 PM
 */
public class HentNoInputActivity extends EANInputActivity {
    protected boolean validateCheckSum(EAN ean) {
        return HentNoCheck.isValid(ean);
    }

    protected int getEANLength(Country country) {
        return country.getHentNoLength();
    }

    protected boolean isAllowOverrideBadChkSum() {
        return true;
    }
}
