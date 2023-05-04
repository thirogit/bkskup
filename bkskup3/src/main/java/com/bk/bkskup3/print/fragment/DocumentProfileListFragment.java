package com.bk.bkskup3.print.fragment;

/**
 * Created by SG0891787 on 9/28/2017.
 */

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bk.bkskup3.R;
import com.bk.bkskup3.library.DocumentProfile;
import com.bk.bkskup3.print.adapters.ProfilesListAdapter;
import com.bk.bkskup3.print.event.DoPrintProfile;
import com.bk.bkskup3.work.fragment.BusFragment;

import java.util.ArrayList;
import java.util.Collection;

public class DocumentProfileListFragment extends BusFragment {

    private static final String STATE_EXTRA_PROFILES = "state_profiles";

    ArrayList<DocumentProfile> mProfiles;
    ArrayAdapter<DocumentProfile> mAdapter;
    ListView mListView;


    private void refreshProfilesList() {


        if (mProfiles != null) {
            mAdapter.setNotifyOnChange(false);
            mAdapter.clear();

            for (DocumentProfile profile : mProfiles) {
                mAdapter.add(profile);
            }
            mAdapter.setNotifyOnChange(true);
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        View v = inflater.inflate(R.layout.list_fragment, container, false);
        return v;
    }

    public void onViewCreated(View view, Bundle state) {
        super.onViewCreated(view, state);

        mListView = (ListView) view.findViewById(R.id.list);

        mAdapter = new ProfilesListAdapter(getActivity());

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DocumentProfile item = mAdapter.getItem(position);
                postEvent(new DoPrintProfile(item));
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                DocumentProfile item = mAdapter.getItem(position);
                DoPrintProfile event = new DoPrintProfile(item);
                event.setLongClick(true);
                postEvent(event);
                return true;
            }
        });

        mListView.setAdapter(mAdapter);

        if (state != null && state.containsKey(STATE_EXTRA_PROFILES)) {
            mProfiles = (ArrayList<DocumentProfile>) state.getSerializable(STATE_EXTRA_PROFILES);
        }
        refreshProfilesList();
    }


    public void onSaveInstanceState(Bundle state) {
        state.putSerializable(STATE_EXTRA_PROFILES, mProfiles);
    }

    public void setProfiles(Collection<DocumentProfile> profiles) {
        this.mProfiles = new ArrayList<>(profiles);
    }
}