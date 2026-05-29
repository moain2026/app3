package com.p001yd.electricecollector.permissions;

import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import com.p001yd.electricecollector.C1018R;

/* loaded from: classes10.dex */
public class PermissionSendSms extends AChainedPermissionHelper {
    public static int SENDSMS_PERMISSION_REQUESTCODE = 3;

    @Override // com.p001yd.electricecollector.permissions.AChainedPermissionHelper
    public String getDescription() {
        return "Send SMS";
    }

    @Override // com.p001yd.electricecollector.permissions.AChainedPermissionHelper
    public boolean hasGainedPermission(int i, int[] iArr) {
        return i == SENDSMS_PERMISSION_REQUESTCODE && iArr[0] == 0;
    }

    @Override // com.p001yd.electricecollector.permissions.AChainedPermissionHelper
    public boolean hasPermission(Context context) {
        return !this.canAskPermission || context.checkSelfPermission("android.permission.SEND_SMS") == 0;
    }

    @Override // com.p001yd.electricecollector.permissions.AChainedPermissionHelper
    public void requestPermission(final Activity activity) {
        if (this.canAskPermission && activity.checkSelfPermission("android.permission.SEND_SMS") != 0 && this.canAskPermission) {
            if (!activity.shouldShowRequestPermissionRationale("android.permission.SEND_SMS")) {
                if (this.canAskPermission) {
                    activity.requestPermissions(new String[]{"android.permission.SEND_SMS"}, SENDSMS_PERMISSION_REQUESTCODE);
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage(activity.getString(C1018R.string.permissions_send_sms));
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() { // from class: com.yd.electricecollector.permissions.PermissionSendSms.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (PermissionSendSms.this.canAskPermission) {
                            activity.requestPermissions(new String[]{"android.permission.SEND_SMS"}, PermissionSendSms.SENDSMS_PERMISSION_REQUESTCODE);
                        }
                    }
                });
                builder.create().show();
            }
        }
    }
}
