package com.p001yd.electricecollector.p002ui;

import android.R;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.itextpdf.text.html.HtmlTags;
import com.p001yd.electricecollector.Adapter.AccountsAdapter;
import com.p001yd.electricecollector.C1018R;
import com.p001yd.electricecollector.TokenManager;
import com.p001yd.electricecollector.common.AppConfig;
import com.p001yd.electricecollector.entities.Accounts;
import com.p001yd.electricecollector.entities.AccountsResponse;
import com.p001yd.electricecollector.entities.Places;
import com.p001yd.electricecollector.entities.PlacesResponse;
import com.p001yd.electricecollector.entities.TGroipResponse;
import com.p001yd.electricecollector.entities.TGroup;
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
public class LookupAccountsActivity extends AppCompatActivity implements BaseView<Accounts> {
    public static String EXTRA_PROVINSI_ID_VALUE = "acct_id_value";
    public static String EXTRA_SELECTED_VALUE = "selected_value";
    private AccountsAdapter _adapter;
    private AlertDialog _dialog;
    List<Accounts> aList;
    AppConfig appConfig;
    EditText edtFilter;
    List<TGroup> group_List;
    protected LinearLayout layoutOptions;
    ArrayList<Places> places_List;
    LinearLayout progress;
    RecyclerView recyclerView;
    ApiService service;
    private Spinner spmstlm;
    private Spinner sptblh;
    TokenManager tokenManager;
    Users user;
    private final String TAG = getClass().getSimpleName();
    private final String TITLE = "الحسابات";
    int accountSelect = 0;
    Map<Integer, ArrayList<Accounts>> accountMap = new HashMap();
    private String tblhSelect = null;
    private String placesSelect = null;

    /* JADX INFO: Access modifiers changed from: private */
    public void FillSpinnerPlaces(ArrayList<Places> arrayList) {
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add("غيرمحدد");
        if (arrayList != null) {
            Iterator<Places> it = arrayList.iterator();
            while (it.hasNext()) {
                arrayList2.add(it.next().getname());
            }
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.simple_spinner_item, arrayList2);
        arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        this.spmstlm.setAdapter((SpinnerAdapter) arrayAdapter);
    }

    private void initializeView() {
        this.recyclerView = (RecyclerView) findViewById(C1018R.id.recycleV);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.addItemDecoration(new DividerItemDecoration(this, 1));
        this.progress = (LinearLayout) findViewById(C1018R.id.progressDownload);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadAccounts() {
        this.progress.setVisibility(0);
        HashMap hashMap = new HashMap();
        hashMap.put("num", "" + this.appConfig.getUser().getNou());
        if (this.placesSelect != null) {
            hashMap.put(HtmlTags.f312P, "" + this.placesSelect);
        }
        if (this.tblhSelect != null) {
            hashMap.put("m", "" + this.tblhSelect);
        }
        hashMap.put("appid", this.appConfig.getAppId());
        this.service = (ApiService) RetrofitBuilder.createServiceWithAuth(ApiService.class, this.tokenManager, this.appConfig.getBaseUrl());
        this.service.GetListAccounts(hashMap).enqueue(new Callback<AccountsResponse>() { // from class: com.yd.electricecollector.ui.LookupAccountsActivity.7
            @Override // retrofit2.Callback
            public void onFailure(Call<AccountsResponse> call, Throwable th) {
                LookupAccountsActivity.this.progress.setVisibility(8);
                Log.w(LookupAccountsActivity.this.TAG, "onFailure: " + th.getMessage());
            }

            @Override // retrofit2.Callback
            public void onResponse(Call<AccountsResponse> call, Response<AccountsResponse> response) {
                Log.w(LookupAccountsActivity.this.TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    LookupAccountsActivity.this.aList = response.body().getData();
                    LookupAccountsActivity.this._adapter.setItems(LookupAccountsActivity.this.aList);
                    LookupAccountsActivity.this._adapter.setItemsFilter(LookupAccountsActivity.this.aList);
                }
                LookupAccountsActivity.this.progress.setVisibility(8);
            }
        });
    }

    private void loadPlaces() {
        HashMap hashMap = new HashMap();
        hashMap.put("nou", "" + this.appConfig.getUser().getNou());
        hashMap.put("type", "2");
        hashMap.put("appid", this.appConfig.getAppId());
        this.service = (ApiService) RetrofitBuilder.createServiceWithAuth(ApiService.class, this.tokenManager, this.appConfig.getBaseUrl());
        this.service.GetListPlaces(hashMap).enqueue(new Callback<PlacesResponse>() { // from class: com.yd.electricecollector.ui.LookupAccountsActivity.5
            @Override // retrofit2.Callback
            public void onFailure(Call<PlacesResponse> call, Throwable th) {
                Log.w(LookupAccountsActivity.this.TAG, "onFailure: " + th.getMessage());
            }

            @Override // retrofit2.Callback
            public void onResponse(Call<PlacesResponse> call, Response<PlacesResponse> response) {
                Log.w(LookupAccountsActivity.this.TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    LookupAccountsActivity.this.places_List = (ArrayList) response.body().getData();
                    LookupAccountsActivity.this.FillSpinnerPlaces(LookupAccountsActivity.this.places_List);
                }
            }
        });
    }

    private void loadTblh() {
        HashMap hashMap = new HashMap();
        hashMap.put("nou", "" + this.appConfig.getUser().getNou());
        hashMap.put("appid", this.appConfig.getAppId());
        this.service = (ApiService) RetrofitBuilder.createServiceWithAuth(ApiService.class, this.tokenManager, this.appConfig.getBaseUrl());
        this.service.GetListGroup(hashMap).enqueue(new Callback<TGroipResponse>() { // from class: com.yd.electricecollector.ui.LookupAccountsActivity.6
            @Override // retrofit2.Callback
            public void onFailure(Call<TGroipResponse> call, Throwable th) {
                Log.w(LookupAccountsActivity.this.TAG, "onFailure: " + th.getMessage());
            }

            @Override // retrofit2.Callback
            public void onResponse(Call<TGroipResponse> call, Response<TGroipResponse> response) {
                Log.w(LookupAccountsActivity.this.TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    ArrayList arrayList = new ArrayList();
                    arrayList.add("غيرمحدد");
                    LookupAccountsActivity.this.group_List = response.body().getData();
                    if (LookupAccountsActivity.this.group_List != null) {
                        Iterator<TGroup> it = LookupAccountsActivity.this.group_List.iterator();
                        while (it.hasNext()) {
                            arrayList.add(it.next().getname());
                        }
                    }
                    ArrayAdapter arrayAdapter = new ArrayAdapter(LookupAccountsActivity.this, R.layout.simple_spinner_item, arrayList);
                    arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                    LookupAccountsActivity.this.sptblh.setAdapter((SpinnerAdapter) arrayAdapter);
                }
            }
        });
    }

    public ArrayList<Accounts> getListItems(int i, int i2, List<Accounts> list) {
        ArrayList<Accounts> arrayList = new ArrayList<>();
        ArrayList<Accounts> arrayList2 = new ArrayList<>();
        ArrayList<Accounts> arrayList3 = new ArrayList<>();
        if (list == null) {
            return null;
        }
        for (Accounts accounts : list) {
            if (accounts.getType() == 1) {
                arrayList.add(accounts);
            } else if (accounts.getType() == 2) {
                arrayList2.add(accounts);
            } else {
                arrayList3.add(accounts);
            }
        }
        this.accountMap.put(0, arrayList);
        this.accountMap.put(1, arrayList2);
        this.accountMap.put(2, arrayList3);
        return this.accountMap.get(Integer.valueOf(i));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C1018R.layout.lookup_activity);
        setTitle(" المشتركين  ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.layoutOptions = (LinearLayout) findViewById(C1018R.id.layoutOptions);
        this.layoutOptions.setVisibility(8);
        this.progress = (LinearLayout) findViewById(C1018R.id.progressDownload);
        this.edtFilter = (EditText) findViewById(C1018R.id.edtFilter);
        this.sptblh = (Spinner) findViewById(C1018R.id.spTblhFilter);
        this.spmstlm = (Spinner) findViewById(C1018R.id.spPlacesFilter);
        this.appConfig = AppConfig.getInstance();
        this.places_List = new ArrayList<>();
        this.user = this.appConfig.getUser();
        initializeView();
        this.tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", 0));
        this.tokenManager.getToken();
        this._adapter = new AccountsAdapter();
        this._adapter.setItemClickListener(new OnItemClickCallback<Accounts>() { // from class: com.yd.electricecollector.ui.LookupAccountsActivity.1
            @Override // com.p001yd.electricecollector.network.OnItemClickCallback
            public void onItemClicked(Accounts accounts) {
                Intent intent = new Intent();
                intent.putExtra(LookupAccountsActivity.EXTRA_SELECTED_VALUE, accounts);
                LookupAccountsActivity.this.setResult(-1, intent);
                LookupAccountsActivity.this.finish();
            }

            @Override // com.p001yd.electricecollector.network.OnItemClickCallback
            public void onItemClicked(Accounts accounts, int i) {
            }
        });
        this.sptblh.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.yd.electricecollector.ui.LookupAccountsActivity.2
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                if (i > 0) {
                    LookupAccountsActivity.this.tblhSelect = LookupAccountsActivity.this.group_List.get(i - 1).getnum();
                } else {
                    LookupAccountsActivity.this.tblhSelect = null;
                }
                LookupAccountsActivity.this.loadAccounts();
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        this.spmstlm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.yd.electricecollector.ui.LookupAccountsActivity.3
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                if (i > 0) {
                    LookupAccountsActivity.this.placesSelect = String.valueOf(LookupAccountsActivity.this.places_List.get(i - 1).getnum());
                } else {
                    LookupAccountsActivity.this.placesSelect = null;
                }
                LookupAccountsActivity.this.loadAccounts();
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        if (bundle == null) {
            loadPlaces();
            loadAccounts();
        }
        this.recyclerView.setAdapter(this._adapter);
        this.edtFilter.addTextChangedListener(new TextWatcher() { // from class: com.yd.electricecollector.ui.LookupAccountsActivity.4
            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                LookupAccountsActivity.this._adapter.getFilter().filter(charSequence);
            }
        });
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C1018R.menu.o_lookup, menu);
        return true;
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onFailed(Accounts accounts) {
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onLoadDataFailure() {
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onLoadDataSucceed(List<Accounts> list) {
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 16908332) {
            finish();
            return true;
        }
        if (itemId != C1018R.id.mnuItemOptionDropDown) {
            return super.onOptionsItemSelected(menuItem);
        }
        if (this.layoutOptions.getVisibility() == 0) {
            this.layoutOptions.setVisibility(8);
        } else {
            this.layoutOptions.setVisibility(0);
        }
        return true;
    }

    @Override // android.app.Activity
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        this.placesSelect = bundle.getString("placesSelect");
        this.places_List = bundle.getParcelableArrayList("aList");
        FillSpinnerPlaces(this.places_List);
        if (this.places_List.size() > 0) {
            this.spmstlm.setSelection(bundle.getInt("acctpos"));
        }
        loadAccounts();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelableArrayList("aList", this.places_List);
        bundle.putInt("acctpos", this.spmstlm.getSelectedItemPosition());
        bundle.putString("placesSelect", this.placesSelect);
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onSucceed(Accounts accounts) {
    }
}
