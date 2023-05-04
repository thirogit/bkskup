package com.bk.bkskup3.management;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.*;
import android.widget.*;
import com.bk.bkskup3.R;
import com.bk.bkskup3.library.DocumentDefinition;
import com.bk.bkskup3.library.DocumentLibraryService;
import com.bk.bkskup3.library.DocumentPreference;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;

public class DocumentsCfgActivity extends ListActivity {

    static class DocumentItem {
        String docCode;
        boolean visible;
        boolean hasOptions;
        String title;
    }


    class DocumentListAdapter extends ArrayAdapter<DocumentItem> {
        public DocumentListAdapter(Context context) {
            super(context, R.layout.documents_list_item);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row;
            LayoutInflater inflater = LayoutInflater.from(getContext());

            if (null == convertView) {
                row = inflater.inflate(R.layout.documents_list_item, null);
            } else {
                row = convertView;
            }

            final DocumentItem item = getItem(position);

            TextView documentTitleBox = (TextView) row.findViewById(R.id.documentTitleBox);
            CheckBox documentVisibleCheck = (CheckBox) row.findViewById(R.id.documentVisibleCheck);

            documentTitleBox.setText(item.title);
            documentVisibleCheck.setChecked(item.visible);

            documentVisibleCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean checked) {
                    onDocumentVisibleCheck(item,checked);
                }
            });

            return row;
        }


    }


    private ListView mList;
    private DocumentListAdapter mAdapter;
    private DocumentLibraryService mService;
    private View mProgressContainer;
    private View mContentContainer;

    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.documents_cfg);
        ensureContent();
    }

    private void ensureContent() {
        mContentContainer = findViewById(R.id.content_container);
        mProgressContainer = findViewById(R.id.progress_container);
        mList = (ListView) findViewById(android.R.id.list);
        mAdapter = new DocumentListAdapter(this);
        mList.setAdapter(mAdapter);

        mList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onDocumentSelect((DocumentItem) adapterView.getItemAtPosition(i));
            }
        });
    }

    private void onDocumentSelect(DocumentItem item) {
        invalidateOptionsMenu();
    }

    protected void onResume() {
        super.onResume();

        if (mService == null) {
            showLoading();
            bindService(new Intent(this, DocumentLibraryService.class), mConnection, Context.BIND_AUTO_CREATE);
        } else {
            continueResume();
        }
    }

    protected void onStop()
    {
        super.onStop();

        if (mService != null) {
            unbindService(mConnection);
            mService = null;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        int position = mList.getCheckedItemPosition();
        if(position >= 0 && position < mAdapter.getCount())
        {
            DocumentItem item = mAdapter.getItem(position);
            if(item.hasOptions) {
                if (mService != null) {
                    inflater.inflate(R.menu.document_cfg_menu, menu);
                }
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menuEditProfiles:
                editProfiles();
                break;

//            case R.id.menuFinish:
//                finish();
//                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void editProfiles() {
        int itemPos = mList.getCheckedItemPosition();
        if(itemPos >= 0)
        {
            DocumentItem item = mAdapter.getItem(itemPos);
            Intent intent = new Intent(this, DocumentProfilesActivity.class);
            intent.putExtra(DocumentProfilesActivity.EXTRA_DOC_CODE,item.docCode);
            startActivity(intent);
        }
    }

    private void continueResume() {

            refreshDocumentsList();
            showContent();
            invalidateOptionsMenu();
    }

    private void refreshDocumentsList() {
        Collection<DocumentDefinition> definitions = mService.getDocumentDefinitions();
        Collection<DocumentPreference> preferences = mService.getDocumentPreferences();
        Map<String, DocumentPreference> preferenceMap = Maps.uniqueIndex(preferences, new Function<DocumentPreference, String>() {
            @Nullable
            @Override
            public String apply(DocumentPreference preference) {
                return preference.getDocumentCode();
            }
        });

        mAdapter.clear();
        mAdapter.setNotifyOnChange(false);
        for (DocumentDefinition definition : definitions) {
            DocumentItem item = new DocumentItem();
            item.docCode = definition.getDocId();
            DocumentPreference preference = preferenceMap.get(item.docCode);
            item.title = definition.getDocumentName();
            item.visible = preference != null ? preference.isVisible() : true;
            item.hasOptions = !Iterables.isEmpty(definition.getOptionDefinitions());
            mAdapter.add(item);
        }
        mAdapter.setNotifyOnChange(true);
        mAdapter.notifyDataSetChanged();
    }

    private void showContent() {
        mProgressContainer.setVisibility(View.GONE);
        mContentContainer.setVisibility(View.VISIBLE);
    }


    protected void showLoading() {
        mContentContainer.setVisibility(View.GONE);
        mProgressContainer.setVisibility(View.VISIBLE);
    }

    private void onDocumentVisibleCheck(DocumentItem item, boolean visible) {
        mService.setVisiblePreference(item.docCode,visible);
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {

            DocumentLibraryService.LocalBinder binder = (DocumentLibraryService.LocalBinder) service;
            mService = binder.getService();
            continueResume();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
        }
    };

}
