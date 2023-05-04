package com.bk.bkskup3.utils.check;

import com.bk.bkskup3.model.IBAN;
import com.bk.countries.Country;

import java.math.BigDecimal;

public class IBANCheck
{
	
	private static final BigDecimal NINETYSEVEN = BigDecimal.valueOf(97);
	
	private static int checksum(IBAN iban)
    {
        Country country = iban.getCountry();
        String number = iban.getNumber();
        String countryCode = country.getCode2A();
        String checkSum = number.substring(0,2);

        String tmp = (number.substring(2) + (countryCode +checkSum)).toUpperCase();
		StringBuffer digits = new StringBuffer();
		for (int i = 0; i < tmp.length(); i++) {
			char c = tmp.charAt(i);
			if (c >= '0' && c <= '9')
				digits.append(c);
			else if (c >= 'A' && c <= 'Z') {
				int n = c - 'A' + 10;
				digits.append((char) ('0' + n / 10));
				digits.append((char) ('0' + (n % 10)));
			} else
				return -1;
		}
		BigDecimal n = new BigDecimal(digits.toString());
		int remainder = n.remainder(NINETYSEVEN).intValue();
		return remainder;
	}

   public  static boolean validate(IBAN iban)
   {
       return iban.getCountry() != null && iban.getNumber().length() == 26 && checksum(iban) == 1;
   }


}