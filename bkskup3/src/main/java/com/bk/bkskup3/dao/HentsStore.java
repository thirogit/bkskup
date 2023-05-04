package com.bk.bkskup3.dao;

import android.content.ContentValues;

import com.bk.bkskup3.dao.q.QHent;
import com.bk.bkskup3.db.BKCursor;
import com.bk.bkskup3.db.SQLCallable;
import com.bk.bkskup3.db.SQLDatabase;
import com.bk.bkskup3.db.SQLDatabaseQueue;
import com.bk.bkskup3.model.EAN;
import com.bk.bkskup3.model.Hent;
import com.bk.bkskup3.model.HentObj;
import com.bk.bkskup3.model.HentType;
import com.bk.bkskup3.model.IBAN;
import com.bk.bkskup3.utils.NullUtils;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.mysema.query.support.Query;
import com.mysema.serializer.SQLiteQuerySerializer;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.Future;

import static com.bk.bkskup3.dao.WhereUtils.eq;
import static com.bk.bkskup3.dao.WhereUtils.quote;
import static com.mysema.query.support.QueryBuilder.where;

public class HentsStore extends AbstractSQLStore {
    public HentsStore(SQLDatabaseQueue mDb) {
        super(mDb);
    }

    public static final String TABLE_NAME_HENTS = "HENTS";
    public static final String HENTS_HENTID = "HENTID";
    public static final String HENTS_ALIAS = "ALIAS";
    public static final String HENTS_HENTNAME = "HENTNAME";
    public static final String HENTS_ZIP = "ZIP";
    public static final String HENTS_CITY = "CITY";
    public static final String HENTS_STREET = "STREET";
    public static final String HENTS_POBOX = "POBOX";
    public static final String HENTS_NIP = "NIP";
    public static final String HENTS_HENTNO = "HENTNO";
    public static final String HENTS_WETNO = "WETNO";
    public static final String HENTS_PHONE = "PHONE";
    public static final String HENTS_PLATE = "PLATE";
    public static final String HENTS_HENTTYPE = "HENTTYPE";
    public static final String HENTS_EXTRAS = "EXTRAS";
    public static final String HENTS_ACCOUNTNO = "ACCOUNTNO";
    public static final String HENTS_BANKNAME = "BANKNAME";
    public static final String HENTS_PESEL = "PESEL";
    public static final String HENTS_REGON = "REGON";
    public static final String HENTS_IDNO = "IDNO";
    public static final String HENTS_ISSUEPOST = "ISSUEPOST";
    public static final String HENTS_ISSUEDATE = "ISSUEDATE";
    public static final String HENTS_CELLPHONE = "CELLPHONE";
    public static final String HENTS_EMAIL = "EMAIL";
    public static final String HENTS_LATITUDE = "LATITUDE";
    public static final String HENTS_LONGITUDE = "LONGITUDE";
    public static final String HENTS_WETLICENCENO = "WETLICENCENO";

    public Collection<HentObj> fetchAllHents() {
        return fetchHents(null);
    }

    public Collection<HentObj> fetchHents(Query query) {


        Future<Collection<HentObj>> fetchHentFuture = mDb.submit(new SQLCallable<Collection<HentObj>>() {
            @Override
            public Collection<HentObj> call(SQLDatabase db) {
                Collection<HentObj> hents = new LinkedList<HentObj>();
                BKCursor hentCursor = null;

                String where = null;
                long limit = -1;

                if (query != null) {
                    SQLiteQuerySerializer serializer = new SQLiteQuerySerializer();
                    query.serialize(serializer);
                    where = serializer.getWhere();
                    limit = serializer.getLimit();
                }

                try {
                    hentCursor = db.query(TABLE_NAME_HENTS,
                            new String[]{HENTS_HENTID,
                                    HENTS_ALIAS,
                                    HENTS_HENTNAME,
                                    HENTS_ZIP,
                                    HENTS_CITY,
                                    HENTS_STREET,
                                    HENTS_POBOX,
                                    HENTS_NIP,
                                    HENTS_HENTNO,
                                    HENTS_WETNO,
                                    HENTS_PHONE,
                                    HENTS_PLATE,
                                    HENTS_HENTTYPE,
                                    HENTS_EXTRAS,
                                    HENTS_ACCOUNTNO,
                                    HENTS_BANKNAME,
                                    HENTS_PESEL,
                                    HENTS_REGON,
                                    HENTS_IDNO,
                                    HENTS_ISSUEPOST,
                                    HENTS_ISSUEDATE,
                                    HENTS_CELLPHONE,
                                    HENTS_EMAIL,
                                    HENTS_LATITUDE,
                                    HENTS_LONGITUDE,
                                    HENTS_WETLICENCENO}, where, limit);


                    int hentIdColIndex = hentCursor.getColumnIndexOrThrow(HENTS_HENTID);
                    int hentAliasColIndex = hentCursor.getColumnIndexOrThrow(HENTS_ALIAS);
                    int hentHentNameColIndex = hentCursor.getColumnIndexOrThrow(HENTS_HENTNAME);
                    int hentZipColIndex = hentCursor.getColumnIndexOrThrow(HENTS_ZIP);
                    int hentCityColIndex = hentCursor.getColumnIndexOrThrow(HENTS_CITY);
                    int hentStreetColIndex = hentCursor.getColumnIndexOrThrow(HENTS_STREET);
                    int hentPOBoxColIndex = hentCursor.getColumnIndexOrThrow(HENTS_POBOX);
                    int hentNIPColIndex = hentCursor.getColumnIndexOrThrow(HENTS_NIP);
                    int hentHentNoColIndex = hentCursor.getColumnIndexOrThrow(HENTS_HENTNO);
                    int hentWetNoColIndex = hentCursor.getColumnIndexOrThrow(HENTS_WETNO);
                    int hentPhoneColIndex = hentCursor.getColumnIndexOrThrow(HENTS_PHONE);
                    int hentPlateColIndex = hentCursor.getColumnIndexOrThrow(HENTS_PLATE);
                    int hentHentTypeColIndex = hentCursor.getColumnIndexOrThrow(HENTS_HENTTYPE);
                    int hentExtrasColIndex = hentCursor.getColumnIndexOrThrow(HENTS_EXTRAS);
                    int hentAccountNoColIndex = hentCursor.getColumnIndexOrThrow(HENTS_ACCOUNTNO);
                    int hentBankNameColIndex = hentCursor.getColumnIndexOrThrow(HENTS_BANKNAME);
                    int hentPESELColIndex = hentCursor.getColumnIndexOrThrow(HENTS_PESEL);
                    int hentREGONColIndex = hentCursor.getColumnIndexOrThrow(HENTS_REGON);
                    int hentIdNoColIndex = hentCursor.getColumnIndexOrThrow(HENTS_IDNO);
                    int hentIssuePostColIndex = hentCursor.getColumnIndexOrThrow(HENTS_ISSUEPOST);
                    int hentIssueDateColIndex = hentCursor.getColumnIndexOrThrow(HENTS_ISSUEDATE);
                    int hentCellPhoneColIndex = hentCursor.getColumnIndexOrThrow(HENTS_CELLPHONE);
                    int hentEmailColIndex = hentCursor.getColumnIndexOrThrow(HENTS_EMAIL);
                    int hentLatitudeColIndex = hentCursor.getColumnIndexOrThrow(HENTS_LATITUDE);
                    int hentLongitudeColIndex = hentCursor.getColumnIndexOrThrow(HENTS_LONGITUDE);
                    int hentWetLicenceNoColIndex = hentCursor.getColumnIndexOrThrow(HENTS_WETLICENCENO);

                    while (hentCursor.moveToNext()) {
                        HentObj hent = new HentObj(hentCursor.getInt(hentIdColIndex));

                        hent.setBankAccountNo(IBAN.fromString(hentCursor.getString(hentAccountNoColIndex)));
                        hent.setAlias(hentCursor.getString(hentAliasColIndex));
                        hent.setHentName(hentCursor.getString(hentHentNameColIndex));
                        hent.setZip(hentCursor.getString(hentZipColIndex));
                        hent.setCity(hentCursor.getString(hentCityColIndex));
                        hent.setStreet(hentCursor.getString(hentStreetColIndex));
                        hent.setPoBox(hentCursor.getString(hentPOBoxColIndex));
                        hent.setFiscalNo(hentCursor.getString(hentNIPColIndex));
                        hent.setHentNo(EAN.fromString(hentCursor.getString(hentHentNoColIndex)));
                        hent.setWetNo(hentCursor.getString(hentWetNoColIndex));
                        hent.setPhoneNo(hentCursor.getString(hentPhoneColIndex));
                        hent.setPlateNo(hentCursor.getString(hentPlateColIndex));
                        hent.setHentType(HentType.fromString(hentCursor.getString(hentHentTypeColIndex)));
                        hent.setExtras(hentCursor.getString(hentExtrasColIndex));
                        hent.setBankName(hentCursor.getString(hentBankNameColIndex));
                        hent.setPersonalNo(hentCursor.getString(hentPESELColIndex));
                        hent.setREGON(hentCursor.getString(hentREGONColIndex));
                        hent.setPersonalIdNo(hentCursor.getString(hentIdNoColIndex));
                        hent.setIssueDate(hentCursor.getDate(hentIssueDateColIndex));
                        hent.setCellPhoneNo(hentCursor.getString(hentCellPhoneColIndex));
                        hent.setEmail(hentCursor.getString(hentEmailColIndex));
                        hent.setLatitude(hentCursor.getDouble(hentLatitudeColIndex));
                        hent.setLongitude(hentCursor.getDouble(hentLongitudeColIndex));
                        hent.setWetLicenceNo(hentCursor.getString(hentWetLicenceNoColIndex));
                        hent.setIssuePost(hentCursor.getString(hentIssuePostColIndex));

                        hents.add(hent);
                    }

                } finally {
                    if (hentCursor != null) {
                        hentCursor.close();
                    }
                }
                return hents;
            }
        });


        return get(fetchHentFuture);
    }


    private ContentValues createHentValues(Hent hent) {
        ContentValues hentValues = new ContentValues();
        hentValues.put(HENTS_ALIAS, hent.getAlias());
        hentValues.put(HENTS_HENTNAME, hent.getHentName());
        hentValues.put(HENTS_ZIP, hent.getZip());
        hentValues.put(HENTS_CITY, hent.getCity());
        hentValues.put(HENTS_STREET, hent.getStreet());
        hentValues.put(HENTS_POBOX, hent.getPoBox());
        hentValues.put(HENTS_NIP, Strings.emptyToNull(hent.getFiscalNo()));
        hentValues.put(HENTS_HENTNO, hent.getHentNo().toString());
        hentValues.put(HENTS_WETNO, Strings.emptyToNull(hent.getWetNo()));
        hentValues.put(HENTS_PHONE, Strings.emptyToNull(hent.getPhoneNo()));
        hentValues.put(HENTS_PLATE, Strings.emptyToNull(hent.getPlateNo()));
        hentValues.put(HENTS_HENTTYPE, hent.getHentType().getHentTypeId());
        hentValues.put(HENTS_EXTRAS, Strings.emptyToNull(hent.getExtras()));
        hentValues.put(HENTS_ACCOUNTNO, NullUtils.ibanOrNull(hent.getBankAccountNo()));
        hentValues.put(HENTS_BANKNAME, Strings.emptyToNull(hent.getBankName()));
        hentValues.put(HENTS_PESEL, Strings.emptyToNull(hent.getPersonalNo()));
        hentValues.put(HENTS_REGON, Strings.emptyToNull(hent.getREGON()));
        hentValues.put(HENTS_IDNO, Strings.emptyToNull(hent.getPersonalIdNo()));
        hentValues.put(HENTS_ISSUEPOST, Strings.emptyToNull(hent.getIssuePost()));
        hentValues.put(HENTS_ISSUEDATE, NullUtils.dateTimeLongOrNull(hent.getIssueDate()));
        hentValues.put(HENTS_CELLPHONE, Strings.emptyToNull(hent.getCellPhoneNo()));
        hentValues.put(HENTS_EMAIL, Strings.emptyToNull(hent.getEmail()));
        hentValues.put(HENTS_LATITUDE, hent.getLatitude());
        hentValues.put(HENTS_LONGITUDE, hent.getLongitude());
        hentValues.put(HENTS_WETLICENCENO, Strings.emptyToNull(hent.getWetLicenceNo()));
        return hentValues;
    }

    public HentObj insertHent(Hent newHent) {
        ContentValues hentValues = createHentValues(newHent);

        Future<HentObj> insertedHent = mDb.submit(db -> {
            db.insertOrThrow(TABLE_NAME_HENTS, hentValues);
            int newHentId = db.queryMax(TABLE_NAME_HENTS, HENTS_HENTID, 1);
            HentObj insertedHent1 = new HentObj(newHentId);
            insertedHent1.copyFrom(newHent);
            return insertedHent1;
        });


        return get(insertedHent);
    }

    public void saveHent(Hent hent) {
        ContentValues hentValues = createHentValues(hent);

        Future<Void> updateHentFuture = mDb.submitTransaction((SQLCallable<Void>) db -> {
            String hentNoStr = hent.getHentNo().toString();
            String where = eq(HENTS_HENTNO, quote(hentNoStr));
            BKCursor cursor = db.query(TABLE_NAME_HENTS, new String[]{"1"},  where, -1);
            if (cursor.moveToNext()) {
                db.updateOrThrow(TABLE_NAME_HENTS, hentValues, where);
            } else {
                db.insertOrThrow(TABLE_NAME_HENTS, hentValues);
            }
            return null;
        });

        get(updateHentFuture);
    }

    public void updateHent(Hent hent) {
        ContentValues hentValues = createHentValues(hent);

        Future<Void> updateHentFuture = mDb.submitTransaction((SQLCallable<Void>) db -> {
            db.updateOrThrow(TABLE_NAME_HENTS, hentValues, HENTS_HENTID + '=' + hent.getId());
            return null;
        });

        get(updateHentFuture);
    }

    public HentObj fetchHent(EAN hentNo) {
        Collection<HentObj> hents = fetchHents(where(QHent.hentNo.eq(hentNo.toString())));
        return Iterables.getFirst(hents, null);
    }

    public HentObj fetchHent(int hentId) {
        Collection<HentObj> hents = fetchHents(where(QHent.id.eq(hentId)));
        return Iterables.getFirst(hents, null);
    }
}
