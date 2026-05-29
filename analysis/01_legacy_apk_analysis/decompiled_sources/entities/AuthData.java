package com.p001yd.electricecollector.entities;

import android.os.Parcel;
import android.os.Parcelable;
import com.squareup.moshi.Json;

/* loaded from: classes14.dex */
public class AuthData implements Parcelable {
    public static final Parcelable.Creator<AuthData> CREATOR = new Parcelable.Creator<AuthData>() { // from class: com.yd.electricecollector.entities.AuthData.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AuthData createFromParcel(Parcel parcel) {
            return new AuthData(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AuthData[] newArray(int i) {
            return new AuthData[i];
        }
    };

    @Json(name = "appid")
    String appid;

    @Json(name = "password")
    String password;

    @Json(name = "secureId")
    String secureId;

    @Json(name = "username")
    String username;

    public AuthData() {
    }

    public AuthData(Parcel parcel) {
        this.username = parcel.readString();
        this.password = parcel.readString();
        this.appid = parcel.readString();
        this.secureId = parcel.readString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getAppId() {
        return this.appid;
    }

    public String getPassword() {
        return this.password;
    }

    public String getSecurId() {
        return this.secureId;
    }

    public String getUserName() {
        return this.username;
    }

    public void setAppId(String str) {
        this.appid = str;
    }

    public void setPassword(String str) {
        this.password = str;
    }

    public void setSecurId(String str) {
        this.secureId = str;
    }

    public void setUserName(String str) {
        this.username = str;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.username);
        parcel.writeString(this.password);
        parcel.writeString(this.appid);
        parcel.writeString(this.secureId);
    }
}
