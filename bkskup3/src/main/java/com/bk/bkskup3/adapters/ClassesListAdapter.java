package com.bk.bkskup3.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.bk.bkskup3.R;
import com.bk.bkskup3.model.CowClass;
import com.bk.bkskup3.runtime.CowSexString;
import com.bk.bkskup3.utils.Numbers;

/**
* Created with IntelliJ IDEA.
* User: SG0891787
* Date: 11/11/2014
* Time: 12:24 PM
*/
public class ClassesListAdapter extends ArrayAdapter<CowClass> {

    private CowSexString mCowSexString;

    public ClassesListAdapter(Context context) {
        super(context, R.layout.cowclasses_list_item, R.id.classCode);
        mCowSexString = new CowSexString(context.getResources());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        if (null == convertView) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.cowclasses_list_item, null);
        } else {
            row = convertView;
        }

        CowClass cowClass = getItem(position);

        TextView className = (TextView) row.findViewById(R.id.className);
        className.setText(cowClass.getClassName());

        TextView classCode = (TextView) row.findViewById(R.id.classCode);
        classCode.setText(cowClass.getClassCode());

        TextView classPredefSex = (TextView) row.findViewById(R.id.classPredefSex);
        classPredefSex.setText(mCowSexString.toString(cowClass.getPredefSex()));

        TextView classPricePerKg = (TextView) row.findViewById(R.id.classPricePerKg);
        classPricePerKg.setText(Numbers.formatPrice(cowClass.getPricePerKg()));

        return row;
    }
}
