package com.bk.bkskup3.invoice;

import com.bk.bands.lookup.StrSubstitutor;
import com.bk.bkskup3.utils.Dates;
import com.google.common.base.Strings;
import com.google.common.collect.Multimap;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by SG0891787 on 9/2/2017.
 */

public class InvoiceNoFormatter {

    public static final String YEAR_YYYY = "YYYY";
    public static final String YEAR_YY = "YY";
    public static final String NUMBER = "NUMBER";


    private class TokenSubstitutorBuilder {

        Map<String,String> mTokenValues = new HashMap<>();

        public TokenSubstitutorBuilder with(String token, String value)
        {
            Collection<String> localizedTokens = mLocalizedTokens.get(token);
            mTokenValues.put(token, value);
            for(String localizedToken : localizedTokens)
            {
                mTokenValues.put(localizedToken,value);
            }
            return this;
        }

        public StrSubstitutor build()
        {
            return new StrSubstitutor(mTokenValues);
        }
    }


    private String mFmt;
    private Multimap<String,String> mLocalizedTokens;

    InvoiceNoFormatter(Multimap<String,String> localizedTokens,String fmt) {
        this.mLocalizedTokens = localizedTokens;
        this.mFmt = fmt;
    }

    public String format(int invoiceNo) {

        if (!Strings.isNullOrEmpty(mFmt)) {
            DateFormat yearYYYYFmt = new SimpleDateFormat("yyyy", Locale.US);
            DateFormat yearYYFmt = new SimpleDateFormat("yy", Locale.US);
            Date now = Dates.now();

            TokenSubstitutorBuilder builder = new TokenSubstitutorBuilder();

            builder.with(YEAR_YYYY,yearYYYYFmt.format(now));
            builder.with(YEAR_YY,yearYYFmt.format(now));
            builder.with(NUMBER,String.valueOf(invoiceNo));

            StrSubstitutor substitutor = builder.build();
            return substitutor.replace(mFmt);
        } else {
            return String.valueOf(invoiceNo);
        }
    }
}
