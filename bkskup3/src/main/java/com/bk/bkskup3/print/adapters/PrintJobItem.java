package com.bk.bkskup3.print.adapters;

import com.bk.print.service.JobStatus;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by SG0891787 on 9/25/2017.
 */
public class PrintJobItem implements Serializable {
    public UUID jobId;
    public String jobName;
    public int progress;
    public JobStatus state;
}
