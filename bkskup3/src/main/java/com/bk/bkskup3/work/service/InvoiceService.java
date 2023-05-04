package com.bk.bkskup3.work.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;

import com.bk.bkskup3.BkApplication;
import com.bk.bkskup3.dao.BkStore;
import com.bk.bkskup3.dao.PurchasesStore;
import com.bk.bkskup3.dao.SettingsStore;
import com.bk.bkskup3.dao.q.QCow;
import com.bk.bkskup3.dao.q.QInvoice;
import com.bk.bkskup3.invoice.InvoiceNoFormatter;
import com.bk.bkskup3.invoice.InvoiceNoFormatterBuilder;
import com.bk.bkskup3.invoice.InvoiceNoGenerator;
import com.bk.bkskup3.invoice.InvoiceNoTransaction;
import com.bk.bkskup3.model.Cow;
import com.bk.bkskup3.model.CowObj;
import com.bk.bkskup3.model.DeductionDefinition;
import com.bk.bkskup3.model.EAN;
import com.bk.bkskup3.model.HentObj;
import com.bk.bkskup3.model.InvoiceDeduction;
import com.bk.bkskup3.model.InvoiceDeductionObj;
import com.bk.bkskup3.model.InvoiceDetails;
import com.bk.bkskup3.model.InvoiceHent;
import com.bk.bkskup3.model.InvoiceHentObj;
import com.bk.bkskup3.model.InvoiceObj;
import com.bk.bkskup3.model.InvoiceType;
import com.bk.bkskup3.model.PayWay;
import com.bk.bkskup3.settings.InvoiceSettings;
import com.bk.bkskup3.utils.NullUtils;
import com.bk.bkskup3.work.input.CowInput;
import com.bk.bkskup3.work.input.DeductionInput;
import com.bk.bkskup3.work.input.InvoiceInput;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import static com.mysema.query.support.QueryBuilder.where;

public class InvoiceService extends Service {
    private static final String STORAGE_FILENAME = "invoice.storage";
    private final IBinder mBinder = new LocalBinder();
    private InvoiceInput mInvoiceInput;
    private SettingsStore mSettingsStore;
    private PurchasesStore mPurchasesStore;
    private InvoiceNoGenerator mGenerator;
    private InvoiceNoTransaction mInvoiceNoTransaction;
    private InvoiceSettings mSettings;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (InvoiceSettings.ACTION_SETTINGS_CHANGED.equals(action)) {
                mSettings = mSettingsStore.loadSettings(InvoiceSettings.class);

            }
        }
    };


    public boolean IsUpdating() {
        return mInvoiceInput.getInvoiceId() != 0;
    }

    public InvoiceType getInvoiceType() {
        return mInvoiceInput.getInvoiceType();
    }

    public PayWay getPayWay() {
        return mInvoiceInput.getPayWay();
    }

    public Integer getPayDueDays() {
        return mInvoiceInput.getPayDueDays();
    }

    public Double getVatRate() {
        return mInvoiceInput.getVatRate();
    }

    public int getCowCount() {
        return mInvoiceInput.getCowCount();
    }

    public Collection<CowInput> getCows() {
        return mInvoiceInput.getCows();
    }

    public String getCustomNumber() {
        return mInvoiceInput.getCustomNumber();
    }

    public void setPayDueDays(Integer payDueDays) {
        mInvoiceInput.setPayDueDays(payDueDays);
    }

    public void setPayWay(PayWay payWay) {
        mInvoiceInput.setPayWay(payWay);
    }

    public Collection<DeductionInput> getDeductions() {
        return mInvoiceInput.getDeductions();
    }

    public InvoiceHentObj getHent() {
        return mInvoiceInput.getHent();
    }

    public void save() {
        if (mInvoiceInput.getInvoiceId() == 0) {
            InvoiceObj invoiceObj = mPurchasesStore.insertInvoice(getInvoice());
            mInvoiceInput.setInvoiceId(invoiceObj.getId());

            if(mInvoiceNoTransaction != null) {
                mGenerator.commitInvoiceNo(mInvoiceNoTransaction.getTransactionId());
            }
        } else {
            mPurchasesStore.updateInvoice(getInvoice());
        }
    }

    public void setTransactionPlace(String transactionPlace) {
        mInvoiceInput.setTransactionPlace(transactionPlace);
    }

    public void setCustomNumber(String customInvoiceNo) {
        mInvoiceInput.setCustomNumber(customInvoiceNo);
    }

    public void setExtras(String extras) {
        mInvoiceInput.setExtras(extras);
    }

    public String getExtras() {
        return mInvoiceInput.getExtras();
    }

    public String getTransactionPlace() {
        return mInvoiceInput.getTransactionPlace();
    }

    public void setTransactionDt(Date transactionDt) {
        mInvoiceInput.setTransactionDt(transactionDt);
    }

    public Date getTransactionDt() {
        return mInvoiceInput.getTransactionDt();
    }

    public void setInvoiceDt(Date invoiceDt) {
        mInvoiceInput.setInvoiceDt(invoiceDt);
    }

    public Date getInvoiceDt() {
        return mInvoiceInput.getInvoiceDt();
    }


    public void deleteDeduction(UUID deductionId) {
        mInvoiceInput.deleteDeduction(deductionId);
    }

    public DeductionInput getDeduction(UUID id) {
        return mInvoiceInput.getDeduction(id);
    }

    public void setHent(InvoiceHentObj invoiceHent) {
        mInvoiceInput.setHent(invoiceHent);
    }

    public void setInvoiceType(InvoiceType invoiceType) {
        mInvoiceInput.setInvoiceType(invoiceType);
    }

    public void setVatRate(Double vatRate) {
        mInvoiceInput.setVatRate(vatRate);
    }

    public CowInput getCow(UUID id) {
        CowInput result = null;
        CowInput cow = mInvoiceInput.getCow(id);
        if (cow != null) {
            result = new CowInput(cow.getInputId());
            result.copyFrom(cow);
        }
        return result;
    }

    public void updateCow(CowInput cow) throws DuplicateCowInInvoiceException, DuplicateCowInPurchaseException {
        CowInput cowToUpdate = mInvoiceInput.getCow(cow.getInputId());
        if (cowToUpdate == null) {
            throw new IllegalArgumentException("updating not existing cow");
        }

        final EAN cowNo = cowToUpdate.getCowNo();
        if (!cowNo.equals(cow.getCowNo())) {
            CowInput cowWithDuplicatedNo = Iterables.find(mInvoiceInput.getCows(), new Predicate<CowInput>() {
                @Override
                public boolean apply(@Nullable CowInput cowInput) {
                    return cowInput.getCowNo().equals(cowNo);
                }
            }, null);

            if (cowWithDuplicatedNo != null) {
                throw new DuplicateCowInInvoiceException();
            }

            if (isDuplicatedInPurchase(cow)) {
                throw new DuplicateCowInPurchaseException();
            }
        }
        cowToUpdate.copyFrom(cow);

    }

    public void deleteCow(UUID id) {
        mInvoiceInput.deleteCow(id);
    }

    private boolean isDuplicatedInPurchase(CowInput cowInput) {
        final EAN cowNo = cowInput.getCowNo();
        Collection<CowObj> duplicatedCows = mPurchasesStore.fetchCows(where(QCow.cowNo.eq(cowNo.toString())));
        Collection<Integer> invoiceIds = Collections2.transform(duplicatedCows, new Function<CowObj, Integer>() {
            @Nullable
            @Override
            public Integer apply(@Nullable CowObj cowObj) {
                return cowObj.getInvoice();
            }
        });

        if (!Iterables.isEmpty(invoiceIds)) {
            Set<Integer> uniqueInvoiceId = new HashSet<Integer>(invoiceIds);
            uniqueInvoiceId.remove(mInvoiceInput.getInvoiceId());
            Collection<InvoiceDetails> invoices = mPurchasesStore.fetchInvoiceDetails(where(QInvoice.id.in(uniqueInvoiceId)));

            InvoiceDetails invoiceWithSameCow = Iterables.find(invoices, new Predicate<InvoiceDetails>() {
                @Override
                public boolean apply(@Nullable InvoiceDetails invoiceDetails) {
                    return invoiceDetails.getPurchaseId() == mInvoiceInput.getPurchaseId();
                }
            }, null);

            if (invoiceWithSameCow != null) {
                return true;
            }
        }

        return false;

    }

    public UUID addCow(CowInput cowInput) throws DuplicateCowInInvoiceException, DuplicateCowInPurchaseException {
        final EAN cowNo = cowInput.getCowNo();
        CowInput duplicatedCow = Iterables.find(mInvoiceInput.getCows(), new Predicate<CowInput>() {
            @Override
            public boolean apply(@Nullable CowInput cowInput) {
                return cowInput.getCowNo().equals(cowNo);
            }
        }, null);

        if (duplicatedCow != null) {
            throw new DuplicateCowInInvoiceException();
        }

        if (isDuplicatedInPurchase(cowInput)) {
            throw new DuplicateCowInPurchaseException();
        }


        return mInvoiceInput.addCow(cowInput);
    }

    public void takeNextInvoiceNumber() {
        if (mInvoiceNoTransaction == null) {
            mInvoiceNoTransaction = mGenerator.acquireNextInvoiceNo();

            InvoiceNoFormatterBuilder builder = new InvoiceNoFormatterBuilder(getResources(),mSettings.getInvoiceNoFmt());
            InvoiceNoFormatter formatter = builder.build();
            mInvoiceInput.setCustomNumber(formatter.format(mInvoiceNoTransaction.getInvoiceNo()));
        }
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public InvoiceService getService() {
            // Return this instance of LocalService so clients can call public methods
            return InvoiceService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void onCreate() {
        Context context = getApplicationContext();

        try {

            FileInputStream storage = context.openFileInput(STORAGE_FILENAME);
            ObjectInputStream is = new ObjectInputStream(storage);
            mInvoiceInput = (InvoiceInput) is.readObject();

        } catch (FileNotFoundException e) {
            //ignore
        } catch (Exception e) {
            context.deleteFile(STORAGE_FILENAME);
        }

        BkApplication bkApplication = (BkApplication) getApplication();
        BkStore bkStore = bkApplication.getStore();
        mPurchasesStore = bkStore.getPurchasesStore();
        mSettingsStore = bkStore.getSettingsStore();

        mGenerator = new InvoiceNoGenerator(this);
        mSettings = mSettingsStore.loadSettings(InvoiceSettings.class);

        IntentFilter filter = new IntentFilter(InvoiceSettings.ACTION_SETTINGS_CHANGED);
        this.registerReceiver(mReceiver, filter);


    }

    public void onDestroy() {
        if (mInvoiceInput != null) {
            Context context = getApplicationContext();
            FileOutputStream storage = null;
            try {
                storage = context.openFileOutput(STORAGE_FILENAME, Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(storage);
                os.writeObject(mInvoiceInput);
            } catch (FileNotFoundException e) {
                //ignore
            } catch (IOException e) {
                context.deleteFile(STORAGE_FILENAME);
            }
        }

        this.unregisterReceiver(mReceiver);

    }

    public void newInvoice(int purchaseId) {
        mInvoiceInput = new InvoiceInput();
        mInvoiceInput.setPurchaseId(purchaseId);
        mInvoiceInput.setInvoiceDt(Calendar.getInstance().getTime());
    }

    public void editInvoice(int invoiceId) {
        InvoiceObj invoice = mPurchasesStore.fetchInvoice(invoiceId);
        mInvoiceInput = new InvoiceInput();
        mInvoiceInput.setInvoiceId(invoiceId);
        mInvoiceInput.setPurchaseId(invoice.getPurchase());
        mInvoiceInput.setCustomNumber(invoice.getCustomNumber());
        mInvoiceInput.setExtras(invoice.getExtras());
        mInvoiceInput.setInvoiceDt(invoice.getInvoiceDt());
        mInvoiceInput.setInvoiceType(invoice.getInvoiceType());
        mInvoiceInput.setPayDueDays(invoice.getPayDueDays());
        mInvoiceInput.setPayWay(invoice.getPayWay());
        mInvoiceInput.setTransactionDt(invoice.getTransactionDt());
        mInvoiceInput.setTransactionPlace(invoice.getTransactionPlace());
        mInvoiceInput.setVatRate(invoice.getVatRate());
        InvoiceHent hent = invoice.getInvoiceHent();
        if (hent != null) {
            mInvoiceInput.setHent(new InvoiceHentObj(hent));
        }

        for (InvoiceDeduction deduction : invoice.getDeductions()) {
            DeductionInput deductionInput = new DeductionInput();
            deductionInput.setEnabled(deduction.isEnabled());
            deductionInput.setFraction(deduction.getFraction());
            deductionInput.setReason(deduction.getReason());
            deductionInput.setCode(deduction.getCode());
            mInvoiceInput.addDeduction(deductionInput);
        }

        for (Cow cow : invoice.getCows()) {
            CowInput cowInput = new CowInput();
            cowInput.setCowId(cow.getId());
            cowInput.setCowNo(cow.getCowNo());
            cowInput.setMotherNo(cow.getMotherNo());
            cowInput.setPassportIssueDt(cow.getPassportIssueDt());

            EAN firstOwnerNo = cow.getFirstOwner();
            if (firstOwnerNo != null) {
                HentObj firstOwner = new HentObj();
                firstOwner.setHentNo(firstOwnerNo);
                cowInput.setFirstOwner(firstOwner);
            }
            cowInput.setBirthPlace(cow.getBirthPlace());
            cowInput.setClassCd(cow.getClassCd());
            cowInput.setWeight(cow.getWeight());
            cowInput.setStockCd(cow.getStockCd());
            cowInput.setBirthDt(cow.getBirthDt());
            cowInput.setHealthCertNo(cow.getHealthCertNo());
            cowInput.setPassportNo(cow.getPassportNo());
            cowInput.setNetPrice(cow.getPrice());
            cowInput.setSex(cow.getSex());
            mInvoiceInput.addCow(cowInput);
        }
    }

    public void addDeduction(DeductionDefinition definition) {
        DeductionInput deduction = new DeductionInput();
        deduction.setEnabled(definition.isEnabledByDefault());
        deduction.setCode(definition.getCode());
        deduction.setFraction(definition.getFraction());
        deduction.setReason(definition.getReason());
        mInvoiceInput.addDeduction(deduction);
    }

    public UUID addDeduction(DeductionInput deductionInput) {
        return mInvoiceInput.addDeduction(deductionInput);
    }

    public InvoiceObj getInvoice() {
        InvoiceObj invoice = new InvoiceObj(mInvoiceInput.getInvoiceId());
        invoice.setExtras(mInvoiceInput.getExtras());
        invoice.setVatRate(NullUtils.valueForNull(mInvoiceInput.getVatRate(), 0.0));
        invoice.setPayWay(mInvoiceInput.getPayWay());
        invoice.setCustomNumber(mInvoiceInput.getCustomNumber());
        invoice.setPayDueDays(NullUtils.valueForNull(mInvoiceInput.getPayDueDays(), 0));
        invoice.setHent(mInvoiceInput.getHent());
        invoice.setTransactionPlace(mInvoiceInput.getTransactionPlace());
        invoice.setTransactionDt(mInvoiceInput.getTransactionDt());
        invoice.setInvoiceDt(mInvoiceInput.getInvoiceDt());
        invoice.setInvoiceType(mInvoiceInput.getInvoiceType());
        invoice.setPurchase(mInvoiceInput.getPurchaseId());

        Collection<CowInput> cows = mInvoiceInput.getCows();

        for (CowInput cowInput : cows) {
            CowObj cow = new CowObj(cowInput.getCowId());
            cow.setInvoice(invoice.getId());
            cow.setCowNo(cowInput.getCowNo());
            cow.setMotherNo(cowInput.getMotherNo());
            cow.setStockCd(cowInput.getStockCd());
            cow.setClassCd(cowInput.getClassCd());
            cow.setSex(cowInput.getSex());
            cow.setBirthDt(cowInput.getBirthDt());
            cow.setBirthPlace(cowInput.getBirthPlace());

            HentObj firstOwner = cowInput.getFirstOwner();
            if (firstOwner != null)
                cow.setFirstOwner(firstOwner.getHentNo());

            cow.setHealthCertNo(cowInput.getHealthCertNo());
            cow.setPassportIssueDt(cowInput.getPassportIssueDt());
            cow.setPassportNo(cowInput.getPassportNo());
            cow.setPrice(cowInput.getNetPrice());
            cow.setWeight(cowInput.getWeight());
            invoice.addCow(cow);
        }

        Collection<DeductionInput> deductions = mInvoiceInput.getDeductions();

        for (DeductionInput deductionInput : deductions) {
            InvoiceDeductionObj deduction = new InvoiceDeductionObj();
            deduction.setCode(deductionInput.getCode());
            deduction.setReason(deductionInput.getReason());
            deduction.setFraction(deductionInput.getFraction());
            deduction.setEnabled(deductionInput.isEnabled());
            invoice.addDeduction(deduction);
        }

        return invoice;
    }


}
