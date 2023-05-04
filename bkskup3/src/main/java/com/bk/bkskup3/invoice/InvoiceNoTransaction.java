package com.bk.bkskup3.invoice;

/**
 * Created by sg0891787 on 9/1/2017.
 */

public class InvoiceNoTransaction {
    private int transactionId;
    private int invoiceNo;
    private InvoiceNoState state;

    public InvoiceNoTransaction(int transactionId) {
        this.transactionId = transactionId;
    }

    public InvoiceNoTransaction(int transactionId, int invoiceNo, InvoiceNoState state) {
        this.transactionId = transactionId;
        this.invoiceNo = invoiceNo;
        this.state = state;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public int getInvoiceNo() {
        return invoiceNo;
    }

    public InvoiceNoState getState() {
        return state;
    }

    public void setInvoiceNo(int invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public void setState(InvoiceNoState state) {
        this.state = state;
    }
}
