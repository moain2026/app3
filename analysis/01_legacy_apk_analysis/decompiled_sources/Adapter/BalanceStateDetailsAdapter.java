package com.p001yd.electricecollector.Adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.p001yd.electricecollector.C1018R;
import com.p001yd.electricecollector.Utils;
import com.p001yd.electricecollector.entities.BalanceStateDetails;
import com.p001yd.electricecollector.p002ui.BaseViewAdapter;
import com.p001yd.electricecollector.p002ui.BaseViewHolder;

/* loaded from: classes8.dex */
public class BalanceStateDetailsAdapter extends BaseViewAdapter<BalanceStateDetails> {
    private final String TAG = getClass().getSimpleName();

    /* loaded from: classes8.dex */
    public class BalanceStateDetailsViewHolder extends BaseViewHolder<BalanceStateDetails> {

        /* renamed from: cv */
        CardView f591cv;
        View divider;
        TextView txtvCol1;
        TextView txtvCol2;
        TextView txtvCol3;
        TextView txtvCol4;
        TextView txtvCol5;
        TextView txtvCol6;

        public BalanceStateDetailsViewHolder(int i, ViewGroup viewGroup) {
            super(i, viewGroup);
            this.txtvCol1 = (TextView) this.itemView.findViewById(C1018R.id.txtvCol1);
            this.txtvCol2 = (TextView) this.itemView.findViewById(C1018R.id.txtvCol2);
            this.txtvCol3 = (TextView) this.itemView.findViewById(C1018R.id.txtvCol3);
            this.txtvCol4 = (TextView) this.itemView.findViewById(C1018R.id.txtvCol4);
        }

        @Override // com.p001yd.electricecollector.p002ui.BaseViewHolder
        public void bindView(BalanceStateDetails balanceStateDetails) {
            double dain;
            Log.d(BalanceStateDetailsAdapter.this.TAG, "bindView");
            if (balanceStateDetails.getMden() > 0.0d) {
                dain = balanceStateDetails.getMden();
                this.txtvCol2.setTextColor(Color.parseColor("#D81B60"));
            } else {
                dain = balanceStateDetails.getDain();
                this.txtvCol2.setTextColor(Color.parseColor("#008577"));
            }
            this.txtvCol2.setText(Utils.numberToString(dain));
            this.txtvCol1.setText(balanceStateDetails.getDate());
            this.txtvCol3.setText(balanceStateDetails.getname());
            this.txtvCol4.setText(Utils.numberToString(balanceStateDetails.getRsed()));
        }
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseViewAdapter
    public void bindHolder(BaseViewHolder baseViewHolder, int i) {
        baseViewHolder.bindView(getItem(i));
        getItemCount();
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseViewAdapter
    public BaseViewHolder<BalanceStateDetails> createHolder(ViewGroup viewGroup, int i) {
        return new BalanceStateDetailsViewHolder(C1018R.layout.item_list_report_balance_activity, viewGroup);
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
