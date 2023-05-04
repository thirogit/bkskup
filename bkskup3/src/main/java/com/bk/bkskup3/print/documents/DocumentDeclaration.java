package com.bk.bkskup3.print.documents;

import com.bk.bands.DataSource;
import com.bk.bkskup3.model.Invoice;
import com.google.common.base.Predicate;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 4/3/12
 * Time: 10:31 AM
 */
public class DocumentDeclaration {
    int docNameResId;
    int templateResId;
    Class<? extends DataSource> dataSourceClass;
    Predicate<Invoice> documentFilter;
    String docId;
    int optionsDefsResId;

    public DocumentDeclaration(String docId,
                               int docNameResId,
                               int templateResId,
                               Class<? extends DataSource> dataSourceClass,
                               Predicate<Invoice> documentFilter) {
        this.docNameResId = docNameResId;
        this.templateResId = templateResId;
        this.dataSourceClass = dataSourceClass;
        this.documentFilter = documentFilter;
        this.docId = docId;
    }

    public DocumentDeclaration(String docId,
                               int docNameResId,
                               int templateResId,
                               Class<? extends DataSource> dataSourceClass,
                               Predicate<Invoice> documentFilter, int optionsDefsResId) {
        this(docId, docNameResId, templateResId, dataSourceClass, documentFilter);
        this.optionsDefsResId = optionsDefsResId;
    }

    public String getDocId() {
        return docId;
    }

    public int getDocNameResId() {
        return docNameResId;
    }

    public int getTemplateResId() {
        return templateResId;
    }

    public Class<? extends DataSource> getDataSourceClass() {
        return dataSourceClass;
    }

    public boolean appliesTo(Invoice invoice) {
        return documentFilter.apply(invoice);
    }

    public int getOptionsDefsResId() {
        return optionsDefsResId;
    }

    public Predicate<Invoice> getDocumentFilter() {
        return documentFilter;
    }
}
