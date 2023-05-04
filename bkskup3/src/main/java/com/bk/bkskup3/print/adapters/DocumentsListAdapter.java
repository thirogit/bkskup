package com.bk.bkskup3.print.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bk.bkskup3.R;
import com.bk.bkskup3.library.DocumentDescription;

/**
 * Created by SG0891787 on 9/25/2017.
 */
public class DocumentsListAdapter extends ArrayAdapter<DocumentDescription> {
    public DocumentsListAdapter(Context context) {
        super(context, R.layout.document_list_item);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (null == convertView) {
            row = inflater.inflate( R.layout.document_list_item, null);
        } else {
            row = convertView;
        }

        TextView name = (TextView) row.findViewById(R.id.documentName);
        TextView indicator = (TextView) row.findViewById(R.id.hasProfilesInd);
        DocumentDescription item = getItem(position);


        name.setText(item.getDocName());

        if(item.getProfileCount() > 0) {
            indicator.setBackgroundResource(R.drawable.ic_action_more_vert);
        } else
        {
            indicator.setBackground(null);
        }
        return row;
    }
}
