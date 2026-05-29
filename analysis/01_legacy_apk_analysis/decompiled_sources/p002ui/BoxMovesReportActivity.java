package com.p001yd.electricecollector.p002ui;

import android.R;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.p001yd.electricecollector.Adapter.AccounttSearchAdapter;
import com.p001yd.electricecollector.Adapter.BoxMoveAdapter;
import com.p001yd.electricecollector.C1018R;
import com.p001yd.electricecollector.LoginActivity;
import com.p001yd.electricecollector.Pdf_temp;
import com.p001yd.electricecollector.TokenManager;
import com.p001yd.electricecollector.Utils;
import com.p001yd.electricecollector.ViewPeriod;
import com.p001yd.electricecollector.common.AppConfig;
import com.p001yd.electricecollector.entities.Accounts;
import com.p001yd.electricecollector.entities.Currency;
import com.p001yd.electricecollector.entities.RepBoxMoves;
import com.p001yd.electricecollector.entities.RepBoxMovesResponse;
import com.p001yd.electricecollector.network.ApiService;
import com.p001yd.electricecollector.network.OnItemClickCallback;
import com.p001yd.electricecollector.network.RetrofitBuilder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* loaded from: classes12.dex */
public class BoxMovesReportActivity extends AppCompatActivity implements BaseView<RepBoxMoves> {
    List<RepBoxMoves> ListBalance;
    private BoxMoveAdapter _adapter;
    private AlertDialog _dialog;
    List<Accounts> aList;
    AccounttSearchAdapter acAdp;
    private ArrayList<Accounts> acctList;
    AppConfig appConfig;
    ImageView btnLeft;
    ImageView btnRight;
    Call<RepBoxMovesResponse> call;
    private ArrayList<Currency> curr_List;
    protected LinearLayout layoutOptions;
    Pdf_temp pdf_temp;
    LinearLayout progress;
    RecyclerView recyclerView;
    ApiService service;
    SwipeRefreshLayout swipeRefreshLayout;
    TokenManager tokenManager;
    TextView tvCase;
    TextView tvStartDate;
    TextView tvTotal1;
    public ViewPeriod viewPeriod;
    private final String TAG = getClass().getSimpleName();
    Map<Integer, ArrayList<Accounts>> accountMap = new HashMap();
    private int _datePickerInput = 0;
    private boolean _isSetDate = false;
    private int _pageNumber = 1;
    private int _pageSize = 30;
    private int[] _pagesCount = new int[1];
    public Date currDate = new Date();

    /* JADX INFO: Access modifiers changed from: private */
    public void AsyncDownloadStart() {
        HashMap hashMap = new HashMap();
        hashMap.put("sdate", Utils.getShortDateStrApi(this, Utils.getDateFromString(this.tvStartDate.getText().toString())));
        hashMap.put("appid", this.appConfig.getAppId());
        this.progress.setVisibility(0);
        Log.w(this.TAG, "_base_url: " + this.appConfig.getBaseUrl());
        if (this.appConfig.getBaseUrl() != null) {
            this.service = (ApiService) RetrofitBuilder.createServiceWithAuth(ApiService.class, this.tokenManager, this.appConfig.getBaseUrl());
            this.call = this.service.GetRepBoxMove(hashMap);
            this.call.enqueue(new Callback<RepBoxMovesResponse>() { // from class: com.yd.electricecollector.ui.BoxMovesReportActivity.6
                @Override // retrofit2.Callback
                public void onFailure(Call<RepBoxMovesResponse> call, Throwable th) {
                    Log.w(BoxMovesReportActivity.this.TAG, "onFailure: " + th.getMessage());
                    BoxMovesReportActivity.this.progress.setVisibility(8);
                }

                @Override // retrofit2.Callback
                public void onResponse(Call<RepBoxMovesResponse> call, Response<RepBoxMovesResponse> response) {
                    Log.w(BoxMovesReportActivity.this.TAG, "onResponse: " + response);
                    BoxMovesReportActivity.this.ListBalance = new ArrayList();
                    if (response.isSuccessful()) {
                        BoxMovesReportActivity.this.ListBalance = response.body().getData();
                        if (BoxMovesReportActivity.this.ListBalance != null) {
                            BoxMovesReportActivity.this._adapter.setItems(response.body().getData());
                            BoxMovesReportActivity.this._adapter.setItemsFilter(BoxMovesReportActivity.this._adapter.getItems());
                        }
                        BoxMovesReportActivity.this.progress.setVisibility(8);
                    } else {
                        response.code();
                    }
                    BoxMovesReportActivity.this.progress.setVisibility(8);
                }
            });
        }
    }

    private double getTotals(List<RepBoxMoves> list) {
        for (RepBoxMoves repBoxMoves : list) {
        }
        this.tvTotal1.setText(Utils.numberToString(0.0d));
        if (0.0d > 0.0d) {
            this.tvTotal1.setTextColor(Color.parseColor("#D81B60"));
        } else if (0.0d < 0.0d) {
            this.tvTotal1.setTextColor(Color.parseColor("#008577"));
        }
        this.tvCase.setText(0.0d > 0.0d ? "مدين" : "دائن");
        return 0.0d - 0.0d;
    }

    private void initializeView() {
        this.recyclerView = (RecyclerView) findViewById(C1018R.id.recycleV);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setHasFixedSize(true);
        this.progress = (LinearLayout) findViewById(C1018R.id.progressDownload);
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
        setContentView(C1018R.layout.list_box_moves_report_activity);
        setTitle("حركة الصندوق اجمالي  ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.appConfig = AppConfig.getInstance();
        initializeView();
        this.layoutOptions = (LinearLayout) findViewById(C1018R.id.layoutOptions);
        this.layoutOptions.setVisibility(8);
        this.tvStartDate = (TextView) findViewById(C1018R.id.edtDate);
        this.viewPeriod = AppConfig.getInstance().getViewPeriod();
        if (this.viewPeriod == null) {
            this.viewPeriod = new ViewPeriod(this);
        }
        this.btnRight = (ImageView) findViewById(C1018R.id.btnRight);
        this.btnLeft = (ImageView) findViewById(C1018R.id.btnLeft);
        this.btnRight.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.BoxMovesReportActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(BoxMovesReportActivity.this.currDate);
                calendar.add(5, 1);
                BoxMovesReportActivity.this.currDate = calendar.getTime();
                BoxMovesReportActivity.this.tvStartDate.setText(Utils.getShortDateStr((FragmentActivity) BoxMovesReportActivity.this, calendar.getTime()));
                BoxMovesReportActivity.this.AsyncDownloadStart();
            }
        });
        this.btnLeft.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.BoxMovesReportActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(BoxMovesReportActivity.this.currDate);
                calendar.add(5, -1);
                BoxMovesReportActivity.this.currDate = calendar.getTime();
                BoxMovesReportActivity.this.tvStartDate.setText(Utils.getShortDateStr((FragmentActivity) BoxMovesReportActivity.this, calendar.getTime()));
                BoxMovesReportActivity.this.AsyncDownloadStart();
            }
        });
        this.tvStartDate.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.BoxMovesReportActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(BoxMovesReportActivity.this.currDate);
                new DatePickerDialog(BoxMovesReportActivity.this, new DatePickerDialog.OnDateSetListener() { // from class: com.yd.electricecollector.ui.BoxMovesReportActivity.3.1
                    @Override // android.app.DatePickerDialog.OnDateSetListener
                    public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
                        Calendar calendar2 = Calendar.getInstance();
                        calendar2.set(i, i2, i3);
                        BoxMovesReportActivity.this.tvStartDate.setText(Utils.getShortDateStr((FragmentActivity) BoxMovesReportActivity.this, calendar2.getTime()));
                        BoxMovesReportActivity.this.currDate = calendar2.getTime();
                        BoxMovesReportActivity.this.AsyncDownloadStart();
                    }
                }, calendar.get(1), calendar.get(2), calendar.get(5)).show();
            }
        });
        Log.w(this.TAG, "currDateL: " + this.currDate.toString());
        this.tvStartDate.setText(Utils.getShortDateStr((FragmentActivity) this, this.currDate));
        this.tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", 0));
        if (this.tokenManager.getToken() == null) {
            startActivity(new Intent(this, (Class<?>) LoginActivity.class));
        }
        if (bundle == null) {
            AsyncDownloadStart();
        }
        this._pageSize = this.appConfig.getPageSize();
        this._adapter = new BoxMoveAdapter();
        this.swipeRefreshLayout = (SwipeRefreshLayout) findViewById(C1018R.id.swipeRefresh);
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() { // from class: com.yd.electricecollector.ui.BoxMovesReportActivity.4
            @Override // androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
            public void onRefresh() {
                BoxMovesReportActivity.this.layoutOptions.setVisibility(8);
                BoxMovesReportActivity.this.AsyncDownloadStart();
                BoxMovesReportActivity.this._adapter.notifyDataSetChanged();
                BoxMovesReportActivity.this.swipeRefreshLayout.setRefreshing(false);
            }
        });
        this._adapter.setItemClickListener(new OnItemClickCallback<RepBoxMoves>() { // from class: com.yd.electricecollector.ui.BoxMovesReportActivity.5
            @Override // com.p001yd.electricecollector.network.OnItemClickCallback
            public void onItemClicked(RepBoxMoves repBoxMoves) {
                if (repBoxMoves.getDain() > 0.0d) {
                    repBoxMoves.setDate(BoxMovesReportActivity.this.tvStartDate.getText().toString());
                    Intent intent = new Intent(BoxMovesReportActivity.this, (Class<?>) BoxMovesDetailsReportActivity.class);
                    intent.putExtra("data", repBoxMoves);
                    BoxMovesReportActivity.this.startActivity(intent);
                }
            }

            @Override // com.p001yd.electricecollector.network.OnItemClickCallback
            public void onItemClicked(RepBoxMoves repBoxMoves, int i) {
            }
        });
        this.recyclerView.setAdapter(this._adapter);
        getWindow().setSoftInputMode(2);
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
    public void onFailed(RepBoxMoves repBoxMoves) {
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onLoadDataFailure() {
        Toast.makeText(this, "فشل تحميل البيانات", 0).show();
        this.progress.setVisibility(8);
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onLoadDataSucceed(List<RepBoxMoves> list) {
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

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStart() {
        super.onStart();
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onSucceed(RepBoxMoves repBoxMoves) {
    }
}
