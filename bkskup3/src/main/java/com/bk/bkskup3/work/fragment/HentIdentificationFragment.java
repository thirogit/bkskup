package com.bk.bkskup3.work.fragment;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.bk.bkskup3.R;
import com.bk.bkskup3.utils.Dates;
import com.bk.bkskup3.work.BusActivity;
import com.bk.bkskup3.work.fragment.event.DatePickedEvent;
import com.squareup.otto.Subscribe;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/22/2014
 * Time: 11:40 AM
 */
public class HentIdentificationFragment extends HentFragment {

//    private static final String STATE_EXTRA_PERSONALIDISSUEDT = "personal_id_issue_dt";
//    private static final String STATE_EXTRA_WETLICENCENO = "wet_licence_no";
//    private static final String STATE_EXTRA_WETNO = "wet_no";
//    private static final String STATE_EXTRA_FISCALNO = "fiscal_no";
//    private static final String STATE_EXTRA_REGON = "regon";
//    private static final String STATE_EXTRA_PERSONALIDNO = "personal_id_no";
//    private static final String STATE_EXTRA_PERSONALIDISSUEPOST = "personal_id_issue_post";
//    private static final String STATE_EXTRA_PERSONALNO = "personal_no";

    private static final String PERSONALID_ISSUE_DT_PICKER_FRAGMENT = "personalid_dt_picker_fragment";

//    private Date mIdIssueDt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.hent_identification, container, false);
    }

    public void onViewCreated(View view, Bundle state) {
        super.onViewCreated(view, state);
        ensureContent();

//        if(state != null) {
//            setWetLicenceNo(state.getString(STATE_EXTRA_WETLICENCENO));
//            setWetNo(state.getString(STATE_EXTRA_WETNO));
//            setFiscalNo(state.getString(STATE_EXTRA_FISCALNO));
//            setREGON(state.getString(STATE_EXTRA_REGON));
//            setPersonalIdNo(state.getString(STATE_EXTRA_PERSONALIDNO));
//            setIssuePost(state.getString(STATE_EXTRA_PERSONALIDISSUEPOST));
//            setPersonalNo(state.getString(STATE_EXTRA_PERSONALNO));
//            setIssueDate((Date) state.getSerializable(STATE_EXTRA_PERSONALIDISSUEDT));
//        }
    }
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateInputs();
    }

    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
//        state.putSerializable(STATE_EXTRA_PERSONALIDISSUEDT, getIssueDate());
//        state.putString(STATE_EXTRA_WETLICENCENO, getWetLicenceNo());
//        state.putString(STATE_EXTRA_WETNO, getWetNo());
//        state.putString(STATE_EXTRA_FISCALNO, getFiscalNo());
//        state.putString(STATE_EXTRA_REGON, getREGON());
//        state.putString(STATE_EXTRA_PERSONALIDNO, getPersonalIdNo());
//        state.putString(STATE_EXTRA_PERSONALIDISSUEPOST, getIssuePost());
//        state.putString(STATE_EXTRA_PERSONALNO, getPersonalNo());
    }

    private void ensureContent() {
        getIssueDateBox().setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onChangeIssueDt();
            }
        });


        Resources r = getResources();
        getPersonalIdNoBox().setFilters(
                new InputFilter[]{new InputFilter.AllCaps(),
                        new InputFilter.LengthFilter(r.getInteger(R.integer.MAX_IDNO))});


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        ((BusActivity) activity).register(this);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        BusActivity activity = (BusActivity) getActivity();
        activity.unregister(this);

    }

    @Subscribe
    public void issueDtPicked(DatePickedEvent event) {
        mInput.setIssueDate(event.getDate());
        updateIssueDate();
    }

    private void onChangeIssueDt()
    {
        DatePickerFragment dateFragment = new DatePickerFragment();
        dateFragment.setDate(mInput.getIssueDate());
        dateFragment.show(getFragmentManager(), PERSONALID_ISSUE_DT_PICKER_FRAGMENT);
    }

    protected String getFiscalNo()
    {
        return getFiscalNoBox().getText().toString();
    }

    protected EditText getFiscalNoBox()
    {
        return (EditText) findViewById(R.id.hentFiscalNoBox);
    }

    protected void updateFiscalNo()
    {
        getFiscalNoBox().setText(mInput.getFiscalNo());
    }

    protected String getWetNo()
    {
        return getWetIdNoBox().getText().toString();
    }

    protected EditText getWetIdNoBox()
    {
        return (EditText) findViewById(R.id.hentWetNoEditbox);
    }

    protected void updateWetNo()
    {
        getWetIdNoBox().setText(mInput.getWetNo());
    }

    protected String getPersonalNo()
    {
        return getPersonalNoBox().getText().toString();
    }

    protected EditText getPersonalNoBox()
    {
        return (EditText) findViewById(R.id.hentPersonalNoBox);
    }

    protected void updatePersonalNo()
    {
        getPersonalNoBox().setText(mInput.getPersonalNo());
    }

    protected String getREGON()
    {
        return getREGONBox().getText().toString();
    }

    protected EditText getREGONBox()
    {
        return (EditText) findViewById(R.id.hentREGON);
    }

    protected void updateREGON()
    {
        getREGONBox().setText(mInput.getREGON());
    }

    protected String getPersonalIdNo()
    {
        return getPersonalIdNoBox().getText().toString();
    }

    protected EditText getPersonalIdNoBox()
    {
        return (EditText) findViewById(R.id.hentIdNoEditbox);
    }

    protected void updatePersonalIdNo()
    {
        EditText hentIdNo = getPersonalIdNoBox();
        hentIdNo.setText(mInput.getPersonalIdNo());
    }

    protected String getIssuePost()
    {
        return getIssuePostBox().getText().toString();
    }

    protected EditText getIssuePostBox()
    {
        return (EditText) findViewById(R.id.hentIdIssuePostEditbox);
    }

    protected void updateIssuePost()
    {
        getIssuePostBox().setText(mInput.getIssuePost());
    }

//    protected Date getIssueDate()
//    {
//        return mIdIssueDt;
//    }

    protected EditText getIssueDateBox()
    {
        return (EditText) findViewById(R.id.hentIdIssueDateEditbox);
    }

    protected void updateIssueDate()
    {
        Date issueDate = mInput.getIssueDate();
        EditText hentIdIssueDateBox = getIssueDateBox();
        if(issueDate != null)
        {
            hentIdIssueDateBox.setText(Dates.toDayDate(issueDate));
        }
        else
        {
            hentIdIssueDateBox.getText().clear();
        }
    }


    protected String getWetLicenceNo()
    {
        return getWetLicenceNoBox().getText().toString();
    }

    protected EditText getWetLicenceNoBox()
    {
        return (EditText) findViewById(R.id.hentWetLicenceNo);
    }

    protected void updateWetLicenceNo()
    {
        getWetLicenceNoBox().setText(mInput.getWetLicenceNo());
    }


    private View findViewById(int viewId) {
        return getView().findViewById(viewId);
    }

    public void updateInputs() {

        updateWetLicenceNo();
        updateWetNo();
        updateFiscalNo();
        updateREGON();
        updatePersonalIdNo();
        updateIssueDate();
        updateIssuePost();
        updatePersonalNo();
        
    }

    @Override
    public void save() {

        if(getView() == null) return;

        mInput.setWetLicenceNo(getWetLicenceNo());
        mInput.setWetNo(getWetNo());
        mInput.setFiscalNo(getFiscalNo());
        mInput.setREGON(getREGON());
        mInput.setPersonalIdNo(getPersonalIdNo());
        mInput.setIssuePost(getIssuePost());
        mInput.setPersonalNo(getPersonalNo());

    }
}