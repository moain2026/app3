package com.p001yd.electricecollector.entities;

import android.os.Parcel;
import android.os.Parcelable;
import com.squareup.moshi.Json;

/* loaded from: classes14.dex */
public class Accounts implements Parcelable {
    public static final Parcelable.Creator<Accounts> CREATOR = new Parcelable.Creator<Accounts>() { // from class: com.yd.electricecollector.entities.Accounts.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Accounts createFromParcel(Parcel parcel) {
            return new Accounts(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Accounts[] newArray(int i) {
            return new Accounts[i];
        }
    };

    @Json(name = "balance")
    double balance;

    @Json(name = "dain")
    double dain;

    @Json(name = "mden")
    double mden;

    @Json(name = "name")
    String name;

    @Json(name = "namep")
    String namep;

    @Json(name = "namet")
    String namet;

    @Json(name = "noadad")
    String noadad;

    @Json(name = "nog")
    int nog;

    @Json(name = "nomstlm")
    int nomstlm;

    @Json(name = "notblh")
    int notblh;

    @Json(name = "num")
    int num;

    @Json(name = "tel")
    String tel;

    @Json(name = "type")
    int type;

    public Accounts() {
    }

    protected Accounts(Parcel parcel) {
        this.name = parcel.readString();
        this.num = parcel.readInt();
        this.type = parcel.readInt();
        this.nomstlm = parcel.readInt();
        this.notblh = parcel.readInt();
        this.noadad = parcel.readString();
        this.nog = parcel.readInt();
        this.tel = parcel.readString();
        this.mden = parcel.readDouble();
        this.dain = parcel.readDouble();
        this.balance = parcel.readDouble();
        this.namet = parcel.readString();
        this.namep = parcel.readString();
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

    public double getMden() {
        return this.mden;
    }

    public String getNoadad() {
        return this.noadad;
    }

    public int getNog() {
        return this.nog;
    }

    public int getNomstlm() {
        return this.nomstlm;
    }

    public int getNotblh() {
        return this.notblh;
    }

    public String getTel() {
        return this.tel;
    }

    public int getType() {
        return this.type;
    }

    public String getname() {
        return this.name;
    }

    public String getnamep() {
        return this.namep;
    }

    public String getnamet() {
        return this.namet;
    }

    public int getnum() {
        return this.num;
    }

    public void seMden(double d) {
        this.mden = d;
    }

    public void setBalance(double d) {
        this.balance = d;
    }

    public void setDain(double d) {
        this.mden = d;
    }

    public void setNoadad(String str) {
        this.noadad = str;
    }

    public void setNog(int i) {
        this.nog = i;
    }

    public void setNomstlm(int i) {
        this.nomstlm = i;
    }

    public void setNotblh(int i) {
        this.notblh = i;
    }

    public void setTel(String str) {
        this.tel = str;
    }

    public void setType(int i) {
        this.type = i;
    }

    public void setname(String str) {
        this.name = str;
    }

    public void setnamep(String str) {
        this.namep = str;
    }

    public void setnamet(String str) {
        this.namet = str;
    }

    public void setnum(int i) {
        this.num = i;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
        parcel.writeInt(this.num);
        parcel.writeInt(this.type);
        parcel.writeInt(this.nomstlm);
        parcel.writeInt(this.notblh);
        parcel.writeString(this.noadad);
        parcel.writeInt(this.nog);
        parcel.writeString(this.tel);
        parcel.writeDouble(this.mden);
        parcel.writeDouble(this.dain);
        parcel.writeDouble(this.balance);
        parcel.writeString(this.namet);
        parcel.writeString(this.namep);
    }
}
