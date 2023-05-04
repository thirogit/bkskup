package com.bk.bkskup3.work.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.bk.bkskup3.R;
import com.bk.bkskup3.print.PrintActivity;
import com.bk.bkskup3.work.InvoiceViewActivity;
import com.squareup.otto.InheritSubscribers;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/14/13
 * Time: 10:00 AM
 */

@InheritSubscribers
public class PurchaseViewInvoicesFragment extends PurchaseInvoicesFragment {

    private static final int VIEW_INVOICE_REQUEST_CODE = 1001;

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(mInvoicesList.getCheckedItemPosition() >= 0)
            inflater.inflate(R.menu.purchase_view_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menuViewInvoice:
                onViewInvoice();
                break;

            case R.id.menuPrintInvoice:
                onPrintInvoice();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onViewInvoice() {
        int position = mInvoicesList.getCheckedItemPosition();
        InvoiceItem item = mAdapter.getItem(position);
        Intent intent = new Intent(getActivity(), InvoiceViewActivity.class);
        intent.putExtra(InvoiceViewActivity.EXTRA_INVOICE_ID,item.invoiceId);
        startActivityForResult(intent,VIEW_INVOICE_REQUEST_CODE);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == VIEW_INVOICE_REQUEST_CODE)
        {
            if(resultCode == InvoiceViewActivity.RESULT_PRINT)
            {
                int invoiceId = data.getIntExtra(InvoiceViewActivity.EXTRA_INVOICE_ID,0);
                startPrintInvoiceActivity(invoiceId);
            }
        }
    }

    private void onPrintInvoice() {
        int position = mInvoicesList.getCheckedItemPosition();
        InvoiceItem item = mAdapter.getItem(position);
        startPrintInvoiceActivity(item.invoiceId);
    }

    private void startPrintInvoiceActivity(int invoiceId) {
        Intent intent = new Intent(getActivity(), PrintActivity.class);
        intent.putExtra(PrintActivity.EXTRA_INVOICE_TO_PRINT, invoiceId);
        startActivity(intent);
    }

}
