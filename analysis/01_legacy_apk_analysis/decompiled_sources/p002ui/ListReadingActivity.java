package com.p001yd.electricecollector.p002ui;

import android.R;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.itextpdf.text.pdf.PdfBoolean;
import com.p001yd.electricecollector.Adapter.CustomAdapter;
import com.p001yd.electricecollector.Adapter.ListReadingAdapter;
import com.p001yd.electricecollector.C1018R;
import com.p001yd.electricecollector.LoginActivity;
import com.p001yd.electricecollector.TokenManager;
import com.p001yd.electricecollector.Utils;
import com.p001yd.electricecollector.Validation;
import com.p001yd.electricecollector.common.AppConfig;
import com.p001yd.electricecollector.common.ErrorHandler;
import com.p001yd.electricecollector.entities.ItemReading;
import com.p001yd.electricecollector.entities.Places;
import com.p001yd.electricecollector.entities.PlacesResponse;
import com.p001yd.electricecollector.entities.ReadingResponse;
import com.p001yd.electricecollector.entities.TGroipResponse;
import com.p001yd.electricecollector.entities.TGroup;
import com.p001yd.electricecollector.entities.Tblh;
import com.p001yd.electricecollector.network.ApiService;
import com.p001yd.electricecollector.network.OnItemClickCallback;
import com.p001yd.electricecollector.network.RetrofitBuilder;
import com.p001yd.electricecollector.p002ui.tools.LookupGroupsActivity;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import okhttp3.WebSocket;
import org.json.JSONException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* loaded from: classes12.dex */
public class ListReadingActivity extends AppCompatActivity implements BaseView<ItemReading>, ErrorHandler.ErrorCallback {
    private static final String TAG = "ListReadingActivity";
    private ListReadingAdapter _adapter;
    private String _appId;
    private String _baseUrl;
    ReadingPresenter _presenter;
    private String _token;
    AppConfig appConfig;
    BottomSheetDialog bsh;
    Button btnShow;
    ImageView btnTblh;
    Button btnView;
    ImageView btnmstlm;
    Call<ReadingResponse> call;
    CheckBox checkBox;
    List<TGroup> group_List;
    protected LinearLayout layoutOptions;
    List<ItemReading> listReport;
    List<Places> places_List;
    LinearLayout progress;
    private CheckBox rad1;
    private CheckBox rad2;
    private CheckBox rad3;
    RecyclerView recyclerView;
    ApiService service;
    EditText spgroup;
    private TextView spmstlm;
    private TextView sptblh;
    List<Tblh> tblh_List;
    TokenManager tokenManager;
    TextView tv1;
    WebSocket webSocket = null;
    int itemposation = -1;
    Places _places = null;
    TGroup _tgroup = null;
    private String placesSelect = null;
    private String groupSelect = null;
    private String tblhSelect = null;

    /* JADX INFO: Access modifiers changed from: private */
    public void AsyncDownloadStart() {
        HashMap hashMap = new HashMap();
        hashMap.put("id", "" + this.appConfig.getUser().getNou());
        if (this._places != null) {
            hashMap.put("nomstlm", "" + this._places.getnum());
        }
        if (this._tgroup != null) {
            hashMap.put("notblh", this._tgroup.getnum());
        }
        if (!this.spgroup.getText().toString().isEmpty()) {
            hashMap.put("nogroup", "" + this.spgroup.getText().toString());
        }
        if (this.checkBox.isChecked()) {
            hashMap.put("isnull", PdfBoolean.TRUE);
        }
        hashMap.put("appId", this.appConfig.getAppId());
        this.progress.setVisibility(0);
        this.service = (ApiService) RetrofitBuilder.createServiceWithAuth(ApiService.class, this.tokenManager, this.appConfig.getBaseUrl());
        this.call = this.service.GetListReadingCounter(hashMap);
        this.call.enqueue(new Callback<ReadingResponse>() { // from class: com.yd.electricecollector.ui.ListReadingActivity.7
            static final /* synthetic */ boolean $assertionsDisabled = false;

            @Override // retrofit2.Callback
            public void onFailure(Call<ReadingResponse> call, Throwable th) {
                Log.w(ListReadingActivity.TAG, "onFailure: " + th.getMessage());
                ListReadingActivity.this.progress.setVisibility(8);
                ErrorHandler.handleError(th, ListReadingActivity.this);
            }

            @Override // retrofit2.Callback
            public void onResponse(Call<ReadingResponse> call, Response<ReadingResponse> response) {
                Log.w(ListReadingActivity.TAG, "onResponse: " + response);
                if (!response.isSuccessful()) {
                    ListReadingActivity.this.handleResponseError(response);
                } else {
                    if (response.body() == null) {
                        throw new AssertionError();
                    }
                    if (response.body().getData() != null) {
                        ListReadingActivity.this.listReport = response.body().getData();
                        ListReadingActivity.this._adapter.setItems(response.body().getData());
                        ListReadingActivity.this._adapter.setItemsFilter(ListReadingActivity.this._adapter.getItems());
                        ListReadingActivity.this.progress.setVisibility(8);
                    }
                }
                ListReadingActivity.this.progress.setVisibility(8);
            }
        });
    }

    private void getDataReport() {
        new ProgressBar(this).setClickable(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleResponseError(Response<?> response) {
        try {
            showErrorMessage("خطأ: " + response.code() + " - " + response.errorBody().string());
        } catch (IOException e) {
            showErrorMessage("خطأ في قراءة رسالة الخطأ");
        }
    }

    private void loadPlaces() {
        HashMap hashMap = new HashMap();
        hashMap.put("nou", "" + this.appConfig.getUser().getNou());
        hashMap.put("type", "1");
        hashMap.put("appid", this.appConfig.getAppId());
        this.service = (ApiService) RetrofitBuilder.createServiceWithAuth(ApiService.class, this.tokenManager, this.appConfig.getBaseUrl());
        this.service.GetListPlaces(hashMap).enqueue(new Callback<PlacesResponse>() { // from class: com.yd.electricecollector.ui.ListReadingActivity.8
            @Override // retrofit2.Callback
            public void onFailure(Call<PlacesResponse> call, Throwable th) {
                Log.w(ListReadingActivity.TAG, "onFailure: " + th.getMessage());
            }

            @Override // retrofit2.Callback
            public void onResponse(Call<PlacesResponse> call, Response<PlacesResponse> response) {
                Log.w(ListReadingActivity.TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    ArrayList arrayList = new ArrayList();
                    arrayList.add("غيرمحدد");
                    ListReadingActivity.this.places_List = response.body().getData();
                    if (ListReadingActivity.this.places_List != null) {
                        Iterator<Places> it = ListReadingActivity.this.places_List.iterator();
                        while (it.hasNext()) {
                            arrayList.add(it.next().getname());
                        }
                    }
                    new ArrayAdapter(ListReadingActivity.this, R.layout.simple_spinner_item, arrayList).setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                }
            }
        });
    }

    private void loadTblh() {
        HashMap hashMap = new HashMap();
        hashMap.put("no_mstlm", "" + this.placesSelect);
        hashMap.put("appid", this.appConfig.getAppId());
        this.service = (ApiService) RetrofitBuilder.createServiceWithAuth(ApiService.class, this.tokenManager, this.appConfig.getBaseUrl());
        this.service.GetListGroup(hashMap).enqueue(new Callback<TGroipResponse>() { // from class: com.yd.electricecollector.ui.ListReadingActivity.9
            @Override // retrofit2.Callback
            public void onFailure(Call<TGroipResponse> call, Throwable th) {
                Log.w(ListReadingActivity.TAG, "onFailure: " + th.getMessage());
            }

            @Override // retrofit2.Callback
            public void onResponse(Call<TGroipResponse> call, Response<TGroipResponse> response) {
                Log.w(ListReadingActivity.TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    ArrayList arrayList = new ArrayList();
                    arrayList.add("غيرمحدد");
                    ListReadingActivity.this.group_List = response.body().getData();
                    if (ListReadingActivity.this.group_List != null) {
                        Iterator<TGroup> it = ListReadingActivity.this.group_List.iterator();
                        while (it.hasNext()) {
                            arrayList.add(it.next().getname());
                        }
                    }
                    new ArrayAdapter(ListReadingActivity.this, R.layout.simple_spinner_item, arrayList).setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                }
            }
        });
    }

    private void setDataInRecyclerView() {
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setAdapter(new CustomAdapter(this, this.listReport));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showEditFragment(final ItemReading itemReading) {
        this.bsh = new BottomSheetDialog(this);
        this.bsh.setContentView(C1018R.layout.reading_edit);
        TextView textView = (TextView) this.bsh.findViewById(C1018R.id.txtvConsmer);
        final TextView textView2 = (TextView) this.bsh.findViewById(C1018R.id.tvReadingKs);
        TextView textView3 = (TextView) this.bsh.findViewById(C1018R.id.tvlsl);
        ImageView imageView = (ImageView) this.bsh.findViewById(C1018R.id.btn_save);
        final EditText editText = (EditText) this.bsh.findViewById(C1018R.id.ed_cust_name);
        final TextView textView4 = (TextView) this.bsh.findViewById(C1018R.id.ed_sk);
        textView3.setText(String.valueOf(itemReading.getAsts()));
        textView.setText(itemReading.getNoadad() + "  " + itemReading.getname());
        textView2.setText(String.valueOf(itemReading.getKs()));
        editText.addTextChangedListener(new TextWatcher() { // from class: com.yd.electricecollector.ui.ListReadingActivity.10
            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
            }

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                double d = 0.0d;
                try {
                    d = Utils.parseDouble(charSequence.toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                double d2 = 0.0d;
                try {
                    d2 = Utils.parseDouble(textView2.getText().toString());
                } catch (ParseException e2) {
                    e2.printStackTrace();
                }
                double d3 = d - d2;
                String numberToString = Utils.numberToString(d3);
                if (d3 > 0.0d) {
                    textView4.setText(numberToString);
                }
                if (Utils.stringToNumber(textView4.getText().toString()) > itemReading.getAsts()) {
                    textView4.setTextColor(Color.parseColor("#D81B60"));
                } else {
                    textView4.setTextColor(Color.parseColor("#0C0C0C"));
                }
            }
        });
        editText.setText(itemReading.getKh() > 0.0d ? String.valueOf(itemReading.getKh()) : "");
        editText.requestFocus();
        ((InputMethodManager) getSystemService("input_method")).showSoftInput(editText, 1);
        imageView.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.ListReadingActivity.11
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (Validation.hasText(editText)) {
                    double ks = itemReading.getKs();
                    double parseDouble = Double.parseDouble(editText.getText().toString());
                    if (parseDouble < ks) {
                        Toast.makeText(ListReadingActivity.this, "قيمة غير مقبولة القراءة الحالية اصعر من السابقة", 0).show();
                        return;
                    }
                    itemReading.setKh(parseDouble);
                    ListReadingActivity.this.bsh.dismiss();
                    ListReadingActivity.this.progress.setVisibility(0);
                    ListReadingActivity.this._presenter = new ReadingPresenter(ListReadingActivity.this._baseUrl, ListReadingActivity.this._token, ListReadingActivity.this._appId, ListReadingActivity.this);
                    try {
                        ListReadingActivity.this._presenter.save(itemReading);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        this.bsh.show();
    }

    private void showErrorMessage(String str) {
        new AlertDialog.Builder(this).setTitle("خطأ").setMessage(str).setPositiveButton("حسناً", (DialogInterface.OnClickListener) null).show();
    }

    void getReports() {
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 500 && i2 == -1) {
            this._places = (Places) intent.getExtras().get(LookupPleasesActivity.EXTRA_SELECTED_VALUE);
            this.spmstlm.setText(this._places.getname());
        }
        if (i == 600 && i2 == -1) {
            this._tgroup = (TGroup) intent.getExtras().get(LookupGroupsActivity.EXTRA_SELECTED_VALUE);
            this.sptblh.setText(this._tgroup.getname());
        }
    }

    @Override // com.yd.electricecollector.common.ErrorHandler.ErrorCallback
    public void onBadGateway(String str) {
        showErrorMessage(str);
    }

    @Override // com.yd.electricecollector.common.ErrorHandler.ErrorCallback
    public void onBadRequest(String str) {
        showErrorMessage(str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C1018R.layout.list_reading_activity);
        setTitle("ادخال القراءات");
        ((ActionBar) Objects.requireNonNull(getSupportActionBar())).setDisplayHomeAsUpEnabled(true);
        this.appConfig = AppConfig.getInstance();
        this._baseUrl = this.appConfig.getBaseUrl();
        this._appId = this.appConfig.getAppId();
        this.btnShow = (Button) findViewById(C1018R.id.btnShow);
        this.tv1 = (TextView) findViewById(C1018R.id.textView);
        this.recyclerView = (RecyclerView) findViewById(C1018R.id.recycleV);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.addItemDecoration(new DividerItemDecoration(this, 1));
        this.progress = (LinearLayout) findViewById(C1018R.id.progressDownload);
        this.btnTblh = (ImageView) findViewById(C1018R.id.btnTblh);
        this.btnmstlm = (ImageView) findViewById(C1018R.id.btnmstlm);
        this.layoutOptions = (LinearLayout) findViewById(C1018R.id.layoutOptions);
        this.appConfig = AppConfig.getInstance();
        this.btnTblh.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.ListReadingActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ListReadingActivity.this.sptblh.setText("");
                ListReadingActivity.this._tgroup = null;
            }
        });
        this.btnmstlm.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.ListReadingActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ListReadingActivity.this.spmstlm.setText("");
                ListReadingActivity.this._places = null;
                ListReadingActivity.this.sptblh.setText("");
                ListReadingActivity.this._tgroup = null;
            }
        });
        this.tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", 0));
        this.tokenManager.getToken();
        this._token = this.tokenManager.getToken().getAccessToken();
        this.checkBox = (CheckBox) findViewById(C1018R.id.checkBox);
        this.sptblh = (TextView) findViewById(C1018R.id.spTblhFilter);
        this.spgroup = (EditText) findViewById(C1018R.id.spGroupFilter);
        this.spmstlm = (TextView) findViewById(C1018R.id.spPlacesFilter);
        this.btnView = (Button) findViewById(C1018R.id.btnView);
        this.sptblh.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.ListReadingActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Intent intent = new Intent(ListReadingActivity.this, (Class<?>) LookupGroupsActivity.class);
                intent.putExtra(LookupGroupsActivity.EXTRA_SELECTED_VALUE, ListReadingActivity.this._places);
                ListReadingActivity.this.startActivityForResult(intent, 600);
            }
        });
        this.spmstlm.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.ListReadingActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Intent intent = new Intent(ListReadingActivity.this, (Class<?>) LookupPleasesActivity.class);
                intent.putExtra(LookupGroupsActivity.EXTRA_PROVINSI_ID_VALUE, 1);
                ListReadingActivity.this.startActivityForResult(intent, 500);
            }
        });
        this.btnView.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.ui.ListReadingActivity.5
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ListReadingActivity.this.AsyncDownloadStart();
            }
        });
        this._adapter = new ListReadingAdapter();
        this.recyclerView.setAdapter(this._adapter);
        this._adapter.setItemClickListener(new OnItemClickCallback<ItemReading>() { // from class: com.yd.electricecollector.ui.ListReadingActivity.6
            @Override // com.p001yd.electricecollector.network.OnItemClickCallback
            public void onItemClicked(ItemReading itemReading) {
            }

            @Override // com.p001yd.electricecollector.network.OnItemClickCallback
            public void onItemClicked(ItemReading itemReading, int i) {
                if (itemReading.getCas() != 0) {
                    Toast.makeText(ListReadingActivity.this, "لايمكن تعديل القراءة المرحلة ", 0).show();
                } else {
                    ListReadingActivity.this.showEditFragment(itemReading);
                    ListReadingActivity.this.itemposation = i;
                }
            }
        });
        this.spgroup.setText("");
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C1018R.menu.o_report, menu);
        MenuItem findItem = menu.findItem(C1018R.id.search);
        SearchManager searchManager = (SearchManager) getSystemService("search");
        SearchView searchView = (SearchView) findItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("بحث");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() { // from class: com.yd.electricecollector.ui.ListReadingActivity.12
            @Override // android.widget.SearchView.OnQueryTextListener
            public boolean onQueryTextChange(String str) {
                ListReadingActivity.this.onQueryTextChange2(str);
                return false;
            }

            @Override // android.widget.SearchView.OnQueryTextListener
            public boolean onQueryTextSubmit(String str) {
                return false;
            }
        });
        menu.findItem(C1018R.id.mnuItemPrint).setVisible(false);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onFailed(ItemReading itemReading) {
        this.progress.setVisibility(8);
        Toast.makeText(this, "فشل عملية التعديل", 0).show();
    }

    @Override // com.yd.electricecollector.common.ErrorHandler.ErrorCallback
    public void onForbidden(String str) {
        showErrorMessage(str);
    }

    @Override // com.yd.electricecollector.common.ErrorHandler.ErrorCallback
    public void onHttpError(String str) {
        showErrorMessage(str);
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onLoadDataFailure() {
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onLoadDataSucceed(List<ItemReading> list) {
    }

    @Override // com.yd.electricecollector.common.ErrorHandler.ErrorCallback
    public void onNetworkError(String str) {
        showErrorMessage(str);
    }

    @Override // com.yd.electricecollector.common.ErrorHandler.ErrorCallback
    public void onNotFound(String str) {
        showErrorMessage(str);
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 16908332) {
            finish();
            return true;
        }
        if (itemId == C1018R.id.mnuItemOptionDropDown) {
            if (this.layoutOptions.getVisibility() == 0) {
                this.layoutOptions.setVisibility(8);
            } else {
                this.layoutOptions.setVisibility(0);
            }
        }
        return super.onOptionsItemSelected(menuItem);
    }

    void onQueryTextChange2(String str) {
        this._adapter.getFilter().filter(str);
    }

    @Override // android.app.Activity
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        this._places = (Places) bundle.getParcelable("places");
        this._tgroup = (TGroup) bundle.getParcelable("tgroup");
        this.spmstlm.setText(this._places.getname());
        this.sptblh.setText(this._tgroup.getname());
        AsyncDownloadStart();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable("places", this._places);
        bundle.putParcelable("tgroup", this._tgroup);
    }

    @Override // com.yd.electricecollector.common.ErrorHandler.ErrorCallback
    public void onServerError(String str) {
        showErrorMessage("خطأ في الخادم: " + str);
    }

    @Override // com.yd.electricecollector.common.ErrorHandler.ErrorCallback
    public void onServiceUnavailable(String str) {
        showErrorMessage(str);
    }

    @Override // com.p001yd.electricecollector.p002ui.BaseView
    public void onSucceed(ItemReading itemReading) {
        this.progress.setVisibility(8);
        this._adapter.updateItem(itemReading, this.itemposation);
        this.recyclerView.smoothScrollToPosition(this.itemposation);
        Toast.makeText(this, "تم الحفظ ", 0).show();
        this.itemposation = -1;
        this.bsh.dismiss();
    }

    @Override // com.yd.electricecollector.common.ErrorHandler.ErrorCallback
    public void onTimeoutError(String str) {
        showErrorMessage(str);
    }

    @Override // com.yd.electricecollector.common.ErrorHandler.ErrorCallback
    public void onUnauthorized(String str) {
        showErrorMessage(str);
        startActivity(new Intent(this, (Class<?>) LoginActivity.class));
        finish();
    }

    @Override // com.yd.electricecollector.common.ErrorHandler.ErrorCallback
    public void onUnknownError(String str) {
        showErrorMessage(str);
    }
}
