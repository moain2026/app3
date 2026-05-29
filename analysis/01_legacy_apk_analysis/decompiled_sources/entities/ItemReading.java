package com.p001yd.electricecollector.entities;

import android.os.Parcel;
import android.os.Parcelable;
import com.squareup.moshi.Json;

/* loaded from: classes14.dex */
public class ItemReading implements Parcelable {
    public static final Parcelable.Creator<ItemReading> CREATOR = new Parcelable.Creator<ItemReading>() { // from class: com.yd.electricecollector.entities.ItemReading.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ItemReading createFromParcel(Parcel parcel) {
            return new ItemReading(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ItemReading[] newArray(int i) {
            return new ItemReading[i];
        }
    };

    @Json(name = "asts")
    double asts;

    @Json(name = "cas")
    int cas;

    @Json(name = "ind")
    int ind;

    /* renamed from: kh */
    @Json(name = "kh")
    double f667kh;

    /* renamed from: ks */
    @Json(name = "ks")
    double f668ks;

    @Json(name = "name")
    String name;

    @Json(name = "namet")
    String namet;

    @Json(name = "noadad")
    String noadad;

    @Json(name = "nog")
    int nog;

    @Json(name = "nomstlm")
    int nomstlm;

    @Json(name = "notblh")
    long notblh;

    @Json(name = "num")
    int num;

    public ItemReading() {
    }

    protected ItemReading(Parcel parcel) {
        this.namet = parcel.readString();
        this.name = parcel.readString();
        this.num = parcel.readInt();
        this.ind = parcel.readInt();
        this.nomstlm = parcel.readInt();
        this.notblh = parcel.readLong();
        this.noadad = parcel.readString();
        this.nog = parcel.readInt();
        this.f668ks = parcel.readDouble();
        this.f667kh = parcel.readDouble();
        this.cas = parcel.readInt();
        this.asts = parcel.readDouble();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public double getAsts() {
        return this.asts;
    }

    public int getCas() {
        return this.cas;
    }

    public double getKh() {
        return this.f667kh;
    }

    public double getKs() {
        return this.f668ks;
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

    public long getNotblh() {
        return this.notblh;
    }

    public int getType() {
        return this.ind;
    }

    public String getname() {
        return this.name;
    }

    public String getnamet() {
        return this.namet;
    }

    public int getnum() {
        return this.num;
    }

    public void setAsts(double d) {
        this.asts = d;
    }

    public void setCas(int i) {
        this.cas = i;
    }

    public void setKh(double d) {
        this.f667kh = d;
    }

    public void setKs(double d) {
        this.f668ks = d;
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

    public void setNotblh(long j) {
        this.notblh = j;
    }

    public void setType(int i) {
        this.ind = i;
    }

    public void setname(String str) {
        this.name = str;
    }

    public void setnamet(String str) {
        this.namet = str;
    }

    public void setnum(int i) {
        this.num = i;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.namet);
        parcel.writeString(this.name);
        parcel.writeInt(this.num);
        parcel.writeInt(this.ind);
        parcel.writeInt(this.nomstlm);
        parcel.writeLong(this.notblh);
        parcel.writeString(this.noadad);
        parcel.writeInt(this.nog);
        parcel.writeDouble(this.f668ks);
        parcel.writeDouble(this.f667kh);
        parcel.writeInt(this.cas);
        parcel.writeDouble(this.asts);
    }
}
