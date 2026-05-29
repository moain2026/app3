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
import com.p001yd.electricecollector.entities.RepBoxMoves;
import com.p001yd.electricecollector.entities.RepBoxMovesDetailsResponse;
import com.p001yd.electricecollector.entities.RepBoxMovesDetals;
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
public class BoxMovesDetailsReportActivity extends AppCompatActivity implements BaseView<RepBoxMovesDetals> {
    private BoxMoveDetailsAdapter _adapter;
    ArrayList<Users> aList;
    AppConfig appConfig;
    ImageView btnLeft;
    ImageView btnRight;
    Call<RepBoxMovesDetailsResponse> call;
    private ArrayList<Currency> curr_List;
    protected LinearLayout layoutOptions;
    LinearLayout progress;
    RecyclerView recyclerView;
    ApiService service;
    Spinner spUserAcct;
    SwipeRefreshLayout swipeRefreshLayout;
    TokenManager tokenManager;
    TextView tvCase;
    TextView tvStartDate;
    TextView tvTotal1;
    public ViewPeriod viewPeriod;
    private final String TAG = getClass().getSimpleName();
    Map<Integer, ArrayList<Accounts>> accountMap = new HashMap();
    RepBoxMoves repBoxMoves = null;
    private Date currDate = new Date();

    /* JADX INFO: Access modifiers changed from: private */
    public void AsyncDownloadStart() {
        this.progress.setVisibility(0);
        HashMap hashMap = new HashMap();
        hashMap.put("sdate", Utils.getShortDateStrApi(this, Utils.getDateFromString(this.tvStartDate.getText().toString())));
        if (this.repBoxMoves != null) {
            hashMap.put("num", "" + this.repBoxMoves.getnum());
        }
        hashMap.put("appid", this.appConfig.getAppId());
        RetrofitBuilder.BASE_URL = this.appConfig.getBaseUrl();
        Log.w(this.TAG, "_base_url: " + this.appConfig.getBaseUrl());
        this.service = (ApiService) RetrofitBuilder.createServiceWithAuth(ApiService.class, this.tokenManager, this.appConfig.getBaseUrl());
        this.call = this.service.GetRepBoxMoveDetails(hashMap);
        this.call.enqueue(new Callback<RepBoxMovesDetailsResponse>() { // from class: com.yd.electricecollector.ui.BoxMovesDetailsReportActivity.5
            @Override // retrofit2.Callback
            public void onFailure(Call<RepBoxMovesDetailsResponse> call, Throwable th) {
                Log.w(BoxMovesDetailsReportActivity.this.TAG, "onFailure: " + th.getMessage());
                BoxMovesDetailsReportActivity.this.progress.setVisibility(8);
            }

            @Override // retrofit2.Callback
            public void onResponse(Call<RepBoxMovesDetailsResponse> call, Response<RepBoxMovesDetailsResponse> response) {
                Log.w(BoxMovesDetailsReportActivity.this.TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    if (response.body().getData() != null) {
                        BoxMovesDetailsReportActivity.this._adapter.setItems(response.body().getData());
                        BoxMovesDetailsReportActivity.this._adapter.setItemsFilter(BoxMovesDetailsReportActivity.this._adapter.getItems());
                    }
                    BoxMovesDetailsReportActivity.this.progress.setVisibility(8);
                }
                BoxMovesDetailsReportActivity.this.progress.setVisibility(8);
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
        this.service.GetListUsers(hashMap).enqueue(new Callback<UserResponse>() { // from class: com.yd.electricecollector.ui.BoxMovesDetailsReportActivity.6
            @Override // retrofit2.Callback
            public void onFailure(Call<UserResponse> call, Throwable th) {
                Log.w(BoxMovesDetailsReportActivity.this.TAG, "onFailure: " + th.getMessage());
            }

            @Override // retrofit2.Callback
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                Log.w(BoxMovesDetailsReportActivity.this.TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    ArrayList arrayList = new ArrayList();
                    arrayList.add("كلي");
                    BoxMovesDetailsReportActivity.this.aList = (ArrayList) response.body().getData();
                    if (BoxMovesDetailsReportActivity.this.aList != null) {
                        Iterator<Users> it = BoxMovesDetailsReportActivity.this.aList.iterator();
                        while (it.hasNext()) {
                            arrayList.add(it.next().getname());
                        }
                        ArrayAdapter arrayAdapter = new ArrayAdapter(BoxMovesDetailsReportActivity.this, R.layout.simple_spinner_item, arrayList);
                        arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                        BoxMovesDetailsReportActivity.this.spUserAcct.setAdapter((SpinnerAdapter) arrayAdapter);
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
        setTitle("حركة الصندوق تفصيلي ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.repBoxMoves = (RepBoxMoves) getIntent().getParcelableExtra("data");
        initializeView();
        this.appConfig = AppConfig.getInstance();
        this.appConfig.getBaseUrl();
        this.appConfig.getToken();
        this.appConfig.getAppId();
        this.layoutOptions = (LinearLayout) findViewById(C1018R.id.layoutOptions);
        this.layoutOptions.setVisibility(8);
        this.tvStartDate = (TextView) findViewById(C1018R.id.edtDate);
        this.btnRight = (ImageView) findViewById(C1018R.id.btnRight);
        this.btnLeft = (ImageView) findViewById(C1018R.id.btnLeft);
        this.btnRight.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.BoxMovesDetailsReportActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(BoxMovesDetailsReportActivity.this.currDate);
                calendar.add(5, 1);
                BoxMovesDetailsReportActivity.this.currDate = calendar.getTime();
                BoxMovesDetailsReportActivity.this.tvStartDate.setText(Utils.getShortDateStr((FragmentActivity) BoxMovesDetailsReportActivity.this, calendar.getTime()));
                BoxMovesDetailsReportActivity.this.AsyncDownloadStart();
            }
        });
        this.btnLeft.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.BoxMovesDetailsReportActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(BoxMovesDetailsReportActivity.this.currDate);
                calendar.add(5, -1);
                BoxMovesDetailsReportActivity.this.currDate = calendar.getTime();
                BoxMovesDetailsReportActivity.this.tvStartDate.setText(Utils.getShortDateStr((FragmentActivity) BoxMovesDetailsReportActivity.this, calendar.getTime()));
                BoxMovesDetailsReportActivity.this.AsyncDownloadStart();
            }
        });
        this.tvStartDate.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.BoxMovesDetailsReportActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(BoxMovesDetailsReportActivity.this.currDate);
                new DatePickerDialog(BoxMovesDetailsReportActivity.this, new DatePickerDialog.OnDateSetListener() { // from class: com.yd.electricecollector.ui.BoxMovesDetailsReportActivity.3.1
                    @Override // android.app.DatePickerDialog.OnDateSetListener
                    public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
                        Calendar calendar2 = Calendar.getInstance();
                        calendar2.set(i, i2, i3);
                        BoxMovesDetailsReportActivity.this.tvStartDate.setText(Utils.getShortDateStr((FragmentActivity) BoxMovesDetailsReportActivity.this, calendar2.getTime()));
                        BoxMovesDetailsReportActivity.this.currDate = calendar2.getTime();
                        BoxMovesDetailsReportActivity.this.AsyncDownloadStart();
                    }
                }, calendar.get(1), calendar.get(2), calendar.get(5)).show();
            }
        });
        this.viewPeriod = AppConfig.getInstance().getViewPeriod();
        if (this.viewPeriod == null) {
            this.viewPeriod = new ViewPeriod(this);
        }
        this.tvStartDate.setText(this.repBoxMoves.getDate());
        this.currDate = Utils.getDateFromString(this.tvStartDate.getText().toString());
        this.tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", 0));
        this.tokenManager.getToken();
        this.swipeRefreshLayout = (SwipeRefreshLayout) findViewById(C1018R.id.swipeRefresh);
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() { // from class: com.yd.electricecollector.ui.BoxMovesDetailsReportActivity.4
            @Override // androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
            public void onRefresh() {
                BoxMovesDetailsReportActivity.this.AsyncDownloadStart();
                BoxMovesDetailsReportActivity.this._adapter.notifyDataSetChanged();
                BoxMovesDetailsReportActivity.this.swipeRefreshLayout.setRefreshing(false);
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
        Log.w(this.TAG, "currDateG: " + this.currDate);
        AsyncDownloadStart();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString("tvStartDate", this.tvStartDate.getText().toString());
        bundle.putString("currDate", this.currDate.toString());
        Log.w(this.TAG, "currDateS: " + this.currDate.toString());
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onSucceed(RepBoxMovesDetals repBoxMovesDetals) {
    }
}
