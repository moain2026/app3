package com.p001yd.electricecollector.p002ui;

import android.view.ViewGroup;
import android.widget.Filterable;
import androidx.recyclerview.widget.RecyclerView;
import com.p001yd.electricecollector.network.OnItemClickCallback;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes12.dex */
public abstract class BaseViewAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> implements Filterable {
    private OnItemClickCallback _itemClickListener;
    private List<T> _items = new ArrayList();
    private List<T> mStringFilterList = new ArrayList();

    public abstract void bindHolder(BaseViewHolder baseViewHolder, int i);

    public void clearData() {
        int itemCount = getItemCount();
        this._items.clear();
        notifyItemRangeRemoved(0, itemCount);
    }

    public abstract BaseViewHolder<T> createHolder(ViewGroup viewGroup, int i);

    public T getItem(int i) {
        return this._items.get(i);
    }

    public OnItemClickCallback getItemClickListener() {
        return this._itemClickListener;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        List<T> list = this._items;
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    public List<T> getItems() {
        return this._items;
    }

    public List<T> getItemsFilter() {
        return this.mStringFilterList;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(BaseViewHolder baseViewHolder, int i) {
        bindHolder(baseViewHolder, i);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return createHolder(viewGroup, i);
    }

    public void removeItem(int i) {
        this._items.remove(i);
        notifyItemRemoved(i);
        notifyItemRangeChanged(i, this._items.size());
    }

    public void setItem(T t) {
        this._items.add(t);
        notifyItemInserted(this._items.size() - 1);
    }

    public void setItemClickListener(OnItemClickCallback onItemClickCallback) {
        this._itemClickListener = onItemClickCallback;
    }

    public void setItems(List<T> list) {
        this._items = list;
        notifyDataSetChanged();
    }

    public void setItemsFilter(List<T> list) {
        this.mStringFilterList = list;
    }

    public void updateItem(T t, int i) {
        this._items.set(i, t);
        notifyItemChanged(i, t);
    }
}
