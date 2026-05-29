package com.p001yd.electricecollector.Adapter;

import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.core.internal.view.SupportMenu;
import androidx.recyclerview.widget.RecyclerView;
import com.p001yd.electricecollector.BuildConfig;
import com.p001yd.electricecollector.C1018R;
import com.p001yd.electricecollector.entities.ItemReading;
import com.p001yd.electricecollector.p002ui.BaseViewAdapter;
import com.p001yd.electricecollector.p002ui.BaseViewHolder;
import java.util.ArrayList;
import java.util.Locale;

/* loaded from: classes8.dex */
public class ListReadingAdapter extends BaseViewAdapter<ItemReading> {
    private final String TAG = getClass().getSimpleName();
    int posation = 0;
    String searchString = BuildConfig.VERSION_NAME;
    ValueFilter valueFilter;

    /* loaded from: classes8.dex */
    public class ListReadingViewHolder extends BaseViewHolder<ItemReading> {

        /* renamed from: cv */
        CardView f599cv;
        View divider;
        TextView txtvCol1;
        TextView txtvCol2;
        TextView txtvCol3;
        TextView txtvCol4;
        TextView txtvCol5;
        TextView txtvCol6;
        TextView txtvCol7;
        TextView txtvCol8;

        public ListReadingViewHolder(int i, ViewGroup viewGroup) {
            super(i, viewGroup);
            this.txtvCol1 = (TextView) this.itemView.findViewById(C1018R.id.txtvCol1);
            this.txtvCol2 = (TextView) this.itemView.findViewById(C1018R.id.txtvCol2);
            this.txtvCol3 = (TextView) this.itemView.findViewById(C1018R.id.txtvCol3);
            this.txtvCol4 = (TextView) this.itemView.findViewById(C1018R.id.txtvCol4);
            this.txtvCol5 = (TextView) this.itemView.findViewById(C1018R.id.txtvCol5);
            this.txtvCol6 = (TextView) this.itemView.findViewById(C1018R.id.txtvCol6);
            this.txtvCol7 = (TextView) this.itemView.findViewById(C1018R.id.txtvCol7);
            this.txtvCol8 = (TextView) this.itemView.findViewById(C1018R.id.txtvCol8);
            this.txtvCol1.setVisibility(8);
            this.txtvCol6.setVisibility(8);
        }

        @Override // com.p001yd.electricecollector.p002ui.BaseViewHolder
        public void bindView(ItemReading itemReading) {
            this.txtvCol1.setText(String.valueOf(itemReading.getnum()));
            this.txtvCol2.setText(itemReading.getname());
            this.txtvCol3.setText(itemReading.getnamet());
            this.txtvCol4.setText(itemReading.getNoadad());
            this.txtvCol5.setText(String.valueOf(itemReading.getNomstlm()));
            this.txtvCol6.setText(String.valueOf(itemReading.getNog()));
            this.txtvCol7.setText(String.valueOf(itemReading.getKs()));
            this.txtvCol8.setText(String.valueOf(itemReading.getKh()));
            String lowerCase = itemReading.getname().toLowerCase(Locale.getDefault());
            if (lowerCase.contains(ListReadingAdapter.this.searchString)) {
                int indexOf = lowerCase.indexOf(ListReadingAdapter.this.searchString);
                int length = ListReadingAdapter.this.searchString.length() + indexOf;
                Spannable newSpannable = Spannable.Factory.getInstance().newSpannable(this.txtvCol2.getText());
                newSpannable.setSpan(new ForegroundColorSpan(SupportMenu.CATEGORY_MASK), indexOf, length, 33);
                this.txtvCol2.setText(newSpannable, TextView.BufferType.SPANNABLE);
            }
            String lowerCase2 = String.valueOf(itemReading.getNoadad()).toLowerCase(Locale.getDefault());
            if (lowerCase2.contains(ListReadingAdapter.this.searchString)) {
                int indexOf2 = lowerCase2.indexOf(ListReadingAdapter.this.searchString);
                int length2 = ListReadingAdapter.this.searchString.length() + indexOf2;
                Spannable newSpannable2 = Spannable.Factory.getInstance().newSpannable(this.txtvCol4.getText());
                newSpannable2.setSpan(new ForegroundColorSpan(SupportMenu.CATEGORY_MASK), indexOf2, length2, 33);
                this.txtvCol4.setText(newSpannable2, TextView.BufferType.SPANNABLE);
            }
            String lowerCase3 = String.valueOf(itemReading.getnamet()).toLowerCase(Locale.getDefault());
            if (lowerCase3.contains(ListReadingAdapter.this.searchString)) {
                int indexOf3 = lowerCase3.indexOf(ListReadingAdapter.this.searchString);
                int length3 = ListReadingAdapter.this.searchString.length() + indexOf3;
                Spannable newSpannable3 = Spannable.Factory.getInstance().newSpannable(this.txtvCol3.getText());
                newSpannable3.setSpan(new ForegroundColorSpan(SupportMenu.CATEGORY_MASK), indexOf3, length3, 33);
                this.txtvCol3.setText(newSpannable3, TextView.BufferType.SPANNABLE);
            }
        }

        public int getItemPosation() {
            return ListReadingAdapter.this.posation;
        }

        public void setItemPosation(int i) {
            ListReadingAdapter.this.posation = i;
        }
    }

    /* loaded from: classes8.dex */
    private class ValueFilter extends Filter {
        private ValueFilter() {
        }

        @Override // android.widget.Filter
        protected Filter.FilterResults performFiltering(CharSequence charSequence) {
            Filter.FilterResults filterResults = new Filter.FilterResults();
            ListReadingAdapter.this.searchString = charSequence.toString();
            if (charSequence == null || charSequence.length() <= 0) {
                filterResults.count = ListReadingAdapter.this.getItemsFilter().size();
                filterResults.values = ListReadingAdapter.this.getItemsFilter();
            } else {
                ArrayList arrayList = new ArrayList();
                for (int i = 0; i < ListReadingAdapter.this.getItemsFilter().size(); i++) {
                    if (ListReadingAdapter.this.getItemsFilter().get(i).getname().toUpperCase().contains(charSequence.toString().toUpperCase()) || ListReadingAdapter.this.getItemsFilter().get(i).getNoadad().toUpperCase().contains(charSequence.toString().toUpperCase()) || ListReadingAdapter.this.getItemsFilter().get(i).getnamet().toUpperCase().contains(charSequence.toString().toUpperCase())) {
                        ItemReading itemReading = new ItemReading();
                        itemReading.setnum(ListReadingAdapter.this.getItemsFilter().get(i).getnum());
                        itemReading.setname(ListReadingAdapter.this.getItemsFilter().get(i).getname());
                        itemReading.setType(ListReadingAdapter.this.getItemsFilter().get(i).getType());
                        itemReading.setnamet(ListReadingAdapter.this.getItemsFilter().get(i).getnamet());
                        itemReading.setNoadad(ListReadingAdapter.this.getItemsFilter().get(i).getNoadad());
                        itemReading.setNog(ListReadingAdapter.this.getItemsFilter().get(i).getNog());
                        itemReading.setNomstlm(ListReadingAdapter.this.getItemsFilter().get(i).getNomstlm());
                        itemReading.setNotblh(ListReadingAdapter.this.getItemsFilter().get(i).getNotblh());
                        itemReading.setKh(ListReadingAdapter.this.getItemsFilter().get(i).getKh());
                        itemReading.setKs(ListReadingAdapter.this.getItemsFilter().get(i).getKs());
                        itemReading.setCas(ListReadingAdapter.this.getItemsFilter().get(i).getCas());
                        itemReading.setAsts(ListReadingAdapter.this.getItemsFilter().get(i).getAsts());
                        arrayList.add(itemReading);
                    }
                }
                filterResults.count = arrayList.size();
                filterResults.values = arrayList;
            }
            return filterResults;
        }

        @Override // android.widget.Filter
        protected void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
            ListReadingAdapter.this.setItems((ArrayList) filterResults.values);
            ListReadingAdapter.this.notifyDataSetChanged();
        }
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseViewAdapter
    public void bindHolder(BaseViewHolder baseViewHolder, final int i) {
        final ItemReading item = getItem(i);
        this.posation = i;
        baseViewHolder.bindView(getItem(i));
        baseViewHolder.itemView.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.Adapter.ListReadingAdapter.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (ListReadingAdapter.this.getItemClickListener() == null) {
                    return;
                }
                ListReadingAdapter.this.getItemClickListener().onItemClicked(item, i);
            }
        });
        getItemCount();
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseViewAdapter
    public BaseViewHolder<ItemReading> createHolder(ViewGroup viewGroup, int i) {
        return new ListReadingViewHolder(C1018R.layout.item_list_reading_card_activity, viewGroup);
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
