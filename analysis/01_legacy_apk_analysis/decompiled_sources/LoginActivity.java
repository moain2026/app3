package com.p001yd.electricecollector;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.p001yd.electricecollector.common.AppConfig;
import com.p001yd.electricecollector.entities.AccessToken;
import com.p001yd.electricecollector.entities.AuthData;
import com.p001yd.electricecollector.entities.CompanyInfoResult;
import com.p001yd.electricecollector.entities.Users;
import com.p001yd.electricecollector.network.ApiService;
import com.p001yd.electricecollector.network.RetrofitBuilder;
import com.p001yd.electricecollector.p002ui.BaseView;
import com.p001yd.electricecollector.p002ui.ListReadingActivity;
import com.p001yd.electricecollector.p002ui.ViewSettingActivity;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import cz.msebera.android.httpclient.Header;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* loaded from: classes6.dex */
public class LoginActivity extends AppCompatActivity implements BaseView<Users> {
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private String _appId;
    private String _baseUrl;
    private PreferencesManager _prefManager;
    private LoginPresenter _presenter;
    AppConfig appConfig;
    Call<AccessToken> call;
    TextView go_to_register;
    ProgressBar loadingProgressBar;
    TextInputEditText passwordEditText;
    ApiService service;
    TokenManager tokenManager;
    TextInputEditText urlServerText;
    TextInputEditText usernameEditText;
    public static String IS_LOGOUT = "is_logout";
    public static String SERVER_ID = "server_id";
    public static String SERVER_NAME = "server_name";
    public static String USER_NAME = "user_name";
    public static String USER_PASSWORD = "user_password";
    private final String TAG = getClass().getSimpleName();
    AsyncHttpClient clientj = new AsyncHttpClient();
    boolean isPermitted = false;

    private void alertView() {
        new AlertDialog.Builder(this, 2132017700).setTitle("Permission Denied").setInverseBackgroundForced(true).setMessage("Without those permission the app is unable to save your profile. App needs to save profile image in your external storage and also need to get profile image from camera or external storage.Are you sure you want to deny this permission?").setNegativeButton("I'M SURE", new DialogInterface.OnClickListener() { // from class: com.yd.electricecollector.LoginActivity.8
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setPositiveButton("RE-TRY", new DialogInterface.OnClickListener() { // from class: com.yd.electricecollector.LoginActivity.7
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                LoginActivity.this.checkRunTimePermission();
            }
        }).show();
    }

    private boolean checkAndRequestPermissions() {
        ArrayList arrayList;
        ContextCompat.checkSelfPermission(this, "android.permission.CAMERA");
        int checkSelfPermission = ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE");
        ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION");
        ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION");
        int checkSelfPermission2 = ContextCompat.checkSelfPermission(this, "android.permission.READ_CONTACTS");
        int checkSelfPermission3 = ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE");
        int checkSelfPermission4 = ContextCompat.checkSelfPermission(this, "android.permission.BLUETOOTH");
        int checkSelfPermission5 = ContextCompat.checkSelfPermission(this, "android.permission.BLUETOOTH_ADMIN");
        int checkSelfPermission6 = ContextCompat.checkSelfPermission(this, "android.permission.SEND_SMS");
        int checkSelfPermission7 = ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION");
        ArrayList arrayList2 = new ArrayList();
        if (checkSelfPermission3 != 0) {
            arrayList = arrayList2;
            arrayList.add("android.permission.READ_EXTERNAL_STORAGE");
        } else {
            arrayList = arrayList2;
        }
        if (checkSelfPermission != 0) {
            arrayList.add("android.permission.WRITE_EXTERNAL_STORAGE");
        }
        if (checkSelfPermission4 != 0) {
            arrayList.add("android.permission.BLUETOOTH");
        }
        if (checkSelfPermission5 != 0) {
            arrayList.add("android.permission.BLUETOOTH_ADMIN");
        }
        if (checkSelfPermission6 != 0) {
            arrayList.add("android.permission.SEND_SMS");
        }
        if (checkSelfPermission7 != 0) {
            arrayList.add("android.permission.ACCESS_COARSE_LOCATION");
        }
        if (checkSelfPermission2 != 0) {
            arrayList.add("android.permission.READ_CONTACTS");
        }
        if (arrayList.isEmpty()) {
            return true;
        }
        ActivityCompat.requestPermissions(this, (String[]) arrayList.toArray(new String[arrayList.size()]), 1);
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkRunTimePermission() {
        requestPermissions(new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION", "android.permission.READ_CONTACTS", "android.permission.READ_EXTERNAL_STORAGE"}, 11111);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean checkValidation() {
        boolean z = Validation.hasText(this.usernameEditText);
        if (!Validation.hasText(this.passwordEditText)) {
            z = false;
        }
        if (Validation.hasText(this.urlServerText)) {
            return z;
        }
        return false;
    }

    private AlertDialog.Builder msgDialogConfirm(String str, Context context) {
        return new AlertDialog.Builder(context).setTitle("تأكيد ").setMessage(str).setIcon(C1018R.drawable.info).setCancelable(false).setNegativeButton("نعم", new DialogInterface.OnClickListener() { // from class: com.yd.electricecollector.LoginActivity.4
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).setPositiveButton("لا", new DialogInterface.OnClickListener() { // from class: com.yd.electricecollector.LoginActivity.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).setView(getLayoutInflater().inflate(C1018R.layout.cusrom_dialog, (ViewGroup) null));
    }

    private void showLoginFailed(Integer num) {
        Toast.makeText(getApplicationContext(), num.intValue(), 0).show();
    }

    void loadCompanyInfo() {
        TokenManager.getInstance(getSharedPreferences("prefs", 0));
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.addHeader("Authorization", "Bearer " + this.appConfig.getToken());
        asyncHttpClient.get(String.format("%s?appId=%s", new StringBuilder().append(this.appConfig.getBaseUrl()).append("GetCompanyData").toString(), this._appId), new JsonHttpResponseHandler() { // from class: com.yd.electricecollector.LoginActivity.6
            @Override // com.loopj.android.http.JsonHttpResponseHandler
            public void onFailure(int i, Header[] headerArr, Throwable th, JSONObject jSONObject) {
            }

            @Override // com.loopj.android.http.JsonHttpResponseHandler
            public void onSuccess(int i, Header[] headerArr, JSONObject jSONObject) {
                JsonAdapter adapter = new Moshi.Builder().build().adapter(CompanyInfoResult.class);
                JSONObject jSONObject2 = null;
                try {
                    jSONObject2 = jSONObject.getJSONObject("GetCompanyDataResult");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    CompanyInfoResult companyInfoResult = (CompanyInfoResult) adapter.fromJson(jSONObject2.toString());
                    if (companyInfoResult != null) {
                        LoginActivity.this._prefManager.setValue("CompanyName", companyInfoResult.getCompanyName());
                        LoginActivity.this._prefManager.setValue("Phone", companyInfoResult.getTelephone());
                        LoginActivity.this._prefManager.setValue("Address", companyInfoResult.getAddress());
                    }
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        });
    }

    void loginRetrofit() throws JSONException {
        this._baseUrl = this.appConfig.getBaseUrl();
        this.urlServerText.setText(this._prefManager.getValue("appId", "1"));
        String obj = ((Editable) Objects.requireNonNull(this.usernameEditText.getText())).toString();
        String obj2 = ((Editable) Objects.requireNonNull(this.passwordEditText.getText())).toString();
        this._appId = ((Editable) Objects.requireNonNull(this.urlServerText.getText())).toString();
        this.usernameEditText.setError(null);
        this.passwordEditText.setError(null);
        this.urlServerText.setError(null);
        AuthData authData = new AuthData();
        authData.setUserName(obj);
        authData.setPassword(obj2);
        authData.setSecurId(Utils.GetDeviceId(this));
        authData.setAppId(this._appId);
        this._presenter = new LoginPresenter(this._baseUrl, this._appId, this);
        this._presenter.auth(authData);
    }

    void loginRetrofit2() {
        String obj = this.usernameEditText.getText().toString();
        String obj2 = this.passwordEditText.getText().toString();
        this.usernameEditText.setError(null);
        this.passwordEditText.setError(null);
        HashMap hashMap = new HashMap();
        hashMap.put("username", obj);
        hashMap.put("password", obj2);
        AuthData authData = new AuthData();
        authData.setUserName(obj);
        authData.setPassword(obj2);
        this.service = (ApiService) RetrofitBuilder.createService(ApiService.class, "");
        this.service.login(hashMap).enqueue(new Callback<Users>() { // from class: com.yd.electricecollector.LoginActivity.5
            @Override // retrofit2.Callback
            public void onFailure(Call<Users> call, Throwable th) {
                Log.w(LoginActivity.this.TAG, "onFailure: " + th.getMessage());
            }

            @Override // retrofit2.Callback
            public void onResponse(Call<Users> call, Response<Users> response) {
                Log.w(LoginActivity.this.TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    LoginActivity.this.startActivity(new Intent(LoginActivity.this, (Class<?>) ListReadingActivity.class));
                    LoginActivity.this.finish();
                    return;
                }
                if (response.code() == 422) {
                    Toast.makeText(LoginActivity.this, "errrrrrrrrrrrrrrr", 1).show();
                }
                if (response.code() == 401) {
                    Toast.makeText(LoginActivity.this, Utils.converErrors(response.errorBody()).getMessage(), 1).show();
                }
            }
        });
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C1018R.layout.activity_login2);
        checkAndRequestPermissions();
        AppCompatDelegate.setDefaultNightMode(1);
        this.usernameEditText = (TextInputEditText) findViewById(C1018R.id.username);
        this.passwordEditText = (TextInputEditText) findViewById(C1018R.id.password);
        this.urlServerText = (TextInputEditText) findViewById(C1018R.id.nameServerText);
        this.go_to_register = (TextView) findViewById(C1018R.id.go_to_register);
        this.urlServerText.setVisibility(8);
        this.tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", 0));
        this._prefManager = new PreferencesManager(this);
        this.appConfig = AppConfig.getInstance();
        this._baseUrl = TAPreferences.getHostingIP(this);
        this.go_to_register.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.LoginActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                LoginActivity.this.startActivity(new Intent(LoginActivity.this, (Class<?>) ViewSettingActivity.class));
            }
        });
        ((TextInputLayout) findViewById(C1018R.id.til_serverl)).setVisibility(8);
        this.urlServerText.setText(this._prefManager.getValue("appid", "1"));
        Button button = (Button) findViewById(C1018R.id.btn_login);
        this.loadingProgressBar = (ProgressBar) findViewById(C1018R.id.loader);
        button.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.LoginActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                LoginActivity.this.usernameEditText.getText().toString();
                LoginActivity.this.passwordEditText.getText().toString();
                LoginActivity.this.usernameEditText.setError(null);
                LoginActivity.this.passwordEditText.setError(null);
                if (LoginActivity.this.checkValidation()) {
                    try {
                        LoginActivity.this.loadingProgressBar.setVisibility(0);
                        LoginActivity.this.loginRetrofit();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        LoginActivity.this.loadingProgressBar.setVisibility(8);
                    }
                }
            }
        });
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onFailed(Users users) {
        this.loadingProgressBar.setVisibility(8);
        Toast.makeText(this, users.getErrorMsg(), 1).show();
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onLoadDataFailure() {
        this.loadingProgressBar.setVisibility(8);
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onLoadDataSucceed(List<Users> list) {
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i == 11111) {
            for (int i2 = 0; i2 < iArr.length; i2++) {
                String str = strArr[i2];
                this.isPermitted = iArr[i2] == 0;
                if (iArr[i2] == -1 && shouldShowRequestPermissionRationale(str)) {
                    alertView();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        this._baseUrl = this.appConfig.getBaseUrl();
        this.urlServerText.setText(this._prefManager.getValue("appId", "1"));
        super.onResume();
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onSucceed(Users users) {
        this.loadingProgressBar.setVisibility(8);
        AppConfig appConfig = AppConfig.getInstance();
        appConfig.setAppId(this._appId);
        appConfig.setToken(users.getAccessToken());
        appConfig.setUser(users);
        RetrofitBuilder.BASE_URL = this._baseUrl;
        appConfig.setListHakAccess(users.getHakAkses());
        this._prefManager.setValue("appId", this._appId);
        this._prefManager.setValue("access_token", users.getAccessToken());
        AccessToken accessToken = new AccessToken();
        accessToken.setAccessToken(users.getAccessToken());
        this.tokenManager.saveToken(accessToken);
        this._prefManager.setValue("date_server", "1980-01-01");
        this._prefManager.setValue(IS_LOGOUT, false);
        this._prefManager.setValue(USER_NAME, ((Editable) Objects.requireNonNull(this.usernameEditText.getText())).toString());
        this._prefManager.setValue(USER_PASSWORD, ((Editable) Objects.requireNonNull(this.passwordEditText.getText())).toString());
        this._prefManager.setValue("DE", users.getDE());
        this._prefManager.setValue("ED", users.getED());
        this._prefManager.setValue("REP", users.getREP());
        this._prefManager.setValue("S_K", users.getS_K());
        this._prefManager.setValue("S_S", users.getS_S());
        this._prefManager.setValue("SYS", users.getSYS());
        this._prefManager.setValue("NOA", users.getNOA());
        this._prefManager.setValue("NOU", users.getNou());
        loadCompanyInfo();
        startActivity(new Intent(this, (Class<?>) MainActivity.class));
        finish();
    }
}
