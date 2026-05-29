package com.p001yd.electricecollector.permissions;

import android.app.Activity;
import android.content.Context;

/* loaded from: classes10.dex */
public abstract class AChainedPermissionHelper {
    boolean canAskPermission = true;
    private AChainedPermissionHelper nextPermissionHelper;

    public AChainedPermissionHelper add(AChainedPermissionHelper aChainedPermissionHelper) {
        this.nextPermissionHelper = aChainedPermissionHelper;
        return aChainedPermissionHelper;
    }

    public abstract String getDescription();

    public AChainedPermissionHelper getNextWithoutPermission(Context context) {
        if (this.nextPermissionHelper == null) {
            return null;
        }
        return !this.nextPermissionHelper.hasPermission(context) ? this.nextPermissionHelper : this.nextPermissionHelper.getNextWithoutPermission(context);
    }

    public abstract boolean hasGainedPermission(int i, int[] iArr);

    public abstract boolean hasPermission(Context context);

    public abstract void requestPermission(Activity activity);
}
