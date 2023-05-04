package com.bk.bkskup3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.bk.bkskup3.dao.PurchasesStore;
import com.bk.bkskup3.dao.SettingsStore;
import com.bk.bkskup3.feedback.ErrorMessageFragment;
import com.bk.bkskup3.management.ManagementActivity;
import com.bk.bkskup3.model.Agent;
import com.bk.bkskup3.model.PurchaseObj;
import com.bk.bkskup3.model.PurchaseState;
import com.bk.bkskup3.preferences.AgentPreferencesActivity;
import com.bk.bkskup3.utils.Dates;
import com.bk.bkskup3.work.*;

import javax.inject.Inject;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 1/20/12
 * Time: 11:36 AM
 */
public class WelcomeActivity extends BkActivity {

    private static final int REQUESTCODE_OPENPURCHASE = 101;
    private static final String ERROR_MSG_FRAMGENT_TAG = "error_fragment";

    protected ErrorMessageFragment mErrMsgFragment;
    private State mState;
    private Agent mAgent;
    private ErrorMessageFragment.ErrorFragmentListener mOnErrorHideListener = new ErrorMessageFragment.ForwardErrorFragmentListener() {
        @Override
        public void onAbandon() {
            clearState();
            mErrMsgFragment = null;
        }
    };

    enum State {
        Idle,
        Splash,
        ShowingError
    }

    @Inject
    SettingsStore mSettingsStore;

    @Inject
    PurchasesStore mPurchasesStore;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);


        Button syncBtn = (Button) findViewById(R.id.newPurchaseBtn);
        syncBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNewPurchase();
            }
        });

        Button openPurchasesBtn = (Button) findViewById(R.id.openPurchasesBtn);
        openPurchasesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShowOpenPurchases();
            }
        });

        Button purchaseHistoryBtn = (Button) findViewById(R.id.purchaseHistoryBtn);
        purchaseHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPurchaseHistory();
            }
        });

        Button settingsBtn = (Button) findViewById(R.id.settingsBtn);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSettings();
            }
        });

        setState(State.Idle);
        mAgent = mSettingsStore.getAgent();




    }

    private void onSettings() {
        startActivity(new Intent(this, AgentPreferencesActivity.class));
    }

    private void onPurchaseHistory() {
        startActivity(new Intent(this, PurchasesHistoryActivity.class));
    }

    private void onShowOpenPurchases() {
        startActivity(new Intent(this, OpenPurchasesActivity.class));
    }

    private void onManagement() {
        startActivity(new Intent(this, ManagementActivity.class));
    }

    private void onNewPurchase() {
        startActivityForResult(new Intent(this, OpenNewPurchaseActivity.class), REQUESTCODE_OPENPURCHASE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUESTCODE_OPENPURCHASE) {
                startNewPurchaseActivity(data.getIntExtra(OpenNewPurchaseActivity.EXTRAS_HERDNO, 0));
            }
        }

    }

    private void startNewPurchaseActivity(int herdNo) {


        PurchaseObj newPurchase = new PurchaseObj(0);
        newPurchase.setState(PurchaseState.OPEN);
        newPurchase.setHerdNo(herdNo);
        newPurchase.setPurchaseStart(Dates.now());
        newPurchase.setPlateNo(mAgent.getPlateNo());
        PurchaseObj purchaseObj = mPurchasesStore.insertPurchase(newPurchase);

        Intent intent = new Intent(this, PurchaseEditActivity.class);
        intent.putExtra(PurchaseEditActivity.EXTRA_PURCHASE_ID, purchaseObj.getId());
        startActivity(intent);


    }

    public void setState(State state) {
        this.mState = state;
    }

    public State getState() {
        return mState;
    }

    private void clearState() {
        mState = State.Idle;
    }

    private void showError(String error) {
        setState(State.ShowingError);
        mErrMsgFragment = new ErrorMessageFragment();
        mErrMsgFragment.setCaption(getResources().getString(R.string.errorCaption));
        mErrMsgFragment.setErrorMsg(error);
        mErrMsgFragment.setListener(mOnErrorHideListener);
        mErrMsgFragment.show(getFragmentManager(), ERROR_MSG_FRAMGENT_TAG);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mState == State.ShowingError) {
            mErrMsgFragment = (ErrorMessageFragment) getFragmentManager().findFragmentByTag(ERROR_MSG_FRAMGENT_TAG);
            mErrMsgFragment.setListener(mOnErrorHideListener);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.management_menu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuGoManagement:
                onManagement();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
