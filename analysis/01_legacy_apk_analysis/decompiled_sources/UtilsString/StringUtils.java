package com.p001yd.electricecollector.UtilsString;

/* loaded from: classes6.dex */
public final class StringUtils {
    public static final String EMPTY_STRING = "";

    private StringUtils() {
    }

    public static boolean isBlank(CharSequence charSequence) {
        int length;
        if (charSequence == null || (length = charSequence.length()) == 0) {
            return true;
        }
        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(charSequence.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEmpty(CharSequence charSequence) {
        return charSequence == null || charSequence.length() == 0;
    }

    public static String shortenString(String str, int i, String str2) {
        if (isEmpty(str2)) {
            throw new IllegalArgumentException("appendix must not be empty.");
        }
        if (i <= str2.length()) {
            throw new IllegalArgumentException("maxLength must be greater then appendix length.");
        }
        if (isEmpty(str)) {
            return "";
        }
        if (str.length() <= i) {
            return str;
        }
        return str.substring(0, i - str2.length()) + str2;
    }

    public static String trim(String str) {
        if (str == null) {
            return null;
        }
        return str.trim();
    }

    public static String trimToEmpty(String str) {
        return str == null ? "" : str.trim();
    }

    public static String trimToNull(String str) {
        String trim = trim(str);
        if (isEmpty(trim)) {
            return null;
        }
        return trim;
    }
}
