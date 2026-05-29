package com.p001yd.electricecollector.entities;

import android.os.Parcel;
import android.os.Parcelable;
import com.squareup.moshi.Json;

/* loaded from: classes14.dex */
public class Reports implements Parcelable {
    public static final Parcelable.Creator<Reports> CREATOR = new Parcelable.Creator<Reports>() { // from class: com.yd.electricecollector.entities.Reports.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Reports createFromParcel(Parcel parcel) {
            return new Reports(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Reports[] newArray(int i) {
            return new Reports[i];
        }
    };

    @Json(name = "name")
    String name;

    @Json(name = "num")
    String num;

    @Json(name = "type")
    String type;

    private Reports(Parcel parcel) {
        this.name = parcel.readString();
        this.num = parcel.readString();
        this.type = parcel.readString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getName() {
        return this.name;
    }

    public String getnum() {
        return this.num;
    }

    public String gettype() {
        return this.type;
    }

    public void setName(String str) {
        this.name = str;
    }

    public void setnum(String str) {
        this.num = str;
    }

    public void settype(String str) {
        this.type = str;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
        parcel.writeString(this.num);
        parcel.writeString(this.type);
    }
}
