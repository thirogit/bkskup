package com.bk.bkskup3.management;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bk.bkskup3.R;
import com.bk.bkskup3.feedback.ErrorMessageFragment;
import com.bk.bkskup3.feedback.ErrorToast;
import com.bk.bkskup3.library.DocumentLibraryService;
import com.bk.bkskup3.library.DocumentOption;
import com.bk.bkskup3.library.DocumentOptionDefinition;
import com.bk.bkskup3.library.DocumentOptionType;
import com.bk.bkskup3.library.DocumentProfile;
import com.bk.bkskup3.management.fragment.event.DeleteProfileEvent;
import com.bk.bkskup3.management.fragment.event.EditProfileEvent;
import com.bk.bkskup3.management.fragment.event.ProfileDeletedEvent;
import com.bk.bkskup3.management.fragment.event.SaveProfileEvent;
import com.bk.bkskup3.management.widget.DocumentOptionView;
import com.bk.bkskup3.management.widget.DocumentOptionViewInflater;
import com.bk.bkskup3.tasks.CreateNewProfileTask;
import com.bk.bkskup3.tasks.DeleteProfileTask;
import com.bk.bkskup3.tasks.LoadProfilesTask;
import com.bk.bkskup3.tasks.SaveProfileTask;
import com.bk.bkskup3.tasks.TaskResult;
import com.bk.bkskup3.work.BusActivity;
import com.bk.bkskup3.work.fragment.BusDialogFragment;
import com.bk.bkskup3.work.fragment.BusFragment;
import com.bk.bkskup3.work.fragment.BusyFragment;
import com.bk.bkskup3.work.fragment.event.NewProfileEvent;
import com.bk.bkskup3.work.fragment.event.ProfileNameInputEvent;
import com.bk.bkskup3.work.fragment.event.ProfilesLoadedEvent;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.squareup.otto.Subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * Created by SG0891787 on 2/14/2017.
 */

public class DocumentProfilesActivity extends BusActivity {

    public static final String EXTRA_DOC_CODE = "doc_code";

    private static final String LIST_FRAGMENT_TAG = "profile_list";
    private static final String INPUT_FRAGMENT_TAG = "input_fragment";
    private static final String OPTIONS_FRAGMENT_TAG = "options";
    private static final String RETAINER_FRAGMENT_TAG = "retainer";
    private static final String ERROR_MSG_FRAMGENT_TAG = "error_fragment";
    private static final String BUSY_MSG_FRAMGENT_TAG = "busy_fragment";
    private static final String BACK_STACK_ROOT_TAG = "root_fragment";
    private static final String STATE_EXTRA_PROFILES = "state_profiles";
    private static final String STATE_EXTRA_STATE = "state";
    private static final String STATE_EXTRA_PREVSTATE = "prev_state";


    enum State {
        BindingToService,
        LoadingProfiles,
        ShowingError,
        Idle,
        CreatingNewProfile,
        SavingProfile,
        DeletingProfile
    }

    public static class Retainer extends Fragment {
        public Retainer() {
            setRetainInstance(true);
        }

        public LoadProfilesTask loadProfilesTask;
        public CreateNewProfileTask createNewProfileTask;
        public DeleteProfileTask deleteProfileTask;
        public SaveProfileTask saveProfileTask;
    }


    private View mProgressContainer;
    private LinearLayout mContentContainer;
    private DocumentLibraryService mService;


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

    LoadProfilesTask.Observer mLoadProfilesTaskObserver = new LoadProfilesTask.Observer() {
        @Override
        public void onLoadStarted() {
            showLoading();
        }

        @Override
        public void onLoadSuccessful(Collection<DocumentProfile> result) {

            onProfilesLoaded(result);
        }

        @Override
        public void onLoadError(Exception e) {
            setState(State.ShowingError);
            showErrorWithRetry(e.getMessage(), mLoadProfilesErrorListener);
        }
    };

    CreateNewProfileTask.Observer mCreateNewProfileTaskObserver = new CreateNewProfileTask.Observer() {
        @Override
        public void onSaveStarted() {
            showBusy(R.string.creatingNewProflieBusyText);
        }

        @Override
        public void onSaveSuccessful(DocumentProfile result) {
            mProfiles.add(result);
            mProfilesMap.put(result.getProfileId(), result);
            post(new NewProfileEvent(result));
            setState(State.Idle);
            hideBusy();
            invalidateOptionsMenu();
        }

        @Override
        public void onSaveError(Exception e) {
            setState(State.ShowingError);
            showErrorWithRetry(e.getMessage(), mCreateProfileErrorListener);
        }
    };

    DeleteProfileTask.Observer mDeleteProfileTaskObserver = new DeleteProfileTask.Observer() {
        @Override
        public void onDeleteStarted() {
            showBusy(R.string.deletingProfileBusyText);
        }

        @Override
        public void onDeleteSuccessful(final Integer result) {

            Iterables.removeIf(mProfiles, new Predicate<DocumentProfile>() {
                @Override
                public boolean apply(@Nullable DocumentProfile input) {
                    return input.getProfileId() == result.intValue();
                }
            });
            mProfilesMap.remove(result);
            post(new ProfileDeletedEvent(result));
            hideBusy();
            invalidateOptionsMenu();
        }

        @Override
        public void onDeleteError(Exception e) {
            setState(State.ShowingError);
            showErrorNoRetry(e.getMessage(), mDeleteProfileErrorListener);
        }
    };


    SaveProfileTask.Observer mSaveProfileTaskObserver = new SaveProfileTask.Observer() {
        @Override
        public void onSaveStarted() {

            showBusy(R.string.savingProfileBusyText);
        }

        @Override
        public void onSaveSuccessful(final Integer result) {
            hideBusy();
            showProfileList();
        }

        @Override
        public void onSaveError(Exception e) {
            setState(State.ShowingError);
            showErrorNoRetry(e.getMessage(), mSaveProfileErrorListener);
        }
    };

    private ErrorMessageFragment.ErrorFragmentListener mLoadProfilesErrorListener = new ErrorMessageFragment.ForwardErrorFragmentListener() {
        @Override
        public void onAbandon() {
            mErrMsgFragment = null;
            clearState();
            finish();
        }

        @Override
        public void onRetry() {
            mErrMsgFragment = null;
            loadProfiles();
        }
    };

    private ErrorMessageFragment.ErrorFragmentListener mCreateProfileErrorListener = new ErrorMessageFragment.ForwardErrorFragmentListener() {
        @Override
        public void onAbandon() {
            mErrMsgFragment = null;
            clearState();
        }

        @Override
        public void onRetry() {
        }
    };

    private ErrorMessageFragment.ErrorFragmentListener mDeleteProfileErrorListener = new ErrorMessageFragment.ForwardErrorFragmentListener() {
        @Override
        public void onAbandon() {
            mErrMsgFragment = null;
            clearState();
        }

        @Override
        public void onRetry() {
        }
    };

    private ErrorMessageFragment.ErrorFragmentListener mSaveProfileErrorListener = new ErrorMessageFragment.ForwardErrorFragmentListener() {
        @Override
        public void onAbandon() {
            mErrMsgFragment = null;
            clearState();
        }

        @Override
        public void onRetry() {
        }
    };

    private LoadProfilesTask mLoadProfilesTask;
    private CreateNewProfileTask mCreatingNewProfileTask;
    private DeleteProfileTask mDeleteProfileTask;
    private SaveProfileTask mSaveProfileTask;

    private String mDocumentCode;
    private ArrayList<DocumentProfile> mProfiles;
    private Map<Integer, DocumentProfile> mProfilesMap = new HashMap<>();
    private boolean mDualPane;

    private ProfileListFragment mProfileListFragment;
    private ProfileOptionsFragment mProfileOptionsFragment;
    private InputProfileNameDialogFragment mInputProfileNameFragment;
    private State mState;
    private State mPreviousState;
    protected ErrorMessageFragment mErrMsgFragment;
    protected BusyFragment mBusyFragment;

    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.doc_profile_options);

        Intent intent = getIntent();
        mDocumentCode = intent.getStringExtra(EXTRA_DOC_CODE);
        mDualPane = findViewById(R.id.dual_pane) != null;

        FragmentManager fm = getFragmentManager();
        Retainer retainer = (Retainer) fm.findFragmentByTag(RETAINER_FRAGMENT_TAG);

        if (retainer != null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(retainer);
            ft.commit();

            mLoadProfilesTask = retainer.loadProfilesTask;
            mCreatingNewProfileTask = retainer.createNewProfileTask;
            mDeleteProfileTask = retainer.deleteProfileTask;
            mSaveProfileTask = retainer.saveProfileTask;
        }



        ensureContent();

    }

    protected void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);

        if (savedState != null) {
            mState = (State) savedState.getSerializable(STATE_EXTRA_STATE);
            mPreviousState = (State) savedState.getSerializable(STATE_EXTRA_PREVSTATE);
            mProfiles = (ArrayList<DocumentProfile>) savedState.getSerializable(STATE_EXTRA_PROFILES);
            resetProfilesMap();
        }

    }

    public void resetProfilesMap() {
        mProfilesMap.clear();
        mProfilesMap.putAll(Maps.uniqueIndex(mProfiles, new Function<DocumentProfile, Integer>() {
            @Nullable
            @Override
            public Integer apply(@Nullable DocumentProfile input) {
                return input.getProfileId();
            }
        }));
    }

    private void clearState() {
        setState(State.Idle);
    }

    public void setState(State state) {
        this.mPreviousState = mState;
        this.mState = state;
    }

    private void ensureContent() {

        mContentContainer = (LinearLayout) findViewById(R.id.content_container);
        mProgressContainer = findViewById(R.id.progress_container);

        FragmentManager fm = getFragmentManager();

        mProfileListFragment = (ProfileListFragment) fm.findFragmentByTag(LIST_FRAGMENT_TAG);
        mProfileOptionsFragment = (ProfileOptionsFragment) fm.findFragmentByTag(OPTIONS_FRAGMENT_TAG);
        mInputProfileNameFragment = (InputProfileNameDialogFragment) fm.findFragmentByTag(INPUT_FRAGMENT_TAG);
        mErrMsgFragment = (ErrorMessageFragment) fm.findFragmentByTag(ERROR_MSG_FRAMGENT_TAG);
        mBusyFragment = (BusyFragment) fm.findFragmentByTag(BUSY_MSG_FRAMGENT_TAG);

        if (mProfileListFragment == null) {
            mProfileListFragment = (ProfileListFragment) Fragment.instantiate(this, ProfileListFragment.class.getName(), null);
            fm.beginTransaction().add(R.id.content_container, mProfileListFragment, LIST_FRAGMENT_TAG).commit();
        }

        if (mProfileOptionsFragment == null) {
            mProfileOptionsFragment = (ProfileOptionsFragment) Fragment.instantiate(this, ProfileOptionsFragment.class.getName(), null);
        }

//        if(mDualPane)
//        {
//
//        }
//        else
//        {

//        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mLoadProfilesTask != null)
            mLoadProfilesTask.detachObserver();

        if (mCreatingNewProfileTask != null)
            mCreatingNewProfileTask.detachObserver();

        if (mDeleteProfileTask != null)
            mDeleteProfileTask.detachObserver();

        if (mSaveProfileTask != null) {
            mSaveProfileTask.detachObserver();
        }

        if (mErrMsgFragment != null)
            mErrMsgFragment.setListener(null);

        retainNonConfigurationInstance();
    }

    public void retainNonConfigurationInstance() {
        Retainer retainer = new Retainer();
        retainer.loadProfilesTask = mLoadProfilesTask;
        retainer.createNewProfileTask = mCreatingNewProfileTask;
        retainer.deleteProfileTask = mDeleteProfileTask;
        getFragmentManager().beginTransaction().add(retainer, RETAINER_FRAGMENT_TAG).commit();
    }

    protected void onStop() {
        super.onStop();

        if (mService != null) {
            unbindService(mConnection);
            mService = null;
        }
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

    @Subscribe
    public void onEditProfile(EditProfileEvent event) {
        if (!mDualPane) {
            showProfileOptions(event.getProfile());
        }
    }

    private void showProfileList()
    {
        FragmentManager fm = getFragmentManager();
        fm.popBackStack();
        fm.beginTransaction()
                .replace(R.id.content_container, mProfileListFragment)
                .commit();
    }

    private void showProfileOptions(DocumentProfile profile) {

        mProfileOptionsFragment.mProfileId = profile.getProfileId();

        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .replace(R.id.content_container, mProfileOptionsFragment)
                .addToBackStack(BACK_STACK_ROOT_TAG)
                .commit();
    }

    @Subscribe
    public void onProfileNameInput(ProfileNameInputEvent event) {
        setState(State.CreatingNewProfile);
        mCreatingNewProfileTask = new CreateNewProfileTask(mService, mDocumentCode, event.getProfileName());
        mCreatingNewProfileTask.attachObserver(mCreateNewProfileTaskObserver);
        mCreatingNewProfileTask.execute();
    }

    private void continueResume() {

        if (mState == null) {
            loadProfiles();
            return;
        }

        if (mState == State.Idle) {
            showContent();
            return;
        }

        if (mState == State.CreatingNewProfile) {
            if (mCreatingNewProfileTask != null) {
                if (mCreatingNewProfileTask.getStatus() == AsyncTask.Status.FINISHED) {
                    TaskResult<DocumentProfile> result = mCreatingNewProfileTask.getResult();
                    if (result.isError()) {
                        setState(State.ShowingError);
                        mCreatingNewProfileTask.attachObserver(mCreateNewProfileTaskObserver);
                        showContent();
                        showErrorWithRetry(result.getException().getMessage(), mCreateProfileErrorListener);
                    } else {
                        showContent();
                        mCreateNewProfileTaskObserver.onSaveSuccessful(result.getResult());
                    }
                } else {
                    showContent();
//                    showBusy(R.string.creatingNewProflieBusyText);
                    mCreatingNewProfileTask.attachObserver(mCreateNewProfileTaskObserver);
                }
            } else {
                showContent();
                setState(State.Idle);
            }
            return;
        }

        if (mState == State.LoadingProfiles) {
            if (mLoadProfilesTask != null) {
                if (mLoadProfilesTask.getStatus() == AsyncTask.Status.FINISHED) {
                    TaskResult<Collection<DocumentProfile>> result = mLoadProfilesTask.getResult();
                    if (result.isError()) {
                        setState(State.ShowingError);
                        showErrorWithRetry(result.getException().getMessage(), mLoadProfilesErrorListener);
                    } else {
                        mLoadProfilesTaskObserver.onLoadSuccessful(result.getResult());
                    }
                } else {
                    showLoading();
                    mLoadProfilesTask.attachObserver(mLoadProfilesTaskObserver);
                }
            } else {
                loadProfiles();
            }
            return;
        }

        if (mState == State.DeletingProfile) {
            if (mDeleteProfileTask != null) {
                if (mDeleteProfileTask.getStatus() == AsyncTask.Status.FINISHED) {
                    hideBusy();
                    TaskResult<Integer> result = mDeleteProfileTask.getResult();
                    if (result.isError()) {
                        setState(State.ShowingError);
                        showErrorNoRetry(result.getException().getMessage(), mDeleteProfileErrorListener);
                    } else {
                        showContent();
                        mDeleteProfileTaskObserver.onDeleteSuccessful(result.getResult());
                    }
                } else {
                    showContent();
//                    showBusy(R.string.deletingProfileBusyText);
                    mDeleteProfileTask.attachObserver(mDeleteProfileTaskObserver);
                }
            } else {
                loadProfiles();
            }
            return;
        }

        if (mState == State.SavingProfile) {
            if (mSaveProfileTask != null) {
                showContent();
                if (mSaveProfileTask.getStatus() == AsyncTask.Status.FINISHED) {
                    TaskResult<Integer> result = mSaveProfileTask.getResult();
                    if (result.isError()) {
                        setState(State.ShowingError);
                        showErrorNoRetry(result.getException().getMessage(), mSaveProfileErrorListener);
                    } else {
                        mSaveProfileTaskObserver.onSaveSuccessful(result.getResult());
                    }
                } else {
                    mSaveProfileTask.attachObserver(mSaveProfileTaskObserver);
                }
            } else {
                loadProfiles();
            }
            return;
        }

        if (mState == State.ShowingError) {

            switch (mPreviousState) {
                case CreatingNewProfile:
                    mErrMsgFragment.setListener(mCreateProfileErrorListener);
                    break;
                case DeletingProfile:
                    mErrMsgFragment.setListener(mDeleteProfileErrorListener);
                    break;
                case LoadingProfiles:
                    mErrMsgFragment.setListener(mLoadProfilesErrorListener);
                    break;
            }

        }

    }


    private void hideBusy() {
        if (mBusyFragment != null) {
            mBusyFragment.dismiss();
            mBusyFragment = null;
        }
    }

    private void showBusy(int busyTextResId) {
        FragmentManager fm = getFragmentManager();

        if (mBusyFragment == null) {
            mBusyFragment = BusyFragment.newInstance(getString(busyTextResId));
        }
        mBusyFragment.setCancelable(false);
        mBusyFragment.show(fm, BUSY_MSG_FRAMGENT_TAG);
    }

    private void loadProfiles() {
        setState(State.LoadingProfiles);
        mLoadProfilesTask = new LoadProfilesTask(mService, mDocumentCode);
        mLoadProfilesTask.attachObserver(mLoadProfilesTaskObserver);
        mLoadProfilesTask.execute();
    }


    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putSerializable(STATE_EXTRA_STATE, mState);
        state.putSerializable(STATE_EXTRA_PREVSTATE, mPreviousState);
        state.putSerializable(STATE_EXTRA_PROFILES, mProfiles);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();

//        if(mState == State.Idle) {
//            inflater.inflate(R.menu.profiles_add_menu, menu);
//        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menuAdd:
                addNewProfile();
                break;
//            case R.id.menuCancel:
//                finish();
//                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void addNewProfile() {
        FragmentManager fm = getFragmentManager();

        if (mInputProfileNameFragment == null) {
            mInputProfileNameFragment = InputProfileNameDialogFragment.newInstance();
        }
        mInputProfileNameFragment.setCancelable(false);
        mInputProfileNameFragment.show(fm, INPUT_FRAGMENT_TAG);
    }


    private void showContent() {
        mProgressContainer.setVisibility(View.GONE);
        mContentContainer.setVisibility(View.VISIBLE);
    }

    protected void showLoading() {

        mContentContainer.setVisibility(View.GONE);
        mProgressContainer.setVisibility(View.VISIBLE);
    }

    private void afterProfilesLoaded() {
        setState(State.Idle);
        post(new ProfilesLoadedEvent(mProfiles));
        invalidateOptionsMenu();
        showContent();
    }

    private void onProfilesLoaded(Collection<DocumentProfile> result) {

        mProfiles = new ArrayList<>(result);
        resetProfilesMap();
        afterProfilesLoaded();
    }


    private void showError(String message, ErrorMessageFragment.ErrorFragmentListener listener, boolean retryEnabled) {
        setState(State.ShowingError);
        mErrMsgFragment = new ErrorMessageFragment();
        mErrMsgFragment.setCaption(getResources().getString(R.string.errorCaption));
        mErrMsgFragment.setErrorMsg(message);
        mErrMsgFragment.setListener(listener);
        mErrMsgFragment.show(getFragmentManager(), ERROR_MSG_FRAMGENT_TAG);
    }

    private void showErrorWithRetry(String message, ErrorMessageFragment.ErrorFragmentListener listener) {
        showError(message, listener, true);
    }

    private void showErrorNoRetry(String message, ErrorMessageFragment.ErrorFragmentListener listener) {
        showError(message, listener, false);
    }

    @Subscribe
    public void onDeleteProfile(DeleteProfileEvent event) {
        final int profileId = event.getProfileId();
        DocumentProfile profile = mProfilesMap.get(profileId);
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {

                    runDeleteProfile(profileId);
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.askDeleteProfile, profile.getProfileName()));
        builder.setPositiveButton(android.R.string.yes, dialogClickListener);
        builder.setNegativeButton(android.R.string.no, dialogClickListener);
        builder.show();
    }

    private void runDeleteProfile(int profileId) {
        setState(State.DeletingProfile);
        mDeleteProfileTask = new DeleteProfileTask(mService, profileId);
        mDeleteProfileTask.attachObserver(mDeleteProfileTaskObserver);
        mDeleteProfileTask.execute();

    }

    @Subscribe
    public void onSaveProfile(SaveProfileEvent event) {
        setState(State.SavingProfile);
        DocumentProfile profile = mProfilesMap.get(event.getProfieId());
        mSaveProfileTask = new SaveProfileTask(mService, profile);
        mSaveProfileTask.attachObserver(mSaveProfileTaskObserver);
        mSaveProfileTask.execute();
    }

    public static class InputProfileNameDialogFragment extends BusDialogFragment {

        private EditText mInputBox;

        public static InputProfileNameDialogFragment newInstance() {
            InputProfileNameDialogFragment frag = new InputProfileNameDialogFragment();
//            Bundle args = new Bundle();
//            args.putString(TITLE, dataToShow);
//            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            LayoutInflater factory = LayoutInflater.from(getActivity());
            View inputBoxView = factory.inflate(R.layout.text_input, null);
            mInputBox = (EditText) inputBoxView.findViewById(R.id.inputBox);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.askInputProfileName);
            builder.setView(inputBoxView);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    postEvent(new ProfileNameInputEvent(mInputBox.getText().toString()));
                }
            });
            setCancelable(false);
            builder.setNegativeButton(android.R.string.cancel, null);
            return builder.create();

        }

        @Override
        public void onResume() {
            super.onResume();
            final AlertDialog d = (AlertDialog) getDialog();
            if (d != null) {
                Button positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Editable text = mInputBox.getText();
                        int length = text.length();
                        if (length != 0) {

                            postEvent(new ProfileNameInputEvent(text.toString()));

                            d.dismiss();
                            return;
                        }
                        new ErrorToast(getActivity()).show(R.string.errNoProfileNameProvided);
                    }
                });
            }
        }


    }

    public static class ProfileListFragment extends BusFragment {

        private static final String STATE_EXTRA_PROFILES = "state_profiles";

        ArrayList<DocumentProfile> mProfiles;
        ArrayAdapter<DocumentProfile> mAdapter;
        ListView mListView;

        @Subscribe
        public void onProfilesLoaded(ProfilesLoadedEvent event) {
            mProfiles = new ArrayList<>(event.getProfiles());
            if (getView() != null) {
                refreshProfilesList();
            }
        }

        @Subscribe
        public void OnNewProfile(NewProfileEvent event) {
            mProfiles.add(event.getProfile());

            mAdapter.add(event.getProfile());
        }

        @Subscribe
        public void OnProfileDeleted(ProfileDeletedEvent event) {
            final int profileId = event.getProfileId();
            Iterables.removeIf(mProfiles, new Predicate<DocumentProfile>() {
                @Override
                public boolean apply(@Nullable DocumentProfile input) {
                    return input.getProfileId() == profileId;
                }
            });
            mAdapter.remove(new DocumentProfile(profileId));
        }

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
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
            View v = inflater.inflate(R.layout.list_fragment, container, false);
            return v;
        }

        public void onViewCreated(View view, Bundle state) {
            super.onViewCreated(view, state);

            mListView = (ListView) view.findViewById(R.id.list);
            mAdapter = new DocumentProfileAdapter();


            mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    getActivity().invalidateOptionsMenu();
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

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

            if (mListView.getCheckedItemPosition() >= 0) {
                inflater.inflate(R.menu.profiles_crud_menu, menu);
            } else {
                inflater.inflate(R.menu.add_menu, menu);
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {

                case R.id.menuEdit:
                    onEditProfile();
                    break;
                case R.id.menuDelete:
                    deleteProfileWithQuestion();
                    break;

            }
            return super.onOptionsItemSelected(item);
        }

        private void deleteProfileWithQuestion() {
            final DocumentProfile profileToDelete = getSelectedProfile();
            if (profileToDelete != null) {
                postEvent(new DeleteProfileEvent(profileToDelete.getProfileId()));
            } else {
                showNoItemSelectedError();
            }
        }


        private void showNoItemSelectedError() {

            new ErrorToast(getActivity()).show(R.string.errProfileNotSelected);

        }


        private void onEditProfile() {

            int checkedItemPosition = mListView.getCheckedItemPosition();
            if (checkedItemPosition >= 0) {
                DocumentProfile profile = mAdapter.getItem(checkedItemPosition);
                postEvent(new EditProfileEvent(profile));
            }


        }

        public DocumentProfile getSelectedProfile() {
            int checkedItemPosition = mListView.getCheckedItemPosition();
            if (checkedItemPosition >= 0) {
                return mAdapter.getItem(checkedItemPosition);
            }
            return null;
        }

        private class DocumentProfileAdapter extends ArrayAdapter<DocumentProfile> {
            public DocumentProfileAdapter() {
                super(ProfileListFragment.this.getActivity(), R.layout.profiles_list_check_item);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View row;
                if (null == convertView) {
                    LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    row = inflater.inflate(R.layout.profiles_list_check_item, null);
                } else {
                    row = convertView;
                }

                DocumentProfile profile = getItem(position);
                TextView text = (TextView) row.findViewById(R.id.profileName);
                text.setText(profile.getProfileName());

                return row;
            }
        }
    }


    public static class ViewState implements Serializable {
        public DocumentOptionDefinition optionDefinition;
        public String optionValue;
    }

    public static class FragmentViewState implements Serializable {
        private ArrayList<ViewState> mViewStates;


    }

    public static class ProfileOptionsFragment extends BusFragment {
        private static final String STATE_EXTRA_PROFILE_ID = "state_profile_id";
        private static final String STATE_EXTRA_VIEWSTATE = "state_view_state";

        int mProfileId;

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setHasOptionsMenu(true);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.save_menu, menu);

        }


        void hideKeyboard()
        {
            Activity activity = getActivity();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            View currentFocus = activity.getCurrentFocus();
            if(currentFocus != null) {
                imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menuSave:
                    hideKeyboard();
                    saveProfile();
                    break;
            }
            return super.onOptionsItemSelected(item);
        }

        private void saveProfile() {
            saveToProfile();
            postEvent(new SaveProfileEvent(mProfileId));
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
            View v = inflater.inflate(R.layout.profile_options, container, false);

            return v;
        }

        public void onViewCreated(View view, Bundle state) {
            super.onViewCreated(view, state);
            FragmentViewState viewState = null;
            if (state != null) {
                viewState = (FragmentViewState) state.getSerializable(STATE_EXTRA_VIEWSTATE);
            }

            if (viewState != null) {
                restoreOptionsView(viewState);
            } else {
                createOptionsView();
            }
        }

        private void restoreOptionsView(FragmentViewState fragmentViewState) {
            LinearLayout container = (LinearLayout) getView();
            container.removeAllViews();


            for (ViewState viewState : fragmentViewState.mViewStates) {
                String value = viewState.optionValue;

                View optionView = createOptionView(viewState.optionDefinition, value);

                optionView.setId(View.generateViewId());

                if (optionView != null) {
                    container.addView(optionView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                }
            }

            container.requestLayout();
        }

        public FragmentViewState saveViewState() {

            LinearLayout container = (LinearLayout) getView();
            int count = container.getChildCount();
            FragmentViewState fragmentViewState = new FragmentViewState();
            fragmentViewState.mViewStates = new ArrayList<>(count);


            for (int i = 0; i < count; i++) {
                DocumentOptionView optionView = (DocumentOptionView) container.getChildAt(i);
                String optionCode = optionView.getOptionCode();
                String value = optionView.getValue();
                String title = optionView.getTitle();
                DocumentOptionType type = optionView.getOptionType();


                ViewState viewState = new ViewState();

                viewState.optionDefinition = new DocumentOptionDefinition(optionCode, type);
                viewState.optionDefinition.setTitle(title);
                viewState.optionValue = value;
                fragmentViewState.mViewStates.add(viewState);
            }

            return fragmentViewState;
        }

        private DocumentProfile getProfile() {
            DocumentProfilesActivity activity = (DocumentProfilesActivity) getActivity();
            return activity.mProfilesMap.get(mProfileId);
        }

        private DocumentLibraryService getService() {
            DocumentProfilesActivity activity = (DocumentProfilesActivity) getActivity();
            return activity.mService;
        }

        private void createOptionsView() {
            DocumentProfile profile = getProfile();
            LinearLayout container = (LinearLayout) getView();
            container.removeAllViews();

            Iterable<DocumentOptionDefinition> documentOptionDefinitions = getService().getDocumentOptionDefinition(profile.getDocumentCode());

            for (DocumentOptionDefinition optionDefinition : documentOptionDefinitions) {
                String value = optionDefinition.getDefaultValue();
                String code = optionDefinition.getCode();
                DocumentOption option = profile.getOption(code);
                if (option != null) {
                    value = option.getOptionValue();
                }

                View optionView = createOptionView(optionDefinition, value);

                optionView.setId(View.generateViewId());

                if (optionView != null) {
                    container.addView(optionView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                }
            }

            container.requestLayout();
        }

        private View createOptionView(DocumentOptionDefinition optionDefinition, String value) {

            DocumentOptionViewInflater inflater = new DocumentOptionViewInflater(LayoutInflater.from(getActivity()));
            DocumentOptionView optionView = inflater.inflate(optionDefinition);
            optionView.setValue(value);
            return optionView;
        }

        public void onSaveInstanceState(Bundle state) {
            state.putSerializable(STATE_EXTRA_PROFILE_ID, mProfileId);
            state.putSerializable(STATE_EXTRA_VIEWSTATE, saveViewState());
        }

        public void saveToProfile() {
            LinearLayout container = (LinearLayout) getView();
            for (int i = 0, count = container.getChildCount(); i < count; i++) {
                DocumentOptionView optionView = (DocumentOptionView) container.getChildAt(i);
                String optionCode = optionView.getOptionCode();
                String value = optionView.getValue();

                DocumentProfile profile = getProfile();
                profile.addOption(optionCode, value);
            }
        }

        @Override
        public void onPause() {
            super.onPause();
            saveToProfile();
        }
    }


}
