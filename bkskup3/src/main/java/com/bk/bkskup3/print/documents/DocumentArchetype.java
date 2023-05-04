package com.bk.bkskup3.print.documents;

import com.bk.bands.DataSource;
import com.bk.bands.serializer.DocumentFactory;
import com.bk.bands.serializer.DocumentBean;
import com.bk.bands.template.Template;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 2/28/12
 * Time: 11:06 PM
 */
public class DocumentArchetype {
    private String docName;
    private Template docTemplate;
    private DataSource dataSource;

    public DocumentArchetype() {
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public Template getDocTemplate() {
        return docTemplate;
    }

    public void setDocTemplate(Template docTemplate) {
        this.docTemplate = docTemplate;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DocumentBean createDocument() {
        return DocumentFactory.create(docName,docTemplate,dataSource);
    }
}
