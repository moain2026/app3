package com.p001yd.electricecollector.menu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.p001yd.electricecollector.p002ui.BaseView;
import com.p001yd.electricecollector.p002ui.ListBondsActivity;
import com.p001yd.electricecollector.p002ui.ListReadingActivity;
import java.util.List;

/* loaded from: classes15.dex */
public class MenuOprationFragment extends Fragment implements BaseView<AppMenu> {
    private final String TAG = getClass().getSimpleName();
    private MenuReportAdapter _adapter;
    private AppConfig _appConfig;
    private Context _context;
    private MenuOprationPresenter _presenter;
    RecyclerView recyclerView;

    private void initializeView(View view) {
        this.recyclerView = (RecyclerView) view.findViewById(C1018R.id.recycle_menu);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this._context));
        this.recyclerView.setHasFixedSize(true);
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this._appConfig = (AppConfig) getActivity().getApplication();
    }

    @Override // androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this._context = context;
        Log.v(this.TAG, "onAttach");
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.v(this.TAG, "onCreate");
        this._appConfig = AppConfig.getInstance();
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(C1018R.layout.fragment_menu_report, viewGroup, false);
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        Log.v(this.TAG, "onDestroyView");
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
            HakAccess hakAkses = HakAccessHelper.getHakAkses(appMenu.getMenuId(), this._appConfig.getUser().getHakAkses());
            if (hakAkses != null) {
                appMenu.setCreate(hakAkses.isCreate());
                appMenu.setRead(hakAkses.isRead());
                appMenu.setUpdate(hakAkses.isUpdate());
                appMenu.setDelete(hakAkses.isDelete());
            }
        }
        this._adapter.setItems(list);
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        Log.v(this.TAG, "onPause");
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        Log.v(this.TAG, "onResume");
    }

    @Override // androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        Log.v(this.TAG, "onSaveinstanceState");
        super.onSaveInstanceState(bundle);
    }

    @Override // androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        Log.v(this.TAG, "onStart");
    }

    @Override // androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        Log.v(this.TAG, "onStop");
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onSucceed(AppMenu appMenu) {
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        Log.v(this.TAG, "onViewCreated");
        initializeView(view);
        this._adapter = new MenuReportAdapter(this._context);
        this._adapter.setItemClickListener(new OnItemClickCallback<AppMenu>() { // from class: com.yd.electricecollector.menu.MenuOprationFragment.1
            /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
            @Override // com.p001yd.electricecollector.network.OnItemClickCallback
            public void onItemClicked(AppMenu appMenu) {
                char c;
                Intent intent;
                String menuId = appMenu.getMenuId();
                switch (menuId.hashCode()) {
                    case -985349357:
                        if (menuId.equals("repBondsPayment")) {
                            c = 1;
                            break;
                        }
                        c = 65535;
                        break;
                    case 883738045:
                        if (menuId.equals("repBondsReciept")) {
                            c = 0;
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
                        intent = new Intent(MenuOprationFragment.this.getContext(), (Class<?>) ListBondsActivity.class);
                        intent.putExtra("title", appMenu.getMenuTitle());
                        break;
                    case 1:
                        intent = new Intent(MenuOprationFragment.this.getContext(), (Class<?>) ListReadingActivity.class);
                        intent.putExtra("title", appMenu.getMenuTitle());
                        break;
                    default:
                        intent = null;
                        break;
                }
                if (intent != null) {
                    MenuOprationFragment.this.startActivity(intent);
                }
            }

            @Override // com.p001yd.electricecollector.network.OnItemClickCallback
            public void onItemClicked(AppMenu appMenu, int i) {
            }
        });
        this.recyclerView.setAdapter(this._adapter);
        this._presenter = new MenuOprationPresenter(this, this._context);
        this._presenter.loadData();
    }
}
