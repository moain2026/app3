package com.p001yd.electricecollector.p002ui;

import android.R;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.itextpdf.text.DocumentException;
import com.p001yd.electricecollector.Adapter.AccounttSearchAdapter;
import com.p001yd.electricecollector.Adapter.BalanceStateDetailsAdapter;
import com.p001yd.electricecollector.C1018R;
import com.p001yd.electricecollector.LoginActivity;
import com.p001yd.electricecollector.Pdf_temp;
import com.p001yd.electricecollector.TokenManager;
import com.p001yd.electricecollector.Utils;
import com.p001yd.electricecollector.ViewPeriod;
import com.p001yd.electricecollector.common.AppConfig;
import com.p001yd.electricecollector.entities.Accounts;
import com.p001yd.electricecollector.entities.AccountsResponse;
import com.p001yd.electricecollector.entities.BalanceState;
import com.p001yd.electricecollector.entities.BalanceStateDetails;
import com.p001yd.electricecollector.entities.BalanceStateDetailsRespons;
import com.p001yd.electricecollector.entities.Currency;
import com.p001yd.electricecollector.entities.CurrencyResponse;
import com.p001yd.electricecollector.network.ApiService;
import com.p001yd.electricecollector.network.RetrofitBuilder;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* loaded from: classes12.dex */
public class BalanceStateDetailsReportActivity extends AppCompatActivity implements BaseView<BalanceStateDetails> {
    BalanceState BalanceStateHeader;
    List<BalanceStateDetails> ListBalance;
    Accounts _account;
    private BalanceStateDetailsAdapter _adapter;
    private AlertDialog _dialog;
    List<Accounts> aList;
    AccounttSearchAdapter acAdp;
    private ArrayList<Accounts> acctList;
    AppConfig appConfig;
    Button btnView;
    Call<BalanceStateDetailsRespons> call;
    private ArrayList<Currency> curr_List;
    protected LinearLayout layoutOptions;
    ProgressBar loadingProgressBar;
    Pdf_temp pdf_temp;
    LinearLayout progress;
    RecyclerView recyclerView;
    ApiService service;
    Spinner spCurrency;
    Spinner spTypeAcct;
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
    private int _datePickerInput = 0;
    private boolean _isSetDate = false;
    private int _pageNumber = 1;
    private int _pageSize = 30;
    private int[] _pagesCount = new int[1];
    int accountSelect = -1;
    int currencySelect = -1;
    int typeAccountSelect = -1;
    Map<Integer, ArrayList<Accounts>> accountMap = new HashMap();
    AutoCompleteTextView searchAccount = null;
    int _mode = 0;

    /* JADX INFO: Access modifiers changed from: private */
    public void AsyncDownloadStart() {
        if (this._mode == 1 && (this.searchAccount.getText().length() == 0 || this._account == null)) {
            Utils.msgBox("يجب اختيار حساب", this, new Object[0]);
            return;
        }
        this.layoutOptions.setVisibility(8);
        this.progress.setVisibility(0);
        HashMap hashMap = new HashMap();
        if (this._mode == 1) {
            if (this._account != null && this.searchAccount.getText().length() > 0) {
                hashMap.put("num", "" + this._account.getnum());
            }
            if (this.currencySelect != -1) {
                hashMap.put("currency", "" + this.currencySelect);
            }
        } else {
            hashMap.put("num", this.BalanceStateHeader.getnum());
        }
        hashMap.put("sdate", Utils.getShortDateStrApi(this, this.viewPeriod.startDate));
        hashMap.put("edate", Utils.getShortDateStrApi(this, this.viewPeriod.endDate));
        hashMap.put("appid", this.appConfig.getAppId());
        Log.w(this.TAG, "_base_url: " + this.appConfig.getBaseUrl());
        this.service = (ApiService) RetrofitBuilder.createServiceWithAuth(ApiService.class, this.tokenManager, this.appConfig.getBaseUrl());
        this.call = this.service.GetRepBalanceDetailsByDate(hashMap);
        this.call.enqueue(new Callback<BalanceStateDetailsRespons>() { // from class: com.yd.electricecollector.ui.BalanceStateDetailsReportActivity.8
            @Override // retrofit2.Callback
            public void onFailure(Call<BalanceStateDetailsRespons> call, Throwable th) {
                Log.w(BalanceStateDetailsReportActivity.this.TAG, "onFailure: " + th.getMessage());
                BalanceStateDetailsReportActivity.this.progress.setVisibility(8);
            }

            @Override // retrofit2.Callback
            public void onResponse(Call<BalanceStateDetailsRespons> call, Response<BalanceStateDetailsRespons> response) {
                Log.w(BalanceStateDetailsReportActivity.this.TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    BalanceStateDetailsReportActivity.this.ListBalance = response.body().getData();
                    if (BalanceStateDetailsReportActivity.this.ListBalance != null) {
                        BalanceStateDetailsReportActivity.this._adapter.setItems(BalanceStateDetailsReportActivity.this.ListBalance);
                        double d = 0.0d;
                        for (BalanceStateDetails balanceStateDetails : BalanceStateDetailsReportActivity.this.ListBalance) {
                            d = (balanceStateDetails.getMden() + d) - Math.abs(balanceStateDetails.getDain());
                            balanceStateDetails.seRsed(d);
                        }
                        BalanceStateDetailsReportActivity.this.tvTotal1.setText(Utils.numberToString(d));
                        if (d > 0.0d) {
                            BalanceStateDetailsReportActivity.this.tvTotal1.setTextColor(Color.parseColor("#D81B60"));
                        } else if (d < 0.0d) {
                            BalanceStateDetailsReportActivity.this.tvTotal1.setTextColor(Color.parseColor("#008577"));
                        }
                        BalanceStateDetailsReportActivity.this.tvCase.setText(d > 0.0d ? "مدين" : "دائن");
                    }
                } else {
                    Toast.makeText(BalanceStateDetailsReportActivity.this, response.message(), 1).show();
                }
                BalanceStateDetailsReportActivity.this.progress.setVisibility(8);
            }
        });
    }

    public static int getItemPosition(int i, ArrayList<Currency> arrayList) {
        if (arrayList == null) {
            return 0;
        }
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            if (arrayList.get(i2).getNum() == i) {
                return i2;
            }
        }
        return 0;
    }

    private double getTotals(List<BalanceStateDetails> list) {
        double d = 0.0d;
        for (BalanceStateDetails balanceStateDetails : list) {
            d += balanceStateDetails.getMden() - balanceStateDetails.getDain();
        }
        this.tvTotal1.setText(Utils.numberToString(d));
        this.tvCase.setText(d > 0.0d ? "مدين" : "دائن");
        return 0.0d - d;
    }

    private void initializeView() {
        this.recyclerView = (RecyclerView) findViewById(C1018R.id.recycleV);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setHasFixedSize(true);
        this.loadingProgressBar = (ProgressBar) findViewById(C1018R.id.loading);
        this.progress = (LinearLayout) findViewById(C1018R.id.progressDownload);
    }

    private void loadAccounts(int i) {
        HashMap hashMap = new HashMap();
        if (i > 1) {
            hashMap.put("id", "" + i);
        }
        this.service = (ApiService) RetrofitBuilder.createServiceWithAuth(ApiService.class, this.tokenManager, this.appConfig.getBaseUrl());
        this.service.GetListAccounts(hashMap).enqueue(new Callback<AccountsResponse>() { // from class: com.yd.electricecollector.ui.BalanceStateDetailsReportActivity.10
            @Override // retrofit2.Callback
            public void onFailure(Call<AccountsResponse> call, Throwable th) {
                Log.w(BalanceStateDetailsReportActivity.this.TAG, "onFailure: " + th.getMessage());
            }

            @Override // retrofit2.Callback
            public void onResponse(Call<AccountsResponse> call, Response<AccountsResponse> response) {
                Log.w(BalanceStateDetailsReportActivity.this.TAG, "onResponse: " + response);
                BalanceStateDetailsReportActivity.this.acctList = new ArrayList();
                if (response.isSuccessful()) {
                    BalanceStateDetailsReportActivity.this.aList = response.body().getData();
                    if (BalanceStateDetailsReportActivity.this.aList != null) {
                        Iterator<Accounts> it = BalanceStateDetailsReportActivity.this.aList.iterator();
                        while (it.hasNext()) {
                            BalanceStateDetailsReportActivity.this.acctList.add(it.next());
                        }
                        BalanceStateDetailsReportActivity.this.acAdp = new AccounttSearchAdapter(BalanceStateDetailsReportActivity.this, R.layout.simple_dropdown_item_1line, BalanceStateDetailsReportActivity.this.acctList);
                        BalanceStateDetailsReportActivity.this.searchAccount.setAdapter(BalanceStateDetailsReportActivity.this.acAdp);
                    }
                }
            }
        });
    }

    private void loadCurrency() {
        this.service = (ApiService) RetrofitBuilder.createServiceWithAuth(ApiService.class, this.tokenManager, this.appConfig.getBaseUrl());
        this.service.GetListCurrency().enqueue(new Callback<CurrencyResponse>() { // from class: com.yd.electricecollector.ui.BalanceStateDetailsReportActivity.11
            @Override // retrofit2.Callback
            public void onFailure(Call<CurrencyResponse> call, Throwable th) {
            }

            @Override // retrofit2.Callback
            public void onResponse(Call<CurrencyResponse> call, Response<CurrencyResponse> response) {
                Log.w(BalanceStateDetailsReportActivity.this.TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    ArrayList arrayList = new ArrayList();
                    arrayList.add("كلي");
                    BalanceStateDetailsReportActivity.this.curr_List = (ArrayList) response.body().getData();
                    if (BalanceStateDetailsReportActivity.this.curr_List != null) {
                        Iterator it = BalanceStateDetailsReportActivity.this.curr_List.iterator();
                        while (it.hasNext()) {
                            arrayList.add(((Currency) it.next()).getName());
                        }
                    }
                    ArrayAdapter arrayAdapter = new ArrayAdapter(BalanceStateDetailsReportActivity.this, R.layout.simple_spinner_item, arrayList);
                    arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                    BalanceStateDetailsReportActivity.this.spCurrency.setAdapter((SpinnerAdapter) arrayAdapter);
                }
            }
        });
    }

    private void loadTypeAccount() {
        ArrayList arrayList = new ArrayList();
        arrayList.add("كلي");
        arrayList.add("عميل");
        arrayList.add("مورد");
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        this.spTypeAcct.setAdapter((SpinnerAdapter) arrayAdapter);
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

    public Currency getItem(int i, ArrayList<Currency> arrayList) {
        if (arrayList == null) {
            return null;
        }
        Iterator<Currency> it = arrayList.iterator();
        while (it.hasNext()) {
            Currency next = it.next();
            if (next.getNum() == i) {
                return next;
            }
        }
        return null;
    }

    public ArrayList<Accounts> getListItems(int i, List<Accounts> list) {
        ArrayList<Accounts> arrayList = new ArrayList<>();
        ArrayList<Accounts> arrayList2 = new ArrayList<>();
        if (list == null) {
            return null;
        }
        for (Accounts accounts : list) {
            if (accounts.getType() == 2) {
                arrayList.add(accounts);
            } else if (accounts.getType() == 3) {
                arrayList2.add(accounts);
            }
        }
        this.accountMap.put(2, arrayList);
        this.accountMap.put(3, arrayList2);
        return this.accountMap.get(Integer.valueOf(i));
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 5 && i2 == -1) {
            try {
                ViewPeriod viewPeriod = (ViewPeriod) intent.getExtras().get("VIEW_PERIOD");
                if (viewPeriod != null) {
                    this.viewPeriod = viewPeriod;
                    ((AppConfig) getApplicationContext()).setViewPeriod(viewPeriod);
                    this.tvStartDate.setText(Utils.getShortDateStr((FragmentActivity) this, this.viewPeriod.startDate));
                    this.tvEndDate.setText(Utils.getShortDateStr((FragmentActivity) this, this.viewPeriod.endDate));
                    AsyncDownloadStart();
                    this.progress.setVisibility(8);
                }
            } catch (Exception e) {
            }
        }
        if (i == 300 && i2 == -1 && intent.getParcelableExtra(LookupAccountsActivity.EXTRA_SELECTED_VALUE) != null) {
            this._account = (Accounts) intent.getParcelableExtra(LookupAccountsActivity.EXTRA_SELECTED_VALUE);
            this.searchAccount.setText(this._account.getname());
            setTitle(this.searchAccount.getText().toString());
            AsyncDownloadStart();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C1018R.layout.list_balance_details_report_activity);
        this._mode = getIntent().getIntExtra("mode", 0);
        setTitle("كشف حساب  ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initializeView();
        this.tvTotal1 = (TextView) findViewById(C1018R.id.tvTotal1);
        this.tvTitle = (TextView) findViewById(C1018R.id.tvTitle);
        this.tvCase = (TextView) findViewById(C1018R.id.tvCase);
        this.tvStartDate = (TextView) findViewById(C1018R.id.txtStartDate);
        this.tvEndDate = (TextView) findViewById(C1018R.id.txtEndDate);
        this.layoutOptions = (LinearLayout) findViewById(C1018R.id.layoutOptions);
        ((TableLayout) findViewById(C1018R.id.tableRowCurrency)).setVisibility(8);
        ((TableLayout) findViewById(C1018R.id.tableRowTypeAccount)).setVisibility(8);
        this.btnView = (Button) findViewById(C1018R.id.btnView);
        this.btnView.setVisibility(8);
        if (this._mode == 0) {
            this.layoutOptions.setVisibility(8);
            ((TableLayout) findViewById(C1018R.id.tableRowAccount)).setVisibility(8);
            ((TableLayout) findViewById(C1018R.id.tableRowCurrency)).setVisibility(8);
            ((Button) findViewById(C1018R.id.btnView)).setVisibility(8);
        }
        this.btnView.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.BalanceStateDetailsReportActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                BalanceStateDetailsReportActivity.this.AsyncDownloadStart();
            }
        });
        this.spCurrency = (Spinner) findViewById(C1018R.id.spCurrencyFilter);
        this.spTypeAcct = (Spinner) findViewById(C1018R.id.spnType);
        this.searchAccount = (AutoCompleteTextView) findViewById(C1018R.id.AccountFilter);
        this.searchAccount.setOnTouchListener(new View.OnTouchListener() { // from class: com.yd.electricecollector.ui.BalanceStateDetailsReportActivity.2
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (1 == motionEvent.getAction()) {
                    Intent intent = new Intent(BalanceStateDetailsReportActivity.this, (Class<?>) LookupAccountsActivity.class);
                    if (BalanceStateDetailsReportActivity.this._account != null) {
                        intent.putExtra(LookupAccountsActivity.EXTRA_SELECTED_VALUE, BalanceStateDetailsReportActivity.this._account);
                        intent.putExtra(LookupAccountsActivity.EXTRA_PROVINSI_ID_VALUE, 0);
                    }
                    BalanceStateDetailsReportActivity.this.startActivityForResult(intent, 300);
                }
                return false;
            }
        });
        this.tvStartDate.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.BalanceStateDetailsReportActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                BalanceStateDetailsReportActivity.this.openViewPeriod();
            }
        });
        this.tvEndDate.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.BalanceStateDetailsReportActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                BalanceStateDetailsReportActivity.this.openViewPeriod();
            }
        });
        this.viewPeriod = AppConfig.getInstance().getViewPeriod();
        if (this.viewPeriod == null) {
            this.viewPeriod = new ViewPeriod(this);
        }
        this.tvStartDate.setText(Utils.getShortDateStr((FragmentActivity) this, this.viewPeriod.startDate));
        this.tvEndDate.setText(Utils.getShortDateStr((FragmentActivity) this, this.viewPeriod.endDate));
        this.tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", 0));
        if (this.tokenManager.getToken() == null) {
            startActivity(new Intent(this, (Class<?>) LoginActivity.class));
            finish();
        }
        this.spCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.yd.electricecollector.ui.BalanceStateDetailsReportActivity.5
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                if (i <= 0) {
                    BalanceStateDetailsReportActivity.this.currencySelect = -1;
                } else {
                    BalanceStateDetailsReportActivity.this.currencySelect = ((Currency) BalanceStateDetailsReportActivity.this.curr_List.get(i - 1)).getNum();
                }
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        this.spTypeAcct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.yd.electricecollector.ui.BalanceStateDetailsReportActivity.6
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        this.BalanceStateHeader = (BalanceState) getIntent().getParcelableExtra("data");
        this.appConfig = AppConfig.getInstance();
        this.appConfig.getBaseUrl();
        this.appConfig.getToken();
        this.appConfig.getAppId();
        this._pageSize = this.appConfig.getPageSize();
        this.swipeRefreshLayout = (SwipeRefreshLayout) findViewById(C1018R.id.swipeRefresh);
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() { // from class: com.yd.electricecollector.ui.BalanceStateDetailsReportActivity.7
            @Override // androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
            public void onRefresh() {
                BalanceStateDetailsReportActivity.this.AsyncDownloadStart();
                BalanceStateDetailsReportActivity.this._adapter.notifyDataSetChanged();
                BalanceStateDetailsReportActivity.this.swipeRefreshLayout.setRefreshing(false);
            }
        });
        this._adapter = new BalanceStateDetailsAdapter();
        this.recyclerView.setAdapter(this._adapter);
        if (this.BalanceStateHeader != null) {
            setTitle(((Object) " ") + this.BalanceStateHeader.getname());
            AsyncDownloadStart();
        }
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C1018R.menu.o_report, menu);
        menu.findItem(C1018R.id.search).setVisible(false);
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
    public void onFailed(BalanceStateDetails balanceStateDetails) {
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onLoadDataFailure() {
        Toast.makeText(this, "فشل تحميل البيانات", 0).show();
        this.progress.setVisibility(8);
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onLoadDataSucceed(List<BalanceStateDetails> list) {
        double d = 0.0d;
        for (BalanceStateDetails balanceStateDetails : list) {
            d = (d - balanceStateDetails.getDain()) + balanceStateDetails.getMden();
            balanceStateDetails.seRsed(d);
        }
        this._adapter.setItems(list);
        getTotals(list);
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
                try {
                    pdfF();
                } catch (DocumentException e) {
                    e.printStackTrace();
                } catch (IOException e2) {
                    e2.printStackTrace();
                } catch (ParseException e3) {
                    e3.printStackTrace();
                }
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override // android.app.Activity
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        this.BalanceStateHeader = (BalanceState) bundle.getParcelable("Balance_State_Header");
        this._account = (Accounts) bundle.getParcelable("acct_info");
        if (this._account != null) {
            setTitle(this._account.getname());
            AsyncDownloadStart();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable("acct_info", this._account);
        bundle.putParcelable("Balance_State_Header", this.BalanceStateHeader);
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onSucceed(BalanceStateDetails balanceStateDetails) {
    }

    /* JADX WARN: Type inference failed for: r3v13, types: [com.yd.electricecollector.ui.BalanceStateDetailsReportActivity$9] */
    public void pdfF() throws DocumentException, IOException, ParseException {
        final ArrayList arrayList = new ArrayList();
        new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        final StringBuilder sb = new StringBuilder();
        sb.append("كشف حساب/ " + getTitle().toString());
        if (this.tvStartDate.getText().toString().length() > 0) {
            sb.append(" \r\n");
            sb.append(" من : " + Utils.getShortDateStrApi(this, Utils.getDateFromString(this.tvStartDate.getText().toString())));
        }
        if (this.tvEndDate.getText().toString().length() > 0) {
            sb.append("  إلى: " + Utils.getShortDateStrApi(this, Utils.getDateFromString(this.tvEndDate.getText().toString())));
        }
        new AsyncTask<String, Integer, String>() { // from class: com.yd.electricecollector.ui.BalanceStateDetailsReportActivity.9
            ProgressDialog progressD;

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public String doInBackground(String... strArr) {
                BalanceStateDetailsReportActivity.this.pdf_temp = new Pdf_temp(BalanceStateDetailsReportActivity.this.getApplicationContext(), "كشف حساب " + BalanceStateDetailsReportActivity.this.getTitle().toString(), 2);
                BalanceStateDetailsReportActivity.this.pdf_temp.setReportName(sb.toString());
                BalanceStateDetailsReportActivity.this.pdf_temp.openDocument();
                BalanceStateDetailsReportActivity.this.pdf_temp.setReportName("كشف حساب/ " + BalanceStateDetailsReportActivity.this.getTitle().toString());
                BalanceStateDetailsReportActivity.this.pdf_temp.addTtle("تقرير", "تقرير جميع السجلات", "6/1/2017");
                String[] strArr2 = {"التاريخ", "التفاصيل", "له/ دائن", "علية/ مدين", "الرصيد"};
                double d = 0.0d;
                double d2 = 0.0d;
                for (BalanceStateDetails balanceStateDetails : BalanceStateDetailsReportActivity.this.ListBalance) {
                    d += Math.abs(balanceStateDetails.getDain());
                    d2 += balanceStateDetails.getMden();
                    arrayList.add(new String[]{balanceStateDetails.getDate(), balanceStateDetails.getname(), NumberFormat.getInstance(Locale.ENGLISH).format(balanceStateDetails.getDain()), NumberFormat.getInstance(Locale.ENGLISH).format(balanceStateDetails.getMden()), NumberFormat.getInstance(Locale.ENGLISH).format(balanceStateDetails.getRsed())});
                }
                try {
                    BalanceStateDetailsReportActivity.this.pdf_temp.createTable3(strArr2, arrayList, " كشف حساب/ " + BalanceStateDetailsReportActivity.this.getTitle().toString() + sb.toString(), d, d2, d2 - d);
                } catch (DocumentException e) {
                    e.printStackTrace();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
                BalanceStateDetailsReportActivity.this.pdf_temp.closDocument();
                return null;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(String str) {
                super.onPostExecute((AsyncTaskC11409) str);
                BalanceStateDetailsReportActivity.this.pdf_temp.viewPdf();
                this.progressD.dismiss();
            }

            @Override // android.os.AsyncTask
            protected void onPreExecute() {
                this.progressD = new ProgressDialog(BalanceStateDetailsReportActivity.this);
                this.progressD = new ProgressDialog(BalanceStateDetailsReportActivity.this);
                this.progressD.setTitle("يتم إعداد التقرير..");
                this.progressD.setMessage("يرجى الانتظار...");
                this.progressD.setCancelable(false);
                try {
                    this.progressD.show();
                } catch (Exception e) {
                    this.progressD.dismiss();
                }
                super.onPreExecute();
            }
        }.execute(new String[0]);
    }
}
