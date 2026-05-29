package com.p001yd.electricecollector.Adapter;

import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.p001yd.electricecollector.C1018R;
import com.p001yd.electricecollector.Utils;
import com.p001yd.electricecollector.entities.ItemBonds;
import com.p001yd.electricecollector.network.OnContextMenuItemClickCallback;
import com.p001yd.electricecollector.network.OnCreateContextMenuCallback;
import com.p001yd.electricecollector.p002ui.BaseViewAdapter;
import com.p001yd.electricecollector.p002ui.BaseViewHolder;

/* loaded from: classes8.dex */
public class ListBondsReportAdapter extends BaseViewAdapter<ItemBonds> {
    private OnContextMenuItemClickCallback onContextMenuItemClickCallback;
    private OnCreateContextMenuCallback onCreateContextMenuCallback;
    private final String TAG = getClass().getSimpleName();
    int posation = 0;

    /* loaded from: classes8.dex */
    public class ListBondsViewHolder extends BaseViewHolder<ItemBonds> implements View.OnCreateContextMenuListener {

        /* renamed from: cv */
        CardView f598cv;
        View divider;
        MenuItem.OnMenuItemClickListener onMenuItemClickListener;
        TextView txtvCol1;
        TextView txtvCol2;
        TextView txtvCol3;
        TextView txtvCol4;
        TextView txtvCol5;
        TextView txtvCol6;

        public ListBondsViewHolder(int i, ViewGroup viewGroup) {
            super(i, viewGroup);
            this.onMenuItemClickListener = new MenuItem.OnMenuItemClickListener() { // from class: com.yd.electricecollector.Adapter.ListBondsReportAdapter.ListBondsViewHolder.1
                @Override // android.view.MenuItem.OnMenuItemClickListener
                public boolean onMenuItemClick(MenuItem menuItem) {
                    if (ListBondsReportAdapter.this.onContextMenuItemClickCallback == null) {
                        return true;
                    }
                    ListBondsReportAdapter.this.onContextMenuItemClickCallback.onContextMenuItemClick(menuItem, ListBondsViewHolder.this.getAdapterPosition());
                    return true;
                }
            };
            this.txtvCol1 = (TextView) this.itemView.findViewById(C1018R.id.txtvCol1);
            this.txtvCol2 = (TextView) this.itemView.findViewById(C1018R.id.txtvCol2);
            this.txtvCol3 = (TextView) this.itemView.findViewById(C1018R.id.txtvCol3);
            this.txtvCol4 = (TextView) this.itemView.findViewById(C1018R.id.txtvCol4);
            this.txtvCol5 = (TextView) this.itemView.findViewById(C1018R.id.txtvCol5);
            this.txtvCol6 = (TextView) this.itemView.findViewById(C1018R.id.txtvCol6);
            this.txtvCol5.setVisibility(8);
            this.txtvCol1.setVisibility(8);
            this.txtvCol6.setVisibility(8);
        }

        @Override // com.p001yd.electricecollector.p002ui.BaseViewHolder
        public void bindView(ItemBonds itemBonds) {
            Log.d(ListBondsReportAdapter.this.TAG, "bindView");
            this.txtvCol2.setText(Utils.numberToString(Math.abs(itemBonds.getRsed())));
            this.txtvCol1.setText(itemBonds.getDate());
            this.txtvCol3.setText(itemBonds.getname());
            this.txtvCol4.setText(itemBonds.getNmstnd());
            this.txtvCol5.setText(itemBonds.getCurrencyName());
            this.txtvCol6.setText(itemBonds.getBin());
        }

        public int getItemPosation() {
            return ListBondsReportAdapter.this.posation;
        }

        @Override // android.view.View.OnCreateContextMenuListener
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            if (ListBondsReportAdapter.this.onCreateContextMenuCallback != null) {
                ListBondsReportAdapter.this.onCreateContextMenuCallback.onCreateContextMenu(contextMenu, view, contextMenuInfo, getAdapterPosition(), this.onMenuItemClickListener);
            }
        }

        public void setItemPosation(int i) {
            ListBondsReportAdapter.this.posation = i;
        }
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseViewAdapter
    public void bindHolder(BaseViewHolder baseViewHolder, final int i) {
        final ItemBonds item = getItem(i);
        this.posation = i;
        baseViewHolder.bindView(getItem(i));
        baseViewHolder.itemView.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.Adapter.ListBondsReportAdapter.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (ListBondsReportAdapter.this.getItemClickListener() == null) {
                    return;
                }
                ListBondsReportAdapter.this.getItemClickListener().onItemClicked(item, i);
            }
        });
        getItemCount();
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseViewAdapter
    public BaseViewHolder<ItemBonds> createHolder(ViewGroup viewGroup, int i) {
        return new ListBondsViewHolder(C1018R.layout.item_list_bonds_activity, viewGroup);
    }

    @Override // android.widget.Filterable
    public Filter getFilter() {
        return null;
    }

    public OnContextMenuItemClickCallback getOnContextMenuItemClickCallback() {
        return this.onContextMenuItemClickCallback;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void setOnContextMenuItemClickCallback(OnContextMenuItemClickCallback onContextMenuItemClickCallback) {
        this.onContextMenuItemClickCallback = onContextMenuItemClickCallback;
    }

    public void setOnCreateContextMenu(OnCreateContextMenuCallback onCreateContextMenuCallback) {
        this.onCreateContextMenuCallback = onCreateContextMenuCallback;
    }
}
