package com.p001yd.electricecollector.entities;

import android.os.Parcel;
import android.os.Parcelable;
import com.p001yd.electricecollector.Utils;
import com.squareup.moshi.Json;

/* loaded from: classes14.dex */
public class RepBoxMovesDetals implements Parcelable {
    public static final Parcelable.Creator<RepBoxMovesDetals> CREATOR = new Parcelable.Creator<RepBoxMovesDetals>() { // from class: com.yd.electricecollector.entities.RepBoxMovesDetals.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RepBoxMovesDetals createFromParcel(Parcel parcel) {
            return new RepBoxMovesDetals(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RepBoxMovesDetals[] newArray(int i) {
            return new RepBoxMovesDetals[i];
        }
    };

    @Json(name = "amount")
    String amount;

    @Json(name = "name")
    String name;

    @Json(name = "nmstnd")
    String nmstnd;

    @Json(name = "notes")
    String notes;

    @Json(name = "typems")
    String typems;

    protected RepBoxMovesDetals(Parcel parcel) {
        this.name = parcel.readString();
        this.amount = parcel.readString();
        this.typems = parcel.readString();
        this.nmstnd = parcel.readString();
        this.notes = parcel.readString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getAmount() {
        return Utils.number_ToString(this.amount);
    }

    public String getNmstnd() {
        return this.nmstnd;
    }

    public String getNotes() {
        return this.notes;
    }

    public String getTypems() {
        return this.typems;
    }

    public String getname() {
        return this.name;
    }

    public void setAmount(String str) {
        this.amount = str;
    }

    public void setNmstnd(String str) {
        this.nmstnd = str;
    }

    public void setNotes(String str) {
        this.notes = str;
    }

    public void setTypems(String str) {
        this.typems = str;
    }

    public void setname(String str) {
        this.name = str;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
        parcel.writeString(this.amount);
        parcel.writeString(this.typems);
        parcel.writeString(this.nmstnd);
        parcel.writeString(this.notes);
    }
}
