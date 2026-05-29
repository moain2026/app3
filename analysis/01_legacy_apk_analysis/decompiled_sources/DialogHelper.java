package com.p001yd.electricecollector;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import androidx.appcompat.app.AlertDialog;
import com.p001yd.electricecollector.network.DialogCallback;
import java.util.Arrays;

/* loaded from: classes6.dex */
public final class DialogHelper {
    public static DatePickerDialog dialogTanggal(Context context, final DatePickerDialog.OnDateSetListener onDateSetListener, final DialogCallback dialogCallback, int i, int i2, int i3) {
        final DatePickerDialog datePickerDialog = new DatePickerDialog(context, onDateSetListener, i, i2, i3);
        datePickerDialog.setButton(-2, "نعم", new DialogInterface.OnClickListener() { // from class: com.yd.electricecollector.DialogHelper.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i4) {
                DialogCallback.this.onOk(this);
                DatePicker datePicker = datePickerDialog.getDatePicker();
                onDateSetListener.onDateSet(datePicker, datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
            }
        });
        datePickerDialog.setButton(-1, "لا", new DialogInterface.OnClickListener() { // from class: com.yd.electricecollector.DialogHelper.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i4) {
                DialogCallback.this.onCancel();
            }
        });
        return datePickerDialog;
    }

    public static AlertDialog.Builder msgDialogConfirm(String str, Context context, final DialogCallback dialogCallback) {
        return new AlertDialog.Builder(context).setTitle("تأكيد ").setMessage(str).setIcon(C1018R.drawable.info).setCancelable(false).setNegativeButton("نعم", new DialogInterface.OnClickListener() { // from class: com.yd.electricecollector.DialogHelper.4
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                DialogCallback.this.onOk(this);
            }
        }).setPositiveButton("لا", new DialogInterface.OnClickListener() { // from class: com.yd.electricecollector.DialogHelper.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                DialogCallback.this.onCancel();
            }
        });
    }

    public static AlertDialog.Builder msgDialogConfirm3(String str, Context context, final DialogCallback dialogCallback) {
        final boolean[] zArr = new boolean[2];
        Arrays.asList("طباعة", "رسالةSMS");
        Arrays.fill(zArr, true);
        View inflate = LayoutInflater.from(context).inflate(C1018R.layout.confirm_dialog, (ViewGroup) null);
        CheckBox checkBox = (CheckBox) inflate.findViewById(C1018R.id.checkBoxPrint);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.yd.electricecollector.DialogHelper.5
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                zArr[0] = z;
            }
        });
        checkBox.setChecked(true);
        CheckBox checkBox2 = (CheckBox) inflate.findViewById(C1018R.id.checkBoxSMS);
        checkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.yd.electricecollector.DialogHelper.6
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                zArr[1] = z;
            }
        });
        checkBox2.setChecked(TAPreferences.getSendSMS(context));
        return new AlertDialog.Builder(context).setTitle("تاكيد").setMessage(str).setIcon(C1018R.drawable.info).setCancelable(false).setView(inflate).setNegativeButton("موافق", new DialogInterface.OnClickListener() { // from class: com.yd.electricecollector.DialogHelper.8
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                DialogCallback.this.onOk(this, zArr);
            }
        }).setPositiveButton("الغاء", new DialogInterface.OnClickListener() { // from class: com.yd.electricecollector.DialogHelper.7
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                DialogCallback.this.onCancel();
            }
        });
    }

    public static AlertDialog.Builder msgDialogDelete(Context context, final DialogCallback dialogCallback) {
        return new AlertDialog.Builder(context).setTitle("تأكيد").setMessage("هل تريد حذف هذه البيانات  ?").setIcon(C1018R.drawable.ic_delete).setCancelable(false).setNegativeButton("نعم", new DialogInterface.OnClickListener() { // from class: com.yd.electricecollector.DialogHelper.10
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                DialogCallback.this.onOk(this);
            }
        }).setPositiveButton("لا", new DialogInterface.OnClickListener() { // from class: com.yd.electricecollector.DialogHelper.9
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                DialogCallback.this.onCancel();
            }
        });
    }

    public static AlertDialog.Builder msgDialogDelete(String str, Context context, final DialogCallback dialogCallback) {
        return new AlertDialog.Builder(context).setTitle("تأكيد").setMessage(String.format("هل تريد حذف هذه البيانات ?", str)).setIcon(C1018R.drawable.ic_delete).setCancelable(false).setNegativeButton("Yes", new DialogInterface.OnClickListener() { // from class: com.yd.electricecollector.DialogHelper.12
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                DialogCallback.this.onOk(this);
            }
        }).setPositiveButton("No", new DialogInterface.OnClickListener() { // from class: com.yd.electricecollector.DialogHelper.11
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                DialogCallback.this.onCancel();
            }
        });
    }

    public static void showProgressBar(android.app.AlertDialog alertDialog, Boolean bool) {
        if (bool.booleanValue()) {
            alertDialog.show();
        } else {
            alertDialog.dismiss();
        }
    }
}
