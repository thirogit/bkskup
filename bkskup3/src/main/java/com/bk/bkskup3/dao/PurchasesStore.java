package com.bk.bkskup3.dao;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.bk.bkskup3.dao.q.QCow;
import com.bk.bkskup3.dao.q.QInvoice;
import com.bk.bkskup3.dao.q.QPurchase;
import com.bk.bkskup3.db.BKCursor;
import com.bk.bkskup3.db.SQLCallable;
import com.bk.bkskup3.db.SQLDatabase;
import com.bk.bkskup3.db.SQLDatabaseQueue;
import com.bk.bkskup3.model.Cow;
import com.bk.bkskup3.model.CowDetails;
import com.bk.bkskup3.model.CowObj;
import com.bk.bkskup3.model.CowSex;
import com.bk.bkskup3.model.EAN;
import com.bk.bkskup3.model.IBAN;
import com.bk.bkskup3.model.Invoice;
import com.bk.bkskup3.model.InvoiceDeduction;
import com.bk.bkskup3.model.InvoiceDeductionObj;
import com.bk.bkskup3.model.InvoiceDetails;
import com.bk.bkskup3.model.InvoiceHent;
import com.bk.bkskup3.model.InvoiceHentObj;
import com.bk.bkskup3.model.InvoiceObj;
import com.bk.bkskup3.model.InvoiceType;
import com.bk.bkskup3.model.PayWay;
import com.bk.bkskup3.model.Purchase;
import com.bk.bkskup3.model.PurchaseDetails;
import com.bk.bkskup3.model.PurchaseObj;
import com.bk.bkskup3.model.PurchaseState;
import com.bk.bkskup3.utils.Dates;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mysema.query.support.Query;
import com.mysema.serializer.SQLiteQuerySerializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

import static com.bk.bkskup3.utils.NullUtils.dateTimeLongOrNull;
import static com.bk.bkskup3.utils.NullUtils.ibanOrNull;
import static com.bk.bkskup3.utils.NullUtils.nullOrToString;
import static com.mysema.query.support.QueryBuilder.where;

public class PurchasesStore extends AbstractSQLStore {


    public static final String TABLE_NAME_PURCHASES = "purchases";
    public static final String PURACHES_PURACHEID = "purchaseid";
    public static final String PURACHES_PURACHESTART = "start";
    public static final String PURACHES_PURACHEEND = "end";
    public static final String PURACHES_PLATENO = "plateno";
    public static final String PURACHES_HERDNO = "herdno";
    public static final String PURACHES_STATE = "state";

    public static final String TABLE_NAME_INVOICES = "INVOICES";
    public static final String INVOICES_VATRATE = "VATRATE";
    public static final String INVOICES_PAYWAY = "PAYWAY";
    public static final String INVOICES_CUSTOMNUMBER = "CUSTOMNUMBER";
    public static final String INVOICES_PAYDUEDAYS = "PAYDUEDAYS";
    public static final String INVOICES_INVOICEID = "INVOICEID";
    public static final String INVOICES_PURACHE = "PURACHE";
    public static final String INVOICES_INVOICEDT = "INVOICEDT";
    public static final String INVOICES_TRANSACTIONDT = "TRANSACTIONDT";
    public static final String INVOICES_TRANSACTIONPLACE = "TRANSACTIONPLACE";
    public static final String INVOICES_INVOICETYPE = "INVOICETYPE";
    public static final String INVOICES_EXTRAS = "EXTRAS";

    public static final String TABLE_NAME_COWS = "COWS";
    public static final String COWS_COWID = "COWID";
    public static final String COWS_EAN = "EAN";
    public static final String COWS_SEX = "SEX";
    public static final String COWS_STOCK = "STOCK";
    public static final String COWS_CLASS = "CLASS";
    public static final String COWS_WEIGHT = "WEIGHT";
    public static final String COWS_PRICE = "PRICE";
    public static final String COWS_LATITUDE = "LATITUDE";
    public static final String COWS_LONGITUDE = "LONGITUDE";
    public static final String COWS_INVOICE = "INVOICE";
    public static final String COWS_FIRSTOWNER = "FIRSTOWNER";
    public static final String COWS_PASSPORTNO = "PASSPORTNO";
    public static final String COWS_PASSPORTISSUEDT = "PASSPORTISSUEDT";
    public static final String COWS_HEALTHCERTNO = "HEALTHCERTNO";
    public static final String COWS_MOTHEREAN = "MOTHEREAN";
    public static final String COWS_BIRTHPLACE = "BIRTHPLACE";
    public static final String COWS_BIRTHDT = "BIRTHDT";


    public static final String TABLE_NAME_INVOICEHENTS = "INVOICE_HENTS";
    public static final String INVOICEHENTS_HENTNAME = "HENTNAME";
    public static final String INVOICEHENTS_ZIP = "ZIP";
    public static final String INVOICEHENTS_CITY = "CITY";
    public static final String INVOICEHENTS_STREET = "STREET";
    public static final String INVOICEHENTS_POBOX = "POBOX";
    public static final String INVOICEHENTS_NIP = "NIP";
    public static final String INVOICEHENTS_HENTNO = "HENTNO";
    public static final String INVOICEHENTS_WETNO = "WETNO";
    public static final String INVOICEHENTS_ACCOUNTNO = "ACCOUNTNO";
    public static final String INVOICEHENTS_BANKNAME = "BANKNAME";
    public static final String INVOICEHENTS_PESEL = "PESEL";
    public static final String INVOICEHENTS_REGON = "REGON";
    public static final String INVOICEHENTS_IDNO = "IDNO";
    public static final String INVOICEHENTS_ISSUEPOST = "ISSUEPOST";
    public static final String INVOICEHENTS_ISSUEDATE = "ISSUEDATE";
    public static final String INVOICEHENTS_CELLPHONE = "CELLPHONE";
    public static final String INVOICEHENTS_EMAIL = "EMAIL";
    public static final String INVOICEHENTS_WETLICENCENO = "WETLICENCENO";
    public static final String INVOICEHENTS_INVOICEID = "INVOICEID";
    public static final String INVOICEHENTS_PHONE = "PHONE";


    public static final String TABLE_NAME_INVOICEDEDUCTIONS = "INVOICE_DEDUCTIONS";
    public static final String INVOICEDEDUCTIONS_CODE = "CODE";
    public static final String INVOICEDEDUCTIONS_FRACTION = "FRACTION";
    public static final String INVOICEDEDUCTIONS_REASON = "REASON";
    public static final String INVOICEDEDUCTIONS_INVOICEID = "INVOICEID";
    public static final String INVOICEDEDUCTIONS_ENABLED = "ENABLED";


    public PurchasesStore(SQLDatabaseQueue mDb) {
        super(mDb);
    }


    public PurchaseObj fetchPurchase(int purchaseId) {
        Collection<PurchaseObj> purchaseObjs = fetchPurchases(where(QPurchase.id.eq(purchaseId)));
        return Iterables.getFirst(purchaseObjs, null);
    }

    public Collection<PurchaseObj> fetchAllPurchases() {
        return fetchPurchases(null);
    }

    public PurchaseDetails fetchPurchaseDetails(int purchaseId) {
        Collection<PurchaseDetails> purchaseDetails = fetchPurchaseDetails(where(QPurchase.id.eq(purchaseId)));
        return Iterables.getFirst(purchaseDetails, null);
    }

    public Collection<PurchaseDetails> fetchPurchaseDetails(Query q) {
        Collection<PurchaseDetails> result = new ArrayList<PurchaseDetails>();
        Collection<PurchaseObj> purchaseShells = fetchPurchaseShells(q);

        for (PurchaseObj purchaseShell : purchaseShells) {
            result.add(purchaseShell.getDetails());
        }

        return result;
    }


    private Collection<PurchaseObj> fetchPurchaseShells(Query q) {

        Future<Collection<PurchaseObj>> future = mDb.submit(new SQLCallable<Collection<PurchaseObj>>() {
            @Override
            public Collection<PurchaseObj> call(SQLDatabase db) {
                LinkedList<PurchaseObj> purchases = new LinkedList<PurchaseObj>();

                long limit = -1;
                String where = null;

                if (q != null) {
                    SQLiteQuerySerializer serializer = new SQLiteQuerySerializer();
                    q.serialize(serializer);

                    limit = serializer.getLimit();
                    where = serializer.getWhere();
                }


                BKCursor purchaseCursor =
                        db.query(TABLE_NAME_PURCHASES,
                                new String[]{PURACHES_PURACHEID,
                                        PURACHES_PURACHESTART,
                                        PURACHES_PURACHEEND,
                                        PURACHES_PLATENO,
                                        PURACHES_STATE,
                                        PURACHES_HERDNO},
                                where, limit);

                int puracheIdColIndex = purchaseCursor.getColumnIndexOrThrow(PURACHES_PURACHEID);
                int puracheStartColIndex = purchaseCursor.getColumnIndexOrThrow(PURACHES_PURACHESTART);
                int puracheEndColIndex = purchaseCursor.getColumnIndexOrThrow(PURACHES_PURACHEEND);
                int purachePlateNoColIndex = purchaseCursor.getColumnIndexOrThrow(PURACHES_PLATENO);
                int puracheStateColIndex = purchaseCursor.getColumnIndexOrThrow(PURACHES_STATE);
                int puracheHerdNoColIndex = purchaseCursor.getColumnIndexOrThrow(PURACHES_HERDNO);

                while (purchaseCursor.moveToNext()) {
                    PurchaseObj purchase = new PurchaseObj(purchaseCursor.getInt(puracheIdColIndex));
                    purchase.setPlateNo(purchaseCursor.getString(purachePlateNoColIndex));
                    purchase.setPurchaseEnd(purchaseCursor.getDate(puracheEndColIndex));
                    purchase.setPurchaseStart(purchaseCursor.getDate(puracheStartColIndex));
                    purchase.setState(PurchaseState.fromString(purchaseCursor.getString(puracheStateColIndex)));
                    purchase.setHerdNo(purchaseCursor.getInt(puracheHerdNoColIndex));
                    purchases.add(purchase);
                }

                return purchases;
            }
        });

        return get(future);
    }

    public Collection<PurchaseObj> fetchPurchases(Query q) {

        Collection<PurchaseObj> purchases = fetchPurchaseShells(q);

        for (PurchaseObj purchaseObj : purchases) {
            Collection<InvoiceObj> invoiceObjs = fetchInvoices(where(QInvoice.purchase.eq(purchaseObj.getId())));
            purchaseObj.addInvoices(invoiceObjs);
        }

        return purchases;

    }


    public PurchaseObj insertPurchase(Purchase newPurchase) {


        Future<PurchaseObj> future = mDb.submit(new SQLCallable<PurchaseObj>() {
            @Override
            public PurchaseObj call(SQLDatabase db) {
                ContentValues purchaseValues = new ContentValues();

                purchaseValues.put(PURACHES_PURACHESTART, Dates.toDateTimeLong(newPurchase.getPurchaseStart()));
                if (newPurchase.getPurchaseEnd() != null) {
                    purchaseValues.put(PURACHES_PURACHEEND, Dates.toDateTimeLong(newPurchase.getPurchaseEnd()));
                }
                purchaseValues.put(PURACHES_PLATENO, newPurchase.getPlateNo());
                purchaseValues.put(PURACHES_HERDNO, newPurchase.getHerdNo());
                purchaseValues.put(PURACHES_STATE, newPurchase.getState().name());

                db.insertOrThrow(TABLE_NAME_PURCHASES, purchaseValues);
                int newPurchaseId = db.queryMax(TABLE_NAME_PURCHASES, PURACHES_PURACHEID, 1);

                PurchaseObj purchase = new PurchaseObj(newPurchaseId);
                purchase.copyFrom(newPurchase);
                return purchase;
            }
        });

        return get(future);
    }


    public InvoiceObj fetchInvoice(int invoiceId) {

        Collection<InvoiceObj> invoices = fetchInvoices(where(QInvoice.id.eq(invoiceId)));
        return Iterables.getFirst(invoices, null);
    }

    public List<InvoiceObj> fetchInvoices(Query q) {

        Future<List<InvoiceObj>> future = mDb.submit(new SQLCallable<List<InvoiceObj>>() {
            @Override
            public List<InvoiceObj> call(SQLDatabase db) throws Exception {
                List<InvoiceObj> invoices = fetchInvoiceShells(db,q);

                for (InvoiceObj invoice : invoices) {
                    Collection<CowObj> invoiceCows = fetchInvoiceCows(db,invoice.getId());
                    invoice.addCows(invoiceCows);

                    Collection<InvoiceDeductionObj> invoiceDeductions = fetchInvoiceDeductions(db,invoice.getId());
                    invoice.addDeductions(invoiceDeductions);

                    InvoiceHentObj invoiceHent = fetchInvoiceHent(db, invoice.getId());
                    invoice.setHent(invoiceHent);

                }

                return invoices;

            }
        });

        return get(future);


    }

    private List<InvoiceObj> fetchInvoiceShells(SQLDatabase db, Query q) {


        long limit = -1;
        String where = null;
        List<String> orderBy = null;

        if (q != null) {
            SQLiteQuerySerializer serializer = new SQLiteQuerySerializer();
            q.serialize(serializer);

            limit = serializer.getLimit();
            where = serializer.getWhere();
            orderBy = serializer.getOrderBy();
        }

        LinkedList<InvoiceObj> invoices = new LinkedList<InvoiceObj>();
        BKCursor invoicesCursor =
                db.query(TABLE_NAME_INVOICES,
                        new String[]{INVOICES_INVOICEID, INVOICES_VATRATE, INVOICES_PAYWAY,
                                INVOICES_CUSTOMNUMBER, INVOICES_PAYDUEDAYS,
                                INVOICES_INVOICEDT, INVOICES_TRANSACTIONDT, INVOICES_TRANSACTIONPLACE,
                                INVOICES_INVOICETYPE, INVOICES_EXTRAS, INVOICES_PURACHE}, where, orderBy, null, limit);

        int invoiceIdColIndex = invoicesCursor.getColumnIndexOrThrow(INVOICES_INVOICEID);
        int invoiceVatRateColIndex = invoicesCursor.getColumnIndexOrThrow(INVOICES_VATRATE);
        int invoicePayWayColIndex = invoicesCursor.getColumnIndexOrThrow(INVOICES_PAYWAY);
        int invoiceCustomNumberColIndex = invoicesCursor.getColumnIndexOrThrow(INVOICES_CUSTOMNUMBER);
        int invoicePayDueDaysColIndex = invoicesCursor.getColumnIndexOrThrow(INVOICES_PAYDUEDAYS);
        int invoiceDtColIndex = invoicesCursor.getColumnIndexOrThrow(INVOICES_INVOICEDT);
        int invoiceTransactionDtColIndex = invoicesCursor.getColumnIndexOrThrow(INVOICES_TRANSACTIONDT);
        int invoiceTransactionPlace = invoicesCursor.getColumnIndexOrThrow(INVOICES_TRANSACTIONPLACE);
        int invoiceInvoiceType = invoicesCursor.getColumnIndexOrThrow(INVOICES_INVOICETYPE);
        int invoiceExtras = invoicesCursor.getColumnIndex(INVOICES_EXTRAS);
        int invoicePurchaseCol = invoicesCursor.getColumnIndex(INVOICES_PURACHE);

        while (invoicesCursor.moveToNext()) {
            int invoiceId = invoicesCursor.getInt(invoiceIdColIndex);
            InvoiceObj invoice = new InvoiceObj(invoiceId);
            invoice.setCustomNumber(invoicesCursor.getString(invoiceCustomNumberColIndex));
            invoice.setPayDueDays(invoicesCursor.getInt(invoicePayDueDaysColIndex));
            invoice.setPayWay(PayWay.fromString(invoicesCursor.getString(invoicePayWayColIndex)));
            invoice.setPurchase(invoicesCursor.getInt(invoicePurchaseCol));
            invoice.setVatRate(invoicesCursor.getDouble(invoiceVatRateColIndex));
            invoice.setInvoiceType(InvoiceType.fromString(invoicesCursor.getString(invoiceInvoiceType)));
            invoice.setExtras(invoicesCursor.getString(invoiceExtras));
            invoice.setInvoiceDt(invoicesCursor.getDate(invoiceDtColIndex));

            if (!invoicesCursor.isNull(invoiceTransactionDtColIndex))
                invoice.setTransactionDt(invoicesCursor.getDate(invoiceTransactionDtColIndex));

            if (!invoicesCursor.isNull(invoiceTransactionPlace))
                invoice.setTransactionPlace(invoicesCursor.getString(invoiceTransactionPlace));

            invoices.add(invoice);
        }
        return invoices;
    }


    private InvoiceHentObj fetchInvoiceHent(SQLDatabase db, int invoiceId) {

        ContentValues condition = new ContentValues();
        condition.put(INVOICEHENTS_INVOICEID, invoiceId);
        BKCursor hentCursor = null;

        try {
            hentCursor = db.query(TABLE_NAME_INVOICEHENTS,
                    new String[]{INVOICEHENTS_HENTNAME,
                            INVOICEHENTS_ZIP,
                            INVOICEHENTS_CITY,
                            INVOICEHENTS_STREET,
                            INVOICEHENTS_POBOX,
                            INVOICEHENTS_NIP,
                            INVOICEHENTS_HENTNO,
                            INVOICEHENTS_WETNO,
                            INVOICEHENTS_ACCOUNTNO,
                            INVOICEHENTS_BANKNAME,
                            INVOICEHENTS_PESEL,
                            INVOICEHENTS_REGON,
                            INVOICEHENTS_IDNO,
                            INVOICEHENTS_ISSUEPOST,
                            INVOICEHENTS_ISSUEDATE,
                            INVOICEHENTS_CELLPHONE,
                            INVOICEHENTS_EMAIL,
                            INVOICEHENTS_WETLICENCENO,
                            INVOICEHENTS_PHONE},
                    condition);


            int hentHentNameColIndex = hentCursor.getColumnIndexOrThrow(INVOICEHENTS_HENTNAME);
            int hentZipColIndex = hentCursor.getColumnIndexOrThrow(INVOICEHENTS_ZIP);
            int hentCityColIndex = hentCursor.getColumnIndexOrThrow(INVOICEHENTS_CITY);
            int hentStreetColIndex = hentCursor.getColumnIndexOrThrow(INVOICEHENTS_STREET);
            int hentPOBoxColIndex = hentCursor.getColumnIndexOrThrow(INVOICEHENTS_POBOX);
            int hentNIPColIndex = hentCursor.getColumnIndexOrThrow(INVOICEHENTS_NIP);
            int hentHentNoColIndex = hentCursor.getColumnIndexOrThrow(INVOICEHENTS_HENTNO);
            int hentWetNoColIndex = hentCursor.getColumnIndexOrThrow(INVOICEHENTS_WETNO);
            int hentAccountNoColIndex = hentCursor.getColumnIndexOrThrow(INVOICEHENTS_ACCOUNTNO);
            int hentBankNameColIndex = hentCursor.getColumnIndexOrThrow(INVOICEHENTS_BANKNAME);
            int hentPESELColIndex = hentCursor.getColumnIndexOrThrow(INVOICEHENTS_PESEL);
            int hentREGONColIndex = hentCursor.getColumnIndexOrThrow(INVOICEHENTS_REGON);
            int hentIdNoColIndex = hentCursor.getColumnIndexOrThrow(INVOICEHENTS_IDNO);
            int hentIssuePostColIndex = hentCursor.getColumnIndexOrThrow(INVOICEHENTS_ISSUEPOST);
            int hentIssueDateColIndex = hentCursor.getColumnIndexOrThrow(INVOICEHENTS_ISSUEDATE);
            int hentCellPhoneColIndex = hentCursor.getColumnIndexOrThrow(INVOICEHENTS_CELLPHONE);
            int hentEmailColIndex = hentCursor.getColumnIndexOrThrow(INVOICEHENTS_EMAIL);
            int hentWetLicenceNoColIndex = hentCursor.getColumnIndexOrThrow(INVOICEHENTS_WETLICENCENO);
            int hentPhoneColIndex = hentCursor.getColumnIndexOrThrow(INVOICEHENTS_PHONE);

            if (hentCursor.moveToNext()) {

                InvoiceHentObj hent = new InvoiceHentObj();

                hent.setBankAccountNo(IBAN.fromString(hentCursor.getString(hentAccountNoColIndex)));
                hent.setHentName(hentCursor.getString(hentHentNameColIndex));
                hent.setZip(hentCursor.getString(hentZipColIndex));
                hent.setCity(hentCursor.getString(hentCityColIndex));
                hent.setStreet(hentCursor.getString(hentStreetColIndex));
                hent.setPoBox(hentCursor.getString(hentPOBoxColIndex));
                hent.setFiscalNo(hentCursor.getString(hentNIPColIndex));
                hent.setHentNo(EAN.fromString(hentCursor.getString(hentHentNoColIndex)));
                hent.setWetNo(hentCursor.getString(hentWetNoColIndex));
                hent.setPhoneNo(hentCursor.getString(hentPhoneColIndex));
                hent.setBankName(hentCursor.getString(hentBankNameColIndex));
                hent.setPersonalNo(hentCursor.getString(hentPESELColIndex));
                hent.setREGON(hentCursor.getString(hentREGONColIndex));
                hent.setPersonalIdNo(hentCursor.getString(hentIdNoColIndex));
                hent.setIssueDate(hentCursor.getDate(hentIssueDateColIndex));
                hent.setCellPhoneNo(hentCursor.getString(hentCellPhoneColIndex));
                hent.setEmail(hentCursor.getString(hentEmailColIndex));
                hent.setWetLicenceNo(hentCursor.getString(hentWetLicenceNoColIndex));
                hent.setIssuePost(hentCursor.getString(hentIssuePostColIndex));

                return hent;
            }

        } finally {
            if (hentCursor != null) {
                hentCursor.close();
            }
        }


        return null;

    }

    private Collection<InvoiceDeductionObj> fetchInvoiceDeductions(SQLDatabase db, int invoiceId) {


        Collection<InvoiceDeductionObj> result = new LinkedList<InvoiceDeductionObj>();
        ContentValues condition = new ContentValues();
        condition.put(INVOICEDEDUCTIONS_INVOICEID, invoiceId);
        BKCursor cursor = null;

        try {
            cursor = db.query(TABLE_NAME_INVOICEDEDUCTIONS,
                    new String[]{INVOICEDEDUCTIONS_CODE,
                            INVOICEDEDUCTIONS_FRACTION,
                            INVOICEDEDUCTIONS_REASON,
                            INVOICEDEDUCTIONS_ENABLED},
                    condition);


            int deductionCodeColIndex = cursor.getColumnIndexOrThrow(INVOICEDEDUCTIONS_CODE);
            int deductionFractionColIndex = cursor.getColumnIndexOrThrow(INVOICEDEDUCTIONS_FRACTION);
            int deductionReasonColIndex = cursor.getColumnIndexOrThrow(INVOICEDEDUCTIONS_REASON);
            int deductionEnabledColIndex = cursor.getColumnIndexOrThrow(INVOICEDEDUCTIONS_ENABLED);

            while (cursor.moveToNext()) {

                InvoiceDeductionObj deduction = new InvoiceDeductionObj();

                deduction.setCode(cursor.getString(deductionCodeColIndex));
                deduction.setFraction(cursor.getDouble(deductionFractionColIndex));
                deduction.setReason(cursor.getString(deductionReasonColIndex));
                deduction.setEnabled(cursor.getInt(deductionEnabledColIndex) != 0);
                result.add(deduction);
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;

    }

    private Collection<CowObj> fetchInvoiceCows(SQLDatabase db,int invoiceId) {
        return fetchCows(db,where(QCow.invoiceId.eq(invoiceId)));
    }

    public CowObj fetchCow(EAN cowNo) {
        Collection<CowObj> cows = fetchCows(where(QCow.cowNo.eq(cowNo.toString())));
        return Iterables.getFirst(cows, null);
    }

    public Collection<CowObj> fetchCows(Query query) {

        Future<Collection<CowObj>> future = mDb.submit(new SQLCallable<Collection<CowObj>>() {
            @Override
            public Collection<CowObj> call(SQLDatabase db) throws Exception {
                return fetchCows(db, query);
            }
        });

        return get(future);
    }

    @NonNull
    private Collection<CowObj> fetchCows(SQLDatabase db, Query query) {
        LinkedList<CowObj> cows = new LinkedList<CowObj>();
        String where = null;
        long limit = -1;

        if (query != null) {
            SQLiteQuerySerializer serializer = new SQLiteQuerySerializer();
            query.serialize(serializer);
            where = serializer.getWhere();
            limit = serializer.getLimit();
        }


        BKCursor cowsCursor = null;

        try {
            cowsCursor = db.query(TABLE_NAME_COWS,
                    new String[]{COWS_COWID, COWS_EAN, COWS_SEX, COWS_STOCK, COWS_CLASS,
                            COWS_WEIGHT, COWS_PRICE, COWS_LATITUDE, COWS_LONGITUDE,
                            COWS_FIRSTOWNER, COWS_PASSPORTNO, COWS_PASSPORTISSUEDT,
                            COWS_HEALTHCERTNO, COWS_MOTHEREAN, COWS_BIRTHPLACE,
                            COWS_BIRTHDT, COWS_INVOICE}, where, limit
            );


            int cowsIdColIndex = cowsCursor.getColumnIndexOrThrow(COWS_COWID);
            int cowsEanColIndex = cowsCursor.getColumnIndexOrThrow(COWS_EAN);
            int cowSexColIndex = cowsCursor.getColumnIndexOrThrow(COWS_SEX);
            int cowsStockColIndex = cowsCursor.getColumnIndexOrThrow(COWS_STOCK);
            int cowsClassColIndex = cowsCursor.getColumnIndexOrThrow(COWS_CLASS);
            int cowsWeightColIndex = cowsCursor.getColumnIndexOrThrow(COWS_WEIGHT);
            int cowsPriceColIndex = cowsCursor.getColumnIndexOrThrow(COWS_PRICE);
            int cowsLatitudeColIndex = cowsCursor.getColumnIndexOrThrow(COWS_LATITUDE);
            int cowLongitudeColIndex = cowsCursor.getColumnIndexOrThrow(COWS_LONGITUDE);
            int cowsFstOwnerColIndex = cowsCursor.getColumnIndexOrThrow(COWS_FIRSTOWNER);
            int cowsPassNoColIndex = cowsCursor.getColumnIndexOrThrow(COWS_PASSPORTNO);
            int cowsPassIssueDtColIndex = cowsCursor.getColumnIndexOrThrow(COWS_PASSPORTISSUEDT);
            int cowsHealthCertNoColIndex = cowsCursor.getColumnIndexOrThrow(COWS_HEALTHCERTNO);
            int cowsMotherNoColIndex = cowsCursor.getColumnIndexOrThrow(COWS_MOTHEREAN);
            int cowsBirthPlaceColIndex = cowsCursor.getColumnIndexOrThrow(COWS_BIRTHPLACE);
            int cowsBirthDtColIndex = cowsCursor.getColumnIndexOrThrow(COWS_BIRTHDT);
            int cowsInvoiceColIndex = cowsCursor.getColumnIndexOrThrow(COWS_INVOICE);

            while (cowsCursor.moveToNext()) {
                CowObj cow = new CowObj(cowsCursor.getInt(cowsIdColIndex));
                cow.setCowNo(EAN.fromString(cowsCursor.getString(cowsEanColIndex)));
                cow.setPrice(cowsCursor.getDouble(cowsPriceColIndex));
                cow.setSex(CowSex.fromInt(cowsCursor.getInt(cowSexColIndex)));
                cow.setStockCd(cowsCursor.getString(cowsStockColIndex));
                cow.setClassCd(cowsCursor.getString(cowsClassColIndex));
                cow.setWeight(cowsCursor.getInt(cowsWeightColIndex));

                cow.setInvoice(cowsCursor.getInt(cowsInvoiceColIndex));

                if (!cowsCursor.isNull(cowsLatitudeColIndex))
                    cow.setLatitude(cowsCursor.getDouble(cowsLatitudeColIndex));

                if (!cowsCursor.isNull(cowLongitudeColIndex))
                    cow.setLongitude(cowsCursor.getDouble(cowLongitudeColIndex));

                if (!cowsCursor.isNull(cowsFstOwnerColIndex))
                    cow.setFirstOwner(EAN.fromString(cowsCursor.getString(cowsFstOwnerColIndex)));

                if (!cowsCursor.isNull(cowsPassNoColIndex))
                    cow.setPassportNo(cowsCursor.getString(cowsPassNoColIndex));

                if (!cowsCursor.isNull(cowsPassIssueDtColIndex))
                    cow.setPassportIssueDt(cowsCursor.getDate(cowsPassIssueDtColIndex));

                if (!cowsCursor.isNull(cowsHealthCertNoColIndex))
                    cow.setHealthCertNo(cowsCursor.getString(cowsHealthCertNoColIndex));

                if (!cowsCursor.isNull(cowsMotherNoColIndex)) {
                    EAN motherNo = EAN.fromString(cowsCursor.getString(cowsMotherNoColIndex));
                    cow.setMotherNo(motherNo);
                }

                if (!cowsCursor.isNull(cowsBirthPlaceColIndex))
                    cow.setBirthPlace(cowsCursor.getString(cowsBirthPlaceColIndex));

                if (!cowsCursor.isNull(cowsBirthDtColIndex))
                    cow.setBirthDt(cowsCursor.getDate(cowsBirthDtColIndex));

                cows.add(cow);
            }
            return cows;
        } finally {
            if (cowsCursor != null) {
                cowsCursor.close();
            }
        }
    }


    private ContentValues createInvoiceValues(Invoice invoice) {
        ContentValues invoiceValues = new ContentValues();
        PayWay payWay = invoice.getPayWay();
        if (payWay != null) {
            invoiceValues.put(INVOICES_PAYWAY, payWay.getPayWayId());
        }

        invoiceValues.put(INVOICES_CUSTOMNUMBER, invoice.getCustomNumber());
        Integer payDueDays = invoice.getPayDueDays();
        if (payDueDays != null) {
            invoiceValues.put(INVOICES_PAYDUEDAYS, payDueDays);
        }

        invoiceValues.put(INVOICES_PURACHE, invoice.getPurchase());
        invoiceValues.put(INVOICES_VATRATE, invoice.getVatRate());
        invoiceValues.put(INVOICES_TRANSACTIONPLACE, invoice.getTransactionPlace());
        invoiceValues.put(INVOICES_TRANSACTIONDT, dateTimeLongOrNull(invoice.getTransactionDt()));
        invoiceValues.put(INVOICES_INVOICEDT, dateTimeLongOrNull(invoice.getInvoiceDt()));
        InvoiceType invoiceType = invoice.getInvoiceType();
        if (invoiceType != null) {
            invoiceValues.put(INVOICES_INVOICETYPE, invoiceType.getInvoiceTypeId());
        }

        invoiceValues.put(INVOICES_EXTRAS, invoice.getExtras());

        return invoiceValues;
    }


    public InvoiceObj insertInvoice(Invoice invoice) {

        Future<InvoiceObj> future = mDb.submitTransaction(new SQLCallable<InvoiceObj>() {
            @Override
            public InvoiceObj call(SQLDatabase db) {
                ContentValues invoiceValues = createInvoiceValues(invoice);
                db.insertOrThrow(TABLE_NAME_INVOICES, invoiceValues);
                int newInvoiceId = db.queryMax(TABLE_NAME_INVOICES, INVOICES_INVOICEID, 1);
                InvoiceObj newInvoice = new InvoiceObj(newInvoiceId);
                newInvoice.copyFrom(invoice);

                for (Cow cow : invoice.getCows()) {
                    int newCowId = insertInvoiceCow(db, newInvoiceId, new CowDetails(cow));
                    CowObj newCow = new CowObj(newCowId);
                    newCow.copyFrom(cow);
                    newCow.setInvoice(newInvoiceId);
                    newInvoice.addCow(newCow);
                }

                for (InvoiceDeduction deduction : invoice.getDeductions()) {
                    insertDeduction(db, newInvoiceId, deduction);
                    InvoiceDeductionObj newDeduction = new InvoiceDeductionObj();
                    newDeduction.copyFrom(deduction);
                    newInvoice.addDeduction(newDeduction);
                }

                insertInvoiceHent(db, newInvoiceId, invoice.getInvoiceHent());

                return newInvoice;

            }
        });

        return get(future);

    }

    private void insertInvoiceHent(SQLDatabase db, int invoiceId, InvoiceHent hent) {
        ContentValues invoiceHentValues = createInvoiceHentValues(hent);
        invoiceHentValues.put(INVOICEHENTS_INVOICEID, invoiceId);
        db.insertOrThrow(TABLE_NAME_INVOICEHENTS, invoiceHentValues);

    }

    private ContentValues createInvoiceHentValues(InvoiceHent hent) {
        ContentValues hentValues = new ContentValues();
        hentValues.put(INVOICEHENTS_HENTNAME, hent.getHentName());
        hentValues.put(INVOICEHENTS_ZIP, hent.getZip());
        hentValues.put(INVOICEHENTS_CITY, hent.getCity());
        hentValues.put(INVOICEHENTS_STREET, hent.getStreet());
        hentValues.put(INVOICEHENTS_POBOX, hent.getPoBox());
        hentValues.put(INVOICEHENTS_NIP, Strings.emptyToNull(hent.getFiscalNo()));
        hentValues.put(INVOICEHENTS_HENTNO, hent.getHentNo().toString());
        hentValues.put(INVOICEHENTS_WETNO, Strings.emptyToNull(hent.getWetNo()));
        hentValues.put(INVOICEHENTS_PHONE, Strings.emptyToNull(hent.getPhoneNo()));
        hentValues.put(INVOICEHENTS_ACCOUNTNO, ibanOrNull(hent.getBankAccountNo()));
        hentValues.put(INVOICEHENTS_BANKNAME, Strings.emptyToNull(hent.getBankName()));
        hentValues.put(INVOICEHENTS_PESEL, Strings.emptyToNull(hent.getPersonalNo()));
        hentValues.put(INVOICEHENTS_REGON, Strings.emptyToNull(hent.getREGON()));
        hentValues.put(INVOICEHENTS_IDNO, Strings.emptyToNull(hent.getPersonalIdNo()));
        hentValues.put(INVOICEHENTS_ISSUEPOST, Strings.emptyToNull(hent.getIssuePost()));
        hentValues.put(INVOICEHENTS_ISSUEDATE, dateTimeLongOrNull(hent.getIssueDate()));
        hentValues.put(INVOICEHENTS_CELLPHONE, Strings.emptyToNull(hent.getCellPhoneNo()));
        hentValues.put(INVOICEHENTS_EMAIL, Strings.emptyToNull(hent.getEmail()));
        hentValues.put(INVOICEHENTS_WETLICENCENO, Strings.emptyToNull(hent.getWetLicenceNo()));
        return hentValues;
    }

    private void insertDeduction(SQLDatabase db, int invoiceId, InvoiceDeduction deduction) {
        ContentValues deductionValues = createDeductionValues(deduction);
        deductionValues.put(INVOICEDEDUCTIONS_INVOICEID, invoiceId);
        db.insertOrThrow(TABLE_NAME_INVOICEDEDUCTIONS, deductionValues);
    }

    private ContentValues createDeductionValues(InvoiceDeduction deduction) {
        ContentValues deductionValues = new ContentValues();
        deductionValues.put(INVOICEDEDUCTIONS_CODE, deduction.getCode());
        deductionValues.put(INVOICEDEDUCTIONS_FRACTION, deduction.getFraction());
        deductionValues.put(INVOICEDEDUCTIONS_REASON, deduction.getReason());
        deductionValues.put(INVOICEDEDUCTIONS_ENABLED, deduction.isEnabled());
        return deductionValues;
    }

    private int insertInvoiceCow(SQLDatabase db, int invoiceId, CowDetails cowDetails) {

        ContentValues cowValues = createCowValues(cowDetails);
        cowValues.put(COWS_INVOICE, invoiceId);
        db.insertOrThrow(TABLE_NAME_COWS, cowValues);
        return db.queryMax(TABLE_NAME_COWS, COWS_COWID, 1);
    }

    private ContentValues createCowValues(CowDetails details) {
        ContentValues cowValues = new ContentValues();
        cowValues.put(COWS_EAN, details.getCowNo().toString());
        cowValues.put(COWS_SEX, details.getSex().getSexId());
        cowValues.put(COWS_STOCK, details.getStockCd());
        cowValues.put(COWS_CLASS, details.getClassCd());
        cowValues.put(COWS_WEIGHT, details.getWeight());
        cowValues.put(COWS_PRICE, details.getPrice());
        cowValues.put(COWS_LATITUDE, details.getLatitude());
        cowValues.put(COWS_LONGITUDE, details.getLongitude());
        cowValues.put(COWS_FIRSTOWNER, nullOrToString(details.getFirstOwner()));
        cowValues.put(COWS_PASSPORTNO, details.getPassportNo());
        cowValues.put(COWS_PASSPORTISSUEDT, dateTimeLongOrNull(details.getPassportIssueDt()));
        cowValues.put(COWS_HEALTHCERTNO, details.getHealthCertNo());
        cowValues.put(COWS_MOTHEREAN, nullOrToString(details.getMotherNo()));
        cowValues.put(COWS_BIRTHPLACE, details.getBirthPlace());
        cowValues.put(COWS_BIRTHDT, dateTimeLongOrNull(details.getBirthDt()));
        return cowValues;
    }


    public void updateInvoice(Invoice invoice) {

        Future<Void> future = mDb.submitTransaction(new SQLCallable<Void>() {
            @Override
            public Void call(SQLDatabase db) {
                ContentValues invoiceValues = createInvoiceValues(invoice);
                int invoiceId = invoice.getId();
                db.updateOrThrow(TABLE_NAME_INVOICES, invoiceValues, INVOICES_INVOICEID + '=' + invoiceId);

                deleteInvoiceCows(db, invoiceId);
                deleteInvoiceDeductions(db, invoiceId);
                deleteInvoiceHent(db, invoiceId);

                for (Cow cow : invoice.getCows()) {
                    insertInvoiceCow(db, invoiceId, new CowDetails(cow));
                }

                for (InvoiceDeduction deduction : invoice.getDeductions()) {
                    insertDeduction(db, invoiceId, deduction);
                }

                insertInvoiceHent(db, invoiceId, invoice.getInvoiceHent());
                return null;
            }
        });

        get(future);


    }

    private void deleteInvoiceHent(SQLDatabase db, int invoiceId) {
        ContentValues where = new ContentValues();
        where.put(INVOICEHENTS_INVOICEID, invoiceId);
        db.deleteOrThrow(TABLE_NAME_INVOICEHENTS, where);
    }

    private void deleteInvoiceDeductions(SQLDatabase db, int invoiceId) {
        ContentValues where = new ContentValues();
        where.put(INVOICEDEDUCTIONS_INVOICEID, invoiceId);
        db.deleteOrThrow(TABLE_NAME_INVOICEDEDUCTIONS, where);
    }

    private void deleteInvoiceCows(SQLDatabase db, int invoiceId) {
        ContentValues where = new ContentValues();
        where.put(COWS_INVOICE, invoiceId);
        db.deleteOrThrow(TABLE_NAME_COWS, where);
    }

    private ContentValues createPurchaseDetailsValues(PurchaseDetails details) {
        ContentValues values = new ContentValues();

        values.put(PURACHES_PURACHESTART, Dates.toDateTimeLong(details.getPurchaseStart()));
        if (details.getPurchaseEnd() != null) {
            values.put(PURACHES_PURACHEEND, Dates.toDateTimeLong(details.getPurchaseEnd()));
        }
        values.put(PURACHES_PLATENO, details.getPlateNo());
        values.put(PURACHES_HERDNO, details.getHerdNo());
        values.put(PURACHES_STATE, nullOrToString(details.getState()));

        return values;

    }

    public void updatePurchaseState(int purchaseId, PurchaseState state) {

        Future<Void> future = mDb.submit(new SQLCallable<Void>() {
            @Override
            public Void call(SQLDatabase db) {
                ContentValues values = new ContentValues();
                values.put(PURACHES_STATE, nullOrToString(state));
                db.updateOrThrow(TABLE_NAME_PURCHASES, values, PURACHES_PURACHEID + '=' + purchaseId);
                return null;
            }
        });

        get(future);
    }

    public void updatePurchaseDetails(int purchaseId, PurchaseDetails purchaseDetails) {

        Future<Void> future = mDb.submit(new SQLCallable<Void>() {
            @Override
            public Void call(SQLDatabase db) {
                ContentValues detailsValues = createPurchaseDetailsValues(purchaseDetails);
                db.updateOrThrow(TABLE_NAME_PURCHASES, detailsValues, PURACHES_PURACHEID + '=' + purchaseId);
                return null;
            }
        });

        get(future);
    }

    public void deleteInvoice(int invoiceId) {

        Future<Void> future = mDb.submitTransaction(new SQLCallable<Void>() {
            @Override
            public Void call(SQLDatabase db) {
                deleteInvoiceCows(db, invoiceId);
                deleteInvoiceDeductions(db, invoiceId);
                deleteInvoiceHent(db, invoiceId);

                ContentValues condition = new ContentValues();
                condition.put(INVOICES_INVOICEID, invoiceId);
                db.deleteOrThrow(TABLE_NAME_INVOICES, condition);
                return null;
            }
        });

        get(future);

    }


    public Collection<InvoiceDetails> fetchInvoiceDetails(Query query) {

        Future<Collection<InvoiceDetails>> future = mDb.submit(new SQLCallable<Collection<InvoiceDetails>>() {
            @Override
            public Collection<InvoiceDetails> call(SQLDatabase db) throws Exception {
                List<InvoiceObj> invoiceShells = fetchInvoiceShells(db,query);
                return Lists.transform(invoiceShells, new Function<InvoiceObj, InvoiceDetails>() {
                    @Override
                    public InvoiceDetails apply(InvoiceObj invoiceObj) {
                        return invoiceObj.getDetails();
                    }
                });
            }
        });

        return get(future);

    }

}
