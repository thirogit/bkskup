package com.bk.bkskup3.work.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;

import com.bk.bkskup3.work.BusActivity;
import com.bk.bkskup3.work.fragment.event.FragmentEvent;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/14/2014
 * Time: 2:30 PM
 */


public abstract class BusDialogFragment extends DialogFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
        BusActivity activity = (BusActivity) getActivity();
        activity.unregister(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        ((BusActivity) getActivity()).register(this);
    }


    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
    }

    public void onDestroyView() {
       super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected void postEvent(FragmentEvent event)
    {
        event.setFragmentTag(getTag());
        BusActivity activity = (BusActivity) getActivity();
        activity.post(event);
    }

}