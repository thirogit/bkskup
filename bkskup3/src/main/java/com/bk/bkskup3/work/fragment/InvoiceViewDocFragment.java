package com.bk.bkskup3.work.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.bk.bkskup3.R;
import com.bk.bkskup3.model.PayWay;
import com.bk.bkskup3.utils.Dates;

import java.util.Date;

public class InvoiceViewDocFragment extends InvoiceViewFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        return inflater.inflate(R.layout.invoiceview_doc, container, false);
    }

    public void onViewCreated(View view, Bundle state) {
        super.onViewCreated(view, state);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateInputsFromInvoice();
    }

    protected void updateTransactionPlace() {
        getTransactionPlaceBox().setText(mInvoice.getTransactionPlace());
    }

    protected void updateCustomNumber() {
        getCustomInvoiceNoBox().setText(mInvoice.getCustomNumber());
    }


    protected void updateExtras() {
        getExtrasBox().setText(mInvoice.getExtras());
    }

    protected EditText getCustomInvoiceNoBox() {
        return (EditText) getView().findViewById(R.id.customInvoiceNoValueBox);
    }

    protected EditText getTransactionDateBox() {
        return (EditText) getView().findViewById(R.id.transactionDateBox);
    }


    protected EditText getTransactionPlaceBox() {
        return (EditText) getView().findViewById(R.id.transactionPlaceBox);
    }

    protected EditText getExtrasBox() {
        return (EditText) getView().findViewById(R.id.extrasBox);
    }

    protected void updateTransactionDate() {

        Date transactionDate = mInvoice.getTransactionDt();
        String transactionDtStr = "";
        if (transactionDate != null) {
            transactionDtStr = Dates.toDayDate(transactionDate);
        }
        getTransactionDateBox().setText(transactionDtStr);
    }


    protected void updatePayWay() {
        PayWay payWay = mInvoice.getPayWay();

        String payWayStr = "";
        if (payWay == PayWay.CASH) {
            payWayStr = getString(R.string.payWayCash);
        } else if (payWay == PayWay.TRANSFER) {
            payWayStr = getString(R.string.payWayTransfer);
        }

        getPayWayBox().setText(payWayStr);
    }

    private EditText getPayWayBox() {
        return (EditText) getView().findViewById(R.id.payWayBox);
    }

    protected void updatePayDueDays() {

        String payDueDaysStr = "-";
        if (mInvoice.getPayWay() == PayWay.TRANSFER) {

            Integer payDueDays = mInvoice.getPayDueDays();
            if (payDueDays != null)
                payDueDaysStr = String.valueOf(payDueDays);
        }
        getPayDueDaysBox().setText(payDueDaysStr);
    }

    private EditText getPayDueDaysBox() {
        return (EditText) getView().findViewById(R.id.payDueDaysBox);
    }


    public void updateInputsFromInvoice() {
        updatePayWay();
        updatePayDueDays();
        updateTransactionPlace();
        updateTransactionDate();
        updateCustomNumber();
        updateExtras();
    }

}
