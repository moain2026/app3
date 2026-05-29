package com.p001yd.electricecollector.Adapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.p001yd.electricecollector.BuildConfig;
import com.p001yd.electricecollector.C1018R;
import com.p001yd.electricecollector.Utils;
import com.p001yd.electricecollector.entities.BondsHeader;
import com.p001yd.electricecollector.p002ui.BaseViewAdapter;
import com.p001yd.electricecollector.p002ui.BaseViewHolder;
import java.util.ArrayList;

/* loaded from: classes8.dex */
public class BondsHeaderReportAdapter extends BaseViewAdapter<BondsHeader> {
    private final String TAG = getClass().getSimpleName();
    String searchString = BuildConfig.VERSION_NAME;
    ValueFilter valueFilter;

    /* loaded from: classes8.dex */
    public class BondsHeaderReportViewHolder extends BaseViewHolder<BondsHeader> {

        /* renamed from: cv */
        CardView f592cv;
        View divider;
        TextView txtvCol1;
        TextView txtvCol2;
        TextView txtvCol3;
        TextView txtvCol4;
        TextView txtvCol5;
        TextView txtvCol6;

        public BondsHeaderReportViewHolder(int i, ViewGroup viewGroup) {
            super(i, viewGroup);
            this.txtvCol1 = (TextView) this.itemView.findViewById(C1018R.id.txtvCol1);
            this.txtvCol2 = (TextView) this.itemView.findViewById(C1018R.id.txtvCol2);
            this.txtvCol3 = (TextView) this.itemView.findViewById(C1018R.id.txtvCol3);
            this.txtvCol4 = (TextView) this.itemView.findViewById(C1018R.id.txtvCol4);
            this.txtvCol5 = (TextView) this.itemView.findViewById(C1018R.id.txtvCol5);
            this.divider = this.itemView.findViewById(C1018R.id.divider);
        }

        @Override // com.p001yd.electricecollector.p002ui.BaseViewHolder
        public void bindView(BondsHeader bondsHeader) {
            Log.d(BondsHeaderReportAdapter.this.TAG, "bindView");
            this.txtvCol2.setText(Utils.numberToString(Math.abs(bondsHeader.getBalance())));
            this.txtvCol1.setText(bondsHeader.getDate());
            this.txtvCol3.setText(bondsHeader.getname());
            this.txtvCol4.setText(bondsHeader.getType() == 1 ? "قبض" : "صرف");
            this.txtvCol5.setText(bondsHeader.getCurrencyName());
        }
    }

    /* loaded from: classes8.dex */
    private class ValueFilter extends Filter {
        private ValueFilter() {
        }

        @Override // android.widget.Filter
        protected Filter.FilterResults performFiltering(CharSequence charSequence) {
            Filter.FilterResults filterResults = new Filter.FilterResults();
            BondsHeaderReportAdapter.this.searchString = charSequence.toString();
            if (charSequence == null || charSequence.length() <= 0) {
                filterResults.count = BondsHeaderReportAdapter.this.getItemsFilter().size();
                filterResults.values = BondsHeaderReportAdapter.this.getItemsFilter();
            } else {
                ArrayList arrayList = new ArrayList();
                for (int i = 0; i < BondsHeaderReportAdapter.this.getItemsFilter().size(); i++) {
                    if (BondsHeaderReportAdapter.this.getItemsFilter().get(i).getname().toUpperCase().contains(charSequence.toString().toUpperCase()) || BondsHeaderReportAdapter.this.getItemsFilter().get(i).getnum().toUpperCase().contains(charSequence.toString().toUpperCase())) {
                        BondsHeader bondsHeader = new BondsHeader();
                        bondsHeader.seMden(BondsHeaderReportAdapter.this.getItemsFilter().get(i).getMden());
                        bondsHeader.seBalance(BondsHeaderReportAdapter.this.getItemsFilter().get(i).getBalance());
                        bondsHeader.setDate(BondsHeaderReportAdapter.this.getItemsFilter().get(i).getDate());
                        bondsHeader.setType(BondsHeaderReportAdapter.this.getItemsFilter().get(i).getType());
                        bondsHeader.setname(BondsHeaderReportAdapter.this.getItemsFilter().get(i).getname());
                        bondsHeader.seMdenain(BondsHeaderReportAdapter.this.getItemsFilter().get(i).getDain());
                        bondsHeader.setCurrencyId(BondsHeaderReportAdapter.this.getItemsFilter().get(i).getCurrencyId());
                        bondsHeader.setCurrencyName(BondsHeaderReportAdapter.this.getItemsFilter().get(i).getCurrencyName());
                        arrayList.add(bondsHeader);
                    }
                }
                filterResults.count = arrayList.size();
                filterResults.values = arrayList;
            }
            return filterResults;
        }

        @Override // android.widget.Filter
        protected void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
            BondsHeaderReportAdapter.this.setItems((ArrayList) filterResults.values);
            BondsHeaderReportAdapter.this.notifyDataSetChanged();
        }
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseViewAdapter
    public void bindHolder(BaseViewHolder baseViewHolder, int i) {
        final BondsHeader item = getItem(i);
        baseViewHolder.bindView(item);
        baseViewHolder.itemView.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.Adapter.BondsHeaderReportAdapter.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (BondsHeaderReportAdapter.this.getItemClickListener() == null) {
                    return;
                }
                BondsHeaderReportAdapter.this.getItemClickListener().onItemClicked(item);
            }
        });
        if (i == getItemCount() - 1) {
            ((BondsHeaderReportViewHolder) baseViewHolder).divider.setVisibility(8);
        }
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseViewAdapter
    public BaseViewHolder<BondsHeader> createHolder(ViewGroup viewGroup, int i) {
        return new BondsHeaderReportViewHolder(C1018R.layout.item_list_bonds_activity, viewGroup);
    }

    @Override // android.widget.Filterable
    public Filter getFilter() {
        if (this.valueFilter == null) {
            this.valueFilter = new ValueFilter();
        }
        return this.valueFilter;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
