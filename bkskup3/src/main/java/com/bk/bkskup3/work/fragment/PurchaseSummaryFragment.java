package com.bk.bkskup3.work.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.bk.bkskup3.R;
import com.bk.bkskup3.model.Invoice;
import com.bk.bkskup3.runtime.InvoiceCalculator;
import com.bk.bkskup3.utils.Numbers;
import com.bk.bkskup3.work.fragment.event.PurchaseLoaded;
import com.squareup.otto.Subscribe;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/14/13
 * Time: 10:00 AM
 */

public class PurchaseSummaryFragment extends PurchaseFragment {

    private TextView mCowCountBox;
    private TextView mTotalWeightBox;
    private TextView mTotalNetBox;
    private TextView mTotalGrossBox;
    private TextView mTotalGrossAfterDeductionsBox;
    private TextView mCashCowCountBox;
    private TextView mTransferCowCountBox;
    private TextView mCashTotalWeightBox;
    private TextView mTransferTotalWeightBox;
    private TextView mCashTotalNetBox;
    private TextView mTransferTotalNetBox;
    private TextView mCashTotalGrossBox;
    private TextView mTransferTotalGrossBox;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.purchase_summary, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ensureContent();
    }

    private void ensureContent() {

        mCowCountBox = (TextView) findView(R.id.purchaseSummaryCowCountBox);
        mTotalWeightBox = (TextView) findView(R.id.purchaseSummaryTotalWeightBox);
        mTotalNetBox = (TextView) findView(R.id.purchaseSummaryTotalNetBox);
        mTotalGrossBox = (TextView) findView(R.id.purchaseSummaryTotalGrossBox);
        mTotalGrossAfterDeductionsBox = (TextView) findView(R.id.purchaseSummaryTotalGrossAfterDeductionsBox);
        mCashCowCountBox = (TextView) findView(R.id.purchaseSummaryCashCowCountBox);
        mTransferCowCountBox = (TextView) findView(R.id.purchaseSummaryTransferCowCountBox);
        mCashTotalWeightBox = (TextView) findView(R.id.purchaseSummaryCashTotalWeightBox);
        mTransferTotalWeightBox = (TextView) findView(R.id.purchaseSummaryTransferTotalWeightBox);
        mCashTotalNetBox = (TextView) findView(R.id.purchaseSummaryCashTotalNetBox);
        mTransferTotalNetBox = (TextView) findView(R.id.purchaseSummaryTransferTotalNetBox);
        mCashTotalGrossBox = (TextView) findView(R.id.purchaseSummaryCashTotalGrossBox);
        mTransferTotalGrossBox = (TextView) findView(R.id.purchaseSummaryTransferTotalGrossBox);
    }

    private View findView(int viewId) {
        return getView().findViewById(viewId);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refreshSummary();
    }


    protected void refreshSummary() {


        double totalNet = 0.0;
        double totalGross = 0.0;
        double totalWeight = 0.0;

        int cowCount = 0;
        int cashCowCount = 0;
        int transferCowCount = 0;
        double cashTotalWeight = 0.0;
        double transferTotalWeight = 0.0;
        double cashTotalNet = 0.0;
        double transferTotalNet = 0.0;
        double cashTotalGross = 0.0;
        double transferTotalGross = 0.0;
        double transferTotalGrossAfterDeduction = 0.0;

        if(mPurchase != null) {

            cowCount = mPurchase.getCowCount();
            for (Invoice invoice : mPurchase.getInvoices()) {
                totalNet += invoice.getTotalNet();
                totalGross += invoice.getTotalGross();
                InvoiceCalculator calculator = new InvoiceCalculator(invoice);
                transferTotalGrossAfterDeduction += calculator.getGrossAfterDeductions();
                double invoiceTotalWeight = calculator.getTotalWeight();
                totalWeight += invoiceTotalWeight;

                switch (invoice.getPayWay()) {
                    case CASH:
                        cashCowCount += invoice.getCowCount();
                        cashTotalNet += invoice.getTotalNet();
                        cashTotalGross += invoice.getTotalGross();
                        cashTotalWeight += invoiceTotalWeight;

                        break;
                    case TRANSFER:
                        transferCowCount += invoice.getCowCount();
                        transferTotalNet += invoice.getTotalNet();
                        transferTotalGross += invoice.getTotalGross();
                        transferTotalWeight += invoiceTotalWeight;
                        break;
                }
            }
        }

        mCowCountBox.setText(String.valueOf(cowCount));
        mTotalWeightBox.setText(Numbers.formatWeight(totalWeight));
        mTotalGrossBox.setText(Numbers.formatPrice(totalGross));
        mTotalGrossAfterDeductionsBox.setText(Numbers.formatPrice(transferTotalGrossAfterDeduction));
        mTotalNetBox.setText(Numbers.formatPrice(totalNet));

        mCashCowCountBox.setText(String.valueOf(cashCowCount));
        mTransferCowCountBox.setText(String.valueOf(transferCowCount));
        mCashTotalWeightBox.setText(Numbers.formatWeight(cashTotalWeight));
        mTransferTotalWeightBox.setText(Numbers.formatWeight(transferTotalWeight));
        mCashTotalNetBox.setText(Numbers.formatPrice(cashTotalNet));
        mTransferTotalNetBox.setText(Numbers.formatPrice(transferTotalNet));
        mCashTotalGrossBox.setText(Numbers.formatPrice(cashTotalGross));
        mTransferTotalGrossBox.setText(Numbers.formatPrice(transferTotalGross));

    }

    @Subscribe
    public void onPurchaseLoaded(PurchaseLoaded event)
    {
        refreshSummary();
    }

}
