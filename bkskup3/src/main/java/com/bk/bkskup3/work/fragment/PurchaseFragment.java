package com.bk.bkskup3.work.fragment;

import android.app.Activity;
import android.app.Fragment;
import com.bk.bkskup3.model.PurchaseObj;
import com.bk.bkskup3.work.BusActivity;
import com.bk.bkskup3.work.fragment.event.FragmentEvent;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/15/2014
 * Time: 4:20 PM
 */
public abstract class PurchaseFragment extends Fragment {
    protected PurchaseObj mPurchase;

    public void setPurchase(PurchaseObj purchase) {
        this.mPurchase = purchase;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        ((BusActivity) activity).register(this);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        BusActivity activity = (BusActivity) getActivity();
        activity.unregister(this);

    }

    protected void postEvent(FragmentEvent event)
    {
        event.setFragmentTag(getTag());
        BusActivity activity = (BusActivity) getActivity();
        activity.post(event);
    }
}
