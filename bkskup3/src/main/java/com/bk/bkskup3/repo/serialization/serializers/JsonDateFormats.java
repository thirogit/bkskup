package com.bk.bkskup3.repo.serialization.serializers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 5/24/2015
 * Time: 9:59 PM
 */
public class JsonDateFormats {
    private JsonDateFormats() {
    }

    public static final DateFormat DAY_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final DateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
}
