package com.bk.bkskup3.dao;


import com.bk.bkskup3.db.SQLDatabaseQueue;

public class BkStore {

    private SQLDatabaseQueue mDbQueue;

    private HentsStore hentsStore;
    private PurchasesStore purchasesStore;
    private DefinitionsStore definitionsStore;
    private DocumentOptionsStore documentOptionsStore;
    private InvoiceNoTransactionStore invoiceNoTransactionStore;
    private SettingsStore settingsStore;


    public BkStore(SQLDatabaseQueue mDbQueue) {
        this.mDbQueue = mDbQueue;

        this.hentsStore = new HentsStore(mDbQueue);
        this.purchasesStore = new PurchasesStore(mDbQueue);
        this.definitionsStore = new DefinitionsStore(mDbQueue);
        this.documentOptionsStore = new DocumentOptionsStore(mDbQueue);
        this.invoiceNoTransactionStore = new InvoiceNoTransactionStore(mDbQueue);
        this.settingsStore = new SettingsStore(mDbQueue);
    }

    public HentsStore getHentsStore() {
        return hentsStore;
    }

    public PurchasesStore getPurchasesStore() {
        return purchasesStore;
    }

    public DefinitionsStore getDefinitionsStore() {
        return definitionsStore;
    }

    public DocumentOptionsStore getDocumentOptionsStore() {
        return documentOptionsStore;
    }

    public InvoiceNoTransactionStore getInvoiceNoTransactionStore() {
        return invoiceNoTransactionStore;
    }

    public SettingsStore getSettingsStore() {
        return settingsStore;
    }
}
