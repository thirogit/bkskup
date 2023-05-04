package com.bk.bkskup3.print.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bk.bkskup3.R;
import com.bk.bkskup3.library.DocumentDescription;
import com.bk.bkskup3.print.PrintActivity;
import com.bk.bkskup3.print.adapters.DocumentsListAdapter;
import com.bk.bkskup3.print.event.BackendAvailableEvent;
import com.bk.bkskup3.print.event.DocumentSelected;
import com.bk.bkskup3.work.fragment.BusFragment;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by SG0891787 on 9/28/2017.
 */

public class DocumentsListFragment extends BusFragment {

    private static final String STATE_EXTRA_DOCUMENTS = "state_documents";

    ArrayList<DocumentDescription> mDocuments;
    DocumentsListAdapter mAdapter;
    ListView mListView;


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
        mAdapter = new DocumentsListAdapter(getActivity());

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DocumentDescription selectedDocument = mAdapter.getItem(position);
                postEvent(new DocumentSelected(selectedDocument.getDocCode()));
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                DocumentDescription selectedDocument = mAdapter.getItem(position);
                DocumentSelected event = new DocumentSelected(selectedDocument.getDocCode());
                event.setLongClick(true);
                postEvent(event);
                return true;
            }
        });

        mListView.setAdapter(mAdapter);

        if (state != null && state.containsKey(STATE_EXTRA_DOCUMENTS)) {
            mDocuments = (ArrayList<DocumentDescription>) state.getSerializable(STATE_EXTRA_DOCUMENTS);
        }
        refreshDocumentList();
    }


    public void setDocuments(Collection<DocumentDescription> documents) {
        if(documents != null) {
            this.mDocuments = new ArrayList<>(documents);
        }
        else
        {
            mDocuments = null;
        }
    }

        @Subscribe
    public void onBackendAvailable(BackendAvailableEvent event)
    {
        setDocuments(getPrintActivity().getDocuments());
        refreshDocumentList();
    }

    private PrintActivity getPrintActivity() {
        return (PrintActivity) getActivity();
    }

    private void refreshDocumentList() {

        if (mDocuments != null) {
            mAdapter.setNotifyOnChange(false);
            mAdapter.clear();

            for (DocumentDescription descriptor : mDocuments) {
                mAdapter.add(descriptor);
            }
            mAdapter.setNotifyOnChange(true);
            mAdapter.notifyDataSetChanged();
        }
    }


    public void onSaveInstanceState(Bundle state) {
        state.putSerializable(STATE_EXTRA_DOCUMENTS, mDocuments);

    }

}