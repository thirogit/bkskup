package com.bk.bkskup3.work.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import com.bk.bkskup3.R;
import com.bk.bkskup3.work.CowViewActivity;
import com.bk.bkskup3.work.input.CowInput;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/14/13
 * Time: 10:00 AM
 */

public class InvoiceViewCowsFragment extends InvoiceViewFragment {

    protected ListView mCowsList;
    protected InvoiceCowListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.invoice_cows, container, false);

        mCowsList = (ListView) v.findViewById(R.id.list);

        mCowsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onCowSelect((CowInput) adapterView.getItemAtPosition(i));
            }
        });

        mCowsList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new InvoiceCowListAdapter(getActivity());
        for (CowInput cow : mInvoice.getCows()) {
            mAdapter.add(cow);
        }
        mCowsList.setAdapter(mAdapter);

        setHasOptionsMenu(true);

    }

    private void onCowSelect(CowInput cow) {
        getActivity().invalidateOptionsMenu();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menuViewCow:
                onViewCow();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onViewCow() {

        int position = mCowsList.getCheckedItemPosition();
        if (position >= 0) {
            CowInput item = mAdapter.getItem(position);
            Intent intent = new Intent(getActivity(), CowViewActivity.class);
            intent.putExtra(CowViewActivity.EXTRA_COW_INPUT,item);
            intent.putExtra(CowViewActivity.EXTRA_VAT_RATE,mInvoice.getVatRate());
            startActivity(intent);
        }


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        if (mCowsList.getCheckedItemPosition() >= 0) {
            inflater.inflate(R.menu.invoice_cowview_menu, menu);
        } else {
//            inflater.inflate(R.menu.finish_menu, menu);
        }
    }
}
