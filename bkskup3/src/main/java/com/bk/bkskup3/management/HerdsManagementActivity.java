package com.bk.bkskup3.management;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bk.bkskup3.BkActivity;
import com.bk.bkskup3.R;
import com.bk.bkskup3.adapters.HerdListAdapter;
import com.bk.bkskup3.dao.BkStore;
import com.bk.bkskup3.dao.DefinitionsStore;
import com.bk.bkskup3.feedback.ErrorMessageFragment;
import com.bk.bkskup3.feedback.ErrorToast;
import com.bk.bkskup3.model.CompanyObj;
import com.bk.bkskup3.model.Herd;
import com.bk.bkskup3.model.HerdObj;
import com.bk.bkskup3.tasks.TaskResult;
import com.bk.bkskup3.tasks.download.DownloadCompanyTask;
import com.bk.bkskup3.tasks.download.DownloadHerdsTask;
import com.bk.bkskup3.tasks.download.DownloadTaskObserver;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/9/2014
 * Time: 1:50 PM
 */
public class HerdsManagementActivity extends BkActivity {

    private static final String RETAINER_FRAGMENT_TAG = "retainer";
    private static final String ERRORMSG_FRAGMENT_TAG = "error_fragment";

    private ListView mHerdsListView;
    private ArrayAdapter<Herd> mListAdapter;

    private State mState;
    private View mProgressContainer;
    private View mContentContainer;
    protected ErrorMessageFragment mErrMsgFragment;
    private DownloadHerdsTask mDownloadTask;

    @Inject
    DefinitionsStore mDefinitionsStore;

    @Inject
    BkStore mStore;

    enum State {
        Downloading,
        ShowingError,
        Idle,
    }

    public static class Retainer extends Fragment {
        public Retainer() {
            setRetainInstance(true);
        }

        public DownloadHerdsTask downloadTask;
    }

    DownloadTaskObserver<List<HerdObj>> mDownloadTaskObserver = new DownloadTaskObserver<List<HerdObj>>() {
        @Override
        public void onLoadStarted() {
            showLoading();
        }

        @Override
        public void onLoadSuccessful(List<HerdObj> result) {

            onDownloaded(result);
        }

        @Override
        public void onLoadError(Exception e) {
            mState = State.ShowingError;
            showError(e.getMessage());
        }
    };


    private ErrorMessageFragment.ErrorFragmentListener mOnErrorHideListener = new ErrorMessageFragment.ForwardErrorFragmentListener() {
        @Override
        public void onAbandon() {
            mState = State.Idle;
            mErrMsgFragment = null;
            showContent();
        }
        @Override
        public void onRetry() {
            download();
        }
    };

    private static final int REQUEST_CODE_NEW_HERD = 101;
    private static final int REQUEST_CODE_EDIT_HERD = 102;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.herds_management);

        mContentContainer = findViewById(R.id.content_container);
        mProgressContainer = findViewById(R.id.progress_container);

        mHerdsListView = (ListView) findViewById(R.id.list);
        mListAdapter = new HerdListAdapter(this);

        mHerdsListView.setAdapter(mListAdapter);
        mHerdsListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        mHerdsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                invalidateOptionsMenu();
            }
        });

        FragmentManager fm = getFragmentManager();
        Retainer retainer = (Retainer) fm.findFragmentByTag(RETAINER_FRAGMENT_TAG);
        if (retainer != null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(retainer);
            ft.commit();

            mDownloadTask = retainer.downloadTask;
        }

        if(mState == null || mState == State.Idle ) {
            refreshHerdsList();
        }
    }

    private void showContent() {
        mProgressContainer.setVisibility(View.GONE);
        mContentContainer.setVisibility(View.VISIBLE);
    }

    private void showError(String error) {
        mErrMsgFragment = new ErrorMessageFragment();
        mErrMsgFragment.setCaption(getResources().getString(R.string.errorCaption));
        mErrMsgFragment.setErrorMsg(error);
        mErrMsgFragment.setListener(mOnErrorHideListener);
        mErrMsgFragment.show(getFragmentManager(), ERRORMSG_FRAGMENT_TAG);
    }

    protected void showLoading() {

        mContentContainer.setVisibility(View.GONE);
        mProgressContainer.setVisibility(View.VISIBLE);
    }

    private void onDownloaded(List<HerdObj> downloadedHerds) {
        mState = State.Idle;

        Collection<HerdObj> herds = mDefinitionsStore.fetchAllHerds();
        Map<Integer, HerdObj> byHerdNo = Maps.uniqueIndex(herds, herd -> herd.getHerdNo());

        List<HerdObj> mergedHerds = new ArrayList<>(downloadedHerds.size() + herds.size());
        for(Herd downloadedHerd : downloadedHerds)
        {
            HerdObj existingHerd = byHerdNo.get(downloadedHerd.getHerdNo());
            if(existingHerd == null)
            {
                existingHerd = mDefinitionsStore.insertHerd(downloadedHerd);
            }
            else
            {
                existingHerd.copyFrom(downloadedHerd);
                mDefinitionsStore.updateHerd(existingHerd);
            }

            mergedHerds.add(existingHerd);
        }

        mListAdapter.clear();
        mListAdapter.addAll(mergedHerds);
        mListAdapter.notifyDataSetChanged();
        showContent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();

        if(getSelectedHerd() != null) {
            inflater.inflate(R.menu.cloud_add_del_edit_menu, menu);
        }
        else {
            inflater.inflate(R.menu.cloud_add_menu, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    protected void onResume() {

        super.onResume();

        if (mState == null || mState == State.Idle) {
            showContent();
            return;
        }

        if (mState == State.Downloading) {
            if (mDownloadTask != null) {

                if (mDownloadTask.getStatus() == AsyncTask.Status.FINISHED) {
                    TaskResult<List<HerdObj>> result = mDownloadTask.getResult();
                    if (result.isError()) {
                        mState = State.ShowingError;
                        showError(result.getException().getMessage());
                    } else {
                        mDownloadTaskObserver.onLoadSuccessful(result.getResult());
                    }
                } else {
                    showLoading();
                    mDownloadTask.attachObserver(mDownloadTaskObserver);
                }
            } else {
                download();
            }
            return;
        }

        if (mState == State.ShowingError) {
            mErrMsgFragment = (ErrorMessageFragment) getFragmentManager().findFragmentByTag(ERRORMSG_FRAGMENT_TAG);
            mErrMsgFragment.setListener(mOnErrorHideListener);
            return;
        }
    }

    private void download() {
        mState = State.Downloading;
        mDownloadTask = new DownloadHerdsTask(mStore);
        mDownloadTask.attachObserver(mDownloadTaskObserver);
        mDownloadTask.execute();
    }



    public void retainNonConfigurationInstance() {
        Retainer retainer = new Retainer();
        retainer.downloadTask = mDownloadTask;
        getFragmentManager().beginTransaction().add(retainer, RETAINER_FRAGMENT_TAG).commit();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menuAdd:
                addHerd();
                break;
            case R.id.menuDelete:
                deleteHerdWithQuestion();
                break;
            case R.id.menuEdit:
                editHerd();
                break;
            case R.id.menuDownload:
                download();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;

    }


    private void refreshHerdsList() {
        Collection<HerdObj> herds = mDefinitionsStore.fetchAllHerds();
        mListAdapter.clear();
        mListAdapter.addAll(herds);
        mListAdapter.notifyDataSetChanged();
    }


    private Herd getSelectedHerd() {
        int checkedItemPos = mHerdsListView.getCheckedItemPosition();
        if (checkedItemPos >= 0) {
            return mListAdapter.getItem(checkedItemPos);
        }

        return null;
    }

    private void showNoHerdSelectedError() {
        new ErrorToast(this).show(R.string.errHerdNotSelected);
    }

    private void editHerd() {

        Herd herdToEdit = getSelectedHerd();
        if (herdToEdit == null) {
            showNoHerdSelectedError();
            return;
        }

        Intent editHerd = new Intent(this, EditHerdActivity.class);
        editHerd.putExtra(EditHerdActivity.EXTRAS_HERDID, herdToEdit.getId());
        startActivityForResult(editHerd, REQUEST_CODE_EDIT_HERD);

    }

    private void addHerd() {
        startActivityForResult(new Intent(this, NewHerdActivity.class), REQUEST_CODE_NEW_HERD);
    }

    private void deleteHerdWithQuestion() {
        final Herd herdToDelete = getSelectedHerd();
        if(herdToDelete != null)
        {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        deleteHerd(herdToDelete);
                        mHerdsListView.clearChoices();
                        refreshHerdsList();
                    }
                }
            };

            Resources r = getResources();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(r.getString(R.string.askDeleteHerd, herdToDelete.getHerdNo()));
            builder.setPositiveButton(android.R.string.yes, dialogClickListener);
            builder.setNegativeButton(android.R.string.no, dialogClickListener);
            builder.show();

        } else {
            showNoHerdSelectedError();
        }
    }

    private void deleteHerd(Herd herd)
    {
        mDefinitionsStore.deleteHerd(herd.getId());
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        refreshHerdsList();
    }

}
