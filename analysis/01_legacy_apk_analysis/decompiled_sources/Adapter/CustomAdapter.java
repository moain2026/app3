package com.p001yd.electricecollector.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.p001yd.electricecollector.C1018R;
import com.p001yd.electricecollector.entities.ItemReading;
import java.util.List;

/* loaded from: classes8.dex */
public class CustomAdapter extends RecyclerView.Adapter<ViewHolder> {
    Context context;
    List<ItemReading> mDataset;

    /* loaded from: classes8.dex */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        /* renamed from: cv */
        CardView f595cv;
        TextView txtvCol1;
        TextView txtvCol2;
        TextView txtvCol3;
        TextView txtvCol4;
        TextView txtvCol5;
        TextView txtvCol6;
        TextView txtvCol7;
        TextView txtvCol8;

        public ViewHolder(View view) {
            super(view);
            this.txtvCol1 = (TextView) view.findViewById(C1018R.id.txtvCol1);
            this.txtvCol2 = (TextView) view.findViewById(C1018R.id.txtvCol2);
            this.txtvCol3 = (TextView) view.findViewById(C1018R.id.txtvCol3);
            this.txtvCol4 = (TextView) view.findViewById(C1018R.id.txtvCol4);
            this.txtvCol5 = (TextView) view.findViewById(C1018R.id.txtvCol5);
            this.txtvCol6 = (TextView) view.findViewById(C1018R.id.txtvCol6);
            this.txtvCol7 = (TextView) view.findViewById(C1018R.id.txtvCol7);
            this.txtvCol8 = (TextView) view.findViewById(C1018R.id.txtvCol8);
        }
    }

    public CustomAdapter(Context context, List<ItemReading> list) {
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
        viewHolder.txtvCol2.setText(this.mDataset.get(i).getname());
        viewHolder.txtvCol3.setText(String.valueOf(this.mDataset.get(i).getnamet()));
        viewHolder.txtvCol4.setText(this.mDataset.get(i).getNoadad());
        viewHolder.txtvCol5.setText(String.valueOf(this.mDataset.get(i).getNomstlm()));
        viewHolder.txtvCol6.setText(String.valueOf(this.mDataset.get(i).getNog()));
        viewHolder.txtvCol7.setText(String.valueOf(this.mDataset.get(i).getKs()));
        viewHolder.txtvCol8.setText(String.valueOf(this.mDataset.get(i).getKh()));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(C1018R.layout.item_list_reading_activity, viewGroup, false));
    }
}
