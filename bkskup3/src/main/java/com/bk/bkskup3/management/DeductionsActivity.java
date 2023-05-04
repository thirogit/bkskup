package com.bk.bkskup3.management;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bk.bkskup3.BkActivity;
import com.bk.bkskup3.R;
import com.bk.bkskup3.adapters.DeductionsListAdapter;
import com.bk.bkskup3.dao.DefinitionsStore;
import com.bk.bkskup3.feedback.ErrorToast;
import com.bk.bkskup3.model.DeductionDefinition;
import com.bk.bkskup3.model.DeductionDefinitionObj;

import java.util.Collection;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/9/2014
 * Time: 1:50 PM
 */
public class DeductionsActivity extends BkActivity {

    private ListView mListView;
    private ArrayAdapter<DeductionDefinition> mListAdapter;

    @Inject
    DefinitionsStore mDefinitionsStore;

    private static final int REQUEST_CODE_NEW_DEDUCTION = 101;
    private static final int REQUEST_CODE_EDIT_DEDUCTION = 102;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deductions_management);

        mListView = (ListView) findViewById(R.id.list);
        mListAdapter = new DeductionsListAdapter(this);

        mListView.setAdapter(mListAdapter);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                invalidateOptionsMenu();
            }
        });

        refreshDeductionsList();

    }

    private void refreshDeductionsList() {
        Collection<DeductionDefinitionObj> deductions = mDefinitionsStore.fetchAllDeductions();
        mListAdapter.clear();
        mListAdapter.addAll(deductions);
    }

    private DeductionDefinition getSelectedDeduction() {
        int checkedItemPos = mListView.getCheckedItemPosition();
        if (checkedItemPos >= 0) {
            return mListAdapter.getItem(checkedItemPos);
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();

        if(getSelectedDeduction() != null) {
            inflater.inflate(R.menu.add_del_edit_finish_menu, menu);
        }
        else {
            inflater.inflate(R.menu.add_menu, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menuAdd:
                addDeduction();
                break;
            case R.id.menuDelete:
                deleteDeductionWithQuestion();
                break;
            case R.id.menuEdit:
                editDeduction();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;

    }

    private void showNoItemSelectedError() {
        new ErrorToast(this).show(R.string.errDeductionNotSelected);
    }

    private void editDeduction() {

        DeductionDefinition deductionToEdit = getSelectedDeduction();
        if (deductionToEdit == null) {
            showNoItemSelectedError();
            return;
        }

        Intent editClass = new Intent(this, EditDeductionActivity.class);
        editClass.putExtra(EditDeductionActivity.EXTRA_DEDUCTION_ID, deductionToEdit.getId());
        startActivityForResult(editClass, REQUEST_CODE_EDIT_DEDUCTION);

    }

    private void addDeduction() {
        startActivityForResult(new Intent(this, NewDeductionActivity.class), REQUEST_CODE_NEW_DEDUCTION);
    }

    private void deleteDeductionWithQuestion() {
        final DeductionDefinition deductionToDelete = getSelectedDeduction();
        if (deductionToDelete != null) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        deleteDeduction(deductionToDelete);
                        mListView.clearChoices();
                        refreshDeductionsList();
                        invalidateOptionsMenu();
                    }
                }
            };

            Resources r = getResources();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(r.getString(R.string.askDeleteDeduction, deductionToDelete.getCode()));
            builder.setPositiveButton(android.R.string.yes, dialogClickListener);
            builder.setNegativeButton(android.R.string.no, dialogClickListener);
            builder.show();

        } else {
            showNoItemSelectedError();
        }
    }

    private void deleteDeduction(DeductionDefinition deduction) {
        mDefinitionsStore.deleteDeduction(deduction.getId());
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        refreshDeductionsList();
    }

}
