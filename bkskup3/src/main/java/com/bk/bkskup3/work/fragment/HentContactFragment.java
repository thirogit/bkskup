package com.bk.bkskup3.work.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.bk.bkskup3.R;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/22/2014
 * Time: 11:40 AM
 */
public class HentContactFragment extends HentFragment {

//    private static final String STATE_EXTRA_PHONENO = "phone_no";
//    private static final String STATE_EXTRA_CELLPHONENO = "cell_phone_no";
//    private static final String STATE_EXTRA_EMAIL = "email";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.hent_contact, container, false);
    }

    public void onViewCreated(View view, Bundle state) {
        super.onViewCreated(view, state);
        ensureContent();

//        if(state != null) {
//            setPhoneNo(state.getString(STATE_EXTRA_PHONENO));
//            setCellPhoneNo(state.getString(STATE_EXTRA_CELLPHONENO));
//            setEmail(state.getString(STATE_EXTRA_EMAIL));
//        }

    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateInputs();
    }

    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
//        state.putString(STATE_EXTRA_PHONENO, getPhoneNo());
//        state.putString(STATE_EXTRA_CELLPHONENO, getCellPhoneNo());
//        state.putString(STATE_EXTRA_EMAIL, getEmailAddr());
    }

    private void ensureContent() {
    }

    protected String getPhoneNo()
    {
        return getPhoneNoBox().getText().toString();
    }

    protected EditText getPhoneNoBox()
    {
        return (EditText) findViewById(R.id.hentPhoneNoEditbox);
    }

    protected void updatePhoneNo()
    {
        getPhoneNoBox().setText(mInput.getPhoneNo());
    }

    protected String getCellPhoneNo()
    {
        return getCellPhoneNoBox().getText().toString();
    }

    protected EditText getCellPhoneNoBox()
    {
        return (EditText) findViewById(R.id.hentCellPhoneNoEditbox);
    }

    protected void updateCellPhoneNo()
    {
        getCellPhoneNoBox().setText(mInput.getCellPhoneNo());
    }

    protected String getEmailAddr()
    {
        return getEmailAddrBox().getText().toString();
    }

    protected EditText getEmailAddrBox()
    {
        return (EditText) findViewById(R.id.hentEmailAddrEditbox);
    }

    protected void updateEmail()
    {
        getEmailAddrBox().setText(mInput.getEmail());
    }

    private View findViewById(int viewId) {
        return getView().findViewById(viewId);
    }


    public void updateInputs() {
        if(getView() != null)
        {
            updatePhoneNo();
            updateCellPhoneNo();
            updateEmail();

        }
    }

    @Override
    public void save() {

        if(getView() != null) {
            mInput.setPhoneNo(getPhoneNo());
            mInput.setCellPhoneNo(getCellPhoneNo());
            mInput.setEmail(getEmailAddr());
        }
    }
}
