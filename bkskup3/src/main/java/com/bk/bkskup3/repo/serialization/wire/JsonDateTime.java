package com.bk.bkskup3.repo.serialization.wire;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 5/24/2015
 * Time: 9:34 PM
 */
public class JsonDateTime {

    private Date date;

    public JsonDateTime(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }
}
