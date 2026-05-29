package com.p001yd.electricecollector;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.datecs.api.printer.Printer;
import com.datecs.api.printer.PrinterInformation;
import com.p001yd.electricecollector.common.AppConfig;
import com.p001yd.electricecollector.entities.AuthData;
import com.p001yd.electricecollector.entities.Users;
import com.p001yd.electricecollector.p002ui.BaseView;
import com.p001yd.electricecollector.printer.bluetooth.BluetoothConnector;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import org.json.JSONException;

/* loaded from: classes6.dex */
public class SplashScreenActivity extends AppCompatActivity {
    private static final int SPLASH_TIME_OUT = 1000;
    public static BluetoothConnector mBthConnector = null;
    public static Printer mPrinter = null;
    public static PrinterInformation mPrinterInfo = null;
    private String _appId;
    private String _baseUrl;
    private PreferencesManager _prefManager;
    private String _secureId;
    private final String TAG = getClass().getSimpleName();
    private Handler mHandler = new Handler();
    BaseView<Users> loginCallback = new BaseView<Users>() { // from class: com.yd.electricecollector.SplashScreenActivity.1
        @Override // com.p001yd.electricecollector.p002ui.BaseView
        public void onFailed(Users users) {
            SplashScreenActivity.this.showLoginActivity();
        }

        @Override // com.p001yd.electricecollector.p002ui.BaseView
        public void onLoadDataFailure() {
            Toast.makeText(SplashScreenActivity.this, C1018R.string.msgCantCreateConnection, 1).show();
        }

        @Override // com.p001yd.electricecollector.p002ui.BaseView
        public void onLoadDataSucceed(List<Users> list) {
        }

        @Override // com.p001yd.electricecollector.p002ui.BaseView
        public void onSucceed(Users users) {
            Log.v(SplashScreenActivity.this.TAG, "onResponse: success login");
            AppConfig appConfig = AppConfig.getInstance();
            appConfig.setToken(users.getAccessToken());
            appConfig.setUser(users);
            SplashScreenActivity.this._prefManager.setValue("DE", users.getDE());
            SplashScreenActivity.this._prefManager.setValue("ED", users.getED());
            SplashScreenActivity.this._prefManager.setValue("REP", users.getREP());
            SplashScreenActivity.this._prefManager.setValue("S_K", users.getS_K());
            SplashScreenActivity.this._prefManager.setValue("S_S", users.getS_S());
            SplashScreenActivity.this._prefManager.setValue("SYS", users.getSYS());
            SplashScreenActivity.this._prefManager.setValue("NOA", users.getNOA());
            SplashScreenActivity.this._prefManager.setValue("NOU", users.getNou());
            appConfig.setToken(users.getAccessToken());
            appConfig.setListHakAccess(users.getHakAkses());
            SplashScreenActivity.this.startActivity(new Intent(SplashScreenActivity.this, (Class<?>) MainActivity.class));
            SplashScreenActivity.this.finish();
        }
    };
    private Thread splashTread = null;

    public static void closePrinter() {
        if (mBthConnector != null) {
            try {
                mPrinter = null;
                mBthConnector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setLocale(Context context) {
        Context baseContext = getBaseContext();
        String string = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("languagePref", "ar");
        Configuration configuration = baseContext.getResources().getConfiguration();
        if (string.isEmpty()) {
            return;
        }
        Locale locale = new Locale(string);
        Locale.setDefault(locale);
        configuration.locale = locale;
        baseContext.getResources().updateConfiguration(configuration, baseContext.getResources().getDisplayMetrics());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showLoginActivity() {
        startActivity(new Intent(this, (Class<?>) LoginActivity.class));
        finish();
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        closePrinter();
        finish();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(C1018R.layout.activity_splash_screen);
        ((ImageView) findViewById(C1018R.id.slash_img)).setImageResource(C1018R.mipmap.ic_launcher_2_round);
        this._prefManager = new PreferencesManager(this);
        setLocale(getBaseContext());
        Calendar calendar = Calendar.getInstance();
        StringBuilder sb = new StringBuilder();
        sb.append("2024 - ");
        sb.append(calendar.getTime().getYear() + 1900);
        ((ImageView) findViewById(C1018R.id.slash_img)).setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.SplashScreenActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SplashScreenActivity.this.showLoginActivity();
            }
        });
        this.splashTread = new Thread() { // from class: com.yd.electricecollector.SplashScreenActivity.3
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                try {
                    synchronized (this) {
                        wait(1000L);
                    }
                    SplashScreenActivity.this.runOnUiThread(new Runnable() { // from class: com.yd.electricecollector.SplashScreenActivity.3.1
                        @Override // java.lang.Runnable
                        public void run() {
                            if (SplashScreenActivity.this._prefManager.getValue(LoginActivity.IS_LOGOUT, true)) {
                                SplashScreenActivity.this.showLoginActivity();
                                return;
                            }
                            SplashScreenActivity.this._baseUrl = "http://" + SplashScreenActivity.this._prefManager.getValue("HostingIP", "192.168.0.100") + ":3000/electric/";
                            SplashScreenActivity.this._secureId = SplashScreenActivity.this._prefManager.getValue(Utils.securrid, "");
                            String value = SplashScreenActivity.this._prefManager.getValue(LoginActivity.USER_NAME, "");
                            String value2 = SplashScreenActivity.this._prefManager.getValue(LoginActivity.USER_PASSWORD, "");
                            String value3 = SplashScreenActivity.this._prefManager.getValue("appId", "1");
                            AuthData authData = new AuthData();
                            authData.setUserName(value);
                            authData.setPassword(value2);
                            authData.setSecurId(Utils.GetDeviceId(SplashScreenActivity.this));
                            authData.setAppId(value3);
                            try {
                                if (value.length() > 0 && value2.length() > 0) {
                                    new LoginPresenter(SplashScreenActivity.this._baseUrl, SplashScreenActivity.this._secureId, SplashScreenActivity.this.loginCallback).auth(authData);
                                }
                                SplashScreenActivity.this.showLoginActivity();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (InterruptedException e) {
                }
            }
        };
        this.splashTread.start();
    }
}
