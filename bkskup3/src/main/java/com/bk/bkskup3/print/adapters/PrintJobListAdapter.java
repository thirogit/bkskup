package com.bk.bkskup3.print.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bk.bkskup3.R;
import com.bk.print.service.JobDescriptor;
import com.bk.print.service.JobStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by SG0891787 on 9/25/2017.
 */
public class PrintJobListAdapter extends ArrayAdapter<PrintJobItem> {

    private Map<UUID, PrintJobItem> mItemMap = new HashMap<>();

    public PrintJobListAdapter(Context context) {
        super(context, R.layout.print_job_list_item);
    }


    public PrintJobItem[] toArray() {
        int count = getCount();
        if (count > 0) {
            ArrayList<PrintJobItem> itemList = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                itemList.add(getItem(i));
            }
            return itemList.toArray(new PrintJobItem[count]);
        }
        return null;
    }

    public void addAll(PrintJobItem... items)
    {
       for(PrintJobItem item : items)
       {
           mItemMap.put(item.jobId,item);
       }
       super.addAll(items);
    }

    public UUID[] getIds()
    {
        int count = getCount();
        if (count > 0) {
            ArrayList<UUID> itemList = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                itemList.add(getItem(i).jobId);
            }
            return itemList.toArray(new UUID[count]);
        }
        return null;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        if (null == convertView) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.print_job_list_item, null);
        } else {
            row = convertView;
        }

        PrintJobItem printJobItem = getItem(position);

        TextView printJobTitleLbl = (TextView) row.findViewById(R.id.printJobTitleLbl);
        printJobTitleLbl.setText(printJobItem.jobName);

        ProgressBar printJobProgress = (ProgressBar) row.findViewById(R.id.printJobProgress);
        TextView jobActionNameLbl = (TextView) row.findViewById(R.id.jobActionNameLbl);
        TextView jobStatusMsgLbl = (TextView) row.findViewById(R.id.jobStatusMsgLbl);

        switch (printJobItem.state) {
            case Completed:
                jobActionNameLbl.setText(R.string.printJobCompleted);
                printJobProgress.setVisibility(View.INVISIBLE);
                jobStatusMsgLbl.setVisibility(View.INVISIBLE);
                break;
            case Error:
                jobActionNameLbl.setText(R.string.printJobError);
                printJobProgress.setVisibility(View.INVISIBLE);
                jobStatusMsgLbl.setVisibility(View.VISIBLE);
//                    jobStatusMsgLbl.setText(printJobItem.getErrorMsg());
                break;
            case Printing:
                jobActionNameLbl.setText(R.string.printJobPrinting);
                printJobProgress.setVisibility(View.VISIBLE);
                printJobProgress.setMax(100);
                printJobProgress.setProgress(printJobItem.progress);
                jobStatusMsgLbl.setVisibility(View.INVISIBLE);
                break;
            case Waiting:
                jobActionNameLbl.setText(R.string.printJobWaiting);
                printJobProgress.setVisibility(View.INVISIBLE);
                jobStatusMsgLbl.setVisibility(View.INVISIBLE);
                break;
            case Aborted:
                jobActionNameLbl.setText(R.string.printJobAborted);
                printJobProgress.setVisibility(View.INVISIBLE);
                jobStatusMsgLbl.setVisibility(View.VISIBLE);
                break;
        }

        return row;
    }

    public PrintJobItem findJobItem(UUID jobId) {
        return mItemMap.get(jobId);
    }

    private PrintJobItem createJobItem(JobDescriptor job) {
        PrintJobItem item = new PrintJobItem();
        item.state = job.getStatus();
        item.jobId = job.getJobId();
        item.jobName = job.getJobName();
        item.progress = job.getProgress();
        return item;
    }

    public void addJob(JobDescriptor job) {
        silentAddJob(job);
        notifyDataSetChanged();
    }

    private void silentAddJob(JobDescriptor job) {
        PrintJobItem item = mItemMap.get(job.getJobId());
        if(item != null)
        {
            item.jobName = job.getJobName();
            item.progress = job.getProgress();
            item.state = job.getStatus();
        }
        else
        {
            item = createJobItem(job);
            mItemMap.put(job.getJobId(),item);
            add(item);
        }
    }

    public void addJobs(Collection<JobDescriptor> jobs) {
        for(JobDescriptor job : jobs)
        {
            silentAddJob(job);
        }
        notifyDataSetChanged();
    }

    public void jobStarted(UUID jobId) {
        PrintJobItem item = findJobItem(jobId);
        if (item != null) {
            item.state = JobStatus.Printing;
            notifyDataSetChanged();
        }
    }

    public void jobProgress(UUID jobId, int progress) {
        PrintJobItem item = findJobItem(jobId);
        if (item != null) {
            item.state = JobStatus.Printing;
            item.progress = progress;
            notifyDataSetChanged();
        }
    }

    public void jobCompleted(UUID jobId) {
        PrintJobItem item = findJobItem(jobId);
        if (item != null) {
            item.state = JobStatus.Completed;
            notifyDataSetChanged();
        }
    }

    public void jobAborted(UUID jobId) {
        PrintJobItem item = findJobItem(jobId);
        if (item != null) {
            item.state = JobStatus.Aborted;
            notifyDataSetChanged();
        }
    }

    public void jobError(UUID jobId,String errorCd) {
            PrintJobItem item = findJobItem(jobId);
            if (item != null) {
                item.state = JobStatus.Error;
                notifyDataSetChanged();
            }
    }
}
