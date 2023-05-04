package com.bk.bkskup3.print.context;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import android.content.res.Resources;

import com.bk.bkskup3.R;
import com.bk.bkskup3.model.Agent;
import com.bk.bkskup3.model.CowClass;
import com.bk.bkskup3.model.PayWay;
import com.bk.bkskup3.model.CowSex;
import com.bk.bkskup3.model.Stock;
import com.bk.bkskup3.print.context.pricetotext.PriceToText;
import com.bk.bkskup3.print.context.pricetotext.PriceToTextFactory;
import com.bk.bkskup3.tasks.DependenciesForInvoicePrint;
import com.bk.bkskup3.utils.DoubleFormat;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 8/22/12
 * Time: 1:23 PM
 */
public class DocumentContextBuilder {
    class UnitsImpl implements Units {
        private String kgUnitSymbol;
        private String pieceUnitSymbol;


        @Override
        public String getUnit(Unit unit) {
            switch (unit) {
                case PIECE:
                    return pieceUnitSymbol;
                case KG:
                    return kgUnitSymbol;
            }

            return "";
        }
    }

    class CurrencyImpl implements Currency {
        private String currencySymbol;
        private String fractionSymbol;

        @Override
        public String getCurrencySymbol() {
            return currencySymbol;
        }

        @Override
        public String getFractionSymbol() {
            return fractionSymbol;
        }
    }

    class PayWaysImpl implements PayWays {
        private String payWayCash;
        private String payWayTransfer;

        @Override
        public String getPayWay(PayWay payWay) {
            switch (payWay) {
                case CASH:
                    return payWayCash;
                case TRANSFER:
                    return payWayTransfer;
            }

            return "";
        }
    }

    class DayDeclinationImpl implements NumeralDeclination {
        private String dayPlural;
        private String daySingular;

        @Override
        public String declinate(int amount) {
            return amount == 1 ? daySingular : dayPlural;
        }
    }

    class GendersImpl implements Genders {
        private Map<CowSex, String> genderMap = new HashMap<CowSex, String>(3);

        @Override
        public String getGender(CowSex gender) {
            return genderMap.get(gender);
        }
    }


    class DefinitionsImpl implements Definitions {

        DependenciesForInvoicePrint dependencies;

        @Override
        public Collection<? extends CowClass> getCowClasses() {
            return dependencies.getClasses();
        }

        @Override
        public CowClass getCowClass(String classCd) {
            for(CowClass cowClass : getCowClasses())
            {
                if(cowClass.getClassCode().equals(classCd))
                {
                    return cowClass;
                }
            }
            return null;
        }

        @Override
        public Collection<? extends Stock> getStocks() {
            return dependencies.getStocks();
        }

        @Override
        public Agent getAgent() {
            return dependencies.getAgent();
        }


    }


    class LocalizedTokensImpl implements LocalizedTokens
    {
        Multimap<String,String> tokenTranslation;

        @Override
        public Collection<String> getLocalizedToken(String tokenName) {
            return tokenTranslation.get(tokenName);
        }
    }



    class DocumentContextImpl implements DocumentContext {
        protected PayWays payWays;
        protected Currency currency;
        protected DoubleFormat priceFmt;
        protected DoubleFormat weightFmt;
        protected DoubleFormat pricePerKgFmt;
        protected DoubleFormat vatRateFmt;
        protected NumeralDeclination dayDeclination;
        protected PriceToText priceToText;
        protected Units units;
        protected Genders genders;
        protected Definitions definitions;
        protected LocalizedTokens tokens;

        @Override
        public Currency getCurrencySymbol() {
            return currency;
        }

        @Override
        public PayWays getPayWays() {
            return payWays;
        }

        @Override
        public DoubleFormat getWeightFormat() {
            return weightFmt;
        }

        public Units getUnits() {
            return units;
        }

        @Override
        public Genders getGenders() {
            return genders;
        }

        @Override
        public Definitions getDefinitions() {
            return definitions;
        }

        @Override
        public LocalizedTokens getTokens() {
            return tokens;
        }

        @Override
        public DoubleFormat getPriceFormat() {
            return priceFmt;
        }

        @Override
        public DoubleFormat getVATRateFormat() {
            return vatRateFmt;
        }

        @Override
        public DoubleFormat getPricePerKgFormat() {
            return pricePerKgFmt;
        }

        @Override
        public NumeralDeclination getDayDeclination() {
            return dayDeclination;
        }

        @Override
        public PriceToText getPriceToText() {
            return priceToText;
        }
    }

    private Resources mResources;
    private NumberFormatDefinition weightFmtDef = new NumberFormatDefinition(',', -3);
    private NumberFormatDefinition priceFmtDef = new NumberFormatDefinition(',', 2);
    private NumberFormatDefinition pricePerKgFmtDef = new NumberFormatDefinition(',', 2);
    private NumberFormatDefinition vatRateFmtDef = new NumberFormatDefinition(',', 2);
    private DependenciesForInvoicePrint mDependencies;

    public DocumentContextBuilder(Resources resources,
                                  DependenciesForInvoicePrint dependencies) {
        this.mResources = resources;
        this.mDependencies = dependencies;
    }

    public DocumentContext build() {
        DocumentContextImpl result = new DocumentContextImpl();
        PayWaysImpl payWays = new PayWaysImpl();
        payWays.payWayCash = mResources.getString(R.string.payWayCash);
        payWays.payWayTransfer = mResources.getString(R.string.payWayTransfer);

        CurrencyImpl currency = new CurrencyImpl();
        currency.currencySymbol = mResources.getString(R.string.currencySymbol);
        currency.fractionSymbol = mResources.getString(R.string.currencyFractionSymbol);

        DayDeclinationImpl dayDeclination = new DayDeclinationImpl();
        dayDeclination.dayPlural = mResources.getString(R.string.numeralDayPlural);
        dayDeclination.daySingular = mResources.getString(R.string.numeralDaySingular);

        UnitsImpl units = new UnitsImpl();
        units.kgUnitSymbol = mResources.getString(R.string.kilogramUnitSymbol);
        units.pieceUnitSymbol = mResources.getString(R.string.pieceUnitSymbol);

        GendersImpl genders = new GendersImpl();
        genders.genderMap.put(CowSex.NONE, mResources.getString(R.string.cowSexNONE));
        genders.genderMap.put(CowSex.XX, mResources.getString(R.string.cowSexXX));
        genders.genderMap.put(CowSex.XY, mResources.getString(R.string.cowSexXY));

        result.currency = currency;
        result.payWays = payWays;
        result.dayDeclination = dayDeclination;
        result.units = units;

        result.priceFmt = new DoubleFormat(priceFmtDef);
        result.weightFmt = new DoubleFormat(weightFmtDef);
        result.pricePerKgFmt = new DoubleFormat(pricePerKgFmtDef);
        result.vatRateFmt = new DoubleFormat(vatRateFmtDef);

        result.priceToText = PriceToTextFactory.getPriceToText(mResources.getString(R.string.language));
        result.genders = genders;

        DefinitionsImpl definitions = new DefinitionsImpl();
        definitions.dependencies = mDependencies;
        result.definitions = definitions;

        LocalizedTokensImpl tokens = new LocalizedTokensImpl();
        tokens.tokenTranslation = LinkedListMultimap.create();
        tokens.tokenTranslation.put("GOODS",mResources.getString(R.string.tokenGOODS));
        tokens.tokenTranslation.put("COWNO",mResources.getString(R.string.tokenCOWNO));
        tokens.tokenTranslation.put("SEX",mResources.getString(R.string.tokenSEX));
        tokens.tokenTranslation.put("SEX",mResources.getString(R.string.tokenSEX_noPolish));
        tokens.tokenTranslation.put("CLASS",mResources.getString(R.string.tokenCLASS));
        tokens.tokenTranslation.put("CLASSNAME",mResources.getString(R.string.tokenCLASSNAME));
        tokens.tokenTranslation.put("WEIGHT",mResources.getString(R.string.tokenWEIGHT));
        tokens.tokenTranslation.put("STOCK",mResources.getString(R.string.tokenSTOCK));
        tokens.tokenTranslation.put("QTY",mResources.getString(R.string.tokenQTY));
        tokens.tokenTranslation.put("QTY",mResources.getString(R.string.tokenQTY_noPolish));
        result.tokens = tokens;

        return result;
    }

}
