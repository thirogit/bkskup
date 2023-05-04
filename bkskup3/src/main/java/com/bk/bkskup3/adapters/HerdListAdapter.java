package com.bk.bkskup3.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.bk.bkskup3.R;
import com.bk.bkskup3.model.Herd;

/**
* Created with IntelliJ IDEA.
* User: SG0891787
* Date: 11/11/2014
* Time: 12:24 PM
*/
public class HerdListAdapter extends ArrayAdapter<Herd> {
    public HerdListAdapter(Context context) {
        super(context, R.layout.herd_list_item, R.id.herdNumber);
    }

    public void removeAt(int index)
    {

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        if (null == convertView) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.herd_list_item, null);
        } else {
            row = convertView;
        }

        Herd herd = getItem(position);

        TextView herdNumberLbl = (TextView) row.findViewById(R.id.herdNumber);
        herdNumberLbl.setText(String.format("%03d", herd.getHerdNo()));

        TextView herdAddressLbl = (TextView) row.findViewById(R.id.herdAddress);

        String herdAddress = herd.getStreet() + " " + herd.getPoBox() + "\n" + herd.getZip() + " " + herd.getCity();

        herdAddressLbl.setText(herdAddress);

        return row;
    }
}
