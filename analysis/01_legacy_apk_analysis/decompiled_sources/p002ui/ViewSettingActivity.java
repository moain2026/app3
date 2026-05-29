package com.p001yd.electricecollector.p002ui;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import com.p001yd.electricecollector.C1018R;
import com.p001yd.electricecollector.TAPreferences;
import com.p001yd.electricecollector.Utils;
import com.p001yd.electricecollector.common.AppConfig;
import java.util.ArrayList;

/* loaded from: classes12.dex */
public class ViewSettingActivity extends AppCompatActivity implements View.OnClickListener {
    private static final Integer[] periodValues = {Integer.valueOf(C1018R.string.txtCustom), Integer.valueOf(C1018R.string.txtToday), Integer.valueOf(C1018R.string.txtYesterday), Integer.valueOf(C1018R.string.txtThisWeek), Integer.valueOf(C1018R.string.txtThisMonth)};

    /* renamed from: B */
    ArrayList<String> f679B;
    AppConfig appConfig;
    Button btnCancel;
    Button btnOk;
    private Group contentViews;
    private LinearLayout contentViews1;
    EditText edtIp;
    EditText edtIp1;
    EditText edtIp2;
    EditText edtIp3;
    EditText edtIp4;
    private EditText googleSignIn;
    Button login;
    private EditText mBranchField;
    private EditText mPasswordField;
    String password;
    TextView tvserial;
    EditText txtPass;

    /* renamed from: B0 */
    private void m210B0() {
        View findViewById;
        ((TextView) findViewById(C1018R.id.tvp1)).setText("");
        ((TextView) findViewById(C1018R.id.tvp2)).setText("");
        ((TextView) findViewById(C1018R.id.tvp3)).setText("");
        ((TextView) findViewById(C1018R.id.tvp4)).setText("");
        ((TextView) findViewById(C1018R.id.tvp1)).setBackgroundDrawable(getResources().getDrawable(C1018R.drawable.bg_loginpassword_empty));
        ((TextView) findViewById(C1018R.id.tvp2)).setBackgroundDrawable(getResources().getDrawable(C1018R.drawable.bg_loginpassword_empty));
        ((TextView) findViewById(C1018R.id.tvp3)).setBackgroundDrawable(getResources().getDrawable(C1018R.drawable.bg_loginpassword_empty));
        ((TextView) findViewById(C1018R.id.tvp4)).setBackgroundDrawable(getResources().getDrawable(C1018R.drawable.bg_loginpassword_empty));
        if (this.f679B.size() >= 4) {
            ((TextView) findViewById(C1018R.id.tvp1)).setBackgroundDrawable(getResources().getDrawable(C1018R.drawable.bg_loginpassword_fill));
            ((TextView) findViewById(C1018R.id.tvp2)).setBackgroundDrawable(getResources().getDrawable(C1018R.drawable.bg_loginpassword_fill));
            ((TextView) findViewById(C1018R.id.tvp3)).setBackgroundDrawable(getResources().getDrawable(C1018R.drawable.bg_loginpassword_fill));
            findViewById = findViewById(C1018R.id.tvp4);
        } else if (this.f679B.size() >= 3) {
            ((TextView) findViewById(C1018R.id.tvp1)).setBackgroundDrawable(getResources().getDrawable(C1018R.drawable.bg_loginpassword_fill));
            ((TextView) findViewById(C1018R.id.tvp2)).setBackgroundDrawable(getResources().getDrawable(C1018R.drawable.bg_loginpassword_fill));
            findViewById = findViewById(C1018R.id.tvp3);
        } else if (this.f679B.size() >= 2) {
            ((TextView) findViewById(C1018R.id.tvp1)).setBackgroundDrawable(getResources().getDrawable(C1018R.drawable.bg_loginpassword_fill));
            findViewById = findViewById(C1018R.id.tvp2);
        } else if (this.f679B.isEmpty()) {
            return;
        } else {
            findViewById = findViewById(C1018R.id.tvp1);
        }
        ((TextView) findViewById).setBackgroundDrawable(getResources().getDrawable(C1018R.drawable.bg_loginpassword_fill));
    }

    private void ClearView() {
        try {
            if (this.f679B.isEmpty()) {
                return;
            }
            this.f679B.remove(r0.size() - 1);
            m210B0();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillView(String str) {
        try {
            if (this.f679B.size() < 4) {
                this.f679B.add(str);
                m210B0();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initViews() {
        this.mPasswordField = (EditText) m211$(C1018R.id.passWord);
        m211$(C1018R.id.t9_key_0).setOnClickListener(this);
        m211$(C1018R.id.t9_key_1).setOnClickListener(this);
        m211$(C1018R.id.t9_key_2).setOnClickListener(this);
        m211$(C1018R.id.t9_key_3).setOnClickListener(this);
        m211$(C1018R.id.t9_key_4).setOnClickListener(this);
        m211$(C1018R.id.t9_key_5).setOnClickListener(this);
        m211$(C1018R.id.t9_key_6).setOnClickListener(this);
        m211$(C1018R.id.t9_key_7).setOnClickListener(this);
        m211$(C1018R.id.t9_key_8).setOnClickListener(this);
        m211$(C1018R.id.t9_key_9).setOnClickListener(this);
        m211$(C1018R.id.btn_delete).setOnClickListener(this);
        m211$(C1018R.id.btn_ok).setOnClickListener(this);
        m211$(C1018R.id.tvp1).setOnClickListener(this);
        m211$(C1018R.id.tvp2).setOnClickListener(this);
        m211$(C1018R.id.tvp3).setOnClickListener(this);
        m211$(C1018R.id.tvp4).setOnClickListener(this);
    }

    /* renamed from: $ */
    protected <T extends View> T m211$(int i) {
        return (T) super.findViewById(i);
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.core.app.ComponentActivity, android.app.Activity, android.view.Window.Callback
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        Log.v("onClick", "onKeyDown=" + keyEvent.getAction());
        Log.v("onClick", "getKeyCode=" + keyEvent.getKeyCode());
        new KeyEvent(66, 1);
        return super.dispatchKeyEvent(keyEvent);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        Log.v("onClick", "onClick");
        if (view.getTag() != null && "number_button".equals(view.getTag())) {
            this.mPasswordField.append(((TextView) view).getText());
            String obj = ((TextView) view).getText().toString();
            Log.v("onClick", obj);
            fillView(obj);
            if (this.f679B.size() >= 4) {
                this.password = TAPreferences.getLoginUserPassword(this);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < this.f679B.size(); i++) {
                    sb.append(this.f679B.get(i));
                }
                if (sb.length() == 4 && this.password.equalsIgnoreCase(sb.toString())) {
                    this.contentViews.setVisibility(0);
                    this.contentViews1.setVisibility(8);
                } else {
                    Toast.makeText(this, "الرمزالسري غيرصحيح", 0).show();
                }
            }
        }
        switch (view.getId()) {
            case C1018R.id.btn_delete /* 2131361916 */:
                ClearView();
                return;
            case C1018R.id.btn_login /* 2131361917 */:
            default:
                return;
            case C1018R.id.btn_ok /* 2131361918 */:
                this.password = "0000";
                StringBuilder sb2 = new StringBuilder();
                for (int i2 = 0; i2 < this.f679B.size(); i2++) {
                    sb2.append(this.f679B.get(i2));
                }
                if (sb2.length() != 4 || !this.password.equalsIgnoreCase(sb2.toString())) {
                    Toast.makeText(this, "الرمزالسري غيرصحيح", 0).show();
                    return;
                } else {
                    this.contentViews.setVisibility(0);
                    this.contentViews1.setVisibility(8);
                    return;
                }
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C1018R.layout.cusrom_dialog);
        setTitle("اعدادات الاتصال");
        this.appConfig = AppConfig.getInstance();
        this.edtIp1 = (EditText) findViewById(C1018R.id.hostIp1);
        this.edtIp2 = (EditText) findViewById(C1018R.id.hostIp2);
        this.edtIp3 = (EditText) findViewById(C1018R.id.hostIp3);
        this.edtIp4 = (EditText) findViewById(C1018R.id.hostIp4);
        this.edtIp = (EditText) findViewById(C1018R.id.hostIp);
        this.edtIp1.setVisibility(8);
        this.edtIp2.setVisibility(8);
        this.edtIp3.setVisibility(8);
        this.edtIp4.setVisibility(8);
        this.f679B = new ArrayList<>();
        this.txtPass = (EditText) findViewById(C1018R.id.passWord);
        this.contentViews = (Group) findViewById(C1018R.id.content_views);
        this.contentViews1 = (LinearLayout) findViewById(C1018R.id.content_views1);
        if (Integer.parseInt(TAPreferences.getLockType(this)) == 0) {
            this.contentViews.setVisibility(0);
            this.contentViews1.setVisibility(8);
        }
        this.mPasswordField = (EditText) m211$(C1018R.id.passWord);
        this.tvserial = (TextView) findViewById(C1018R.id.serial_device);
        this.mBranchField = (EditText) findViewById(C1018R.id.branch_no);
        String hostingIP = TAPreferences.getHostingIP(this);
        this.mBranchField.setText(TAPreferences.getAppId(this));
        String[] split = hostingIP.split("\\.");
        Log.v("partIp", "lenth=" + split.length);
        if (split.length > 0) {
            this.edtIp1.setText(split[0]);
        }
        if (split.length > 1) {
            this.edtIp2.setText(split[1]);
        }
        if (split.length > 2) {
            this.edtIp3.setText(split[2]);
        }
        if (split.length > 3) {
            this.edtIp4.setText(split[3]);
        }
        this.edtIp.setText(TAPreferences.getHostingIP(this));
        initViews();
        this.tvserial.setText(Utils.GetDeviceId(this));
        this.btnOk = (Button) findViewById(C1018R.id.btnOk);
        this.btnCancel = (Button) findViewById(C1018R.id.btnCancle);
        this.btnOk.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.ViewSettingActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (ViewSettingActivity.this.edtIp1.getText().toString().isEmpty()) {
                    ViewSettingActivity.this.edtIp1.setText("0");
                }
                if (ViewSettingActivity.this.edtIp2.getText().toString().isEmpty()) {
                    ViewSettingActivity.this.edtIp2.setText("0");
                }
                if (ViewSettingActivity.this.edtIp3.getText().toString().isEmpty()) {
                    ViewSettingActivity.this.edtIp3.setText("0");
                }
                if (ViewSettingActivity.this.edtIp4.getText().toString().isEmpty()) {
                    ViewSettingActivity.this.edtIp4.setText("0");
                }
                StringBuilder sb = new StringBuilder();
                sb.append(ViewSettingActivity.this.edtIp1.getText().toString());
                sb.append(".");
                sb.append(ViewSettingActivity.this.edtIp2.getText().toString());
                sb.append(".");
                sb.append(ViewSettingActivity.this.edtIp3.getText().toString());
                sb.append(".");
                sb.append(ViewSettingActivity.this.edtIp4.getText().toString());
                TAPreferences.setHostingIP(ViewSettingActivity.this, ViewSettingActivity.this.edtIp.getText().toString());
                TAPreferences.setAppId(ViewSettingActivity.this, ViewSettingActivity.this.mBranchField.getText().toString());
                ViewSettingActivity.this.appConfig.setBaseUrl(ViewSettingActivity.this.edtIp.getText().toString());
                ViewSettingActivity.this.appConfig.setAppId(ViewSettingActivity.this.mBranchField.getText().toString());
                ViewSettingActivity.this.finish();
            }
        });
        this.btnCancel.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.ViewSettingActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ViewSettingActivity.this.finish();
            }
        });
    }

    @Override // androidx.appcompat.app.AppCompatActivity, android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        Log.v("onClick", "onKeyDown=" + i);
        return super.onKeyDown(i, keyEvent);
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        Log.v("onClick", "onKeyUp=" + i);
        return super.onKeyUp(i, keyEvent);
    }
}
