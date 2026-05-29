package com.p001yd.electricecollector.entities;

import android.os.Parcel;
import android.os.Parcelable;
import com.squareup.moshi.Json;

/* loaded from: classes14.dex */
public class Tblh implements Parcelable {
    public static final Parcelable.Creator<Tblh> CREATOR = new Parcelable.Creator<Tblh>() { // from class: com.yd.electricecollector.entities.Tblh.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Tblh createFromParcel(Parcel parcel) {
            return new Tblh(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Tblh[] newArray(int i) {
            return new Tblh[i];
        }
    };

    @Json(name = "name")
    String name;

    @Json(name = "num")
    int num;

    protected Tblh(Parcel parcel) {
        this.name = parcel.readString();
        this.num = parcel.readInt();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getname() {
        return this.name;
    }

    public int getnum() {
        return this.num;
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
    }
}
