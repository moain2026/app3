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
import com.p001yd.electricecollector.entities.TGroup;
import com.p001yd.electricecollector.p002ui.BaseViewAdapter;
import com.p001yd.electricecollector.p002ui.BaseViewHolder;
import java.util.ArrayList;
import java.util.Locale;

/* loaded from: classes8.dex */
public class GroupsAdapter extends BaseViewAdapter<TGroup> {
    private final String TAG = getClass().getSimpleName();
    private int _selectedItem = -1;
    String searchString = BuildConfig.VERSION_NAME;
    ValueFilter valueFilter;

    /* loaded from: classes8.dex */
    class GroupViewHolder extends BaseViewHolder<TGroup> {
        RadioButton rdoSelection;
        TextView tvAccNo;
        TextView tvBalance;
        TextView tvName;
        TextView tvPlace;
        TextView tvTblh;

        public GroupViewHolder(int i, ViewGroup viewGroup) {
            super(i, viewGroup);
            this.rdoSelection = (RadioButton) this.itemView.findViewById(C1018R.id.rdoSelection);
            this.tvAccNo = (TextView) this.itemView.findViewById(C1018R.id.tvAccNo);
            this.tvName = (TextView) this.itemView.findViewById(C1018R.id.tvName);
            this.tvTblh = (TextView) this.itemView.findViewById(C1018R.id.tvTblh);
            this.tvTblh.setVisibility(8);
        }

        @Override // com.p001yd.electricecollector.p002ui.BaseViewHolder
        public void bindView(TGroup tGroup) {
            this.tvAccNo.setText(String.valueOf(tGroup.getnum()));
            this.tvName.setText(tGroup.getname());
            String lowerCase = tGroup.getname().toLowerCase(Locale.getDefault());
            if (lowerCase.contains(GroupsAdapter.this.searchString)) {
                int indexOf = lowerCase.indexOf(GroupsAdapter.this.searchString);
                int length = GroupsAdapter.this.searchString.length() + indexOf;
                Spannable newSpannable = Spannable.Factory.getInstance().newSpannable(this.tvName.getText());
                newSpannable.setSpan(new ForegroundColorSpan(SupportMenu.CATEGORY_MASK), indexOf, length, 33);
                this.tvName.setText(newSpannable, TextView.BufferType.SPANNABLE);
            }
            String lowerCase2 = String.valueOf(tGroup.getnum()).toLowerCase(Locale.getDefault());
            if (lowerCase2.contains(GroupsAdapter.this.searchString)) {
                int indexOf2 = lowerCase2.indexOf(GroupsAdapter.this.searchString);
                int length2 = GroupsAdapter.this.searchString.length() + indexOf2;
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
            GroupsAdapter.this.searchString = charSequence.toString();
            if (charSequence == null || charSequence.length() <= 0) {
                filterResults.count = GroupsAdapter.this.getItemsFilter().size();
                filterResults.values = GroupsAdapter.this.getItemsFilter();
            } else {
                ArrayList arrayList = new ArrayList();
                for (int i = 0; i < GroupsAdapter.this.getItemsFilter().size(); i++) {
                    if (GroupsAdapter.this.getItemsFilter().get(i).getname().toUpperCase().contains(charSequence.toString().toUpperCase()) || GroupsAdapter.this.getItemsFilter().get(i).getnum().toUpperCase().contains(charSequence.toString().toUpperCase())) {
                        TGroup tGroup = new TGroup();
                        tGroup.setname(GroupsAdapter.this.getItemsFilter().get(i).getname());
                        tGroup.setnum(GroupsAdapter.this.getItemsFilter().get(i).getnum());
                        arrayList.add(tGroup);
                    }
                }
                filterResults.count = arrayList.size();
                filterResults.values = arrayList;
            }
            return filterResults;
        }

        @Override // android.widget.Filter
        protected void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
            GroupsAdapter.this.setItems((ArrayList) filterResults.values);
            GroupsAdapter.this.notifyDataSetChanged();
        }
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseViewAdapter
    public void bindHolder(BaseViewHolder baseViewHolder, final int i) {
        final TGroup item = getItem(i);
        baseViewHolder.bindView(item);
        View.OnClickListener onClickListener = new View.OnClickListener() { // from class: com.yd.electricecollector.Adapter.GroupsAdapter.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (GroupsAdapter.this.getItemClickListener() != null) {
                    GroupsAdapter.this.getItemClickListener().onItemClicked(item);
                    GroupsAdapter.this._selectedItem = i;
                    GroupsAdapter.this.notifyDataSetChanged();
                }
            }
        };
        RadioButton radioButton = ((GroupViewHolder) baseViewHolder).rdoSelection;
        radioButton.setChecked(i == this._selectedItem);
        baseViewHolder.itemView.setOnClickListener(onClickListener);
        radioButton.setOnClickListener(onClickListener);
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseViewAdapter
    public BaseViewHolder<TGroup> createHolder(ViewGroup viewGroup, int i) {
        return new GroupViewHolder(C1018R.layout.item_lookup_selection, viewGroup);
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
