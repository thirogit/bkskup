package com.bk.bkskup3.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.bk.bkskup3.R;
import com.bk.bkskup3.model.Stock;

/**
* Created with IntelliJ IDEA.
* User: SG0891787
* Date: 11/11/2014
* Time: 12:24 PM
*/
public class StocksListAdapter extends ArrayAdapter<Stock> {
    public StocksListAdapter(Context context) {
        super(context, R.layout.stocks_list_item, R.id.stockCode);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        if (null == convertView) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.stocks_list_item, null);
        } else {
            row = convertView;
        }

        Stock stock = getItem(position);

        TextView stockName = (TextView) row.findViewById(R.id.stockName);
        stockName.setText(stock.getStockName());

        TextView stockCode = (TextView) row.findViewById(R.id.stockCode);
        stockCode.setText(stock.getStockCode());

        return row;
    }
}
