package com.p001yd.electricecollector.entities;

import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import com.squareup.moshi.Json;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes14.dex */
public class Users implements Parcelable {
    public static final Parcelable.Creator<Users> CREATOR = new Parcelable.Creator<Users>() { // from class: com.yd.electricecollector.entities.Users.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Users createFromParcel(Parcel parcel) {
            return new Users(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Users[] newArray(int i) {
            return new Users[i];
        }
    };

    /* renamed from: DE */
    @Json(name = "DE")
    int f670DE;

    /* renamed from: ED */
    @Json(name = "ED")
    int f671ED;

    @Json(name = "error_msg")
    String Error_msg;

    @Json(name = "error_no")
    int Error_no;

    @Json(name = "NAME_U")
    String NAME_U;

    @Json(name = "NOA")
    int NOA;

    @Json(name = "NOU")
    int NOU;

    @Json(name = "PASS")
    String PASS;

    @Json(name = "REP")
    int REP;

    @Json(name = "SYS")
    int SYS;

    @Json(name = "S_K")
    int S_K;

    @Json(name = "S_S")
    int S_S;

    @Json(name = "access_token")
    String access_token;

    @Json(name = "date_server")
    String date_server;
    private List<HakAccess> hakAccess;

    public Users() {
        this.hakAccess = null;
    }

    public Users(SharedPreferences sharedPreferences) {
        this.hakAccess = null;
        setDE(sharedPreferences.getInt("DE", 0));
        setED(sharedPreferences.getInt("ED", 0));
        setS_K(sharedPreferences.getInt("S_K", 0));
        setS_S(sharedPreferences.getInt("S_S", 0));
        setSYS(sharedPreferences.getInt("SYS", 0));
        setREP(sharedPreferences.getInt("REP", 0));
        setname(sharedPreferences.getString("user_name", ""));
        setPASS(sharedPreferences.getString("user_password", ""));
        setNOU(sharedPreferences.getInt("NOU", 0));
        setNOA(sharedPreferences.getInt("NOA", 0));
        setDateServer(sharedPreferences.getString("date_server", "1980/01/01"));
        setAccessToken(sharedPreferences.getString("access_token", ""));
        ArrayList arrayList = new ArrayList();
        HakAccess hakAccess = new HakAccess();
        hakAccess.setMenuName("repBondsReciept");
        hakAccess.setUpdate(getED() > 0 || getSYS() > 0);
        hakAccess.setDelete(getDE() > 0 || getSYS() > 0);
        hakAccess.setRead(getS_K() > 0 || getSYS() > 0);
        arrayList.add(hakAccess);
        HakAccess hakAccess2 = new HakAccess();
        hakAccess2.setMenuName("repBondsPayment");
        hakAccess2.setUpdate(getED() > 0 || getSYS() > 0);
        hakAccess2.setDelete(getDE() > 0 || getSYS() > 0);
        hakAccess2.setRead(getS_S() > 0 || getSYS() > 0);
        arrayList.add(hakAccess2);
        HakAccess hakAccess3 = new HakAccess();
        hakAccess3.setMenuName("repBalanceHeader");
        hakAccess3.setUpdate(getED() > 0 || getSYS() > 0);
        hakAccess3.setDelete(getDE() > 0 || getSYS() > 0);
        hakAccess3.setRead(getREP() > 0 || getS_K() > 0 || getSYS() > 0);
        arrayList.add(hakAccess3);
        HakAccess hakAccess4 = new HakAccess();
        hakAccess4.setMenuName("repBalanceDetails");
        hakAccess4.setUpdate(getED() > 0 || getSYS() > 0);
        hakAccess4.setDelete(getDE() > 0 || getSYS() > 0);
        hakAccess4.setRead(getREP() > 0 || getS_K() > 0 || getSYS() > 0);
        arrayList.add(hakAccess4);
        HakAccess hakAccess5 = new HakAccess();
        hakAccess5.setMenuName("repCollectorMony");
        hakAccess5.setUpdate(getED() > 0 || getSYS() > 0);
        hakAccess5.setDelete(getDE() > 0 || getSYS() > 0);
        hakAccess5.setRead(getSYS() > 0);
        arrayList.add(hakAccess5);
        HakAccess hakAccess6 = new HakAccess();
        hakAccess6.setMenuName("repListReading");
        hakAccess6.setUpdate(getED() > 0 || getSYS() > 0);
        hakAccess6.setDelete(getDE() > 0 || getSYS() > 0);
        hakAccess6.setRead(getSYS() > 0);
        arrayList.add(hakAccess6);
        HakAccess hakAccess7 = new HakAccess();
        hakAccess7.setMenuName("repBoxMoves");
        hakAccess7.setUpdate(getED() > 0 || getSYS() > 0);
        hakAccess7.setDelete(getDE() > 0 || getSYS() > 0);
        hakAccess7.setRead(getSYS() > 0);
        arrayList.add(hakAccess7);
        HakAccess hakAccess8 = new HakAccess();
        hakAccess8.setMenuName("repExpenses");
        hakAccess8.setUpdate(getED() > 0 || getSYS() > 0);
        hakAccess8.setDelete(getDE() > 0 || getSYS() > 0);
        hakAccess8.setRead(getSYS() > 0);
        arrayList.add(hakAccess8);
        setHakAkses(arrayList);
    }

    protected Users(Parcel parcel) {
        this.hakAccess = null;
        this.f670DE = parcel.readInt();
        this.f671ED = parcel.readInt();
        this.NAME_U = parcel.readString();
        this.NOA = parcel.readInt();
        this.NOU = parcel.readInt();
        this.PASS = parcel.readString();
        this.REP = parcel.readInt();
        this.SYS = parcel.readInt();
        this.S_K = parcel.readInt();
        this.S_S = parcel.readInt();
        this.Error_no = parcel.readInt();
        this.access_token = parcel.readString();
        this.Error_msg = parcel.readString();
        this.date_server = parcel.readString();
        this.hakAccess = parcel.createTypedArrayList(HakAccess.CREATOR);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getAccessToken() {
        return this.access_token;
    }

    public int getDE() {
        return this.f670DE;
    }

    public String getDateServer() {
        return this.date_server;
    }

    public int getED() {
        return this.f671ED;
    }

    public String getErrorMsg() {
        return this.Error_msg;
    }

    public int getErrorNo() {
        return this.Error_no;
    }

    public List<HakAccess> getHakAkses() {
        return this.hakAccess;
    }

    public int getNOA() {
        return this.NOA;
    }

    public int getNou() {
        return this.NOU;
    }

    public String getPASS() {
        return this.PASS;
    }

    public int getREP() {
        return this.REP;
    }

    public int getSYS() {
        return this.SYS;
    }

    public int getS_K() {
        return this.S_K;
    }

    public int getS_S() {
        return this.S_S;
    }

    public String getname() {
        return this.NAME_U;
    }

    public void setAccessToken(String str) {
        this.access_token = str;
    }

    public void setDE(int i) {
        this.f670DE = i;
    }

    public void setDateServer(String str) {
        this.date_server = str;
    }

    public void setED(int i) {
        this.f671ED = i;
    }

    public void setErrorMsg(String str) {
        this.Error_msg = str;
    }

    public void setErrorNo(int i) {
        this.Error_no = i;
    }

    public void setHakAkses(List<HakAccess> list) {
        this.hakAccess = list;
    }

    public void setNOA(int i) {
        this.NOA = i;
    }

    public void setNOU(int i) {
        this.NOU = i;
    }

    public void setPASS(String str) {
        this.PASS = str;
    }

    public void setREP(int i) {
        this.REP = i;
    }

    public void setSYS(int i) {
        this.SYS = i;
    }

    public void setS_K(int i) {
        this.S_K = i;
    }

    public void setS_S(int i) {
        this.S_S = i;
    }

    public void setname(String str) {
        this.NAME_U = str;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.f670DE);
        parcel.writeInt(this.f671ED);
        parcel.writeString(this.NAME_U);
        parcel.writeInt(this.NOA);
        parcel.writeInt(this.NOU);
        parcel.writeString(this.PASS);
        parcel.writeInt(this.REP);
        parcel.writeInt(this.SYS);
        parcel.writeInt(this.S_K);
        parcel.writeInt(this.S_S);
        parcel.writeString(this.access_token);
        parcel.writeString(this.Error_msg);
        parcel.writeInt(this.Error_no);
        parcel.writeTypedList(this.hakAccess);
        parcel.writeString(this.date_server);
    }
}
