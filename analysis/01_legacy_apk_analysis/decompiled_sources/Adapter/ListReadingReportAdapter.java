package com.p001yd.electricecollector.Adapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.p001yd.electricecollector.C1018R;
import com.p001yd.electricecollector.entities.RepReading;
import com.p001yd.electricecollector.p002ui.BaseViewAdapter;
import com.p001yd.electricecollector.p002ui.BaseViewHolder;

/* loaded from: classes8.dex */
public class ListReadingReportAdapter extends BaseViewAdapter<RepReading> {
    private final String TAG = getClass().getSimpleName();
    int posation = 0;

    /* loaded from: classes8.dex */
    public class ListReadingViewHolder extends BaseViewHolder<RepReading> {

        /* renamed from: cv */
        CardView f600cv;
        View divider;
        TextView txtvCol1;
        TextView txtvCol2;
        TextView txtvCol3;

        public ListReadingViewHolder(int i, ViewGroup viewGroup) {
            super(i, viewGroup);
            this.txtvCol1 = (TextView) this.itemView.findViewById(C1018R.id.txtvCol1);
            this.txtvCol2 = (TextView) this.itemView.findViewById(C1018R.id.txtvCol2);
            this.txtvCol3 = (TextView) this.itemView.findViewById(C1018R.id.txtvCol3);
        }

        @Override // com.p001yd.electricecollector.p002ui.BaseViewHolder
        public void bindView(RepReading repReading) {
            Log.d(ListReadingReportAdapter.this.TAG, "bindView");
            this.txtvCol1.setText(String.valueOf(repReading.getnum()));
            this.txtvCol2.setText(repReading.getname());
            this.txtvCol3.setText(repReading.getAst());
        }

        public int getItemPosation() {
            return ListReadingReportAdapter.this.posation;
        }

        public void setItemPosation(int i) {
            ListReadingReportAdapter.this.posation = i;
        }
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseViewAdapter
    public void bindHolder(BaseViewHolder baseViewHolder, int i) {
        getItem(i);
        this.posation = i;
        baseViewHolder.bindView(getItem(i));
        getItemCount();
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseViewAdapter
    public BaseViewHolder<RepReading> createHolder(ViewGroup viewGroup, int i) {
        return new ListReadingViewHolder(C1018R.layout.item_list_rep_reading_activity, viewGroup);
    }

    @Override // android.widget.Filterable
    public Filter getFilter() {
        return null;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
