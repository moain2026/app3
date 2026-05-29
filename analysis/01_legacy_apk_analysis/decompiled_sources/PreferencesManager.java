package com.p001yd.electricecollector;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/* loaded from: classes6.dex */
public class PreferencesManager {
    private SharedPreferences prefs;

    public PreferencesManager(Context context) {
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public int getValue(String str, int i) {
        return this.prefs.getInt(str, i);
    }

    public String getValue(String str, String str2) {
        return this.prefs.getString(str, str2);
    }

    public boolean getValue(String str, boolean z) {
        return this.prefs.getBoolean(str, z);
    }

    public void setValue(String str, int i) {
        this.prefs.edit().putInt(str, i).apply();
    }

    public void setValue(String str, String str2) {
        this.prefs.edit().putString(str, str2).apply();
    }

    public void setValue(String str, boolean z) {
        this.prefs.edit().putBoolean(str, z).apply();
    }
}
