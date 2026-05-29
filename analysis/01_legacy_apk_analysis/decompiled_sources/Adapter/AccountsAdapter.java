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
import com.p001yd.electricecollector.Utils;
import com.p001yd.electricecollector.entities.Accounts;
import com.p001yd.electricecollector.p002ui.BaseViewAdapter;
import com.p001yd.electricecollector.p002ui.BaseViewHolder;
import java.util.ArrayList;
import java.util.Locale;

/* loaded from: classes8.dex */
public class AccountsAdapter extends BaseViewAdapter<Accounts> {
    private final String TAG = getClass().getSimpleName();
    private int _selectedItem = -1;
    String searchString = BuildConfig.VERSION_NAME;
    ValueFilter valueFilter;

    /* loaded from: classes8.dex */
    class AccountsViewHolder extends BaseViewHolder<Accounts> {
        RadioButton rdoSelection;
        TextView tvAccNo;
        TextView tvBalance;
        TextView tvName;
        TextView tvPlace;
        TextView tvTblh;

        public AccountsViewHolder(int i, ViewGroup viewGroup) {
            super(i, viewGroup);
            this.rdoSelection = (RadioButton) this.itemView.findViewById(C1018R.id.rdoSelection);
            this.tvAccNo = (TextView) this.itemView.findViewById(C1018R.id.tvAccNo);
            this.tvName = (TextView) this.itemView.findViewById(C1018R.id.tvName);
            this.tvTblh = (TextView) this.itemView.findViewById(C1018R.id.tvTblh);
            this.tvPlace = (TextView) this.itemView.findViewById(C1018R.id.tvPlace);
            this.tvBalance = (TextView) this.itemView.findViewById(C1018R.id.tvBalance);
        }

        @Override // com.p001yd.electricecollector.p002ui.BaseViewHolder
        public void bindView(Accounts accounts) {
            this.tvAccNo.setText(String.valueOf(accounts.getnum()));
            this.tvName.setText(accounts.getname());
            this.tvTblh.setText(accounts.getnamet());
            this.tvPlace.setText(accounts.getnamep());
            this.tvBalance.setText(Utils.numberToString(accounts.getBalance()));
            String lowerCase = accounts.getname().toLowerCase(Locale.getDefault());
            if (lowerCase.contains(AccountsAdapter.this.searchString)) {
                int indexOf = lowerCase.indexOf(AccountsAdapter.this.searchString);
                int length = AccountsAdapter.this.searchString.length() + indexOf;
                Spannable newSpannable = Spannable.Factory.getInstance().newSpannable(this.tvName.getText());
                newSpannable.setSpan(new ForegroundColorSpan(SupportMenu.CATEGORY_MASK), indexOf, length, 33);
                this.tvName.setText(newSpannable, TextView.BufferType.SPANNABLE);
            }
            String lowerCase2 = String.valueOf(accounts.getnum()).toLowerCase(Locale.getDefault());
            if (lowerCase2.contains(AccountsAdapter.this.searchString)) {
                int indexOf2 = lowerCase2.indexOf(AccountsAdapter.this.searchString);
                int length2 = AccountsAdapter.this.searchString.length() + indexOf2;
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
            AccountsAdapter.this.searchString = charSequence.toString();
            if (charSequence == null || charSequence.length() <= 0) {
                filterResults.count = AccountsAdapter.this.getItemsFilter().size();
                filterResults.values = AccountsAdapter.this.getItemsFilter();
            } else {
                ArrayList arrayList = new ArrayList();
                for (int i = 0; i < AccountsAdapter.this.getItemsFilter().size(); i++) {
                    if (AccountsAdapter.this.getItemsFilter().get(i).getname().toUpperCase().contains(charSequence.toString().toUpperCase()) || AccountsAdapter.this.getItemsFilter().get(i).getnamet().toUpperCase().contains(charSequence.toString().toUpperCase()) || AccountsAdapter.this.getItemsFilter().get(i).getnamep().toUpperCase().contains(charSequence.toString().toUpperCase()) || String.valueOf(AccountsAdapter.this.getItemsFilter().get(i).getnum()).toUpperCase().contains(charSequence.toString().toUpperCase())) {
                        Accounts accounts = new Accounts();
                        accounts.setname(AccountsAdapter.this.getItemsFilter().get(i).getname());
                        accounts.setnum(AccountsAdapter.this.getItemsFilter().get(i).getnum());
                        accounts.setNoadad(AccountsAdapter.this.getItemsFilter().get(i).getNoadad());
                        accounts.setNomstlm(AccountsAdapter.this.getItemsFilter().get(i).getNomstlm());
                        accounts.setNotblh(AccountsAdapter.this.getItemsFilter().get(i).getNotblh());
                        accounts.setNog(AccountsAdapter.this.getItemsFilter().get(i).getNog());
                        accounts.setnamet(AccountsAdapter.this.getItemsFilter().get(i).getnamet());
                        accounts.setnamep(AccountsAdapter.this.getItemsFilter().get(i).getnamep());
                        accounts.setBalance(AccountsAdapter.this.getItemsFilter().get(i).getBalance());
                        arrayList.add(accounts);
                    }
                }
                filterResults.count = arrayList.size();
                filterResults.values = arrayList;
            }
            return filterResults;
        }

        @Override // android.widget.Filter
        protected void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
            AccountsAdapter.this.setItems((ArrayList) filterResults.values);
            AccountsAdapter.this.notifyDataSetChanged();
        }
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseViewAdapter
    public void bindHolder(BaseViewHolder baseViewHolder, final int i) {
        final Accounts item = getItem(i);
        baseViewHolder.bindView(item);
        View.OnClickListener onClickListener = new View.OnClickListener() { // from class: com.yd.electricecollector.Adapter.AccountsAdapter.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (AccountsAdapter.this.getItemClickListener() != null) {
                    AccountsAdapter.this.getItemClickListener().onItemClicked(item);
                    AccountsAdapter.this._selectedItem = i;
                    AccountsAdapter.this.notifyDataSetChanged();
                }
            }
        };
        RadioButton radioButton = ((AccountsViewHolder) baseViewHolder).rdoSelection;
        radioButton.setChecked(i == this._selectedItem);
        baseViewHolder.itemView.setOnClickListener(onClickListener);
        radioButton.setOnClickListener(onClickListener);
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseViewAdapter
    public BaseViewHolder<Accounts> createHolder(ViewGroup viewGroup, int i) {
        return new AccountsViewHolder(C1018R.layout.item_selection, viewGroup);
    }

    @Override // android.widget.Filterable
    public Filter getFilter() {
        if (this.valueFilter == null) {
            this.valueFilter = new ValueFilter();
        }
        return this.valueFilter;
    }

    public void setDefaultAccount(Accounts accounts) {
        if (accounts != null) {
        }
    }
}
