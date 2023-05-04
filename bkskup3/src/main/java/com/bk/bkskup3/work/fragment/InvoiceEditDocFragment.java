package com.bk.bkskup3.work.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.style.DynamicDrawableSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bk.bkskup3.BkApplication;
import com.bk.bkskup3.R;
import com.bk.bkskup3.dao.BkStore;
import com.bk.bkskup3.dao.PurchasesStore;
import com.bk.bkskup3.dao.q.QInvoice;
import com.bk.bkskup3.input.BKDatePickerDialog;
import com.bk.bkskup3.model.Herd;
import com.bk.bkskup3.model.IBAN;
import com.bk.bkskup3.model.Invoice;
import com.bk.bkskup3.model.InvoiceHent;
import com.bk.bkskup3.model.InvoiceHentObj;
import com.bk.bkskup3.model.InvoiceObj;
import com.bk.bkskup3.model.PayWay;
import com.bk.bkskup3.settings.InputDefaultsSettings;
import com.bk.bkskup3.tasks.TaskResult;
import com.bk.bkskup3.utils.Dates;
import com.bk.bkskup3.widgets.PayWayButton;
import com.bk.bkskup3.work.fragment.event.InvoiceHentChanged;
import com.bk.widgets.warningedittext.WarningEditText;
import com.mysema.query.support.Query;
import com.squareup.otto.InheritSubscribers;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 3/14/13
 * Time: 10:00 AM
 */

@InheritSubscribers
public class InvoiceEditDocFragment extends InvoiceEditFragment {

    class ExtrasTemplateSpan extends DynamicDrawableSpan {

        public class TextDrawable extends Drawable {

            private final String text;
            private final Paint paint;

            public TextDrawable(String text) {

                this.text = text;

                this.paint = new Paint();
                paint.setColor(Color.WHITE);
                paint.setTextSize(22f);
                paint.setAntiAlias(true);
                paint.setFakeBoldText(true);
                paint.setShadowLayer(6f, 0, 0, Color.BLACK);
                paint.setStyle(Paint.Style.FILL);
                paint.setTextAlign(Paint.Align.LEFT);
            }

            @Override
            public void draw(Canvas canvas) {
                canvas.drawText(text, 0, 0, paint);
            }

            @Override
            public void setAlpha(int alpha) {
                paint.setAlpha(alpha);
            }

            @Override
            public void setColorFilter(ColorFilter cf) {
                paint.setColorFilter(cf);
            }

            @Override
            public int getOpacity() {
                return PixelFormat.TRANSLUCENT;
            }
        }

        private TextDrawable mDrawable;

        public ExtrasTemplateSpan(String templateText) {
            mDrawable = new TextDrawable(templateText);
        }

        @Override
        public Drawable getDrawable() {
            return mDrawable;
        }
    }


    private PayWayButton mPayWayBtn;
    private Button mTakeNextInvoiceNumberBtn;
    private Button mSetTransactionPlaceFromHentBtn;
    private Button mSetTransactionPlaceFromHerdBtn;
    private EditText mExtrasBox;
    private WarningEditText mPayDueDaysBox;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        View v = inflater.inflate(R.layout.invoice_doc, container, false);

        mPayWayBtn = (PayWayButton) v.findViewById(R.id.payWayBtn);
        mTakeNextInvoiceNumberBtn = (Button) v.findViewById(R.id.takeNextInvoiceNumberBtn);
        mPayDueDaysBox = (WarningEditText) v.findViewById(R.id.payDueDaysValueBox);

        Button changeTransactionDtBtn = (Button) v.findViewById(R.id.changeTransactionDtBtn);
        changeTransactionDtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChooseTransactionDt();
            }
        });

        Button setTransactionDtToNowBtn = (Button) v.findViewById(R.id.setTransactionDtToNowBtn);
        setTransactionDtToNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSetTransactionDtToNow();
            }
        });

        mSetTransactionPlaceFromHentBtn = (Button) v.findViewById(R.id.setTransactionPlaceFromHentBtn);
        mSetTransactionPlaceFromHentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCopyTransactionPlaceFromHent();
            }
        });

        mSetTransactionPlaceFromHerdBtn = (Button) v.findViewById(R.id.setTransactionPlaceFromHerdBtn);
        mSetTransactionPlaceFromHerdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCopyTransactionPlaceFromHerd();
            }
        });

        Button changeInvoiceDtBtn = (Button) v.findViewById(R.id.changeInvoiceDtBtn);
        changeInvoiceDtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChooseInvoiceDt();
            }
        });

        Button setInvoiceDtToNowBtn = (Button) v.findViewById(R.id.setInvoiceDtToNowBtn);
        setInvoiceDtToNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSetInvoiceDtToNow();
            }
        });


        Button payDueDays3Btn = (Button) v.findViewById(R.id.payDueDays3Btn);
        payDueDays3Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPayDueDays(3);
            }
        });

        Button payDueDays7Btn = (Button) v.findViewById(R.id.payDueDays7Btn);
        payDueDays7Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPayDueDays(7);
            }
        });

        Button payDueDays14Btn = (Button) v.findViewById(R.id.payDueDays14Btn);
        payDueDays14Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPayDueDays(14);
            }
        });

        Button payDueDays21Btn = (Button) v.findViewById(R.id.payDueDays21Btn);
        payDueDays21Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPayDueDays(21);
            }
        });

        mPayDueDaysBox.setKeyListener(null);

        Button increasePayDueDaysBtn = (Button) v.findViewById(R.id.increasePayDueDaysBtn);
        increasePayDueDaysBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer payDueDays = mService.getPayDueDays();
                if (payDueDays != null) {
                    setPayDueDays(mService.getPayDueDays() + 1);
                } else {
                    setPayDueDays(1);
                }
            }
        });

        Button dereasePayDueDaysBtn = (Button) v.findViewById(R.id.dereasePayDueDaysBtn);
        dereasePayDueDaysBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer payDueDays = mService.getPayDueDays();
                if (payDueDays != null) {
                    setPayDueDays(Math.max(0, payDueDays - 1));
                }
            }
        });

        mPayWayBtn.setOnPayWayListener(new PayWayButton.OnPayWayListener() {
            @Override
            public void onCash() {
                onPayWayCash();
            }

            @Override
            public void onTransfer() {
                onPayWayTransfer();
            }
        });

        Button showLastInvoicesBtn = (Button) v.findViewById(R.id.showLastInvoicesBtn);
        showLastInvoicesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLastInvoicesPopUp();
            }
        });

        Button takeNextInvoiceNumberBtn = getTakeNextInvoiceNumberBtn();
        takeNextInvoiceNumberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTakeNextInvoiceNumber();
            }
        });

        mExtrasBox = (EditText) v.findViewById(R.id.extrasBox);

        hideAccountNoWarning();
        enableTakeNextInvoiceNumberBtn(false);
        return v;
    }

    public void onViewCreated(View view, Bundle state) {
        super.onViewCreated(view, state);
        enablePayDueDaysButtons(false);

        scheduleAfterBoundService(new Runnable() {
            @Override
            public void run() {
                getSetTransactionPlaceFromHentBtn().setEnabled(mService.getHent() != null);
            }
        });
    }

    private void setPayDueDays(int payDueDays) {
        mService.setPayDueDays(payDueDays);
        updatePayDueDays();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        save();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        scheduleAfterBoundService(new Runnable() {
            @Override
            public void run() {
                getSetTransactionPlaceFromHentBtn().setEnabled(mService.getHent() != null);
                enableTakeNextInvoiceNumberBtn(!mService.IsUpdating());

            }
        });
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        scheduleAfterBoundService(new Runnable() {
            @Override
            public void run() {
                updateInputsFromInvoice();

            }
        });
    }

    @Subscribe
    public void onInvoiceHentChanged(InvoiceHentChanged event) {
        scheduleAfterBoundService(new Runnable() {
            @Override
            public void run() {
                decideOnNoAccountNoWarning();
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.invoice_doc_menu, menu);
    }

    public void onStart() {
        super.onStart();
    }

    public void onStop() {
        super.onStop();
    }

    protected void onTakeNextInvoiceNumber() {
        mService.takeNextInvoiceNumber();
        updateCustomNumber();
    }

    private void showAccountNoWarning() {
        mPayDueDaysBox.showWarning(true);
    }

    private void hideAccountNoWarning() {
        mPayDueDaysBox.showWarning(false);
    }


    private void onCopyTransactionPlaceFromHent() {
        InvoiceHentObj invoiceHent = mService.getHent();
        if (invoiceHent != null) {
            getTransactionPlaceBox().setText(invoiceHent.getCity());
        }
    }

    private void onCopyTransactionPlaceFromHerd() {
        Herd invoiceHerd = mDependencies.getHerd();
        if (invoiceHerd != null) {
            getTransactionPlaceBox().setText(invoiceHerd.getCity());
        }
    }


    protected EditText getTransactionDateBox() {
        return (EditText) getView().findViewById(R.id.transactionDateBox);
    }

    protected EditText getInvoiceDateBox() {
        return (EditText) getView().findViewById(R.id.invoiceDateBox);
    }

    private void onSetTransactionDtToNow() {
        mService.setTransactionDt(Calendar.getInstance().getTime());
        updateTransactionDate();
    }

    private void onSetInvoiceDtToNow() {
        mService.setInvoiceDt(Calendar.getInstance().getTime());
        updateInvoiceDate();
    }

    protected EditText getTransactionPlaceBox() {
        return (EditText) getView().findViewById(R.id.transactionPlaceBox);
    }

    private Button getSetTransactionPlaceFromHentBtn() {
        return mSetTransactionPlaceFromHentBtn;
    }

    private void onChooseTransactionDt() {
        Date dt = mService.getTransactionDt();
        if (dt == null) {
            dt = Calendar.getInstance().getTime();
        }

        BKDatePickerDialog transactionDtPickerDlg =
                new BKDatePickerDialog(getActivity(), dt,
                        new BKDatePickerDialog.OnDateListener() {
                            public void onDate(Date dt) {
                                mService.setTransactionDt(dt);
                                updateTransactionDate();
                            }
                        });

        transactionDtPickerDlg.show();
    }

    private void onChooseInvoiceDt() {
        Date dt = mService.getInvoiceDt();
        if (dt == null) {
            dt = Calendar.getInstance().getTime();
        }

        BKDatePickerDialog invoiceDtPickerDlg =
                new BKDatePickerDialog(getActivity(), dt,
                        new BKDatePickerDialog.OnDateListener() {
                            public void onDate(Date dt) {
                                mService.setInvoiceDt(dt);
                                updateInvoiceDate();
                            }
                        });

        invoiceDtPickerDlg.show();
    }

    protected void updateTransactionDate() {

        Date transactionDate = mService.getTransactionDt();
        String transactionDtStr = "";
        if (transactionDate != null) {
            transactionDtStr = Dates.toDayDate(transactionDate);
        }
        getTransactionDateBox().setText(transactionDtStr);
    }

    protected void updateInvoiceDate() {

        Date invoiceDate = mService.getInvoiceDt();
        String invoiceDtStr = "";
        if (invoiceDate != null) {
            invoiceDtStr = Dates.toDayDate(invoiceDate);
        }
        getInvoiceDateBox().setText(invoiceDtStr);
    }


    protected EditText getCustomInvoiceNoBox() {
        return (EditText) getView().findViewById(R.id.customInvoiceNoValueBox);
    }

    protected EditText getExtrasBox() {
        return mExtrasBox;
    }


    private void decideOnNoAccountNoWarning() {
        InvoiceHent invoiceHent = mService.getHent();
        if (invoiceHent != null) {
            IBAN accountNo = invoiceHent.getBankAccountNo();
            if (accountNo == null) {
                if (PayWay.TRANSFER == getPayWay()) {
                    showAccountNoWarning();
                    return;
                }
            }
        }
        hideAccountNoWarning();
    }


    protected PayWayButton getPayWayBtn() {
        return mPayWayBtn;
    }

    protected PayWay getPayWay() {
        return getPayWayBtn().getPayWay();
    }

    protected void updatePayWay() {
        PayWay payWay = mService.getPayWay();
        getPayWayBtn().setPayWay(payWay);
        if (payWay == PayWay.CASH) {
            onPayWayCash();
        }
    }

    private void onPayWayTransfer() {
        mService.setPayWay(PayWay.TRANSFER);

        if (mService.getPayDueDays() == null && mService.getInvoiceType() != null) {
            InputDefaultsSettings inputDefaults = mDependencies.getInputDefaultsSettings();

            switch (mService.getInvoiceType()) {
                case LUMP:
                    mService.setPayDueDays(inputDefaults.getDefaultPayDueDaysForIndividual());
                    break;
                case REGULAR:
                    mService.setPayDueDays(inputDefaults.getDefaultPayDueDaysForCompany());
                    break;
            }
        }

        updatePayDueDays();
        enablePayDueDaysButtons(true);
    }


    protected void onPayWayCash() {
        mService.setPayWay(PayWay.CASH);
        enablePayDueDaysButtons(false);
        hideAccountNoWarning();
        updatePayDueDays();
    }

    private void enablePayDueDaysButtons(boolean enable) {

        final int paydueDaysButtonsIds[] = {
                R.id.payDueDays3Btn,
                R.id.payDueDays7Btn,
                R.id.payDueDays14Btn,
                R.id.payDueDays21Btn,
                R.id.increasePayDueDaysBtn,
                R.id.dereasePayDueDaysBtn
        };

        for (int payDueDaysBtnId : paydueDaysButtonsIds) {
            View viewById = getView().findViewById(payDueDaysBtnId);

            viewById.setEnabled(enable);
        }
    }


    protected void updatePayDueDays() {

        if (mService.getPayWay() == PayWay.TRANSFER) {
            String payDueDaysStr = "";
            Integer payDueDays = mService.getPayDueDays();
            if (payDueDays != null)
                payDueDaysStr = String.valueOf(payDueDays);

            mPayDueDaysBox.setText(payDueDaysStr);
            decideOnNoAccountNoWarning();
        } else {
            mPayDueDaysBox.setText("-");
            hideAccountNoWarning();
        }
    }

    protected void updateTransactionPlace() {
        getTransactionPlaceBox().setText(mService.getTransactionPlace());
    }

    protected String getTransactionPlace() {
        return getTransactionPlaceBox().getText().toString();
    }

    protected void updateCustomNumber() {
        getCustomInvoiceNoBox().setText(mService.getCustomNumber());
    }

    protected String getCustomInvoiceNo() {
        return getCustomInvoiceNoBox().getText().toString();
    }

    protected String getExtras() {
        return getExtrasBox().getText().toString();
    }

    protected void updateExtras() {
        getExtrasBox().setText(mService.getExtras());
    }


    private Button getTakeNextInvoiceNumberBtn() {
        return mTakeNextInvoiceNumberBtn;
    }

    protected void enableTakeNextInvoiceNumberBtn(boolean enable) {
        getTakeNextInvoiceNumberBtn().setEnabled(enable);
    }

    private List<InvoiceObj> findLastInvoices(int howMany) {
        Activity activity = getActivity();
        BkApplication bkApplication = (BkApplication) activity.getApplication();
        BkStore bkStore = bkApplication.getStore();
        PurchasesStore purchasesStore = bkStore.getPurchasesStore();
        return purchasesStore.fetchInvoices(new Query().limit(howMany).orderBy(QInvoice.id.desc()));
    }

    private void showLastInvoicesPopUp() {

        View popupContentView;
        List<InvoiceObj> lastInvoices = findLastInvoices(5);
        final Activity context = getActivity();
        if (lastInvoices.size() > 0) {
            final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ArrayAdapter<Invoice> lastInvoicesListAdapter = new ArrayAdapter<Invoice>(context, 0) {
                public View getView(int position, View convertView, ViewGroup parent) {
                    View row;


                    if (null == convertView) {
                        row = inflater.inflate(R.layout.last_invoices_list_item, null);
                    } else {
                        row = convertView;
                    }
                    Invoice invoice = getItem(position);
                    TextView lastInvoiceNoBox = (TextView) row.findViewById(R.id.lastInvoiceNoBox);
                    lastInvoiceNoBox.setText(invoice.getCustomNumber());
                    TextView lastInvoiceDtBox = (TextView) row.findViewById(R.id.lastInvoiceDtBox);
                    lastInvoiceDtBox.setText(Dates.toDayDate(invoice.getInvoiceDt()));
                    return row;
                }
            };
            lastInvoicesListAdapter.addAll(lastInvoices);

            popupContentView = inflater.inflate(R.layout.last_invoices_popup, null);
            ListView lastInvoicesList = (ListView) popupContentView.findViewById(R.id.lastInvoicesList);
            lastInvoicesList.setAdapter(lastInvoicesListAdapter);
        } else {
            TextView noLastInvoicesView = new TextView(context);
            noLastInvoicesView.setText(getString(R.string.noLastInvoicesToShow));
            noLastInvoicesView.setTextSize(20);
            popupContentView = noLastInvoicesView;
        }


        EditText invoiceNoBox = getCustomInvoiceNoBox();
        PopupWindow lastInvoicesPopup = new PopupWindow(context);
        lastInvoicesPopup.setFocusable(true);
        lastInvoicesPopup.setContentView(popupContentView);

        lastInvoicesPopup.showAtLocation(invoiceNoBox, Gravity.CENTER, 0, 0);

    }

    @Subscribe
    public void onInvoiceHentChange(InvoiceHentChanged event) {
        scheduleAfterBoundService(new Runnable() {
            @Override
            public void run() {
                getSetTransactionPlaceFromHentBtn().setEnabled(mService.getHent() != null);
                decideOnNoAccountNoWarning();
            }
        });
    }


    public void updateInputsFromInvoice() {
        updatePayWay();
        updatePayDueDays();
        updateTransactionPlace();
        updateTransactionDate();
        updateInvoiceDate();
        updateCustomNumber();
        updateExtras();
    }

    private void updateInvoiceFromInputs() {
        mService.setPayWay(mPayWayBtn.getPayWay());
        mService.setTransactionPlace(getTransactionPlace());
        mService.setCustomNumber(getCustomInvoiceNo());
        mService.setExtras(getExtras());
    }

    public void save() {
        if (getView() != null) {
            updateInvoiceFromInputs();
        }
    }

    static class LoadLastInvoices extends AsyncTask<Void, Void, TaskResult<Collection<InvoiceObj>>> {
        public interface Observer {
            void onLoadStarted();

            void onLoadSuccessful(InvoiceObj result);

            void onLoadError(Exception e);
        }

        private PurchasesStore mPurchasesStore;
        private int mHowMany;

        public LoadLastInvoices(PurchasesStore purchasesStore, int howMany) {
            this.mPurchasesStore = purchasesStore;
            this.mHowMany = howMany;
        }

        @Override
        protected TaskResult<Collection<InvoiceObj>> doInBackground(Void... params) {

            if (mHowMany <= 0) {
                return TaskResult.<Collection<InvoiceObj>>withResult(new ArrayList<InvoiceObj>());
            }


            Collection<InvoiceObj> invoiceObjs = mPurchasesStore.fetchInvoices(new Query().limit(mHowMany).orderBy(QInvoice.id.asc()));

            return TaskResult.withResult(invoiceObjs);
        }
    }

    class LastInvoicesPopup extends PopupWindow {

    }
}
