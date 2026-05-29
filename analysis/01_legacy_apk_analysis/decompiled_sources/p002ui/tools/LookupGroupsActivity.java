package com.p001yd.electricecollector.p002ui.tools;

import android.R;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.p001yd.electricecollector.Adapter.GroupsAdapter;
import com.p001yd.electricecollector.C1018R;
import com.p001yd.electricecollector.TokenManager;
import com.p001yd.electricecollector.common.AppConfig;
import com.p001yd.electricecollector.entities.Places;
import com.p001yd.electricecollector.entities.PlacesResponse;
import com.p001yd.electricecollector.entities.TGroipResponse;
import com.p001yd.electricecollector.entities.TGroup;
import com.p001yd.electricecollector.entities.Users;
import com.p001yd.electricecollector.network.ApiService;
import com.p001yd.electricecollector.network.OnItemClickCallback;
import com.p001yd.electricecollector.network.RetrofitBuilder;
import com.p001yd.electricecollector.p002ui.BaseView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* loaded from: classes9.dex */
public class LookupGroupsActivity extends AppCompatActivity implements BaseView<TGroup> {
    public static String EXTRA_PROVINSI_ID_VALUE = "acct_id_value";
    public static String EXTRA_SELECTED_VALUE = "selected_value";
    private GroupsAdapter _adapter;
    private AlertDialog _dialog;
    List<TGroup> aList;
    AppConfig appConfig;
    EditText edtFilter;
    protected LinearLayout layoutOptions;
    List<Places> places_List;
    LinearLayout progress;
    RecyclerView recyclerView;
    ApiService service;
    private Spinner spmstlm;
    TokenManager tokenManager;
    Users user;
    private final String TAG = getClass().getSimpleName();
    private final String TITLE = "الطبلات";
    Places itemSelect = null;
    Map<Integer, ArrayList<TGroup>> accountMap = new HashMap();
    private String tblhSelect = null;
    private String placesSelect = null;

    private void initializeView() {
        this.recyclerView = (RecyclerView) findViewById(C1018R.id.recycleV);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.addItemDecoration(new DividerItemDecoration(this, 1));
        this.progress = (LinearLayout) findViewById(C1018R.id.progressDownload);
    }

    private void loadPlaces() {
        HashMap hashMap = new HashMap();
        hashMap.put("nou", "" + this.appConfig.getUser().getNou());
        this.service = (ApiService) RetrofitBuilder.createServiceWithAuth(ApiService.class, this.tokenManager, this.appConfig.getBaseUrl());
        this.service.GetListPlaces(hashMap).enqueue(new Callback<PlacesResponse>() { // from class: com.yd.electricecollector.ui.tools.LookupGroupsActivity.3
            @Override // retrofit2.Callback
            public void onFailure(Call<PlacesResponse> call, Throwable th) {
                Log.w(LookupGroupsActivity.this.TAG, "onFailure: " + th.getMessage());
            }

            @Override // retrofit2.Callback
            public void onResponse(Call<PlacesResponse> call, Response<PlacesResponse> response) {
                Log.w(LookupGroupsActivity.this.TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    ArrayList arrayList = new ArrayList();
                    arrayList.add("غيرمحدد");
                    LookupGroupsActivity.this.places_List = response.body().getData();
                    if (LookupGroupsActivity.this.places_List != null) {
                        Iterator<Places> it = LookupGroupsActivity.this.places_List.iterator();
                        while (it.hasNext()) {
                            arrayList.add(it.next().getname());
                        }
                    }
                    ArrayAdapter arrayAdapter = new ArrayAdapter(LookupGroupsActivity.this, R.layout.simple_spinner_item, arrayList);
                    arrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                    LookupGroupsActivity.this.spmstlm.setAdapter((SpinnerAdapter) arrayAdapter);
                }
            }
        });
    }

    private void loadTblh() {
        HashMap hashMap = new HashMap();
        hashMap.put("nou", "" + this.appConfig.getUser().getNou());
        this.service = (ApiService) RetrofitBuilder.createServiceWithAuth(ApiService.class, this.tokenManager, this.appConfig.getBaseUrl());
        this.service.GetListGroup(hashMap).enqueue(new Callback<TGroipResponse>() { // from class: com.yd.electricecollector.ui.tools.LookupGroupsActivity.4
            @Override // retrofit2.Callback
            public void onFailure(Call<TGroipResponse> call, Throwable th) {
                Log.w(LookupGroupsActivity.this.TAG, "onFailure: " + th.getMessage());
            }

            @Override // retrofit2.Callback
            public void onResponse(Call<TGroipResponse> call, Response<TGroipResponse> response) {
                Log.w(LookupGroupsActivity.this.TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    LookupGroupsActivity.this.aList = response.body().getData();
                    LookupGroupsActivity.this._adapter.setItems(LookupGroupsActivity.this.aList);
                    LookupGroupsActivity.this._adapter.setItemsFilter(LookupGroupsActivity.this.aList);
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C1018R.layout.lookup_activity2);
        setTitle(" الطبلات  ");
        this.itemSelect = (Places) getIntent().getParcelableExtra(EXTRA_SELECTED_VALUE);
        if (this.itemSelect != null) {
            setTitle(this.itemSelect.getname());
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.layoutOptions = (LinearLayout) findViewById(C1018R.id.layoutOptions);
        this.layoutOptions.setVisibility(8);
        this.progress = (LinearLayout) findViewById(C1018R.id.progressDownload);
        this.edtFilter = (EditText) findViewById(C1018R.id.edtFilter);
        this.spmstlm = (Spinner) findViewById(C1018R.id.spPlacesFilter);
        ((TextView) findViewById(C1018R.id.tvcounter)).setVisibility(8);
        this.appConfig = AppConfig.getInstance();
        this.user = this.appConfig.getUser();
        initializeView();
        this.tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", 0));
        this.tokenManager.getToken();
        this._adapter = new GroupsAdapter();
        this._adapter.setItemClickListener(new OnItemClickCallback<TGroup>() { // from class: com.yd.electricecollector.ui.tools.LookupGroupsActivity.1
            @Override // com.p001yd.electricecollector.network.OnItemClickCallback
            public void onItemClicked(TGroup tGroup) {
                Intent intent = new Intent();
                intent.putExtra(LookupGroupsActivity.EXTRA_SELECTED_VALUE, tGroup);
                LookupGroupsActivity.this.setResult(-1, intent);
                LookupGroupsActivity.this.finish();
            }

            @Override // com.p001yd.electricecollector.network.OnItemClickCallback
            public void onItemClicked(TGroup tGroup, int i) {
            }
        });
        if (this.itemSelect != null) {
            this.aList = this.itemSelect.GetListGroup();
            this._adapter.setItems(this.aList);
            this._adapter.setItemsFilter(this.aList);
        }
        this.recyclerView.setAdapter(this._adapter);
        this.edtFilter.addTextChangedListener(new TextWatcher() { // from class: com.yd.electricecollector.ui.tools.LookupGroupsActivity.2
            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                LookupGroupsActivity.this._adapter.getFilter().filter(charSequence);
            }
        });
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C1018R.menu.o_lookup, menu);
        return true;
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onFailed(TGroup tGroup) {
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onLoadDataFailure() {
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onLoadDataSucceed(List<TGroup> list) {
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

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onSucceed(TGroup tGroup) {
    }
}
