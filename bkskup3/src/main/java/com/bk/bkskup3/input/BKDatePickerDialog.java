package com.bk.bkskup3.input;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: root
 * Date: 23.06.11
 * Time: 22:16
 */
public class BKDatePickerDialog implements DatePickerDialog.OnDateSetListener{

    private DatePickerDialog dtPicketDlg;
    private OnDateListener onDateCallback;

    public BKDatePickerDialog(Context context, Date dt,OnDateListener onDateCallback)
    {
        int year,monthOfYear,dayOfMonth;
        if(dt == null)
        {
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            monthOfYear = calendar.get(Calendar.MONTH);
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        }
        else
        {

            year = dt.getYear()+1900;
            monthOfYear = dt.getMonth();
            dayOfMonth = dt.getDate();
        }

        this.dtPicketDlg = new DatePickerDialog(context,this,year,monthOfYear,dayOfMonth);
        this.onDateCallback = onDateCallback;
    }

    protected void onDate(Date dt) {
        onDateCallback.onDate(dt);
    }


    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth)
    {
        onDate(new Date(year-1900,monthOfYear,dayOfMonth,0,0));
    }

    public void show()
    {
        dtPicketDlg.show();
    }

    public static interface OnDateListener  {

        void onDate(Date dt);
    }

}
