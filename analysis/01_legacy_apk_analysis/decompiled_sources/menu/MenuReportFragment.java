package com.p001yd.electricecollector.menu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.p001yd.electricecollector.AppMenu;
import com.p001yd.electricecollector.C1018R;
import com.p001yd.electricecollector.HakAccessHelper;
import com.p001yd.electricecollector.common.AppConfig;
import com.p001yd.electricecollector.entities.HakAccess;
import com.p001yd.electricecollector.network.OnItemClickCallback;
import com.p001yd.electricecollector.p002ui.BalanceStateDetailsReportActivity;
import com.p001yd.electricecollector.p002ui.BalanceStateReportActivity;
import com.p001yd.electricecollector.p002ui.BaseView;
import com.p001yd.electricecollector.p002ui.BondsHeaderReportActivity;
import com.p001yd.electricecollector.p002ui.BoxMovesReportActivity;
import com.p001yd.electricecollector.p002ui.ExpensesReportActivity;
import com.p001yd.electricecollector.p002ui.ListReadingReportActivity;
import java.util.List;

/* loaded from: classes15.dex */
public class MenuReportFragment extends Fragment implements BaseView<AppMenu> {
    private final String TAG = getClass().getSimpleName();
    private MenuReportAdapter _adapter;
    private AppConfig _appConfig;
    private Context _context;
    private MenuReportPresenter _presenter;
    RecyclerView recyclerView;

    private void initializeView(View view) {
        this.recyclerView = (RecyclerView) view.findViewById(C1018R.id.recycle_menu);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this._context));
        this.recyclerView.setHasFixedSize(true);
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this._appConfig = AppConfig.getInstance();
    }

    @Override // androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this._context = context;
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(C1018R.layout.fragment_menu_report, viewGroup, false);
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onFailed(AppMenu appMenu) {
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onLoadDataFailure() {
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onLoadDataSucceed(List<AppMenu> list) {
        for (AppMenu appMenu : list) {
            HakAccess hakAkses = HakAccessHelper.getHakAkses(appMenu.getMenuId(), this._appConfig.getListHakAccess());
            if (hakAkses != null) {
                appMenu.setCreate(hakAkses.isCreate());
                appMenu.setRead(hakAkses.isRead());
                appMenu.setUpdate(hakAkses.isUpdate());
                appMenu.setDelete(hakAkses.isDelete());
            }
        }
        this._adapter.setItems(list);
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onSucceed(AppMenu appMenu) {
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        initializeView(view);
        this._adapter = new MenuReportAdapter(this._context);
        this._adapter.setItemClickListener(new OnItemClickCallback<AppMenu>() { // from class: com.yd.electricecollector.menu.MenuReportFragment.1
            /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
            @Override // com.p001yd.electricecollector.network.OnItemClickCallback
            public void onItemClicked(AppMenu appMenu) {
                char c;
                Intent intent;
                String menuId = appMenu.getMenuId();
                switch (menuId.hashCode()) {
                    case -2004182589:
                        if (menuId.equals("repBalanceDetails")) {
                            c = 1;
                            break;
                        }
                        c = 65535;
                        break;
                    case -1064137859:
                        if (menuId.equals("repCollectorMony")) {
                            c = 2;
                            break;
                        }
                        c = 65535;
                        break;
                    case -1059076372:
                        if (menuId.equals("repBalanceHeader")) {
                            c = 0;
                            break;
                        }
                        c = 65535;
                        break;
                    case -1030307023:
                        if (menuId.equals("repListReading")) {
                            c = 3;
                            break;
                        }
                        c = 65535;
                        break;
                    case 366602100:
                        if (menuId.equals("repBoxMoves")) {
                            c = 4;
                            break;
                        }
                        c = 65535;
                        break;
                    case 490762968:
                        if (menuId.equals("repExpenses")) {
                            c = 5;
                            break;
                        }
                        c = 65535;
                        break;
                    default:
                        c = 65535;
                        break;
                }
                switch (c) {
                    case 0:
                        intent = new Intent(MenuReportFragment.this.getContext(), (Class<?>) BalanceStateReportActivity.class);
                        intent.putExtra("title", appMenu.getMenuTitle());
                        break;
                    case 1:
                        intent = new Intent(MenuReportFragment.this.getContext(), (Class<?>) BalanceStateDetailsReportActivity.class);
                        intent.putExtra("title", appMenu.getMenuTitle());
                        intent.putExtra("mode", 1);
                        break;
                    case 2:
                        intent = new Intent(MenuReportFragment.this.getContext(), (Class<?>) BondsHeaderReportActivity.class);
                        intent.putExtra("title", appMenu.getMenuTitle());
                        break;
                    case 3:
                        intent = new Intent(MenuReportFragment.this.getContext(), (Class<?>) ListReadingReportActivity.class);
                        intent.putExtra("title", appMenu.getMenuTitle());
                        break;
                    case 4:
                        intent = new Intent(MenuReportFragment.this.getContext(), (Class<?>) BoxMovesReportActivity.class);
                        intent.putExtra("title", appMenu.getMenuTitle());
                        break;
                    case 5:
                        intent = new Intent(MenuReportFragment.this.getContext(), (Class<?>) ExpensesReportActivity.class);
                        intent.putExtra("title", appMenu.getMenuTitle());
                        break;
                    default:
                        intent = null;
                        break;
                }
                if (intent != null) {
                    MenuReportFragment.this.startActivity(intent);
                }
            }

            @Override // com.p001yd.electricecollector.network.OnItemClickCallback
            public void onItemClicked(AppMenu appMenu, int i) {
            }
        });
        this.recyclerView.setAdapter(this._adapter);
        this._presenter = new MenuReportPresenter(this, this._context);
        this._presenter.loadData();
    }
}
