package com.p001yd.electricecollector.p002ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.p001yd.electricecollector.Adapter.BalanceStateAdapter;
import com.p001yd.electricecollector.C1018R;
import com.p001yd.electricecollector.LoginActivity;
import com.p001yd.electricecollector.TokenManager;
import com.p001yd.electricecollector.common.AppConfig;
import com.p001yd.electricecollector.entities.AccessToken;
import com.p001yd.electricecollector.entities.BalanceState;
import com.p001yd.electricecollector.entities.PostResponse;
import com.p001yd.electricecollector.network.ApiService;
import com.p001yd.electricecollector.p002ui.BalanceStateReportActivity;
import com.p001yd.electricecollector.p002ui.BaseView;
import com.p001yd.electricecollector.p002ui.ListBondsActivity;
import java.util.List;
import retrofit2.Call;

/* loaded from: classes15.dex */
public class HomeFragment extends Fragment implements BaseView<BalanceState> {
    private final String TAG = getClass().getSimpleName();
    private BalanceStateAdapter _adapter;
    private AppConfig _appConfig;
    private Context _context;
    Call<PostResponse> call;
    private HomeViewModel homeViewModel;
    LinearLayout progress;
    ApiService service;
    TokenManager tokenManager;

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.homeViewModel = (HomeViewModel) ViewModelProviders.m49of(this).get(HomeViewModel.class);
        View inflate = layoutInflater.inflate(C1018R.layout.fragment_home, viewGroup, false);
        final TextView textView = (TextView) inflate.findViewById(C1018R.id.text_home);
        Button button = (Button) inflate.findViewById(C1018R.id.button);
        Button button2 = (Button) inflate.findViewById(C1018R.id.button2);
        Button button3 = (Button) inflate.findViewById(C1018R.id.button3);
        Button button4 = (Button) inflate.findViewById(C1018R.id.button4);
        ((Button) inflate.findViewById(C1018R.id.button5)).setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.home.HomeFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
            }
        });
        button4.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.home.HomeFragment.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                HomeFragment.this.startActivity(new Intent(HomeFragment.this.getActivity(), (Class<?>) LoginActivity.class));
            }
        });
        this.homeViewModel.getText().observe(this, new Observer<AccessToken>() { // from class: com.yd.electricecollector.ui.home.HomeFragment.3
            @Override // androidx.lifecycle.Observer
            public void onChanged(AccessToken accessToken) {
                textView.setText(accessToken.getAccessToken());
            }
        });
        button.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.home.HomeFragment.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                HomeFragment.this.startActivity(new Intent(HomeFragment.this.getActivity(), (Class<?>) BalanceStateReportActivity.class));
            }
        });
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.home.HomeFragment.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                HomeFragment.this.startActivity(new Intent(HomeFragment.this.getActivity(), (Class<?>) ListBondsActivity.class));
            }
        });
        button3.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.home.HomeFragment.6
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
            }
        });
        return inflate;
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onFailed(BalanceState balanceState) {
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onLoadDataFailure() {
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onLoadDataSucceed(List<BalanceState> list) {
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onSucceed(BalanceState balanceState) {
    }
}
