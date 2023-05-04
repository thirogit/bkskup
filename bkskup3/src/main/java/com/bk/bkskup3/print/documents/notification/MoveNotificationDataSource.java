package com.bk.bkskup3.print.documents.notification;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.bk.bands.evaluate.*;
import com.bk.bands.lookup.StrLookup;
import com.bk.bkskup3.model.Company;
import com.bk.bkskup3.model.Cow;
import com.bk.bkskup3.model.Invoice;
import com.bk.bkskup3.model.PurchaseDetails;
import com.bk.bkskup3.print.context.DocumentContext;
import com.bk.bkskup3.print.documents.DocumentDataSource;
import com.bk.bkskup3.print.lookup.LookupAdapter;
import com.bk.bkskup3.print.lookup.LookupField;
import com.google.common.base.Strings;

import javax.inject.Inject;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 8/22/12
 * Time: 11:12 AM
 */
public class MoveNotificationDataSource extends DocumentDataSource {
    private static final int MIN_NUMBER_OF_ITEMS = 4;

    @Inject
    Invoice invoice;

    @Inject
    Company company;

    @Inject
    PurchaseDetails purchase;

    private Map<String, String> headerLookup;
    private Map<String, String> footerLookup;


    public MoveNotificationDataSource(DocumentContext context) {
        super(context);
    }

    @Override
    public Evaluator getHeaderValues() {
        if(headerLookup == null)
        {
            headerLookup = new HashMap<String, String>();
            headerLookup.put("SRCFARMNO", invoice.getInvoiceHent().getHentNo().toString());
        }

        return new IndexNotationDollarEvaluator(StrLookup.mapLookup(headerLookup));
    }

    @Override
    public Evaluator getFooterValues() {
        if(footerLookup == null)
        {
            footerLookup = new HashMap<String, String>();

            int herdNo = purchase.getHerdNo();

            footerLookup.put("DESTFARMNO", company.getFarmNo().toString() + Strings.padStart(String.valueOf(herdNo),3,'0'));
            footerLookup.put("EVENTBUY", "");
            footerLookup.put("EVENTSELL", "");
            footerLookup.put("EVENTBUYANDSELL", "X");
            footerLookup.put("EVENTEXPORTTOEU", "");
            footerLookup.put("EVENTEXPORTNOTTOEU", "");
            footerLookup.put("EXPORTCOUNTRY", "");
            footerLookup.put("EVENTOTHER", "");


            Date eventDt = invoice.getTransactionDt();
            if (eventDt == null)
                eventDt = invoice.getInvoiceDt();

            DateFormat dayDateFmt = new SimpleDateFormat("dd");
            DateFormat monDateFmt = new SimpleDateFormat("MM");
            DateFormat yearDateFmt = new SimpleDateFormat("yyyy");

            footerLookup.put("EVENTDATEDAY", dayDateFmt.format(eventDt));
            footerLookup.put("EVENTDATEMON", monDateFmt.format(eventDt));
            footerLookup.put("EVENTDATEYEAR", yearDateFmt.format(eventDt));

            Date nowDt = Calendar.getInstance().getTime();

            footerLookup.put("PRINTDATEDAY", dayDateFmt.format(nowDt));
            footerLookup.put("PRINTDATEMON", monDateFmt.format(nowDt));
            footerLookup.put("PRINTDATEYEAR", yearDateFmt.format(nowDt));
        }
        return new IndexNotationDollarEvaluator(StrLookup.mapLookup(footerLookup));
    }

    @Override
    public BandEvaluator getBandValues(String bandName) {
        return new OnlyBodyBandEvaluator(new NotificationItemsIterator(createItems()));
    }

    private Collection<NotificationItem> createItems() {
        ArrayList<NotificationItem> result = new ArrayList<NotificationItem>(MIN_NUMBER_OF_ITEMS);
        Iterator<Cow> cowIt = invoice.getCows().iterator();

        while (result.size() < MIN_NUMBER_OF_ITEMS || cowIt.hasNext()) {
            String cowNo = "";
            if (cowIt.hasNext()) {
                Cow cow = cowIt.next();
                cowNo = cow.getCowNo().toString();
            }
            NotificationItem item = new NotificationItem();
            item.setCowNo(cowNo);
            result.add((item));
        }
        return result;
    }

    class NotificationItem {
        @LookupField("COWNO")
        private String cowNo;

        public void setCowNo(String cowNo) {
            this.cowNo = cowNo;
        }

        public String getCowNo() {
            return cowNo;
        }


    }



    class NotificationItemsIterator implements ValueIterator {
        private Iterator<NotificationItem> itemIt;
        private Evaluator evaluator;

        NotificationItemsIterator(Collection<NotificationItem> items) {
            this.itemIt = items.iterator();
        }


        @Override
        public boolean hasNext() {
            return itemIt.hasNext();
        }

        @Override
        public boolean next() {
            if (itemIt.hasNext()) {
                NotificationItem currentItem = itemIt.next();
                evaluator = new IndexNotationDollarEvaluator(new LookupAdapter(currentItem));
                return true;
            }
            return false;
        }

        @Override
        public String evaluate(String valueExpression) {
            return evaluator.evaluate(valueExpression);
        }
    }

    ;
}
