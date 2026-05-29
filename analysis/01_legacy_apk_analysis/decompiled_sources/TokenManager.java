package com.p001yd.electricecollector;

import android.content.SharedPreferences;
import com.p001yd.electricecollector.entities.AccessToken;

/* loaded from: classes6.dex */
public class TokenManager {
    private static TokenManager INSTANCE = null;
    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;

    private TokenManager(SharedPreferences sharedPreferences) {
        this.prefs = sharedPreferences;
        this.editor = sharedPreferences.edit();
    }

    public static synchronized TokenManager getInstance(SharedPreferences sharedPreferences) {
        TokenManager tokenManager;
        synchronized (TokenManager.class) {
            if (INSTANCE == null) {
                INSTANCE = new TokenManager(sharedPreferences);
            }
            tokenManager = INSTANCE;
        }
        return tokenManager;
    }

    public void deleteToken() {
        this.editor.remove("ACCESS_TOKEN").commit();
        this.editor.remove("REFRESH_TOKEN").commit();
    }

    public AccessToken getToken() {
        AccessToken accessToken = new AccessToken();
        accessToken.setAccessToken(this.prefs.getString("ACCESS_TOKEN", null));
        accessToken.setRefreshToken(this.prefs.getString("REFRESH_TOKEN", null));
        return accessToken;
    }

    public void saveToken(AccessToken accessToken) {
        this.editor.putString("ACCESS_TOKEN", accessToken.getAccessToken()).commit();
        this.editor.putString("REFRESH_TOKEN", accessToken.getRefreshToken()).commit();
    }
}
