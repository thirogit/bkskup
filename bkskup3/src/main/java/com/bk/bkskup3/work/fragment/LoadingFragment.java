package com.bk.bkskup3.work.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bk.bkskup3.R;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/14/2014
 * Time: 10:26 PM
 */
public class LoadingFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.loading, container, false);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

}
