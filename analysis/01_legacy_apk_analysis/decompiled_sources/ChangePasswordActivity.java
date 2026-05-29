package com.p001yd.electricecollector;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.p001yd.electricecollector.common.AppConfig;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes6.dex */
public class ChangePasswordActivity extends AppCompatActivity {
    private String _appId;
    private String _baseUrl;
    AppConfig appConfig;
    TextInputEditText etConfirmPassword;
    TextInputEditText etNewPassword;
    TextInputEditText etOldPassword;
    ProgressBar loadingProgressBar;
    TokenManager tokenManager;

    /* JADX INFO: Access modifiers changed from: private */
    public void ChangePasswordTask() {
        TokenManager.getInstance(getSharedPreferences("prefs", 0));
        final String obj = this.etNewPassword.getText().toString();
        String obj2 = this.etOldPassword.getText().toString();
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("username", this.appConfig.getUser().getname());
            jSONObject.put("password", obj2);
            jSONObject.put("newpassword", obj);
            jSONObject.put("uId", this.appConfig.getUser().getNou());
            jSONObject.put("appId", this.appConfig.getAppId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        StringEntity stringEntity = new StringEntity(jSONObject.toString(), "UTF-8");
        asyncHttpClient.addHeader("Authorization", "Bearer " + this.appConfig.getToken());
        asyncHttpClient.post(null, new StringBuilder().append(this.appConfig.getBaseUrl()).append("ReSetPassword").toString(), stringEntity, RequestParams.APPLICATION_JSON, new JsonHttpResponseHandler() { // from class: com.yd.electricecollector.ChangePasswordActivity.2
            @Override // com.loopj.android.http.JsonHttpResponseHandler
            public void onFailure(int i, Header[] headerArr, Throwable th, JSONObject jSONObject2) {
                ChangePasswordActivity.this.loadingProgressBar.setVisibility(8);
                if (jSONObject2 == null) {
                    Toast.makeText(ChangePasswordActivity.this, "فشلة العملية ", 0).show();
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "فشلة العملية " + jSONObject2.toString(), 0).show();
                }
            }

            @Override // com.loopj.android.http.JsonHttpResponseHandler
            public void onSuccess(int i, Header[] headerArr, JSONObject jSONObject2) {
                try {
                    jSONObject2.getJSONObject("ReSetPasswordResult");
                } catch (JSONException e2) {
                    e2.printStackTrace();
                }
                Toast.makeText(ChangePasswordActivity.this, "تمت العملية بنجاح", 0).show();
                ChangePasswordActivity.this.loadingProgressBar.setVisibility(8);
                PreferencesManager preferencesManager = new PreferencesManager(ChangePasswordActivity.this);
                preferencesManager.setValue("isResetPass", true);
                preferencesManager.setValue(LoginActivity.IS_LOGOUT, true);
                ChangePasswordActivity.this.appConfig.getUser().setPASS(obj);
                ChangePasswordActivity.this.startActivity(new Intent(ChangePasswordActivity.this, (Class<?>) LoginActivity.class));
                ChangePasswordActivity.this.finish();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean validateInputs(String str, String str2, String str3) {
        if (str.isEmpty() || str2.isEmpty() || str3.isEmpty()) {
            Toast.makeText(this, "يجب تعبئة جميع الحقول", 0).show();
            return false;
        }
        if (str2.equals(str3)) {
            str2.length();
            return true;
        }
        Toast.makeText(this, "تاكيد كلمة المرور غير مطابق", 0).show();
        return false;
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C1018R.layout.activity_change_password);
        this.etNewPassword = (TextInputEditText) findViewById(C1018R.id.etNewPassword);
        this.etConfirmPassword = (TextInputEditText) findViewById(C1018R.id.etConfirmPassword);
        this.etOldPassword = (TextInputEditText) findViewById(C1018R.id.etOldPassword);
        this.tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", 0));
        this.appConfig = AppConfig.getInstance();
        Button button = (Button) findViewById(C1018R.id.btn_login);
        this.loadingProgressBar = (ProgressBar) findViewById(C1018R.id.loader);
        button.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ChangePasswordActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                String obj = ChangePasswordActivity.this.etNewPassword.getText().toString();
                String obj2 = ChangePasswordActivity.this.etConfirmPassword.getText().toString();
                if (ChangePasswordActivity.this.validateInputs(ChangePasswordActivity.this.etOldPassword.getText().toString(), obj, obj2)) {
                    ChangePasswordActivity.this.ChangePasswordTask();
                }
            }
        });
    }
}
