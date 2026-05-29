package com.p001yd.electricecollector.permissions;

import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import com.p001yd.electricecollector.C1018R;

/* loaded from: classes10.dex */
public class PermissionRecieveSms extends AChainedPermissionHelper {
    public static int RECIEVESMS_PERMISSION_REQUESTCODE = 4;
    private AChainedPermissionHelper nextPermissionHelper;

    @Override // com.p001yd.electricecollector.permissions.AChainedPermissionHelper
    public String getDescription() {
        return "Recieve SMS";
    }

    @Override // com.p001yd.electricecollector.permissions.AChainedPermissionHelper
    public boolean hasGainedPermission(int i, int[] iArr) {
        return i == RECIEVESMS_PERMISSION_REQUESTCODE && iArr[0] == 0;
    }

    @Override // com.p001yd.electricecollector.permissions.AChainedPermissionHelper
    public boolean hasPermission(Context context) {
        return !this.canAskPermission || context.checkSelfPermission("android.permission.RECEIVE_SMS") == 0;
    }

    @Override // com.p001yd.electricecollector.permissions.AChainedPermissionHelper
    public void requestPermission(final Activity activity) {
        if (this.canAskPermission && activity.checkSelfPermission("android.permission.RECEIVE_SMS") != 0 && this.canAskPermission) {
            if (!activity.shouldShowRequestPermissionRationale("android.permission.RECEIVE_SMS")) {
                if (this.canAskPermission) {
                    activity.requestPermissions(new String[]{"android.permission.RECEIVE_SMS"}, RECIEVESMS_PERMISSION_REQUESTCODE);
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage(activity.getString(C1018R.string.permissions_sms));
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() { // from class: com.yd.electricecollector.permissions.PermissionRecieveSms.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (PermissionRecieveSms.this.canAskPermission) {
                            activity.requestPermissions(new String[]{"android.permission.RECEIVE_SMS"}, PermissionRecieveSms.RECIEVESMS_PERMISSION_REQUESTCODE);
                        }
                    }
                });
                builder.create().show();
            }
        }
    }
}
