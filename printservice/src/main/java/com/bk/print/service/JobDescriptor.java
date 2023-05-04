package com.bk.print.service;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/27/2014
 * Time: 10:12 PM
 */
public class JobDescriptor implements Serializable {
    UUID jobId;
    String jobName;
    int progress;
    JobStatus status;
    String errorCode;

    public JobDescriptor(UUID jobId, String jobName, JobStatus status) {
        this.jobId = jobId;
        this.jobName = jobName;
        this.status = status;
    }

    public UUID getJobId() {
        return jobId;
    }

    public void setJobId(UUID jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public int getProgress() {
        return progress;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
