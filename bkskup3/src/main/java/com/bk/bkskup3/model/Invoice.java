package com.bk.bkskup3.model;


import java.util.Collection;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/23/11
 * Time: 1:00 PM
 */
public interface Invoice extends Idable {
    Double getVatRate();

    PayWay getPayWay();

    String getCustomNumber();

    Integer getPayDueDays();

    int getPurchase();

    InvoiceHent getInvoiceHent();

    Collection<Cow> getCows();

    Collection<InvoiceDeduction> getDeductions();

    int getCowCount();

    int getDeductionsCount();

    double getTotalNet();

    double getTotalGross();

    Date getTransactionDt();

    String getTransactionPlace();

    Date getInvoiceDt();

    InvoiceType getInvoiceType();

    String getExtras();

}
