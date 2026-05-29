package com.p001yd.electricecollector.entities;

import com.squareup.moshi.Json;

/* loaded from: classes14.dex */
public class AccessToken {

    @Json(name = "access_token")
    String accessToken;

    @Json(name = "expires_in")
    int expiresIn;

    @Json(name = "refresh_token")
    String refreshToken;

    @Json(name = "token_type")
    String tokenType;

    public String getAccessToken() {
        return this.accessToken;
    }

    public int getExpiresIn() {
        return this.expiresIn;
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }

    public String getTokenType() {
        return this.tokenType;
    }

    public void setAccessToken(String str) {
        this.accessToken = str;
    }

    public void setExpiresIn(int i) {
        this.expiresIn = i;
    }

    public void setRefreshToken(String str) {
        this.refreshToken = str;
    }

    public void setTokenType(String str) {
        this.tokenType = str;
    }
}
