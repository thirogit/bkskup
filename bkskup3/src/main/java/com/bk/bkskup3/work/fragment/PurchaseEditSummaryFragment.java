package com.bk.bkskup3.work.fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.bk.bkskup3.R;
import com.bk.bkskup3.work.fragment.event.InvoiceAdded;
import com.bk.bkskup3.work.fragment.event.InvoiceDeleted;
import com.bk.bkskup3.work.fragment.event.InvoiceUpdated;
import com.bk.bkskup3.work.fragment.event.NewInvoiceMenuItemSelected;
import com.squareup.otto.Subscribe;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/14/13
 * Time: 10:00 AM
 */

public class PurchaseEditSummaryFragment extends PurchaseSummaryFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Subscribe
    public void onInvoiceAdded(InvoiceAdded event) {
        refreshSummary();
    }

    @Subscribe
    public void onInvoiceDeleted(InvoiceDeleted event) {
        refreshSummary();
    }

    @Subscribe
    public void onInvoiceUpdated(InvoiceUpdated event) {
        refreshSummary();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.purchase_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menuNewInvoice:
                onNewInvoice();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onNewInvoice() {
      postEvent(new NewInvoiceMenuItemSelected());
    }




}
