package com.bk.bkskup3.work;

import com.bk.bkskup3.model.EAN;
import com.bk.countries.Country;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 9/20/12
 * Time: 3:10 PM
 */
public class FarmNoInputActivity extends EANInputActivity {
    protected boolean validateCheckSum(EAN ean) {
        return true;
    }

    protected int getEANLength(Country country) {
        return country.getFarmNoLength();
    }

    protected boolean isAllowOverrideBadChkSum() {
        return true;
    }
}
