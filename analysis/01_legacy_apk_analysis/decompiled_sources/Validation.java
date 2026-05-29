package com.p001yd.electricecollector;

import android.util.Log;
import android.widget.EditText;
import java.util.regex.Pattern;

/* loaded from: classes6.dex */
public class Validation {
    private static final String EMAIL_MSG = "invalid email";
    private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String PHONE_MSG = "رقم";
    private static final String PHONE_REGEX = "(\\d+)";
    private static final String REQUIRED_MSG = "يجب تعبئة الحقل";

    public static boolean hasText(EditText editText) {
        String trim = editText.getText().toString().trim();
        editText.setError(null);
        if (trim.length() != 0) {
            return true;
        }
        editText.setError(REQUIRED_MSG);
        return false;
    }

    public static boolean isEmailAddress(EditText editText, boolean z) {
        return isValid(editText, EMAIL_REGEX, EMAIL_MSG, z);
    }

    public static boolean isPhoneNumber(EditText editText, boolean z) {
        return isValid(editText, PHONE_REGEX, PHONE_MSG, z);
    }

    public static boolean isValid(EditText editText, String str, String str2, boolean z) {
        String trim = editText.getText().toString().trim();
        editText.setError(null);
        if (z && !hasText(editText)) {
            editText.setError(str2);
            return false;
        }
        Pattern.compile(str);
        if (!z || Pattern.matches(str, trim)) {
            Log.d("Matched:", trim + ":" + str + ":" + String.valueOf(Pattern.matches(str, trim)));
            return true;
        }
        editText.setError(str2);
        return false;
    }

    public static boolean liciense_check() {
        return true;
    }
}
