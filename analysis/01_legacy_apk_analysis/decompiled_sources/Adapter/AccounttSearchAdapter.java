package com.p001yd.electricecollector.Adapter;

import android.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import com.p001yd.electricecollector.entities.Accounts;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes8.dex */
public class AccounttSearchAdapter extends ArrayAdapter<Accounts> implements Filterable {
    private ArrayList<Accounts> items;
    private ArrayList<Accounts> itemsAll;
    Filter nameFilter;
    private ArrayList<Accounts> suggestions;
    private int viewResourceId;

    public AccounttSearchAdapter(Context context, int i, ArrayList<Accounts> arrayList) {
        super(context, i, arrayList);
        this.suggestions = new ArrayList<>();
        this.nameFilter = new Filter() { // from class: com.yd.electricecollector.Adapter.AccounttSearchAdapter.1
            boolean AcceptedMatch(String str, String str2) {
                String lowerCase = str.toLowerCase();
                String[] split = str2.toLowerCase().trim().split(" ");
                String RemoveAmbiguousChars = RemoveAmbiguousChars(lowerCase);
                for (String str3 : split) {
                    String RemoveAmbiguousChars2 = RemoveAmbiguousChars(str3);
                    if (lowerCase.trim().equals("") || RemoveAmbiguousChars2.trim().equals("")) {
                        return false;
                    }
                    if (!lowerCase.contains(RemoveAmbiguousChars2) && !lowerCase.contains(str3) && !RemoveAmbiguousChars.contains(RemoveAmbiguousChars2)) {
                        return false;
                    }
                }
                return true;
            }

            String RemoveAmbiguousChars(String str) {
                return str.replace("ة", "ه").replace("ه", "ه").replace("أ", "ا").replace("إ", "ا");
            }

            @Override // android.widget.Filter
            public String convertResultToString(Object obj) {
                return ((Accounts) obj).getname();
            }

            @Override // android.widget.Filter
            protected Filter.FilterResults performFiltering(CharSequence charSequence) {
                Filter.FilterResults filterResults = new Filter.FilterResults();
                synchronized (filterResults) {
                    if (charSequence != null) {
                        Iterator it = AccounttSearchAdapter.this.itemsAll.iterator();
                        while (it.hasNext()) {
                            Accounts accounts = (Accounts) it.next();
                            if (AcceptedMatch(accounts.getname(), charSequence.toString())) {
                                AccounttSearchAdapter.this.suggestions.add(accounts);
                            }
                        }
                        filterResults.values = AccounttSearchAdapter.this.suggestions;
                        filterResults.count = AccounttSearchAdapter.this.suggestions.size();
                    }
                }
                return filterResults;
            }

            @Override // android.widget.Filter
            protected void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
                ArrayList arrayList2 = (ArrayList) filterResults.values;
                if (filterResults == null || filterResults.count <= 0) {
                    AccounttSearchAdapter.this.notifyDataSetInvalidated();
                    return;
                }
                AccounttSearchAdapter.this.clear();
                Iterator it = arrayList2.iterator();
                while (it.hasNext()) {
                    AccounttSearchAdapter.this.add((Accounts) it.next());
                }
                AccounttSearchAdapter.this.notifyDataSetChanged();
            }
        };
        this.items = arrayList;
        this.itemsAll = (ArrayList) arrayList.clone();
        this.suggestions = new ArrayList<>();
        this.viewResourceId = i;
    }

    @Override // android.widget.ArrayAdapter, android.widget.Filterable
    public Filter getFilter() {
        return this.nameFilter;
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView textView;
        View view2 = view;
        if (view2 == null) {
            view2 = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(this.viewResourceId, (ViewGroup) null);
        }
        Accounts accounts = this.items.get(i);
        if (accounts != null && (textView = (TextView) view2.findViewById(R.id.text1)) != null) {
            textView.setText(accounts.getname());
        }
        return view2;
    }
}
