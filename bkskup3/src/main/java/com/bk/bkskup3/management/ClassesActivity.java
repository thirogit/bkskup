package com.bk.bkskup3.management;

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
import com.bk.bkskup3.adapters.ClassesListAdapter;
import com.bk.bkskup3.dao.BkStore;
import com.bk.bkskup3.dao.DefinitionsStore;
import com.bk.bkskup3.feedback.ErrorMessageFragment;
import com.bk.bkskup3.feedback.ErrorToast;
import com.bk.bkskup3.model.CowClass;
import com.bk.bkskup3.model.CowClassObj;
import com.bk.bkskup3.tasks.TaskResult;
import com.bk.bkskup3.tasks.download.DownloadClassesTask;
import com.bk.bkskup3.tasks.download.DownloadTaskObserver;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/9/2014
 * Time: 1:50 PM
 */
public class ClassesActivity extends BkActivity {

    private static final int REQUEST_CODE_NEW_CLASS = 101;
    private static final int REQUEST_CODE_EDIT_CLASS = 102;
    private static final String RETAINER_FRAGMENT_TAG = "retainer";
    private static final String ERRORMSG_FRAGMENT_TAG = "error_fragment";

    private ListView mListView;
    private ArrayAdapter<CowClass> mListAdapter;

    private State mState;
    private View mProgressContainer;
    private View mContentContainer;
    protected ErrorMessageFragment mErrMsgFragment;
    private DownloadClassesTask mDownloadTask;

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

        public DownloadClassesTask downloadTask;
    }

    DownloadTaskObserver<List<CowClassObj>> mDownloadTaskObserver = new DownloadTaskObserver<List<CowClassObj>>() {
        @Override
        public void onLoadStarted() {
            showLoading();
        }

        @Override
        public void onLoadSuccessful(List<CowClassObj> result) {

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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cowclass_management);

        mContentContainer = findViewById(R.id.content_container);
        mProgressContainer = findViewById(R.id.progress_container);


        mListView = (ListView) findViewById(R.id.list);
        mListAdapter = new ClassesListAdapter(this);

        mListView.setAdapter(mListAdapter);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
            refreshClassesList();
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

    private void onDownloaded(List<CowClassObj> downloadedClasses) {
        mState = State.Idle;

        Set<CowClassObj> uniqueDownloadedClasses = new TreeSet<>((o1, o2) -> o1.getClassCode().compareTo(o2.getClassCode()));
        uniqueDownloadedClasses.addAll(downloadedClasses);
        Collection<CowClassObj> classes = mDefinitionsStore.fetchAllClasses();
        Map<String, CowClassObj> byClassCode = Maps.uniqueIndex(classes, cowClass -> cowClass.getClassCode());

        List<CowClassObj> mergedClasses = new ArrayList<>(uniqueDownloadedClasses.size() + classes.size());
        for(CowClassObj downloadedClass : uniqueDownloadedClasses)
        {
            CowClassObj existingClass = byClassCode.get(downloadedClass.getClassCode());
            if(existingClass == null)
            {
                existingClass = mDefinitionsStore.insertClass(downloadedClass);
            }
            else
            {
                existingClass.copyFrom(downloadedClass);
                mDefinitionsStore.updateClass(existingClass);
            }

            mergedClasses.add(existingClass);
        }

        mListAdapter.clear();
        mListAdapter.addAll(mergedClasses);
        mListAdapter.notifyDataSetChanged();
        showContent();
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
                    TaskResult<List<CowClassObj>> result = mDownloadTask.getResult();
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
        mDownloadTask = new DownloadClassesTask(mStore);
        mDownloadTask.attachObserver(mDownloadTaskObserver);
        mDownloadTask.execute();
    }



    public void retainNonConfigurationInstance() {
        Retainer retainer = new Retainer();
        retainer.downloadTask = mDownloadTask;
        getFragmentManager().beginTransaction().add(retainer, RETAINER_FRAGMENT_TAG).commit();
    }



    private void refreshClassesList() {
        Collection<CowClassObj> classes = mDefinitionsStore.fetchAllClasses();
        mListAdapter.clear();
        mListAdapter.addAll(classes);
    }


    private CowClass getSelectedClass() {
        int checkedItemPos = mListView.getCheckedItemPosition();
        if (checkedItemPos >= 0) {
            return mListAdapter.getItem(checkedItemPos);
        }

        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();

        if(getSelectedClass() != null) {
            inflater.inflate(R.menu.cloud_add_del_edit_menu, menu);
        }
        else {
            inflater.inflate(R.menu.cloud_add_menu, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menuAdd:
                addClass();
                break;
            case R.id.menuDelete:
                deleteClassWithQuestion();
                break;
            case R.id.menuEdit:
                editClass();
                break;
            case R.id.menuDownload:
                download();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;

    }


    private void showNoItemSelectedError() {
        new ErrorToast(this).show(R.string.errCowClassNotSelected);
    }

    private void editClass() {

        CowClass classToEdit = getSelectedClass();
        if (classToEdit == null) {
            showNoItemSelectedError();
            return;
        }

        Intent editClass = new Intent(this, EditCowClassActivity.class);
        editClass.putExtra(EditCowClassActivity.EXTRA_CLASS_ID, classToEdit.getId());
        startActivityForResult(editClass, REQUEST_CODE_EDIT_CLASS);

    }

    private void addClass() {
        startActivityForResult(new Intent(this, NewCowClassActivity.class), REQUEST_CODE_NEW_CLASS);
    }

    private void deleteClassWithQuestion() {
        final CowClass classToDelete = getSelectedClass();
        if (classToDelete != null) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        deleteClass(classToDelete);
                        mListView.clearChoices();
                        refreshClassesList();
                        invalidateOptionsMenu();
                    }
                }
            };

            Resources r = getResources();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(r.getString(R.string.askDeleteCowClass, classToDelete.getClassCode()));
            builder.setPositiveButton(android.R.string.yes, dialogClickListener);
            builder.setNegativeButton(android.R.string.no, dialogClickListener);
            builder.show();

        } else {
            showNoItemSelectedError();
        }
    }

    private void deleteClass(CowClass cowClass) {
        mDefinitionsStore.deleteClass(cowClass.getId());
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        refreshClassesList();
    }

}
