package com.p001yd.electricecollector;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

/* loaded from: classes6.dex */
public class EnterPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    /* renamed from: B */
    ArrayList<String> f602B;

    /* renamed from: D */
    Vibrator f603D;
    Button login;
    private EditText mPasswordField;
    String password;
    EditText txtPass;

    /* renamed from: B0 */
    private void m207B0() {
        View findViewById;
        ((TextView) findViewById(C1018R.id.tvp1)).setText("");
        ((TextView) findViewById(C1018R.id.tvp2)).setText("");
        ((TextView) findViewById(C1018R.id.tvp3)).setText("");
        ((TextView) findViewById(C1018R.id.tvp4)).setText("");
        ((TextView) findViewById(C1018R.id.tvp1)).setBackgroundDrawable(getResources().getDrawable(C1018R.drawable.bg_loginpassword_empty));
        ((TextView) findViewById(C1018R.id.tvp2)).setBackgroundDrawable(getResources().getDrawable(C1018R.drawable.bg_loginpassword_empty));
        ((TextView) findViewById(C1018R.id.tvp3)).setBackgroundDrawable(getResources().getDrawable(C1018R.drawable.bg_loginpassword_empty));
        ((TextView) findViewById(C1018R.id.tvp4)).setBackgroundDrawable(getResources().getDrawable(C1018R.drawable.bg_loginpassword_empty));
        if (this.f602B.size() >= 4) {
            ((TextView) findViewById(C1018R.id.tvp1)).setBackgroundDrawable(getResources().getDrawable(C1018R.drawable.bg_loginpassword_fill));
            ((TextView) findViewById(C1018R.id.tvp2)).setBackgroundDrawable(getResources().getDrawable(C1018R.drawable.bg_loginpassword_fill));
            ((TextView) findViewById(C1018R.id.tvp3)).setBackgroundDrawable(getResources().getDrawable(C1018R.drawable.bg_loginpassword_fill));
            findViewById = findViewById(C1018R.id.tvp4);
        } else if (this.f602B.size() >= 3) {
            ((TextView) findViewById(C1018R.id.tvp1)).setBackgroundDrawable(getResources().getDrawable(C1018R.drawable.bg_loginpassword_fill));
            ((TextView) findViewById(C1018R.id.tvp2)).setBackgroundDrawable(getResources().getDrawable(C1018R.drawable.bg_loginpassword_fill));
            findViewById = findViewById(C1018R.id.tvp3);
        } else if (this.f602B.size() >= 2) {
            ((TextView) findViewById(C1018R.id.tvp1)).setBackgroundDrawable(getResources().getDrawable(C1018R.drawable.bg_loginpassword_fill));
            findViewById = findViewById(C1018R.id.tvp2);
        } else if (this.f602B.size() < 1) {
            return;
        } else {
            findViewById = findViewById(C1018R.id.tvp1);
        }
        ((TextView) findViewById).setBackgroundDrawable(getResources().getDrawable(C1018R.drawable.bg_loginpassword_fill));
    }

    private void ClearView() {
        try {
            if (this.f602B.size() > 0) {
                this.f602B.remove(r0.size() - 1);
                m207B0();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillView(String str) {
        try {
            if (this.f602B.size() < 4) {
                this.f602B.add(str);
                m207B0();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initViews() {
        this.mPasswordField = (EditText) m208$(C1018R.id.txtPassWord);
        m208$(C1018R.id.t9_key_0).setOnClickListener(this);
        m208$(C1018R.id.t9_key_1).setOnClickListener(this);
        m208$(C1018R.id.t9_key_2).setOnClickListener(this);
        m208$(C1018R.id.t9_key_3).setOnClickListener(this);
        m208$(C1018R.id.t9_key_4).setOnClickListener(this);
        m208$(C1018R.id.t9_key_5).setOnClickListener(this);
        m208$(C1018R.id.t9_key_6).setOnClickListener(this);
        m208$(C1018R.id.t9_key_7).setOnClickListener(this);
        m208$(C1018R.id.t9_key_8).setOnClickListener(this);
        m208$(C1018R.id.t9_key_9).setOnClickListener(this);
        m208$(C1018R.id.btn_delete).setOnClickListener(this);
        m208$(C1018R.id.btn_ok).setOnClickListener(this);
        m208$(C1018R.id.tvp1).setOnClickListener(this);
        m208$(C1018R.id.tvp2).setOnClickListener(this);
        m208$(C1018R.id.tvp3).setOnClickListener(this);
        m208$(C1018R.id.tvp4).setOnClickListener(this);
    }

    private void runProgress() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("يرجى الانتظار...");
        progressDialog.show();
    }

    /* renamed from: $ */
    protected <T extends View> T m208$(int i) {
        return (T) super.findViewById(i);
    }

    public String getInputText() {
        return this.mPasswordField.getText().toString();
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), (Class<?>) Preferences.class);
        intent.setFlags(268435456);
        startActivity(intent);
        super.onBackPressed();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view.getTag() == null || !"number_button".equals(view.getTag())) {
            switch (view.getId()) {
                case C1018R.id.btn_delete /* 2131361916 */:
                    ClearView();
                    return;
                case C1018R.id.btn_login /* 2131361917 */:
                default:
                    return;
                case C1018R.id.btn_ok /* 2131361918 */:
                    this.password = TAPreferences.getLoginUserPassword(this);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < this.f602B.size(); i++) {
                        sb.append(this.f602B.get(i));
                    }
                    if (sb.length() == 4 && this.password.equalsIgnoreCase(sb.toString())) {
                        finish();
                        return;
                    } else {
                        Toast.makeText(this, "كلمة المرور غير صحيحة", 0).show();
                        return;
                    }
            }
        }
        this.mPasswordField.append(((TextView) view).getText());
        fillView(((TextView) view).getText().toString());
        if (this.f602B.size() >= 4) {
            this.password = TAPreferences.getLoginUserPassword(this);
            StringBuilder sb2 = new StringBuilder();
            for (int i2 = 0; i2 < this.f602B.size(); i2++) {
                sb2.append(this.f602B.get(i2));
            }
            if (sb2.length() == 4 && this.password.equalsIgnoreCase(sb2.toString())) {
                finish();
            } else {
                Toast.makeText(this, "كلمة المرور غير صحيحة", 0).show();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C1018R.layout.activity_input_password);
        this.f603D = (Vibrator) getSystemService("vibrator");
        ImageView imageView = (ImageView) findViewById(C1018R.id.imageView);
        this.f602B = new ArrayList<>();
        String companyLogo = TAPreferences.getCompanyLogo(this);
        if (companyLogo != null) {
            imageView.setImageURI(Uri.parse(companyLogo));
        }
        initViews();
        this.txtPass = (EditText) findViewById(C1018R.id.txtPassWord);
        this.login = (Button) findViewById(C1018R.id.btnLogin);
    }
}
