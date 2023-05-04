package com.bk.bkskup3.work.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.bk.bkskup3.BkApplication;
import com.bk.bkskup3.R;
import com.bk.bkskup3.dao.BkStore;
import com.bk.bkskup3.dao.PurchasesStore;
import com.bk.bkskup3.feedback.ErrorToast;
import com.bk.bkskup3.model.PurchaseObj;
import com.bk.bkskup3.model.PurchaseState;

import java.util.Calendar;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 6/9/2015
 * Time: 11:49 PM
 */
public class ClosePurchaseActivityFragment extends Fragment {


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void updatePurchaseState(PurchaseObj purchaseObj) {

        Date endDt = Calendar.getInstance().getTime();

        if (endDt.before(purchaseObj.getPurchaseStart())) {
            endDt = purchaseObj.getPurchaseStart();
        }
        purchaseObj.setPurchaseEnd(endDt);
        purchaseObj.setState(PurchaseState.CLOSED);

        int purchaseId = purchaseObj.getId();

        Activity activity = getActivity();
        BkApplication bkApplication = (BkApplication) activity.getApplication();
        BkStore bkStore = bkApplication.getStore();
        PurchasesStore purchasesStore = bkStore.getPurchasesStore();
        purchasesStore.updatePurchaseDetails(purchaseId, purchaseObj.getDetails());
    }

    public void closePurchase(final PurchaseObj purchaseObj, final Runnable onDone) {

        if (purchaseObj.getInvoices().isEmpty()) {
            new ErrorToast(getActivity()).show(R.string.errCantClosePurchaseIsEmpty);
            return;
        }

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    updatePurchaseState(purchaseObj);
                    if(onDone != null)
                        onDone.run();
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.askCloseAndSendPurchase);
        builder.setPositiveButton(android.R.string.yes, dialogClickListener);
        builder.setNegativeButton(android.R.string.no, dialogClickListener);
        builder.show();
    }

    public void onPause() {
        super.onPause();
    }

    public void onStart() {
        super.onStart();
    }

    public void onStop() {
        super.onStop();
    }
}
