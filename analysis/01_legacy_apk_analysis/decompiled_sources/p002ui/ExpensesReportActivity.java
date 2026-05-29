package com.p001yd.electricecollector.p002ui;

import android.R;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.p001yd.electricecollector.Adapter.BoxMoveDetailsAdapter;
import com.p001yd.electricecollector.C1018R;
import com.p001yd.electricecollector.TokenManager;
import com.p001yd.electricecollector.Utils;
import com.p001yd.electricecollector.ViewPeriod;
import com.p001yd.electricecollector.common.AppConfig;
import com.p001yd.electricecollector.entities.Accounts;
import com.p001yd.electricecollector.entities.Currency;
import com.p001yd.electricecollector.entities.RepBoxMovesDetals;
import com.p001yd.electricecollector.entities.RepExpensesResponse;
import com.p001yd.electricecollector.entities.UserResponse;
import com.p001yd.electricecollector.entities.Users;
import com.p001yd.electricecollector.network.ApiService;
import com.p001yd.electricecollector.network.RetrofitBuilder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* loaded from: classes12.dex */
public class ExpensesReportActivity extends AppCompatActivity implements BaseView<RepBoxMovesDetals> {
    private BoxMoveDetailsAdapter _adapter;
    private AlertDialog _dialog;
    ArrayList<Users> aList;
    AppConfig appConfig;
    ImageView btnLeft;
    ImageView btnRight;
    Call<RepExpensesResponse> call;
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
    private Date currDate = new Date();

    /* JADX INFO: Access modifiers changed from: private */
    public void AsyncDownloadStart() {
        this.progress.setVisibility(0);
        HashMap hashMap = new HashMap();
        hashMap.put("sdate", Utils.getShortDateStrApi(this, Utils.getDateFromString(this.tvStartDate.getText().toString())));
        hashMap.put("appid", this.appConfig.getAppId());
        RetrofitBuilder.BASE_URL = this.appConfig.getBaseUrl();
        Log.w(this.TAG, "_base_url: " + this.appConfig.getBaseUrl());
        this.service = (ApiService) RetrofitBuilder.createServiceWithAuth(ApiService.class, this.tokenManager, this.appConfig.getBaseUrl());
        this.call = this.service.GetRepExpenses(hashMap);
        this.call.enqueue(new Callback<RepExpensesResponse>() { // from class: com.yd.electricecollector.ui.ExpensesReportActivity.5
            @Override // retrofit2.Callback
            public void onFailure(Call<RepExpensesResponse> call, Throwable th) {
                Log.w(ExpensesReportActivity.this.TAG, "onFailure: " + th.getMessage());
                ExpensesReportActivity.this.progress.setVisibility(8);
            }

            @Override // retrofit2.Callback
            public void onResponse(Call<RepExpensesResponse> call, Response<RepExpensesResponse> response) {
                Log.w(ExpensesReportActivity.this.TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    if (response.body().getData() != null) {
                        ExpensesReportActivity.this._adapter.setItems(response.body().getData());
                        ExpensesReportActivity.this._adapter.setItemsFilter(ExpensesReportActivity.this._adapter.getItems());
                    }
                    ExpensesReportActivity.this.progress.setVisibility(8);
                }
                ExpensesReportActivity.this.progress.setVisibility(8);
            }
        });
    }

    private double getTotals(List<RepBoxMovesDetals> list) {
        for (RepBoxMovesDetals repBoxMovesDetals : list) {
        }
        this.tvTotal1.setText(Utils.numberToString(0.0d));
        this.tvCase.setText(0.0d > 0.0d ? "مدين" : "دائن");
        return 0.0d - 0.0d;
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
        this.service.GetListUsers(hashMap).enqueue(new Callback<UserResponse>() { // from class: com.yd.electricecollector.ui.ExpensesReportActivity.6
            @Override // retrofit2.Callback
            public void onFailure(Call<UserResponse> call, Throwable th) {
                Log.w(ExpensesReportActivity.this.TAG, "onFailure: " + th.getMessage());
            }

            @Override // retrofit2.Callback
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                Log.w(ExpensesReportActivity.this.TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    ArrayList arrayList = new ArrayList();
                    arrayList.add("كلي");
                    ExpensesReportActivity.this.aList = (ArrayList) response.body().getData();
                    if (ExpensesReportActivity.this.aList != null) {
                        Iterator<Users> it = ExpensesReportActivity.this.aList.iterator();
                        while (it.hasNext()) {
                            arrayList.add(it.next().getname());
                        }
                        ArrayAdapter arrayAdapter = new ArrayAdapter(ExpensesReportActivity.this, R.layout.simple_spinner_item, arrayList);
                        arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                        ExpensesReportActivity.this.spUserAcct.setAdapter((SpinnerAdapter) arrayAdapter);
                    }
                }
            }
        });
    }

    private void openViewPeriod() {
        Intent intent = new Intent(getApplicationContext(), (Class<?>) ViewPeriodActivity.class);
        ViewPeriod viewPeriod = this.viewPeriod;
        Bundle bundle = new Bundle();
        bundle.putSerializable("VIEW_PERIOD", viewPeriod);
        intent.putExtras(bundle);
        startActivityForResult(intent, 5);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C1018R.layout.list_box_moves_details_report_activity);
        setTitle("المصروفات اليومية ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initializeView();
        this.appConfig = AppConfig.getInstance();
        this.layoutOptions = (LinearLayout) findViewById(C1018R.id.layoutOptions);
        this.layoutOptions.setVisibility(8);
        this.tvStartDate = (TextView) findViewById(C1018R.id.edtDate);
        this.btnRight = (ImageView) findViewById(C1018R.id.btnRight);
        this.btnLeft = (ImageView) findViewById(C1018R.id.btnLeft);
        this.btnRight.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.ExpensesReportActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(ExpensesReportActivity.this.currDate);
                calendar.add(5, 1);
                ExpensesReportActivity.this.currDate = calendar.getTime();
                ExpensesReportActivity.this.tvStartDate.setText(Utils.getShortDateStr((FragmentActivity) ExpensesReportActivity.this, calendar.getTime()));
                ExpensesReportActivity.this.AsyncDownloadStart();
            }
        });
        this.btnLeft.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.ExpensesReportActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(ExpensesReportActivity.this.currDate);
                calendar.add(5, -1);
                ExpensesReportActivity.this.currDate = calendar.getTime();
                ExpensesReportActivity.this.tvStartDate.setText(Utils.getShortDateStr((FragmentActivity) ExpensesReportActivity.this, calendar.getTime()));
                ExpensesReportActivity.this.AsyncDownloadStart();
            }
        });
        this.tvStartDate.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.ExpensesReportActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(ExpensesReportActivity.this.currDate);
                new DatePickerDialog(ExpensesReportActivity.this, new DatePickerDialog.OnDateSetListener() { // from class: com.yd.electricecollector.ui.ExpensesReportActivity.3.1
                    @Override // android.app.DatePickerDialog.OnDateSetListener
                    public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
                        Calendar calendar2 = Calendar.getInstance();
                        calendar2.set(i, i2, i3);
                        ExpensesReportActivity.this.tvStartDate.setText(Utils.getShortDateStr((FragmentActivity) ExpensesReportActivity.this, calendar2.getTime()));
                        ExpensesReportActivity.this.currDate = calendar2.getTime();
                        ExpensesReportActivity.this.AsyncDownloadStart();
                    }
                }, calendar.get(1), calendar.get(2), calendar.get(5)).show();
            }
        });
        this.viewPeriod = AppConfig.getInstance().getViewPeriod();
        if (this.viewPeriod == null) {
            this.viewPeriod = new ViewPeriod(this);
        }
        this.tvStartDate.setText(Utils.getShortDateStr((FragmentActivity) this, this.viewPeriod.endDate));
        this.tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", 0));
        this.tokenManager.getToken();
        this._pageSize = this.appConfig.getPageSize();
        this.swipeRefreshLayout = (SwipeRefreshLayout) findViewById(C1018R.id.swipeRefresh);
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() { // from class: com.yd.electricecollector.ui.ExpensesReportActivity.4
            @Override // androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
            public void onRefresh() {
                ExpensesReportActivity.this.AsyncDownloadStart();
                ExpensesReportActivity.this._adapter.notifyDataSetChanged();
                ExpensesReportActivity.this.swipeRefreshLayout.setRefreshing(false);
            }
        });
        this._adapter = new BoxMoveDetailsAdapter();
        this.recyclerView.setAdapter(this._adapter);
        if (bundle == null) {
            AsyncDownloadStart();
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
    public void onFailed(RepBoxMovesDetals repBoxMovesDetals) {
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onLoadDataFailure() {
        Toast.makeText(this, "فشل تحميل البيانات", 0).show();
        this.progress.setVisibility(8);
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onLoadDataSucceed(List<RepBoxMovesDetals> list) {
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

    @Override // android.app.Activity
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        this.tvStartDate.setText(bundle.getString("tvStartDate"));
        this.currDate = Utils.getDateFromString(bundle.getString("tvStartDate"));
        AsyncDownloadStart();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString("tvStartDate", this.tvStartDate.getText().toString());
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onSucceed(RepBoxMovesDetals repBoxMovesDetals) {
    }
}
