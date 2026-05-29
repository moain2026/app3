package com.p001yd.electricecollector.menu;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.constraintlayout.widget.Constraints;
import com.p001yd.electricecollector.AppMenu;
import com.p001yd.electricecollector.C1018R;
import com.p001yd.electricecollector.p002ui.BaseViewAdapter;
import com.p001yd.electricecollector.p002ui.BaseViewHolder;

/* loaded from: classes15.dex */
public class MenuReportAdapter extends BaseViewAdapter<AppMenu> {
    private Context _context;

    /* loaded from: classes15.dex */
    class MenuReportViewHolder extends BaseViewHolder<AppMenu> {
        View divider;
        ImageView imgMenu;
        TextView txtMenuDescription;
        TextView txtMenuTitle;

        public MenuReportViewHolder(int i, ViewGroup viewGroup) {
            super(i, viewGroup);
            this.imgMenu = (ImageView) this.itemView.findViewById(C1018R.id.img_menu);
            this.txtMenuTitle = (TextView) this.itemView.findViewById(C1018R.id.mnu_title);
            this.txtMenuDescription = (TextView) this.itemView.findViewById(C1018R.id.menu_description);
            this.divider = this.itemView.findViewById(C1018R.id.divider);
        }

        @Override // com.p001yd.electricecollector.p002ui.BaseViewHolder
        public void bindView(AppMenu appMenu) {
            this.txtMenuTitle.setText(appMenu.getMenuTitle());
            this.txtMenuDescription.setText(appMenu.getMenuDescription());
        }
    }

    public MenuReportAdapter(Context context) {
        this._context = context;
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseViewAdapter
    public void bindHolder(BaseViewHolder baseViewHolder, int i) {
        final AppMenu item = getItem(i);
        baseViewHolder.bindView(item);
        if (item.isRead()) {
            Log.w(Constraints.TAG, "appMenu: true");
            baseViewHolder.itemView.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.menu.MenuReportAdapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (MenuReportAdapter.this.getItemClickListener() == null) {
                        return;
                    }
                    MenuReportAdapter.this.getItemClickListener().onItemClicked(item);
                }
            });
        } else {
            baseViewHolder.itemView.setAlpha(0.4f);
            Log.w(Constraints.TAG, "appMenu: false");
        }
        if (i == getItemCount() - 1) {
            ((MenuReportViewHolder) baseViewHolder).divider.setVisibility(8);
        }
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseViewAdapter
    public BaseViewHolder<AppMenu> createHolder(ViewGroup viewGroup, int i) {
        return new MenuReportViewHolder(C1018R.layout.item_list_menu, viewGroup);
    }

    @Override // android.widget.Filterable
    public Filter getFilter() {
        return null;
    }
}
