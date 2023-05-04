package com.bk.bkskup3.print.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bk.bkskup3.R;
import com.bk.bkskup3.library.DocumentProfile;

/**
 * Created by SG0891787 on 10/3/2017.
 */

public class ProfilesListAdapter extends ArrayAdapter<DocumentProfile> {
    public ProfilesListAdapter(Context context) {
        super(context, R.layout.profiles_list_item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        if (null == convertView) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.profiles_list_item, null);
        } else {
            row = convertView;
        }

        DocumentProfile profile = getItem(position);
        TextView text = (TextView) row.findViewById(R.id.profileName);
        text.setText(profile.getProfileName());

        return row;
    }
}
