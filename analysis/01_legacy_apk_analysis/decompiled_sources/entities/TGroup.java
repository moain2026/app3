package com.p001yd.electricecollector.entities;

import android.os.Parcel;
import android.os.Parcelable;
import com.squareup.moshi.Json;

/* loaded from: classes14.dex */
public class TGroup implements Parcelable {
    public static final Parcelable.Creator<TGroup> CREATOR = new Parcelable.Creator<TGroup>() { // from class: com.yd.electricecollector.entities.TGroup.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public TGroup createFromParcel(Parcel parcel) {
            return new TGroup(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public TGroup[] newArray(int i) {
            return new TGroup[i];
        }
    };

    @Json(name = "name")
    String name;

    @Json(name = "nomk2")
    String nomk2;

    @Json(name = "num")
    String num;

    public TGroup() {
    }

    public TGroup(Parcel parcel) {
        this.name = parcel.readString();
        this.num = parcel.readString();
        this.nomk2 = parcel.readString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getNomk2() {
        return this.nomk2;
    }

    public String getname() {
        return this.name;
    }

    public String getnum() {
        return this.num;
    }

    public void setNomk2(String str) {
        this.nomk2 = str;
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
        parcel.writeString(this.nomk2);
    }
}
