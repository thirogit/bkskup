package com.bk.bkskup3.utils;

import com.google.common.base.Strings;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 8/1/12
 * Time: 3:17 PM
 */
public class Numbers {
    public static final int WEIGHT_PRECISION = -3;
    public static final int PRICE_PRECISION = 2;

    private static DecimalFormatSymbols decimalSymbols = DecimalFormatSymbols.getInstance(Locale.ENGLISH);
    private static DecimalFormat percentFormatter = new DecimalFormat("#.##'%'", decimalSymbols);


    public static long valueForNull(Long longObj, long valueWhenNull) {
        if (longObj == null)
            return valueWhenNull;

        return longObj;
    }

    public static int valueForNull(Integer intObj, int valueWhenNull) {
        if (intObj == null)
            return valueWhenNull;

        return intObj;
    }

    public static String formatPercent(Double percent) {
        if(percent == null) return "";
        return formatNumberUsing(percentFormatter, percent * 100.0);
    }

    public static String formatVatRate(Double vatRate) {
        return formatPercent(vatRate);
    }

    public static String formatWeight(Double weight) {
        return formatWithPrecision(weight, WEIGHT_PRECISION);
    }

    public static String formatPrice(Double price) {
        return formatWithPrecision(price, PRICE_PRECISION);
    }

    public static String formatWithPrecision(Double value, int precision) {
        if (value == null)
            return "";
        boolean bFormatAllDigits = precision > 0;
        int absolutePrecision = Math.abs(precision);

        DecimalFormat formatter = new DecimalFormat();
        formatter.setDecimalFormatSymbols(decimalSymbols);
        formatter.setGroupingUsed(false);
        formatter.setRoundingMode(RoundingMode.HALF_UP);
        formatter.setMaximumFractionDigits(absolutePrecision);
        if (bFormatAllDigits)
            formatter.setMinimumFractionDigits(absolutePrecision);
        return formatNumberUsing(formatter, value);

    }

    private static String formatNumberUsing(DecimalFormat formatter, Double number) {
        if (number == null) return "";
        return formatter.format(number);
    }

    public static boolean isAllDigits(String str)
    {
        if(Strings.isNullOrEmpty(str))
        {
            return false;
        }

        for(char c : str.toCharArray())
        {
            if(c < '0' || c > '9' ){
                return false;
            }
        }

        return true;


    }

}
