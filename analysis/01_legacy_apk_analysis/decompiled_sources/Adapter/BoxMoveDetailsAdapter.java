package com.p001yd.electricecollector.Adapter;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.p001yd.electricecollector.C1018R;
import com.p001yd.electricecollector.entities.RepBoxMovesDetals;
import com.p001yd.electricecollector.p002ui.BaseViewAdapter;
import com.p001yd.electricecollector.p002ui.BaseViewHolder;

/* loaded from: classes8.dex */
public class BoxMoveDetailsAdapter extends BaseViewAdapter<RepBoxMovesDetals> {
    private final String TAG = getClass().getSimpleName();

    /* loaded from: classes8.dex */
    public class BoxMoveDetailsViewHolder extends BaseViewHolder<RepBoxMovesDetals> {

        /* renamed from: cv */
        CardView f594cv;
        TextView txtvCol1;
        TextView txtvCol2;
        TextView txtvCol3;
        TextView txtvCol4;
        TextView txtvCol5;
        TextView txtvCol6;

        public BoxMoveDetailsViewHolder(int i, ViewGroup viewGroup) {
            super(i, viewGroup);
            this.txtvCol1 = (TextView) this.itemView.findViewById(C1018R.id.txtvCol1);
            this.txtvCol2 = (TextView) this.itemView.findViewById(C1018R.id.txtvCol2);
            this.txtvCol3 = (TextView) this.itemView.findViewById(C1018R.id.txtvCol3);
            this.txtvCol4 = (TextView) this.itemView.findViewById(C1018R.id.txtvCol4);
            this.txtvCol5 = (TextView) this.itemView.findViewById(C1018R.id.txtvCol5);
            this.txtvCol6 = (TextView) this.itemView.findViewById(C1018R.id.txtvCol6);
        }

        @Override // com.p001yd.electricecollector.p002ui.BaseViewHolder
        public void bindView(RepBoxMovesDetals repBoxMovesDetals) {
            Log.d(BoxMoveDetailsAdapter.this.TAG, "bindView");
            this.txtvCol1.setText(repBoxMovesDetals.getname());
            this.txtvCol2.setText(repBoxMovesDetals.getTypems());
            this.txtvCol3.setText(repBoxMovesDetals.getNmstnd());
            this.txtvCol4.setText(repBoxMovesDetals.getNotes());
            this.txtvCol5.setText(repBoxMovesDetals.getAmount());
        }
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseViewAdapter
    public void bindHolder(BaseViewHolder baseViewHolder, int i) {
        baseViewHolder.bindView(getItem(i));
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseViewAdapter
    public BaseViewHolder<RepBoxMovesDetals> createHolder(ViewGroup viewGroup, int i) {
        return new BoxMoveDetailsViewHolder(C1018R.layout.item_list_box_moves_details_activity, viewGroup);
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
