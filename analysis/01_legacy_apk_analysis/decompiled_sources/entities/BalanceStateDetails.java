package com.p001yd.electricecollector.entities;

import android.os.Parcel;
import android.os.Parcelable;
import com.squareup.moshi.Json;

/* loaded from: classes14.dex */
public class BalanceStateDetails implements Parcelable {
    public static final Parcelable.Creator<BalanceStateDetails> CREATOR = new Parcelable.Creator<BalanceStateDetails>() { // from class: com.yd.electricecollector.entities.BalanceStateDetails.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BalanceStateDetails createFromParcel(Parcel parcel) {
            return new BalanceStateDetails(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BalanceStateDetails[] newArray(int i) {
            return new BalanceStateDetails[i];
        }
    };

    @Json(name = "cas")
    int cas;

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

    @Json(name = "notes")
    String name;

    @Json(name = "nmstnd")
    String nmstnd;

    @Json(name = "nref")
    String nref;

    @Json(name = "num")
    String num;

    @Json(name = "balance")
    double rsed;

    @Json(name = "type")
    String type;

    protected BalanceStateDetails(Parcel parcel) {
        this.name = parcel.readString();
        this.num = parcel.readString();
        this.type = parcel.readString();
        this.cas = parcel.readInt();
        this.nref = parcel.readString();
        this.nmstnd = parcel.readString();
        this.mden = parcel.readDouble();
        this.dain = parcel.readDouble();
        this.rsed = parcel.readDouble();
        this.mdate = parcel.readString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public int getCas() {
        return this.cas;
    }

    public double getDain() {
        return this.dain;
    }

    public String getDate() {
        return this.mdate.length() > 10 ? this.mdate.substring(0, this.mdate.length() - 9) : this.mdate;
    }

    public double getMden() {
        return this.mden;
    }

    public String getNmstnd() {
        return this.nmstnd;
    }

    public double getRsed() {
        return this.rsed;
    }

    public String getType() {
        return this.type;
    }

    public String getname() {
        return this.name;
    }

    public String getnum() {
        return this.num;
    }

    public void seMden(double d) {
        this.mden = d;
    }

    public void seMdenain(double d) {
        this.mden = d;
    }

    public void seRsed(double d) {
        this.rsed = d;
    }

    public void setCas(int i) {
        this.cas = i;
    }

    public void setDate(String str) {
        this.mdate = str;
    }

    public void setNmstnd(String str) {
        this.nmstnd = str;
    }

    public void setType(String str) {
        this.type = str;
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
        parcel.writeString(this.type);
        parcel.writeInt(this.cas);
        parcel.writeString(this.nref);
        parcel.writeString(this.nmstnd);
        parcel.writeDouble(this.mden);
        parcel.writeDouble(this.dain);
        parcel.writeDouble(this.rsed);
        parcel.writeString(this.mdate);
    }
}
