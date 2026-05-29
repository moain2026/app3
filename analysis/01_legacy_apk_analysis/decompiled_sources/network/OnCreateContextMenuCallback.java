package com.p001yd.electricecollector.network;

import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;

/* loaded from: classes12.dex */
public interface OnCreateContextMenuCallback<T> {
    void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo, int i, MenuItem.OnMenuItemClickListener onMenuItemClickListener);
}
