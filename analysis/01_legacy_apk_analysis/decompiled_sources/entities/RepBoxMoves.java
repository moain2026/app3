package com.p001yd.electricecollector.entities;

import android.os.Parcel;
import android.os.Parcelable;
import com.squareup.moshi.Json;

/* loaded from: classes14.dex */
public class RepBoxMoves implements Parcelable {
    public static final Parcelable.Creator<RepBoxMoves> CREATOR = new Parcelable.Creator<RepBoxMoves>() { // from class: com.yd.electricecollector.entities.RepBoxMoves.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RepBoxMoves createFromParcel(Parcel parcel) {
            return new RepBoxMoves(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RepBoxMoves[] newArray(int i) {
            return new RepBoxMoves[i];
        }
    };

    @Json(name = "balance")
    double balance;

    @Json(name = "dain")
    double dain;

    @Json(name = "fbalance")
    double fbalance;
    String mdate;

    @Json(name = "mden")
    double mden;

    @Json(name = "name")
    String name;

    @Json(name = "num")
    String num;

    protected RepBoxMoves(Parcel parcel) {
        this.name = parcel.readString();
        this.num = parcel.readString();
        this.balance = parcel.readDouble();
        this.mden = parcel.readDouble();
        this.dain = parcel.readDouble();
        this.fbalance = parcel.readDouble();
        this.mdate = parcel.readString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public double getBalance() {
        return this.balance;
    }

    public double getDain() {
        return this.dain;
    }

    public String getDate() {
        return this.mdate;
    }

    public double getFBalance() {
        return this.fbalance;
    }

    public double getMden() {
        return this.mden;
    }

    public String getname() {
        return this.name;
    }

    public String getnum() {
        return this.num;
    }

    public void setBalance(double d) {
        this.balance = d;
    }

    public void setDain(double d) {
        this.dain = d;
    }

    public void setDate(String str) {
        this.mdate = str;
    }

    public void setFBalance(double d) {
        this.fbalance = d;
    }

    public void setMden(double d) {
        this.mden = d;
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
        parcel.writeDouble(this.balance);
        parcel.writeDouble(this.mden);
        parcel.writeDouble(this.dain);
        parcel.writeDouble(this.fbalance);
        parcel.writeString(this.mdate);
    }
}
