package com.p001yd.electricecollector.entities;

import android.os.Parcel;
import android.os.Parcelable;
import com.squareup.moshi.Json;

/* loaded from: classes14.dex */
public class AccountBalanceInfo implements Parcelable {
    public static final Parcelable.Creator<AccountBalanceInfo> CREATOR = new Parcelable.Creator<AccountBalanceInfo>() { // from class: com.yd.electricecollector.entities.AccountBalanceInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AccountBalanceInfo createFromParcel(Parcel parcel) {
            return new AccountBalanceInfo(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AccountBalanceInfo[] newArray(int i) {
            return new AccountBalanceInfo[i];
        }
    };

    @Json(name = "balance")
    double balance;

    @Json(name = "balancelocal")
    double balancelocal;

    @Json(name = "currencyname")
    String currencyname;

    public AccountBalanceInfo() {
    }

    public AccountBalanceInfo(Parcel parcel) {
        this.balancelocal = parcel.readDouble();
        this.balance = parcel.readDouble();
        this.currencyname = parcel.readString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public double getBalance() {
        return this.balance;
    }

    public double getBalanceLocal() {
        return this.balancelocal;
    }

    public String getCurrencyName() {
        return this.currencyname;
    }

    public void setBalance(double d) {
        this.balance = d;
    }

    public void setBalanceLocal(double d) {
        this.balancelocal = d;
    }

    public void setCurrencyName(String str) {
        this.currencyname = str;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.currencyname);
        parcel.writeDouble(this.balance);
        parcel.writeDouble(this.balancelocal);
    }
}
