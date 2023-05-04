package com.bk.bkskup3.work.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.bk.bkskup3.R;
import com.bk.bkskup3.model.Invoice;
import com.bk.bkskup3.model.InvoiceType;
import com.bk.bkskup3.runtime.InvoiceCalculator;
import com.bk.bkskup3.utils.Numbers;
import com.bk.bkskup3.work.fragment.event.PurchaseLoaded;
import com.squareup.otto.Subscribe;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/14/13
 * Time: 10:00 AM
 */
public class PurchaseInvoicesFragment extends PurchaseFragment {

    class InvoiceItem {
        int invoiceId;
        InvoiceType invoiceType;
        String customNumber;
        String hent;
        int cowAmount;
        double netTotal;
        double grossTotal;
        double deductedGrossTotal;
    }

    protected InvoiceItemAdapter mAdapter;
    protected ListView mInvoicesList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.purchase, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ensureContent();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        refreshInvoicesList();
    }

    @Subscribe
    public void onPurchaseLoaded(PurchaseLoaded event)
    {
        refreshInvoicesList();
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    private void ensureContent() {
        View v = getView();

        mInvoicesList = (ListView) v.findViewById(R.id.list);

        mAdapter = new InvoiceItemAdapter(getActivity());

        mInvoicesList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        mInvoicesList.setAdapter(mAdapter);

        mInvoicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onInvoiceSelect((InvoiceItem) adapterView.getItemAtPosition(i));
            }
        });
    }

    private void onInvoiceSelect(InvoiceItem item) {
        getActivity().invalidateOptionsMenu();
    }

    private void refreshInvoicesList() {
        mAdapter.clear();

        if(mPurchase != null) {
            Collection<InvoiceItem> items = new ArrayList<InvoiceItem>();
            for (Invoice invoice : mPurchase.getInvoices()) {
                items.add(createItem(invoice));
            }
            mAdapter.addAll(items);
        }
    }

    protected InvoiceItem createItem(Invoice invoice)
    {
        InvoiceItem item = new InvoiceItem();
        refreshItem(item,invoice);
        return item;
    }

    protected void refreshItem(InvoiceItem item, Invoice invoice) {
        item.invoiceId = invoice.getId();
        item.cowAmount = invoice.getCowCount();
        item.customNumber = invoice.getCustomNumber();
        item.hent = invoice.getInvoiceHent().getHentName();
        item.invoiceType = invoice.getInvoiceType();

        InvoiceCalculator calculator = new InvoiceCalculator(invoice);

        item.grossTotal = calculator.getGross();
        item.netTotal = calculator.getNet();
        item.deductedGrossTotal = calculator.getGrossAfterDeductions();
    }

    protected static class InvoiceItemAdapter extends BaseAdapter {

        private List<InvoiceItem> mItems = new ArrayList<InvoiceItem>();
        private Map<Integer, InvoiceItem> mItemsById = new HashMap<Integer, InvoiceItem>();
        private Context mContext;

        private InvoiceItemAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public InvoiceItem getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mItems.get(position).invoiceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row;
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (null == convertView) {
                row = inflater.inflate(R.layout.invoice_list_item, null);
            } else {
                row = convertView;
            }

            InvoiceItem invoice = mItems.get(position);
            TextView invoiceCustomNoBox = (TextView) row.findViewById(R.id.invoiceCustomNoBox);
            TextView invoiceHentBox = (TextView) row.findViewById(R.id.invoiceHentBox);
            TextView invoiceCowAmountBox = (TextView) row.findViewById(R.id.invoiceCowAmountBox);
            TextView invoiceTotalNetBox = (TextView) row.findViewById(R.id.invoiceTotalNetBox);
            TextView invoiceTotalGrossBox = (TextView) row.findViewById(R.id.invoiceTotalGrossBox);
            TextView deductedInvoiceTotalGrossBox = (TextView) row.findViewById(R.id.deductedInvoiceTotalGrossBox);

            invoiceCowAmountBox.setText(String.valueOf(invoice.cowAmount));

            invoiceTotalNetBox.setText(Numbers.formatPrice(invoice.netTotal));
            invoiceHentBox.setText(invoice.hent);
            invoiceCustomNoBox.setText(invoice.customNumber);
            invoiceTotalGrossBox.setText(Numbers.formatPrice(invoice.grossTotal));
            deductedInvoiceTotalGrossBox.setText(Numbers.formatPrice(invoice.deductedGrossTotal));
            Resources r = mContext.getResources();
            InvoiceType invoiceType = invoice.invoiceType;
            View invoiceTypeIndicator = row.findViewById(R.id.invoiceTypeIndicator);
            switch (invoiceType) {
                case LUMP:
                    invoiceTypeIndicator.setBackgroundColor(r.getColor(R.color.lumpInvoiceColor));
                    break;
                case REGULAR:
                    invoiceTypeIndicator.setBackgroundColor(r.getColor(R.color.regularInvoiceColor));
                    break;
            }
            return row;
        }

        public InvoiceItem findItemWithId(int invoiceId)
        {
            return mItemsById.get(invoiceId);
        }

        public void removeInvoice(int invoiceId) {
            InvoiceItem item;
            for(int i = 0,count = getCount();i < count;i++)
            {
                item = mItems.get(i);
                if(item.invoiceId == invoiceId)
                {
                    mItems.remove(i);
                    mItemsById.remove(invoiceId);
                    notifyDataSetChanged();
                    break;
                }
            }
        }

        public void clear() {
            mItemsById.clear();
            mItems.clear();
            notifyDataSetChanged();
        }

        public void add(InvoiceItem item) {
            internalAdd(item);
            notifyDataSetChanged();
        }

        private void internalAdd(InvoiceItem item) {
            if(findItemWithId(item.invoiceId) != null)
            {
                removeInvoice(item.invoiceId);
            }

            mItems.add(item);
            mItemsById.put(item.invoiceId,item);
        }

        public void addAll(Collection<InvoiceItem> items) {
            for(InvoiceItem item : items)
            {
                internalAdd(item);
            }
        }
    }
}
