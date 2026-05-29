package com.p001yd.electricecollector.entities;

import android.os.Parcel;
import android.os.Parcelable;
import com.squareup.moshi.Json;

/* loaded from: classes14.dex */
public class RepReading implements Parcelable {
    public static final Parcelable.Creator<RepReading> CREATOR = new Parcelable.Creator<RepReading>() { // from class: com.yd.electricecollector.entities.RepReading.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RepReading createFromParcel(Parcel parcel) {
            return new RepReading(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RepReading[] newArray(int i) {
            return new RepReading[i];
        }
    };

    @Json(name = "ast")
    String ast;

    @Json(name = "name")
    String name;

    @Json(name = "num")
    String num;

    protected RepReading(Parcel parcel) {
        this.name = parcel.readString();
        this.num = parcel.readString();
        this.ast = parcel.readString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getAst() {
        return this.ast;
    }

    public String getname() {
        return this.name;
    }

    public String getnum() {
        return this.num;
    }

    public void setAst(String str) {
        this.ast = str;
    }

    public void setname(String str) {
        this.name = str;
    }

    public void setnum(String str) {
        this.num = str;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
        parcel.writeString(this.num);
        parcel.writeString(this.ast);
    }
}
