package com.bk.bkskup3.work.fragment.event;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/24/2014
 * Time: 3:36 PM
 */
public class DatePickedEvent extends FragmentEvent {

    private Date mDate;


    public DatePickedEvent(Date date) {
        this.mDate = date;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        this.mDate = date;
    }
}
