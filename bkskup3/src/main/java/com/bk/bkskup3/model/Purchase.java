package com.bk.bkskup3.model;

import java.util.Collection;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/23/11
 * Time: 1:44 PM
 */
public interface Purchase extends Idable {
    Date getPurchaseStart();

    Date getPurchaseEnd();

    String getPlateNo();

    Collection<Invoice> getInvoices();

    int getCowCount();

    Invoice getInvoice(int invoiceId);

    PurchaseState getState();

    int getHerdNo();
}
