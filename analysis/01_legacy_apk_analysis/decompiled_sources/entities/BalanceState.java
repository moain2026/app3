package com.p001yd.electricecollector.entities;

import android.os.Parcel;
import android.os.Parcelable;
import com.squareup.moshi.Json;

/* loaded from: classes14.dex */
public class BalanceState implements Parcelable {
    public static final Parcelable.Creator<BalanceState> CREATOR = new Parcelable.Creator<BalanceState>() { // from class: com.yd.electricecollector.entities.BalanceState.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BalanceState createFromParcel(Parcel parcel) {
            return new BalanceState(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BalanceState[] newArray(int i) {
            return new BalanceState[i];
        }
    };

    @Json(name = "balance")
    double balance;

    @Json(name = "dain")
    double dain;

    @Json(name = "dain2")
    double dain2;

    @Json(name = "mdate")
    String mdate;

    @Json(name = "mden")
    double mden;

    @Json(name = "mden2")
    double mden2;

    @Json(name = "name")
    String name;

    @Json(name = "num")
    String num;

    @Json(name = "type")
    int type;

    public BalanceState() {
    }

    protected BalanceState(Parcel parcel) {
        this.name = parcel.readString();
        this.num = parcel.readString();
        this.type = parcel.readInt();
        this.mden = parcel.readDouble();
        this.dain = parcel.readDouble();
        this.mden2 = parcel.readDouble();
        this.dain2 = parcel.readDouble();
        this.balance = parcel.readDouble();
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

    public double getDain2() {
        return this.dain2;
    }

    public String getDate() {
        return this.mdate;
    }

    public double getMden() {
        return this.mden;
    }

    public double getMden2() {
        return this.mden2;
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

    public void seMden2(double d) {
        this.mden2 = d;
    }

    public void seMdenain2(double d) {
        this.mden2 = d;
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

    public void setMden(double d) {
        this.mden = d;
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
        parcel.writeDouble(this.mden2);
        parcel.writeDouble(this.dain2);
        parcel.writeDouble(this.balance);
        parcel.writeString(this.mdate);
    }
}
