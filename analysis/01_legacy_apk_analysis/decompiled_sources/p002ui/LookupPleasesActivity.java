package com.p001yd.electricecollector.p002ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.p001yd.electricecollector.Adapter.PlacesAdapter;
import com.p001yd.electricecollector.C1018R;
import com.p001yd.electricecollector.TokenManager;
import com.p001yd.electricecollector.common.AppConfig;
import com.p001yd.electricecollector.entities.Places;
import com.p001yd.electricecollector.entities.PlacesResponse;
import com.p001yd.electricecollector.entities.TGroup;
import com.p001yd.electricecollector.entities.Users;
import com.p001yd.electricecollector.network.ApiService;
import com.p001yd.electricecollector.network.OnItemClickCallback;
import com.p001yd.electricecollector.network.RetrofitBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* loaded from: classes12.dex */
public class LookupPleasesActivity extends AppCompatActivity implements BaseView<Places> {
    public static String EXTRA_PROVINSI_ID_VALUE = "acct_id_value";
    public static String EXTRA_SELECTED_VALUE = "selected_value";
    private PlacesAdapter _adapter;
    private AlertDialog _dialog;
    List<Places> aList;
    AppConfig appConfig;
    EditText edtFilter;
    List<TGroup> group_List;
    protected LinearLayout layoutOptions;
    List<Places> places_List;
    LinearLayout progress;
    RecyclerView recyclerView;
    ApiService service;
    private Spinner spmstlm;
    private Spinner sptblh;
    TokenManager tokenManager;
    Users user;
    private final String TAG = getClass().getSimpleName();
    private final String TITLE = "الحسابات";
    int typeSelect = 1;
    Map<Integer, ArrayList<Places>> accountMap = new HashMap();

    private void initializeView() {
        this.recyclerView = (RecyclerView) findViewById(C1018R.id.recycleV);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.addItemDecoration(new DividerItemDecoration(this, 1));
        this.progress = (LinearLayout) findViewById(C1018R.id.progressDownload);
    }

    private void loadPlaces() {
        this.progress.setVisibility(0);
        HashMap hashMap = new HashMap();
        hashMap.put("nou", "" + this.appConfig.getUser().getNou());
        hashMap.put("type", "" + this.typeSelect);
        hashMap.put("appId", this.appConfig.getAppId());
        this.service = (ApiService) RetrofitBuilder.createServiceWithAuth(ApiService.class, this.tokenManager, this.appConfig.getBaseUrl());
        this.service.GetListPlaces(hashMap).enqueue(new Callback<PlacesResponse>() { // from class: com.yd.electricecollector.ui.LookupPleasesActivity.3
            @Override // retrofit2.Callback
            public void onFailure(Call<PlacesResponse> call, Throwable th) {
                LookupPleasesActivity.this.progress.setVisibility(8);
                Log.w(LookupPleasesActivity.this.TAG, "onFailure: " + th.getMessage());
            }

            @Override // retrofit2.Callback
            public void onResponse(Call<PlacesResponse> call, Response<PlacesResponse> response) {
                Log.w(LookupPleasesActivity.this.TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    LookupPleasesActivity.this.aList = response.body().getData();
                    LookupPleasesActivity.this._adapter.setItems(LookupPleasesActivity.this.aList);
                    LookupPleasesActivity.this._adapter.setItemsFilter(LookupPleasesActivity.this.aList);
                }
                LookupPleasesActivity.this.progress.setVisibility(8);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C1018R.layout.lookup_activity2);
        setTitle(" المناطق  ");
        this.layoutOptions = (LinearLayout) findViewById(C1018R.id.layoutOptions);
        this.layoutOptions.setVisibility(8);
        this.progress = (LinearLayout) findViewById(C1018R.id.progressDownload);
        this.edtFilter = (EditText) findViewById(C1018R.id.edtFilter);
        this.typeSelect = getIntent().getIntExtra(EXTRA_PROVINSI_ID_VALUE, 1);
        this.sptblh = (Spinner) findViewById(C1018R.id.spTblhFilter);
        this.spmstlm = (Spinner) findViewById(C1018R.id.spPlacesFilter);
        this.appConfig = AppConfig.getInstance();
        this.user = this.appConfig.getUser();
        initializeView();
        this.tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", 0));
        this.tokenManager.getToken();
        this._adapter = new PlacesAdapter();
        this._adapter.setItemClickListener(new OnItemClickCallback<Places>() { // from class: com.yd.electricecollector.ui.LookupPleasesActivity.1
            @Override // com.p001yd.electricecollector.network.OnItemClickCallback
            public void onItemClicked(Places places) {
                Intent intent = new Intent();
                intent.putExtra(LookupPleasesActivity.EXTRA_SELECTED_VALUE, places);
                LookupPleasesActivity.this.setResult(-1, intent);
                LookupPleasesActivity.this.finish();
            }

            @Override // com.p001yd.electricecollector.network.OnItemClickCallback
            public void onItemClicked(Places places, int i) {
            }
        });
        loadPlaces();
        this.recyclerView.setAdapter(this._adapter);
        this.edtFilter.addTextChangedListener(new TextWatcher() { // from class: com.yd.electricecollector.ui.LookupPleasesActivity.2
            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                LookupPleasesActivity.this._adapter.getFilter().filter(charSequence);
            }
        });
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C1018R.menu.o_lookup, menu);
        return true;
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onFailed(Places places) {
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onLoadDataFailure() {
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onLoadDataSucceed(List<Places> list) {
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
    public void onSucceed(Places places) {
    }
}
