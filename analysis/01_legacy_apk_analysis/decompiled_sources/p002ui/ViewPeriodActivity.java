package com.p001yd.electricecollector.p002ui;

import android.R;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import com.p001yd.electricecollector.C1018R;
import com.p001yd.electricecollector.Utils;
import com.p001yd.electricecollector.ViewPeriod;
import java.util.ArrayList;
import java.util.Calendar;

/* loaded from: classes12.dex */
public class ViewPeriodActivity extends AppCompatActivity {
    private static final Integer[] periodValues = {Integer.valueOf(C1018R.string.txtCustom), Integer.valueOf(C1018R.string.txtToday), Integer.valueOf(C1018R.string.txtYesterday), Integer.valueOf(C1018R.string.txtThisWeek), Integer.valueOf(C1018R.string.txtThisMonth)};
    Button btnCancel;
    Button btnEndDate;
    Button btnOk;
    Button btnStartDate;
    Spinner spnPeriod;
    public ViewPeriod viewPeriod;

    /* JADX INFO: Access modifiers changed from: private */
    public void setPeriod() {
        this.btnStartDate.setEnabled(this.viewPeriod.getPeriodType() == 0);
        this.btnEndDate.setEnabled(this.viewPeriod.getPeriodType() == 0);
        this.btnStartDate.setText(Utils.getShortDateStr((FragmentActivity) this, this.viewPeriod.getStartDate()));
        this.btnEndDate.setText(Utils.getShortDateStr((FragmentActivity) this, this.viewPeriod.getEndDate()));
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.viewPeriod = (ViewPeriod) getIntent().getExtras().get("VIEW_PERIOD");
        if (this.viewPeriod == null) {
            this.viewPeriod = new ViewPeriod(this);
        }
        setContentView(C1018R.layout.view_period);
        this.spnPeriod = (Spinner) findViewById(C1018R.id.spnPeriod);
        ArrayList arrayList = new ArrayList();
        for (Integer num : periodValues) {
            arrayList.add(getString(num.intValue()));
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        this.spnPeriod.setAdapter((SpinnerAdapter) arrayAdapter);
        this.btnStartDate = (Button) findViewById(C1018R.id.tvStartDate);
        this.btnEndDate = (Button) findViewById(C1018R.id.btnEndDate);
        this.spnPeriod.setSelection(this.viewPeriod.getPeriodType());
        setPeriod();
        this.btnStartDate.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.ViewPeriodActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(ViewPeriodActivity.this.viewPeriod.startDate);
                new DatePickerDialog(ViewPeriodActivity.this, new DatePickerDialog.OnDateSetListener() { // from class: com.yd.electricecollector.ui.ViewPeriodActivity.1.1
                    @Override // android.app.DatePickerDialog.OnDateSetListener
                    public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
                        Calendar calendar2 = Calendar.getInstance();
                        calendar2.set(i, i2, i3);
                        ViewPeriodActivity.this.viewPeriod.setStartDate(calendar2.getTime());
                        ViewPeriodActivity.this.btnStartDate.setText(Utils.getShortDateStr((FragmentActivity) ViewPeriodActivity.this, ViewPeriodActivity.this.viewPeriod.startDate));
                    }
                }, calendar.get(1), calendar.get(2), calendar.get(5)).show();
            }
        });
        this.btnEndDate.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.ViewPeriodActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(ViewPeriodActivity.this.viewPeriod.endDate);
                new DatePickerDialog(ViewPeriodActivity.this, new DatePickerDialog.OnDateSetListener() { // from class: com.yd.electricecollector.ui.ViewPeriodActivity.2.1
                    @Override // android.app.DatePickerDialog.OnDateSetListener
                    public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
                        Calendar calendar2 = Calendar.getInstance();
                        calendar2.set(i, i2, i3);
                        ViewPeriodActivity.this.viewPeriod.setEndDate(calendar2.getTime());
                        ViewPeriodActivity.this.btnEndDate.setText(Utils.getShortDateStr((FragmentActivity) ViewPeriodActivity.this, ViewPeriodActivity.this.viewPeriod.endDate));
                    }
                }, calendar.get(1), calendar.get(2), calendar.get(5)).show();
            }
        });
        this.spnPeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.yd.electricecollector.ui.ViewPeriodActivity.3
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                ViewPeriodActivity.this.viewPeriod.setPeriodType(i);
                ViewPeriodActivity.this.setPeriod();
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        this.btnOk = (Button) findViewById(C1018R.id.btnOk);
        this.btnCancel = (Button) findViewById(C1018R.id.btnCancel);
        this.btnOk.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.ViewPeriodActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Intent intent = new Intent(ViewPeriodActivity.this.getBaseContext(), (Class<?>) ViewPeriodActivity.class);
                ViewPeriod viewPeriod = ViewPeriodActivity.this.viewPeriod;
                Bundle bundle2 = new Bundle();
                bundle2.putSerializable("VIEW_PERIOD", viewPeriod);
                intent.putExtras(bundle2);
                ViewPeriodActivity.this.setResult(-1, intent);
                ViewPeriodActivity.this.finish();
            }
        });
        this.btnCancel.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.ViewPeriodActivity.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ViewPeriodActivity.this.setResult(0, new Intent(ViewPeriodActivity.this.getBaseContext(), (Class<?>) ViewPeriodActivity.class));
                ViewPeriodActivity.this.finish();
            }
        });
    }
}
