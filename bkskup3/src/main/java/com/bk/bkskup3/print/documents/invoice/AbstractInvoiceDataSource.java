package com.bk.bkskup3.print.documents.invoice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.bk.bands.evaluate.BandEvaluator;
import com.bk.bands.evaluate.DollarLookup;
import com.bk.bands.evaluate.Evaluator;
import com.bk.bands.evaluate.ValueIterator;
import com.bk.bands.lookup.StrSubstitutor;
import com.bk.bkskup3.library.DocumentProfile;
import com.bk.bkskup3.model.*;
import com.bk.bkskup3.print.context.DocumentContext;
import com.bk.bkskup3.print.context.NumeralDeclination;
import com.bk.bkskup3.runtime.DeductionCalculator;
import com.bk.bkskup3.runtime.InvoiceCalculator;
import com.bk.bkskup3.utils.DoubleFormat;
import com.bk.bkskup3.print.documents.DocumentDataSource;
import com.bk.bkskup3.utils.Dates;
import com.bk.bkskup3.print.lookup.LookupAdapter;
import com.bk.bkskup3.print.lookup.LookupField;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import javax.inject.Inject;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/27/12
 * Time: 2:46 PM
 */
public abstract class AbstractInvoiceDataSource extends DocumentDataSource {

    static class GoodsNameDeclinationImpl implements NumeralDeclination {
        String goodsName_1;
        String goodsName_2_4;
        String goodsName_5plus;

        @Override
        public String declinate(int amount) {
            if (amount == 1)
                return goodsName_1;

            if (amount >= 2 && amount <= 4)
                return goodsName_2_4;

            if (amount >= 5)
                return goodsName_5plus;

            return "";
        }
    }


    @Inject
    Invoice invoice;
    @Inject
    Company company;
    @Inject
    PurchaseDetails purchase;


    private GoodsNameDeclinationImpl goodsNameDeclination;
    private String goodsCategory;
    private String invoiceItemNameFmt;
    private DocumentProfile profile;
    private DollarLookup headerLookup;
    private DollarLookup footerLookup;
    private static final String NOT_APPLICABLE = "-";

    public AbstractInvoiceDataSource(DocumentContext context, DocumentProfile profile) {
        super(context);
        this.profile = profile;
        this.goodsNameDeclination = new GoodsNameDeclinationImpl();

        if(profile != null) {
            this.goodsNameDeclination.goodsName_1 = profile.getOptionValue("GOODSDECLINATION_1");
            this.goodsNameDeclination.goodsName_2_4 = profile.getOptionValue("GOODSDECLINATION_2-4");
            this.goodsNameDeclination.goodsName_5plus = profile.getOptionValue("GOODSDECLINATION_5+");
            this.goodsCategory = profile.getOptionValue("GOODSCATEGORY");
            this.invoiceItemNameFmt = profile.getOptionValue("ITEMNAMEFMT");
        }

        if(Strings.isNullOrEmpty(goodsNameDeclination.goodsName_1))
            goodsNameDeclination.goodsName_1 = "cow#1";


        if(Strings.isNullOrEmpty(goodsNameDeclination.goodsName_2_4))
            goodsNameDeclination.goodsName_2_4 = "cow#2-4";

        if(Strings.isNullOrEmpty(goodsNameDeclination.goodsName_5plus))
            goodsNameDeclination.goodsName_5plus = "cow#5+";

        if(Strings.isNullOrEmpty(this.goodsCategory))
            this.goodsCategory = "XX.XX.XX.X";


    }

    protected String formatAddress(String city, String zipCode, String street, String pobox) {
        return Strings.nullToEmpty(street) + ' ' + Strings.nullToEmpty(pobox) + "\n" + Strings.nullToEmpty(zipCode) + ' ' + Strings.nullToEmpty(city);
    }

    protected DocumentProfile getProfile() {
        return profile;
    }

    public String getGoodsCategory() {
        return goodsCategory;
    }

    public String getInvoiceItemNameFmt() {
        return invoiceItemNameFmt;
    }

    public void setInvoiceItemNameFmt(String invoiceItemNameFmt) {
        this.invoiceItemNameFmt = invoiceItemNameFmt;
    }

    public GoodsNameDeclinationImpl getGoodsNameDeclination() {
        return goodsNameDeclination;
    }

    @Override
    public Evaluator getHeaderValues() {
        if (headerLookup == null) {
            headerLookup = new DollarLookup();
            headerLookup.put("CUSTOMNUMBER", invoice.getCustomNumber());
            headerLookup.put("INVOICEDATE", Dates.toDayDate(invoice.getInvoiceDt()));
            InvoiceHent invoiceHent = invoice.getInvoiceHent();

            headerLookup.put("BUYERNAME", Strings.nullToEmpty(company.getName()));
            headerLookup.put("BUYERADDRESS", formatAddress(company.getCity(),
                    company.getZip(),
                    company.getStreet(),
                    company.getPoBox()));
            headerLookup.put("BUYERFISCALNO", company.getFiscalNo());
            headerLookup.put("BUYERFARMNO", company.getFarmNo().toString() + String.format("%03d", purchase.getHerdNo()));
            headerLookup.put("BUYERPHONENO", Joiner.on(',').join(Strings.nullToEmpty(company.getPhoneNo()), Strings.nullToEmpty(company.getCellPhoneNo())));

            headerLookup.put("SELLERIDISSUEPOST", Strings.nullToEmpty(invoiceHent.getIssuePost()));
            headerLookup.put("SELLERIDISSUEDATE", Dates.toDayDate(invoiceHent.getIssueDate()));
            headerLookup.put("SELLERPERSONALIDNO", Strings.nullToEmpty(invoiceHent.getPersonalIdNo()));
            headerLookup.put("SELLERPERSONALNO", Strings.nullToEmpty(invoiceHent.getPersonalNo()));
            headerLookup.put("SELLERFARMNO", invoiceHent.getHentNo().toString());
            headerLookup.put("SELLERADDRESS", formatAddress(invoiceHent.getCity(),
                    invoiceHent.getZip(),
                    invoiceHent.getStreet(),
                    invoiceHent.getPoBox()));
            headerLookup.put("SELLERNAME", invoiceHent.getHentName());
            headerLookup.put("SELLERFISCALNO", Strings.nullToEmpty(invoiceHent.getFiscalNo()));


            headerLookup.put("TRANSACTIONDATE", Dates.toDayDate(invoice.getTransactionDt()));
            headerLookup.put("TRANSACTIONPLACE", Strings.nullToEmpty(invoice.getTransactionPlace()));

        }

        return headerLookup;
    }

    @Override
    public Evaluator getFooterValues() {

        if (footerLookup == null) {
            footerLookup = new DollarLookup();
            InvoiceHent invoiceHent = invoice.getInvoiceHent();
            InvoiceCalculator invoiceCalc = new InvoiceCalculator(invoice);
            DoubleFormat priceFmt = mContext.getPriceFormat();
            double totalOwed = invoiceCalc.getGrossAfterDeductions();

            PayWay payWay = invoice.getPayWay();
            footerLookup.put("PAYWAY", mContext.getPayWays().getPayWay(payWay));
            footerLookup.put("TOTALOWED", priceFmt.format(totalOwed) + ' ' + mContext.getCurrencySymbol().getCurrencySymbol());
            footerLookup.put("TOTALTEXT", mContext.getPriceToText().translate(totalOwed, mContext.getCurrencySymbol()));

            String bankAccountNo = NOT_APPLICABLE;
            String bankName = NOT_APPLICABLE;
            String payDueDays = NOT_APPLICABLE;

            if (payWay == PayWay.TRANSFER) {
                IBAN iban = invoiceHent.getBankAccountNo();
                if (iban != null) {
                    bankAccountNo = iban.toString();
                }
                bankName = invoiceHent.getBankName();
                payDueDays = String.valueOf(invoice.getPayDueDays()) + ' ' + mContext.getDayDeclination().declinate(invoice.getPayDueDays());
            }

            footerLookup.put("ACCOUNTNO", bankAccountNo);
            footerLookup.put("BANKNAME", Strings.nullToEmpty(bankName));
            footerLookup.put("PAYDUEDAYS", payDueDays);

            footerLookup.put("EXTRAS", Strings.nullToEmpty(invoice.getExtras()));

        }

        return footerLookup;

    }

    @Override
    public BandEvaluator getBandValues(String bandName) {

        if ("INVOICEDEDUCTIONS".equals(bandName)) {
            return new DeductionItemsBandEvaluator();
        } else if ("INVOICEITEMS".equals(bandName)) {
            return new InvoiceItemsBandEvaluator();
        }

        return null;
    }

    private Collection<DeductionItem> createDeductionItems() {

        InvoiceCalculator invoiceCalculator = new InvoiceCalculator(invoice);
        Collection<DeductionCalculator> deductionCalculators = invoiceCalculator.getDeductionCalculators();
        Collection<DeductionItem> result = new ArrayList<DeductionItem>(deductionCalculators.size());

        DoubleFormat priceFmt = mContext.getPriceFormat();

        for (DeductionCalculator deductionCalculator : deductionCalculators) {
            DeductionItem item = new DeductionItem();
            InvoiceDeduction deduction = deductionCalculator.getDeduction();
            item.setDeductedAmount(priceFmt.format(deductionCalculator.getDeductedAmount()) + ' ' + mContext.getCurrencySymbol().getCurrencySymbol());
            item.setDeductionReason(deduction.getReason());
            result.add(item);
        }
        return result;
    }

    protected abstract Collection<InvoiceItem> createItems();

    private class DeductionItemsBandEvaluator implements BandEvaluator {
        InvoiceCalculator invoiceCalc = new InvoiceCalculator(invoice);

        @Override
        public Evaluator getHeaderValues() {
            return null;
        }

        @Override
        public Evaluator getFooterValues() {
            return null;
        }

        @Override
        public ValueIterator getBandValues() {
            return new DeductionIterator(createDeductionItems());
        }
    }


    private class InvoiceItemsBandEvaluator implements BandEvaluator {
        InvoiceCalculator invoiceCalc = new InvoiceCalculator(invoice);
        private DollarLookup footerValues;


        @Override
        public Evaluator getHeaderValues() {
            return null;
        }

        @Override
        public Evaluator getFooterValues() {
            if (footerValues == null) {
                footerValues = new DollarLookup();
                footerValues.put("VATRATE", mContext.getVATRateFormat().format(invoice.getVatRate() * 100) + '%');
                DoubleFormat priceFmt = mContext.getPriceFormat();
                footerValues.put("TOTALNET", priceFmt.format(invoiceCalc.getNet()));
                footerValues.put("TOTALVAT", priceFmt.format(invoiceCalc.getTax()));
                double totalGross = invoiceCalc.getGross();
                footerValues.put("TOTALGROSS", priceFmt.format(totalGross));
            }
            return footerValues;
        }

        @Override
        public ValueIterator getBandValues() {
            return new InvoiceValueIterator(createItems());
        }
    }

    private class InvoiceValueIterator implements ValueIterator {
        private DollarLookup indexLookup = new DollarLookup();
        private int index = 0;
        private Iterator<InvoiceItem> itemIt;
        private StrSubstitutor evaluator;

        private InvoiceValueIterator(Collection<InvoiceItem> invoiceItems) {
            this.itemIt = invoiceItems.iterator();
        }

        @Override
        public boolean hasNext() {
            return itemIt.hasNext();
        }

        @Override
        public boolean next() {
            if (itemIt.hasNext()) {
                InvoiceItem currentItem = itemIt.next();
                evaluator = new StrSubstitutor(new LookupAdapter(currentItem));
                index++;
                indexLookup.put("INDEX", String.valueOf(index));
                return true;
            }
            return false;
        }

        @Override
        public String evaluate(String valueExpression) {
            String valueExprIndexReplaced = indexLookup.evaluate(valueExpression);
            return evaluator.replace(valueExprIndexReplaced);
        }
    }

    private class DeductionIterator implements ValueIterator {
        private Iterator<DeductionItem> itemIt;
        private StrSubstitutor evaluator;

        private DeductionIterator(Collection<DeductionItem> deductionItems) {
            this.itemIt = deductionItems.iterator();
        }


        @Override
        public boolean hasNext() {
            return itemIt.hasNext();
        }

        @Override
        public boolean next() {
            if (itemIt.hasNext()) {
                DeductionItem currentItem = itemIt.next();
                evaluator = new StrSubstitutor(new LookupAdapter(currentItem));

                return true;
            }
            return false;
        }

        @Override
        public String evaluate(String valueExpression) {
            return evaluator.replace(valueExpression);
        }
    }

    public class InvoiceItem {
        @LookupField("GOODSNAME")
        String goodsName;

        @LookupField("GOODSCATEGORY")
        String goodsCategory;

        @LookupField("GOODSQTY")
        String goodsQty;

        @LookupField("GOODSQTYUNIT")
        String goodsQtyUnit;

        @LookupField("UNITPRICENET")
        String unitPriceNet;

        @LookupField("NETPRICE")
        String netPrice;

        @LookupField("VATVALUE")
        String vatValue;

        @LookupField("GROSSPRICE")
        String grossPrice;

        public String getGoodsName() {
            return goodsName;
        }

        public void setGoodsName(String goodsName) {
            this.goodsName = goodsName;
        }

        public String getGoodsCategory() {
            return goodsCategory;
        }

        public void setGoodsCategory(String goodsCategory) {
            this.goodsCategory = goodsCategory;
        }

        public String getGoodsQty() {
            return goodsQty;
        }

        public void setGoodsQty(String goodsQty) {
            this.goodsQty = goodsQty;
        }

        public String getGoodsQtyUnit() {
            return goodsQtyUnit;
        }

        public void setGoodsQtyUnit(String goodsQtyUnit) {
            this.goodsQtyUnit = goodsQtyUnit;
        }

        public String getNetPrice() {
            return netPrice;
        }

        public void setNetPrice(String netPrice) {
            this.netPrice = netPrice;
        }

        public String getVatValue() {
            return vatValue;
        }

        public void setVatValue(String vatValue) {
            this.vatValue = vatValue;
        }

        public String getGrossPrice() {
            return grossPrice;
        }

        public void setGrossPrice(String grossPrice) {
            this.grossPrice = grossPrice;
        }

        public String getUnitPriceNet() {
            return unitPriceNet;
        }

        public void setUnitPriceNet(String unitPriceNet) {
            this.unitPriceNet = unitPriceNet;
        }
    }

    public class DeductionItem {
        @LookupField("DEDUCTIONREASON")
        String deductionReason;

        @LookupField("DEDUCTEDAMOUNT")
        String deductedAmount;

        public String getDeductionReason() {
            return deductionReason;
        }

        public void setDeductionReason(String deductionReason) {
            this.deductionReason = deductionReason;
        }

        public void setDeductedAmount(String deductedAmount) {
            this.deductedAmount = deductedAmount;
        }
    }

}
