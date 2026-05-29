package com.p001yd.electricecollector;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.p001yd.electricecollector.permissions.PermissionSendSms;
import java.util.Iterator;

/* loaded from: classes6.dex */
public class ActivitySMS extends AppCompatActivity {
    EditText message;
    EditText phonenumber;
    Button send;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C1018R.layout.activity_sms);
        PermissionSendSms permissionSendSms = new PermissionSendSms();
        permissionSendSms.hasPermission(this);
        permissionSendSms.requestPermission(this);
        this.send = (Button) findViewById(C1018R.id.button);
        this.phonenumber = (EditText) findViewById(C1018R.id.editText);
        this.message = (EditText) findViewById(C1018R.id.editText2);
        this.send.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ActivitySMS.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                try {
                    ActivitySMS.this.openSendSmsSilent(ActivitySMS.this.phonenumber.getText().toString(), ActivitySMS.this.message.getText().toString());
                    Toast.makeText(ActivitySMS.this.getApplicationContext(), "Message Sent", 1).show();
                } catch (Exception e) {
                    Toast.makeText(ActivitySMS.this.getApplicationContext(), "Some fields is Empty", 1).show();
                }
            }
        });
    }

    public void openSendSmsSilent(String str, String str2) {
        if (str2 == null) {
            return;
        }
        PendingIntent broadcast = PendingIntent.getBroadcast(this, 0, new Intent(), 0);
        SmsManager smsManager = SmsManager.getDefault();
        if (str2.length() < 70) {
            smsManager.sendTextMessage(str, null, str2, broadcast, null);
            return;
        }
        Iterator<String> it = smsManager.divideMessage(str2).iterator();
        while (it.hasNext()) {
            smsManager.sendTextMessage(str, null, it.next(), broadcast, null);
        }
    }
}
