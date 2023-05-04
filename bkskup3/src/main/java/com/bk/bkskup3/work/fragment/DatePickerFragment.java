package com.bk.bkskup3.work.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import com.bk.bkskup3.work.BusActivity;
import com.bk.bkskup3.work.fragment.event.DatePickedEvent;

import java.util.Calendar;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/24/2014
 * Time: 3:26 PM
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {


    private Date mDate;


    public void setDate(Date date) {
        this.mDate = date;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        if (mDate != null) {
            year = mDate.getYear() + 1900;
            month = mDate.getMonth();
            day = mDate.getDate();
        }

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
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

    public void onDateSet(DatePicker view, int year, int month, int day) {

        BusActivity activity = (BusActivity) getActivity();
        activity.post(new DatePickedEvent( new Date(year - 1900, month, day)));
    }
}