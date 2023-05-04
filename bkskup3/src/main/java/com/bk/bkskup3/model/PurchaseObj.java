package com.bk.bkskup3.model;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * Created by IntelliJ IDEA.
 * User: root
 * Date: 14.06.11
 * Time: 21:11
 */
public class PurchaseObj extends IdableObj implements Purchase {

    private PurchaseDetails details = new PurchaseDetails();
    protected Collection<InvoiceObj> invoices;

    public PurchaseObj(int id) {
        super(id);
        invoices = new LinkedList<InvoiceObj>();
    }

    public void copyFrom(Purchase src) {
        this.details.setPurchaseStart(src.getPurchaseStart());
        this.details.setPurchaseEnd(src.getPurchaseEnd());
        this.details.setPlateNo(src.getPlateNo());
        this.details.setHerdNo(src.getHerdNo());
        this.details.state = src.getState();
        invoices.clear();

        for (Invoice srcInvoice : src.getInvoices()) {
            InvoiceObj invoice = new InvoiceObj(src.getId());
            invoice.copyFrom(srcInvoice);
            invoices.add(invoice);
        }

    }

    public PurchaseDetails getDetails()
    {
        return details;
    }

    public Date getPurchaseStart() {
        return details.getPurchaseStart();
    }


    public Date getPurchaseEnd() {
        return details.getPurchaseEnd();
    }

    public String getPlateNo() {
        return details.getPlateNo();
    }

    public Collection<Invoice> getInvoices() {
        ImmutableList.Builder<Invoice> builder = ImmutableList.builder();
        return builder.addAll(invoices).build();
    }

    public int getCowCount() {
        int count = 0;
        for (Invoice invoice : invoices) {
            count += invoice.getCowCount();
        }
        return count;
    }

    public Invoice getInvoice(int invoiceId) {
        return findInvoice(invoiceId);
    }

    @Override
    public PurchaseState getState() {
        return details.state;
    }

    @Override
    public int getHerdNo() {
        return details.getHerdNo();
    }

    public void setHerdNo(int herdNo) {
        this.details.setHerdNo(herdNo);
    }

    protected InvoiceObj findInvoice(final int invoiceId) {
        return Iterables.find(invoices, new Predicate<Invoice>() {
            @Override
            public boolean apply(Invoice invoice) {
                return invoice.getId() == invoiceId;
            }
        }, null);
    }

    public void addInvoice(InvoiceObj invoice) {
        invoices.add(invoice);
    }

    public void addInvoices(Collection<InvoiceObj> invoices) {
        this.invoices.addAll(invoices);
    }

    public boolean removeInvoice(int invoiceId) {
        for (Iterator<InvoiceObj> invoiceIt = invoices.iterator(); invoiceIt.hasNext(); ) {
            InvoiceObj invoice = invoiceIt.next();
            if (invoice.getId() == invoiceId) {
                invoiceIt.remove();
                return true;
            }
        }

        return false;
    }


    public void setPurchaseStart(Date purchaseStart) {
        details.setPurchaseStart(purchaseStart);
    }


    public void setPurchaseEnd(Date purchaseEnd) {
        details.setPurchaseEnd(purchaseEnd);
    }

    public void setPlateNo(String plateNo) {
        details.setPlateNo(plateNo);
    }

    public void setState(PurchaseState state) {
        this.details.state = state;
    }

}
