package com.bk.bkskup3.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.bk.bkskup3.R;
import com.bk.bkskup3.model.DeductionDefinition;
import com.bk.bkskup3.runtime.YesNoString;
import com.bk.bkskup3.utils.Numbers;

/**
* Created with IntelliJ IDEA.
* User: SG0891787
* Date: 11/11/2014
* Time: 12:24 PM
*/
public class DeductionsListAdapter extends ArrayAdapter<DeductionDefinition> {

    private YesNoString mYesNoString;

    public DeductionsListAdapter(Context context) {
        super(context, R.layout.deductions_list_item, R.id.deductionCode);
        mYesNoString = new YesNoString(context.getResources());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        if (null == convertView) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.deductions_list_item, null);
        } else {
            row = convertView;
        }

        DeductionDefinition deduction = getItem(position);

        TextView deductionReason = (TextView) row.findViewById(R.id.deductionReason);
        deductionReason.setText(deduction.getReason());

        TextView deductionCode = (TextView) row.findViewById(R.id.deductionCode);
        deductionCode.setText(deduction.getCode());

        TextView deductionFraction = (TextView) row.findViewById(R.id.deductionFraction);
        deductionFraction.setText(Numbers.formatPercent(deduction.getFraction()));

        TextView deductAlways = (TextView) row.findViewById(R.id.deductAlways);
        deductAlways.setText(mYesNoString.toString(deduction.isEnabledByDefault()));

        return row;
    }
}
