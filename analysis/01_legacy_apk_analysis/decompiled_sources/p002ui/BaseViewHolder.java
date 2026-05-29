package com.p001yd.electricecollector.p002ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;

/* loaded from: classes12.dex */
public class BaseViewHolder<T> extends RecyclerView.ViewHolder {
    public BaseViewHolder(int i, ViewGroup viewGroup) {
        super(LayoutInflater.from(viewGroup.getContext()).inflate(i, viewGroup, false));
    }

    public void bindView(T t) {
    }
}
