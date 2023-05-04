package com.bk.bkskup3.work.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import com.bk.bkskup3.R;
import com.bk.bkskup3.work.CowActivity;
import com.bk.bkskup3.work.EditCowActivity;
import com.bk.bkskup3.work.NewCowActivity;
import com.bk.bkskup3.work.QuickCowActivity;
import com.bk.bkskup3.work.fragment.event.CowAddedEvent;
import com.bk.bkskup3.work.fragment.event.CowDeletedEvent;
import com.bk.bkskup3.work.fragment.event.CowEditedEvent;
import com.bk.bkskup3.work.input.CowInput;
import com.squareup.otto.InheritSubscribers;
import com.todobom.opennotescanner.OpenNoteScannerActivity;

import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/14/13
 * Time: 10:00 AM
 */

@InheritSubscribers
public class InvoiceEditCowsFragment extends InvoiceFragment {

    private static final int NEW_COW_REQUEST_CODE = 10004;
    private static final int EDIT_COW_REQUEST_CODE = 10005;
    private static final int SCAN_COW_REQUEST_CODE = 10006;
    private static final int CAPTURE_PASSPORT_REQUEST_CODE = 10007;


    protected ListView mCowsList;
    protected InvoiceCowListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.invoice_cows, container, false);

        mCowsList = (ListView) v.findViewById(R.id.list);

        mCowsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onCowSelect((CowInput) adapterView.getItemAtPosition(i));
            }
        });

        mCowsList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new InvoiceCowListAdapter(getActivity());
        scheduleAfterBoundService(new Runnable() {
            @Override
            public void run() {
                for (CowInput cow : mService.getCows()) {
                    mAdapter.add(cow);
                }
                mCowsList.setAdapter(mAdapter);
            }
        });
        setHasOptionsMenu(true);
    }

    private void onCowSelect(CowInput cow) {
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        if (mCowsList.getCheckedItemPosition() >= 0) {
            inflater.inflate(R.menu.invoice_cow_crud_menu, menu);
        } else {
            inflater.inflate(R.menu.invoice_cow_menu, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menuAddCow:
                onAddNewCow();
                break;

            case R.id.menuScanCow:
                onScanCow();
                break;

            case R.id.menuEditCow:
                onEditCow();
                break;

            case R.id.menuDeleteCow:
                onDeleteCow();
                break;
            case R.id.menuCapturePassport:
//                onOCRPassport();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onOCRPassport() {
            Intent ocrPassportIntent = new Intent(getActivity(), OpenNoteScannerActivity.class);
            startActivityForResult(ocrPassportIntent, CAPTURE_PASSPORT_REQUEST_CODE);
    }

    private void onDeleteCow() {
        int position = mCowsList.getCheckedItemPosition();
        if (position >= 0) {
            CowInput item = mAdapter.getItem(position);
            mService.deleteCow(item.getInputId());
            mAdapter.delete(position);
            postEvent(new CowDeletedEvent(item.getInputId()));
        }
    }

    private void onEditCow() {
        int position = mCowsList.getCheckedItemPosition();
        if (position >= 0) {
            CowInput item = mAdapter.getItem(position);
            startEditCowActivity(item);
        }
    }

    private void startEditCowActivity(CowInput item) {
        Intent editCowIntent = new Intent(getActivity(), EditCowActivity.class);
        editCowIntent.putExtra(CowActivity.EXTRA_COW_INPUT_ID, item.getInputId());
        startActivityForResult(editCowIntent, EDIT_COW_REQUEST_CODE);
    }

    private void onAddNewCow() {
        startActivityForResult(new Intent(getActivity(), NewCowActivity.class), NEW_COW_REQUEST_CODE);
    }


    private void onScanCow() {
        Intent scanCowIntent = new Intent(getActivity(), QuickCowActivity.class);

        scanCowIntent.putExtra(QuickCowActivity.EXTRA_VAT_RATE, mService.getVatRate());

        startActivityForResult(scanCowIntent, SCAN_COW_REQUEST_CODE);
    }


    public void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED)
            return;

        switch (requestCode) {
            case NEW_COW_REQUEST_CODE:
                final UUID newCowInputId = (UUID) data.getSerializableExtra(CowActivity.EXTRA_COW_INPUT_ID);
                scheduleAfterBoundService(new Runnable() {
                    @Override
                    public void run() {
                        addCow(newCowInputId);
                    }
                });
                break;
            case EDIT_COW_REQUEST_CODE:
                final UUID editedCowInputId = (UUID) data.getSerializableExtra(CowActivity.EXTRA_COW_INPUT_ID);
                scheduleAfterBoundService(new Runnable() {
                    @Override
                    public void run() {
                        editCow(editedCowInputId);
                    }
                });

                break;
            case SCAN_COW_REQUEST_CODE:
                final UUID scannedCowInputId = (UUID) data.getSerializableExtra(QuickCowActivity.EXTRA_COW_INPUT_ID);
                scheduleAfterBoundService(new Runnable() {
                    @Override
                    public void run() {
                        CowInput newlyAddedCow = addCow(scannedCowInputId);
                        if (resultCode == QuickCowActivity.RESULT_FILL) {
                            startEditCowActivity(newlyAddedCow);
                        }
                    }
                });
                break;
        }
    }

    private void editCow(UUID editedCowInputId) {
        CowInput cowInput = mService.getCow(editedCowInputId);
        mAdapter.getCow(cowInput.getInputId()).copyFrom(cowInput);
        mAdapter.notifyDataSetChanged();
        postEvent(new CowEditedEvent(cowInput));
    }

    private CowInput addCow(UUID cowInputId) {
        CowInput newlyAddedCow = mService.getCow(cowInputId);
        mAdapter.add(newlyAddedCow);
        postEvent(new CowAddedEvent(newlyAddedCow));
        return newlyAddedCow;
    }





}
