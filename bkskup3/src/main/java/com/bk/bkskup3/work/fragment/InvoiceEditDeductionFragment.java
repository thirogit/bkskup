package com.bk.bkskup3.work.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.bk.bkskup3.R;
import com.bk.bkskup3.utils.MoneyRounding;
import com.bk.bkskup3.utils.Numbers;
import com.bk.bkskup3.work.EditInvoiceDeductionActivity;
import com.bk.bkskup3.work.NewInvoiceDeductionActivity;
import com.bk.bkskup3.work.fragment.event.*;
import com.bk.bkskup3.work.input.CowInput;
import com.bk.bkskup3.work.input.DeductionInput;
import com.squareup.otto.InheritSubscribers;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/18/2014
 * Time: 10:31 AM
 */
@InheritSubscribers
public class InvoiceEditDeductionFragment extends InvoiceEditFragment {

    class InvoiceDeductionListAdapter extends BaseAdapter {

        private Context mContext;
        private List<DeductionItem> mItems = new ArrayList<DeductionItem>();

        public InvoiceDeductionListAdapter(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        public DeductionItem getDeductionItemAt(int position) {
            return mItems.get(position);
        }

        public DeductionItem findDeduction(UUID id) {
            for (Iterator<DeductionItem> iterator = mItems.iterator(); iterator.hasNext(); ) {
                DeductionItem item = iterator.next();
                if(item.id.equals(id))
                {
                    return item;
                }
            }
            return null;
        }

        public void addItem(DeductionItem item)
        {
            mItems.add(item);
        }

        public void removeItem(UUID id)
        {
            for (Iterator<DeductionItem> iterator = mItems.iterator(); iterator.hasNext(); ) {
                DeductionItem item = iterator.next();
                if(item.id.equals(id))
                {
                    iterator.remove();
                    break;
                }
            }
        }

        @Override
        public Object getItem(int position) {
            return getDeductionItemAt(position);
        }

        @Override
        public long getItemId(int position) {
            return getDeductionItemAt(position).id.hashCode() ;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row;
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (null == convertView) {
                row = inflater.inflate(R.layout.invoice_deductions_list_item, null);
            } else {
                row = convertView;
            }

            final DeductionItem item = getDeductionItemAt(position);

            TextView deductionCodeBox = (TextView) row.findViewById(R.id.deductionCode);
            TextView deductionFractionBox = (TextView) row.findViewById(R.id.deductionFraction);
            TextView deductedAmountBox = (TextView) row.findViewById(R.id.deductedAmount);
            CheckBox deductionCheckBox = (CheckBox) row.findViewById(R.id.invoiceDeductionCheck);

            deductionCodeBox.setText(item.code);
            deductionFractionBox.setText(Numbers.formatPercent(item.fraction));
            deductedAmountBox.setText(Numbers.formatPrice(item.amount));
            deductionCheckBox.setChecked(item.enabled);

            deductionCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean checked) {
                    onDeductionCheck(item,checked);
                }
            });

            return row;
        }

        public void delete(int position) {
            mItems.remove(position);
            notifyDataSetChanged();
        }
    }


    private static final int NEW_DEDUCTION_REQUEST_CODE = 501;
    private static final int EDIT_DEDUCTION_REQUEST_CODE = 502;

    private ListView mDeductionsList;
    private InvoiceDeductionListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.invoice_deductions, container, false);

        mDeductionsList = (ListView) v.findViewById(R.id.list);

        mDeductionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onDeductionSelect(((DeductionItem) adapterView.getItemAtPosition(i)).id);

            }
        });

        mDeductionsList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        return v;
    }

    private void onDeductionSelect(UUID deductionInputId) {
        getActivity().invalidateOptionsMenu();
    }

    private double getInvoiceTotalNet()
    {
        double total = 0.0;
        for(CowInput cow : mService.getCows())
        {
            total += cow.getNetPrice();
        }
        return total;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        mAdapter = new InvoiceDeductionListAdapter(getActivity());
        scheduleAfterBoundService(new Runnable() {
            @Override
            public void run() {
                for (DeductionInput deduction : mService.getDeductions()) {
                    DeductionItem item = new DeductionItem();
                    refreshItemFromDeduction(item, deduction);
                    mAdapter.addItem(item);
                }
                mDeductionsList.setAdapter(mAdapter);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        if(mDeductionsList.getCheckedItemPosition() >= 0) {
            inflater.inflate(R.menu.invoice_deduction_crud_menu, menu);
        } else {
            inflater.inflate(R.menu.invoice_deduction_menu, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menuAddDeduction:
                onAddNewDeduction();
                break;

            case R.id.menuEditDeduction:
                onEditDeduction();
                break;

            case R.id.menuDeleteDeduction:
                onDeleteDeduction();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onDeleteDeduction() {
        int selectedDeduction = mDeductionsList.getCheckedItemPosition();
        if (selectedDeduction >= 0) {

            DeductionItem deductionItem = mAdapter.getDeductionItemAt(selectedDeduction);
            mService.deleteDeduction(deductionItem.id);
            mAdapter.delete(selectedDeduction);
            postEvent(new DeductionDeletedEvent(deductionItem.id));
        }
    }

    public DeductionInput getSelectedDeduction() {
        int selectedDeduction = mDeductionsList.getCheckedItemPosition();
        if (selectedDeduction >= 0) {

            DeductionItem deductionItem = mAdapter.getDeductionItemAt(selectedDeduction);
            return mService.getDeduction(deductionItem.id);
        }
        return null;
    }

    private void onEditDeduction() {

        DeductionInput selectedDeduction = getSelectedDeduction();
        if(selectedDeduction != null)
        {
            Intent editDeduction = new Intent(getActivity(), EditInvoiceDeductionActivity.class);
            editDeduction.putExtra(EditInvoiceDeductionActivity.EXTRA_DEDUCTION_INPUT, selectedDeduction);
            startActivityForResult(editDeduction, EDIT_DEDUCTION_REQUEST_CODE);
        }

    }

    private void onAddNewDeduction() {

        Intent newDeduction = new Intent(getActivity(), NewInvoiceDeductionActivity.class);
        newDeduction.putExtra(NewInvoiceDeductionActivity.EXTRA_DEDUCTION_INPUT,new DeductionInput());
        startActivityForResult(newDeduction,NEW_DEDUCTION_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK)
        {
            switch(requestCode)
            {
                case NEW_DEDUCTION_REQUEST_CODE:

                    final DeductionInput newDeduction = (DeductionInput) data.getSerializableExtra(NewInvoiceDeductionActivity.EXTRA_DEDUCTION_INPUT);

                    scheduleAfterBoundService(new Runnable() {
                        @Override
                        public void run() {
                            addNewDeduction(newDeduction);
                        }
                    });

                    break;

                case EDIT_DEDUCTION_REQUEST_CODE:
                    final DeductionInput editedDeduction = (DeductionInput) data.getSerializableExtra(EditInvoiceDeductionActivity.EXTRA_DEDUCTION_INPUT);
                    scheduleAfterBoundService(new Runnable() {
                        @Override
                        public void run() {
                            editDeduction(editedDeduction);
                        }
                    });
                    break;
            }

        }
    }

    private void editDeduction(DeductionInput editedDeduction) {
        DeductionInput deductionToUpdate = mService.getDeduction(editedDeduction.getInputId());
        deductionToUpdate.copyFrom(editedDeduction);
        postEvent(new DeductionUpdatedEvent(editedDeduction.getInputId()));
        DeductionItem item = mAdapter.findDeduction(editedDeduction.getInputId());
        refreshItemFromDeduction(item,deductionToUpdate);

    }

    void refreshItemFromDeduction(DeductionItem item,DeductionInput deductionInput)
    {
        double invoiceTotalNet = getInvoiceTotalNet();
        item.id = deductionInput.getInputId();
        item.code = deductionInput.getCode();
        item.fraction = deductionInput.getFraction();
        item.enabled = deductionInput.isEnabled();

        if(deductionInput.isEnabled()) {
            item.amount = MoneyRounding.roundToInteger(item.fraction * invoiceTotalNet);
        } else {
            item.amount = null;
        }

        item.enabled = deductionInput.isEnabled();
    }

    private void addNewDeduction(DeductionInput newDeduction) {
        UUID deductionId = mService.addDeduction(newDeduction);

        postEvent(new DeductionAddedEvent(deductionId));

        DeductionInput deduction = mService.getDeduction(deductionId);
        DeductionItem item = new DeductionItem();
        refreshItemFromDeduction(item,deduction);
        mAdapter.addItem(item);
    }

    private void onDeductionCheck(DeductionItem item, boolean check) {

        DeductionInput deduction = mService.getDeduction(item.id);
        deduction.setEnabled(check);
        refreshItemFromDeduction(item,deduction);
        mAdapter.notifyDataSetInvalidated();

        if(check) {
            postEvent(new DeductionEnabled(deduction.getInputId()));
        } else {
            postEvent(new DeductionDisabled(deduction.getInputId()));
        }
    }

   @Subscribe
   public void onCowAdded(CowAddedEvent event)
   {
        refreshItems();
   }

    @Subscribe
    public void onCowUpdated(CowEditedEvent event)
    {
        refreshItems();
    }

    @Subscribe
    public void onCowDeleted(CowDeletedEvent event)
    {
        refreshItems();
    }

    private void refreshItems() {


        for(int i = 0,count = mAdapter.getCount(); i < count;i++)
        {
            DeductionItem item = mAdapter.getDeductionItemAt(i);
            DeductionInput deduction = mService.getDeduction(item.id);
            refreshItemFromDeduction(item,deduction);
        }

        mAdapter.notifyDataSetInvalidated();

    }

}
