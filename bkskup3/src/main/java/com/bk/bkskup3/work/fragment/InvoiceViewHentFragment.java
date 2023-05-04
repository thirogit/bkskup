package com.bk.bkskup3.work.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.bk.bkskup3.R;
import com.bk.bkskup3.model.EAN;
import com.bk.bkskup3.model.IBAN;
import com.bk.bkskup3.model.InvoiceHentObj;
import com.bk.bkskup3.utils.Dates;
import com.bk.bkskup3.widgets.AccountNoEditText;

import java.util.Date;

public class InvoiceViewHentFragment extends InvoiceViewFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.invoice_hent, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        InvoiceHentObj invoiceHent = mInvoice.getHent();
        setBoxes(invoiceHent);
    }

    private View findViewById(int viewId) {
        return getView().findViewById(viewId);
    }

    protected EditText getNameBox() {
        return (EditText) findViewById(R.id.hentNameEditBox);
    }

    public void setHentName(String hentName) {
        getNameBox().setText(hentName);
    }

    protected EditText getStreetBox() {
        return (EditText) findViewById(R.id.hentStreetEditBox);
    }

    public void setStreet(String street) {
        getStreetBox().setText(street);
    }

    protected EditText getPOBoxBox() {
        return (EditText) findViewById(R.id.hentPOBoxEditBox);
    }

    public void setPoBox(String poBox) {
        getPOBoxBox().setText(poBox);
    }

    protected EditText getCityBox() {
        return (EditText) findViewById(R.id.hentCityEditBox);
    }

    public void setCity(String city) {
        getCityBox().setText(city);
    }

    public void setZipCode(String zipCode) {
        getZipCodeBox().setText(zipCode);
    }

    protected EditText getZipCodeBox() {
        return (EditText) findViewById(R.id.hentZipEditBox);
    }


    public void setHentFarmNo(EAN hentFarmNo) {
        final EditText farmNoBox = getFarmNoBox();
        if (hentFarmNo == null) {
            farmNoBox.getText().clear();
        } else {
            farmNoBox.setText(hentFarmNo.toString());
        }
    }

    protected EditText getFarmNoBox() {
        return (EditText) findViewById(R.id.hentHentNoEditBox);
    }

    protected EditText getPhoneNoBox() {
        return (EditText) findViewById(R.id.hentPhoneNoEditbox);
    }

    protected void setPhoneNo(String phoneNo) {
        getPhoneNoBox().setText(phoneNo);
    }

    protected EditText getCellPhoneNoBox() {
        return (EditText) findViewById(R.id.hentCellPhoneNoEditbox);
    }

    protected void setCellPhoneNo(String cellPhoneNo) {
        getCellPhoneNoBox().setText(cellPhoneNo);
    }

    protected EditText getEmailAddrBox() {
        return (EditText) findViewById(R.id.hentEmailAddrEditbox);
    }

    protected void setEmail(String email) {
        getEmailAddrBox().setText(email);
    }

    protected EditText getFiscalNoBox() {
        return (EditText) findViewById(R.id.hentFiscalNoBox);
    }

    protected void setFiscalNo(String fiscalNo) {
        getFiscalNoBox().setText(fiscalNo);
    }

    protected EditText getWetIdNoBox() {
        return (EditText) findViewById(R.id.hentWetNoEditbox);
    }

    protected void setWetNo(String wetNo) {
        getWetIdNoBox().setText(wetNo);
    }

    protected EditText getPersonalNoBox() {
        return (EditText) findViewById(R.id.hentPersonalNoBox);
    }

    protected void setPersonalNo(String personalNo) {
        getPersonalNoBox().setText(personalNo);
    }

    protected EditText getStatsNoBox() {
        return (EditText) findViewById(R.id.hentREGON);
    }

    protected void setStatsNo(String statsNo) {
        getStatsNoBox().setText(statsNo);
    }

    protected EditText getPersonalIdNoBox() {
        return (EditText) findViewById(R.id.hentIdNoEditbox);
    }

    protected void setPersonalIdNo(String personalIdNo) {
        getPersonalIdNoBox().setText(personalIdNo);
    }

    protected EditText getIssuePostBox() {
        return (EditText) findViewById(R.id.hentIdIssuePostEditbox);
    }

    protected void setIssuePost(String issuePost) {
        getIssuePostBox().setText(issuePost);
    }

    protected EditText getBankNameBox() {
        return (EditText) findViewById(R.id.hentBankName);
    }

    protected void setBankName(String bankName) {
        getBankNameBox().setText(bankName);
    }

    protected AccountNoEditText getAccountNoBox() {
        return (AccountNoEditText) findViewById(R.id.hentAccountNo);
    }

    protected void setAccountNo(IBAN iban) {
        getAccountNoBox().setIBAN(iban);
    }

    protected EditText getWetLicenceNoBox() {
        return (EditText) findViewById(R.id.hentWetLicenceNo);
    }

    protected void setWetLicenceNo(String wetLicenceNo) {
        getWetLicenceNoBox().setText(wetLicenceNo);
    }

    protected EditText getIssueDateBox() {
        return (EditText) findViewById(R.id.hentIdIssueDateEditbox);
    }

    protected void setIssueDate(Date issueDate) {
        EditText hentIdIssueDateBox = getIssueDateBox();
        if (issueDate != null) {
            hentIdIssueDateBox.setText(Dates.toDayDate(issueDate));
        } else {
            hentIdIssueDateBox.getText().clear();
        }
    }

    protected void setBoxes(InvoiceHentObj hent) {

        setHentName(hent.getHentName());
        setStreet(hent.getStreet());
        setPoBox(hent.getPoBox());
        setCity(hent.getCity());
        setZipCode(hent.getZip());
        setHentFarmNo(hent.getHentNo());
        setPhoneNo(hent.getPhoneNo());
        setCellPhoneNo(hent.getCellPhoneNo());
        setEmail(hent.getEmail());
        setAccountNo(hent.getBankAccountNo());
        setBankName(hent.getBankName());
        setFiscalNo(hent.getFiscalNo());
        setStatsNo(hent.getREGON());
        setWetNo(hent.getWetNo());
        setWetLicenceNo(hent.getWetLicenceNo());
        setPersonalIdNo(hent.getPersonalIdNo());
        setIssueDate(hent.getIssueDate());
        setIssuePost(hent.getIssuePost());
        setPersonalNo(hent.getPersonalNo());
    }


}
