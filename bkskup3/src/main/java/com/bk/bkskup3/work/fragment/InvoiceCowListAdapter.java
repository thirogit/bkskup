package com.bk.bkskup3.work.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.bk.bkskup3.R;
import com.bk.bkskup3.utils.Numbers;
import com.bk.bkskup3.work.input.CowInput;

import java.util.*;

class InvoiceCowListAdapter extends BaseAdapter {

    private boolean mNotifyOnChange = true;
    private List<CowInput> cows = new ArrayList<CowInput>();
    private Map<UUID,CowInput> idToCow = new HashMap<UUID, CowInput>();
    private Context context;


    InvoiceCowListAdapter(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (null == convertView) {
            row = inflater.inflate(R.layout.cow_list_item, null);
        } else {
            row = convertView;
        }

        CowInput cow = getItem(position);

        TextView cowNoBox = (TextView) row.findViewById(R.id.cowNo);
        TextView cowWeightBox = (TextView) row.findViewById(R.id.cowWeight);
        TextView cowClassCdBox = (TextView) row.findViewById(R.id.cowClassCd);
        TextView cowNetPriceBox = (TextView) row.findViewById(R.id.cowNetPrice);
        TextView cowNetPricePerKgListLbl = (TextView) row.findViewById(R.id.cowNetPricePerKg);

        cowNoBox.setText(cow.getCowNo().toString());
        double weight = cow.getWeight();
        cowWeightBox.setText(Numbers.formatWeight(weight));
        cowClassCdBox.setText(cow.getClassCd());
        double netPrice = cow.getNetPrice();
        cowNetPriceBox.setText(Numbers.formatPrice(netPrice));
        cowNetPricePerKgListLbl.setText(Numbers.formatWithPrecision(netPrice / weight, 4));

        return row;
    }

    public void add(CowInput cow) {

        UUID inputId = cow.getInputId();
        if(idToCow.get(inputId) != null)
                throw new IllegalArgumentException("cow with inputid=" + inputId + " already present");

        cows.add(cow);
        idToCow.put(inputId, cow);

        if (mNotifyOnChange) notifyDataSetChanged();
    }

    public void delete(int position) {

        CowInput cowInput = cows.get(position);
        idToCow.remove(cowInput.getInputId());
        cows.remove(position);
        if (mNotifyOnChange) notifyDataSetChanged();
    }

    public CowInput getCow(UUID id)
    {
        return idToCow.get(id);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        mNotifyOnChange = true;
    }

    public void setNotifyOnChange(boolean notifyOnChange) {
        mNotifyOnChange = notifyOnChange;
    }

    public int getCount() {
        return cows.size();
    }


    public CowInput getItem(int position) {
        return cows.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

}
