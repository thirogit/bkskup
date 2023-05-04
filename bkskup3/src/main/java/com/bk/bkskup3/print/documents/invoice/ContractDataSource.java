package com.bk.bkskup3.print.documents.invoice;

import com.bk.bands.evaluate.BandEvaluator;
import com.bk.bands.evaluate.DollarLookup;
import com.bk.bands.evaluate.Evaluator;
import com.bk.bands.evaluate.ValueIterator;
import com.bk.bands.lookup.StrSubstitutor;
import com.bk.bkskup3.model.Company;
import com.bk.bkskup3.model.Cow;
import com.bk.bkskup3.model.Invoice;
import com.bk.bkskup3.model.InvoiceHent;
import com.bk.bkskup3.model.PurchaseDetails;
import com.bk.bkskup3.print.context.DocumentContext;
import com.bk.bkskup3.print.documents.DocumentDataSource;
import com.bk.bkskup3.print.lookup.LookupAdapter;
import com.bk.bkskup3.print.lookup.LookupField;
import com.bk.bkskup3.runtime.CowCalculator;
import com.bk.bkskup3.runtime.InvoiceCalculator;
import com.bk.bkskup3.utils.Dates;
import com.bk.bkskup3.utils.DoubleFormat;
import com.bk.bkskup3.utils.NullUtils;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.inject.Inject;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 4/5/12
 * Time: 9:20 PM
 */
public class ContractDataSource extends DocumentDataSource {
    @Inject
    Invoice invoice;
    @Inject
    Company company;
    @Inject
    PurchaseDetails purchase;

    private DollarLookup headerLookup;
    private DollarLookup footerLookup;

    public ContractDataSource(DocumentContext context) {
        super(context);
    }

    @Override
    public Evaluator getHeaderValues() {
        if (headerLookup == null) {
            headerLookup = new DollarLookup();
            headerLookup.put("CONTRACTDT", Dates.toDayDate(invoice.getInvoiceDt()));
            headerLookup.put("CONTRACTNO", invoice.getCustomNumber());
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
            headerLookup.put("TRANSACTIONDT", Dates.toDayDate(invoice.getTransactionDt()));
            headerLookup.put("TRANSACTIONPLACE", Strings.nullToEmpty(invoice.getTransactionPlace()));

        }

        return headerLookup;
    }

    protected String formatAddress(String city, String zipCode, String street, String pobox) {
        return Strings.nullToEmpty(street) + ' ' + Strings.nullToEmpty(pobox) + "\n" + Strings.nullToEmpty(zipCode) + ' ' + Strings.nullToEmpty(city);
    }

    @Override
    public Evaluator getFooterValues() {

        if (footerLookup == null) {
            footerLookup = new DollarLookup();
            footerLookup.put("TRANSACTIONDT", Dates.toDayDate(invoice.getTransactionDt()));
            footerLookup.put("PAYDUEDAYS", NullUtils.valueForNull(invoice.getPayDueDays(), 0).toString());
        }

        return footerLookup;

    }

    @Override
    public BandEvaluator getBandValues(String bandName) {

        if ("COWS".equals(bandName)) {
            return new CowItemsBandEvaluator();
        }

        return null;
    }

    private Collection<CowItem> createCowItems() {

        InvoiceCalculator invoiceCalculator = new InvoiceCalculator(invoice);

        Collection<CowCalculator> cowCalculators = invoiceCalculator.getCowCalculators();
        Collection<CowItem> result = new ArrayList<CowItem>(cowCalculators.size());

        DoubleFormat priceFmt = mContext.getPriceFormat();
        DoubleFormat weightFormat = mContext.getWeightFormat();

        for (CowCalculator cowCalculator : cowCalculators) {
            Cow cow = cowCalculator.getCow();
//          DollarLookup itemDescLookup = new DollarLookup();
//          //         itemDescLookup.put("NAZWAKLASY",);
//                   itemDescLookup.put("KODKLASY",cow.getClassCd());
//          //         itemDescLookup.put("TOWAR");
//                   itemDescLookup.put("PLEC",mContext.getGenders().getGender(cow.getSex()));
//                   itemDescLookup.put("KOLCZYK",cow.getCowNo().toString());
//                   itemDescLookup.put("WAGA",weightFormat.format(cow.getWeight()));

            CowItem item = new CowItem();


            item.setItemDescription(cow.getCowNo().toString() + " ," + mContext.getGenders().getGender(cow.getSex()));
            item.setGrossPrice(priceFmt.format(cowCalculator.getGrossPricePerKg()));
            result.add(item);
        }
        return result;
    }


    private class CowItemsBandEvaluator implements BandEvaluator {
        @Override
        public Evaluator getHeaderValues() {
            return null;
        }

        @Override
        public Evaluator getFooterValues() {
            return ContractDataSource.this.getFooterValues();
        }

        @Override
        public ValueIterator getBandValues() {
            return new ItemsIterator(createCowItems());
        }
    }


    private class ItemsIterator implements ValueIterator {
        private DollarLookup indexLookup = new DollarLookup();
        private int index = 0;
        private Iterator<CowItem> itemIt;
        private StrSubstitutor evaluator;

        private ItemsIterator(Collection<CowItem> items) {
            this.itemIt = items.iterator();
        }

        @Override
        public boolean hasNext() {
            return itemIt.hasNext();
        }

        @Override
        public boolean next() {
            if (itemIt.hasNext()) {
                CowItem currentItem = itemIt.next();
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

    public class CowItem {
        @LookupField("ITEMDESCRIPTION")
        String itemDescription;

        @LookupField("ITEMGROSSPRICE")
        String grossPrice;

        public String getItemDescription() {
            return itemDescription;
        }

        public void setItemDescription(String itemDescription) {
            this.itemDescription = itemDescription;
        }

        public String getGrossPrice() {
            return grossPrice;
        }

        public void setGrossPrice(String grossPrice) {
            this.grossPrice = grossPrice;
        }
    }


}
