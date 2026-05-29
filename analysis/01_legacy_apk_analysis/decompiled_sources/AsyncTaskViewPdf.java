package com.p001yd.electricecollector;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/* loaded from: classes6.dex */
public class AsyncTaskViewPdf extends AsyncTask<Object, Integer, Long> {
    protected Activity activity;
    protected ViewGroup footer;
    protected LayoutInflater layoutInflater;
    public int listCurrentPosition = 0;
    protected LinearLayout progressDownload;

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // android.os.AsyncTask
    public Long doInBackground(Object... objArr) {
        return null;
    }
}
