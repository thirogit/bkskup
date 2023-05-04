package com.bk.print.service;

import java.util.UUID;

/**
 * Created by SG0891787 on 10/11/2017.
 */
public class PrintJobLogEntry {
    private UUID id;
    private JobStatus status;
    private String documentName;
    private String errorCode;

    public PrintJobLogEntry() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
