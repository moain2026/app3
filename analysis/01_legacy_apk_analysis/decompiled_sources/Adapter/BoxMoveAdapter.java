package com.p001yd.electricecollector.Adapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.p001yd.electricecollector.C1018R;
import com.p001yd.electricecollector.Utils;
import com.p001yd.electricecollector.entities.RepBoxMoves;
import com.p001yd.electricecollector.p002ui.BaseViewAdapter;
import com.p001yd.electricecollector.p002ui.BaseViewHolder;

/* loaded from: classes8.dex */
public class BoxMoveAdapter extends BaseViewAdapter<RepBoxMoves> {
    private final String TAG = getClass().getSimpleName();

    /* loaded from: classes8.dex */
    public class BoxMoveViewHolder extends BaseViewHolder<RepBoxMoves> {

        /* renamed from: cv */
        CardView f593cv;
        TextView txtvCol1;
        TextView txtvCol2;
        TextView txtvCol3;
        TextView txtvCol4;
        TextView txtvCol5;
        TextView txtvCol6;

        public BoxMoveViewHolder(int i, ViewGroup viewGroup) {
            super(i, viewGroup);
            this.txtvCol1 = (TextView) this.itemView.findViewById(C1018R.id.txtvCol1);
            this.txtvCol2 = (TextView) this.itemView.findViewById(C1018R.id.txtvCol2);
            this.txtvCol3 = (TextView) this.itemView.findViewById(C1018R.id.txtvCol3);
            this.txtvCol4 = (TextView) this.itemView.findViewById(C1018R.id.txtvCol4);
            this.txtvCol5 = (TextView) this.itemView.findViewById(C1018R.id.txtvCol5);
            this.txtvCol6 = (TextView) this.itemView.findViewById(C1018R.id.txtvCol6);
        }

        @Override // com.p001yd.electricecollector.p002ui.BaseViewHolder
        public void bindView(RepBoxMoves repBoxMoves) {
            Log.d(BoxMoveAdapter.this.TAG, "bindView");
            this.txtvCol1.setText(repBoxMoves.getnum());
            this.txtvCol2.setText(repBoxMoves.getname());
            this.txtvCol3.setText(Utils.numberToString(repBoxMoves.getBalance()));
            this.txtvCol4.setText(Utils.numberToString(repBoxMoves.getMden()));
            this.txtvCol5.setText(Utils.numberToString(repBoxMoves.getDain()));
            this.txtvCol6.setText(Utils.numberToString(repBoxMoves.getFBalance()));
        }
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseViewAdapter
    public void bindHolder(BaseViewHolder baseViewHolder, int i) {
        final RepBoxMoves item = getItem(i);
        baseViewHolder.bindView(item);
        baseViewHolder.itemView.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.Adapter.BoxMoveAdapter.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (BoxMoveAdapter.this.getItemClickListener() == null) {
                    return;
                }
                BoxMoveAdapter.this.getItemClickListener().onItemClicked(item);
            }
        });
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseViewAdapter
    public BaseViewHolder<RepBoxMoves> createHolder(ViewGroup viewGroup, int i) {
        return new BoxMoveViewHolder(C1018R.layout.item_list_box_moves_activity, viewGroup);
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
