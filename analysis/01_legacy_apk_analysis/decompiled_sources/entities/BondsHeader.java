package com.p001yd.electricecollector.entities;

import android.os.Parcel;
import android.os.Parcelable;
import com.squareup.moshi.Json;

/* loaded from: classes14.dex */
public class BondsHeader implements Parcelable {
    public static final Parcelable.Creator<BondsHeader> CREATOR = new Parcelable.Creator<BondsHeader>() { // from class: com.yd.electricecollector.entities.BondsHeader.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BondsHeader createFromParcel(Parcel parcel) {
            return new BondsHeader(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BondsHeader[] newArray(int i) {
            return new BondsHeader[i];
        }
    };

    @Json(name = "balance")
    double balance;

    @Json(name = "currencyid")
    String currencyid;

    @Json(name = "currencyname")
    String currencyname;

    @Json(name = "dain")
    double dain;

    @Json(name = "mdate")
    String mdate;

    @Json(name = "mden")
    double mden;

    @Json(name = "name")
    String name;

    @Json(name = "num")
    String num;

    @Json(name = "type")
    int type;

    public BondsHeader() {
    }

    protected BondsHeader(Parcel parcel) {
        this.name = parcel.readString();
        this.num = parcel.readString();
        this.type = parcel.readInt();
        this.mden = parcel.readDouble();
        this.dain = parcel.readDouble();
        this.balance = parcel.readDouble();
        this.mdate = parcel.readString();
        this.currencyname = parcel.readString();
        this.currencyid = parcel.readString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public double getBalance() {
        return this.balance;
    }

    public String getCurrencyId() {
        return this.currencyid;
    }

    public String getCurrencyName() {
        return this.currencyname;
    }

    public double getDain() {
        return this.dain;
    }

    public String getDate() {
        return this.mdate;
    }

    public double getMden() {
        return this.mden;
    }

    public int getType() {
        return this.type;
    }

    public String getname() {
        return this.name;
    }

    public String getnum() {
        return this.num;
    }

    public void seBalance(double d) {
        this.balance = d;
    }

    public void seMden(double d) {
        this.mden = d;
    }

    public void seMdenain(double d) {
        this.mden = d;
    }

    public void setCurrencyId(String str) {
        this.currencyid = str;
    }

    public void setCurrencyName(String str) {
        this.currencyname = str;
    }

    public void setDate(String str) {
        this.mdate = str;
    }

    public void setType(int i) {
        this.type = i;
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
        parcel.writeInt(this.type);
        parcel.writeDouble(this.mden);
        parcel.writeDouble(this.dain);
        parcel.writeDouble(this.balance);
        parcel.writeString(this.mdate);
        parcel.writeString(this.currencyname);
        parcel.writeString(this.currencyid);
    }
}
