package com.p001yd.electricecollector;

import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

/* loaded from: classes6.dex */
public class AboutActivity extends AppCompatActivity {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(C1018R.layout.activity_about);
        int i = Calendar.getInstance().get(1);
        ((TextView) findViewById(C1018R.id.tvCopyRight)).setText(String.format("Copyright © %s. YDsoft", i > 2024 ? String.format("%s - %s", 2024, Integer.valueOf(i)) : String.valueOf(2024)));
    }

    @Override // android.app.Activity
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() != 0) {
            return true;
        }
        finish();
        return true;
    }
}
