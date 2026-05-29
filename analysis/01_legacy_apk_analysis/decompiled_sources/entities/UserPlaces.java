package com.p001yd.electricecollector.entities;

import android.os.Parcel;
import android.os.Parcelable;
import com.squareup.moshi.Json;

/* loaded from: classes14.dex */
public class UserPlaces implements Parcelable {
    public static final Parcelable.Creator<UserPlaces> CREATOR = new Parcelable.Creator<UserPlaces>() { // from class: com.yd.electricecollector.entities.UserPlaces.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public UserPlaces createFromParcel(Parcel parcel) {
            return new UserPlaces(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public UserPlaces[] newArray(int i) {
            return new UserPlaces[i];
        }
    };

    @Json(name = "name")
    String name;

    @Json(name = "no_mstlm")
    String nomstlm;

    @Json(name = "num")
    int num;

    @Json(name = "RED")
    int red;

    @Json(name = "SDAD")
    int sdad;

    protected UserPlaces(Parcel parcel) {
        this.name = parcel.readString();
        this.num = parcel.readInt();
        this.red = parcel.readInt();
        this.sdad = parcel.readInt();
        this.nomstlm = parcel.readString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getNomstlm() {
        return this.nomstlm;
    }

    public int getRed() {
        return this.red;
    }

    public int getSdad() {
        return this.sdad;
    }

    public String getname() {
        return this.name;
    }

    public int getnum() {
        return this.num;
    }

    public void setNomstlm(String str) {
        this.nomstlm = str;
    }

    public void setRed(int i) {
        this.red = i;
    }

    public void setSdad(int i) {
        this.sdad = i;
    }

    public void setname(String str) {
        this.name = str;
    }

    public void setnum(int i) {
        this.num = i;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
        parcel.writeInt(this.num);
        parcel.writeInt(this.red);
        parcel.writeInt(this.sdad);
        parcel.writeString(this.nomstlm);
    }
}
