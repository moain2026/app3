package com.p001yd.electricecollector;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.util.Date;

/* loaded from: classes6.dex */
public class TAPreferences {
    public static String getAccessToken(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("access_token", null);
    }

    public static int getAgentId(Context context) {
        return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("AgentId", "0"));
    }

    public static boolean getAllowContractorEdit(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("AllowContractorEdit", false);
    }

    public static String getAnswerKey(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("AnswerKey", "");
    }

    public static String getAppId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("appId", "1");
    }

    public static String getApplicationName(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("ApplicationType", "Al Momaiaz");
    }

    public static int getApplicationType(Context context) {
        return 0;
    }

    public static String getBackupFolder(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("GoogleDocsBackupFolder", null);
    }

    public static String getCityStateZip(Context context) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return defaultSharedPreferences.getString("City", "") + " " + defaultSharedPreferences.getString("CountryState", "") + " " + defaultSharedPreferences.getString("Zip", "");
    }

    public static String getCompanyAddress(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("Address", "....");
    }

    public static String getCompanyLogo(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("CompanyLogo", null);
    }

    public static String getCompanyName(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("CompanyName", "...");
    }

    public static String getCompanyPhone(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("Phone", "...");
    }

    public static String getCreditCaption(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("credit_caption", "له");
    }

    public static String getCreditDebitCaption(Context context, int i) {
        return i > 0 ? PreferenceManager.getDefaultSharedPreferences(context).getString("debit_caption", "عليه") : i < 0 ? PreferenceManager.getDefaultSharedPreferences(context).getString("credit_caption", "له") : "";
    }

    public static Date getDateOfLastSynchroContractors(Context context) {
        try {
            return DateUtils.getDateFromString(PreferenceManager.getDefaultSharedPreferences(context).getString("DateOfLastSynchroContractors", DateUtils.getDateToString(DateUtils.getDateFromString("1980-01-01 00:00:00"))));
        } catch (Exception e) {
            return DateUtils.getDateFromString("1980-01-01 00:00:00");
        }
    }

    public static Date getDateOfLastSynchroGoods(Context context) {
        try {
            return DateUtils.getDateFromString(PreferenceManager.getDefaultSharedPreferences(context).getString("DateOfLastSynchroGoods", DateUtils.getDateToString(DateUtils.getDateFromString("1980-01-01 00:00:00"))));
        } catch (Exception e) {
            return DateUtils.getDateFromString("1980-01-01 00:00:00");
        }
    }

    public static Date getDateOfStartEvaluation(Context context) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Date date = new Date();
        Date dateFromString = DateUtils.getDateFromString(defaultSharedPreferences.getString("DateOfStartEvaluation", DateUtils.getDateToString(date)));
        if (DateUtils.daysBetween(date, dateFromString) == 30) {
            setDateOfStartEvaluation(context, dateFromString);
        }
        return dateFromString;
    }

    public static String getDebitCaption(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("debit_caption", "عليه");
    }

    public static boolean getDocumentUploadImmediately(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("DocumentUploadImmediately", false);
    }

    public static boolean getDownloadOnlyExistsGoods(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("DownloadOnlyExistsGoods", false);
    }

    public static int getFontSize(Context context) {
        return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("FontSize", "1"));
    }

    public static int getGoodsSelectionBehavior(Context context) {
        return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("GoodsSelectionBehavior", "0"));
    }

    public static String getHostingIP(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("HostingIP", "192.168.0.100");
    }

    public static String getHostingIP2(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("HostingIP2", "127.0.0.1");
    }

    public static String getHostingPort(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("HostingPort", "9002");
    }

    public static String getHostingPort2(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("HostingPort2", "9002");
    }

    public static String getLicenseOwner(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("LicenseOwner", "");
    }

    public static String getLockType(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("LockType", "1");
    }

    public static String getLoginUserPassword(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("AccountPassword", "0");
    }

    public static String getLoginUserPattern(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("AccountPattern", "0");
    }

    public static String getMessageFooterText(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("message_footer_key", "الى حسابكم");
    }

    public static String getMessageText(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("message_text_key", "تم تسديد مبلغ :");
    }

    public static int getMostImportantGoodsColor(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt("MostImportantGoodsColor", 1728027084);
    }

    public static String getNewDocNumber(Context context, int i) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        switch (i) {
            case 1:
                return defaultSharedPreferences.getString("PayInSlipStartNumber", "1");
            case 2:
                return defaultSharedPreferences.getString("PayoutOrderStartNumber", "1");
            default:
                return "1";
        }
    }

    public static boolean getNotfayAmount(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("amountbond_on_off_key", false);
    }

    public static int getPaperSize(Context context) {
        return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("PaperSize", "0"));
    }

    public static int getPrinterRype(Context context) {
        return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("PrinterType", "0"));
    }

    public static int getSelectedMenuId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt("selectedMenuId", 1);
    }

    public static boolean getSendSMS(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("message_on_off_key", false);
    }

    public static String getSetectedPrinterConnectionString(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("PrinterSelection", "");
    }

    public static String getSetectedPrinterModel(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("PrinterModel", "");
    }

    public static int getSetectedRestaurantTableId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt("SetectedRestaurantTableId", 0);
    }

    public static boolean getSettlementsByAgent(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("SettlementsByAgent", false);
    }

    public static boolean getShowCreditRecord(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("showCreditRecord", false);
    }

    public static String getUserLogin(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("GoogleAccountLogin", null);
    }

    public static String getUserPassword(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("GoogleAccountPassword", null);
    }

    public static void setAccessToken(Context context, String str) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("access_token", str).commit();
    }

    public static void setAnswerKey(Context context, String str) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("AnswerKey", str).commit();
    }

    public static void setAppId(Context context, String str) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("appId", str).commit();
    }

    public static void setDateOfLastSynchroContractors(Context context, Date date) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("DateOfLastSynchroContractors", DateUtils.getDateToString(date)).commit();
    }

    public static void setDateOfLastSynchroGoods(Context context, Date date) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("DateOfLastSynchroGoods", DateUtils.getDateToString(date)).commit();
    }

    public static void setDateOfStartEvaluation(Context context, Date date) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("DateOfStartEvaluation", DateUtils.getDateToString(date)).commit();
    }

    public static void setHostingIP(Context context, String str) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("HostingIP", str).commit();
    }

    public static void setLicenseOwner(Context context, String str) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("LicenseOwner", str).commit();
    }

    public static void setNewDocNumber(Context context, int i, String str) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int parseInt = Integer.parseInt(getNewDocNumber(context, i));
        try {
            int parseInt2 = Integer.parseInt(str);
            if (parseInt2 >= parseInt) {
                int i2 = parseInt2 + 1;
                switch (i) {
                    case 1:
                        defaultSharedPreferences.edit().putString("PayInSlipStartNumber", Integer.toString(i2)).commit();
                        return;
                    case 2:
                        defaultSharedPreferences.edit().putString("PayoutOrderStartNumber", Integer.toString(i2)).commit();
                        return;
                    default:
                        return;
                }
            }
        } catch (Exception e) {
        }
    }

    public static void setSelectedMenuId(Context context, int i) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("selectedMenuId", i).commit();
    }

    public static void setSelectedPrinterModel(Context context, String str) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("PrinterModel", str).commit();
    }

    public static void setSetectedRestaurantTableId(Context context, int i) {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.putInt("SetectedRestaurantTableId", i);
        edit.commit();
    }
}
