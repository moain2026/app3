package com.p001yd.electricecollector.p002ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.tabs.TabLayout;
import com.p001yd.electricecollector.Adapter.ListReadingReportAdapter;
import com.p001yd.electricecollector.C1018R;
import com.p001yd.electricecollector.LoginActivity;
import com.p001yd.electricecollector.TokenManager;
import com.p001yd.electricecollector.Utils;
import com.p001yd.electricecollector.common.AppConfig;
import com.p001yd.electricecollector.entities.Places;
import com.p001yd.electricecollector.entities.RepReading;
import com.p001yd.electricecollector.entities.RepReadingResponse;
import com.p001yd.electricecollector.entities.TGroup;
import com.p001yd.electricecollector.entities.Tblh;
import com.p001yd.electricecollector.network.ApiService;
import com.p001yd.electricecollector.network.OnItemClickCallback;
import com.p001yd.electricecollector.network.RetrofitBuilder;
import java.util.HashMap;
import java.util.List;
import okhttp3.WebSocket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* loaded from: classes12.dex */
public class ListReadingReportActivity extends AppCompatActivity implements BaseView<RepReading> {
    private static final String TAG = "ListReadingReportActivity";
    private ListReadingReportAdapter _adapter;
    private String _appId;
    private String _baseUrl;
    ReadingPresenter _presenter;
    private String _token;
    AppConfig appConfig;
    Button btnShow;
    Call<RepReadingResponse> call;
    List<TGroup> group_List;
    List<RepReading> listReport;
    List<Places> places_List;
    LinearLayout progress;
    RecyclerView recyclerView;
    ApiService service;
    TabLayout tab;
    List<Tblh> tblh_List;
    TokenManager tokenManager;
    TextView tv1;
    TextView tvCase;
    TextView tvTitle;
    TextView tvTotal1;
    WebSocket webSocket = null;
    int itemposation = -1;

    /* JADX INFO: Access modifiers changed from: private */
    public void AsyncDownloadStart() {
        HashMap hashMap = new HashMap();
        hashMap.put("type", "" + this.tab.getSelectedTabPosition());
        hashMap.put("appid", this.appConfig.getAppId());
        this.progress.setVisibility(0);
        this.service = (ApiService) RetrofitBuilder.createServiceWithAuth(ApiService.class, this.tokenManager, this.appConfig.getBaseUrl());
        this.call = this.service.GetListRepReading(hashMap);
        this.call.enqueue(new Callback<RepReadingResponse>() { // from class: com.yd.electricecollector.ui.ListReadingReportActivity.3
            @Override // retrofit2.Callback
            public void onFailure(Call<RepReadingResponse> call, Throwable th) {
                Log.w(ListReadingReportActivity.TAG, "onFailure: " + th.getMessage());
                ListReadingReportActivity.this.progress.setVisibility(8);
            }

            @Override // retrofit2.Callback
            public void onResponse(Call<RepReadingResponse> call, Response<RepReadingResponse> response) {
                Log.w(ListReadingReportActivity.TAG, "onResponse: " + response);
                if (!response.isSuccessful()) {
                    ListReadingReportActivity.this.tokenManager.deleteToken();
                    ListReadingReportActivity.this.startActivity(new Intent(ListReadingReportActivity.this, (Class<?>) LoginActivity.class));
                    ListReadingReportActivity.this.finish();
                } else if (response.body().getData() != null) {
                    ListReadingReportActivity.this.listReport = response.body().getData();
                    ListReadingReportActivity.this._adapter.setItems(response.body().getData());
                    ListReadingReportActivity.this._adapter.setItemsFilter(ListReadingReportActivity.this._adapter.getItems());
                    ListReadingReportActivity.this.progress.setVisibility(8);
                    ListReadingReportActivity.this.getTotals(ListReadingReportActivity.this.listReport);
                }
                ListReadingReportActivity.this.progress.setVisibility(8);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public double getTotals(List<RepReading> list) {
        double d = 0.0d;
        while (list.iterator().hasNext()) {
            d += Utils.stringToNumber(r0.next().getAst());
        }
        this.tvTotal1.setText(Utils.numberToString(d));
        this.tvCase.setVisibility(8);
        return d;
    }

    void getReports() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C1018R.layout.list_reading_activity2);
        setTitle("تقرير الاستهلاك");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.appConfig = AppConfig.getInstance();
        this._baseUrl = this.appConfig.getBaseUrl();
        this._appId = this.appConfig.getAppId();
        this.tvTotal1 = (TextView) findViewById(C1018R.id.tvTotal1);
        this.tvTitle = (TextView) findViewById(C1018R.id.tvTitle);
        this.tvCase = (TextView) findViewById(C1018R.id.tvCase);
        this.tvTitle.setText("اجمالي:");
        this.tab = (TabLayout) findViewById(C1018R.id.tabR);
        this.tab.setOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() { // from class: com.yd.electricecollector.ui.ListReadingReportActivity.1
            @Override // com.google.android.material.tabs.TabLayout.BaseOnTabSelectedListener
            public void onTabReselected(TabLayout.Tab tab) {
            }

            @Override // com.google.android.material.tabs.TabLayout.BaseOnTabSelectedListener
            public void onTabSelected(TabLayout.Tab tab) {
                ListReadingReportActivity.this.AsyncDownloadStart();
            }

            @Override // com.google.android.material.tabs.TabLayout.BaseOnTabSelectedListener
            public void onTabUnselected(TabLayout.Tab tab) {
            }
        });
        this.recyclerView = (RecyclerView) findViewById(C1018R.id.recycleV);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.addItemDecoration(new DividerItemDecoration(this, 1));
        this.progress = (LinearLayout) findViewById(C1018R.id.progressDownload);
        this.appConfig = AppConfig.getInstance();
        this.tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", 0));
        this.tokenManager.getToken();
        this._token = this.tokenManager.getToken().getAccessToken();
        this._adapter = new ListReadingReportAdapter();
        this.recyclerView.setAdapter(this._adapter);
        this._adapter.setItemClickListener(new OnItemClickCallback<RepReading>() { // from class: com.yd.electricecollector.ui.ListReadingReportActivity.2
            @Override // com.p001yd.electricecollector.network.OnItemClickCallback
            public void onItemClicked(RepReading repReading) {
            }

            @Override // com.p001yd.electricecollector.network.OnItemClickCallback
            public void onItemClicked(RepReading repReading, int i) {
            }
        });
        AsyncDownloadStart();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onFailed(RepReading repReading) {
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onLoadDataFailure() {
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onLoadDataSucceed(List<RepReading> list) {
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        finish();
        return true;
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onSucceed(RepReading repReading) {
    }
}
