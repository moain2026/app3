package com.p001yd.electricecollector.Adapter;

import android.graphics.Color;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.core.internal.view.SupportMenu;
import androidx.recyclerview.widget.RecyclerView;
import com.p001yd.electricecollector.BuildConfig;
import com.p001yd.electricecollector.C1018R;
import com.p001yd.electricecollector.Utils;
import com.p001yd.electricecollector.entities.BalanceState;
import com.p001yd.electricecollector.p002ui.BaseViewAdapter;
import com.p001yd.electricecollector.p002ui.BaseViewHolder;
import java.util.ArrayList;
import java.util.Locale;

/* loaded from: classes8.dex */
public class BalanceStateAdapter extends BaseViewAdapter<BalanceState> {
    private final String TAG = getClass().getSimpleName();
    String searchString = BuildConfig.VERSION_NAME;
    ValueFilter valueFilter;

    /* loaded from: classes8.dex */
    public class BalanceStateViewHolder extends BaseViewHolder<BalanceState> {

        /* renamed from: cv */
        CardView f590cv;
        View divider;
        TextView txtvCol1;
        TextView txtvCol2;
        TextView txtvCol3;
        TextView txtvCol4;
        TextView txtvCol5;
        TextView txtvCol6;

        public BalanceStateViewHolder(int i, ViewGroup viewGroup) {
            super(i, viewGroup);
            this.txtvCol1 = (TextView) this.itemView.findViewById(C1018R.id.txtvCol1);
            this.txtvCol2 = (TextView) this.itemView.findViewById(C1018R.id.txtvCol2);
            this.txtvCol3 = (TextView) this.itemView.findViewById(C1018R.id.txtvCol3);
            this.txtvCol4 = (TextView) this.itemView.findViewById(C1018R.id.txtvCol4);
            this.divider = this.itemView.findViewById(C1018R.id.divider);
        }

        @Override // com.p001yd.electricecollector.p002ui.BaseViewHolder
        public void bindView(BalanceState balanceState) {
            Log.d(BalanceStateAdapter.this.TAG, "bindView");
            double mden = balanceState.getMden() - balanceState.getDain();
            if (mden > 0.0d) {
                this.txtvCol1.setTextColor(Color.parseColor("#D81B60"));
                this.txtvCol1.setText(Utils.numberToString(mden));
                this.txtvCol2.setText("عليه");
            } else if (mden < 0.0d) {
                this.txtvCol1.setText(Utils.numberToString(mden));
                this.txtvCol2.setText("لـه");
                this.txtvCol1.setTextColor(Color.parseColor("#024D94"));
            }
            this.txtvCol3.setText(balanceState.getname());
            this.txtvCol4.setText(balanceState.getDate());
            String lowerCase = balanceState.getname().toLowerCase(Locale.getDefault());
            if (lowerCase.contains(BalanceStateAdapter.this.searchString)) {
                int indexOf = lowerCase.indexOf(BalanceStateAdapter.this.searchString);
                int length = BalanceStateAdapter.this.searchString.length() + indexOf;
                Spannable newSpannable = Spannable.Factory.getInstance().newSpannable(this.txtvCol3.getText());
                newSpannable.setSpan(new ForegroundColorSpan(SupportMenu.CATEGORY_MASK), indexOf, length, 33);
                this.txtvCol3.setText(newSpannable, TextView.BufferType.SPANNABLE);
            }
        }
    }

    /* loaded from: classes8.dex */
    private class ValueFilter extends Filter {
        private ValueFilter() {
        }

        @Override // android.widget.Filter
        protected Filter.FilterResults performFiltering(CharSequence charSequence) {
            Filter.FilterResults filterResults = new Filter.FilterResults();
            BalanceStateAdapter.this.searchString = charSequence.toString();
            if (charSequence == null || charSequence.length() <= 0) {
                filterResults.count = BalanceStateAdapter.this.getItemsFilter().size();
                filterResults.values = BalanceStateAdapter.this.getItemsFilter();
            } else {
                ArrayList arrayList = new ArrayList();
                for (int i = 0; i < BalanceStateAdapter.this.getItemsFilter().size(); i++) {
                    if (BalanceStateAdapter.this.getItemsFilter().get(i).getname().toUpperCase().contains(charSequence.toString().toUpperCase()) || BalanceStateAdapter.this.getItemsFilter().get(i).getnum().toUpperCase().contains(charSequence.toString().toUpperCase())) {
                        BalanceState balanceState = new BalanceState();
                        balanceState.setMden(BalanceStateAdapter.this.getItemsFilter().get(i).getMden());
                        balanceState.setDate(BalanceStateAdapter.this.getItemsFilter().get(i).getDate());
                        balanceState.setType(BalanceStateAdapter.this.getItemsFilter().get(i).getType());
                        balanceState.setname(BalanceStateAdapter.this.getItemsFilter().get(i).getname());
                        balanceState.setDain(BalanceStateAdapter.this.getItemsFilter().get(i).getDain());
                        balanceState.setnum(BalanceStateAdapter.this.getItemsFilter().get(i).getnum());
                        arrayList.add(balanceState);
                    }
                }
                filterResults.count = arrayList.size();
                filterResults.values = arrayList;
            }
            return filterResults;
        }

        @Override // android.widget.Filter
        protected void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
            BalanceStateAdapter.this.setItems((ArrayList) filterResults.values);
            BalanceStateAdapter.this.notifyDataSetChanged();
        }
    }

    private void changeBackgroundColor(View view, boolean z) {
        if (z) {
            view.setBackgroundColor(Color.parseColor("#FFCAD1F5"));
        } else {
            view.setBackgroundColor(Color.parseColor("#000000"));
        }
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseViewAdapter
    public void bindHolder(BaseViewHolder baseViewHolder, int i) {
        final BalanceState item = getItem(i);
        baseViewHolder.bindView(item);
        baseViewHolder.itemView.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.Adapter.BalanceStateAdapter.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (BalanceStateAdapter.this.getItemClickListener() == null) {
                    return;
                }
                BalanceStateAdapter.this.getItemClickListener().onItemClicked(item);
            }
        });
        if (i == getItemCount() - 1) {
            ((BalanceStateViewHolder) baseViewHolder).divider.setVisibility(8);
        }
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseViewAdapter
    public BaseViewHolder<BalanceState> createHolder(ViewGroup viewGroup, int i) {
        return new BalanceStateViewHolder(C1018R.layout.item_list_report_balance_total_activity, viewGroup);
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
