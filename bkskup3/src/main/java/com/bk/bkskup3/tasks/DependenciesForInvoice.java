package com.bk.bkskup3.tasks;

import com.bk.bkskup3.model.DeductionDefinitionObj;
import com.bk.bkskup3.model.Herd;
import com.bk.bkskup3.model.PurchaseDetails;
import com.bk.bkskup3.settings.ConstraintsSettings;
import com.bk.bkskup3.settings.InputDefaultsSettings;
import com.bk.bkskup3.settings.InvoiceSettings;
import com.bk.bkskup3.settings.TaxSettings;

import java.io.Serializable;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/19/2014
 * Time: 4:45 PM
 */
public class DependenciesForInvoice implements Serializable {
    private TaxSettings taxSettings;
    private ConstraintsSettings constraintsSettings;
    private InvoiceSettings invoiceSettings;
    private InputDefaultsSettings inputDefaultsSettings;
    private PurchaseDetails purchaseDetails;
    private Herd herd;

    private Collection<DeductionDefinitionObj> deductionDefinitions;

    public TaxSettings getTaxSettings() {
        return taxSettings;
    }

    public void setTaxSettings(TaxSettings taxSettings) {
        this.taxSettings = taxSettings;
    }

    public ConstraintsSettings getConstraintsSettings() {
        return constraintsSettings;
    }

    public void setConstraintsSettings(ConstraintsSettings constraintsSettings) {
        this.constraintsSettings = constraintsSettings;
    }

    public InvoiceSettings getInvoiceSettings() {
        return invoiceSettings;
    }

    public void setInvoiceSettings(InvoiceSettings invoiceSettings) {
        this.invoiceSettings = invoiceSettings;
    }

    public Collection<DeductionDefinitionObj> getDeductionDefinitions() {
        return deductionDefinitions;
    }

    public void setDeductionDefinitions(Collection<DeductionDefinitionObj> deductionDefinitions) {
        this.deductionDefinitions = deductionDefinitions;
    }

    public InputDefaultsSettings getInputDefaultsSettings() {
        return inputDefaultsSettings;
    }

    public void setInputDefaultsSettings(InputDefaultsSettings inputDefaultsSettings) {
        this.inputDefaultsSettings = inputDefaultsSettings;
    }

    public PurchaseDetails getPurchaseDetails() {
        return purchaseDetails;
    }

    public void setPurchaseDetails(PurchaseDetails purchaseDetails) {
        this.purchaseDetails = purchaseDetails;
    }

    public Herd getHerd() {
        return herd;
    }

    public void setHerd(Herd herd) {
        this.herd = herd;
    }
}
