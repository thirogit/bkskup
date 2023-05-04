package com.bk.bkskup3.invoice;

import android.content.res.Resources;

import com.bk.bkskup3.R;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.Arrays;

import static com.bk.bkskup3.invoice.InvoiceNoFormatter.NUMBER;
import static com.bk.bkskup3.invoice.InvoiceNoFormatter.YEAR_YY;
import static com.bk.bkskup3.invoice.InvoiceNoFormatter.YEAR_YYYY;

/**
 * Created by SG0891787 on 9/2/2017.
 */

public class InvoiceNoFormatterBuilder {

    private String mFmt;
    private Resources mResources;

    public InvoiceNoFormatterBuilder(Resources r,String fmt) {
        this.mFmt = fmt;
        this.mResources = r;
    }

    public InvoiceNoFormatter build()
    {
        Multimap<String,String> localizedTokens = ArrayListMultimap.create();
        localizedTokens.put(YEAR_YYYY,mResources.getString(R.string.tokenYEAR));
        localizedTokens.put(YEAR_YYYY,mResources.getString(R.string.tokenYYYY));
        localizedTokens.put(YEAR_YY,mResources.getString(R.string.tokenYY));
        localizedTokens.putAll(NUMBER, Arrays.asList(mResources.getString(R.string.tokenCOUNTER),mResources.getString(R.string.tokenNUMBER)));
        return new InvoiceNoFormatter(localizedTokens,mFmt);
    }
}
