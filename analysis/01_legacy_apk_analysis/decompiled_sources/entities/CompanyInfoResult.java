package com.p001yd.electricecollector.entities;

import android.os.Parcel;
import android.os.Parcelable;
import com.squareup.moshi.Json;

/* loaded from: classes14.dex */
public class CompanyInfoResult implements Parcelable {
    public static final Parcelable.Creator<CompanyInfoResult> CREATOR = new Parcelable.Creator<CompanyInfoResult>() { // from class: com.yd.electricecollector.entities.CompanyInfoResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CompanyInfoResult createFromParcel(Parcel parcel) {
            return new CompanyInfoResult(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CompanyInfoResult[] newArray(int i) {
            return new CompanyInfoResult[i];
        }
    };

    @Json(name = "compactive")
    String active;

    @Json(name = "compaddress")
    String address;

    @Json(name = "compname")
    String compname;

    @Json(name = "comptelephone")
    String telephone;

    protected CompanyInfoResult(Parcel parcel) {
        this.compname = parcel.readString();
        this.address = parcel.readString();
        this.active = parcel.readString();
        this.telephone = parcel.readString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getActive() {
        return this.active;
    }

    public String getAddress() {
        return this.address;
    }

    public String getCompanyName() {
        return this.compname;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public void setActive(String str) {
        this.active = str;
    }

    public void setAddress(String str) {
        this.address = str;
    }

    public void setCompanyName(String str) {
        this.compname = str;
    }

    public void setTelephone(String str) {
        this.telephone = str;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.compname);
        parcel.writeString(this.address);
        parcel.writeString(this.active);
        parcel.writeString(this.telephone);
    }
}
