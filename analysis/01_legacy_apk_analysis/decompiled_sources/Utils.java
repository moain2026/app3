package com.p001yd.electricecollector;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.telephony.SmsManager;
import android.text.TextUtils;
import androidx.fragment.app.FragmentActivity;
import com.p001yd.electricecollector.entities.ApiError;
import com.p001yd.electricecollector.network.RetrofitBuilder;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.sql.Time;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;
import okhttp3.ResponseBody;

/* loaded from: classes6.dex */
public class Utils {
    static String securrid = String.valueOf(Build.BOARD.length() % 10) + String.valueOf(Build.BRAND.length() % 10) + String.valueOf(Build.DEVICE.length() % 10) + String.valueOf(Build.DISPLAY.length() % 10) + String.valueOf(Build.HOST.length() % 10) + String.valueOf(Build.ID.length() % 10) + String.valueOf(Build.MANUFACTURER.length() % 10) + String.valueOf(Build.MODEL.length() % 10) + String.valueOf(Build.PRODUCT.length() % 10) + String.valueOf(Build.TAGS.length() & 10) + String.valueOf(Build.TYPE.length() % 10) + String.valueOf(Build.USER.length() % 10);
    static String serial = Build.getRadioVersion();
    public static String uuid = new UUID(securrid.hashCode(), serial.hashCode()).toString();

    public static Boolean ComperDate(String str, String str2) {
        String[] split = str2.split("/");
        int parseInt = Integer.parseInt(split[0]);
        int parseInt2 = Integer.parseInt(split[1]);
        int parseInt3 = Integer.parseInt(split[2]);
        String[] split2 = str.split("/");
        return ((parseInt == Integer.parseInt(split2[0])) & (parseInt2 == Integer.parseInt(split2[1]))) & (parseInt3 == Integer.parseInt(split2[2]));
    }

    public static String GetDeviceId(Context context) {
        return new Defence(context).getDeviceId();
    }

    public static String MoneyFormat(double d) {
        return NumberFormat.getInstance(Locale.ENGLISH).format(d);
    }

    public static String NumberFormat(double d) {
        return NumberFormat(d, 6);
    }

    public static String NumberFormat(double d, int i) {
        NumberFormat numberInstance = NumberFormat.getNumberInstance(Locale.ENGLISH);
        numberInstance.setMaximumFractionDigits(i);
        return numberInstance.format(d);
    }

    public static String PercentFormat(double d) {
        return NumberFormat.getPercentInstance(Locale.ENGLISH).format(d);
    }

    public static ApiError converErrors(ResponseBody responseBody) {
        try {
            return (ApiError) RetrofitBuilder.getRetrofit().responseBodyConverter(ApiError.class, new Annotation[0]).convert(responseBody);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getArabicDate(String str) {
        String[] split = str.split("/");
        String str2 = split[0];
        return String.format("%s-%s-%s", split[2], split[1], str2);
    }

    public static Calendar getCalendarSystem(Context context) {
        return Calendar.getInstance();
    }

    public static String getConvertEnglishDate(String str, String str2) {
        String[] split = str.split("/");
        int parseInt = Integer.parseInt(split[2]);
        int parseInt2 = Integer.parseInt(split[1]);
        Calendar calendar = Calendar.getInstance();
        calendar.set(1, parseInt);
        calendar.set(2, parseInt2 - 1);
        calendar.set(5, Integer.parseInt(split[0]));
        return new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH).format(calendar.getTime());
    }

    public static String getCurrentDate() {
        return new SimpleDateFormat(" HH:mm:aa yyyy-MM-dd", Locale.ENGLISH).format(new Date(new Date().getTime()));
    }

    public static Time getCurrentTime() {
        return new Time(new Date().getTime());
    }

    private static SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    }

    private static SimpleDateFormat getDateFormatApi() {
        return new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
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

    private static SimpleDateFormat getDateTimeFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public static String getDateToString(Date date) {
        if (date != null) {
            return getDateFormat().format(date);
        }
        return null;
    }

    public static String getDateToStringNullTime(Date date) {
        return getDateFormatNullTime().format(date);
    }

    public static String getEnglishDate(String str, String str2) {
        String[] split = str.split("/");
        int parseInt = Integer.parseInt(split[0]);
        int parseInt2 = Integer.parseInt(split[1]);
        Calendar calendar = Calendar.getInstance();
        calendar.set(1, parseInt);
        calendar.set(2, parseInt2 - 1);
        calendar.set(5, Integer.parseInt(split[2]));
        return new SimpleDateFormat(str2, Locale.ENGLISH).format(calendar.getTime());
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

    public static String getShortDateStr(FragmentActivity fragmentActivity, Date date) {
        return getDateFormatNullTime().format(date);
    }

    public static String getShortDateStrApi(FragmentActivity fragmentActivity, Date date) {
        return getDateFormatApi().format(date);
    }

    public static AlertDialog msgBox(int i, Context context, Object... objArr) {
        return msgBox((DialogInterface.OnClickListener) null, context.getText(i).toString(), context, objArr);
    }

    public static AlertDialog msgBox(DialogInterface.OnClickListener onClickListener, int i, Context context, Object... objArr) {
        return msgBox(onClickListener, context.getText(i).toString(), context, objArr);
    }

    public static AlertDialog msgBox(DialogInterface.OnClickListener onClickListener, String str, Context context, Object... objArr) {
        return msgBox(onClickListener, str, context.getString(C1018R.string.app_name), context, objArr);
    }

    public static AlertDialog msgBox(DialogInterface.OnClickListener onClickListener, String str, String str2, Context context, Object... objArr) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(str2);
        builder.setMessage(objArr == null ? str : String.format(str, objArr));
        builder.setPositiveButton("موافق", onClickListener);
        return builder.show();
    }

    public static AlertDialog msgBox(String str, Context context, Object... objArr) {
        return msgBox((DialogInterface.OnClickListener) null, str, context, objArr);
    }

    public static String numberToString(double d) {
        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        decimalFormat.applyPattern("#,###.##");
        return decimalFormat.format(d);
    }

    public static String number_ToString(String str) {
        return NumberFormat.getInstance(Locale.ENGLISH).format(Double.parseDouble(str));
    }

    public static void openSendSmsSilent(String str, String str2, Context context) {
        if (str2 == null) {
            return;
        }
        PendingIntent broadcast = PendingIntent.getBroadcast(context, 0, new Intent(), 0);
        SmsManager smsManager = SmsManager.getDefault();
        if (str2.length() < 70) {
            smsManager.sendTextMessage(str, null, str2, broadcast, null);
            return;
        }
        Iterator<String> it = smsManager.divideMessage(str2).iterator();
        while (it.hasNext()) {
            smsManager.sendTextMessage(str, null, it.next(), broadcast, null);
        }
    }

    public static double parseDouble(String str) throws ParseException {
        try {
            return NumberFormat.getPercentInstance(Locale.ENGLISH).parse(str).doubleValue();
        } catch (ParseException e) {
            return NumberFormat.getNumberInstance(Locale.ENGLISH).parse(str).doubleValue();
        }
    }

    public static double parsePercent(String str) throws ParseException {
        if (!TextUtils.substring(str, str.length(), str.length()).equals("%")) {
            str = String.valueOf(str) + "%";
        }
        try {
            return NumberFormat.getPercentInstance(Locale.ENGLISH).parse(str).doubleValue();
        } catch (ParseException e) {
            return NumberFormat.getNumberInstance(Locale.ENGLISH).parse(str).doubleValue() / 100.0d;
        }
    }

    private static String removeSeparator(String str, boolean z) {
        String replace = str.replace(",", "");
        return !z ? replace.replace(".", "") : replace;
    }

    public static int stringToNumber(String str) {
        String removeSeparator = removeSeparator(str, false);
        if (removeSeparator.length() == 0) {
            removeSeparator = "0";
        }
        return Integer.parseInt(removeSeparator);
    }
}
