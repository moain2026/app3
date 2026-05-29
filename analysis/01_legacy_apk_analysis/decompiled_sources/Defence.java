package com.p001yd.electricecollector;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import java.util.Date;

/* loaded from: classes6.dex */
public class Defence {
    private Context context;

    public Defence(Context context) {
        this.context = context;
    }

    private static String addStr(String str, int i, int i2) {
        String str2 = "";
        switch (i) {
            case 2:
                str2 = "1";
                break;
            case 4:
                str2 = "0";
                break;
            case 6:
                str2 = "1";
                break;
        }
        str.substring(0, i);
        str.substring(i + i2);
        return str.substring(0, i) + str2 + str.substring(i);
    }

    private String clearKey(String str) {
        return Long.toString(Long.parseLong(cutStr(cutStr(cutStr(Long.toBinaryString(Long.valueOf(Long.parseLong(str, 10)).longValue()), 6, 1), 4, 1), 2, 1), 2));
    }

    private static String cutStr(String str, int i, int i2) {
        return str.substring(0, i) + str.substring(i + i2);
    }

    private String fillKey(String str) {
        addStr(addStr(addStr(Long.toBinaryString(Long.valueOf(Long.parseLong(str, 10)).longValue()), 2, 1), 4, 1), 6, 1);
        return Long.toString(Long.parseLong(addStr(addStr(addStr(Long.toBinaryString(Long.valueOf(Long.parseLong(str, 10)).longValue()), 2, 1), 4, 1), 6, 1), 2));
    }

    public String GenerateKey(String str) {
        byte parseByte = Byte.parseByte(str);
        long j = 0;
        switch (parseByte) {
            case 1:
                j = Integer.parseInt(mashineSerialNumber().substring(0, 7)) ^ 3452971;
                break;
            case 2:
                j = Integer.parseInt(mashineSerialNumber().substring(0, 7)) ^ 3376542;
                break;
            case 3:
                j = Integer.parseInt(mashineSerialNumber().substring(0, 7)) ^ 1298472;
                break;
            case 4:
                j = Integer.parseInt(mashineSerialNumber().substring(0, 7)) ^ 9087321;
                break;
            case 5:
                j = Integer.parseInt(mashineSerialNumber().substring(0, 7)) ^ 9845023;
                break;
            case 6:
                j = Integer.parseInt(mashineSerialNumber().substring(0, 7)) ^ 3213453;
                break;
            case 7:
                j = Integer.parseInt(mashineSerialNumber().substring(0, 7)) ^ 9823569;
                break;
        }
        return fillKey(String.valueOf((int) parseByte) + String.valueOf(j));
    }

    public int addKeyValue(String str) {
        String binaryString = Long.toBinaryString(Long.parseLong(str));
        return Integer.parseInt(binaryString.substring(2, 3) + binaryString.substring(4, 5) + binaryString.substring(6, 7), 2);
    }

    public String getDeviceId() {
        return Long.toString(Long.parseLong(Settings.Secure.getString(this.context.getContentResolver(), "android_id").substring(0, 8), 16));
    }

    public String getImei() {
        return ((TelephonyManager) this.context.getSystemService("phone")).getDeviceId();
    }

    public String getRegistrationKey(int i) {
        return String.valueOf(Long.toString(Long.parseLong(mashineSerialNumber().substring(0, 7)) ^ 6335737)) + "-" + i;
    }

    public boolean isDocumentQuantityLimited(int i, Date date, String str) {
        return !isKeyValid(str, i);
    }

    public boolean isKeyValid(String str, int i) {
        if (str == "") {
            return false;
        }
        try {
            String clearKey = clearKey(str);
            byte parseByte = Byte.parseByte(clearKey.substring(0, 1));
            long j = 0;
            Long valueOf = Long.valueOf(Long.parseLong(clearKey.substring(1)));
            switch (parseByte) {
                case 1:
                    j = valueOf.longValue() ^ 3452971;
                    break;
                case 2:
                    j = valueOf.longValue() ^ 3376542;
                    break;
                case 3:
                    j = valueOf.longValue() ^ 1298472;
                    break;
                case 4:
                    j = valueOf.longValue() ^ 9087321;
                    break;
                case 5:
                    j = valueOf.longValue() ^ 9845023;
                    break;
                case 6:
                    j = valueOf.longValue() ^ 3213453;
                    break;
                case 7:
                    j = valueOf.longValue() ^ 9823569;
                    break;
            }
            Integer.parseInt(mashineSerialNumber().substring(0, 7));
            if (j == Integer.parseInt(mashineSerialNumber().substring(0, 7)) && j != 0) {
                return i <= addKeyValue(str) + (-5);
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public String mashineSerialNumber() {
        return getDeviceId().substring(r0.length() - 8);
    }
}
