package com.bk.bkskup3.management;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bk.bkskup3.R;
import com.bk.bkskup3.feedback.ErrorToast;
import com.bk.bkskup3.library.DocumentLibraryService;
import com.bk.bkskup3.library.DocumentProfile;

import java.util.Collection;

public class EditDocumentProfilesActivity extends Activity {

    public static final String EXTRA_DOCUMENT_CODE = "document_code";
    private static final int REQUEST_EDIT_PROFILE = 1001;
    private static final int REQUEST_NEW_PROFILE = 1002;

    private ListView mList;
    private ProfileListAdapter mAdapter;
    private DocumentLibraryService mService;
    private View mProgressContainer;
    private View mContentContainer;
    private String mDocumentCode;
    private Handler mUIBridge = new Handler();

    private class ProfileListAdapter extends ArrayAdapter<DocumentProfile> {
        public ProfileListAdapter() {
            super(EditDocumentProfilesActivity.this, android.R.layout.simple_list_item_1);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View row;
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (null == convertView) {
                row = inflater.inflate(android.R.layout.simple_list_item_1, null);
            } else {
                row = convertView;
            }

            TextView text1 = (TextView) row.findViewById(android.R.id.text1);
            text1.setText(getItem(position).getProfileName());
            return row;
        }
    }


    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.doc_profiles);
        ensureContent();

        Intent intent = getIntent();
        mDocumentCode = intent.getStringExtra(EXTRA_DOCUMENT_CODE);
    }

    private void ensureContent() {
        mContentContainer = findViewById(R.id.content_container);
        mProgressContainer = findViewById(R.id.progress_container);
        mList = (ListView) findViewById(R.id.list);
        mAdapter = new ProfileListAdapter();
        mList.setAdapter(mAdapter);
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

    private void continueResume() {

        refreshProfilesList();
        showContent();
    }

    private void refreshProfilesList() {
        mService.getDocumentProfiles(mDocumentCode, new DocumentLibraryService.Callback<Collection<DocumentProfile>>() {
            @Override
            public void callback(final Collection<DocumentProfile> result) {

                mUIBridge.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.clear();
                        mAdapter.addAll(result);
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        if (mList.getCheckedItemPosition() >= 0) {
            inflater.inflate(R.menu.profiles_crud_menu, menu);
        } else {
            inflater.inflate(R.menu.profiles_add_menu, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menuEdit: {

                int selected = mList.getCheckedItemPosition();
                DocumentProfile profileToEdit = mAdapter.getItem(selected);

                Intent editProfileIntent = new Intent(this, DocumentProfilesActivity.class);
                editProfileIntent.putExtra(DocumentProfilesActivity.EXTRA_DOC_CODE, mDocumentCode);
//                editProfileIntent.putExtra(DocumentProfilesActivity.EXTRA_PROFILE_ID, profileToEdit.getProfileId());
                startActivityForResult(editProfileIntent, REQUEST_EDIT_PROFILE);
                break;
            }
            case R.id.menuAdd:
                Intent newProfileIntent = new Intent(this,DocumentProfilesActivity.class);
                newProfileIntent.putExtra(DocumentProfilesActivity.EXTRA_DOC_CODE,mDocumentCode);
                startActivityForResult(newProfileIntent,REQUEST_NEW_PROFILE);
                break;
            case R.id.menuDelete:
                deleteProfileWithQuestion();
                break;
//            case R.id.menuFinish:
//                finish();
//                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteProfileWithQuestion() {
        final DocumentProfile profileToDelete = getSelectedProfile();
        if (profileToDelete != null) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        mService.deleteProfile(profileToDelete.getProfileId());
                        mList.clearChoices();
                        refreshProfilesList();
                        invalidateOptionsMenu();
                    }
                }
            };

            Resources r = getResources();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(r.getString(R.string.askDeleteProfile, profileToDelete.getProfileName()));
            builder.setPositiveButton(android.R.string.yes, dialogClickListener);
            builder.setNegativeButton(android.R.string.no, dialogClickListener);
            builder.show();

        } else {
            showNoItemSelectedError();
        }
    }

    private void showNoItemSelectedError() {

            new ErrorToast(this).show(R.string.errProfileNotSelected);

    }

    public DocumentProfile getSelectedProfile() {
        int selected = mList.getCheckedItemPosition();
        if(selected >= 0) {
            return mAdapter.getItem(selected);
        }
        return null;

    }


    private void showContent() {
        mProgressContainer.setVisibility(View.GONE);
        mContentContainer.setVisibility(View.VISIBLE);
    }


    protected void showLoading() {

        mContentContainer.setVisibility(View.GONE);
        mProgressContainer.setVisibility(View.VISIBLE);
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
