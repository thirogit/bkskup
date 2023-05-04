package com.bk.bkskup3.library;

import com.bk.bands.DataSource;
import com.bk.bands.template.Template;
import com.bk.bkskup3.model.Invoice;
import com.google.common.base.Predicate;

import java.util.Collection;

/**
 * Created by SG0891787 on 2/14/2017.
 */

public class DocumentDefinition {
    String documentName;
    Template template;
    Class<? extends DataSource> dataSourceClass;
    Predicate<Invoice> documentFilter;
    String docId;
    Collection<DocumentOptionDefinition> optionDefinitions;

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    public Class<? extends DataSource> getDataSourceClass() {
        return dataSourceClass;
    }

    public void setDataSourceClass(Class<? extends DataSource> dataSourceClass) {
        this.dataSourceClass = dataSourceClass;
    }

    public Predicate<Invoice> getDocumentFilter() {
        return documentFilter;
    }

    public void setDocumentFilter(Predicate<Invoice> documentFilter) {
        this.documentFilter = documentFilter;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public Collection<DocumentOptionDefinition> getOptionDefinitions() {
        return optionDefinitions;
    }

    public void setOptionDefinitions(Collection<DocumentOptionDefinition> optionDefinitions) {
        this.optionDefinitions = optionDefinitions;
    }
}
