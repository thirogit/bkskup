package com.bk.bkskup3.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.bk.bkskup3.dao.q.QDeduction;
import com.bk.bkskup3.db.SQLCallable;
import com.bk.bkskup3.db.SQLDatabase;
import com.bk.bkskup3.db.SQLDatabaseQueue;
import com.bk.bkskup3.model.CowClass;
import com.bk.bkskup3.model.CowClassObj;
import com.bk.bkskup3.model.CowSex;
import com.bk.bkskup3.model.DeductionDefinition;
import com.bk.bkskup3.model.DeductionDefinitionObj;
import com.bk.bkskup3.model.Herd;
import com.bk.bkskup3.model.HerdObj;
import com.bk.bkskup3.model.Stock;
import com.bk.bkskup3.model.StockObj;
import com.google.common.collect.Iterables;
import com.mysema.query.support.Query;
import com.mysema.serializer.SQLiteQuerySerializer;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.Future;

import static com.mysema.query.support.QueryBuilder.where;

public class DefinitionsStore extends AbstractSQLStore {

    public static final String TABLE_NAME_HERDS = "HERDS";
    public static final String HERDS_HERDID = "HERDID";
    public static final String HERDS_NUMBER = "HERDNUMBER";
    public static final String HERDS_ZIP = "ZIP";
    public static final String HERDS_CITY = "CITY";
    public static final String HERDS_STREET = "STREET";
    public static final String HERDS_POBOX = "POBOX";

    public static final String TABLE_NAME_STOCKS = "STOCKS";
    public static final String STOCKS_STOCKID = "STOCKID";
    public static final String STOCKS_NAME = "NAME";
    public static final String STOCKS_CODE = "CODE";

    public static final String TABLE_NAME_CLASSES = "CLASSES";
    public static final String CLASSES_CLASSID = "CLASSID";
    public static final String CLASSES_NAME = "NAME";
    public static final String CLASSES_CODE = "CODE";
    public static final String CLASSES_PREDEFSEX = "PREDEFSEX";
    public static final String CLASSES_PRICEPERKG = "PRICEPERKG";

    public static final String TABLE_NAME_DEDUCTIONS = "DEDUCTION_DEFINITIONS";
    public static final String DEDUCTIONS_DEDUCTIONID = "DEDUCTION_ID";
    public static final String DEDUCTIONS_REASON = "REASON";
    public static final String DEDUCTIONS_CODE = "CODE";
    public static final String DEDUCTIONS_FRACTION = "FRACTION";
    public static final String DEDUCTIONS_ALWAYS = "ALWAYS";

    public DefinitionsStore(SQLDatabaseQueue mDb) {
        super(mDb);
    }

    public Collection<HerdObj> fetchAllHerds() {
        return fetchHerds(null);
    }


    public Collection<HerdObj> fetchHerds(Query q) {

        Future<Collection<HerdObj>> future = mDb.submit(new SQLCallable<Collection<HerdObj>>() {
            @Override
            public Collection<HerdObj> call(SQLDatabase db) {
                Collection<HerdObj> herds = new LinkedList<>();
                Cursor herdCursor = null;

                try {

                    String whereClause = null;
                    long limit = -1;

                    if (q != null) {

                        SQLiteQuerySerializer serializer = new SQLiteQuerySerializer();
                        q.serialize(serializer);
                        whereClause = serializer.getWhere();
                        limit = serializer.getLimit();
                    }

                    herdCursor = db.query(TABLE_NAME_HERDS,
                            new String[]{HERDS_HERDID,
                                    HERDS_ZIP,
                                    HERDS_CITY,
                                    HERDS_STREET,
                                    HERDS_POBOX,
                                    HERDS_NUMBER}, whereClause, limit
                    );

                    int herdIdColIndex = herdCursor.getColumnIndexOrThrow(HERDS_HERDID);
                    int herdZipColIndex = herdCursor.getColumnIndexOrThrow(HERDS_ZIP);
                    int herdCityColIndex = herdCursor.getColumnIndexOrThrow(HERDS_CITY);
                    int herdStreetColIndex = herdCursor.getColumnIndexOrThrow(HERDS_STREET);
                    int herdPOBoxColIndex = herdCursor.getColumnIndexOrThrow(HERDS_POBOX);
                    int herdNumberColIndex = herdCursor.getColumnIndexOrThrow(HERDS_NUMBER);

                    while (herdCursor.moveToNext()) {
                        HerdObj herd = new HerdObj(herdCursor.getInt(herdIdColIndex));

                        herd.setZip(herdCursor.getString(herdZipColIndex));
                        herd.setCity(herdCursor.getString(herdCityColIndex));
                        herd.setStreet(herdCursor.getString(herdStreetColIndex));
                        herd.setPoBox(herdCursor.getString(herdPOBoxColIndex));
                        herd.setHerdNo(herdCursor.getInt(herdNumberColIndex));

                        herds.add(herd);
                    }
                } finally {
                    if (herdCursor != null) {
                        herdCursor.close();
                    }
                }
                return herds;
            }
        });

        return get(future);
    }


    private ContentValues createHerdValues(Herd herd) {
        ContentValues hentValues = new ContentValues();
        hentValues.put(HERDS_ZIP, herd.getZip());
        hentValues.put(HERDS_CITY, herd.getCity());
        hentValues.put(HERDS_STREET, herd.getStreet());
        hentValues.put(HERDS_POBOX, herd.getPoBox());
        hentValues.put(HERDS_NUMBER, herd.getHerdNo());
        return hentValues;
    }

    public void updateHerd(Herd herd) {

        Future<Void> future = mDb.submit(new SQLCallable<Void>() {
            @Override
            public Void call(SQLDatabase db) {
                ContentValues herdValues = createHerdValues(herd);
                ContentValues where = new ContentValues();
                where.put(HERDS_HERDID, herd.getId());
                db.updateOrThrow(TABLE_NAME_HERDS, herdValues, where);
                return null;
            }
        });

        get(future);

    }

    public HerdObj insertHerd(Herd newHerd) {

        Future<HerdObj> future = mDb.submit(new SQLCallable<HerdObj>() {
            @Override
            public HerdObj call(SQLDatabase db) {
                ContentValues herdValues = createHerdValues(newHerd);

                db.insertOrThrow(TABLE_NAME_HERDS, herdValues);

                int newHentId = db.queryMax(TABLE_NAME_HERDS, HERDS_HERDID, 1);
                HerdObj insertedHent = new HerdObj(newHentId);
                insertedHent.copyFrom(newHerd);
                return insertedHent;
            }
        });

        return get(future);
    }


    public void deleteHerd(int herdId) {

        Future<Void> future = mDb.submit(new SQLCallable<Void>() {
            @Override
            public Void call(SQLDatabase db) {
                ContentValues where = new ContentValues();
                where.put(HERDS_HERDID, herdId);
                db.deleteOrThrow(TABLE_NAME_HERDS, where);
                return null;
            }
        });

        get(future);

    }

    public Collection<StockObj> fetchAllStocks() {
        return fetchStocks(null);
    }

    public Collection<StockObj> fetchStocks(Query q) {

        Future<Collection<StockObj>> future = mDb.submit(new SQLCallable<Collection<StockObj>>() {
            @Override
            public Collection<StockObj> call(SQLDatabase db) {
                LinkedList<StockObj> stocks = new LinkedList<StockObj>();
                Cursor stockCursor = null;
                try {

                    String whereClause = null;
                    long limit = -1;

                    if (q != null) {

                        SQLiteQuerySerializer serializer = new SQLiteQuerySerializer();
                        q.serialize(serializer);
                        whereClause = serializer.getWhere();
                        limit = serializer.getLimit();
                    }

                    stockCursor = db.query(TABLE_NAME_STOCKS,
                            new String[]{STOCKS_STOCKID,
                                    STOCKS_NAME, STOCKS_CODE}, whereClause, limit
                    );

                    int stockIdColIndex = stockCursor.getColumnIndexOrThrow(STOCKS_STOCKID);
                    int stockNameColIndex = stockCursor.getColumnIndexOrThrow(STOCKS_NAME);
                    int stockCodeColIndex = stockCursor.getColumnIndexOrThrow(STOCKS_CODE);

                    while (stockCursor.moveToNext()) {
                        StockObj stock = new StockObj(stockCursor.getInt(stockIdColIndex));

                        stock.setStockCode(stockCursor.getString(stockCodeColIndex));
                        if (!stockCursor.isNull(stockNameColIndex)) {
                            stock.setStockName(stockCursor.getString(stockNameColIndex));
                        }

                        stocks.add(stock);
                    }
                } finally {
                    if (stockCursor != null) {
                        stockCursor.close();
                    }
                }
                return stocks;
            }
        });

        return get(future);


    }

    public void deleteStock(int stockId) {

        Future<Void> future = mDb.submit(new SQLCallable<Void>() {
            @Override
            public Void call(SQLDatabase db) {
                ContentValues condition = new ContentValues();
                condition.put(STOCKS_STOCKID, stockId);

                db.deleteOrThrow(TABLE_NAME_STOCKS, condition);
                return null;
            }
        });

        get(future);


    }

    public Collection<CowClassObj> fetchAllClasses() {
        return fetchClasses(null);
    }

    public Collection<CowClassObj> fetchClasses(Query query) {

        Future<Collection<CowClassObj>> future = mDb.submit(new SQLCallable<Collection<CowClassObj>>() {
            @Override
            public Collection<CowClassObj> call(SQLDatabase db) throws Exception {
                LinkedList<CowClassObj> classes = new LinkedList<CowClassObj>();
                Cursor classCursor = null;
                try {

                    String whereClause = null;
                    long limit = -1;

                    if (query != null) {

                        SQLiteQuerySerializer serializer = new SQLiteQuerySerializer();
                        query.serialize(serializer);
                        whereClause = serializer.getWhere();
                        limit = serializer.getLimit();
                    }

                    classCursor = db.query(TABLE_NAME_CLASSES,
                            new String[]{CLASSES_CLASSID,
                                    CLASSES_NAME, CLASSES_CODE, CLASSES_PREDEFSEX, CLASSES_PRICEPERKG}, whereClause, limit);

                    int classIdColIndex = classCursor.getColumnIndexOrThrow(CLASSES_CLASSID);
                    int classNameColIndex = classCursor.getColumnIndexOrThrow(CLASSES_NAME);
                    int classCodeColIndex = classCursor.getColumnIndexOrThrow(CLASSES_CODE);
                    int classPreDefSexColIndex = classCursor.getColumnIndexOrThrow(CLASSES_PREDEFSEX);
                    int classPricePerKgColIndex = classCursor.getColumnIndexOrThrow(CLASSES_PRICEPERKG);

                    while (classCursor.moveToNext()) {
                        CowClassObj classObj = new CowClassObj(classCursor.getInt(classIdColIndex));

                        classObj.setClassCode(classCursor.getString(classCodeColIndex));
                        if (!classCursor.isNull(classNameColIndex)) {
                            classObj.setClassName(classCursor.getString(classNameColIndex));
                        }

                        if (!classCursor.isNull(classPreDefSexColIndex)) {
                            classObj.setPredefSex(CowSex.fromInt(classCursor.getInt(classPreDefSexColIndex)));
                        }

                        if (!classCursor.isNull(classPricePerKgColIndex)) {
                            classObj.setPricePerKg(classCursor.getDouble(classPricePerKgColIndex));
                        }

                        classes.add(classObj);
                    }
                } finally {
                    if (classCursor != null) {
                        classCursor.close();
                    }
                }
                return classes;
            }
        });

        return get(future);


    }

    public void deleteClass(int cowClassId) {


        Future<Void> future = mDb.submit(new SQLCallable<Void>() {
            @Override
            public Void call(SQLDatabase db) {
                ContentValues condition = new ContentValues();
                condition.put(CLASSES_CLASSID, cowClassId);

                db.deleteOrThrow(TABLE_NAME_CLASSES, condition);
                return null;
            }
        });

        get(future);


    }

    private ContentValues createCowClassValues(CowClass cowClass) {
        ContentValues values = new ContentValues();
        values.put(CLASSES_CODE, cowClass.getClassCode());
        values.put(CLASSES_NAME, cowClass.getClassName());

        CowSex predefSex = cowClass.getPredefSex();
        if (predefSex == null)
            predefSex = CowSex.NONE;

        values.put(CLASSES_PREDEFSEX, predefSex.getSexId());
        values.put(CLASSES_PRICEPERKG, cowClass.getPricePerKg());

        return values;
    }

    public CowClassObj insertClass(CowClass classObj) {

        Future<CowClassObj> insertFuture = mDb.submitTransaction(new SQLCallable<CowClassObj>() {
            @Override
            public CowClassObj call(SQLDatabase db) {
                db.insertOrThrow(TABLE_NAME_CLASSES, createCowClassValues(classObj));
                int newClassId = db.queryMax(TABLE_NAME_CLASSES, CLASSES_CLASSID, 1);

                CowClassObj newClass = new CowClassObj(newClassId);
                newClass.copyFrom(classObj);

                return newClass;
            }
        });

        return get(insertFuture);

    }

    public void updateClass(CowClass cowClass) {
        Future<Void> future = mDb.submit(new SQLCallable<Void>() {
            @Override
            public Void call(SQLDatabase db) {
                ContentValues classValues = createCowClassValues(cowClass);
                ContentValues where = new ContentValues();
                where.put(CLASSES_CLASSID, cowClass.getId());
                db.updateOrThrow(TABLE_NAME_CLASSES, classValues, where);
                return null;
            }
        });

        get(future);



    }

    public void updateStock(Stock stock) {


        Future<Void> future = mDb.submit(new SQLCallable<Void>() {
            @Override
            public Void call(SQLDatabase db) {
                ContentValues stockValues = createStockValues(stock);
                ContentValues where = new ContentValues();
                where.put(STOCKS_STOCKID, stock.getId());
                db.updateOrThrow(TABLE_NAME_STOCKS, stockValues, where);
                return null;
            }
        });

        get(future);


    }

    public StockObj insertStock(Stock stock) {

        Future<StockObj> future = mDb.submit(new SQLCallable<StockObj>() {
            @Override
            public StockObj call(SQLDatabase db) {
                db.insertOrThrow(TABLE_NAME_STOCKS, createStockValues(stock));
                int newStockId = db.queryMax(TABLE_NAME_STOCKS, STOCKS_STOCKID, 1);

                StockObj newStock = new StockObj(newStockId);
                newStock.copyFrom(stock);

                return newStock;
            }
        });

        return get(future);


    }

    private ContentValues createStockValues(Stock stock) {
        ContentValues values = new ContentValues();
        values.put(STOCKS_CODE, stock.getStockCode());
        values.put(STOCKS_NAME, stock.getStockName());
        return values;
    }

    public Collection<DeductionDefinitionObj> fetchAllDeductions() {
        return fetchDeductions(null);
    }

    public Collection<DeductionDefinitionObj> fetchDeductions(Query query) {


        Future<Collection<DeductionDefinitionObj>> future = mDb.submit(new SQLCallable<Collection<DeductionDefinitionObj>>() {
            @Override
            public Collection<DeductionDefinitionObj> call(SQLDatabase db) throws Exception {
                LinkedList<DeductionDefinitionObj> deductions = new LinkedList<DeductionDefinitionObj>();
                Cursor deductionCursor = null;
                try {

                    String whereClause = null;
                    long limit = -1;

                    if (query != null) {

                        SQLiteQuerySerializer serializer = new SQLiteQuerySerializer();
                        query.serialize(serializer);
                        whereClause = serializer.getWhere();
                        limit = serializer.getLimit();
                    }

                    deductionCursor = db.query(TABLE_NAME_DEDUCTIONS,
                            new String[]{DEDUCTIONS_DEDUCTIONID,
                                    DEDUCTIONS_CODE, DEDUCTIONS_REASON, DEDUCTIONS_FRACTION, DEDUCTIONS_ALWAYS}, whereClause, limit);

                    int deductionIdColIndex = deductionCursor.getColumnIndexOrThrow(DEDUCTIONS_DEDUCTIONID);
                    int deductionCodeColIndex = deductionCursor.getColumnIndexOrThrow(DEDUCTIONS_CODE);
                    int deductionReasonColIndex = deductionCursor.getColumnIndexOrThrow(DEDUCTIONS_REASON);
                    int deductionFractionColIndex = deductionCursor.getColumnIndexOrThrow(DEDUCTIONS_FRACTION);
                    int deductionAlwaysColIndex = deductionCursor.getColumnIndexOrThrow(DEDUCTIONS_ALWAYS);

                    while (deductionCursor.moveToNext()) {
                        DeductionDefinitionObj deductionObj = new DeductionDefinitionObj(deductionCursor.getInt(deductionIdColIndex));

                        deductionObj.setCode(deductionCursor.getString(deductionCodeColIndex));
                        deductionObj.setReason(deductionCursor.getString(deductionReasonColIndex));
                        deductionObj.setFraction(deductionCursor.getDouble(deductionFractionColIndex));
                        deductionObj.setEnabledByDefault(deductionCursor.getInt(deductionAlwaysColIndex) != 0);

                        deductions.add(deductionObj);
                    }
                } finally {
                    if (deductionCursor != null) {
                        deductionCursor.close();
                    }
                }
                return deductions;
            }
        });

        return get(future);


    }

    public DeductionDefinitionObj insertDeduction(DeductionDefinition deduction) {

        Future<DeductionDefinitionObj> future = mDb.submit(new SQLCallable<DeductionDefinitionObj>() {
            @Override
            public DeductionDefinitionObj call(SQLDatabase db) {
                db.insertOrThrow(TABLE_NAME_DEDUCTIONS, createDeductionValues(deduction));
                int newDeductionId = db.queryMax(TABLE_NAME_DEDUCTIONS, DEDUCTIONS_DEDUCTIONID, 1);

                DeductionDefinitionObj newDeduction = new DeductionDefinitionObj(newDeductionId);
                newDeduction.copyFrom(deduction);

                return newDeduction;
            }
        });

        return get(future);


    }

    private ContentValues createDeductionValues(DeductionDefinition deduction) {
        ContentValues values = new ContentValues();
        values.put(DEDUCTIONS_CODE, deduction.getCode());
        values.put(DEDUCTIONS_REASON, deduction.getReason());
        values.put(DEDUCTIONS_FRACTION, deduction.getFraction());
        values.put(DEDUCTIONS_ALWAYS, deduction.isEnabledByDefault() ? 1 : 0);
        return values;
    }

    public DeductionDefinitionObj fetchDeduction(int deductionId) {
        Collection<DeductionDefinitionObj> deductionObjs = fetchDeductions(where(QDeduction.id.eq(deductionId)));
        return Iterables.getFirst(deductionObjs, null);
    }

    public void deleteDeduction(int deductionId) {

        Future<Void> future = mDb.submit(new SQLCallable<Void>() {
            @Override
            public Void call(SQLDatabase db) {
                ContentValues condition = new ContentValues();
                condition.put(DEDUCTIONS_DEDUCTIONID, deductionId);
                db.deleteOrThrow(TABLE_NAME_DEDUCTIONS, condition);
                return null;
            }
        });

        get(future);

    }

    public void updateDeduction(DeductionDefinition deduction) {

        Future<Void> future = mDb.submit(new SQLCallable<Void>() {
            @Override
            public Void call(SQLDatabase db) {
                ContentValues condition = new ContentValues();
                condition.put(DEDUCTIONS_DEDUCTIONID, deduction.getId());
                db.updateOrThrow(TABLE_NAME_DEDUCTIONS, createDeductionValues(deduction), condition);
                return null;
            }
        });

        get(future);


    }


}
