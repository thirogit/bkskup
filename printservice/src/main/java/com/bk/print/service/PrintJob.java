package com.bk.print.service;

import com.bk.bands.serializer.DocumentBean;

import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/27/2014
 * Time: 10:17 PM
 */
public class PrintJob {
    private UUID jobId;
    private DocumentBean document;

    public PrintJob(UUID jobId, DocumentBean document) {
        this.jobId = jobId;
        this.document = document;
    }

    public UUID getJobId() {
        return jobId;
    }

    public DocumentBean getDocument() {
        return document;
    }
}
