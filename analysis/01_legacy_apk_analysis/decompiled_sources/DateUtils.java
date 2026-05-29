package com.p001yd.electricecollector;

import android.content.Context;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/* loaded from: classes6.dex */
public class DateUtils {
    static final long MILLIS_IN_A_DAY = 86400000;
    static final long ONE_HOUR = 3600000;

    /* loaded from: classes6.dex */
    public class ViewPeriodType {
        public static final int Custom = 0;
        public static final int ThisMonth = 4;
        public static final int ThisWeek = 3;
        public static final int Today = 1;
        public static final int Yestrday = 2;

        public ViewPeriodType() {
        }
    }

    public static Date GetUTCtime() {
        return getDateFromString(GetUTCtimeAsString());
    }

    public static String GetUTCtimeAsString() {
        SimpleDateFormat dateFormat = getDateFormat();
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(new Date());
    }

    public static Date dateBackward(Date date, int i) {
        return new Date(date.getTime() + (i * MILLIS_IN_A_DAY));
    }

    public static Date dateForward(Date date, int i) {
        return new Date(date.getTime() + (i * MILLIS_IN_A_DAY));
    }

    public static int daysBetween(Date date, Date date2) {
        return (int) (((getDatePart(date2).getTimeInMillis() - getDatePart(date).getTimeInMillis()) + ONE_HOUR) / MILLIS_IN_A_DAY);
    }

    private static SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    }

    private static SimpleDateFormat getDateFormatNullTime() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    }

    public static Date getDateFromString(String str) {
        try {
            return getDateFormat().parse(str);
        } catch (Exception e) {
            return new Date();
        }
    }

    public static Calendar getDatePart(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar;
    }

    public static String getDateToString(Date date) {
        return getDateFormat().format(date);
    }

    public static String getDateToStringNullTime(Date date) {
        return getDateFormatNullTime().format(date);
    }

    public static DateFormat getLongDateFormat(Context context) {
        return android.text.format.DateFormat.getLongDateFormat(context);
    }

    public static String getLongDateStr(Context context, Date date) {
        return getLongDateFormat(context).format(date);
    }

    public static DateFormat getMediumDateFormat(Context context) {
        return android.text.format.DateFormat.getMediumDateFormat(context);
    }

    public static String getMediumDateStr(Context context, Date date) {
        return getMediumDateFormat(context).format(date);
    }

    public static DateFormat getShortDateFormat(Context context) {
        return android.text.format.DateFormat.getDateFormat(context);
    }

    public static String getShortDateStr(Context context, Date date) {
        return getDateFormatNullTime().format(date);
    }
}
