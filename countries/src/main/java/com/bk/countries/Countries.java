package com.bk.countries;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 8/25/12
 * Time: 1:00 PM
 */
public class Countries {
    static class CountryDescriptor {
        int isoNumber;
        int countryNameResId;
        String isoCode2;
        String isoCode3;
        boolean inEU;
        int cowNoLength;
        int farmNoLength;
        int hentNoLength;
        int flagImgResId;

        CountryDescriptor(int countryNameResId,
                          String isoCode2,
                          String isoCode3,
                          int isoNumber,
                          boolean inEU,
                          int cowNoLength,
                          int farmNoLength,
                          int hentNoLength,
                          int flagImgResId) {
            this.countryNameResId = countryNameResId;
            this.isoCode2 = isoCode2;
            this.isoCode3 = isoCode3;
            this.inEU = inEU;
            this.cowNoLength = cowNoLength;
            this.isoNumber = isoNumber;
            this.flagImgResId = flagImgResId;
            this.farmNoLength = farmNoLength;
            this.hentNoLength = hentNoLength;
        }
    }


    private static CountryDescriptor[] countries = new CountryDescriptor[]
            {
                    new CountryDescriptor(R.string.countryLUX, "LU", "LUX", 442, true, 14, 11, 14, R.drawable.luxembourg),
                    new CountryDescriptor(R.string.countryDNK, "DK", "DNK", 208, true, 13, 11, 14, R.drawable.denmark),
                    new CountryDescriptor(R.string.countryIRL, "IE", "IRL", 372, true, 14, 11, 14, R.drawable.ireland),
                    new CountryDescriptor(R.string.countryAUT, "AT", "AUT", 32, true, 11, 8, 14, R.drawable.austria),
                    new CountryDescriptor(R.string.countryNLD, "NL", "NLD", 528, true, 11, 8, 14, R.drawable.netherlands),
                    new CountryDescriptor(R.string.countryBEL, "BE", "BEL", 46, true, 11, 11, 14, R.drawable.belgium),
                    new CountryDescriptor(R.string.countryFRA, "FR", "FRA", 250, true, 12, 11, 14, R.drawable.france),
                    new CountryDescriptor(R.string.countryDEU, "DE", "DEU", 276, true, 12, 11, 14, R.drawable.germany),
                    new CountryDescriptor(R.string.countryGBR, "GB", "GBR", 826, true, 14, 11, 14, R.drawable.united_kindom),
                    new CountryDescriptor(R.string.countryITA, "IT", "ITA", 380, true, 14, 11, 14, R.drawable.italy),
                    new CountryDescriptor(R.string.countryFIN, "FI", "FIN", 246, true, 14, 11, 14, R.drawable.finland),
                    new CountryDescriptor(R.string.countrySWE, "SE", "SWE", 752, true, 14, 11, 14, R.drawable.sweden),
                    new CountryDescriptor(R.string.countryESP, "ES", "ESP", 724, true, 14, 11, 14, R.drawable.spain),
                    new CountryDescriptor(R.string.countryGRC, "GR", "GRC", 300, true, 14, 11, 14, R.drawable.greece),
                    new CountryDescriptor(R.string.countryCYP, "CY", "CYP", 196, true, 14, 11, 14, R.drawable.cyprus),
                    new CountryDescriptor(R.string.countrySVN, "SI", "SVN", 705, true, 14, 11, 14, R.drawable.slovenia),
                    new CountryDescriptor(R.string.countryPRT, "PT", "PRT", 620, true, 14, 11, 14, R.drawable.portugal),
                    new CountryDescriptor(R.string.countryMLT, "MT", "MLT", 370, true, 14, 11, 14, R.drawable.malta),
                    new CountryDescriptor(R.string.countryCZE, "CZ", "CZE", 203, true, 11, 11, 14, R.drawable.czech_republic),
                    new CountryDescriptor(R.string.countryHUN, "HU", "HUN", 348, true, 14, 11, 14, R.drawable.hungary),
                    new CountryDescriptor(R.string.countrySVK, "SK", "SVK", 703, true, 11, 8, 14, R.drawable.slovakia),
                    new CountryDescriptor(R.string.countryEST, "EE", "EST", 233, true, 12, 11, 14, R.drawable.estonia),
                    new CountryDescriptor(R.string.countryPOL, "PL", "POL", 616, true, 14, 11, 14, R.drawable.poland),
                    new CountryDescriptor(R.string.countryLTU, "LT", "LTU", 440, true, 14, 11, 14, R.drawable.lithuania),
                    new CountryDescriptor(R.string.countryLVA, "LV", "LVA", 428, true, 14, 11, 14, R.drawable.latvia),
                    new CountryDescriptor(R.string.countryRUS, "RU", "RUS", 643, false, 14, 11, 14, R.drawable.russia),
                    new CountryDescriptor(R.string.countryCHE, "CH", "CHE", 756, false, 14, 11, 14, R.drawable.switzerland),
                    new CountryDescriptor(R.string.countryTUR, "TR", "TUR", 792, false, 14, 11, 14, R.drawable.turkey),
                    new CountryDescriptor(R.string.countryNOR, "NO", "NOR", 578, false, 14, 11, 14, R.drawable.norway),
                    new CountryDescriptor(R.string.countryUKR, "UA", "UKR", 804, false, 14, 11, 14, R.drawable.ukraine),
                    new CountryDescriptor(R.string.countryROU, "RO", "ROU", 642, false, 14, 11, 14, R.drawable.romania),
                    new CountryDescriptor(R.string.countryKAZ, "KZ", "KAZ", 398, false, 14, 11, 14, R.drawable.kazakhstan),
                    new CountryDescriptor(R.string.countryHRV, "HR", "HRV", 191, false, 14, 11, 14, R.drawable.croatia),
                    new CountryDescriptor(R.string.countryBLR, "BY", "BLR", 112, false, 14, 11, 14, R.drawable.belarus),
                    new CountryDescriptor(R.string.countrySRB, "RS", "SRB", 688, false, 14, 11, 14, R.drawable.serbia),
                    new CountryDescriptor(R.string.countryMNE, "ME", "MNE", 499, false, 14, 11, 14, R.drawable.montenegro),
                    new CountryDescriptor(R.string.countryBGR, "BG", "BGR", 100, false, 14, 11, 14, R.drawable.bulgaria),
                    new CountryDescriptor(R.string.countryISL, "IS", "ISL", 352, false, 14, 11, 14, R.drawable.iceland),
                    new CountryDescriptor(R.string.countryAZE, "AZ", "AZE", 31, false, 14, 11, 14, R.drawable.azerbaijan),
                    new CountryDescriptor(R.string.countryMKD, "MK", "MKD", 807, false, 14, 11, 14, R.drawable.macedonia),
                    new CountryDescriptor(R.string.countrySGS, "GS", "SGS", 239, false, 14, 11, 14, R.drawable.georgia),
                    new CountryDescriptor(R.string.countryARM, "AM", "ARM", 51, false, 14, 11, 14, R.drawable.armenia),
                    new CountryDescriptor(R.string.countryAND, "AD", "AND", 20, false, 14, 11, 14, R.drawable.andorra),
                    new CountryDescriptor(R.string.countryLIE, "LI", "LIE", 438, false, 14, 11, 14, R.drawable.liechtenstein),
                    new CountryDescriptor(R.string.countryMCO, "MC", "MCO", 492, false, 14, 11, 14, R.drawable.monaco),
                    new CountryDescriptor(R.string.countrySMR, "SM", "SMR", 674, false, 14, 11, 14, R.drawable.san_marino)
            };

    private Map<String, Country> perIsoCode2Index = new HashMap<String, Country>(countries.length);
    private Map<String, Country> perIsoCode3Index = new HashMap<String, Country>(countries.length);
    private Map<Integer, Country> perIsoNumberIndex = new HashMap<Integer, Country>(countries.length);
    private Map<Integer, Integer> flagIdsPerIsoNumberIndex = new HashMap<Integer, Integer>(countries.length);
    private Map<Integer, Integer> countryNameIdsPerIsoNumberIndex = new HashMap<Integer, Integer>(countries.length);
    private static Countries instance = new Countries();


    private Countries() {

        for (CountryDescriptor countryDescriptor : countries) {
            CountryObj country = new CountryObj(countryDescriptor.isoNumber);
            country.setCode2A(countryDescriptor.isoCode2);
            country.setCode3A(countryDescriptor.isoCode3);
            country.setCowNoLength(countryDescriptor.cowNoLength);
            country.setFarmNoLength(countryDescriptor.farmNoLength);
            country.setHentNoLength(countryDescriptor.hentNoLength);
            country.setInEU(countryDescriptor.inEU);

            perIsoCode2Index.put(countryDescriptor.isoCode2, country);
            perIsoCode3Index.put(countryDescriptor.isoCode3, country);
            perIsoNumberIndex.put(countryDescriptor.isoNumber, country);

            flagIdsPerIsoNumberIndex.put(countryDescriptor.isoNumber, countryDescriptor.flagImgResId);
            countryNameIdsPerIsoNumberIndex.put(countryDescriptor.isoNumber, countryDescriptor.countryNameResId);
        }

    }


    public Country getCountryIsoCode2(String isoCode2) {
        return perIsoCode2Index.get(isoCode2);
    }

    public Country getCountryIsoCode3(String isoCode3) {
        return perIsoCode3Index.get(isoCode3);
    }

    public Country getCountryIsoNumber(int isoNumber) {
        return perIsoNumberIndex.get(isoNumber);
    }

    public static Countries getCountries() {
        return instance;
    }

    public Collection<Country> getAllCountries() {
        return perIsoCode2Index.values();
    }

    public int getCountryFlag(int isoNumber) {
        return getSafeInt(flagIdsPerIsoNumberIndex, isoNumber, 0);
    }

    public int getCountryName(int isoNumber) {
        return getSafeInt(countryNameIdsPerIsoNumberIndex, isoNumber, 0);
    }

    private int getSafeInt(Map<Integer, Integer> map, int keyValue, int whenNullValue) {
        Integer value = map.get(keyValue);

        return value == null ? whenNullValue : value;
    }
}
