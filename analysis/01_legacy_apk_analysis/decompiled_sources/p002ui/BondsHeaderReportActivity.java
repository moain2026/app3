package com.p001yd.electricecollector.p002ui;

import android.R;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.p001yd.electricecollector.Adapter.BondsHeaderReportAdapter;
import com.p001yd.electricecollector.C1018R;
import com.p001yd.electricecollector.TokenManager;
import com.p001yd.electricecollector.Utils;
import com.p001yd.electricecollector.ViewPeriod;
import com.p001yd.electricecollector.common.AppConfig;
import com.p001yd.electricecollector.entities.Accounts;
import com.p001yd.electricecollector.entities.BondsHeader;
import com.p001yd.electricecollector.entities.BondsHeaderResponse;
import com.p001yd.electricecollector.entities.Currency;
import com.p001yd.electricecollector.entities.CurrencyResponse;
import com.p001yd.electricecollector.entities.UserResponse;
import com.p001yd.electricecollector.entities.Users;
import com.p001yd.electricecollector.network.ApiService;
import com.p001yd.electricecollector.network.OnItemClickCallback;
import com.p001yd.electricecollector.network.RetrofitBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* loaded from: classes12.dex */
public class BondsHeaderReportActivity extends AppCompatActivity implements BaseView<BondsHeader> {
    private BondsHeaderReportAdapter _adapter;
    private AlertDialog _dialog;
    ArrayList<Users> aList;
    AppConfig appConfig;
    Call<BondsHeaderResponse> call;
    private ArrayList<Currency> curr_List;
    protected LinearLayout layoutOptions;
    LinearLayout progress;
    RecyclerView recyclerView;
    ApiService service;
    Spinner spCurrency;
    Spinner spUserAcct;
    SwipeRefreshLayout swipeRefreshLayout;
    TokenManager tokenManager;
    TextView tvCase;
    TextView tvEndDate;
    TextView tvStartDate;
    TextView tvTitle;
    TextView tvTotal1;
    TextView tvTotal2;
    TextView tvTotal3;
    public ViewPeriod viewPeriod;
    private final String TAG = getClass().getSimpleName();
    Map<Integer, ArrayList<Accounts>> accountMap = new HashMap();
    private int _datePickerInput = 0;
    private boolean _isSetDate = false;
    private int _pageNumber = 1;
    private int _pageSize = 30;
    private int[] _pagesCount = new int[1];
    int currencySelect = -1;
    int userSelect = -1;

    /* JADX INFO: Access modifiers changed from: private */
    public void AsyncDownloadStart() {
        this.progress.setVisibility(0);
        HashMap hashMap = new HashMap();
        hashMap.put("sdate", Utils.getShortDateStrApi(this, Utils.getDateFromString(this.tvStartDate.getText().toString())));
        hashMap.put("edate", Utils.getShortDateStrApi(this, Utils.getDateFromString(this.tvEndDate.getText().toString())));
        hashMap.put("appid", this.appConfig.getAppId());
        if (this.userSelect != -1) {
            hashMap.put("num", "" + this.userSelect);
        }
        if (this.currencySelect != -1) {
            hashMap.put("currency", "" + this.currencySelect);
        }
        RetrofitBuilder.BASE_URL = this.appConfig.getBaseUrl();
        Log.w(this.TAG, "_base_url: " + this.appConfig.getBaseUrl());
        this.service = (ApiService) RetrofitBuilder.createServiceWithAuth(ApiService.class, this.tokenManager, this.appConfig.getBaseUrl());
        this.call = this.service.GetRepBondsHeader(hashMap);
        this.call.enqueue(new Callback<BondsHeaderResponse>() { // from class: com.yd.electricecollector.ui.BondsHeaderReportActivity.7
            @Override // retrofit2.Callback
            public void onFailure(Call<BondsHeaderResponse> call, Throwable th) {
                Log.w(BondsHeaderReportActivity.this.TAG, "onFailure: " + th.getMessage());
                BondsHeaderReportActivity.this.progress.setVisibility(8);
            }

            @Override // retrofit2.Callback
            public void onResponse(Call<BondsHeaderResponse> call, Response<BondsHeaderResponse> response) {
                Log.w(BondsHeaderReportActivity.this.TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    if (response.body().getData() != null) {
                        BondsHeaderReportActivity.this._adapter.setItems(response.body().getData());
                        BondsHeaderReportActivity.this._adapter.setItemsFilter(BondsHeaderReportActivity.this._adapter.getItems());
                    }
                    BondsHeaderReportActivity.this.progress.setVisibility(8);
                }
                BondsHeaderReportActivity.this.progress.setVisibility(8);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void FillSpinnerAccounts(ArrayList<Users> arrayList) {
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add("كلي");
        if (arrayList != null) {
            Iterator<Users> it = arrayList.iterator();
            while (it.hasNext()) {
                arrayList2.add(it.next().getname());
            }
            ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.simple_spinner_item, arrayList2);
            arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            this.spUserAcct.setAdapter((SpinnerAdapter) arrayAdapter);
        }
    }

    private double getTotals(List<BondsHeader> list) {
        double d = 0.0d;
        for (BondsHeader bondsHeader : list) {
            d += bondsHeader.getMden() - bondsHeader.getDain();
        }
        this.tvTotal1.setText(Utils.numberToString(d));
        this.tvCase.setText(d > 0.0d ? "مدين" : "دائن");
        return 0.0d - d;
    }

    private void initializeView() {
        this.recyclerView = (RecyclerView) findViewById(C1018R.id.recycleV);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setHasFixedSize(true);
        this.progress = (LinearLayout) findViewById(C1018R.id.progressDownload);
    }

    private void loadAccounts() {
        HashMap hashMap = new HashMap();
        hashMap.put("appid", this.appConfig.getAppId());
        this.service = (ApiService) RetrofitBuilder.createServiceWithAuth(ApiService.class, this.tokenManager, this.appConfig.getBaseUrl());
        this.service.GetListUsers(hashMap).enqueue(new Callback<UserResponse>() { // from class: com.yd.electricecollector.ui.BondsHeaderReportActivity.9
            @Override // retrofit2.Callback
            public void onFailure(Call<UserResponse> call, Throwable th) {
                Log.w(BondsHeaderReportActivity.this.TAG, "onFailure: " + th.getMessage());
            }

            @Override // retrofit2.Callback
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                Log.w(BondsHeaderReportActivity.this.TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    BondsHeaderReportActivity.this.aList = (ArrayList) response.body().getData();
                    BondsHeaderReportActivity.this.FillSpinnerAccounts(BondsHeaderReportActivity.this.aList);
                }
            }
        });
    }

    private void loadCurrency() {
        this.service = (ApiService) RetrofitBuilder.createServiceWithAuth(ApiService.class, this.tokenManager, this.appConfig.getBaseUrl());
        this.service.GetListCurrency().enqueue(new Callback<CurrencyResponse>() { // from class: com.yd.electricecollector.ui.BondsHeaderReportActivity.8
            @Override // retrofit2.Callback
            public void onFailure(Call<CurrencyResponse> call, Throwable th) {
            }

            @Override // retrofit2.Callback
            public void onResponse(Call<CurrencyResponse> call, Response<CurrencyResponse> response) {
                Log.w(BondsHeaderReportActivity.this.TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    ArrayList arrayList = new ArrayList();
                    arrayList.add("كلي");
                    BondsHeaderReportActivity.this.curr_List = (ArrayList) response.body().getData();
                    if (BondsHeaderReportActivity.this.curr_List != null) {
                        Iterator it = BondsHeaderReportActivity.this.curr_List.iterator();
                        while (it.hasNext()) {
                            arrayList.add(((Currency) it.next()).getName());
                        }
                    }
                    ArrayAdapter arrayAdapter = new ArrayAdapter(BondsHeaderReportActivity.this, R.layout.simple_spinner_item, arrayList);
                    arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                    BondsHeaderReportActivity.this.spCurrency.setAdapter((SpinnerAdapter) arrayAdapter);
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openViewPeriod() {
        Intent intent = new Intent(getApplicationContext(), (Class<?>) ViewPeriodActivity.class);
        ViewPeriod viewPeriod = this.viewPeriod;
        Bundle bundle = new Bundle();
        bundle.putSerializable("VIEW_PERIOD", viewPeriod);
        intent.putExtras(bundle);
        startActivityForResult(intent, 5);
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 5 && i2 == -1) {
            try {
                ViewPeriod viewPeriod = (ViewPeriod) intent.getExtras().get("VIEW_PERIOD");
                if (viewPeriod != null) {
                    this.viewPeriod = viewPeriod;
                    AppConfig.getInstance().setViewPeriod(viewPeriod);
                    this.tvStartDate.setText(Utils.getShortDateStr((FragmentActivity) this, this.viewPeriod.startDate));
                    this.tvEndDate.setText(Utils.getShortDateStr((FragmentActivity) this, this.viewPeriod.endDate));
                    AsyncDownloadStart();
                    this.progress.setVisibility(8);
                }
            } catch (Exception e) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C1018R.layout.list_bonds_report_activity2);
        setTitle("اجمالي التحصيل ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initializeView();
        this.appConfig = AppConfig.getInstance();
        this.appConfig.getBaseUrl();
        this.appConfig.getToken();
        this.appConfig.getAppId();
        this.layoutOptions = (LinearLayout) findViewById(C1018R.id.layoutOptions);
        this.layoutOptions.setVisibility(8);
        ((TableLayout) findViewById(C1018R.id.tableRowCurrency)).setVisibility(8);
        this.aList = new ArrayList<>();
        this.spCurrency = (Spinner) findViewById(C1018R.id.spCurrencyFilter);
        this.spUserAcct = (Spinner) findViewById(C1018R.id.spnType);
        this.spCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.yd.electricecollector.ui.BondsHeaderReportActivity.1
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        this.spUserAcct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.yd.electricecollector.ui.BondsHeaderReportActivity.2
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                if (i <= 0) {
                    BondsHeaderReportActivity.this.userSelect = -1;
                } else {
                    BondsHeaderReportActivity.this.userSelect = BondsHeaderReportActivity.this.aList.get(i - 1).getNou();
                }
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        this.tvStartDate = (TextView) findViewById(C1018R.id.txtStartDate);
        this.tvEndDate = (TextView) findViewById(C1018R.id.txtEndDate);
        this.tvStartDate.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.BondsHeaderReportActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                BondsHeaderReportActivity.this.openViewPeriod();
            }
        });
        this.tvEndDate.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.BondsHeaderReportActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                BondsHeaderReportActivity.this.openViewPeriod();
            }
        });
        this.viewPeriod = AppConfig.getInstance().getViewPeriod();
        if (this.viewPeriod == null) {
            this.viewPeriod = new ViewPeriod(this);
        }
        this.tvStartDate.setText(Utils.getShortDateStr((FragmentActivity) this, this.viewPeriod.endDate));
        this.tvEndDate.setText(Utils.getShortDateStr((FragmentActivity) this, this.viewPeriod.endDate));
        this.tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", 0));
        this.tokenManager.getToken();
        this._pageSize = this.appConfig.getPageSize();
        this.swipeRefreshLayout = (SwipeRefreshLayout) findViewById(C1018R.id.swipeRefresh);
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() { // from class: com.yd.electricecollector.ui.BondsHeaderReportActivity.5
            @Override // androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
            public void onRefresh() {
                BondsHeaderReportActivity.this.AsyncDownloadStart();
                BondsHeaderReportActivity.this._adapter.notifyDataSetChanged();
                BondsHeaderReportActivity.this.swipeRefreshLayout.setRefreshing(false);
            }
        });
        this._adapter = new BondsHeaderReportAdapter();
        this._adapter.setItemClickListener(new OnItemClickCallback<BondsHeader>() { // from class: com.yd.electricecollector.ui.BondsHeaderReportActivity.6
            @Override // com.p001yd.electricecollector.network.OnItemClickCallback
            public void onItemClicked(BondsHeader bondsHeader) {
                Intent intent = new Intent(BondsHeaderReportActivity.this, (Class<?>) ListBondsReportActivity.class);
                intent.putExtra("data", bondsHeader);
                intent.putExtra("num", bondsHeader.getnum());
                intent.putExtra("mdate", bondsHeader.getDate());
                intent.putExtra("name", bondsHeader.getname());
                BondsHeaderReportActivity.this.startActivity(intent);
            }

            @Override // com.p001yd.electricecollector.network.OnItemClickCallback
            public void onItemClicked(BondsHeader bondsHeader, int i) {
            }
        });
        this.recyclerView.setAdapter(this._adapter);
        if (bundle == null) {
            AsyncDownloadStart();
            loadAccounts();
        }
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C1018R.menu.o_report, menu);
        menu.findItem(C1018R.id.search).setVisible(false);
        menu.findItem(C1018R.id.mnuItemPrint).setVisible(false);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        if (this.call != null) {
            this.call.cancel();
            this.call = null;
        }
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onFailed(BondsHeader bondsHeader) {
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onLoadDataFailure() {
        Toast.makeText(this, "فشل تحميل البيانات", 0).show();
        this.progress.setVisibility(8);
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onLoadDataSucceed(List<BondsHeader> list) {
        this._adapter.setItems(list);
        this.progress.setVisibility(8);
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.home:
                finish();
                return true;
            case C1018R.id.mnuItemOptionDropDown /* 2131362189 */:
                if (this.layoutOptions.getVisibility() == 0) {
                    this.layoutOptions.setVisibility(8);
                } else {
                    this.layoutOptions.setVisibility(0);
                }
                return true;
            case C1018R.id.mnuItemPrint /* 2131362190 */:
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    void onQueryTextChange2(String str) {
        this._adapter.getFilter().filter(str);
    }

    @Override // android.app.Activity
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        this.tvStartDate.setText(bundle.getString("tvStartDate"));
        this.tvEndDate.setText(bundle.getString("tvEndDate"));
        this.aList = bundle.getParcelableArrayList("aList");
        FillSpinnerAccounts(this.aList);
        if (this.aList.size() > 0) {
            this.spUserAcct.setSelection(bundle.getInt("acctpos"));
        }
        AsyncDownloadStart();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString("tvStartDate", this.tvStartDate.getText().toString());
        bundle.putString("tvEndDate", this.tvStartDate.getText().toString());
        bundle.putParcelableArrayList("aList", this.aList);
        bundle.putInt("acctpos", this.spUserAcct.getSelectedItemPosition());
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onSucceed(BondsHeader bondsHeader) {
    }
}
