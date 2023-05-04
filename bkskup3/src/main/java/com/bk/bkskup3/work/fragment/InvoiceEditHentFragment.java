package com.bk.bkskup3.work.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.*;

import com.bk.bkskup3.BkApplication;
import com.bk.bkskup3.R;
import com.bk.bkskup3.dao.HentsStore;
import com.bk.bkskup3.model.*;
import com.bk.bkskup3.settings.TaxSettings;
import com.bk.bkskup3.tasks.DependenciesForInvoice;
import com.bk.bkskup3.utils.EqualsUtils;
import com.bk.bkskup3.work.*;
import com.bk.bkskup3.work.fragment.event.InvoiceHentChanged;
import com.bk.bkskup3.work.fragment.event.InvoiceTypeChanged;
import com.bk.bkskup3.work.fragment.event.InvoiceVatRateChanged;
import com.squareup.otto.InheritSubscribers;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/3/2014
 * Time: 10:31 PM
 */

@InheritSubscribers
public class InvoiceEditHentFragment extends InvoiceHentFragment {

    private static final int FIND_HENT_REQUEST_CODE = 10001;
    private static final int EDIT_HENT_REQUEST_CODE = 10002;
    private static final int NEW_HENT_REQUEST_CODE = 10003;
    private static final int SCAN_HENT_REQUEST_CODE = 10004;

    private DependenciesForInvoice mDependencies;

    public void setDependencies(DependenciesForInvoice mDependencies) {
        this.mDependencies = mDependencies;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        if(mService != null) {
            if (mService.getHent() != null) {
                inflater.inflate(R.menu.invoice_hent_crud_menu, menu);
            } else {
                inflater.inflate(R.menu.invoice_hent_menu, menu);
            }
        }
        else
        {
            scheduleAfterBoundService(new Runnable() {
                @Override
                public void run() {
                    getActivity().invalidateOptionsMenu();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menuAddHent:
                onAddHent();
                break;

            case R.id.menuEditHent:
                onEditHent();
                break;

            case R.id.menuScanHent:
                onScanHent();
                break;

            case R.id.menuFindHent:
                onFindHent();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void onEditHent() {

        Activity activity = getActivity();
        HentsStore hentsStore = ((BkApplication)activity.getApplication()).getStore().getHentsStore();
        final InvoiceHentObj invoiceHent = mService.getHent();
        HentObj hent = hentsStore.fetchHent(invoiceHent.getHentNo());
        if (hent == null) {


            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {

                        Intent addHentIntent = new Intent(getActivity(), NewHentActivity.class);
                        addHentIntent.putExtra(NewHentActivity.EXTRA_HENT_FOR_INPUTS, invoiceHent.asHent());
                        startActivityForResult(addHentIntent, NEW_HENT_REQUEST_CODE);
                    }
                }
            };

            Resources r = getResources();
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(r.getString(R.string.hentDoesntExistAnymoreAskAdd));
            builder.setPositiveButton(android.R.string.yes, dialogClickListener);
            builder.setNegativeButton(android.R.string.no, dialogClickListener);
            builder.show();

        } else {
            Intent editHentIntent = new Intent(activity, EditHentActivity.class);
            editHentIntent.putExtra(EditHentActivity.EXTRA_HENT_TO_EDIT_ID, hent.getId());
            startActivityForResult(editHentIntent, EDIT_HENT_REQUEST_CODE);
        }
    }

    private void onAddHent() {
        Intent addHentIntent = new Intent(getActivity(), NewHentActivity.class);
        startActivityForResult(addHentIntent, NEW_HENT_REQUEST_CODE);
    }

    private void onFindHent() {
        Intent findHentIntent = new Intent(getActivity(), FindHentActivity.class);
        startActivityForResult(findHentIntent, FIND_HENT_REQUEST_CODE);
    }

    private void onScanHent() {
        Intent scanFindHent = new Intent(getActivity(), ScanHentActivity.class);
        startActivityForResult(scanFindHent, SCAN_HENT_REQUEST_CODE);
    }

    void setInvoiceHentDeferred(final HentObj hent)
    {
        scheduleAfterBoundService(new Runnable() {
            @Override
            public void run() {
                setInvoiceHent(hent);
            }
        });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED)
            return;

        switch (requestCode) {
            case FIND_HENT_REQUEST_CODE:
                final HentObj foundHent = (HentObj) data.getSerializableExtra(FindHentActivity.EXTRA_FOUND_HENT);
                setInvoiceHentDeferred(foundHent);

                break;
            case EDIT_HENT_REQUEST_CODE:
                HentObj editedHent = (HentObj) data.getSerializableExtra(EditHentActivity.EXTRA_SAVED_HENT);
                setInvoiceHentDeferred(editedHent);
                break;
            case NEW_HENT_REQUEST_CODE:
                HentObj newHent = (HentObj) data.getSerializableExtra(NewHentActivity.EXTRA_SAVED_HENT);
                setInvoiceHentDeferred(newHent);
                break;
            case SCAN_HENT_REQUEST_CODE: {
                switch(resultCode)
                {
                    case ScanHentActivity.RESULT_OK:
                        HentObj hent = (HentObj) data.getSerializableExtra(ScanHentActivity.EXTRA_HENT);
                        if(hent != null)
                        {
                            setInvoiceHentDeferred(hent);
                        }
                        break;
                    case ScanHentActivity.RESULT_ADD_NEW_HENT:
                        EAN newHentHentNo = (EAN) data.getSerializableExtra(ScanHentActivity.EXTRA_NEWHENT_HENTNO);
                        if(newHentHentNo != null)
                        {
                            HentObj hent4Input = new HentObj(0);
                            hent4Input.setHentNo(newHentHentNo);

                            Intent newHentIntent = new Intent(getActivity(), NewHentActivity.class);
                            newHentIntent.putExtra(NewHentActivity.EXTRA_HENT_FOR_INPUTS,hent4Input);
                            startActivityForResult(newHentIntent, NEW_HENT_REQUEST_CODE);
                        }
                        break;
                }
                break;
            }
        }
    }


    private InvoiceType getInvoiceTypeFromHent(Hent hent) {
        HentType hentType = hent.getHentType();
        if (hentType == HentType.COMPANY)
            return InvoiceType.REGULAR;

        return InvoiceType.LUMP;
    }

    private Double getVatRateForHent(Hent hent) {
        HentType hentType = hent.getHentType();
        TaxSettings taxSettings = mDependencies.getTaxSettings();
        if (hentType == HentType.COMPANY) {
            return taxSettings.getVatRateForCompany();
        } else {
            return taxSettings.getVatRateForIndividual();
        }
    }

    private void setInvoiceHent(HentObj hent) {

        InvoiceHentObj invoiceHent = new InvoiceHentObj();
        invoiceHent.copyFrom(hent);
        mService.setHent(invoiceHent);

        InvoiceType invoiceType = getInvoiceTypeFromHent(hent);
        if (invoiceType != mService.getInvoiceType()) {
            mService.setInvoiceType(invoiceType);
            postEvent(new InvoiceTypeChanged(invoiceType));
        }

        Double vatRate = getVatRateForHent(hent);
        if (!EqualsUtils.equals(vatRate, mService.getVatRate())) {
            mService.setVatRate(vatRate);
            postEvent(new InvoiceVatRateChanged(vatRate));
        }

        postEvent(new InvoiceHentChanged());

        setBoxes(invoiceHent);
        getActivity().invalidateOptionsMenu();
    }
}
