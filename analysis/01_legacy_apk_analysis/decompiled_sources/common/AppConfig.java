package com.p001yd.electricecollector.common;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatDelegate;
import com.p001yd.electricecollector.SplashScreenActivity;
import com.p001yd.electricecollector.Utils;
import com.p001yd.electricecollector.ViewPeriod;
import com.p001yd.electricecollector.entities.HakAccess;
import com.p001yd.electricecollector.entities.Users;
import com.p001yd.electricecollector.printer.driver.AbstractPrinterDriver;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes15.dex */
public class AppConfig extends Application {
    public static AbstractPrinterDriver printerDriver;
    private static AppConfig sInstance;
    private String appId;
    private String baseUrl;
    private boolean isLogOut;
    private List<HakAccess> listHakAccess = new ArrayList();
    private int pageSize;
    SharedPreferences prefs;
    private String secureId;
    private Socket socket;
    private String token;
    private Users user;
    private int versionNumber;
    private ViewPeriod viewPeriod;

    public static AppConfig getInstance() {
        return sInstance;
    }

    public String getAppId() {
        return this.appId;
    }

    public String getBaseUrl() {
        return "http://" + this.baseUrl + ":3000/electric/";
    }

    public List<HakAccess> getListHakAccess() {
        return this.listHakAccess;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public String getSecurId() {
        return this.secureId;
    }

    public Socket getSocket() {
        return this.socket;
    }

    public String getToken() {
        return this.token;
    }

    public Users getUser() {
        return this.user;
    }

    public int getVersionNumber() {
        return this.versionNumber;
    }

    public ViewPeriod getViewPeriod() {
        if (this.viewPeriod == null) {
            this.viewPeriod = new ViewPeriod(getApplicationContext());
        }
        return this.viewPeriod;
    }

    protected void initializeInstance() {
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
        this.user = new Users(this.prefs);
        setSecurId(Utils.GetDeviceId(this));
        setAppId(this.prefs.getString("appId", "1"));
        setBaseUrl(this.prefs.getString("HostingIP", "192.168.0.100"));
        setListHakAccess(this.user.getHakAkses());
    }

    public boolean isLogOut() {
        return this.isLogOut;
    }

    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(1);
        sInstance = this;
        sInstance.initializeInstance();
    }

    @Override // android.app.Application
    public void onTerminate() {
        if (SplashScreenActivity.mPrinter != null) {
            SplashScreenActivity.mPrinter = null;
            if (SplashScreenActivity.mBthConnector != null) {
                try {
                    SplashScreenActivity.mBthConnector.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        super.onTerminate();
    }

    public void setAppId(String str) {
        this.appId = str;
    }

    public void setBaseUrl(String str) {
        this.baseUrl = str;
    }

    public void setListHakAccess(List<HakAccess> list) {
        this.listHakAccess = list;
    }

    public void setLogOut(boolean z) {
        this.isLogOut = z;
    }

    public void setPageSize(int i) {
        this.pageSize = i;
    }

    public void setSecurId(String str) {
        this.secureId = str;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void setToken(String str) {
        this.token = str;
    }

    public void setUser(Users users) {
        this.user = users;
    }

    public void setVersionNumber(int i) {
        this.versionNumber = i;
    }

    public void setViewPeriod(ViewPeriod viewPeriod) {
        this.viewPeriod = viewPeriod;
    }
}
