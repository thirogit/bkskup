package com.bk.bkskup3.print.documents.invoice;

import com.bk.bands.evaluate.BandEvaluator;
import com.bk.bands.evaluate.DollarLookup;
import com.bk.bands.evaluate.Evaluator;
import com.bk.bkskup3.model.Company;
import com.bk.bkskup3.model.Invoice;
import com.bk.bkskup3.model.PurchaseDetails;
import com.bk.bkskup3.print.context.DocumentContext;
import com.bk.bkskup3.print.documents.DocumentDataSource;
import com.bk.bkskup3.utils.Dates;
import com.google.common.base.Strings;

import javax.inject.Inject;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 4/5/12
 * Time: 9:20 PM
 */
public class GDPRDataSource extends DocumentDataSource {
    @Inject
    Invoice invoice;
    @Inject
    Company company;
    @Inject
    PurchaseDetails purchase;

    private DollarLookup headerLookup;
    private DollarLookup footerLookup;

    public GDPRDataSource(DocumentContext context) {
        super(context);
    }

    @Override
    public Evaluator getHeaderValues() {
        if (headerLookup == null) {
            headerLookup = new DollarLookup();
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
            footerLookup.put("COMPANY", Strings.nullToEmpty(company.getName()));
            footerLookup.put("COMPANYADR",formatAddress(company.getCity(),
                    company.getZip(),
                    company.getStreet(),
                    company.getPoBox()));
            footerLookup.put("NOWDATE", Dates.toDayDate(Dates.now()));
        }

        return footerLookup;

    }

    @Override
    public BandEvaluator getBandValues(String bandName) {



        return null;
    }




}
