package com.p001yd.electricecollector.Adapter;

import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.core.internal.view.SupportMenu;
import com.p001yd.electricecollector.BuildConfig;
import com.p001yd.electricecollector.C1018R;
import com.p001yd.electricecollector.entities.Places;
import com.p001yd.electricecollector.p002ui.BaseViewAdapter;
import com.p001yd.electricecollector.p002ui.BaseViewHolder;
import java.util.ArrayList;
import java.util.Locale;

/* loaded from: classes8.dex */
public class PlacesAdapter extends BaseViewAdapter<Places> {
    private final String TAG = getClass().getSimpleName();
    private int _selectedItem = -1;
    String searchString = BuildConfig.VERSION_NAME;
    ValueFilter valueFilter;

    /* loaded from: classes8.dex */
    class PlacesViewHolder extends BaseViewHolder<Places> {
        RadioButton rdoSelection;
        TextView tvAccNo;
        TextView tvBalance;
        TextView tvName;
        TextView tvPlace;
        TextView tvTblh;

        public PlacesViewHolder(int i, ViewGroup viewGroup) {
            super(i, viewGroup);
            this.rdoSelection = (RadioButton) this.itemView.findViewById(C1018R.id.rdoSelection);
            this.tvAccNo = (TextView) this.itemView.findViewById(C1018R.id.tvAccNo);
            this.tvName = (TextView) this.itemView.findViewById(C1018R.id.tvName);
            this.tvTblh = (TextView) this.itemView.findViewById(C1018R.id.tvTblh);
        }

        @Override // com.p001yd.electricecollector.p002ui.BaseViewHolder
        public void bindView(Places places) {
            this.tvAccNo.setText(String.valueOf(places.getnum()));
            this.tvName.setText(places.getname());
            this.tvTblh.setText("" + places.GetListGroup().size());
            String lowerCase = places.getname().toLowerCase(Locale.getDefault());
            if (lowerCase.contains(PlacesAdapter.this.searchString)) {
                int indexOf = lowerCase.indexOf(PlacesAdapter.this.searchString);
                int length = PlacesAdapter.this.searchString.length() + indexOf;
                Spannable newSpannable = Spannable.Factory.getInstance().newSpannable(this.tvName.getText());
                newSpannable.setSpan(new ForegroundColorSpan(SupportMenu.CATEGORY_MASK), indexOf, length, 33);
                this.tvName.setText(newSpannable, TextView.BufferType.SPANNABLE);
            }
            String lowerCase2 = String.valueOf(places.getnum()).toLowerCase(Locale.getDefault());
            if (lowerCase2.contains(PlacesAdapter.this.searchString)) {
                int indexOf2 = lowerCase2.indexOf(PlacesAdapter.this.searchString);
                int length2 = PlacesAdapter.this.searchString.length() + indexOf2;
                Spannable newSpannable2 = Spannable.Factory.getInstance().newSpannable(this.tvAccNo.getText());
                newSpannable2.setSpan(new ForegroundColorSpan(SupportMenu.CATEGORY_MASK), indexOf2, length2, 33);
                this.tvAccNo.setText(newSpannable2, TextView.BufferType.SPANNABLE);
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
            PlacesAdapter.this.searchString = charSequence.toString();
            if (charSequence == null || charSequence.length() <= 0) {
                filterResults.count = PlacesAdapter.this.getItemsFilter().size();
                filterResults.values = PlacesAdapter.this.getItemsFilter();
            } else {
                ArrayList arrayList = new ArrayList();
                for (int i = 0; i < PlacesAdapter.this.getItemsFilter().size(); i++) {
                    if (PlacesAdapter.this.getItemsFilter().get(i).getname().toUpperCase().contains(charSequence.toString().toUpperCase()) || PlacesAdapter.this.getItemsFilter().get(i).getnum().toUpperCase().contains(charSequence.toString().toUpperCase())) {
                        Places places = new Places();
                        places.setname(PlacesAdapter.this.getItemsFilter().get(i).getname());
                        places.setnum(PlacesAdapter.this.getItemsFilter().get(i).getnum());
                        places.SetListGroup(PlacesAdapter.this.getItemsFilter().get(i).GetListGroup());
                        arrayList.add(places);
                    }
                }
                filterResults.count = arrayList.size();
                filterResults.values = arrayList;
            }
            return filterResults;
        }

        @Override // android.widget.Filter
        protected void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
            PlacesAdapter.this.setItems((ArrayList) filterResults.values);
            PlacesAdapter.this.notifyDataSetChanged();
        }
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseViewAdapter
    public void bindHolder(BaseViewHolder baseViewHolder, final int i) {
        final Places item = getItem(i);
        baseViewHolder.bindView(item);
        View.OnClickListener onClickListener = new View.OnClickListener() { // from class: com.yd.electricecollector.Adapter.PlacesAdapter.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (PlacesAdapter.this.getItemClickListener() != null) {
                    PlacesAdapter.this.getItemClickListener().onItemClicked(item);
                    PlacesAdapter.this._selectedItem = i;
                    PlacesAdapter.this.notifyDataSetChanged();
                }
            }
        };
        RadioButton radioButton = ((PlacesViewHolder) baseViewHolder).rdoSelection;
        radioButton.setChecked(i == this._selectedItem);
        baseViewHolder.itemView.setOnClickListener(onClickListener);
        radioButton.setOnClickListener(onClickListener);
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseViewAdapter
    public BaseViewHolder<Places> createHolder(ViewGroup viewGroup, int i) {
        return new PlacesViewHolder(C1018R.layout.item_lookup_selection, viewGroup);
    }

    @Override // android.widget.Filterable
    public Filter getFilter() {
        if (this.valueFilter == null) {
            this.valueFilter = new ValueFilter();
        }
        return this.valueFilter;
    }

    public void setDefaultAccount(Places places) {
        if (places != null) {
        }
    }
}
