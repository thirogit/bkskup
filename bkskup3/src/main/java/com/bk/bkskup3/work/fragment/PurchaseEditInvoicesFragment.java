package com.bk.bkskup3.work.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.*;

import com.bk.bkskup3.BkApplication;
import com.bk.bkskup3.R;
import com.bk.bkskup3.dao.HentsStore;
import com.bk.bkskup3.dao.PurchasesStore;
import com.bk.bkskup3.model.InvoiceHentObj;
import com.bk.bkskup3.model.InvoiceObj;
import com.bk.bkskup3.work.fragment.event.*;
import com.squareup.otto.InheritSubscribers;
import com.squareup.otto.Subscribe;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/14/13
 * Time: 10:00 AM
 */

@InheritSubscribers
public class PurchaseEditInvoicesFragment extends PurchaseInvoicesFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(mInvoicesList.getCheckedItemPosition() >= 0)
            inflater.inflate(R.menu.purchase_crud_menu, menu);
        else
            inflater.inflate(R.menu.purchase_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menuNewInvoice:
                onNewInvoice();
                break;
            case R.id.menuEditInvoice:
                onEditInvoice();
                break;
            case R.id.menuPrintInvoice:
                onPrintInvoice();
                break;
            case R.id.menuDeleteInvoice:
                onDeleteInvoice();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onDeleteInvoice() {

        int position = mInvoicesList.getCheckedItemPosition();
        if(position >= 0) {
            final InvoiceItem item = mAdapter.getItem(position);

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        deleteInvoice(item.invoiceId);
                        mAdapter.removeInvoice(item.invoiceId);
                        mInvoicesList.clearChoices();
                        postEvent(new InvoiceDeleted(item.invoiceId));
                        getFragmentManager().invalidateOptionsMenu();
                    }
                }
            };

            Resources r = getResources();
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(r.getString(R.string.askDeleteInvoice));
            builder.setPositiveButton(android.R.string.yes, dialogClickListener);
            builder.setNegativeButton(android.R.string.no, dialogClickListener);
            builder.show();
        }
    }

    private void deleteInvoice(int invoiceId) {
        PurchasesStore purchasesStore = ((BkApplication)getActivity().getApplication()).getStore().getPurchasesStore();
        purchasesStore.deleteInvoice(invoiceId);
    }

    private void onNewInvoice() {
        postEvent(new NewInvoiceMenuItemSelected());
    }

    private void onPrintInvoice() {
        int position = mInvoicesList.getCheckedItemPosition();
        InvoiceItem item = mAdapter.getItem(position);
        postEvent(new PrintInvoiceMenuItemSelected(item.invoiceId));
    }

    private void onEditInvoice()
    {
        int position = mInvoicesList.getCheckedItemPosition();
        InvoiceItem item = mAdapter.getItem(position);
        postEvent(new EditInvoiceMenuItemSelected(item.invoiceId));
    }



    @Subscribe
    public void onInvoiceAdded(InvoiceAdded event) {
        mAdapter.add(createItem(event.getInvoice()));
    }

    @Subscribe
    public void onInvoiceUpdated(InvoiceUpdated event) {
        InvoiceObj updatedInvoice = event.getInvoice();
        PurchaseInvoicesFragment.InvoiceItem item = mAdapter.findItemWithId(updatedInvoice.getId());
        refreshItem(item, updatedInvoice);
        mAdapter.notifyDataSetInvalidated();
    }

}
