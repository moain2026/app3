package com.p001yd.electricecollector;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: classes6.dex */
public class AppMenu implements Parcelable {
    public static final Parcelable.Creator<AppMenu> CREATOR = new Parcelable.Creator<AppMenu>() { // from class: com.yd.electricecollector.AppMenu.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AppMenu createFromParcel(Parcel parcel) {
            return new AppMenu(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AppMenu[] newArray(int i) {
            return new AppMenu[i];
        }
    };
    private boolean create;
    private boolean delete;
    private String menuDescription;
    private String menuId;
    private String menuTitle;
    private boolean read;
    private boolean update;

    public AppMenu() {
    }

    protected AppMenu(Parcel parcel) {
        this.menuId = parcel.readString();
        this.menuTitle = parcel.readString();
        this.menuDescription = parcel.readString();
        this.create = parcel.readByte() != 0;
        this.update = parcel.readByte() != 0;
        this.read = parcel.readByte() != 0;
        this.delete = parcel.readByte() != 0;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getMenuDescription() {
        return this.menuDescription;
    }

    public String getMenuId() {
        return this.menuId;
    }

    public String getMenuTitle() {
        return this.menuTitle;
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

    public void setMenuDescription(String str) {
        this.menuDescription = str;
    }

    public void setMenuId(String str) {
        this.menuId = str;
    }

    public void setMenuTitle(String str) {
        this.menuTitle = str;
    }

    public void setRead(boolean z) {
        this.read = z;
    }

    public void setUpdate(boolean z) {
        this.update = z;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.menuId);
        parcel.writeString(this.menuTitle);
        parcel.writeString(this.menuDescription);
    }
}
