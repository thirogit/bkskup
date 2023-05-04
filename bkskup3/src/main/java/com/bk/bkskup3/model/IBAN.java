package com.bk.bkskup3.model;

import android.text.TextUtils;
import com.bk.countries.Countries;
import com.bk.countries.Country;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/22/2014
 * Time: 3:07 PM
 */
public class IBAN implements Serializable {

    private Country mCountry;
    private String mNumber;

    public IBAN(Country country, String number) {

        Preconditions.checkNotNull(country);
        this.mCountry = country;
        Preconditions.checkArgument(TextUtils.isDigitsOnly(number));
        this.mNumber = number;
    }

    public static IBAN fromString(String iban) {
        if (Strings.isNullOrEmpty(iban))
            return null;

        if (iban.length() < 10)
            throw new IllegalArgumentException("iban should be atleast 10 chars. in length");

        String countryCode = iban.substring(0, 2);
        String number = iban.substring(2);

        Country country = Countries.getCountries().getCountryIsoCode2(countryCode);

        if (country == null)
            throw new IllegalArgumentException(countryCode + " is not a valid country code");

        return new IBAN(country, number);

    }

    @Override
    public String toString() {
        return getCountry().getCode2A() + getNumber();
    }

    public Country getCountry() {
        return mCountry;
    }

    public String getNumber() {
        return mNumber;
    }
}
