package com.p001yd.electricecollector.entities;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: classes14.dex */
public class Server implements Parcelable {
    public static final Parcelable.Creator<Server> CREATOR = new Parcelable.Creator<Server>() { // from class: com.yd.electricecollector.entities.Server.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Server createFromParcel(Parcel parcel) {
            return new Server(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Server[] newArray(int i) {
            return new Server[i];
        }
    };
    private String nameServer;
    private String serverId;

    public Server() {
    }

    protected Server(Parcel parcel) {
        this.serverId = parcel.readString();
        this.nameServer = parcel.readString();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getNamaServer() {
        return this.nameServer;
    }

    public String getServerId() {
        return this.serverId;
    }

    public void setNamaServer(String str) {
        this.nameServer = str;
    }

    public void setServerId(String str) {
        this.serverId = str;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.serverId);
        parcel.writeString(this.nameServer);
    }
}
