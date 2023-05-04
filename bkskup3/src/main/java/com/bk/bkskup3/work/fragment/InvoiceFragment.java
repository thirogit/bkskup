package com.bk.bkskup3.work.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bk.bkskup3.work.BusActivity;
import com.bk.bkskup3.work.InvoiceActivity;
import com.bk.bkskup3.work.fragment.event.FragmentEvent;
import com.bk.bkskup3.work.fragment.event.InvoiceServiceBoundEvent;
import com.bk.bkskup3.work.service.InvoiceService;
import com.squareup.otto.Subscribe;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/14/2014
 * Time: 2:30 PM
 */
public abstract class InvoiceFragment extends Fragment {

    private static final String TAG = "InvoiceFragment";
    protected InvoiceService mService;

    private List<Runnable> mRunAfterBound = new LinkedList<Runnable>();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mService = ((InvoiceActivity)getActivity()).getInvoiceService();
        Log.d(TAG, "on activity created "+ this.getTag()+", service=" + mService);
    }

    @Override
    public void onStop() {
        super.onStop();
        mService = null;
        BusActivity activity = (BusActivity) getActivity();
        activity.unregister(this);
        Log.d(TAG, "on stop "+ this.getTag()+", service=" + mService);
    }

    @Override
    public void onStart() {
        super.onStart();
        ((BusActivity) getActivity()).register(this);
        Log.d(TAG, "on start "+ this.getTag()+", service=" + mService);
        if(mService != null) {
            Log.d(TAG, "running pending "+ this.getTag()+", service=" + mService);
            runPending();
        }
    }


    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        Log.d(TAG, "on view created "+ this.getTag()+", service=" + mService);
    }

    public void onDestroyView() {
       super.onDestroyView();
        Log.d(TAG, "on destroy view "+ this.getTag()+", service=" + mService);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "on attach "+ this.getTag()+", service=" + mService);


    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "on detach "+ this.getTag()+", service=" + mService);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "on destroy "+ this.getTag()+", service=" + mService);
    }

    protected void scheduleAfterBoundService(Runnable runnable) {

//        this.onDestroyView();
        Log.d(TAG, "run after bound service " + this.getTag() + ", service=" + mService);
        if(mService != null)
        {
            runnable.run();
        }
        else
        {
            mRunAfterBound.add(runnable);
        }
    }

    @Subscribe
    public void onServiceBound(InvoiceServiceBoundEvent event)
    {
        mService = event.getService();
        Log.d(TAG, "on service bound "+ this.getTag()+", service=" + mService);

        if(mService != null) {
            Log.d(TAG,"running pending after set service " + this.getTag());
            runPending();
        }

    }

    protected void postEvent(FragmentEvent event)
    {
        event.setFragmentTag(getTag());
        BusActivity activity = (BusActivity) getActivity();
        activity.post(event);
    }

    public void setService(InvoiceService service) {
        this.mService = service;
            Log.d(TAG, "set service "+ this.getTag()+", service=" + mService);
    }

    private void runPending() {
        for (Runnable runnable : mRunAfterBound) {
            runnable.run();
        }
        mRunAfterBound.clear();
    }
}