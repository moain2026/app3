package com.p001yd.electricecollector.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.p001yd.electricecollector.C1018R;
import com.p001yd.electricecollector.entities.Reports;
import java.util.List;

/* loaded from: classes8.dex */
public class ReportsAdapter extends RecyclerView.Adapter<ViewHolder> {
    Context context;
    List<Reports> mDataset;

    /* loaded from: classes8.dex */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        /* renamed from: cv */
        CardView f601cv;
        TextView txtvCol1;
        TextView txtvCol2;
        TextView txtvCol3;
        TextView txtvCol4;
        TextView txtvCol5;
        TextView txtvCol6;

        public ViewHolder(View view) {
            super(view);
            this.txtvCol1 = (TextView) view.findViewById(C1018R.id.txtvCol1);
            this.txtvCol2 = (TextView) view.findViewById(C1018R.id.txtvCol2);
            this.txtvCol3 = (TextView) view.findViewById(C1018R.id.txtvCol3);
        }
    }

    public ReportsAdapter(Context context, List<Reports> list) {
        this.mDataset = list;
        this.context = context;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.mDataset.size();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.txtvCol1.setText(this.mDataset.get(i).getnum());
        viewHolder.txtvCol2.setText(this.mDataset.get(i).getName());
        viewHolder.txtvCol2.setText(this.mDataset.get(i).gettype());
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(C1018R.layout.list_report_activity, viewGroup, false));
    }
}
