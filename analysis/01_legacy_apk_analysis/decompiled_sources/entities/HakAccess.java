package com.p001yd.electricecollector.entities;

import android.os.Parcel;
import android.os.Parcelable;
import com.squareup.moshi.Json;

/* loaded from: classes14.dex */
public class HakAccess implements Parcelable {
    public static final Parcelable.Creator<HakAccess> CREATOR = new Parcelable.Creator<HakAccess>() { // from class: com.yd.electricecollector.entities.HakAccess.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public HakAccess createFromParcel(Parcel parcel) {
            return new HakAccess(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public HakAccess[] newArray(int i) {
            return new HakAccess[i];
        }
    };

    @Json(name = "CR")
    private boolean create;

    @Json(name = "DE")
    private boolean delete;

    @Json(name = "menuName")
    private String menuName;

    @Json(name = "read")
    private boolean read;

    @Json(name = "ED")
    private boolean update;

    public HakAccess() {
    }

    protected HakAccess(Parcel parcel) {
        this.create = parcel.readByte() != 0;
        this.delete = parcel.readByte() != 0;
        this.menuName = parcel.readString();
        this.read = parcel.readByte() != 0;
        this.update = parcel.readByte() != 0;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getMenuName() {
        return this.menuName;
    }

    public boolean isCreate() {
        return this.create;
    }

    public boolean isDelete() {
        return this.delete;
    }

    public boolean isRead() {
        return this.read;
    }

    public boolean isUpdate() {
        return this.update;
    }

    public void setCreate(boolean z) {
        this.create = z;
    }

    public void setDelete(boolean z) {
        this.delete = z;
    }

    public void setMenuName(String str) {
        this.menuName = str;
    }

    public void setRead(boolean z) {
        this.read = z;
    }

    public void setUpdate(boolean z) {
        this.update = z;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte(this.create ? (byte) 1 : (byte) 0);
        parcel.writeByte(this.delete ? (byte) 1 : (byte) 0);
        parcel.writeString(this.menuName);
        parcel.writeByte(this.read ? (byte) 1 : (byte) 0);
        parcel.writeByte(this.update ? (byte) 1 : (byte) 0);
    }
}
