package com.bk.bkskup3.work.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.bk.bkskup3.R;
import com.bk.bkskup3.utils.Numbers;
import com.bk.bkskup3.widgets.CheckBoxIndicator;
import com.bk.bkskup3.work.input.CowInput;
import com.bk.bkskup3.work.input.DeductionInput;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/18/2014
 * Time: 10:31 AM
 */
public class InvoiceViewDeductionFragment extends InvoiceViewFragment {

    private ListView mDeductionsList;
    private ArrayAdapter<DeductionItem> mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.invoice_deductions, container, false);

        mDeductionsList = (ListView) v.findViewById(R.id.list);

        mDeductionsList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        return v;
    }


    private double getInvoiceTotalNet() {
        double total = 0.0;
        for (CowInput cow : mInvoice.getCows()) {
            total += cow.getNetPrice();
        }
        return total;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new ArrayAdapter<DeductionItem>(getActivity(), R.layout.invoiceview_deductions_list_item) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View row;
                LayoutInflater inflater = LayoutInflater.from(getContext());

                if (null == convertView) {
                    row = inflater.inflate(R.layout.invoiceview_deductions_list_item, null);
                } else {
                    row = convertView;
                }

                final DeductionItem item = getItem(position);

                TextView deductionCodeBox = (TextView) row.findViewById(R.id.deductionCode);
                TextView deductionFractionBox = (TextView) row.findViewById(R.id.deductionFraction);
                TextView deductedAmountBox = (TextView) row.findViewById(R.id.deductedAmount);
                CheckBoxIndicator deductionEnabledBox = (CheckBoxIndicator) row.findViewById(R.id.invoiceDeductionCheck);

                deductionCodeBox.setText(item.code);
                deductionFractionBox.setText(Numbers.formatPercent(item.fraction));
                deductedAmountBox.setText(Numbers.formatPrice(item.amount));
                deductionEnabledBox.setChecked(item.enabled);

                return row;
            }
        };


        double invoiceTotalNet = getInvoiceTotalNet();

        for (DeductionInput deduction : mInvoice.getDeductions()) {
            DeductionItem item = new DeductionItem();


            item.id = deduction.getInputId();
            item.code = deduction.getCode();
            item.fraction = deduction.getFraction();
            item.enabled = deduction.isEnabled();

            if (deduction.isEnabled()) {
                item.amount = item.fraction * invoiceTotalNet;
            } else {
                item.amount = null;
            }

            item.enabled = deduction.isEnabled();
            mAdapter.add(item);
        }

        mDeductionsList.setAdapter(mAdapter);
    }


}