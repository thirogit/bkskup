package com.bk.bkskup3.work;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import com.bk.bkskup3.BkActivity;
import com.bk.bkskup3.work.fragment.event.FragmentEvent;
import com.squareup.otto.Bus;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/24/2014
 * Time: 1:50 PM
 */
public class BusActivity extends BkActivity {

    Bus mBus = new Bus();

    public Bus getBus() {
        return mBus;
    }

    public void register(Fragment fragment)
    {
        mBus.register(fragment);
    }

    protected void onCreate(Bundle state)
    {
        super.onCreate(state);
        mBus.register(this);
    }

    protected void onDestroy()
    {
        super.onDestroy();
        mBus.unregister(this);
    }

    public void unregister(Fragment fragment)
    {
        mBus.unregister(fragment);
    }

    public void post(FragmentEvent event)
    {
        mBus.post(event);
    }
}
