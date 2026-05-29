package com.p001yd.electricecollector.entities;

import android.os.Parcel;
import android.os.Parcelable;
import com.squareup.moshi.Json;

/* loaded from: classes14.dex */
public class Currency implements Parcelable {
    public static final Parcelable.Creator<Currency> CREATOR = new Parcelable.Creator<Currency>() { // from class: com.yd.electricecollector.entities.Currency.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Currency createFromParcel(Parcel parcel) {
            return new Currency(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Currency[] newArray(int i) {
            return new Currency[i];
        }
    };

    @Json(name = "fls")
    String fls;

    @Json(name = "name")
    String name;

    @Json(name = "num")
    int num;

    @Json(name = "sars")
    double sars;

    protected Currency(Parcel parcel) {
        this.num = parcel.readInt();
        this.name = parcel.readString();
        this.fls = parcel.readString();
        this.sars = parcel.readDouble();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getFls() {
        return this.fls;
    }

    public String getName() {
        return this.name;
    }

    public int getNum() {
        return this.num;
    }

    public double getSars() {
        return this.sars;
    }

    public void setFls(String str) {
        this.fls = str;
    }

    public void setName(String str) {
        this.name = str;
    }

    public void setNum(int i) {
        this.num = i;
    }

    public void setSars(double d) {
        this.sars = d;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.num);
        parcel.writeString(this.name);
        parcel.writeString(this.fls);
        parcel.writeDouble(this.sars);
    }
}
