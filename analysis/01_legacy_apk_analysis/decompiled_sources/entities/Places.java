package com.p001yd.electricecollector.entities;

import android.os.Parcel;
import android.os.Parcelable;
import com.squareup.moshi.Json;
import java.util.List;

/* loaded from: classes14.dex */
public class Places implements Parcelable {
    public static final Parcelable.Creator<Places> CREATOR = new Parcelable.Creator<Places>() { // from class: com.yd.electricecollector.entities.Places.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Places createFromParcel(Parcel parcel) {
            return new Places(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Places[] newArray(int i) {
            return new Places[i];
        }
    };

    @Json(name = "GetListGroup")
    List<TGroup> ListGroup;

    @Json(name = "name")
    String name;

    @Json(name = "num")
    String num;

    public Places() {
    }

    public Places(Parcel parcel) {
        this.name = parcel.readString();
        this.num = parcel.readString();
        this.ListGroup = parcel.createTypedArrayList(TGroup.CREATOR);
    }

    public List<TGroup> GetListGroup() {
        return this.ListGroup;
    }

    public void SetListGroup(List<TGroup> list) {
        this.ListGroup = list;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getname() {
        return this.name;
    }

    public String getnum() {
        return this.num;
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
        parcel.writeTypedList(this.ListGroup);
    }
}
