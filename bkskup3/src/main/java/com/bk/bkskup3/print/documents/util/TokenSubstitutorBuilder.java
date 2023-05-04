package com.bk.bkskup3.print.documents.util;

import com.bk.bands.lookup.StrSubstitutor;
import com.bk.bkskup3.print.context.LocalizedTokens;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by SG0891787 on 10/13/2017.
 */

public class TokenSubstitutorBuilder {

    private LocalizedTokens mLocalizedTokens;
    private Map<String,String> mTokenValues = new HashMap<>();


    public TokenSubstitutorBuilder(LocalizedTokens localizedTokens) {
        this.mLocalizedTokens = localizedTokens;
    }

    public TokenSubstitutorBuilder with(String token,String value)
    {
        Collection<String> localizedTokens = mLocalizedTokens.getLocalizedToken(token);
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
