package com.bk.bkskup3.print.documents.foodchain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.bk.bands.evaluate.*;
import com.bk.bands.lookup.StrSubstitutor;
import com.bk.bkskup3.library.DocumentProfile;
import com.bk.bkskup3.model.Cow;
import com.bk.bkskup3.model.Invoice;
import com.bk.bkskup3.model.InvoiceHent;
import com.bk.bkskup3.model.PurchaseDetails;
import com.bk.bkskup3.print.context.Genders;
import com.bk.bkskup3.print.documents.DocumentDataSource;
import com.bk.bkskup3.print.context.DocumentContext;
import com.bk.bkskup3.print.lookup.LookupAdapter;
import com.bk.bkskup3.print.lookup.LookupField;
import com.google.common.base.Strings;

import javax.inject.Inject;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 9/22/12
 * Time: 9:52 PM
 */
public class FoodChainDataSource extends DocumentDataSource {
    private DollarLookup headerLookup;
    private DollarLookup footerLookup = new DollarLookup();

    @Inject
    Invoice invoice;
    @Inject
    PurchaseDetails purchase;

    private DocumentProfile profile;
    private String spiecies;

    public FoodChainDataSource(DocumentContext context, DocumentProfile profile) {
        super(context);
        this.profile = profile;

        if(profile != null)
        {
            spiecies = profile.getOptionValue("SPIECIES");
        }

        spiecies = Strings.nullToEmpty(spiecies);
    }

    @Override
    public Evaluator getHeaderValues() {
        if(headerLookup == null)
        {
            headerLookup = new DollarLookup();
            InvoiceHent invoiceHent = invoice.getInvoiceHent();
            headerLookup.put("OWNERNAME", invoiceHent.getHentName());
            headerLookup.put("OWNERZIPCODE", invoiceHent.getZip());
            headerLookup.put("OWNERCITY", invoiceHent.getCity());
            headerLookup.put("OWNERSTREETADDRR", invoiceHent.getStreet() + ' ' + invoiceHent.getPoBox());
            headerLookup.put("FARMNO", invoiceHent.getHentNo().toString());
            headerLookup.put("PLATENO", Strings.nullToEmpty(purchase.getPlateNo()));
            headerLookup.put("DESTSLAUCHTERCITY", "");
            headerLookup.put("ANIMALSPECIES", spiecies);
            headerLookup.put("ANIMALCOUNT", String.valueOf(invoice.getCowCount()));
        }
        return headerLookup;
    }

    @Override
    public Evaluator getFooterValues() {
        return footerLookup;
    }

    @Override
    public BandEvaluator getBandValues(String bandName) {
        return new OnlyBodyBandEvaluator(new FoodChainItemIterator(createItems()));
    }

    private Collection<FoodChainItem> createItems() {
        DocumentContext context = getContext();
        Genders genders = context.getGenders();

        Collection<FoodChainItem> items = new ArrayList<FoodChainItem>(invoice.getCowCount());
        for (Cow cow : invoice.getCows()) {
            FoodChainItem item = new FoodChainItem();
            item.setCowGender(genders.getGender(cow.getSex()));
            item.setCowNo(cow.getCowNo().toString());
            items.add(item);
        }

        return items;
    }

    class FoodChainItemIterator implements ValueIterator {

        private DollarLookup indexLookup = new DollarLookup();
        private int index = 0;
        private Iterator<FoodChainItem> itemIt;
        private StrSubstitutor evaluator;

        private FoodChainItemIterator(Collection<FoodChainItem> invoiceItems) {
            this.itemIt = invoiceItems.iterator();
        }

        @Override
        public boolean hasNext() {
            return itemIt.hasNext();
        }

        @Override
        public boolean next() {
            if (itemIt.hasNext()) {
                FoodChainItem currentItem = itemIt.next();
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

    class FoodChainItem {
        @LookupField("COWNO")
        private String cowNo;

        @LookupField("COWGENDER")
        private String cowGender;

        public String getCowNo() {
            return cowNo;
        }

        public void setCowNo(String cowNo) {
            this.cowNo = cowNo;
        }

        public String getCowGender() {
            return cowGender;
        }

        public void setCowGender(String cowGender) {
            this.cowGender = cowGender;
        }
    }
}
