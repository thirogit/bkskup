package com.bk.bkskup3.tasks;

import com.bk.bkskup3.model.*;

import java.io.Serializable;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/7/2014
 * Time: 2:45 PM
 */
public class DependenciesForInvoicePrint implements Serializable
{

    private PurchaseDetails purchaseDetails;
    private InvoiceObj invoice;
    private CompanyObj company;
    private AgentObj agent;
    private Collection<CowClassObj> classes;
    private Collection<StockObj> stocks;

    public PurchaseDetails getPurchaseDetails() {
        return purchaseDetails;
    }

    public void setPurchaseDetails(PurchaseDetails purchaseDetails) {
        this.purchaseDetails = purchaseDetails;
    }

    public InvoiceObj getInvoice() {
        return invoice;
    }

    public void setInvoice(InvoiceObj invoice) {
        this.invoice = invoice;
    }

    public CompanyObj getCompany() {
        return company;
    }

    public void setCompany(CompanyObj company) {
        this.company = company;
    }

    public AgentObj getAgent() {
        return agent;
    }

    public void setAgent(AgentObj agent) {
        this.agent = agent;
    }

    public Collection<CowClassObj> getClasses() {
        return classes;
    }

    public void setClasses(Collection<CowClassObj> classes) {
        this.classes = classes;
    }

    public Collection<StockObj> getStocks() {
        return stocks;
    }

    public void setStocks(Collection<StockObj> stocks) {
        this.stocks = stocks;
    }
}
