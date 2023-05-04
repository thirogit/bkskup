package com.bk.bkskup3.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: root
 * Date: 24.06.11
 * Time: 00:06
 */
public class Dates
{
   private static DateFormat dayDateFmt = new SimpleDateFormat("yyyy-MM-dd");
   private static DateFormat dateTimeFullFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   private static DateFormat dateTimeShortFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");


    public static  Date fromDayDate(String dayDtStr) throws ParseException {
        return dayDateFmt.parse(dayDtStr);
    }

   public static String toDayDate(Date dt)
   {
      return formatDate(dt,dayDateFmt);
   }
   
   public static String toDateTimeLong(Date dt)
   {
      return formatDate(dt,dateTimeFullFmt);
   }

   public static String toDateTimeShort(Date dt)
   {
      return formatDate(dt,dateTimeShortFmt);
   }

    public static Date now()
    {
        return Calendar.getInstance().getTime();
    }

   private static String formatDate(Date dt, DateFormat dtFmt)
   {
      if(dt != null)
      {
         return dtFmt.format(dt);
      }
      return "";
   }

    public static boolean sameMonth(Date dt1,Date dt2)
    {
        if(dt1.getYear() == dt2.getYear())
        {
            return dt1.getMonth() == dt2.getMonth();
        }
        return false;
    }

    public static Date toBeginOfMonth(Date dt)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dt);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date plusMonth(Date dt,int months)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dt);
        calendar.add(Calendar.MONTH, months);
        return calendar.getTime();
    }

    public static Date plusYear(Date dt,int years)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dt);
        calendar.add(Calendar.YEAR, years);
        return calendar.getTime();
    }

    public static Date plusDays(Date dt,int days)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dt);
        calendar.add(Calendar.DATE, days);
        return calendar.getTime();
    }

    public static Date toEndOfMonth(Date dt)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dt);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }
}
