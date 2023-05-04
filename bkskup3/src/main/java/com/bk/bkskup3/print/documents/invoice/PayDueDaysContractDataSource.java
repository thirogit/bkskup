package com.bk.bkskup3.print.documents.invoice;

import android.text.format.DateUtils;

import com.bk.bands.evaluate.BandEvaluator;
import com.bk.bands.evaluate.DollarLookup;
import com.bk.bands.evaluate.Evaluator;
import com.bk.bkskup3.library.DocumentOption;
import com.bk.bkskup3.library.DocumentProfile;
import com.bk.bkskup3.model.Company;
import com.bk.bkskup3.model.Invoice;
import com.bk.bkskup3.model.InvoiceHent;
import com.bk.bkskup3.model.PurchaseDetails;
import com.bk.bkskup3.print.context.DocumentContext;
import com.bk.bkskup3.print.documents.DocumentDataSource;
import com.bk.bkskup3.utils.Dates;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 4/5/12
 * Time: 9:20 PM
 */
public class PayDueDaysContractDataSource extends DocumentDataSource {
    @Inject
    Invoice invoice;
    @Inject
    Company company;
    @Inject
    PurchaseDetails purchase;

    private DollarLookup headerLookup;
    private DollarLookup footerLookup;

    private DocumentProfile profile;

    public PayDueDaysContractDataSource(DocumentContext context, DocumentProfile profile) {
        super(context);
        this.profile = profile;
    }

    @Override
    public Evaluator getHeaderValues() {
        if (headerLookup == null) {
            headerLookup = new DollarLookup();


            String contractDtStr = null;

            if(profile != null)
            {
                DocumentOption contractDtOption = profile.getOption("CONTRACTDT");

                if(contractDtOption != null) {
                    contractDtStr = contractDtOption.getOptionValue();
                }
            }

            if(Strings.isNullOrEmpty(contractDtStr))
            {
                contractDtStr = Dates.toDayDate(invoice.getInvoiceDt());
            }

            headerLookup.put("CONTRACTDT", contractDtStr);

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
            headerLookup.put("PAYDUEDAYS", String.valueOf(getPayDueDays()));
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
            footerLookup.put("PAYDUEDAYS", String.valueOf(getPayDueDays()));

            String fromDtStr = "",toDtStr = "";

            if(profile != null)
            {

                DocumentOption fromDtOption = profile.getOption("CONTRACTFROMDT");
                DocumentOption toDtOption = profile.getOption("CONTRACTTODT");

                Date now = Calendar.getInstance().getTime();
                if(fromDtOption != null) {
                    fromDtStr = fromDtOption.getOptionValue();
                }

                if(Strings.isNullOrEmpty(fromDtStr))
                {
                    fromDtStr = Dates.toDayDate(Dates.plusYear(now,-5));
                }


                if(toDtOption != null) {
                    toDtStr = toDtOption.getOptionValue();
                }

                if(Strings.isNullOrEmpty(toDtStr))
                {
                    toDtStr = Dates.toDayDate(Dates.plusYear(now,5));
                }

            }

            footerLookup.put("CONTRACTFROMDT", fromDtStr);
            footerLookup.put("CONTRACTTODT", toDtStr);

        }

        return footerLookup;

    }

    @Override
    public BandEvaluator getBandValues(String bandName) {

        return null;
    }

    public int getPayDueDays() {
        return 30;
    }


}
