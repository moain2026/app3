package com.p001yd.electricecollector.entities;

import android.os.Parcel;
import android.os.Parcelable;
import com.squareup.moshi.Json;

/* loaded from: classes14.dex */
public class ItemBonds implements Parcelable {
    public static final Parcelable.Creator<ItemBonds> CREATOR = new Parcelable.Creator<ItemBonds>() { // from class: com.yd.electricecollector.entities.ItemBonds.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ItemBonds createFromParcel(Parcel parcel) {
            return new ItemBonds(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ItemBonds[] newArray(int i) {
            return new ItemBonds[i];
        }
    };

    @Json(name = "account")
    Accounts account;

    @Json(name = "notes2")
    String bin;

    @Json(name = "branchid")
    String branchid;

    @Json(name = "cas")
    int cas;

    @Json(name = "currency")
    Currency currency;

    @Json(name = "currencyid")
    int currencyid;

    @Json(name = "currencyname")
    String currencyname;

    @Json(name = "dain")
    double dain;

    @Json(name = "equal")
    double equal;
    double finalbalance;

    @Json(name = "mdate")
    String mdate;

    @Json(name = "mden")
    double mden;

    @Json(name = "name")
    String name;

    @Json(name = "name_s")
    String name_s;

    @Json(name = "nmstnd")
    String nmstnd;

    @Json(name = "notes")
    String notes;

    @Json(name = "notes_box")
    String notes_box;

    @Json(name = "nref")
    String nref;

    @Json(name = "nref_docno")
    String nref_docno;

    @Json(name = "num")
    int num;

    @Json(name = "num_s")
    int num_s;

    @Json(name = "price_trans")
    double price_trans;

    @Json(name = "balance")
    double rsed;

    @Json(name = "type")
    int type;

    @Json(name = "userid")
    String userid;

    public ItemBonds() {
    }

    protected ItemBonds(Parcel parcel) {
        this.currency = (Currency) parcel.readParcelable(Currency.class.getClassLoader());
        this.account = (Accounts) parcel.readParcelable(Accounts.class.getClassLoader());
        this.branchid = parcel.readString();
        this.cas = parcel.readInt();
        this.currencyid = parcel.readInt();
        this.currencyname = parcel.readString();
        this.dain = parcel.readDouble();
        this.equal = parcel.readDouble();
        this.mdate = parcel.readString();
        this.mden = parcel.readDouble();
        this.name = parcel.readString();
        this.name_s = parcel.readString();
        this.nmstnd = parcel.readString();
        this.notes = parcel.readString();
        this.notes_box = parcel.readString();
        this.nref = parcel.readString();
        this.nref_docno = parcel.readString();
        this.num = parcel.readInt();
        this.num_s = parcel.readInt();
        this.price_trans = parcel.readDouble();
        this.rsed = parcel.readDouble();
        this.type = parcel.readInt();
        this.userid = parcel.readString();
        this.bin = parcel.readString();
        this.finalbalance = parcel.readDouble();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public Accounts getAccount() {
        return this.account;
    }

    public String getBin() {
        return this.bin;
    }

    public int getCas() {
        return this.cas;
    }

    public Currency getCurrency() {
        return this.currency;
    }

    public int getCurrencyId() {
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

    public double getFinalBalance() {
        return this.finalbalance;
    }

    public double getMden() {
        return this.mden;
    }

    public String getNmstnd() {
        return this.nmstnd;
    }

    public String getNotes() {
        return this.notes;
    }

    public String getNotesBox() {
        return this.notes_box;
    }

    public double getPriceTrans() {
        return this.price_trans;
    }

    public double getRsed() {
        return this.rsed;
    }

    public int getType() {
        return this.type;
    }

    public String getname() {
        return this.name;
    }

    public String getname_s() {
        return this.name_s;
    }

    public int getnum() {
        return this.num;
    }

    public int getnum_s() {
        return this.num_s;
    }

    public void setAccount(Accounts accounts) {
        this.account = accounts;
    }

    public void setBin(String str) {
        this.bin = str;
    }

    public void setCas(int i) {
        this.cas = i;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public void setCurrencyId(int i) {
        this.currencyid = i;
    }

    public void setCurrencyName(String str) {
        this.currencyname = str;
    }

    public void setDain(double d) {
        this.dain = d;
    }

    public void setDate(String str) {
        this.mdate = str;
    }

    public void setEqual(double d) {
        this.equal = d;
    }

    public void setFinalBalance(double d) {
        this.finalbalance = d;
    }

    public void setMden(double d) {
        this.mden = d;
    }

    public void setNmstnd(String str) {
        this.nmstnd = str;
    }

    public void setNotes(String str) {
        this.notes = str;
    }

    public void setNotesBox(String str) {
        this.notes_box = str;
    }

    public void setPriceTrans(double d) {
        this.price_trans = d;
    }

    public void setRsed(double d) {
        this.rsed = d;
    }

    public void setType(int i) {
        this.type = i;
    }

    public void setname(String str) {
        this.name = str;
    }

    public void setname_s(String str) {
        this.name_s = str;
    }

    public void setnum(int i) {
        this.num = i;
    }

    public void setnum_s(int i) {
        this.num_s = i;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(this.currency, i);
        parcel.writeParcelable(this.account, i);
        parcel.writeString(this.branchid);
        parcel.writeInt(this.cas);
        parcel.writeInt(this.currencyid);
        parcel.writeString(this.currencyname);
        parcel.writeDouble(this.dain);
        parcel.writeDouble(this.equal);
        parcel.writeString(this.mdate);
        parcel.writeDouble(this.mden);
        parcel.writeString(this.name);
        parcel.writeString(this.name_s);
        parcel.writeString(this.nmstnd);
        parcel.writeString(this.notes);
        parcel.writeString(this.notes_box);
        parcel.writeString(this.nref);
        parcel.writeString(this.nref_docno);
        parcel.writeInt(this.num);
        parcel.writeInt(this.num_s);
        parcel.writeDouble(this.price_trans);
        parcel.writeDouble(this.rsed);
        parcel.writeInt(this.type);
        parcel.writeString(this.userid);
        parcel.writeString(this.bin);
        parcel.writeDouble(this.finalbalance);
    }
}
