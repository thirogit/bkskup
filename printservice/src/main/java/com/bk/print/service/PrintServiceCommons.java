package com.bk.print.service;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/9/13
 * Time: 12:01 PM
 */
public class PrintServiceCommons {
    public static final int MSG_REGISTER_LISTENER = 1;
    public static final int MSG_UNREGISTER_LISTENER = 2;
    public static final int MSG_GET_PRINTER_ADDRESS = 5;
    public static final int MSG_SET_PRINTER_ADDRESS = 6;
    public static final int MSG_GET_STATE = 7;
    public static final int MSG_SUBMIT_JOB = 8;
    public static final int MSG_LIST_JOBS = 9;
    public static final int MSG_ABORT_JOB = 10;

    public static final int MSG_NOTIFY_CONNECTED = 9;
    public static final int MSG_NOTIFY_CONNECTING = 10;
    public static final int MSG_NOTIFY_DISCONNECTED = 11;
    public static final int MSG_NOTIFY_PRINTER_ADDRESS = 12;
    public static final int MSG_NOTIFY_JOB_SUBMITTED = 13;
    public static final int MSG_NOTIFY_JOB_STARTED = 14;
    public static final int MSG_NOTIFY_JOB_PROGRESS = 15;
    public static final int MSG_NOTIFY_JOB_COMPLETED = 16;
    public static final int MSG_NOTIFY_JOB_ABORTED = 17;
    public static final int MSG_NOTIFY_JOB_ERROR = 18;
    public static final int MSG_NOTIFY_JOBS_LIST = 19;

    public static final String MSG_DOCUMENT_KEY = "document";
    public static final String MSG_JOBID_KEY = "job_id";
    public static final String MSG_PROGRESS_KEY = "progress";
    public static final String MSG_JOBIDS_KEY = "job_ids";
    public static final String MSG_ERROR_CODE = "error_code";
    public static final String MSG_DESCRIPTOR_KEY = "job_descriptor";
    public static final String MSG_JOBSLIST_KEY = "jobs_list";
    public static final String MSG_PRINTER_BTADDR_KEY = "printer_bt_address";

    public static final String ERRCODE_NOT_CONNECTED = "NOT_CONNECTED";

}
